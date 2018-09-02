package cn.mkblog.huashelper.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.LinkedList;
import java.util.List;

import cn.mkblog.huashelper.adapter.SunAdapter;
import cn.mkblog.huashelper.api.AppApi;
import cn.mkblog.huashelper.bean.SunList;
import cn.mkblog.huashelper.bean.SunListBean;
import cn.mkblog.huashelper.R;
import cn.mkblog.huashelper.tool.Tools;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SunListAty extends BaseAty {
    private Context mContext;

    private ListView listSun;
    private ProgressBar pbSunLoading;
    private View listFoot, listLoading;

    private List<SunList> sunData = null;
    private SunAdapter mAdapter = null;

    private int loadPage = 1;       // 加载页码
    private String laseItemID = "";     // 上一次的最后一条 ID
    private loadStatus sunLoadStatus;   // 加载状态

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            default:
        }
    }

    @Override
    public void initParams(Bundle params) {
        // 解析bundle内容或者设置是否旋转，沉浸，全屏
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.aty_sunlist;
    }

    @Override
    public void initView(View view) {
        listSun = $(R.id.sun_list);             // 列表
        pbSunLoading = $(R.id.pb_sun_loading);  // 加载中进度条
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {
        this.mContext = mContext;

        setTitle("阳光服务");

        listFoot = getLayoutInflater().inflate(R.layout.list_loading, null);
        listLoading = listFoot.findViewById(R.id.list_loading);
        listSun.addFooterView(listFoot);

        sunData = new LinkedList<>();
        mAdapter = new SunAdapter((LinkedList<SunList>) sunData, mContext);
        listSun.setAdapter(mAdapter);
        // 监听项目点击
        listSun.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i >= sunData.size()) return;    // 防止下表越界

                SunList sunInfo = sunData.get(i);

                // 失败的情况
                if (sunInfo.getID().equals("")) {
                    sunData.remove(sunData.size() - 1);   // 移除加载失败提示语
                    sunLoadStatus = loadStatus.OK;     // 再给一次机会
                    loadSunList();
                    return;
                }

                // 跳转至帖子详情页
                Bundle mBundle = new Bundle();
                mBundle.putString("id", sunInfo.getID());
                mBundle.putString("no", sunInfo.getNo());
                startActivity(SunViewAty.class, mBundle);
            }
        });
        // 监听滚动
        listSun.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount) {
                    loadSunList();      // 滚动加载
                }
            }
        });

        // 加载阳光帖子信息
        loadSunList();

        sunLoadStatus = loadStatus.OK;

    }

    // 加载帖子列表
    private void loadSunList() {
        if (sunLoadStatus != loadStatus.OK) return;

        sunLoadStatus = loadStatus.LOADING;
        if (loadPage == 1) {     // 加载第一页
            sunData.clear();   // 清空列表
            pbSunLoading.setVisibility(View.VISIBLE);
            listSun.setVisibility(View.GONE);
        }
        listLoading.setVisibility(View.VISIBLE);

        // 获取帖子列表数据
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(AppApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        AppApi appApi = retrofit2.create(AppApi.class);

        Call<SunListBean> call = appApi.sunList(loadPage);
        call.enqueue(new Callback<SunListBean>() {
            @Override
            public void onResponse(Call<SunListBean> call, Response<SunListBean> response) {
                SunListBean sunBean = response.body();

                // 隐藏进度条，显示列表
                pbSunLoading.setVisibility(View.GONE);
                listLoading.setVisibility(View.GONE);
                listSun.setVisibility(View.VISIBLE);

                if (sunBean.getCode() == 200) {
                    // 两次获取到的最后一项 ID 值是一样的，则加载完成
                    if (sunBean.getItems().get(sunBean.getItems().size() - 1).getId().equals(laseItemID)) {
                        showToast("加载完啦！");
                        return;
                    }

                    // 逐项显示到界面中
                    for (int i = 0; i < sunBean.getItems().size(); i++) {
                        sunData.add(new SunList(
                                sunBean.getItems().get(i).getTitle(),
                                sunBean.getItems().get(i).getData(),
                                sunBean.getItems().get(i).getTypes(),
                                sunBean.getItems().get(i).getId(),
                                sunBean.getItems().get(i).getNo(),
                                sunBean.getItems().get(i).getAuth(),
                                sunBean.getItems().get(i).getStatus()
                        ));

                    }
                    laseItemID = sunBean.getItems().get(sunBean.getItems().size() - 1).getId();
                    loadPage++;     // 页码加一
                    sunLoadStatus = loadStatus.OK;
                    // 刷新显示
                    mAdapter.notifyDataSetChanged();
                } else {    // 状态码不为 200 则是读取失败
                    sunReadErr();
                    showToast("读取失败-" + sunBean.getMsg());
                }
            }

            @Override
            public void onFailure(Call<SunListBean> call, Throwable t) {
                sunReadErr();
                showToast(Tools.connErrHandle(t));
            }
        });
    }

    // 读取出错
    private void sunReadErr() {
        sunData.add(new SunList("帖子列表读取失败", "", "点我重试", "", "", "", ""));
        sunLoadStatus = loadStatus.ERR;
        mAdapter.notifyDataSetChanged();
    }

    // 列表加载状态
    private enum loadStatus {
        LOADING,        // 加载中
        ERR,            // 遇到了错误
        OK,             // 正常加载
        FINISHED        // 所有的内容都加载完了
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sunlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sun_consult: // 我要咨询
                openWeb("http://www.huas.cn:316/web/regist3.jsp");
                break;

            case R.id.menu_sun_appeal:  // 我要求助
                openWeb("http://www.huas.cn:316/web/regist31.jsp");
                break;

            case R.id.menu_sun_praise:  // 我要表扬
                openWeb("http://www.huas.cn:316/web/regist41.jsp");
                break;

            case R.id.menu_sun_suggestions: // 我要建议
                openWeb("http://www.huas.cn:316/web/regist42.jsp");
                break;

            case R.id.menu_sun_complaint:   // 我要投诉
                openWeb("http://www.huas.cn:316/web/regist30.jsp");
                break;

            default:
                showToast("功能建设中");
        }
        return true;

    }
}
