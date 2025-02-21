package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCheckBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.SimplePanel;

public class ContractAttachPanel extends SimplePanel {
	
	public static interface AonContractAttachPanelCallback {
		void onAccept();
		void onCancel();
	}
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private static final String CREATEURL = GWT.getModuleBaseURL() + "attach/create/";
	private static final String UPDATEURL = GWT.getModuleBaseURL() + "attach/update/";
	
	private HTMLPanel messagePanel = new HTMLPanel(AonStringUtils.EMPTY);
	
	private AonCustomTextBox description = new AonCustomTextBox("Descripci\u00f3n");
	private AonCustomListBox type = new AonCustomListBox("Tipo");
	private AonCustomDateBox date = new AonCustomDateBox("Fecha");
	private AonCustomListBox scope = new AonCustomListBox("Ambito");
	private AonCustomCheckBox visibility = new AonCustomCheckBox("Visibilidad");
	
	// Form
	private FormPanel form;
	private FileUpload fileUpload;
	private Hidden userLoginHidden = new Hidden("login", Wnd.getCurrentUser());
	private Hidden currentDomainHidden = new Hidden("domain", Wnd.getCurrentDomainNameURL());
	private Hidden attachIdHidden = new Hidden("attachId", "");
	private Hidden contractIdHidden = new Hidden("attachModule", "");
	private Hidden descriptionHidden = new Hidden("description", "");
	private Hidden typeHidden = new Hidden("type", "");
	private Hidden securityHidden = new Hidden("security", "");
	private Hidden dateHidden = new Hidden("date", "");
	private Hidden scopeHidden = new Hidden("scope", "");
	private Hidden mimeTypeHidden = new Hidden("mimeType", "");
	
	private Button okButton;
	
	private Map<Integer, String> scopes = new HashMap<Integer, String>();
	
	// ------------------------------------------------- Constructor
	
	protected ContractAttachPanel(Integer domain, Integer contractId, Map<Integer, String> scopes, AonContractAttachPanelCallback callback) {
		this.scopes = scopes;
		show(new Attach().setDomain(new Domain().setId(domain)).setAttachModule(contractId).setDate(new Date()), callback);
	}
	
	protected ContractAttachPanel(Attach attach, Map<Integer, String> scopes, AonContractAttachPanelCallback callback) {
		this.scopes = scopes;
		show(attach, callback);
	}
	
