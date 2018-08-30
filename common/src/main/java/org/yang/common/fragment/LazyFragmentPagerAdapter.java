package org.yang.common.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gxy on 2017/2/4
 */
public class LazyFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<BaseLazyFragment> fragments;

    public LazyFragmentPagerAdapter(FragmentManager fm, List fragments) {
        super(fm);
        this.fragments = fragments;
    }

    public LazyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
    }

    public List<BaseLazyFragment> getFragments() {
        return fragments;
    }

    public void setFragments(List<BaseLazyFragment> fragments) {
        this.fragments = fragments;
        notifyDataSetChanged();
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
