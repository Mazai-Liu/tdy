package com.example.tdy.utils;

import ws.schild.jave.MultimediaInfo;
import ws.schild.jave.MultimediaObject;

import java.net.URL;

/**
 * @author Mazai-Liu
 * @time 2024/4/2
 */
public class FileUtil {

    public static String getDuration(String videoUrl) {
        String[] length = new String[2];
        try {
            URL source = new URL(videoUrl);
            MultimediaObject instance = new MultimediaObject(source);
            MultimediaInfo result = instance.getInfo();
            Long ls = result.getDuration() / 1000;
            length[0] = String.valueOf(ls);
            Integer hour = (int) (ls / 3600);
            Integer minute = (int) (ls % 3600) / 60;
            Integer second = (int) (ls - hour * 3600 - minute * 60);
            String hr = hour.toString();
            String mi = minute.toString();
            String se = second.toString();
            if (hr.length() < 2) {
                hr = "0" + hr;
            }
            if (mi.length() < 2) {
                mi = "0" + mi;
            }
            if (se.length() < 2) {
                se = "0" + se;
            }

            String noHour = "00";
            if (noHour.equals(hr)) {
                length[1] = mi + ":" + se;
            } else {
                length[1] = hr + ":" + mi + ":" + se;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return length[1];
    }
}
