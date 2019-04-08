package com.ghy.yueplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ghy.yueplayer.MainActivity;
import com.ghy.yueplayer.R;
import com.ghy.yueplayer.common.PreferManager;
import com.ghy.yueplayer.constant.Global;
import com.ghy.yueplayer.global.Constant;
import com.ghy.yueplayer.util.SPUtil;

public class SetActivity extends Activity {

    ImageView app_icon_back;
    TextView tv_activity_name;
    TextView tv_fx;

    RadioGroup set_group;
    RadioGroup anim_group;
    RadioGroup circle_group;
    RadioButton radioButton1;
    RadioButton radioButton2;
    RadioButton radioButton3;
    RadioButton anim1;
    RadioButton anim2;
    RadioButton anim3;
    RadioButton c_anim1;
    RadioButton c_anim2;
    RadioButton c_anim3;
    RadioButton rb_fx;
    RadioButton radioButtonAlbumColor;
    RadioButton radioButtonMusicNote;
    RadioButton radioButtonListAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        initToolBar();
        initView();
    }

    private void initToolBar() {
        app_icon_back = findViewById(R.id.app_icon_back);
        tv_activity_name = findViewById(R.id.tv_activity_name);
        tv_activity_name.setText("Set");
        app_icon_back.setOnClickListener(view -> finish());
    }

    private void initView() {

        tv_fx = findViewById(R.id.tv_fx);
        set_group = findViewById(R.id.set_group);
        anim_group = findViewById(R.id.anim_group);
        circle_group = findViewById(R.id.circle_group);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);
        anim1 = findViewById(R.id.anim1);
        anim2 = findViewById(R.id.anim2);
        anim3 = findViewById(R.id.anim3);
        c_anim1 = findViewById(R.id.c_anim1);
        c_anim2 = findViewById(R.id.c_anim2);
        c_anim3 = findViewById(R.id.c_anim3);
        rb_fx = findViewById(R.id.rb_fx);
        radioButtonAlbumColor = findViewById(R.id.radioButton_color_test);
        radioButtonMusicNote = findViewById(R.id.radioButton_note_test);
        radioButtonListAnim = findViewById(R.id.radioButton_open_list_anim);

        tv_fx.setOnClickListener(view -> startActivity(new Intent(this, MusicFxActivity.class)));
        rb_fx.setOnClickListener(view -> startActivity(new Intent(this, MusicFxActivity.class)));

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
            default:
                break;
        }

        int animMode = PreferManager.getInt(PreferManager.MAIN_BOTTOM_ANIM, -1);
        switch (animMode) {
            case -1:
            case 1:
                //未设置过，默认
                anim1.setChecked(true);
                break;
            case 2:
                anim2.setChecked(true);
                break;
            case 3:
                anim3.setChecked(true);
                break;
            default:
                break;
        }

        int animCMode = PreferManager.getInt(PreferManager.MAIN_CIRCLE_ANIM, -1);
        switch (animCMode) {
            case -1:
            case 1:
                //未设置过，默认
                c_anim1.setChecked(true);
                break;
            case 2:
                c_anim2.setChecked(true);
                break;
            case 3:
                c_anim3.setChecked(true);
                break;
            default:
                break;
        }

        set_group.setOnCheckedChangeListener((radioGroup, i) -> {
            int radioButtonId = radioGroup.getCheckedRadioButtonId();
            RadioButton rb = findViewById(radioButtonId);
            Toast.makeText(SetActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
            int playMode1 = -1;
            if (rb.getText().equals("列表循环")) {
                playMode1 = 1;
            } else if (rb.getText().equals("随机播放")) {
                playMode1 = 2;
            } else if (rb.getText().equals("单曲循环")) {
                playMode1 = 3;
            }
            //保存设置项
            SPUtil.saveSP(SetActivity.this, Constant.MUSIC_SP, "playMode", playMode1);
        });

        anim_group.setOnCheckedChangeListener((radioGroup, i) -> {
            int radioButtonId = radioGroup.getCheckedRadioButtonId();
            RadioButton rb = findViewById(radioButtonId);
            Toast.makeText(SetActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
            int animModeSet = -1;
            switch (radioButtonId) {
                case R.id.anim1:
                    animModeSet = 1;
                    break;
                case R.id.anim2:
                    animModeSet = 2;
                    setAnimModeChange();
                    break;
                case R.id.anim3:
                    animModeSet = 3;
                    setAnimModeChange();
                    break;
                default:
                    break;

            }
            PreferManager.setInt(PreferManager.MAIN_LAST_ANIM, PreferManager.getInt(PreferManager.MAIN_BOTTOM_ANIM, -1));
            PreferManager.setInt(PreferManager.MAIN_BOTTOM_ANIM, animModeSet);
            Global.setYueAnimType(animModeSet);
        });

        circle_group.setOnCheckedChangeListener((radioGroup, i) -> {
            int radioButtonId = radioGroup.getCheckedRadioButtonId();
            RadioButton rb = findViewById(radioButtonId);
//            Toast.makeText(SetActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
            Toast.makeText(SetActivity.this, "敬请期待哦~", Toast.LENGTH_SHORT).show();
            int animModeSet = -1;
            switch (radioButtonId) {
                case R.id.c_anim1:
                    animModeSet = 1;
                    break;
                case R.id.c_anim2:
                    animModeSet = 2;
                    break;
                case R.id.c_anim3:
                    animModeSet = 3;
                    break;
                default:
                    break;

            }
            PreferManager.setInt(PreferManager.MAIN_CIRCLE_ANIM, animModeSet);
            Global.setCircleAnimType(animModeSet);
        });

        boolean isOpenAlbumColor = PreferManager.getBoolean(PreferManager.ALBUM_COLOR, false);
        if (isOpenAlbumColor) {
            radioButtonAlbumColor.setChecked(true);
        } else {
            radioButtonAlbumColor.setChecked(false);
        }
        radioButtonAlbumColor.setOnClickListener(view -> {
            boolean isCheck = PreferManager.getBoolean(PreferManager.ALBUM_COLOR, false);
            if (isCheck) {
                radioButtonAlbumColor.setChecked(false);
                PreferManager.setBoolean(PreferManager.ALBUM_COLOR, false);
            } else {
                radioButtonAlbumColor.setChecked(true);
                PreferManager.setBoolean(PreferManager.ALBUM_COLOR, true);
            }
        });

        boolean isOpenMusicNote = PreferManager.getBoolean(PreferManager.MUSIC_NOTE, false);
        if (isOpenMusicNote) {
            radioButtonMusicNote.setChecked(true);
        } else {
            radioButtonMusicNote.setChecked(false);
        }
        radioButtonMusicNote.setOnClickListener(view -> {
            boolean isCheck = PreferManager.getBoolean(PreferManager.MUSIC_NOTE, false);
            if (isCheck) {
                radioButtonMusicNote.setChecked(false);
                PreferManager.setBoolean(PreferManager.MUSIC_NOTE, false);
            } else {
                radioButtonMusicNote.setChecked(true);
                PreferManager.setBoolean(PreferManager.MUSIC_NOTE, true);
            }
        });

        boolean isOpenListAnim = PreferManager.getBoolean(PreferManager.LIST_ANIM, false);
        if (isOpenListAnim) {
            radioButtonListAnim.setChecked(true);
        } else {
            radioButtonListAnim.setChecked(false);
        }
        radioButtonListAnim.setOnClickListener(view -> {
            boolean isCheck = PreferManager.getBoolean(PreferManager.LIST_ANIM, false);
            if (isCheck) {
                radioButtonListAnim.setChecked(false);
                PreferManager.setBoolean(PreferManager.LIST_ANIM, false);
            } else {
                radioButtonListAnim.setChecked(true);
                PreferManager.setBoolean(PreferManager.LIST_ANIM, true);
            }
        });
    }

    private void setAnimModeChange() {
        if (MainActivity.MA != null) {
            MainActivity.MA.setYueAnimLayoutGone();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
