package com.example.iamdenay.tmdb

import android.content.Context
import android.os.AsyncTask
import java.net.HttpURLConnection
import java.net.URL
import java.util.*



interface OnEventListener<T>{
    fun onSuccess(obj: T)
    fun onFailure(e: Exception)
}

public class AsyncTaskHandleJson constructor(callback: OnEventListener<String>) : AsyncTask<String, String, String>(){

    private var mCallBack: OnEventListener<String> = callback
    var mException: Exception? = null


    override fun doInBackground(vararg params: String?): String? {
        var text: String
        val connection = URL(params[0]).openConnection() as HttpURLConnection
        try {
            connection.connect()
            text = connection.inputStream.use { it.reader().use { reader -> reader.readText() } }
            return text
        } catch (e: Exception) {
            mException = e
        }finally {
            connection.disconnect()
        }
        return null

    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(result);
            } else {
                mCallBack.onFailure(mException!!);
            }
        }
    }

}