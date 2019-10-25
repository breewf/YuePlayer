package com.ghy.yueplayer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;

import com.ghy.yueplayer.R;


/**
 * @author HY
 * Desc:Activity切换动画
 */
public class AnimUtils {

    /**
     * 打开新页面-淡入
     *
     * @param context
     * @param clazz
     */
    public static void toFadeIn(Context context, Class clazz) {
        Activity mActivity = (Activity) context;
        mActivity.startActivity(new Intent(context, clazz));
        mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * 打开新页面-淡入
     *
     * @param context
     * @param clazz
     * @param finishSelf
     */
    public static void toFadeIn(Context context, Class clazz, boolean finishSelf) {
        Activity mActivity = (Activity) context;
        mActivity.startActivity(new Intent(context, clazz));
        mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        if (finishSelf) {
            mActivity.finish();
        }
    }

    /**
     * 打开新页面-淡入
     *
     * @param context
     * @param intent
     */
    public static void toFadeIn(Context context, Intent intent) {
        Activity mActivity = (Activity) context;
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * 打开新页面-淡入
     *
     * @param context
     * @param intent
     * @param finishSelf
     */
    public static void toFadeIn(Context context, Intent intent, boolean finishSelf) {
        Activity mActivity = (Activity) context;
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        if (finishSelf) {
            mActivity.finish();
        }
    }

    /**
     * 关闭页面-淡出
     *
     * @param context
     */
    public static void toFadeOut(Context context) {
        Activity mActivity = (Activity) context;
        mActivity.finish();
        mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * 关闭页面-淡出
     *
     * @param context
     * @param intent
     */
    public static void toFadeOut(Context context, Intent intent) {
        Activity mActivity = (Activity) context;
        if (intent != null) {
            mActivity.setResult(Activity.RESULT_OK, intent);
        } else {
            mActivity.setResult(Activity.RESULT_OK);
        }
        mActivity.finish();
        mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static void toLeftAnim(Context context, Class clazz) {
        Activity mActivity = (Activity) context;
        mActivity.startActivity(new Intent(context, clazz));
        mActivity.overridePendingTransition(R.anim.right_to_current, R.anim.curent_to_left);
    }

    public static void toLeftAnim(Context context, Class clazz, boolean finishSelf) {
        Activity mActivity = (Activity) context;
        mActivity.startActivity(new Intent(context, clazz));
        mActivity.overridePendingTransition(R.anim.right_to_current, R.anim.curent_to_left);
        if (finishSelf) {
            mActivity.finish();
        }
    }

    public static void toLeftAnim(Context context, Intent intent) {
        Activity mActivity = (Activity) context;
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(R.anim.right_to_current, R.anim.curent_to_left);
    }

    public static void toLeftAnim(Context context, Intent intent, boolean finishSelf) {
        Activity mActivity = (Activity) context;
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(R.anim.right_to_current, R.anim.curent_to_left);
        if (finishSelf) {
            mActivity.finish();
        }
    }

    public static void toRightAnim(Context context, Intent intent, boolean finishSelf) {
        Activity mActivity = (Activity) context;
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(R.anim.left_to_current, R.anim.curent_to_right);
        if (finishSelf) {
            mActivity.finish();
        }
    }

    public static void toLeftAnimForResult(Context context, Intent intent, int requestCode) {
        Activity mActivity = (Activity) context;
        mActivity.startActivityForResult(intent, requestCode);
        mActivity.overridePendingTransition(R.anim.right_to_current, R.anim.curent_to_left);
    }

    public static void toFadeInForResult(Context context, Intent intent, int requestCode) {
        Activity mActivity = (Activity) context;
        mActivity.startActivityForResult(intent, requestCode);
        mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static void toRightForResult(Context context, Intent intent) {
        Activity mActivity = (Activity) context;
        if (intent != null) {
            mActivity.setResult(Activity.RESULT_OK, intent);
        } else {
            mActivity.setResult(Activity.RESULT_OK);
        }
        mActivity.finish();
        mActivity.overridePendingTransition(R.anim.left_to_current, R.anim.curent_to_right);
    }

    public static void toRightAnim(Context context) {
        Activity mActivity = (Activity) context;
        mActivity.finish();
        mActivity.overridePendingTransition(R.anim.left_to_current, R.anim.curent_to_right);
    }

    /**
     * 转场动画
     *
     * @param context           activity
     * @param intent            intent
     * @param sharedElement     共享view
     * @param sharedElementName 共享view的name,和目标activity的目标view要有相同的sharedElementName
     */
    public static void makeSceneTransitionAnimation(Context context, Intent intent,
                                                    View sharedElement, String sharedElementName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElement.setTransitionName(sharedElementName);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
                    sharedElement, sharedElementName);
            ActivityCompat.startActivity(context, intent, options.toBundle());
        } else {
            AnimUtils.toLeftAnim(context, intent);
        }
    }

    /**
     * 转场动画
     *
     * @param context        activity
     * @param intent         intent
     * @param sharedElements 共享view集
     */
    public static void makeSceneTransitionAnimationPair(Context context, Intent intent,
                                                        Pair<View, String>... sharedElements) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (Activity) context, sharedElements);
            ActivityCompat.startActivity(context, intent, options.toBundle());
        } else {
            AnimUtils.toLeftAnim(context, intent);
        }
    }

    /**
     * 拨打客服电话
     */
    private static void callServicePhone(Context context, String telePhone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telePhone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AnimUtils.toFadeIn(context, intent, false);
    }

}
