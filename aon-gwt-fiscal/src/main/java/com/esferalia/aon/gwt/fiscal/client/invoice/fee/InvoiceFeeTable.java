package com.esferalia.aon.gwt.fiscal.client.invoice.fee;

import java.util.LinkedList;
import java.util.function.Supplier;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.event.AonErrorEvent;
import com.esferalia.aon.gwt.common.client.widget.event.AonErrorHandler;
import com.esferalia.aon.gwt.common.client.widget.event.HasAonErrorHandlers;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoicePanel.InvoicePanelCallback;
import com.esferalia.aon.occam.api.model.finance.FeeBillingParams;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class InvoiceFeeTable extends ScrollPanel implements HasAonErrorHandlers, HasSelectionHandlers<Invoice> {
	
	private FlowPanel container;
	
	private AonDisplayGrid grid;
	private AonDisplayGridRow selectedRow;
	private InvoiceFeeTableInfo tableInfo = new InvoiceFeeTableInfo();
	
	InvoiceFeeTable(InvoiceFeeModuleOptions opts, FeeBillingParams params, InvoiceFeeTableCallback callback){
		container = new FlowPanel();
		this.setWidget(container);
		
		grid = new AonDisplayGrid();
		container.add(grid);
		
		this.getElement().getStyle().setProperty("margin", "0 1rem");
		paintHeader();
		onSearch( opts, params, callback );
	}
	
	private void onSearch(InvoiceFeeModuleOptions opts, FeeBillingParams params, InvoiceFeeTableCallback callback) {
		callback.onSearchStart();
		InvoiceFeeModule.SERVICE.getInvoices(opts.getOccam(), params, new AsyncCallback<LinkedList<Invoice>>() {
	
				@Override
				public void onSuccess(LinkedList<Invoice> feeList) {
					if (AonCollectionUtils.isEmpty(feeList)) {
						showNoDataPanel();
					}
					AonCollectionUtils.stream(feeList)
						.forEach( inv -> {
							paintRow(callback, inv, grid.addRow());
							refreshInfoPanel(tableInfo, inv, null);
						}
					);
					callback.onSearchEnd( tableInfo );
				}
				
				private void showNoDataPanel() {
					FlowPanel line = new FlowPanel();
					InlineLabel label = new InlineLabel(AON.MSG.noData());
					line.add(label);
					container.clear();
					container.add(line);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onEditSearch();
					callback.onError("Error cargando panel de facturaci\u00f3n: " + caught.getMessage());
				}
			}
		);
	}

//	private void showInfoPanel() {
//		infoPanel.clear();
//		
//		AonDisplayTable infoTab = new AonDisplayTable();
//		infoTab.addStyleName(AON.CSS.aonBlockCenter());
//		infoTab.addStyleName(AON.CSS.aonDisplayGridRowSelected() );
//		
//		Label totalCountLabel = new Label( AON.FMT_INT.format(totalCount.getValue() ));
//		totalCountLabel.setStyleName(AON.CSS.aonMarginRight());
//		totalCountLabel.addStyleName(AON.CSS.aonMarginLeft());
//		totalCountLabel.addStyleName(AON.CSS.aonBold());
//		totalCountLabel.addStyleName(AON.CSS.aonTextRight());
//		totalCountLabel.addStyleName(AON.CSS.aonValueChanged());
//		
//		Label totalVATLabel = new Label( AON.FMT.format(totalVAT.getValue()));   
//		totalVATLabel.setStyleName(AON.CSS.aonMarginRight());
//		totalVATLabel.addStyleName(AON.CSS.aonMarginLeft());
//		totalVATLabel.addStyleName(AON.CSS.aonBold());
//		totalVATLabel.addStyleName(AON.CSS.aonTextRight());
//		totalVATLabel.addStyleName(AON.CSS.aonValueChanged());
//
//		Label totalRetentionLabel = new Label( AON.FMT.format(totalRetention.getValue()));   
//		totalRetentionLabel.setStyleName(AON.CSS.aonMarginRight());
//		totalRetentionLabel.addStyleName(AON.CSS.aonMarginLeft());
//		totalRetentionLabel.addStyleName(AON.CSS.aonBold());
//		totalRetentionLabel.addStyleName(AON.CSS.aonTextRight());
//		totalRetentionLabel.addStyleName(AON.CSS.aonValueChanged());
//
//		Label totalAmountLabel = new Label( AON.FMT.format(totalAmount.getValue()));
//		totalAmountLabel.setStyleName(AON.CSS.aonMarginRight());
//		totalAmountLabel.addStyleName(AON.CSS.aonMarginLeft());
//		totalAmountLabel.addStyleName(AON.CSS.aonBold());
//		totalAmountLabel.addStyleName(AON.CSS.aonTextRight());
//		totalAmountLabel.addStyleName(AON.CSS.aonValueChanged());
//
//		infoTab
//			.addLabelWidgetRow("Total facturas:", totalCountLabel)
//			.addLabelWidgetRow("Total IVA:", totalVATLabel)
//			.addLabelWidgetRow("Total Retenci\u00F3n:", totalRetentionLabel)
//			.addLabelWidgetRow("Importe total:", totalAmountLabel)
//		;
//		infoPanel.add(infoTab);
//		
//		new Timer() {
//			@Override
//			public void run() {
//				totalCountLabel.removeStyleName(AON.CSS.aonValueChanged());
//				totalAmountLabel.removeStyleName(AON.CSS.aonValueChanged());
//				totalVATLabel.removeStyleName(AON.CSS.aonValueChanged());
//				totalRetentionLabel.removeStyleName(AON.CSS.aonValueChanged());
//				infoTab.removeStyleName(AON.CSS.aonDisplayGridRowSelected() );
//			}
//		}.schedule(3000);
//		
//	}
	
	private void paintHeader() {
		grid.addHeaderRow()
			.addCell(new Label(""),AON.CSS.aonWidth20())
			.addCell(new Label(""),AON.CSS.aonWidth20())
			.addCell(new Label(""),AON.CSS.aonWidth20())
			.addCell(new Label("Doc.Tit."),AON.CSS.aonWidthAuto())
			.addCell(new Label("Nombre/raz\u00F3n social"),AON.CSS.aonWidth300(),AON.CSS.aonNowrap())
			.addCell(new Label("Fec. Fac."),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
			.addCell(new Label("Total"),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
		;
	}
	
	public void fireError(String message) {
		AonErrorEvent.fire( InvoiceFeeTable.this, message );
	}

	@Override
	public HandlerRegistration addAonErrorHandler(AonErrorHandler handler) {
		return super.addHandler(handler, AonErrorEvent.getType());
	}
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Invoice> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
	
	private void paintRow(InvoiceFeeTableCallback callback, Invoice inv, AonDisplayGridRow row) {
		CheckBox checkBox = new CheckBox();
		checkBox.addClickHandler(e -> {
			callback.onCheck(checkBox.getValue().booleanValue(), inv);
			e.stopPropagation();
		});
		Label statusLabel = new Label();
		statusLabel.setStyleName(AON.CSS.aonIconLabel());
		if (inv.getId() == null) {
			statusLabel.addStyleName(AON.CSS.aonIconQrCodeOrange());
			statusLabel.setTitle("Factura pendiente");
		} else {
			statusLabel.addStyleName(AON.CSS.aonIconQrCodeGreen());
			statusLabel.setTitle("Factura grabada");
		}
		Label prepaymentLabel = new Label();
		if (inv.hasPrepayments()) {
			prepaymentLabel.setStyleName(AON.CSS.aonIconLetterS());
			prepaymentLabel.addStyleName(AON.CSS.aonIconLabel());
			prepaymentLabel.setTitle("Factura con Suplidos");
		}
		row
			.addCell(checkBox)
			.addCell(statusLabel)
			.addCell(prepaymentLabel)
			.addCell(new Label(ensure(inv.getRegistryDocument(), inv::getRegistryDocument, AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(inv.getRegistryName(), () -> AonStringUtils.abbreviate(inv.getRegistryName(),25), AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(inv.getIssueDate(), () -> AON.DATE_FORMAT.format(inv.getIssueDate()), AonStringUtils.EMPTY)))
			.addCell(new Label( AON.FMT.format(inv.getTotal())), AON.CSS.aonTextRight() )
		;
		
		row.addClickHandler(e -> {
			if (selectedRow != null) {
				selectedRow.removeStyleName(AON.CSS.aonDisplayGridRowSelected());
			}
			row.addStyleName(AON.CSS.aonDisplayGridRowSelected());
			selectedRow = row;
			callback.onSelect( inv, new InvoicePanelCallback() {
				@Override
				public void onSave(Invoice invoice) {
					row.clear();
					paintRow(callback, invoice, row);
					refreshInfoPanel(tableInfo, invoice, inv);
				}
				@Override
				public void onDelete(Invoice invoice) {
					row.clear();
					refreshInfoPanel(tableInfo, null, inv);
				}
				@Override
				public Invoice getInvoice() {
					return inv;
				}
				@Override
				public void onError(String message) {
					fireError(message);
				}
			});
		});
		
	}
	
	private <T> T ensure(Object nullable, Supplier<T>  supplier, T defaultValue) {
		return (nullable == null) 
			? defaultValue
			: supplier.get();
	}

	static class InvoiceFeeTableInfo {
		private int totalCount;
		private double totalAmount;
		private double totalVAT;
		private double totalRetention;
		private int totalPrepaymentCount;
		
		InvoiceFeeTableInfo() {
			
		}
		
		int getTotalCount() {
			return totalCount;
		}
		InvoiceFeeTableInfo setTotalCount(int totalCount) {
			this.totalCount = totalCount;
			return this;
		}
		InvoiceFeeTableInfo addTotalCount() {
			this.totalCount++;
			return this;
		}
		InvoiceFeeTableInfo substractTotalCount() {
			this.totalCount--;
			return this;
		}

		double getTotalAmount() {
			return totalAmount;
		}
		InvoiceFeeTableInfo setTotalAmount(double totalAmount) {
			this.totalAmount = totalAmount;
			return this;
		}
		InvoiceFeeTableInfo addTotalAmount(double amount) {
			this.totalAmount = AonMathUtils.round( this.totalAmount + amount);
			return this;
		}
		InvoiceFeeTableInfo substractTotalAmount(double amount) {
			this.totalAmount = AonMathUtils.round( this.totalAmount - amount);
			return this;
		}

		double getTotalVAT() {
			return totalVAT;
		}
		InvoiceFeeTableInfo setTotalVAT(double totalVAT) {
			this.totalVAT = totalVAT;
			return this;
		}
		InvoiceFeeTableInfo addTotalVAT(double vat) {
			this.totalVAT = AonMathUtils.round( this.totalVAT + vat);
			return this;
		}
		InvoiceFeeTableInfo substractTotalVAT(double vat) {
			this.totalVAT = AonMathUtils.round( this.totalVAT - vat);
			return this;
		}

		double getTotalRetention() {
			return totalRetention;
		}
		InvoiceFeeTableInfo setTotalRetention(double totalRetention) {
			this.totalRetention = totalRetention;
			return this;
		}
		InvoiceFeeTableInfo addTotalRetention(double retention) {
			this.totalRetention = AonMathUtils.round( this.totalRetention + retention);
			return this;
		}
		InvoiceFeeTableInfo substractTotalRetention(double retention) {
			this.totalRetention = AonMathUtils.round( this.totalRetention - retention);
			return this;
		}
		
		int getTotalPrepaymentCount() {
			return totalPrepaymentCount;
		}
		InvoiceFeeTableInfo setTotalPrepaymentCount(int totalPrepaymentCount) {
			this.totalPrepaymentCount = totalPrepaymentCount;
			return this;
		}
		InvoiceFeeTableInfo addPrepaymentCount(boolean hasPrepayments) {
			totalPrepaymentCount += hasPrepayments ? 1 : 0;
			return this;
		}
		InvoiceFeeTableInfo substractPrepaymentCount(boolean hasPrepayments) {
			totalPrepaymentCount -= hasPrepayments ? 1 : 0;
			return this;
		}
		
	}

	private void refreshInfoPanel(InvoiceFeeTableInfo tableInfo, Invoice toAdd, Invoice toSubstract) {
		if ( toAdd != null ) {
			tableInfo.addTotalCount();
			tableInfo.addTotalAmount( toAdd.getTotal() );
			tableInfo.addTotalVAT( toAdd.getVatQuota() );
			tableInfo.addTotalRetention( toAdd.getRetentionQuota() );
			tableInfo.addPrepaymentCount(toAdd.hasPrepayments());
		}
		if ( toSubstract != null ) {
			tableInfo.substractTotalCount( );
			tableInfo.substractTotalAmount( toSubstract.getTotal() );
			tableInfo.substractTotalVAT( toSubstract.getVatQuota() );
			tableInfo.substractTotalRetention( toSubstract.getRetentionQuota() );
			if ( toSubstract.hasPrepayments() ) {
				tableInfo.substractPrepaymentCount( toSubstract.hasPrepayments() );
			}
		}
		// Total suplidos.-
		// Total Advances.-
	}

	public interface InvoiceFeeTableCallback {
		void onSelect( Invoice invoice, InvoicePanelCallback callback);
		void onEditSearch();
		void onSearchStart();
		void onSearchEnd(InvoiceFeeTableInfo info);
		void onError( String message);
		void onCheck( boolean checked, Invoice invoice );
		void onInfoSelected( InvoiceFeeTableInfo info );
	}
}
