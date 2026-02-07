package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AnnualCalendarWidget;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCheckBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomNumberBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
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

    private static final Logger LOGGER = Logger.getLogger(WorkplaceCalendar.class.getName());
    static { LOGGER.addHandler(new ConsoleLogHandler()); }

    private HTMLPanel container;
    private HTMLPanel messagePanel = new HTMLPanel("");

    private SimpleLayoutPanel centerPanel;
    private ScrollPanel westPanel = new ScrollPanel();
   
    private FlowPanel form = new FlowPanel();
    private FlowPanel formContent = new FlowPanel();
    private FlowPanel formCollapsibles = new FlowPanel();

    private AonCustomTextBox txtDescription = new AonCustomTextBox("Descripci\u00f3n");
    private AonCustomTextBox txtComment = new AonCustomTextBox("Comentarios");
    private AonCustomNumberBox txtAnnualHours = new AonCustomNumberBox("Horas anuales");
    private AonCustomNumberBox txtAnnualHolidays = new AonCustomNumberBox("Vacaciones anuales");
    private AonCustomListBox lbAnnualHolidays = new AonCustomListBox("T. Vacaciones");
    private AonCustomListBox lbHoliday = new AonCustomListBox("Festivo asociado");


    private AonToolbarButton showHide = new AonToolbarButton("", AON.CSS.aonIconMenuCollapse());
    private AonToolbarButton createFestive = new AonToolbarButton("Nuevo Festivo", AON.CSS.aonIconEditCalendar());
    private AonToolbarButton workingDays = new AonToolbarButton("Horario Laboral", AON.CSS.aonIconCalendarClock());

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

    public WorkplaceCalendar(Workplace workplace) {
        super(workplace.getDescription());
        this.workplace = workplace;

        hideToolbarFilterMessages();
        hideSearchWidget();

        loaderOverlay.setStyleName("loader-overlay");
        add(loaderOverlay);

        container = new HTMLPanel("");
        container.addStyleName(AON.CSS.aonFlexColumn());
        container.add(messagePanel);

        centerPanel = new SimpleLayoutPanel();
        centerPanel.setHeight("100%");
        centerPanel.getElement().getStyle().setProperty("position", "relative");

        container.add(centerPanel);

        westPanel.addStyleName(AON.CSS.aonFlexColumn());
        addWest(westPanel, 300);

        add(container);

        loadData();
    }

    @Override
    protected void onClearFilter() {}

    private void addButtonsToolbar() {
        showHide.addClickHandler(e -> {
            if (!westShow) {
                setWidgetSize(westPanel, 300);
                animate(300);
                showHide.removeStyleName(AON.CSS.aonIconMenu());
                showHide.addStyleName(AON.CSS.aonIconMenuCollapse());
            } else {
                setWidgetSize(westPanel, 0);
                animate(300);
                showHide.removeStyleName(AON.CSS.aonIconMenuCollapse());
                showHide.addStyleName(AON.CSS.aonIconMenu());
            }
            westShow = !westShow;
        });
        
        createFestive.addClickHandler(e -> openDateDialog(null));
        workingDays.addClickHandler(e -> openWorkingDaysDialog());

        createFestive.setEnabled(null != calendar.getId());
        workingDays.setEnabled(null != calendar.getId());
        
        addToolbarButton(showHide);
        addToolbarButton(createFestive);
        addToolbarButton(workingDays);
    }

    private void loadData() {
        getDomain(d -> {
            getHolidays(h -> {
                getCalendar(c -> {
                	if(c.isEmpty()) calendar = new Calendar().setDomain(domain);
                	else calendar = c.get(0);

                    addButtonsToolbar();
                    setToolbarTitle(calendar.getDescription());

                    buildForm();
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
                holidaysList = result;
                success.accept(result);
            }
            @Override public void onFailure(Throwable caught) {
            	loaderOverlay.setVisible(false);
                AonMessagePanel.showError(messagePanel, "Error obteniendo festivos: " + caught.getMessage());
            }
        });
    }

    private void buildForm() {
        westPanel.clear();
        westPanel.getElement().getStyle().setProperty("margin-left", "1rem");

        form.clear();
        formContent.clear();
        
        txtDescription = new AonCustomTextBox("Descripci\u00f3n");
        txtComment = new AonCustomTextBox("Comentarios");
        txtAnnualHours = new AonCustomNumberBox("Horas anuales");
        txtAnnualHolidays = new AonCustomNumberBox("Vacaciones anuales");
        lbAnnualHolidays = new AonCustomListBox("T. Vacaciones");
        lbHoliday = new AonCustomListBox("Festivo asociado");

        txtAnnualHours.hideNearBy();
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
            
            txtAnnualHolidays.setValue(calendar.getAnnualHolidays());
            
            lbAnnualHolidays.setValue(null == calendar.getHolidaysType() ? "0" :calendar.getHolidaysType().toString());
        	lbHoliday.setValue(null != calendar.getHoliday() && null != calendar.getHoliday().getId() ? calendar.getHoliday().getId().toString() : "");
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
        	Optional<Holiday> selectedHolidayOpt = holidaysList.stream().filter(h -> h.getId().equals(Integer.parseInt(lbHoliday.getValue()))).findFirst();
        	
        	if(selectedHolidayOpt.isPresent()) {
        		
        		Holiday selectedHoliday = selectedHolidayOpt.get();
        		
        		if(null == currentHoliday || null == currentHoliday.getId()) {
            			
        			if(selectedHoliday.getDomain().equals(domain))
        				calendar.setHoliday(selectedHoliday);
        			else {
        				
        				Optional<Holiday> ownSelectedHolidayOpt = holidaysList.stream().filter(
        							h -> null != h.getHolidayParent() && h.getHolidayParent().equals(Integer.parseInt(lbHoliday.getValue())) && h.getDomain().equals(domain)
        						).findFirst();
        				
        				if(ownSelectedHolidayOpt.isPresent()) calendar.setHoliday(ownSelectedHolidayOpt.get());
        				else
	        				calendar.setHoliday(new Holiday()
	            					.setDomain(domain)
	            					.setDescription("Festivos Propios (" + selectedHoliday.getDescription() + ")")
	            					.setDetails(new ArrayList<HolidayDetail>())
	            					.setEditable(true)
	            					.setHolidayParent(selectedHoliday.getId()));
        			}
            	
            		
            	} else {
            		
            		if(selectedHoliday.getDomain().equals(domain))
            			calendar.setHoliday(selectedHoliday);
            		else {
            			Optional<Holiday> ownSelectedHolidayOpt = holidaysList.stream().filter(
    							h -> null != h.getHolidayParent() && h.getHolidayParent().equals(Integer.parseInt(lbHoliday.getValue())) && h.getDomain().equals(domain)
    						).findFirst();
    				
	    				if(ownSelectedHolidayOpt.isPresent()) calendar.setHoliday(ownSelectedHolidayOpt.get());
	    				else
	        				calendar.setHoliday(new Holiday()
	            					.setDomain(domain)
	            					.setDescription("Festivos Propios (" + selectedHoliday.getDescription() + ")")
	            					.setDetails(new ArrayList<HolidayDetail>())
	            					.setEditable(true)
	            					.setHolidayParent(selectedHoliday.getId()));
	            		}
        				
        			
            	}
        		
        	}
        	
        	saveCalendar(true);
        });
        
        formContent.add(txtDescription);
        formContent.add(txtComment);
        formContent.add(txtAnnualHours);
        
        FlowPanel holidays = new FlowPanel();
        holidays.addStyleName(AON.CSS.aonItemFlex());
        holidays.add(txtAnnualHolidays);
        holidays.add(lbAnnualHolidays);
        
        formContent.add(holidays);
        formContent.add(lbHoliday);

        Label festiveLabel = new Label("Festivos:");
        festiveLabel.getElement().getStyle().setProperty("margin", "1rem 0 .5rem 0");
        formContent.add(festiveLabel);
        
        form.add(formContent);
        form.add(formCollapsibles);
    }

    private void buildHolidayCollapsibles() {
    	
    	formCollapsibles.clear();
    	
        Holiday selected = calendar.getHoliday();
        List<Holiday> chain = buildHolidayChain(selected, holidaysList);

        Date startYear = DateUtils.getFirstDayOfYear(currentYear - 1900);
        Date endYear = DateUtils.getLastDayOfYear(currentYear - 1900);

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
            title.addStyleName("holiday-title");

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
                Label empty = new Label("No hay festivos para el a\u00f1o en curso");
                empty.addStyleName("holiday-empty");
                detailsPanel.add(empty);
            }

            dp.setContent(detailsPanel);

            if (null != calendar.getHoliday().getId() && h.getId().equals(calendar.getHoliday().getId())) {
                dp.setOpen(true);
                icon.addStyleName("open");
            }

            formCollapsibles.add(dp);
        }
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
    
    private void openWorkingDaysDialog() {
		FlowPanel dialogContent = new FlowPanel();
		
		// Monday
		FlowPanel mondayContent = new FlowPanel();
		mondayContent.addStyleName(AON.CSS.aonItemFlex());
    	
    	AonCustomCheckBox mondayCB = new AonCustomCheckBox("Lunes");
    	mondayCB.setWidth("6rem");
    	mondayCB.setValue(calendar.isMonday());
    	
    	AonCustomNumberBox mondayHours = new AonCustomNumberBox("Horas Lunes");
    	mondayHours.hideNearBy();
    	mondayHours.setEnable(calendar.isMonday());
    	mondayHours.setValue(calendar.getMondayHours());
    	
    	mondayContent.add(mondayCB);
    	mondayContent.add(mondayHours);
    	dialogContent.add(mondayContent);
    	
    	// Tuesday
		FlowPanel tuesdayContent = new FlowPanel();
		tuesdayContent.addStyleName(AON.CSS.aonItemFlex());
    	
    	AonCustomCheckBox tuesdayCB = new AonCustomCheckBox("Martes");
    	tuesdayCB.setWidth("6rem");
    	tuesdayCB.setValue(calendar.isTuesday());
    	
    	AonCustomNumberBox tuesdayHours = new AonCustomNumberBox("Horas Martes");
    	tuesdayHours.hideNearBy();
    	tuesdayHours.setEnable(calendar.isTuesday());
    	tuesdayHours.setValue(calendar.getTuesdayHours());
    	
    	tuesdayContent.add(tuesdayCB);
    	tuesdayContent.add(tuesdayHours);
    	dialogContent.add(tuesdayContent);
    	
    	// Wednesday
		FlowPanel wednesdayContent = new FlowPanel();
		wednesdayContent.addStyleName(AON.CSS.aonItemFlex());
    	
    	AonCustomCheckBox wednesdayCB = new AonCustomCheckBox("Miercoles");
    	wednesdayCB.setWidth("6rem");
    	wednesdayCB.setValue(calendar.isWednesday());
    	
    	AonCustomNumberBox wednesdayHours = new AonCustomNumberBox("Horas Miercoles");
    	wednesdayHours.hideNearBy();
    	wednesdayHours.setEnable(calendar.isWednesday());
    	wednesdayHours.setValue(calendar.getWednesdayHours());
    	
    	wednesdayContent.add(wednesdayCB);
    	wednesdayContent.add(wednesdayHours);
    	dialogContent.add(wednesdayContent);
    	
    	// Thursday
		FlowPanel thursdayContent = new FlowPanel();
		thursdayContent.addStyleName(AON.CSS.aonItemFlex());
    	
    	AonCustomCheckBox thursdayCB = new AonCustomCheckBox("Jueves");
    	thursdayCB.setWidth("6rem");
    	thursdayCB.setValue(calendar.isThursday());
    	
    	AonCustomNumberBox thursdayHours = new AonCustomNumberBox("Horas Jueves");
    	thursdayHours.hideNearBy();
    	thursdayHours.setEnable(calendar.isThursday());
    	thursdayHours.setValue(calendar.getThursdayHours());
    	
    	thursdayContent.add(thursdayCB);
    	thursdayContent.add(thursdayHours);
    	dialogContent.add(thursdayContent);
    	
    	// Friday
		FlowPanel fridayContent = new FlowPanel();
		fridayContent.addStyleName(AON.CSS.aonItemFlex());
    	
    	AonCustomCheckBox fridayCB = new AonCustomCheckBox("Viernes");
    	fridayCB.setWidth("6rem");
    	fridayCB.setValue(calendar.isFriday());
    	
    	AonCustomNumberBox fridayHours = new AonCustomNumberBox("Horas Viernes");
    	fridayHours.hideNearBy();
    	fridayHours.setEnable(calendar.isFriday());
    	fridayHours.setValue(calendar.getFridayHours());
    	
    	fridayContent.add(fridayCB);
    	fridayContent.add(fridayHours);
    	dialogContent.add(fridayContent);
		
      	// Saturday
		FlowPanel saturdayContent = new FlowPanel();
		saturdayContent.addStyleName(AON.CSS.aonItemFlex());
    	
    	AonCustomCheckBox saturdayCB = new AonCustomCheckBox("Sabado");
    	saturdayCB.setWidth("6rem");
    	saturdayCB.setValue(calendar.isSaturday());
    	
    	AonCustomNumberBox saturdayHours = new AonCustomNumberBox("Horas Sabado");
    	saturdayHours.hideNearBy();
    	saturdayHours.setEnable(calendar.isSaturday());
    	saturdayHours.setValue(calendar.getSaturdayHours());
    	
    	saturdayContent.add(saturdayCB);
    	saturdayContent.add(saturdayHours);
    	dialogContent.add(saturdayContent);
    	
      	// Sunday
		FlowPanel sundayContent = new FlowPanel();
		sundayContent.addStyleName(AON.CSS.aonItemFlex());
    	
    	AonCustomCheckBox sundayCB = new AonCustomCheckBox("Domingo");
    	sundayCB.setWidth("6rem");
    	sundayCB.setValue(calendar.isSunday());
    	
    	AonCustomNumberBox sundayHours = new AonCustomNumberBox("Horas Domingo");
    	sundayHours.hideNearBy();
    	sundayHours.setEnable(calendar.isSunday());
    	sundayHours.setValue(calendar.getSundayHours());
    	
    	sundayContent.add(sundayCB);
    	sundayContent.add(sundayHours);
    	dialogContent.add(sundayContent);
    	
    	mondayCB.addValueChangeHandler(e -> {
    		if(!e.getValue()) mondayHours.setValue(null);
    		mondayHours.setEnable(e.getValue());
    	});
    	
    	tuesdayCB.addValueChangeHandler(e -> {
    		if(!e.getValue()) tuesdayHours.setValue(null);
    		tuesdayHours.setEnable(e.getValue());
    	});
    	
    	wednesdayCB.addValueChangeHandler(e -> {
    		if(!e.getValue()) wednesdayHours.setValue(null);
    		wednesdayHours.setEnable(e.getValue());
    	});
    	
    	thursdayCB.addValueChangeHandler(e -> {
    		if(!e.getValue()) thursdayHours.setValue(null);
    		thursdayHours.setEnable(e.getValue());
    	});
    	
    	fridayCB.addValueChangeHandler(e -> {
    		if(!e.getValue()) fridayHours.setValue(null);
    		fridayHours.setEnable(e.getValue());
    	});
    	
    	saturdayCB.addValueChangeHandler(e -> {
    		if(!e.getValue()) saturdayHours.setValue(null);
    		saturdayHours.setEnable(e.getValue());
    	});
    	
    	sundayCB.addValueChangeHandler(e -> {
    		if(!e.getValue()) sundayHours.setValue(null);
    		sundayHours.setEnable(e.getValue());
    	});
		
		AonDialog dialog = new AonDialog("Horario Laboral", dialogContent);
    	dialog.confirm(new AonAcceptDialogCallback() {
			
			@Override
			public void onCancel() {}
			
			@Override
			public void onAccept() {
				
				calendar.setMonday(mondayCB.getValue());
				calendar.setMondayHours(mondayHours.getValue());
				
				calendar.setTuesday(tuesdayCB.getValue());
				calendar.setTuesdayHours(tuesdayHours.getValue());
				
				calendar.setWednesday(wednesdayCB.getValue());
				calendar.setWednesdayHours(wednesdayHours.getValue());
				
				calendar.setThursday(thursdayCB.getValue());
				calendar.setThursdayHours(thursdayHours.getValue());
				
				calendar.setFriday(fridayCB.getValue());
				calendar.setFridayHours(fridayHours.getValue());
				
				calendar.setSaturday(saturdayCB.getValue());
				calendar.setSaturdayHours(saturdayHours.getValue());
				
				calendar.setSunday(sundayCB.getValue());
				calendar.setSundayHours(sundayHours.getValue());
				
				saveCalendar(true);
				
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
                buildAnnualCalendar();
            },
            selectedDate -> openDateDialog(selectedDate)
        );

        ScrollPanel scroll = new ScrollPanel(annual);
        scroll.setHeight("100%");

        centerPanel.setWidget(scroll);
    }
}
