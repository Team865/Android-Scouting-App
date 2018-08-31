package ca.warp7.android.scouting;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;


public class QRFragment extends Fragment {


    public static QRFragment createInstance(String message) {
        QRFragment fragment = new QRFragment();
        Bundle args = new Bundle();
        args.putString("qr_message", message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                setQRImage(view);
            }
        });

        view.findViewById(R.id.send_with_another_method)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String message = getArguments().getString("qr_message");
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, message);
                        intent.setType("text/plain");

                        startActivity(Intent.createChooser(intent, message));
                    }
                });
    }

    private void setQRImage(View view) {
        ImageView qrImage = view.findViewById(R.id.qr_image);
        qrImage.setPadding(16, 16, 16, 16);
        int dim = qrImage.getWidth();

        qrImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_background));
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            qrImage.setImageBitmap(createBitmap(
                    new MultiFormatWriter().encode(
                            getArguments().getString("qr_message"),
                            BarcodeFormat.QR_CODE,
                            dim,
                            dim,
                            hints)));

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a Bitmap from a BitMatrix
     * <p>
     * Code taken and modified from
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
}
