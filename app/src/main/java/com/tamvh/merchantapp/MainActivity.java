package com.tamvh.merchantapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import vn.zalopay.sdk.ZaloPayErrorCode;
import vn.zalopay.sdk.ZaloPayListener;
import vn.zalopay.sdk.ZaloPaySDK;


public class MainActivity extends AppCompatActivity {
    private Button btn_send;
    private EditText txt_number;
    private TextView txt_view;
    private int AppId = 33;
    private String zptranstoken = "";
    int money = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btn_send = (Button) findViewById(R.id.btn_send);
        txt_number = (EditText) findViewById(R.id.txt_number);
        txt_view = (TextView) findViewById(R.id.txt_view);
        txt_number.setHint(R.string.app_null);
	// khởi tạo ZPDK
	ZaloPaySDK.getInstance().initWithAppId(AppId);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(txt_number.getText().toString().isEmpty()) {
                        txt_number.setHint(R.string.app_alarm);
                        txt_number.setHintTextColor(Color.RED);
                    }
                    else {
                        money = Integer.parseInt(txt_number.getText().toString());
                        makeGetRequest(money);
                        // pay                        
                        ZaloPaySDK.getInstance().payOrder(
                                MainActivity.this, zptranstoken, new MyZaloPayListener()
                        );
                        txt_number.setHint(R.string.app_null);
                    }
                } catch (Exception ex) {

                }


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        ZaloPaySDK.getInstance().onActivityResult(requestCode, resultCode, data);
    }
	
    private void makeGetRequest(int money) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("https://gbcstaging.zing.vn/mcdemo/zp/createordertk?amount=" + String.valueOf(money) + "&items=com");
        // replace with your url
        HttpResponse response;
        try {
            response = client.execute(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String json = reader.readLine();
            JSONObject object = new JSONObject(json);
            JSONObject obj_data = object.getJSONObject("data");
            String qrinfo = obj_data.getString("qrinfo");
            JSONObject obj_qrinfo = new JSONObject(qrinfo);

            zptranstoken = obj_qrinfo.getString("zptranstoken");
            Log.d("Response of GET request", json);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class MyZaloPayListener implements ZaloPayListener {
        String TAG = "MyZaloPayListener";
        @Override
        public void onPaymentSucceeded(String transactionId) {
            Log.d(TAG, "onSuccess: On successful with transactionId: " + transactionId);
            txt_view.setText("(*)THÔNG BÁO: GIAO DỊCH THÀNH CÔNG \n (SỐ TIỀN: " + String.valueOf(money) + " VNĐ)");
            txt_view.setTextColor(Color.BLUE);
        }
        @Override
        public void onPaymentError(ZaloPayErrorCode errorCode, int paymentErrorCode) {
            Log.d(TAG, String.format("onPaymentError: payment error with [error: %s, paymentError: %d]", errorCode, paymentErrorCode));
            txt_view.setText("(*)THÔNG BÁO: GIAO DỊCH THẤT BẠI");
            txt_view.setTextColor(Color.RED);
        }

    }
}



