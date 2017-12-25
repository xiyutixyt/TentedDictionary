package tented.dictionary

import com.saki.aidl.PluginMsg
import tented.file.File
import tented.internet.Internet
import java.lang.reflect.Method
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.regex.Matcher
import java.util.regex.Pattern

@Version("now")
object EntryHandler
{
    var entries : Map<String, List<String>> = HashMap()         //词库缓存, 需要发送[重载]载入
    var rootPath : String = "E:/Projects/IntelliJIDEA/TentedDictionary/"        //默认的rootPath, 之前在IDEA上编写的时候用的, 现在搬到Android上就没用了
                                                                                //不过为了纪念一下也不是不可以的啦~

    /**
     * 转换词库文件为Map
     * @param code 词库文件内容
     * @return Map
     */
    @Version("1.0")
    fun toEntries(code : String) : Map<String, List<String>>
    {
        val lines : List<String> = code.split('\n')
        val entries : HashMap<String, List<String>> = HashMap()
        val iterator : Iterator<String> = lines.iterator()      //使用迭代器遍历

        while (iterator.hasNext())      //如果有下一个元素
        {
            val line : String = iterator.next()     //存放到line里

            if (line != "" && ! line.startsWith("//"))     //如果非空行
            {
                val entryRegex : String = line      //词条的指令
                val entryBody : ArrayList<String> = ArrayList()     //词条本体

                while (iterator.hasNext())      //继续遍历
                {
                    val next : String = iterator.next()

                    if (next != "" && ! line.startsWith("//")) entryBody.add(next) else break      //如果为空行就跳出, 否则添加到本体
                }

                entries[entryRegex] = entryBody

                @Version("1.4") saki.demo.Demo.printEntry(entryRegex)       //print entry name to the console
            }
        }

        return entries
    }

