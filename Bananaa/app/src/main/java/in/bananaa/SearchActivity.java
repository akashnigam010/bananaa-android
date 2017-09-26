package in.bananaa;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import in.bananaa.utils.DrawableClickListener;
import in.bananaa.utils.Utils;

public class SearchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView tvTitle;
    EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        customizeToolbar();
        customizeSearchBar();
        setFont();
    }

    private Toolbar customizeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvTitle = (TextView) findViewById(R.id.search_toolbar_title);
        return toolbar;
    }

    private void customizeSearchBar() {
        etSearch = (EditText) findViewById(R.id.etGlobalSearch);
        etSearch.setOnTouchListener( new DrawableClickListener.RightDrawableClickListener(etSearch)
        {
            @Override
            public boolean onDrawableClick()
            {
                etSearch.setText("Hello Mr. Chandler!");
                return true;
            }
        } );
    }

    private void setFont() {
        tvTitle.setTypeface(Utils.getRegularFont(this));
        etSearch.setTypeface(Utils.getRegularFont(this));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
