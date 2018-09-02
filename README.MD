# 文理助手 APP

![LOGO](https://user-images.githubusercontent.com/16880885/44885982-76ced980-acf6-11e8-804e-389aff23df4d.png)

本人的大学毕业设计，本来想做成一个类似于超级课程表那样的服务于校友的工具软件，后来发现还没做完就毕业了、、毕业了……忧伤……

里面的功能大多是针对 HUASER 定制的，其它学校可能不适用。

不过，涉及教务系统的部分（导入课表、成绩查询）应该是通用的，理论上支持与所有的 **强智科技** 教务系统程序对接。

校园网一键登录部分可用于自动登录 **哆点** 校园网热点。

----

软件构架
----

软件分为服务端与客户端，数据的爬取在服务端完成，爬取后格式化为json供客户端调用，客户端仅作数据展示（便于维护升级）。如下如：


![软件构架](https://user-images.githubusercontent.com/16880885/44951207-91d55100-ae90-11e8-97f7-8a782c5a7a70.png)

`server`文件夹内为服务器部分的源代码（采用 PHP 编写）


部分界面截图
----

| 软件首页   | 新闻列表    | 新闻详情 |  登录教务系统 | 
| ----------- | ----------- | ----------- | ----------- |
| ![软件首页](https://user-images.githubusercontent.com/16880885/44942042-e10d7a00-addb-11e8-8689-651bfde383b5.png) | ![新闻列表](https://user-images.githubusercontent.com/16880885/44942049-fedadf00-addb-11e8-848c-3a58dec3f174.png) | ![新闻详情](https://user-images.githubusercontent.com/16880885/44942056-1ca84400-addc-11e8-84b7-72513713f360.png) | ![登录教务系统](https://user-images.githubusercontent.com/16880885/44942068-4bbeb580-addc-11e8-8332-d9dbcabe5f68.png) |
| 阳光服务   | 课程表    | 课程添加 |  成绩查询 | 
| ![阳光服务](https://user-images.githubusercontent.com/16880885/44942080-8a547000-addc-11e8-8f6a-41c28b255e75.png) | ![课程表](https://user-images.githubusercontent.com/16880885/44942084-a9eb9880-addc-11e8-81c1-1343dc979550.png) | ![课程添加](https://user-images.githubusercontent.com/16880885/44942088-c1c31c80-addc-11e8-9c8a-0a7ee1b3461b.png) | ![成绩查询](https://user-images.githubusercontent.com/16880885/44942227-d6ed7a80-addf-11e8-8d22-d1a6279699c0.png) |


拥有的功能
----
- 校园新闻浏览
- 阳光服务
- 学籍成绩查询
- 图书馆藏查询（嵌入网页）
- 课程表（可从教务系统导入）
- 在线缴费（嵌入网页）
- 一键登录校园网
- 校友交流社区（吐个槽）
- 校园街景查看（腾讯街景）
- 校园贴吧（嵌入网页）
- 在线检查更新

更新日志
----

#### v1.3    2018/5/15
- 解决高版本 Android 无法显示英语每日图像的 BUG
- 重写 ListView 适配器，优化软件性能
- 状态栏改为（伪）沉浸式

#### v1.2    2018/1/1

- 新增教务系统课程导入功能
- 修复新闻列表点击加载条闪退的BUG
- 大量细节优化
- 完成服务器的迁移工作

#### v1.1    2017/11/26

- 首页全新改版
- 新增天气、每日英语功能
- web页面新增加载中提示
- 其它一些细节优化

#### v1.0    2017/11/19
- 文理助手的第一个版本，拥有 文理新闻、阳光服务、成绩查询、馆藏检索、我的课表、在线缴费、一键联网、校友社区、校园街景、百度贴吧等基础功能


致谢
----

开发这个软件，参照学习了很多网络上的开源模块。列举如下：

[1].Retrofit2 https://square.github.io/retrofit

[2].ViewPagerIndicator https://github.com/LuckyJayce/ViewPagerIndicator

[3].设计一个通用的BaseActivity http://www.jianshu.com/p/4d4b54c98f5d

[4].HuasTools https://github.com/xbdcc/HuasTools

[5].模仿课程表的布局 http://blog.csdn.net/supervictim/article/details/52809516

[6].使用GridView实现九宫格布局 http://blog.csdn.net/qq1123655345/article/details/48029623

程序中部分图标素材来自 https://www.flaticon.com 和 http://iconfont.cn


# License

````
MIT License

Copyright (c) 2018 mengkun

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
````