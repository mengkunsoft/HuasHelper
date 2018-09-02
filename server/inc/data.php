<?php
// 公共函数模块
if(!defined('huas')) die('非法访问 - Insufficient Permissions');


/**
 * 获取新闻列表
 * @param $treeID 树节点ID
 * @param $pages 页码
 * @return 包含新闻列表的数组
 */
function getNewsList($treeID = 1015, $pages = 1) {
    // 获取新闻数据
    $content = curl('http://www.huas.cn/list.jsp?urltype=tree.TreeTempUrl&wbtreeid='.$treeID.'&PAGENUM='.$pages);
    
    // 缩小范围
    preg_match('/<table id="line_u10_0" style="" width="100%" border="0" cellspacing="1" cellpadding="0">(.*)<table align="center"><\/table>/sU', $content, $temp);
    if(!isset($temp[1])) {
        $news['code'] = -1;
        $news['msg'] = '新闻栏目维护中';
        return $news;
    }
    
    $content = $temp[1];
    
    preg_match_all('/href="info\/(\d*)\/(\d*)\.htm" target=_blank title="(.*)" >(.*) <font color="#999999">(.*)<\/font>/sU', $content, $matches);
    
    $news['code'] = 200;
    $news['msg'] = 'ok';
    $news['count'] = count($matches[0]);
    
    for ($i=0; $i< $news['count']; $i++) {
        $tmpArr['types'] = $matches[1][$i];     // 新闻分类ID
        $tmpArr['id'] = $matches[2][$i];        // 新闻ID
        $tmpArr['title'] = $matches[4][$i];     // 新闻标题
        $tmpArr['data'] = $matches[5][$i];      // 新闻发布日期
        
        $news['items'][] = $tmpArr;
        unset($tmpArr);    // 清空临时数组
    }
    return $news;
}

/**
 * 获取阳光服务信息列表
 * @param $pages 页码
 * @return 包含帖子列表的数组
 */
function getSunList($pages = 1) {
    // 获取阳光服务数据
    $content = curl('http://www.huas.cn:316/web/visitMoreList.jsp?pageIndex='.($pages - 1));
    
    // 缩小范围
    preg_match('/<td id="demo1">(.*)<\/table><\/td>/sU', $content, $temp);
    if(!isset($temp[1])) {
        $lists['code'] = -1;
        $lists['msg'] = '信件列表获取失败';
        return $lists;
    }
    
    $content = $temp[1];
    // 去除干扰
    $content = preg_replace('/<td[^>]*>/isU', '<td>', $content);    
    $content = preg_replace('/\n/isU', '', $content);
    $content = preg_replace('/\s/isU', '', $content);
    
    $content = toUtf8($content);
    
    // 提取出每一条信件
    preg_match_all('/<ahref="visit_detail.jsp\?id=(\w*)&showVisitCode=(\w*)&showFetchCode=(\w*)"target="_blank"class="title4">([^<]*)<\/a><\/td><td>([^<]*)<\/td><td>([^<]*)<\/td><td>([^<]*)<\/td><td>([^<]*)<\/td><\/tr>/sU', $content, $matches);
    
    $lists['code'] = 200;
    $lists['msg'] = 'ok';
    $lists['count'] = count($matches[0]);
    
    for ($i=0; $i< $lists['count']; $i++) {
        $tmpArr['types'] = $matches[5][$i];     // 信件类型
        $tmpArr['id'] = $matches[1][$i];        // 信件ID
        $tmpArr['no'] = $matches[2][$i];        // 信件编号
        $tmpArr['title'] = $matches[4][$i];     // 信件标题
        $tmpArr['auth'] = $matches[7][$i];      // 信件作者
        $tmpArr['data'] = $matches[6][$i];      // 信件发布日期
        $tmpArr['status'] = $matches[8][$i];    // 信件状态
        
        $lists['items'][] = $tmpArr;
        unset($tmpArr);    // 清空临时数组
    }
    return $lists;
}

/**
 * 获取阳光服务帖子详细内容
 * @param $id 
 * @param $no
 * @return 包含帖子列表的数组
 */
