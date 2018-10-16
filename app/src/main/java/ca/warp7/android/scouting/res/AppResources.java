package ca.warp7.android.scouting.res;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;
import android.text.Html;
import android.text.Spanned;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @since v0.4.2
 */

@SuppressWarnings({"ResultOfMethodCallIgnored", "WeakerAccess", "ConstantConditions"})
public class AppResources {

    private static final String kSpecsRoot = "Warp7/specs/";
    private static final String kEventsRoot = "Warp7/events/";

    public static File getSpecsRoot() {
        File root = new File(Environment.getExternalStorageDirectory(), kSpecsRoot);
        root.mkdirs();
        return root;
    }

    public static void copySpecsAssets(Context context) {
        try {
            File root = getSpecsRoot();

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

    public static File getEventsRoot() {
        File root = new File(Environment.getExternalStorageDirectory(), kEventsRoot);
        root.mkdirs();
        return root;
    }

    private static boolean recursiveDelete(File f) {
        boolean deleted = true;
        if (f.isDirectory())
            for (File ff : f.listFiles())
                deleted = deleted && recursiveDelete(ff);
        return deleted && f.delete();
    }

    public static void copyEventAssets(Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            File root = getEventsRoot();
            File[] rootDirs = root.listFiles();
            for (String assetEvent : assetManager.list("events")) {
                for (File rootEventName : rootDirs) {
                    if (rootEventName.isDirectory() && assetEvent.equals(rootEventName.getName())) {
                        recursiveDelete(rootEventName);
                    }
                }
                String eventPath = "events/" + assetEvent;
                File eventDirectory = new File(root, assetEvent);
                eventDirectory.mkdir();
                for (String assetEventFile : assetManager.list(eventPath)) {
                    InputStream inputStream = assetManager.open(eventPath + "/" + assetEventFile);
                    byte[] buffer = new byte[inputStream.available()];
                    inputStream.read(buffer);
                    inputStream.close();
                    File outFile = new File(eventDirectory, assetEventFile);
                    OutputStream outputStream = new FileOutputStream(outFile);
                    outputStream.write(buffer);
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(File f) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(f));
        StringBuilder sb = new StringBuilder();

        String line = br.readLine();

        while (line != null) {
            sb.append(line);
            line = br.readLine();
        }

        br.close();
        return sb.toString();
    }

    public static List<EventInfo> getEvents() {
        File root = getEventsRoot();
        File[] eventRoots = root.listFiles();
        List<EventInfo> eventsList = new ArrayList<>();
        for (File eventRoot : eventRoots) {
            if (eventRoot.isDirectory()) {
                try {
                    EventInfo eventInfo = new EventInfo(eventRoot);
                    eventsList.add(eventInfo);
                } catch (EventInfo.NotProperEventFormat e) {
                    e.printStackTrace();
                }
            }
        }
        return eventsList;
    }
}
