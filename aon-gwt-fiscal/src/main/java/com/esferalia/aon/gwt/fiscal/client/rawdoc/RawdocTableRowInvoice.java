package com.esferalia.aon.gwt.fiscal.client.rawdoc;

import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.rawdoc.RawdocModuleNew.RawdocCallback;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import es.translogia.tedi.ewok.TediRegistry;

public class RawdocTableRowInvoice extends AonDisplayGridRow {

	public RawdocTableRowInvoice(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		
		Label nature = new Label(rawdoc.getNature().getDescription());
		Label type = new Label(Optional.ofNullable(rawdoc.getType()).map( t -> t.getDescription() ).orElse(""));
		Label status = new Label(rawdoc.getStatus() == null?"":rawdoc.getStatus().getDescription());

		AonTableButton logButton = null;
		if ( AonStringUtils.isBlank(rawdoc.getLog())) {
			logButton = new AonTableButton(AON.MSG.tracking(), AON.CSS.aonIconHistory());
			logButton.addClickHandler(event -> cbk.showExtraInfo( new RawdocLogPanel( rawdoc.getLog())));
		}
		
		AonTableButton accountEntry = null;
		if (rawdoc.getStatus() == RawdocStatus.INBOX) {
			accountEntry = new AonTableButton(AON.MSG.acceptInvoice(), AON.CSS.aonIconAddTask());
			accountEntry.addClickHandler( event -> RawdocModuleNew.RAWDOC_SERVICE.parse(opt.getOccam(), rawdoc.getId() 
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
										if (result.getAccountingInvoice() != null) {
											if (result.getAccountingInvoice().getAccountEntry() != null) {
												buf.append(AON.MSG.journal());
												buf.append(": ");
												buf.append(result.getAccountingInvoice().getAccountEntry().getJournal());
											}
											if (result.getInvoice()!= null) {
												buf.append(" Doc: ");
												buf.append(result.getInvoice().getDocumentNumber());
											}
										} else {
											buf.append("CONTABILIZADO");
										}
	//									refreshCell(tab,buf.toString());
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
		
		AonTableButton delete = null;
		if (rawdoc.getStatus() == RawdocStatus.INBOX || rawdoc.getStatus() == RawdocStatus.REJECTED) {
			delete = new AonTableButton(AON.MSG.draftDocs(), AON.CSS.aonIconDelete());
			delete.getElement().getStyle().setMarginRight(5, Unit.PX);
			delete.addClickHandler(event -> {
				AonConfirmDialog cd = new AonConfirmDialog();
				cd.confirm(AON.MSG.confirmDraftAction(), () -> 
					RawdocModuleNew.RAWDOC_SERVICE.toDraft(opt.getDomainName(),opt.getDomain(),opt.getUser(), rawdoc.getId()
						, new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
//								refreshCell(tab,"PAPELERA");
							}
							
							@Override
							public void onFailure(Throwable caught) {
								cbk.showError(caught.getMessage());
							}
						}));
			});
		}
		
		AonTableButton reject = null;
		if (rawdoc.getStatus() == RawdocStatus.INBOX) {
			reject = new AonTableButton(AON.MSG.reject(), AON.CSS.aonIconReject());
			reject.getElement().getStyle().setMarginRight(5, Unit.PX);
			reject.addClickHandler(event -> {
				final AonCustomDialog toast = new AonCustomDialog();
				toast.setCaption(AON.MSG.rejectReason());
				FlowPanel reasonPanel = new FlowPanel();
				reasonPanel.setStyleName(AON.CSS.aonTextCenter());
				reasonPanel.addStyleName(AON.CSS.aonPadding());
				TextArea reason = new TextArea();
				reason.setWidth("400px");
				reason.setHeight("100px");
				reason.addKeyUpHandler(event1 -> {
					if (event1.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
						toast.hide();
					}
				});
				
				FlowPanel buttons = new FlowPanel();
				buttons.setStyleName(AON.CSS.aonTextCenter());
				buttons.addStyleName(AON.CSS.aonMarginTop());
				
				final Button okButton = new Button();
				okButton.setStyleName(AON.CSS.aonOkButton());
				okButton.setText( AON.MSG.accept());
				okButton.addKeyUpHandler(event1 -> {
					if (event1.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
						toast.hide();
					}
				});
				okButton.addClickHandler(event1 -> {
					if (AonStringUtils.isBlank( reason.getValue() )) {
						AonMessageDialog msg = new AonMessageDialog();
						msg.show("ERROR", "Debe indicar una raz\u00F3n para proceder a rechazar el documento.", () -> {});
					} else {
						okButton.setEnabled(false);
						toast.hide();
						RawdocModuleNew.RAWDOC_SERVICE.toRejected(opt.getOccam(), rawdoc.getId(), reason.getValue()
							, new AsyncCallback<Void>() {
							
								@Override
								public void onSuccess(Void result) {
	//								refreshCell(tab, "RECHAZADA");
								}
								
								@Override
								public void onFailure(Throwable caught) {
									cbk.showError(caught.getMessage());
								}
							}
						);
					}
				});
				buttons.add(okButton);
				
				final Button cancelButton = new Button();
				cancelButton.setStyleName(AON.CSS.aonCancelButton());
				cancelButton.addStyleName(AON.CSS.aonMarginLeft());
				cancelButton.setText( AON.MSG.cancelAction());
				cancelButton.addClickHandler(event1 -> {
					cancelButton.setEnabled(false);
					toast.hide();
				});
				cancelButton.addKeyUpHandler(event1 -> {
					if (event1.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
						toast.hide();
					}
				});
				buttons.add(cancelButton);
				
				reasonPanel.add(reason);
				reasonPanel.add(buttons);
				toast.add(reasonPanel);
				
				Scheduler.get().scheduleDeferred(() -> reason.setFocus(true));
				toast.center();
				toast.show();
			});
		}
		
		AonTableButton restore = null;
		if (rawdoc.getStatus() == RawdocStatus.DRAFT || rawdoc.getStatus() == RawdocStatus.REJECTED) {
			restore = new AonTableButton(AON.MSG.restoreAction(),
					rawdoc.getStatus() == RawdocStatus.REJECTED
						?AON.CSS.aonIconRestoreRejected()
						:AON.CSS.aonIconRestoreDeleted());
			restore.getElement().getStyle().setMarginRight(5, Unit.PX);
			restore.addClickHandler(event -> {
				AonConfirmDialog cd = new AonConfirmDialog();
				cd.confirm((rawdoc.getStatus() == RawdocStatus.REJECTED
					?AON.MSG.confirmRestoreRejected()
					:AON.MSG.confirmRestoreAction()), () -> 
						RawdocModuleNew.RAWDOC_SERVICE.toInbox(opt.getOccam(), rawdoc.getId()
							, new AsyncCallback<Void>() {
								
									@Override
									public void onSuccess(Void result) {
//										refreshCell(tab,"INBOX");
									}
									
									@Override
									public void onFailure(Throwable caught) {
										cbk.showError(caught.getMessage());
									}
							}
						)
						);
			});
		}

		AonTableButton deleteForever = null;
		if (rawdoc.getStatus() == RawdocStatus.DRAFT) {
			deleteForever = new AonTableButton(AON.MSG.deleteForeverAction(),AON.CSS.aonIconDeleteForever());
			deleteForever.getElement().getStyle().setMarginRight(5, Unit.PX);
			deleteForever.addClickHandler(event -> {
				AonConfirmDialog cd = new AonConfirmDialog();
				cd.confirm(AON.MSG.confirmDeleteForever(), () -> 
					RawdocModuleNew.RAWDOC_SERVICE.delete(opt.getOccam(), rawdoc.getId()
						, new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
//								refreshCell(tab,"ELIMINADO");
							}
							
							@Override
							public void onFailure(Throwable caught) {
								cbk.showError(caught.getMessage());
							}
						}
					)
				);
			});
		}
		
