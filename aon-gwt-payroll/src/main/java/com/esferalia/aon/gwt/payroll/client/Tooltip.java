package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson.DischargeCause;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson.Type;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class Tooltip extends DecoratedPopupPanel {

	interface Style extends CssResource {
		@ClassName("legend-icon")
		String legendIcon();
	}
	
	interface Listener {
		
		void onStartDateChangeEvent();
		void onEndDateChangeEvent();
	}

	private static TooltipUiBinder uiBinder = GWT.create(TooltipUiBinder.class);

	@UiField
	Style style;
	
	@UiField
	Label baja;
	
	@UiField
	Label alta;

	@UiField
	ListBox typeLeaveListBox;

	@UiField
	ListBox typeDischargeListBox;

	@UiField
	InlineLabel socialSecurityNum;

	@UiField
	InlineLabel fullNameLabel;

	@UiField
	InlineLabel dniLabel;

	@UiField
	HTML iconStatusLabel;

	@UiField
	InlineLabel statusLabel;

	@UiField
	InlineLabel workPeriodLabel;

	@UiField
	InlineLabel numDaysLabel;

	@UiField
	InlineLabel startDateLabel;

	@UiField
	InlineLabel endDateLabel;

	@UiField
	InlineLabel causeStartLabel;

	@UiField
	InlineLabel causeEndLabel;

	@UiField
	DateBox endDateBox;

	@UiField
	DateBox startLeaveDateBox;

	@UiField
	Button acceptButton;

	@UiField
	InlineLabel daysLabel;
	
	@UiField Label startDate;
	@UiField Label typeLeave;
	@UiField Label endDate;
	@UiField Label typeDischarge;
	
	private List<Listener> listeners;
	
	private final String ACTIVE = "Activo";

	private Date startContract;
	private Date endContract;
	
	private boolean changes;
	
	private int dischargeCause;

	interface TooltipUiBinder extends UiBinder<Widget, Tooltip> {
	}

	public Tooltip() {

		setGlassEnabled(false);
		setStyleName(AON.AON_TOOLTIP);
		add(uiBinder.createAndBindUi(this));
		changes = false;
		startLeaveDateBox.setFormat(new DateBox.DefaultFormat(AON.DATE_FORMAT));
		startLeaveDateBox.setWidth("6em");
	
		endDateBox.setFormat(new DateBox.DefaultFormat(AON.DATE_FORMAT));
		endDateBox.setWidth("6em");				
		
		loadTypeListBox();
		setAutoHideEnabled(true);
		
		startLeaveDateBox.getTextBox().addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {				
				changes = true;								
			}
		});
		
		endDateBox.getTextBox().addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				changes = true;				
			}
		});
		
		typeLeaveListBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				changes = true;				
			}
		});

		typeDischargeListBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				changes = true;
				
			}
		});
	}

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

		startDate.setVisible(false);
		typeLeave.setVisible(false);
		endDate.setVisible(false);
		typeDischarge.setVisible(false);
		
		startDateLabel.setText("Fecha");
		
		causeStartLabel.setText("Motivo");
		typeLeaveListBox.setItemSelected(0, true);

		endDateLabel.setText("Fecha");
		causeEndLabel.setText("Motivo");
		typeDischargeListBox.setItemSelected(0, true);

		showToolTip(clientX, clientY);

	}	
	public void showContractEndedTooltip(final int clientX, final int clientY) {
		
		startDate.setVisible(false);
		typeLeave.setVisible(false);
		endDate.setVisible(false);
		typeDischarge.setVisible(false);
		
		baja.setVisible(false);		
		startDateLabel.setVisible(false);
		startLeaveDateBox.setVisible(false);
		causeStartLabel.setVisible(false);
		typeLeaveListBox.setVisible(false);
		
		alta.setVisible(false);
		endDateLabel.setVisible(false);
		endDateBox.setVisible(false);
		causeEndLabel.setVisible(false);
		typeDischargeListBox.setVisible(false);
		
		acceptButton.setVisible(false);
		
		showToolTip(clientX, clientY);
		
	}
	
	public void showLeaveActiveTooltip(final int clientX, final int clientY) {
		
		startDate.setVisible(false);
		typeLeave.setVisible(false);
		endDate.setVisible(false);
		typeDischarge.setVisible(false);
		
		startLeaveDateBox.getTextBox().setReadOnly(true);
		endDateBox.getTextBox().setReadOnly(true);
		
		startDateLabel.setText("Fecha");
		startLeaveDateBox.setValue(getStartContract());
		causeStartLabel.setText("Motivo");
	//	typeLeaveListBox.setItemSelected(0, true);
		typeDischargeListBox.setItemSelected(getDischargeCause(), true);
		endDateLabel.setText("Fecha");
		endDateBox.setValue(getEndContract());
		causeEndLabel.setText("Motivo");
		
		showToolTip(clientX, clientY);		
	}
	
	public void showLeaveEndedTooltip(final int clientX, final int clientY) {
		
		workPeriodLabel.setVisible(false);
		startDateLabel.setVisible(false);
		startLeaveDateBox.setVisible(false);
		typeLeaveListBox.setVisible(false);
		typeDischargeListBox.setVisible(false);
		endDateBox.setVisible(false);
		
		causeStartLabel.setVisible(false);
		typeLeave.setVisible(false);
		causeEndLabel.setVisible(false);
		typeDischarge.setVisible(false);
		
		startDate.setText(workPeriodLabel.getText());
		endDate.setText(typeDischargeListBox.getItemText(getDischargeCause()));
		
		acceptButton.setVisible(false);
		
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
						popupX -= popupX + offsetWidth - windowWidth;

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
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	// ------------------------------------------------------------- UiHandlers

	@UiHandler("startLeaveDateBox")
	void onValueChangeStartDateBox(ValueChangeEvent<Date> event) {

	}

	@UiHandler("endDateBox")
	void onValueChangeFromDateBox(ValueChangeEvent<Date> event) {

	}

	private boolean accept = false;

	@UiHandler("acceptButton")
	void onAcceptClick(ClickEvent event) {		
		
		if(changes == false) {
			accept = false;
			hide();
		}
		else {
			accept = true;
			hide();
		}
	}

	public boolean isAccept() {
		return accept;
	}

	public Date getFromDateBoxValue() {
		return endDateBox.getValue();
	}

	public int getTypeLeaveListBox() {
		
		//Because my firts position is '-'
		return typeLeaveListBox.getSelectedIndex() - 1;
	}

	public int getTypeDischargeListBox() {
		
		return typeDischargeListBox.getSelectedIndex() - 1;	
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
		
		if(pStatus.equals(ACTIVE)) 					
			typeLeaveListBox.setSelectedIndex(0);
		
		else {
			
			int index = getPosTypeListBox(pStatus);
			typeLeaveListBox.setItemSelected(index, true);			
		}
		statusLabel.setText(pStatus);
	}
	
	private int getPosTypeListBox(String pStatus) {
		
		int index = 0;		
		
		if(pStatus.equals(ACTIVE))
			return 0;
		
		for( int x = 0; x < typeLeaveListBox.getItemCount(); x ++) {
			 
			if( typeLeaveListBox.getItemText(x).equals(pStatus))
				index = x;
		}
		return index;	
		
	}

	public void setColor(String background) {

		iconStatusLabel.setStyleName(style.legendIcon(), true);
		iconStatusLabel.getElement().getStyle().setBackgroundColor(background);

	}

	public void setWorkPeriod(String pStartDate, String pEndDate) {
		
		workPeriodLabel.setText(pStartDate + " - " + pEndDate);
	}

	public void setNumDays(Date start, Date finish) {

		this.startContract = start;
		this.endContract = finish;

		int days = DateUtils.getDaysBetween(start, (finish == null ? new Date()
				: finish)) + 1;

		numDaysLabel.setText(Integer.toString(days));

		if (days == 1) {
			daysLabel.setText("d\u00EDa");
		} else {
			daysLabel.setText("d\u00EDas");
		}

	}

	public Date getStartContract() {
		return startContract;
	}

	public Date getEndContract() {
		
		if(endContract == null) {
			return null;
		}
		else {
			return endContract;
		}
			
	}

	public void setDischargeCause(Integer pDischargeCause) {
		
		if(pDischargeCause == null) {
			this.dischargeCause = 0;
		}
		if(pDischargeCause < 0) {
			this.dischargeCause = 0;
		}
		else {
			this.dischargeCause = pDischargeCause + 1;
		}
	}
	
	private int getDischargeCause() {		
		return this.dischargeCause;
	}
	
	private void loadTypeListBox() {
		typeDischargeListBox.addItem("-");
		for (ITDataPerson.DischargeCause cause : DischargeCause.values()) {
			typeDischargeListBox.addItem(cause.getDescription());
		}
		
		typeLeaveListBox.addItem("-");
		for (ITDataPerson.Type type : Type.values()) {
			typeLeaveListBox.addItem(type.getDescription());
		}
		
	}
}
