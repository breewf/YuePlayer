package com.ghy.yueplayer.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.adapter.OnLineMusicListAdapter;
import com.ghy.yueplayer.api.APIS;
import com.ghy.yueplayer.base.BaseActivity;
import com.ghy.yueplayer.bean.OnLineListInfo;
import com.ghy.yueplayer.network.HttpListener;
import com.ghy.yueplayer.network.JavaBeanRequest;
import com.github.ybq.android.spinkit.SpinKitView;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;

import butterknife.Bind;


public class AllTopListDetailActivity extends BaseActivity {

    private String type;//分类
    private String name;//名称

    private OnLineMusicListAdapter mOnLineMusicListAdapter;

    @Bind(R.id.spin_kit)
    SpinKitView mSpinKitView;
    @Bind(R.id.lv_music)
    ListView mLvMusic;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_all_top_list_detail;
    }

    @Override
    protected void initView() {
        type = getIntent().getStringExtra("type");
        name = getIntent().getStringExtra("name");
        backWithTitle(TextUtils.isEmpty(name) ? "" : name);
    }

    @Override
    protected void initData() {
        requestListMusic(TextUtils.isEmpty(type) ? "2" : type);
    }

    private void requestListMusic(String type) {
        String url = APIS.BASE_URL_BAI_DU_MUSIC;
        Request<OnLineListInfo> request = new JavaBeanRequest<>(url, RequestMethod.GET, OnLineListInfo.class);
        requestParams.put("from", "android");
        requestParams.put("version", "6.0.0.3");
        requestParams.put("method", APIS.BAI_DU_METHOD_LIST);
        requestParams.put("format", "json");
        requestParams.put("type", type);
        requestParams.put("offset", "0");
        requestParams.put("size", "100");
        requestAPI(request, getTopListListener, null);
    }

    /**
     * 获取歌曲列表回调
     */
    private HttpListener<OnLineListInfo> getTopListListener = new HttpListener<OnLineListInfo>() {

        @Override
        public void onSucceed(final OnLineListInfo onLineListInfo) {
            Log.i("onLineMusic", "获取榜单成功-->>");
            mSpinKitView.setVisibility(View.GONE);
            if (onLineListInfo == null || onLineListInfo.getSong_list() == null ||
                    onLineListInfo.getSong_list().size() == 0) {
                Toast.makeText(AllTopListDetailActivity.this, "获取数据为空", Toast.LENGTH_SHORT).show();
                return;
            }
            mOnLineMusicListAdapter = new OnLineMusicListAdapter(AllTopListDetailActivity.this, onLineListInfo);
            mLvMusic.setAdapter(mOnLineMusicListAdapter);
            mLvMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //播放
                    String musicName = onLineListInfo.getSong_list().get(i).getTitle();
                    String artist = onLineListInfo.getSong_list().get(i).getAuthor();
                    String imgUrl = onLineListInfo.getSong_list().get(i).getPic_big();
                    String songId = onLineListInfo.getSong_list().get(i).getSong_id();

                    String musicPath = APIS.BASE_URL_BAI_DU_MUSIC + "?method="
                            + APIS.BAI_DU_METHOD_PLAY + "&songid=" + songId;

                }
            });
        }

        @Override
        public void onFailed(int errorCode, String msg) {
            Log.i("onLineMusic", "获取榜单出错-->>" + msg);
            mSpinKitView.setVisibility(View.GONE);
            Toast.makeText(AllTopListDetailActivity.this, "请求出错", Toast.LENGTH_SHORT).show();
        }
    };

}
