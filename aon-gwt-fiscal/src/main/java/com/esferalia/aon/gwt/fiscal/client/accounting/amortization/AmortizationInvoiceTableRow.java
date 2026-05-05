package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import java.util.function.Supplier;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridHeaderRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.AonInvoiceViewer;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.accounting.AmortizationInvoice;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

class AmortizationInvoiceTableRow extends AonDisplayGridRow {
	
	AmortizationInvoiceTableRow(AmortizationModuleOptions opts, AmortizationInvoice ami) {
		Invoice inv = ami.getInvoice();
		String issueDate = ensure(inv.getIssueDate(), () -> AON.DATE_FORMAT.format(inv.getIssueDate()), AonStringUtils.EMPTY);
		Label issueDateLabel = new Label(issueDate);
		String creationDate = ensure(inv.getCreationDate(), () -> AON.DATE_FORMAT.format(inv.getCreationDate()), AonStringUtils.EMPTY);
		Label creationDateLabel = new Label(creationDate);
		
		AonTableButton debugInvoice = new AonTableButton("DEBUG",AON.CSS.aonIconDebug());
		debugInvoice.addClickHandler( event -> debugInvoice(opts, inv));
		
		FlowPanel recordedPanel = new FlowPanel();
		if (inv.isRecorded()) {
			AonTableButton recorded = new AonTableButton(AON.MSG.recorded(), AON.CSS.aonIconValid());
			recorded.setTitle( AON.MSG.viewAccountEntry() );
			recorded.addClickHandler( event -> viewAccountEntry(opts, ami.getAccountEntryId()) );
			recordedPanel.add(recorded);
		} else {
			Label unrecorded = new Label();
			unrecorded.setTitle( AON.MSG.unrecorded() );
			unrecorded.setStyleName(AON.CSS.aonLabelWithIcon());
			unrecorded.addStyleName(AON.CSS.aonIconError());
			recordedPanel.add(unrecorded);
		}
		
		this
			.addCell( debugInvoice )
			.addCell(new Label(ensure(inv.getType(),inv.getType()::getAbbrDescription)))
			.addCell(new Label(ensure(inv.getDocumentNumber(), inv::getDocumentNumber, AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(inv.getRegistryDocument(), inv::getRegistryDocument, AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(inv.getRegistryName(), () -> AonStringUtils.abbreviate(inv.getRegistryName(),25), AonStringUtils.EMPTY)))
			.addCellIfElse( inv.isProforma() 
					,new Label(AonStringUtils.EMPTY)
					,new Label(ensure(inv.getReferenceCode(), inv::getReferenceCode, AonStringUtils.EMPTY)))
			.addCell(issueDateLabel)
			.addCell(creationDateLabel)
			.addCell(new Label( AON.FMT.format(inv.getTotal())), AON.CSS.aonTextRight() )
			.addCell(recordedPanel);
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

	static void fillHeader(AonDisplayGridHeaderRow headerRow) {
		headerRow
			.addCell(new Label("Ver"),AON.CSS.aonWidth20())
			.addCell(new Label("Tipo"),AON.CSS.aonWidth40())
			.addCell(new Label("N\u00BA.Doc"),AON.CSS.aonWidth100(),AON.CSS.aonNowrap())
			.addCell(new Label("Doc.Tit."),AON.CSS.aonWidthAuto())
			.addCell(new Label("Nombre/raz\u00F3n social"),AON.CSS.aonWidth300(),AON.CSS.aonNowrap())
			.addCell(new Label("N\u00BA Referencia"),AON.CSS.aonWidth100(),AON.CSS.aonNowrap())
			.addCell(new Label("Fec. Fac."),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
			.addCell(new Label("Fec. Crea."),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
			.addCell(new Label("Total"),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
			.addCell(new Label("Cont"),AON.CSS.aonWidth40())
		;
	}
	

	private void debugInvoice(AmortizationModuleOptions opts, Invoice inv) {
		AonCustomPopup dialog = new AonCustomPopup();
		dialog.setWidth((Window.getClientWidth() - 100) + "px");
		dialog.setHeight((Window.getClientHeight() - 100) + "px");
		dialog.setAnimationEnabled(true);
		dialog.setGlassEnabled(true);
		dialog.setModal(true);
		dialog.setCaption(AON.MSG.invoice());
		dialog.add(new AonInvoiceViewer( inv ));
		dialog.center();
		dialog.show();
	}
	
	private void viewAccountEntry(AmortizationModuleOptions opts, Integer entryId) {
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
			.setAccountEntryId(entryId)
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
