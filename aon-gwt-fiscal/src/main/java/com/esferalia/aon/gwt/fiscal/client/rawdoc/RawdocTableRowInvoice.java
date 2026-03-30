package com.esferalia.aon.gwt.fiscal.client.rawdoc;

import java.util.Optional;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceMessagesLabel;
import com.esferalia.aon.gwt.fiscal.client.rawdoc.RawdocModule.RawdocCallback;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

class RawdocTableRowInvoice extends RawdocTableRowAbs<Invoice> {

	RawdocTableRowInvoice(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		super( opt, cbk, rawdoc);
	}

	@Override
	protected Optional<Invoice> getDoc(Rawdoc rawdoc) {
		if (rawdoc == null) return Optional.empty();
		return Optional.ofNullable( rawdoc.getInvoice() );
	}

	@Override
	protected Label getDocumentLabel(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		return new Label( getDoc(rawdoc).map( Invoice::getRegistryDocument ).orElse( "" ));
	}

	@Override
	protected Label getNameLabel(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		return new Label( getDoc(rawdoc).map( Invoice::getRegistryName ).orElse( "" ));
	}
	
	@Override
	protected Label getAmountLabel(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		return new Label( getDoc(rawdoc).map( Invoice::getTotal ).map( AON.FMT::format ).orElse( "" ));
	}

	@Override
	protected Label getDateLabel(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		return new Label( getDoc(rawdoc).map( Invoice::getIssueDate ).map( AON.DATE_FORMAT::format ).orElse( "" ));
	}

	@Override
	protected Label getReferenceLabel(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		return new Label( getDoc(rawdoc).map( Invoice::getReferenceCode ).orElse( "" ) );
	}

	protected AonTableButton getActionButton(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		AonTableButton accountEntry = null;
		if (rawdoc.isRecordable()) {
			accountEntry = new AonTableButton(AON.MSG.acceptInvoice(), AON.CSS.aonIconAddTask());
			accountEntry.addClickHandler( event -> RawdocModule.RAWDOC_SERVICE.parse(opt.getOccam(), rawdoc.getId() 
				, new AsyncCallback<TediResult>() {

					@Override
					public void onSuccess(TediResult result) {
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
								.setDomainName(opt.getDomainName())
								.setDomain(opt.getDomain())
								.setUser(opt.getUser())
								.setConfiguration(opt.getConfiguration())
								.setAccountingInvoice(result.getAccountingInvoice())
								.setTediResult(result)
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
										entryDialog.hide();
										result.setAon((AccountingInvoice) changed);
										StringBuilder buf = new StringBuilder();
										if (result.getAccountingInvoice() != null 
										 && result.getAccountingInvoice().getAccountEntry() != null) {
											buf.append( result.getAccountingInvoice().getInvoice().getDocumentNumber() )
												.append(" (")
												.append(result.getAccountingInvoice().getAccountEntry().getJournal())
												.append(")");
										} else {
											buf.append("CONTABILIZADO");
										}
										Label label = new Label( buf.toString() );
										label.setStyleName( AON.CSS.aonColorGreen() );
										refreshRow(opt, cbk, rawdoc, label, true);
									}
								}));
						entryDialog.center();
						entryDialog.show();
					}
	
					@Override
					public void onFailure(Throwable caught) {
						AonMessageDialog msg = new AonMessageDialog();
						msg.show("ERROR", "Se ha producido un error al intentar mostrar el documento de la factura.", () -> {});
					}
				}
			));
			accountEntry.getElement().getStyle().setMarginRight(5, Unit.PX);
		}
		return accountEntry;
	}
	
	@Override
	protected Widget getValidationInfo(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		if ( rawdoc.isInbox() || rawdoc.isProcessed()) {
			return new InvoiceMessagesLabel( rawdoc.getInvoice() );
		}
		return new Label();
	}
	
	@Override
	protected boolean isCheckEnabled(Rawdoc rawdoc) {
		if (rawdoc.getInvoice() == null) return false;
		InvoiceErrorLevel level = rawdoc.getInvoice().getMoreSeriousLevel().orElse( null );
		return  (level != InvoiceErrorLevel.WRN && level != InvoiceErrorLevel.ERR);
	}	
}
