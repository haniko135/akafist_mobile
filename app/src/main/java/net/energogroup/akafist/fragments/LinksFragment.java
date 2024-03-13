package net.energogroup.akafist.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.energogroup.akafist.db.StarredDTO;
import net.energogroup.akafist.dialogs.DialogLinks;
import net.energogroup.akafist.MainActivity;

import net.energogroup.akafist.databinding.FragmentLinksBinding;
import net.energogroup.akafist.models.LinksModel;
import net.energogroup.akafist.models.StarredModel;
import net.energogroup.akafist.recyclers.AudioRecyclerAdapter;
import net.energogroup.akafist.viewmodel.LinksViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Класс фрагмента с записями просветительских бесед
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class LinksFragment extends Fragment {

    private String date;
    private String dateTxt;
    private String finalPath;
    private Boolean isStarredFrag;
    private LinksViewModel linksViewModel;
    private AudioRecyclerAdapter recyclerAdapter;
    private MainActivity mainActivity;
    private SQLiteDatabase db;
    private List<LinksModel> downloadAudio = new ArrayList<>();
    private ArrayList<String> downloadAudioNames = new ArrayList<>();
    private ArrayList<StarredModel> starredAudio;
    private SharedPreferences appPref;
    public static boolean isChecked = false; //для пользовательского соглашения
    public FragmentLinksBinding binding;

    public String getDate() {
        return date;
    }

    /**
     * Required class constructor
     */
    public LinksFragment() { }


    /**
     * This method calls the constructor of the fragment class of educational conversation recordings
     * @return LinksFragment
     */
    public static LinksFragment newInstance() {
        return new LinksFragment();
    }

    /**
     * This method prepares the activity for the fragment operation
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeLinks();
        sharedPrefInit();
    }

    /**
     * This method creates a fragment taking into account certain
     * in {@link LinksFragment#onCreate(Bundle)} fields
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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

        //list of downloaded audio files
        downloadAudio = linksViewModel.getDownload(finalPath);
        downloadAudio.forEach(it -> downloadAudioNames.add(it.getName()));

        //agreement to download audio files
        if (!isChecked) {
            SharedPreferences.Editor editor = appPref.edit();
            DialogLinks dialogLinks = new DialogLinks(editor, this);
            dialogLinks.show(requireActivity().getSupportFragmentManager(), "userAlertLinks");
        }


        if(getActivity().getApplicationContext() != null) {
            //checking for an internet connection
            MainActivity.networkConnection.observe(getViewLifecycleOwner(), isCheckeds -> {
                if (isCheckeds) {

                    //creating RecyclerView
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    binding.linksRv.setLayoutManager(linearLayoutManager);
                    linksViewModel.getMutableLinksDate().observe(getViewLifecycleOwner(), linksModels -> {
                        List<LinksModel> filtered;
                        if(isStarredFrag != null){
                             filtered = linksModels.stream()
                                    .filter(linksModel -> starredAudio.stream()
                                            .anyMatch(starredModel -> starredModel.getObjectUrl().equals(linksModel.getUrl())))
                                    .collect(Collectors.toList());
                        }else {
                            filtered = linksModels;
                        }

                        if (recyclerAdapter == null) {
                            recyclerAdapter = new AudioRecyclerAdapter(filtered, downloadAudioNames,
                                    this, finalPath, date);
                        }
                        recyclerAdapter.setList(filtered, downloadAudioNames);
                        binding.linksRv.setAdapter(recyclerAdapter);
                    });

                } else {

                    //creating RecyclerView
                    binding.linksRv.setLayoutManager(new LinearLayoutManager(getContext()));
                    if (recyclerAdapter == null)
                        recyclerAdapter = new AudioRecyclerAdapter(linksViewModel.getDownload(finalPath), this);
                    recyclerAdapter.setList(linksViewModel.getDownload(finalPath), downloadAudioNames);
                    binding.linksRv.setAdapter(recyclerAdapter);
                }
            });
        }

        //updating RecyclerView
        binding.linksRoot.setOnRefreshListener(() -> {
            binding.linksRoot.setRefreshing(true);
            downloadAudio = linksViewModel.getDownload(finalPath);
            downloadAudioNames.clear();
            downloadAudio.forEach(it -> downloadAudioNames.add(it.getName()));
            linksViewModel.retryGetJson(date, getLayoutInflater());
            binding.linksRoot.setRefreshing(false);
        });

        return binding.getRoot();
    }

    /**
     * This method initialize base parameters of fragment
     */
    public void initializeLinks(){
        mainActivity = (MainActivity) getActivity();
        if (getArguments() != null){
            date = getArguments().getString("date");
            if(getArguments().getString("dateTxt")!=null){
                dateTxt = getArguments().getString("dateTxt");
            }
            if(getArguments().getBoolean("starred")){
                isStarredFrag = getArguments().getBoolean("starred");
                db = mainActivity.getDbHelper().getReadableDatabase();
            }
        }
        ViewModelProvider provider = new ViewModelProvider(this);
        linksViewModel = provider.get(LinksViewModel.class);
        if((AppCompatActivity)getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(dateTxt);
            linksViewModel.getJson(date, getLayoutInflater());
            if(isStarredFrag != null){
                if(starredAudio != null){
                    starredAudio.clear();
                }
                starredAudio = linksViewModel.getStarredAudio(date, db);
            }
        }
    }

    /**
     * This method initializes parameters of shared preferences
     */
    public void sharedPrefInit(){
        appPref = getActivity().getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        if(appPref.contains("app_pref_checked")){
            isChecked = appPref.getBoolean("app_pref_checked", false);
        }
    }

    @Override
    public void onDestroyView() {
        if(db != null) db.close();
        super.onDestroyView();
    }
}