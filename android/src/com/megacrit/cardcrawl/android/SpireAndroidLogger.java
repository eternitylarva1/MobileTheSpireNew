package com.megacrit.cardcrawl.android;

import android.media.MediaPlayer;
import android.util.Log;
import com.badlogic.gdx.ApplicationLogger;

import java.util.HashMap;
import java.util.Map;

public class SpireAndroidLogger {
    private Class<?> loggerCls;
    private ApplicationLogger logger;
    private static Map<Class<?>, ApplicationLogger> loggerMap = new HashMap<>();

    private SpireAndroidLogger(Class<?> cls) {
        this.loggerCls = cls;
        this.logger = loggerMap.get(cls);
    }

    public static SpireAndroidLogger getLogger(Class<?> cls) {
        if (!loggerMap.containsKey(cls)) {
            loggerMap.put(cls, new ApplicationLogger() {
                private String tagName = cls.getName().replace("com.megacrit.cardcrawl.", "");

                @Override
                public void log(String tag, String message) {
                    Log.i(tagName, message);
                }

                @Override
                public void log(String tag, String message, Throwable exception) {
                    Log.i(tagName, message, exception);
                }

                @Override
                public void error(String tag, String message) {
                    Log.e(tagName, message);
                }

                @Override
                public void error(String tag, String message, Throwable exception) {
                    Log.e(tagName, message, exception);
                }

                @Override
                public void debug(String tag, String message) {
                    Log.d(tagName, message);
                }

                @Override
                public void debug(String tag, String message, Throwable exception) {
                    Log.d(tagName, message, exception);
                }
            });
        }
        return new SpireAndroidLogger(cls);
    }

    private String getFormatString(String msg, Object... params) {
        int i = 0;
        while (msg.contains("{}")) {
            if (i < params.length) {
                msg = msg.replaceFirst("\\{\\}", params[i].toString());
            } else {
                break;
            }
            i++;
        }
        return msg;
    }

    public void debug(String msg, Object... params) {
        msg = getFormatString(msg, params);
        this.logger.debug("", msg);
    }

    public void info(String msg, Object... params) {
        msg = getFormatString(msg, params);
        this.logger.log("", msg);
    }

    public void warn(String msg, Object... params) {
        msg = getFormatString(msg, params);
        Log.w(this.loggerCls.getName().replace("com.megacrit.cardcrawl.", ""), msg);
    }

    public void error(String msg, Object... params) {
        msg = getFormatString(msg, params);
        this.logger.error("", msg);
    }

}
