package in.bananaa.activity;

import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import in.bananaa.R;

import static com.bumptech.glide.Glide.with;

public class ImageViewActivity extends AppCompatActivity {
    private ImageView ivImage;
    private ScaleGestureDetector scaleGestureDetector;
    private Matrix matrix = new Matrix();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        ivImage = (ImageView)findViewById(R.id.ivImage);
        with(ImageViewActivity.this)
                .load("https://bna-s3.s3.amazonaws.com/d/12/pasta-donatella.jpg")
                .centerCrop()
                .placeholder(R.color.grey)
                .crossFade()
                .into(ivImage);
        scaleGestureDetector = new ScaleGestureDetector(this,new ScaleListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        scaleGestureDetector.onTouchEvent(ev);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.
            SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
            matrix.setScale(scaleFactor, scaleFactor);
            ivImage.setImageMatrix(matrix);
            return true;
        }
    }
}
