# TentedDictionary
emmmmmm无聊而已, 随便写的一个v8词库插件
## 词库文档
dic.txt存放路径	sd卡/内部存储 -> Tented -> Dictionary -> dic.txt<br>
词库读写内容存放路径	dic路径 -> data<br>
<br>
改动词库后记得发送【重载】噢！(将词库载入内存, 因此这个词库插件就不会提供加密功能了, 毕竟可以直接列出内存列表然后。。。。<br>
<br>
指令结构:<br>
	指令<br>
	代码1<br>
	代码2<br>
	...<br>
<br>
其中，指令为正则表达式<br>
就是判断某条信息是否符合某个格式<br>
<br>
例如:<br>
你好 符合 你好 这个格式<br>
123 符合 [0-9]+ (一个或以上个数字)这个格式<br>
<br>
使用变量:<br>
	$变量名<br>
	如:Hello $QQ<br>
	如果需要使用$，则使用$value表示<br>
	如果需要使用, ，则使用$comma表示<br>
<br>
内置变量:<br>
	QQ -> 发送者QQ<br>
	GROUP -> 所在群号<br>
	NAME -> 昵称<br>
	GNAME -> 群名<br>
	ROBOT -> 机器人QQ号<br>
	CODE -> 临时标识码<br>
	comma -> ,<br>
	value -> $<br>
	line -> \n<br>
<br>
	AT[数字] -> 获取at<br>
	G[数字] -> 获取组(正则表达式)<br>
<br>
	__msg__ -> 消息包内消息<br>
	__xml__ -> 消息包内的xml消息<br>
	__json__ -> 消息包内的json消息<br>
	__img__ -> 消息包内的图片消息<br>
	__at__ -> 消息包内的at消息<br>
<br>
如果需要换行请写\n或者\r<br>
如果你感觉\很难打, 也可以使用$line代替<br>
<br>
代码格式: 指令 [参数1, 参数2...]<br>
<br>
代码文档:<br>
	消息相关:<br>
		msg 字符串<br>
			向消息包内【添加】一条消息<br>
		xml 字符串<br>
			【覆盖】消息包内的xml消息<br>
		json 字符串<br>
			【覆盖】消息包内的json消息<br>
		img 字符串<br>
			【覆盖】消息包内的图片消息<br>
		at QQ号@昵称<br>
		    【覆盖】消息包内的at消息<br>
		db 字符串<br>
			立即发送消息到控制台<br>
			一般用于调试...<br>
<br>
	文件相关:<br>
		w 文件路径, 键, 值<br>
			向目标文件写入 对应的键和值<br>
		r 变量1, 文件路径, 键, 默认值<br>
			读取目标文件中 键对应的值(如果读取不到就获得默认值) 并将结果赋值给变量1<br>
		d 文件路径<br>
		    删除目标文件<br>
<br>
	变量相关:<br>
		set 变量1, 字符串<br>
			将【字符串】赋值给【变量1】<br>
		cpy 变量1, 变量2				//并不觉得这个什么用<br>
			将【变量2】赋值给【变量1】<br>
		opt 变量1, 算式(v8)<br>
			将算式结果赋值给【变量1】<br>
		rand 变量1, 数字1, 数字2<br>
			取 数字1~数字2(不包含) 之间的数字存入变量1<br>
		time 变量1, 时间代码<br>
		    获取时间代码格式的时间并存入变量1中<br>
<br>
	网络相关:<br>
		get 变量1, 网址<br>
			访问网址, 赋值给变量1<br>
		post 变量1, 网址, 数据<br>
			post网址, 赋值给变量1<br>
		enc 变量1, 变量2<br>
			将变量2中的内容使用百分号编码转码之后赋值给变量1<br>
<br>
	群管相关:<br>
		stu 群号, QQ号, 时长<br>
			禁言, 详细内容不用我说了<br>
		rm 群号, QQ号<br>
			踢人<br>
		rn 群号, QQ号, 新名字<br>
			改名<br>
		like QQ号, 次数<br>
			赞！！！<br>
<br>
	字符串操作:<br>
		rep 变量1, 被操作的变量, 将要被替换的字符串, 替换的字符串<br>
			将 【被操作的变量】 中的 【将要被替换的字符串】 替换为 【替换的字符串】<br>
			并将结果赋值给变量1<br>
		sub 变量1， 被操作的字符串, 开始的字符, 结束的字符<br>
			把 【被操作的变量】 中 【开始的字符】 和 【结束的字符】 中间的所有内容赋值给变量1<br>
<br>
	流程控制:<br>
		if 布尔表达式<br>
			如果布尔表达式为真，则执行模块内的代码<br>
		end<br>
			if模块结束的标识<br>
		exit<br>
			退出插件<br>
		ret<br>
			立即发送消息包并退出本条指令的执行<br>
		inv 字符串, 布尔值(true|false)<br>
			以【字符串】为消息，调用相应的指令<br>
			如果布尔值为true，则将本指令的变量池传递（也就是说发送消息的时候会添加到本条指令的消息里<br>
			会堵塞<br>
		cal 字符串<br>
			以【字符串】为消息，新建线程调用相应的指令<br>
		slp 时长<br>
			堵塞当前线程(1000=1s)<br>
<br>
	对象操作:   (不会用就别用)<br>
        详细请见Skills文件<br>
<br>
Tip:所有的代码指令都可以全大写(例如msg -> MSG)<br>
<br>
<br>
关于流程控制:<br>
if 布尔表达式<br>
代码块1<br>
end<br>
代码块2<br>
<br>
如果布尔表达式为真，执行代码块1，然后再执行代码块2<br>
反之，只执行代码块2<br>
<br>
可以嵌套if噢！！！<br>
<br>
本词库插件支持你美化代码格式<br>
如:(这段代码可能显得有点蠢, 不过为了演示就将就一下吧。。。)<br>
demo<br>
set bool, true<br>
if $bool<br>
    msg bool is true!<br>
    ret<br>
end<br>
msg bool is false!<br>
<br>
注意, 如果指令名以 __(两个下划线) 开头, 插件将会把其视为函数<br>
即无法通过发送消息调用!!!(试验阶段, 可能会发生bug)<br>
<br>
例如:<br>
__hello<br>
msg hi<br>
<br>
hello<br>
这一条是成功的<br>
cal __hello<br>
这两条也是成功的<br>
inv __hello, false<br>
inv __hello, true<br>
<br>
但是发送__hello时, 什么也不会发生<br>
<br>
这个功能你可能无法更改, 但是可以通过组来妨碍插件的识别<br>
例如:(使用?:非捕获组是个好想法)<br>
(?:__)hello<br>
msg hi<br>
这时, 发送__hello会反馈一些东西<br>

## 对象操作
v1.3的对象操作详解<br>
<br>
支持的类标识:<br>
    json -> org.json.JSONObject<br>
    string -> java.lang.String<br>
    class -> java.lang.Class<br>
<br>
支持的值标识:<br>
    Integer -> int.class<br>
    Long -> long.class<br>
    String -> java.lang.String<br>
    Class -> java.lang.Class<br>
<br>
    如果不是上面规定的值标识, 将把传入的标识当作类路径查找(Class.forName)<br>
<br>
指令:<br>
    new 存放的变量名, 类标识[, 构造参数...]<br>
        用构建参数实例化类标识代表的类<br>
        并将对象存放到变量里<br>
    mtd 存放的变量名, 方法所在的类(值标识), 方法名[, 值标识...]<br>
        获取指定类中的指定方法<br>
        存放到变量里<br>
    fun 存放的变量名, mtd对象名, 使用的对象的名称[, 参数的名称...]<br>
        调用指定的方法<br>
        存放到变量里<br>
<br>
实例:<br>
    解析json<br>
    实例化一个json对象<br>
    new jsonObj, json, {"code":200}<br>
    要获取的数据的名称<br>
    new index, string, code<br>
    获取指定方法<br>
    mtd method_get, org.json.JSONObject, get, String<br>
    执行方法<br>
    fun result, method_get, jsonObj, index<br>
    输出<br>
    msg $result<br>
<br>
    输出结果将会是200<br>
<br>
    使用集合(非常麻烦, 不过可以存其他对象<br>
        //子词条, 用于添加对象(需使用inv指令, 并且变量池传递为true<br>
        arrayList::add<br>
        获取添加值的方法<br>
        mtd add, java.util.ArrayList, add, java.lang.Object<br>
        调用<br>
        fun _, add, list, addObj<br>
<br>
        //子词条, 用于创建集合, 需求同上<br>
        arrayList::new<br>
        获取ArrayList的class对象<br>
        new listClass, class, java.util.ArrayList<br>
        获取newInstance方法(调用午餐构造器<br>
        mtd new, Class, newInstance<br>
        执行方法<br>
        fun list, new, listClass<br>
<br>
        //主词条<br>
        list<br>
        调用 创建集合<br>
        inv arrayList::new, true<br>
        放置需要添加的值<br>
        set addObj, 123<br>
        调用 添加值<br>
        inv arrayList::add, true<br>
        输出<br>
        msg $list<br>
<br>
        输出结果应该是[123]<br>


