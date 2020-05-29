package com.example.pictures;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;

public class originalPicture extends AppCompatActivity {

    private ImageView imageView;
    private MyDatabaseHelper helper;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_original_picture);
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle("表情包原图");
        actionBar.setDisplayHomeAsUpEnabled(true);

        imageView=findViewById(R.id.image);
        helper=new MyDatabaseHelper(this,"pictures.db",null,1);
        helper.getWritableDatabase();
        db=helper.getWritableDatabase();
        Intent intent = getIntent();
        int id=intent.getIntExtra("id",1);
        Cursor cursor = db.query("PICTURES", new String[]{"path"}, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();
        String path=cursor.getString(cursor.getColumnIndex("path"));
        imageView.setImageURI(Uri.fromFile(new File(path)));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
