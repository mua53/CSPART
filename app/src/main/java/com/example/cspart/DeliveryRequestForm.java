package com.example.cspart;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cspart.api.RetrofitClient;
import com.example.cspart.models.ExportWareHouseResponse;
import com.example.cspart.models.Material;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryRequestForm extends AppCompatActivity{

    ArrayList<Material>  arrayList;
    String requestCode = "";
    final static String START_SCANSERVICE = "unitech.scanservice.start";
    final static String SCANNER_INIT = "unitech.scanservice.init";
    final static String SCAN2KEY_SETTING = "unitech.scanservice.scan2key_setting";
    final static String SOFTWARE_SCANKEY = "unitech.scanservice.software_scankey";
    final static String CLOSE_SCANSERVICE = "unitech.scanservice.close";
    final static String ACTION_RECEIVE_DATA = "unitech.scanservice.data";
    final static String ACTION_RECEIVE_DATABYTES = "unitech.scanservice.databyte";
    final static String ACTION_RECEIVE_DATALENGTH = "unitech.scanservice.datalength";
    final static String ACTION_RECEIVE_DATATYPE = "unitech.scanservice.datatype";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_request_form);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.rtcimg);
        getSupportActionBar().setTitle("  CS PART");
        initAction();
        registerScannerReceiver();
    }

    private void initAction(){
    }


    public void onClickBtn(View v) {
        switch (v.getId())
        {
            case (R.id.btnDetail):
                viewDetail();
                break;
//            case (R.id.btnScan):
//                callScanner();
//                break;
            default:
                break;
        }
    }

    public void viewDetail() {
        EditText editTextRequestCode = (EditText)findViewById(R.id.editTextRequestCode);
        requestCode = editTextRequestCode.getText().toString();
        RetrofitClient.INSTANCE.getInstance().getDeliveryRequestForm(requestCode).enqueue(new Callback<ExportWareHouseResponse>() {
            @Override
            public void onResponse(Call<ExportWareHouseResponse> call, Response<ExportWareHouseResponse> response) {
                //response
                int statusCode = response.body().getStatusCode();
                if (statusCode == 1){
                    TextView txtViewCode = (TextView)findViewById(R.id.txtViewCode);
                    txtViewCode.setText(response.body().getRequestCode());
                    TextView txtArivalTime = (TextView) findViewById(R.id.txtArivalTime);
                    txtArivalTime.setText("Ngày hóa đơn: " + response.body().getArivalTime());
                    List<Material> lstMaterial = response.body().getListMaterial();
                    arrayList = new ArrayList<Material>(lstMaterial);
                    Integer countMaterial = lstMaterial.toArray().length;
                    TextView txtContent = (TextView)  findViewById(R.id.txtContent);
                    txtContent.setText("Số lượng mặt hàng: " + countMaterial.toString());
                    txtContent.setTextColor(Color.BLACK);
                    Intent intent = new Intent(getApplicationContext(),ListExportWareHouse.class);
                    Bundle bundle = new Bundle();
                    intent.putExtra("requestCode",requestCode);
                    startActivity(intent);
                } else {
                    Toast.makeText(DeliveryRequestForm.this,response.body().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ExportWareHouseResponse> call, Throwable t) {
                //error
                Toast.makeText(DeliveryRequestForm.this,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) return;
            String qrCodeStr = bundle.getString("text");
            if (qrCodeStr != "" && qrCodeStr != null) {
                qrCodeStr = qrCodeStr.trim();
                EditText editTextRequestCode = (EditText)findViewById(R.id.editTextRequestCode);
                editTextRequestCode.setText(qrCodeStr);
                requestCode = qrCodeStr;
                viewDetail();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mScanReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mScanReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RECEIVE_DATA);
        filter.addAction(ACTION_RECEIVE_DATABYTES);
        filter.addAction(ACTION_RECEIVE_DATALENGTH);
        filter.addAction(ACTION_RECEIVE_DATATYPE);
        registerReceiver(mScanReceiver,filter);
    }



//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode==139 || keyCode==66 || keyCode == 293 || keyCode == 290){
//            callScanner();
//            if(event.getRepeatCount()==0) {
//                return true;
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if(keyCode==139 || keyCode == 293 || keyCode == 290){
//            callScanner();
//            if(event.getRepeatCount()==0) {
//                return true;
//            }
//        }
//        return super.onKeyUp(keyCode, event);
//    }

    private void startScanService() {
        Bundle bundleStart = new Bundle();
        bundleStart.putBoolean("close",true);
        Intent intentStart = new Intent().setAction(START_SCANSERVICE).putExtras(bundleStart);
        sendBroadcast(intentStart);

        Bundle bundleScanKey = new Bundle();
        bundleScanKey.putBoolean("scan2key",true);
        Intent intentScanKey = new Intent().setAction(SCAN2KEY_SETTING).putExtras(bundleScanKey);
        sendBroadcast(intentScanKey);

        Bundle bundleScanInit = new Bundle();
        bundleScanInit.putBoolean("enable",true);
        Intent intentScanInit = new Intent().setAction(SCANNER_INIT).putExtras(bundleScanInit);
        sendBroadcast(intentScanInit);
    }

    private void closeScanService() {
        //to close scan service
        Bundle bundle = new Bundle();
        bundle.putBoolean("close",true);
        Intent mIntent = new Intent().setAction(CLOSE_SCANSERVICE).putExtras(bundle);
        sendBroadcast(mIntent);
    }

    public void registerScannerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RECEIVE_DATA);
        filter.addAction(ACTION_RECEIVE_DATABYTES);
        filter.addAction(ACTION_RECEIVE_DATALENGTH);
        filter.addAction(ACTION_RECEIVE_DATATYPE);
        registerReceiver(mScanReceiver,filter);
    }

    private void callScanner() {
        Log.v("","callScanner");
        startScanService();
        Bundle bundle = new Bundle();
        bundle.putBoolean("scan",true);
        Intent mIntent = new Intent().setAction(SOFTWARE_SCANKEY).putExtras(bundle);
        sendBroadcast(mIntent);
    }

}