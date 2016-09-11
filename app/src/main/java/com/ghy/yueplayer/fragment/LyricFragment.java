package com.ghy.yueplayer.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ghy.yueplayer.R;
import com.ghy.yueplayer.bean.LyricInfo;
import com.ghy.yueplayer.bean.LyricResult;
import com.ghy.yueplayer.global.Constant;
import com.ghy.yueplayer.lrc.DefaultLrcParser;
import com.ghy.yueplayer.lrc.LrcRow;
import com.ghy.yueplayer.lrc.LrcView;
import com.ghy.yueplayer.service.MusicPlayService;
import com.ghy.yueplayer.util.FileUtil;
import com.ghy.yueplayer.util.GetJsonUtil;
import com.ghy.yueplayer.util.SPUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LyricFragment extends Fragment implements GetJsonUtil.JsonCallBack{

    public static LyricFragment LYFInstance;

    private LrcView lrcView;
    private SeekBar seek_bar;
    private TextView tv_search_lyric;
    private TextView tv_view_yue;

    String lyricPath;
    String musicName;
    String artist;

    private PowerManager.WakeLock wakeLock;
    private boolean isVisibility=false;

    public LyricFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lyric, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LYFInstance=this;
        //创建歌词文件夹
        lyricPath = FileUtil.createFilePath("Lyric");

        initView();
        startInitLyric();

    }

    private void initView() {
        lrcView= (LrcView) getActivity().findViewById(R.id.lrcView);
        seek_bar= (SeekBar) getActivity().findViewById(R.id.seek_bar);
        tv_search_lyric= (TextView) getActivity().findViewById(R.id.tv_search_lyric);
        tv_view_yue= (TextView) getActivity().findViewById(R.id.tv_view_yue);
    }

    /*
    * 播放歌曲时准备加载歌词
    * 1.先搜索本地是否存在歌词文件
    * 2.有则加载本地歌词文件，没有则联网搜索保存到本地
    * */
    public void startInitLyric(){
        //加载歌词前先重置
        lrcView.reset();
        if (seek_bar.getVisibility()!=View.GONE){
            seek_bar.setVisibility(View.GONE);
        }
        if (tv_view_yue.getVisibility()!=View.GONE){
            tv_view_yue.setVisibility(View.GONE);
        }
        if (tv_search_lyric.getVisibility()!=View.GONE){
            tv_search_lyric.setVisibility(View.GONE);
        }

        //获取正在播放的歌曲信息
        musicName= SPUtil.getStringSP(getActivity(), Constant.MUSIC_SP, "musicName");
        artist= SPUtil.getStringSP(getActivity(), Constant.MUSIC_SP,"musicArtist");

        //搜索本地歌词文件
        if (lyricPath!=null){
            //歌词的命名格式为：歌手名-歌曲名.lrc
            String lyricName = artist+"-"+musicName+".lrc";
            File musicFile=new File(lyricPath+lyricName);
            if (musicFile.exists()){
                //本地歌词存在，加载本地歌词
                initLyric(musicFile);

            }else {
                //本地歌词不存在，联网搜索
                //是否开启了自动搜索歌词
                boolean isSearch=SPUtil.getLyricBooleanSP(getActivity(), Constant.MUSIC_SP, "autoSearchLyric");
                if (isSearch){
                    if (tv_view_yue.getVisibility()==View.GONE){
                        tv_view_yue.setVisibility(View.VISIBLE);
                    }
                    if (tv_search_lyric.getVisibility()==View.GONE){
                        tv_search_lyric.setVisibility(View.VISIBLE);
                        tv_search_lyric.setText("搜索歌词中...");
                    }
                    //传入歌词搜索接口搜索歌词
                    String lyricUrl="http://geci.me/api/lyric/"+musicName+"/"+artist;
                    GetJsonUtil.getJsonFromWeb(lyricUrl.toString(),this);
                }else {
                    if (tv_view_yue.getVisibility()==View.GONE){
                        tv_view_yue.setVisibility(View.VISIBLE);
                    }
                    if (tv_search_lyric.getVisibility()==View.GONE){
                        tv_search_lyric.setVisibility(View.VISIBLE);
                        tv_search_lyric.setText("未开启歌词搜索");
                    }
                }
            }
        }else {
            //如果SD卡不可用，即没有保存路径
            if (tv_view_yue.getVisibility()==View.GONE){
                tv_view_yue.setVisibility(View.VISIBLE);
            }
            if (tv_search_lyric.getVisibility()==View.GONE){
                tv_search_lyric.setVisibility(View.VISIBLE);
                tv_search_lyric.setText("SD卡不可用");
            }
        }

        //歌词加载完毕去判断界面是否常亮
        judgeFragmentVisibilityAndWeakLock();
    }

    @Override
    public void getJson(String getJson) {
        if (getJson!=null){
            //解析json数据
            parserJson(getJson,musicName,artist);
        }else {
            if (tv_search_lyric.getVisibility()==View.GONE){
                tv_search_lyric.setVisibility(View.VISIBLE);
                tv_search_lyric.setText("获取数据失败");
            }else {
                tv_search_lyric.setText("获取数据失败");
            }
        }
    }

    List<LyricInfo> lyricInfoList;
    /*
    * 解析json数据
    * */
    private void parserJson(String s,String musicName,String artist) {
        Gson gson=new Gson();
        LyricResult lyricResult=gson.fromJson(s,LyricResult.class);
        String count=lyricResult.getCount();//歌词数量
        if (Integer.parseInt(count)==0){
            //无歌词
            if (tv_search_lyric.getVisibility()==View.GONE){
                tv_search_lyric.setVisibility(View.VISIBLE);
                tv_search_lyric.setText("未搜索到歌词");
            }else {
                tv_search_lyric.setText("未搜索到歌词");
            }
        }else {
            lyricInfoList=lyricResult.getResult();
            //使用搜索到的第一个歌词
            LyricInfo lyricInfo=lyricInfoList.get(0);
            //得到歌词的下载地址
            String lyricUrl=lyricInfo.getLrc();
            //下载歌词到本地
            downloadLyric(lyricUrl,musicName,artist);

        }
    }

    /*
    * 下载歌词方法
    * */

    HttpHandler downloadHandler;
    private void downloadLyric(String lyricUrl, String musicName, String artist) {
        HttpUtils http = new HttpUtils();
        downloadHandler=http.download(
                lyricUrl,
                lyricPath + artist + "-" + musicName + ".lrc",
                true, // 如果目标文件存在，接着未完成的部分继续下载。若服务器不支持时将重新下载。
                false, // 为true时如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                new RequestCallBack<File>() {
                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {

                        //下载到本地，则重新加载本地歌词
                        startInitLyric();
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        if (tv_search_lyric.getVisibility()==View.GONE){
                            tv_search_lyric.setVisibility(View.VISIBLE);
                            tv_search_lyric.setText("歌词下载失败");
                        }else {
                            tv_search_lyric.setText("歌词下载失败");
                        }
                    }
                }
        );
    }

    private void initLyric(File musicFile) {
        lrcView.setLrcRows(getLrcRows(musicFile));
        if (getLrcRows(musicFile)==null){
            if (tv_view_yue.getVisibility()==View.GONE){
                tv_view_yue.setVisibility(View.VISIBLE);
            }
            if (tv_search_lyric.getVisibility()==View.GONE){
                tv_search_lyric.setVisibility(View.VISIBLE);
                tv_search_lyric.setText("歌词文件错误");
            }else {
                tv_search_lyric.setText("歌词文件错误");
            }
        }
        lrcView.setOnSeekToListener(onSeekToListener);
        lrcView.setOnLrcClickListener(onLrcClickListener);
        seek_bar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        handler.sendEmptyMessage(0);
    }

    Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if (MusicPlayService.player!=null){
                seek_bar.setMax(MusicPlayService.player.getDuration());
                seek_bar.setProgress(MusicPlayService.player.getCurrentPosition());
            }else {
                seek_bar.setMax(100);
                seek_bar.setProgress(0);
            }
            handler.sendEmptyMessageDelayed(0, 100);
        }
    };

    LrcView.OnSeekToListener onSeekToListener = new LrcView.OnSeekToListener() {

        @Override
        public void onSeekTo(int progress) {
            if (MusicPlayService.player!=null){
                MusicPlayService.player.seekTo(progress);
            }
        }
    };


    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (seek_bar==seekBar){
                if (MusicPlayService.player!=null){
                    MusicPlayService.player.seekTo(seekBar.getProgress());
                }
                handler.sendEmptyMessageDelayed(0, 100);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            if(seek_bar == seekBar){
                handler.removeMessages(0);
            }
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if(seek_bar == seekBar){
                lrcView.seekTo(progress, true, fromUser);
            }
        }

    };


    //点击歌词呼出和隐藏进度条
    LrcView.OnLrcClickListener onLrcClickListener = new LrcView.OnLrcClickListener() {

        @Override
        public void onClick() {
            if (seek_bar.getVisibility()==View.GONE){
                seek_bar.setVisibility(View.VISIBLE);
                seek_bar.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), R.anim.view_show_alpha
                ));
            }else {
                seek_bar.setVisibility(View.GONE);
                seek_bar.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), R.anim.view_hide_alpha
                ));
            }
        }
    };

    /**
     * 获取歌词List集合
     */
    private List<LrcRow> getLrcRows(File musicFile){
        List<LrcRow> rows = null;
        InputStream is = null;
        try {
            is = new FileInputStream(musicFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line ;
        StringBuffer sb = new StringBuffer();
        try {
            while((line = br.readLine()) != null){
                sb.append(line+"\n");
            }
            rows = DefaultLrcParser.getIstance().getLrcRows(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rows;
    }

    private void showToast(String s){
        Toast.makeText(getActivity(),s,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            isVisibility=true;
            //歌词界面可见
            if (tv_view_yue.getVisibility()==View.GONE){
                //YuePlayer文字不可见即表明加载了歌词，此时背景常亮
                PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
                wakeLock=pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "==KeepScreenOn==");
                wakeLock.acquire();
            }else {
                if (wakeLock!=null){
                    wakeLock.release();
                    wakeLock=null;
                }
            }
        }else {
            isVisibility=false;
            //歌词界面不可见
            if (wakeLock!=null){
                wakeLock.release();
                wakeLock=null;
            }
        }
    }

    public void homeBackground(){
        //进入后台取消常亮
        if (wakeLock!=null){
            wakeLock.release();
            wakeLock=null;
        }
    }

    public void fromBackgroundBack(){
        judgeFragmentVisibilityAndWeakLock();
    }

    /*
    * 判断当前歌词fragment是否可见并设置常亮
    * */
    private void judgeFragmentVisibilityAndWeakLock() {
        //先判断歌词界面是否可见
        if (isVisibility){
            if (tv_view_yue.getVisibility() == View.GONE){
                //YuePlayer文字不可见即表明加载了歌词，此时背景常亮
                PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
                wakeLock=pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "==KeepScreenOn==");
                wakeLock.acquire();
            }else {
                if (wakeLock!=null){
                    wakeLock.release();
                    wakeLock=null;
                }
            }
        }else {
            //do nothing...
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeMessages(0);
        lrcView.reset();
        if (downloadHandler!=null){
            downloadHandler.cancel();
        }
        if (wakeLock!=null){
            wakeLock.release();
            wakeLock=null;
        }
        if (LYFInstance!=null){
            LYFInstance=null;
        }
    }
}
