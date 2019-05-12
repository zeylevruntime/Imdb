package com.tes.imdb;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tes.imdb.Constants.isOnline;
import static com.tes.imdb.Constants.showSnackbarConnection;

public class DetailActivity extends AppCompatActivity {
String id;
ImageView poster;
ProgressDialog pd;
ProgressBar pb;
LinearLayout empty,utama;
SwipeRefreshLayout pullToRefresh ;

TextView Mtitle,year,released,runtime,genre,director,writer,actors,plot,country,awards,imdbRating,imdbVotes,type,boxOffice,production,website;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        pd = new ProgressDialog(this,R.style.Theme_Dialog);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        setToolbar("Movie Detail");
        pb = findViewById(R.id.pb);
        pb.setVisibility(View.GONE);
        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                utama.setVisibility(View.GONE);
                getDetailMovie(id);
            }
        });
        empty = findViewById(R.id.empty);
        empty.setVisibility(View.GONE);
        utama = findViewById(R.id.utama);
        utama.setVisibility(View.GONE);
        poster = findViewById(R.id.poster);
        Mtitle = findViewById(R.id.Mtitle);
        year = findViewById(R.id.year);
        released = findViewById(R.id.released);
        runtime = findViewById(R.id.runtime);
        genre = findViewById(R.id.genre);
        director = findViewById(R.id.director);
        writer = findViewById(R.id.writer);
        actors = findViewById(R.id.actors);
        plot = findViewById(R.id.plot);
        country = findViewById(R.id.country);
        awards = findViewById(R.id.awards);
        imdbRating = findViewById(R.id.imdbRating);
        imdbVotes = findViewById(R.id.imdbVotes);
        type = findViewById(R.id.type);
        boxOffice = findViewById(R.id.boxOffice);
        production = findViewById(R.id.production);
        website = findViewById(R.id.website);
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            id = null;
        } else {
            id= extras.getString("id");
            System.out.println("id="+id);
            getDetailMovie(id);
        }
    }
    public void setToolbar(String name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        TextView title;
        ImageView back;
        title = findViewById(R.id.title);
        title.setText(name.toUpperCase());
        back = findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void getDetailMovie(String id) {
        if (isOnline(DetailActivity.this)){
        }
        else {
            showSnackbarConnection(DetailActivity.this);
            return;
        }
        pd.show();
        ApiServ service = ServiceGenerator.createService(ApiServ.class);
        Call<MovieDetail> call = service.getDetail(id);
        call.enqueue(new Callback<MovieDetail>() {
            @Override
            public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                pd.dismiss();
                pullToRefresh.setRefreshing(false);
                MovieDetail res = response.body();
                System.out.println("res.raw()= "+response.raw());
                System.out.println("res="+new Gson().toJson(res));
                if (res.Response.equals("True")){
                    utama.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                    if (res.getPoster().length()>10){
                        Glide.with(getApplicationContext())
                                .load(res.getPoster())
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        pb.setVisibility(View.VISIBLE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        pb.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .crossFade(1).fitCenter().into(poster);
                    }
                    else {
                        poster.setImageResource(R.drawable.imagenotfound);
                    }

                    Mtitle.setText(res.getTitle());
                    year.setText(res.getYear());
                    released.setText(res.getReleased());
                    runtime.setText(res.getRuntime());
                    genre.setText(res.getGenre());
                    director.setText(res.getDirector());
                    writer.setText(res.getWriter());
                    actors.setText(res.getActors());
                    plot.setText(res.getPlot());
                    country.setText(res.getCountry());
                    awards.setText(res.getAwards());
                    imdbRating.setText(res.getImdbRating());
                    imdbVotes.setText(res.getImdbVotes());
                    type.setText(res.getType());
                    boxOffice.setText(res.getBoxOffice());
                    production.setText(res.getProduction());
                    website.setText(res.getWebsite());

                    }
                    else {
                        utama.setVisibility(View.GONE);
                        empty.setVisibility(View.VISIBLE);
                    }
                }

            @Override
            public void onFailure(Call<MovieDetail> call, Throwable t) {

            }
        });
    }
}
