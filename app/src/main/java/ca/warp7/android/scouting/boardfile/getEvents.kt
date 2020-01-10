package ca.warp7.android.scouting.boardfile

import android.os.AsyncTask
import java.io.InputStreamReader
import java.lang.Exception
import java.net.URL

class getEvents : AsyncTask<Void, Void, String?>() {
    private var data: String? = null


    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg p0: Void?): String? {
        try {
            val url = URL("https://www.thebluealliance.com/api/v3/team/frc865/events/2019/simple")
            val connection = url.openConnection()
            connection.addRequestProperty("User-Agent", "User-agent")
            connection.setRequestProperty(
                "X-TBA-Auth-Key",
                "NTFtIarABYtYkZ4u3VmlDsWUtv39Sp5kiowxP1CArw3fiHi3IQ0XcenrH5ONqGOx"
            )

            data = InputStreamReader(connection.getInputStream()).readText()
            println(data)
        } catch (e : Exception){
            e.printStackTrace()
            data = "f"
        }
            return data
    }

    override fun onPostExecute(result: String?) {
    }




}