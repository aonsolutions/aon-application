package com.esferalia.aon.gwt.mod200.client.matrix;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.AonCertificationPopup;
import com.esferalia.aon.gwt.fiscal.client.AonCertificationPopup.AonCertificationPopupParams;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.http.AonHttpUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsDate;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.xhr.client.XMLHttpRequest;

class ModelMatrixFilterPanel extends AonDisplayTable implements HasValueChangeHandlers<FiscalMatrixParams>, Focusable {
	
	private static final Logger LOGGER = Logger.getLogger(ModelMatrixFilterPanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	private static CommonServiceAsync COMMON_SERVICE;

	private ListBox year;
	private ListBox model;
	private ListBox admon;
	private ListBox scopeBox;
	private CheckBox showConfigurated;
	private CheckBox showMadeModels;
	private AonTextBox declared;
	private AonSearchPanelButton refreshButton;
//	private AonSearchPanelButton configButton;
	private AonSearchPanelButton sendButton;
	private ListBox statusBox;
	private ListBox periodBox;	
	private CheckBox multiplePresentation;
	private boolean sending;
	
	protected ModelMatrixFilterPanel(MatrixModuleOptions options) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		COMMON_SERVICE.getAonConfiguration(options.getDomainName(), options.getDomain(), options.getUser(),
				new AsyncCallback<AonConfiguration>() {
			@Override
			public void onSuccess(AonConfiguration result) {
				options.setConfiguration(result);
				load(options);
			}
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("No se puede cargar la página [Interno: " + caught.getMessage()+ "]");
			}
		});
	}
	
	protected void load(MatrixModuleOptions options) {
		if (!options.isCompactMode()) {
			addStyleName(AON.CSS.aonSearchPanel());
			addStyleName(AON.CSS.aonMarginTop());
			addStyleName(AON.CSS.aonWidthAlmostAll());
			addStyleName(AON.CSS.aonMarginLeft());
			addStyleName(AON.CSS.aonMarginRight());
		}
		addStyleName(AON.CSS.aonBlockCenter());

		InlineLabel yearLabel = new InlineLabel(AON.MSG.fiscalYear());
		yearLabel.setStyleName(AON.CSS.aonMarginRight());
		year = new ListBox();
		year.setStyleName(AON.CSS.aonMarginRight());
		for (int i = 2012; i < 2025; i++) {
			String y = AonNumberUtils.toString(i);
			year.addItem(y, y);
			if (i == AonDateUtils.getCurrentYear()) {
				year.setSelectedIndex(year.getItemCount() - 1);	
			}
		}
		year.addChangeHandler(event -> {
			options.setResult(null);
			fireValueChangeEvent();	
		});
		
		InlineLabel modelLabel = new InlineLabel(AON.MSG.fiscalModels());
		modelLabel.setStyleName(AON.CSS.aonMarginRight());
		model = new ListBox();
		model.setStyleName(AON.CSS.aonMarginRight());
		model.addItem(" TODOS ", "");
		for (FiscalModelType m : FiscalModelType.values()) {
			if (m != FiscalModelType.M140 && m != FiscalModelType.M240 && m != FiscalModelType.SII && m != FiscalModelType.M303_RG && m != FiscalModelType.M303_RS && m != FiscalModelType.M310 && m != FiscalModelType.M311 && m != FiscalModelType.M340 ) {
				model.addItem(AON.MSG.fiscalModelType(m), m.toString());
			}
		}
		model.addChangeHandler(event -> fireValueChangeEvent());

		InlineLabel declaredLabel = new InlineLabel(AON.MSG.declared());
		declaredLabel.setStyleName(AON.CSS.aonMarginRight());
		declared = new AonTextBox();
		declared.setStyleName(AON.CSS.aonMarginRight());
		declared.addValueChangeHandler(event -> fireValueChangeEvent());

		InlineLabel admonLabel = new InlineLabel(AON.MSG.administration());
		admonLabel.setStyleName(AON.CSS.aonMarginRight());
		
		admon = new ListBox();
		admon.setStyleName(AON.CSS.aonMarginRight());
		admon.addItem(" TODAS ", "");
		for (Administration a : Administration.values()) {
			admon.addItem(a.getDescription());
		}
		admon.addChangeHandler(event -> fireValueChangeEvent());
		
		InlineLabel scopeLabel = new InlineLabel(AON.MSG.scope());
		scopeBox = new ListBox();
		boolean showScopes = (options != null && options.getConfiguration() != null && options.getConfiguration().hasAvailableScopes());
		if (showScopes) {
			scopeLabel.setStyleName(AON.CSS.aonMarginRight());
			scopeBox.setStyleName(AON.CSS.aonMarginRight());
			scopeBox.addItem(" TODOS ", "");
			for (Scope scope : options.getConfiguration().getAvailableScopes()) {
				scopeBox.addItem(scope.getDescription(),AonNumberUtils.toString(scope.getId()));
			}
			scopeBox.addChangeHandler(event -> fireValueChangeEvent());
		}

		showConfigurated = new CheckBox();
		showConfigurated.setValue(false);
		showConfigurated.setStyleName(AON.CSS.aonMarginRight());
		showConfigurated.setText("Mostrar los configurados en par\u00E1metros fiscales");
		showConfigurated.addClickHandler(event -> fireValueChangeEvent());
		
		showMadeModels = new CheckBox();
		showMadeModels.setValue(true);
		showMadeModels.setStyleName(AON.CSS.aonMarginRight());
		showMadeModels.setText("Mostrar los realizados");
		showMadeModels.addClickHandler(event -> fireValueChangeEvent());
		
		// Botón refrescar
		refreshButton = new AonSearchPanelButton(AON.MSG.refresh(),AON.CSS.aonIconRefresh());
		refreshButton.addStyleName(AON.CSS.aonMarginRight());
		refreshButton.addClickHandler(event -> fireValueChangeEvent());

//		configButton = new AonSearchPanelButton(AON.MSG.settings(),AON.CSS.aonIconSettings());
//		configButton.addStyleName(AON.CSS.aonMarginRight());
//		configButton.addClickHandler(event -> showConfigurationPanel());
		
		// Boton presentación multiple
		sendButton = new AonSearchPanelButton("Presentaci\u00F3n m\u00FAltiple de los modelos seleccionados",AON.CSS.aonIconSend());
		sendButton.addStyleName(AON.CSS.aonMarginRight());
		sendButton.setVisible(false);
		sendButton.addClickHandler(event -> send(options));
		
		// Estado 
		InlineLabel statusLabel = new InlineLabel(AON.MSG.status());
		statusLabel.setStyleName(AON.CSS.aonMarginRight());

		statusBox = new ListBox();
		statusBox.setStyleName(AON.CSS.aonMarginRight());
		statusBox.addItem(" TODOS ", "");
		for (FiscalStatus fs : FiscalStatus.values()) {
			statusBox.addItem(fs.getName());
		}
		statusBox.addChangeHandler(event -> fireValueChangeEvent());
		
		// Periodo
		InlineLabel periodLabel = new InlineLabel(AON.MSG.period());
		periodLabel.setStyleName(AON.CSS.aonMarginRight());

		periodBox = new ListBox();
		periodBox.setStyleName(AON.CSS.aonMarginRight());
		periodBox.addItem(" TODOS ", "");
		for (Period p : Period.values()) {
			periodBox.addItem(p.getDescription());
		}
		periodBox.addChangeHandler(event -> {
			options.setResult(null);
			fireValueChangeEvent();
		});
		
		// Check para habilitar presentacion múltiple
		multiplePresentation = new CheckBox();		
		multiplePresentation.setVisible(true);
		multiplePresentation.setValue(false);
		multiplePresentation.setStyleName(AON.CSS.aonMarginRight());
		multiplePresentation.setText("Habilitar presentaci\u00F3n m\u00FAltiple");		
		multiplePresentation.addClickHandler(event -> {
			
			// Si se marca "Habilitar Presentacion Múltiple", ponemos Territorio Común, Estado Finalizado y Periodo por defecto (si no esta seleccionado periodo)
			if (multiplePresentation.getValue()) {
				admon.setSelectedIndex(Administration.COMMON_TERRITORY.ordinal() + 1);
				statusBox.setSelectedIndex(FiscalStatus.FINISHED.ordinal() + 1);
				
				if (periodBox.getSelectedIndex() == 0) {
					int m = JsDate.create().getMonth() + 1;					
					Period p;
					switch (m) {
						case 1:
							p = Period.T4;
							break;
						case 4:
							p = Period.T1;
							break;
						case 7:
							p = Period.T2;
							break;
						case 10:
							p = Period.T3;
							break;
						default:
							p = Period.values()[m-2];
							break;
					}					
					periodBox.setSelectedIndex(p.ordinal() + 1); 
				}		
			}			
			fireValueChangeEvent();	
		});
		
		if (!options.isCompactMode()) {
			addRow()
				.addCell(yearLabel,AON.CSS.aonTableLabel())
				.addCell(year)
				.addCell(modelLabel,AON.CSS.aonTableLabel())
				.addCell(model)
				.addCell(statusLabel,AON.CSS.aonTableLabel())
				.addCell(statusBox)
				.addCell(showConfigurated)
				.addCell(declaredLabel)
				.addCell(declared)
				.addCell(new InlineLabel(),AON.CSS.aonFlexGrow1())
				;
			
			addRow()
				.addCell(admonLabel,AON.CSS.aonTableLabel())
				.addCell(admon)
				.addCell(showScopes?scopeLabel:new InlineLabel(),AON.CSS.aonTableLabel())
				.addCell(showScopes?scopeBox:new InlineLabel())
				.addCell(periodLabel,AON.CSS.aonTableLabel())
				.addCell(periodBox)
				.addCell(showMadeModels)
				.addCell(new AonDisplayTable().addRow().addCell(refreshButton).addCell(sendButton))
				.addCell(multiplePresentation)
				.addCell(new InlineLabel(),AON.CSS.aonFlexGrow1())
			;
		} else {
			showConfigurated.setValue(true);
			addRow()
				.addCell(yearLabel,AON.CSS.aonTableLabel())
				.addCell(year)
				.addCell(new InlineLabel())
				.addCell(showConfigurated,AON.CSS.aonFlexGrow1())
				;
		}
		
		fireValueChangeEvent();
	}

	protected void fireValueChangeEvent() {
		int y = AonNumberUtils.toint(year.getSelectedValue());
		FiscalMatrixParams params = new FiscalMatrixParams();
		
		// Administracion
		Administration administration = null;
		if ( admon.getSelectedIndex() > 0) {
			administration = Administration.values()[admon.getSelectedIndex() - 1];
		}
		
		// Modelo 
		FiscalModelType modelType = null;
		if ( model.getSelectedIndex() > 0) {
			modelType = FiscalModelType.valueOf(model.getSelectedValue());
		}
		
		// Scope
//		LOGGER.info("Scope ..: " + scopeBox.getSelectedValue()); 
		Integer scope = AonNumberUtils.toInteger(scopeBox.getSelectedValue());
		
		// Estado 
		FiscalStatus status = null;
		if (statusBox.getSelectedIndex() > 0) {
			status = FiscalStatus.values()[statusBox.getSelectedIndex() - 1];
		}
		
		// Periodo 
		Period period = null;
		if (periodBox.getSelectedIndex() > 0) {
			period = Period.values()[periodBox.getSelectedIndex() - 1];		
		}
		
		if (administration != Administration.COMMON_TERRITORY || status != FiscalStatus.FINISHED || period == null) {
			multiplePresentation.setValue(false,false);
		}
		
		sendButton.setVisible(multiplePresentation.getValue());
		
		// Si está marcado "Habilitar presentación múltiple", desmarcamos "Mostrar los configurados", solo se actua sobre los realizados 
		if (multiplePresentation.getValue()) {
			showConfigurated.setValue(false,false);
		}			
		showConfigurated.setEnabled(!multiplePresentation.getValue());
		
		params.setYear(y)
			.setModel(modelType)
			.setAdministration(administration)
			.setScope(scope)
			.setConfiguredVisible(showConfigurated.getValue())
			.setMadeModelsVisible(showMadeModels.getValue())
			.setDeclared( declared.getValue() )
			.setStatus(status)
			.setPeriod(period)
			.setMultiplePresentation(multiplePresentation.getValue())			
			;

		ValueChangeEvent.fire(ModelMatrixFilterPanel.this, params);
	}

	@Override
	public int getTabIndex() {
		return year.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		// Nothing
	}

	@Override
	public void setFocus(boolean focused) {
		year.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		year.setTabIndex(index);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<FiscalMatrixParams> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	public int getSelectedYear() {
		return AonNumberUtils.toint(year.getSelectedItemText()); 
	}

//	private void showConfigurationPanel() {
//		// TODO Auto-generated method stub
//		
//	}

	public AonSearchPanelButton getRefreshButton() {
		return refreshButton;
	}
	
	private void send(MatrixModuleOptions options) {
		
		// Comprobar si se ha seleccionado algún modelo
		if (options.getSelected().size() == 0) {
			AonMessageDialog.show("PRESENTACION MULTIPLE", "Debe seleccionar al menos un modelo.");
			return;
		}
		
		if (!sending) {
			sending = true;
			
			API api = new API(GWT.getModuleBaseURL(), 
					options.getConfiguration().getMd5(),
					options.getConfiguration().getDomain().getName(), 
					options.getConfiguration().getDomain().getId(),
					options.getConfiguration().getUser().getLogin());
			
			// Pedir Certificado (se usa el que está en aon-gwt-fiscal)
			AonCertificationPopupParams params = new AonCertificationPopupParams()
					.setDocument(options.getConfiguration().fiscal().getCertificateDocument())
					.setName(options.getConfiguration().fiscal().getCertificateName())
					.setTestEnvironment(options.getConfiguration().fiscal().isTestEnvironment())
					.setShowNRC(false)									
					.setInfoMessage("Presentaci\u00F3n m\u00FAltiple de los modelos seleccionados");

			AonCertificationPopup certPopup = new AonCertificationPopup(api, params) {
					
				@Override
				protected void onCancel() {
					sending = false;
				}
				
				@Override
				protected void onAccept(AEATParams aeatParams) {
					
					// Pedir confirmación del envío de la presentación
					AonConfirmDialog cd = new AonConfirmDialog();
					cd.confirm(AON.MSG.confirmDeclarationsendAction(), new AonConfirmDialogCallback() {

						@Override
						public void onAccept() {
							aeatParams.setDomainName(options.getDomainName())
								  	  .setDomainId(options.getDomain())
									  .setUser(options.getUser())
									  .setMod(null)
									  .setSelected(options.getSelected());
							
							sendAEAT(aeatParams, options);
						}

						@Override
						public void onCancel() {
							sending = false;
						}
					});
				}
			};
			certPopup.center();
		}				
				
	}
	
	private void sendAEAT(AEATParams params, MatrixModuleOptions options) {
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		
		options.setResult(null);

//		cleanViewers();
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open(FormPanel.METHOD_POST, GWT.getHostPageBaseURL() +"aon_gwt_mod200/ms/MatrixSendAEAT");
		xhr.setRequestHeader(AonHttpUtils.CONTENT_TYPE,AonHttpUtils.APPLICATION_FORM_URLENCODED);
		xhr.setOnReadyStateChange(xhreq -> {
			int state = xhreq.getReadyState();
			if (state == XMLHttpRequest.DONE) {
				ArrayBuffer buff = xhreq.getResponseArrayBuffer();
//				String contentTypeHeader = xhreq.getResponseHeader( AonHttpUtils.CONTENT_TYPE);
//				getCallback().sendSuccessfully(); // Siempre se recarga el modelo, por si se ha grabado el NRC
//				if (AonStringUtils.equals(MimeType.PDF.getName(), contentTypeHeader)) {
//					Scheduler.get().scheduleDeferred(() -> showPDF( buff.toString() ));
//				} else {
//					showHtml( buff.toString() );
//				}
//				Window.alert(buff.toString());
				options.setResult(buff.toString());
				options.getSelected().clear();
				sending = false;
				popup.hide();					
				refreshButton.click();
			}
		});	
		StringBuilder requestData = new StringBuilder();
		requestData.append("&"+IRequestParamsNames.AEAT_PARAMS +"=" + JsonParams.convert( params ));
		xhr.send(requestData.toString());
		
	}

}