		AonTableButton viewDoc = null;
		if ( rawdoc.getMimeType() != null || rawdoc.getS3Key() != null) {
			viewDoc = new AonTableButton(AON.MSG.attach(), getAttachIcon(rawdoc.getMimeType() != null
					? rawdoc.getMimeType() : MimeType.PDF));
			viewDoc.getElement().getStyle().setMarginRight(5, Unit.PX);
			viewDoc.addClickHandler(event -> {
				if(rawdoc.getS3Key() != null) {
					RawdocModuleNew.RAWDOC_SERVICE.getS3Url(rawdoc, new AsyncCallback<String>() {
						@Override
						public void onSuccess(String url) {
							cbk.showViewer(MimeType.PDF, url);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							cbk.showError(caught.getMessage());
						}
					});
				} else {
					String params = 
						"domain="+ opt.getDomain() 
					  + "&id=" +  rawdoc.getId();
					params = RawdocTableRowInvoice.b64encode(params);
					String url = URL.encode(GWT.getModuleBaseURL() + "ms/download_rawdoc" 
						+ "/" + opt.getDomainName() 
						+ "/" + opt.getUser() 
						+ "/" +  params);
					cbk.showViewer(rawdoc.getMimeType(),url);
				}
			});
		}

		Label invReference = new Label();
		Label invDate = new Label();
		Label regDoc  = new Label();
		Label regName = new Label();
		Label amount =  new Label();
		if (rawdoc.getTediInvoice() != null) {
			invReference.setText(rawdoc.getTediInvoice().getReference());
			Date date = rawdoc.getTediInvoice().getDate();
			invDate.setText(date == null ? "" :AON.DATE_FORMAT.format(rawdoc.getTediInvoice().getDate()));
			TediRegistry registry = rawdoc.getTediInvoice().getRegistry();
			if (registry != null) {
				regDoc  = new Label( registry.getDocument());
				regName = new Label( registry.getName());
			}
			Double total = rawdoc.getTediInvoice().getTotal();
			amount.setText( total == null? "" : AON.FMT.format(rawdoc.getTediInvoice().getTotal()));	
		}
		this
			.addCell(nature, AON.CSS.aonTextCenter() )
			.addCell(type, AON.CSS.aonTextCenter() )
			.addCell(status, AON.CSS.aonTextCenter() )
			.addCell(invReference)
			.addCell(invDate)
			.addCell(regDoc)
			.addCell(regName)
			.addCell(amount, AON.CSS.aonTextRight() )
			.addCell( ensureButton(viewDoc))
			.addCell( ensureButton(logButton))
			.addCell( ensureButton(accountEntry))
			.addCell( ensureButton(delete))
			.addCell( ensureButton(reject))
			.addCell( ensureButton(restore))
			.addCell( ensureButton(deleteForever))
		;
	}

