package com.ayush.steganography;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ayush.steganographylibrary.Text.AsyncTaskCallback.TextEncodingCallback;
import com.ayush.steganographylibrary.Text.TextEncoding;
import com.ayush.steganographylibrary.Text.TextSteganography;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class Encode extends AppCompatActivity implements TextEncodingCallback {

    private static final int SELECT_PICTURE = 100;
    private static final String TAG = "Encode Class";

    private Uri filepath;

    private Bitmap original_image;
    private Bitmap encoded_image;

    TextView whether_encoded;
    ImageView imageView;
    EditText message, secret_key;
    Button choose_image_button, encode_button, save_image_button;

    TextEncoding textEncoding;
    TextSteganography textSteganography, result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode);

        whether_encoded = (TextView) findViewById(R.id.whether_encoded);

        imageView = (ImageView) findViewById(R.id.imageview);

        message = (EditText) findViewById(R.id.message);
        secret_key = (EditText) findViewById(R.id.secret_key);

        choose_image_button = (Button) findViewById(R.id.choose_image_button);
        encode_button = (Button) findViewById(R.id.encode_button);
        save_image_button = (Button) findViewById(R.id.save_image_button);

        choose_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageChooser();
            }
        });

        encode_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whether_encoded.setText("");
                if (filepath != null){
                    if (message.getText() != null ){
                        textSteganography = new TextSteganography(message.getText().toString(),
                                secret_key.getText().toString(),
                                original_image);
                        textEncoding = new TextEncoding(Encode.this, Encode.this);
                        textEncoding.execute(textSteganography);
                    }
                }
            }
        });

        save_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = UUID.randomUUID().toString();

                File file = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                File rootdir = new File(file, name);
                rootdir.mkdir();

                if (encoded_image != null){

                    String name_image = name + "_encoded" + ".png";
                    File encoded_file = new File(rootdir, name_image);
                    try {
                        encoded_file.createNewFile();
                        FileOutputStream fout_encoded_image = new FileOutputStream(encoded_file);
                        encoded_image.compress(Bitmap.CompressFormat.PNG, 100, fout_encoded_image);
                        fout_encoded_image.flush();
                        fout_encoded_image.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                if (original_image != null){

                    String name_image = name + "_original" + ".png";
                    File original_file = new File(rootdir, name_image);
                    try {
                        original_file.createNewFile();
                        FileOutputStream fout_original_image = new FileOutputStream(original_file);
                        original_image.compress(Bitmap.CompressFormat.PNG, 100, fout_original_image);
                        fout_original_image.flush();
                        fout_original_image.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    void ImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Image set to imageView
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null){

            filepath = data.getData();
            try{
                original_image = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);

                imageView.setImageBitmap(original_image);
            }
            catch (IOException e){
                Log.d(TAG, "Error : " + e);
            }
        }

    }

    @Override
    public void onStartTextEncoding() {

    }

    @Override
    public void onCompleteTextEncoding(TextSteganography result) {
        this.result = result;
        if (result != null && result.isEncoded()){
            encoded_image = result.getEncrypted_image();
            whether_encoded.setText("Encoded");
            imageView.setImageBitmap(encoded_image);
        }
    }
}
