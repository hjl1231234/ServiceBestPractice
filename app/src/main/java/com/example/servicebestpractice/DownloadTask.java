package com.example.servicebestpractice;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//泛型三参数传入后台任务 显示单位 显示结果
public class DownloadTask extends AsyncTask<String, Integer, Integer> {
    /**
     * 四种状态 成功 失败 暂停 取消
     */
    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;
    /**
     * 下载接口
     */
    private DownloadListener listener;
    /**
     * 取消 暂停标志位
     */
    private boolean isCanceled = false;
    private boolean isPaused = false;
    /**
     * 最终进度
     */
    private int lastProgress;

    public DownloadTask(DownloadListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    /**
     * 这个函数一直被轮训
     * @param params
     * @return
     */
    @Override
    protected Integer doInBackground(String... params) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file = null;
        try {
            long downloadedLength = 0;
            //	记录已下载的文件长度
            /**
             * 第0个参数是url，下载的文件名，下载目录路径，
             */
            String downloadUrl = params[0];
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory = Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS).getPath();
//          将目录路径和文件名拼接成全路径
            file = new File(directory + fileName);
//            如果有文件再记录已下载的大小
            if (file.exists()) {
                downloadedLength = file.length();
            }
//            得到下载文件总大小。
            long contentLength = getContentLength(downloadUrl);
            if (contentLength == 0) {
                return TYPE_FAILED;
            } else if (contentLength == downloadedLength) {
                //	已下载字节和文件总字节相等,说明已经下载完成了
                return TYPE_SUCCESS;
            }

            //OKHTTP客户端
            OkHttpClient client = new OkHttpClient();

//            发出请求
            Request request = new Request.Builder()
                    //	断点下载,指定从哪个字节开始下载
                    .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                    .url(downloadUrl)
                    .build();
            //响应
            Response response = client.newCall(request).execute();
//            如果有响应就读入字节流
            if (response != null) {
                is = response.body().byteStream();
                //断点续传
                savedFile = new RandomAccessFile(file, "rw");
                savedFile.seek(downloadedLength);
                //	跳过已下载的字节
                //缓冲区
                byte[] b = new byte[1024];
                int total = 0;
                int len;
//                只要流中还有数据
                while ((len = is.read(b)) != -1) {
                    //如果取消
                    if (isCanceled) {
                        return TYPE_CANCELED;
                    } else if (isPaused) {
//                        如果暂停
                        return TYPE_PAUSED;
                    } else {
//                        如果运行，累加长度，并保存到savedfile
                        total += len;
                        savedFile.write(b, 0, len);
                        //	计算已下载的百分比
                        int progress = (int) ((total + downloadedLength) * 100 /
                                contentLength);
                        publishProgress(progress);
                    }
                }
                //关闭响应体
                response.body().close();
                return TYPE_SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭流、randomfile、file、
            try {
                if (is != null) {
                    is.close();
                }
                if (savedFile != null) {
                    savedFile.close();
                }
                if (isCanceled && file != null) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //没有在try中返回则视为failed
        return TYPE_FAILED;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        //谁给values传值了?
        int progress = values[0];
        if (progress > lastProgress) {
            listener.onProgress(progress);
            lastProgress = progress;
        }
    }

    @Override
    protected void onPostExecute(Integer status) {
        switch (status) {
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
            default:
                break;
        }
    }

    public void pauseDownload() {
        isPaused = true;
    }

    public void cancelDownload() {
        isCanceled = true;
    }

    private long getContentLength(String downloadUrl) throws IOException {

        //okhttp客户端
        OkHttpClient client = new OkHttpClient();
        //请求对象
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        //响应对象
        Response response = client.newCall(request).execute();
        //有响应则会计算长度。
        if (response != null && response.isSuccessful()) {
            long contentLength = response.body().contentLength();
            response.body().close();
            return contentLength;
        }
        return 0;
    }
}