/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.monitor.quartz.model;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.DirectSchedulerFactory;

/**
 *
 * @author Christian Beikov
 */
@Entity
@Table(name = "QRTZ_SCHEDULER_CONFIGURATION")
public class SchedulerConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(SchedulerConfiguration.class.getName());
    private Integer id;
    private String schedulerName;
    private String schedulerInstanceId;
    private String rmiHost;
    private Integer rmiPort;

    public SchedulerConfiguration() {
    }

    public SchedulerConfiguration(Integer id) {
        this.id = id;
    }

    public SchedulerConfiguration(Integer id, String schedulerName,
            String schedulerInstanceId, String rmiHost, Integer rmiPort) {
        this.id = id;
        this.schedulerName = schedulerName;
        this.schedulerInstanceId = schedulerInstanceId;
        this.rmiHost = rmiHost;
        this.rmiPort = rmiPort;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sc_id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "sc_rmi_host")
    public String getRmiHost() {
        return rmiHost;
    }

    public void setRmiHost(String rmiHost) {
        this.rmiHost = rmiHost;
    }

    @Column(name = "sc_rmi_port")
    public Integer getRmiPort() {
        return rmiPort;
    }

    public void setRmiPort(Integer rmiPort) {
        this.rmiPort = rmiPort;
    }

    @Column(name = "sc_scheduler_instance_id")
    public String getSchedulerInstanceId() {
        return schedulerInstanceId;
    }

    public void setSchedulerInstanceId(String schedulerInstanceId) {
        this.schedulerInstanceId = schedulerInstanceId;
    }

    @Column(name = "sc_scheduler_name")
    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    @Transient
    public String getSchedulerUid() {
        return (schedulerName == null || schedulerName.isEmpty() ? "QuartzScheduler"
                : schedulerName)
                + "_$_"
                + (schedulerInstanceId == null || schedulerInstanceId.isEmpty() ? "NON_CLUSTERED"
                : schedulerInstanceId);
    }

    @Transient
    public Scheduler getScheduler() {
        Scheduler s = null;

        try {
            DirectSchedulerFactory fact = DirectSchedulerFactory.getInstance();

            if (fact.getScheduler(schedulerName) == null) {
                fact.createRemoteScheduler(schedulerName == null
                        || schedulerName.isEmpty() ? "QuartzScheduler"
                        : schedulerName, schedulerInstanceId == null
                        || schedulerInstanceId.isEmpty() ? "NON_CLUSTERED"
                        : schedulerInstanceId, rmiHost, rmiPort);
            }

            s = fact.getScheduler(schedulerName == null ? "QuartzScheduler"
                    : schedulerName);
        } catch (SchedulerException ex) {
            log.log(Level.SEVERE, "Could not retrieve Scheduler for config!", ex);
        }

        return s;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SchedulerConfiguration)) {
            return false;
        }
        final SchedulerConfiguration other = (SchedulerConfiguration) obj;
        if (this.getId() != other.getId()
                && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }
}
