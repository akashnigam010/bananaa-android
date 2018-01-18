package in.bananaa.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import in.bananaa.R;
import in.bananaa.fragment.EditImageFragment;
import in.bananaa.fragment.FiltersListFragment;
import in.bananaa.utils.BitmapUtils;
import in.bananaa.utils.Constant;
import in.bananaa.utils.RequestPermissionHandler;
import in.bananaa.utils.Utils;

public class Upload2Activity extends AppCompatActivity implements FiltersListFragment.FiltersListFragmentListener, EditImageFragment.EditImageFragmentListener {
    public static final String FILE_PATH = "filePath";
    public static final String IS_FROM_GALLERY = "isFromGallery";

    private AppCompatActivity mContext;
    private ImageView imagePreview;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CoordinatorLayout coordinatorLayout;

    private Bitmap originalImage;
    // to backup image with filter applied
    private Bitmap filteredImage;
    // the final image after applying brightness, saturation, contrast
    private Bitmap finalImage;
    private FiltersListFragment filtersListFragment;
    private EditImageFragment editImageFragment;

    // modified image values
    private int brightnessFinal = 0;
    private float saturationFinal = 1.0f;
    private float contrastFinal = 1.0f;
    private String filePath = null;
    private Boolean isFromGallery;
    private RequestPermissionHandler mRequestPermissionHandler;

    // load native image filters library
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload2);
        mRequestPermissionHandler = new RequestPermissionHandler();

        imagePreview = findViewById(R.id.image_preview);
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);
        coordinatorLayout = findViewById(R.id.coordinator_layout);

        filePath = getIntent().getStringExtra(FILE_PATH);
        isFromGallery = getIntent().getBooleanExtra(IS_FROM_GALLERY, true);

        if (filePath == null) {
            Utils.genericErrorToast(this, mContext.getString(R.string.genericError));
            finish();
            return;
        }

        customizeToolbar();
        loadImage();
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private Toolbar customizeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.upload));
        return toolbar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_open) {
            openImageFromGallery();
            return true;
        }

        if (id == R.id.action_save) {
            saveImageToGallery();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        filtersListFragment = FiltersListFragment.newInstance(filePath);
        filtersListFragment.setListener(this);
        editImageFragment = new EditImageFragment();
        editImageFragment.setListener(this);
        adapter.addFragment(filtersListFragment, getString(R.string.tab_filters));
        adapter.addFragment(editImageFragment, getString(R.string.tab_edit));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFilterSelected(Filter filter) {
        resetControls();
        // applying the selected filter
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        // preview filtered image
        imagePreview.setImageBitmap(filter.processFilter(filteredImage));
        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
    }

    @Override
    public void onBrightnessChanged(final int brightness) {
        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onSaturationChanged(final float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onContrastChanged(final float contrast) {
        contrastFinal = contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
        // once the editing is done i.e seekbar is drag is completed,
        // apply the values on to filtered image
        final Bitmap bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        finalImage = myFilter.processFilter(bitmap);
    }

    /**
     * Resets image edit controls to normal when new filter
     * is selected
     */
    private void resetControls() {
        if (editImageFragment != null) {
            editImageFragment.resetControls();
        }
        brightnessFinal = 0;
        saturationFinal = 1.0f;
        contrastFinal = 1.0f;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void loadImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();

        // down sizing image as it throws OutOfMemory Exception for larger images
        options.inSampleSize = 8;
        options.outWidth = 300;
        options.outHeight = 300;

        final Bitmap bitmap = BitmapUtils.getScaledBitmap(filePath, 1000, 1000);

        originalImage = bitmap;
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        imagePreview.setImageBitmap(originalImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == Constant.SELECT_GALLERY_IMAGE_ITEM) {
            filePath = BitmapUtils.getRealPathFromURI(data.getData(), mContext);
            final Bitmap bitmap = BitmapUtils.getScaledBitmap(filePath, 1000, 1000);
            isFromGallery = true;

            // clear bitmap memory
            originalImage.recycle();
            filteredImage.recycle();
            finalImage.recycle();

            originalImage = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            imagePreview.setImageBitmap(originalImage);
            bitmap.recycle();

            // render selected image thumbnails
            filtersListFragment.prepareThumbnail(filePath);
        }
    }

    private void openImageFromGallery() {
        mRequestPermissionHandler.requestPermission(mContext, new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, Constant.REQUEST_WRITE_STORAGE, new RequestPermissionHandler.RequestPermissionListener() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, Constant.SELECT_GALLERY_IMAGE_ITEM);
            }

            @Override
            public void onFailed() {
                Utils.genericErrorToast(mContext, mContext.getString(R.string.allowStoragePermission));
            }
        });
    }

    /*
    * saves image to camera gallery
    * */
    private void saveImageToGallery() {
        mRequestPermissionHandler.requestPermission(mContext, new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, Constant.REQUEST_WRITE_STORAGE, new RequestPermissionHandler.RequestPermissionListener() {
            @Override
            public void onSuccess() {
                final String path = BitmapUtils.insertImage(getContentResolver(), finalImage, System.currentTimeMillis() + "_profile.jpg", null);
                if (!TextUtils.isEmpty(path)) {
                    Snackbar.make(coordinatorLayout, mContext.getString(R.string.savedToGallery), Snackbar.LENGTH_LONG)
                            .setAction("OPEN", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    openImage(path);
                                }
                            }).show();
                    // delete original unedited camera file
                    if(!isFromGallery) {
                        File file = new File(filePath);
                        file.delete();
                        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filePath))));
                    }
                } else {
                    Snackbar.make(coordinatorLayout, mContext.getString(R.string.notSavedToGallery), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailed() {
                Utils.genericErrorToast(mContext, mContext.getString(R.string.allowStoragePermission));
            }
        });
    }

    private void openImage(String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path), "image/*");
        startActivity(intent);
    }
}
