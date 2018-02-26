package com.zafnks.server;

import java.util.concurrent.atomic.AtomicInteger;

import com.zafnks.db.DbPoolConnection;

public class LogServer {

    private final static String ADD_LOG_SQL = "INSERT INTO novel_main.novel_log(event, event_time, event_number) VALUES (?, now() ,?)";

    private static LogServer server = new LogServer();

    private DbPoolConnection dbPoolConnection = DbPoolConnection.getInstance();

    private AtomicInteger count = new AtomicInteger(0);

    private String event;

    private boolean isStartEvent = false;

    private LogServer() {
    }

    public static LogServer getInstance() {
        return server;
    }

    public void startCount(String event) {
        if (isStartEvent) {
            throw new RuntimeException("you must stop event before start a new one");
        }
        this.event = event;
        isStartEvent = true;
    }

    public void addCount(int number) {
        if (!isStartEvent) {
            throw new RuntimeException("you must start a event before");
        }
        count.addAndGet(number);
    }

    public void destoryEvent() {
        event = "";
        count.set(0);
        isStartEvent = false;
    }

    public void finishEvent() {
        dbPoolConnection.updateSQL(ADD_LOG_SQL, new Object[]{event, count.get()});
        count.set(0);
        event = "";
        isStartEvent = false;
    }

}
