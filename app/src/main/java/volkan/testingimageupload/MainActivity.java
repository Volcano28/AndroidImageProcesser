package volkan.testingimageupload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
 * This example shows how to create and handle image picker in Android.
 *
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 *
 */
public class MainActivity extends Activity {
    private Uri mImageCaptureUri;
    private ImageView mImageView;
    private Bitmap operation;

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
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picuri);

                    operation= Bitmap.createBitmap(bitmap.getWidth(),
                            bitmap.getHeight(), bitmap.getConfig());
                   /* int fac = 2;
                    for(int i=0; i<bitmap.getWidth(); i++){
                        for(int j=0; j<bitmap.getHeight(); j++){
                            int p = bitmap.getPixel(i, j);
                            int r = Color.red(p);
                            int g = Color.green(p);
                            int b = Color.blue(p);
                            int alpha = Color.alpha(p);
                            if (r * fac > 255){
                            r = 255;
                            } else {
                                r = fac * r;
                            }
                            if (g * fac > 255){
                                g = 255;
                            } else {
                                g = fac * g;
                            }
                            if (b * fac > 255){
                                b = 255;
                            } else {
                                b = fac * b;
                            }
                            if (alpha * fac > 255){
                                alpha = 255;
                            } else {
                                alpha = fac * alpha;
                            }


                            r = 100  +  r;
                            g = 100  + g;
                            b = 100  + b;
                            alpha = 100 + alpha;

                            operation.setPixel(i, j, Color.argb(Color.alpha(p), r, g, b));
                        } */
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

                            /*rd = (int) red * r;
                            gr = (int) green * g;
                            bl = (int) blue * b;*/

                            r = g = b = (int)(red * r + g * green + b * blue);

                            operation.setPixel(i, j, Color.argb(Color.alpha(p), r, g, b));
                        }
                    }

                    mImageView.setImageBitmap(operation);

                } catch (FileNotFoundException f) {
                    // Bitmap bmp=BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                    //  mImageView.setImageURI(data.getData());
                    //  mImageView.setImageBitmap(bmp);
                } catch (IOException O) {


                }
            }

        /*if (resultCode != RESULT_OK) return;

        Bitmap bitmap 	= null;
        String path		= "";
        mImageView = (ImageView) findViewById(R.id.iv_pic);
        mImageView.setImageURI(data.getData());
        if (requestCode == PICK_FROM_FILE) {
            mImageCaptureUri = data.getData();
            //path = getRealPathFromURI(mImageCaptureUri); //from Gallery
            //Toast.makeText(this, path,Toast.LENGTH_LONG).show();
            //if (path == null)
                path = mImageCaptureUri.getPath(); //from File Manager

            if (path != null){
              //  bitmap 	= BitmapFactory.decodeFile(path);

                Toast.makeText(this, path,Toast.LENGTH_LONG).show();
            }


            mImageView.setImageBitmap(bitmap);
            Toast.makeText(this, data.getData().toString(),Toast.LENGTH_LONG).show();
        } else {
            path	= mImageCaptureUri.getPath();
            bitmap  = BitmapFactory.decodeFile(path);
        }

        mImageView.setImageBitmap(bitmap);*/
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