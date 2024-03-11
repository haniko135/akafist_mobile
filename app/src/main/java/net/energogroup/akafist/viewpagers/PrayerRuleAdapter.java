package net.energogroup.akafist.viewpagers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import net.energogroup.akafist.fragments.lists.AudioDragAndDropFragment;
import net.energogroup.akafist.fragments.lists.TextPrayersDragDropListFragment;

public class PrayerRuleAdapter extends FragmentStateAdapter {


    public PrayerRuleAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0){
            return new TextPrayersDragDropListFragment();
        }else {
            return new AudioDragAndDropFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
