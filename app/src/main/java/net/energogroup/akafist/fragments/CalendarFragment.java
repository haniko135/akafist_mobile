package net.energogroup.akafist.fragments;

import static com.kizitonwose.calendar.core.ExtensionsKt.daysOfWeek;
import static com.kizitonwose.calendar.core.ExtensionsKt.firstDayOfWeekFromLocale;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;

import net.energogroup.akafist.R;
import net.energogroup.akafist.databinding.FragmentCalendarBinding;
import net.energogroup.akafist.fragments.calendar.CalendarDayView;
import net.energogroup.akafist.fragments.calendar.MonthViewContainer;
import net.energogroup.akafist.viewmodel.CalendarViewModel;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import kotlin.Unit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding calendarBinding;
    private CalendarViewModel calendarViewModel;

    /**
     * Required empty public constructor
     */
    public CalendarFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CalendarFragment.
     */
    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.calendar_name);
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        calendarBinding = FragmentCalendarBinding.inflate(inflater, container, false);

        LocalDate today = LocalDate.now();
        int dayToday = today.getDayOfMonth();
        int monthToday = today.getMonthValue();
        int yearValue = today.getYear();

        calendarViewModel.getByDate(String.valueOf(today), getContext());

        Fragment thisContext = this;
        calendarBinding.calendarMain.setDayBinder(new MonthDayBinder<CalendarDayView>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void bind(@NonNull CalendarDayView container, CalendarDay calendarDay) {
                container.day = calendarDay;
                container.dayTextView.setText(Integer.toString(calendarDay.getDate().getDayOfMonth()));
                if (calendarDay.getPosition() == DayPosition.MonthDate) {
                    container.dayTextView.setTextColor(Color.BLACK);

                    calendarViewModel.getIsFinished().observe(getViewLifecycleOwner(), aBoolean -> {
                        if(aBoolean){
                            calendarBinding.calendarChurchBlockLoad.setVisibility(View.GONE);
                            calendarViewModel.getWholeDays().observe(getViewLifecycleOwner(), wholeDays -> {
                                calendarBinding.calendarChurchBlockDay.setVisibility(View.VISIBLE);
                                SpannableString content = new SpannableString(wholeDays.get(0).getDate());
                                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                                calendarBinding.calendarChurchBlockTextUp.setText(content);
                                calendarBinding.calendarChurchBlockTextDown.setText(
                                        wholeDays.get(0).getMemorialDays()
                                                .stream()
                                                .map(s -> Html.fromHtml(s, Html.FROM_HTML_MODE_COMPACT))
                                                .collect(Collectors.joining(", "))
                                );
                                calendarViewModel.getIsFinishedTexts().observe(getViewLifecycleOwner(), aBoolean1 -> {
                                    if(aBoolean1){
                                        calendarBinding.calendarChurchBlockReadDayText.setText(
                                                Html.fromHtml(wholeDays.get(0).getTexts()
                                                        .stream()
                                                        .filter(wholeDayText -> wholeDayText.getType() == 1)
                                                        .collect(Collectors.toList())
                                                        .get(0)
                                                        .getText(),
                                                        Html.FROM_HTML_MODE_COMPACT)
                                        );
                                    }
                                });
                            });
                        }else {
                            calendarBinding.calendarChurchBlockDay.setVisibility(View.INVISIBLE);
                            calendarBinding.calendarChurchBlockLoad.setVisibility(View.VISIBLE);
                        }
                    });

                    if(calendarDay.getDate().getDayOfMonth() == dayToday
                            && calendarDay.getDate().getMonth().getValue() == monthToday
                            && calendarDay.getDate().getYear() == yearValue){
                        container.dayTextView.setTextColor(Color.WHITE);
                        container.dayTextView.setBackgroundResource(R.drawable.calendar_today);
                    }
                } else {
                    container.dayTextView.setTextColor(Color.GRAY);
                }
            }

            @NonNull
            @Override
            public CalendarDayView create(@NonNull View view) {
                return new CalendarDayView(view, calendarViewModel);
            }

        });

        calendarBinding.calendarMain.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthViewContainer>() {
            @NonNull
            @Override
            public MonthViewContainer create(@NonNull View view) {
                return new MonthViewContainer(view);
            }

            @Override
            public void bind(@NonNull MonthViewContainer container, CalendarMonth calendarMonth) {
                if (container.titlesContainer.getTag() == null) {
                    container.titlesContainer.setTag(calendarMonth.getYearMonth());
                    for (int i = 0; i < container.titlesContainer.getChildCount(); i++) {
                        DayOfWeek dayOfWeek = daysOfWeek().get(i);
                        TextView textView = (TextView) container.titlesContainer.getChildAt(i);
                        String title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault());
                        textView.setText(title);
                    }
                }
            }
        });

        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(100);
        YearMonth endMonth = currentMonth.plusMonths(100);
        DayOfWeek firstDayOfWeek = firstDayOfWeekFromLocale();
        calendarBinding.calendarMain.setup(startMonth, endMonth, firstDayOfWeek);
        calendarBinding.calendarMain.scrollToMonth(currentMonth);


        calendarBinding.calendarMain.setMonthScrollListener(calendarMonth -> {
            if(!Objects.equals(calendarMonth.getYearMonth().getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()), "")) {
                calendarBinding.monthTitle.setText(calendarMonth.getYearMonth().getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
                        + " " + calendarMonth.getYearMonth().getYear());
            }else {
                calendarBinding.monthTitle.setText("декабрь " + calendarMonth.getYearMonth().getYear());
            }
            return Unit.INSTANCE;
        });

        return calendarBinding.getRoot();
    }
}