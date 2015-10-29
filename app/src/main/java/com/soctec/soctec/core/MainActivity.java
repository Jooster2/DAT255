package com.soctec.soctec.core;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.soctec.soctec.R;
import com.soctec.soctec.achievements.Achievement;
import com.soctec.soctec.achievements.AchievementUnlocker;
import com.soctec.soctec.achievements.Stats;
import com.soctec.soctec.network.ConnectionChecker;
import com.soctec.soctec.network.NetworkHandler;
import com.soctec.soctec.profile.Profile;
import com.soctec.soctec.profile.ProfileActivity;
import com.soctec.soctec.profile.ProfileMatchActivity;
import com.soctec.soctec.utils.APIHandler;
import com.soctec.soctec.utils.Encryptor;
import com.soctec.soctec.utils.FileHandler;

/**
 * MainActivity is a tabbed activity, and sets up most of the other objects for the App
 * @author Jesper, Joakim, David, Carl-Henrik, Robin
 * @version 1.3
 */
public class MainActivity extends AppCompatActivity implements ActionBar.TabListener
{
    public static final boolean TESTING = false;

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    ConnectionChecker connectionChecker = null;

    private Stats stats;
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

        //Initialize the FileHandler
        FileHandler.getInstance().setContext(this);

        //Initialize the Profile
        String userCode = new Encryptor().encrypt(account);
        Profile.setUserCode(userCode);
        Profile.initProfile();

        //Initialize the Achievement engine
        if(!TESTING)
            stats = (Stats)FileHandler.getInstance().readObject("stats.sav");
        if (stats == null)
            stats = new Stats();
        unlocker = new AchievementUnlocker(this, stats);

        //Initialize networkHandler. Start server thread
        NetworkHandler.getInstance().setMyActivity(this);
        NetworkHandler.getInstance().startThread();

        //Initialize the ActionBar
        setupActionBar();
        mViewPager.addOnPageChangeListener(new PageChangeListener());
        mViewPager.setCurrentItem(1);

        //Display help to user. Only on the very first startup
        SharedPreferences preferences = getSharedPreferences("com.soctec.soctec", MODE_PRIVATE);
        if (preferences.getBoolean("first_time", true))
        {
            //Do this only the first startup
            FragmentManager fm = getSupportFragmentManager();
            HelpFragment hFragment = new HelpFragment();
            hFragment.show(fm, "Help", 1);

            preferences.edit().putBoolean("first_time", false).apply();
        }
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
     * @param v currently unused
     */
    public void scanNow (View v)
    {
        startActivityForResult(
                new Intent(getApplicationContext(), ScanActivity.class), REQUEST_CODE);
        updateAchievementFragment();
    }

    /**
     * Initiates refresh of AchievementFragment by retrieving the fragment from
     * mSectionsPagerAdapter and calling the method
     */
    protected void updateAchievementFragment()
    {
        AchievementsFragment aF = (AchievementsFragment)mSectionsPagerAdapter.getFragment(2);
        if (unlocker.getUnlockableAchievements()!= null)
        {
            aF.refreshAchievements(unlocker.getUnlockableAchievements(), stats.getAchievements());
            aF.setPoints(stats.getPoints());
        }
    }

