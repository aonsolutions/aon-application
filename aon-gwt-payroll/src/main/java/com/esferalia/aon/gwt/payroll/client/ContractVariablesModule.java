package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.MultiFileUpload;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSettleDateDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class ContractVariablesModule extends MainEntryPoint{
	
	// ----------------------------------------------- Variables
	
	private DockLayoutPanel panel;
	
	private HTMLPanel containerPanel;
	private HTMLPanel messagePanel;
	
	private ScrollPanel scrollPanel;
	private HTMLPanel resultsPanel;
	
	private AonToolbar toolbar;
	private AonToolbarButton exportBtn;
	private AonToolbarButton importBtn;
	private MultiFileUpload excelFileUpload;
	
	private DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	// ----------------------------------------------- Constructor

	public ContractVariablesModule() {	
		createToolbar();
		
		// Inject rich styles.
		AON.ensureInjected();
		GWT.<GWTResources>create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources>create(AonResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.CodeMirrorResources>create(MainEntryPoint.CodeMirrorResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
	
		panel = new DockLayoutPanel(Unit.PX);
		
		containerPanel = new HTMLPanel(AonStringUtils.EMPTY);
		containerPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		messagePanel = new HTMLPanel(AonStringUtils.EMPTY);
		
		scrollPanel = new ScrollPanel();
		scrollPanel.setHeight((Window.getClientHeight() - 170) + "px");
		scrollPanel.getElement().getStyle().setProperty("padding", "0 1rem");
		
		resultsPanel = new HTMLPanel(AonStringUtils.EMPTY);
		resultsPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(panel);
	}
	
	// ----------------------------------------------- onModuleLoad
	
	@Override
	public void onModuleLoad() {
		panel.addNorth(toolbar, 50);
		
		containerPanel.add(messagePanel);
		
		scrollPanel.add(resultsPanel);
		containerPanel.add(scrollPanel);
		
		panel.add(containerPanel);
		
		initialize();
	}
	
	private void initialize() {
		resultsPanel.clear();
		
		Label errorsTitle = new Label("Descargue la plantilla Excel con la variables de calculo para poder rellenarla adecuadamente.");
		errorsTitle.getElement().getStyle().setProperty("font-size", "1rem");
		errorsTitle.getElement().getStyle().setProperty("font-weight", "bold");
		resultsPanel.add(errorsTitle);
	}
	
	// ----------------------------------------------- Toolbar
	
	private void createToolbar() {
		this.toolbar = new AonToolbar("Gesti\u00f3n Variables Calculo");
		
		// Export
		exportBtn = new AonToolbarButton("Exportar Variables Calculo Contrato", AON.CSS.aonIconExcel() );
		exportBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AonSettleDateDialog dateDialog = new AonSettleDateDialog("Periodo", "Fecha exportaci\u00f3n variables calculo:") {
					
					@Override
					protected void onAccept(Date date) {
						String printURL = URL.encode(GWT.getModuleBaseURL() + "contract_events/export");
						
						FormPanel formPanel = new FormPanel("_blank");
						formPanel.setAction(printURL);
						formPanel.setMethod(FormPanel.METHOD_POST);
						
						FlowPanel flowPanel = new FlowPanel();
						flowPanel.add(new Hidden("domain", Wnd.getCurrentDomainNameURL()));
						flowPanel.add(new Hidden("user", Wnd.getCurrentUser()));
						flowPanel.add(new Hidden("date", dateFormat.format(date)));
						
						formPanel.add(flowPanel);
						
						toolbar.add(formPanel);
						
						formPanel.submit();
					}
				};
				
				dateDialog.show();
			}
		});
		toolbar.add(exportBtn);
		
		// FORM
		FormPanel excelFormPanel = new FormPanel();
		excelFormPanel.setMethod(FormPanel.METHOD_POST);
		excelFormPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		excelFormPanel.setAction(URL.encode(GWT.getModuleBaseURL() + "contract_events/import"));
		
		Hidden userNameHidden = new Hidden("user", Wnd.getCurrentUser());
		Hidden domainNameHidden = new Hidden("domain", Wnd.getCurrentDomainNameURL());
		
		excelFileUpload = new MultiFileUpload();
		excelFileUpload.setName("uploader");
		excelFileUpload.setVisible(false);
		excelFileUpload.setAccept(".xls, .xlsx");
		excelFileUpload.addChangeHandler(e -> {
			AonMessagePanel.showLoading(messagePanel, "Analizando fichero excel para importaci\u00f3n ...");
			excelFormPanel.submit();
			
		});
		
		excelFormPanel.addSubmitCompleteHandler(e -> {
			if(AonStringUtils.equalsIgnoreCase(e.getResults(), "<pre>{}</pre>")) {
				AonMessagePanel.showSuccess(messagePanel, "No existen cambios para importar o eliminar en el fichero introducido.");
			} else {
				JSONValue json = getJsonResult(e.getResults());
				resultsPanel.clear();
				
				if (null != json.isObject().get("errors") && null == json.isObject().get("success"))
					AonMessagePanel.showWarning(messagePanel, "No se ha podido importar el fichero. Por favor revise los mensajes de respuesta.");
				else {
					AonMessagePanel.showSuccess(messagePanel, "Importaci\u00f3n realizada correctamente. Compruebe en los logs los datos modificados.");
				}
				
				if (null != json.isObject().get("errors")) createErrorsPanel((JSONArray)json.isObject().get("errors"));
				if (null != json.isObject().get("success")) createSuccessPanel((JSONArray)json.isObject().get("success"));
			}			
		});
		
		FlowPanel formFlowPanel = new FlowPanel();
		formFlowPanel.add(userNameHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(excelFileUpload);
		
		excelFormPanel.add(formFlowPanel);
		toolbar.add(excelFormPanel);
		
		importBtn = new AonToolbarButton("Importar Variables Calculo Contrato", AON.CSS.aonIconImport());
		importBtn.addClickHandler(e -> excelFileUpload.click());
		
		toolbar.add(importBtn);
	}

	private void createErrorsPanel(JSONArray jsonArr) {
		Label errorsTitle = new Label("Errores importando variables de calculo");
		errorsTitle.getElement().getStyle().setProperty("font-size", "1rem");
		errorsTitle.getElement().getStyle().setProperty("font-weight", "bold");
		resultsPanel.add(errorsTitle);
		
		for(int i=0; i < jsonArr.size(); i++) {
			Label error = new Label((i + 1) + ". " + jsonArr.get(i).isString().stringValue());
			error.getElement().getStyle().setProperty("padding", "0 1rem");
			resultsPanel.add(error);
		}
	}
	
	private void createSuccessPanel(JSONArray jsonArr) {
		Label successTitle = new Label("Log importaci\u00f3n variables de calculo");
		successTitle.getElement().getStyle().setProperty("font-size", "1rem");
		successTitle.getElement().getStyle().setProperty("font-weight", "bold");
		resultsPanel.add(successTitle);
		
		for(int i=0; i < jsonArr.size(); i++) {
			Label success = new Label((i + 1) + ". " + jsonArr.get(i).isString().stringValue());
			success.getElement().getStyle().setProperty("padding", "0 1rem");
			resultsPanel.add(success);
		}
	}

	private JSONValue getJsonResult(String results) {
		String jsonStr = results.split(">")[1].split("<")[0];
		return JSONParser.parseStrict(jsonStr);
	}

}
