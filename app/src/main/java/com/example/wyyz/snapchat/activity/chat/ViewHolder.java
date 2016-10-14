package com.example.wyyz.snapchat.activity.chat;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewHolder {

    public int mPosition;
    SparseArray<View> viewMap;
    View mConvertView;
    public ViewHolder(Context context, int resourceID, ViewGroup parentView, int mPosition) {
        this.mPosition = mPosition;
        viewMap= new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(resourceID,parentView,false);
        mConvertView.setTag(this);
    }

    public static ViewHolder getViewHolder(Context context, int resourceID, ViewGroup parentView, int mPosition, View convertView){
        if (convertView == null){
            return  new ViewHolder(context,resourceID,parentView,mPosition);
        }else {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.mPosition = mPosition;
            return  holder;
        }
    }

    public <T extends View> T getViewById(int id) {
        View temp = viewMap.get(id);
        if (temp == null){
            temp = mConvertView.findViewById(id);
            viewMap.put(id,temp);
        }
        return (T)temp;
    }

    public View getConvertView(){
        return mConvertView;
    }

    public ViewHolder setBackgroundImage(int resourceID, int imageID) {
        getViewById(resourceID).setBackgroundResource(imageID);
        return this;
    }
    public ViewHolder setOnClickListener(int resourceID, View.OnClickListener l){
        getViewById(resourceID).setOnClickListener(l);
        return this;
    }
    public void setTextViewText(int id ,String text){
        TextView textView = getViewById(id);
        textView.setText(text);
    }


}
