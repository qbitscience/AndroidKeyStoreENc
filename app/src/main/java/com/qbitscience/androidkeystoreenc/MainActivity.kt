package com.qbitscience.androidkeystoreenc

import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException


class MainActivity : AppCompatActivity() {
    lateinit var enCryptor:EnCryptor
    lateinit var deCryptor: DeCryptor
    lateinit var encrypted_text:TextView
    lateinit var decrypted_text:TextView
    private val TAG = MainActivity::class.java.simpleName
    private val SAMPLE_ALIAS = "MYALIAS"
    lateinit var entry:EditText
    lateinit var encryptedText: ByteArray
    lateinit var sharedPreferences:SharedPreferences
    lateinit var editor:SharedPreferences.Editor
    lateinit var stored_text:TextView
    lateinit var sqLiteDatabase:SQLiteDatabase
    lateinit var sqlText:TextView



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        entry=findViewById(R.id.entry)
        var encrypt:Button=findViewById(R.id.encrypt)
        var decrypt:Button=findViewById(R.id.decrypt)
        encrypted_text=findViewById(R.id.encrypted_text)
        decrypted_text=findViewById(R.id.decrypted_text)
        stored_text=findViewById(R.id.stored_text)
        sqlText=findViewById(R.id.sql_text)
        sharedPreferences=getSharedPreferences("DataMy", MODE_PRIVATE)
        sqLiteDatabase=openOrCreateDatabase("myDatabase", MODE_PRIVATE,null)
        sqLiteDatabase.execSQL("create table if not exists Encryption(name varchar(50))")

        editor=sharedPreferences.edit()
        enCryptor = EnCryptor()

        try {
            deCryptor = DeCryptor()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        encrypt.setOnClickListener {

       encryptText()
        }
        decrypt.setOnClickListener {
  decryptText()
        }

    }
    private fun decryptText() {
        try {
         /*   decrypted_text.setText(
                deCryptor.decryptData(SAMPLE_ALIAS,encryptedText, enCryptor.getIv())
            )*/
            sharedPreferences=getSharedPreferences("DataMy", MODE_PRIVATE)

            var data=sharedPreferences.getString("test","default")
            stored_text.setText(data)
            val array: ByteArray = Base64.decode(data, Base64.DEFAULT)
            Log.d("encData", encryptedText.toString())
            decrypted_text.setText(deCryptor.decryptData(SAMPLE_ALIAS,
                array, enCryptor.getIv()))

                //  var sql=
            var cursor:Cursor=sqLiteDatabase.rawQuery("select * from Encryption",null)
            cursor.moveToLast()
            var name=cursor.getString(0)
            val name2Sql: ByteArray = Base64.decode(name, Base64.DEFAULT)
            sqlText.setText(deCryptor.decryptData(SAMPLE_ALIAS,
                name2Sql, enCryptor.getIv()))

           // sqlText.setText(name2Sql)
        } catch (e: UnrecoverableEntryException) {
        } catch (e: NoSuchAlgorithmException) {
        } catch (e: KeyStoreException) {
        } catch (e: NoSuchPaddingException) {
        } catch (e: NoSuchProviderException) {
        } catch (e: IOException) {
        } catch (e: InvalidKeyException) {
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun encryptText() {
        try {
            encryptedText= enCryptor
                .encryptText(SAMPLE_ALIAS, entry.getText().toString())
            sharedPreferences=getSharedPreferences("DataMy", MODE_PRIVATE)
            editor=sharedPreferences.edit()
            val saveThis = Base64.encodeToString(encryptedText, Base64.DEFAULT)


            editor.putString("test",saveThis).apply()

            sqLiteDatabase.execSQL("insert into Encryption values('$saveThis')")

            // encrypted_text.setText(Base64.encodeToString(encryptedText, Base64.DEFAULT))
                encrypted_text.setText(encryptedText.toString())
        } catch (e: UnrecoverableEntryException) {
        } catch (e: NoSuchAlgorithmException) {
        } catch (e: NoSuchProviderException) {
        } catch (e: KeyStoreException) {
       } catch (e: IOException) {
        } catch (e: NoSuchPaddingException) {
        } catch (e: InvalidKeyException) {
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        } catch (e: SignatureException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        }
    }


}
