package com.esferalia.aon.gwt.fiscal.client.invoice;

import java.util.Date;
import java.util.Optional;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCertificateListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.console.AonConsoleLogger;
import com.esferalia.aon.gwt.fiscal.client.console.ConsoleReadyStateChangeHandler;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessage;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessageType.ConsoleDomainMessageTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.occam.api.model.finance.InvoiceProcessOutput;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class InvoiceCommunicatorPanel extends DockLayoutPanel implements Focusable {
	private static final Logger LOGGER = Logger.getLogger(InvoiceCommunicatorPanel.class.getName());
	
	private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
	private static final String CONTENT_TYPE = "Content-type";
	private static final String COMMUNICATE_INVOICE_SERVLET = URL.encode(GWT.getModuleBaseURL() + "roms/invoiceCommunication");

	public interface InvoiceCommunicatorPanelCallback {
		void onFinish( InvoiceProcessOutput output );
		void onCancel();
	}

	private static final double CONSOLE_WIDTH = 200;
	
	private AonToolbar toolbar;
	AonToolbarButton okButton = new AonToolbarButton( AON.MSG.accept(), AON.CSS.aonIconAccept());
	AonToolbarButton cancelButton = new AonToolbarButton(AON.MSG.cancelAction(), AON.CSS.aonIconCancel());
	private SimpleLayoutPanel contentPanel = new SimpleLayoutPanel();
	private AonCertificateListBox certificateBox;
	private SimpleLayoutPanel consoleContainer = new SimpleLayoutPanel();
	
	public InvoiceCommunicatorPanel( InvoiceModuleOptions opts, InvoiceConsoleParams params ,InvoiceCommunicatorPanelCallback callback) {
		super(Unit.PX);
		this.setWidth("850px");
		this.setHeight("550px");
    	
		addNorth( getToolbar(opts, params, callback ), AonToolbar.HEIGTH );
    	
    	contentPanel.setWidget( getMainPanel(opts, params) );
		add(contentPanel);
		
		addSouth( consoleContainer, 0 );
	}
	
	private AonToolbar getToolbar(InvoiceModuleOptions opts, InvoiceConsoleParams params, InvoiceCommunicatorPanelCallback callback ) {
		toolbar = new AonToolbar(AON.MSG.generateInvoices());
    	okButton.addClickHandler(event -> {
			okButton.setEnabled(false);
			cancelButton.setEnabled(false);
			send(opts, params, callback);
		});
    	toolbar.add(okButton);
    	
    	cancelButton.addClickHandler(event -> {
    		cancelButton.setEnabled(false);
			cancelButton.setEnabled(false);
			callback.onCancel();
		});
    	toolbar.add(cancelButton);
    	return toolbar;
	}
	
	void send(InvoiceModuleOptions opts, InvoiceConsoleParams params, InvoiceCommunicatorPanelCallback callback) {
		if (validate( opts, params )) {
			AonConfirmDialog.showConfirm(
				"Confirmar"
				,"\u00BFComunicar factura?"
				, new AonConfirmDialogCallback() {
					
					@Override
					public void onAccept() {
						accept(opts, params, callback);
					}
				
					@Override
					public void onCancel() {
						okButton.setEnabled(true);
						cancelButton.setEnabled(true);
					}
				}
				);
		} else {
			okButton.setEnabled(true);
			cancelButton.setEnabled(true);
		}
	}

	private FlowPanel getMainPanel( InvoiceModuleOptions opts, InvoiceConsoleParams params ) {
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.setStyleName(AON.CSS.aonPadding());
		
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginTop());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		Date expDate = new Date();
		if (opts.hasCommunication( expDate ) ) {
			
			if ( opts.getCommunicationConfiguration()
					.filter( icc -> icc.isTbai( expDate ))
					.isPresent()) {
				okButton.setEnabled(false);
				toolbar.showErrorMessage("Programa no preparado para la comunicaci\u00F3n TicketBAI.");
			}
							
			if ( opts.getCommunicationConfiguration()
					.filter( icc -> icc.isLroe( expDate ))
					.isPresent()) {
				okButton.setEnabled(false);
				toolbar.showErrorMessage("Programa no preparado para la comunicaci\u00F3n LROE.");
			}
				
			if ( opts.getCommunicationConfiguration()
					.filter( icc -> icc.isSii( expDate ))
					.isPresent()) {
				okButton.setEnabled(false);
				toolbar.showErrorMessage("Programa no preparado para la comunicaci\u00F3n SII.");
			}
			
			certificateBox = (opts.isCertificateNeededForCommunication())
				? new AonCertificateListBox(opts)
				: null;
			if (certificateBox != null) {
				certificateBox.addSelectionHandler(e -> params.setCertId(certificateBox.getCertificateId().orElse(null)));
			}
			
			opts.optCommunicationConfig().ifPresent( icc -> {
				
				StringBuilder desc = new StringBuilder(AON.MSG.communicateInvoices());
				desc.append(" ");
				icc.typesStream()
					.map( t -> t.getCommunicationType() )
					.flatMap( Optional::stream )
					.forEach( t -> {
						if (desc.length() > 0) desc.append(", ");
						desc.append("[");
						desc.append( t.getAbbr() );
						desc.append(" ");
						desc.append("] ");
					})	
				;
				
				if (certificateBox != null) {
					tab.addLabelWidgetRow(AON.MSG.certificate(), certificateBox);
				}
				
			});
		}
		
		mainPanel.add(tab);
		return mainPanel;
	}

	private boolean hasCommunication(InvoiceModuleOptions opts) {
		return (opts != null 
			&& opts.getConfiguration() != null 
			&& opts.getConfiguration().getCommunicationConfig() != null
			&& opts.getConfiguration().getCommunicationConfig().hasCommunication());
	}

	private boolean validate(InvoiceModuleOptions opts, InvoiceConsoleParams params) {
		toolbar.hideMessages();
		if (hasCommunication( opts ) 
			&& opts.isCertificateNeededForCommunication() 
			&& params.getCertId() == null) {
			
			toolbar.showErrorMessage("El certificado no puede estar vac\u00EDo.");
			certificateBox.setFocus(true);
			return false;
		}
		
		return true;
	}

	@Override
	public int getTabIndex() {
		return okButton.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		okButton.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focus) {
		okButton.setFocus(focus);
	}

	@Override
	public void setTabIndex(int index) {
		okButton.setTabIndex(index);
	}

	private void accept(InvoiceModuleOptions opts, InvoiceConsoleParams params, InvoiceCommunicatorPanelCallback callback) {
		AonConsoleLogger aonConsole = new AonConsoleLogger( () -> new ConsoleDomainMessageTypeVisitor<Widget>() {

			@Override public Widget visitIntegrity(ConsoleDomainMessage cdm) { return null; }
			@Override public Widget visitScopeIntegrity(ConsoleDomainMessage cdm) { return null; }
			@Override public Widget visitProduct(ConsoleDomainMessage cdm) { return null; }
			@Override public Widget visitAgreement(ConsoleDomainMessage cdm) { return null; }
			
			@Override 
			public Widget visitInvoiceProcessOutput(ConsoleDomainMessage cdm) {
				String jsonMessage = cdm.getMessage();
				if( AonStringUtils.isNotBlank( jsonMessage ) ) {
					try {
						JSONObject data = new JSONObject(JsonUtils.safeEval(jsonMessage));
						InvoiceProcessOutput output = new InvoiceProcessOutput();
						getString(data, IJsonNames.LEVEL).ifPresent( s -> output.setProcessErrorLevel( InvoiceErrorLevel.safeValueOf(s) ) );
						getString(data, IJsonNames.MESSAGE).ifPresent( output::setProcessMessage );
						
						getInteger(data, IJsonNames.INVOICE_COUNT).ifPresent( i -> output.getInvoicesInfo().setTotalCount( i ) );
						getDouble(data, IJsonNames.INVOICE_TOTAL_AMOUNT).ifPresent( d -> output.getInvoicesInfo().setTotalAmount( d ) );
						getDouble(data, IJsonNames.INVOICE_TOTAL_VAT).ifPresent( d -> output.getInvoicesInfo().setTotalVAT( d ) );
						getDouble(data, IJsonNames.INVOICE_TOTAL_RETENTION).ifPresent( d -> output.getInvoicesInfo().setTotalRetention( d ) );
						getInteger(data, IJsonNames.INVOICE_TOTAL_PREPAYMENT_COUNT).ifPresent( i -> output.getInvoicesInfo().setTotalPrepaymentCount( i ) );
						
						getInteger(data, IJsonNames.INVOICE_PROFORMA_COUNT).ifPresent( i -> output.getProformasInfo().setTotalCount( i ) );
						getDouble(data, IJsonNames.INVOICE_PROFORMA_TOTAL_AMOUNT).ifPresent( d -> output.getProformasInfo().setTotalAmount( d ) );
						getDouble(data, IJsonNames.INVOICE_PROFORMA_TOTAL_VAT).ifPresent( d -> output.getProformasInfo().setTotalVAT( d ) );
						getDouble(data, IJsonNames.INVOICE_PROFORMA_TOTAL_RETENTION).ifPresent( d -> output.getProformasInfo().setTotalRetention( d ) );
						getInteger(data, IJsonNames.INVOICE_PROFORMA_TOTAL_PREPAYMENT_COUNT).ifPresent( i -> output.getProformasInfo().setTotalPrepaymentCount( i ) );

						getInteger(data, IJsonNames.FROM_ID).ifPresent( output::setFromId );
						getInteger(data, IJsonNames.TO_ID).ifPresent( output::setToId );
						InvoiceProcessOutputPanel panel = new InvoiceProcessOutputPanel( output );
						contentPanel.setWidget(panel);
						callback.onFinish(output);
					} catch (Exception e) {
						e.printStackTrace();
						LOGGER.severe("Error parsing InvoiceProcessOutput JSON: " + e.getMessage());
						FlowPanel panel = new FlowPanel();
						panel.add(new Label("Proceso finalizado."));
						panel.add(new Label("No se ha podido interpretar la informaci\u00f3n adicional del proceso."));
						contentPanel.setWidget(panel);
					}
				} else {
					FlowPanel panel = new FlowPanel();
					panel.add(new Label("Proceso finalizado."));
					panel.add(new Label("No se ha recibido informaci\u00f3n adicional del proceso."));
					contentPanel.setWidget(panel);
				}
				return null;
			}
			private Optional<String> getString(JSONObject data, String key) {
				JSONValue levelV =  data.get(key);
				JSONString s = levelV.isString();
				if (s != null) {
					return Optional.of(s.stringValue());
				}
				return Optional.empty();
			}
			private Optional<Double> getDouble(JSONObject data, String key) {
				JSONValue levelV =  data.get(key);
				JSONNumber s = levelV.isNumber();
				if (s != null) {
					return Optional.of(s.doubleValue());
				}
				return Optional.empty();
			}
			private Optional<Integer> getInteger(JSONObject data, String key) {
				return getDouble(data, key)
					.map( Number::intValue );
			}
		} ); 
		consoleContainer.clear();
		consoleContainer.add(aonConsole);
		this.setWidgetHidden(consoleContainer, false);
		this.setWidgetSize(consoleContainer, CONSOLE_WIDTH);
		this.animate(200);
		
		try {
			XMLHttpRequest xhreq = XMLHttpRequest.create();
			xhreq.open(FormPanel.METHOD_POST, COMMUNICATE_INVOICE_SERVLET);
			xhreq.setRequestHeader(CONTENT_TYPE,APPLICATION_X_WWW_FORM_URLENCODED);
			xhreq.setOnReadyStateChange(  new ConsoleReadyStateChangeHandler( aonConsole, new AsyncCallback<Boolean>() {
				
				@Override
				public void onSuccess(Boolean result) {
					// No se hace nada, se finaliza en visitInvoiceProcessOutput
				}
				
				@Override
				public void onFailure(Throwable caught) {
					// No se hace nada, el error ya se muestra en la consola.
				}
			}));
			StringBuilder requestData = new StringBuilder();
			requestData.append(IRequestParamsNames.DOMAIN_NAME +"=" + opts.getDomainName());
			requestData.append("&"+IRequestParamsNames.DOMAIN_ID +"=" + opts.getDomain());
			requestData.append("&"+IRequestParamsNames.USER +"=" + opts.getUser());
			params.setDomain(opts.getDomain());
			requestData.append("&"+IRequestParamsNames.INVOICE_PARAMS +"=" + JsonParams.convert(params));
			xhreq.send(requestData.toString());
		} catch (Throwable e){
			e.printStackTrace();
			LOGGER.severe("No se pudo ejecutar el proceso." + e.getMessage());
			toolbar.showErrorMessage("No se pudo ejecutar el proceso. " + e.getMessage());
		}
	}
	
}
