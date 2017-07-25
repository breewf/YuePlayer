package com.ghy.yueplayer.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.ghy.yueplayer.bean.MusicInfo;

import java.util.ArrayList;
import java.util.List;

public class MediaUtil {

    public static List<MusicInfo> getMusicInfoList(Context c) {

        List<MusicInfo> list = new ArrayList<>();
        ContentResolver cr = c.getContentResolver();
        Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {

                //歌曲ID：MediaStore.Audio.Media._ID
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                //歌曲的名称：MediaStore.Audio.Media.TITLE
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                //歌曲的专辑名：MediaStore.Audio.Media.ALBUM
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                //专辑ID：MediaStore.Audio.Media.ALBUM_ID
                int albumId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                //歌曲的歌手名：MediaStore.Audio.Media.ARTIST
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                //歌曲文件的路径：MediaStore.Audio.Media.DATA
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

                //歌曲的时长：MediaStore.Audio.Media.DURATION
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));


                if (url.endsWith(".mp3") || url.endsWith(".MP3")
                        || url.endsWith(".ape") || url.endsWith(".APE")) {
                    //过滤时长小于60秒的歌曲
                    if (duration >= 60000) {
                        MusicInfo musicInfo = new MusicInfo();
                        musicInfo.setTitle(title);
                        musicInfo.setArtist(artist);
                        musicInfo.setId(id);
                        musicInfo.setUrl(url);
                        musicInfo.setAlbumId(albumId);
                        list.add(musicInfo);
                    }
                }
            }
        }
        try {
            if (cursor != null) cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
        return list;

    }

}