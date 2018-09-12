package ca.warp7.android.scouting.res;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.Html;
import android.text.Spanned;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import ca.warp7.android.scouting.model.Specs;

/**
 * @since v0.4.2
 */

public class AppResources {

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

    @SuppressWarnings("WeakerAccess")
    public static String getRaw(Context context, int id) {
        Resources resources = context.getResources();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    resources.openRawResource(id)));

            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Spanned getHTML(Context context, int id) {
        return Html.fromHtml(getRaw(context, id));
    }
}
