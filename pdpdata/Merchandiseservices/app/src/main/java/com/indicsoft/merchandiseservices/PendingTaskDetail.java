package com.indicsoft.merchandiseservices;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.indicsoft.merchandiseservices.DataModel.LoginResponse.PendingTaskDetailModel.PendingTaskDetails;
import com.indicsoft.merchandiseservices.Utility.CommonMethod;
import com.indicsoft.merchandiseservices.Utility.GlobalFuctions;
import com.indicsoft.merchandiseservices.Utility.SharedPreferencesManager;
import com.indicsoft.merchandiseservices.api.ApiConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PendingTaskDetail extends AppCompatActivity {

    private static final String TAG = "PendingTaskDetail";
    private Toolbar toolbar;
    private TextView tv_title, tv_description, tv_name, tv_email, tv_mobile, tv_address;
    private Button btn_proceed;
    private ProgressDialog pDialog;
    private String deviceToken;
    private PendingTaskDetails pendingTaskDetails;
    private String task_id;
    private String questionStatus = null;
    private String OutletVisit;
    private LinearLayout ll_address, ll_phone, ll_email, ll_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_pending_task_detail);
            pDialog = new ProgressDialog(PendingTaskDetail.this);
            pDialog.setMessage(getString(R.string.loading));
            deviceToken = SharedPreferencesManager.getUserToken(this);
            inintToolbar();
            initUI();
            getDataFromIntent();
            actiononUI();
            if (GlobalFuctions.isNetworkConnected(PendingTaskDetail.this)) {
                hitServerForPendingTaskDetails();
            } else {
                Toast.makeText(PendingTaskDetail.this, "" + getResources().getString(R.string.no_conection), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actiononUI() {

        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PendingTaskDetail.this, QuestionListU.class);
                intent.putExtra("task_id", task_id);
                startActivity(intent);
                finish();
            }
        });
    }

    //
    private void getDataFromIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            task_id = bundle.getString("task_id");
            questionStatus = bundle.getString("status");
            OutletVisit = bundle.getString("OutletVisit");
            if (!GlobalFuctions.isNullOrEmpty(OutletVisit)) {
                if (OutletVisit.equalsIgnoreCase("false")) {

                    ll_address.setVisibility(View.GONE);
                    ll_phone.setVisibility(View.GONE);
                    ll_email.setVisibility(View.GONE);
                    ll_name.setVisibility(View.GONE);
                }

                if (!GlobalFuctions.isNullOrEmpty(questionStatus)) {
                    if (questionStatus.equalsIgnoreCase("true")) {
                        btn_proceed.setText("Task Completed");
                        btn_proceed.setEnabled(false);
                    }
                }
            }
        }
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != pDialog)
            pDialog.dismiss();
    }

    private void hitServerForPendingTaskDetails() {
        try {
            showDialog();

            StringRequest stringRequest = new StringRequest(Request.Method.GET, ApiConstants.BASEURL + "api/taskMoreData?" + "taskId=" + task_id,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            hideDialog();
                            if (!GlobalFuctions.isNullOrEmpty(response)) {
                                try {
                                    String respo = response;
                                    Gson gson = new Gson();
                                    JSONObject myJsonObj = new JSONObject(response);
                                    pendingTaskDetails = gson.fromJson(myJsonObj.toString(), PendingTaskDetails.class);
                                    parseDataAndSaveIntoSharedPreference(pendingTaskDetails);

                                } catch (Exception e) {
                                    Log.e(TAG, e.toString());
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideDialog();
                            Log.i(TAG, error.toString());
                            String json = null;
                            NetworkResponse networkResponse = error.networkResponse;
                            try {
                                if (networkResponse.data != null) {
                                    json = new String(networkResponse.data);
                                    json = trimMessage(json, "message");
                                    if (json != null) {
                                        Toast.makeText(PendingTaskDetail.this, json, Toast.LENGTH_LONG).show();

                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(PendingTaskDetail.this, R.string.oops_some_thing_wrong, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<String, String>();

                    String android_id = Settings.Secure.getString(PendingTaskDetail.this.getContentResolver(),
                            Settings.Secure.ANDROID_ID);

                    map.put("x-access-token", deviceToken);
                    map.put("device-id", android_id);

                    return map;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    String android_id = Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("x-access-token", deviceToken);
                    map.put("device-id", android_id);
                    return map;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(PendingTaskDetail.this);
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String trimMessage(String json, String key) {
        String trimmedString = null;

        try {
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }

    private void parseDataAndSaveIntoSharedPreference(PendingTaskDetails pendingTaskDetails) {
        if (!GlobalFuctions.isNullOrEmpty(String.valueOf(pendingTaskDetails))) {
            if (!GlobalFuctions.isNullOrEmpty(pendingTaskDetails.getProject().getTitle())) {
                tv_title.setText(pendingTaskDetails.getProject().getTitle());
            }

            if (!GlobalFuctions.isNullOrEmpty(pendingTaskDetails.getProject().getDescription())) {
                tv_description.setText(pendingTaskDetails.getProject().getDescription());
            }


            if (!GlobalFuctions.isNullOrEmpty(pendingTaskDetails.getOutlet().getName())) {
                tv_name.setText(pendingTaskDetails.getOutlet().getName());
            }


            if (!GlobalFuctions.isNullOrEmpty(pendingTaskDetails.getOutlet().getEmail())) {
                tv_email.setText(pendingTaskDetails.getOutlet().getEmail());
            }


            if (!GlobalFuctions.isNullOrEmpty(pendingTaskDetails.getOutlet().getMobile())) {
                tv_mobile.setText(pendingTaskDetails.getOutlet().getMobile());
            }

            if (!GlobalFuctions.isNullOrEmpty(pendingTaskDetails.getOutlet().getAddress())) {
                tv_address.setText(pendingTaskDetails.getOutlet().getAddress());
            }


        }
    }

    private void initUI() {


        ll_address = (LinearLayout) findViewById(R.id.ll_address);
        ll_phone = (LinearLayout) findViewById(R.id.ll_phone);
        ll_email = (LinearLayout) findViewById(R.id.ll_email);
        ll_name = (LinearLayout) findViewById(R.id.ll_name);

        tv_description = (TextView) findViewById(R.id.tv_description);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_mobile = (TextView) findViewById(R.id.tv_mobile);
        tv_address = (TextView) findViewById(R.id.tv_address);
        btn_proceed = (Button) findViewById(R.id.btn_proceed);
    }

    private void inintToolbar() {
        try {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            getSupportActionBar();
            toolbar.setNavigationIcon(R.drawable.back_arrow);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorGrayToolbar));
            toolbar.setTitle("Task Detail");
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
