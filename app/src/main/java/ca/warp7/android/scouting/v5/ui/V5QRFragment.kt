package ca.warp7.android.scouting.v5.ui

/*
This file contains code modified from
https://github.com/journeyapps/zxing-android-embedded/,
which is licensed under the Apache License, Version 2.0
 */

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageView
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.v5.BaseScoutingActivity
import com.google.zxing.WriterException

/**
 * @since v0.4.2
 */

class V5QRFragment : Fragment(), V5Tab {

    private var message = " "
    private var scoutingActivity: BaseScoutingActivity? = null

    private var sendButton: Button? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is BaseScoutingActivity) scoutingActivity = context
    }

    override fun onDetach() {
        super.onDetach()
        scoutingActivity = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                setQRImage(view)
            }
        })

        sendButton = view.findViewById(R.id.send_with_another_method)
        sendButton?.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, message))
        }
        sendButton?.text = ""
    }

    private fun setQRImage(view: View) {
        val qrImage = view.findViewById<ImageView>(R.id.qr_image)
        val dim = qrImage.width
        try {
            qrImage.setImageBitmap(createQRBitmap(message, dim))
        } catch (e: WriterException) {
            qrImage.setImageDrawable(context?.getDrawable(R.drawable.ic_launcher_background))
            e.printStackTrace()
        }

    }

    override fun updateTabState() {
        val newMessage = scoutingActivity?.entry?.encoded ?: " "
        sendButton?.text = newMessage
        if (newMessage != message) {
            message = newMessage
            view?.let { setQRImage(it) }
        }
    }
}
