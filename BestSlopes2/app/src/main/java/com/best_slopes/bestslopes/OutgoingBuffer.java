package com.best_slopes.bestslopes;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.best_slopes.bestslopes.Trail;

public class OutgoingBuffer {

    private static final String EXT_PREPARING = ".preparing";
    private static final String EXT_READY = ".ready";

    public static void toast(final Activity activity, final String message) {
        try {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void write(final Activity activity, final Trail trail) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    File dir = computeDirectory(activity);
                    long now = System.currentTimeMillis();
                    String prepFilename = now + EXT_PREPARING;
                    File prepFile = new File(dir, prepFilename);
                    writeToFile(trail, prepFile);
                    String readyFilename = now + EXT_READY;
                    File readyFile = new File(dir, readyFilename);
                    prepFile.renameTo(readyFile);
                } catch (IOException e) {
                    toast(activity, "Unable to save file");
                }
            }

        }).start();
    }

    private static File computeDirectory(Context context) {
        File mainDir = context.getFilesDir();
        File myDir = new File(mainDir, "outgoing");
        myDir.mkdirs();

        return myDir;
    }

    private static void writeToFile(Trail trail, File file) throws IOException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
        try {
            trail.serialize(dos);
        } finally {
            try {
                dos.close();
            } catch (IOException ignoreThis) {
            }
        }
    }

    private static Trail readFromFile(File file) {
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            try {
                return Trail.deserialize(dis);
            } finally {
                try {
                    dis.close();
                } catch (IOException ignoreThis) {
                }
            }
        } catch (IOException ioe) {
            return null;
        }
    }

    private static UploaderThread uploaderThread;

    public static synchronized void startUploaderThread(final Activity activity) {
        if (uploaderThread == null) {
            uploaderThread = new UploaderThread(activity);
            uploaderThread.start();
        }
    }

    public static synchronized void stopUploaderThread() {
        if (uploaderThread != null) {
            if (!uploaderThread.isInterrupted())
                uploaderThread.interrupt();
            uploaderThread.cleanup();
            uploaderThread = null;
        }
    }

    private static boolean isConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private static class UploaderThread extends Thread {
        private Activity activity;

        public UploaderThread(Activity activity) {
            this.activity = activity;
        }

        public void run() {
            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    cleanup();
                }
                if (isConnected(activity))
                    doUpload(activity);
            }
        }

        public void cleanup() {

        }
    }

    private static void doUpload(Activity activity) {
        File dir = computeDirectory(activity);
        File[] ready = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename != null
                        && filename.endsWith(EXT_READY);
            }
        });
        if (ready == null)
            ready = new File[0];
        for (int i = 0; i < ready.length; i++) {
            try {
                File file = ready[i];
                boolean deleteFile = false;

                Trail trail = readFromFile(file);
                if (trail != null) {
                    boolean uploaded = Uploader.upload(trail);
                    if (uploaded)
                        deleteFile = true;
                } else {
                    // corrupted
                    deleteFile = true;
                }

                if (deleteFile)
                    file.delete();
            } catch (Exception e) {
                toast(activity, "Unable to read/upload file");
            }
        }

    }

}
