package net.energogroup.akafist.fragments;

import static com.kizitonwose.calendar.core.ExtensionsKt.daysOfWeek;
import static com.kizitonwose.calendar.core.ExtensionsKt.firstDayOfWeekFromLocale;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;

import net.energogroup.akafist.R;
import net.energogroup.akafist.databinding.FragmentAccountCalendarBinding;
import net.energogroup.akafist.fragments.calendar.CalendarDayView;
import net.energogroup.akafist.fragments.calendar.MonthViewContainer;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;

import kotlin.Unit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountCalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountCalendarFragment extends Fragment {

    private FragmentAccountCalendarBinding accountCalendarBinding;

    public AccountCalendarFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccountCalendarFragment.
     */
    public static AccountCalendarFragment newInstance() {
        return new AccountCalendarFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Мой календарь");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        accountCalendarBinding = FragmentAccountCalendarBinding.inflate(inflater, container, false);

        LocalDate today = LocalDate.now();
        int dayToday = today.getDayOfMonth();
        int monthToday = today.getMonthValue();
        int yearValue = today.getYear();

        accountCalendarBinding.calendarAccountMain.setDayBinder(new MonthDayBinder<CalendarDayView>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void bind(@NonNull CalendarDayView container, CalendarDay calendarDay) {
                container.day = calendarDay;
                container.dayTextView.setText(Integer.toString(calendarDay.getDate().getDayOfMonth()));
                if (calendarDay.getPosition() == DayPosition.MonthDate) {
                    container.dayTextView.setTextColor(Color.BLACK);
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
                return new CalendarDayView(view);
            }

        });

        accountCalendarBinding.calendarAccountMain.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthViewContainer>() {
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
        accountCalendarBinding.calendarAccountMain.setup(startMonth, endMonth, firstDayOfWeek);
        accountCalendarBinding.calendarAccountMain.scrollToMonth(currentMonth);


        accountCalendarBinding.calendarAccountMain.setMonthScrollListener(calendarMonth -> {
            if(!Objects.equals(calendarMonth.getYearMonth().getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()), "")) {
                accountCalendarBinding.accountMonthTitle.setText(
                        getResources().getString(
                                R.string.calendar_month_year_label,
                                calendarMonth.getYearMonth().getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
                                calendarMonth.getYearMonth().getYear()
                        )
                );
            }else {
                accountCalendarBinding.accountMonthTitle.setText(
                        getResources().getString(
                            R.string.calendar_month_year_label,
                            "декабрь ",
                            calendarMonth.getYearMonth().getYear()
                ));
            }
            return Unit.INSTANCE;
        });


        return accountCalendarBinding.getRoot();
    }
}