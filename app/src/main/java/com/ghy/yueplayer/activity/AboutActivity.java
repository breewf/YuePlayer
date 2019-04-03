package com.ghy.yueplayer.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.util.AppUtils;

public class AboutActivity extends AppCompatActivity {


    ImageView app_icon_back;
    TextView tv_activity_name;
    TextView tv_version;
    RelativeLayout layout_yue;
    boolean isAnim = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initToolBar();
        initView();

    }

    private void initToolBar() {
        app_icon_back = findViewById(R.id.app_icon_back);
        tv_activity_name = findViewById(R.id.tv_activity_name);
        tv_version = findViewById(R.id.tv_version);
        tv_activity_name.setText("About");
        app_icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv_version.setText("VERSION : V" + AppUtils.getVersionName(this));
    }

    private void initView() {

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
