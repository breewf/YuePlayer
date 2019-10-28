package com.ghy.yueplayer.activity;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.adapter.OnLineMusicListAdapter;
import com.ghy.yueplayer.api.APIS;
import com.ghy.yueplayer.base.BaseActivity;
import com.ghy.yueplayer.bean.OnLineListInfo;
import com.ghy.yueplayer.constant.Global;
import com.ghy.yueplayer.network.RetrofitManager;
import com.ghy.yueplayer.network.observer.EntityObserverNoBase;
import com.ghy.yueplayer.utils.ViewUtils;
import com.github.ybq.android.spinkit.SpinKitView;

import butterknife.Bind;


public class AllTopListDetailActivity extends BaseActivity {

    @Bind(R.id.iv_back)
    ImageView mIvBack;

    private String type;//分类
    private String name;//名称

    private OnLineMusicListAdapter mOnLineMusicListAdapter;

    @Bind(R.id.spin_kit)
    SpinKitView mSpinKitView;
    @Bind(R.id.lv_music)
    ListView mLvMusic;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_all_top_list_detail;
    }

    @Override
    protected void initView() {
        type = getIntent().getStringExtra("type");
        name = getIntent().getStringExtra("name");
        backWithTitle(TextUtils.isEmpty(name) ? "" : name);

        initDarkModeIcon(Global.DAY_MODE);

        mSpinKitView.setColor(ContextCompat.getColor(this,
                Global.DAY_MODE ? R.color.dn_title_5_night : R.color.dn_title_5));
        setListDivider(Global.DAY_MODE);
    }

    private void initDarkModeIcon(boolean isDayMode) {
        if (isDayMode) {
            mIvBack.setImageDrawable(ViewUtils.getTintDrawable(this,
                    R.mipmap.icon_back, R.color.dn_page_title));
        } else {
            mIvBack.setImageDrawable(ViewUtils.getTintDrawable(this,
                    R.mipmap.icon_back, R.color.dn_page_title_night));
        }
    }

    private void setListDivider(boolean isDayMode) {
        if (mLvMusic == null) {
            return;
        }
        if (isDayMode) {
            mLvMusic.setDivider(new ColorDrawable(ContextCompat.getColor(this, R.color.gray1)));
            mLvMusic.setDividerHeight(1);
        } else {
            mLvMusic.setDivider(new ColorDrawable(ContextCompat.getColor(this, R.color.gray8)));
            mLvMusic.setDividerHeight(1);
        }
    }

    @Override
    protected void initData() {
        requestListMusic(TextUtils.isEmpty(type) ? "2" : type);
    }

    private void requestListMusic(String type) {
        String url = APIS.BASE_URL_BAI_DU_MUSIC;
        requestParams.put("from", "android");
        requestParams.put("version", "6.0.0.3");
        requestParams.put("method", APIS.BAI_DU_METHOD_LIST);
        requestParams.put("format", "json");
        requestParams.put("type", type);
        requestParams.put("offset", "0");
        requestParams.put("size", "100");
        getAPiService().getOnLineListInfo(url, requestParams)
                .compose(RetrofitManager.schedulersTransformer())
                .compose(this.bindToLifecycle())
                .subscribe(new EntityObserverNoBase<OnLineListInfo>(AllTopListDetailActivity.this, null) {
                    @Override
                    public void requestCallback(OnLineListInfo onLineListInfo, String msg, int code, boolean success) {
                        if (success) {
                            Log.i("onLineMusic", "获取榜单成功-->>");
                            mSpinKitView.setVisibility(View.GONE);
                            if (onLineListInfo == null || onLineListInfo.getSong_list() == null ||
                                    onLineListInfo.getSong_list().size() == 0) {
                                Toast.makeText(AllTopListDetailActivity.this, "获取数据为空", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            mOnLineMusicListAdapter = new OnLineMusicListAdapter(AllTopListDetailActivity.this, onLineListInfo);
                            mLvMusic.setAdapter(mOnLineMusicListAdapter);
                            mLvMusic.setOnItemClickListener((adapterView, view, i, l) -> {
                                //播放
                                String musicName = onLineListInfo.getSong_list().get(i).getTitle();
                                String artist = onLineListInfo.getSong_list().get(i).getAuthor();
                                String imgUrl = onLineListInfo.getSong_list().get(i).getPic_big();
                                String songId = onLineListInfo.getSong_list().get(i).getSong_id();
                                String musicPath = APIS.BASE_URL_BAI_DU_MUSIC + "?method="
                                        + APIS.BAI_DU_METHOD_PLAY + "&songid=" + songId;

                                Toast.makeText(AllTopListDetailActivity.this, R.string.for_fun, Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            Log.i("onLineMusic", "获取榜单出错-->>" + msg);
                            mSpinKitView.setVisibility(View.GONE);
                            Toast.makeText(AllTopListDetailActivity.this, "请求出错", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
