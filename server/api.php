<?php
// API 接口

// 默认cookie(用于调试)
$defaultCookie = '';

require_once('init.php');

$types = getParam('types', 'timetable');

switch($types) {
    // 获取新闻列表
    case 'newslist':
        $treeID = getParam('treeID');    // 新闻分类 ID
        $pages = getParam('pages');        // 新闻页码
        
        echoJson(json_encode(getNewsList($treeID, $pages)));
        break;
    
    // 阳光服务平台信件列表
    case 'sunlist':     
        $pages = getParam('pages', 1);        // 页码
        
        echoJson(json_encode(getSunList($pages)));
        break;
    
    // 阳光平台帖子详细内容
    case 'sundetail':     
        $id = getParam('id');
        $no = getParam('no');
        
        echoJson(json_encode(getSunDetail($id, $no)));
        break;
    
    // 获取验证码
    case 'verifypic':       
        echoJson(json_encode(getVerifyPic()));
        break;
    
    // 登录教务系统
    case 'loginjw':       
        $verify = getParam('verify');   // 验证码
        $cookie = getParam('cookie');   // cookie
        $sid = getParam('sid');   // 账号
        $pw = getParam('pw');   // 密码
        
        $arr = loginJw($sid, $pw, $cookie, $verify);
        echoJson(json_encode($arr));
        break;
    
    // 教务系统成绩查询
    case 'getscore':        
        $cookie = getParam('cookie', $defaultCookie);   // cookie
        
        echoJson(json_encode(getScore($cookie)));
        break;
    
    // App 内置，跳转至指定页面
    case 'goto':     
        $target = getParam('target');
        switch($target) {
            // 移动图书馆
            case 'library':     
                header('location:http://220.168.209.130:9999/sms/opac/search/showiphoneSearch.action');
                die('加载中...');
                break;
            
            // 缴费
            case 'pay':     
                header('location:http://payment.cloud.ccb.com/index_u.jhtml?param=4C4A647F371B43F11DBA91BA7799EE583EE89FECE5A55A0E7CE6C5522E5A9F463AD4F37C4B626DBE0D7B8A93E67D7A94D15776F0B17D91BE65EC5221B4A931F1C1F6331EE5E1ED11232A46AA1E196B49DC7901A2E97F9D32C9472BFD4CA567687AC7760BB7E6CB52E11236DC3E5F61C1D1B21F50E66048EA47A4526B703C5C90546B9B12CB61C99D9264D07D287F5EC8');
                die('加载中...');
                break;
            
            // 街景
            case 'vista':     
                header('location:http://jiejing.qq.com/#pano=25071038140427132347700');
                die('加载中...');
                break;
                
            default:
                
        }
        break;
    
    // 检查更新
    case 'update':  
        echoJson(json_encode($webConfig['appinfo']['update']));
        break;
    
    // 获取课程表
    case 'timetable':   
        $cookie = getParam('cookie', $defaultCookie);   // cookie
        
        echoJson(json_encode(getTimetable($cookie)));
        break;
    
    // 获取本地天气
    case 'weather':     
        $ip = getIP();
        $content = curl('http://api.k780.com/?app=weather.today&weaid='.$ip.'&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json');
        echoJson($content);
        break;
    
    default:
        
}

