package net.energogroup.akafist.fragments.calendar;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.kizitonwose.calendar.view.ViewContainer;

public class MonthViewContainer extends ViewContainer {

    public final ViewGroup titlesContainer;
    public MonthViewContainer(@NonNull View view) {
        super(view);
        titlesContainer = (ViewGroup) view;
    }
}
