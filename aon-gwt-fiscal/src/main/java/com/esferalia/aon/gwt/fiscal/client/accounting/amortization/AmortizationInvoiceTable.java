package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import java.util.LinkedList;
import java.util.function.Supplier;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonFlexTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonFlexTable.AonFlexTableRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.accounting.amortization.AmortizationPanel.AmortizationPanelCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.AonInvoiceViewer;
import com.esferalia.aon.gwt.fiscal.client.invoice.console.InvoiceConsoleAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.invoice.console.InvoiceConsoleService;
import com.esferalia.aon.gwt.fiscal.client.invoice.console.InvoiceConsoleServiceAsync;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.accounting.AmortizationInvoice;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class AmortizationInvoiceTable extends ScrollPanel {
	
	private static final InvoiceConsoleServiceAsync INVOICE_SERVICE;
	static {
		InvoiceConsoleServiceAsync fiscalServiceRaw = GWT.create(InvoiceConsoleService.class);
		INVOICE_SERVICE = new InvoiceConsoleAsyncDecorator(fiscalServiceRaw);
	}
	
	private static final String[] COLUMN_WIDTHS = new String[] {
			"40px","40px","40px","40px","100px","80px","80px","100px","300px","100px"	
	};
	
	FlowPanel containerPanel = new FlowPanel();
	AonFlexTable grid = new AonFlexTable(COLUMN_WIDTHS, AON.CSS.aonBlockCenter());
	
	AmortizationInvoiceTable(AmortizationModuleOptions opts, AmortizationPanelCallback callback) {
		setStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMarginTop());
		
		setWidget(containerPanel);
		
		grid
			.addHeaderCell(new Label())
			.addHeaderCell(new Label("Ver"))
			.addHeaderCell(new Label("Cont"))
			.addHeaderCell(new Label("Tipo"))
			.addHeaderCell(new Label("N\u00BA Referencia"),AON.CSS.aonNowrap())
			.addHeaderCell(new Label("Fec. Fac."),AON.CSS.aonNowrap())
			.addHeaderCell(new Label("Total"),AON.CSS.aonNowrap())
			.addHeaderCell(new Label("Doc.Tit."))
			.addHeaderCell(new Label("Nombre/raz\u00F3n social"),AON.CSS.aonNowrap())
			.addHeaderCell(new Label("N\u00BA.Doc"),AON.CSS.aonNowrap())
		;
		containerPanel.add(grid);
		onSearch(opts, callback);
	}
	
	private void onSearch(AmortizationModuleOptions opts, AmortizationPanelCallback callback) {
		Integer domain = callback.getAmortization().getDomain();
		Integer id = callback.getAmortization().getId();
		AmortizationModule.SERVICE.getInvoices(opts.getOccam(), domain, id, new AsyncCallback<LinkedList<AmortizationInvoice>>() {
			
			@Override
			public void onSuccess(LinkedList<AmortizationInvoice> amis) {
				AonCollectionUtils.stream(amis)
					.forEach( ami -> {
						AonFlexTableRow row = grid.addRow();
						fillRow( opts, callback, row , ami);
					});
			}
			
			@Override
			public void onFailure(Throwable e) {
				AonMessageDialog.error( "Error inexperado: " + e.getMessage());
			}
		});
	}
	

	private void fillRow(AmortizationModuleOptions opts, AmortizationPanelCallback callback, AonFlexTableRow row, AmortizationInvoice ami) {
		Invoice inv = ami.getInvoice();
		String issueDate = ensure(inv.getIssueDate(), () -> AON.DATE_FORMAT.format(inv.getIssueDate()), AonStringUtils.EMPTY);
		Label issueDateLabel = new Label(issueDate);
		
		AonTableButton unlinkInvoice = new AonTableButton(AON.MSG.unlinkInvoice(),AON.CSS.aonIconLinkOff());
		unlinkInvoice.addClickHandler( event -> unlinkInvoice(opts, callback, inv));
		
		AonTableButton viewInvoice = new AonTableButton(AON.MSG.viewInvoice(), AON.CSS.aonIconData());
		viewInvoice.addClickHandler( event -> viewInvoice(opts, callback, inv));
		
		FlowPanel recordedPanel = new FlowPanel();
		if (inv.isRecorded()) {
			AonTableButton recorded = null; 
			if (ami.isFixedAssetInAccountEntry()) {
				recorded = new AonTableButton(AON.MSG.viewAccountEntry(), AON.CSS.aonIconValid());
			} else {
				String title = AON.MSG.fixedAssetNotInAccountEntry() + ". " + AON.MSG.viewAccountEntry();
				recorded = new AonTableButton(title, AON.CSS.aonIconWarning());
				recorded.addStyleName(AON.CSS.aonBlink());
			} 
			recorded.addClickHandler( event -> viewAccountEntry(opts, ami.getAccountEntry()) );
			recordedPanel.add(recorded);
		} else {
			Label unrecorded = new Label();
			unrecorded.setTitle( AON.MSG.unrecorded() );
			unrecorded.setStyleName(AON.CSS.aonLabelWithIcon());
			unrecorded.addStyleName(AON.CSS.aonIconError());
			recordedPanel.add(unrecorded);
		}

		String registryName = ensure(inv.getRegistryName(), () -> AonStringUtils.abbreviate(inv.getRegistryName(),40), AonStringUtils.EMPTY);
		Label registryNameLabel = new Label(registryName);
		if (AonStringUtils.length(registryName) > 40) {
			registryNameLabel.setTitle(inv.getRegistryName());
		}
		row
			.addCell( unlinkInvoice )
			.addCell(viewInvoice )
			.addCell(recordedPanel)
			.addCell(new Label(ensure(inv.getType(),inv.getType()::getAbbrDescription)))
			.addCellIfElse( inv.isProforma() 
					,new Label(AonStringUtils.EMPTY)
					,new Label(ensure(inv.getReferenceCode(), inv::getReferenceCode, AonStringUtils.EMPTY)))
			.addCell(issueDateLabel)
			.addCell(new Label( AON.FMT.format(inv.getTotal())), AON.CSS.aonTextRight() )
			.addCell(new Label(ensure(inv.getRegistryDocument(), inv::getRegistryDocument, AonStringUtils.EMPTY)))
			.addCell(registryNameLabel)
			.addCell(new Label(ensure(inv.getDocumentNumber(), inv::getDocumentNumber, AonStringUtils.EMPTY)))
		;
	}
	
	private String ensure(Object nullable, Supplier<String>  supplier) {
		return ensure(nullable, supplier, "---");
	}
	private <T> T ensure(Object nullable, Supplier<T>  supplier, T defaultValue) {
		return (nullable == null) 
			? defaultValue
			: supplier.get();
	}

	private void unlinkInvoice(AmortizationModuleOptions opts, AmortizationPanelCallback callback, Invoice invoice) {
		AonConfirmDialog.showConfirm(AON.MSG.unlinkInvoice(), AON.MSG.confirmUnlinkInvoice()
			, new AonConfirmDialogCallback() {
			
			@Override
			public void onAccept() {
				AmortizationModule.SERVICE.unlinkInvoice(opts.getOccam()
					, opts.getDomain()
					, callback.getAmortization().getId()
					, invoice.getId(), new AsyncCallback<Void>() {

						@Override
						public void onSuccess(Void arg0) {
							callback.refresh();
						}
						
						@Override
						public void onFailure(Throwable arg0) {
							callback.showError( arg0.getMessage() );
						}
					}
				);
			}
		});
	}

	private void viewInvoice(AmortizationModuleOptions opts, AmortizationPanelCallback callback, Invoice inv) {
		INVOICE_SERVICE.getInvoice( opts.getOccam(), opts.getDomain(), inv.getId(), new AsyncCallback<Invoice>() {

			@Override
			public void onSuccess(Invoice fullInvoice) {
				AonCustomPopup dialog = new AonCustomPopup();
				dialog.setWidth((Window.getClientWidth() - 100) + "px");
				dialog.setHeight((Window.getClientHeight() - 100) + "px");
				dialog.setAnimationEnabled(true);
				dialog.setGlassEnabled(true);
				dialog.setModal(true);
				dialog.setCaption(AON.MSG.invoice());
				dialog.add(new AonInvoiceViewer( fullInvoice ));
				dialog.center();
				dialog.show();
			}
			
			@Override
			public void onFailure(Throwable arg0) {
				callback.showError( arg0.getMessage() );
			}
		});
	}
	
	
	private void viewAccountEntry(AmortizationModuleOptions opts, AccountEntry entry) {
		if (entry == null) {
			AonMessageDialog.error(AON.MSG.accountEntryNotFound());
			return;
		}
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption(AON.MSG.accountEntries());
		AccountEntryModule module = new AccountEntryModule();
		module.onModuleLoad(new AccountEntryModuleOptions()
			.setParentWidget(entryDialog)
			.setDomainName(opts.getDomainName())
			.setUser(opts.getUser())
			.setDomain(opts.getDomain())
			.setAccountEntryId(entry.getId())
			.setSessionLogTabVisible(false)
			.setJournalTabVisible(false)
			.setExtraInfoTabVisible(false)
			.setExternalCallback( new ModuleCallback() {
				
				private static final long serialVersionUID = -7040645114567563036L;

				@Override
				public void onRemove(IAccountEntryWrapper removed) {
					entryDialog.hide();
				}
				
				@Override
				public void onFailure(Throwable caught) {
					entryDialog.hide();
				}
				
				@Override
				public void onExit() {
					entryDialog.hide();
				}
				
				@Override
				public void onChange(IAccountEntryWrapper changed) {
					entryDialog.hide();
				}
			})
		);
		entryDialog.center();
		entryDialog.show();
	}
	
}
