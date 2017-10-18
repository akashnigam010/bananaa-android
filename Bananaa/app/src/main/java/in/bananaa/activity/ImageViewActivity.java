package in.bananaa.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import in.bananaa.R;

import static com.bumptech.glide.Glide.with;

public class ImageViewActivity extends AppCompatActivity {
    public static final String IMAGE_URL = "imageUrl";
    private ImageView ivImage;
    private ImageButton ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        String imageUrl = (String) getIntent().getSerializableExtra(IMAGE_URL);
        ivImage = (ImageView)findViewById(R.id.ivImage);
        with(ImageViewActivity.this)
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.color.grey)
                .crossFade()
                .into(ivImage);
        ivBack = (ImageButton) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
