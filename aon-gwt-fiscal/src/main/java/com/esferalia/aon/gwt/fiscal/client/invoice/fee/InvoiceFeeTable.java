package com.esferalia.aon.gwt.fiscal.client.invoice.fee;

import java.util.Objects;
import java.util.function.Supplier;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.event.AonErrorEvent;
import com.esferalia.aon.gwt.common.client.widget.event.AonErrorHandler;
import com.esferalia.aon.gwt.common.client.widget.event.HasAonErrorHandlers;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceDockPanel.InvoiceDockPanelCallback;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceModuleOptions;
import com.esferalia.aon.occam.api.model.finance.FeeBillingParams;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceProcessOutput;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus.InvoiceCommunicationStatusVisitor;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel.InvoiceErrorLevelVisitor;
import com.esferalia.aon.watson.mutable.MutableBoolean;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

class InvoiceFeeTable extends ScrollPanel implements HasAonErrorHandlers, HasSelectionHandlers<Invoice> {
	
	private FlowPanel container;
	
	private AonDisplayGrid grid;
	private AonDisplayGridRow selectedRow;
	
	InvoiceFeeTable(InvoiceModuleOptions opts, FeeBillingParams params, InvoiceFeeTableCallback callback){
		container = new FlowPanel();
		this.setWidget(container);
		
		grid = new AonDisplayGrid();
		container.add(grid);
		
		this.getElement().getStyle().setProperty("margin", "0 1rem");
		paintHeader();
		onSearch( opts, params, callback );
	}
	
	private void onSearch(InvoiceModuleOptions opts, FeeBillingParams params, InvoiceFeeTableCallback callback) {
		callback.onSearchStart();
		InvoiceFeeModule.SERVICE.getInvoices(opts.getOccam(), params, new AsyncCallback<InvoiceProcessOutput>() {
	
				@Override
				public void onSuccess(InvoiceProcessOutput output) {
					MutableBoolean hasData = new MutableBoolean(false);
					output.invoiceStream()
						.forEach( inv -> {
							paintRow(opts, callback, inv, grid.addRow());
							hasData.setValue(true);
						}
					);
					callback.onSearchEnd( output );
				}
				
				@Override
				public void onFailure(Throwable caught) {
					callback.onEditSearch();
					callback.onError("Error cargando panel de facturaci\u00f3n: " + caught.getMessage());
				}
			}
		);
	}

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
	
