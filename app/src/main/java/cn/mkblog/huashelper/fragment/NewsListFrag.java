package cn.mkblog.huashelper.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.shizhefei.fragment.LazyFragment;

import java.util.LinkedList;
import java.util.List;

import cn.mkblog.huashelper.activity.WebAty;
import cn.mkblog.huashelper.adapter.NewsAdapter;
import cn.mkblog.huashelper.api.AppApi;
import cn.mkblog.huashelper.bean.News;
import cn.mkblog.huashelper.bean.NewsBean;
import cn.mkblog.huashelper.R;
import cn.mkblog.huashelper.tool.Tools;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 新闻列表懒加载 fragment
public class NewsListFrag extends LazyFragment {
    private Context mContext;

    private int tabIndex;       // tab编号
    public static final String INTENT_INT_INDEX = "intent_int_index";

    private List<News> newsData = null;
    private NewsAdapter mAdapter = null;

    private ListView listNews;              // 新闻列表
    private View listFoot, listLoading;
    private ProgressBar progressBar;    // 加载进度条

    private int loadPage = 1, lastNewsID = 99999999;   // 加载的新闻页码，最后一条新闻的ID

    private loadStatus newsStatus;      // 新闻加载状况

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.frag_news_list);

        mContext = getApplicationContext();
        tabIndex = getArguments().getInt(INTENT_INT_INDEX);

        newsStatus = loadStatus.OK;

        initViews();    // 控件绑定
        loadNews();     // 加载新闻列表
    }

    @Override
    public void onDestroyViewLazy() {
        super.onDestroyViewLazy();
    }

    // 绑定控件
    private void initViews() {
        progressBar = (ProgressBar) findViewById(R.id.pb_news_loading);
        listNews = (ListView) findViewById(R.id.list_news);
        listFoot = getActivity().getLayoutInflater().inflate(R.layout.list_loading, null);
        listLoading = listFoot.findViewById(R.id.list_loading);
        listNews.addFooterView(listFoot);
        newsData = new LinkedList<>();
        mAdapter = new NewsAdapter((LinkedList<News>) newsData, mContext);
        listNews.setAdapter(mAdapter);

        // 新闻列表点击
        listNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i >= newsData.size()) return;    // 防止下表越界

                News news = newsData.get(i);

                if (news.getnType() == 0) {   // 失败的情况
                    newsData.remove(newsData.size() - 1);   // 移除加载失败提示语
                    newsStatus = loadStatus.OK;     // 再给一次机会
                    loadNews();
                    return;
                }

                // 合成新闻页面URL
                String url = AppApi.BASE_URL + "news/view.php?types=" + news.getnType() + "&id=" + news.getnID();

                // 跳转新闻浏览界面
                Intent intent = new Intent(mContext, WebAty.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        // 添加一个滚动监听
        listNews.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount) {
                    loadNews();     // 滚动加载更多新闻
                }
            }
        });
    }

    // 加载新闻列表
    private void loadNews() {
        if (newsStatus != loadStatus.OK) return;

        newsStatus = loadStatus.LOADING;
        if (loadPage == 1) {     // 加载第一页
            newsData.clear();   // 清空列表
            progressBar.setVisibility(View.VISIBLE);
            listNews.setVisibility(View.GONE);
        }
        listLoading.setVisibility(View.VISIBLE);

        // 获取新闻列表数据
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(AppApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        AppApi appApi = retrofit2.create(AppApi.class);

        Call<NewsBean> call = appApi.newsList(tabIndex + 1015, loadPage);
        call.enqueue(new Callback<NewsBean>() {
            @Override
            public void onResponse(Call<NewsBean> call, Response<NewsBean> response) {
                NewsBean newsBean = response.body();

                // 隐藏进度条，显示列表
                progressBar.setVisibility(View.GONE);
                listLoading.setVisibility(View.GONE);
                listNews.setVisibility(View.VISIBLE);


                if (newsBean.code == 200) {
                    // 两次获取到的新闻 ID 是一样的，则加载完成
                    if (newsBean.items.get(newsBean.items.size() - 1).id == lastNewsID) {
                        Toast.makeText(mContext, "加载完啦！", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 逐项显示到界面中
                    for (int i = 0; i < newsBean.items.size(); i++) {
                        newsData.add(new News(newsBean.items.get(i).title,
                                newsBean.items.get(i).data,
                                newsBean.items.get(i).types,
                                newsBean.items.get(i).id));

                    }
                    lastNewsID = newsBean.items.get(newsBean.items.size() - 1).id;
                    loadPage++;     // 页码加一
                    newsStatus = loadStatus.OK;
                } else {    // 状态码不为 200 则是读取失败
                    newsReadErr();
                    Toast.makeText(mContext, "读取失败-" + newsBean.msg, Toast.LENGTH_SHORT).show();
                }

                // 刷新显示
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<NewsBean> call, Throwable t) {
                newsReadErr();
                Toast.makeText(mContext, "读取失败-" + Tools.connErrHandle(t), Toast.LENGTH_SHORT).show();
                Log.d("新闻读取失败", "错误原因：" + t.toString());
            }
        });
    }

    // 显示新闻读取错误信息
    private void newsReadErr() {
        newsData.add(new News("新闻列表读取失败", "[点击重试]", 0, 0));
        newsStatus = loadStatus.ERR;
        mAdapter.notifyDataSetChanged();
    }

    // 新闻加载状态
    private enum loadStatus {
        LOADING,        // 加载中
        ERR,            // 遇到了错误
        OK,             // 正常加载
        FINISHED        // 所有的新闻都加载完了
    }
}
