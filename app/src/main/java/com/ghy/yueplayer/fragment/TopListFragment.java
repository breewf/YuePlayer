package com.ghy.yueplayer.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.adapter.OnLineMusicListAdapter;
import com.ghy.yueplayer.api.APIS;
import com.ghy.yueplayer.base.BaseFragment;
import com.ghy.yueplayer.bean.OnLineListInfo;
import com.ghy.yueplayer.constant.Global;
import com.ghy.yueplayer.network.RetrofitManager;
import com.ghy.yueplayer.network.observer.EntityObserverNoBase;
import com.github.ybq.android.spinkit.SpinKitView;

import butterknife.Bind;

/**
 * 榜单页面
 */
public class TopListFragment extends BaseFragment {

    @Bind(R.id.spin_kit)
    SpinKitView mSpinKitView;
    @Bind(R.id.lv_hot_music)
    ListView mLvHotMusic;

    private OnLineMusicListAdapter mOnLineMusicListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_top_list;
    }

    @Override
    protected void initView() {
        mSpinKitView.setColor(ContextCompat.getColor(getContext(),
                Global.DAY_MODE ? R.color.dn_title_5_night : R.color.dn_title_5));
        setListDivider(Global.DAY_MODE);
    }

    private void setListDivider(boolean isDayMode) {
        if (mLvHotMusic == null || getActivity() == null) {
            return;
        }
        if (isDayMode) {
            mLvHotMusic.setDivider(new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.gray1)));
            mLvHotMusic.setDividerHeight(1);
        } else {
            mLvHotMusic.setDivider(new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.gray8)));
            mLvHotMusic.setDividerHeight(1);
        }
    }

    @Override
    protected void initData() {
        requestListMusic();
    }

    /**
     * 请求热歌榜单信息
     * http://tingapi.ting.baidu.com/v1/restserver/ting?
     * from=android&version=6.0.0.3&method=baidu.ting.billboard.billList&format=json&type=2&offset=0&size=50
     */
    private void requestListMusic() {
        String url = APIS.BASE_URL_BAI_DU_MUSIC;
        requestParams.put("from", "android");
        requestParams.put("version", "6.0.0.3");
        requestParams.put("method", APIS.BAI_DU_METHOD_LIST);
        requestParams.put("format", "json");
        requestParams.put("type", "2");
        requestParams.put("offset", "0");
        requestParams.put("size", "100");
        getAPiService().getOnLineListInfo(url, requestParams)
                .compose(RetrofitManager.schedulersTransformer())
                .compose(this.bindToLifecycle())
                .subscribe(new EntityObserverNoBase<OnLineListInfo>(getActivity(), null) {
                    @Override
                    public void requestCallback(OnLineListInfo onLineListInfo, String msg, int code, boolean success) {
                        if (success) {

                            Log.i("onLineMusic", "获取榜单成功-->>");
                            mSpinKitView.setVisibility(View.GONE);
                            if (onLineListInfo == null || onLineListInfo.getSong_list() == null ||
                                    onLineListInfo.getSong_list().size() == 0) {
                                Toast.makeText(getActivity(), "获取数据为空", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            mOnLineMusicListAdapter = new OnLineMusicListAdapter(getActivity(), onLineListInfo);
                            mLvHotMusic.setAdapter(mOnLineMusicListAdapter);
                            mLvHotMusic.setOnItemClickListener((adapterView, view, i, l) -> {
                                //播放
                                String musicName = onLineListInfo.getSong_list().get(i).getTitle();
                                String artist = onLineListInfo.getSong_list().get(i).getAuthor();
                                String imgUrl = onLineListInfo.getSong_list().get(i).getPic_big();
                                String songId = onLineListInfo.getSong_list().get(i).getSong_id();
                                String musicPath = APIS.BASE_URL_BAI_DU_MUSIC + "?method="
                                        + APIS.BAI_DU_METHOD_PLAY + "&songid=" + songId;

                                Toast.makeText(getActivity(), R.string.for_fun, Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            Log.i("onLineMusic", "获取榜单出错-->>" + msg);
                            mSpinKitView.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "请求出错", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
