package com.develop.childtracking.ui.ParentChilds;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.develop.childtracking.Model.Child;
import com.develop.childtracking.R;
import com.develop.childtracking.ui.ParentMap.ParentMapActivity;

import java.util.ArrayList;
import java.util.List;


public class ParentChildsAdapter extends RecyclerView.Adapter<ParentChildsAdapter.ViewHolder> {

    private List<Child> childList = new ArrayList<>();

    public ParentChildsAdapter() {
    }

    public void setChildList(List<Child> childList) {
        this.childList = childList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_item, parent, false);
        return new ParentChildsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(holder, position);
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView profile;
        private TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.profile);
            name = itemView.findViewById(R.id.name);

        }

        public void bind(ViewHolder holder, int position) {
            final Child child = childList.get(position);
            holder.name.setText(child.getUsername());

            if (child.getImageUrl().equals("default")) {
                holder.profile.setImageResource(R.drawable.children);
            } else {
                Glide.with(itemView.getContext()).load(child.getImageUrl()).into(holder.profile);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), ParentMapActivity.class);
                    intent.putExtra("child", child);
                    itemView.getContext().startActivity(intent);
                }
            });

        }
    }
}
