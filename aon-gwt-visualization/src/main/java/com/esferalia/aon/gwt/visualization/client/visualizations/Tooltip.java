package com.esferalia.aon.gwt.visualization.client.visualizations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class Tooltip extends DecoratedPopupPanel {

	public static final String AON_TOOLTIP = "aon-tooltip";
	public static final DateTimeFormat DATE_FORMAT = DateTimeFormat
			.getFormat("dd/MM/yyyy");

	public interface Listener {

		void onStartDateChangeEvent(ValueChangeEvent<Date> event);

		void onEndDateChangeEvent(ValueChangeEvent<Date> event);

		void onAcceptButtonClickEvent(ClickEvent event);
	}

	interface Binder extends UiBinder<Widget, Tooltip> {
	}

	private static Binder binder = GWT.create(Binder.class);

	@UiField
	protected InlineLabel nameLabel;
	@UiField
	protected DateBox endDateBox;
	@UiField
	protected DateBox startDateBox;
	@UiField
	protected Button acceptButton;
	@UiField
	protected SimplePanel valuePanel;
	@UiField
	protected SimplePanel titlePanel;

	private List<Listener> listeners;
	private HasValue<String> hasValue;


	public Tooltip() {

		setGlassEnabled(false);
		setStyleName(AON_TOOLTIP);
		add(binder.createAndBindUi(this));
		listeners = new ArrayList<Tooltip.Listener>();

		startDateBox.setWidth("6em");
		startDateBox.setFormat(new DateBox.DefaultFormat(DATE_FORMAT));
		endDateBox.setWidth("6em");
		endDateBox.setFormat(new DateBox.DefaultFormat(DATE_FORMAT));

		setAutoHideEnabled(true);

	}

	public Date getEndDate() {
		return endDateBox.getValue();
	}

	public void setEndDate(Date endDate) {
		endDateBox.setValue(endDate);
	}

	public Date getStartDate() {
		return startDateBox.getValue();
	}

	public void setStartDate(Date startDate) {
		startDateBox.setValue(startDate);
	}

	public void setName(String pFullName) {
		nameLabel.setText(pFullName);
	}
	
	public String getName() {
		return nameLabel.getText();
	}
	
	public void setTitle(Widget widget) {
		titlePanel.setWidget(widget);
	}
	
	public String getValue(){
		return hasValue.getValue();
	}
	
	public <T extends IsWidget & HasValue<String> & HasAllFocusHandlers & Focusable> void setValueEditor(T valueEditor) {
		hasValue = valueEditor;
		valuePanel.setWidget(valueEditor);
	}
	
	public void setVisibleAcceptButton(boolean visible){
		this.acceptButton.setVisible(visible);
	}

	public void showToolTip(final int clientX, final int clientY) {

		try {

			setPopupPositionAndShow(new PopupPanel.PositionCallback() {

				@Override
				public void setPosition(int offsetWidth, int offsetHeight) {

					int windowWidth = Window.getClientWidth();
					int popupX = clientX - offsetWidth / 3;
					int popupY = clientY;

					if (popupX + offsetWidth >= windowWidth - offsetWidth / 2)
						popupX -= popupX + offsetWidth * 1.2 - windowWidth;

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

	@UiHandler("acceptButton")
	void onAcceptClick(ClickEvent event) {
		for (Listener listener : listeners)
			listener.onAcceptButtonClickEvent(event);
	}

	@UiHandler("startDateBox")
	void onValueChangeStartDateBox(ValueChangeEvent<Date> event) {
		for (Listener listener : listeners)
			listener.onStartDateChangeEvent(event);
	}

	@UiHandler("endDateBox")
	void onValueChangeFromDateBox(ValueChangeEvent<Date> event) {
		for (Listener listener : listeners)
			listener.onEndDateChangeEvent(event);
	}

}
