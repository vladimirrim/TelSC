package ru.spbau.mit.telsc.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.view.stickerList.StickerList;

public class BrowseAllActivity extends AppCompatActivity {

    private String oldestPostId = "";
    private ArrayList<Bitmap> stickers = new ArrayList<>();
    private ArrayList<String> stickerNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_all);

        DatabaseReference Dbref = FirebaseDatabase.getInstance().getReference();

        ListView stickerList = findViewById(R.id.stickerList);

        Dbref.startAt(oldestPostId).limitToFirst(10).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    oldestPostId = child.getKey();
                    stickerNames.add(child.getKey());
                    byte[] sticker = (byte[]) dataSnapshot.getValue();
                    stickers.add(BitmapFactory.decodeByteArray(sticker, 0, sticker.length));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        StickerList adapter = new StickerList(BrowseAllActivity.this, stickerNames,stickers);
        stickerList.setAdapter(adapter);


        stickerList.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;
            private LinearLayout lBelow;


            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItem = totalItemCount;


            }

            private void isScrollCompleted() {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && this.currentScrollState == SCROLL_STATE_IDLE) {
                    Dbref.orderByKey().startAt(oldestPostId).limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {

                                oldestPostId = child.getKey();
                                stickerNames.add(child.getKey());
                                byte[] sticker = (byte[]) dataSnapshot.getValue();
                                stickers.add(BitmapFactory.decodeByteArray(sticker, 0, sticker.length));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    StickerList adapter = new StickerList(BrowseAllActivity.this,stickerNames, stickers);
                    stickerList.setAdapter(adapter);

                }
            }
        });
    }
    }
