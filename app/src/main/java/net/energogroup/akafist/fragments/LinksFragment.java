package net.energogroup.akafist.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.energogroup.akafist.dialogs.DialogLinks;
import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;

import net.energogroup.akafist.databinding.FragmentLinksBinding;
import net.energogroup.akafist.models.LinksModel;
import net.energogroup.akafist.recyclers.AudioRecyclerAdapter;
import net.energogroup.akafist.viewmodel.LinksViewModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Класс фрагмента с записями просветительских бесед
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class LinksFragment extends Fragment {

    private String date;
    private String dateTxt;
    private String finalPath;
    private LinksViewModel linksViewModel;
    private AudioRecyclerAdapter recyclerAdapter;
    private List<LinksModel> downloadAudio = new ArrayList<>();
    private ArrayList<String> downloadAudioNames = new ArrayList<>();
    public String urlForLink;
    public String fileName;
    private SharedPreferences appPref;
    public static boolean isChecked = false; //для пользовательского соглашения
    public FragmentLinksBinding binding;

    /**
     * Обязательный конструктор класса
     */
    public LinksFragment() { }

    public String getFinalPath() {
        return finalPath;
    }

    /**
     * Этот метод вызывает конструктор класса фрагмента записей просветительских бесед
     * @return LinksFragment
     */
    public static LinksFragment newInstance() {
        return new LinksFragment();
    }

    /**
     * Этот метод подготавливает активность к работе фрагмента
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            date = getArguments().getString("date");
            if(getArguments().getString("dateTxt")!=null){
                dateTxt = getArguments().getString("dateTxt");
            }
        }
        ViewModelProvider provider = new ViewModelProvider(this);
        linksViewModel = provider.get(LinksViewModel.class);
        if((AppCompatActivity)getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(dateTxt);
            linksViewModel.getJson(date, getLayoutInflater());
        }

        appPref = getActivity().getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        if(appPref.contains("app_pref_checked")){
            isChecked = appPref.getBoolean("app_pref_checked", false);
        }
    }

    /**
     * Этот метод создаёт фрагмент с учетом определённых
     * в {@link LinksFragment#onCreate(Bundle)} полей
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLinksBinding.inflate(inflater, container, false);

        //пути сохранения файлов
        String linksAudiosFilesDir = getContext().getFilesDir().getPath() + "/links_records";
        String molitvyOfflainFilesDir = getContext().getFilesDir().getPath() + "/prayers_records";

        switch (date) {
            case "links":
                finalPath = linksAudiosFilesDir;
                break;
            case "molitvyOfflain":
                finalPath = molitvyOfflainFilesDir;
                break;
        }

        //список загруженных аудиофайлов
        downloadAudio = linksViewModel.getDownload(finalPath);
        downloadAudio.forEach(it -> {
            downloadAudioNames.add(it.getName());
        });

        if (!isChecked) {
            SharedPreferences.Editor editor = appPref.edit();
            DialogLinks dialogLinks = new DialogLinks(editor);
            dialogLinks.show(requireActivity().getSupportFragmentManager(), "userAlertLinks");
        }


        if(getActivity().getApplicationContext() != null) {
            //проверка на наличие интернет-соединения
            MainActivity.networkConnection.observe(getViewLifecycleOwner(), isCheckeds -> {
                if (isCheckeds) {
                    //прослушивание кнопки загрузки
                    binding.downloadLinkButton.setOnClickListener(view -> {
                        preNotification("Загрузка начата");
                        linksViewModel.getLinkDownload(urlForLink, inflater, container, finalPath, fileName);
                    });

                    //создание RecyclerView
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    binding.linksRv.setLayoutManager(linearLayoutManager);
                    linksViewModel.getMutableLinksDate().observe(getViewLifecycleOwner(), linksModels -> {
                        if (recyclerAdapter == null) {
                            recyclerAdapter = new AudioRecyclerAdapter(linksModels, downloadAudioNames, this);
                        }
                        recyclerAdapter.setList(linksModels, downloadAudioNames);
                        binding.linksRv.setAdapter(recyclerAdapter);
                    });

                    //прослушивание нажатия на унтральную кнопку плеера
                    binding.imageButtonPlay.setOnClickListener(view -> {
                        if (recyclerAdapter.playAudios != null) {
                            recyclerAdapter.playAudios.playAndStop();
                        }
                    });
                } else {
                    //создание RecyclerView
                    binding.linksRv.setLayoutManager(new LinearLayoutManager(getContext()));
                    if (recyclerAdapter == null)
                        recyclerAdapter = new AudioRecyclerAdapter(linksViewModel.getDownload(finalPath), this);
                    recyclerAdapter.setList(linksViewModel.getDownload(finalPath), downloadAudioNames);
                    binding.linksRv.setAdapter(recyclerAdapter);
                }
            });
        }

        //обновление RecyclerView
        binding.linksRoot.setOnRefreshListener(() -> {
            binding.linksRoot.setRefreshing(true);
            downloadAudio = linksViewModel.getDownload(finalPath);
            downloadAudioNames.clear();
            downloadAudio.forEach(it -> {
                downloadAudioNames.add(it.getName());
            });
            linksViewModel.retryGetJson(date, getLayoutInflater());
            binding.linksRoot.setRefreshing(false);
        });

        return binding.getRoot();
    }

    /**
     * Этот метод вызывает уничтожение фрагмента
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(recyclerAdapter != null) {
            if (recyclerAdapter.playAudios != null) {
                recyclerAdapter.playAudios.destroyPlayAudios();
            }
        }
    }

    /**
     * Этот метод создаёт уведомление о начале загрузки аудио-файла
     */
    public void preNotification(String s){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(binding.getRoot().getContext(), MainActivity.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Помощник чтеца")
                .setContentText(s)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(binding.getRoot().getContext());
        int NOTIFICATION_ID = 101;
        managerCompat.notify(NOTIFICATION_ID, builder.build());
    }
}