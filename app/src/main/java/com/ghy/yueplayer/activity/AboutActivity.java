package com.ghy.yueplayer.activity;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.base.BaseActivity;
import com.ghy.yueplayer.constant.Global;
import com.ghy.yueplayer.utils.AppUtils;
import com.ghy.yueplayer.utils.ViewUtils;

public class AboutActivity extends BaseActivity {

    ImageView app_icon_back;
    ImageView iv_logo;
    TextView tv_activity_name;
    TextView tv_version;
    RelativeLayout layout_yue;
    boolean isAnim = false;

    private void initToolBar() {
        app_icon_back = findViewById(R.id.app_icon_back);
        iv_logo = findViewById(R.id.iv_logo);
        tv_activity_name = findViewById(R.id.tv_activity_name);
        tv_version = findViewById(R.id.tv_version);
        tv_activity_name.setText("About");
        app_icon_back.setOnClickListener(view -> finish());

        tv_version.setText("VERSION : V" + AppUtils.getVersionName(this));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initView() {

        initToolBar();

        layout_yue = (RelativeLayout) findViewById(R.id.layout_yue);

        layout_yue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAnim) {
                    isAnim = true;
                    layout_yue.startAnimation(AnimationUtils.loadAnimation(
                            AboutActivity.this, R.anim.yue_hide_scale_rotate_from_center
                    ));

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            layout_yue.startAnimation(AnimationUtils.loadAnimation(
                                    AboutActivity.this, R.anim.view_show_translate_scale_from_top
                            ));

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    isAnim = false;
                                }
                            }, 1000);
                        }
                    }, 2000);
                } else {
                    //do nothing...
                }
            }
        });

        initDarkModeIcon(Global.DAY_MODE);
    }

    private void initDarkModeIcon(boolean isDayMode) {
        if (isDayMode) {
            iv_logo.setImageDrawable(ViewUtils.getTintDrawable(this,
                    R.mipmap.icon_app_white_big, R.color.dn_page_title));
        } else {
            iv_logo.setImageDrawable(ViewUtils.getTintDrawable(this,
                    R.mipmap.icon_app_white_big, R.color.dn_page_title_night));
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_about, menu);
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
