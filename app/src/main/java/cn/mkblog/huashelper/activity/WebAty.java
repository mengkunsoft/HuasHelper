package cn.mkblog.huashelper.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.mkblog.huashelper.R;

import static android.view.KeyEvent.KEYCODE_BACK;

/**
 * 程序内置浏览器
 */
public class WebAty extends BaseAty {
    private final static String TAG = WebAty.class.getSimpleName();
    private Context mContext;

    private WebView webView;
    private ProgressBar webProgress;

    private String loadUrl, webPost = null;

    private TextView tvLoading;

    @Override
    public void widgetClick(View v) {
    }

    @Override
    public void initParams(Bundle params) {
        // 获取传来的网址
        if (params != null && params.containsKey("url")) {
            loadUrl = params.getString("url");
            if (params.containsKey("post")) {
                webPost = params.getString("post");
            }
        } else {
            loadUrl = "https://www.baidu.com";
        }
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.aty_web;
    }

    @Override
    public void initView(View view) {
        webView = $(R.id.web_view);
        webProgress = $(R.id.web_progress);
        tvLoading = $(R.id.tv_web_loading);
    }

    @Override
    public void setListener() {
    }

    @Override
    public void doBusiness(final Context mContext) {
        this.mContext = mContext;
        setTitle("");

        initWeb();  // 初始化 webview
    }

    // 初始化 web
    @SuppressLint("SetJavaScriptEnabled")
    private void initWeb() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 设置在webView点击打开的新网页在当前界面显示,而不跳转到新的浏览器中

                if (url == null) return false;

