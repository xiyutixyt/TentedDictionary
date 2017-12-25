package tented.dictionary

import com.saki.aidl.PluginMsg

/**
 * @author Hoshino Tented
 */

@Version("0.9")
object Robot
{
    val robotAccount : Long get() = PluginMsg(PluginMsg.TYPE_GET_LOGIN_ACCOUNT).send()!!.uin

    fun shutUp(gid : Long, uin : Long, time : Int)
    {
        val msg = PluginMsg(PluginMsg.TYPE_SET_MEMBER_SHUTUP)

        msg.group = gid

        msg.uin = uin

        msg.value = time * 60

        msg.send()
    }

    fun remove(gid : Long, uin : Long)
    {
        val msg = PluginMsg(PluginMsg.TYPE_DELETE_MEMBER)

        msg.group = gid

        msg.uin = uin

        msg.send()
    }

    fun send(gid : Long, message : String, type : String)
    {
        val msg = PluginMsg()

        msg.group = gid
        msg.addMsg(type, message)

        msg.send()
    }

    fun rename(gid : Long, uin : Long, newName : String)
    {
        val msg = PluginMsg(PluginMsg.TYPE_SET_MEMBER_CARD)

        msg.group = gid

        msg.uin = uin

        msg.title = newName

        msg.send()
    }

    /**
     * 全员禁言
     * @param gid 群号
     */
    fun allGroupShutUp(gid : Long, mod : Boolean)
    {
        val msg = PluginMsg(PluginMsg.TYPE_SET_GROUP_SHUTUP)

        msg.group = gid

        msg.value = if (mod) 0 else 1

        msg.send()
    }

    fun sendToQQ(uin : Long, code : Long, message : String)
    {
        val msg = PluginMsg()

        msg.uin = uin
        msg.code = code

        msg.addMsg("msg", message)

        msg.send()
    }

    fun favourite(uin : Long, times : Int)
    {
        val msg = PluginMsg(PluginMsg.TYPE_FAVOURITE)
        msg.value = times
        msg.uin = uin
        msg.send()
    }

    fun exit()
    {
        System.exit(0)
    }
}
