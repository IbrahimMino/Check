package com.example.addedittext


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.addedittext.model.Food
import kotlinx.android.synthetic.main.activity_main.*

import java.io.File
import java.io.FileOutputStream

import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    lateinit var bitmap: Bitmap
    lateinit var scaledBitmap: Bitmap
    val pageWidth  = 1200
    val prices = intArrayOf(21000,18000,17000,15000,27000,29000)



    var foodList:MutableList<Food> = ArrayList()
   lateinit var pdfDocument: PdfDocument

    lateinit var edtSoni:EditText

    lateinit var linearList: LinearLayout

    var lists = mutableListOf<String>("Tanlang...","Gamburger","Xot Dog","Lavash","Chizburger")


    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        linearList = findViewById(R.id.layout_list)

        if (ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
             //Request permisson
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),100)
        }else {
            //We have permisson

            bitmap = BitmapFactory.decodeResource(resources,R.drawable.evos)
            scaledBitmap = Bitmap.createScaledBitmap(bitmap,pageWidth,510,false)



            val btnAdd = findViewById<Button>(R.id.btn_addM)
            btnAdd.setOnClickListener {
                addView()
            }



            //Initalizatsiya qilinganlar
             pdfDocument = PdfDocument()

             val edtName = findViewById<EditText>(R.id.edt_name)
            val edtPhone = findViewById<EditText>(R.id.edt_phone)
            val btnSave = findViewById<Button>(R.id.btn_save)
            btnSave.setOnClickListener {
                if (listSave() && edtName.text.isNotEmpty() && edtPhone.text.isNotEmpty()) {



                    val titlePaint = Paint() //for title
                    val paint = Paint()  // for body --> yozuv uchun

                    val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, 2010, 1).create()

                      val myPage = pdfDocument.startPage(pageInfo)
                   // val canvas: Canvas = pdfDocument.startPage(PdfDocument.PageInfo.Builder(pageWidth, 2010, 1).create()).canvas
                    val canvas : Canvas = myPage.canvas
                    canvas.drawBitmap(scaledBitmap, 0f, 0f, paint)


                    //Header number
                    paint.color = Color.WHITE
                    paint.textSize = 35f
                    paint.textAlign = Paint.Align.RIGHT
                    canvas.drawText("+99894-553-40-01", pageWidth.toFloat() - 40, 50f, paint)

                    titlePaint.textAlign = Paint.Align.CENTER
                    titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    titlePaint.textSize = 65f
                    titlePaint.color = Color.BLACK
                    canvas.drawText("Hisobot", (pageWidth / 2).toFloat(), 500f, titlePaint)

//Chap qismi
                    paint.textAlign = Paint.Align.LEFT
                    paint.color = Color.BLACK
                    canvas.drawText("Mijoz: ${edtName.text}", 20f, 590f, paint)
                    canvas.drawText("Tel: ${edtPhone.text}", 20f, 640f, paint)

//Ong qismi
                    paint.textAlign = Paint.Align.RIGHT

                    canvas.drawText("Buyurtma kodi: ${UUID.randomUUID().toString().substring(0, 5)}", pageWidth - 20f, 590f, paint)

//Buyurtma sanasi
                    val date = Date()
                    var dateFormat = SimpleDateFormat("dd/MM/yy")
                    canvas.drawText("Sana: ${dateFormat.format(date)}", pageWidth.toFloat() - 20f, 640f, paint)

                    dateFormat = SimpleDateFormat("HH:mm")
                    canvas.drawText("Vaqt: ${dateFormat.format(date)}", pageWidth.toFloat() - 20f, 690f, paint)


                    //Lines
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = 2f
                    canvas.drawRect(20f, 780f, pageWidth.toFloat() - 20, 860f, paint)

                    paint.textAlign = Paint.Align.LEFT
                    paint.style = Paint.Style.FILL

                    canvas.drawText("No:", 40f, 830f, paint)
                    canvas.drawText("Maxsulot:", 200f, 830f, paint)
                    canvas.drawText("Narxi:", 700f, 830f, paint)
                    canvas.drawText("Soni:", 900f, 830f, paint)
                    canvas.drawText("Jami:", 1050f, 830f, paint)

                    //Middle line
                    canvas.drawLine(180f, 790f, 180f, 850f, paint)
                    canvas.drawLine(680f, 790f, 680f, 850f, paint)
                    canvas.drawLine(880f, 790f, 880f, 850f, paint)
                    canvas.drawLine(1030f, 790f, 1030f, 850f, paint)

                    //Mahsulotlarni chizadi
                    var y = 0
                       var s=0
                    for (n in 0 until foodList.size) {
                        Log.d("Tagi", "Barchasi: ${foodList[n].name}, ${foodList.size}")

                        var total1 = 0

                        canvas.drawText("${n+1}", 40f, (950 + y).toFloat(), paint)
                        canvas.drawText("${foodList[n].name}", 200f, (950 + y).toFloat(), paint)
                        canvas.drawText("${foodList[n].price}", 700f, (950 + y).toFloat(), paint)
                        canvas.drawText("${foodList[n].count}", 900f, (950 + y).toFloat(), paint)
                        total1 = foodList[n].count * foodList[n].price
                        s+= total1
                        canvas.drawText("$total1", 1050f, (950 + y).toFloat(), paint)
                        y += 100
                    }
                    //Final calculate foodlist bn y=0 qilib ketamiz oxirida

                    canvas.drawLine(680f,1200f,pageWidth.toFloat() - 20,1200f,paint)
                    canvas.drawText("Jami",700f,1250f,paint)
                    canvas.drawText(":",900f,1250f,paint)
                    paint.textAlign = Paint.Align.RIGHT
                    canvas.drawText("$s",pageWidth.toFloat() - 40,1250f,paint)

                    paint.textAlign = Paint.Align.LEFT
                    canvas.drawText("10%",700f,1300f,paint)
                    canvas.drawText(":",900f,1300f,paint)
                    paint.textAlign = Paint.Align.RIGHT
                    canvas.drawText("${s/10}",pageWidth.toFloat() - 40,1300f,paint)

                    paint.textAlign = Paint.Align.LEFT
                    paint.color = Color.rgb(0,161,84)
                    canvas.drawRect(680f,1350f,pageWidth.toFloat() - 20,1450f,paint)

                    paint.color = Color.WHITE
                    paint.textSize = 50f
                    paint.textAlign = Paint.Align.LEFT
                    canvas.drawText("To'lov",700f,1415f,paint)
                    canvas.drawText(":",900f,1415f,paint)

                    paint.textAlign = Paint.Align.RIGHT
                    canvas.drawText("${s + s/10}",pageWidth.toFloat() - 40,1415f,paint)
                    //Qoldiqlarni tozalab ketish
                    foodList.clear()
                    y=0
                    s=0
                     edtName.text.clear()
                    edtPhone.text.clear()







                    //pdfni yopish va yozish
                    pdfDocument.finishPage(myPage)

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){

                        try {
                            // val file = File(dir,"/Api29.pdf")
                            val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "/Api.pdf")

                            Log.d("Tag", "listSave: Api 29")
                            pdfDocument.writeTo(FileOutputStream(file))
                          remoeFoodAll()

                        }catch (ex:IOException){
                            ex.printStackTrace()
                        }
                       //pdfDocument.close()

                    }else {
                        Log.d("Tag", "listSave: Api 21")

                        val file = File(Environment.getExternalStorageDirectory(), "/Hisobot-${UUID.randomUUID().toString().substring(0, 5)}.pdf")
                        try {
                            pdfDocument.writeTo(FileOutputStream(file))
                            remoeFoodAll()
                        } catch (ex: IOException) {
                            ex.printStackTrace()
                        }
                       // pdfDocument.close()
                    }

                    Toast.makeText(applicationContext, "Chekni oling!", Toast.LENGTH_SHORT).show()






                }else{
                    Toast.makeText(applicationContext, "Ma'lumotlar to'liq kiritilmadi!", Toast.LENGTH_SHORT).show()
                }

            }

        }
    }


    private fun addView() {

      val viewMain = layoutInflater.inflate(R.layout.row_add_clicked,null,false)

        val textCount = viewMain.findViewById<TextView>(R.id.tv_count)
        val spinnerMain = viewMain.findViewById<Spinner>(R.id.spinner)
            edtSoni = viewMain.findViewById<EditText>(R.id.edt_text)
        val close = viewMain.findViewById<Button>(R.id.btn_close)

          val arrayAdapter = ArrayAdapter(this,android.R.layout.simple_spinner_item,lists)
          spinnerMain.adapter = arrayAdapter


         val count = linearList.childCount + 1
         textCount.text = count.toString()

          //O'chirish
         close.setOnClickListener {
             runOnUiThread {
                 removeFood(viewMain)
             }
             }

         linearList.addView(viewMain)
    }

    private fun removeFood(v: View) {
       linearList.removeView(v)
    }
    private fun remoeFoodAll(){
        runOnUiThread {
           linearList.removeAllViews()
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun listSave():Boolean {
        var result = false
        foodList.clear()
        for (i in 0 until  linearList.childCount){
            val view = linearList.getChildAt(i)
            val spinnerMain = view.findViewById<Spinner>(R.id.spinner)
            val editText = view.findViewById<EditText>(R.id.edt_text)
            if (editText.text.isNotEmpty() && spinnerMain.selectedItemPosition != 0){
                Log.d("Tag", "listSave: ${editText.text} va ${lists[spinnerMain.selectedItemPosition]}")
                val process = prices[spinnerMain.selectedItemPosition]
                foodList.add(Food(lists[spinnerMain.selectedItemPosition],editText.text.toString().toInt(),process))
                    result = true

                 }else{
                Toast.makeText(applicationContext, "Ma'lumotlar to'liq kiririlmadi!", Toast.LENGTH_SHORT).show()
                result = false
            }
        }

        return result

    }



}