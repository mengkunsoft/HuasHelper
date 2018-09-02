<?php
// 公共函数模块
if(!defined('huas')) die('非法访问 - Insufficient Permissions');

/**
 * 获取GET或POST过来的参数
 * @param $key 键值
 * @param $default 默认值
 * @return 获取到的内容（没有则为默认值）
 */
function getParam($key,$default='')
{
    return trim($key && is_string($key) ? (isset($_POST[$key]) ? $_POST[$key] : (isset($_GET[$key]) ? $_GET[$key] : $default)) : $default);
}

/**
 * 输出一条简短的消息（一般是错误消息）
 * @param $code 消息代码
 * @param $msg 消息内容
 */
function echoMsg($code, $msg)    //发出消息
{
    $tempArr = array('code'=>$code,'msg'=>$msg);
    echojson(json_encode($tempArr));
}

/**
 * 输出返回结果，支持输出 json和jsonp 格式
 * @param $data 输出的内容(json格式)
 */
function echoJson($data)    //json和jsonp通用
{
    header("Content-type: application/json");
    $callback = getParam('callback');
    if($callback != '') //输出jsonp格式
    {
        die(htmlspecialchars($callback).'('.$data.')');
    }
    else
    {
        die($data);
    }
}

/**
 * 获取IP地址的函数
 * @return IP地址
 */
function getIP()
{
    $headers = array('HTTP_X_REAL_FORWARDED_FOR', 'HTTP_X_FORWARDED_FOR', 'HTTP_CLIENT_IP', 'REMOTE_ADDR');
    foreach ($headers as $h){
        $ip = @$_SERVER[$h];
        // 有些ip可能隐匿，即为unknown
        if ( isset($ip) && strcasecmp($ip, 'unknown') ){
            break;
        }
    }
    if( $ip ){
        // 可能通过多个代理，其中第一个为真实ip地址
        list($ip) = explode(', ', $ip, 2);
    }
    /* 如果是服务器自身访问，获取服务器的ip地址(该地址可能是局域网ip)
    if ('127.0.0.1' == $ip){
        $ip = $_SERVER['SERVER_ADDR'];
    }
    */
    return $ip;
}

/**
 * curl 获取网页源码函数
 * @param $url 目标页面 URL
 * @return 页面源码
 */
function curl($url) {
    $ch = curl_init(); 
    $timeout = 30; 
    $ua = 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36';
    curl_setopt($ch, CURLOPT_URL, $url); 
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1); 
    // curl_setopt($ch, CURLOPT_FOLLOWLOCATION, 1);     // 跟随重定向
    curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, $timeout);
    curl_setopt($ch, CURLOPT_ENCODING, '');
    curl_setopt($ch, CURLOPT_USERAGENT, $ua);   // 伪造ua 
    curl_setopt($ch, CURLOPT_ENCODING, 'gzip'); // 取消gzip压缩
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, FALSE); // https请求 不验证证书和hosts
    curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, FALSE);
    $content = trim(curl_exec($ch)); 
    curl_close($ch); 
    return $content; 
}

/**
 * 提取阳光平台回信内容反馈格式化
 * @param $content 原格式内容
 */
function formatSunReply($content){
    $content = preg_replace('/<p([^>]*)>(.*)<\/p>/isU', '$2<br>', $content);
    $content = preg_replace('/<div([^>]*)>(.*)<\/div>/isU', '$2<br>', $content);
    $content = preg_replace('/<br>/isU', '\n', $content);
    $content = preg_replace('/&nbsp;/isU', ' ', $content);
    $content = preg_replace('/<([^>]*)>/', '', $content);
	return $content;
}

/**
 * 从教务系统页面中提取出学生学号及姓名
 * @param $content 页面内容
 * @return 学号，姓名
 */
function getStuInfo($content) {
    preg_match('/<div id="Top1_divLoginName" [^>]+>([^<]*)\((\w*)\)<\/div>/isU', $content, $stu); // 获取学生姓名和学号
    $result = array(
        'name' => isset($stu[1])? $stu[1]: '',
        'sid' => isset($stu[2])? $stu[2]: '');
    return $result;
}

/**
 * 转换任意编码字符串为 UTF8 编码
 * @param $content 原编码内容
 */
function toUtf8($content){
	return mb_convert_encoding($content, 'utf-8', 'GBK,UTF-8,ASCII');    // 自动解决编码问题
}

/**
 * 返回配置
 * @param $name 配置名称
 */
function C($name=''){
	global $webConfig;
	if(!$name)
		return $webConfig;
	else
		return $webConfig[$name]? $webConfig[$name]: '';
}

/**
 * 写 Cookie
 * @param $key 键
 * @param $val 值
 * @param $time 过期时间
 */
function writeCookie($key, $val, $time = 0) {
    $key = 'huas_'.$key;
    setcookie($key, $val, $time, '/');
}

/**
 * 读 Cookie
 * @param $key 键
 * @param $default 默认值
 */
function readCookie($key, $default = '') {
    $key = 'huas_'.$key;
    if(empty($_COOKIE[$key])) {
        return $default;
    } else {
        return $_COOKIE[$key];
    }
}
