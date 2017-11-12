package in.bananaa.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import in.bananaa.R;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewActivity extends AppCompatActivity {
    public static final String IMAGE_URL = "imageUrl";
    private ImageView ivImage;
    private ImageButton ivBack;
    private PhotoViewAttacher photoViewAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        String imageUrl = (String) getIntent().getSerializableExtra(IMAGE_URL);

        ivImage = (ImageView) findViewById(R.id.ivImage);
        Glide.with(this).load(imageUrl).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                photoViewAttacher = new PhotoViewAttacher(ivImage);
                return false;
            }
        }).into(ivImage);

        ivBack = (ImageButton) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
