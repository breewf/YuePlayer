package com.ghy.yueplayer.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.api.APIS;
import com.ghy.yueplayer.base.BaseFragment;
import com.ghy.yueplayer.bean.OnLineListInfo;
import com.ghy.yueplayer.network.HttpListener;
import com.ghy.yueplayer.network.JavaBeanRequest;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * 榜单页面
 */
public class TopListFragment extends BaseFragment {

    @Bind(R.id.tv_see_all)
    TextView mTvSeeAll;
    @Bind(R.id.lv_hot_music)
    ListView mLvHotMusic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_top_list;
    }

    @Override
    protected void initView() {

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
        Request<OnLineListInfo> request = new JavaBeanRequest<>(url, RequestMethod.GET, OnLineListInfo.class);
        requestParams.put("from", "android");
        requestParams.put("version", "6.0.0.3");
        requestParams.put("method", APIS.BAI_DU_METHOD_LIST);
        requestParams.put("format", "json");
        requestParams.put("type", "2");
        requestParams.put("offset", "0");
        requestParams.put("size", "100");
        requestAPI(request, getTopListListener, null);
    }

    /**
     * 获取歌词回调
     */
    private HttpListener<OnLineListInfo> getTopListListener = new HttpListener<OnLineListInfo>() {

        @Override
        public void onSucceed(OnLineListInfo onLineListInfo) {
            Log.i("onLineMusic", "获取榜单成功-->>");
        }

        @Override
        public void onFailed(int errorCode, String msg) {
            Log.i("onLineMusic", "获取榜单出错-->>" + msg);
        }
    };

    @OnClick({R.id.tv_see_all})
    public void topListClick(View view) {
        switch (view.getId()) {
            case R.id.tv_see_all:
                Toast.makeText(getActivity(), "查看更多", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
