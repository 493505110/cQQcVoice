package ml.zhou2008.cqqcvoice

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ml.zhou2008.cqqcvoice.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Suppress("UNREACHABLE_CODE", "DEPRECATION")
class MainActivity : AppCompatActivity() {
    private var permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val tencent = "/storage/emulated/0/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/"
    private var alldone = false
    private var slkpath = ""
    private val cfgf=File("/storage/emulated/0/cqqcvcfg")
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.button1.setOnClickListener { button1Clicked() }
        binding.button2.setOnClickListener { button2Clicked() }
        binding.button3.setOnClickListener { button3Clicked() }

        if (cfgf.exists() && cfgf.isFile){
            binding.txtQQnumber.setText(cfgf.readText())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK){
            val uri = data?.data
            if (uri != null) {
                binding.debug1.text = uri.path?.let { getPath(it) }
            }
        }
    }

    private fun button1Clicked(){
        if (!alldone) {
            if (binding.txtQQnumber.text.toString() == "") {
                Toast.makeText(this, "你还没有输入QQ号哦", Toast.LENGTH_SHORT).show()
                return
            }
            reqPermissions()
            val ptt =
                tencent + binding.txtQQnumber.text.toString() + "/ptt/" + getDate() + "/" + getDay() + "/"
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

    private fun button2Clicked(){
        if (alldone) {
            val f = File(binding.debug1.text.toString())
            f.copyTo(File(slkpath), true)
            Toast.makeText(this, "操作执行成功", Toast.LENGTH_SHORT).show()
            cfgf.writeText(binding.txtQQnumber.text.toString())
            alldone=false
            binding.debug1.text = ""
        }else{
            Toast.makeText(this, "?", Toast.LENGTH_SHORT).show()
        }
    }

    private fun button3Clicked(){
        if (binding.txtQQnumber.text.toString() == "") {
            Toast.makeText(this, "你还没有输入QQ号哦", Toast.LENGTH_SHORT).show()
            return
        }
        reqPermissions()
        val ptt = tencent + binding.txtQQnumber.text + "/ptt/" + getDate() + "/" + getDay() + "/"
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
        val fileList = File(ptt).listFiles()
        if (fileList != null) {
            if (fileList.isNotEmpty()) {
                for (file in fileList){
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

    private fun getPath(uri: String): String{
        val list = uri.split("/document/primary:")
        return "/storage/emulated/0/" + list[1]
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate(): String{
        return if (Build.VERSION.SDK_INT >= 24){
            SimpleDateFormat("yyyyMM").format(Date())
        }else{
            val tms = Calendar.getInstance()
            var month = (tms.get(Calendar.MONTH) + 1).toString()
            val year = tms.get(Calendar.YEAR).toString()
            if (Integer.parseInt(month) <= 9)
                month = "0$month"
            year+month
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDay(): String{
        return if (Build.VERSION.SDK_INT >= 24){
            SimpleDateFormat("d").format(Date())
        }else{
            val tms = Calendar.getInstance()
            val day = tms.get(Calendar.DAY_OF_MONTH).toString()
            day
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