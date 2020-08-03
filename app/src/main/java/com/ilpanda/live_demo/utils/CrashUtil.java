package com.ilpanda.live_demo.utils;

import com.tencent.mars.xlog.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CrashUtil {

    private static final String TAG = CrashUtil.class.getSimpleName();

    public static String getThreadStack(Throwable ex) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);

        Throwable cause = ex;
        while (cause != null) {
            cause.printStackTrace(printWriter);
            break;
        }
        String stackTrace = result.toString();

        printWriter.close();

        return stackTrace.trim();
    }


    public static void logFileToSD(String path, String fileName, String crashTxt) {

        BufferedOutputStream bufferedOutputStream = null;
        try {
            File logFile = new File(path);
            if (!logFile.exists()) {
                logFile.mkdirs();
            }
            File file = new File(path, fileName);
            if (file.length() >= 1024 * 1024 * 3) {
                file.delete();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
            crashTxt = String.format("\n%s\n%s", format, crashTxt);
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file, true));
            bufferedOutputStream.write(crashTxt.getBytes());
            bufferedOutputStream.close();

        } catch (IOException e) {
            Log.d(TAG, e.toString());
        } finally {
            Closeables.closeQuietly(bufferedOutputStream);
        }
    }

}
