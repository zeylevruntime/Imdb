package com.tes.imdb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class PaginationAdapter extends RecyclerView.Adapter<PaginationAdapter.MovieVH> {
    private Context context;
    private List<SearchResult> dataResults = new ArrayList<>();

    public PaginationAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MovieVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_demo, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_demo1, parent, false);
        return new MovieVH (view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieVH holder, int position) {
        final SearchResult result = dataResults.get(position);
        System.out.println("result "+position+" "+new Gson().toJson(result));
        if (result.getPoster().length()<10){
            holder.img.setImageResource(R.drawable.imagenotfound);
        }
        else {
            Glide.with(context)
                    .load(result.getPoster())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            holder.pb.setVisibility(View.VISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.pb.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .crossFade(1).fitCenter().into(holder.img);
        }

        holder.title.setText(result.getTitle());
        holder.year.setText(result.getYear());
        holder.type.setText(result.getType());
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("id",result.imdbID);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataResults == null ? 0 : dataResults.size();
    }

    public void addAll(List<SearchResult> moveResults) {
        System.out.println("result addAll= "+moveResults.size());
        for (int i=0; i<moveResults.size(); i++)  {
            add(moveResults.get(i));
        }
    }

    private void add(SearchResult movieList) {
        System.out.println("result r= "+new Gson().toJson(movieList));
        dataResults.add(movieList);
        notifyItemInserted(dataResults.size() - 1);
    }

    public void newrecord() {
        System.out.println("newrecord");
        dataResults.clear();
    }


    public class MovieVH extends RecyclerView.ViewHolder {
        TextView title,year,type;
        ImageView img;
        View view;
        ProgressBar pb;
        CardView cv;
        public MovieVH(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            cv =  itemView.findViewById(R.id.cv);
            title =  itemView.findViewById(R.id.title);
            year =  itemView.findViewById(R.id.year);
            type =  itemView.findViewById(R.id.type);
            img =  itemView.findViewById(R.id.img);
            pb =  itemView.findViewById(R.id.pb);

        }
    }
}
