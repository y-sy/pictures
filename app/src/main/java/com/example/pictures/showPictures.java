package com.example.pictures;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class showPictures extends AppCompatActivity {

    private MyDatabaseHelper helper;
    private SQLiteDatabase db;
    private GridView gridView;
    Map<Integer, Integer> ids=new HashMap<>();
    Map<Integer, String>paths=new HashMap<>();
    private AlertDialog.Builder builder,builder1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pictures);
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle("表情包图库");
        actionBar.setDisplayHomeAsUpEnabled(true);
        gridView=findViewById(R.id.gridView);
        helper=new MyDatabaseHelper(this,"pictures.db",null,1);
        helper.getWritableDatabase();
        db=helper.getWritableDatabase();
        init();
        clickGridView();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init()
    {

        Cursor cursor=db.query("PICTURES",null,null,null,null,null,null);
        List<Map<String, Object>> data = new ArrayList<>();

        int i=0;
        if (cursor.moveToFirst())
        {
            do{
                Map<String, Object> map = new HashMap<>();
                int id=cursor.getInt(cursor.getColumnIndex("id"));
                ids.put(i,id);
                String path=cursor.getString(cursor.getColumnIndex("path"));
                paths.put(i,path);
                i++;
                map.put("picture", Uri.fromFile(new File(path)));
                //如果只需要显示图片，可以不用这一行，需要同时将from和to中的相关内容删去
//                map.put("name", name);
                data.add(map);
            }while (cursor.moveToNext());
        }
        cursor.close();
        String[] from = {"picture"};
        int[] to = {R.id.img};
        //实例化一个适配器
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.item, from, to);
        //为GridView设置适配器
        gridView.setAdapter(adapter);



    }//init

    private void clickGridView()
    {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, final long l) {
                final String[] operate = new String[]{"删除","查看原图"};
                builder1=new AlertDialog.Builder(showPictures.this);
                builder1.setTitle("请选择操作");
                builder1.setItems(operate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(operate[which]=="删除")
                        {
                            builder = new AlertDialog.Builder(showPictures.this).setTitle("删除")
                                    .setMessage("是否删除此图片？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //ToDo: 你想做的事情
                                            long res =db.delete("PICTURES","id=?",new String[]{String.valueOf(ids.get(new Long(l).intValue()))});
                                            File file = new File(paths.get(new Long(l).intValue()));
//删除系统缩略图
                                            getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{paths.get(new Long(l).intValue())});
//删除手机中图
                                            file.delete();

                                            if(res != -1) {
                                                Toast.makeText(showPictures.this,"删除成功",Toast.LENGTH_SHORT).show();
                                                init();
                                            }else{
                                                Toast.makeText(showPictures.this,"删除失败",Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //ToDo: 你想做的事情
                                            Toast.makeText(showPictures.this, "取消删除", Toast.LENGTH_SHORT).show();
                                            dialogInterface.dismiss();
                                        }
                                    });
                            builder.create().show();
                        }
                        else
                        {
                            Intent intent=new Intent(showPictures.this,originalPicture.class);
                            intent.putExtra("id",ids.get(new Long(l).intValue()));
                            //启动
                            startActivity(intent);
                        }
                    }
                });
                builder1.show();

//                System.out.println(view.findViewById(i));

            }
        });//click
    }




}
