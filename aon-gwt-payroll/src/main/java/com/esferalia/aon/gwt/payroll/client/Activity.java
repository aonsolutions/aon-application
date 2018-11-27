package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class Activity extends ResizeComposite implements ContextMenuHandler {

	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static EmployeeDraftUiBinder uiBinder = GWT.create(EmployeeDraftUiBinder.class);

	interface EmployeeDraftUiBinder extends UiBinder<Widget, Activity> {
	}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String columnWidth();
		String columnWidth2();
		String headerStyle();
		String elementWidth80();
		String elementWidth95();
		String hide();
		String widthO();
		String paddingTop();
	}

	// TABLA DATOS ACTIVIDAD
	
	@UiField
	TableElement activityDataTable;
	
	@UiField
	TextBox activityDescription;
	
	@UiField
	SuggestBox activityCNAE2009;
	
	@UiField
	DateBoxEx startDate;
	
	@UiField
	DateBoxEx endDate;
	
	@UiField
	Label activityRegime;
	
	@UiField
	CheckBox activityActive;
	
	// TABLA DATOS CCCs
	
	@UiField
	Grid cccDataTableHeader;
	
	@UiField
	ScrollPanel scrollPanel;
	
	@UiField
	Grid cccDataTable;
	
	@UiField
	Label newAccount;

	// --------------------------------------------------------- CONSTRUCTOR --------------------------------------------------------

	public Activity() {
		// Inicializamos la vista
		initWidget(uiBinder.createAndBindUi(this));
		initializeView();
	}
	
	// ------------------------------------------------------------------------
	//								UiHandlers
	// ------------------------------------------------------------------------
	
	// TABLA DATOS ACTIVIDAD
	
	@UiHandler("activityDescription")
	void onDescriptionChangeValue(ChangeEvent event) {
		onActivityDescriptionChange();
	}
	
	@UiHandler("activityCNAE2009")
	void onCNAE2009ChangeValue(SelectionEvent<Suggestion> event) {
		onActivityCNAE2009Change(); 
	}
	
	@UiHandler("startDate")
	void onStartDateChangeValue(ValueChangeEvent<Date> event) {
		onActivityStartDateChange();
	}
	
	@UiHandler("endDate")
	void onEndDateChangeValue(ValueChangeEvent<Date> event) {
		onActivityEndDateChange();
	}
	
	@UiHandler("activityActive")
	void onActiveClick(ClickEvent event) {
		onActivityActiveChange(); 
	}
	
	// TABLA DATOS CCCs
	
	@UiHandler("newAccount")
	void onNewAccountClick(ClickEvent event) {
		onActivityNewAccountChange(); 
	}

	// ------------------------------------------------------------------------
	//							Abstraact Methods
	// ------------------------------------------------------------------------
	
	// TABLA DATOS ACTIVIDAD
	
	public abstract void onActivityDescriptionChange();
	public abstract void onActivityCNAE2009Change();
	public abstract void onActivityStartDateChange();
	public abstract void onActivityEndDateChange();
	public abstract void onActivityActiveChange();
	
	// TABLA DATOS CCCs
	
	public abstract void onActivityNewAccountChange();


	// ------------------------------------------------------------------------
	//							Class Methods
	// ------------------------------------------------------------------------

	private void initializeView() {
		resetElements();
	}

	private void resetElements() {
		this.activityDescription.setValue("");
		this.activityCNAE2009.setValue("");
		this.startDate.setValue(null);
		this.endDate.setValue(null);
		this.activityRegime.setText("");
		this.activityActive.setValue(false);
		
		cccDataTableHeader.clear();
		cccDataTableHeader.resize(0, 0);
		cccDataTable.clear();
		cccDataTable.resize(0, 0);
	}

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		// TODO Auto-generated method stub
	}

}
