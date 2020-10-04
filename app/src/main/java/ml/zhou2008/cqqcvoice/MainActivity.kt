package ml.zhou2008.cqqcvoice

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.mbms.FileInfo
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Suppress("UNREACHABLE_CODE")
class MainActivity : AppCompatActivity() {
    private var permissions = arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cfgf=File("/storage/emulated/0/cqqccfg")
        if (cfgf.exists() && cfgf.isFile){
            txt_QQnumber.setText(cfgf.readText())
        }
    }

    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK){
            val uri = data?.data
            if (uri != null) {
                debug_1.text = getPath(uri.path)
            }
        }
    }
    val tencent = "/storage/emulated/0/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/"
    var alldone = false
    var slkpath = ""
    fun onClick(v: View){
        when(v.id){
            R.id.button1 -> deleteSlk()
            R.id.button2 -> button1Clicked()
            R.id.button3 -> button2Clicked()
        }
    }
    private fun button2Clicked(){
        if (alldone) {
            val f = File(debug_1.text.toString())
            f.copyTo(File(slkpath), true)
            Toast.makeText(this, "操作执行成功", Toast.LENGTH_SHORT).show()
            File("/storage/emulated/0/cqqccfg").writeText(txt_QQnumber.text.toString())
            alldone=false
            debug_1.text = ""
        }else{
            Toast.makeText(this, "?", Toast.LENGTH_SHORT).show()
        }
    }
    private fun button1Clicked(){
        if (txt_QQnumber.text.toString() == "") {
            Toast.makeText(this, "你还没有输入QQ号哦", Toast.LENGTH_SHORT).show()
            return
        }
        reqPermissions()
        val ptt = tencent + txt_QQnumber.text + "/ptt/" + getDate() + "/" + getDay() + "/"
        val files = File(ptt).listFiles()
        if (files != null) {
            if (files.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                    .setType("*/*")
                    .addCategory(Intent.CATEGORY_OPENABLE)
                startActivityForResult(intent, 1)
            }else{
                Toast.makeText(this, "目录是空的", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this,"目录是空的",Toast.LENGTH_SHORT).show()
        }
        val filelist = File(ptt).listFiles()
        if (filelist != null) {
            if (filelist.isNotEmpty()) {
                for (file in filelist){
                    if (file.isFile && file.name.endsWith(".slk")){
                        slkpath=file.path
                    }
                }
                alldone=true
            } else {
                Toast.makeText(this, "目录是空的", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "目录是空的", Toast.LENGTH_SHORT).show()
        }
    }
    private fun deleteSlk(){
        if (!alldone) {
            if (txt_QQnumber.text.toString() == "") {
                Toast.makeText(this, "你还没有输入QQ号哦", Toast.LENGTH_SHORT).show()
                return
            }
            reqPermissions()
            val ptt =
                tencent + txt_QQnumber.text.toString() + "/ptt/" + getDate() + "/" + getDay() + "/"
            val files = File(ptt).listFiles()
            if (files != null) {
                if (files.isNotEmpty()) {
                    for (file in files) {
                        if (file.isFile && file.name.endsWith(".slk")) {
                            Toast.makeText(this, "已删除: ${file.name}", Toast.LENGTH_SHORT).show()
                            file.delete()
                        }
                    }
                } else {
                    Toast.makeText(this, "目录是空的", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "目录是空的", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this,"?",Toast.LENGTH_SHORT).show()
        }
    }
    private fun getPath(uri: String?): String?{
        val list = uri?.split("/document/primary:")
        return "/storage/emulated/0/" + list?.get(1)
    }
    @SuppressLint("SimpleDateFormat")
    private fun getDate(): String{
        if (Build.VERSION.SDK_INT >= 24){
            return SimpleDateFormat("yyyyMM").format(Date())
        }else{
            val tms = Calendar.getInstance()
            var month = Integer.toString(tms.get(Calendar.MONTH)+1)
            val year = Integer.toString(tms.get(Calendar.YEAR))
            if (Integer.parseInt(month) <= 9)
                month = "0" + month
            return year+month
        }
    }
    @SuppressLint("SimpleDateFormat")
    private fun getDay(): String{
        if (Build.VERSION.SDK_INT >= 24){
            return SimpleDateFormat("d").format(Date())
        }else{
            val tms = Calendar.getInstance()
            val day = Integer.toString(tms.get(Calendar.DAY_OF_MONTH))
            return day
        }
    }
    private fun reqPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(permissions,1)
            }
        }
    }
}