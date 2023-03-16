package com.example.cspart;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cspart.api.RetrofitClient;
import com.example.cspart.models.Input;
import com.example.cspart.models.MaterialInput;
import com.zebra.adc.decoder.Barcode2DWithSoft;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScreenInput extends AppCompatActivity {

    Barcode2DWithSoft barcode2DWithSoft=null;
    ArrayList<MaterialInput> arrayList;
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
        setContentView(R.layout.activity_screen_input);
        barcode2DWithSoft=Barcode2DWithSoft.getInstance();
        LayoutInflater layoutinflater = getLayoutInflater();
        new InitTask().execute();
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.rtcimg);
        getSupportActionBar().setTitle("  CS PART");
        initAction();
        registerScannerReceiver();
    }

    private void initAction(){
        Button btnViewDetail = (Button) findViewById(R.id.btnDetailInput);
        btnViewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextRequestCode = (EditText)findViewById(R.id.edtExportCode);
                requestCode = editTextRequestCode.getText().toString();
                RetrofitClient.INSTANCE.getInstance().getIntput(requestCode).enqueue(new Callback<Input>() {
                    @Override
                    public void onResponse(Call<Input> call, Response<Input> response) {
                        //response
                        int statusCode = response.body().getStatus();
                        if (statusCode == 1){
                            List<MaterialInput> lstMaterial = response.body().getMaterial();
                            arrayList = new ArrayList<MaterialInput>(lstMaterial);
                            Intent intent = new Intent(getApplicationContext(),ScreenListInput.class);
                            intent.putExtra("requestCode",requestCode);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ScreenInput.this,response.body().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Input> call, Throwable t) {
                        //error
                        Toast.makeText(ScreenInput.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    protected void onDestroy() {
        if(barcode2DWithSoft!=null){
            barcode2DWithSoft.stopScan();
            barcode2DWithSoft.close();
        }
        super.onDestroy();
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


    private  class ScanBack implements  Barcode2DWithSoft.ScanCallback{
        @Override
        public void onScanComplete(int i, int length, byte[] bytes) {
            if (length < 1) {
                if (length == -1) {
                    Toast.makeText(ScreenInput.this,"Bị dừng scan giữa chừng",Toast.LENGTH_SHORT).show();
                } else if (length == 0) {
                    Toast.makeText(ScreenInput.this,"Thời gian scan bị dài",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ScreenInput.this,"Scan lỗi",Toast.LENGTH_SHORT).show();
                }
            }else{
                String barCode = new String(bytes, 0, length);
                Toast.makeText(ScreenInput.this,barCode,Toast.LENGTH_SHORT).show();
                requestCode = barCode;
                RetrofitClient.INSTANCE.getInstance().getIntput(barCode).enqueue(new Callback<Input>() {
                    @Override
                    public void onResponse(Call<Input> call, Response<Input> response) {
                        //response
                        int statusCode = response.body().getStatus();
                        if (statusCode == 1){
                            List<MaterialInput> lstMaterial = response.body().getMaterial();
                            arrayList = new ArrayList<MaterialInput>(lstMaterial);
                            Intent intent = new Intent(getApplicationContext(),ScreenListInput.class);
                            intent.putExtra("requestCode",requestCode);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ScreenInput.this,response.body().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Input> call, Throwable t) {
                        //error
                    }
                });
            }
        }
    }

    private void ScanBarcode(){
        if(barcode2DWithSoft!=null)  {
            barcode2DWithSoft.scan();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==139 || keyCode==66 || keyCode == 293){
            if(event.getRepeatCount()==0) {
                ScanBarcode();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode==139 || keyCode == 293){
            if(event.getRepeatCount()==0) {
                barcode2DWithSoft.stopScan();
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    public class InitTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            if(barcode2DWithSoft==null){
                barcode2DWithSoft=Barcode2DWithSoft.getInstance();
            }
            boolean reuslt=false;
            if(barcode2DWithSoft!=null) {
                reuslt=  barcode2DWithSoft.open(ScreenInput.this);
                barcode2DWithSoft.setScanCallback(new ScreenInput.ScanBack());
            }
            return reuslt;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                barcode2DWithSoft.setParameter(324, 1);
                barcode2DWithSoft.setParameter(300, 0); // Snapshot Aiming
                barcode2DWithSoft.setParameter(361, 0); // Image Capture Illumination

                // interleaved 2 of 5
                barcode2DWithSoft.setParameter(6, 1);
                barcode2DWithSoft.setParameter(22, 0);
                barcode2DWithSoft.setParameter(23, 55);
                barcode2DWithSoft.setParameter(402, 1);
            }else{
                Toast.makeText(ScreenInput.this,"Fail", Toast.LENGTH_SHORT).show();
            }
        }
    }

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
                requestCode = qrCodeStr;
                viewDetail();
            }
        }
    };

    public void viewDetail() {
        EditText editTextRequestCode = (EditText)findViewById(R.id.edtExportCode);
        requestCode = editTextRequestCode.getText().toString();
        RetrofitClient.INSTANCE.getInstance().getIntput(requestCode).enqueue(new Callback<Input>() {
            @Override
            public void onResponse(Call<Input> call, Response<Input> response) {
                //response
                int statusCode = response.body().getStatus();
                if (statusCode == 1){
                    Intent intent = new Intent(getApplicationContext(),ScreenListInput.class);
                    Bundle bundle = new Bundle();
                    intent.putExtra("requestCode",requestCode);
                    startActivity(intent);
                } else {
                    Toast.makeText(ScreenInput.this,response.body().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Input> call, Throwable t) {
                //error
                Toast.makeText(ScreenInput.this,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

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