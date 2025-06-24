package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCheckBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomSuggestBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.payroll.client.CCCDialog.CCCDialogCallback;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class Activity extends ScrollPanel {
	
	// ------------------------------------------- CCC

	private class CCCWidgetImpl extends CCC {

		protected CCCWidgetImpl(Integer activityId) {
			super(activityId);
		}

		@Override
		protected void fireError(String message) {
			showError(message);
		}

		@Override
		protected void fireWarning(String message) {
			showWarning(message);
		}

		@Override
		protected void fireLoading(String message) {
			showLoading(message);
		}
		
		@Override
		protected void hideMessagePanel() {
			hideMessage();
		}

		@Override
		protected void showPDF(String dataURI, String title, boolean isLaboralLife) {
			Activity.this.showPDF(dataURI, isLaboralLife);
		}
			
	}
	
	// ------------------------------------------- UiBinder
	
	private HTMLPanel content = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel messagePanel = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel gridPanel = new HTMLPanel(AonStringUtils.EMPTY);

	private AonCustomTextBox description = new AonCustomTextBox("Descripci\u00f3n");
	private AonCustomSuggestBox cnae = new AonCustomSuggestBox("CNAE2009");
	private AonCustomDateBox startDate = new AonCustomDateBox("F. Inicio");
	private AonCustomDateBox endDate = new AonCustomDateBox("F. Fin");
	private AonCustomCheckBox principal = new AonCustomCheckBox("Actividad principal");
	
	private AonCustomCard cccCard;
	private CCC cccWidget;
	
	private com.esferalia.aon.occam.api.model.payroll.Activity activity;
	
	// ------------------------------------------- Variables
	
	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private Map<Integer, String> cnaeMap = Collections.emptyMap();

	// ------------------------------------------- Constructor

	protected Activity(com.esferalia.aon.occam.api.model.payroll.Activity activity) {
		this.activity = activity;
		initializeView();
	}
	
	public void initializeView() {
		clear();
		content.clear();
		content.addStyleName(AON.CSS.aonFlexColumn());
		content.add(messagePanel);
		
		initializeCnae();
		
		initHandlers();
		
		cccWidget = new CCCWidgetImpl(this.activity.getId());
		
		initView();
		
		Scheduler.get().scheduleDeferred(() -> {
			setWidget(content);
		});
	}
	
	private void initializeCnae() {
		impl.getCNAE2009(new AsyncCallback<Map<Integer,String>>() {
			
			@Override
			public void onSuccess(Map<Integer, String> cnaeMapIn) {
				cnaeMap = cnaeMapIn;
				
				List<String> cnaeDescriptions = new ArrayList<>();
				cnaeMap.entrySet().forEach(entry -> cnaeDescriptions.add(entry.getValue()));
				cnaeDescriptions.sort((o1, o2) -> o1.compareTo(o2));
				
				MultiWordSuggestOracle orclCnaes = (MultiWordSuggestOracle) cnae.getSuggestBox().getSuggestOracle();
				orclCnaes.addAll(cnaeDescriptions);
				orclCnaes.setDefaultSuggestionsFromText(cnaeDescriptions);
				cnae.setAutoSelectEnabled(true);
				cnae.setPlaceHolder("CNAE... (Ctrl + espacio para ver sugerencias)");
				
				cnae.getSuggestBox().getValueBox().addKeyUpHandler(e -> {
					if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
						cnae.setValue(AonStringUtils.EMPTY);
						cnae.showSuggestionList();
					} else if(e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE)
						cnae.hideSuggestionList();
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				showError("CNAE : " + caught.getMessage());
			}
			
		});
		
	}
	
	private void initHandlers() {
		description.addValueChangeHandler(e -> {
			if(AonStringUtils.isBlank(e.getValue())) {
				description.addError();
				showError("Descripci\u00F3n obligatoria");
			} else {
				description.removeError();
				onActivityDescriptionChange(e.getValue());
			}
		});
		
		cnae.getSuggestBox().addSelectionHandler(e -> {
			String cnae2009Value = cnae.getValue();
			Optional<Entry<Integer, String>> cnae2009Opt = this.cnaeMap.entrySet().stream().filter(entry -> AonStringUtils.equalsIgnoreCase(entry.getValue(), cnae2009Value)).findAny();
			if(cnae2009Opt.isPresent()) { 
				Integer cnaeId = cnae2009Opt.get().getKey();
				String cnaeCode = cnae2009Opt.get().getValue().split(" - ")[0];
				String cnaeTitle = cnae2009Opt.get().getValue().split(" - ")[1];
				onActivityCNAE2009Change(cnaeId, cnaeCode, cnaeTitle); 
			} else onActivityCNAE2009Change(null, null, null);
		});

		startDate.addValueChangeHandler(e -> onActivityStartDateChange(e.getValue()));
		endDate.addValueChangeHandler(e -> onActivityEndDateChange(e.getValue()));
		principal.addValueChangeHandler(e -> onActivityActiveChange(e.getValue()));
	}

	private void initView() {
		gridPanel.clear();
		gridPanel.setStyleName(AON.CSS.aonGridTwoCols());
		gridPanel.getElement().getStyle().setProperty("padding", "0 1rem");
		
		AonCustomCard activityCard = new AonCustomCard("Datos actividad");
		
		if(null != activity.getId()) {
			description.setValue(activity.getDescription());
			cnae.setValue(activity.getCnaeCode() + " - " + activity.getCnaeDescription());
			startDate.setValue(activity.getStartDate());
			endDate.setValue(activity.getEndDate());
			principal.setValue(activity.isPrincipal());
		}
		
		principal.setWidth("20rem");
		
		HTMLPanel tableInfo = createTable();
		tableInfo.add(createRow(description, cnae, null));
		tableInfo.add(createRow(startDate, endDate, principal));
		
		activityCard.add(tableInfo);
		gridPanel.add(activityCard);
		
		AonToolbarButton addCCC = new AonToolbarButton("Nuevo CCC", AON.CSS.aonIconAdd());
		addCCC.addClickHandler(e -> createCCC());
		cccCard = new AonCustomCard("Cuentas Cotizaci\u00f3n", addCCC);
		cccCard.add(cccWidget);
		
		gridPanel.add(cccCard);
		
		content.remove(gridPanel);
		content.add(gridPanel);
	}
	private HTMLPanel createTable() {
		HTMLPanel table = new HTMLPanel("");
		table.setStyleName(AON.CSS.aonFlexColumn());
		return table;
	}
	
	private HTMLPanel createRow(Widget w1, Widget w2, Widget w3) {
		HTMLPanel panel = new HTMLPanel("");
		panel.setStyleName(AON.CSS.aonItemFlex());
		
		panel.add(w1);
		if(null != w2) panel.add(w2);
		if(null != w3) panel.add(w3);
		
		return panel;
	}
	
	void createCCC() {
		new CCCDialog(activity.getDomain(), activity.getId(), new CCCDialogCallback() {
			@Override
			public void onAccept(EnterpriseCCC ccc) {
				initializeView();
			}
		});
	}

	void hideCCCCard() {
		cccCard.setVisible(false);
	}
	
	CCC getCCCWidget() {
		return cccWidget;
	}
	
	// ------------------------------------------- Abstract Methods
	
	// TABLA DATOS ACTIVIDAD
	
	public abstract void onActivityDescriptionChange(String description);
	public abstract void onActivityCNAE2009Change(Integer cnaeId, String cnaeCode, String cnaeTitle);
	public abstract void onActivityStartDateChange(Date startDate);
	public abstract void onActivityEndDateChange(Date endDate);
	public abstract void onActivityActiveChange(Boolean principal);
	
	public abstract void showPDF(String dataURI, boolean isLaboralLife);

	// ------------------------------------------- Auxiliar Methods

	void showError(String message) {
		AonMessagePanel.showError(messagePanel, message);
	}
	
	void showSuccess(String message) {
		AonMessagePanel.showSuccess(messagePanel, message);
	}
	
	void showWarning(String message) {
		AonMessagePanel.showWarning(messagePanel, message);
	}
	
	void showLoading(String message) {
		AonMessagePanel.showLoading(messagePanel, message);
	}
	
	void hideMessage() {
		AonMessagePanel.hideMessage(messagePanel);
	}

}