    /**
     * 构建词库
     * @param msg 传入的消息包
     * @param entry 接受处理的词条本体
     * @param valuePool 变量池
     * @return 是否使用了ret指令, 用于if模块跳出
     */
    @Version("now")
    @Throws(Exception::class)
    private fun build(msg : PluginMsg, entry : List<String>, valuePool : HashMap<String, Any>) : Boolean
    {
        @Version("1.0")
        fun valuesReplace(input : String) : String      //内置函数, 将字符串模板进行处理
        {
            var message : String = input

            for ((key, value) in valuePool) message = message.replace("\$$key", value.toString())       //遍历变量池中所有元素并替换

            return message
        }

        var codeLine : Int = -1     //代码处理的行数

        while (++ codeLine < entry.size)        //开始遍历代码
        {
            val code : String = entry[codeLine].trimStart()     //获取当前行代码前端空格, 为了支持代码美化
            val index : Int = code.indexOf(' ')     //获取到第一个空格

            val symbol : String = if( index == -1 ) code else code.substring(0, index)      //获取指令
            val arguments : List<String> = code.substring(index + 1).replace("\\,", "\$comma").replace("\\$", "\$value").split(Regex(", *"))
            //获取参数

            when (symbol)       //根据指令处理
            {
                //消息相关
                "msg", "MSG" -> valuePool["__msg__"] = (valuePool["__msg__"] ?: "").toString() + valuesReplace(arguments[0])
                "xml", "XML" -> valuePool["__xml__"] = valuesReplace(arguments[0])
                "json", "JSON" -> valuePool["__json__"] = valuesReplace(arguments[0])
                "img", "IMG" -> valuePool["__img__"] = valuesReplace(arguments[0])
                "at", "AT" -> valuePool["__at__"] = valuesReplace(arguments[0])
                "db", "DB" -> saki.demo.Demo.debug(valuesReplace(arguments[0]))

                //值相关
                "set", "SET" ->
                {
                    if (arguments.size == 2)
                    {
                        val key : String = valuesReplace(arguments[0])
                        val value : String = valuesReplace(arguments[1])

                        valuePool[key] = value
                    }
                }

                "cpy", "CPY" ->
                {
                    if (arguments.size == 2)
                    {
                        val input : String = valuesReplace(arguments[0])
                        val value : String = valuesReplace(arguments[1])

                        if (valuePool.containsKey(value))
                        {
                            valuePool[input] = valuePool[value]!!
                        }
                    }
                }

                "time", "TIME" -> if( arguments.size == 2 ) valuePool[valuesReplace(arguments[0])] = SimpleDateFormat(valuesReplace(arguments[1])).format(Date())

                "opt", "OPT" ->
                {
                    if (arguments.size == 2)
                    {
                        val input : String = valuesReplace(arguments[0])
                        var operation : String = valuesReplace(arguments[1])

                        var matcher : Matcher = Pattern.compile("\\[(-?[0-9]+)([+\\-*/])(-?[0-9]+)]").matcher(operation)

                        while( matcher.find() )
                        {
                            val before : Long = java.lang.Long.parseLong(matcher.group(1))
                            val after : Long = java.lang.Long.parseLong(matcher.group(3))
                            operation =
                                when( matcher.group(2) )
                                {
                                    "+" -> operation.replace(matcher.group(), (before + after).toString())
                                    "-" -> operation.replace(matcher.group(), (before - after).toString())
                                    "*" -> operation.replace(matcher.group(), (before * after).toString())
                                    "/" -> operation.replace(matcher.group(), (before / after).toString())

                                    else -> throw Exception("Operation Exception!!!!!!!!!!")
                                }

                            matcher = Pattern.compile("\\[(-?[0-9]+)([+\\-*/])(-?[0-9]+)]").matcher(operation)
                        }

                        valuePool[input] = operation
                    }
                }

                "rand", "RAND" ->
                {
                    if( arguments.size == 3 )
                    {
                        val input : String = valuesReplace(arguments[0])
                        val from : String = valuesReplace(arguments[1])
                        val to : String = valuesReplace(arguments[2])

                        if( from.isNumber() && to.isNumber() ) valuePool[input] = java.lang.Long.parseLong(from) randomTo java.lang.Long.parseLong(to)
                    }
                }

                //字符串处理
                "rep", "REP" ->
                {
                    if (arguments.size == 4)
                    {
                        val input : String = valuesReplace(arguments[0])
                        val beReplaced : String = valuesReplace(arguments[1])
                        val replaced : String = valuesReplace(arguments[2])
                        val replace : String = valuesReplace(arguments[3])

                        valuePool[input] = beReplaced.replace(replaced, replace)

                    }
                }

                "sub", "SUB" ->
                {
                    if (arguments.size == 4)
                    {
                        val input : String = valuesReplace(arguments[0])
                        var beSubstring : String = valuesReplace(arguments[1])
                        val startString : String = valuesReplace(arguments[2])
                        val endString : String = valuesReplace(arguments[3])

                        if (valuePool.containsKey(beSubstring))
                        {
                            beSubstring = (valuePool[beSubstring] ?: "null").toString()

                            val startIndex : Int = beSubstring.indexOf(startString)

                            var result : String = beSubstring

                            if (startIndex != -1)
                            {
                                val endIndex : Int = beSubstring.indexOf(endString, startIndex + 1)

                                if (endIndex != -1)
                                {
                                    result = result.substring(startIndex + 1, endIndex)
                                }
                            }

                            valuePool[input] = result
                        }
                    }
                }

                //文件相关
                "r", "R" ->
                {
                    if (arguments.size == 4)
                    {
                        val input : String = valuesReplace(arguments[0])
                        val file : String = valuesReplace(arguments[1])
                        val key : String = valuesReplace(arguments[2])
                        val default : String = valuesReplace(arguments[3])

                        valuePool[input] = File.read(rootPath + file, key, default)
                    }
                }

                "w", "W" ->
                {
                    if (arguments.size == 3)
                    {
                        val file : String = valuesReplace(arguments[0])
                        val key : String = valuesReplace(arguments[1])
                        val value : String = valuesReplace(arguments[2])

                        File.write(rootPath + file, key, value)
                    }
                }

                "d", "D" -> if ( arguments.isNotEmpty() ) java.io.File(rootPath + valuesReplace(arguments[0])).delete()

                //群管相关
                "stu", "STU" ->
                {
                    if( arguments.size == 3 )
                    {
                        val group : String = valuesReplace(arguments[0])
                        val uin : String = valuesReplace(arguments[1])
                        val time : String = valuesReplace(arguments[2])

                        if( group.isNumber() && uin.isNumber() && time.isNumber() ) Robot.shutUp(java.lang.Long.parseLong(group), java.lang.Long.parseLong(uin), Integer.parseInt(time))
                    }
                }

                "rm", "RM" ->
                {
                    if( arguments.size == 2 )
                    {
                        val group : String = valuesReplace(arguments[0])
                        val uin : String = valuesReplace(arguments[1])

                        if( group.isNumber() && uin.isNumber() ) Robot.remove(java.lang.Long.parseLong(group), java.lang.Long.parseLong(uin))
                    }
                }

                "rn", "RN" ->
                {
                    if( arguments.size == 3 )
                    {
                        val group : String = valuesReplace(arguments[0])
                        val uin : String = valuesReplace(arguments[1])
                        val name : String = valuesReplace(arguments[2])

                        if( group.isNumber() && uin.isNumber() ) Robot.rename(java.lang.Long.parseLong(group), java.lang.Long.parseLong(uin), name)
                    }
                }

                "like", "LIKE" ->
                {
                    if( arguments.size == 2 )
                    {
                        val uin : String = valuesReplace(arguments[1])
                        val times : String = valuesReplace(arguments[2])

                        if( uin.isNumber() && times.isNumber() ) Robot.favourite( java.lang.Long.parseLong(uin), Integer.parseInt(times))
                    }
                }

                //流程控制
                "if", "IF" ->
                {
                    val expression : String = valuesReplace(arguments[0])

                    val ifBody : List<String>

                    var inlineIfCount = 1
                    val startIndex : Int = codeLine + 1

                    while (++codeLine < entry.size)
                    {
                        val ifCode : String = entry[codeLine]

                        if (ifCode.startsWith("if") || ifCode.startsWith("IF")) inlineIfCount++
                        else if (ifCode == "end" || ifCode == "END") inlineIfCount--

                        if (inlineIfCount == 0) break
                    }

                    if (BooleanBuilder.build(expression))
                    {
                        ifBody = entry.subList(startIndex, codeLine)

                        if(build(msg, ifBody, valuePool)) return true
                    }
                }

                "cal", "CAL" ->       //thread mode
                {
                    Thread(Runnable {
                        if (arguments.isNotEmpty())
                        {
                            val inlineMsg : PluginMsg? = msg.deepClone()

                            if (inlineMsg != null)
                            {
                                inlineMsg.msg = valuesReplace(arguments[0])
                                onEntryHandle(inlineMsg, HashMap(mapOf( "__function__" to "call")))
                            }
                        }
                    }).start()
                }

                "inv", "INV" ->     //function mode
                {
                    if (arguments.size == 2)
                    {
                        val inlineMsg : PluginMsg? = msg.deepClone()
                        val oldMessage : String = valuePool["__msg__"]?.toString() ?: ""
                        val passable : Boolean = valuesReplace(arguments[1]) == "true"

                        if (inlineMsg != null)
                        {
                            val inlinePool : HashMap<String, Any> = if (passable) valuePool else HashMap()

                            inlinePool["__function__"] = "invoke"       //向变量池添加__function__字段

                            inlineMsg.msg = valuesReplace(arguments[0])
                            onEntryHandle(inlineMsg, inlinePool)
                        }

                        @Version("1.4")
                        if( passable ) valuePool["__msg__"] = oldMessage
                    }
                }

                "slp", "SLP" -> if( arguments.size == 1 ) Thread.sleep(java.lang.Long.parseLong(valuesReplace(arguments[0])))

                //对象相关
                @Version("1.0")
                "new", "NEW" ->
                {
                    if( arguments.size > 1 )
                    {
                        val valueName : String = valuesReplace(arguments[0])
                        val cls : String = valuesReplace(arguments[1])

                        val args : Array<String> =
                                if( arguments.size > 2 )
                                {
                                    val inlineArgs : Array<String> = arguments.subList(2, arguments.size).toTypedArray()

                                    inlineArgs.map { valuesReplace(it) }.toTypedArray()
                                }
                                else arrayOf()

                        val `object` : Any? = Instance.newInstance(cls, args)

                        if( `object` != null ) valuePool[valueName] = `object`
                    }
                }

                //生成一个method对象              mtd field, class, name[, classes...]
                @Version("1.3")
                "mtd", "MTD" ->
                {
                    if( arguments.size > 2 )
                    {
                        val input : String = valuesReplace(arguments[0])
                        val className : String = valuesReplace(arguments[1])
                        val methodName : String = valuesReplace(arguments[2])

                        val classes : Array<Class<*>?> = if( arguments.size > 3 ) arguments.subList(3, arguments.size).map { Instance.turnClass(valuesReplace(it)) }.toTypedArray() else arrayOf()

                        valuePool[input] = Instance.turnClass(className)?.getMethod(methodName, * classes) ?: "null"
                    }
                }

                //invoke一个method对象          fun field, method_object, objName[, argumentsName...]
                @Version("1.3")
               "fun", "FUN" ->
                {
                    if( arguments.size > 2 )
                    {
                        val methodName : String = valuesReplace(arguments[1])
                        val methodObj : Method? = valuePool[methodName] as? Method

                        if( methodObj != null )
                        {
                            val input : String = valuesReplace(arguments[0])
                            val objName : String = valuesReplace(arguments[2])
                            val arg : Array<Any?> = if( arguments.size > 3 ) arguments.subList(3, arguments.size).map { valuePool[valuesReplace(it)] }.toTypedArray() else arrayOf()       //获取传入参数所对应的对象名称(请求的是对象名称!!!不是值!!!

                            valuePool[input] = methodObj.invoke(valuePool[objName], * arg)
                        }
                        else throw Exception(saki.demo.Demo.debug("[TentedDictionary-Error]$codeLine: $code       其中, ${arguments[1]}指向的对象不是Method对象, 请使用is指令判断").toString())
                    }
                }

                /*                      1.2版的fun指令处理
                @Version("1.0")
                "fun", "FUN" ->
                {
                    if( arguments.size > 2 )
                    {
                        val result : String = valuesReplace(arguments[0])
                        val `object` : Any? = valuePool[valuesReplace(arguments[1])]
                        val function : String = valuesReplace(arguments[2])

                        val args : Array<String> =
                            if( arguments.size > 3 )
                            {
                                val inlineArgs : Array<String> = arguments.subList(3, arguments.size).toTypedArray()

                                for ( (inlineIndex, arg) in inlineArgs.withIndex() ) inlineArgs[inlineIndex] = valuesReplace(arg)

                                inlineArgs
                            }
                            else arrayOf()

                        val `return` : Any? = Instance.doFunction(`object`, function, args)

                        if (`return` != null) valuePool[result] = `return`
                    }
                }
                */

                "is", "IS" ->
                {
                    if (arguments.size == 3)
                    {
                        val input : String = valuesReplace(arguments[0])
                        val `object` : Any? = valuePool[valuesReplace(arguments[1])]
                        val cls : String = valuesReplace(arguments[2])

                        valuePool[input] = `object`?.javaClass == Class.forName(cls)
                    }
                }

                //网络相关
                "get", "GET" ->
                    if( arguments.size == 2 )
                    valuePool[valuesReplace(arguments[0])] = Internet.get(valuesReplace(arguments[1]))!!


                "post", "POST" ->
                    if( arguments.size == 3 )
                    valuePool[valuesReplace(arguments[0])] = Internet.post(valuesReplace(arguments[1]), valuesReplace(arguments[2]))

                "enc", "ENC" ->
                    if( arguments.size == 2 )
                    valuePool[valuesReplace(arguments[0])] = URLEncoder.encode(valuesReplace(arguments[1]), "UTF-8")

                //特殊处理
                "exit", "EXIT" -> Robot.exit()
                "ret", "RET" -> return true
            }
        }

        return false
    }

