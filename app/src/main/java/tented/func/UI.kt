package tented.func

import com.tented.demo.kotlin.R
import android.app.Activity
import android.os.Bundle

/**
 * Created by Hoshino Tented on 2017/11/5.
 */

class UI : Activity()
{
    override fun onCreate(bundle: Bundle?)
    {
        super.onCreate(bundle)
        setContentView(R.layout.layout)
        finish()        //启动界面后立即退出
    }
}
