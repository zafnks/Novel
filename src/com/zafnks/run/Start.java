package com.zafnks.run;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import com.zafnks.utils.PathContants;
import com.zafnks.utils.QuartzUtils;

public class Start {

    private final static Logger log = Logger.getLogger(Start.class);

    public static void main(String[] args) throws SchedulerException {
        Properties props = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(new File(PathContants.getBinPath() + "config.properties"));
            props.load(in);
        } catch (FileNotFoundException e) {
            log.error("config.properties文件未找到");
        } catch (IOException e) {
            log.error("出现IOException");
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (IOException e) {
                log.error("config.properties文件流关闭出现异常");
            }
        }
        String cron = (String) props.get("task_cron");
        log.info("启动目录爬虫服务");
        log.info("cron = " + cron);
        Scheduler sched = new StdSchedulerFactory().getScheduler();
        QuartzUtils.addJob(sched, CatchJob.class, cron);
        sched.start();
    }

}
