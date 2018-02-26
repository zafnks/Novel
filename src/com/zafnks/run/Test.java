package com.zafnks.run;

import java.sql.SQLException;

import org.quartz.JobExecutionException;

public class Test {

    public static void main(String[] args) throws SQLException, JobExecutionException {

        CatchJob job = new CatchJob();
        job.execute(null);

    }

}
