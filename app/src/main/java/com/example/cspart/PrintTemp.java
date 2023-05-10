package com.example.cspart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.cspart.common.Ultis;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PrintTemp extends AppCompatActivity {

    private Connection connection;
    private Integer typeOfQR = 0;
    final static String ACTION_RECEIVE_DATA = "unitech.scanservice.data";
    final static String ACTION_RECEIVE_DATABYTES = "unitech.scanservice.databyte";
    final static String ACTION_RECEIVE_DATALENGTH = "unitech.scanservice.datalength";
    final static String ACTION_RECEIVE_DATATYPE = "unitech.scanservice.datatype";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_temp);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.rtcimg);
        getSupportActionBar().setTitle("  CS PART");

        initAction();
        registerScannerReceiver();
    }

    private void initAction() {
        Button btnRePrint = (Button) findViewById(R.id.btnRePrint);
        btnRePrint.setOnClickListener(new RePrint());
    }

    private class RePrint implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            try {
                connection = getZebraPrinterConn();
                connection.open();
                ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
                getPrinterStatus();
                PrinterStatus printerStatus = printer.getCurrentStatus();
                EditText edtQRCode = (EditText) findViewById(R.id.edtQRCodePrint);
                String serialCode = edtQRCode.getText().toString();
                if(typeOfQR == 0) {
                    EditText edtNumberTo = (EditText) findViewById(R.id.edtNumberTo);
                    String strIndexStart = edtNumberTo.getText().toString();
                    EditText edtNumberFrom = (EditText) findViewById(R.id.edtNumberFrom);
                    String strIndexEnd = edtNumberFrom.getText().toString();
                    if (strIndexStart.isEmpty()){
                        Toast.makeText(PrintTemp.this, "Vui lòng nhập thông tin số bắt đầu", Toast.LENGTH_LONG).show();
                        return;
                    }
                    int indexStart = Integer.parseInt(strIndexStart);
                    int indexEnd = Integer.parseInt(strIndexEnd);

                    if (indexStart > indexEnd) {
                        Toast.makeText(PrintTemp.this, "Số từ phải nhỏ hơn số đến", Toast.LENGTH_LONG).show();
                        return;
                    }
                    int count = 0;
                    String zplBitmap = "";
                    for (int i = indexStart; i <= indexEnd; i++) {
                        QRCodeWriter writer = new QRCodeWriter();
                        String serialCodeTypeBig = "";
                        if(i < 10) {
                            serialCodeTypeBig = serialCode + "@0" + i;
                        } else {
                            serialCodeTypeBig = serialCode + "@" + i;
                        }

                        BitMatrix bitMatrix = writer.encode(serialCodeTypeBig, BarcodeFormat.QR_CODE, 115, 115);
                        int width = bitMatrix.getWidth();
                        int height = bitMatrix.getHeight();
                        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                        for (int x = 0; x < width; x++) {
                            for (int y = 0; y < height; y++) {
                                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                            }
                        }
                        String zplBitmapCode = Ultis.getZplCode(bmp, false);
                        if(count%2 == 0 && i == indexEnd) {
                            zplBitmapCode = "^XA " + zplBitmapCode + " ^XZ";
//                            Toast.makeText(PrintTemp.this, zplBitmapCode, Toast.LENGTH_LONG).show();
                            printPhotoFromExternal(bmp, printerStatus, printer, zplBitmapCode, connection);
                        }
                        if (count%2 == 0 && i != indexEnd) {
                            zplBitmap = "^XA " + zplBitmapCode;
                        } else if(count%2 == 1) {
                            zplBitmap = zplBitmap + "^FO290,0^FD " + zplBitmapCode + "^FS ^XZ";
//                            Toast.makeText(PrintTemp.this, zplBitmapCode, Toast.LENGTH_LONG).show();
                            printPhotoFromExternal(bmp, printerStatus, printer, zplBitmapCode, connection);
                        }
                        count++;
                    }
                } else {
                    QRCodeWriter writer = new QRCodeWriter();
                    BitMatrix bitMatrix = writer.encode(serialCode, BarcodeFormat.QR_CODE, 115, 115);
                    int width = bitMatrix.getWidth();
                    int height = bitMatrix.getHeight();
                    Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                        }
                    }
                    String zplBitmapCode = Ultis.getZplCode(bmp, false);
                    zplBitmapCode = "^XA " + zplBitmapCode + " ^XZ";
//                    Toast.makeText(PrintTemp.this, zplBitmapCode, Toast.LENGTH_LONG).show();
                    printPhotoFromExternal(bmp, printerStatus, printer, zplBitmapCode, connection);
                }
                connection.close();
            } catch (ZebraPrinterLanguageUnknownException e) {
//                Toast.makeText(MaterialDetail.this, "Lỗi ngôn ngữ:" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            catch (Exception e) {
                System.out.println("Error " + e.getMessage());
            }
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.btnTypeBig:
                if (checked) {
                    EditText edtNumberTo = (EditText) findViewById(R.id.edtNumberTo);
                    edtNumberTo.setEnabled(true);
                    EditText edtNumberFrom = (EditText) findViewById(R.id.edtNumberFrom);
                    edtNumberFrom.setEnabled(true);
                    typeOfQR = 0;
                    break;
                }
            case R.id.btnTypeSmall:
                if (checked) {
                    EditText edtNumberTo = (EditText) findViewById(R.id.edtNumberTo);
                    edtNumberTo.setEnabled(false);
                    EditText edtNumberFrom = (EditText) findViewById(R.id.edtNumberFrom);
                    edtNumberFrom.setEnabled(false);
                    typeOfQR = 1;
                    break;
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
                EditText editTextRequestCode = (EditText)findViewById(R.id.edtQRCodePrint);
                editTextRequestCode.setText(qrCodeStr);
            }
        }
    };

    private Connection getZebraPrinterConn() {
        return new BluetoothConnection("48a49369c705");
    }

    private void getPrinterStatus() throws ConnectionException {
        final String printerLanguage = SGD.GET("device.languages", connection);

        final String displayPrinterLanguage = "Printer Language is " + printerLanguage;

        SGD.SET("device.languages", "zpl", connection);

        PrintTemp.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PrintTemp.this, displayPrinterLanguage + "\n" + "Language set to ZPL", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void printPhotoFromExternal(final Bitmap bitmap, PrinterStatus printerStatus, ZebraPrinter printer, String zplBitmap, Connection connection1) {

        if (printerStatus.isReadyToPrint) {
            try {
//                    printer.printImage(new ZebraImageAndroid(bitmap), 0, 0, 100, 100, false);
                connection1.write(zplBitmap.getBytes());
            } catch (ConnectionException e) {
                Toast.makeText(PrintTemp.this, "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else if (printerStatus.isHeadOpen) {
            Toast.makeText(PrintTemp.this, "Vui lòng đóng phần đầu máy in", Toast.LENGTH_LONG).show();
        } else if (printerStatus.isPaused) {
            Toast.makeText(PrintTemp.this, "Máy in đang tạm dừng", Toast.LENGTH_LONG).show();
        } else if (printerStatus.isPaperOut) {
            Toast.makeText(PrintTemp.this, "Máy in hết giấy", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(PrintTemp.this, "Lỗi không xác định", Toast.LENGTH_LONG).show();
        }

    }

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