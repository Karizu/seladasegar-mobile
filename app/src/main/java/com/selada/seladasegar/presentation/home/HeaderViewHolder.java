package com.selada.seladasegar.presentation.home;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.selada.seladasegar.R;

public class HeaderViewHolder extends RecyclerView.ViewHolder {

    private final TextView txtSourceName;
    private final RecyclerView rvHeadline;

    public HeaderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtSourceName = itemView.findViewById(R.id.txt_source_name);
        rvHeadline = itemView.findViewById(R.id.rv_headline);
    }

    public void setSourceName(String sourceName) {
        this.txtSourceName.setText(sourceName);
    }

    public void setupRecyclerView(Context context, HomeFeedAdapter adapter) {
        rvHeadline.setLayoutManager(new LinearLayoutManager(context));
        rvHeadline.setAdapter(adapter);
    }
}
