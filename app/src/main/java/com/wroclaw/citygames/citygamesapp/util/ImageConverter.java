package com.wroclaw.citygames.citygamesapp.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.wroclaw.citygames.citygamesapp.App;
import com.wroclaw.citygames.citygamesapp.Globals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ImageConverter {
    private static final String TAG = ImageConverter.class.getName();
    public static Bitmap decode(String image){

        byte[] bytes = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static String saveBitmap(String filename,Bitmap bitmap){
        ContextWrapper cw = new ContextWrapper(App.getCtx());
        File directory = cw.getDir(Globals.IMAGE_DIRECTORY_PATH, Context.MODE_PRIVATE);
        File image=new File(directory,filename);
        Log.d(TAG,"zapis do pliku "+image.getAbsolutePath());
        try {
            FileOutputStream fos = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image.getAbsolutePath();
    }

    public static Bitmap loadBitmap(String filename) {
        try {
            File f = new File(filename);//(Globals.IMAGE_DIRECTORY_PATH, filename);
            Log.d(TAG, "odczyt pliku " + f.getAbsolutePath());
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "nie znaleziono pliku " + filename);
            e.printStackTrace();
        }
        return null;
    }

    public static void cleanPhotoDir() {
        ContextWrapper cw = new ContextWrapper(App.getCtx());
        File directory = cw.getDir(Globals.IMAGE_DIRECTORY_PATH, Context.MODE_PRIVATE);
        Log.d(TAG,"Rozmiar katalogu "+ String.valueOf(directory.getUsableSpace()));
        if (directory.isDirectory()) {
            for (File c : directory.listFiles()) {
                boolean delete = c.delete();
            }
        }
        Log.d(TAG,"Rozmiar katalogu " + String.valueOf(directory.getUsableSpace()));
    }
}