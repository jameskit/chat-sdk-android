package com.braunster.chatsdk.adapter;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.astuetz.PagerSlidingTabStrip;
import com.braunster.chatsdk.R;
import com.braunster.chatsdk.fragments.BaseFragment;
import com.braunster.chatsdk.fragments.ContactsFragment;
import com.braunster.chatsdk.fragments.ConversationsFragment;
import com.braunster.chatsdk.fragments.ProfileFragment;
import com.braunster.chatsdk.fragments.ThreadsFragment;

/**
 * Created by itzik on 6/16/2014.
 */
public class PagerAdapterTabs extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

    private final String[] TITLES = { "Profile", "Chat Rooms", "Contacts", "Conversations"};

    private final BaseFragment[] FRAGMENTS = new BaseFragment[] {ProfileFragment.newInstance(), ThreadsFragment.newInstance(), ContactsFragment.newInstance(), ConversationsFragment.newInstance()};

    private final int[] ICONS = new int[] {R.drawable.ic_action_user, R.drawable.ic_action_public, R.drawable.ic_action_contacts, R.drawable.ic_action_private};

    public static final int Profile = 0, ChatRooms = 1, Contacts = 2, Conversations = 3;

    public PagerAdapterTabs(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public BaseFragment getItem(int position) {
        return FRAGMENTS[position];
    }

    @Override
    public int getPageIconResId(int position) {
        return ICONS[position];
    }
}