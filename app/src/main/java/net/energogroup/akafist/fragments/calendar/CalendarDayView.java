package net.energogroup.akafist.fragments.calendar;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.ViewContainer;

import net.energogroup.akafist.R;
import net.energogroup.akafist.api.AzbykaAPI;
import net.energogroup.akafist.viewmodel.CalendarViewModel;

public class CalendarDayView extends ViewContainer {

    public final TextView dayTextView;
    public CalendarDay day;

    public CalendarDayView(@NonNull View view, CalendarViewModel viewModel, AzbykaAPI azbykaAPI) {
        super(view);
        dayTextView = view.findViewById(R.id.calendarDayText);
        view.setOnClickListener(view1 -> {
            if (day.getPosition()== DayPosition.MonthDate) {
                Log.e("CALENDAR_DAY_VIEW", String.valueOf(day.getDate()));
                //viewModel.getByDate(String.valueOf(day.getDate()), view.getContext());
                viewModel.getByDate(azbykaAPI, String.valueOf(day.getDate()), view.getContext());
            }
        });
    }
    public CalendarDayView(@NonNull View view) {
        super(view);
        dayTextView = view.findViewById(R.id.calendarDayText);
        view.setOnClickListener(view1 -> {
            if (day.getPosition()== DayPosition.MonthDate) {
                Log.e("CALENDAR_DAY_VIEW", String.valueOf(day.getDate()));
            }
        });
    }
}
