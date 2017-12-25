package tented.dictionary

import java.util.regex.Matcher
import java.util.regex.Pattern

@Version("0.9")
object BooleanBuilder
{
    private val relationPattern : Pattern = Pattern.compile("([^|&]+?)(==|>=|<=|>|<|!=)([^|&]+)")
    private val logicPattern : Pattern = Pattern.compile("(true|false)([|&])(true|false)")

    fun build ( input : String ) : Boolean
    {
        var input : String = input.replace("&&", "&")
        var matcher : Matcher = relationPattern.matcher(input)

        while( matcher.find() )
        {
            val before : String = matcher.group(1)
            val after : String = matcher.group(3)
            val symbol : String = matcher.group(2)

            val result : Boolean =
            if( symbol == "==" ) before == after
            else if( symbol == "!=" ) before != after
            else if( before.isNumber() && after.isNumber() )
            {
                val bef : Long = java.lang.Long.parseLong(before)
                val aft : Long = java.lang.Long.parseLong(after)

                when ( symbol )
                {
                    ">=" -> bef >= aft
                    "<=" -> bef <= aft
                    ">" -> bef > aft
                    "<" -> bef < aft

                    else -> false
                }
            }
            else false

            input = input.replace(matcher.group(), result.toString())

            matcher = relationPattern.matcher(input)
        }

        matcher = logicPattern.matcher(input)

        while( matcher.find() )
        {
            val before : String = matcher.group(1)
            val symbol : String = matcher.group(2)
            val after : String = matcher.group(3)

            val result : Boolean = if( symbol == "|" )  before == "true" || after == "true" else before == after && before == "true"

            input = input.replace(matcher.group(), result.toString())
            matcher = logicPattern.matcher(input)
        }

        return input == "true"
    }
}