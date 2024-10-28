package com.esferalia.aon.gwt.fiscal.client.mod130;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.Objects;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAuditDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToastModel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.accounting.js.JsAccountingBreakdown;
import com.esferalia.aon.gwt.fiscal.client.accounting.js.JsAccountingBreakdownGridPanel;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.AonInvoiceViewer;
import com.esferalia.aon.gwt.fiscal.client.invoice.irpf.JsIRPFBreakdown;
import com.esferalia.aon.gwt.fiscal.client.invoice.irpf.JsIRPFBreakdownInvoiceGridPanel;
import com.esferalia.aon.gwt.fiscal.client.invoice.irpf.JsIRPFComputeInfo;
import com.esferalia.aon.gwt.fiscal.client.invoice.irpf.JsIRPFComputeInfoGridPanel;
import com.esferalia.aon.gwt.fiscal.client.invoice.vat.JsVatComputeKeyInfo;
import com.esferalia.aon.gwt.fiscal.client.invoice.vat.JsVatComputeKeyInfoGridPanel;
import com.esferalia.aon.gwt.fiscal.client.mod130.Model130.Model130Callback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFinishDeclarationPopup;
import com.esferalia.aon.gwt.fiscal.client.model.AonFinishDeclarationPopup.IFinishDeclarationPopupCallback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelIdentificationPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelKeyInfoVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.fiscal.mod130.Model130ScriptProvider;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
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

public abstract class Model130Base extends DockLayoutPanel {

	protected FiscalModelAdmonPanel<Mod130, Model130ModuleOptions> admonPanel;

	private static final String BLANK = "_blank";
	
	private static final int MAX_LABEL_LENGTH = 300;
	private static final int COL_NUMBER = 4;
	
	protected static final String MODEL130_PRINT = "/aon_gwt_fiscal/ms/Model130Print";
	protected static final String MODEL130_FILE = "/aon_gwt_fiscal/ms/Model130File";

	private Model130Callback callback;
	private Mod130 mod130;
	private EnumMap<Mod130Key,AonDoubleBox> fieldsMap;
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
	
	private FlowPanel paymentContainer;
	
	private final AonToolbar decToolbar = new AonToolbar();
	private final InlineLabel dirtyLabel = new InlineLabel();
	private final InlineLabel alcatrazUnboundLabel = new InlineLabel();
	private final InlineLabel adjLabel = new InlineLabel();
	private final InlineLabel replacedLabel = new InlineLabel();
	private final Label statusLabel = new Label();
	
	protected FormPanel diskForm = new FormPanel(BLANK);
	protected Hidden mod130Hidden = new Hidden("mod130");
	protected Hidden domainIdHidden = new Hidden("domainId");
	protected Hidden domainNameHidden = new Hidden("domainName");
	protected Hidden userHidden = new Hidden("user");

	private AonToastModel toast = null;
	
	protected Model130Base(Mod130 mod130, final Model130Callback callback) {
		super(Unit.PX);
		setStyleName(AON.CSS.aonSelector());
		this.callback = callback;
		this.fieldsMap = new EnumMap<>(Mod130Key.class);
		
		select( mod130 );
		
		AonFiscalModelHeader modelHeader = new AonFiscalModelHeader( mod130 );
		addNorth(modelHeader, AonFiscalModelHeader.HEIGTH);
		addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		addNorth(getDeclarationToolbarPanel(), AonToolbar.HEIGTH);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		showPaymentInfo(mod130);
		
		paintIdentificationTab(tabPanel);
		paintDeclarationTab(tabPanel);
		paintLiquidationTab(tabPanel);
		paintAdministrationTab(tabPanel);
	}
	
	public Model130Callback getCallback() {
		return callback;
	}
	public Mod130 getModel() {
		return mod130;
	}
	public void setModel(Mod130 model) {
		this.mod130 = model;
	}
	
	private void select( Mod130 mod130) {
		setModel(mod130);
		refreshToolbarState();
		styleStatusLabel(mod130);
	}
	
