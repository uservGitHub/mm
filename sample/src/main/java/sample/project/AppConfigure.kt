package sample.project

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.TextView
import org.jetbrains.anko.info
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

/**
 * Created by Administrator on 2018/3/5.
 */

class AppConfigure{
    companion object {
        const val PDFPATH = "gxd.book/atest"
        val PERMISSION = arrayOf<String>(
                //Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    inner class CheckPermission(val success:()->Unit):AppCompatActivity() {
        val permissionCode = 1
        lateinit var errorMsg: TextView
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            //region Permisson(check and request)
            if (savedInstanceState == null) {
                if (checkPermission()) {
                    //this.pass()
                    success()
                } else {
                    ActivityCompat.requestPermissions(
                            this,
                            PERMISSION,
                            permissionCode)
                }
            }
            //endregion

            verticalLayout {
                errorMsg = textView() {
                    textSize = 40F
                    text = "权限校验中 ..."
                }.lparams(width = matchParent, height = matchParent) {
                    gravity = Gravity.CENTER
                }
            }
        }

        //region Permission
        private fun checkPermission() = PERMISSION.any {
            PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, it)
        }

        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            if (requestCode == permissionCode) {
                val notOk = grantResults.any {
                    PackageManager.PERMISSION_GRANTED != it
                }

                if (notOk) {
                    val dump = StringBuilder()
                    dump.append("RequestPermissions Fail\n")
                    for (i in grantResults.indices) {
                        dump.append("${Pair<String, Int>(permissions[i], grantResults[i]).toString()}\n")
                    }
                    //info { dump.toString() }
                    errorMsg.text = dump.toString()
                } else {
                    //info { "RequestPermissions Success" }
                    //this.pass()
                    success()
                }
            }
        }
        //endregion
    }
}