package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
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
		protected List<EnterpriseCCC> getEnterpriseCCCs() {
			return Activity.this.getEnterpriseCCCs();
		}

		@Override
		protected void fireWarningMessage(Map<String, String> warningMap) {
			Activity.this.fireWarningMessage(warningMap);
		}
		
		@Override
		protected void fireInfoMessage(Map<String, String> warningMap) {
			Activity.this.fireInfoMessage(warningMap);
		}

		@Override
		protected void fireLoadingMessage(String message) {
			Activity.this.fireLoadingMessage(message);
		}

		@Override
		protected void hideMessage() {
			Activity.this.fireHideMessage();
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
	
	// ------------------------------------------- Variables
	
	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private Map<Integer, String> cnaeMap = Collections.emptyMap();

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
			
			fireErrorMessage(new HashMap<String, String>(){{ put("Descripci\u00F3n obligatoria", "El campo descripci\u00F3n es obligatorio"); }});
		} else {
			removeWarningIcon(activityDescription);
			activityDescription.setTitle("");
			
			onActivityDescriptionChange();
		}
	}
	
	@UiHandler("activityCNAE2009")
	void onCNAE2009SelectionValue(SelectionEvent<Suggestion> event) {
		String cnae2009Value = activityCNAE2009.getValue();
		Optional<Entry<Integer, String>> cnae2009Opt = this.cnaeMap.entrySet().stream().filter(entry -> AonStringUtils.equalsIgnoreCase(entry.getValue(), cnae2009Value)).findAny();
		if(cnae2009Opt.isPresent()) { 
			Integer cnaeId = cnae2009Opt.get().getKey();
			String cnaeCode = cnae2009Opt.get().getValue().split(" - ")[0];
			String cnaeTitle = cnae2009Opt.get().getValue().split(" - ")[1];
			onActivityCNAE2009Change(cnaeId, cnaeCode, cnaeTitle); 
		} else onActivityCNAE2009Change(null, null, null);
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
	public abstract void onActivityCNAE2009Change(Integer cnaeId, String cnaeCode, String cnaeTitle);
	public abstract void onActivityStartDateChange();
	public abstract void onActivityEndDateChange();
	public abstract void onActivityActiveChange();
	
	public abstract void onInsertRow();
	public abstract void onInsertRows();
	public abstract void onDeleteCCC(Integer cccId);
	public abstract void onInsertCCC(EnterpriseCCC ccc);
	public abstract Set<Entry<Integer, String>> getActivities();
	public abstract List<EnterpriseCCC> getEnterpriseCCCs();

	protected abstract void fireErrorMessage(Map<String, String> messages);
	protected abstract void fireWarningMessage(Map<String, String> messages);
	protected abstract void fireInfoMessage(Map<String, String> messages);
	protected abstract void fireLoadingMessage(String message);
	protected abstract void fireHideMessage();
	
	public abstract void showPDF(String dataURI, boolean isLaboralLife);

	// ------------------------------------------- Auxiliar Methods

	public void initializeView() {
		initializeCnae();
		removeWarningIcon(activityDescription);
		cccWidget.resetPreview();
	}
	
	private void initializeCnae() {
		impl.getCNAE2009(new AsyncCallback<Map<Integer,String>>() {
			
			@Override
			public void onSuccess(Map<Integer, String> cnaeMapIn) {
				cnaeMap = cnaeMapIn;
				List<String> cnaeDescriptions = new ArrayList<>();
				
				for (Entry<Integer, String> entry : cnaeMap.entrySet())
					cnaeDescriptions.add(entry.getValue());
				
				cnaeDescriptions.sort((o1, o2) -> o1.compareTo(o2));
				
				MultiWordSuggestOracle orclCnaes = (MultiWordSuggestOracle) activityCNAE2009.getSuggestOracle();
				orclCnaes.addAll(cnaeDescriptions);
				orclCnaes.setDefaultSuggestionsFromText(cnaeDescriptions);
				activityCNAE2009.setAutoSelectEnabled(true);
				activityCNAE2009.getElement().setPropertyString("placeholder", "C\u00f3digo/Descripci\u00f3n del CNAE... (Ctrl + espacio para ver sugerencias)");
				
				activityCNAE2009.getValueBox().addKeyUpHandler(e -> {
					if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
						activityCNAE2009.setText("");
						activityCNAE2009.showSuggestionList();
					} else if(e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE)
						activityCNAE2009.hideSuggestionList();
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				fireWarningMessage(new HashMap<String,String>() {{ put("ERRO CNAE", caught.getMessage()); }});
			}
		});
		
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