	protected void selectAndPopulate( Mod130 mod130) {
		select(mod130);
		populate(mod130);
		decorateDeclarationTab();
		decorateAdministrationTab();
	}

	private void populate(Mod130 mod130) {
		for (Entry<Mod130Key, AonDoubleBox> entry : fieldsMap.entrySet()) {
			double d1 = mod130.getAmount(entry.getKey());
			double d2 = entry.getValue().getValue();
			if (!AonNumberUtils.equals(d1, d2)) {
				entry.getValue().setValue(d1,false,true);
			}
		}
	}

	private AonToolbar getToolbarPanel() {
		 
		if (getCallback().getOptions().isBackButtonVisible() && getCallback().getOptions().hasExternalCallback()) {
			cancelButton.setText(AON.MSG.backAction());
			cancelButton.setTitle(AON.MSG.backAction());
		}
		cancelButton.addClickHandler(event ->  cancel() );
		toolbarPanel.add(cancelButton);

		newButton.addClickHandler( event ->  getCallback().onNew() );
		toolbarPanel.add(newButton);

		saveButton.addClickHandler(event ->  save());
		toolbarPanel.add(saveButton);
		
		deleteButton.addClickHandler(event -> delete());
		toolbarPanel.add(deleteButton);
		
		printButton.addClickHandler( event ->  print());
		toolbarPanel.add(printButton);
		
		commentsButton.addClickHandler( event -> {
			if (toast == null || toast.getParent() == null) {
				toast = new AonToastModel(this);
				FlowPanel commentPanel = new FlowPanel();
				commentPanel.setStyleName( FiscalModelUtils.getAdministrationBackgroundStyle(getModel().getAdministration()) );
				commentPanel.addStyleName(AON.CSS.aonHeightAll());
				commentPanel.addStyleName(AON.CSS.aonTextCenter());
				TextArea comment = new TextArea();
				comment.addValueChangeHandler(event1 -> {
					getModel().setComments(event1.getValue());
					styleCommentsButton();
					Model130.SERVICE.saveComments( getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod130>() {
						@Override
						public void onSuccess(Mod130 result) {
							toast.hide();
						}
	
						@Override
						public void onFailure(Throwable caught) {
							toast.hide();
							getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						}
					});
				});
				comment.setText(getModel().getComments());
				comment.setWidth("90%");
				comment.setHeight("5em");
				commentPanel.add(comment);
				toast.show(AON.MSG.comments(), commentPanel);
			}
		});
		toolbarPanel.add( commentsButton );
		styleCommentsButton();

		auditButton.addClickHandler( event -> audit());
		toolbarPanel.add(auditButton);

		toolbarPanel.add(diskForm);
		
		return toolbarPanel;
	}

