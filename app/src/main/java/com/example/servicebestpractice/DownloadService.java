package com.example.servicebestpractice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;


import java.io.File;

public class DownloadService extends Service {
    //downloadtask对象
    private DownloadTask downloadTask;
    private String downloadUrl;
    //创建binder
    private DownloadBinder mBinder = new DownloadBinder();


    /**
     * 用内部类形式重写下载逻辑
     */
    private DownloadListener listener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            //开通知
            getNotificationManager().notify(1, getNotification("Downloading...",
                    progress));
        }

        @Override
        public void onSuccess() {
            downloadTask = null;
            //	下载成功时将前台服务通知关闭,并创建一个下载成功的通知
            stopForeground(true);
            //通知配弹窗
            getNotificationManager().notify(1, getNotification("Download	Success",
                    -1));
            Toast.makeText(DownloadService.this, "Download	Success",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed() {
            downloadTask = null;
            //	下载失败时将前台服务通知关闭,并创建一个下载失败的通知
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download	Failed", -1));
            Toast.makeText(DownloadService.this, "Download	Failed",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            downloadTask = null;
            Toast.makeText(DownloadService.this, "Paused", Toast.LENGTH_SHORT).
                    show();
        }

        @Override
        public void onCanceled() {
            downloadTask = null;
            stopForeground(true);
            Toast.makeText(DownloadService.this, "Canceled", Toast.LENGTH_SHORT).
                    show();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //binder类，service和activity通信准备
    class DownloadBinder extends Binder {
        public void startDownload(String url) {
            if (downloadTask == null) {
                downloadUrl = url;
                //为downloadtask传入downloadlisten类型，同时被初始化
                downloadTask = new DownloadTask(listener);
                //用asynctask执行url
                downloadTask.execute(downloadUrl);
                //前台提示


                startForeground(1, getNotification("Downloading...", 0));
                Toast.makeText(DownloadService.this, "Downloading...", Toast.
                        LENGTH_SHORT).show();
            }
        }

        public void pauseDownload() {
            if (downloadTask != null) {
                //将暂停标志位置true
                downloadTask.pauseDownload();
            }
        }

        public void cancelDownload() {
            if (downloadTask != null) {
                downloadTask.cancelDownload();
            } else {
                //task没有启动但uri已经存在
                if (downloadUrl != null) {
                    //	取消下载时需将文件删除,并将通知关闭
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory
                            (Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + fileName);
                    //如果文件存在则删除
                    if (file.exists()) {
                        file.delete();
                    }
                    //发出取消notification
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    Toast.makeText(DownloadService.this, "Canceled",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, int progress) {

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        //产生notification
        Notification notification = builder.setContentTitle("This   is  download    title")
                .setContentText("This	is	download	text")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .build();


        NotificationChannel channel = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            channel = new NotificationChannel("1", "my_channel", NotificationManager.IMPORTANCE_DEFAULT);
            //设置提示灯
            channel.enableLights(true);
            channel.setLightColor(Color.green(1));
            //显示图标
            channel.canShowBadge();
            channel.setShowBadge(true);
            //必须设置channel
            builder.setChannelId("1");
        }
        /**
         * 不在这里添加startForeground
         */
//        startForeground(1, notification);


        if (progress >= 0) {
            //	当progress大于或等于0时才需显示下载进度
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }
}