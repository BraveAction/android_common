package org.yang.common.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Gxy on 2017/2/4
 */
public class LazyFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public LazyFragmentPagerAdapter(FragmentManager fm, List fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }
}
