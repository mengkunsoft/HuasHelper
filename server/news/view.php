<?php
include('../init.php'); //引用公共函数模块

$types = getParam('types', 1015);
$id = getParam('id', 5655);

// 新闻地址
$newsInfo['url'] = "http://www.huas.cn/info/{$types}/{$id}.htm";

// 获取新闻正文
$content = curl($newsInfo['url']);

preg_match('/font-weight: bold; font-family: 黑体;">(.*)<div class="bdsharebuttonbox"/sU', $content, $temp);
if(!isset($temp[1])) echoMsg(-1, '新闻正文获取失败');

$content = $temp[0];

// echo $content;

// 标题
preg_match("/font-weight: bold; font-family: 黑体;\">(.*)\r/iU", $content, $temp);
$newsInfo['title'] = isset($temp[1])? $temp[1]: '新闻正文';

// 时间
preg_match("/录入时间：(.*)<\/div><\/td>/iU", $content, $temp);
$newsInfo['data'] = isset($temp[1])? $temp[1]: '-';

// 正文
preg_match("/<td height=\"1\" colspan=\"2\">(.*)<\/form><!--#endeditable-->/isU", $content, $temp);
if(isset($temp[1])) {
    $newsInfo['content'] = $temp[1];
    
    // 替换相对路径图片
    $newsInfo['content'] = preg_replace('/src="\/__local\//','src="http://www.huas.cn/__local/', $newsInfo['content']); 
    
    // $newsInfo['content'] = preg_replace('/<span([^>]*)>(.*)<\/span>/',"$2", $newsInfo['content']); 
    
    // $newsInfo['content'] = preg_replace('/<p([^>]*)>(.*)<\/p>/',"<p>$2</p>", $newsInfo['content']); 
    
    // 去除图片外围的 P 标签
    // $newsInfo['content'] =  preg_replace('/<p[^>]*>(.*)(<img[^>]*>)(.*)<\/p>/isU', '<div>\1\2\3</div>', $newsInfo['content']);

} else {
    $newsInfo['content'] = "新闻正文获取失败";
}

// echoJson(json_encode($newsInfo));

ui_head($newsInfo['title']);

ui_topNav();
?>

<style>
.news-title {
    font-weight: normal;
    font-size: 20px;
    text-align: center;
}
.news-content {
    padding: 0 10px;
}
.news-content img {
    max-width: 100%!important;
}
</style>

<div class="content">
    <div class="container news-content">
        <h2 class="news-title"><?php echo $newsInfo['title']; ?></h2>
        
        <?php echo $newsInfo['content']; ?>
        
        <p style="margin-left: 10px"><a href="<?php echo $newsInfo['url']; ?>" target="_blank">阅读原文</a></p>
    </div>
</div>



<?php

ui_topNav();