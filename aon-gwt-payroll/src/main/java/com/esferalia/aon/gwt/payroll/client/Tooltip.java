package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson.DischargeCause;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson.Type;
import com.esferalia.aon.gwt.payroll.shared.StringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarModel;
import com.google.gwt.user.datepicker.client.CalendarView;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.user.datepicker.client.DefaultCalendarView;
import com.google.gwt.user.datepicker.client.DefaultMonthSelector;

public class Tooltip extends DecoratedPopupPanel {

	interface Style extends CssResource {

		@ClassName("legend-icon")
		String legendIcon();

	}

	private static TooltipUiBinder uiBinder = GWT.create(TooltipUiBinder.class);	

	@UiField Style style;	
	
	@UiField ListBox typeListBox;	

	@UiField InlineLabel socialSecurityNum;	

	@UiField InlineLabel fullNameLabel;	

	@UiField InlineLabel dniLabel;	

	@UiField HTML iconStatusLabel;	

	@UiField InlineLabel statusLabel;	
	
	@UiField InlineLabel workPeriodLabel;	
	
	@UiField InlineLabel numDaysLabel;	

	@UiField InlineLabel startDateLabel;	
	
	@UiField InlineLabel endDateLabel;	

	@UiField InlineLabel causeLabel;	
	
	@UiField DateBox fromDateBox;	
	
	@UiField DateBox startLeaveDateBox;	
	
	@UiField TextBox startLeaveTextBox;
	
	@UiField TextBox endLeaveTextBox;

	@UiField Button acceptButton;
	
	@UiField InlineLabel daysLabel;
	
	private static final DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG);
	
	
	private Date startContract;
	private Date endContract;
	
	/**
	 *  0 - contract active
	 *  1 - contract ended
	 *  2 - leave active
	 *  3 - leave ended
  	**/

	private Date contractStartDate;	
	private int dischargeCause;
	
	interface TooltipUiBinder extends UiBinder<Widget, Tooltip> {
	}

	public Tooltip() {

		setGlassEnabled(false);
		setStyleName(AON.AON_TOOLTIP);
		add(uiBinder.createAndBindUi(this));		
		
		startLeaveDateBox.setFormat(new DateBox.DefaultFormat(AON.DATE_FORMAT));		
		fromDateBox.setFormat(new DateBox.DefaultFormat(AON.DATE_FORMAT));
		
		startLeaveDateBox.getTextBox().setReadOnly(true);
		fromDateBox.getTextBox().setReadOnly(true);
		
		acceptButton.setVisible(false);
		
		

		setAutoHideEnabled(true);
		
	/*	fromDateBox.getDatePicker().addShowRangeHandler(new ShowRangeHandler<Date>() {
			
			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {
				
				setValidDates(event);
				
			}
		});*/

		
	}
	
