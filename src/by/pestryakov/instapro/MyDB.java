package by.pestryakov.instapro;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDB extends SQLiteOpenHelper {

	static MyDB myDB;
	static SQLiteDatabase db;

	public MyDB(Context context) {
		super(context, "MyDB", null, 1);
	}

	static void init(Context context) {
		db = new MyDB(context).getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table accounts (user_id text primary key, token text, username text, full_name text)");
		db.execSQL("create table images (image_id text primary key, bitmap text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}