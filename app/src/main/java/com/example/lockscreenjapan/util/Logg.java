package com.example.lockscreenjapan.util;

import android.util.Log;

public class Logg {
    public static void v(String msg) {
        Log.v(tag(), "" + msg);
    }

    public static void d(String msg) {
        Log.d(tag(), "" + msg);
    }

    public static void i(String msg) {
        Log.i(tag(), "" + msg);
    }

    public static void w(String msg) {
        Log.w(tag(), "" + msg);
    }

    public static void e(String msg) {
        Log.e(tag(), "" + msg);
    }

    public static void v(String User, String msg) {
        Log.v(tag(User), "" + msg);
    }

    public static void d(String User, String msg) {
        Log.d(tag(User), "" + msg);
    }

    public static void i(String User, String msg) {
        Log.i(tag(User), "" + msg);
    }

    public static void w(String User, String msg) {
        Log.w(tag(User), "" + msg);
    }

    public static void e(String User, String msg) {
        Log.e(tag(User), "" + msg);
    }

    private static String tag() {
        StackTraceElement trace = Thread.currentThread().getStackTrace()[4];
        String fileName = trace.getFileName();
        String classPath = trace.getClassName();
        String className = classPath.substring(classPath.lastIndexOf(".") + 1);
        String methodName = trace.getMethodName();
        int lineNumber = trace.getLineNumber();
        return "APP# " + className + "." + methodName + "(" + fileName + ":" + lineNumber + ")";
    }

    private static String tag(String User) {
        StackTraceElement trace = Thread.currentThread().getStackTrace()[4];
        String fileName = trace.getFileName();
        String classPath = trace.getClassName();
        String className = classPath.substring(classPath.lastIndexOf(".") + 1);
        String methodName = trace.getMethodName();
        int lineNumber = trace.getLineNumber();
        return "User: " + User + "    APP# " + className + "." + methodName + "(" + fileName + ":" + lineNumber + ")";
    }

}
