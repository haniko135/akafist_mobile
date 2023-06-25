package net.energogroup.akafist.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.energogroup.akafist.models.LinksModel;

public class PlayerViewModel extends ViewModel {

    private MutableLiveData<String> workMode = new MutableLiveData<>();
    private MutableLiveData<String> urlForAudio = new MutableLiveData<>();
    private MutableLiveData<Boolean> isInitialized = new MutableLiveData<>();

    private MutableLiveData<LinksModel> linksModel = new MutableLiveData<>();


    public MutableLiveData<String> getWorkMode() {
        return workMode;
    }

    public void setWorkMode(String workMode) {
        this.workMode.setValue(workMode);
    }

    public MutableLiveData<String> getUrlForAudio() {
        return urlForAudio;
    }

    public void setUrlForAudio(String urlForAudio) {
        this.urlForAudio.setValue(urlForAudio);
    }

    public MutableLiveData<Boolean> getIsInitialized() { return isInitialized; }

    public void setIsInitialized(Boolean isInitialized) { this.isInitialized.setValue(isInitialized); }

    public MutableLiveData<LinksModel> getLinksModel() {
        return linksModel;
    }

    public void setLinksModel(LinksModel linksModel) {
        this.linksModel.setValue(linksModel);
    }
}
