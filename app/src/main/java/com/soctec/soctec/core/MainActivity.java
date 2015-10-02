package com.soctec.soctec.core;

import java.util.Locale;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soctec.soctec.R;
import com.soctec.soctec.achievements.AchievementCreator;
import com.soctec.soctec.achievements.AchievementUnlocker;
import com.soctec.soctec.achievements.Stats;
import com.soctec.soctec.network.ConnectionChecker;
import com.soctec.soctec.network.NetworkHandler;
import com.soctec.soctec.profile.Profile;
import com.soctec.soctec.profile.ProfileActivity;

/**
 * MainActivity is a tabbed activity, and sets up most of the other objects for the App
 * @author Jesper, Joakim, David
 * @version 1.1
 */
public class MainActivity extends AppCompatActivity implements ActionBar.TabListener
{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    ConnectionChecker connectionChecker = null;

    private Stats stats;
    private AchievementCreator creator;
    private AchievementUnlocker unlocker;


    private static int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String account = getPlayAcc();
        if(account == null)
            account = "walla";
        //TODO crash and burn (handle this some way...)

        //String userCode = new Encryptor().encrypt(account);
        Profile.setUserCode(account);

        //Initialize the FileHandler
        FileHandler.getInstance().setContext(this);

        //Initialize the Achievement engine
        stats = new Stats();
        creator = new AchievementCreator(this);
        unlocker = new AchievementUnlocker(stats, creator);
        creator.addObserver(unlocker);
        creator.createFromFile();

        //Initialize networkHandler. Start server thread
        NetworkHandler.getInstance(this);

        //Initialize the ActionBar
        setupActionBar();
    }

    /**
     * Initiate the BroadcastReceiver and register it.
     */
    public void startReceiver()
    {
        IntentFilter intentFilter =
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(
                connectionChecker = new ConnectionChecker(MainActivity.this), intentFilter);

        Log.i("Main", "Receiver started!");
    }

    /**
     * Tells ScanActivity to start scanning
     * @param v
     */
    public void scanNow(View v)
    {
        startActivityForResult(
                new Intent(getApplicationContext(), ScanActivity.class), REQUEST_CODE);
    }

    /**
     * Called when an activity started with "startActivityForResult()" has finished.
     * @param requestCode Indicates which request this method call is a response to
     * @param resultCode The result of the request
     * @param data The data from the finished activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE)
        {
            String scannedCode = data.getExtras().getString("result");
            NetworkHandler.getInstance(this).sendScanInfoToPeer(scannedCode);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        NetworkHandler.getInstance(this).stopConnectionListener(); //TODO: Is this working??????
        unregisterReceiver(connectionChecker);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        NetworkHandler.getInstance(this).startConnectionListener();

        IntentFilter intentFilter =
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(connectionChecker, intentFilter);
    }

    /**
     * Called by NetworkHandler when data has been received from the server.
     * @param dataFromServer Contains user profile data
     */
    public void receiveDataFromServer(String dataFromServer)
    {
    }

    /**
     * Called by NetworkHandler when data has been received from a peer.
     * @param idFromPeer The unique ID of the peer
     * @param profileFromPeer The peer's profile data
     */
    public void receiveDataFromPeer(String idFromPeer, String profileFromPeer)
    {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(100);

        unlocker.receiveEvent(1, idFromPeer);
        String achievement = stats.getlastScanned();
        Toast.makeText(getApplicationContext(), achievement, Toast.LENGTH_LONG).show();
        String time = String.valueOf(stats.getTimeTalked());
        Toast.makeText(getApplicationContext(), time, Toast.LENGTH_LONG).show();
    }

    /**
     * Update the QR image
     * @param QR
     */
    public void updateQR(Bitmap QR)
    {
        Log.i("MainFrag", "Update qr");

        ImageView im = (ImageView) findViewById(R.id.qr_image);
        im.setImageBitmap(QR);
    }

    /**
     * Returns the device's google account name
     * @return the device's google account name
     */
    private String getPlayAcc()
    {
        String accMail = null;

        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();

        for(Account account: list)
        {
            if(account.type.equalsIgnoreCase("com.google")) //TODO Felhatering vid avsaknad av gmail-konto
            {
                accMail = account.name;
                break;
            }
        }
        return accMail;
    }

    //--------------- Below is all auto-generated code from ActionBar --------------------

    /**
     * Sets up the ActionBar
     */
    private void setupActionBar()
    {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
        {
            @Override
            public void onPageSelected(int position)
            {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for(int i = 0; i < mSectionsPagerAdapter.getCount(); i++)
        {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings)
        {
            return true;
        }
        else if(id == R.id.action_profile)
        {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
            switch(position)
            {
                case 0:
                    return new MainFragment();
                case 1:
                    return new AchievementsFragment();
                //case 2:
                //    return new LeftFragment();
            }
            return null;
        }

        @Override
        public int getCount()
        {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            Locale l = Locale.getDefault();
            switch(position)
            {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                //case 2:
                //    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

}