function getSunDetail($id = 'fc6804de5f2422e0015f4c51420e008d', $no = 'JXX002820170161') {
    // 获取阳光服务数据
    $content = curl("http://www.huas.cn:316/web/visit_detail.jsp?id={$id}&showVisitCode={$no}&showFetchCode=");
    
    $content = toUtf8($content);
    
    // 缩小范围
    preg_match('/编号：<\/td>(.*)<table width="900" border="0" align="center" cellpadding="5" cellspacing="0" bordercolor="#999999">/sU', $content, $temp);
    if(!isset($temp[1])) {
        $lists['code'] = -1;
        $lists['msg'] = '信件内容读取失败';
        return $lists;
    }
    
    $content = $temp[1];
    $content = preg_replace('/\s/isU', '', $content);
    
    // die($content);
    
    $lists['code'] = 200;
    $lists['msg'] = 'ok';
    $lists['data']['no'] = $no;    // 编号
    
    preg_match('/来信人：<\/td><tdwidth="35%">(.*)&nbsp;<\/td><\/tr>/isU', $content, $temp);
    $lists['data']['from'] = isset($temp[1])? $temp[1]: '-';    // 来信人
    
    preg_match('/来信时间：<\/td><td>(.*)&nbsp;<\/td><tdheight/isU', $content, $temp);
    $lists['data']['sendtime'] = isset($temp[1])? $temp[1]: '-';    // 来信时间
    
    preg_match('/服务类型：<\/td><td>(.*)&nbsp;<\/td><\/tr><tr>/isU', $content, $temp);
    $lists['data']['types'] = isset($temp[1])? $temp[1]: '-';    // 信件类型
    
    preg_match('/受理单位：<\/td><td>(.*)&nbsp;<\/td><tdheight="25"/isU', $content, $temp);
    $lists['data']['to'] = isset($temp[1])? $temp[1]: '-';    // 受理单位
    
    preg_match('/办理状态：<\/td><td>(.*)&nbsp;<\/td><\/tr>/isU', $content, $temp);
    $lists['data']['status'] = isset($temp[1])? $temp[1]: '-';    // 办理状态
    
    preg_match('/来信主题：<\/td><tdclass="bdpd"colspan="3">(.*)&nbsp;<\/td><\/tr><tr>/isU', $content, $temp);
    $lists['data']['subject'] = isset($temp[1])? $temp[1]: '-';    // 信件主题
    
    preg_match('/来信内容：<\/td><tdclass="bdpd"colspan="3">(.*)&nbsp;<\/td><\/tr>/isU', $content, $temp);
    $lists['data']['content'] = isset($temp[1])? formatSunReply($temp[1]): '-';    // 信件内容
    
    // preg_match('/回文单位：<\/td><tdclass="bdpd"width="35%">(.*)&nbsp;<\/td><tdwidth="15%"/isU', $content, $temp);
    // $lists['data']['to'] = isset($temp[1])? $temp[1]: '-';    // 回文单位
    
    preg_match('/办理时间：<\/td><tdclass="bdpd"width="35%">(.*)&nbsp;<\/td><\/tr><tr><tdwidth="100"/isU', $content, $temp);
    $lists['data']['replytime'] = isset($temp[1])? $temp[1]: '-';    // 处理时间
    
    preg_match('/;处理结果：<\/td><tdclass="bdpd"colspan="3"style="min-height:50px">(.*)<\/td><\/tr><\/table><tablewidth="100%"border="0"cellspacing="0"cellpadding="0">/isU', $content, $temp);
    $lists['data']['reply'] = isset($temp[1])? formatSunReply($temp[1]): '-';    // 回信内容
    
    return $lists;
}

/**
 * 获取验证码
 * @param null
 * @return 包含验证码信息的数组
 */
function getVerifyPic() {
    // 获取cookie
    $c = new wcurl(JIAOWU_URL.'/');
    $data = $c->getCookies();
    $c->close();
    
    if(!isset($data['JSESSIONID'])) {
        $lists['code'] = -1;
        $lists['msg'] = 'cookie读取失败';
        return $lists;
    }
    
    $lists['code'] = 200;
    $lists['msg'] = 'ok';
    $lists['cookie'] = $data['JSESSIONID'];
    
    // 获取验证码图片
    $c = new wcurl(JIAOWU_URL.'/verifycode.servlet');
    $c->addCookie('JSESSIONID='.$lists['cookie']);
    $lists['verify'] = base64_encode($c->get());
    $c->close();
    
    return $lists;
}

/**
 * 登录教务系统
 * @param $id 学号
 * @param $pw 密码
 * @param $cookie cookie信息
 * @param $verify 验证码
 * @return 包含验证码信息的数组
 */
