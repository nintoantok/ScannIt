package com.hoffenglobal.scannit;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hoffenglobal.scannit.Utils.DummyProducts;
import com.hoffenglobal.scannit.Utils.MyDialog;
import com.hoffenglobal.scannit.models.Product;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ListView productListView;
    List<String> productDisplayList = new ArrayList<>();
    List<Product> productsList = new ArrayList<>();
    ArrayAdapter<String> listAdapter;

    HashMap<String, String> productBarCodeMapping;

    private WritableCellFormat timesBoldUnderline;
    private WritableCellFormat times;
    private File inputFile;

    private Button clearButton;
    private Button exportButton;

    private Button addProductButton;
    private MyDialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productBarCodeMapping = DummyProducts.getProducts();

        productListView = (ListView) findViewById(R.id.product_list);

        clearButton = (Button) findViewById(R.id.clear_button);
        exportButton = (Button) findViewById(R.id.export_button);

        addProductButton = (Button) findViewById(R.id.add_button);

        listAdapter = new ArrayAdapter<>(this, R.layout.product_item, productDisplayList);
        productListView.setAdapter(listAdapter);


        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productDisplayList.clear();
                productsList.clear();
                listAdapter.notifyDataSetChanged();

            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportProducts();
            }
        });

        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanMethod();
            }
        });

    }

    private void exportProducts() {


        showProgressDialog();
        if(isExternalStorageWritable()){
            Log.d(LOG_TAG, "Writable ");
        } else {
            Log.d(LOG_TAG, "Not Writable ");
        }
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "ExportData");
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }else {
            Log.e(LOG_TAG, "Directory created");
        }
//        return file;
//        setOutputFile(file);
        try {
            write(file);
            Toast.makeText(this, "Data Exported", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }

        dismissProgressDialog();

    }

    public void scanMethod(){

        new IntentIntegrator(this).initiateScan();

    }

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
//                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                Log.d("Product_TAG", "Product Id : " + result.getContents());



                //Create a new Product as well
                if(productBarCodeMapping.get(result.getContents()) != null){

                    productDisplayList.add(productBarCodeMapping.get(result.getContents()));

                    Product newProd = new Product(result.getContents(), productBarCodeMapping.get(result.getContents()));
                    productsList.add(newProd);
                } else {
                    productDisplayList.add("Product - " + result.getContents());

                    Product newProd = new Product(result.getContents(), "Product - " + result.getContents());
                    productsList.add(newProd);
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void write(File file) throws IOException, WriteException {

        File excelFile = new File(file,"ProductList.xls");
        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("en", "EN"));

        WritableWorkbook workbook = Workbook.createWorkbook(excelFile, wbSettings);
        workbook.createSheet("Report", 0);
        WritableSheet excelSheet = workbook.getSheet(0);
        createLabel(excelSheet);
        createContent(excelSheet);

        workbook.write();
        workbook.close();
    }


    private void createLabel(WritableSheet sheet)
            throws WriteException {
        // Lets create a times font
        WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
        // Define the cell format
        times = new WritableCellFormat(times10pt);
        // Lets automatically wrap the cells
        times.setWrap(true);

        // create create a bold font with unterlines
        WritableFont times10ptBoldUnderline = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false,
                UnderlineStyle.SINGLE);
        timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
        // Lets automatically wrap the cells
        timesBoldUnderline.setWrap(true);

        CellView cv = new CellView();
        cv.setFormat(times);
        cv.setFormat(timesBoldUnderline);
        cv.setAutosize(true);

        // Write a few headers
        addCaption(sheet, 0, 0, "ID");
        addCaption(sheet, 1, 0, "Product");
    }

    private void addCaption(WritableSheet sheet, int column, int row, String s)
            throws RowsExceededException, WriteException {
        Label label;
        label = new Label(column, row, s, timesBoldUnderline);
        sheet.addCell(label);
    }

    private void createContent(WritableSheet sheet) throws WriteException,
            RowsExceededException {
        // Write a few number
        for (int i = 1; i <= productsList.size(); i++) {
            // First column
            addLabel(sheet, 0, i, productsList.get(i-1).getProductId());
            // Second column
            addLabel(sheet, 1, i, productsList.get(i-1).getProductName());
        }
    }




    private void addLabel(WritableSheet sheet, int column, int row, String s)
            throws WriteException, RowsExceededException {
        Label label;
        label = new Label(column, row, s, times);
        sheet.addCell(label);
    }


    public void showProgressDialog() {

        myDialog = new MyDialog(this);
        myDialog.show();
    }

    public void dismissProgressDialog() {
        if (myDialog != null) {
            myDialog.dismiss();
            myDialog = null;
        }
    }

    private void finishCurrentActivity() {
        if (myDialog != null) {
            myDialog.dismiss();
            myDialog = null;
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        if (myDialog != null) {
            myDialog.dismiss();
            myDialog = null;
        }
        super.onDestroy();
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
