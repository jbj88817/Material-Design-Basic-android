package com.bojie.materialtest.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bojie.materialtest.pojo.Information;
import com.bojie.materialtest.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by bojiejiang on 4/25/15.
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    List<Information> data = Collections.emptyList();
    private Context mContext;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public RecycleAdapter(Context context, List<Information> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_HEADER) {

            View view = inflater.inflate(R.layout.drawer_header, viewGroup, false);
            HeaderHolder holder = new HeaderHolder(view);
            return holder;

        } else {
            View view = inflater.inflate(R.layout.item_drawer, viewGroup, false);
            ItemHolder holder = new ItemHolder(view);
            return holder;
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder myViewHolder, int position) {

        if (myViewHolder instanceof HeaderHolder) {

        } else {
            ItemHolder itemHolder = (ItemHolder) myViewHolder;
            Information current = data.get(position - 1);
            itemHolder.tv_title.setText(current.title);
            itemHolder.iv_icon.setImageResource(current.iconId);
        }

    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        ImageView iv_icon;
        TextView tv_title;

        public ItemHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.listText);
            iv_icon = (ImageView) itemView.findViewById(R.id.listIcon);
        }

    }

    class HeaderHolder extends RecyclerView.ViewHolder {

        public HeaderHolder(View itemView) {
            super(itemView);
        }
    }
}
