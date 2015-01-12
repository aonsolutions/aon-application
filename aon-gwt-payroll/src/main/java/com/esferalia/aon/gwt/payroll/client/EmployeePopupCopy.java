package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
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
	Label warnLabel;
	
	private Employee originalEmployee;
	
	private Employee employee;
	
	private final String CAMPO_OBLIGATORIO = "Campo obligatorio";
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
		//this.startDate.getTextBox().setReadOnly(true);
		this.endDate.setFormat(new DateBox.DefaultFormat(AON.DATE_FORMAT));
		//this.endDate.getTextBox().setReadOnly(true);

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
			String name = map.get(document);
			
			if (name.compareToIgnoreCase(employee.getFullname()) == 0) {
				this.document = document;
				suggest.getValueBox().setText(name.concat(separator).concat(document));
			}
			names.add(name.concat(separator).concat(document));
		}		
	}
	
	public String getDocument () {
		return suggest.getValueBox().getText().split(" - ")[1];
	}


	// -------------------------------------------------------UiHandlers

/*	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent event) {
		for (Listener listener : listeners)
			listener.onAcceptButtonClickButton(event);
	}
*/
	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent event) {
		if(startDate.getValue() == null) {
			startDate.setStyleName(AON.AON_ICON_WARN);
			startDate.setTitle(CAMPO_OBLIGATORIO);
			return;
		}
		
		if ( endDate.getValue() != null && startDate.getValue().after(endDate.getValue())) {
			warnLabel.setStyleName(AON.AON_ICON_WARN + "" + AON.AON_ICON_CMD_BUTTON);
			warnLabel.setTitle("Rango de fechas no correcto");
			return;
			
		}
/*		
		if ( startDate.getValue().after(endDate.getValue())) {
			warnLabel.setStyleName(AON.AON_ICON_WARN + "" + AON.AON_ICON_CMD_BUTTON);
			warnLabel.setTitle("Rango de fechas no correcto");
			return;
		}
*/		
		Employee employee = new Employee();
		employee.setDocument(getDocument());
		employee.setStartDate(getStartDateWidget().getValue());
		employee.setEndDate(getEndDateWidget().getValue());
		
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

	public DateBox getStartDateWidget() {
		return startDate;
	}

	public DateBox getEndDateWidget() {
		return endDate;
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
	
	public void duplicateEmployee(Employee selectedEmployee) {
		
		String fullName = selectedEmployee.getFullname();
		String document = selectedEmployee.getDocument();
		
		suggest.getValueBox().setText(fullName.concat(separator).concat(document));
		suggest.getValueBox().setReadOnly(true);
	}
	
	

	// --------------------------------------------------------------------

	@Override
	public void center() {
		startDate.setValue(null);
		endDate.setValue(null);
		super.center();
	}

	@Override
	public void show() {
		startDate.setValue(null);
		endDate.setValue(null);
		super.show();
	}
}