	private AonToolbar getDeclarationToolbarPanel() {
		
		markAsFinishedButton.setText(markAsFinishedButton.getTitle());
		markAsFinishedButton.addClickHandler( event -> markAsFinished());
		decToolbar.add(markAsFinishedButton);

		markAsSentButton.setText(markAsSentButton.getTitle());
		markAsSentButton.addClickHandler( event -> markAsSent());
		decToolbar.add(markAsSentButton);

		markAsPendingButton.setText(markAsPendingButton.getTitle());
		markAsPendingButton.addClickHandler( event -> reopenDeclaration());
		decToolbar.add(markAsPendingButton);

		FlowPanel marksPanels = new FlowPanel();
		marksPanels.setStyleName(AON.CSS.aonFlexBlock());
		
		dirtyLabel.setStyleName(AON.CSS.aonIconLabel());
		dirtyLabel.addStyleName(AON.CSS.aonIconDirty());
		dirtyLabel.setTitle("Cambios sin guardar");
		dirtyLabel.getElement().getStyle().setWidth(10, Unit.PX);
		dirtyLabel.getElement().getStyle().setHeight(10, Unit.PX);
		marksPanels.add(dirtyLabel);
		
		alcatrazUnboundLabel.setStyleName(AON.CSS.aonMarginLeft());
		alcatrazUnboundLabel.addStyleName(AON.CSS.aonLabelWithIcon());
		alcatrazUnboundLabel.addStyleName(AON.CSS.aonIconWarning());
		alcatrazUnboundLabel.setTitle("Modelo sin facturas/n\u00F3minas vinculadas");
		marksPanels.add(alcatrazUnboundLabel);

		adjLabel.setStyleName(AON.CSS.aonMarginLeft());
		adjLabel.addStyleName(AON.CSS.aonIconLabel());
		adjLabel.addStyleName(AON.CSS.aonIconWrench());
		adjLabel.addStyleName(AON.CSS.aonColorBlue());
		adjLabel.setTitle("Ajustes realizados");
		marksPanels.add(adjLabel);

		if (getModel().isReplacement()) {
			replacedLabel.setText(AON.MSG.replacement());
			replacedLabel.setStyleName(AON.CSS.aonMarginLeft());
			replacedLabel.addStyleName(AON.CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.CSS.aonLabelWithIcon());
		}
		if (getModel().isComplementary()) {
			replacedLabel.setText( AON.MSG.complementary());
			replacedLabel.setStyleName(AON.CSS.aonMarginLeft());
			replacedLabel.addStyleName(AON.CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.CSS.aonLabelWithIcon());
		}
		marksPanels.add(replacedLabel);

		styleDirtyLabel();
		styleStatusLabel(this.getModel());
		
		decToolbar.getMessagePanel().add(marksPanels);
		
		decToolbar.setTitle(statusLabel);
		return decToolbar;
	}
	
	protected void markAsDirty() {
		setDirty(true);
	}
	private boolean isDirty() {
		return this.dirty;
	}
	private void setDirty(boolean dirty) {
		this.dirty = dirty;
		styleDirtyLabel();
	}

	private void styleDirtyLabel() {
		dirtyLabel.setVisible(isDirty());
		alcatrazUnboundLabel.setVisible(!getModel().isAlcatrazBound()); 		
		boolean adjusted = false;
		for (FiscalModelDetail det : this.getModel().getMap().values()) {
			if (AonMathUtils.isNotZero( det.getAdjustAmount())) {
				adjusted = true;
				break;
			}
		}
		adjLabel.setVisible(adjusted);
	}

	private void refreshToolbarState() {
		toolbarPanel.setTitle(AonStringUtils.join(getModel().getDocument(),AonStringUtils.SPACE,getModel().getFullName()));
		auditButton.setVisible(!getModel().isNew());
		newButton.setVisible(!getModel().isNew() && !getCallback().getOptions().isBackButtonVisible() && !getCallback().getOptions().hasExternalCallback());
		cancelButton.setVisible(true);
		saveButton.setVisible(!getModel().isFinished() && !getModel().isSent());
		deleteButton.setVisible(!getModel().isNew() && !getModel().isFinished() && !getModel().isSent());
		printButton.setVisible(!getModel().isNew());
//		markAsPendingButton.setVisible(!getModel().isNew() &&
//				(getModel().getStatus() == FiscalStatus.FINISHED 
//				|| getModel().getStatus() == FiscalStatus.BATCHED
//				|| getModel().getStatus() == FiscalStatus.SENT
//				|| getModel().getStatus() == FiscalStatus.CUSTOMER_CHECK
//				|| getModel().getStatus() == FiscalStatus.BLOCKED));
//		markAsFinishedButton.setVisible(!getModel().isNew() &&
//				(getModel().getStatus() == FiscalStatus.PENDING
//				|| getModel().getStatus() == FiscalStatus.CUSTOMER_CHECK
//				|| getModel().getStatus() == FiscalStatus.MISSING));
//		markAsSentButton.setVisible(!getModel().isNew() &&
//				(getModel().getStatus() == FiscalStatus.FINISHED));
		markAsPendingButton.setVisible(!getModel().isNew() && !getModel().isRecorded() && FiscalModelUtils.canChangeStatus(getModel(), FiscalStatus.PENDING));
		markAsFinishedButton.setVisible(!getModel().isNew() && FiscalModelUtils.canChangeStatus(getModel(), FiscalStatus.FINISHED));
		markAsSentButton.setVisible(!getModel().isNew() && FiscalModelUtils.canChangeStatus(getModel(), FiscalStatus.SENT));
		
	}
	
