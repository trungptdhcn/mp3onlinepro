package mp3onlinepro.trungpt.com.mp3onlinepro.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.widget.ImageView;

/**
 * Created by trung on 04/27/2016.
 */
public class ImageUtil
{

//    public static void displayImage(ImageView view, String path, ImageLoadingListener listener)
//    {
//        ImageLoader loader = ImageLoader.getInstance();
//        try
//        {
//            loader.displayImage(path, view, DEFAULT_DISPLAY_IMAGE_OPTIONS, listener);
//        }
//        catch (OutOfMemoryError e)
//        {
//            e.printStackTrace();
//            loader.clearMemoryCache();
//        }
//    }
//
//    public static void displayImageWithSize(ImageView view, String path, ImageLoadingListener listener, final int x, final int y)
//    {
//        ImageLoader loader = ImageLoader.getInstance();
//        try
//        {
//            DisplayImageOptions DEFAULT_DISPLAY_IMAGE_OPTIONS_CUSTOM = new DisplayImageOptions.Builder()
//                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
//                    .displayer(new FadeInBitmapDisplayer(300, true, false, false))
//                    .showImageForEmptyUri(R.drawable.ic_default)
//                    .showImageOnLoading(R.drawable.ic_default)
//                    .showImageOnFail(R.drawable.ic_default).cacheOnDisk(true)
//                    .cacheInMemory(true).bitmapConfig(Bitmap.Config.ARGB_8888)
//                    .preProcessor(new BitmapProcessor()
//                    {
//                        @Override
//                        public Bitmap process(Bitmap bitmap)
//                        {
//                            return scaleCenterCrop(bitmap, x, y);
//                        }
//                    }).build();
//            loader.displayImage(path, view, DEFAULT_DISPLAY_IMAGE_OPTIONS_CUSTOM, listener);
//        }
//        catch (OutOfMemoryError e)
//        {
//            e.printStackTrace();
//            loader.clearMemoryCache();
//        }
//    }
//
//    public static void displayRoundImage(ImageView view, String path, ImageLoadingListener listener)
//    {
//        ImageLoader loader = ImageLoader.getInstance();
//        try
//        {
//            loader.displayImage(path, view, ROUND_DISPLAY_IMAGE_OPTIONS, listener);
//        }
//        catch (OutOfMemoryError e)
//        {
//            e.printStackTrace();
//            loader.clearMemoryCache();
//        }
//    }
//
//    public static void loadImage(String path, ImageLoadingListener listener)
//    {
//        ImageLoader loader = ImageLoader.getInstance();
//        try
//        {
//            loader.loadImage(path, DEFAULT_DISPLAY_IMAGE_OPTIONS, listener);
//        }
//        catch (OutOfMemoryError e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    //TODO Change default image
//    private static final DisplayImageOptions.Builder DEFAULT_DISPLAY_IMAGE_OPTIONS_BUIDLER = new DisplayImageOptions.Builder()
//            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
//            .displayer(new FadeInBitmapDisplayer(300, true, false, false))
//            .showImageForEmptyUri(R.drawable.ic_default)
//            .showImageOnLoading(R.drawable.ic_default)
//            .showImageOnFail(R.drawable.ic_default).cacheOnDisk(true)
//            .cacheInMemory(true).bitmapConfig(Bitmap.Config.ARGB_8888);
//
//    //    private static final DisplayImageOptions DEFAULT_DISPLAY_IMAGE_OPTIONS_CUSTOM = DEFAULT_DISPLAY_IMAGE_OPTIONS_BUIDLER.preProcessor
////            (new BitmapProcessor()
////            {
////                @Override
////                public Bitmap process(Bitmap bitmap)
////                {
////                    Display display = activity.getWindowManager().getDefaultDisplay();
////                    Point size = new Point();
////                    display.getSize(size);
////                    int width = size.x;
////                    return Utils.scaleCenterCrop(bitmap, width / 3, width / 3);
////                }
////            })
////            .build();
//    private static final DisplayImageOptions DEFAULT_DISPLAY_IMAGE_OPTIONS = DEFAULT_DISPLAY_IMAGE_OPTIONS_BUIDLER
//            .build();
//    private static final DisplayImageOptions ROUND_DISPLAY_IMAGE_OPTIONS = DEFAULT_DISPLAY_IMAGE_OPTIONS_BUIDLER
//            .displayer(new RoundedBitmapDisplayer(500)).build();

    public static Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth)
    {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }


    @SuppressLint("NewApi")
    public static Bitmap blurRenderScript(Context context, Bitmap smallBitmap, int radius) {

        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }

    public static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }
}

