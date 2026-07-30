package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import java.util.LinkedList;
import java.util.function.Supplier;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonFlexTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonFlexTable.AonFlexTableRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.amortization.AmortizationPanel.AmortizationPanelCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.AonInvoiceViewer;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonInvoiceCheckedEvent;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonInvoiceCheckedHandler;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonInvoiceUncheckedEvent;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonInvoiceUncheckedHandler;
import com.esferalia.aon.gwt.fiscal.client.invoice.HasInvoiceCheckedHandlers;
import com.esferalia.aon.gwt.fiscal.client.invoice.HasInvoiceUncheckedHandlers;
import com.esferalia.aon.gwt.fiscal.client.invoice.console.InvoiceConsoleAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.invoice.console.InvoiceConsoleService;
import com.esferalia.aon.gwt.fiscal.client.invoice.console.InvoiceConsoleServiceAsync;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsole;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.watson.mutable.MutableBoolean;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class AmortizationInvoiceSelectionTable extends ScrollPanel implements HasInvoiceCheckedHandlers, HasInvoiceUncheckedHandlers{
	
	private static final InvoiceConsoleServiceAsync INVOICE_SERVICE;
	static {
		InvoiceConsoleServiceAsync fiscalServiceRaw = GWT.create(InvoiceConsoleService.class);
		INVOICE_SERVICE = new InvoiceConsoleAsyncDecorator(fiscalServiceRaw);
	}
	private static final String[] COLUMN_WIDTHS = new String[] {
		"40px","40px","40px","40px","100px","80px","80px","150px","300px","100px" };

	FlowPanel containerPanel = new FlowPanel();
	private AonFlexTable grid = new AonFlexTable(COLUMN_WIDTHS, AON.CSS.aonBlockCenter());
	
	private static final int LIMIT = 50;
	private final MutableInt offset = new MutableInt(0);
	private final MutableBoolean moreData = new MutableBoolean(true);
	private final MutableBoolean searchEnabled = new MutableBoolean( true );
	private int lastScrollPos = 0;	
	
	AmortizationInvoiceSelectionTable(AmortizationModuleOptions opts, AmortizationPanelCallback callback, InvoiceConsoleParams params) {
		setStyleName(AON.CSS.aonScrollArea());
		
		setWidget(containerPanel);
		
		containerPanel.add(grid);
		fillHeader();
		
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
					onSearch(opts, callback, params);
				}
			}
		});
		onSearch(opts, callback, params);
	}
	
	private boolean isSearchEnabled() {
		return searchEnabled.isTrue();
	}
	private void disableSearch() {
		searchEnabled.setValue(false);
	}
	private void enableSearch() {
		searchEnabled.setValue(true);
	}
	private boolean isMoreData() {
		return moreData.isTrue();
	}
	private void disableMoreData() {
		moreData.setValue(false);
	}
	private void enableMoreData() {
		moreData.setValue(true);
	}
	
	private void onSearch(AmortizationModuleOptions opts, AmortizationPanelCallback callback, InvoiceConsoleParams params) {
		if (!isMoreData()) return;
		params.setOffset( offset.getValue() );
		params.setLimit( LIMIT );
		INVOICE_SERVICE.getInvoices(opts.getOccam(), params, new AsyncCallback<LinkedList<InvoiceConsole>>() {
			
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
			
			public AonFlexTableRow addRow(InvoiceConsole invConsole) {
				AmortizationInvoiceSelectionTableRow row = new AmortizationInvoiceSelectionTableRow(opts, callback, invConsole);
				grid.add( row );
				row.addInvoiceCheckedHandler(e -> AonInvoiceCheckedEvent.fire(AmortizationInvoiceSelectionTable.this, e.getInvoice()));
				row.addInvoiceUncheckedHandler(e -> AonInvoiceUncheckedEvent.fire(AmortizationInvoiceSelectionTable.this, e.getInvoice()));
				return row;
			}
		});
	}
	
	private void fillHeader() {
		grid
			.addHeaderCell(new Label(""))
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
	}

	@Override
	public HandlerRegistration addInvoiceCheckedHandler(AonInvoiceCheckedHandler handler) {
		return super.addHandler(handler, AonInvoiceCheckedEvent.getType());
	}

	@Override
	public HandlerRegistration addInvoiceUncheckedHandler(AonInvoiceUncheckedHandler handler) {
		return super.addHandler(handler, AonInvoiceUncheckedEvent.getType());
	}
	
	static class AmortizationInvoiceSelectionTableRow extends AonFlexTableRow implements HasInvoiceCheckedHandlers, HasInvoiceUncheckedHandlers{
		
		AmortizationInvoiceSelectionTableRow(AmortizationModuleOptions opts, AmortizationPanelCallback callback, InvoiceConsole invConsole) {
			Invoice inv = invConsole.getInvoice();
			String issueDate = ensure(inv.getIssueDate(), () -> AON.DATE_FORMAT.format(inv.getIssueDate()), AonStringUtils.EMPTY);
			Label issueDateLabel = new Label(issueDate);
			
			AonTableButton debugInvoice = new AonTableButton(AON.MSG.viewInvoice(),AON.CSS.aonIconData());
			debugInvoice.addClickHandler( event -> viewInvoice(opts, callback, invConsole));
			
			FlowPanel recordedPanel = new FlowPanel();
			if (inv.isRecorded()) {
				Label recorded = new Label();
				recorded.setStyleName(AON.CSS.aonLabelWithIcon());
				recorded.addStyleName(AON.CSS.aonIconValid());
				recorded.setTitle( AON.MSG.recorded() );
				recordedPanel.add(recorded);
			} else {
				Label unrecorded = new Label();
				unrecorded.setTitle( AON.MSG.unrecorded() );
				unrecorded.setStyleName(AON.CSS.aonLabelWithIcon());
				unrecorded.addStyleName(AON.CSS.aonIconError());
				recordedPanel.add(unrecorded);
			}
			
			this
				.addCell( new AonInvoiceCheckButton( inv ) )
				.addCell( debugInvoice )
				.addCell( recordedPanel )
				.addCell(new Label(ensure(inv.getType(),inv.getType()::getAbbrDescription)))
				.addCellIfElse( inv.isProforma() 
						,new Label(AonStringUtils.EMPTY)
						,new Label(ensure(inv.getReferenceCode(), inv::getReferenceCode, AonStringUtils.EMPTY)))
				.addCell(issueDateLabel)
				.addCell(new Label( AON.FMT.format(inv.getTotal())), AON.CSS.aonTextRight() )
				.addCell(new Label(ensure(inv.getRegistryDocument(), inv::getRegistryDocument, AonStringUtils.EMPTY)))
				.addCell(new Label(ensure(inv.getRegistryName(), () -> AonStringUtils.abbreviate(inv.getRegistryName(),25), AonStringUtils.EMPTY)))
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

		private void viewInvoice(AmortizationModuleOptions opts, AmortizationPanelCallback callback, InvoiceConsole invConsole) {
			INVOICE_SERVICE.getInvoice( opts.getOccam(), opts.getDomain(), invConsole.getId(), new AsyncCallback<Invoice>() {

				@Override
				public void onSuccess(Invoice arg0) {
					AonCustomPopup dialog = new AonCustomPopup();
					dialog.setWidth((Window.getClientWidth() - 100) + "px");
					dialog.setHeight((Window.getClientHeight() - 100) + "px");
					dialog.setAnimationEnabled(true);
					dialog.setGlassEnabled(true);
					dialog.setModal(true);
					dialog.setCaption(AON.MSG.invoice());
					dialog.add(new AonInvoiceViewer( invConsole.getInvoice() ));
					dialog.center();
					dialog.show();
				}
				
				@Override
				public void onFailure(Throwable arg0) {
					callback.showError( arg0.getMessage() );
				}
			});
		}
		
		@Override
		public HandlerRegistration addInvoiceCheckedHandler(AonInvoiceCheckedHandler handler) {
			return super.addHandler(handler, AonInvoiceCheckedEvent.getType());
		}

		@Override
		public HandlerRegistration addInvoiceUncheckedHandler(AonInvoiceUncheckedHandler handler) {
			return super.addHandler(handler, AonInvoiceUncheckedEvent.getType());
		}
		
		private class AonInvoiceCheckButton extends AonTableButton {
			private boolean checked = false;
			
			public AonInvoiceCheckButton( Invoice inv) {
				super("",AON.CSS.aonIconCheck());
				addClickHandler( event -> {
					checked = !checked;
					if (checked) {
						this.addStyleName(AON.CSS.aonIconChecked());
						this.removeStyleName(AON.CSS.aonIconCheck());
						AonInvoiceCheckedEvent.fire(AmortizationInvoiceSelectionTableRow.this, inv);
					} else {
						this.addStyleName(AON.CSS.aonIconCheck());
						this.removeStyleName(AON.CSS.aonIconChecked());
						AonInvoiceUncheckedEvent.fire(AmortizationInvoiceSelectionTableRow.this, inv);
					}
				});
			}

		}
	}
}
