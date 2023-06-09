package com.example.cspart;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cspart.api.RetrofitClient;
import com.example.cspart.models.Area;
import com.example.cspart.models.InputUpdateResponse;
import com.example.cspart.models.MaterialByArea;
import com.example.cspart.models.MaterialDetailResponse;
import com.example.cspart.models.MaterialInput;
import com.example.cspart.models.PackListRequest;
import com.example.cspart.models.PackingListResponse;
import com.example.cspart.models.ReportResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScreenReportByArea extends AppCompatActivity {

    final static String ACTION_RECEIVE_DATA = "unitech.scanservice.data";
    final static String ACTION_RECEIVE_DATABYTES = "unitech.scanservice.databyte";
    final static String ACTION_RECEIVE_DATALENGTH = "unitech.scanservice.datalength";
    final static String ACTION_RECEIVE_DATATYPE = "unitech.scanservice.datatype";

    MaterialInput material;
    String inputCode;
    String currentAreaCode = "";
    Integer indexOfMaterial =0;
//    MaterialInput currentMaterial = null;
    ArrayList<String> lstSerialCode = new ArrayList<String>();
    ArrayList<String> lstAreaCode = new ArrayList<String>();
    ArrayList<MaterialInput> lstMaterialInput = new ArrayList<MaterialInput>();
    ArrayList<MaterialInput> lstCurrentMaterialInput = new ArrayList<MaterialInput>();
    ArrayList<String> lstMaterialName = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_report_by_area);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.rtcimg);
        getSupportActionBar().setTitle("  CS PART");
        registerScannerReceiver();
        bindingData();
        initAction();
    }

    /*
    * Thực hiện binding data vào màn hình
    * Created by tupt - 20230317
    * */
    private void bindingData(){
        TextView txtNameMaterial =  (TextView)findViewById(R.id.txtReportName);
        inputCode = getIntent().getStringExtra("requestCode");
        txtNameMaterial.setText(inputCode);
        RetrofitClient.INSTANCE.getInstance().getInventoryWarehouse(inputCode).enqueue(new Callback<ReportResponse>(){

            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                //response
                int statusCode = response.body().getStatus();
                if (statusCode == 1) {
                    lstMaterialInput = (ArrayList<MaterialInput>) response.body().getData().getMaterial();
                    for (int i =0; i < lstMaterialInput.size(); i++) {
                        MaterialInput item = lstMaterialInput.get(i);
                        String areaCode = item.getAreaCode().replace("#","");
                        if (!lstAreaCode.contains(areaCode)) {
                            lstAreaCode.add(areaCode);
                        }
                    }
                    Spinner dropdown = findViewById(R.id.spinnerReportArea);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, lstAreaCode);
                    dropdown.setAdapter(adapter);
                    Spinner dropdownMaterialName = findViewById(R.id.spinnerReportMaterialName);

                    dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            String areaCode = "#" + dropdown.getSelectedItem().toString() + "#";
                            currentAreaCode = dropdown.getSelectedItem().toString();
                            lstCurrentMaterialInput.clear();
                            lstMaterialName.clear();
                            lstSerialCode.clear();
                            indexOfMaterial = 0;
                            for(int j =0; j < lstMaterialInput.size(); j++) {
                                if (areaCode.equals(lstMaterialInput.get(j).getAreaCode())){
                                    lstMaterialName.add(lstMaterialInput.get(j).getMaterialName());
                                    lstCurrentMaterialInput.add(lstMaterialInput.get(j));
                                }
                            }
                            ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, lstMaterialName);
                            dropdownMaterialName.setAdapter(adapter2);
                            dropdownMaterialName.setSelection(indexOfMaterial);
                            dropdownMaterialName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    material = lstCurrentMaterialInput.get(i);
                                    indexOfMaterial = i;
                                    EditText edtMaterialCode = (EditText) findViewById(R.id.edtReportByAreaMaterialCode);
                                    edtMaterialCode.setText(material.getMaterialCode());
                                    EditText quantityReal = (EditText) findViewById(R.id.edtReportNumberDone);
                                    quantityReal.setText(material.getQuantityReal().toString());
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    if (!currentAreaCode.isEmpty()) {
                        int indexAreaCode = lstAreaCode.indexOf(currentAreaCode);
                        if (indexAreaCode != -1) {
                            dropdown.setSelection(indexAreaCode);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {
                Toast.makeText(ScreenReportByArea.this,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    * Thực hiện khởi tạo các hành động
    * Created by tupt - 20230317
    * */
    private void  initAction(){
        Button btnSave = (Button) findViewById(R.id.btnSaveReportByArea);
        btnSave.setOnClickListener(new ScreenReportByArea.SaveReport());
    }

    public class SaveReport implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            try {
                if(material.getTypeMaterial() == true && material.getQuantityReal() > 0) {
                    CustomDialogConfirm();
                } else {
                    save();
                }
            }
            catch (Exception e) {
                System.out.println("Error " + e.getMessage());
            }
        }
    }

    public void CustomDialogConfirm() {
        final Dialog customDialog = new Dialog(ScreenReportByArea.this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.custom_dialog_confirm);
        customDialog.setTitle("Xác nhận");

        Button btnClose = (Button) customDialog.findViewById(R.id.btnCloseDialog);
        Button btnSave = (Button) customDialog.findViewById(R.id.btnUpdateReport);
        TextView txtContent = (TextView) customDialog.findViewById(R.id.txtContentMess);
        txtContent.setText("Vật tư đã tồn tại báo cáo, bạn có muốn update không?");
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.cancel();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
                customDialog.cancel();
            }
        });
        customDialog.show();
    }

    public class Test2 implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            String qrCodeStr = "#A1-10#";
            if (qrCodeStr.contains("#")) {
                qrCodeStr= qrCodeStr.replace("#","");
                String stringQRCode = "";
                for (int i = 0; i < lstAreaCode.size(); i++) {
                    if (lstAreaCode.get(i).equals(qrCodeStr)) {
                        stringQRCode = lstAreaCode.get(i);
                    }
                }
                int spinnerPosition = lstAreaCode.indexOf(stringQRCode);
                if (spinnerPosition == -1) {
                    Toast.makeText(ScreenReportByArea.this, "Vị trí này không tồn tại", Toast.LENGTH_SHORT).show();
                }else {
                    Spinner dropdown = findViewById(R.id.spinnerReportArea);
                    dropdown.setSelection(spinnerPosition);
                }
                return;
            }
        }
    }

    private void save() {
        SharedPreferences sharedPreferences = getSharedPreferences("my_shared_preff", 0);
        String userName = sharedPreferences.getString("code","");
        EditText edtMaterialNumber = (EditText) findViewById(R.id.edtReportNumberDone);
        String numberDone = edtMaterialNumber.getText().toString();
        Spinner mySpinner = (Spinner) findViewById(R.id.spinnerReportArea);
        String codeArea = mySpinner.getSelectedItem().toString();
        EditText edtMaterialCode = (EditText) findViewById(R.id.edtReportByAreaMaterialCode);
        String materialCode = edtMaterialCode.getText().toString();
        int intNumberDone = Integer.parseInt(numberDone);
        edtMaterialNumber.setBackgroundResource(R.drawable.normal_background);
//        lstSerialCode.add("AWW024CC4B61");
        PackListRequest packListRequest = new PackListRequest(
                inputCode,
                "",
                materialCode,
                codeArea,
                intNumberDone,
                lstSerialCode,
                userName
        );
        RetrofitClient.INSTANCE.getInstance().updateInventoryWarehouse(packListRequest).enqueue(new Callback<InputUpdateResponse>(){

            @Override
            public void onResponse(Call<InputUpdateResponse> call, Response<InputUpdateResponse> response) {
                Toast.makeText(ScreenReportByArea.this,response.body().getMessage(),Toast.LENGTH_SHORT).show();
                bindingData();
                //                onBackPressed();
            }

            @Override
            public void onFailure(Call<InputUpdateResponse> call, Throwable t) {
                Toast.makeText(ScreenReportByArea.this,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        return;
    }


    private void reloadData() {
        RetrofitClient.INSTANCE.getInstance().getPackList(inputCode).enqueue(new Callback<PackingListResponse>(){

            @Override
            public void onResponse(Call<PackingListResponse> call, Response<PackingListResponse> response) {
                if(response.body().getStatus() == 1) {
                    List<MaterialInput> materials = response.body().getMaterial();

                }
            }

            @Override
            public void onFailure(Call<PackingListResponse> call, Throwable t) {
                Toast.makeText(ScreenReportByArea.this,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
     * Khai bao broadcast - Lắng nghe sự kiện khi có thay đổi
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
                    for (int i = 0; i < lstAreaCode.size(); i++) {
                        if (lstAreaCode.get(i).equals(qrCodeStr)) {
                            stringQRCode = lstAreaCode.get(i);
                        }
                    }
                    int spinnerPosition = lstAreaCode.indexOf(stringQRCode);
                    if (spinnerPosition == -1) {
                        Toast.makeText(ScreenReportByArea.this, "Vị trí này không tồn tại", Toast.LENGTH_SHORT).show();
                    }else {
                        Spinner dropdown = findViewById(R.id.spinnerReportArea);
                        dropdown.setSelection(spinnerPosition);
                    }
                    return;
                }

                if (!qrCodeStr.contains(material.getMaterialCode()) && qrCodeStr.contains("@") && material.getTypeMaterial() == true) {
                    Toast.makeText(ScreenReportByArea.this, "Vật tư không tồn tại!!!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (qrCodeStr.contains("@") && material.getTypeMaterial() == true){
                    Toast.makeText(ScreenReportByArea.this, "Vật tư không tồn tại!!!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!qrCodeStr.contains(material.getMaterialCode()) && material.getTypeMaterial() == false){
                    Toast.makeText(ScreenReportByArea.this, "Vật tư không tồn tại!!!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!qrCodeStr.contains("@") && material.getTypeMaterial() == false) {
                    Toast.makeText(ScreenReportByArea.this, "Vật tư không tồn tại!!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                EditText edtNumberDone = (EditText) findViewById(R.id.edtReportNumberDone);
                String numberDone = edtNumberDone.getText().toString();
                int intNumberDone;
                if (numberDone.isEmpty()) {
                    intNumberDone = 0;
                } else {
                    intNumberDone = Integer.parseInt(numberDone);
                }
                intNumberDone++;
                edtNumberDone.setText(String.valueOf(intNumberDone));
                lstSerialCode.add(qrCodeStr);
            }
        }
    };

    /*
    * Đăng ký lắng nghe sự kiện Scan từ máy
    * Created by tupt - 20230317
    * */
    public void registerScannerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RECEIVE_DATA);
        filter.addAction(ACTION_RECEIVE_DATABYTES);
        filter.addAction(ACTION_RECEIVE_DATALENGTH);
        filter.addAction(ACTION_RECEIVE_DATATYPE);
        registerReceiver(mScanReceiver,filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mScanReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerScannerReceiver();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mScanReceiver);
        super.onDestroy();
    }
}