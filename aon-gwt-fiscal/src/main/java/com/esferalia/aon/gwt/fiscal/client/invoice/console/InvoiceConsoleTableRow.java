package com.esferalia.aon.gwt.fiscal.client.invoice.console;

import java.util.function.Supplier;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridHeaderRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonInvoiceCheckedEvent;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonInvoiceCheckedHandler;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonInvoiceUncheckedEvent;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonInvoiceUncheckedHandler;
import com.esferalia.aon.gwt.fiscal.client.invoice.HasInvoiceCheckedHandlers;
import com.esferalia.aon.gwt.fiscal.client.invoice.HasInvoiceUncheckedHandlers;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceCommunicationPanel;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceModuleOptions;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsole;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;

class InvoiceConsoleTableRow extends AonDisplayGridRow implements HasInvoiceCheckedHandlers, HasInvoiceUncheckedHandlers{
	
	private static final Logger LOGGER = Logger.getLogger(InvoiceConsoleTableRow.class.getName());   

	InvoiceConsoleTableRow(InvoiceModuleOptions opts, InvoiceConsole invConsole) {
		Invoice inv = invConsole.getInvoice();
		String issueDate = ensure(inv.getIssueDate(), () -> AON.DATE_FORMAT.format(inv.getIssueDate()), AonStringUtils.EMPTY);
		Label issueDateLabel = new Label(issueDate);
//		String taxDate = ensure(inv.getTaxDate(), () -> AON.DATE_FORMAT.format(inv.getTaxDate()), AonStringUtils.EMPTY);
//		Label taxDateLabel = new Label(taxDate);
//		if (!AonStringUtils.equals(issueDate, taxDate)) {
//			taxDateLabel.addStyleName(AON.CSS.aonBackgroundHighlightedOrange());
//		}
		String creationDate = ensure(inv.getCreationDate(), () -> AON.DATE_FORMAT.format(inv.getCreationDate()), AonStringUtils.EMPTY);
		Label creationDateLabel = new Label(creationDate);
		
		AonTableButton debugInvoice = new AonTableButton("DEBUG",AON.CSS.aonIconDebug());
		debugInvoice.addClickHandler( event -> debugInvoice(opts, invConsole));
		
		Label invoiceRecorded = new Label();
		invoiceRecorded.setTitle( AON.MSG.recorded() );
		invoiceRecorded.setStyleName(AON.CSS.aonLabelWithIcon());
		invoiceRecorded.addStyleName(AON.CSS.aonIconValid());
		
		this
			.addCellIfElse( inv.isAnnulled() 
				, new Label() 
				, new AonInvoiceCheckButton( inv ) )
			.addCell( debugInvoice )
//			.addCellIfElse( inv.isAnnulled() 
//				, new Label() 
//				, new InvoiceMessagesLabel( inv )  )
//			.addCell(new Label( getSourceDescription(invConsole.getSource())))
			.addCell(new Label(ensure(inv.getType(),inv.getType()::getAbbrDescription)))
//			.addCell(new Label(ensure(inv.getTransaction(),inv.getTransaction()::getTediName)))
			.addCell(new Label(ensure(inv.getDocumentNumber(), inv::getDocumentNumber, AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(inv.getRegistryDocument(), inv::getRegistryDocument, AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(inv.getRegistryName(), () -> AonStringUtils.abbreviate(inv.getRegistryName(),25), AonStringUtils.EMPTY)))
			.addCellIfElse( inv.isProforma() 
					,new Label(AonStringUtils.EMPTY)
					,new Label(ensure(inv.getReferenceCode(), inv::getReferenceCode, AonStringUtils.EMPTY)))
			.addCell(issueDateLabel)
//			.addCell(taxDateLabel)
			.addCell(creationDateLabel)
			.addCell(new Label( AON.FMT.format(inv.getTotal())), AON.CSS.aonTextRight() );
		
		if (inv.isAnnulled()) {
			this.addCell( new Label() );
		} else {
			this.addCellIfElse(inv.isRecorded()
				, invoiceRecorded
				, new Label() );
		}
		
		this.addCell(new InvoiceCommunicationPanel( opts, inv ), AON.CSS.aonTextRight() );
		
		if (inv.isAnnulled()) {
			this.getElement().getStyle().setBackgroundColor("mistyrose");
		}
			
	}
	
//	private String getSourceDescription(InvoiceSource source) {
//		if (source == null) return null;
//		else if (source == InvoiceSource.TEDI) return "Portal";
//		else if (source == InvoiceSource.ACCOUNT) return "Conta.";
//		else if (source == InvoiceSource.FEE) return "Cuotas";
//		return "Gesti\u00F3n";
//	}

	private String ensure(Object nullable, Supplier<String>  supplier) {
		return ensure(nullable, supplier, "---");
	}
	private <T> T ensure(Object nullable, Supplier<T>  supplier, T defaultValue) {
		return (nullable == null) 
			? defaultValue
			: supplier.get();
	}

	static void fillHeader(AonDisplayGridHeaderRow headerRow) {
		headerRow
			.addCell(new Label(""),AON.CSS.aonWidth20())
			.addCell(new Label("Ver"),AON.CSS.aonWidth20())
//			.addCell(new Label("Msg"),AON.CSS.aonWidth20())
//			.addCell(new Label("Orig."),AON.CSS.aonWidth40())
			.addCell(new Label("Tipo"),AON.CSS.aonWidth40())
//			.addCell(new Label("Tran."),AON.CSS.aonWidth40())
			.addCell(new Label("N\u00BA.Doc"),AON.CSS.aonWidth100(),AON.CSS.aonNowrap())
			.addCell(new Label("Doc.Tit."),AON.CSS.aonWidthAuto())
			.addCell(new Label("Nombre/raz\u00F3n social"),AON.CSS.aonWidth300(),AON.CSS.aonNowrap())
			.addCell(new Label("N\u00BA Referencia"),AON.CSS.aonWidth100(),AON.CSS.aonNowrap())
			.addCell(new Label("Fec. Fac."),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
//			.addCell(new Label("Fec. Imp."),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
			.addCell(new Label("Fec. Crea."),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
			.addCell(new Label("Total"),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
			.addCell(new Label("Cont"),AON.CSS.aonWidth40())
			.addCell(new Label(""),AON.CSS.aonWidth120())
		;
	}
	

	private void debugInvoice(InvoiceModuleOptions opts, InvoiceConsole invConsole) {
		AonCustomPopup dialog = new AonCustomPopup();
		dialog.setWidth((Window.getClientWidth() - 100) + "px");
		dialog.setHeight((Window.getClientHeight() - 100) + "px");
		dialog.setAnimationEnabled(true);
		dialog.setGlassEnabled(true);
		dialog.setModal(true);
		dialog.setCaption(AON.MSG.invoice());
		if (invConsole.isAnnulled()) {
			dialog.add(new InvoiceConsoleTextPanel(invConsole.getInvoice()));
		} else {
			dialog.add(new InvoiceConsoleTextPanel(opts.getOccam(), opts.getDomain(), invConsole.getInvoice().getId()));
		}
		dialog.center();
		dialog.show();
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
					LOGGER.info("InvoiceConsoleTableRow: Invoice checked: " + inv.getId());
					AonInvoiceCheckedEvent.fire(InvoiceConsoleTableRow.this, inv);
				} else {
					this.addStyleName(AON.CSS.aonIconCheck());
					this.removeStyleName(AON.CSS.aonIconChecked());
					LOGGER.info("InvoiceConsoleTableRow: Invoice unchecked: " + inv.getId());
					AonInvoiceUncheckedEvent.fire(InvoiceConsoleTableRow.this, inv);
				}
			});
		}

	}
	
}
