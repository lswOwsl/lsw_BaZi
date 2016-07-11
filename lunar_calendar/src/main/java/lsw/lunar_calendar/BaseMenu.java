package lsw.lunar_calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import net.simonvt.menudrawer.MenuDrawer;

import lsw.ContactAuthor;
import lsw.library.DateExt;
import lsw.library.LunarCalendarWrapper;

/**
 * Created by swli on 6/8/2016.
 */
public class BaseMenu extends FragmentActivity {

    protected MenuDrawer mDrawer;
    protected DateExt initialDate;

    protected ViewPager menuViewPager;

    private int menuWidth;

    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDrawer = MenuDrawer.attach(this);
        mDrawer.setMenuView(R.layout.menu_left);

        inflater = LayoutInflater.from(this);

        mDrawer.setOnDrawerStateChangeListener(new MenuDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == MenuDrawer.STATE_CLOSED) {
                    loadBirthdayAndHexagram();
                }

            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {

            }
        });

        menuViewPager = (ViewPager)mDrawer.getMenuView().findViewById(R.id.vPager);

        //侧边栏宽度，占整窗体的3/2
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        menuWidth = dm.widthPixels / 3 * 2;
        mDrawer.setMenuSize(menuWidth);

        loadViewPager();
    }


    protected void loadViewPager()
    {
        menuViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = null;
                if(position == 2) {
                    fragment = HexagramListFragment.newInstance(initialDate);
                }
                if(position == 1)
                {
                    fragment = BirthdayListFragment.newInstance(initialDate);
                }
                if(position == 0)
                {
                    fragment = MemoryListFragment.newInstance(initialDate);
                }
                return fragment;
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
        menuViewPager.setCurrentItem(0);
    }

    protected void loadBirthdayAndHexagram() {
        loadViewPager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_month, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            if (mDrawer.isMenuVisible())
                mDrawer.closeMenu();
            else
                mDrawer.openMenu();
            return true;
        }

        if (id == R.id.menuContact) {
            Intent intentContact = new Intent();
            intentContact.setClass(this, ContactAuthor.class);
            startActivityForResult(intentContact, 0);
            return true;
        }

        if(id == R.id.menuSetting)
        {
            Intent intent = new Intent();
            intent.setClass(this, Setting.class);
            startActivityForResult(intent, 0);
            return true;
        }

        if(id == R.id.menuMemory)
        {
            Intent intent = new Intent();
            intent.setClass(this, MemoryEventList.class);
            startActivityForResult(intent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
