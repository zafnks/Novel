package com.zafnks.utils;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

import java.util.Date;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

public class QuartzUtils {
    private static String JOB_NAME = "EXTJWEB_NAME";
    private static String JOB_GROUP_NAME = "EXTJWEB_JOBGROUP_NAME";
    private static String TRIGGER_NAME = "EXTJWEB_NAME";
    private static String TRIGGER_GROUP_NAME = "EXTJWEB_TRIGGERGROUP_NAME";

    /**
     * @Description: 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名
     * @param sched:调度器
     * @param jobClass:任务
     * @param time:时间设置，CronExpression表达式
     */
    public static void addJob(Scheduler sched, @SuppressWarnings("rawtypes") Class jobClass, String time) {
        addJob(sched, jobClass, time, JOB_NAME, JOB_GROUP_NAME, TRIGGER_NAME, TRIGGER_GROUP_NAME);
    }

    /**
     * @Description: 添加一个定时任务
     * @param sched:调度器
     * @param jobClass:任务
     * @param time:时间设置，CronExpression表达式
     * @param jobName:任务名
     * @param jobGroupName:任务组名
     * @param triggerName:触发器名
     * @param triggerGroupName:触发器组名
     */
    public static void addJob(Scheduler sched, @SuppressWarnings("rawtypes") Class jobClass, String time,
            String jobName, String jobGroupName, String triggerName, String triggerGroupName) {

        JobDetail job = newJob(jobClass).withIdentity(jobName, jobGroupName).build();
        CronTrigger trigger = newTrigger().withIdentity(triggerName, triggerGroupName).withSchedule(cronSchedule(time))
                .build();
        try {
            // 返回为 null 添加失败
            Date ft = sched.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: 定义一个任务之后进行触发设定(使用默认的任务组名，触发器名，触发器组名)
     * @param sched:调度器
     * @param time
     */
    @SuppressWarnings("rawtypes")
    public static void addJObLaterUse(Scheduler sched, @SuppressWarnings("rawtypes") Class jobClass, String time) {

        addJObLaterUse(sched, jobClass, time, JOB_NAME, JOB_GROUP_NAME);
    }

    /**
     * @Description: 定义一个任务之后进行触发设定
     * @param sched:调度器
     * @param time
     * @param jobName:任务名
     * @param jobGroupName:任务组名
     */
    @SuppressWarnings("rawtypes")
    public static void addJObLaterUse(Scheduler sched, @SuppressWarnings("rawtypes") Class jobClass, String time,
            String jobName, String jobGroupName) {

        JobDetail job = newJob(jobClass).withIdentity(jobName, jobGroupName).storeDurably().build();

        try {
            sched.addJob(job, false);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: 对已存储的任务进行scheduling(使用默认的任务组名，触发器名，触发器组名)
     * @param sched:调度器
     * @param time
     * @param jobName:任务名
     * @param jobGroupName:任务组名
     */
    @SuppressWarnings("rawtypes")
    public static void schedulingStoredJOb(Scheduler sched, @SuppressWarnings("rawtypes") Class jobClass, String time) {
        schedulingStoredJOb(sched, jobClass, time, JOB_NAME, JOB_GROUP_NAME, TRIGGER_NAME, TRIGGER_GROUP_NAME);
    }

    /**
     * @Description: 对已存储的任务进行scheduling
     * @param sched:调度器
     * @param time
     * @param jobName:任务名
     * @param jobGroupName:任务组名
     */
    @SuppressWarnings("rawtypes")
    public static void schedulingStoredJOb(Scheduler sched, @SuppressWarnings("rawtypes") Class jobClass, String time,
            String jobName, String jobGroupName, String triggerName, String triggerGroupName) {
        Trigger trigger = newTrigger().withIdentity(triggerName, triggerGroupName).startNow()
                .forJob(jobKey(jobName, jobGroupName)).build();
        try {
            sched.scheduleJob(trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: 修改一个任务的触发时间(使用默认的任务组名，触发器名，触发器组名)
     * @param sched:调度器
     * @param time
     */
    @SuppressWarnings("rawtypes")
    public static void modifyJobTime(Scheduler sched, String time) {
        modifyJobTime(sched, TRIGGER_NAME, TRIGGER_GROUP_NAME, time);
    }

    /**
     * @Description: 修改一个任务的触发时间
     * @param sched:调度器
     * @param triggerName
     * @param triggerGroupName
     * @param time
     */
    public static void modifyJobTime(Scheduler sched, String triggerName, String triggerGroupName, String time) {
        Trigger trigger = newTrigger().withIdentity(triggerName, triggerGroupName).withSchedule(cronSchedule(time))
                .startNow().build();

        try {
            sched.rescheduleJob(triggerKey(triggerName, triggerGroupName), trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: 修改一个任务(使用默认的任务组名，任务名)
     * @param sched:调度器
     */
    @SuppressWarnings("rawtypes")
    public static void modifyJob(Scheduler sched, @SuppressWarnings("rawtypes") Class jobClass) {
        modifyJob(sched, jobClass, JOB_NAME, JOB_GROUP_NAME);
    }

    /**
     * @Description: 修改一个任务
     * @param sched:调度器
     * @param jobName:任务名
     * @param jobGroupName:任务组名
     */
    public static void modifyJob(Scheduler sched, @SuppressWarnings("rawtypes") Class jobClass, String jobName,
            String jobGroupName) {
        JobDetail job1 = newJob(jobClass).withIdentity(jobName, jobGroupName).build();
        try {
            sched.addJob(job1, true);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: 删除一个任务的的trigger
     * @param sched:调度器
     * @param triggerName
     * @param triggerGroupName
     */
    public static void unschedulingJob(Scheduler sched, String triggerName, String triggerGroupName) {
        try {
            sched.unscheduleJob(triggerKey(triggerName, triggerGroupName));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: 移除一个任务，以及任务的所有trigger
     * @param sched:调度器
     * @param jobName
     */
    public static void removeJob(Scheduler sched, String jobName, String jobGroupName) {
        try {
            sched.deleteJob(jobKey(jobName, jobGroupName));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description:启动所有定时任务
     * @param sched:调度器
     */
    public static void startJobs(Scheduler sched) {
        try {
            sched.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description:关闭所有定时任务
     * @param sched:调度器
     */
    public static void shutdownJobs(Scheduler sched) {
        try {
            if (!sched.isShutdown()) {
                // 未传参或false：不等待执行完成便结束；true：等待任务执行完才结束
                sched.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}