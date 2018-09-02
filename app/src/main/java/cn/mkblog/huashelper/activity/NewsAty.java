package cn.mkblog.huashelper.activity;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorFragmentPagerAdapter;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import cn.mkblog.huashelper.fragment.NewsListFrag;
import cn.mkblog.huashelper.R;

// 新闻列表页
public class NewsAty extends AppCompatActivity {
    private Toolbar toolbar;    // 顶部工具栏

    private IndicatorViewPager indicatorViewPager;
    private LayoutInflater inflate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_news);

        initView();

    }

    private void initView() {
        // ToolBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("文理新闻");
        // setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();    // 返回上一页
            }
        });

        // 初始化 Tab 区域
        ViewPager viewPager = (ViewPager) findViewById(R.id.moretab_viewPager);
        ScrollIndicatorView scrollIndicatorView = (ScrollIndicatorView) findViewById(R.id.moretab_indicator);

        scrollIndicatorView.setOnTransitionListener(
                new OnTransitionTextListener().
                        setColor(0xFF2196F3, Color.GRAY).
                        setSize(14, 14)
        );

        // 底部的条条
        scrollIndicatorView.setScrollBar(
                new ColorBar(this, 0xFF2196F3, 4));


        // 限定预加载的页面数
        viewPager.setOffscreenPageLimit(4);

        indicatorViewPager = new IndicatorViewPager(scrollIndicatorView, viewPager);
        inflate = LayoutInflater.from(getApplicationContext());
        indicatorViewPager.setAdapter(new NewsTabAdapter(getSupportFragmentManager()));
    }

    private class NewsTabAdapter extends IndicatorFragmentPagerAdapter {
        public NewsTabAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        private String[] newsType = {"文理要闻", "综合新闻", "通知公告", "媒体形象", "学术动态"};

        @Override
        public int getCount() {
            return newsType.length;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.tab_top, container, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(newsType[position]);

            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            NewsListFrag fragment = new NewsListFrag();
            Bundle bundle = new Bundle();
            bundle.putInt(NewsListFrag.INTENT_INT_INDEX, position);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getItemPosition(Object object) {
            // http://lovelease.iteye.com/blog/2107296
            // 这是ViewPager适配器的特点,有两个值 POSITION_NONE，POSITION_UNCHANGED，默认就是POSITION_UNCHANGED,
            // 表示数据没变化不用更新.notifyDataChange的时候重新调用getViewForPage
            return PagerAdapter.POSITION_NONE;
        }
    }

}