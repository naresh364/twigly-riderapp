package com.app.twiglydb;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.app.twiglydb.models.Order;
import com.eze.api.EzeAPI;
import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by abhishek on 25-06-2016.
 */
public class EzTapServices {
    String strTxnId = null;
    private Context mContext;
    private Order mOrder;
    public EzTapServices(Context context, Order order){
        this.mContext = context;
        this.mOrder = order;
    }
    private final int REQUESTCODE_INIT = 10001;
    private final int REQUESTCODE_PREP = 10002;
    private final int REQUESTCODE_SALE=10006;

    public void initialize () {
        JSONObject jsonInitRequest = new JSONObject();
        try {
            jsonInitRequest.put("demoAppKey", "ca467cbd-9d5e-4981-906a-7932467d6e07");
            jsonInitRequest.put("prodAppKey", "ca467cbd-9d5e-4981-906a-7932467d6e07");
            jsonInitRequest.put("merchantName", "9686444640");
            jsonInitRequest.put("userName", "9686444640");
            jsonInitRequest.put("currencyCode", "INR");
            jsonInitRequest.put("appMode", "DEMO");
            jsonInitRequest.put("captureSignature", "false");
            jsonInitRequest.put("prepareDevice", "true");// Set it true if you want
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EzeAPI.initialize(mContext, REQUESTCODE_INIT, jsonInitRequest);

    }
    public void pay() {
        JSONObject jsonPayRequest = new JSONObject();
        JSONObject jsonOptionalParams = new JSONObject();
        JSONObject jsonReferences = new JSONObject();
        JSONObject jsonCustomer = new JSONObject();
        try {
            // Building Customer Object
            jsonCustomer.put("name", mOrder.getName());
            jsonCustomer.put("mobileNo", mOrder.getMobileNumber());

            // Building References Object
            jsonReferences.put("reference1", mOrder.getOrderId());

            // Building final request object
            jsonPayRequest.put("amount", mOrder.getTotal());
            jsonPayRequest.put("mode", "SALE");// type of transaction
            jsonPayRequest.put("options", jsonOptionalParams);

            EzeAPI.cardTransaction(mContext, REQUESTCODE_SALE, jsonPayRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*

*/

    /*
	private void closeEzetap() {
		EzeAPI.close(this, REQUESTCODE_CLOSE);
	}

    private void displayToast(String message) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    */
}
