package ca.warp7.android.scouting.data;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ca.warp7.android.scouting.model.Specs;

public class AppAssets {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void copyAssets(Context context) {
        try {
            File root = Specs.getSpecsRoot();

            AssetManager assetManager = context.getAssets();
            for (String fileName : assetManager.list("specs")) {

                InputStream inputStream = assetManager.open("specs/" + fileName);
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                inputStream.close();

                File outFile = new File(root, fileName);
                OutputStream outputStream = new FileOutputStream(outFile);
                outputStream.write(buffer);
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
