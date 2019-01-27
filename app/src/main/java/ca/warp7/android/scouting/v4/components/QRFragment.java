package ca.warp7.android.scouting.v4.components;

/*
This file contains code modified from
https://github.com/journeyapps/zxing-android-embedded/,
which is licensed under the Apache License, Version 2.0
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import ca.warp7.android.scouting.R;
import ca.warp7.android.scouting.v4.abstraction.ScoutingActivityListener;
import ca.warp7.android.scouting.v4.abstraction.ScoutingTab;
import ca.warp7.android.scouting.v4.model.EntryFormatter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

/**
 * @since v0.4.2
 */

public class QRFragment extends Fragment implements ScoutingTab {

    private String mMessage = " ";
    private ScoutingActivityListener mListener;

    public static QRFragment createInstance() {
        return new QRFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ScoutingActivityListener) {
            mListener = (ScoutingActivityListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ScoutingActivityListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setQRImage(view);
            }
        });

        view.findViewById(R.id.send_with_another_method)
                .setOnClickListener(v -> onSendIntent());
    }

    private void onSendIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, mMessage);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, mMessage));
    }

    private void setQRImage(View view) {
        ImageView qrImage = view.findViewById(R.id.qr_image);
        int dim = qrImage.getWidth();

        qrImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_background));
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, 1);

            qrImage.setImageBitmap(createBitmap(
                    new MultiFormatWriter().encode(
                            mMessage, BarcodeFormat.QR_CODE, dim, dim, hints)));

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a Bitmap from a BitMatrix
     * <p>
     * Code modified from
     * https://github.com/journeyapps/zxing-android-embedded/
     * <p>
     * LICENSED UNDER Apache 2.0
     */

    private Bitmap createBitmap(BitMatrix matrix) {

        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = matrix.get(x, y) ? 0xFF000000 : 0xFFe5e5e5;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    @Override
    public void updateTabState() {
        if (mListener != null) {
            mListener.getEntry().clean();
            String newMessage = EntryFormatter.formatEncode(mListener.getEntry());
            if (!newMessage.equals(mMessage)) {
                mMessage = newMessage;
                View view = getView();
                if (view != null) {
                    setQRImage(view);
                }
            }
        }
    }
}
