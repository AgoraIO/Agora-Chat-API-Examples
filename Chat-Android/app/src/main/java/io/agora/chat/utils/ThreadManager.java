package io.agora.chat.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Simple thread manager
 */
public class ThreadManager {
    private static ThreadManager mInstance;
    private ThreadManager(){}

    public synchronized static ThreadManager getInstance() {
        if(mInstance == null) {
            synchronized (ThreadManager.class) {
                if(mInstance == null) {
                    mInstance = new ThreadManager();
                }
            }
        }
        return mInstance;
    }

    public void executeUI(Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }

    public void execute(Runnable runnable) {
        new Thread(runnable).start();
    }
}
