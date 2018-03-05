package pdfbook.sample.stages

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.TextView
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.*
import sample.project.AppConfigure

/**
 * Created by Administrator on 2017/12/31.
 * 1_1 权限检查，通过了发消息
 */

class Stage_1_1:AppCompatActivity(),AnkoLogger{
    override val loggerTag: String
        get() = "Stage_1_1"
    val permissionArray:Array<String>
    lateinit var errorMsg: TextView
    init {
        permissionArray = AppConfigure.PERMISSION
    }
    private fun pass(){
        StageUtils.pass(this)
    }

    override fun onResume() {
        super.onResume()
        async {
            Thread.sleep(1000)
            if(checkPermission()){
                this@Stage_1_1.pass()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //region Permisson(check and request)

        if (savedInstanceState == null) {
            if (!checkPermission()) {
                ActivityCompat.requestPermissions(
                        this@Stage_1_1,
                        permissionArray,
                        permissionCode)
            }
        }
        //endregion
        //StageUtils.defaultRender(this)

        verticalLayout {
            errorMsg = textView() {
                textSize = 30F
                text = "权限校验中 ..."
            }.lparams(width = matchParent, height = matchParent) {
                gravity = Gravity.CENTER
            }
        }
    }
    //region    Permission

    /*val permissionArray = arrayOf<String>(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )*/
    val permissionCode = 1
    private fun checkPermission() = !permissionArray.any {
        PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this@Stage_1_1, it)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == permissionCode) {
            val notOk = grantResults.any {
                PackageManager.PERMISSION_GRANTED != it
            }

            if(notOk){
                val dump = StringBuilder()
                dump.append("RequestPermissions Fail\n")
                for (i in grantResults.indices){
                    dump.append("${Pair<String,Int>(permissions[i],grantResults[i]).toString()}\n")
                }
                info { dump.toString() }
                errorMsg.textColor = Color.RED
                errorMsg.text = dump.toString()
            }else{
                info { "RequestPermissions Success" }
                this.pass()
            }
        }
    }
    //endregion
}