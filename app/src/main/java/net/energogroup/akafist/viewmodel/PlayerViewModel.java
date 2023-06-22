package net.energogroup.akafist.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PlayerViewModel extends ViewModel {

    private MutableLiveData<String> workMode = new MutableLiveData<>();
    private MutableLiveData<String> urlForAudio = new MutableLiveData<>();


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
}