	private void styleStatusLabel(Mod130 mod130) {
		statusLabel.setText(mod130.getStatus().getName());
		statusLabel.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB( mod130.getStatus() ));
		statusLabel.getElement().getStyle().setColor(FiscalModelUtils.getStatusFrgColorRGB( mod130.getStatus() ));
		statusLabel.setStyleName(AON.CSS.aonToolbarTitle());
		statusLabel.addStyleName(AON.CSS.aonPaddingLeft());
		statusLabel.addStyleName(AON.CSS.aonPaddingRight());
		statusLabel.addStyleName(AON.CSS.aonTextCenter());
		statusLabel.addStyleName(AON.CSS.aonBorder());
		statusLabel.addStyleName(AON.CSS.aonNowrap());
	}
	
	private void styleCommentsButton() {
		if (AonStringUtils.isEmpty(getModel().getComments())) {
			commentsButton.addStyleName(AON.CSS.aonIconNoComments());
			commentsButton.removeStyleName(AON.CSS.aonIconComments());
		} else {
			commentsButton.addStyleName(AON.CSS.aonIconComments());
			commentsButton.removeStyleName(AON.CSS.aonIconNoComments());
		}
		commentsButton.setTitle(getModel().getComments());
	}
	
	private void cancel() {
		cancelButton.setEnabled(false);
		if (isDirty()) {
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.confirmDeclarationCancelAction(), new AonConfirmDialogCallback() {

				@Override
				public void onAccept() {
					if (getCallback().getOptions().isBackButtonVisible() && getCallback().getOptions().hasExternalCallback()) {
						getCallback().getOptions().getExternalCallback().onExit(getModel());
					} else {
						getCallback().onCancel(getModel());
					}
				}
				@Override
				public void onCancel() {
					cancelButton.setEnabled(true);
				}
			});
		} else {
			if (getCallback().getOptions().isBackButtonVisible() && getCallback().getOptions().hasExternalCallback()) {
				getCallback().getOptions().getExternalCallback().onExit(getModel());
			} else {
				getCallback().onCancel(getModel());
			}
		}
	}
	
	private void save() {
		save(null);
	}
	private void save(AsyncCallback<Mod130> cbk) {
		saveButton.setEnabled(false);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add(new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model130.SERVICE.save(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod130>() {
			@Override
			public void onSuccess(Mod130 result) {
				setDirty( false );
				select(result);
				popup.hide();
				saveButton.setEnabled(true);
				if (cbk != null) cbk.onSuccess(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
				getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
				saveButton.setEnabled(true);
			}
		});
	}
	
	private void delete() {
		deleteButton.setEnabled(false);
		AonConfirmDialog cd = new AonConfirmDialog();
		cd.confirm(AON.MSG.confirmDeclarationDeleteAction(), new AonConfirmDialogCallback() {

			@Override
			public void onAccept() {
				Model130.SERVICE.delete(getCallback().getOptions().getOccam(),getModel(), new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						deleteButton.setEnabled(true);
						getCallback().onRemove(getModel());
					}

					@Override
					public void onFailure(Throwable caught) {
						deleteButton.setEnabled(true);
						getCallback().showError(AON.MSG.unableToDeleteDeclaration(caught.getMessage()));
					}
				});
			}

			@Override
			public void onCancel() {
				deleteButton.setEnabled(true);
			}
		});
	}
	
	private void print() {
		if (isDirty()) {
			new AonConfirmDialog().confirm(AON.MSG.draftPrint(),AON.MSG.draftPrintNote(), new AonConfirmDialogCallback() {
				@Override
				public void onAccept() {
					submitForm(MODEL130_PRINT);
				}
				@Override
				public void onCancel() {
					// Nothing
				}
			});
		} else {
			submitForm(MODEL130_PRINT);
		}
	}

	private void audit() {
		AonAuditDialog dialog = new AonAuditDialog();
		dialog.show(getModel());
	}
	
	private void reopenDeclaration() {
		markAsPendingButton.setEnabled(false);
		if (getModel().isSent() && AonStringUtils.isNotBlank(getModel().getNumber())) {
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.confirmReopenDeclarationAction(), new AonConfirmDialogCallback() {
				
				@Override
				public void onAccept() {
					doReopenDeclaration();
				}
				
				@Override
				public void onCancel() {
					markAsPendingButton.setEnabled(true);
				}
			});
		} else {
			doReopenDeclaration();
		}
	}
	
	private void doReopenDeclaration() {
		markAsPendingButton.setEnabled(false);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model130.SERVICE.markAsPending(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod130>() {
					@Override
					public void onSuccess(Mod130 result) {
						setDirty(false);
						selectAndPopulate(result);
						popup.hide();
						markAsPendingButton.setEnabled(true);
						showPaymentInfo(getModel());
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						getCallback().showError(AON.MSG.unableToReopenDeclaration(caught.getMessage()));
						markAsPendingButton.setEnabled(true);
					}
				});
	}

	private void markAsSent() {
		markAsSentButton.setEnabled(false);
		Model130.SERVICE.markAsSent(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod130>() {
					@Override
					public void onSuccess(Mod130 result) {
						setDirty(false);
						selectAndPopulate(result);
						markAsSentButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						getCallback().showError(AON.MSG.unableToMarkAsSentDeclaration(caught.getMessage()));
						markAsSentButton.setEnabled(true);
					}
				});
	}

	private void markAsFinished() {
		markAsFinishedButton.setEnabled(false);
		Model130.SERVICE.initializeForFinish(getCallback().getOptions().getOccam(),getModel(),
				new AsyncCallback<Mod130>() {
					@Override
					public void onSuccess(Mod130 m130) {
						selectAndPopulate(m130);
						showFinalizePopup(getModel());
						markAsFinishedButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						getCallback().showError(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
						markAsFinishedButton.setEnabled(true);
					}
				});
	}
	
	private void showFinalizePopup(Mod130 model) {
		AonFinishDeclarationPopup<Mod130,Model130ModuleOptions> finalizeDialog = new AonFinishDeclarationPopup<>(
			model,
			getCallback(), 
			new IFinishDeclarationPopupCallback<Mod130>() {

				@Override
				public void onAccept(Mod130 t) {
					Model130Base.this.doMarkAsFinished();
				}

				@Override
				public void onCancel(Mod130 t) {
					// Nothing
					
				}

				@Override
				public void onCustomerCheck(Mod130 t) {
					Model130Base.this.markAsCustomerCheck();
				}
		});
		finalizeDialog.center();
		finalizeDialog.show();
	}
	
	private void doMarkAsFinished() {
		markAsFinishedButton.setEnabled(false);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model130.SERVICE.markAsFinished(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod130>() {
			@Override
			public void onSuccess(Mod130 result) {
				setDirty(false);
				selectAndPopulate(result);
				popup.hide();
				markAsFinishedButton.setEnabled(true);
				showPaymentInfo(getModel());
			}

			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
				getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
				markAsFinishedButton.setEnabled(true);
			}
		});
	}
	
	
	private void markAsCustomerCheck() {
		markAsFinishedButton.setEnabled(false);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model130.SERVICE.markAsCustomerCheck(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod130>() {
			@Override
			public void onSuccess(Mod130 result) {
				setDirty(false);
				select(result);
				popup.hide();
				markAsFinishedButton.setEnabled(true);
				showPaymentInfo(getModel());
			}

			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
				getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
				markAsFinishedButton.setEnabled(true);
			}
		});
	}

	protected void showPaymentInfo(Mod130 mod) {
		if (paymentContainer != null) {
			this.remove(paymentContainer);
			this.forceLayout();
		}
		if (mod.isFinished() || mod.isSent() || mod.isCustomerCheck()) {
//			StringBuilder buff = new StringBuilder(AON.MSG.result());
//			buff.append(AonStringUtils.SPACE);
//			buff.append(AON.FMT.format(mod.getDeclarationResult()));
//			if (mod.getDeclarationResultType() != null) {
//				buff.append(AonStringUtils.SPACE);
//				buff.append(mod.getDeclarationResultType().getDescription());
//			}
//			if (mod.getFinance() != null && mod.getFinance().getBankAccount() != null && AonStringUtils.isNotBlank(mod.getFinance().getBankAccount().getIban())) {
//				buff.append(AonStringUtils.SPACE);
//				buff.append(AonStringUtils.defaultString(mod.getFinance().getBankAccount().getIban()));
//				buff.append(AonStringUtils.SPACE);
//				buff.append(AonStringUtils.defaultString(mod.getFinance().getBankAlias()));
//			}
			paymentContainer = new FlowPanel();
			paymentContainer.setStyleName(AON.CSS.aonWidthAll());
//			Label label = new Label( buff.toString() );
			Label label = new Label( FiscalModelUtils.getPaymentInfo(mod) );
			label.setStyleName(AON.CSS.aonWidthAll());
			label.addStyleName(AON.CSS.aonTextCenter());
			label.addStyleName(AON.CSS.aonBold());
			paymentContainer.add(label);
			this.insert( paymentContainer, Direction.NORTH, 30, decToolbar);
			this.forceLayout();
		}
	}
	
	private void paintLiquidationTab(TabLayoutPanel tabPanel) {
		ScrollPanel liquidationScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		FlexTable table = new FlexTable();
		container.add(table);
		defineTable(table);
		for (IModelScript<Mod130Key> ms : Model130ScriptProvider.obtainScript(getModel())) {
			if (ms.paintHeaderBefore()) {
				paintHeader(table);
			}
			paintRow(table,getCallback(),ms);	
		}
		liquidationScrollPanel.setWidget(container);
		tabPanel.add(liquidationScrollPanel, AON.MSG.liquidacion());
	}
	
	protected void defineTable(FlexTable table) {
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );
		
		table.getColumnFormatter().setWidth(1, "40px");
		table.getColumnFormatter().setStyleName(5, AON.CSS.aonTextCenter());
		table.getColumnFormatter().setWidth(2, "140px");
		
		table.getColumnFormatter().setWidth(3, "50px");
	}
	
	protected void paintHeader(FlexTable table) {
		int row = table.getRowCount();
		table.setWidget(row, 0, new Label(" "));
		table.getFlexCellFormatter().setStyleName(row, 0,AON.CSS.aonBold() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonTextCenter() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderTop() );

		table.setWidget(row, 1, new Label(" "));
		table.getFlexCellFormatter().setStyleName(row, 1,AON.CSS.aonBold() );
		table.getFlexCellFormatter().addStyleName(row, 1,AON.CSS.aonTextCenter() );
		table.getFlexCellFormatter().addStyleName(row, 1,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 1,AON.CSS.aonBorderTop() );
		table.getFlexCellFormatter().setColSpan(row, 1, 2);

		table.setWidget(row, 2, new Label("Inf."));
		table.getFlexCellFormatter().setStyleName(row, 2,AON.CSS.aonBold() );
		table.getFlexCellFormatter().addStyleName(row, 2,AON.CSS.aonTextCenter() );
		table.getFlexCellFormatter().addStyleName(row, 2,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 2,AON.CSS.aonBorderTop() );
	}
		
	protected void paintEmptyRow(FlexTable table) {
		int row = table.getRowCount();
		table.setWidget(row, 0, new Label());
		table.getFlexCellFormatter().setColSpan(row, 0, COL_NUMBER);
	}

	protected void paintRow(FlexTable table, final Model130Callback callback, IModelScript<Mod130Key> script) {
		if (script.hasGraphicParticularity()) {
			paintParticularyRow(table, callback,script);		
		} else {
			int row = table.getRowCount();
			paintLabel(table, row,script);
			if (script.getKeys() == null) {
				table.getFlexCellFormatter().setColSpan(row, 0, COL_NUMBER);	
			} else {
				int col = 1;
				for (Mod130Key key : script.getKeys()) {
					col = paintBox(table, row, col, key );
					col = paintField(table, row, col, callback, script, key );
				}
				paintInfoCol(table, row,col,callback,script);	
			}
		}
	}
	
	protected void paintParticularyRow(FlexTable table, final Model130Callback callback, IModelScript<Mod130Key> script) {
		
	}
	
	protected void paintLabel(FlexTable table, int row, IModelScript<Mod130Key> script) {
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
	
	private int paintBox(FlexTable table, int row, int col, Mod130Key key) {
		table.setWidget(row, col, new AonBoxLabel(key.getBox()));
		return ++col;
	}

	private int paintField(FlexTable table, int row, int col, final Model130Callback callback, IModelScript<Mod130Key> script, final Mod130Key key) {
		final FiscalModelDetail det1 = getModel().ensureDetail(key);
		final AonDoubleBox input = new AonDoubleBox();
		fieldsMap.put(key, input);
		input.setEnabled(script.isEnabled()); 
		input.setValue(det1.getAmount());
		input.addValueChangeHandler(event -> {
			if (event.getValue() == null) input.setValue(0.0, false);
			double result = getModel().getResultAmount(key);
			double adjust = getModel().getAdjustAmount(key);
			double amount = input.getValue();
			if (AonMathUtils.isNotZero(result - adjust - amount)) {
				getModel().ensureDetail(key).setAdjustAmount( result - amount);	
			}
			getModel().ensureDetail(key).setAmount(input.getValue());
			if (input.isEnabled()) {
				calculateAndRefresh( callback );
			}
			markAsDirty();
		});
		table.setWidget(row, col, input);
		return ++col;
	}
	
	private void paintInfoCol(FlexTable table, int row, int col, final Model130Callback callback, final IModelScript<Mod130Key> script) {
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
					
					Model130.SERVICE.getInfo(callback.getOptions().getOccam(),getModel(),script, infoKey, new AsyncCallback<String>() {
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
							grid.setSubTitle(AonStringUtils.join(
								Arrays.stream(script.getKeys())
									.filter( Objects::nonNull )
									.map( Mod130Key::getBoxFormatted )
									.reduce("", String::concat)
								, " " 
								, script.getLabel()));
							
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

				private void showInvoiceIrpfBreakdownInfo(AonTableButton button) {
					button.setEnabled(false);
					Model130.SERVICE.getInfo(callback.getOptions().getOccam(),getModel(),script, infoKey, new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							callback.showError(AON.MSG.errorMessage());
							button.setEnabled(true);
						}
	
						@Override
						public void onSuccess(String result) {
							JsIRPFBreakdownInvoiceGridPanel grid = new JsIRPFBreakdownInvoiceGridPanel( true );
							grid.addSelectionHandler(event -> showInvoice(event.getSelectedItem()));
							grid.setTitle(AON.MSG.modelRelatedInvoices(getModel().getModelFullName()));
							grid.setSubTitle(script.getLabel());
							JavaScriptObject arrayObject = JsonUtils.safeEval(result);
							JsArray<JsIRPFBreakdown> array = arrayObject.cast();
							for (int i = 0; i < array.length(); i++) {
								grid.addRow(array.get(i));
							}
							grid.addFooterRow();
							callback.showInfoPanelWidget(grid);
							button.setEnabled(true);
						}
					});
				}
				
				private void showComputeInfo(AonTableButton button) {
					button.setEnabled(false);
					Model130.SERVICE.getInfo(callback.getOptions().getOccam(),getModel(),script, infoKey, new AsyncCallback<String>() {
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
								Mod130Key key = script.getKeys()[i];
								JsIRPFComputeInfoGridPanel grid = new JsIRPFComputeInfoGridPanel() {

									@Override
									protected String resolveKey(String keyString) {
										Mod130Key key = Mod130Key.valueOf(keyString);
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
					Model130.SERVICE.getInfo(callback.getOptions().getOccam(),getModel(),script, infoKey, new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							callback.showError(AON.MSG.errorMessage());
							button.setEnabled(true);
						}
	
						@Override
						public void onSuccess(String result) {
							JsAccountingBreakdownGridPanel grid = new JsAccountingBreakdownGridPanel( true );
							grid.setTitle(AON.MSG.modelRelatedInvoices(getModel().getModelFullName()));
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
					buttonContainer.add(button);
					return button;
				}
				
				@Override 
				public Void visitModelInvoiceIrpfBreakdown() {
					if (getModel().isAlcatrazBound()) {
						final AonTableButton button = addButton();
						button.addClickHandler(event -> showInvoiceIrpfBreakdownInfo(button));
					}
					return null;
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

	public void calculateAndRefresh(final Model130Callback callback) {
		Model130.SERVICE.calculate(callback.getOptions().getOccam(),getModel(),
			new AsyncCallback<Mod130>() {

				@Override
				public void onFailure(Throwable caught) {
					callback.showError(AON.MSG.errorMessage());
				}

				@Override
				public void onSuccess(Mod130 result) {
					selectAndPopulate(result);
				}
			}
		);	
	}
	
	protected void submitForm(String action) {
		diskForm.setMethod(FormPanel.METHOD_POST);
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		diskForm.clear();
		FlowPanel diskPanel = new FlowPanel();
		diskPanel.add(mod130Hidden);
		diskPanel.add(domainIdHidden);
		diskPanel.add(domainNameHidden);
		diskPanel.add(userHidden);
		diskForm.add(diskPanel);
		mod130Hidden.setValue(String.valueOf(getModel().getId()));
		domainIdHidden.setValue(String.valueOf(getCallback().getOptions().getDomain()));
		domainNameHidden.setValue(getCallback().getOptions().getDomainName());
		userHidden.setValue(getCallback().getOptions().getUser());
		diskForm.submit();
	}

	private void paintIdentificationTab(TabLayoutPanel tabPanel) {
		AonFiscalModelIdentificationPanel<Mod130> identificationData = new AonFiscalModelIdentificationPanel<>( getModel() ) ;
		identificationData.addValueChangeHandler(event -> {
			toolbarPanel.setTitle(AonStringUtils.join(getModel().getDocument(),AonStringUtils.SPACE,getModel().getFullName()));
			markAsDirty();			
		});
		tabPanel.add(identificationData, AON.MSG.identification());
	}

	void decorateAdministrationTab() {
		if (admonPanel != null) {
			admonPanel.manageLinks();
		}
	}

	void paintDeclarationTab(TabLayoutPanel tabPanel) {}
	void paintAdministrationTab(TabLayoutPanel tabPanel) {}
	void decorateDeclarationTab() {}
	
	private void showInvoice(JsIRPFBreakdown br) {
		int invoiceId = br.getInvoice();
		Model130.SERVICE.getInvoice(getCallback().getOptions().getOccam(), invoiceId,new AsyncCallback<Invoice>() {
			@Override
			public void onSuccess(Invoice inv) {
				AonCustomPopup dialog = new AonCustomPopup();
				dialog.setWidth((Window.getClientWidth() - 100) + "px");
				dialog.setHeight((Window.getClientHeight() - 100) + "px");
				dialog.setAnimationEnabled(true);
				dialog.setGlassEnabled(true);
				dialog.setModal(true);
				dialog.setCaption(AON.MSG.invoice());
				dialog.add(new AonInvoiceViewer(inv));
				dialog.center();
				dialog.show();
			}

			@Override
			public void onFailure(Throwable caught) {
				getCallback().showError(caught.getMessage());
			}
		});
	}
}