function loginJw($sid, $pw, $cookie = '', $verify = '') {
    $verify = strtolower($verify);      // 验证码必须小写
    
    $c = new wcurl(JIAOWU_URL.'/xk/LoginToXk');
    $c->addCookie('JSESSIONID='.$cookie);
    $data = $c->post(array('USERNAME'=> $sid, 'PASSWORD'=> $pw, 'RANDOMCODE'=> $verify));
    $c->close();
    $data = toUtf8($data);
    
    if(preg_match('/<font color="red">(.*)<\/font>/isU', $data, $tips))   // 登录失败
    {
        if(preg_match('/验证码错误/isU', $tips[1], $temp))   // 验证码错误
        {
            $lists = array('code' => 4003, 'msg' => '验证码错误');
        } else {
            $lists = array('code' => 4001, 'msg' => $tips[1]);
        }
        return $lists;
    }
    
    
    return array(
            'code' => 200, 
            'msg' => 'success',
            'name' => '',
            'sid' => $sid
            );
    
    // *************** 不跟随重定向，以下已失效
    
    // 获取学生信息
    $stuInfo = getStuInfo($data);
    
    // 姓名和学号不为空，判定登陆成功
    if(($stuInfo['name'] != '') || ($stuInfo['sid'] != '')) {
        $lists = array(
            'code' => 200, 
            'msg' => 'success',
            'name' => $stuInfo['name'],
            'sid' => $stuInfo['sid']
            );
    } else {
        $lists = array('code' => 4002, 'msg' => '学生信息获取失败');
    }
    return $lists;
}

/**
 * 查分
 * @param $cookie cookie信息
 * @return 包含分数信息的数组
 */
function getScore($cookie) {
    $c = new wcurl(JIAOWU_URL.'/kscj/cjcx_list');
    $c->addCookie('JSESSIONID='.$cookie);
    $data = $c->post(array('kksj'=> '', 'kcxz'=> '', 'kcmc'=> '', 'xsfs'=> 'all'));
    $c->close();
    
    // 获取学生信息
    $stuInfo = getStuInfo($data);
    
    // 去除干扰
    $data = preg_replace('/<th[^>]*>/isU', '<th>', $data);    
    $data = preg_replace('/<td[^>]*>/isU', '<td>', $data);    
    $data = preg_replace('/\n/isU', '', $data);
    $data = preg_replace('/\s/isU', '', $data);
    
    $data = toUtf8($data);
    
    // 读取修读学分信息
    preg_match('/<\/span>-->一共需要修读<span>([^<]*)<\/span>学分，已修读<span>([^<]*)<\/span>学分，还需修读<span>([^<]*)<\/span>学分，平均学分绩点<span>([^。<]*)。<\/span>/isU', $data, $tmp);
    $lists = array(
        'stuName' => $stuInfo['name'],      // 学生姓名
        'scoreAll'=> isset($tmp[1])? $tmp[1]: 'NaN',    // 总学分
        'scoreNow'=> isset($tmp[2])? $tmp[2]: 'NaN',    // 已修读学分
        'scoreRest'=> isset($tmp[3])? $tmp[3]: 'NaN',   // 剩余学分
        'GPA'=> isset($tmp[4])? $tmp[4]: 'NaN',         // 平均学分绩点
        );
    
    // 读取成绩表头
    preg_match('/class="Nsb_r_listNsb_table"><tr>(<th>([^<]*)<\/th>)*<\/tr>/isU', $data, $tmp);
    if(!isset($tmp[0])) {
        $lists = array(
            'code' => 4101, 
            'msg' => '成绩项目读取失败'
        );
        return $lists;
    }
    
    preg_match_all('/<th>([^<]*)<\/th>/isU', $tmp[0], $tmp);
    if(!isset($tmp[1])) {
        $lists = array(
            'code' => 4102, 
            'msg' => '成绩项目读取失败-2'
        );
        return $lists;
    }
    $enName = array(
        '序号' => 'no',
        '开课学期' => 'term',
        '课程编号' => 'id',
        '课程名称' => 'name',
        '成绩' => 'score',
        '学分' => 'credit',
        '总学时' => 'period',
        '考核方式' => 'methods',
        '课程属性' => 'property',
        '课程性质' => 'types',
        '审核学年学期' => 'examterm',
        '考试性质' => 'examtypes',
        );
    
    for($i=0; $i<count($tmp[1]); $i++) {
        $lists['head'][$i] = $tmp[1][$i];
        $tmpHead[$i] = isset($enName[$tmp[1][$i]])? $enName[$tmp[1][$i]]: $i;
    }
    
    
    // 读取分数每一行
    preg_match_all('/<tr>(<td>([^<]*)<\/td>)*<\/tr>/isU', $data, $tmp);
    if(!isset($tmp[0])) {
        $lists = array(
            'code' => 4103, 
            'msg' => '分数读取失败'
        );
        return $lists;
    }
    
    // 拆散读取分数列
    for($i=0; $i<count($tmp[0]); $i++) {
        preg_match_all('/<td>([^<]*)<\/td>/isU', $tmp[0][$i], $items);
        for($j=0; $j<count($items[1]); $j++) {
            $lists['score'][$i][$tmpHead[$j]] = $items[1][$j];
        }
    }
    
    $lists['code'] = 200;
    $lists['msg'] = 'success';
    
    return $lists;
}

