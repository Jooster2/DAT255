package com.soctec.soctec.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
/**
 * Scans QR-codes and returns the result via the Intent
 *
 * Uses the following libraries which is under Apache License version 2.0:
 *
 *  me.dm7.barcodescanner.zbar.Result;
 *  me.dm7.barcodescanner.zbar.ZBarScannerView;
 *
 * See license in License.txt in the project root.
 *
 * @author Carl-Henrik Hult
 * @version 1.0
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
        mScannerView.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeLeft()
            {
                finish();
            }
        });
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

    /**
     * Detects left and right swipes across a view.
     */
    public class OnSwipeTouchListener implements View.OnTouchListener
    {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context context) {
            gestureDetector = new GestureDetector(context, new GestureListener());
        }

        public void onSwipeLeft() {

        }

        public void onSwipeRight() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener
        {

            private static final int SWIPE_DISTANCE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float distanceX = e2.getX() - e1.getX();
                float distanceY = e2.getY() - e1.getY();
                if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (distanceX > 0)
                        onSwipeRight();
                    else
                        onSwipeLeft();
                    return true;
                }
                return false;
            }
        }
    }
}