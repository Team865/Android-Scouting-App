package ca.warp7.android.scouting.boardfile

import android.os.AsyncTask
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class getEvent() : AsyncTask<Void,Void, String>(){

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg p0: Void?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
    }
}