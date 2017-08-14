package in.bananaa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        String msg = intent.getStringExtra(MainActivity.SEARCH_STR);

        TextView tv = (TextView) findViewById(R.id.textView4);
        tv.setText(msg);
    }
}
