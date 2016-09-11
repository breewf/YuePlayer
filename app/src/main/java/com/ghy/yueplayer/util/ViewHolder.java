package com.ghy.yueplayer.util;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by GHY on 2015/4/10.
 * 简洁ViewHolder写法
 */
public class ViewHolder {

    public static <T extends View> T getView(View view,int id){
        //从convertView中获取tag，SparseArray里面存放View和View对应的id，通过View的id来查找View
        SparseArray<View> viewHolder= (SparseArray<View>) view.getTag();
        //如果获取不到，则创建SparseArray对象，并将SparseArray对象放入convertView的tag中
        if (viewHolder==null){
            viewHolder=new SparseArray<>();
            view.setTag(viewHolder);
        }
        //通过View的id来获取View
        View childView=viewHolder.get(id);
        //如果在SparseArray中没有对应的id，则通过findViewById来获取View
        if (childView==null){
            childView=view.findViewById(id);
            //将获取到的View和与View对应的id存入SparseArray中
            viewHolder.put(id,childView);
        }
        //返回值是方法的第二个参数id对应的View
        return (T) childView;
    }
}
