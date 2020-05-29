package com.example.pictures;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private Button add, show;
    private MyDatabaseHelper helper;
    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/PicturesDb";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        add=findViewById(R.id.add);
        show=findViewById(R.id.show);
        helper=new MyDatabaseHelper(this,"pictures.db",null,1);
        helper.getWritableDatabase();
        final int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"};
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(MainActivity.this,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 2);
            }
        });
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,showPictures.class);
                //启动
                startActivity(intent);

            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                System.out.println("===="+filePath+"=====");
                File localFile = new File(filePath);
                if (!localFile.exists()) {
                    localFile.mkdir();
                }
                File finalImageFile = new File(localFile, System.currentTimeMillis()+ ".jpg");

                System.out.println("===="+finalImageFile+"=====");
                if (finalImageFile.exists()) {
                    finalImageFile.delete();
                }
                try {
                    finalImageFile.createNewFile();
//                    System.out.println("====create====");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(finalImageFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (bitmap == null) {
                        Toast.makeText(this, "图片不存在", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    try {
                        fos.flush();
                        fos.close();
                        Toast.makeText(this, "图片保存在："+ finalImageFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }

                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("path",finalImageFile+"");
                db.insert("PICTURES",null, values);
//                if(res != -1) {
//                    Toast.makeText(MainActivity.this,"insert db Successful",Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(MainActivity.this,"insert db failed",Toast.LENGTH_SHORT).show();
//                }
            }//data != null
        }//requestCode == 2
    }//onActivityResult



}