package com.ghy.yueplayer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	// 数据库文件的文件名
	private static final String DB_NAME = "likeMusicList.db";
	// 数据库的版本号
	private static final int VERSION = 1;

	public DBHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		//喜欢音乐列表 包括音乐名，艺术家，专辑封面uri，音乐路径
		db.execSQL("CREATE TABLE IF NOT EXISTS like_music_list (_id integer PRIMARY KEY AUTOINCREMENT," +
				"musicName char, musicArtist char, musicAlbumId integer,musicUrl char)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
