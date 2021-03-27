package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class Activity extends ResizeComposite {

	private class CCCWidgetImpl extends CCC {
		
		@Override
		protected void onInsertRow() {
			Activity.this.onInsertRow();
		}

		@Override
		protected void onInsertRows() {
			Activity.this.onInsertRows();
		}

		@Override
		protected void onDeleteCCC(Integer cccId) {
			Activity.this.onDeleteCCC(cccId);
		}

		@Override
		protected void onInsertCCC(Integer cccId, int activityId, byte cccRegime, String cccRegimeCode, String account, String province, String provinceCode) {
			Activity.this.onInsertCCC(cccId, activityId, cccRegime, cccRegimeCode, account, province, provinceCode);
		}

		@Override
		protected Set<Entry<Integer, String>> getActivities() {
			return Activity.this.getActivities();
		}
		
	}
	
	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static EmployeeDraftUiBinder uiBinder = GWT.create(EmployeeDraftUiBinder.class);

	interface EmployeeDraftUiBinder extends UiBinder<Widget, Activity> {
	}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {}

	// TABLA DATOS ACTIVIDAD
	
	@UiField
	HTMLPanel activityDataTable;
	
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
	
	@UiField (provided = true)
	CCC cccWidget;

	// --------------------------------------------------------- CONSTRUCTOR --------------------------------------------------------

	public Activity() {
		cccWidget = new CCCWidgetImpl();
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
	
	// ------------------------------------------------------------------------
	//							Abstraact Methods
	// ------------------------------------------------------------------------
	
	// TABLA DATOS ACTIVIDAD
	
	public abstract void onActivityDescriptionChange();
	public abstract void onActivityCNAE2009Change();
	public abstract void onActivityStartDateChange();
	public abstract void onActivityEndDateChange();
	public abstract void onActivityActiveChange();
	
	public abstract void onInsertRow();
	public abstract void onInsertRows();
	public abstract void onDeleteCCC(Integer cccId);
	public abstract void onInsertCCC(Integer cccId, int activityId, byte cccRegime, String cccRegimeCode, String account, String province, String provinceCode);
	public abstract Set<Entry<Integer, String>> getActivities();

	// ------------------------------------------------------------------------
	//							Class Methods
	// ------------------------------------------------------------------------

	private void initializeView() {
		cccWidget.resetPreview();
	}
	
	public void addNewCCC(Integer newId) {
		cccWidget.insertNewRow(newId);
	}
	
	public void hideActivityColumn() {
		cccWidget.hideActivityColumn();
	}

}
