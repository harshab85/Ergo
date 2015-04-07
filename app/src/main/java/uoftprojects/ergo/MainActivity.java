package uoftprojects.ergo;

import android.app.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.content.SharedPreferences;






import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.Locale;

import uoftprojects.ergo.R;
import uoftprojects.ergo.SignUpScreens.WelcomeFragments;
import uoftprojects.ergo.VideoActivity;
import uoftprojects.ergo.util.ActivityUtil;
import uoftprojects.ergo.util.SetupUtil;
import uoftprojects.ergo.util.StorageUtil;

/**
 * Created by Harsha Balasubramanian on 3/2/2015.
 */
public class MainActivity extends FragmentActivity implements WelcomeFragments.OnFragmentInteractionListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        /*SharedPreferences sharedPreferences = getSharedPreferences("ErgoSetup", 0);
        if(sharedPreferences != null) {
            boolean setupCompleted = sharedPreferences.getBoolean("setupCompleted", false);
            if (setupCompleted) {
                openVideoLibrary(findViewById(R.id.button));
            }
        }*/

      //  ActivityUtil.setMainActivity(this);
//
//        if( SetupUtil.isSetupCompeted()){
 //           openVideoLibrary(findViewById(R.id.button));
//        }
//
       // Create the adapter that will return a fragment for each of the three
 //       primary sections of the activity.


        mSectionsPagerAdapter = new SectionsPagerAdapter(this.getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        SetupUtil.setAppStartDate();
    }

    public void openVideoLibrary(View view) {
        //TODO: Add are you sure you want to proceed without an email address?

        /*SharedPreferences sharedPreferences = getSharedPreferences("ErgoSetup", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("setupCompleted", true);
        editor.commit();*/
        EditText editText = (EditText)findViewById(R.id.editText);
        String emailAddress = editText.getText().toString();

        StorageUtil.addString("emailAddress", emailAddress);
        SetupUtil.setupCompleted();

        Intent intent = new Intent(this, TopActivity.class);












        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_up_tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void onFragmentInteraction(Uri uri){
        System.out.println("ON FRAGMENT INTERACTION METHOD CALLED");
    }


    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return WelcomeFragments.newInstance(position + 1);
                case 1:
                    return WelcomeFragments.newInstance(position + 1);
                case 2:
                    return WelcomeFragments.newInstance(position + 1);
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }
}
