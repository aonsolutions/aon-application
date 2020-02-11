package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class EmployeePopupCopy extends CustomDialog {

	interface Listener {
		void onAcceptClick (Employee employee, boolean value);
	}

	private static EmployeePopupCopyUiBinder uiBinder = GWT
			.create(EmployeePopupCopyUiBinder.class);

	@UiField(provided = true)
	SuggestBox suggest;
	@UiField
	DateBox startDate;
	@UiField
	DateBox endDate;
	@UiField
	Button acceptButton;
	@UiField
	Button cancelButton;
	@UiField
	CheckBox especifico;
	@UiField
	InlineLabel warnLabel;
	
	private Employee employee;
	
	private final String separator = " - ";
	private String document;
	
	private MultiWordSuggestOracle names = new MultiWordSuggestOracle();

	interface EmployeePopupCopyUiBinder extends
			UiBinder<Widget, EmployeePopupCopy> {
	}

	private List<Listener> listeners;

	public EmployeePopupCopy() {
		setCaption("Introduce nuevos datos...");
		suggest = new SuggestBox(names);
		
		setWidget(uiBinder.createAndBindUi(this));
		
		this.especifico.setValue(true);
		this.startDate.setFormat(new DateBox.DefaultFormat(AON.DATE_FORMAT));
		this.startDate.getTextBox().setReadOnly(true);
		this.endDate.setFormat(new DateBox.DefaultFormat(AON.DATE_FORMAT));
		this.endDate.getTextBox().setReadOnly(true);

		this.listeners = new ArrayList<Listener>();
		
		setAnimationEnabled(true);
		setGlassEnabled(true);
		
		suggest.getValueBox().addFocusHandler( new FocusHandler() {
			
			@Override
			public void onFocus(FocusEvent event) {
				suggest.getValueBox().selectAll();
			}
		});

	}
	
	// -------------------------------------------------------Private Methods
	
	private final void initSuggestBox(Map<String, String> map) {
		names.clear();		
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String document = iterator.next();
			if(null != document) {
				String name = map.get(document);
				
				if (name.compareToIgnoreCase(employee.getFullname()) == 0) {
					this.document = document;
					suggest.getValueBox().setText(name.concat(separator).concat(document));
				}
				names.add(name.concat(separator).concat(document));
			}
		}		
	}
	
	public String getDocument () {
		return suggest.getValueBox().getText().split(" - ")[1];
	}


	// -------------------------------------------------------UiHandlers

	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent event) {

		if(startDate.getValue() == null) {
			startDate.setFocus(true);
			return;
		}

		if(checkDates(getStartDate(), getEndDate())) {
			warnLabel.setVisible(true);
			return;
		}

		Employee employee = new Employee();
		employee.setDocument(getDocument());
		employee.setStartDate(getStartDate());
		employee.setEndDate(getEndDate());
		
		for (Listener listener : listeners)
			listener.onAcceptClick(employee, getEspecificoValue());

	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		hide();
	}
	
	@UiHandler("suggest")
	void onSelectionValue(SelectionEvent<SuggestOracle.Suggestion> event) {
		especifico.setValue(false);
		
		String documentAux = event.getSelectedItem()
				.getReplacementString().split(separator)[1];
		
		especifico.setEnabled(documentAux.equals(document));
	}
	
	
	
	// -----------------------------------------------------------------
	
	@Override
	public void hide() {
		suggest.setText("");
		especifico.setEnabled(true);
		especifico.setValue(true);
		super.hide();
	}

	public void showPopUpPanel() {
		center();
	}
	
	public void setEmployee (final Employee employee) {
		this.employee = employee;
	}
	
	public void setMapAvaiableEmployees (Map<String, String> map) {
		initSuggestBox(map);
	}

	public Date getStartDate() {
		return startDate.getValue();
	}

	public Date getEndDate() {
		return endDate.getValue();
	}

	public Button getAcceptButton() {
		return acceptButton;
	}

	public boolean getEspecificoValue() {
		return especifico.getValue();
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	private boolean checkDates (Date start, Date end) {
		return DateUtils.isAfterOrEquals(start, end);
	}
	

	// --------------------------------------------------------------------

	@Override
	public void center() {
		super.center();
	}

	@Override
	public void show() {
		super.show();
	}

}
