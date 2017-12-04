package io.maerlyn.inventorymanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Handles image conversions
 *
 * @author Maerlyn Broadbent
 */
public class ImageUtil {

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public static byte[] getBytes(int resourceId, Context context) {
        Bitmap bitmap = getImage(resourceId, context);
        return getBytes(bitmap);
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static Bitmap getImage(Uri uri, Context context) throws IOException {
        return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
    }

    private static Bitmap getImage(int resourceId, Context context) {
        return BitmapFactory.decodeResource(context.getResources(), resourceId);
    }
}
