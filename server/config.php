<?php

// 网站配置文件
if(!defined('huas')) die('非法访问 - Insufficient Permissions');

// 网站的一些设置
$webConfig = array(
    // 部署的网址
    'siteurl' => 'http://huas.mkblog.cn/huas',    // 网站网址
    
    // 网站标题
    'slogan' => '文理人社区',    // 网站标题
    
    // App相关信息
    'appinfo' => array(
            
            // 更新配置
            'update' => array(
                
                // app最新版本
                'code' => 11,      
                
                // 版本号
                'version' => '1.3',    
                
                // 最新版下载地址（直链）
                'url' => 'http://huas.mkblog.cn/app/download/',  
                
                // 更新日期（时间戳）
                'data' => 1526392044,   
                
                // 更新说明（用“\n”换行）
                'description' => '1.解决高版本 Android 无法显示英语每日图像的 BUG\n2.重写 ListView 适配器，优化软件性能\n3.状态栏改为沉浸式'   
            )
        )
);