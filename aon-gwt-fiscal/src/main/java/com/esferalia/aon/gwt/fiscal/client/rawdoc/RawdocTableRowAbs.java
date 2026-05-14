package com.esferalia.aon.gwt.fiscal.client.rawdoc;

import java.util.Optional;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.rawdoc.RawdocModule.RawdocCallback;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

abstract class RawdocTableRowAbs<T> extends AonDisplayGridRow {

	RawdocTableRowAbs(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		paintRow(opt, cbk, rawdoc);
	}
	
	protected abstract Optional<T> getDoc(Rawdoc rawdoc);
	protected abstract Widget getValidationInfo(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc);
	protected abstract boolean isCheckEnabled(Rawdoc rawdoc);
	protected abstract Label getDocumentLabel(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc);
	protected abstract Label getNameLabel(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc);
	protected abstract Label getAmountLabel(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc);
	protected abstract Label getDateLabel(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc);
	protected abstract Label getReferenceLabel(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc);
	protected abstract AonTableButton getActionButton(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc);
	
	private void paintRow(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		paintRow(opt, cbk, rawdoc, getStatusLabel(rawdoc), false);
	}
	
	private void paintRow(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc, Label statusLabel, boolean recorded) {
		AonTableButton checkButton = new AonTableButton(AON.MSG.selectAction(), cbk.getSelectedItems().contains(rawdoc.getId())?AON.CSS.aonIconChecked():AON.CSS.aonIconCheck());
		checkButton.addClickHandler(event -> {
			if (cbk.getSelectedItems().contains(rawdoc.getId())) {
				rawdoc.setSelected(false);
				checkButton.addStyleName(AON.CSS.aonIconCheck());
				checkButton.removeStyleName(AON.CSS.aonIconChecked());
			} else {
				rawdoc.setSelected(true);
				checkButton.addStyleName(AON.CSS.aonIconChecked());
				checkButton.removeStyleName(AON.CSS.aonIconCheck());
			}
			cbk.manageSelection( rawdoc );
			event.stopPropagation();
		});
		this
			.addCellIf(isCheckEnabled( rawdoc ), checkButton)
			.addCellIf(!isCheckEnabled( rawdoc ), new Label())
			
			.addCell(getValidationInfo(opt, cbk, rawdoc), AON.CSS.aonTextCenter() )
			.addCell(getNatureLabel(rawdoc), AON.CSS.aonTextCenter() )
			.addCell(getTypeLabel(rawdoc), AON.CSS.aonTextCenter() )
			.addCell(statusLabel, AON.CSS.aonTextCenter() )
			.addCell(ensureButton(getViewDocButton(opt, cbk, rawdoc)))
			.addCell(recorded?new Label() : ensureButton(getActionButton( opt, cbk, rawdoc)))
			.addCell(getReferenceLabel(opt, cbk, rawdoc))
			.addCell(getDateLabel(opt, cbk, rawdoc))
			.addCell(getDocumentLabel(opt, cbk, rawdoc))
			.addCell(getNameLabel(opt, cbk, rawdoc))
			.addCell(getAmountLabel(opt, cbk, rawdoc), AON.CSS.aonTextRight() )
			.addCell(recorded?new Label() : ensureButton(getDeleteButton(opt, cbk, rawdoc)))
			.addCell(recorded?new Label() : ensureButton(getRejectButton(opt, cbk, rawdoc)))
			.addCell(recorded?new Label() : ensureButton(getRestoreButton(opt, cbk, rawdoc)))
			.addCell(recorded?new Label() : ensureButton(getDeleteForeverButton(opt, cbk, rawdoc)))
			.addCell(recorded?new Label() : ensureButton(getLogButton( opt, cbk, rawdoc)))
		;
	}
	
	protected void refreshRow(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc, Label label, boolean recorded) {
		this.clear();
		paintRow(opt, cbk, rawdoc, label, recorded);
	}

	private Label getNatureLabel(Rawdoc rawdoc) {
		return  new Label(rawdoc.getNature().getDescription());
	}
	
	private Label getTypeLabel(Rawdoc rawdoc) {
		return new Label(Optional.ofNullable(rawdoc.getType()).map( t -> t.getDescription() ).orElse(""));
	}
	
	private Label getStatusLabel(Rawdoc rawdoc) {
		return new Label(Optional.ofNullable(rawdoc.getStatus()).map( t -> t.getDescription() ).orElse(""));
	}


