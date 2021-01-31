package com.wizzapps.android.miwok;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class WordAdapter extends ArrayAdapter<Word> {

    private int colorId;

    public WordAdapter(@NonNull Context context, @NonNull List<Word> objects, int color) {
        super(context, 0, objects);
        this.colorId = color;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        Word currentWord = getItem(position);
        TextView miwokTranslationTV = listItemView.findViewById(R.id.miwok_text_view);
        miwokTranslationTV.setText(currentWord.getMiwokTranslation());
        TextView defaultTranslationTV = listItemView.findViewById(R.id.default_text_view);
        defaultTranslationTV.setText(currentWord.getDefaultTranslation());
        ImageView imageView = listItemView.findViewById(R.id.miwok_img_view);
        ViewGroup layout = listItemView.findViewById(R.id.layout_item);
        layout.setBackgroundColor(listItemView.getResources().getColor(this.colorId));
        if (currentWord.getmImageResourceId() != -1) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(currentWord.getmImageResourceId());
        } else {
            imageView.setVisibility(View.GONE);
        }
//        ImageButton playBtn = listItemView.findViewById(R.id.play_btn);
//        playBtn.setBackgroundColor(listItemView.getResources().getColor(this.colorId));
        return listItemView;
    }
}
