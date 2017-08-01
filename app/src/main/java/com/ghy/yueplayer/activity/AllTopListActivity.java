package com.ghy.yueplayer.activity;

import android.content.Intent;
import android.view.View;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.base.BaseActivity;
import com.ghy.yueplayer.util.AnimUtils;

import butterknife.OnClick;

public class AllTopListActivity extends BaseActivity {

    @Override
    protected int getLayoutID() {
        return R.layout.activity_all_top_list;
    }

    @Override
    protected void initView() {
        backWithTitle("全部榜单");
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.tv_list_new, R.id.tv_list_hot, R.id.tv_list_pop, R.id.tv_list_us,
            R.id.tv_list_old, R.id.tv_list_love, R.id.tv_list_movie, R.id.tv_list_net})
    public void allListClick(View view) {
        switch (view.getId()) {
            case R.id.tv_list_new:
                startActivity("新歌榜", "1");
                break;
            case R.id.tv_list_hot:
                startActivity("热歌榜", "2");
                break;
            case R.id.tv_list_pop:
                startActivity("流行榜", "16");
                break;
            case R.id.tv_list_us:
                startActivity("欧美金曲", "21");
                break;
            case R.id.tv_list_old:
                startActivity("经典老歌", "22");
                break;
            case R.id.tv_list_love:
                startActivity("情歌对唱", "23");
                break;
            case R.id.tv_list_movie:
                startActivity("影视金曲", "24");
                break;
            case R.id.tv_list_net:
                startActivity("网络歌曲", "25");
                break;
        }
    }

    private void startActivity(String name, String type) {
        Intent intent = new Intent(AllTopListActivity.this, AllTopListDetailActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("type", type);
        AnimUtils.toLeftAnim(AllTopListActivity.this, intent);
    }

}
