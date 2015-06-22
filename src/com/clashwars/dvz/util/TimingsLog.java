package com.clashwars.dvz.util;

import com.clashwars.dvz.DvZ;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimingsLog {

    private DvZ dvz;
    private String fileName;

    public TimingsLog(DvZ dvz, String fileName) {
        this.dvz = dvz;

        if (fileName == null || fileName.isEmpty()) {
            fileName = "timings.txt";
        }
        this.fileName = fileName;
    }

    public void log(String message, Long startTime) {
        try {
            File dataFolder = dvz.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdir();
            }

            File file = new File(dvz.getDataFolder(), fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            PrintWriter pw = new PrintWriter(new FileWriter(file, true));
            Long ms = System.currentTimeMillis() - startTime;
            pw.println(Util.getTimeStamp() + getTag(ms) + "\t> " + ms + " \t{ " + message + " }");
            pw.flush();
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getTag(Long ms) {
        if (ms < 10) {
            return "(V-LOW) ";
        }
        if (ms < 50) {
            return "(LOW)   ";
        }
        if (ms < 200) {
            return "(HIGH)  ";
        }
        if (ms < 500) {
            return "(V-HIGH)";
        }
        return "(EXTREME)";
    }
}
