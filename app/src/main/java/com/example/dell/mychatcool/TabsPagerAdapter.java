package com.example.dell.mychatcool;

/**
 * Created by DELL on 3/17/2018.
 */

//this is our class that implements the fragment adapter that listens
//when the user is swipping from one tab to another

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


class TabsPagerAdapter extends FragmentPagerAdapter {
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                messagesFragment mssfragment=new messagesFragment();
                return mssfragment;
            case 1:
                contactsFragment contfragment=new contactsFragment();
                return contfragment;
            case 2:
                followsFragment frdFragment=new followsFragment();
                return frdFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    //this method sets the title of each tab we have
   public CharSequence getPageTitle(int position){
        switch(position){
            case 0:
                return "Messages";
            case 1:
                return "Contacts";
            case 2:
                return "Follows";

            default:
                return null;
        }

    }

}
