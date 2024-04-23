package net.energogroup.akafist.fragments.calendar;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.ViewContainer;

import net.energogroup.akafist.R;

public class CalendarDayView extends ViewContainer {

    public TextView dayTextView;
    public CalendarDay day;

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
