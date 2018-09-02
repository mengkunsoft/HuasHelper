package cn.mkblog.huashelper.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cn.mkblog.huashelper.adapter.ScoreAdapter;
import cn.mkblog.huashelper.api.AppApi;
import cn.mkblog.huashelper.bean.ScoreBean;
import cn.mkblog.huashelper.R;
import cn.mkblog.huashelper.tool.DataSave;
import cn.mkblog.huashelper.tool.Tools;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CheckScoresAty extends BaseAty {
    private Context mContext;

    private ExpandableListView scoreLists;
    private List<ScoreBean.ScoresBean> scoreData = null;
    private ScoreAdapter mAdapter = null;

    private ProgressBar pbScoreLoading;

    private TextView sInfo, sName, sID;

    private String stuCookie;

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            default:
        }
    }

    @Override
    public void initParams(Bundle params) {
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.aty_check_scores;
    }

    @Override
    public void initView(View view) {
        pbScoreLoading = $(R.id.pb_score_loading);  // 进度条
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {
        this.mContext = mContext;

        // 获取 Cookie
        stuCookie = (String) DataSave.get(mContext, "stu_cookie", "");
        if (stuCookie == null) {     // 没有获取到 Cookie，弹出登录教务系统
            jwLogin();
            return;
        }

        setTitle("成绩查询");

        // 初始化列表
        scoreData = new LinkedList<>();
        scoreLists = (ExpandableListView) findViewById(R.id.score_lists);

        final LayoutInflater inflater = LayoutInflater.from(this);
        View headView = inflater.inflate(R.layout.list_score_head, null, false);
        scoreLists.addHeaderView(headView);

        mAdapter = new ScoreAdapter((LinkedList<ScoreBean.ScoresBean>) scoreData, mContext);
        scoreLists.setAdapter(mAdapter);

        sInfo = (TextView) headView.findViewById(R.id.tv_score_info);
        sName = (TextView) headView.findViewById(R.id.tv_score_sname);
        sID = (TextView) headView.findViewById(R.id.tv_score_sno);

        // sName.setText((String) DataSave.get(mContext, "stu_name", ""));
        sID.setText((String) DataSave.get(mContext, "stu_id", ""));

        // 获取分数
        loadScores();
    }

    // 查询分数
    private void loadScores() {
        // 初始化 retrofit2
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(AppApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        AppApi appApi = retrofit2.create(AppApi.class);

        // 发出请求
        Call<ScoreBean> call = appApi.getScore(stuCookie);
        call.enqueue(new Callback<ScoreBean>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ScoreBean> call, Response<ScoreBean> response) {
                ScoreBean scores = response.body();
                if (scores.getCode() == 200) {
                    pbScoreLoading.setVisibility(View.INVISIBLE);
                    scoreLists.setVisibility(View.VISIBLE);

                    // 姓名
                    sName.setText(scores.getStuName());

                    // 学分绩点
                    sInfo.setText(
                            "学分：" + scores.getScoreNow() + "/" + scores.getScoreAll() + "\n" +
                                    "绩点：" + scores.getGPA());

                    // 逐项显示到界面中
                    for (int i = 0; i < scores.getScore().size(); i++) {
                        scoreData.add(
                                new ScoreBean.ScoresBean(
                                        scores.getScore().get(i).getNo(),
                                        scores.getScore().get(i).getTerm(),
                                        scores.getScore().get(i).getId(),
                                        scores.getScore().get(i).getName(),
                                        scores.getScore().get(i).getScore(),
                                        scores.getScore().get(i).getCredit(),
                                        scores.getScore().get(i).getPeriod(),
                                        scores.getScore().get(i).getMethods(),
                                        scores.getScore().get(i).getProperty(),
                                        scores.getScore().get(i).getTypes(),
                                        scores.getScore().get(i).getExamterm(),
                                        scores.getScore().get(i).getExamtypes()
                                ));

                    }

                    // 倒序
                    Collections.reverse(scoreData);

                    // 刷新显示
                    mAdapter.notifyDataSetChanged();

                } else if (scores.getCode() == 4101) {   // 需要登录才能使用
                    jwLogin();  // 登录教务系统
                } else {
                    // 其它错误
                    showToast("分数获取失败 - " + scores.getCode() + "\n" + scores.getMsg());
                }
            }

            @Override
            public void onFailure(Call<ScoreBean> call, Throwable t) {
                showToast(Tools.connErrHandle(t));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_score, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int groupCount = scoreLists.getCount() - 1;
        switch (item.getItemId()) {
            case R.id.menu_score_expand:    // 全部展开
                for (int i = 0; i < groupCount; i++) {
                    scoreLists.expandGroup(i);
                }
                break;

            case R.id.menu_score_collapse:  // 全部收起
                for (int i = 0; i < groupCount; i++) {
                    scoreLists.collapseGroup(i);
                }
                break;

            case R.id.menu_score_account:   // 切换账号
                jwLogin();
                break;

            default:
        }
        return true;
    }

}
