package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.calendar.Calendar;
import com.esferalia.aon.occam.api.model.calendar.Holiday;
import com.esferalia.aon.occam.api.model.calendar.HolidayDetail;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;

public class AnnualCalendarWidget extends FlowPanel {

    private FlowPanel annualCalendar;

    private Integer domain;
    private Calendar calendar;
    private List<Holiday> holidays;
    private List<Holiday> chain;

    private int year;
    private Consumer<Integer> onYearChange;
    private Consumer<Date> onDateSelected;

    public AnnualCalendarWidget(Integer domain, Calendar calendar, List<Holiday> holidays, int year, Consumer<Integer> onYearChange, Consumer<Date> onDateSelected) {
        this.domain = domain;
    	this.calendar = calendar;
        this.holidays = holidays;
        this.year = year;
        this.onYearChange = onYearChange;
        this.onDateSelected = onDateSelected;

        Holiday selected = calendar.getHoliday();
        chain = buildHolidayChain(selected, holidays);

        buildHeader();

        annualCalendar = new FlowPanel();
        annualCalendar.addStyleName("annual-calendar");
        add(annualCalendar);

        build();
    }

    private List<Holiday> buildHolidayChain(Holiday h, List<Holiday> all) {
        List<Holiday> chain = new ArrayList<>();
        Holiday current = h;

        while (current != null) {
            chain.add(0, current);
            current = findParent(current, all);
        }
        return chain;
    }

    private Holiday findParent(Holiday h, List<Holiday> all) {
        if (h.getHolidayParent() == null) return null;
        for (Holiday x : all) {
            if (x.getId().equals(h.getHolidayParent())) return x;
        }
        return null;
    }

    private void buildHeader() {
        FlowPanel header = new FlowPanel();
        header.addStyleName("calendar-header");

        AonToolbarButton prev = new AonToolbarButton("", AON.CSS.aonIconLeft());
        prev.addClickHandler(e -> onYearChange.accept(year - 1));

        Label title = new Label(year + "");
        title.addStyleName("calendar-title");

        AonToolbarButton next = new AonToolbarButton("", AON.CSS.aonIconRight());
        next.addClickHandler(e -> onYearChange.accept(year + 1));

        header.add(prev);
        header.add(title);
        header.add(next);

        add(header);
    }

    private void build() {
        for (int month = 0; month < 12; month++) {
            annualCalendar.add(buildMonth(month));
        }
    }

    private FlowPanel buildMonth(int month) {
        FlowPanel panel = new FlowPanel();
        panel.addStyleName("month-panel");

        DateTimeFormat fmt = DateTimeFormat.getFormat("MMMM");
        Date firstDayDate = new Date(year - 1900, month, 1);
        panel.add(new Label(fmt.format(firstDayDate)));

        Grid grid = new Grid(7, 7);
        grid.addStyleName("month-grid");

        String[] days = {"L", "M", "X", "J", "V", "S", "D"};
        for (int i = 0; i < 7; i++) {
            Label lbl = new Label(days[i]);
            lbl.addStyleName("day-header");
            grid.setWidget(0, i, lbl);
        }

        int firstDay = firstDayDate.getDay(); // 0=domingo, 1=lunes...
        int daysInMonth = getDaysInMonth(year, month);

        int col = (firstDay == 0) ? 6 : firstDay - 1;
        int row = 1;

        for (int day = 1; day <= daysInMonth; day++) {
            Label lbl = new Label(String.valueOf(day));
            lbl.addStyleName("day-cell");

            int dayCopy = day;

            lbl.addClickHandler(e -> {
            	if(isHoliday(month, dayCopy)) return;
            	Date selectedDate = new Date(year - 1900, month, dayCopy);
            	onDateSelected.accept(selectedDate);
            });

            if (isWeekend(col)) {
                lbl.addStyleName("weekend");
                lbl.getElement().setTitle("No Laborable");
            }

            if (isHoliday(month, day) || isOwnHoliday(month, day)) {
                lbl.addStyleName(isHoliday(month, day) ? "holiday" : "own-holiday");

                String desc = getHolidayDescription(month, day);
                if (desc != null) {
                    lbl.getElement().setTitle(desc);
                }
            }

            grid.setWidget(row, col, lbl);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }

        panel.add(grid);
        return panel;
    }

    private boolean isWeekend(int col) {
		switch (col) {
			case 0: 
				return calendar.isMonday();
			case 1: 
				return calendar.isTuesday();
			case 2: 
				return calendar.isWednesday();
			case 3: 
				return calendar.isThursday();
			case 4: 
				return calendar.isFriday();
			case 5: 
				return calendar.isSaturday();
			case 6: 
				return calendar.isSunday();
			default:
				return false;
		}
	}

	private String getHolidayDescription(int month, int day) {
        for (Holiday h : chain) {
            if (h.getDetails() != null) {
                for (HolidayDetail d : h.getDetails()) {
                    Date date = d.getDate();
                    if (date.getMonth() == month && date.getDate() == day) {
                        return d.getDescription() + " (" + h.getDescription() + ")";
                    }
                }
            }
        }
        return null;
    }

    private int getDaysInMonth(int year, int month) {
        switch (month) {
            case 0: return 31;
            case 1: return (isLeap(year) ? 29 : 28);
            case 2: return 31;
            case 3: return 30;
            case 4: return 31;
            case 5: return 30;
            case 6: return 31;
            case 7: return 31;
            case 8: return 30;
            case 9: return 31;
            case 10: return 30;
            case 11: return 31;
        }
        return 30;
    }

    private boolean isLeap(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    private boolean isHoliday(int month, int day) {
        Date compareDate = new Date(year - 1900, month, day);
        for (Holiday h : chain) {
            if (h.getDetails() != null) {
                for (HolidayDetail d : h.getDetails()) {
                    if (d.getDate().equals(compareDate) && !d.getDomain().equals(domain)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean isOwnHoliday(int month, int day) {
    	Date compareDate = new Date(year - 1900, month, day);
        for (Holiday h : chain) {
            if (h.getDetails() != null) {
                for (HolidayDetail d : h.getDetails()) {
                    if (d.getDate().equals(compareDate) && d.getDomain().equals(domain)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
