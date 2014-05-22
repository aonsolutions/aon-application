package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson.DischargeCause;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson.Type;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
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
	DateBox fromDateBox;

	@UiField
	DateBox startLeaveDateBox;

	@UiField
	Button acceptButton;

	@UiField
	InlineLabel daysLabel;


	private Date startContract;
	private Date endContract;

	/**
	 * 0 - contract active 1 - contract ended 2 - leave active 3 - leave ended
	 **/

	private Date contractStartDate;

	interface TooltipUiBinder extends UiBinder<Widget, Tooltip> {
	}

	public Tooltip() {

		setGlassEnabled(false);
		setStyleName(AON.AON_TOOLTIP);
		add(uiBinder.createAndBindUi(this));

		startLeaveDateBox.setFormat(new DateBox.DefaultFormat(AON.DATE_FORMAT));
		startLeaveDateBox.setWidth("6em");

		fromDateBox.setFormat(new DateBox.DefaultFormat(AON.DATE_FORMAT));
		fromDateBox.setWidth("6em");

		setAutoHideEnabled(true);
		
		acceptButton.setVisible(false);

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

		startDateLabel.setText("Fecha");
		startLeaveDateBox.setValue(getStartContract());
		causeStartLabel.setText("Motivo");
		typeLeaveListBox.setItemSelected(0, true);

		endDateLabel.setText("Fecha");
		causeEndLabel.setText("Motivo");
		typeDischargeListBox.setItemSelected(0, true);

		showToolTip(clientX, clientY);

	}	
	public void showContractEndedTooltip(final int clientX, final int clientY) {
		
		baja.setVisible(false);		
		startDateLabel.setVisible(false);
		startLeaveDateBox.setVisible(false);
		causeStartLabel.setVisible(false);
		typeLeaveListBox.setVisible(false);
		
		alta.setVisible(false);
		endDateLabel.setVisible(false);
		fromDateBox.setVisible(false);
		causeEndLabel.setVisible(false);
		typeDischargeListBox.setVisible(false);
		
		acceptButton.setVisible(false);
		
		showToolTip(clientX, clientY);
		
	}
	
	public void showLeaveActiveTooltip(final int clientX, final int clientY) {
		
		startDateLabel.setText("Fecha");
		startLeaveDateBox.setValue(getStartContract());
		causeStartLabel.setText("Motivo");
		typeLeaveListBox.setItemSelected(0, true);
		
		endDateLabel.setText("Fecha");
		fromDateBox.setValue(getEndContract());
		causeEndLabel.setText("Motivo");
		
		showToolTip(clientX, clientY);
		
		
	}
	
	public void showLeaveEndedTooltip(final int clientX, final int clientY) {
		
		
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

	// ------------------------------------------------------------- UiHandlers

	@UiHandler("startLeaveDateBox")
	void onValueChangeStartDateBox(ValueChangeEvent<Date> event) {

		if (event.getValue().before(contractStartDate)) {
			Window.alert("Comienzo de " + statusLabel.getText() + " - "
					+ AON.DATE_FORMAT.format(contractStartDate));
			startLeaveDateBox.setValue(new Date());
		}

		if (getFromDateBoxValue() != null
				&& getStartDateBoxValue().after(getFromDateBoxValue())) {

			Window.alert("Rango de fechas incorrectas");
			startLeaveDateBox.setValue(getStartContract());
			fromDateBox.setValue(getEndContract());
		}
	}

	@UiHandler("fromDateBox")
	void onValueChangeFromDateBox(ValueChangeEvent<Date> event) {

		if (event.getValue().before(getStartDateBoxValue())) {
			Window.alert("La fecha de alta no puede ser menor que la de baja.");
			startLeaveDateBox.setValue(getStartContract());
			fromDateBox.setValue(getEndContract());
		}
	}

	private boolean accept = false;

	@UiHandler("acceptButton")
	void onAcceptClick(ClickEvent event) {

		if (typeLeaveListBox.getSelectedIndex() > 0
				&& typeDischargeListBox.getSelectedIndex() > 0) {

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

	public int getTypeLeaveListBox() {

		return typeLeaveListBox.getSelectedIndex();
	}

	public int getTypeDischargeListBox() {

		return typeDischargeListBox.getSelectedIndex();
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
		
		typeLeaveListBox.addItem("-");

		for (ITDataPerson.Type type : Type.values()) {

			typeLeaveListBox.addItem(type.getDescription());
		}
		typeLeaveListBox.setItemSelected(0, false);
		
		statusLabel.setText(pStatus);
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
		
		startLeaveDateBox.setValue(start);
		
		if(endContract != null) {
			fromDateBox.setValue(endContract);
		}

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

	public void setContractStartDate(Date pContractStart) {
		contractStartDate = pContractStart;
	}

	public void setDischargeCause(int pDischargeCause) {
		
		typeDischargeListBox.addItem("-");

		for (ITDataPerson.DischargeCause cause : DischargeCause.values()) {

			typeDischargeListBox.addItem(cause.getDescription());
		}
		
		typeDischargeListBox.setItemSelected(pDischargeCause, true);
	}
}
