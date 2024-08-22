package net.energogroup.akafist.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.energogroup.akafist.R;
import net.energogroup.akafist.fragments.Home;
import net.energogroup.akafist.models.HomeBlocksModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A class containing data processing logic
 * {@link Home} and {@link HomeBlocksModel}
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class MenuViewModel extends ViewModel {
    private final List<HomeBlocksModel> blocksModelList = new ArrayList<>();
    private final MutableLiveData<List<HomeBlocksModel>> mutableLiveData = new MutableLiveData<>();

    public List<HomeBlocksModel> getBlocksModelList() {
        return blocksModelList;
    }

    public MutableLiveData<List<HomeBlocksModel>> getMutableLiveData() {
        return mutableLiveData;
    }

    /**
     * This method initializes the list of blocks of the "Home" and "Menu" pages for the first time
     */
    public void firstSet(String mode, Context context){
        if (mode.equals("energogroup")) {
            blocksModelList.add(new HomeBlocksModel("skypeConfs",
                    context.getString(R.string.skype_confs), context.getString(R.string.skype_confs_2)));
        }
        blocksModelList.add(new HomeBlocksModel("menuOnlineTemple",
                context.getString(R.string.online_Michael), " "));
        blocksModelList.add(new HomeBlocksModel("links",
                context.getString(R.string.links1), context.getString(R.string.links2)));
        blocksModelList.add(new HomeBlocksModel("menuChurch",
                context.getString(R.string.menu_church_title)," "));
        blocksModelList.add(new HomeBlocksModel("notes",
                context.getString(R.string.notes1), context.getString(R.string.notes2)));
        blocksModelList.add(new HomeBlocksModel("talks",
                context.getString(R.string.talks1), context.getString(R.string.talks2)));
        mutableLiveData.setValue(blocksModelList);
    }

}
