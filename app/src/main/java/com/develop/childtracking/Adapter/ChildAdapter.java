package com.develop.childtracking.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.develop.childtracking.Model.Child;
import com.develop.childtracking.R;
import com.develop.childtracking.ui.ParentMapActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ViewHolder> {

    private Context mContext;
    private List<Child> childList;

    public ChildAdapter(Context mContext, List<Child> childList) {
        this.mContext = mContext;
        this.childList = childList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.child_item, parent, false);
        return new ChildAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Child child = childList.get(position);
        holder.name.setText(child.getUsername());

        if (child.getImageUrl().equals("default")) {
            holder.profile.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(child.getImageUrl()).into(holder.profile);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ParentMapActivity.class);
                intent.putExtra("child", child);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profile;
        private TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.profile);
            name = itemView.findViewById(R.id.name);

        }
    }
}
