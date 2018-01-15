package in.bananaa.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.entity.mime.content.StringBody;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import in.bananaa.R;
import in.bananaa.utils.URLs;

public class UploadActivity extends AppCompatActivity {

    private AppCompatActivity mContext;
    private TextView tvTitle;
    private ProgressBar progressBar;
    private String filePath = null;
    private TextView txtPercentage;
    private ImageView imgPreview;
    private Button btnUpload;
    long totalSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        txtPercentage = findViewById(R.id.txtPercentage);
        btnUpload = findViewById(R.id.btnUpload);
        progressBar = findViewById(R.id.progressBar);
        imgPreview = findViewById(R.id.imgPreview);
        customizeToolbar();

        // Receiving the data from previous activity
        Intent i = getIntent();

        // image or video path that is captured in previous activity
        filePath = i.getStringExtra("filePath");

        // boolean flag to identify the media type, image or video
        boolean isImage = i.getBooleanExtra("isImage", true);

        if (filePath != null) {
            // Displaying the image or video on the screen
            previewMedia(isImage);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
        }

        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // uploading the file to server
                //new UploadFileToServer().execute();
            }
        });
    }

    private Toolbar customizeToolbar() {
        Toolbar toolbar = findViewById(R.id.upload_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvTitle = findViewById(R.id.upload_toolbar_title);
        return toolbar;
    }

    /**
     * Displaying captured image/video on the screen
     * */
    private void previewMedia(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

            imgPreview.setImageBitmap(bitmap);
        } else {
            imgPreview.setVisibility(View.GONE);
        }
    }

    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URLs.FILE_UPLOAD_URL);

            try {
//                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
//                        new AndroidMultiPartEntity.ProgressListener() {
//
//                            @Override
//                            public void transferred(long num) {
//                                publishProgress((int) ((num / (float) totalSize) * 100));
//                            }
//                        });
                MultipartEntityBuilder entity = MultipartEntityBuilder.create();
                File sourceFile = new File(filePath);

                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart("website",
                        new StringBody("www.androidhive.info"));
                entity.addPart("email", new StringBody("abc@gmail.com"));

                final HttpEntity yourEntity = entity.build();

                ProgressiveEntity progressiveEntity = new ProgressiveEntity(yourEntity);


                //totalSize = entity.getContentLength();
                httppost.setEntity(progressiveEntity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(mContext.getPackageName(), "Response from server: " + result);

            // showing the server response in an alert dialog
            showAlert(result);

            super.onPostExecute(result);
        }

    }

    class ProgressiveEntity implements HttpEntity {

        private HttpEntity httpEntity;

        public ProgressiveEntity(HttpEntity yourEntity) {
            this.httpEntity = yourEntity;
        }
        @Override
        public void consumeContent() throws IOException {
            httpEntity.consumeContent();
        }
        @Override
        public InputStream getContent() throws IOException,
                IllegalStateException {
            return httpEntity.getContent();
        }
        @Override
        public Header getContentEncoding() {
            return httpEntity.getContentEncoding();
        }
        @Override
        public long getContentLength() {
            return httpEntity.getContentLength();
        }
        @Override
        public Header getContentType() {
            return httpEntity.getContentType();
        }
        @Override
        public boolean isChunked() {
            return httpEntity.isChunked();
        }
        @Override
        public boolean isRepeatable() {
            return httpEntity.isRepeatable();
        }
        @Override
        public boolean isStreaming() {
            return httpEntity.isStreaming();
        } // CONSIDER put a _real_ delegator into here!

        @Override
        public void writeTo(OutputStream outstream) throws IOException {

            class ProxyOutputStream extends FilterOutputStream {
                /**
                 * @author Stephen Colebourne
                 */

                public ProxyOutputStream(OutputStream proxy) {
                    super(proxy);
                }
                public void write(int idx) throws IOException {
                    out.write(idx);
                }
                public void write(byte[] bts) throws IOException {
                    out.write(bts);
                }
                public void write(byte[] bts, int st, int end) throws IOException {
                    out.write(bts, st, end);
                }
                public void flush() throws IOException {
                    out.flush();
                }
                public void close() throws IOException {
                    out.close();
                }
            } // CONSIDER import this class (and risk more Jar File Hell)

            class ProgressiveOutputStream extends ProxyOutputStream {
                public ProgressiveOutputStream(OutputStream proxy) {
                    super(proxy);
                }
                public void write(byte[] bts, int st, int end) throws IOException {

                    // FIXME  Put your progress bar stuff here!

                    out.write(bts, st, end);
                }
            }

            httpEntity.writeTo(new ProgressiveOutputStream(outstream));
        }

    };

    /**
     * Method to show alert dialog
     * */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
