package volkan.testingimageupload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.graphics.Color;
import android.provider.MediaStore;
import android.net.Uri;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.os.Environment;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;
import android.content.Context;

/**
 * This example shows how to create and handle image picking in Android. Moreover Three common algorithms is being used
 * in this program, which are Gray Level scaling, increasing Brightness and Dithering algorithms.
 *
 *
 * @author Volkan Oztuzun <volkanoztuzun@gmail.com>
 *
 */
public class MainActivity extends Activity {
    private Uri mImageCaptureUri;
    private ImageView mImageView;
    private Bitmap bitmap;
    private Bitmap operation;
    public double[] threshold = { 0.25, 0.26, 0.27, 0.28, 0.29, 0.3, 0.31,
            0.32, 0.33, 0.34, 0.35, 0.36, 0.37, 0.38, 0.39, 0.4, 0.41, 0.42,
            0.43, 0.44, 0.45, 0.46, 0.47, 0.48, 0.49, 0.5, 0.51, 0.52, 0.53,
            0.54, 0.55, 0.56, 0.57, 0.58, 0.59, 0.6, 0.61, 0.62, 0.63, 0.64,
            0.65, 0.66, 0.67, 0.68, 0.69 };

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final String [] items			= new String [] {"From Camera", "From SD Card"};
        ArrayAdapter<String> adapter	= new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder		= new AlertDialog.Builder(this);

        builder.setTitle("Select Image");
        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int item ) {
                if (item == 0) {
                    Intent intent 	 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File file		 = new File(Environment.getExternalStorageDirectory(),
                            "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                    mImageCaptureUri = Uri.fromFile(file);

                    try {
                        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        intent.putExtra("return-data", true);

                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dialog.cancel();
                } else {
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }
        } );

        final AlertDialog dialog = builder.create();

        mImageView = (ImageView) findViewById(R.id.iv_pic);

        ((Button) findViewById(R.id.btn_choose)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {

        if(reqCode == PICK_FROM_FILE) {
            try {
                mImageView = (ImageView) findViewById(R.id.iv_pic);
                Uri picuri = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picuri);

                operation= Bitmap.createBitmap(bitmap.getWidth(),

                        bitmap.getHeight(), Bitmap.Config.ARGB_8888);

                mImageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException f) {
                // Bitmap bmp=BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                //  mImageView.setImageURI(data.getData());
                //  mImageView.setImageBitmap(bmp);
            } catch (IOException O) {


            }
        }
    }

    public void Gray(View view){

        operation= Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), bitmap.getConfig());
        int rd;
        int gr;
        int bl;
        double red = 0.30;
        double green = 0.59;
        double blue = 0.11;

        for(int i=0; i<bitmap.getWidth(); i++){
            for(int j=0; j<bitmap.getHeight(); j++){
                int p = bitmap.getPixel(i, j);
                int r = Color.red(p);
                int g = Color.green(p);
                int b = Color.blue(p);


                r = g = b = (int)(red * r + g * green + b * blue);

                operation.setPixel(i, j, Color.argb(Color.alpha(p), r, g, b));
            }
        }
        mImageView.setImageBitmap(operation);
    }

    public void Brightness(View view){

        operation= Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), bitmap.getConfig());
        int fac = 2;
        int value = 50;
        for(int i=0; i<bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                int p = bitmap.getPixel(i, j);
                int r = Color.red(p);
                int g = Color.green(p);
                int b = Color.blue(p);
                int alpha = Color.alpha(p);

                r += value;
                if( r > 255 ){
                    r = 255;
                } else if ( r < 0 ){
                    r = 0;
                }

                g += value;
                if( g > 255 ){
                    g = 255;
                } else if ( g < 0 ){
                    g = 0;
                }

                b += value;
                if( b > 255 ){
                    b = 255;
                } else if ( b < 0 ){
                    b = 0;
                }

                operation.setPixel(i, j, Color.argb(Color.alpha(p), r, g, b));
            }
        }
        mImageView.setImageBitmap(operation);
    }

    public void Dithering(View view) {
        operation = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Random randomnumber = new Random();

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {

                int color = bitmap.getPixel(i, j);
                int red = (color >>> 16) & 0xFF;
                int green = (color >>> 8) & 0xFF;
                int blue = color & 0xFF;

                double lim = (red * 0.21f + green * 0.71f + blue * 0.07f) / 255;


                if (lim <= threshold[randomnumber.nextInt(threshold.length)]) {

                    operation.setPixel(i, j, Color.argb(Color.alpha(color), 0x000000, 0x000000, 0x000000));

                } else {

                    operation.setPixel(i, j, Color.argb(Color.alpha(color), 0xFFFFFF, 0xFFFFFF, 0xFFFFFF));

                }
            }

        }
        mImageView.setImageBitmap(operation);

    }

    public String getRealPathFromURI(Uri contentUri) {
        String [] proj 		= {MediaStore.Images.Media.DATA};
        Cursor cursor 		= managedQuery( contentUri, proj, null, null,null);

        if (cursor == null) return null;

        int column_index 	= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);
    }
}