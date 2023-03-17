package com.example.cspart;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cspart.api.RetrofitClient;
import com.example.cspart.common.Ultis;
import com.example.cspart.models.ExportWareHouseDataRequest;
import com.example.cspart.models.Material;
import com.example.cspart.models.MaterialImage;
import com.example.cspart.models.UpdateExportWareHouseResponse;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.squareup.picasso.Picasso;
import com.zebra.adc.decoder.Barcode2DWithSoft;

//Thu vien ket noi bluetooth
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.graphics.internal.ZebraImageAndroid;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
import com.zebra.sdk.printer.ZebraPrinterLinkOs;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaterialDetail extends AppCompatActivity {

    Material material;
    private Connection connection;
    Barcode2DWithSoft barcode2DWithSoft=null;
    ArrayList<String> listSerialCode = new ArrayList<String>();
    List<MaterialImage> lstImage;
    int indexImage = 0;
    int lengthImage = 0;
    int lengthQR = 0;
    private static final String[] paths = {"Loại lớn", "Loại nhỏ"};
    final static String START_SCANSERVICE = "unitech.scanservice.start";
    final static String SCANNER_INIT = "unitech.scanservice.init";
    final static String SCAN2KEY_SETTING = "unitech.scanservice.scan2key_setting";
    final static String SOFTWARE_SCANKEY = "unitech.scanservice.software_scankey";
    final static String CLOSE_SCANSERVICE = "unitech.scanservice.close";
    final static String ACTION_RECEIVE_DATA = "unitech.scanservice.data";
    final static String ACTION_RECEIVE_DATABYTES = "unitech.scanservice.databyte";
    final static String ACTION_RECEIVE_DATALENGTH = "unitech.scanservice.datalength";
    final static String ACTION_RECEIVE_DATATYPE = "unitech.scanservice.datatype";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_detail);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.rtcimg);
        getSupportActionBar().setTitle("  CS PART");
        bindingData();
        initAction();