//	private void refreshCell(FlexTable tab, int row,String label) {
//	clearFootInfo();
//	int col = Cols.ADJ.ordinal() - 1; 
//	for (int i = (Cols.values().length - 1) ; i >  col; i-- ) {
//		tab.removeCell(row, i);
//	}
//	tab.getFlexCellFormatter().setColSpan(row, Cols.ADJ.ordinal(), (Cols.values().length - col));
//	tab.setWidget(row, Cols.ADJ.ordinal(), new Label(label));
//	tab.getCellFormatter().setStyleName(row, Cols.ADJ.ordinal(), AON.CSS.aonTextCenter());
//	tab.getCellFormatter().addStyleName(row, Cols.ADJ.ordinal(), AON.CSS.aonBold());
//}
	
	private Widget ensureButton(Widget button) {
		if (button == null) {
			Label widget = new Label();
			widget.setStyleName(AON.CSS.aonIconLabel());
			return widget;	
		}
		return button;
	}
	
	private String getAttachIcon(MimeType mimeType) {
		if (mimeType.isPDF() ) {
			return AON.CSS.aonIconPdf();	
		} else if (mimeType.isImage()) {
			return AON.CSS.aonIconImage();
		} else if (mimeType.isMsExcel()) {
			return AON.CSS.aonIconExcel();
		} else if (mimeType.isMsWord()) {
			return AON.CSS.aonIconWord();
		} 
		return AON.CSS.aonIconUnknown();
	}

	private static native String b64encode(String a) /*-{
	  return window.btoa(a);
	}-*/;	

}
