package cn.mkblog.huashelper.tool;

/*
 * 异步下载类
 */

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadAsyncTask extends AsyncTask<String, Integer, Void>{

    public static Integer NET_ERR = 0x30201;    // 网络错误
    public static Integer FINISH = 0x30202;      // 下载完成
    public static Integer PROGRESS = 0x30203;     // 进度变化
    public static Integer URL_ERR = 0x30204;     // 地址错误

    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private Handler mHandler;
    private ProgressDialog dialog;
    private String fileName;

    public DownloadAsyncTask(Context mContext, Handler mHandler, String fileName) {
        super();
        this.mContext = mContext;
        this.mHandler = mHandler;
        this.fileName = fileName;
    }

    /**
     * 显示下载进度条
     */
    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(mContext);
        dialog.setMax(100);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("文件下载中...");
        dialog.show();
    }

    /**
     * 后台下载文件
     */
    @Override
    protected Void doInBackground(String... arg0) {
        try {
            URL url=new URL(arg0[0]);
            HttpURLConnection connection=(HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.connect();
            InputStream inputStream=connection.getInputStream();
            int length = connection.getContentLength();

            File file = new File(Environment.getExternalStorageDirectory(), this.fileName);

            //如果文件存在就删除重新下载
            if (file.exists()) {
                Log.i("文件下载", "删除文件成功 - " + file.delete());
            }
            FileOutputStream outputStream=new FileOutputStream(file);

            int count = 0;
            int num_read;
            byte[] buffer = new byte[1024];
            int progress;
            int last_progress=0;
            while(true){
                num_read=inputStream.read(buffer);
                count += num_read;
                progress = (int)(((float)count/length)*100);
                Log.i("文件下载", "progress=="+progress+" lastprogress=="+last_progress);

                if (progress >= last_progress+1) {   // 进度变化
                    last_progress = progress;
                    publishProgress(progress);
                    Message message = new Message();
                    message.what = PROGRESS;
                    message.arg1 = progress;
                    mHandler.sendMessage(message);
                }

                if (num_read<=0) {   // 下载完成
                    mHandler.sendEmptyMessage(FINISH);
                    break;
                }
                outputStream.write(buffer, 0, num_read);
            }
            connection.disconnect();
            outputStream.close();
            inputStream.close();

        } catch (MalformedURLException e) {     // 地址错误
            mHandler.sendEmptyMessage(URL_ERR);
            e.printStackTrace();
        } catch (IOException e) {           // 网络错误
            mHandler.sendEmptyMessage(NET_ERR);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        dialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Void result) {
        dialog.dismiss();
    }
}