    /**
     * 接受一个消息包并处理
     * @param msg 接受处理的消息包
     * @param valuePool 可选, 变量池
     */
    @Version("1.2")
    @Throws(Exception::class)
    fun onEntryHandle(msg : PluginMsg, valuePool : HashMap<String, Any> = HashMap())
    {
        if (entries.isNotEmpty())       //如果词库非空, 其实没啥用
        {
            for ((regex, body) in entries)      //遍历词库
            {
                if( regex.startsWith("__") && ! valuePool.containsKey("__function__") ) continue

                val matcher : Matcher = Pattern.compile(regex).matcher(msg.msg)     //构建一个Matcher, 每一条指令都要这样, 所以时间消耗较大

                if (matcher.matches())      //如果匹配
                {
                    if (valuePool["__function__"] != "invoke")        //如果调用方式非invoke
                    {
                        valuePool["QQ"] = msg.uin
                        valuePool["GROUP"] = msg.group
                        valuePool["NAME"] = msg.uinName ?: "null"
                        valuePool["GNAME"] = msg.groupName ?: "null"
                        valuePool["ROBOT"] = Robot.robotAccount
                        valuePool["CODE"] = msg.code
                        valuePool["comma"] = ","
                        valuePool["value"] = "$"
                        valuePool["line"] = "\n"

                        //写入AT
                        var index : Int = -1
                        while ( ++index < msg.ats.size ) valuePool["AT$index"] = msg.ats[index].toString()

                        index = -1

                        //写入组
                        val groupCount : Int = matcher.groupCount() + 1
                        while( ++ index < groupCount ) valuePool["G$index"] = matcher.group(index)
                    }

                    @Version("1.5") valuePool.remove("__function__")        //无论如何都要移除__function__这个key
                                                                                        //毕竟会影响到双重invoke

                    println("Entry exit by:" + build(msg, body, valuePool))     //构建词库, 并打印日志

                    val type : String? =        //获取发送的消息类型 : xml, json优先
                            when
                            {
                                valuePool.containsKey("__xml__") -> "xml"
                                valuePool.containsKey("__json__") -> "json"
                                valuePool.containsKey("__msg__") -> "msg"

                                else -> null
                            }

                    if (type != null)       //如果有需要发送的消息
                    {
                        msg.clearMsg()      //清空消息包
                        if( valuePool.containsKey("__img__") ) msg.addMsg("img", valuePool["__img__"].toString())   //如果有图片
                        if( valuePool.containsKey("__at__") ) msg.addMsg("at", valuePool["__at__"].toString())      //如果有at
                        msg.addMsg(type, valuePool["__${type}__"].toString().replace(Regex("\\\\[nr]"), "\n"))      //添加消息, 替换\n\r
                        msg.send()      //发送消息
                    }
                }
            }
        }
    }
}