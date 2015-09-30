package com.soctec.soctec.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
/**
 * Created by MSI on 2015-09-21.
 */
public class ScanActivity extends Activity implements ZBarScannerView.ResultHandler
{
    private ZBarScannerView mScannerView;

    @Override
    public void onCreate(Bundle state)
    {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    /**
     * Called when activity is resumed.
     */
    @Override
    public void onResume()
    {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    /**
     * Called when activity is paused.
     */
    @Override
    public void onPause()
    {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    /**
     * When a QR-code is found and decrypted by mScannerView, the result (the code) is handled here.
     * @param rawResult The raw result from the scanner, which content is the code.
     */
    @Override
    public void handleResult(Result rawResult)
    {
        System.out.println(rawResult.getContents());

        //Send scanned code back
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", rawResult.getContents());
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}