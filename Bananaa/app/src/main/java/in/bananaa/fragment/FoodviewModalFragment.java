package in.bananaa.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.bananaa.R;
import in.bananaa.adapter.DishSearchAdapter;
import in.bananaa.object.DishSearchResponse;
import in.bananaa.object.ItemFoodViewDetails;
import in.bananaa.utils.AlertMessages;
import in.bananaa.utils.Constant;
import in.bananaa.utils.URLs;
import in.bananaa.utils.Utils;

public class FoodviewModalFragment extends DialogFragment {
    public final static String ITEM_DETAILS = "item_details";
    boolean itemNotSelected = true;
    boolean isRatingLoadedFromFoodview = false;

    AlertMessages messages;
    TextView foodViewModalTitle;
    TextView tvRestName;
    ItemFoodViewDetails itemFoodViewDetails;
    EditText etDishSearch;
    EditText etFoodView;
    TextView tvTextCount;
    ProgressBar progress;
    ImageView cancelIcon;

    ListView lvDishSearchResults;
    LinearLayout rateAndReviewLayout;
    RatingBar dishRatingBar;
    DishSearchAdapter dishSearchAdapter;

    Menu menu;

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            itemFoodViewDetails = args.getParcelable(ITEM_DETAILS);
            tvRestName.setText("At " + itemFoodViewDetails.getRestName() + ", " + itemFoodViewDetails.getLocality());
            setDetailsIfExistingFoodview();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.foodview_modal, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_clear);
            actionBar.setTitle("");
        }
        messages = new AlertMessages(this.getContext());
        dishSearchAdapter = new DishSearchAdapter(this.getActivity());

        foodViewModalTitle = (TextView) view.findViewById(R.id.foodViewModalTitle);
        tvRestName = (TextView) view.findViewById(R.id.tvRestName);
        customizeListView(view);
        rateAndReviewLayout = (LinearLayout) view.findViewById(R.id.rateAndReviewLayout);
        dishRatingBar = (RatingBar) view.findViewById(R.id.dishRatingBar);
        etDishSearch = (EditText) view.findViewById(R.id.etDishSearch);
        cancelIcon = (ImageView) view.findViewById(R.id.dishCancelButton);
        progress = (ProgressBar) view.findViewById(R.id.dishSearchLoader);
        etFoodView = (EditText) view.findViewById(R.id.etFoodView);
        tvTextCount = (TextView) view.findViewById(R.id.tvTextCount);
        dishRatingBar.setOnRatingBarChangeListener(onRatingBarChangeListener);
        setHasOptionsMenu(true);
        customizeSearch();
        customizeFoodview();
        setFont();
        return view;
    }

    private void setDetailsIfExistingFoodview() {
        if (!itemFoodViewDetails.getAddNewFoodview()) {
            itemNotSelected = false;
            etDishSearch.setText(itemFoodViewDetails.getItemName());
            lvDishSearchResults.setVisibility(View.GONE);
            rateAndReviewLayout.setVisibility(View.VISIBLE);
            etFoodView.setText(itemFoodViewDetails.getDesc());
            isRatingLoadedFromFoodview = true;
            dishRatingBar.setRating(Float.parseFloat(itemFoodViewDetails.getRating()));
        }
    }

    private void customizeSearch() {
        cancelIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etDishSearch.setText("");
                lvDishSearchResults.setVisibility(View.VISIBLE);
                rateAndReviewLayout.setVisibility(View.GONE);
                return true;
            }
        });

        etDishSearch.addTextChangedListener(new TextWatcher() {
            final android.os.Handler handler = new android.os.Handler();
            Runnable runnable;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(runnable);
            }

            @Override
            public void afterTextChanged(final Editable s) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (s.toString().length() >= 2 && itemNotSelected) {
                            isRatingLoadedFromFoodview = true;
                            dishRatingBar.setRating(0.0f);
                            etFoodView.setText("");
                            doSearch(s.toString());
                        } else {
                            itemNotSelected = true;
                        }
                    }
                };
                handler.postDelayed(runnable, 500);
            }
        });
    }

    RatingBar.OnRatingBarChangeListener onRatingBarChangeListener = new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
            if (!isRatingLoadedFromFoodview) {
                Toast.makeText(getActivity(),
                        "Your rating has been saved. Thank you!", Toast.LENGTH_SHORT).show();
            } else {
                isRatingLoadedFromFoodview = false;
            }
        }
    };

    private void customizeFoodview() {
        etFoodView.addTextChangedListener(new TextWatcher() {
            final android.os.Handler handler = new android.os.Handler();
            Runnable runnable;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(runnable);
            }

            @Override
            public void afterTextChanged(final Editable s) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        int foodviewLength = etFoodView.getText().length();
                        tvTextCount.setText(foodviewLength + "/200");
                        if (foodviewLength <= 0) {
                            menu.getItem(0).setVisible(false);
                        } else {
                            menu.getItem(0).setVisible(true);
                        }
                    }
                };
                handler.postDelayed(runnable, 10);
            }
        });
    }

    private void doSearch(String searchString) {
        if (!Utils.isInternetConnected(this.getContext())) {
            messages.showCustomMessage("No Internet Connection");
            return;
        } else {
            try {
                asyncStart();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("merchantId", 1);
                jsonObject.put("searchString", searchString);
                StringEntity entity = new StringEntity(jsonObject.toString());
                AsyncHttpClient client = new AsyncHttpClient();
                //client.addHeader("Authorization", "Bearer " + PreferenceManager.getToken());
                client.setTimeout(Constant.TIMEOUT);
                client.post(this.getContext(), URLs.DISH_SEARCH, entity, "application/json", new DishSearchResponseHandler());
            } catch (UnsupportedEncodingException e) {
                messages.showCustomMessage("Something seems fishy! Please try again");
                e.printStackTrace();
            } catch (Exception e) {
                messages.showCustomMessage("Something seems fishy! Please try again");
                e.printStackTrace();
            }
        }
    }

    public class DishSearchResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            asyncEnd();
            DishSearchResponse response = new Gson().fromJson(new String(responseBody), DishSearchResponse.class);
            if (response.isResult()) {
                if (response.getItems() != null) {
                    // remove below extra logic to handle no results found
                    if (response.getItems().size() == 1) {
                        if (response.getItems().get(0).getId() != -999){
                            dishSearchAdapter.addAll(response.getItems());
                        } else {
                            messages.showCustomMessage("No results found");
                        }
                    } else {
                        dishSearchAdapter.addAll(response.getItems());
                    }

                } else {
                    messages.showCustomMessage("No results found");
                }
            } else {
                messages.showCustomMessage("Something seems fishy! Please try after some time.");
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            asyncEnd();
            //messages.showCustomMessage("Something seems fishy! Please try after some time.");
        }
    }

    private void customizeListView(View view) {
        lvDishSearchResults = (ListView) view.findViewById(R.id.lvDishSearchResults);
        lvDishSearchResults.setAdapter(dishSearchAdapter);
        lvDishSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemNotSelected = false;
                etDishSearch.setText(dishSearchAdapter.getItem(position).getName());
                etDishSearch.setSelection(etDishSearch.getText().length());
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                rateAndReviewLayout.requestFocus();
                lvDishSearchResults.setVisibility(View.GONE);
                rateAndReviewLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void asyncStart() {
        cancelIcon.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);
    }

    private void asyncEnd() {
        lvDishSearchResults.setVisibility(View.VISIBLE);
        rateAndReviewLayout.setVisibility(View.GONE);
        cancelIcon.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.foodview_modal_menu, menu);

        SpannableStringBuilder title = new SpannableStringBuilder(getContext().getString(R.string.save));
        title.setSpan(Utils.getRegularFont(getContext()), 0, title.length(), 0);
        MenuItem menuItem = this.menu.getItem(0);
        menuItem.setTitle(title);
        menuItem.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            float rating = dishRatingBar.getRating();
            if (rating == 0.0) {
                Toast.makeText(getActivity(),
                        "Please provide a rating!", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (etFoodView.getText().length() < 50) {
                Toast.makeText(getActivity(),
                        "Foodview must be atleast 50 characters long", Toast.LENGTH_SHORT).show();
                return true;
            }
            Toast.makeText(getActivity(),
                    "Your foodview has been saved. Thank you!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == android.R.id.home) {
            dismiss();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFont() {
        foodViewModalTitle.setTypeface(Utils.getBold(getContext()));
        tvRestName.setTypeface(Utils.getRegularFont(getContext()));
        etDishSearch.setTypeface(Utils.getBold(getContext()));
        etFoodView.setTypeface(Utils.getRegularFont(getContext()));
        tvTextCount.setTypeface(Utils.getRegularFont(getContext()));
    }
}
