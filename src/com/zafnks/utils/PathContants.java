package com.zafnks.utils;

import java.net.URL;

import com.zafnks.run.Start;

public class PathContants {

    public static String getBinPath() {
        try {
            URL binUrl = Start.class.getResource("/");
            String binPath = "";
            if (null == binUrl) {
                binPath = System.getProperty("user.dir") + "/";
            } else {
                binPath = java.net.URLDecoder.decode(binUrl.getPath(), "utf-8");
            }
            return binPath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