                // 正常的内容，打开
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    view.loadUrl(url);
                    return true;    // 返回true自己处理，返回false不处理
                }

                // 不显示贴吧打开提示
                if (url.startsWith("tbfrs://") || url.startsWith("tbpb://")) return true;

                try {   // 调用第三方应用
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                } catch (Exception e) { // 防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    return false;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // 网页开始加载，显示进度条
                webProgress.setProgress(0);
                webProgress.setVisibility(View.VISIBLE);
                tvLoading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 网页加载完毕，隐藏进度条
                webProgress.setVisibility(View.INVISIBLE);
                tvLoading.setVisibility(View.GONE);

                // 去除百度贴吧的小广告~
                if (url.contains("tieba.baidu.com/"))
                    webView.loadUrl("javascript:{function img_view(f,i){$('#imgdv').attr('src',f);$('#imgd,#dback').show();$('#imgd').css('top',($(window).height()-$('#imgdv').attr('height'))/2+'px');$('#imgd').css('left',($(window).width()-$('#imgdv').attr('width'))/2+'px');$('#oiv').unbind();$('#oiv').click(function(){window.open(i)})}function sub_url(u){var i=decodeURIComponent(u);i=i.substr(i.indexOf('http://'),i.length-1);return i}function global_init(){$('.fixed_bar,#index-app-overlay,.appPromote,.j_footer,.j_maker_prompt_back,.dia_mask,.dia_wrapper ,.frs_sign_in_show,.icon_tieba_logo,.region_head,.forum_recommend_w,#index-tuijian-wrap,.j_tab_con_search,.j_tab_selected,.j_tab_discovery,.j_cool_game,.light_top_ext_area,.addbodybottom,.no_mean,#j_light_see_index,.medias_modal,.tag_link,.special-thread,.u9_recommend_news_wrapper,#more_content,.icon_tieba_logo,.iSlider-wrapper-container-hot-thread').remove();const ima_view='<div id=\"imgd\" style=\"position:fixed;z-index:999;display:none\"><img id=\"imgdv\" src=\"\" style=\"transform:scale(0.8,0.8);\"/><botton id=\"oiv\" style=\"position:fixed;bottom:20px;left:176px;z-index:999;color:#FFF;\">查看原图</button></div><div id=\"dback\" style=\"background:#000000;opacity:0.8;height:100%;width:100%;z-index:998;position:fixed;top:0px;left:0px;display:none\"></div>';$('#imgd,#imgdv,#oiv,#dback').remove();$('#glob').before(ima_view);$('#imgdv').unbind();$('#imgdv').click(function(){$('#imgd').hide();$('#dback').hide()});$('body').bind('DOMNodeInserted',function(){if($('.dia_wrapper').length!==0){$('.dia_wrapper').remove();$('.dia_mask').remove()}if($('.j_make_prompt').length!==0){$('.j_make_prompt_back').remove()}})}function home_init(){$('.expand-all').click();$('.j_tab_index_head').css('top','0px');$('.j_top_bar').css({'position':'fixed','width':'100%','z-index':'99'});$('.j_tab_index').css('top','44px');$('.j_tab_index_head').css('top','45px')}function bar_init(){$('#top_kit').css({'position':'fixed','width':'100%'});$('.j_light_post_entrance').css('bottom','5px');$('.j_click_stats').parent().remove();$('#tlist').bind('DOMNodeInserted',function(){if($('.j_click_stats').length!==0){$('.j_click_stats').parent().remove();$('.special-thread').remove()}})}function tie_init(){$('.j_click_stats,.pic_icon').parent().remove();$('.class_hide_flag').css('display','block');$('#pb_imgs_weixin').prev().remove();$('#pb_imgs').unbind();$('a.video').unbind();$('a.video').click(function(){window.open($(this).attr('data-vsrc'))});$('div[class^=\"pb_imgs_\"] img').unbind();$('div[class^=\"pb_imgs_\"] img').click(function(){var f_url=$(this).attr('src');var i_url=sub_url(f_url);img_view(f_url,i_url)});$('#pblist').bind('DOMNodeInserted',function(){if($('#more_content').length!==0)$('#more_content').remove();if($('.j_click_stats').length!==0)$('.j_click_stats').parent().remove();$('.class_hide_flag').css('display','block')})}if(location.pathname=='/'){home_init()}else if(location.pathname=='/f'||location.pathname=='/mo/q/m'){bar_init()}else{tie_init()}global_init();$('body').css('margin-top', '45px');}");
            }
        });     // setWebViewClient

        webView.setWebChromeClient(new mkWebChrome());   // 重写 WebChromeClient

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);    // 启用 js 功能
        settings.setUserAgentString(settings.getUserAgentString() + " huasTools/0.1.0"); // 设置浏览器 UserAgent

        // 设置自适应屏幕，两者合用
        settings.setUseWideViewPort(true); // 将图片调整到适合webview的大小
        settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        // 缩放操作
        settings.setSupportZoom(true); // 支持缩放，默认为true。是下面那个的前提。
        settings.setBuiltInZoomControls(true); // 设置内置的缩放控件。若为false，则该WebView不可缩放
        settings.setDisplayZoomControls(false); // 隐藏原生的缩放控件

        // 其他细节操作
        settings.setCacheMode(WebSettings.LOAD_DEFAULT); // 缓存
        settings.setAllowFileAccess(true); // 设置可以访问文件
        settings.setJavaScriptCanOpenWindowsAutomatically(true); // 支持通过JS打开新窗口
        settings.setLoadsImagesAutomatically(true);     // 支持自动加载图片
        settings.setDefaultTextEncodingName("utf-8");   // 设置编码格式
        settings.setDomStorageEnabled(true);    // 本地存储？

        // 加载要访问的页面
        if (webPost == null) {
            webView.loadUrl(loadUrl);
        } else {
            webView.postUrl(loadUrl, webPost.getBytes());
        }
    }   // initWeb


    // 重写 WebChromeClient
    private class mkWebChrome extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            // 加载进度变动，刷新进度条
            webProgress.setProgress(newProgress);
            if (newProgress != 0) {
                tvLoading.setVisibility(View.GONE);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            setTitle(title);    // 改变标题
        }
    }   // 重写  WebChromeClient


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_web_refresh:     // 刷新
                webView.reload();
                break;

            case R.id.menu_web_share:       // 分享
                Intent textIntent = new Intent(Intent.ACTION_SEND);
                textIntent.setType("text/plain");
                textIntent.putExtra(Intent.EXTRA_TEXT,
                        webView.getTitle() + "\n" + webView.getUrl() + "\n(分享自文理助手App)");
                startActivity(Intent.createChooser(textIntent, "分享"));
                break;

            case R.id.menu_web_copy_link:   // 复制链接地址
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                assert cm != null;
                cm.setPrimaryClip(ClipData.newPlainText(null, webView.getUrl()));
                showToast("链接地址已复制到剪切板");
                break;

            case R.id.menu_web_open_local:  // 系统浏览器打开
                Intent intent = new Intent(Intent.ACTION_VIEW);    // 为Intent设置Action属性
                intent.setData(Uri.parse(webView.getUrl()));    // 为Intent设置DATA属性
                startActivity(intent);
                break;

            default:
        }
        return true;
    }

    // 对返回按键的处理
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();   // 返回键后退
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
