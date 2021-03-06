package com.example.wyyz.snapchat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.model.Snap;

import java.util.ArrayList;

/**
 * Created by ZIYUAN on 11/10/2016.
 * ImageAdapter, used by SnapsFragment, CameraRollFragment
 * This adapter supports the behaviour of multiple select images
 */

public class ImageAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private boolean[] selectMap;
    private ArrayList<CheckBox> checkBoxes=new ArrayList<>();
    private ArrayList<ImageView> imageViews=new ArrayList<>();
    private ArrayList<View> shadows=new ArrayList<>();
    private String fragname;

    public ImageAdapter(Context context, int layoutResourceId, ArrayList data, String fragmentName) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.selectMap=new boolean[data.size()];
        this.fragname = fragmentName;
        for(int i=0;i<selectMap.length;i++){
            selectMap[i]=false;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) view.findViewById(R.id.iv_image);
            holder.shadow=(View) view.findViewById(R.id.alpha_view);
            holder.checkBox=(CheckBox)view.findViewById(R.id.checkimages);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final Snap item = (Snap)getItem(position);
        //holder.image.setImageBitmap(item.getPhoto());
        Glide.with(context)
                .load(item.getPath())
                .centerCrop()
                .crossFade()
                .into(holder.image);
        checkBoxes.add(holder.checkBox);
        imageViews.add(holder.image);
        shadows.add(holder.shadow);
        holder.checkBox.setId(position);
        holder.image.setId(position);
        holder.shadow.setId(position);
        final ViewHolder finalHolder = holder;
        //click checkBox for multiple select
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    selectMap[position]=true;
                    finalHolder.shadow.setVisibility(View.VISIBLE);
                }else{
                    selectMap[position]=false;
                    finalHolder.shadow.setVisibility(View.GONE);
                }
            }
        });
        //single select image, will be redirect to OpenMySnapActivity
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Toast.makeText(context, "Single Photo Edit!",
                        Toast.LENGTH_SHORT).show();*/
                Intent intent = new Intent(context,OpenMySnapActivity.class);
                intent.putExtra("SnapPath",item.getPath());
                intent.putExtra("TimeStamp",item.getTimestamp());
                intent.putExtra("Size",item.getSize());
                intent.putExtra("Label",fragname);
                context.startActivity(intent);

            }
        });
        return view;
    }

    //Enable multiple click
    public void toggleOnSelect(){
        for (CheckBox cb: checkBoxes) {
            cb.setVisibility(View.VISIBLE);
        }
       for(ImageView imgv:imageViews){
            imgv.setClickable(false);
        }

    }
    public void disableSelectImg(){
        for(ImageView imgv:imageViews){
            imgv.setClickable(false);
        }
    }

    //disable multiple select
    public void toggleOffSelect() {
        for (CheckBox cb: checkBoxes) {
            cb.setChecked(false);
            cb.setVisibility(View.GONE);
        }
       for(ImageView imgv : imageViews){
            imgv.setClickable(true);
        }
        for(View s : shadows){
            s.setVisibility(View.GONE);
        }
        for(int i=0;i<selectMap.length;i++){
            selectMap[i]=false;
        }
    }

    static class ViewHolder {
        int id;
        ImageView image;
        View shadow;
        CheckBox checkBox;
    }

    public boolean[] getSelectMap() {
        return selectMap;
    }
}
