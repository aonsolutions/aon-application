package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AnnualCalendarWidget;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomNumberBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextArea;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.occam.api.model.calendar.Calendar;
import com.esferalia.aon.occam.api.model.calendar.Holiday;
import com.esferalia.aon.occam.api.model.calendar.HolidayDetail;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class WorkplaceCalendar extends AonCustomDockLayout {
	
	private class DayControl {
	    Supplier<Boolean> getterDay;
	    Consumer<Boolean> setterDay;
	    Consumer<Double> setterHours;

	    Label dayButton;
	    AonDoubleBox hours;
	}

    private static final Logger LOGGER = Logger.getLogger(WorkplaceCalendar.class.getName());
    static { LOGGER.addHandler(new ConsoleLogHandler()); }

    private HTMLPanel container;
    private HTMLPanel messagePanel = new HTMLPanel("");

    private SimpleLayoutPanel centerPanel;
    private ScrollPanel westPanel = new ScrollPanel();
   
    private FlowPanel form = new FlowPanel();
    private FlowPanel formContent = new FlowPanel();
    private FlowPanel formCollapsibles = new FlowPanel();
    private FlowPanel formWorkingDays = new FlowPanel();

    private AonCustomTextBox txtDescription = new AonCustomTextBox("Descripci\u00f3n");
    private AonCustomTextArea txtComment = new AonCustomTextArea("Comentarios");
    private AonCustomNumberBox txtAnnualHours = new AonCustomNumberBox("Horas anuales", 2);
    private AonCustomNumberBox txtAnnuaPersonalDays = new AonCustomNumberBox("D. libre disposici\u00f3n", 2);
    private AonCustomNumberBox txtAnnualHolidays = new AonCustomNumberBox("D\u00edas vacaciones", 2);
    private AonCustomListBox lbAnnualHolidays = new AonCustomListBox("Tipo D\u00edas Vac.");
    private AonCustomListBox lbHoliday = new AonCustomListBox("Calendario de festivos asociados");

    private FlowPanel showHidePanel = new FlowPanel();
    private AonToolbarButton showHide = new AonToolbarButton("", AON.CSS.aonIconMenuCollapse());

    private Integer domain;
    private Workplace workplace;
    private Calendar calendar;
    private List<Holiday> holidaysList;

    private Integer currentYear = Integer.parseInt(DateTimeFormat.getFormat("yyyy").format(new Date()));
    private boolean westShow = true;

    private final DomainEnterprisesServiceAsync service = DomainEnterprisesServiceAsync.newInstance();

    // Loader overlay
    private HTMLPanel loaderOverlay = new HTMLPanel(
        "<div class='loader'></div><div class='loader-text'>Cargando calendario</div>"
    );

    public WorkplaceCalendar() {
    	super(null);
        initComponents();
    }
    
    public WorkplaceCalendar(Workplace workplace) {
        super(null);
        initComponents();
        setWorkplace(workplace);
    }
    
    private void initComponents() {
    	loaderOverlay.setStyleName("loader-overlay");
        add(loaderOverlay);

        container = new HTMLPanel("");
        container.addStyleName(AON.CSS.aonFlexColumn());
        container.add(messagePanel);
        
        showHide = new AonToolbarButton("", AON.CSS.aonIconMenuCollapse());
    	showHide.getElement().setId("collapseMenuWorkplaceCalendar");
    	showHide.getElement().getStyle().setProperty("margin-top", ".8rem");
    	
    	showHide.addClickHandler(e -> {
            if (!westShow) {
                setWidgetSize(westPanel, 350);
                animate(300);
                formContent.getElement().getStyle().clearDisplay();
                formCollapsibles.getElement().getStyle().clearDisplay();
                formWorkingDays.getElement().getStyle().clearDisplay();
                showHide.removeStyleName(AON.CSS.aonIconMenu());
                showHide.addStyleName(AON.CSS.aonIconMenuCollapse());
            } else {
                setWidgetSize(westPanel, 70);
                animate(300);
                formContent.getElement().getStyle().setDisplay(Display.NONE);
                formCollapsibles.getElement().getStyle().setDisplay(Display.NONE);
                formWorkingDays.getElement().getStyle().setDisplay(Display.NONE);
                showHide.removeStyleName(AON.CSS.aonIconMenuCollapse());
                showHide.addStyleName(AON.CSS.aonIconMenu());
            }
            westShow = !westShow;
        });
    	
    	showHidePanel.getElement().setId("showHidePanelWorkplaceCalendar");
    	showHidePanel.add(showHide);

        centerPanel = new SimpleLayoutPanel();
        centerPanel.setHeight("100%");
        centerPanel.getElement().getStyle().setProperty("position", "relative");

        container.add(centerPanel);

        westPanel.addStyleName(AON.CSS.aonFlexColumn());
        westPanel.getElement().getStyle().setProperty("margin", "0");
        addWestWithoutCenterStyle(westPanel, 350);

        addWithoutCenterStyle(container);
    }
    
    public void setWorkplace(Workplace workplace) {
    	this.workplace = workplace;
        loadData();
    }

    @Override
    protected void onClearFilter() {}

    private void loadData() {
        getDomain(d -> {
            getHolidays(h -> {
                getCalendar(c -> {
                	if(c.isEmpty()) calendar = new Calendar().setDomain(domain);
                	else calendar = c.get(0);

                    buildForm();
                    buildWorkingDays();
                    buildHolidayCollapsibles();
                    
                    westPanel.setWidget(form);

                    buildAnnualCalendar();

                    loaderOverlay.setVisible(false);
                });
            });
        });
    }

	private void getDomain(Consumer<Integer> success) {
        service.getDomain(new AsyncCallback<Integer>() {
            @Override public void onSuccess(Integer result) {
                domain = result;
                success.accept(result);
            }
            @Override public void onFailure(Throwable caught) {
                AonMessagePanel.showError(messagePanel, "Error obteniendo dominio: " + caught.getMessage());
            }
        });
    }

    private void getCalendar(Consumer<List<Calendar>> success) {
        service.getCalendar(workplace.getId(), new AsyncCallback<List<Calendar>>() {
            @Override public void onSuccess(List<Calendar> result) {
                success.accept(result);
            }
            @Override public void onFailure(Throwable caught) {
            	loaderOverlay.setVisible(false);
                AonMessagePanel.showError(messagePanel, "Error obteniendo calendario: " + caught.getMessage());
            }
        });
    }

    private void getHolidays(Consumer<List<Holiday>> success) {
        service.getHolidays(new AsyncCallback<List<Holiday>>() {
            @Override public void onSuccess(List<Holiday> result) {
            	List<Holiday> filteredList = result.stream().filter(h -> !h.getDomain().equals(domain))
            			.sorted((o1, o2) -> o1.getDescription().compareTo(o2.getDescription()))
            			.collect(Collectors.toList());
                
            	holidaysList = filteredList;
                success.accept(filteredList);
            }
            @Override public void onFailure(Throwable caught) {
            	loaderOverlay.setVisible(false);
                AonMessagePanel.showError(messagePanel, "Error obteniendo festivos: " + caught.getMessage());
            }
        });
    }

    private void buildForm() {
        westPanel.clear();

        form.clear();
        form.addStyleName(AON.CSS.aonFlexColumn());
        
        formContent.clear();
        
        formContent.getElement().setId("workplaceCalendarForm");
        
        txtDescription = new AonCustomTextBox("Descripci\u00f3n");
        txtComment = new AonCustomTextArea("Comentarios");
        txtAnnualHours = new AonCustomNumberBox("Horas anuales", 2);
        txtAnnuaPersonalDays = new AonCustomNumberBox("D. libre disposici\u00f3n", 2);
        txtAnnualHolidays = new AonCustomNumberBox("D\u00edas vacaciones", 2);
        lbAnnualHolidays = new AonCustomListBox("Tipo D\u00edas Vac.");
        lbHoliday = new AonCustomListBox("Calendario de festivos asociados");

        txtComment.getTextBox().setVisibleLines(3);

        txtAnnualHours.hideNearBy();
        txtAnnuaPersonalDays.hideNearBy();
        txtAnnualHolidays.hideNearBy();
        
        lbAnnualHolidays.clearItems();
        lbAnnualHolidays.addItem("Naturales", "0");
        lbAnnualHolidays.addItem("Laborables", "1");

        lbHoliday.clearItems();
        lbHoliday.addItem("-", "");
        for (Holiday h : holidaysList) {
            lbHoliday.addItem(h.getDescription(), h.getId().toString());
        }
        
        
        if(null != calendar && null != calendar.getId()) {
            txtDescription.setValue(calendar.getDescription());
            txtComment.setValue(calendar.getComment());
            txtAnnualHours.setValue(calendar.getAnualHours());
            txtAnnuaPersonalDays.setValue(calendar.getAnualPersonalDays());
            
            txtAnnualHolidays.setValue(calendar.getAnnualHolidays());
            
            lbAnnualHolidays.setValue(null == calendar.getHolidaysType() ? "0" :calendar.getHolidaysType().toString());
        	lbHoliday.setValue(null != calendar.getHoliday() && null != calendar.getHoliday().getHolidayParent() ? calendar.getHoliday().getHolidayParent().toString() : "");
        }
        
        txtDescription.addValueChangeHandler(e -> {
        	calendar.setDescription(e.getValue());
        	saveCalendar(false);
        });
        txtComment.addValueChangeHandler(e -> {
        	calendar.setComment(e.getValue());
        	saveCalendar(false);
        });
        txtAnnualHours.addValueChangeHandler(e -> {
        	calendar.setAnualHours(e.getValue());
        	saveCalendar(false);
        });
        txtAnnuaPersonalDays.addValueChangeHandler(e -> {
        	calendar.setAnualPersonalDays(e.getValue());
        	saveCalendar(false);
        });
        txtAnnualHolidays.addValueChangeHandler(e -> {
        	calendar.setAnnualHolidays(e.getValue());
        	saveCalendar(false);
        });
        
        lbAnnualHolidays.addChangeHandler(e -> {
        	calendar.setHolidaysType(Byte.parseByte(lbAnnualHolidays.getValue()));
        	saveCalendar(false);
        });
        
        lbHoliday.addChangeHandler(e -> {
        	Holiday currentHoliday = calendar.getHoliday();
        	
        	if(AonStringUtils.isBlank(lbHoliday.getValue()))
        		currentHoliday.setHolidayParent(null);
        	
        	else {
        		
            	Optional<Holiday> selectedHolidayOpt = holidaysList.stream().filter(h -> h.getId().equals(Integer.parseInt(lbHoliday.getValue()))).findFirst();
            	
            	if(selectedHolidayOpt.isPresent()) {
            		
            		Holiday selectedHoliday = selectedHolidayOpt.get();
            		
            		if(null == currentHoliday || null == currentHoliday.getId()) {
                			
            			calendar.setHoliday(new Holiday()
            					.setDomain(domain)
            					.setDescription("Festivos " + calendar.getDescription())
            					.setDetails(new ArrayList<HolidayDetail>())
            					.setEditable(true)
            					.setHolidayParent(selectedHoliday.getId()));
                	
                		
                	} else {
                		
                		currentHoliday.setHolidayParent(selectedHoliday.getId());
                		
                	}
            		
            	} else currentHoliday.setHolidayParent(null);
        	}
        	
        	saveCalendar(true);
        });
        
        formContent.add(txtDescription);
        
        FlowPanel annualData = new FlowPanel();
        annualData.addStyleName("aon-annual-data");
        annualData.addStyleName(AON.CSS.aonItemFlex());
        annualData.add(txtAnnualHours);
        annualData.add(txtAnnuaPersonalDays);
        
        formContent.add(annualData);
        
        FlowPanel holidays = new FlowPanel();
        holidays.addStyleName("aon-holidays");
        holidays.addStyleName(AON.CSS.aonItemFlex());
        holidays.add(txtAnnualHolidays);
        holidays.add(lbAnnualHolidays);
        
        formContent.add(holidays);
        formContent.add(lbHoliday);
        formContent.add(txtComment);

        form.add(showHidePanel);
        form.add(formContent);
    }
    
    private void buildWorkingDays() {
    	formWorkingDays.clear();
	
    	Label workingDaysLabel = new Label("Horario Laboral");
    	workingDaysLabel.addStyleName("holiday-title");
    	formWorkingDays.add(workingDaysLabel);
    	
    	List<DayControl> days = Arrays.asList(
    		    createDay("L", calendar::isMonday, calendar::setMonday, calendar::getMondayHours, calendar::setMondayHours),
    		    createDay("M", calendar::isTuesday, calendar::setTuesday, calendar::getTuesdayHours, calendar::setTuesdayHours),
    		    createDay("X", calendar::isWednesday, calendar::setWednesday, calendar::getWednesdayHours, calendar::setWednesdayHours),
    		    createDay("J", calendar::isThursday, calendar::setThursday, calendar::getThursdayHours, calendar::setThursdayHours),
    		    createDay("V", calendar::isFriday, calendar::setFriday, calendar::getFridayHours, calendar::setFridayHours),
    		    createDay("S", calendar::isSaturday, calendar::setSaturday, calendar::getSaturdayHours, calendar::setSaturdayHours),
    		    createDay("D", calendar::isSunday, calendar::setSunday, calendar::getSundayHours, calendar::setSundayHours)
    		);


    	FlowPanel dialogContent = new FlowPanel();

    	FlowPanel daysRow = new FlowPanel();
    	daysRow.addStyleName("aon-workplace-calendar-working-row");
    	
    	Label daysLabel = new Label("D\u00edas:");
    	daysRow.add(daysLabel);

    	FlowPanel hoursRow = new FlowPanel();
    	hoursRow.addStyleName("aon-workplace-calendar-working-row");
    	
    	Label hoursLabel = new Label("Hrs.:");
    	hoursRow.add(hoursLabel);

    	for (DayControl d : days) {
    	    daysRow.add(d.dayButton);
    	    hoursRow.add(d.hours);
    	}

    	dialogContent.add(daysRow);
    	dialogContent.add(hoursRow);
    	formWorkingDays.add(dialogContent);
    	
    	workingDaysLabel.addClickHandler(e -> {
    		if(dialogContent.getStyleName().contains(AON.CSS.aonDisplayNone()))
    			dialogContent.removeStyleName(AON.CSS.aonDisplayNone());
    		else
    			dialogContent.addStyleName(AON.CSS.aonDisplayNone());
    	});
    	
    	form.add(formWorkingDays);
    }

    private DayControl createDay(
            String label,
            Supplier<Boolean> getterDay, Consumer<Boolean> setterDay,
            Supplier<Double> getterHours, Consumer<Double> setterHours) {

        DayControl d = new DayControl();
        d.getterDay = getterDay;
        d.setterDay = setterDay;
        d.setterHours = setterHours;

        // Botón del día
        d.dayButton = new Label(label);
        d.dayButton.addStyleName("aon-workplace-calendar-dayCircle");

        boolean nonWorking = getterDay.get();
        updateDayStyle(d.dayButton, nonWorking);

        // Caja de horas
        d.hours = new AonDoubleBox();
        d.hours.setValue(getterHours.get());
        d.hours.removeStyleName("aon_number_box");
        d.hours.getElement().getStyle().setProperty("text-align", "center");
        d.hours.setEnabled(!nonWorking);
        d.hours.addValueChangeHandler(e -> {
			d.setterDay.accept(d.getterDay.get());
            d.setterHours.accept(d.hours.getValue());
            saveCalendar(true);
		});

        // Evento click
        d.dayButton.addClickHandler(e -> {
            boolean newState = !d.getterDay.get(); // toggle
            d.setterDay.accept(newState);

            d.hours.setEnabled(!newState);
            if (newState) d.hours.setValue(null);

            updateDayStyle(d.dayButton, newState);
            
            d.setterDay.accept(d.getterDay.get());
            d.setterHours.accept(d.hours.getValue());
            saveCalendar(false);
        });

        return d;
    }
    
    private void updateDayStyle(Label day, boolean nonWorking) {
        if (nonWorking) day.addStyleName("nonWorking");
        else day.removeStyleName("nonWorking");
    }
    
    private void buildHolidayCollapsibles() {
    	
    	formCollapsibles.clear();
    	
    	Holiday selected = calendar.getHoliday();
        List<Holiday> chain = buildHolidayChain(selected, holidaysList);
        int totalCount = 0;
        
        Date startYear = DateUtils.getFirstDayOfYear(currentYear - 1900);
        Date endYear = DateUtils.getLastDayOfYear(currentYear - 1900);
        
        for (Holiday h : chain)
        	if (h.getDetails() != null) {
                for (HolidayDetail d : h.getDetails()) {
                    if (!d.getDate().before(startYear) && !d.getDate().after(endYear)) {
                    	totalCount++;
                    }
                }
            }
    	
    	Label festiveLabel = new Label("Festivos " + currentYear + " (" + totalCount + ")");
        festiveLabel.addStyleName("holiday-title");
        formCollapsibles.add(festiveLabel);
    	
    	FlowPanel formCollapsiblesContent = new FlowPanel();
    	
       for (Holiday h : chain) {

            DisclosurePanel dp = new DisclosurePanel();
            dp.setWidth("100%");
            dp.setOpen(false);

            FlowPanel header = new FlowPanel();
            header.addStyleName("holiday-header");

            AonToolbarSmallButton icon = new AonToolbarSmallButton("", AON.CSS.aonIconRight());
            icon.addStyleName("holiday-chevron");

            int count = 0;
            if (h.getDetails() != null) {
                for (HolidayDetail d : h.getDetails()) {
                    if (!d.getDate().before(startYear) && !d.getDate().after(endYear)) {
                        count++;
                    }
                }
            }

            Label title = new Label(h.getDescription() + " (" + count + ")");

            header.add(icon);
            header.add(title);

            dp.setHeader(header);

            dp.addOpenHandler(e -> icon.addStyleName("open"));
            dp.addCloseHandler(e -> icon.removeStyleName("open"));

            FlowPanel detailsPanel = new FlowPanel();

            if (h.getDetails() != null) {
                for (HolidayDetail d : h.getDetails()) {
                    if (!d.getDate().before(startYear) && !d.getDate().after(endYear)) {
                        detailsPanel.add(createHolidayDetailRow(d));
                    }
                }
            }

            if (detailsPanel.getWidgetCount() == 0) {
                Label empty = new Label("No existen festivos");
                empty.addStyleName("holiday-empty");
                detailsPanel.add(empty);
            }

            dp.setContent(detailsPanel);

            if (null != calendar.getHoliday().getId() && h.getId().equals(calendar.getHoliday().getId())) {
                dp.setOpen(true);
                icon.addStyleName("open");
            }

            formCollapsiblesContent.add(dp);
        }
        
        formCollapsibles.add(formCollapsiblesContent);
        
        festiveLabel.addClickHandler(e -> {
    		if(formCollapsiblesContent.getStyleName().contains(AON.CSS.aonDisplayNone()))
    			formCollapsiblesContent.removeStyleName(AON.CSS.aonDisplayNone());
    		else
    			formCollapsiblesContent.addStyleName(AON.CSS.aonDisplayNone());
    	});
        
        form.add(formCollapsibles);
    }

    private List<Holiday> buildHolidayChain(Holiday h, List<Holiday> all) {
        List<Holiday> chain = new ArrayList<>();
        Holiday current = h;

        while (current != null) {
            if( null != current.getId() ) chain.add(0, current);
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

    private FlowPanel createHolidayDetailRow(HolidayDetail d) {
        FlowPanel row = new FlowPanel();
        row.addStyleName("holiday-detail-row");

        boolean editable = d.getDomain().equals(domain);

        row.addStyleName(editable ? "holiday-own" : "holiday-inherited");

        Label dateLbl = new Label(DateTimeFormat.getFormat("dd/MM/yyyy").format(d.getDate()));
        dateLbl.addStyleName("holiday-detail-date");

        Label descLbl = new Label(d.getDescription());
        descLbl.addStyleName("holiday-detail-desc");

        row.add(dateLbl);
        row.add(descLbl);

        if (editable) {
            AonTableButton delete = new AonTableButton("Eliminar", AON.CSS.aonIconDelete());

            row.addDomHandler(e -> {
            	e.stopPropagation();
            	editHolidayDetail(d);
            }, ClickEvent.getType());
            
            delete.addClickHandler(e -> {
            	e.stopPropagation();
            	deleteHolidayDetail(d);
            });

            row.add(delete);
        }

        return row;
    }

    private void editHolidayDetail(HolidayDetail d) {
    	FlowPanel dialogContent = new FlowPanel();
    	
    	AonCustomDateBox dateInput = new AonCustomDateBox("Fecha");
    	dateInput.setValue(d.getDate());
    	
    	AonCustomTextBox descriptionInput = new AonCustomTextBox("Descripci\u00f3n");
    	descriptionInput.setValue(d.getDescription());
    	
    	dialogContent.add(dateInput);
    	dialogContent.add(descriptionInput);
    	
    	AonDialog dialog = new AonDialog("Crear Festivo", dialogContent);
    	dialog.confirm(new AonAcceptDialogCallback() {
			
			@Override
			public void onCancel() {}
			
			@Override
			public void onAccept() {
				if(null == dateInput.getValue() || AonStringUtils.isBlank(descriptionInput.getValue()))
					return;
				
				d.setDate(dateInput.getValue())
				 .setDescription(descriptionInput.getValue());
				
				d.setDirty(true);
				
				saveCalendar(true);
				
			}
		});
    }
    
    private void openDateDialog(Date date) {
		if(null != calendar.getHoliday().getDetails()) {
			Optional<HolidayDetail> holidayDetail = calendar.getHoliday().getDetails().stream().filter(hd -> hd.getDate().equals(date)).findFirst();
			if(holidayDetail.isPresent()) {
				editHolidayDetail(holidayDetail.get());
				return;
			}
		}
    	
    	FlowPanel dialogContent = new FlowPanel();
    	
    	AonCustomDateBox dateInput = new AonCustomDateBox("Fecha");
    	dateInput.setValue(date);
    	
    	AonCustomTextBox descriptionInput = new AonCustomTextBox("Descripci\u00f3n");
    	
    	dialogContent.add(dateInput);
    	dialogContent.add(descriptionInput);
    	
    	AonDialog dialog = new AonDialog("Crear Festivo", dialogContent);
    	dialog.confirm(new AonAcceptDialogCallback() {
			
			@Override
			public void onCancel() {}
			
			@Override
			public void onAccept() {
				if(null == calendar.getHoliday() || null == calendar.getHoliday().getId()) {
					calendar.setHoliday(new Holiday()
        					.setDomain(domain)
        					.setDescription("Festivos " + calendar.getDescription())
        					.setDetails(new ArrayList<HolidayDetail>())
        					.setEditable(true))
							;
				}
				
				if(null == dateInput.getValue() || AonStringUtils.isBlank(descriptionInput.getValue()))
					return;
				
				HolidayDetail newHolidayDetail = new HolidayDetail()
						.setDate(dateInput.getValue())
						.setDescription(descriptionInput.getValue())
						.setDomain(domain)
						.setHoliday(calendar.getHoliday().getId())
						;
				
				calendar.getHoliday().getDetails().add(newHolidayDetail);
				saveCalendar(true);
				
			}
		});
    }

    private void deleteHolidayDetail(HolidayDetail d) {
        AonDialog dialog = new AonDialog("Festivo", new Label("\u00bfEliminar este festivo, " + d.getDescription() + "?"));
        dialog.confirm(new AonAcceptDialogCallback() {

            @Override
            public void onCancel() {}

            @Override
            public void onAccept() {
                service.deleteHolidayDetail(d.getId(), new AsyncCallback<Void>() {
                    @Override public void onSuccess(Void result) {
                        AonMessagePanel.showSuccess(messagePanel, "Festivo eliminado");
                        loadData();
                    }
                    @Override public void onFailure(Throwable caught) {
                        AonMessagePanel.showError(messagePanel, "Error eliminando festivo: " + caught.getMessage());
                    }
                });
            }
        });
    }
    
    private void saveCalendar(boolean showSuccess) {
    	AonMessagePanel.showLoading(messagePanel, "Guardando calendario");
    	service.saveCalendar(calendar, new AsyncCallback<Calendar>() {
            @Override public void onSuccess(Calendar newCalendar) {
            	
            	if(calendar.getId() == null) {
            		service.setPayrollWorkplaceCalendar(workplace.getId(), newCalendar.getId(), new AsyncCallback<Void>() {
                        @Override public void onSuccess(Void result) {
                        	AonMessagePanel.hideMessage(messagePanel);
                            if(showSuccess) AonMessagePanel.showSuccess(messagePanel, "Calendario actualizado");
                            loadData();
                        }
                        @Override public void onFailure(Throwable caught) {
                            AonMessagePanel.showError(messagePanel, "Error actualizando calendario: " + caught.getMessage());
                        }
                    });
            	}
            	
            	
            	AonMessagePanel.hideMessage(messagePanel);
                if(showSuccess) AonMessagePanel.showSuccess(messagePanel, "Calendario actualizado");
                loadData();
            }
            @Override public void onFailure(Throwable caught) {
                AonMessagePanel.showError(messagePanel, "Error actualizando calendario: " + caught.getMessage());
            }
        });
    }

    private void buildAnnualCalendar() {
        centerPanel.clear();

        AnnualCalendarWidget annual = new AnnualCalendarWidget(
        	domain,
            calendar,
            holidaysList,
            currentYear,
            newYear -> {
                currentYear = newYear;
                loadData();
            },
            selectedDate -> openDateDialog(selectedDate)
        );

        ScrollPanel scroll = new ScrollPanel(annual);
        scroll.setHeight("100%");

        centerPanel.setWidget(scroll);
    }
}
