package com.thebeginner.volumiovibes.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thebeginner.volumiovibes.R;
import com.thebeginner.volumiovibes.items.MusicItem;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<MusicItem> music_list;
    final private ListItemClickListener mOnClickListener;

    private final int TYPE_NORMAL = 1;
    private final int TYPE_FOOTER = 2;

    public interface ListItemClickListener {
        void onListItemClickListener(int clickedItemIndex, View view);
    }

    public MusicAdapter(List<MusicItem> music_list, ListItemClickListener mOnClickListener) {
        this.music_list = music_list;
        this.mOnClickListener = mOnClickListener;
    }


    public void addItem(MusicItem item) {
        music_list.add(item);

        // notify item added by position
        notifyItemInserted(music_list.size() - 1);
    }

    public MusicItem getItem(int index) {
        return music_list.get(index);
    }

    public void clearList() {
        music_list.clear();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.event_list_item;
        int layoutIdForListFooter = R.layout.event_list_footer;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        if (viewType == TYPE_NORMAL) {
            View view = inflater.from(parent.getContext()).inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
            return new ViewHolder(view);
        } else {
            View view = inflater.from(parent.getContext()).inflate(layoutIdForListFooter, parent, shouldAttachToParentImmediately);
            return new FooterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolder) {
            ((ViewHolder)holder).track_name.setText(music_list.get(position).getTrack_name());
            ((ViewHolder)holder).track_artist.setText(music_list.get(position).getTrack_artist());
            Picasso.get().load(music_list.get(position).getTrack_image()).into(((ViewHolder)holder).track_image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == music_list.size()) {
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return music_list.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView track_name, track_artist;
        ImageView track_image;

        public ViewHolder(View itemView) {
            super(itemView);

            track_name = (TextView) itemView.findViewById(R.id.track_name);
            track_artist = (TextView) itemView.findViewById(R.id.track_artist);

            /* Picasso Test */
            track_image = (ImageView) itemView.findViewById(R.id.track_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClickListener(clickedPosition, v);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        private FooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
