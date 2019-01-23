package ca.warp7.android.scouting.components

/*
This file contains code modified from
https://github.com/journeyapps/zxing-android-embedded/,
which is licensed under the Apache License, Version 2.0
 */

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import ca.warp7.android.scouting.R
import ca.warp7.android.scouting.ScoutingActivityBase
import ca.warp7.android.scouting.abstraction.ScoutingTab
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import java.util.*

/**
 * @since v0.4.2
 */

class V5QRFragment : Fragment(), ScoutingTab {

    private var message = " "
    private var scoutingActivity: ScoutingActivityBase? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ScoutingActivityBase) scoutingActivity = context
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

        view.findViewById<View>(R.id.send_with_another_method).setOnClickListener { onSendIntent() }
    }

    private fun onSendIntent() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, message)
        intent.type = "text/plain"
        startActivity(Intent.createChooser(intent, message))
    }

    private fun setQRImage(view: View) {
        val qrImage = view.findViewById<ImageView>(R.id.qr_image)
        val dim = qrImage.width
        try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
            hints[EncodeHintType.MARGIN] = 1
            qrImage.setImageBitmap(
                createBitmap(
                    MultiFormatWriter()
                        .encode(message, BarcodeFormat.QR_CODE, dim, dim, hints)
                )
            )
        } catch (e: WriterException) {
            qrImage.setImageDrawable(context?.getDrawable(R.drawable.ic_launcher_background))
            e.printStackTrace()
        }

    }

    /**
     * Creates a Bitmap from a BitMatrix
     *
     *
     * Code modified from
     * https://github.com/journeyapps/zxing-android-embedded/
     *
     *
     * LICENSED UNDER Apache 2.0
     */

    private fun createBitmap(matrix: BitMatrix): Bitmap {

        val width = matrix.width
        val height = matrix.height
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (matrix.get(x, y)) -0x1000000 else -0x1a1a1b
            }
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    override fun updateTabState() {
        val newMessage = scoutingActivity?.entry?.encoded ?: " "
        if (newMessage != message) {
            message = newMessage
            view?.let { setQRImage(it) }
        }
    }
}
