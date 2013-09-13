xiaohuoban
==========

小伙伴是一个让你惊呆了的生活好帮手！--移动app

邮件列表：https://groups.google.com/forum/#!forum/ixiaohuoban

repo 地址：https://github.com/ideafold/xiaohuoban

代码导入说明：
1. 提供的项目：
1.1 actionbarsherlock， slidingmenu_lib； 这两个项目在trunk/extras中。
1.2 merged_xiaohuoban, 在trunk/下

2. 导入actionbarsherlock，之后导入slidingmenu_lib， 点击slidingmenu_lib项目的Properties, 选择“Android”，点击“add”把actionbarsherlock加进来。因为slidingmenu_lib是依赖actionbarsherlock的。

3. 导入merged_xiaohuoban，点击merged_xiaohuoban项目的Properties, 选择“Android”，点击“add”把actionbarsherlock和slidingmenu_lib加进来。

所需要的libs，应该在各个项目的libs目录下都可以找到。

此android客户端开发计划：
1. 解决掉你发现的bug, 现在知道的bug包括：你早上说的问题， 浏览器打开youku.com时，点击部分视频无反应。
2. 优化浏览器，例如，双击浏览器的地址输入栏时，
如果地址栏内有url，双击后，url应该处于全选状态。