/**
 * 获取课程表信息
 * @param $cookie cookie信息
 * @return 课程表信息
 */
function getTimetable($cookie) {
    $c = new wcurl(JIAOWU_URL.'/xskb/xskb_list.do');
    $c->addCookie('JSESSIONID='.$cookie);
    $data = $c->get();
    $c->close();
    
    if(preg_match('/<table id="kbtable"(.*)<\/table>/isU', $data, $tmp)) {
        $data = $tmp[0];
    } else {
        $lists = array(
            'code' => 4101, 
            'msg' => 'COOKIE无效，请重新登录'
        );
        return $lists;
    }
    
    // 去除干扰
    $data = preg_replace('/<th[^>]*>/isU', '<th>', $data);    
    $data = preg_replace('/<td[^>]*>/isU', '<td>', $data);   
    $data = preg_replace('/<input type="hidden"([^>]*)>/isU', '', $data);
    $data = preg_replace('/<div id="([^"]*)"/isU', '<div', $data);
    $data = preg_replace('/&nbsp;/isU', '', $data);
    $data = preg_replace('/\n/isU', '', $data);
    $data = preg_replace('/\s/isU', '', $data);
    
    $data = preg_replace('/<divclass="kbcontent1">(.*)<\/div>/isU', '', $data);
    $data = preg_replace('/<divstyle="display:none;"class="kbcontent"><\/div>/isU', '', $data);
    
    $data = toUtf8($data);
    
    // die($data);
    
    // 读取课程大节内容
    preg_match_all('/大节<\/th>(.*)<\/tr>/isU', $data, $tmp);
    if(!isset($tmp[0])) {
        $lists = array(
            'code' => 4102, 
            'msg' => '课程信息解析失败'
        );
        return $lists;
    }
    
    // var_dump($tmp);
    
    // 拆散读取课程
    for($i=0; $i<count($tmp[0]); $i++) {
        // 读取每一小节
        preg_match_all('/<td>(.*)<\/td>/isU', $tmp[0][$i], $items);
        for($j=0; $j<count($items[1]); $j++) {
            // 读取重复时间段但不同周次的课程
            // echo $items[1][$j]."\n";
            
            preg_match_all('/([^>]*)<br\/><fonttitle=\'老师\'>(.*)<\/font><br\/><fonttitle=\'周次\(节次\)\'>(.*)<\/font><br\/><fonttitle=\'教室\'>(.*)<\/font>/isU', $items[1][$j], $one);
            for($k=0; $k<count($one[1]); $k++) {
                $tmpArr['week'] = $j;
                $tmpArr['section'] = $i;
                $tmpArr['name'] = $one[1][$k];
                $tmpArr['teacher'] = $one[2][$k];
                $tmpArr['classroom'] = $one[4][$k];
                
                // 解析周次信息
                $weeks = $one[3][$k];  // 周次
                // $weeks = preg_replace('/\([^\)]*\)/isU', '', $weeks);   // (周) (单周) (双周)
                
                $weeks = explode(',', $weeks);      // 按英文逗号分隔
                
                $tmpArr['circle'] = array();
                if(count($weeks)) {
                    for($l=0; $l<count($weeks); $l++) {
                        preg_match_all('/(\d+)/', $weeks[$l], $tmpWeeks);
                        if(count($tmpWeeks[1]) > 1) {  // 周次段
                            for($weekNum = $tmpWeeks[1][0]; $weekNum <= $tmpWeeks[1][1]; $weekNum++ ) {
                                // $tmpArr['circle'] .= $weekNum.',';
                                array_push($tmpArr['circle'], (int) $weekNum);
                            }
                        } elseif(count($tmpWeeks[1]) == 1) {    // 单个的周次
                            
                            array_push($tmpArr['circle'], (int) $tmpWeeks[1][0]);    // $tmpArr['circle'] .= $tmpWeeks[1][0].',';
                        }
                    }
                    // $tmpArr['circle'] = substr($tmpArr['circle'], 0, -1);
                } else {
                    // $tmpArr['circle'] = '[]';
                }
                $lists['items'][] = $tmpArr;
            }
        }
    }
    
    $lists['code'] = 200;
    $lists['msg'] = 'success';
    
    return $lists;
}