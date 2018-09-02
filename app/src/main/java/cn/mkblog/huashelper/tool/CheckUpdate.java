package cn.mkblog.huashelper.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.mkblog.huashelper.api.AppApi;
import cn.mkblog.huashelper.bean.UpdateBean;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 检查更新
 */

public class CheckUpdate {

    public static Integer HAVE_UPDATE = 0x30101;    // 有更新
    public static Integer NO_UPDATE = 0x30102;      // 没更新
    public static Integer ERR_UPDATE = 0x30103;     // 检查更新出错

    public static String APK_NAME = "huasHelper.apk";     // 下载的包名

    public static void check(final Context mContext, final Handler mHandler, final Boolean silent) {
        // 获取当前日期
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date curDate = new Date(System.currentTimeMillis());
        final String toDay = formatter.format(curDate);

        if(silent) {    // 后台检测更新一天一次
            // 判断更新是否需要
            String lastUpdate = (String) DataSave.get(mContext, "lastUpdate", "");

            Log.i("检查更新", "上次检查更新日期" + lastUpdate);

            // 今天已检查更新
            if(lastUpdate != null && lastUpdate.equals(toDay)) return;
        } else {
            Toast.makeText(mContext, "检查更新中...", Toast.LENGTH_SHORT).show();
        }

        // 初始化 retrofit2
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(AppApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        AppApi appApi = retrofit2.create(AppApi.class);

        // 发出请求
        Call<UpdateBean> call = appApi.checkUpdate();
        call.enqueue(new Callback<UpdateBean>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<UpdateBean> call, Response<UpdateBean> response) {
                final UpdateBean updateInfo = response.body();
                Log.d("检查更新", "获取成功，服务器最新版本：" + updateInfo.getVersion());

                // 获取 App 版本信息
                PackageInfo info = getAppVersion(mContext);
                if(info == null) {
                    if(silent) return;  // 静默模式下不提示
                    Message message = new Message();
                    message.what = ERR_UPDATE;
                    message.obj = "获取本地版本信息失败";
                    mHandler.sendMessage(message);
                    return;
                }

                DataSave.put(mContext, "lastUpdate", toDay);   // 记录最后一次检查更新日期

                if(info.versionCode < updateInfo.getCode()) {   // 有更新
                    // 静默模式下已忽略版本不再提示
                    if(silent && ((int) DataSave.get(mContext, "ignoreVersion", 0) == updateInfo.getCode())) return;

                    MkDialog.Builder dialog = new MkDialog.Builder(mContext);
                    dialog
                        .setTitle("新版来啦~")
                        .setMessage(updateInfo.getDescription().replace("\\n", "\n"))
                        .setNegativeButton("忽略", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // 这个版本更新将不再提示
                                DataSave.put(mContext, "ignoreVersion", updateInfo.getCode());
                            }
                        })
                        .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // 新建一个下载
                                new DownloadAsyncTask(
                                        mContext,
                                        mHandler,
                                        APK_NAME)
                                        .execute(updateInfo.getUrl());

                                Toast.makeText(mContext, "更新下载中...", Toast.LENGTH_SHORT).show();

                                Message message = new Message();
                                message.what = HAVE_UPDATE;
                                message.obj = updateInfo.getDescription();
                                mHandler.sendMessage(message);
                                dialog.dismiss();   // 关闭弹窗
                            }
                        });
                    dialog.create().show();
                } else {    // 没更新
                    if(silent) return;  // 静默模式下不提示
                    mHandler.sendEmptyMessage(NO_UPDATE);
                }

            }

            @Override
            public void onFailure(Call<UpdateBean> call, Throwable t) {
                if(silent) return;  // 静默模式下不提示
                // 失败
                Message message = new Message();
                message.what = ERR_UPDATE;
                message.obj = Tools.connErrHandle(t);
                mHandler.sendMessage(message);
                Log.d("检查更新", "失败，错误原因：" + t.toString());
            }
        });
    }       // check

    /**
     * 获取app版本信息
     */
    public static PackageInfo getAppVersion(Context mContext) {
        PackageManager manager = mContext.getPackageManager();
        try {
            return manager.getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 安装APK
     */
    public static void InstallApk(Context mContext) {
        InstallApk(mContext, APK_NAME);
    }

    /**
     * 安装APK
     */
    public static void InstallApk(Context mContext, String apkName) {
        File file = new File(Environment.getExternalStorageDirectory(), apkName);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

}