/*	private void setValidDates(ShowRangeEvent<Date> dateShowRangeEvent) {
		
		Date start = dateShowRangeEvent.getStart();
		Date end = dateShowRangeEvent.getEnd();
		
		int between = DateUtils.getDaysBetween(start, end);
		
		for(int i = 0; i < between; i++) {
			
			Date date = new Date(start.getTime());
			CalendarUtil.addDaysToDate(date, 1);
			setDataPickable(date);
			
		}
	}
	
	
	private void setDataPickable(Date date) {
		
		boolean enabled = true;
		
		if(date.before(contractStartDate)) {
			enabled = false;
		}
	
		fromDateBox.getDatePicker().setTransientEnabledOnDates(enabled, date);		
		
	}*/

	protected void onPreviewNativeEvent(final NativePreviewEvent event) {
		super.onPreviewNativeEvent(event);
		switch (event.getTypeInt()) {

		case Event.ONKEYDOWN:

			if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {

				hide();
			}

			break;

		default:
			break;
		}
	}
	
	public void showContractActiveTooltip(final int clientX, final int clientY) {
		
		addLeaveDescriptions();
		
		startLeaveTextBox.setVisible(false);
		endLeaveTextBox.setVisible(false);
		
		startDateLabel.setVisible(false);
		startLeaveDateBox.setVisible(false);
		
		endDateLabel.setText("Fecha Baja: ");		
		fromDateBox.setValue(new Date());
		
		causeLabel.setText("Motivo Baja: ");

		showToolTip(clientX, clientY);
	}
	
	public void showContractEndedTooltip(final int clientX, final int clientY) {
		
		startLeaveTextBox.setVisible(false);
		endLeaveTextBox.setVisible(false);
		startDateLabel.setVisible(false);
		startLeaveDateBox.setVisible(false);
		endDateLabel.setVisible(false);
		fromDateBox.setVisible(false);
		causeLabel.setVisible(false);
		typeListBox.setVisible(false);
	//	acceptButton.setVisible(false);
		
		showToolTip(clientX, clientY);
	}		

	
	public void showContractLeaveActiveTooltip(final int clientX, final int clientY) {
		
		addLeaveEndedDescriptions();
		
		startLeaveTextBox.setVisible(false);
		endLeaveTextBox.setVisible(false);
		
		workPeriodLabel.setVisible(false);
		
		startDateLabel.setText("Fecha Baja:");
		startLeaveDateBox.setValue(getStartContract());
		
		endDateLabel.setText("Fecha Alta");
		fromDateBox.setValue(new Date());
		
		causeLabel.setText("Motivo Alta: ");
		typeListBox.setItemSelected(getDischargeCause(), true);
		showToolTip(clientX, clientY);
		
	}
	
	public void showContractLeaveEndedTooltip(final int clientX, final int clientY) {
		
		addLeaveEndedDescriptions();
		
		workPeriodLabel.setVisible(false);

		startDateLabel.setText("Fecha Baja:");
		startLeaveDateBox.setVisible(false);
		startLeaveTextBox.setText(format.format(getStartContract()));
		startLeaveTextBox.setReadOnly(true);
		
		endDateLabel.setText("Fecha Alta");		
		fromDateBox.setVisible(false);
		endLeaveTextBox.setText(format.format(getEndContract()));
		endLeaveTextBox.setReadOnly(true);
		
		causeLabel.setText("Motivo Alta: ");		
		typeListBox.setItemSelected(getDischargeCause(), true);	
		typeListBox.setEnabled(false);
	//	acceptButton.setVisible(false);
		
		showToolTip(clientX, clientY);		
		
	}

	private void showToolTip(final int clientX, final int clientY) {

		try {

			setPopupPositionAndShow(new PopupPanel.PositionCallback() {

				@Override
				public void setPosition(int offsetWidth, int offsetHeight) {

					int windowWidth = Window.getClientWidth();
					int popupX = clientX - offsetWidth / 3;
					int popupY = clientY;

					if (popupX + offsetWidth >= windowWidth)
						popupX -= popupX + offsetWidth - windowWidth ;

					if (clientY + offsetHeight >= Window.getClientHeight()) {
						popupY = popupY - offsetHeight;
					}

					setPopupPosition(popupX, popupY);
				}
			});

			show();

		} catch (Throwable ex) {
			Window.alert("Error " + ex.getStackTrace() + " " + ex.getMessage());
		}
	}
	
	private void addLeaveDescriptions() {
		
		typeListBox.addItem("-");

		for (ITDataPerson.Type type : Type.values()) {

			if (StringUtils.equals(type.getDescription(), statusLabel.getText()) == false)
				typeListBox.addItem(type.getDescription());
		}
		typeListBox.setItemSelected(0, false);
	}
	
	private void addLeaveEndedDescriptions() {
		
		typeListBox.addItem("-");
		
		for(ITDataPerson.DischargeCause cause : DischargeCause.values()) {
			
			typeListBox.addItem(cause.getDescription());			
		}
		typeListBox.setItemSelected(0, false);
	}

	// ------------------------------------------------------------- UiHandlers

	@UiHandler("fromDateBox")
	void onValueChangeFromDateBox(ValueChangeEvent<Date> event) {

		if (event.getValue().before(contractStartDate)) {
			Window.alert("Comienzo de " + statusLabel.getText() + " - "
					+ AON.DATE_FORMAT.format(contractStartDate));
			fromDateBox.setValue(new Date());
		}
	}
	
	@UiHandler("startLeaveDateBox")
	void onValueChangeStartDateBox(ValueChangeEvent<Date> event) {
		
		if (event.getValue().before(contractStartDate)) {
			Window.alert("Comienzo de " + statusLabel.getText() + " - "
					+ AON.DATE_FORMAT.format(contractStartDate));
			fromDateBox.setValue(new Date());
		}
	}
	
	private boolean accept = false;

	@UiHandler("acceptButton")
	void onAcceptClick(ClickEvent event) {
		
		if(typeListBox.getSelectedIndex() > 0) {
			
			accept = true;
			hide();
		}

	}
	
	public boolean isAccept() {
		return accept;
	}
	
	public Date getFromDateBoxValue() {
		return fromDateBox.getValue();
	}
	public int getTypeListBox() {		
		
		return typeListBox.getSelectedIndex();		
	}
	
	public Date getStartDateBoxValue() {
		return startLeaveDateBox.getValue();
	}

	// ------------------------------------------------------------------------

	public void setFullName(String pFullName) {
		fullNameLabel.setText(pFullName);
	}

	public void setDNI(String pDni) {
		dniLabel.setText(pDni);
	}

	public void setSocialSecurity(String pSocialSecurity) {
		socialSecurityNum.setText(pSocialSecurity);
	}

	public void setStatus(String pStatus) {
		statusLabel.setText(pStatus);
	
	}
	
	public void setStart(Date pStart) {
		this.startContract = pStart;
	}
	
	public void setFinish(Date pFinish) {
		this.endContract = pFinish;
	}

	public void setColor(String background) {

		iconStatusLabel.setStyleName(style.legendIcon(), true);
		iconStatusLabel.getElement().getStyle().setBackgroundColor(background);

	}

	public void setWorkPeriod(String pStartDate, String pEndDate) {	
		
		workPeriodLabel.setText(pStartDate + " - " + pEndDate);
	}

	public void setWorkPeriodNumDays(Date start, Date finish) {
		
		this.startContract = start;
		this.endContract = finish;
		
		int days = DateUtils.getDaysBetween(start, (finish == null ? new Date() : finish )) +1;
		
		numDaysLabel.setText(Integer.toString(days));
		
		if(days == 1) {
			daysLabel.setText("d\u00EDa");
		}
		else {
			daysLabel.setText("d\u00EDas");
		}
		
	}
	
	public Date getStartContract() {
		return startContract;
	}
	public Date getEndContract() {
		
		if(endContract == null)
			return new Date();
		else 
			return endContract;
	}

	public void setContractStartDate(Date pContractStart) {
		contractStartDate = pContractStart;
	}
	
	public void setDischargeCause(int pDischargeCause) {
		dischargeCause = pDischargeCause;
	}
	
	public int getDischargeCause() {
		return dischargeCause;
	}
	
	
	public class DatePickerEnhanced extends DatePicker {
		
	    public DatePickerEnhanced newInstance() {
	        DefaultMonthSelector monthSelector = new DefaultMonthSelector();
	        CalendarView view = new DefaultCalendarView();
	        CalendarModel model = new CalendarModel();
	        return new DatePickerEnhanced(monthSelector, view, model);
	    }

	    public DatePickerEnhanced() {
	        super(new DefaultMonthSelector(), new DefaultCalendarView(), new CalendarModel());
	    }

	    public DatePickerEnhanced(DefaultMonthSelector monthSelector, CalendarView view, CalendarModel model) {
	        super(monthSelector, view, model);
	    }

	    public CalendarView getCalendarView() {
	        return getView();
	    }
	}
}
