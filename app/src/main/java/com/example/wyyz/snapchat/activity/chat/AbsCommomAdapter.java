package com.example.wyyz.snapchat.activity.chat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.wyyz.snapchat.activity.ChViewHolder;

import java.util.List;


abstract  public class AbsCommomAdapter<T> extends BaseAdapter{
   protected Context context;
   protected List<T> dataList;
   private int resourceID;

   public AbsCommomAdapter(Context context, List<T> datas, int resourceID) {
       this.context = context;
       this.dataList = datas;
       this.resourceID = resourceID;
   }

   @Override
   public int getCount() {
       return dataList.size();
   }

   @Override
   public T getItem(int position) {
       return dataList.get(position);
   }

   @Override
   public long getItemId(int position) {
       return position;
   }


   @Override
   public  View getView(int position, View convertView, ViewGroup parent){
       final ChViewHolder viewHolder = ChViewHolder.getViewHolder(context,resourceID,parent,position,convertView);

       injectData(viewHolder, getItem(position));

       return viewHolder.getConvertView();
   }

   protected abstract void injectData(final ChViewHolder holder, T t);

}
