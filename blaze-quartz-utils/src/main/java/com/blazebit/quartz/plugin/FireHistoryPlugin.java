/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.quartz.plugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Matcher;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.EverythingMatcher;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.NameMatcher;
import org.quartz.impl.matchers.OrMatcher;
import org.quartz.listeners.TriggerListenerSupport;
import org.quartz.spi.SchedulerPlugin;
import org.quartz.utils.DBConnectionManager;
import org.quartz.utils.Key;
import org.quartz.utils.PropertiesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blazebit.quartz.PropertiesUtils;

/**
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class FireHistoryPlugin extends TriggerListenerSupport implements
		SchedulerPlugin {

	private static final Logger log = LoggerFactory
			.getLogger(FireHistoryPlugin.class);
	public static final String PLUGIN_PREFIX = "org.quartz.plugin.";
	public static final String HISTORY_TABLE_NAME = "FIRE_HISTORY";
	public static final String HISTORY_INSERT = "INSERT INTO {0}{1}(SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP, JOB_NAME, JOB_GROUP, FIRED_TIME, SCHEDULED_TIME, STATE, JOB_RUN_TIME, JOB_DATA) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String HISTORY_SELECT = "SELECT SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP, JOB_NAME, JOB_GROUP, FIRED_TIME, SCHEDULED_TIME, STATE, JOB_RUN_TIME, JOB_DATA FROM {0}{1}";
	private String triggerGroupStartsWith;
	private String triggerGroupEndsWith;
	private String triggerGroupContains;
	private String triggerGroupEquals;
	private String triggerNameStartsWith;
	private String triggerNameEndsWith;
	private String triggerNameContains;
	private String triggerNameEquals;
	private String dataSourceName;
	private Connection connection;
	private String tablePrefix;

	@Override
	public void initialize(String name, Scheduler schdlr)
			throws SchedulerException {
		Properties props = PropertiesUtils.getProperties();
		PropertiesParser cfg = new PropertiesParser(props);

		if (schdlr.getMetaData().isJobStoreSupportsPersistence()) {
			dataSourceName = cfg
					.getStringProperty("org.quartz.jobStore.dataSource");
			tablePrefix = cfg
					.getStringProperty("org.quartz.jobStore.tablePrefix");

			try {
				connection = DBConnectionManager.getInstance().getConnection(
						dataSourceName);
				Matcher<TriggerKey> matcher = null;

				if (cfg.getBooleanProperty(PLUGIN_PREFIX + name + ".matchAny")) {
					matcher = EverythingMatcher.allTriggers();
				} else {
					matcher = addConstraint(matcher, triggerGroupStartsWith,
							"GroupStartsWith");
					matcher = addConstraint(matcher, triggerGroupEndsWith,
							"GroupEndsWith");
					matcher = addConstraint(matcher, triggerGroupContains,
							"GroupContains");
					matcher = addConstraint(matcher, triggerGroupEquals,
							"GroupEquals");
					matcher = addConstraint(matcher, triggerNameStartsWith,
							"NameStartsWith");
					matcher = addConstraint(matcher, triggerNameEndsWith,
							"NameEndsWith");
					matcher = addConstraint(matcher, triggerNameContains,
							"NameContains");
					matcher = addConstraint(matcher, triggerNameEquals,
							"NameEquals");
				}

				schdlr.getListenerManager().addTriggerListener(this, matcher);
			} catch (SQLException ex) {
				throw new SchedulerException(
						"Error when trying to get a database connection.", ex);
			}
		} else {
			log.warn("The fire history can only work with job stores that support persistence!");
		}
	}

	@Override
	public void start() {
	}

	@Override
	public void shutdown() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void triggerComplete(Trigger trgr, JobExecutionContext jec,
			CompletedExecutionInstruction cei) {
		try {
			addEntry(new FireHistoryEntry(
					jec.getScheduler().getSchedulerName(), jec.getTrigger()
							.getKey(), jec.getJobDetail().getKey(),
					jec.getScheduledFireTime(), jec.getFireTime(), jec
							.getScheduler().getTriggerState(
									jec.getTrigger().getKey()),
					jec.getJobRunTime(), jec.getTrigger().getJobDataMap()));
		} catch (SchedulerException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return FireHistoryPlugin.class.getSimpleName();
	}

	public synchronized List<FireHistoryEntry> getEntries() {
		if (connection == null) {
			return Collections.emptyList();
		}

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = createSelectStatement();
			rs = ps.executeQuery();
			List<FireHistoryEntry> entries = new ArrayList<FireHistoryEntry>();

			while (rs.next()) {
				entries.add(new FireHistoryEntry(rs.getString(1),
						new TriggerKey(rs.getString(2), rs.getString(3)),
						new JobKey(rs.getString(4), rs.getString(5)), new Date(
								rs.getLong(6)), new Date(rs.getLong(7)),
						Trigger.TriggerState.valueOf(rs.getString(8)), rs
								.getLong(9), getJobDataMap(rs.getBytes(10))));
			}

			return entries;
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}

				if (ps != null) {
					ps.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		return Collections.emptyList();
	}

	public synchronized void addEntry(FireHistoryEntry entry) {
		if (connection == null) {
			return;
		}

		PreparedStatement ps = null;

		try {
			ps = createInsertStatement();
			ps.setString(1, entry.getSchedulerName());
			ps.setString(2, entry.getTriggerKey().getName());
			ps.setString(3, entry.getTriggerKey().getGroup());
			ps.setString(4, entry.getJobKey().getName());
			ps.setString(5, entry.getJobKey().getGroup());
			ps.setLong(6, entry.getFiredTime().getTime());
			ps.setLong(7, entry.getScheduledTime().getTime());
			ps.setString(8, entry.getState().toString());
			ps.setLong(9, entry.getRunTime());
			ps.setBytes(10, getByteArray(entry.getDataMap()));
			ps.executeUpdate();

			if (!connection.getAutoCommit()) {
				connection.commit();
			}
		} catch (Throwable e) {
			e.printStackTrace();

			try {
				if (connection != null && !connection.getAutoCommit()) {
					connection.rollback();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	protected PreparedStatement createInsertStatement() throws SQLException {
		return connection.prepareStatement(MessageFormat.format(HISTORY_INSERT,
				tablePrefix, HISTORY_TABLE_NAME));
	}

	protected PreparedStatement createSelectStatement() throws SQLException {
		return connection.prepareStatement(MessageFormat.format(HISTORY_SELECT,
				tablePrefix, HISTORY_TABLE_NAME));
	}

	protected byte[] getByteArray(JobDataMap map) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;

		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(map);
			oos.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException ex) {
					// Ignore
				}
			}
		}

		return baos.toByteArray();
	}

	protected JobDataMap getJobDataMap(byte[] bytes) {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = null;
		JobDataMap map = null;

		try {
			ois = new ObjectInputStream(bais);
			map = (JobDataMap) ois.readObject();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException ex) {
					// Ignore
				}
			}
		}

		return map;
	}

	private <T extends Key<T>> Matcher<T> createMatcher(String matcherName,
			String propertyValue) {
		if ("GroupStartsWith".equals(matcherName)) {
			return GroupMatcher.groupStartsWith(propertyValue);
		} else if ("GroupEndsWith".equals(matcherName)) {
			return GroupMatcher.groupEndsWith(propertyValue);
		} else if ("GroupContains".equals(matcherName)) {
			return GroupMatcher.groupContains(propertyValue);
		} else if ("GroupEquals".equals(matcherName)) {
			return GroupMatcher.groupEquals(propertyValue);
		} else if ("NameStartsWith".equals(matcherName)) {
			return NameMatcher.nameStartsWith(propertyValue);
		} else if ("NameEndsWith".equals(matcherName)) {
			return NameMatcher.nameEndsWith(propertyValue);
		} else if ("NameContains".equals(matcherName)) {
			return NameMatcher.nameContains(propertyValue);
		} else if ("NameEquals".equals(matcherName)) {
			return NameMatcher.nameEquals(propertyValue);
		}
		return null;

	}

	private Matcher<TriggerKey> addConstraint(Matcher<TriggerKey> matcher,
			String matchValue, String matcherName) {
		Matcher<TriggerKey> newMatcher = null;

		if (matchValue != null) {
			newMatcher = createMatcher(matcherName, matchValue);
		}

		if (matcher != null) {
			if (newMatcher != null) {
				newMatcher = OrMatcher.or(matcher, newMatcher);
			} else {
				newMatcher = matcher;
			}
		}

		return newMatcher;
	}

	public String getTriggerGroupContains() {
		return triggerGroupContains;
	}

	public void setTriggerGroupContains(String triggerGroupContains) {
		this.triggerGroupContains = triggerGroupContains;
	}

	public String getTriggerGroupEndsWith() {
		return triggerGroupEndsWith;
	}

	public void setTriggerGroupEndsWith(String triggerGroupEndsWith) {
		this.triggerGroupEndsWith = triggerGroupEndsWith;
	}

	public String getTriggerGroupEquals() {
		return triggerGroupEquals;
	}

	public void setTriggerGroupEquals(String triggerGroupEquals) {
		this.triggerGroupEquals = triggerGroupEquals;
	}

	public String getTriggerGroupStartsWith() {
		return triggerGroupStartsWith;
	}

	public void setTriggerGroupStartsWith(String triggerGroupStartsWith) {
		this.triggerGroupStartsWith = triggerGroupStartsWith;
	}

	public String getTriggerNameContains() {
		return triggerNameContains;
	}

	public void setTriggerNameContains(String triggerNameContains) {
		this.triggerNameContains = triggerNameContains;
	}

	public String getTriggerNameEndsWith() {
		return triggerNameEndsWith;
	}

	public void setTriggerNameEndsWith(String triggerNameEndsWith) {
		this.triggerNameEndsWith = triggerNameEndsWith;
	}

	public String getTriggerNameEquals() {
		return triggerNameEquals;
	}

	public void setTriggerNameEquals(String triggerNameEquals) {
		this.triggerNameEquals = triggerNameEquals;
	}

	public String getTriggerNameStartsWith() {
		return triggerNameStartsWith;
	}

	public void setTriggerNameStartsWith(String triggerNameStartsWith) {
		this.triggerNameStartsWith = triggerNameStartsWith;
	}
}
