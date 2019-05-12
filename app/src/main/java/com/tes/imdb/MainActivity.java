package com.tes.imdb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tes.imdb.Constants.isOnline;
import static com.tes.imdb.Constants.showSnackbarConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView rv_mycard;
    LinearLayoutManager layoutManager;
    StaggeredGridLayoutManager gridLayoutManager;
    ProgressBar pb,footer;
    LinearLayout empty;
    SwipeRefreshLayout pullToRefresh ;
    PaginationAdapter adapter1;
    boolean isLoading=false,isLastPage=false;
    public int totalpage,page=1;
    EditText et_search;
    ImageView bt_search,back,bt_filter;
    TextView title;
    FloatingActionButton fab;

    RadioButton def,movie,series,episode;
    EditText et_year;
    Button set;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar("Movie");
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridLayoutManager.scrollToPositionWithOffset(0, 0);
            }
        });
        rv_mycard = findViewById(R.id.rv_mycard);
        rv_mycard.setHasFixedSize(true);
        footer = findViewById(R.id.footer);
        gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        rv_mycard.setLayoutManager(gridLayoutManager);
        pullToRefresh = findViewById(R.id.pullToRefresh);
        empty = findViewById(R.id.empty);
        pb = findViewById(R.id.pb);
        rv_mycard.setVisibility(View.GONE);
        empty.setVisibility(View.GONE);
        pb.setVisibility(View.GONE);
        et_search = findViewById(R.id.et_search);
        et_search.setCursorVisible(true);
        bt_search = findViewById(R.id.bt_search);
        bt_search.setOnClickListener(this);
        bt_filter = findViewById(R.id.bt_filter);
        bt_filter.setOnClickListener(this);

        rv_mycard.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int pastVisibleItems = 0;
                int visibleItemCount = gridLayoutManager.getChildCount();
                int totalItemCount = gridLayoutManager.getItemCount();
                if ((totalpage+1)==page){
                    isLastPage=true;
                }

                int[] firstVisibleItems = null;
                firstVisibleItems = gridLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);
                if(firstVisibleItems != null && firstVisibleItems.length > 0) {
                    pastVisibleItems = firstVisibleItems[0];
                }

                if (!isLoading &&!isLastPage) {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        System.out.println("LOAD NEXT ITEM");
                        getList(page,null);
                    }
                }
            }
        });

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page=1;
                getList( page,null);
            }
        });
        getList( page,null);
    }

    private void getList(int halaman,String year) {
        if (isOnline(MainActivity.this)){
        }
        else {
            showSnackbarConnection(MainActivity.this);
            return;
        }
        if (page==1){
            pb.setVisibility(View.VISIBLE);
            rv_mycard.setVisibility(View.GONE);
        }
        else {
            footer.setVisibility(View.VISIBLE);
        }
        isLoading=true;
        String s=Pref.getInstance(getApplicationContext()).getSearch();
        String type=Pref.getInstance(getApplicationContext()).getType();
        ApiServ service = ServiceGenerator.createService(ApiServ.class);
        Call<MovieList> call = service.getMovies(s,halaman,type,year);
        call.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                pb.setVisibility(View.GONE);
                rv_mycard.setVisibility(View.VISIBLE);
                footer.setVisibility(View.GONE);
//                empty.setVisibility(View.GONE);
                isLoading=false;
                pullToRefresh.setRefreshing(false);
                MovieList res = response.body();
                System.out.println("res.raw()= "+response.raw());
                System.out.println("res="+new Gson().toJson(res));
                System.out.println("page="+page);
                if (res.Response.equals("True")){
                    double a= Integer.parseInt(res.getTotalResults())/10;
                    totalpage= (int) Math.ceil(a);
                    if (page>1){
                        System.out.println("get page page>1");
                        rv_mycard.setVisibility(View.VISIBLE);
                        adapter1.addAll(res.getSearch());
                        page++;

                    }else {
                        if (res.getSearch().size()<1){
                            System.out.println("1");
                            rv_mycard.setVisibility(View.GONE);
                            empty.setVisibility(View.VISIBLE);
                        }
                        else {
                            System.out.println("2");
                            empty.setVisibility(View.GONE);
                            gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                            rv_mycard.setLayoutManager(gridLayoutManager);
                            rv_mycard.setVisibility(View.VISIBLE);
                            adapter1 = new PaginationAdapter(MainActivity.this);
                            rv_mycard.setAdapter(adapter1);
                            adapter1.newrecord();
                            adapter1.addAll(res.getSearch());
                            adapter1.notifyDataSetChanged();
                            page++;
                            System.out.println("3");
                        }
                    }
                }
                else {
                    empty.setVisibility(View.VISIBLE);
                    rv_mycard.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {

            }
        });


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
        back.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.bt_search){
            String query=et_search.getText().toString().trim();
            if (query.length()>1){
                page=1;
                Pref.getInstance(getApplicationContext()).setSearch(query);
                getList(page,null);
            }
            else {
                et_search.setError("Can't Blank");
                et_search.requestFocus();
            }

        }
        else if (v.getId()==R.id.bt_filter){
            new DialogMenu(this);
        }
    }

    private class DialogMenu implements View.OnClickListener{
        BottomSheetDialog dialog = new BottomSheetDialog(MainActivity.this);

        public DialogMenu(MainActivity context) {
            ImageView close;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_menu_bottom);
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            close= dialog.findViewById(R.id.close);
            def= dialog.findViewById(R.id.def);
            movie= dialog.findViewById(R.id.movie);
            series= dialog.findViewById(R.id.series);
            episode= dialog.findViewById(R.id.episode);
            et_year= dialog.findViewById(R.id.et_year);
            set= dialog.findViewById(R.id.set);
            et_year.setOnClickListener(this);
            close.setOnClickListener(this);
            def.setOnClickListener(this);
            set.setOnClickListener(this);
            movie.setOnClickListener(this);
            series.setOnClickListener(this);
            episode.setOnClickListener(this);
            String type = Pref.getInstance(getApplicationContext()).getType();
            if (type!=null){
                if (type.equals("movie")){
                    movie.setChecked(true);
                }
                else if (type.equals("episode")){
                    episode.setChecked(true);
                }
                else if (type.equals("series")){
                    series.setChecked(true);
                }
            }
            else {
                def.setChecked(true);
            }

            dialog.show();

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.def :
                    page=1;
                    Pref.getInstance(getApplicationContext()).setType(null);
                    break;
                case R.id.movie :
                    page=1;
                    Pref.getInstance(getApplicationContext()).setType("movie");
                    break;
                case R.id.series :
                    page=1;
                    Pref.getInstance(getApplicationContext()).setType("series");
                    break;
                case R.id.episode :
                    page=1;
                    Pref.getInstance(getApplicationContext()).setType("episode");
                    break;
                case R.id.et_year :
                    showYearDialog();
                    break;
                case R.id.close :
                    dialog.dismiss();
                    break;
                case R.id.set :
                    page=1;
                    if (et_year.getText().toString().trim().length()>0){
                        getList(page,et_year.getText().toString().trim());
                    }
                    else {
                        getList(page,null);
                    }
                    dialog.dismiss();
                    break;

            }
        }
    }

    private void showYearDialog() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        final Dialog d = new Dialog(MainActivity.this);
        d.setTitle("Year Picker");
        d.setContentView(R.layout.yeardialog);
        Button set = d.findViewById(R.id.button1);
        Button cancel =  d.findViewById(R.id.button2);
        TextView year_text=d.findViewById(R.id.year_text);
        year_text.setText(""+year);
        final NumberPicker nopicker =  d.findViewById(R.id.numberPicker1);

        nopicker.setMaxValue(year+0);
        nopicker.setMinValue(year-50);
        nopicker.setWrapSelectorWheel(false);
        nopicker.setValue(year);
        nopicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        set.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                et_year.setText(String.valueOf(nopicker.getValue()));
                d.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }

}
