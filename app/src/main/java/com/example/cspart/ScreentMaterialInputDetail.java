package com.example.cspart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cspart.api.RetrofitClient;
import com.example.cspart.models.Area;
import com.example.cspart.models.Input;
import com.example.cspart.models.InputUpdateResponse;
import com.example.cspart.models.MaterialInput;
import com.example.cspart.models.PackListRequest;
import com.example.cspart.storage.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScreentMaterialInputDetail extends AppCompatActivity {

    final static String START_SCANSERVICE = "unitech.scanservice.start";
    final static String SCANNER_INIT = "unitech.scanservice.init";
    final static String SCAN2KEY_SETTING = "unitech.scanservice.scan2key_setting";
    final static String SOFTWARE_SCANKEY = "unitech.scanservice.software_scankey";
    final static String CLOSE_SCANSERVICE = "unitech.scanservice.close";
    final static String ACTION_RECEIVE_DATA = "unitech.scanservice.data";
    final static String ACTION_RECEIVE_DATABYTES = "unitech.scanservice.databyte";
    final static String ACTION_RECEIVE_DATALENGTH = "unitech.scanservice.datalength";
    final static String ACTION_RECEIVE_DATATYPE = "unitech.scanservice.datatype";
    MaterialInput material;
    String inputCode;
    ArrayList<String> lstSerialCode = new ArrayList<String>();
    ArrayList<String> lstSerialCodeBase = new ArrayList<String>();
    ArrayList<String> stringAreaCode = new ArrayList<String>();
    private static final String[] paths = {"Loại lớn", "Loại nhỏ"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screent_material_input_detail);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.rtcimg);
        getSupportActionBar().setTitle("  CS PART");
        registerScannerReceiver();
        bindingData();
        initAction();
    }


    private void bindingData(){
        try {
            material = (MaterialInput) getIntent().getSerializableExtra("item");
            TextView txtNameMaterial =  (TextView)findViewById(R.id.txtInputNameMaterial);
            inputCode = getIntent().getStringExtra("inputCode");
            String inputCodeFull = "Mã phiếu: " + inputCode;
            lstSerialCode = (ArrayList<String>) material.getSerialNumber();
            txtNameMaterial.setText(inputCodeFull);
            EditText edtMaterialName = (EditText) findViewById(R.id.edtInputMaterialName);
            String materialName = material.getMaterialName();
            edtMaterialName.setText(materialName);
            EditText edtMaterialCode = (EditText) findViewById(R.id.edtInputMaterialCode);
            edtMaterialCode.setText(material.getMaterialCode());
            TextView txtNumber = (TextView) findViewById(R.id.txtInputNumber);
            txtNumber.setText("Số lượng: " + material.getQuantityInput().toString() + "/" + material.getQuantityRequest().toString());
            EditText edtMaterialNumber = (EditText) findViewById(R.id.edtInputMaterialNumber);
            edtMaterialNumber.setText(material.getQuantityInput().toString());
            edtMaterialNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String numberDone = edtMaterialNumber.getText().toString();
                    txtNumber.setText("Số lượng: " + numberDone + "/" + material.getQuantityRequest().toString());
                }
            });
            Spinner dropdown = findViewById(R.id.edtInputArea);
            List<Area> lstArea = material.getLstArea();
            for(int i = 0; i < lstArea.size(); i++) {
                Area area = lstArea.get(i);
                String materialCodeArea = area.getMaterialCode();
                if (material.getMaterialCode().equals(materialCodeArea)){
                    stringAreaCode.add(area.getAreaCode() + "  -  Số lượng:" + Integer.toString(area.getTotalQuantity()));
                }
            }
            Spinner spinnerDrop = (Spinner) findViewById(R.id.edtInputType);
            ArrayAdapter<String>adapterType = new ArrayAdapter<String>(ScreentMaterialInputDetail.this,
                    android.R.layout.simple_spinner_item,paths);
            spinnerDrop.setAdapter(adapterType);
            if (material.getTypeMaterial() == false) {
                spinnerDrop.setSelection(0);
            } else {
                spinnerDrop.setSelection(1);
            }
            spinnerDrop.setEnabled(false);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, stringAreaCode);
            dropdown.setAdapter(adapter);
        } catch (Exception e) {
            // This will catch any exception, because they are all descended from Exception
            System.out.println("Error " + e.getMessage());
        }
    }

    private void initAction(){
        Button btnSave = (Button) findViewById(R.id.btnSaveInput);
        btnSave.setOnClickListener(new SaveInput());
    }

    public class SaveInput implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            try {
                save();
            }
            catch (Exception e) {
                System.out.println("Error " + e.getMessage());
            }
        }
    }

    private Boolean validateBeforeSave(){
        EditText edtMaterialNumber = (EditText) findViewById(R.id.edtInputMaterialNumber);
        String numberDone = edtMaterialNumber.getText().toString();
        int intNumberDone;
        if (numberDone.isEmpty()) {
            intNumberDone = 0;
        } else {
            intNumberDone = Integer.parseInt(numberDone);
        }
        edtMaterialNumber.setText(String.valueOf(intNumberDone));
        int intNumberRequest = material.getQuantityRequest();
        int numberGet = material.getQuantityInput();
        if (intNumberRequest < (intNumberDone + numberGet) && material.getTypeMaterial() != false) {
            Toast.makeText(ScreentMaterialInputDetail.this, "Số lượng nhập lớn hơn yêu cầu", Toast.LENGTH_SHORT).show();
            edtMaterialNumber.setBackgroundResource(R.drawable.error_background);
            return false;
        }
        if (lstSerialCode.size() == 0){
            Toast.makeText(ScreentMaterialInputDetail.this, "Vui lòng bắn QRCode", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (material.getTypeMaterial() == true) {
            lstSerialCode.clear();
            lstSerialCode.add(material.getMaterialCode());
        }
        return true;
    }

    private void save() {
        if (!validateBeforeSave()) {
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences("my_shared_preff", 0);
        String userName = sharedPreferences.getString("code","");
        EditText edtMaterialNumber = (EditText) findViewById(R.id.edtInputMaterialNumber);
        String numberDone = edtMaterialNumber.getText().toString();
        Spinner mySpinner = (Spinner) findViewById(R.id.edtInputArea);
        String codeArea = "";
        String codeAreaSpinner = mySpinner.getSelectedItem().toString();
        List<Area> lstArea = material.getLstArea();
        for(int i =0; i < lstArea.size(); i ++) {
            Area area = lstArea.get(i);
            if (codeAreaSpinner.contains(area.getAreaCode())){
                codeArea = area.getAreaCode();
            }
        }

        int intNumberDone = Integer.parseInt(numberDone);
        int intNumberRequest = material.getQuantityRequest();
        if (intNumberDone > intNumberRequest) {
            Toast.makeText(ScreentMaterialInputDetail.this,"Số lượng nhập lớn hơn yêu cầu",Toast.LENGTH_SHORT).show();
            edtMaterialNumber.setBackgroundResource(R.drawable.error_background);
        }else {
            edtMaterialNumber.setBackgroundResource(R.drawable.normal_background);
            PackListRequest packListRequest = new PackListRequest(
                    inputCode,
                    "",
                    material.getMaterialCode(),
                    codeArea,
                    intNumberDone,
                    lstSerialCode,
                    userName
            );
            RetrofitClient.INSTANCE.getInstance().updateInput(packListRequest).enqueue(new Callback<InputUpdateResponse>(){

                @Override
                public void onResponse(Call<InputUpdateResponse> call, Response<InputUpdateResponse> response) {
                    Toast.makeText(ScreentMaterialInputDetail.this,response.body().getMessage(),Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }

                @Override
                public void onFailure(Call<InputUpdateResponse> call, Throwable t) {
                    Toast.makeText(ScreentMaterialInputDetail.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        return;
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
                if (qrCodeStr.contains("#")) {
                    qrCodeStr= qrCodeStr.replace("#","");
                    String stringQRCode = "";
                    for (int i = 0; i < stringAreaCode.size(); i++) {
                        if (stringAreaCode.get(i).contains(qrCodeStr)) {
                            stringQRCode = stringAreaCode.get(i);
                        }
                    }
                    int spinnerPosition = stringAreaCode.indexOf(stringQRCode);
                    if (spinnerPosition == -1) {
                        Toast.makeText(ScreentMaterialInputDetail.this, "Vị trí này không tồn tại", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        Spinner dropdown = findViewById(R.id.edtInputArea);
                        dropdown.setSelection(spinnerPosition);
                    }
                }else {
                    if (!qrCodeStr.contains(material.getMaterialCode()) && material.getTypeMaterial() == true){
                        Toast.makeText(ScreentMaterialInputDetail.this, "Vật tư không tồn tại!!!", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (!qrCodeStr.contains(material.getMaterialCode()) && qrCodeStr.contains("@") && material.getTypeMaterial() == false) {
                        Toast.makeText(ScreentMaterialInputDetail.this, "Vật tư không tồn tại!!!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (lstSerialCode.size() > 0) {
                        if (lstSerialCode.contains(qrCodeStr)) {
                            Toast.makeText(ScreentMaterialInputDetail.this, "Đã tồn tại mã hàng hóa này!!!", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            lstSerialCode.add(qrCodeStr);
                        }
                    } else {
                        lstSerialCode.add(qrCodeStr);
                    }

                    EditText edtMaterialNumber = (EditText) findViewById(R.id.edtInputMaterialNumber);
                    String numberDone = edtMaterialNumber.getText().toString();
                    int intNumberDone;
                    if (numberDone.isEmpty()) {
                        intNumberDone = 0;
                    } else {
                        intNumberDone = Integer.parseInt(numberDone);
                    }
                    intNumberDone++;
                    edtMaterialNumber.setText(String.valueOf(intNumberDone));
                    int intNumberRequest = material.getQuantityRequest();
                    if (intNumberDone > intNumberRequest) {
                        Toast.makeText(ScreentMaterialInputDetail.this, "Số lượng nhập lớn hơn yêu cầu", Toast.LENGTH_SHORT).show();
                        edtMaterialNumber.setBackgroundResource(R.drawable.error_background);
                    }
                }
            }
        }
    };

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

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mScanReceiver);
    }
}