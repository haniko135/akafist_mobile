package net.energogroup.akafist.viewmodel;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.Response;
import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.fragments.Home;
import net.energogroup.akafist.models.HomeBlocksModel;
import net.energogroup.akafist.service.RequestServiceHandler;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                context.getString(R.string.molitvy_offline)," "));
        blocksModelList.add(new HomeBlocksModel("notes",
                context.getString(R.string.notes1), context.getString(R.string.notes2)));
        blocksModelList.add(new HomeBlocksModel("talks",
                context.getString(R.string.talks1), context.getString(R.string.talks2)));
        mutableLiveData.setValue(blocksModelList);
    }

}
