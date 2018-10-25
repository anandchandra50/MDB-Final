package com.demo.mdb.spring2017finalassessment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by hp on 3/14/2017.
 */

public class ScoresAdapter extends RecyclerView.Adapter<ScoresAdapter.CustomViewHolder> {

    private Context context;
    public List<Game> games;
    private FirebaseDatabase firebaseDatabase;

    public ScoresAdapter(Context context, List<Game> games) {
        this.context = context;
        this.games = games;
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_row, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        /* TODO Part 8
         * Was that hard? I sure hope it was! Here's your last problem, and it's super easy! set the
         * three textViews using the ArrayList of games called data (we're ignoring the actual
         * phrase, just using the typedString. Put this code somewhere so that when you click on the
         * shareImageView, it posts your score to social media
         *
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, ***PUT A STRING HERE THAT SAYS YOUR ACCURACY AND WPM SO IT CAN BE SHARED WITH FRIENDS);
        sendIntent.setType("text/plain");
         */
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView wpmTextView;
        TextView accuracyTextView;
        TextView typedTextView;
        ImageView shareImageView;

        public CustomViewHolder(View itemView) {
            super(itemView);
            wpmTextView = (TextView) itemView.findViewById(R.id.wpmTextView);
            accuracyTextView = (TextView) itemView.findViewById(R.id.accuracyTextView);
            typedTextView = (TextView) itemView.findViewById(R.id.typedTextView);
            shareImageView = (ImageView) itemView.findViewById(R.id.shareImageView);
        }
    }
}