    /**
     * Returns the stats-object
     * @return returns the stats-object
     */
    public Stats getStats()
    {
        return stats;
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
            NetworkHandler.getInstance().sendScanInfoToPeer(scannedCode);
        }
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        //mViewPager.setCurrentItem(1);
    }

    /**
     * Starts NetworkHandler and registers connectionChecker as receiver
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        //Start thread that listens for peer connections
        NetworkHandler.getInstance().startThread();

        //Register broadcast receiver that listens for wifi changes
        IntentFilter intentFilter =
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(connectionChecker, intentFilter);

        //Fetch latest rating statistics from server
        NetworkHandler.getInstance().fetchRatingFromServer();

        //Set current tab
        if (mViewPager.getCurrentItem()== 0)
            mViewPager.setCurrentItem(1);

    }

    /**
     * Saves stats and unlocker-data to respective savefiles
     */
    @Override
    protected void onStop()
    {

        unlocker.saveUnlockable();
        FileHandler.getInstance().writeObject("stats.sav", stats);
        super.onStop();
    }

    /**
     * Stops NetworkHandler and unregisters connectionChecker as receiver
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        NetworkHandler.getInstance().stopThread();
        unregisterReceiver(connectionChecker);
    }

    /**
     * Makes sure the application only exits when in main fragment
     * and back button is pressed. Otherwise switches to main fragment.
     */
    @Override
    public void onBackPressed()
    {
        if (mViewPager.getCurrentItem()==1)
            super.onBackPressed();
        else
            mViewPager.setCurrentItem(1);
    }

    public void updateRatingBar()
    {
        ((MainFragment)mSectionsPagerAdapter.getFragment(1)).updateRatingBar();
    }

    /**
     * Called by NetworkHandler when data has been received from a peer.
     * @param idFromPeer The unique ID of the peer
     * @param profileFromPeer The peer's profile data
     */
    public void receiveDataFromPeer(String idFromPeer, ArrayList<ArrayList<String>> profileFromPeer)
    {
        stats.setCanNotRate(false);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(100);

        //Achievement stuff
        unlocker.receiveEvent(1, idFromPeer);
        checkAchievementChanges();

        //Match profile stuff
        Bundle b = new Bundle();
        b.putSerializable("list1", Profile.getProfile());
        b.putSerializable("list2", profileFromPeer);
        Intent matchIntent = new Intent(this, ProfileMatchActivity.class);
        matchIntent.putExtras(b);
        startActivity(matchIntent);

        //Activate rating thumbs
        if(unlocker.wasLegalScan())
            ((MainFragment)mSectionsPagerAdapter.getFragment(1)).enableRatingButtons();


    }

    public void checkAchievementChanges()
    {
        LinkedList<Achievement> newAchievements = stats.getLastCompleted();
        for(int i = newAchievements.size()-1; i >= 0; i--)
        {
            Intent showerIntent = new Intent(this, AchievementShowerActivity.class);
            showerIntent.putExtra("AchievementObject", newAchievements.get(i));
            showerIntent.putExtra("ScanCount", stats.getScanCount());
            startActivity(showerIntent);
        }
        if(newAchievements.size() != 0)
            updateAchievementFragment();
    }

    /**
     * Orders the APIHandler to read Icomera API
     */
    public void checkIcomera()
    {
        APIHandler.getInstance().readIcomera(this);
    }

    /**
     * Called by APIHandler when it has determined that we are on a bus
     * @param icomeraID the ID read from Icomera API
     */
    public void fromIcomera(String icomeraID)
    {
        FileHandler fH = FileHandler.getInstance();
        int resourceID = fH.getResourceID("SID" + icomeraID, "string");
        if(resourceID != 0)
        {
            String vinNumber = fH.readString(resourceID);
            resourceID = fH.getResourceID(vinNumber, "string");
            if(resourceID != 0)
            {
                String busID = fH.readString(resourceID);
                unlocker.receiveEvent(2, busID);
                checkAchievementChanges();

            }
            unlocker.startLivingDemands();
        }
        else
        {
            unlocker.stopLivingDemands();
        }
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

        for(Account account : list)
        {
            if(account.type.equalsIgnoreCase("com.google")) //TODO Felhatering vid avsaknad av gmail-konto
            {
                accMail = account.name;
                break;
            }
        }
        return accMail;
    }

    public class PageChangeListener implements ViewPager.OnPageChangeListener
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
        }

        @Override
        public void onPageSelected(int position)
        {
            if (position == 0)
            {
                mViewPager.setCurrentItem(0);
                scanNow(null);
                //mViewPager.setCurrentItem(1);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
        }
    }

    //-------------------- Below is all auto-generated code from ActionBar --------------------//

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
                            .setCustomView(i == 0 ? R.layout.camera_tab_icon :
                                                   i == 1 ? R.layout.main_tab_icon :
                                                           R.layout.achievement_tab_icon)
                            .setTabListener(this));

            //Set tab icon's width
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int screenWidth = displaymetrics.widthPixels;
            actionBar.getTabAt(i).getCustomView().getLayoutParams().width =
                    (screenWidth/3) -
                    (2*Math.round(17 * (displaymetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)));
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
        else if(id == R.id.about)
        {
            FragmentManager fm = getSupportFragmentManager();
            InfoFragment iFragment = new InfoFragment();
            iFragment.show(fm, "About");
        }
        else if (id == R.id.help)
        {
            FragmentManager fm = getSupportFragmentManager();
            HelpFragment hFragment = new HelpFragment();
            hFragment.show(fm, "Help", 0);
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
        private HashMap<Integer, WeakReference<Fragment>> fragmentReferences;

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
            fragmentReferences = new HashMap<>();
        }

        @Override
        public Fragment getItem(int position)
        {
            Fragment fragment;
            switch(position)
            {
                case 0:
                    fragment = new ScanFragment();
                    break;
                case 1:
                    fragment = new MainFragment();
                    break;
                default: //case 2
                    fragment = new AchievementsFragment();
                    break;
            }
            fragmentReferences.put(position, new WeakReference<Fragment>(fragment));
            return fragment;
        }

        public Fragment getFragment(int id)
        {
            WeakReference<Fragment> reference = fragmentReferences.get(id);
            return reference == null ? null : reference.get();
        }



        @Override
        public int getCount()
        {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            Locale l = Locale.getDefault();
            switch(position)
            {
                case 0:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }

        public int getIcon(int position)
        {
            Locale l = Locale.getDefault();
            FileHandler fileHandler = FileHandler.getInstance();
            switch(position)
            {
                case 0:
                    return fileHandler.getResourceID("camera6", "drawable");
                case 1:
                    return fileHandler.getResourceID("qricon", "drawable");
                case 2:
                    return fileHandler.getResourceID("trophy", "drawable");
            }
            return 0;
        }
    }
}
