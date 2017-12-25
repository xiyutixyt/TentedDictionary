package tented.dictionary

import org.json.JSONObject

object Instance
{
    @Version("1.4")
    fun newInstance(cls : String, args : Array<String>) : Any? =
            when (cls)
            {
                "json", "JSON" -> if ( args.isNotEmpty() ) JSONObject(args[0]) else JSONObject()
                "string", "STRING" -> if( args.isNotEmpty() ) java.lang.String(args[0]) else java.lang.String()
                "int", "INT" -> Integer.parseInt(args[0])
                "long", "LONG" -> java.lang.Long.parseLong(args[0])
                "class", "CLASS" -> if( args.isNotEmpty() ) turnClass(args[0]) else null
                else -> null
            }

    @Version("1.4")
    fun turnClass( classType : String ) : Class<*>? =
        when( classType )
        {
            "Integer" -> Int::class.java
            "Long" -> Long::class.java
            "String" -> java.lang.String::class.java
            "Class" -> Class::class.java

            else -> Class.forName(classType)
        }

    /*
    @Version("1.0")
    fun turnObject ( classType : String , arg : String ) : Any? =
            when( classType )
            {
                "Integer" -> Integer.parseInt(arg)
                "Long" -> java.lang.Long.parseLong(arg)
                "String" -> arg
                "Class" ->  when( arg )
                            {
                                "int", "Int", "Integer", "integer" -> Int::class.java
                                "long", "Long" -> Long::class.java

                                else -> Class.forName(arg)
                            }

                else -> null
            }
    */

    /*
    @Version("1.0")
    @Deprecated("请使用mtd + fun指令组合, fun一体命令已在1.3+废弃")
    fun doFunction(obj : Any?, function : String, args : Array<String> ) : Any? =
        if( obj != null )
        {
            val classArray : Array<Class<*>?> = arrayOfNulls(args.size)
            val argumentsArray : Array<Any?> = arrayOfNulls(args.size)

            for ((index, argument) in args.withIndex())
            {
                val split : Int = argument.indexOf("->")
                val classType : String = argument.substring(0, split)
                val arg : String = argument.substring(split + 2)

                classArray[index] = turnClass(classType)
                argumentsArray[index] = turnObject(classType, arg)
            }

            obj::class.java.getMethod(function, * classArray).invoke(obj, * argumentsArray)
        }

        else null
    */
}