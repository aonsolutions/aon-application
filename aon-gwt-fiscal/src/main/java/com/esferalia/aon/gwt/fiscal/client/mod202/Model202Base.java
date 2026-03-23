package com.esferalia.aon.gwt.fiscal.client.mod202;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.Objects;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAuditDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToastModel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelEmailUtils;
import com.esferalia.aon.gwt.fiscal.client.accounting.js.JsAccountingBreakdown;
import com.esferalia.aon.gwt.fiscal.client.accounting.js.JsAccountingBreakdownGridPanel;
import com.esferalia.aon.gwt.fiscal.client.invoice.irpf.JsIRPFComputeInfo;
import com.esferalia.aon.gwt.fiscal.client.invoice.irpf.JsIRPFComputeInfoGridPanel;
import com.esferalia.aon.gwt.fiscal.client.invoice.vat.JsVatComputeKeyInfo;
import com.esferalia.aon.gwt.fiscal.client.invoice.vat.JsVatComputeKeyInfoGridPanel;
import com.esferalia.aon.gwt.fiscal.client.mod202.Model202.Model202Callback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFinishDeclarationPopup;
import com.esferalia.aon.gwt.fiscal.client.model.AonFinishDeclarationPopup.IFinishDeclarationPopupCallback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelIdentificationPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelKeyInfoVisitor;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.mod202.Model202ScriptProvider;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;

public abstract class Model202Base extends DockLayoutPanel {
	
	protected FiscalModelAdmonPanel<Mod202, Model202ModuleOptions> admonPanel;

	private static final String WIDTH_140PX = "140px";
	private static final String BLANK = "_blank";
	private static final int MAX_LABEL_LENGTH = 180;
	private static final int COL_NUMBER = 8;
	private static final String MODEL202_PRINT = "/aon_gwt_fiscal/ms/Model202Print";
	protected static final String MODEL202_FILE = "/aon_gwt_fiscal/ms/Model202File";

	private EnumMap<Mod202Key,AonDoubleBox> fieldsMap;
	private boolean dirty;	
	
