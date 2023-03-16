package com.example.cspart;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cspart.models.Material;
import com.example.cspart.models.MaterialImage;
import com.example.cspart.models.MaterialInput;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import com.zebra.adc.decoder.Barcode2DWithSoft;

import java.util.ArrayList;
import java.util.List;

public class MaterialInputDetail extends AppCompatActivity {

    MaterialInput material;
    Barcode2DWithSoft barcode2DWithSoft=null;
    ArrayList<String> listSerialCode = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_input_detail);

    }

    private void bindingData() {
        try {
            material = (MaterialInput) getIntent().getSerializableExtra("item");
            TextView txtNameMaterial =  (TextView)findViewById(R.id.txtNameMaterial);
            txtNameMaterial.setText(material.getMaterialName());
            EditText edtMaterialName = (EditText) findViewById(R.id.edtMaterialName);
            edtMaterialName.setText(material.getMaterialName());
            EditText edtMaterialCode = (EditText) findViewById(R.id.edtMaterialCode);
            edtMaterialCode.setText(material.getMaterialCode());
            TextView txtNumber = (TextView) findViewById(R.id.txtNumber);
            txtNumber.setText("Số lượng: " + material.getQuantityInput().toString() + "/" + material.getQuantityRequest().toString());
            EditText edtMaterialNumber = (EditText) findViewById(R.id.edtMaterialNumber);
            edtMaterialNumber.setText(material.getQuantityInput().toString());
        } catch (Exception e) {
            // This will catch any exception, because they are all descended from Exception
            System.out.println("Error " + e.getMessage());
        }

    }

    /**
     * Khởi tạo các hành động
     * Created by tupt
     */
    private void initAction(){

    }



    public void CustomDialog() {
        final Dialog customDialog = new Dialog(MaterialInputDetail.this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.custom_dialog_image);
        customDialog.setTitle("");

        Button nextBtn = (Button) customDialog.findViewById(R.id.nextbtnImage);
        Button backBtn = (Button) customDialog.findViewById(R.id.backbtnImage);
        Button closeBtn = (Button) customDialog.findViewById(R.id.closeDialogImage);

        nextBtn.setEnabled(true);
        backBtn.setEnabled(true);
        closeBtn.setEnabled(true);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.cancel();
            }
        });

        customDialog.show();
    }


    /**
     * Lưu thông tin dữ liệu vật tư
     * Created by tupt
     */
    private void save(){

    }

    @Override
    protected void onDestroy() {
        if(barcode2DWithSoft!=null){
            barcode2DWithSoft.stopScan();
            barcode2DWithSoft.close();
        }
        super.onDestroy();
    }


    private  class ScanBack implements  Barcode2DWithSoft.ScanCallback{
        @Override
        public void onScanComplete(int i, int length, byte[] bytes) {
            if (length < 1) {
                if (length == -1) {
                    Toast.makeText(MaterialInputDetail.this,"Bị dừng scan giữa chừng",Toast.LENGTH_SHORT).show();
                } else if (length == 0) {
                    Toast.makeText(MaterialInputDetail.this,"Thời gian scan bị dài",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MaterialInputDetail.this,"Scan lỗi",Toast.LENGTH_SHORT).show();
                }
            }else{
                String barCode = new String(bytes, 0, length);
                int count = 0;
                for (int index = 0; index < listSerialCode.size(); index ++) {
                    if (barCode == listSerialCode.get(i)){
                        count = count + 1;
                    }
                }
                if (count == 0) {
                    listSerialCode.add(barCode);
                }
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
                reuslt=  barcode2DWithSoft.open(MaterialInputDetail.this);
                barcode2DWithSoft.setScanCallback(new MaterialInputDetail.ScanBack());
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
                Toast.makeText(MaterialInputDetail.this,"Fail", Toast.LENGTH_SHORT).show();
            }
        }
    }
}