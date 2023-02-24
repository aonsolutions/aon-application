package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.watson.util.AonStringUtils;
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
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class Activity extends ResizeComposite {
	
	// ------------------------------------------- CCC

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
		protected void onInsertCCC(EnterpriseCCC ccc) {
			Activity.this.onInsertCCC(ccc);
		}

		@Override
		protected void onInsertActivity(com.esferalia.aon.occam.api.model.payroll.Activity activity) {
			// Nothing to do
		}

		@Override
		protected Set<Entry<Integer, String>> getActivities() {
			return Activity.this.getActivities();
		}

		@Override
		protected void fireWarningMessage(Map<String, String> warningMap) {
			Activity.this.fireWarningMessage(warningMap);
		}

		@Override
		protected void fireLoadingMessage(String message) {
			Activity.this.fireLoadingMessage(message);
		}

		@Override
		protected void hideMessage() {
			Activity.this.hideMessage();
		}

		@Override
		protected void showPDF(String dataURI, boolean isLaboralLife) {
			Activity.this.showPDF(dataURI, isLaboralLife);
		}
		
	}
	
	// ------------------------------------------- UiBinder

	private static EmployeeDraftUiBinder uiBinder = GWT.create(EmployeeDraftUiBinder.class);

	interface EmployeeDraftUiBinder extends UiBinder<Widget, Activity> {}
	
	// ------------------------------------------- UiFields

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String warningTB();
	}

	// TABLA DATOS ACTIVIDAD
	
	@UiField
	HTMLPanel activityDataTable;
	
	@UiField
	HTMLPanel activityDescriptionPanel;
	
	@UiField
	TextBox activityDescription;
	
	@UiField
	HTMLPanel activityCNAE2009Panel;
	
	@UiField
	SuggestBox activityCNAE2009;
	
	@UiField
	DateBoxEx startDate;
	
	@UiField
	DateBoxEx endDate;
	
	@UiField
	CheckBox activityActive;
	
	// TABLA DATOS CCCs
	
	@UiField (provided = true)
	CCC cccWidget;

	// ------------------------------------------- Constructor

	protected Activity() {
		cccWidget = new CCCWidgetImpl();
		initWidget(uiBinder.createAndBindUi(this));
		initializeView();
	}
	
	// ------------------------------------------- UiHandlers
	
	// TABLA DATOS ACTIVIDAD
	
	@UiHandler("activityDescription")
	void onDescriptionChangeValue(ChangeEvent event) {
		if(AonStringUtils.isBlank(activityDescription.getValue())) {
			addWarningIcon(activityDescription);
			activityDescription.setTitle("La descripci\u00f3n debe rellenarse");
			
			Map<String, String> warningMap = new HashMap<>();
			warningMap.put("Descripco\u00F3n obligatoria", "El campo descripci\u00F3n es obligatorio");
			fireWarningMessage(warningMap);
		} else {
			removeWarningIcon(activityDescription);
			activityDescription.setTitle("");
			
			onActivityDescriptionChange();
		}
	}
	
	@UiHandler("activityCNAE2009")
	void onCNAE2009SelectionValue(SelectionEvent<Suggestion> event) {
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
	
	// ------------------------------------------- Abstract Methods
	
	// TABLA DATOS ACTIVIDAD
	
	public abstract void onActivityDescriptionChange();
	public abstract void onActivityCNAE2009Change();
	public abstract void onActivityStartDateChange();
	public abstract void onActivityEndDateChange();
	public abstract void onActivityActiveChange();
	
	public abstract void onInsertRow();
	public abstract void onInsertRows();
	public abstract void onDeleteCCC(Integer cccId);
	public abstract void onInsertCCC(EnterpriseCCC ccc);
	public abstract Set<Entry<Integer, String>> getActivities();

	public abstract void fireWarningMessage(Map<String, String> warningMap);
	protected abstract void fireLoadingMessage(String message);
	protected abstract void hideMessage();
	
	public abstract void showPDF(String dataURI, boolean isLaboralLife);

	// ------------------------------------------- Auxiliar Methods

	private void initializeView() {
		cccWidget.resetPreview();
	}
	
	public void addNewCCC() {
		cccWidget.insertNewRow();
	}
	
	public void hideActivityColumn() {
		cccWidget.hideActivityColumn();
	}

	public void setActivityDraftCCCHeight() {
		cccWidget.setActivityDraftCCCHeight();
	}
	
	public void addWarningIcon(Widget widget) {
		widget.addStyleName(style.warningTB());
	}
	
	public void removeWarningIcon(Widget widget) {
		widget.removeStyleName(style.warningTB());
	}

	public void showCCCMessage() {
		cccWidget.showCCCMessage();
	}

	public void showCCCTable() {
		cccWidget.showCCCTable();
	}

	public void onAddNewCCC() {
		cccWidget.onAddNewCCC();
	}

}