	private void paintRow(InvoiceModuleOptions opts, InvoiceFeeTableCallback callback, Invoice inv, AonDisplayGridRow row) {
		CheckBox checkBox = new CheckBox();
		checkBox.addClickHandler(e -> {
			callback.onCheck(checkBox.getValue().booleanValue(), inv);
			e.stopPropagation();
		});
		row
			.addCell(checkBox)
			.addCell(getStatusLabel( opts, inv ))
			.addCell(getWarningLabel( opts, inv ))
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
			callback.onSelect( inv, new InvoiceDockPanelCallback() {
				@Override
				public void onSave(Invoice invoice) {
					row.clear();
					paintRow(opts, callback, invoice, row);
				}
				@Override
				public void onDelete(Invoice invoice) {
					row.clear();
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
	
	private Widget getWarningLabel(InvoiceModuleOptions opts, Invoice inv) {
		FlowPanel warningContainer = new FlowPanel();
		warningContainer.setStyleName(AON.CSS.aonNowrap());
		warningContainer.addStyleName(AON.CSS.aonFlexBetween());
		
		if (inv.hasPrepayments()) {
			Label prepaymentLabel = getIconLabel();
			prepaymentLabel.addStyleName(AON.CSS.aonIconLetterS());
			prepaymentLabel.setTitle("Factura con Suplidos");
			warningContainer.add(prepaymentLabel);
		}
		
		if (inv.hasMessages() ) {
			inv.getMoreSeriousLevel()
				.ifPresent( 
					level -> {
						Label label = level.visit(new InvoiceErrorLevelVisitor<Label>() {
							@Override
							public Label visitINF() {
								Label levelLabel = getIconLabel();
								levelLabel.addStyleName(AON.CSS.aonIconCircleBlue());
								levelLabel.setTitle(level.getLabel());
								return levelLabel;
							}
							@Override
							public Label visitWRN() {
								Label levelLabel = getIconLabel();
								levelLabel.addStyleName(AON.CSS.aonIconCircleOrange());
								levelLabel.setTitle(level.getLabel());
								return levelLabel;
							}
							@Override
							public Label visitERR() {
								Label levelLabel = getIconLabel();
								levelLabel.addStyleName(AON.CSS.aonIconCircleRed());
								levelLabel.setTitle(level.getLabel());
								return levelLabel;
							}
						});
						warningContainer.add(label);
					}
				);
		} else if (!inv.hasPrepayments()) {
			Label levelLabel = getIconLabel();
			levelLabel.addStyleName(AON.CSS.aonIconCircleGreen());
			levelLabel.setTitle("Sin avisos");
			warningContainer.add(levelLabel);
		}
		return warningContainer;
	}

	private Label getIconLabel() {
		Label iconLabel = new Label();
		iconLabel.setStyleName(AON.CSS.aonIconLabel());
		return iconLabel;
	}
	
	private FlowPanel getStatusLabel(InvoiceModuleOptions opts, Invoice inv) {
		FlowPanel statusContainer = new FlowPanel();
		statusContainer.setStyleName(AON.CSS.aonNowrap());
		statusContainer.addStyleName(AON.CSS.aonFlexBetween());
		
		boolean hasCommunication = opts.getConfiguration() != null
			&& opts.getConfiguration().getCommunicationConfig() != null
			&& opts.getConfiguration().getCommunicationConfig().hasCommunication();
		
		
		if (!hasCommunication) {
			if (inv.getId() == null) {
				Label statusLabel = getIconLabel();
				statusLabel.addStyleName(AON.CSS.aonIconUnknown());
				statusLabel.setTitle("Factura simulada");
				statusContainer.add(statusLabel);
			} else if (inv.isProforma()) {
				Label statusLabel = getIconLabel();
				statusLabel.addStyleName(AON.CSS.aonIconAddTask());
				statusLabel.setTitle("Factura proforma - Pendiente");
				statusContainer.add(statusLabel);
			} else {
				Label statusLabel = getIconLabel();
				statusLabel.addStyleName(AON.CSS.aonIconValid());
				statusLabel.setTitle("Factura grabada");
				statusContainer.add(statusLabel);
			}
			
		}
		
		if (hasCommunication) {
			if (inv.getId() == null) {
				Label statusLabel = getIconLabel();
				statusLabel.addStyleName(AON.CSS.aonIconUnknown());
				statusLabel.setTitle("Factura simulada");
				statusContainer.add(statusLabel);
			} else if (inv.isProforma()) {
				Label statusLabel = getIconLabel();				
				statusLabel.addStyleName(AON.CSS.aonIconQrCodeOrange());
				statusLabel.setTitle("Factura proforma - Pendiente");
				statusContainer.add(statusLabel);
			} else {
				AonCollectionUtils.keysStream( inv.getCommunicationInfo() )
					.map(key -> inv.getCommunicationInfo().get(key))
					.filter( Objects::nonNull )
					.filter( info -> info.getStatus() != null )
					.forEach(info -> {
						info.getStatus().accept(new InvoiceCommunicationStatusVisitor() {
	
							@Override
							public void visitPending() {
								Label statusLabel = getIconLabel();
								statusLabel.addStyleName(AON.CSS.aonIconQrCodeOrange());
								statusLabel.setTitle("Factura pendiente de comunicaci\u00f3n");
								statusContainer.add(statusLabel);
							}
	
							@Override
							public void visitAccepted() {
								Label statusLabel = getIconLabel();
								statusLabel.addStyleName(AON.CSS.aonIconQrCodeGreen());
								statusLabel.setTitle("Factura comunicada correctamente");
								statusContainer.add(statusLabel);
							}
	
							@Override
							public void visitAcceptedWithErrors() {
								Label statusLabel = getIconLabel();
								statusLabel.addStyleName(AON.CSS.aonIconQrCodeGreen());
								statusLabel.setTitle("Factura comunicada con errores");
								statusContainer.add(statusLabel);
							}
	
							@Override
							public void visitWrong() {
								Label statusLabel = getIconLabel();
								statusLabel.addStyleName(AON.CSS.aonIconQrCodeRed());
								statusLabel.setTitle("Factura comunicaci\u00f3n rechazada");
								statusContainer.add(statusLabel);
							}
	
							@Override
							public void visitCancelled() {
								Label statusLabel = getIconLabel();
								statusLabel.addStyleName(AON.CSS.aonIconClose());
								statusLabel.setTitle("Factura anulada");
								statusContainer.add(statusLabel);
							}
							
							@Override
							public void visitExternallyCommunicated() {
								Label statusLabel = getIconLabel();
								statusLabel.addStyleName(AON.CSS.aonIconQrCodeBlue());
								statusLabel.setTitle("Factura comunicada externamente");
								statusContainer.add(statusLabel);
							}
						});
					});
			}
		}
		return statusContainer;
	}

	private <T> T ensure(Object nullable, Supplier<T>  supplier, T defaultValue) {
		return (nullable == null) 
			? defaultValue
			: supplier.get();
	}

	public interface InvoiceFeeTableCallback {
		void onSelect( Invoice invoice, InvoiceDockPanelCallback callback);
		void onEditSearch();
		void onSearchStart();
		void onSearchEnd(InvoiceProcessOutput output);
		void onError( String message);
		void onCheck( boolean checked, Invoice invoice );
		void onInfoSelected( InvoiceProcessOutput output );
	}
}
