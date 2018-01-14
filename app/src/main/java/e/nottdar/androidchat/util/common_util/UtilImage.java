package e.nottdar.androidchat.util.common_util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

public class UtilImage {


    public static Bitmap getBitmapFromAssets(Context context, String fileName) {
        System.out.println("here");
        AssetManager assetManager = context.getAssets();
        Bitmap bitmap = null;
        InputStream istr = null;
        try {
            istr = assetManager.open("IMAGES".concat("/").concat(fileName));
            // istr = assetManager.open(fileName);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (istr != null) {
                    istr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }
}
