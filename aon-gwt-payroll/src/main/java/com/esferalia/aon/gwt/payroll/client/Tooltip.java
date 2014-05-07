package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
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
	InlineLabel socialSecurityNum;

	@UiField
	InlineLabel fullNameLabel;
	
	@UiField
	HTML iconStatusLabel;
	
	@UiField
	InlineLabel statusLabel;
	
	@UiField
	InlineLabel workPeriodLabel;
	
	@UiField
	InlineLabel numDaysLabel;
	
	@UiField
	InlineLabel dateLabel;
	
	@UiField
	DateBox fromDateBox;
	
	@UiField
	Button acceptButton;
	
	
	private String endDate; //estado Activo ??
	private Date finish; //es ultimo contrato ??
	
	interface TooltipUiBinder extends UiBinder<Widget, Tooltip> {
	}

	public Tooltip() {
		
		setGlassEnabled(false);	
		setStyleName(AON.AON_TOOLTIP);
		add(uiBinder.createAndBindUi(this));
	
		fromDateBox.setFormat(new DateBox.DefaultFormat(AON.DATE_FORMAT));	
		
		endDate = "";
		
		setAutoHideEnabled(true);
		
		
	}
	
	protected void onPreviewNativeEvent(final NativePreviewEvent event) {
		super.onPreviewNativeEvent(event);
		switch (event.getTypeInt()) {
		
		case Event.ONKEYDOWN:
			
			if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {		
				hide();
			}
			
			break;

		default:
			break;
		}
	}
	
	public void showToolTip(final int clientX, final int clientY ) {
		
	//	Date today = new Date();
		
		try {
			
		//	if(DateUtils.isAfterOrEquals(getFinish(), today) == false) {
				
				dateLabel.setVisible(false);
				fromDateBox.setVisible(false);
				acceptButton.setVisible(false);
		//	}
			
			setPopupPositionAndShow(new PopupPanel.PositionCallback() {
				
				@Override
				public void setPosition(int offsetWidth, int offsetHeight) {
					
					int windowWidth = Window.getClientWidth();  
					int popupX = clientX - offsetWidth / 3;
					int popupY = clientY;
					
					if(popupX + offsetWidth >= windowWidth )
						popupX -= popupX + offsetWidth - windowWidth ;
					
					if(clientY + offsetHeight >= Window.getClientHeight() ) {
						popupY = popupY - offsetHeight;
					}
					
					setPopupPosition(popupX, popupY);				
				}
			});		
	
			show();
			
			
		}catch(Throwable ex) {
			Window.alert("Error " + ex.getStackTrace() + " " + ex.getMessage());
		}
	}
	
	public void setFullName(String pFullName) {
		fullNameLabel.setText(pFullName);
	}
	
	public void setSocialSecurity(String pSocialSecurity) {
		socialSecurityNum.setText(pSocialSecurity);
	}
	
	public void setStatus(String pStatus) {
		statusLabel.setText(pStatus);
	}
	public void setColor(String background) {
		
		iconStatusLabel.setStyleName(style.legendIcon(), true);
		iconStatusLabel.getElement().getStyle().setBackgroundColor(background);		
		
	}
	public void setWorkPeriod(String pStartDate, String pEndDate) {
		endDate = pEndDate;
		workPeriodLabel.setText(pStartDate + " - " + pEndDate);
	}
	public String getEndDate() {
		return endDate;
	}
	public void setNumDays(int pNumDays) {
		numDaysLabel.setText(Integer.toString(pNumDays));
	}
	
	public void setFinish (Date pFinish) {
		finish = pFinish;
	}
	public Date getFinish() {
		return finish;
	}
	
	
	
	
	// ------------------------------------------------------------- UiHandlers
	
	@UiHandler("acceptButton")
	void onAcceptClick(ClickEvent event) {
		
		Window.alert("Fecha: " + fromDateBox.getValue());
		
	}
	
	// ------------------------------------------------------------------------
	
	
	
	
	
	

}
