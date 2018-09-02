<?php

define('huas', 'huasToos');

define('SYSTEM_ROOT', dirname(__FILE__));    // 系统目录


// 强智教务系统首页地址(不需要最后的“/”)
define('JIAOWU_URL', 'http://www.huas.cn:20011/jsxsd');





/************************* 引入系统模块 ******************************/

// 系统配置模块
require SYSTEM_ROOT.'/config.php';

// 封装函数模块
require SYSTEM_ROOT.'/inc/functions.php';

// Curl 模块
require SYSTEM_ROOT.'/inc/class.wcurl.php';

// 数据获取 API
require SYSTEM_ROOT.'/inc/data.php';

// 前端 UI
require SYSTEM_ROOT.'/inc/ui.php';
