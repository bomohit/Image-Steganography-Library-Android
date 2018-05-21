package com.ayush.steganographylibrary.Text;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.ayush.steganographylibrary.Utils.Crypto;
import com.ayush.steganographylibrary.Utils.Utility;
import com.ayush.steganographylibrary.Utils.Zipping;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * In this class all those method in EncodeDecode class are used to decode secret message in image.
 * All the tasks will run in background.
 */
public class TextDecoding extends AsyncTask<TextSteganography, Void, TextSteganography> {

    //Tag for Log
    private final static String TAG = TextDecoding.class.getName();

    Activity activity;

    private ProgressDialog progressDialog;

    public TextDecoding(Activity activity) {
        super();
        this.activity = activity;
    }

    //setting progress dialog if wanted
    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    //pre execution of method
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(activity);

        //setting parameters of progress dialog
        if (progressDialog != null){
            progressDialog.setMessage("Loading, Please Wait...");
            progressDialog.setTitle("Decoding Message");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }
    }

    @Override
    protected void onPostExecute(TextSteganography textSteganography) {
        super.onPostExecute(textSteganography);

        //dismiss progress dialog
        if(progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

        //Updating progress dialog
        if(progressDialog != null) {
            progressDialog.show();
        }
    }

    @Override
    protected TextSteganography doInBackground(TextSteganography... textSteganographies) {

        //making result object
        TextSteganography result = null;

        publishProgress();

        if (textSteganographies.length > 0){

            TextSteganography textSteganography = textSteganographies[0];

            //getting bitmap image from file
            Bitmap bitmap = textSteganography.getImage();

            //return null if bitmap is null
            if (bitmap == null)
                return result;

            //splitting images
            List<Bitmap> srcEncodedList = Utility.splitImage(bitmap);

            //decoding encrypted zipped message
            String decoded_message = EncodeDecode.decodeMessage(srcEncodedList);

            Log.d("TextDecoding" , "Decoded_Message : " + decoded_message);

            String message = textSteganography.decryptMessage(decoded_message, textSteganography.getSecret_key());

            try {
                message = Zipping.decompress(message.getBytes("ISO-8859-1"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (message != null && Utility.isStringEmpty(message)) {
                try {
                    result.setMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //free memory
            for (Bitmap bitm : srcEncodedList)
                bitm.recycle();

            //Java Garbage Collector
            System.gc();

        }

        return result;
    }
}
