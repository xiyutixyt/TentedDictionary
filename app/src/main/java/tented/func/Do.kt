package tented.func

import tented.dictionary.EntryHandler

/**
 * Created by Hoshino Tented on 2017/11/5.
 */
object Do
{

    fun serviceOnCreate()
    {
        saki.demo.Demo.doLoadEntries()

        val message : String =
                """
                    |*********TentedDictionary - Started*********
                    |Version: ${saki.demo.Demo.version}
                    |Author: Hoshino Tented
                    |GMail: limbolrain@gmail.com
                    |
                    |Entries count: ${EntryHandler.entries.size}
                    |
                    |Entries loaded!!!
                    |Thanks for using this plugin
                    |Have a good time!!!
                    |*********TentedDictionary - Message*********
                """.trimMargin()
        println(message)
    }

    private fun create()
    {
        //TODO
    }

    private fun destroy()
    {
        //TODO
    }

    fun onSet(bool : Boolean) = if (bool) create() else destroy()
}