package com.jugaado.chat.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyLogger {

    String className;
    private static SimpleDateFormat ft = new SimpleDateFormat(
        "E yyyy.MM.dd 'at' hh:mm:ss a zzz");

    MyLogger(String className) {
        this.className = className;
    }

    public static MyLogger getLogger(Class clazz) {
        return new MyLogger(clazz.getSimpleName());
    }

    public void log(String info) {
        String text = ft.format(new Date());
        text += this.className + ":";
        text += info;
        System.out.println(text);
    }
}
