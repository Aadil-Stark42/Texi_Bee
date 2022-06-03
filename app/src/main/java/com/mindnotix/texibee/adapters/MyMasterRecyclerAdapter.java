package com.mindnotix.texibee.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyMasterRecyclerAdapter extends RecyclerView.Adapter<MyMasterRecyclerAdapter.MyViewHolder> {
    private static final String TAG = "MasterRecyclerAdapter";
    private RecyclerView recyclerView;
    private List list;
    private int layout;
    private ViewDataBinding binding;
    private LayoutInflater layoutInflater;
    private OnBind onBind;

    public MyMasterRecyclerAdapter(@NonNull List list, @NonNull int layout) {
        this.layout = layout;
        this.list = list;
//        setHasStableIds(true);

    }

    public MyMasterRecyclerAdapter(Context context, @NonNull List list, @NonNull int layout) {
        this.layout = layout;
        this.list = list;
//        setHasStableIds(true);

    }

    public MyMasterRecyclerAdapter(RecyclerView recyclerView, List list, int layout) {
        this(list, layout);
        setRecyclerView(recyclerView);
    }


    public void updateList(List list) {
        if (!this.list.equals(list)) {
            updateNewList(list);
        } else notifyDataSetChanged();

    }

    public void updateNewList(List list) {
        this.list = list;
        if (recyclerView != null) recyclerView.setAdapter(this);
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        binding = DataBindingUtil.inflate(layoutInflater, layout, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        if (onBind != null) {
            onBind.onBindViewHolder(binding, position);
        }
    }

    private void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(this);
    }

    @Override
    public int getItemCount() {
        if (list != null) return list.size();
        else return 0;
    }

    public View getViewAt(int position) {
        if (recyclerView != null)
            if (getItemCount() > position) return recyclerView.getChildAt(position);
        return null;
    }

    public void setOnBind(OnBind onBind) {
        this.onBind = onBind;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public List getList() {
        return list;
    }

    public interface OnBind {
        void onBindViewHolder(ViewDataBinding viewDataBinding, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
        }
    }
}
