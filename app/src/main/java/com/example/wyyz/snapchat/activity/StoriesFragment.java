package com.example.wyyz.snapchat.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.db.SnapChatDB;
import com.example.wyyz.snapchat.model.Story;
import com.example.wyyz.snapchat.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoriesFragment extends Fragment {
    private GridView gridView;
    private StoryAdapter storyAdapter;
    private ArrayList<Story> stories=new ArrayList<>();
    SnapChatDB db;

    public StoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View storyView=inflater.inflate(R.layout.fragment_stories, container, false);
        final SnapActivity snapActivity = (SnapActivity) getActivity();
        gridView = (GridView) storyView.findViewById(R.id.gridView);
        db=SnapChatDB.getInstance(snapActivity);
        String email= FirebaseAuth.getInstance().getCurrentUser().getEmail();
        User user=db.findUserByEmail(email);
        stories=db.getUserStories(user.getId());
        storyAdapter = new StoryAdapter(snapActivity, R.layout.image_item, stories);
        gridView.setAdapter(storyAdapter);
        /*gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Story story=stories.get(position);
                ArrayList<Snap> snaps=db.getStorySnaps(story);
                ArrayList<String> paths = new ArrayList<String>();
                int[] timers=new int[snaps.size()];
                for(int i=0;i<snaps.size();i++){
                    paths.add(snaps.get(i).getPath());
                    timers[i]=snaps.get(i).getTimingOut();
                    Log.d("path",String.valueOf(snaps.get(i).getPath()));
                    Log.d("path",String.valueOf(snaps.get(i).getTimingOut()));
                }
                Intent intent = new Intent(snapActivity, DisplaySnapActivity.class);
                intent.putExtra("ActivityName","SnapActivity");
                intent.putExtra("SnapPath",paths);
                intent.putExtra("Timer",timers);
                startActivity(intent);
            }
        });*/
        return storyView;
    }

}
