package com.tamvh.merchantapp;

import android.util.Log;

import vn.zalopay.sdk.ZaloPayErrorCode;
import vn.zalopay.sdk.ZaloPayListener;

/**
 * Created by tamvh on 13/03/2017.
 */
public class MyZaloPayListener implements ZaloPayListener {
    String TAG = "MyZaloPayListener";
    @Override
    public void onPaymentSucceeded(String transactionId) {
        Log.d(TAG, "onSuccess: On successful with transactionId: " + transactionId);
    }
    @Override
    public void onPaymentError(ZaloPayErrorCode errorCode, int paymentErrorCode) {
        Log.d(TAG, String.format("onPaymentError: payment error with [error: %s, paymentError: %d]", errorCode, paymentErrorCode));
    }

}
