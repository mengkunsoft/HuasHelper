package cn.mkblog.huashelper.tool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * 下载图片并保存在 SD 卡
 * <p>
 * 参考：
 * http://blog.csdn.net/u011340932/article/details/38819307
 * http://blog.csdn.net/zdy10326621/article/details/46430121
 */

public class DownImage {

    public static Integer OK = 0x30301;    // 下载完了

    private static Handler mHandler;

    public static void SaveImage(String path, String url, Handler handler) {
        String savePath = Environment.getExternalStorageDirectory().getPath() + "/huasHelper/" + path;
        mHandler = handler;
        new Task().execute(url, savePath);
    }

    /**
     * 获取网络图片
     *
     * @param imgUrl 图片网络地址
     * @return Bitmap 返回位图
     */
    public static Bitmap GetImageInputStream(String imgUrl) {
        URL url;
        HttpURLConnection connection;
        Bitmap bitmap = null;
        try {
            url = new URL(imgUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000); //超时设置
            connection.setDoInput(true);
            connection.setUseCaches(false); //设置不使用缓存
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 异步线程下载图片
     */
    static class Task extends AsyncTask<String, Integer, Void> {

        protected Void doInBackground(String... params) {
            Bitmap bitmap = GetImageInputStream(params[0]);
            save(bitmap, params[1]);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // todo:告诉程序已下载完毕
            mHandler.sendEmptyMessage(OK);
        }
    }

    /**
     * 保存位图到本地
     *
     * @param bitmap 图片文件
     * @param path   本地路径
     */
    public static void save(Bitmap bitmap, String path) {
        File file = new File(path);
        FileOutputStream fileOutputStream;

        /*
        // 文件夹不存在，则创建它
        if(!file.exists()){
            file.mkdir();
        }
         **/
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();

        try {
            fileOutputStream = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