	private void show(Attach attach, AonContractAttachPanelCallback callback) {
		getElement().getStyle().setProperty("padding", "1rem");
		getElement().getStyle().setProperty("min-width", "25rem");
		
		FlowPanel rootPanel = new FlowPanel();
		rootPanel.setStyleName(AON.CSS.aonFlexColumnBetween());
		
		rootPanel.add(messagePanel);
		
		KeyUpHandler keyUpHandler = new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					callback.onCancel();	
				}
			}
		};
		
		// InitializeFormData
		dateHidden.setValue(null == attach.getDate() ? "" : formatDate.format(attach.getDate()));
		contractIdHidden.setValue(attach.getAttachModule() + "");
		
		if(null != attach.getId()) {
			attachIdHidden.setValue(attach.getId() + "");
			descriptionHidden.setValue(attach.getDescription());
			typeHidden.setValue(attach.getType() + "");
			securityHidden.setValue(attach.getConfidential() + "");
			scopeHidden.setValue(null == attach.getScope() ? "" : attach.getScope().toString());
			mimeTypeHidden.setValue(null == attach.getMimeType() ? "" : attach.getMimeType().getExtension());	
		}
		
		// InitializeView
		form = new FormPanel();
		form.setAction(null == attach.getId() ? CREATEURL : UPDATEURL);
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.addSubmitCompleteHandler(e -> {
			try {
				String jsonStr = e.getResults().split(">")[1].split("<")[0];
				JSONValue json = JSONParser.parseStrict(jsonStr);
				parseJSON(json.isObject(), callback);
			} catch (NullPointerException | IllegalArgumentException err){
				AonMessagePanel.showError(messagePanel, "Error formateando la informaci\u00f3n");
			}
		});

		// Description TextBox
		description.setValue(attach.getDescription());
		description.addValueChangeHandler(e -> descriptionHidden.setValue(e.getValue()));
		rootPanel.add(description);
		
		// Type ListBox
		type.clearItems();
		type.addItem("-", "");
		type.addItem("Borrador del contrato", "0");
		type.addItem("Copia Contrato laboral", "1");
		type.addItem("Domiciliacion bancaria", "7");
		type.addItem("Anexo I", "8");
		type.addItem("Anexo II", "9");
		type.addItem("Borrador prorroga", "10");
		type.addItem("Prorroga", "11");
		type.addItem("Borrador del certificado de empresa", "22");
		type.addItem("TA (Alta)", "98");
		type.addItem("TA (Baja)", "99");
		type.addItem("Contrato (Comunicaci\u00f3n SEPE)", "101");
		type.addItem("Copia basica (Comunicaci\u00f3n SEPE)", "102");
		type.addItem("Certific\u00402 (Pdf)", "103");
		type.addItem("IDC", "104");
		type.addItem("IDCPlNss", "105");
		type.addItem("Notificaci\u00f3n Laboral", "107");
		type.addItem("Transformaci\u00f3n (Comunicaci\u00f3n SEPE)", "108");
		type.addItem("Borrador transformaci\u00f3n contrato", "109");
		type.addItem("Borrador pr\u00f3rroga contrato", "110");
		type.addItem("Borrador propuesta recolocaci\u00f3n", "111");
		type.addItem("Otros", "106");
		type.setValue(null == attach.getType() ? "" : attach.getType().toString());
		type.addChangeHandler(e -> typeHidden.setValue(type.getValue()));
		type.getListBox().setEnabled(null == attach.getType() || !isComunicationCreated(attach.getType()));
		rootPanel.add(type);
		
		// Confidential CheckBox
		visibility.setValue(attach.isConfidential());
		visibility.setTitle(attach.isConfidential() ? "Privado: S\u00f3lo visible para usuarios de la empresa" : "P\u00fablico: Visible para todos los usuarios");
		visibility.addValueChangeHandler(e -> securityHidden.setValue(Boolean.TRUE.equals(visibility.getValue()) ? "true" : "false"));
		rootPanel.add(visibility);
		
		// Attach DateBoxEx
		date.setValue(null == attach.getDate() ? new Date() : attach.getDate());
		date.addValueChangeHandler(e -> dateHidden.setValue(null == e.getValue() ? "" : formatDate.format(e.getValue())));
		rootPanel.add(date);
		
		// Scope ListBox
		scope.clearItems();
		scope.addItem("-", "");
		for(Entry<Integer, String> entry : this.scopes.entrySet())
			scope.addItem(entry.getValue(), entry.getKey().toString());
		scope.setValue(null == attach.getScope() ? "" : attach.getScope().toString());
		scope.addChangeHandler(e -> scopeHidden.setValue(scope.getValue()));
		rootPanel.add(scope);
		
		// FileUpload
		FileUpload fileUpload = new FileUpload();
		fileUpload.setName("uploader");
		fileUpload.getElement().setPropertyString("multiple", "multiple");
		fileUpload.getElement().getStyle().setDisplay(Display.NONE);

		fileUpload.addChangeHandler(e -> {
			String filename = getFileName(fileUpload.getFilename());
			String fileExt = getFileExtension(fileUpload.getFilename());

			if (filename.length() == 0)
				Window.alert("Cant upload file - Try again");
			else {
				mimeTypeHidden.setValue(fileExt);
				descriptionHidden.setValue(filename);
				description.setValue(filename);

				final int size = getFileSize(fileUpload.getElement());
				if (size > 15000000)
					AonMessagePanel.showError(messagePanel, "El archivo adjunto no puede ser superior a 10 MB");

			}
		});
		
		// Attach File Button
		AonTableButton fileAttach = new AonTableButton("Subir Documento", AON.CSS.aonIconAttach());
		fileAttach.addClickHandler(e -> fileUpload.click());
		fileAttach.setVisible(null == attach.getId());
		description.addButton(fileAttach);

		// Add all to FlowPanel to add to FormPanel
		HTMLPanel flowFormPanel = new HTMLPanel("");
		flowFormPanel.add(userLoginHidden);
		flowFormPanel.add(currentDomainHidden);
		flowFormPanel.add(contractIdHidden);
		flowFormPanel.add(attachIdHidden);
		flowFormPanel.add(descriptionHidden);
		flowFormPanel.add(typeHidden);
		flowFormPanel.add(mimeTypeHidden);
		flowFormPanel.add(securityHidden);
		flowFormPanel.add(dateHidden);
		flowFormPanel.add(scopeHidden);
		flowFormPanel.add(fileUpload);
		form.add(flowFormPanel);
		rootPanel.add(form);
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	
    	okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonIconSave());
    	okButton.addStyleName(AON.CSS.aonButtonDialog());
    	okButton.setText(attach.getId() == null ? "Guardar documento" : "Guardar");
    	okButton.addKeyUpHandler( keyUpHandler);
    	okButton.addClickHandler(e -> {
    		okButton.setEnabled(false);
    		form.submit();
    	});
    	buttons.add(okButton);
    	
    	Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addKeyUpHandler( keyUpHandler);
    	cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cancelButton.setEnabled(false);
				callback.onCancel();
			}
		});
    	buttons.add(cancelButton);
    	rootPanel.add(buttons);
		setWidget(rootPanel);
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	description.setFocus(true);
	        }
	    });	
	}
	
	// ------------------------------------------------------ FileUpload.Methods

	private native int getFileSize(final Element data) /*-{
		return data.files[0].size;
	}-*/;
	
	// ------------------------------------------------------ ToogleButton.Methods

	private boolean isComunicationCreated(byte attachType) {
		return attachType == ((byte)98) || attachType == ((byte)99) || attachType == ((byte)101) || attachType == ((byte)102) || attachType == ((byte)103) || attachType == ((byte)108);
	}

	// ------------------------------------------------- Parse JSON
	
	private void parseJSON(JSONObject json, AonContractAttachPanelCallback callback) {
		JSONValue type = json.get("type");
		if(null != type && AonStringUtils.isNotBlank(type.toString())) {
			if(AonStringUtils.containsIgnoreCase(type.toString(), "create")) {
				AonMessagePanel.showSuccess(messagePanel, "El documento se ha generado correctamente");
				Timer timer = new Timer() {
				     @Override
				     public void run() {
				    	 callback.onAccept();
				     }
				};
				timer.schedule(2500);
			} else if(AonStringUtils.containsIgnoreCase(type.toString(), "update")) {
				AonMessagePanel.showSuccess(messagePanel, "El documento se ha actualizado correctamente");
				Timer timer = new Timer() {
				     @Override
				     public void run() {
				    	 callback.onAccept();
				     }
				};
				timer.schedule(2500);
			} else {
				JSONValue message = json.get("message");
				AonMessagePanel.showError(messagePanel, message.toString());
				okButton.setEnabled(true);
			}
		}
	}
	

	// ------------------------------------------------- Auxiliar Methods
	
	private String getFileName(String filename) {
		String[] splits = filename.split("\\\\");
		return splits[splits.length-1].contains("\\.") ? splits[splits.length-1].split("\\.")[0] : splits[splits.length-1];
	}
	
	private String getFileExtension(String filename) {
		String[] splits = filename.split("\\.");
		return splits[splits.length-1];
	}

	
}