	private AonTableButton getViewDocButton(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		AonTableButton viewDoc = null;
		if ( rawdoc.getMimeType() != null || rawdoc.getS3Key() != null) {
			viewDoc = new AonTableButton(AON.MSG.attach(), getAttachIcon(rawdoc.getMimeType() != null
					? rawdoc.getMimeType() : MimeType.PDF));
			viewDoc.getElement().getStyle().setMarginRight(5, Unit.PX);
			viewDoc.addClickHandler(event -> {
				if(rawdoc.getS3Key() != null) {
					RawdocModule.RAWDOC_SERVICE.getS3Url(rawdoc, new AsyncCallback<String>() {
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
					params = RawdocTableRowAbs.b64encode(params);
					String url = URL.encode(GWT.getModuleBaseURL() + "ms/download_rawdoc" 
						+ "/" + opt.getDomainName() 
						+ "/" + opt.getUser() 
						+ "/" +  params);
					cbk.showViewer(rawdoc.getMimeType(),url);
				}
			});
		}
		return viewDoc;
	}

	private AonTableButton getDeleteForeverButton(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		AonTableButton deleteForever = null;
		if (rawdoc.isTrash()) {
			deleteForever = new AonTableButton(AON.MSG.deleteForeverAction(),AON.CSS.aonIconDeleteForever());
			deleteForever.getElement().getStyle().setMarginRight(5, Unit.PX);
			deleteForever.addClickHandler(event -> {
				AonConfirmDialog cd = new AonConfirmDialog();
				cd.confirm(AON.MSG.confirmDeleteForever(), () -> 
					RawdocModule.RAWDOC_SERVICE.delete(opt.getOccam(), rawdoc.getId()
						, new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								Label label = new Label( "ELIMINADO" );
								label.setStyleName( AON.CSS.aonColorRed() );
								refreshRow(opt, cbk, rawdoc, label, false);
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
		return deleteForever;
	}

	private AonTableButton getRestoreButton(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		AonTableButton restore = null;
		if (rawdoc.isTrash() || rawdoc.isRejected()) {
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
						RawdocModule.RAWDOC_SERVICE.toInbox(opt.getOccam(), rawdoc.getId()
							, new AsyncCallback<Rawdoc>() {
								
									@Override
									public void onSuccess(Rawdoc result) {
										Label label = new Label( "INBOX" );
										label.setStyleName( AON.CSS.aonColorRed() );
										refreshRow(opt, cbk, result, label, false);
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
		return restore;
	}

	private AonTableButton getRejectButton(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		AonTableButton reject = null;
		if (rawdoc.isInbox() || rawdoc.isProcessed()) {
			reject = new AonTableButton(AON.MSG.reject(), AON.CSS.aonIconReject());
			reject.getElement().getStyle().setMarginRight(5, Unit.PX);
			reject.addClickHandler(event -> {
				RawdocRejectPanel rrp = new RawdocRejectPanel(opt,rawdoc, (reason, email) -> 
					RawdocModule.RAWDOC_SERVICE.toRejected(opt.getOccam(), rawdoc.getId(), reason, email
						, new AsyncCallback<Rawdoc>() {
							@Override
							public void onSuccess(Rawdoc result) {
								Label label = new Label( "RECHAZADA" );
								label.setStyleName( AON.CSS.aonColorRed() );
								refreshRow(opt, cbk, result, label, false);
							}
						
							@Override
							public void onFailure(Throwable caught) {
								cbk.showError(caught.getMessage());
							}
						}
					)
				);
				Scheduler.get().scheduleDeferred(() -> rrp.setFocus(true));
				rrp.center();
				rrp.show();
			});
		}
		return reject; 
	}

	private AonTableButton getDeleteButton(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		AonTableButton delete = null;
		if (rawdoc.isInbox() || rawdoc.isRejected() || rawdoc.isProcessed()) {
			delete = new AonTableButton(AON.MSG.draftDocs(), AON.CSS.aonIconDelete());
			delete.getElement().getStyle().setMarginRight(5, Unit.PX);
			delete.addClickHandler(event -> {
				AonConfirmDialog cd = new AonConfirmDialog();
				cd.confirm(AON.MSG.confirmDraftAction(), () -> 
					RawdocModule.RAWDOC_SERVICE.toTrash(opt.getOccam(), rawdoc.getId()
						, new AsyncCallback<Rawdoc>() {
							
							@Override
							public void onSuccess(Rawdoc result) {
								Label label = new Label( "PAPELERA" );
								label.setStyleName( AON.CSS.aonColorRed() );
								refreshRow(opt, cbk, result, label, false);
							}
							
							@Override
							public void onFailure(Throwable caught) {
								cbk.showError(caught.getMessage());
							}
					})
				);
			});
		}
		return delete;
	}

	private AonTableButton getLogButton(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		AonTableButton logButton = null;
		if (hasLog(rawdoc.getLog())) {
			logButton = new AonTableButton(AON.MSG.tracking(), AON.CSS.aonIconHistory());
			if (hasRejectedAction(rawdoc.getLog())) {
				logButton.removeStyleName(AON.CSS.aonIconHistory());
				logButton.addStyleName(AON.CSS.aonIconHistoryRed());
				logButton.addStyleName(AON.CSS.aonBlink());
			}
			logButton.addClickHandler(event -> cbk.showExtraInfo( new RawdocLogPanel( rawdoc.getLog())));
		}
		return logButton;
	}

	private boolean hasLog(String log) {
		return AonStringUtils.isNotBlank(log)
			&& JsonUtils.safeEval(log) != null
			&& (new JSONArray(JsonUtils.safeEval(log)).size() > 0)
			;
	}
	private boolean hasRejectedAction(String log) {
		JSONArray data = new JSONArray(JsonUtils.safeEval(log));
		for (int i = 0; i < data.size(); i++) {
			JSONValue l = data.get(i);
			JSONObject json = l.isObject();
			if (json != null) {
				JSONValue v = json.get(IJsonNames.STATUS);
				String val = (v == null) ? "" : v.isString().stringValue();
				RawdocStatus rs = RawdocStatus.safeValueOf( val );
				if (rs == RawdocStatus.REJECTED || rs == RawdocStatus.DRAFT) {
					return true;
				}
				if (AonStringUtils.equalsIgnoreCase(RawdocStatus.REJECTED.name(), val)) {
					return true;
				}
			}
		}
		return false;
	}
	
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
