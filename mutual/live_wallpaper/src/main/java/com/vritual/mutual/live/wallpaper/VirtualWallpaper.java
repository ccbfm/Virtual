package com.vritual.mutual.live.wallpaper;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class VirtualWallpaper extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        WallpaperManager wm = (WallpaperManager) getApplicationContext().getSystemService(Context.WALLPAPER_SERVICE);
        return new VirtualEngine(wm.getBuiltInDrawable());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("VirtualEngine", "onCreate ");
    }

    private class VirtualEngine extends Engine {

        private final Bitmap mBitmap;

        public VirtualEngine(Drawable drawable) {
            mBitmap = convertByDrawable(drawable);
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            Canvas lockCanvas = holder.lockCanvas();
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Bitmap bitmap = mBitmap;

            if (bitmap != null) {
                Log.d("VirtualEngine", "onSurfaceCreated " + bitmap.getWidth() + " " + bitmap.getHeight());
                lockCanvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
            }
            holder.unlockCanvasAndPost(lockCanvas);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
        }

        private Bitmap convertByDrawable(Drawable drawable) {
            Bitmap bitmap = null;
            if (drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            } else if (!(drawable instanceof NinePatchDrawable)) {
                return null;
            } else {
                int width = drawable.getIntrinsicWidth();
                int height = drawable.getIntrinsicHeight();
                Log.d("VirtualEngine", "convertByDrawable " + width + " " + height);
                Bitmap createBitmap = Bitmap.createBitmap(
                        width,
                        height,
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(createBitmap);
                drawable.setBounds(0, 0, width, height);
                drawable.draw(canvas);
                bitmap = createBitmap;
            }

            if (bitmap != null) {
                Point realSize = new Point();
                Display realDisplay = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                realDisplay.getRealSize(realSize);
                Log.d("VirtualEngine", "onSurfaceCreated realSize " + realSize.x + " " + realSize.y);

                int bWidth = bitmap.getWidth();
                int bHeight = bitmap.getHeight();

                float scaleWidth = ((float) realSize.x) / bWidth;
                float scaleHeight = ((float) realSize.y) / bHeight;
                // 取得想要缩放的matrix参数
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);
                // 得到新的图片
                return Bitmap.createBitmap(bitmap, 0, 0, bWidth, bHeight, matrix, true);
            }
            return null;
        }
    }

    public static boolean isWallpaper(Context context) {
        WallpaperInfo wallpaperInfo = WallpaperManager.getInstance(context).getWallpaperInfo();
        return wallpaperInfo != null && wallpaperInfo.getPackageName().equals(context.getPackageName());
    }

    public static void start(Context context) {
        Intent chooseIntent = new Intent(Intent.ACTION_SET_WALLPAPER);
        Intent intent = new Intent(Intent.ACTION_CHOOSER);
        intent.putExtra(Intent.EXTRA_INTENT, chooseIntent);
        intent.putExtra(Intent.EXTRA_TITLE, "选择壁纸");
        context.startActivity(intent);
    }
}