//        barcode2DWithSoft=Barcode2DWithSoft.getInstance();
//        LayoutInflater layoutinflater = getLayoutInflater();
//        new InitTask().execute();
//        registerScannerReceiver();
    }

    /**
     * Điền thông tin dữ liệu
     * Created by tupt
     */
    private void bindingData() {
        try {
            material = (Material) getIntent().getSerializableExtra("item");
            String requestCode = getIntent().getStringExtra("requestCode");
            requestCode = "Mã phiếu: " + requestCode;
            TextView txtNameMaterial =  (TextView)findViewById(R.id.txtNameMaterial);
            txtNameMaterial.setText(requestCode);
            EditText edtMaterialName = (EditText) findViewById(R.id.edtMaterialName);
            edtMaterialName.setText(material.getMaterialName());
            edtMaterialName.setEnabled(false);
            EditText edtMaterialCode = (EditText) findViewById(R.id.edtMaterialCode);
            edtMaterialCode.setText(material.getMaterialCode());
            edtMaterialCode.setEnabled(false);
            TextView txtNumber = (TextView) findViewById(R.id.txtNumber);
            txtNumber.setText("Số lượng: " + material.getQuantityGet().toString() + "/" + material.getQuantityRequest().toString());
            EditText edtMaterialNumber = (EditText) findViewById(R.id.edtMaterialNumber);
            edtMaterialNumber.setText(material.getQuantityGet().toString());
            Spinner spinnerDrop = (Spinner) findViewById(R.id.edtType);
            ArrayAdapter<String>adapter = new ArrayAdapter<String>(MaterialDetail.this,
                    android.R.layout.simple_spinner_item,paths);
            spinnerDrop.setAdapter(adapter);
            if (material.getTypeMaterial() == false) {
                spinnerDrop.setSelection(0);
            } else {
                spinnerDrop.setSelection(1);
                EditText edtPrint = (EditText) findViewById(R.id.edtPrint);
                edtPrint.setText("1");
                edtPrint.setEnabled(false);
            }
            spinnerDrop.setEnabled(false);
            lstImage = material.getMaterialImage();
            lengthImage = lstImage.size();
            lengthQR = material.getSerialCode().size();
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
        Button btnPrint = (Button) findViewById(R.id.btnSaveReport);
        btnPrint.setOnClickListener(new GoToScreenPrint());

        Button btnImage = (Button) findViewById(R.id.btnViewImage);
        btnImage.setOnClickListener(new GoToScreenViewInmage());

        Button btnSave = (Button) findViewById(R.id.btnSaveMDetail);
        btnSave.setOnClickListener(new SaveDetail());
    }

    public class TestA implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (!"AWW004A787C0-1C5".contains(material.getMaterialCode()) && material.getTypeMaterial() == true){
                Toast.makeText(MaterialDetail.this, "Vật tư không tồn tại!!!", Toast.LENGTH_SHORT).show();
                return;
            } else if (!"AWW004A787C0-1C5".contains(material.getMaterialCode()) && !"AWW004A787C0-1C5".contains("@") && material.getTypeMaterial() == false) {
                Toast.makeText(MaterialDetail.this, "Vật tư không tồn tại!!!", Toast.LENGTH_SHORT).show();
                return;
            }
        }


    }


    private class SaveDetail implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Save();
        }
    }

    private void Save(){
        SharedPreferences sharedPreferences = getSharedPreferences("my_shared_preff", 0);
        String userName = sharedPreferences.getString("code","");
        EditText edtMaterialNumber = (EditText) findViewById(R.id.edtMaterialNumber);
        String numberDone = edtMaterialNumber.getText().toString();
        int intNumberDone = Integer.parseInt(numberDone);
        int intNUmberRequest = material.getQuantityRequest();
        if (intNumberDone > intNUmberRequest) {
            Toast.makeText(MaterialDetail.this,"Số lượng nhập lớn hơn yêu cầu",Toast.LENGTH_SHORT).show();
            edtMaterialNumber.setBackgroundResource(R.drawable.error_background);
            return;
        }else {
            edtMaterialNumber.setBackgroundResource(R.drawable.normal_background);
            ExportWareHouseDataRequest dataRequest = new ExportWareHouseDataRequest(
                    material.getRequestCode(),
                    material.getMaterialCode(),
                    intNumberDone,
                    listSerialCode,
                    userName
            );
            RetrofitClient.INSTANCE.getInstance().updateExportWarehouse(dataRequest).enqueue(new Callback<UpdateExportWareHouseResponse>() {
                @Override
                public void onResponse(Call<UpdateExportWareHouseResponse> call, Response<UpdateExportWareHouseResponse> response) {
                    Toast.makeText(MaterialDetail.this,response.body().getMessage(),Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }

                @Override
                public void onFailure(Call<UpdateExportWareHouseResponse> call, Throwable t) {
                    Toast.makeText(MaterialDetail.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Chuyển sang màn hình in tem phiếu
     * Overrider lại hàm OnClickListener
     * Created by tupt
     */
    public class GoToScreenPrint implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            try {
                List<String> lstSerialCode = material.getSerialCode();
                connection = getZebraPrinterConn();
                connection.open();
                ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
                getPrinterStatus();
                PrinterStatus printerStatus = printer.getCurrentStatus();
                EditText edtPrint = (EditText) findViewById(R.id.edtPrint);
                int numberPrint = Integer.parseInt(edtPrint.getText().toString());
                for (int i =0; i < numberPrint; i++) {
                    String serialCode = lstSerialCode.get(i);
                    QRCodeWriter writer = new QRCodeWriter();
                    try {
                        BitMatrix bitMatrix = writer.encode(serialCode, BarcodeFormat.QR_CODE, 130, 130);
                        int width = bitMatrix.getWidth();
                        int height = bitMatrix.getHeight();
                        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                        for (int x = 0; x < width; x++) {
                            for (int y = 0; y < height; y++) {
                                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                            }
                        }
                        String zplBitmap = Ultis.getZplCode(bmp, false);
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        Date date = new Date();
                        String dateString = formatter.format(date);
                        zplBitmap = "^XA " + zplBitmap + "  ^CF0,25^FO120,30^FD" + material.getMaterialName() + "^FS ^FO120,55^FD" + serialCode + "^FS" + "^FO120,80^FD" + dateString +"^FS ^XZ";
                        printPhotoFromExternal(bmp, printerStatus, printer, zplBitmap, connection);
                        listSerialCode.add(serialCode);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
                connection.close();
                Save();
            } catch (ConnectionException e) {
                Toast.makeText(MaterialDetail.this, "Lỗi kết nối:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (ZebraPrinterLanguageUnknownException e) {
//                Toast.makeText(MaterialDetail.this, "Lỗi ngôn ngữ:" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            catch (Exception e) {
                System.out.println("Error " + e.getMessage());
            }
        }
    }

    public class GoToScreenViewInmage implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            try {
                CustomDialog();
            }
            catch (Exception e) {
                System.out.println("Error " + e.getMessage());
            }
        }
    }

    public void CustomDialog() {
        final Dialog customDialog = new Dialog(MaterialDetail.this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.custom_dialog_image);
        customDialog.setTitle("");

        Button nextBtn = (Button) customDialog.findViewById(R.id.nextbtnImage);
        Button backBtn = (Button) customDialog.findViewById(R.id.backbtnImage);
        Button closeBtn = (Button) customDialog.findViewById(R.id.closeDialogImage);

        if (lstImage.size() > 0) {
            MaterialImage materialImage = lstImage.get(indexImage);
            indexImage++;
            String url = materialImage.getImageLink();
            if (url != null) {
                PhotoView img = customDialog.findViewById(R.id.imgCustom);
                Picasso.get().load(url).into(img);
            }
        }

        nextBtn.setEnabled(true);
        backBtn.setEnabled(true);
        closeBtn.setEnabled(true);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage(customDialog,0);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage(customDialog, 1);
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

    private void loadImage(Dialog dialog, int typeBtn) {
        if (typeBtn == 0) {
            if (indexImage < lengthImage) {
                indexImage++;
                MaterialImage materialImage = lstImage.get(indexImage);
                String url = materialImage.getImageLink();
                if (url != null) {
                    PhotoView img = dialog.findViewById(R.id.imgCustom);
                    Picasso.get().load(url).into(img);
                }
            }
        } else {
            if (indexImage > 0) {
                indexImage = indexImage - 1;
                MaterialImage materialImage = lstImage.get(indexImage);
                String url = materialImage.getImageLink();
                if (url != null) {
                    PhotoView img = dialog.findViewById(R.id.imgCustom);
                    Picasso.get().load(url).into(img);
                }
            }
        }
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
                    Toast.makeText(MaterialDetail.this,"Bị dừng scan giữa chừng",Toast.LENGTH_SHORT).show();
                } else if (length == 0) {
                    Toast.makeText(MaterialDetail.this,"Thời gian scan bị dài",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MaterialDetail.this,"Scan lỗi",Toast.LENGTH_SHORT).show();
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
                    material.setQuantityGet(material.getQuantityGet() + 1);
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
                reuslt=  barcode2DWithSoft.open(MaterialDetail.this);
                barcode2DWithSoft.setScanCallback(new MaterialDetail.ScanBack());
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
                Toast.makeText(MaterialDetail.this,"Fail", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /*
     * Print QRCode
     * @bitmap: bitmap qrcode
     * */
    private void printPhotoFromExternal(final Bitmap bitmap, PrinterStatus printerStatus, ZebraPrinter printer, String zplBitmap, Connection connection1) {

            if (printerStatus.isReadyToPrint) {
                try {
//                    printer.printImage(new ZebraImageAndroid(bitmap), 0, 0, 100, 100, false);
                    connection1.write(zplBitmap.getBytes());
                } catch (ConnectionException e) {
                    Toast.makeText(MaterialDetail.this, "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else if (printerStatus.isHeadOpen) {
                Toast.makeText(MaterialDetail.this, "Vui lòng đóng phần đầu máy in", Toast.LENGTH_LONG).show();
            } else if (printerStatus.isPaused) {
                Toast.makeText(MaterialDetail.this, "Máy in đang tạm dừng", Toast.LENGTH_LONG).show();
            } else if (printerStatus.isPaperOut) {
                Toast.makeText(MaterialDetail.this, "Máy in hết giấy", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MaterialDetail.this, "Lỗi không xác định", Toast.LENGTH_LONG).show();
            }

    }

    private Connection getZebraPrinterConn() {
        return new BluetoothConnection("48a49369c705");
    }

    private void getPrinterStatus() throws ConnectionException {
        final String printerLanguage = SGD.GET("device.languages", connection);

        final String displayPrinterLanguage = "Printer Language is " + printerLanguage;

        SGD.SET("device.languages", "zpl", connection);

        MaterialDetail.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MaterialDetail.this, displayPrinterLanguage + "\n" + "Language set to ZPL", Toast.LENGTH_LONG).show();
            }
        });

    }

    /*
     * Khai bao broadcast
     *
     * */
    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Bundle bundle = intent.getExtras();
                if (bundle == null) return;
                String qrCodeStr = bundle.getString("text");
                if (qrCodeStr != "" && qrCodeStr != null) {
                    qrCodeStr = qrCodeStr.trim();

                    if (!qrCodeStr.contains(material.getMaterialCode()) && material.getTypeMaterial() == true){
                        Toast.makeText(MaterialDetail.this, "Vật tư không tồn tại!!!", Toast.LENGTH_SHORT).show();
                        return;
                    } else if ((!qrCodeStr.contains(material.getMaterialCode()) && !qrCodeStr.contains("@")) && material.getTypeMaterial() == false) {
                        Toast.makeText(MaterialDetail.this, "Vật tư không tồn tại!!!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (listSerialCode.size() > 0) {
                        if (listSerialCode.contains(qrCodeStr)) {
                            Toast.makeText(MaterialDetail.this, "Đã tồn tại mã hàng hóa này!!!", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            listSerialCode.add(qrCodeStr);
                        }
                    } else {
                        listSerialCode.add(qrCodeStr);
                    }

                    EditText edtMaterialNumber = (EditText) findViewById(R.id.edtMaterialNumber);
                    String numberDone = edtMaterialNumber.getText().toString();
                    int intNumberDone;
                    if (material.getTypeMaterial() == false) {
                        if (numberDone.isEmpty()) {
                            intNumberDone = 0;
                        } else {
                            intNumberDone = Integer.parseInt(numberDone);
                        }
                        intNumberDone++;
                    } else {
                        intNumberDone = material.getQuantityRequest();
                    }
                    edtMaterialNumber.setText(String.valueOf(intNumberDone));
                    TextView txtNumber = (TextView) findViewById(R.id.txtNumber);
                    txtNumber.setText("Số lượng: " + String.valueOf(intNumberDone) + "/" + material.getQuantityRequest().toString());
                    EditText edtNumberPrint = (EditText) findViewById(R.id.edtPrint);
                    int intNumberValidate = 0;
                    if (edtNumberPrint.getText().toString() != "") {
                        intNumberValidate = Integer.parseInt(edtNumberPrint.getText().toString());
                    }
                    if (material.getTypeMaterial() == false) {
                        int numberGet = material.getQuantityGet();
                        intNumberValidate = numberGet + intNumberValidate;
                        if (intNumberDone > intNumberValidate) {
                            Toast.makeText(MaterialDetail.this, "Số lượng nhập lớn hơn yêu cầu", Toast.LENGTH_SHORT).show();
                            edtMaterialNumber.setBackgroundResource(R.drawable.error_background);
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(MaterialDetail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };

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
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RECEIVE_DATA);
        filter.addAction(ACTION_RECEIVE_DATABYTES);
        filter.addAction(ACTION_RECEIVE_DATALENGTH);
        filter.addAction(ACTION_RECEIVE_DATATYPE);
        registerReceiver(mScanReceiver,filter);
    }
}