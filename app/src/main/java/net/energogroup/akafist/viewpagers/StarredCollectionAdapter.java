package net.energogroup.akafist.viewpagers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import net.energogroup.akafist.fragments.LinksFragment;
import net.energogroup.akafist.fragments.lists.StarredListFragment;

public class StarredCollectionAdapter extends FragmentStateAdapter {


    public StarredCollectionAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0)
            return new StarredListFragment();
        else {
            LinksFragment fragment = new LinksFragment();
            Bundle args = new Bundle();
            if (position == 1){
                args.putString("date", "molitvyOfflain");
                args.putBoolean("starred", true);
            }else if(position == 2){
                args.putString("date", "links");
                args.putBoolean("starred", true);
            }
            fragment.setArguments(args);
            return fragment;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
