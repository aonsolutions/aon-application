package com.esferalia.aon.gwt.fiscal.client.invoice.fee;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceDockPanel.InvoiceDockPanelCallback;
import com.esferalia.aon.gwt.fiscal.client.invoice.fee.InvoiceFeeTable.InvoiceFeeTableCallback;
import com.esferalia.aon.gwt.fiscal.client.invoice.fee.InvoiceFeeTable.InvoiceFeeTableInfo;
import com.esferalia.aon.occam.api.model.finance.FeeBillingParams;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class InvoiceFeeDockPanel extends AonDockLayout {

	private FlowPanel messagePanel = new FlowPanel();
	private final DockLayoutPanel dockPanel;
	private final InvoiceFeeFilter invoiceFeeFilter;
	private final SimpleLayoutPanel tableContainer;
	private SimpleLayoutPanel eastContainer;
	private LinkedList<Invoice> checkedInvoices = new LinkedList<>();
	private AonToolbarButton infoButton; 
	private AonToolbarButton invoiceAllButton;
	private AonToolbarButton invoiceSomeButton;
	private HandlerRegistration infoButtonClickHandler;
	
	public InvoiceFeeDockPanel(final InvoiceFeeModuleOptions opts) {
		super( "Panel Facturaci\u00f3n de Cuotas" );
		
		paintToolbar( opts );

		dockPanel = new DockLayoutPanel(Unit.PX);

		HTMLPanel container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.add(messagePanel);

		invoiceFeeFilter = new InvoiceFeeFilter( opts );
		invoiceFeeFilter.addAonErrorHandler(event -> {
			AonMessagePanel.showError(messagePanel, event.getMessage());	
		});
		invoiceFeeFilter.addAonSearchHandler(event -> onSearch( opts, invoiceFeeFilter.getWidgetParams(opts) ) );
		invoiceFeeFilter.addAonResetHandler(e -> {
			infoButton.setEnabled( false );
			infoButton.setVisible( false );
			checkedInvoices = new LinkedList<>();
		});
		container.add(invoiceFeeFilter);
		
		dockPanel.addNorth( container, 80 );		
		
		tableContainer = new SimpleLayoutPanel();
		
		FlowPanel lineContainer = new FlowPanel();
		lineContainer.setWidth("80%");
		lineContainer.setStyleName(AON.CSS.aonBlockCenter());
		FlowPanel line = new FlowPanel();
		line.setStyleName(AON.CSS.aonMessageInfo());
		Label messageLabel = new Label("Seleccione Mes/A\u00F1o y las opciones que desee "
			+ "y pulse Buscar para cargar las cuotas pendientes de facturaci\u00f3n.");
		line.add( messageLabel );
		lineContainer.add( line );
		tableContainer.add(lineContainer);
		
		dockPanel.add(tableContainer);
		
		this.add(dockPanel);
		
		this.hideToolbarFilterMessages();
		
	}
	

	private void paintToolbar(InvoiceFeeModuleOptions opts) {
		infoButton = new AonToolbarButton( AON.MSG.information(), AON.CSS.aonIconInfo() );
		infoButton.setEnabled( false );
		infoButton.setVisible( false );
		getToolbar().add(infoButton);
		
		invoiceAllButton = new AonToolbarButton( AON.MSG.information(), AON.CSS.aonIconList() );
		invoiceAllButton.setText( "Facturar TODO" );
		invoiceAllButton.setEnabled( false );
		invoiceAllButton.setVisible( false );
		invoiceAllButton.addClickHandler(e -> { 
			AonCustomDialog popup = new AonCustomDialog();
			popup.setCaption("Datos para la generaci\u00f3n de facturas");
			popup.showCloseButton(true);
			
			InvoiceFeeInvoicingPanel invoicingPanel = new InvoiceFeeInvoicingPanel( 
				opts
				,invoiceFeeFilter.getWidgetParams(opts)
				,new InvoiceFeeInvoicingPanel.InvoiceFeeInvoicingPanelCallback() {
					@Override
					public void onAccept(FeeBillingParams params) {
						String action;
						if (params.isCommunicable()) {
						    action = "la grabaci\u00f3n y comunicaci\u00f3n de facturas";
						} else if (params.mustSaveAsProforma()) {
						    action = "la grabaci\u00f3n de facturas proforma";
						} else {
						    action = "la grabaci\u00f3n de facturas";
						}
						String msg = "Se va a realizar " + action + ". \u00BFEst\u00e1 seguro que desea continuar con la facturaci\u00f3n?";
						
						AonConfirmDialog.showConfirm(
							"Confirmar facturaci\u00f3n"
							,msg
							,new AonConfirmDialogCallback() {
							
								@Override
								public void onAccept() {
									popup.hide();
									invoiceAllButton.setEnabled( false );
									params.setDryRun( false );
									params.setSaveAsProforma( true );
									onSearch(opts, params);
								}
								@Override
								public void onCancel() {
									invoiceAllButton.setEnabled( false );
								}
						});
					}
					@Override
					public void onCancel() {
						popup.hide();
					}
				}
			);
			
			popup.add( invoicingPanel );
			popup.center();
			popup.show();
			
			Scheduler.get().scheduleDeferred(new Command() {
		        public void execute() {
		        	invoicingPanel.setFocus(true);
		        }
		    });
		}
		);
		getToolbar().add(invoiceAllButton);
		
		invoiceSomeButton = new AonToolbarButton( AON.MSG.information(), AON.CSS.aonIconAddBlock() );
		invoiceSomeButton.setText( "Facturar SELECCI\u00D3N" );
		invoiceSomeButton.setEnabled( false );
		invoiceSomeButton.setVisible( false );
		getToolbar().add(invoiceSomeButton);
		

	}

	private void onSearch(InvoiceFeeModuleOptions opts, FeeBillingParams params) {
		tableContainer.clear();
		
		DockLayoutPanel dock = new DockLayoutPanel(Unit.PX);
		
		InvoiceFeeTable tab = new InvoiceFeeTable(opts, params, new InvoiceFeeTableCallback() {
			@Override
			public void onSelect( Invoice invoice, InvoiceDockPanelCallback callback ) {
				InvoiceTabLayout invoicePanel = new InvoiceTabLayout(opts, invoice, callback);
				eastContainer.setWidget( invoicePanel );
			}
			@Override
			public void onEditSearch() {
				infoButton.setEnabled( false );
				infoButton.setVisible( false );
			}
			@Override 
			public void onSearchStart() { 
				AonMessagePanel.showLoading(messagePanel, "Cargando panel de facturaci\u00f3n ..."); 
			}
			@Override 
			public void onSearchEnd(InvoiceFeeTableInfo info) { 
				AonMessagePanel.hideMessage(messagePanel);
				onInfoSelected( info );
				manageButtons();
			}
			@Override 
			public void onError(String message) {
				AonMessagePanel.showError(messagePanel, message ); 
			}
			@Override 
			public void onCheck(boolean checked, Invoice invoice) {  
				manageCheckedInvoice(checked, invoice); 
			}
			@Override 
			public void onInfoSelected( InvoiceFeeTableInfo info ) {
				showInfoPanel( info );
				if (infoButtonClickHandler != null) {
					infoButtonClickHandler.removeHandler();
				}
				infoButtonClickHandler = infoButton.addClickHandler(e -> showInfoPanel( info ) );
				infoButton.setEnabled( true );
				infoButton.setVisible( true );
			}
		});
		tab.addAonErrorHandler(event -> AonMessagePanel.showError(messagePanel, event.getMessage()));
		dock.addWest(tab, 550);
		
		eastContainer = new SimpleLayoutPanel();
		eastContainer.setStyleName(AON.CSS.aonSelector());
		dock.add(eastContainer);
		
		tableContainer.setWidget( dock );
		
		checkedInvoices = new LinkedList<>();
	}
	
	private void showInfoPanel( InvoiceFeeTableInfo info ) {
		eastContainer.setWidget( new InvoiceFeeInfoPanel(info) );
	}

	private void manageCheckedInvoice(boolean checked, Invoice invoice) {
		if ( checked ) {
			checkedInvoices.add( invoice );
		} else {
			checkedInvoices.remove( invoice );
		}
		manageButtons();
	}

	private void manageButtons() {
		boolean somethingCheked = AonCollectionUtils.isNotEmpty(checkedInvoices);
		invoiceAllButton.setEnabled( !somethingCheked );
		invoiceAllButton.setVisible( !somethingCheked );
		invoiceSomeButton.setEnabled( somethingCheked );
		invoiceSomeButton.setVisible( somethingCheked );
	}
}
