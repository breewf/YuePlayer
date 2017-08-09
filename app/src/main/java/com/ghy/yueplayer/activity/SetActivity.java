package com.ghy.yueplayer.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.common.PreferManager;
import com.ghy.yueplayer.global.Constant;
import com.ghy.yueplayer.util.SPUtil;

public class SetActivity extends ActionBarActivity {

    ImageView app_icon_back;
    TextView tv_activity_name;

    RadioGroup set_group;
    RadioButton radioButton1;
    RadioButton radioButton2;
    RadioButton radioButton3;
    RadioButton radioButton4;
    RadioButton radioButtonAlbumColor;
    RadioButton radioButtonMusicNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        initToolBar();
        initView();
    }

    private void initToolBar() {
        app_icon_back = (ImageView) findViewById(R.id.app_icon_back);
        tv_activity_name = (TextView) findViewById(R.id.tv_activity_name);
        tv_activity_name.setText("Set");
        app_icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {

        set_group = (RadioGroup) findViewById(R.id.set_group);
        radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton) findViewById(R.id.radioButton3);
        radioButton4 = (RadioButton) findViewById(R.id.radioButton4);
        radioButtonAlbumColor = (RadioButton) findViewById(R.id.radioButton_color_test);
        radioButtonMusicNote = (RadioButton) findViewById(R.id.radioButton_note_test);

        //获取保存的播放模式
        int playMode = SPUtil.getIntSP(this, Constant.MUSIC_SP, "playMode");
        switch (playMode) {
            case -1:
                //未设置过，默认列表循环
                radioButton1.setChecked(true);
                break;
            case 1:
                //列表循环
                radioButton1.setChecked(true);
                break;
            case 2:
                //随机播放
                radioButton2.setChecked(true);
                break;
            case 3:
                //单曲循环
                radioButton3.setChecked(true);
                break;
        }

        set_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int radioButtonId = radioGroup.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(radioButtonId);
                Toast.makeText(SetActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();

                int playMode = -1;
                if (rb.getText().equals("列表循环")) {
                    playMode = 1;
                } else if (rb.getText().equals("随机播放")) {
                    playMode = 2;
                } else if (rb.getText().equals("单曲循环")) {
                    playMode = 3;
                }
                //保存设置项
                SPUtil.saveSP(SetActivity.this, Constant.MUSIC_SP, "playMode", playMode);
            }
        });

        //获取是否自动下载歌词
        boolean isAutoLyric = SPUtil.getLyricBooleanSP(this, Constant.MUSIC_SP, "autoSearchLyric");
        if (isAutoLyric) {
            radioButton4.setChecked(true);
        } else {
            radioButton4.setChecked(false);
        }

        radioButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isCheck = SPUtil.getLyricBooleanSP(SetActivity.this, Constant.MUSIC_SP, "autoSearchLyric");
                if (isCheck) {
                    radioButton4.setChecked(false);
                    SPUtil.saveSP(SetActivity.this, Constant.MUSIC_SP, "autoSearchLyric", false);
                } else {
                    radioButton4.setChecked(true);
                    SPUtil.saveSP(SetActivity.this, Constant.MUSIC_SP, "autoSearchLyric", true);
                }
            }
        });

        boolean isOpenAlbumColor = PreferManager.getBoolean(PreferManager.ALBUM_COLOR, false);
        if (isOpenAlbumColor) {
            radioButtonAlbumColor.setChecked(true);
        } else {
            radioButtonAlbumColor.setChecked(false);
        }
        radioButtonAlbumColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isCheck = PreferManager.getBoolean(PreferManager.ALBUM_COLOR, false);
                if (isCheck) {
                    radioButtonAlbumColor.setChecked(false);
                    PreferManager.setBoolean(PreferManager.ALBUM_COLOR, false);
                } else {
                    radioButtonAlbumColor.setChecked(true);
                    PreferManager.setBoolean(PreferManager.ALBUM_COLOR, true);
                }
            }
        });

        boolean isOpenMusicNote = PreferManager.getBoolean(PreferManager.MUSIC_NOTE, false);
        if (isOpenMusicNote) {
            radioButtonMusicNote.setChecked(true);
        } else {
            radioButtonMusicNote.setChecked(false);
        }
        radioButtonMusicNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isCheck = PreferManager.getBoolean(PreferManager.MUSIC_NOTE, false);
                if (isCheck) {
                    radioButtonMusicNote.setChecked(false);
                    PreferManager.setBoolean(PreferManager.MUSIC_NOTE, false);
                } else {
                    radioButtonMusicNote.setChecked(true);
                    PreferManager.setBoolean(PreferManager.MUSIC_NOTE, true);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_set, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
