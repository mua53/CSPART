package com.example.cspart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cspart.api.RetrofitClient;
import com.example.cspart.models.ExportWareHouseResponse;
import com.example.cspart.models.Input;
import com.example.cspart.models.MaterialInput;
import com.example.cspart.models.PackingListResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScreenExportWarehouse extends AppCompatActivity {
    String exportWarehouseCode = "";

//    final static String START_SCANSERVICE = "unitech.scanservice.start";
//    final static String SCANNER_INIT = "unitech.scanservice.init";
//    final static String SCAN2KEY_SETTING = "unitech.scanservice.scan2key_setting";
//    final static String SOFTWARE_SCANKEY = "unitech.scanservice.software_scankey";
//    final static String CLOSE_SCANSERVICE = "unitech.scanservice.close";
    final static String ACTION_RECEIVE_DATA = "unitech.scanservice.data";
    final static String ACTION_RECEIVE_DATABYTES = "unitech.scanservice.databyte";
    final static String ACTION_RECEIVE_DATALENGTH = "unitech.scanservice.datalength";
    final static String ACTION_RECEIVE_DATATYPE = "unitech.scanservice.datatype";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_export_warehouse);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.rtcimg);
        getSupportActionBar().setTitle("  CS PART");
        initAction();
        registerScannerReceiver();
    }

    private void initAction(){
        Button btnViewDetail = (Button) findViewById(R.id.btnDetailExportWarehouse);
        btnViewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextRequestCode = (EditText)findViewById(R.id.edtExportCode);
                exportWarehouseCode = editTextRequestCode.getText().toString();
                RetrofitClient.INSTANCE.getInstance().getPackList(exportWarehouseCode).enqueue(new Callback<PackingListResponse>() {
                    @Override
                    public void onResponse(Call<PackingListResponse> call, Response<PackingListResponse> response) {
                        //response
                        int statusCode = response.body().getStatus();
                        if (statusCode == 1){
                            List<MaterialInput> lstMaterial = response.body().getMaterial();
//                            arrayList = new ArrayList<MaterialInput>(lstMaterial);
                            Intent intent = new Intent(getApplicationContext(),ScreenListExportWarehouse.class);
                            intent.putExtra("exportWarehouseCode",exportWarehouseCode);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ScreenExportWarehouse.this,response.body().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PackingListResponse> call, Throwable t) {
                        Toast.makeText(ScreenExportWarehouse.this,t.getMessage(),Toast.LENGTH_SHORT).show();

                    }

                });
            }
        });
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

    @Override
    protected void onDestroy() {
        unregisterReceiver(mScanReceiver);
        super.onDestroy();
    }

    /*
     * Khai bao broadcast
     *
     * */
    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) return;
            String qrCodeStr = bundle.getString("text");
            if (qrCodeStr != "" && qrCodeStr != null) {
                qrCodeStr = qrCodeStr.trim();
                EditText editTextRequestCode = (EditText)findViewById(R.id.edtExportCode);
                editTextRequestCode.setText(qrCodeStr);
                exportWarehouseCode = qrCodeStr;
                viewDetail();
            }
        }
    };

    public void viewDetail() {
        EditText editTextRequestCode = (EditText)findViewById(R.id.edtExportCode);
        exportWarehouseCode = editTextRequestCode.getText().toString();
        RetrofitClient.INSTANCE.getInstance().getPackList(exportWarehouseCode).enqueue(new Callback<PackingListResponse>() {
            @Override
            public void onResponse(Call<PackingListResponse> call, Response<PackingListResponse> response) {
                //response
                int statusCode = response.body().getStatus();
                if (statusCode == 1){
                    Intent intent = new Intent(getApplicationContext(),ScreenListExportWarehouse.class);
                    Bundle bundle = new Bundle();
                    intent.putExtra("exportWarehouseCode",exportWarehouseCode);
                    startActivity(intent);
                } else {
                    Toast.makeText(ScreenExportWarehouse.this,response.body().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PackingListResponse> call, Throwable t) {
                //error
                Toast.makeText(ScreenExportWarehouse.this,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void startScanService() {
//        Bundle bundleStart = new Bundle();
//        bundleStart.putBoolean("close",true);
//        Intent intentStart = new Intent().setAction(START_SCANSERVICE).putExtras(bundleStart);
//        sendBroadcast(intentStart);
//
//        Bundle bundleScanKey = new Bundle();
//        bundleScanKey.putBoolean("scan2key",true);
//        Intent intentScanKey = new Intent().setAction(SCAN2KEY_SETTING).putExtras(bundleScanKey);
//        sendBroadcast(intentScanKey);
//
//        Bundle bundleScanInit = new Bundle();
//        bundleScanInit.putBoolean("enable",true);
//        Intent intentScanInit = new Intent().setAction(SCANNER_INIT).putExtras(bundleScanInit);
//        sendBroadcast(intentScanInit);
//    }
//
//    private void closeScanService() {
//        //to close scan service
//        Bundle bundle = new Bundle();
//        bundle.putBoolean("close",true);
//        Intent mIntent = new Intent().setAction(CLOSE_SCANSERVICE).putExtras(bundle);
//        sendBroadcast(mIntent);
//    }

    public void registerScannerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RECEIVE_DATA);
        filter.addAction(ACTION_RECEIVE_DATABYTES);
        filter.addAction(ACTION_RECEIVE_DATALENGTH);
        filter.addAction(ACTION_RECEIVE_DATATYPE);
        registerReceiver(mScanReceiver,filter);
    }

//    private void callScanner() {
//        Log.v("","callScanner");
//        startScanService();
//        Bundle bundle = new Bundle();
//        bundle.putBoolean("scan",true);
//        Intent mIntent = new Intent().setAction(SOFTWARE_SCANKEY).putExtras(bundle);
//        sendBroadcast(mIntent);
//    }
}