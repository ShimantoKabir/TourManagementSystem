package com.example.maask.tourmanagementsystem;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.SearchManager;
import android.support.v7.widget.SearchView;
import com.example.maask.tourmanagementsystem.WeatherFile.ViewPagerAdapter;
import android.content.ComponentName;


public class WeatherActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar toolbar;
    private TextView[] dots;
    LinearLayout showDots;
    ViewPagerAdapter viewPagerAdapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        toolbar = findViewById(R.id.custom_toolbar);
        viewPager = findViewById(R.id.fragmentContainer);
        showDots = findViewById(R.id.showDots);

        // toolbar ka locha ...
        toolbar.setTitle("");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addDotIndicator(0);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // nothing to do ..........
            }

            @Override
            public void onPageSelected(int position) {
                showDots.removeAllViews();
                addDotIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // nothing to do ..........
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_weather,menu);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_city).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchResultActivity.class)));
        searchView.setSubmitButtonEnabled(true);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                Intent intent = new Intent(WeatherActivity.this,HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void addDotIndicator(int position){

        dots = new TextView[2];
        for (int i = 0; i < dots.length ; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorPrimary));
            showDots.addView(dots[i]);
            if (i==position){
                dots[i].setTextColor(getResources().getColor(R.color.radius_border));
            }

        }

    }

}