	private final AonToolbar toolbarPanel = new AonToolbar(); 
	private final AonToolbarButton newButton = new AonToolbarButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
	private final AonToolbarButton saveButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave());
	private final AonToolbarButton cancelButton = new AonToolbarButton(AON.MSG.cancelAction(),AON.CSS.aonIconBack());
	private final AonToolbarButton deleteButton = new AonToolbarButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
	private final AonToolbarButton printButton = new AonToolbarButton(AON.MSG.draft(),AON.CSS.aonIconExcel());
	private final AonToolbarButton markAsPendingButton = new AonToolbarButton(AON.MSG.reopen(),AON.CSS.aonIconModelReopen());
	private final AonToolbarButton markAsFinishedButton = new AonToolbarButton(AON.MSG.finish(),AON.CSS.aonIconModelFinish());
	private final AonToolbarButton markAsSentButton = new AonToolbarButton(AON.MSG.markAsSent(),AON.CSS.aonIconModelSent());
	private final AonToolbarButton commentsButton = new AonToolbarButton(AON.MSG.comments(), AON.CSS.aonIconNoComments());
	private final AonToolbarButton auditButton = new AonToolbarButton(AON.MSG.audit(),AON.CSS.aonIconAudit());
	
	private AonTextBox receiptBox;
	private FlowPanel paymentContainer;
	
	private final AonToolbar decToolbar = new AonToolbar();
	private final InlineLabel dirtyLabel = new InlineLabel();
	private final InlineLabel adjLabel = new InlineLabel();
	private final InlineLabel replacedLabel = new InlineLabel();
	private final Label statusLabel = new Label();
	
	protected FormPanel diskForm = new FormPanel(BLANK);
	protected Hidden mod202Hidden = new Hidden("mod202");
	protected Hidden domainIdHidden = new Hidden("domainId");
	protected Hidden domainNameHidden = new Hidden("domainName");
	protected Hidden userHidden = new Hidden("user");

	private AonToastModel toast = null;
	
	protected Model202Base(Model202Callback callback) {
		super(Unit.PX);
		setStyleName(AON.CSS.aonSelector());
		fieldsMap = new EnumMap<>(Mod202Key.class);
		select( callback );
		
		AonFiscalModelHeader modelHeader = new AonFiscalModelHeader( callback.getModel() );
		addNorth(modelHeader, AonFiscalModelHeader.HEIGTH);
		addNorth(getToolbarPanel(callback), AonToolbar.HEIGTH);
		addNorth(getDeclarationToolbarPanel(callback), AonToolbar.HEIGTH);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		showPaymentInfo(callback.getModel());
		
		paintIdentificationTab(callback, tabPanel);
		paintDeclarationTab(callback, tabPanel);
		paintDeclarationInfoTab(callback, tabPanel);
		paintLiquidationTab(callback, tabPanel);
		paintDeclarationAditionalInfoTab(callback, tabPanel);
		paintAdministrationTab(callback, tabPanel);
	}

	protected EnumMap<Mod202Key, AonDoubleBox> getFieldsMap() {
		return fieldsMap;
	}
	
	private void select( Model202Callback callback, Mod202 mod202) {
		callback.setModel(mod202);
		select( callback );
	}
	
	private void select( Model202Callback callback ) {
		refreshToolbarState(callback);
		styleStatusLabel(callback.getModel());
	}
	
	protected void selectAndPopulate( Model202Callback callback, Mod202 mod202) {
		select(callback, mod202);
		populate(callback);
		decorateDeclarationTab(callback);
		decorateAdministrationTab();
	}
	
	private void populate(Model202Callback callback) {
		for (Entry<Mod202Key, AonDoubleBox> entry : fieldsMap.entrySet()) {
			double d1 = callback.getModel().getAmount(entry.getKey());
			double d2 = entry.getValue().getValue();
			if (!AonNumberUtils.equals(d1, d2)) {
				entry.getValue().setValue(d1,false,true);
			}
		}
	}
	
	private AonToolbar getToolbarPanel(Model202Callback callback) {
		if (callback.getOptions().isBackButtonVisible() && callback.getOptions().hasExternalCallback()) {
			cancelButton.setText(AON.MSG.backAction());
			cancelButton.setTitle(AON.MSG.backAction());
		}
		cancelButton.addClickHandler(event ->  cancel(callback) );
		toolbarPanel.add(cancelButton);

		newButton.addClickHandler( event ->  callback.onNew() );
		toolbarPanel.add(newButton);

		saveButton.addClickHandler(event ->  save(callback));
		toolbarPanel.add(saveButton);
		
		deleteButton.addClickHandler(event -> delete(callback));
		toolbarPanel.add(deleteButton);
		
		printButton.addClickHandler( event ->  print(callback));
		toolbarPanel.add(printButton);
		
		commentsButton.addClickHandler( event -> {
			if (toast == null || toast.getParent() == null) {
				toast  = new AonToastModel(this);
				FlowPanel commentPanel = new FlowPanel();
				commentPanel.setStyleName( FiscalModelUtils.getAdministrationBackgroundStyle(callback.getModel().getAdministration()) );
				commentPanel.addStyleName(AON.CSS.aonHeightAll());
				commentPanel.addStyleName(AON.CSS.aonTextCenter());
				TextArea comment = new TextArea();
				comment.addValueChangeHandler(event1 -> {
					callback.getModel().setComments(event1.getValue());
					styleCommentsButton(callback);
					Model202.SERVICE.saveComments( callback.getOptions().getOccam(), callback.getModel(), new AsyncCallback<Mod202>() {
						@Override
						public void onSuccess(Mod202 result) {
							toast.hide();
						}
	
						@Override
						public void onFailure(Throwable caught) {
							toast.hide();
							callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						}
					});
				});
				comment.setText(callback.getModel().getComments());
				comment.setWidth("90%");
				comment.setHeight("5em");
				commentPanel.add(comment);
				toast.show(AON.MSG.comments(), commentPanel);
			}
		});
		toolbarPanel.add( commentsButton );
		styleCommentsButton(callback);

		auditButton.addClickHandler( event -> audit(callback));
		toolbarPanel.add(auditButton);
		
		toolbarPanel.add(diskForm);

		return toolbarPanel;
	}
	
	private AonToolbar getDeclarationToolbarPanel(Model202Callback callback) {
		
		markAsFinishedButton.setText(markAsFinishedButton.getTitle());
		markAsFinishedButton.addClickHandler( event -> markAsFinished(callback));
		decToolbar.add(markAsFinishedButton);

		markAsSentButton.setText(markAsSentButton.getTitle());
		markAsSentButton.addClickHandler( event -> markAsSent(callback));
		decToolbar.add(markAsSentButton);

		markAsPendingButton.setText(markAsPendingButton.getTitle());
		markAsPendingButton.addClickHandler( event -> reopenDeclaration(callback));
		decToolbar.add(markAsPendingButton);

		FlowPanel marksPanels = new FlowPanel();
		marksPanels.setStyleName(AON.CSS.aonFlexBlock());
		
		dirtyLabel.setStyleName(AON.CSS.aonIconLabel());
		dirtyLabel.addStyleName(AON.CSS.aonIconDirty());
		dirtyLabel.setTitle("Cambios sin guardar");
		marksPanels.add(dirtyLabel);
		
		adjLabel.setStyleName(AON.CSS.aonMarginLeft());
		adjLabel.addStyleName(AON.CSS.aonIconLabel());
		adjLabel.addStyleName(AON.CSS.aonIconWrench());
		adjLabel.addStyleName(AON.CSS.aonColorBlue());
		adjLabel.setTitle("Ajustes realizados");
		marksPanels.add(adjLabel);

		if (callback.getModel().isReplacement()) {
			replacedLabel.setText(AON.MSG.replacement());
			replacedLabel.setStyleName(AON.CSS.aonMarginLeft());
			replacedLabel.addStyleName(AON.CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.CSS.aonLabelWithIcon());
		}
		if (callback.getModel().isComplementary()) {
			replacedLabel.setText( AON.MSG.complementary());
			replacedLabel.setStyleName(AON.CSS.aonMarginLeft());
			replacedLabel.addStyleName(AON.CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.CSS.aonLabelWithIcon());
		}
		marksPanels.add(replacedLabel);

		styleDirtyLabel(callback);
		styleStatusLabel(callback.getModel());
		
		decToolbar.getMessagePanel().add(marksPanels);
		
		decToolbar.setTitle(statusLabel);
		return decToolbar;
	}
	
	protected void markAsDirty(Model202Callback callback) {
		setDirty(callback, true);
	}
	private boolean isDirty() {
		return this.dirty;
	}
	private void setDirty(Model202Callback callback, boolean dirty) {
		this.dirty = dirty;
		styleDirtyLabel(callback);
	}

	private void styleDirtyLabel(Model202Callback callback) {
		dirtyLabel.setVisible(isDirty());
		boolean adjusted = false;
		for (FiscalModelDetail det : callback.getModel().getMap().values()) {
			if (AonMathUtils.isNotZero( det.getAdjustAmount())) {
				adjusted = true;
				break;
			}
		}
		adjLabel.setVisible(adjusted);
	}

	private void refreshToolbarState(Model202Callback callback) {
		toolbarPanel.setTitle(AonStringUtils.join(callback.getModel().getDocument(),AonStringUtils.SPACE,callback.getModel().getFullName()));
		auditButton.setVisible(!callback.getModel().isNew());
		newButton.setVisible(!callback.getModel().isNew() && !callback.getOptions().isBackButtonVisible() && !callback.getOptions().hasExternalCallback());
		cancelButton.setVisible(true);
		saveButton.setVisible(!callback.getModel().isFinished() && !callback.getModel().isSent());
		deleteButton.setVisible(!callback.getModel().isNew() && !callback.getModel().isFinished() && !callback.getModel().isSent());
		printButton.setVisible(!callback.getModel().isNew());
		markAsPendingButton.setVisible(!callback.getModel().isNew() && FiscalModelUtils.canChangeStatus(callback.getModel(), FiscalStatus.PENDING));
		markAsFinishedButton.setVisible(!callback.getModel().isNew() && FiscalModelUtils.canChangeStatus(callback.getModel(), FiscalStatus.FINISHED));
		markAsSentButton.setVisible(!callback.getModel().isNew() && FiscalModelUtils.canChangeStatus(callback.getModel(), FiscalStatus.SENT));
	}
	
	private void styleStatusLabel(Mod202 mod202) {
		statusLabel.setText(mod202.getStatus().getName());
		statusLabel.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB( mod202.getStatus() ));
		statusLabel.getElement().getStyle().setColor(FiscalModelUtils.getStatusFrgColorRGB( mod202.getStatus() ));
		statusLabel.setStyleName(AON.CSS.aonToolbarTitle());
		statusLabel.addStyleName(AON.CSS.aonPaddingLeft());
		statusLabel.addStyleName(AON.CSS.aonPaddingRight());
		statusLabel.addStyleName(AON.CSS.aonTextCenter());
		statusLabel.addStyleName(AON.CSS.aonBorder());
		statusLabel.addStyleName(AON.CSS.aonNowrap());
	}
	
	private void styleCommentsButton(Model202Callback callback) {
		if (AonStringUtils.isEmpty(callback.getModel().getComments())) {
			commentsButton.addStyleName(AON.CSS.aonIconNoComments());
			commentsButton.removeStyleName(AON.CSS.aonIconComments());
		} else {
			commentsButton.addStyleName(AON.CSS.aonIconComments());
			commentsButton.removeStyleName(AON.CSS.aonIconNoComments());
		}
		commentsButton.setTitle(callback.getModel().getComments());
	}
		
	private void cancel( Model202Callback callback ) {
		cancelButton.setEnabled(false);
		if (isDirty()) {
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.confirmDeclarationCancelAction(), new AonConfirmDialogCallback() {

				@Override
				public void onAccept() {
					if (callback.getOptions().isBackButtonVisible() && callback.getOptions().hasExternalCallback()) {
						callback.getOptions().getExternalCallback().onExit(callback.getModel());
					} else {						
						callback.onCancel(callback.getModel());
					}
				}
				@Override
				public void onCancel() {
					cancelButton.setEnabled(true);
				}
			});
		} else {
			if (callback.getOptions().isBackButtonVisible() && callback.getOptions().hasExternalCallback()) {
				callback.getOptions().getExternalCallback().onExit(callback.getModel());
			} else {
				callback.onCancel(callback.getModel());
			}
		}
	}
	
	private void save(Model202Callback callback) {
		save(callback, null);
	}
	private void save(Model202Callback callback, AsyncCallback<Mod202> cbk) {
		saveButton.setEnabled(false);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add(new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model202.SERVICE.save(callback.getOptions().getOccam(), callback.getModel(), new AsyncCallback<Mod202>() {
			@Override
			public void onSuccess(Mod202 result) {
				setDirty( callback, false );
				select(callback, result);
				popup.hide();
				saveButton.setEnabled(true);
				if (cbk != null) cbk.onSuccess(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
				callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
				saveButton.setEnabled(true);
			}
		});
	}
	
	private void delete(Model202Callback callback) {
		deleteButton.setEnabled(false);
		AonConfirmDialog cd = new AonConfirmDialog();
		cd.confirm(AON.MSG.confirmDeclarationDeleteAction(), new AonConfirmDialogCallback() {

			@Override
			public void onAccept() {
				Model202.SERVICE.delete(callback.getOptions().getOccam(),callback.getModel(), new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						deleteButton.setEnabled(true);
						callback.onRemove(callback.getModel());
					}

					@Override
					public void onFailure(Throwable caught) {
						deleteButton.setEnabled(true);
						callback.showError(AON.MSG.unableToDeleteDeclaration(caught.getMessage()));
					}
				});
			}

			@Override
			public void onCancel() {
				deleteButton.setEnabled(true);
			}
		});
	}
	
	private void print(Model202Callback callback) {
		if (isDirty()) {
			new AonConfirmDialog().confirm(AON.MSG.draftPrint(),AON.MSG.draftPrintNote(), new AonConfirmDialogCallback() {
				@Override
				public void onAccept() {
					submitForm(callback, MODEL202_PRINT);
				}
				@Override
				public void onCancel() {
					// Nothing
				}
			});
		} else {
			submitForm(callback, MODEL202_PRINT);
		}
	}

	private void audit(Model202Callback callback) {
		AonAuditDialog dialog = new AonAuditDialog();
		dialog.show(callback.getModel());
	}
	
	private void reopenDeclaration(Model202Callback callback) {
		markAsPendingButton.setEnabled(false);
		if (callback.getModel().isSent() && AonStringUtils.isNotBlank(callback.getModel().getNumber())) {
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.confirmReopenDeclarationAction(), new AonConfirmDialogCallback() {
				
				@Override
				public void onAccept() {
					doReopenDeclaration(callback);
				}
				
				@Override
				public void onCancel() {
					markAsPendingButton.setEnabled(true);
				}
			});
		} else {
			doReopenDeclaration(callback);
		}
	}
	
	private void doReopenDeclaration(Model202Callback callback) {
		markAsPendingButton.setEnabled(false);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model202.SERVICE.markAsPending(callback.getOptions().getOccam(), callback.getModel(), new AsyncCallback<Mod202>() {
					@Override
					public void onSuccess(Mod202 result) {
						setDirty(callback, false);
						selectAndPopulate(callback, result);
						popup.hide();
						markAsPendingButton.setEnabled(true);
						showPaymentInfo(callback.getModel());
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						callback.showError(AON.MSG.unableToReopenDeclaration(caught.getMessage()));
						markAsPendingButton.setEnabled(true);
					}
				});
	}

	private void markAsSent(Model202Callback callback) {
		markAsSentButton.setEnabled(false);
		Model202.SERVICE.markAsSent(callback.getOptions().getOccam(), callback.getModel(), new AsyncCallback<Mod202>() {
					@Override
					public void onSuccess(Mod202 result) {
						setDirty(callback, false);
						selectAndPopulate(callback, result);
						markAsSentButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.showError(AON.MSG.unableToMarkAsSentDeclaration(caught.getMessage()));
						markAsSentButton.setEnabled(true);
					}
				});
	}

	private void markAsFinished(Model202Callback callback) {
		markAsFinishedButton.setEnabled(false);
		Model202.SERVICE.initializeForFinish(callback.getOptions().getOccam(),callback.getModel(),
				new AsyncCallback<Mod202>() {
					@Override
					public void onSuccess(Mod202 m202) {
						selectAndPopulate(callback, m202);
						showFinalizePopup(callback);
						markAsFinishedButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.showError(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
						markAsFinishedButton.setEnabled(true);
					}
				});
	}
	
	private void showFinalizePopup(Model202Callback callback) {
		AonFinishDeclarationPopup<Mod202,Model202ModuleOptions> finalizeDialog = new AonFinishDeclarationPopup<>(
				callback.getModel(),
				callback, 
				new IFinishDeclarationPopupCallback<Mod202>() {

					@Override
					public void onAccept(Mod202 t) {
						Model202Base.this.doMarkAsFinished(callback);
					}

					@Override
					public void onCancel(Mod202 t) {
						// Nothing
						
					}

					@Override
					public void onCustomerCheck(Mod202 t) {
						Model202Base.this.markAsCustomerCheck(callback);
					}
			});
			finalizeDialog.center();
			finalizeDialog.show();
	}
	
	private void doMarkAsFinished(Model202Callback callback) {
		markAsFinishedButton.setEnabled(false);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model202.SERVICE.markAsFinished(callback.getOptions().getOccam(), callback.getModel(), new AsyncCallback<Mod202>() {
			@Override
			public void onSuccess(Mod202 result) {
				setDirty(callback, false);
				selectAndPopulate(callback, result);
				popup.hide();
				markAsFinishedButton.setEnabled(true);
				showPaymentInfo(callback.getModel());
			}

			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
				callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
				markAsFinishedButton.setEnabled(true);
			}
		});
	}
	
	private void markAsCustomerCheck(Model202Callback callback) {
		markAsFinishedButton.setEnabled(false);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model202.SERVICE.markAsCustomerCheck(callback.getOptions().getOccam(), callback.getModel(), new AsyncCallback<Mod202>() {
			@Override
			public void onSuccess(Mod202 result) {
				setDirty(callback, false);
				select(callback, result);
				popup.hide();
				markAsFinishedButton.setEnabled(true);
				showPaymentInfo(callback.getModel());
				FiscalModelEmailUtils.sendEmail(callback, result); // Si ha ido bien el cambio de estado, entonces se envía email de notificación
			}

			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
				callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
				markAsFinishedButton.setEnabled(true);
			}
		});
	}

	private void showPaymentInfo(Mod202 mod) {
		if (paymentContainer != null) {
			this.remove(paymentContainer);
			this.forceLayout();
		}
		if (mod.isFinished() || mod.isSent() || mod.isCustomerCheck()) {
			paymentContainer = new FlowPanel();
			paymentContainer.setStyleName(AON.CSS.aonWidthAll());
			Label label = new Label( FiscalModelUtils.getPaymentInfo(mod) );
			label.setStyleName(AON.CSS.aonWidthAll());
			label.addStyleName(AON.CSS.aonTextCenter());
			label.addStyleName(AON.CSS.aonBold());
			paymentContainer.add(label);
			this.insert(paymentContainer, Direction.NORTH, 30, decToolbar);
			this.forceLayout();
		}
	}
	
	protected void paintDeclarationInfoTab(Model202Callback callback, TabLayoutPanel tabPanel) {
		
	}

	protected void paintDeclarationAditionalInfoTab(Model202Callback callback, TabLayoutPanel tabPanel) {
		
	}

	protected void paintLiquidationTab(Model202Callback callback, TabLayoutPanel tabPanel) {
		ScrollPanel liquidationScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		FlexTable table = new FlexTable();
		container.add(table);
		defineTable(table);
		for (IModelScript<Mod202Key> ms : Model202ScriptProvider.obtainScript(callback.getModel())) {
			paintRow(table,callback,ms);	
		}

		liquidationScrollPanel.setWidget(container);
		tabPanel.add(liquidationScrollPanel, AON.MSG.liquidacion());
	}
	
	protected void defineTable(FlexTable table) {
		table.setWidth("80%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		table.addStyleName(AON.CSS.aonBlockCenter());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );
		
		table.getColumnFormatter().setWidth(1, "40px");
		table.getColumnFormatter().setStyleName(1, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(2, WIDTH_140PX);
		
		table.getColumnFormatter().setWidth(3, "40px");
		table.getColumnFormatter().setStyleName(3, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(4, WIDTH_140PX);
		
		table.getColumnFormatter().setWidth(5, "40px");
		table.getColumnFormatter().setStyleName(5, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(6, WIDTH_140PX);
		
		table.getColumnFormatter().setWidth(7, "50px");
	}

	protected void paintEmptyRow(FlexTable table) {
		int row = table.getRowCount();
		table.setWidget(row, 0, new Label());
		table.getFlexCellFormatter().setColSpan(row, 0, COL_NUMBER);
	}

	protected void paintRow(FlexTable table, final Model202Callback callback, IModelScript<Mod202Key> script) {
		if (script.hasGraphicParticularity()) {
			paintParticularyRow(table,callback,script);		
		} else {
			int row = table.getRowCount();
			paintLabel(table,row,script);
			if (script.getKeys() == null) {				
				table.getFlexCellFormatter().setColSpan(row, 0, COL_NUMBER);
			} else {
				int c = 1;
				if (script.getKeys().length==1) c = 5;
				if (script.getKeys().length==2) c = 3;
				table.getFlexCellFormatter().setColSpan(row, 0, c );
				int col = 1;
				for (Mod202Key key : script.getKeys()) {
					col = paintBox( table,row, col, key );
					col = paintField( table,row, col, callback, script, key );
				}
				paintInfoCol(table,row,col,callback,script);	
			}
		}
	}
	
	protected void paintParticularyRow(FlexTable table, final Model202Callback callback, IModelScript<Mod202Key> script) {
		
	}
	
	protected void paintLabel(FlexTable table, int row, IModelScript<Mod202Key> script) {
		String labelText = script.getLabel();
		Label label = new Label();
		if (AonStringUtils.length(labelText) > MAX_LABEL_LENGTH) {
			label.setTitle(labelText);	
			labelText = AonStringUtils.abbreviate(labelText, MAX_LABEL_LENGTH);
		}
		label.setText(labelText);
		table.setWidget(row, 0, label);
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		if (script.isTitle() ) {
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBold() );
		} else {
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		}
	}

	private int paintBox(FlexTable table,int row, int col, Mod202Key key) {
		if (key != null && key.getBox() != 0) {
			table.setWidget(row, col, new AonBoxLabel(key.getBox()));
		}
		return ++col;
	}

	private int paintField(FlexTable table, int row, int col, final Model202Callback callback, IModelScript<Mod202Key> script, final Mod202Key key) {
		if (key != null) {
			final FiscalModelDetail det1 = callback.getModel().ensureDetail(key);
			final AonDoubleBox input = new AonDoubleBox();
			fieldsMap.put(key, input);
			input.setEnabled(script.isEnabled()); 
			input.setValue(det1.getAmount());
			input.addValueChangeHandler(event -> {
				if (event.getValue() == null) input.setValue(0.0, false);
				double result = callback.getModel().getResultAmount(key);
				double adjust = callback.getModel().getAdjustAmount(key);
				double amount = input.getValue();
				if (AonMathUtils.isNotZero(result - adjust - amount)) {
					callback.getModel().ensureDetail(key).setAdjustAmount( result - amount);	
				}
				callback.getModel().ensureDetail(key).setAmount(input.getValue());
				if (input.isEnabled()) {
					calculateAndRefresh( callback );
				}
				markAsDirty(callback);
			});
			table.setWidget(row, col, input);
		}
		return ++col;
	}
	
	private void paintInfoCol(FlexTable table, int row, int col, final Model202Callback callback, final IModelScript<Mod202Key> script) {
		FlowPanel buttonContainer = new FlowPanel();
		for (final FiscalModelKeyInfo infoKey : script.getInfoKeys()) {
			buttonContainer.setStyleName(AON.CSS.aonNowrap());
			infoKey.visit(new IFiscalModelKeyInfoVisitor<Void>() {

				private void showComputeKeyInfo(AonTableButton button) {
					button.setEnabled(false);
					final PopupPanel popup = new PopupPanel(false, true);
					popup.add(new AonSplash());
					popup.setGlassEnabled(true);
					popup.setAnimationEnabled(true);
					popup.center();
					Model202.SERVICE.getInfo(callback.getOptions().getOccam(),callback.getModel(),script, infoKey, new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							popup.hide();
							button.setEnabled(true);
							callback.showError(AON.MSG.errorMessage());
						}
	
						@Override
						public void onSuccess(String result) {
							popup.hide();
							FlowPanel gridContainer = new FlowPanel();
							JsVatComputeKeyInfoGridPanel grid = new JsVatComputeKeyInfoGridPanel();
							grid.setTitle(AON.MSG.calcDetail());
							if (Arrays.stream(script.getKeys()).filter( Objects::nonNull ).count() == 1) {
								grid.setSubTitle(AonStringUtils.join(
									Arrays.stream(script.getKeys())
										.filter( Objects::nonNull )
										.map( Mod202Key::getBoxFormatted )
										.reduce("", String::concat)
									, " " 
									, script.getLabel()));
							}
							try {
								JsVatComputeKeyInfo info = JsonUtils.safeEval(result);
								grid.addContent(info);
							} catch (Exception e) {
								grid.addContent(result);
							}
							gridContainer.add(grid);
							callback.showInfoPanelWidget(gridContainer);
							button.setEnabled(true);
						}
					});
				}

				private void showComputeInfo(AonTableButton button) {
					button.setEnabled(false);
					Model202.SERVICE.getInfo(callback.getOptions().getOccam(),callback.getModel(),script, infoKey, new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							callback.showError(AON.MSG.errorMessage());
							button.setEnabled(true);
						}
	
						@Override
						public void onSuccess(String result) {
							FlowPanel gridContainer = new FlowPanel();
							JavaScriptObject arrayObject = JsonUtils.safeEval(result);
							JsArray<JsIRPFComputeInfo> array = arrayObject.cast();
							
							for (int i = 0; i < array.length(); i++) {
								Mod202Key key = script.getKeys()[i];
								JsIRPFComputeInfoGridPanel grid = new JsIRPFComputeInfoGridPanel() {

									@Override
									protected String resolveKey(String keyString) {
										Mod202Key key = Mod202Key.valueOf(keyString);
										return key.getBoxAsString();
									}
									
								};
								grid.setTitle(AON.MSG.calcDetail());
								grid.setSubTitle(key.getBoxFormatted() + " - " + script.getLabel());
								grid.addContent(array.get(i));
								gridContainer.add(grid);
							}
							callback.showInfoPanelWidget(gridContainer);
							button.setEnabled(true);
						}
					});
				}
				
				private void showActAccountInfo(AonTableButton button) {
					button.setEnabled(false);
					Model202.SERVICE.getInfo(callback.getOptions().getOccam(),callback.getModel(),script, infoKey, new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							callback.showError(AON.MSG.errorMessage());
							button.setEnabled(true);
						}
	
						@Override
						public void onSuccess(String result) {
							JsAccountingBreakdownGridPanel grid = new JsAccountingBreakdownGridPanel( true );
							grid.setTitle(AON.MSG.modelRelatedEntries(callback.getModel().getModelFullName()));
							grid.setSubTitle(script.getLabel());
							JavaScriptObject arrayObject = JsonUtils.safeEval(result);
							JsArray<JsAccountingBreakdown> array = arrayObject.cast();
							for (int i = 0; i < array.length(); i++) {
								grid.addRow(array.get(i));
							}
							grid.addFooterRow();
							callback.showInfoPanelWidget(grid);
							button.setEnabled(true);
						}
					});
				}
				
				
				private AonTableButton addButton() {
					final AonTableButton button = new AonTableButton(infoKey.getLabel(),AON.CSS.aonIconHelp());
					button.setTabIndex(-2);
					buttonContainer.add(button);
					return button;
				}
				
				@Override public Void visitCompute() { 
					final AonTableButton button = addButton();
					button.addClickHandler(event -> showComputeInfo(button));
					return null; 
				}
				@Override 
				public Void visitComputeKey() {
					final AonTableButton button = addButton();
					button.addClickHandler(event -> showComputeKeyInfo(button));
					return null; 
				}
				@Override 
				public Void visitActAccount() {
					final AonTableButton button = new AonTableButton(infoKey.getLabel(),AON.CSS.aonIconList());
					button.addClickHandler(event -> showActAccountInfo(button));
					buttonContainer.add(button);
					return null; 
				}
				
				@Override public Void visitModelInvoiceIrpfBreakdown() { return null;}
				@Override public Void visitInvoice() {return null; }
				@Override public Void visitSalary() {return null; }
				@Override public Void visitModelSalaryIrpfBreakdown() {return null;}
				@Override public Void visitModelInVatAccrualInvoice() {return null;}
				@Override public Void visitModelOutVatAccrualInvoice() {return null;}
				@Override public Void visitProrratedModelInvoiceVatBreakdown() {return null;}
				@Override public Void visitModelInvoiceVatBreakdown() {return null;}
				@Override public Void visitDiffInvoice() {return null;}
				@Override public Void visitDiffSalary() { return null;}
				@Override public Void visitNone() { return null; }
				@Override public Void visitInAccrualInvoice() { return null; }
				@Override public Void visitOutAccrualInvoice() { return null; }
				@Override public Void visitDiffInAccrualInvoice() { return null; }
				@Override public Void visitDiffOutAccrualInvoice() { return null; }
				@Override public Void visitTitle() { return null; }
				@Override public Void visitIrpfActivity() { return null; }
				@Override public Void visitCorporate() { return null; }
			});
			table.setWidget(row, col, buttonContainer);
		}
	}

	public void calculateAndRefresh(final Model202Callback callback) {
		Model202.SERVICE.calculate(callback.getOptions().getOccam(),callback.getModel(),
				new AsyncCallback<Mod202>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.showError(AON.MSG.errorMessage());
					}

					@Override
					public void onSuccess(Mod202 result) {
						selectAndPopulate(callback, result);
					}
			
				}
			);	
	}

	protected void submitForm(Model202Callback callback, String action) {
		diskForm.setMethod(FormPanel.METHOD_POST);
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		diskForm.clear();
		FlowPanel diskPanel = new FlowPanel();
		diskPanel.add(mod202Hidden);
		diskPanel.add(domainIdHidden);
		diskPanel.add(domainNameHidden);
		diskPanel.add(userHidden);
		diskForm.add(diskPanel);
		mod202Hidden.setValue(String.valueOf(callback.getModel().getId()));
		domainIdHidden.setValue(String.valueOf(callback.getOptions().getDomain()));
		domainNameHidden.setValue(callback.getOptions().getDomainName());
		userHidden.setValue(callback.getOptions().getUser());
		diskForm.submit();
	}
	
	private void paintIdentificationTab(Model202Callback callback, TabLayoutPanel tabPanel) {
		AonFiscalModelIdentificationPanel<Mod202> identificationData = new AonFiscalModelIdentificationPanel<>( callback.getModel() ) ;
		identificationData.addValueChangeHandler(event -> {
			toolbarPanel.setTitle(AonStringUtils.join(callback.getModel().getDocument(),AonStringUtils.SPACE,callback.getModel().getFullName()));
			markAsDirty(callback);			
		});
		tabPanel.add(identificationData, AON.MSG.identification());
	}

	void decorateAdministrationTab() {
		if (admonPanel != null) {
			admonPanel.manageLinks();
		}
	}
	
	protected void paintDeclarationTab(Model202Callback callback, TabLayoutPanel tabPanel) {
		ScrollPanel declarationScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		AonDisplayTable table = new AonDisplayTable();
		table.addStyleName(AON.CSS.aonBlockCenter());
		
		receiptBox = new AonTextBox();
		receiptBox.setVisibleLength(15);
		receiptBox.setMaxLength(13);
		receiptBox.setValue( callback.getModel().getNumber() );
		receiptBox.addValueChangeHandler( event -> {
			callback.getModel().setNumber(receiptBox.getValue());
			markAsDirty(callback);
		});

		table.addRow()
			.addCell( new Label(AON.MSG.receipt()), AON.CSS.aonTableLabel())
			.addCell(receiptBox);
		
		// Complementaria: Numero justificante de la declaración anterior
		if (callback.getModel().isComplementary()) {
			final AonTextBox previousReceiptBox = new AonTextBox();
			previousReceiptBox.setVisibleLength(15);
			previousReceiptBox.setMaxLength(13);
			previousReceiptBox.setValue( callback.getModel().getReplacedNumber() );
			previousReceiptBox.addValueChangeHandler( event -> {
				callback.getModel().setReplacedNumber(previousReceiptBox.getValue());
				markAsDirty(callback);
			});
			table.addRow()
				.addCell( new Label(AON.MSG.previousReceipt()), AON.CSS.aonTableLabel())
				.addCell(previousReceiptBox);
		}	
		container.add(table);
		declarationScrollPanel.setWidget(container);
		tabPanel.add(declarationScrollPanel, AON.MSG.declaration());
	}
	
	protected void paintAdministrationTab(Model202Callback callback, TabLayoutPanel tabPanel) {
		IFiscalModelAdmonPanelCallback<Mod202, Model202ModuleOptions> cbk = 
			new IFiscalModelAdmonPanelCallback<Mod202, Model202ModuleOptions>() {

				@Override
				public Model202ModuleOptions getOptions() {
					return callback.getOptions();
				}

				@Override
				public Mod202 getModel() {
					return callback.getModel();
				}

				@Override
				public void showError(String msg) {
					callback.showError(msg);
				}

				@Override
				public String getValidatePrintAction() {
					return GWT.getHostPageBaseURL() + "aon_gwt_fiscal/ms/Mod202ValidatePrintAEAT";
					
				}

				@Override
				public String getDownloadFileAction() {
					return Model202Base.MODEL202_FILE;
				}

				@Override
				public String getSendAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod202SendAEAT";
				}

				@Override
				public void sendSuccessfully() {
					Model202.SERVICE.getMod202(callback.getOptions().getOccam(), 
							getModel().getId(), new AsyncCallback<Mod202>() {
						@Override
						public void onSuccess(Mod202 selected) {
							setDirty(callback, false);
							selectAndPopulate(callback, selected);
						}
						@Override
						public void onFailure(Throwable caught) {
							callback.showError(AON.MSG.unableToReadDeclaration(caught.getMessage()));
						}
					});
				}

				@Override
				public String getCheckAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod202CheckAEAT";
				}

				@Override
				public String getCheckDataResponseDataAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod202CheckDataResponseData";
				}

				@Override
				public String getModelInformationURL() {
					return "https://sede.agenciatributaria.gob.es/Sede/procedimientoini/GE00.shtml";
				}
		};
		admonPanel = new FiscalModelAdmonPanel<>(cbk);
		tabPanel.add( admonPanel, AON.MSG.administrationName(callback.getModel().getAdministration()));		
	}
	
	void decorateDeclarationTab(Model202Callback callback) {
		if (receiptBox != null) {
			receiptBox.setValue( callback.getModel().getNumber() );
		}
	}

}
