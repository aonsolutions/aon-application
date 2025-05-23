package com.esferalia.aon.gwt.fiscal.client.invoice.console;

import java.util.LinkedList;
import java.util.function.Supplier;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsole;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.watson.mutable.MutableBoolean;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class InvoiceConsoleTable extends ScrollPanel{

	FlowPanel containerPanel = new FlowPanel();
	AonDisplayGrid grid = new AonDisplayGrid();
	
	private static final int LIMIT = 50;
	private final MutableInt offset = new MutableInt(0);
	private final MutableBoolean moreData = new MutableBoolean(true);
	private final MutableBoolean searchEnabled = new MutableBoolean( true );
	private int lastScrollPos = 0;	
	
	InvoiceConsoleTable(InvoiceConsoleModuleOptions opts, InvoiceConsoleParams params) {
		setStyleName(AON.CSS.aonScrollArea());
		
		setWidget(containerPanel);
		
		grid.addStyleName(AON.CSS.aonMarginTop());
		grid.addStyleName(AON.CSS.aonBlockCenter());
		containerPanel.add(grid);
		paintHeader();
		
		addScrollHandler(event -> {
			// ------------------------------------ Ignore scroll up.
			int oldScrollPos = lastScrollPos;
			lastScrollPos = getVerticalScrollPosition();
			if (oldScrollPos >= lastScrollPos) {
				return;
			}
			// -----------------------------------------------------
			if (isSearchEnabled()) {
				int maxScrollTop = getWidget().getOffsetHeight() - getOffsetHeight();
				if (lastScrollPos >= maxScrollTop) {
					disableSearch();
					onSearch(opts, params);
				}
			}
		});
		onSearch(opts, params);
	}
	
	public boolean isSearchEnabled() {
		return searchEnabled.isTrue();
	}
	public void disableSearch() {
		searchEnabled.setValue(false);
	}
	public void enableSearch() {
		searchEnabled.setValue(true);
	}
	public boolean isMoreData() {
		return moreData.isTrue();
	}
	public void disableMoreData() {
		moreData.setValue(false);
	}
	public void enableMoreData() {
		moreData.setValue(true);
	}
	
	private void paintHeader() {
		grid.addHeaderRow()
			.addCell(new Label(""),AON.CSS.aonWidth20())
			.addCell(new Label(""),AON.CSS.aonWidth20())
			.addCell(new Label(""),AON.CSS.aonWidth20())
			.addCell(new Label("Cont"),AON.CSS.aonWidth40())
			.addCell(new Label("Orig."),AON.CSS.aonWidth40())
			.addCell(new Label("Tipo"),AON.CSS.aonWidth40())
			.addCell(new Label("Tran."),AON.CSS.aonWidth40())
			.addCell(new Label("N\u00BA.Doc"),AON.CSS.aonWidth100(),AON.CSS.aonNowrap())
			.addCell(new Label("Doc.Tit."),AON.CSS.aonWidthAuto())
			.addCell(new Label("Nombre/raz\u00F3n social"),AON.CSS.aonWidth300(),AON.CSS.aonNowrap())
			.addCell(new Label("N\u00BA Referencia"),AON.CSS.aonWidth100(),AON.CSS.aonNowrap())
			.addCell(new Label("Fec. Fac."),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
			.addCell(new Label("Fec. Imp."),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
			.addCell(new Label("Fec. Crea."),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
			.addCell(new Label("Total"),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
		;
	}

	private void onSearch(InvoiceConsoleModuleOptions opts, InvoiceConsoleParams params) {
		if (!isMoreData()) return;
		params.setOffset( offset.getValue() );
		params.setLimit( LIMIT );
		InvoiceConsoleModule.INVOICE_SERVICE.getInvoices(opts.getOccam(), params, new AsyncCallback<LinkedList<InvoiceConsole>>() {
			
			@Override
			public void onSuccess(LinkedList<InvoiceConsole> invoices) {
				int size = AonCollectionUtils.size(invoices);
				AonCollectionUtils.stream(invoices).forEach(this::addRow);
				offset.add( size );
				enableMoreData();
				if ( size < params.getLimit() ) {
					FlowPanel line = new FlowPanel();
					line.setStyleName(AON.CSS.aonTextCenter());
					if (offset.getValue() > 0) {
						line.add(new InlineLabel(AON.MSG.noMoreData()));
					} else {
						line.add(new InlineLabel(AON.MSG.noData()));
					}
					containerPanel.add(line);
					disableMoreData();
				}
				enableSearch();
			}
			
			@Override
			public void onFailure(Throwable e) {
				AonMessageDialog.error( "Error inexperado: " + e.getMessage());
			}
			
			public AonDisplayGridRow addRow(InvoiceConsole invConsole) {
				Invoice inv = invConsole.getInvoice();
				AonDisplayGridRow row = grid.addRow();
				String issueDate = ensure(inv.getIssueDate(), () -> AON.DATE_FORMAT.format(inv.getIssueDate()), AonStringUtils.EMPTY);
				Label issueDateLabel = new Label(issueDate);
				String taxDate = ensure(inv.getTaxDate(), () -> AON.DATE_FORMAT.format(inv.getTaxDate()), AonStringUtils.EMPTY);
				Label taxDateLabel = new Label(taxDate);
				if (!AonStringUtils.equals(issueDate, taxDate)) {
					taxDateLabel.addStyleName(AON.CSS.aonBackgroundHighlightedOrange());
				}
				String creationDate = ensure(inv.getCreationDate(), () -> AON.DATE_FORMAT.format(inv.getCreationDate()), AonStringUtils.EMPTY);
				Label creationDateLabel = new Label(creationDate);
				
				AonTableButton debugInvoice = new AonTableButton("DEBUG",AON.CSS.aonIconWrench());
				debugInvoice.addClickHandler( event -> debugInvoice(inv));
				
				AonTableButton recordInvoice = new AonTableButton(AON.MSG.record(),AON.CSS.aonIconAccountingRecord());
				recordInvoice.addClickHandler( event -> showEntry(opts, inv.getId()) );
				
				AonTableButton showEntry = new AonTableButton(AON.MSG.viewAccountEntry(),AON.CSS.aonIconLink());
				showEntry.addClickHandler( event -> showEntry(opts, inv.getId()));
				
				return row
					.addCell( new InvoiceMessagesLabel( inv )  )
					.addCellIfElse( invConsole.getInvoice().getDoc().isPresent(),debugInvoice,new Label())
					.addCellIfElse(inv.isRecorded(), showEntry, recordInvoice)
					.addCell(new Label( getSourceDescription(invConsole.getSource())))
					.addCell(new Label(ensure(inv.getType(),inv.getType()::getAbbrDescription)))
					.addCell(new Label(ensure(inv.getTransaction(),inv.getTransaction()::getTediName)))
					.addCell(new Label(ensure(inv.getDocumentNumber(), inv::getDocumentNumber, AonStringUtils.EMPTY)))
					.addCell(new Label(ensure(inv.getRegistryDocument(), inv::getRegistryDocument, AonStringUtils.EMPTY)))
					.addCell(new Label(ensure(inv.getRegistryName(), () -> AonStringUtils.abbreviate(inv.getRegistryName(),25), AonStringUtils.EMPTY)))
					.addCell(new Label(ensure(inv.getReferenceCode(), inv::getReferenceCode, AonStringUtils.EMPTY)))
					.addCell(issueDateLabel)
					.addCell(taxDateLabel)
					.addCell(creationDateLabel)
					.addCell(new Label( AON.FMT.format(inv.getTotal())), AON.CSS.aonTextRight() )
				;
			}

			private String getSourceDescription(InvoiceSource source) {
				if (source == null) return null;
				else if (source == InvoiceSource.TEDI) return "Portal";
				else if (source == InvoiceSource.ACCOUNT) return "Conta.";
				return "Gesti\u00F3n";
			}

			private String ensure(Object nullable, Supplier<String>  supplier) {
				return ensure(nullable, supplier, "---");
			}
			private <T> T ensure(Object nullable, Supplier<T>  supplier, T defaultValue) {
				return (nullable == null) 
					? defaultValue
					: supplier.get();
			}

		});
		
		
	}

	private void showEntry(InvoiceConsoleModuleOptions opts,Integer invoiceId) {
		InvoiceConsoleModule.INVOICE_SERVICE.getAccountingInvoice(opts.getOccam(), opts.getDomain(), invoiceId
				, new AsyncCallback<AccountingInvoice>() {

					@Override
					public void onSuccess(AccountingInvoice result) {
						if (result != null) {
							AonCustomPopup entryDialog = new AonCustomPopup();
							entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
							entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
							entryDialog.setAnimationEnabled(true);
							entryDialog.setGlassEnabled(true);
							entryDialog.setModal(true);
							entryDialog.setCaption(AON.MSG.accountingDocument());
							AccountEntryModule module = new AccountEntryModule();
							module.onModuleLoad(new AccountEntryModuleOptions()
									.setParentWidget(entryDialog)
									.setDomainName(opts.getDomainName())
									.setDomain(opts.getDomain())
									.setUser(opts.getUser())
									.setConfiguration(opts.getConfiguration())
									.setAccountingInvoice( result )
									.setTediResult(new TediResult()
										.setAon(result)
										.setInv(result.getInvoice()))
									.setBackButtonVisible(false)
									.setSessionLogTabVisible(false)
									.setJournalTabVisible(false)
									.setExtraInfoTabVisible(false)
									.setExternalCallback(new ModuleCallback() {
		
										private static final long serialVersionUID = -2947804456883665519L;
		
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
	//										entryDialog.hide();
	//										result.setAon((AccountingInvoice) changed);
	//										StringBuilder buf = new StringBuilder();
	//										if (result.getAccountingInvoice() != null 
	//										 && result.getAccountingInvoice().getAccountEntry() != null) {
	//												buf.append(AON.MSG.journal());
	//												buf.append(": ");
	//												buf.append(result.getAccountingInvoice().getAccountEntry().getJournal());
	//										} else {
	//											buf.append("CONTABILIZADO");
	//										}
	//										Label label = new Label( buf.toString() );
	//										label.setStyleName( AON.CSS.aonColorGreen() );
	//										refreshRow(opt, cbk, rawdoc, label, true);
										}
									}));
							entryDialog.center();
							entryDialog.show();
						} else {
							AonMessageDialog.error("Factura no encontrada.", () -> {});
						}
					}
	
					@Override
					public void onFailure(Throwable caught) {
						AonMessageDialog.error("Se ha producido un error al intentar mostrar el documento de la factura.", () -> {});
					}
				});
	}
				
	
	private void debugInvoice(Invoice invoice) {
		AonCustomPopup dialog = new AonCustomPopup();
		dialog.setWidth((Window.getClientWidth() - 100) + "px");
		dialog.setHeight((Window.getClientHeight() - 100) + "px");
		dialog.setAnimationEnabled(true);
		dialog.setGlassEnabled(true);
		dialog.setModal(true);
		dialog.setCaption(AON.MSG.invoice());
		dialog.add(new InvoiceConsoleTextPanel(invoice));
		dialog.center();
		dialog.show();
	}
	
}
