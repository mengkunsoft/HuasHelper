<?php
// 前端输出模块
if(!defined('huas')) die('非法访问 - Insufficient Permissions');

/**
 * 输出网站头，提供网站必备的内容
 * @param $title 页面标题
 * @param $description 页面描述
 * @param $keywords 页面关键字
 * @return 获取到的内容（没有则为默认值）
 */
function ui_head($title, $description=null, $keywords=null) {
    header('Content-type: text/html; charset=utf-8');
    $title .= ' - '.C('slogan');
    $description = empty($description) ? '' : $description;
    $keywords .=''; //这里加上共用的描述词
    ?>
<!doctype html>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    
    <title><?php echo $title;?></title>
    <meta name="description" content="<?php echo $description;?>">
    <meta name="keywords" content="<?php echo $keywords;?>">
    
    <meta name="viewport" content="initial-scale=1, maximum-scale=1">
    <link rel="shortcut icon" href="<?php echo C('siteurl'); ?>/favicon.ico">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    
    <!-- Set render engine for 360 browser -->
    <meta name="renderer" content="webkit">
    
    <!-- No Baidu Siteapp-->
    <meta http-equiv="Cache-Control" content="no-siteapp"/>
    
    <link rel="stylesheet" href="<?php echo C('siteurl'); ?>/static/light7/css/light7.min.css">

    <?php 
    includeCss('style', '页面共用');
    jsSiteInfo();
    ?>
    
<?php
}

/**
 * 输出网站顶部banner
 * @param $show 是否展示底部内容
 * @return 输出页面元素
 */
function ui_topNav() {
?>

</head>
<body>

<?php
}


/**
 * 输出网站底部公共文件
 * @param $show 是否展示底部内容
 * @return 输出页面元素
 */
function ui_footer($show = true) {
?>

    <?php if($show) { ?>
    
    <?php }  ?>


    <!-- layer弹窗插件 -->
    <script src="<?php echo C('siteurl');?>/assets/plugns/layer/layer.js"></script>


<?php
includeJs('jquery.lazyload.min', '滚动加载插件');

?>

<script type="text/javascript">
$(function() {
    
});

// url编码
// 输入参数：待编码的字符串
function urlEncode(String) {
    return encodeURIComponent(String).replace(/'/g,"%27").replace(/"/g,"%22");	
}
</script>

<!-- 百度、360等搜索引擎的主动推送代码放在这里 ↓↓↓ -->

<!-- 百度、360等搜索引擎的主动推送代码放在这里 ↑↑↑ -->

<script src="https//cdn.bootcss.com/jquery/3.1.1/jquery.min.js"></script>
<script src="<?php echo C('siteurl'); ?>/static/light7/js/light7.min.js"></script>
</body>
</html>

<?php
}

/**
 * 输出 js 文件
 * @param $name js 文件名
 * @param $description js文件描述
 * @param $ver js版本号
 * @return 输出对应js文件
 */
function includeJs($name, $description = '', $ver = '1.0') {
    if($description) echo "\n    <!-- $description -->";
    echo "\n    <script src=\"".C('siteurl')."/static/js/{$name}.js?v{$ver}\"></script>\n";
}

/**
 * 输出 css 文件
 * @param $name css文件名
 * @param $description css文件描述
 * @param $ver css版本号
 * @return 输出对应css文件
 */
function includeCss($name, $description = '', $ver = '1.0') {
    if($description) echo "\n    <!-- $description -->";
    echo "\n    <link rel=\"stylesheet\" href=\"".C('siteurl')."/static/css/{$name}.css?v{$ver}\">\n";
}

/**
 * 输出网站相关信息，供页面内的 js 文件调用
 * @param 无
 * @return 无
 */
function jsSiteInfo() {
?>

    <script>
        // 网站相关信息，供页面内的 js 文件调用
        var mkSiteInfo = { siteUrl: "<?php echo C('siteurl'); ?>" }
    </script>
    
    <!-- 百度统计代码放在这里 ↓↓↓ -->
    
    <!-- 百度统计代码放在这里 ↑↑↑ -->
    
<?php
}