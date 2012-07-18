/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.web.monitor.quartz.bean;

import com.blazebit.quartz.JobUtil;
import com.blazebit.quartz.TriggerUtil;
import com.blazebit.quartz.job.GenericJob;
import com.blazebit.quartz.job.JobParameter;
import com.blazebit.web.monitor.quartz.model.Property;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author Christian Beikov
 */
@Named("triggerBean")
@ViewAccessScoped
public class TriggerBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private Scheduler scheduler;
    private String jobName;
    private String jobGroup;
    private String triggerName;
    private String triggerGroup;
    private Date triggerStart;
    private Date triggerEnd;
    private String triggerType;
    private Integer triggerInterval;
    private String triggerCronExpression;
    private String triggerIntervalType;
    private List<String> triggerTypes;
    private List<String> triggerIntervalTypes;
    private JobDetail selectedJob;
    private Trigger selectedTrigger;
    private List<Trigger> jobTriggers;
    private List<Property> triggerDataMap;

    public TriggerBean() {
        triggerTypes = new ArrayList<String>();
        triggerTypes.add("Simple");
        triggerTypes.add("Interval");
        triggerTypes.add("Cron");
        triggerType = "Simple";

        triggerIntervalTypes = new ArrayList<String>();
        triggerIntervalTypes.add("Sekunde");
        triggerIntervalTypes.add("Minute");
        triggerIntervalTypes.add("Stunde");
        triggerIntervalTypes.add("Tag");
        triggerIntervalTypes.add("Woche");
        triggerIntervalTypes.add("Monat");
        triggerIntervalTypes.add("Jahr");
    }

    public void preRender() throws SchedulerException {
        JobDetail job = scheduler.getJobDetail(new JobKey(jobName, jobGroup));

        if (selectedJob == null || (selectedJob != null && !selectedJob.equals(job))) {
            setSelectedJob(job);
        }

        if (selectedJob != null) {
            jobTriggers = JobUtil.getTriggers(scheduler, selectedJob);
        }
    }

    private List<Property> getCopiedPropertiesForTrigger(Class<? extends GenericJob> jobClass, Map<String, Object> jobDataMap) {
        List<Property> copyList = new ArrayList<Property>();
        List<JobParameter> properties = null;

        try {
            properties = jobClass.newInstance().getParameters();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }

        // Add the already defined job parameter properties as not required to the list
        if (properties != null && !properties.isEmpty()) {
            for (JobParameter p : properties) {
                boolean required = p.isRequired();
                Object value = jobDataMap.get(p.getName());

                if (value != null) {
                    required = false;
                }

                copyList.add(new Property(p.getName(), value, required, p.getType(), p.getDescription()));
            }
        }

        OUTER:
        for (Map.Entry<String, Object> entry : jobDataMap.entrySet()) {
            for (Property p : copyList) {
                if (p.getName().equals(entry.getKey())) {
                    continue OUTER;
                }
            }

            copyList.add(new Property(entry.getKey(), entry.getValue(), false, entry.getValue().getClass(), ""));
        }

        return copyList;
    }

    private Map<String, Object> getAsMap(List<Property> properties) {
        Map<String, Object> map = new HashMap<String, Object>();

        for (Property p : properties) {
            if (p.getName() != null && !p.getName().isEmpty() && p.getValue() != null && (((p.getValue() instanceof String) && !((String) p.getValue()).isEmpty()) || !(p.getValue() instanceof String))) {
                map.put(p.getName(), p.getValue());
            }
        }

        return map;
    }

    public List<Property> getSelectedTriggerDataMap() {
        if (selectedTrigger == null) {
            return Collections.emptyList();
        }
        List<Property> dataMap = new ArrayList<Property>();

        for (Map.Entry<String, Object> entry : selectedTrigger.getJobDataMap().entrySet()) {
            dataMap.add(new Property(entry.getKey(), (String) entry.getValue()));
        }

        return dataMap;
    }

    public void addParameter(ActionEvent event) {
        triggerDataMap.add(new Property("", "", false, String.class, ""));
    }

    @SuppressWarnings("unchecked")
    public String addTrigger() throws SchedulerException, ParseException {
        try {
            if ("Simple".equals(triggerType)) {
                TriggerUtil.schedule(TriggerUtil.simple(triggerName, triggerGroup, selectedJob, getAsMap(triggerDataMap), triggerStart, triggerEnd));
            } else if ("Interval".equals(triggerType)) {
                Trigger intervalTrigger = null;

                if ("Sekunde".equals(triggerIntervalType)) {
                    intervalTrigger = TriggerUtil.second(triggerName, triggerGroup, selectedJob.getKey().getName(), selectedJob.getKey().getGroup(), getAsMap(triggerDataMap), triggerInterval, triggerStart, triggerEnd);
                } else if ("Minute".equals(triggerIntervalType)) {
                    intervalTrigger = TriggerUtil.minute(triggerName, triggerGroup, selectedJob.getKey().getName(), selectedJob.getKey().getGroup(), getAsMap(triggerDataMap), triggerInterval, triggerStart, triggerEnd);
                } else if ("Stunde".equals(triggerIntervalType)) {
                    intervalTrigger = TriggerUtil.hour(triggerName, triggerGroup, selectedJob.getKey().getName(), selectedJob.getKey().getGroup(), getAsMap(triggerDataMap), triggerInterval, triggerStart, triggerEnd);
                } else if ("Tag".equals(triggerIntervalType)) {
                    intervalTrigger = TriggerUtil.day(triggerName, triggerGroup, selectedJob.getKey().getName(), selectedJob.getKey().getGroup(), getAsMap(triggerDataMap), triggerInterval, triggerStart, triggerEnd);
                } else if ("Woche".equals(triggerIntervalType)) {
                    intervalTrigger = TriggerUtil.week(triggerName, triggerGroup, selectedJob.getKey().getName(), selectedJob.getKey().getGroup(), getAsMap(triggerDataMap), triggerInterval, triggerStart, triggerEnd);
                } else if ("Monat".equals(triggerIntervalType)) {
                    intervalTrigger = TriggerUtil.month(triggerName, triggerGroup, selectedJob.getKey().getName(), selectedJob.getKey().getGroup(), getAsMap(triggerDataMap), triggerInterval, triggerStart, triggerEnd);
                } else if ("Jahr".equals(triggerIntervalType)) {
                    intervalTrigger = TriggerUtil.year(triggerName, triggerGroup, selectedJob.getKey().getName(), selectedJob.getKey().getGroup(), getAsMap(triggerDataMap), triggerInterval, triggerStart, triggerEnd);
                }

                TriggerUtil.schedule(intervalTrigger);

                if (triggerStart == null || triggerStart.before(new Date())) {
                    TriggerUtil.pause(intervalTrigger.getKey());
                }
            } else if ("Cron".equals(triggerType)) {
                TriggerUtil.schedule(TriggerUtil.cron(triggerName, triggerGroup, selectedJob.getKey().getName(), selectedJob.getKey().getGroup(), getAsMap(triggerDataMap), triggerCronExpression));
            }
        } catch (ObjectAlreadyExistsException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Trigger mit diesem Namen und dieser Gruppe existiert bereits!", null));
        } catch (SchedulerException ex) {
            throw ex;
        }
        triggerName = null;
        triggerGroup = null;
        triggerType = "Simple";
        triggerStart = null;
        triggerEnd = null;
        triggerInterval = 0;
        triggerCronExpression = "";
        triggerDataMap = getCopiedPropertiesForTrigger((Class<? extends GenericJob>) selectedJob.getJobClass(), selectedJob.getJobDataMap().getWrappedMap());

        return "";
    }

    public String deleteTrigger(Trigger trigger) throws SchedulerException {
        TriggerUtil.delete(trigger);
        selectedTrigger = null;
        return "";
    }

    public String pauseTrigger(Trigger trigger) throws SchedulerException {
        TriggerUtil.pause(trigger);
        return "";
    }

    public String resumeTrigger(Trigger trigger) throws SchedulerException {
        TriggerUtil.resume(trigger);
        return "";
    }

    public Trigger.TriggerState getTriggerState(Trigger trigger) throws SchedulerException {
        return TriggerUtil.getState(trigger);
    }

    public List<Trigger> getJobTriggers() {
        return jobTriggers;
    }

    public void setJobTriggers(List<Trigger> jobTriggers) {
        this.jobTriggers = jobTriggers;
    }

    public JobDetail getSelectedJob() {
        return selectedJob;
    }

    @SuppressWarnings("unchecked")
    public void setSelectedJob(JobDetail selectedJob) {
        this.selectedJob = selectedJob;

        if (selectedJob != null) {
            triggerDataMap = getCopiedPropertiesForTrigger((Class<? extends GenericJob>) selectedJob.getJobClass(), selectedJob.getJobDataMap().getWrappedMap());
        } else {
            triggerDataMap = new ArrayList<Property>();
        }
    }

    public Trigger getSelectedTrigger() {
        return selectedTrigger;
    }

    public void setSelectedTrigger(Trigger selectedTrigger) {
        this.selectedTrigger = selectedTrigger;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getTriggerGroup() {
        return triggerGroup;
    }

    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public List<String> getTriggerTypes() {
        return triggerTypes;
    }

    public void setTriggerTypes(List<String> triggerTypes) {
        this.triggerTypes = triggerTypes;
    }

    public Date getTriggerEnd() {
        return triggerEnd;
    }

    public void setTriggerEnd(Date triggerEnd) {
        this.triggerEnd = triggerEnd;
    }

    public Date getTriggerStart() {
        if (triggerStart == null) {
            return new Date(new Date().getTime() + 60000L);
        }
        return triggerStart;
    }

    public void setTriggerStart(Date triggerStart) {
        this.triggerStart = triggerStart;
    }

    public Integer getTriggerInterval() {
        return triggerInterval;
    }

    public void setTriggerInterval(Integer triggerInterval) {
        this.triggerInterval = triggerInterval;
    }

    public String getTriggerIntervalType() {
        return triggerIntervalType;
    }

    public void setTriggerIntervalType(String triggerIntervalType) {
        this.triggerIntervalType = triggerIntervalType;
    }

    public List<String> getTriggerIntervalTypes() {
        return triggerIntervalTypes;
    }

    public void setTriggerIntervalTypes(List<String> triggerIntervalTypes) {
        this.triggerIntervalTypes = triggerIntervalTypes;
    }

    public String getTriggerCronExpression() {
        return triggerCronExpression;
    }

    public void setTriggerCronExpression(String triggerCronExpression) {
        this.triggerCronExpression = triggerCronExpression;
    }

    public List<Property> getTriggerDataMap() {
        return triggerDataMap;
    }

    public void setTriggerDataMap(List<Property> triggerDataMap) {
        this.triggerDataMap = triggerDataMap;
    }

    public String getSchedulerName() throws SchedulerException {
        return scheduler == null ? null : scheduler.getSchedulerName();
    }

    public void setSchedulerName(String schedulerName) throws SchedulerException {
        this.scheduler = new StdSchedulerFactory().getScheduler(schedulerName);

        if (this.scheduler == null) {
            throw new IllegalArgumentException("Scheduler can not be found!");
        }
    }
}
