package in.bananaa.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.bananaa.R;
import in.bananaa.activity.MerchantDetailsActivity;
import in.bananaa.adapter.GlobalSearchAdapter;
import in.bananaa.object.GlobalSearchResponse;
import in.bananaa.utils.AlertMessages;
import in.bananaa.utils.Constant;
import in.bananaa.utils.URLs;
import in.bananaa.utils.Utils;

public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    TextView tvTitle;
    EditText etSearch;
    ListView lvSearchResults;
    GlobalSearchAdapter globalSearchAdapter;
    AlertMessages messages;
    View view;
    AppCompatActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);

        messages = new AlertMessages(activity);
        globalSearchAdapter = new GlobalSearchAdapter(activity);

        customizeToolbar();
        customizeSearchBar();
        customizeListView();
        setFont();
        return view;
    }

    private Toolbar customizeToolbar() {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.search_toolbar);
        activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle("");
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvTitle = (TextView) view.findViewById(R.id.search_toolbar_title);
        return toolbar;
    }

    private void customizeSearchBar() {
        etSearch = (EditText) view.findViewById(R.id.etGlobalSearch);
        etSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (etSearch.getRight() - etSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        etSearch.setText("");
                        // update result list
                        return true;
                    }
                }
                return false;
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
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
                        doSearch(s.toString());
                    }
                };
                handler.postDelayed(runnable, 500);
            }
        });
    }

    private void customizeListView() {
        lvSearchResults = (ListView) view.findViewById(R.id.lvSearchResults);
        lvSearchResults.setAdapter(globalSearchAdapter);
        lvSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(activity, MerchantDetailsActivity.class);
                startActivity(i);
            }
        });
    }

    private void doSearch(String searchString) {
        if (!Utils.isInternetConnected(activity)) {
            messages.showCustomMessage("No Internet Connection");
            return;
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("searchString", searchString);
                StringEntity entity = new StringEntity(jsonObject.toString());
                AsyncHttpClient client = new AsyncHttpClient();
                //client.addHeader("Authorization", "Bearer " + PreferenceManager.getToken());
                client.setTimeout(Constant.TIMEOUT);
                client.post(activity, URLs.GLOBAL_SEARCH, entity, "application/json", new GlobalSearchResponseHandler());
            } catch (UnsupportedEncodingException e) {
                messages.showCustomMessage("Something seems fishy! Please try after some time.");
                e.printStackTrace();
            } catch (Exception e) {
                messages.showCustomMessage("Something seems fishy! Please try after some time.");
                e.printStackTrace();
            }
        }
    }

    public class GlobalSearchResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            GlobalSearchResponse response = new Gson().fromJson(new String(responseBody), GlobalSearchResponse.class);
            if (response.isResult()) {
                if (response.getSearchItems() != null) {
                    // remove below extra logic to handle no results found
                    if (response.getSearchItems().size() == 1) {
                        if (response.getSearchItems().get(0).getType() != null){
                            globalSearchAdapter.addAll(response.getSearchItems());
                        } else {
                            messages.showCustomMessage("No results found");
                        }
                    } else {
                        globalSearchAdapter.addAll(response.getSearchItems());
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
            //messages.showCustomMessage("Something seems fishy! Please try after some time.");
        }
    }

    private void setFont() {
        tvTitle.setTypeface(Utils.getRegularFont(activity));
        etSearch.setTypeface(Utils.getRegularFont(activity));
    }
}
