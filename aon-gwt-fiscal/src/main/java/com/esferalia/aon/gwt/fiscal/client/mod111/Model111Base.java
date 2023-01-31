package com.esferalia.aon.gwt.fiscal.client.mod111;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Objects;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAuditDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToast;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.AonInvoiceViewer;
import com.esferalia.aon.gwt.fiscal.client.invoice.irpf.JsIRPFBreakdown;
import com.esferalia.aon.gwt.fiscal.client.invoice.irpf.JsIRPFBreakdownInvoiceGridPanel;
import com.esferalia.aon.gwt.fiscal.client.invoice.irpf.JsIRPFBreakdownSalaryGridPanel;
import com.esferalia.aon.gwt.fiscal.client.invoice.irpf.JsIRPFComputeInfo;
import com.esferalia.aon.gwt.fiscal.client.invoice.irpf.JsIRPFComputeInfoGridPanel;
import com.esferalia.aon.gwt.fiscal.client.invoice.irpf.JsIRPFComputeKeyInfo;
import com.esferalia.aon.gwt.fiscal.client.invoice.irpf.JsIRPFComputeKeyInfoGridPanel;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111.Model111Callback;
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
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.mod111.Model111ScriptProvider;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
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

public abstract class Model111Base extends DockLayoutPanel {
	protected FiscalModelAdmonPanel<Mod111, Model111ModuleOptions> admonPanel;
	private AonFiscalModelIdentificationPanel<Mod111> identificationData; 
			
	private static final String WIDTH_140PX = "140px";
	private static final String BLANK = "_blank";
	private static final int MAX_LABEL_LENGTH = 100;
	private static final int COL_NUMBER = 8;
	private static final String MODEL111_PRINT = "/aon_gwt_fiscal/ms/Model111Print";
	protected static final String MODEL111_FILE = "/aon_gwt_fiscal/ms/Model111File";
	private static final String MODEL111_BOX_INFO = "/aon_gwt_fiscal/ms/Model111BoxInfoPrint";

	private Model111Callback callback;
	private Mod111 model;
	private EnumMap<Mod111Key,AonDoubleBox> fieldsMap;
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
	private AonTextBox receiptBox;
	
	protected FormPanel diskForm = new FormPanel(BLANK);
	protected Hidden mod111Hidden = new Hidden("mod111");
	protected Hidden domainIdHidden = new Hidden("domainId");
	protected Hidden domainNameHidden = new Hidden("domainName");
	protected Hidden userHidden = new Hidden("user");
	protected Hidden mod111BoxHidden = new Hidden("mod111Box");
	
	protected Model111Base(Mod111 mod111, Model111Callback callback) {
		super(Unit.PX);
		setStyleName(AON.CSS.aonSelector());
		fieldsMap = new EnumMap<>(Mod111Key.class);
		this.callback = callback;
		
		select( mod111 );
		
		AonFiscalModelHeader modelHeader = new AonFiscalModelHeader( mod111 );
		addNorth(modelHeader, AonFiscalModelHeader.HEIGTH);
		addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		addNorth(getDeclarationToolbarPanel(), AonToolbar.HEIGTH);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		showPaymentInfo(mod111);
		
		paintIdentificationTab(tabPanel);
		paintDeclarationTab(tabPanel);
		paintLiquidationTab(tabPanel);
		paintAdministrationTab(tabPanel);
		
		tabPanel.selectTab(2, false);
	}
	
	public Model111Callback getCallback() {
		return callback;
	}
	protected Mod111 getModel() {
		return model;
	}
	public void setModel(Mod111 mod111) {
		this.model = mod111;
	}
	
	private void select( Mod111 mod111) {
		setModel(mod111);
		refreshToolbarState();
		styleStatusLabel(mod111);
	}
	
	protected void selectAndPopulate( Mod111 mod111) {
		select(mod111);
		populate(mod111);
		showPaymentInfo(mod111);		
		decorateDeclarationTab();
		decorateAdministrationTab();
	}
	
	private void populate(Mod111 mod111) {
		identificationData.populate(mod111);
		for (Entry<Mod111Key, AonDoubleBox> entry : fieldsMap.entrySet()) {
			double d1 = mod111.getAmount(entry.getKey());
			double d2 = entry.getValue().getValue();
			entry.getValue().setEnabled(mod111.isEditable());
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

		newButton.addClickHandler( event ->  getCallback().onNew( ) );
		toolbarPanel.add(newButton);

		saveButton.addClickHandler(event ->  save());
		toolbarPanel.add(saveButton);
		
		deleteButton.addClickHandler(event -> delete());
		toolbarPanel.add(deleteButton);
		
		printButton.addClickHandler( event ->  print());
		toolbarPanel.add(printButton);
		
		commentsButton.addClickHandler( event -> {
			final AonToast toast = new AonToast();
			FlowPanel commentPanel = new FlowPanel();
			commentPanel.setStyleName( FiscalModelUtils.getAdministrationBackgroundStyle(getModel().getAdministration()) );
			commentPanel.addStyleName(AON.CSS.aonHeightAll());
			commentPanel.addStyleName(AON.CSS.aonTextCenter());
			TextArea comment = new TextArea();
			comment.addValueChangeHandler(event1 -> {
				getModel().setComments(event1.getValue());
				styleCommentsButton();
				Model111.SERVICE.saveComments( getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod111>() {
					@Override
					public void onSuccess(Mod111 result) {
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
		adjLabel.addStyleName(AON.CSS.aonIconRedWrench());
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
		toolbarPanel.setTitle(AonStringUtils.join(model.getDocument(),AonStringUtils.SPACE,model.getFullName()));
		newButton.setVisible(!model.isNew() && !getCallback().getOptions().isBackButtonVisible() && !getCallback().getOptions().hasExternalCallback());
		cancelButton.setVisible(true);
		saveButton.setVisible(model.isEditable());
		deleteButton.setVisible(!model.isNew() && model.isEditable());
		auditButton.setVisible(!model.isNew());
		printButton.setVisible(!model.isNew());
		markAsPendingButton.setVisible(!model.isNew() && FiscalModelUtils.canChangeStatus(model, FiscalStatus.PENDING));
		markAsFinishedButton.setVisible(!model.isNew() && FiscalModelUtils.canChangeStatus(model, FiscalStatus.FINISHED));
		markAsSentButton.setVisible(!model.isNew() && FiscalModelUtils.canChangeStatus(model, FiscalStatus.SENT));
	}
	
	private void styleStatusLabel(Mod111 mod111) {
		statusLabel.setText(mod111.getStatus().getName());
		statusLabel.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB( mod111.getStatus() ));
		statusLabel.getElement().getStyle().setColor(FiscalModelUtils.getStatusFrgColorRGB( mod111.getStatus() ));
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
					getCallback().onCancel(getModel());
				}
				@Override
				public void onCancel() {
					cancelButton.setEnabled(true);
				}
			});
		} else {
			getCallback().onCancel(getModel());
		}
	}
	
	private void save() {
		save(null);
	}
	private void save(AsyncCallback<Mod111> cbk) {
		saveButton.setEnabled(false);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add(new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model111.SERVICE.save(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod111>() {
			@Override
			public void onSuccess(Mod111 result) {
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
				Model111.SERVICE.delete(getCallback().getOptions().getOccam(),getModel(), new AsyncCallback<Void>() {
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
					submitForm(MODEL111_PRINT);
				}
				@Override
				public void onCancel() {
					// Nothing
				}
			});
		} else {
			submitForm(MODEL111_PRINT);
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
		Model111.SERVICE.markAsPending(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod111>() {
					@Override
					public void onSuccess(Mod111 result) {
						setDirty(false);
						selectAndPopulate(result);
						popup.hide();
						markAsPendingButton.setEnabled(true);
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
		Model111.SERVICE.markAsSent(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod111>() {
					@Override
					public void onSuccess(Mod111 result) {
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
		Model111.SERVICE.initializeForFinish(getCallback().getOptions().getOccam(),getModel(),
				new AsyncCallback<Mod111>() {
					@Override
					public void onSuccess(Mod111 m111) {
						selectAndPopulate(m111);
						showFinalizePopup(model);
						markAsFinishedButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						getCallback().showError(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
						markAsFinishedButton.setEnabled(true);
					}
				});
	}
	
	private void showFinalizePopup(Mod111 model) {
		AonFinishDeclarationPopup<Mod111,Model111ModuleOptions> finalizeDialog = new AonFinishDeclarationPopup<>(
			model,
			getCallback(), 
			new IFinishDeclarationPopupCallback<Mod111>() {

				@Override
				public void onAccept(Mod111 t) {
					Model111Base.this.doMarkAsFinished();
				}

				@Override
				public void onCancel(Mod111 t) {
					// Nothing
					
				}

				@Override
				public void onCustomerCheck(Mod111 t) {
					Model111Base.this.markAsCustomerCheck();
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
		Model111.SERVICE.markAsFinished(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod111>() {
			@Override
			public void onSuccess(Mod111 result) {
				selectAndPopulate(result);
				popup.hide();
				markAsFinishedButton.setEnabled(true);
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
		Model111.SERVICE.markAsCustomerCheck(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod111>() {
			@Override
			public void onSuccess(Mod111 result) {
				setDirty(false);
				select(result);
				popup.hide();
				markAsFinishedButton.setEnabled(true);
			}

			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
				getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
				markAsFinishedButton.setEnabled(true);
			}
		});
	}

	private void showPaymentInfo(Mod111 mod) {
		if (paymentContainer != null) {
			this.remove(paymentContainer);
			this.forceLayout();
		}
		if (!mod.isNew()) {
			StringBuilder buff = new StringBuilder(AON.MSG.result());
			buff.append(AonStringUtils.SPACE);
			buff.append(AON.FMT.format(mod.getDeclarationResult()));
			if (mod.getDeclarationResultType() != null) {
				buff.append(AonStringUtils.SPACE);
				buff.append(mod.getDeclarationResultType().getDescription());
			}
			if (mod.getFinance() != null && mod.getFinance().getBankAccount() != null && AonStringUtils.isNotBlank(mod.getFinance().getBankAccount().getIban())) {
				buff.append(AonStringUtils.SPACE);
				buff.append(AonStringUtils.defaultString(mod.getFinance().getBankAccount().getIban()));
				buff.append(AonStringUtils.SPACE);
				buff.append(AonStringUtils.defaultString(mod.getFinance().getBankAlias()));
			}
			paymentContainer = new FlowPanel();
			paymentContainer.setStyleName(AON.CSS.aonWidthAll());
			Label label = new Label( buff.toString() );
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
		IModelScript<Mod111Key>[] script = null;
		try {
			script = Model111ScriptProvider.obtainScript(getModel());
			for (IModelScript<Mod111Key> ms : script) {
				if (ms.paintHeaderBefore()) {
					paintHeader(table);
				}
				paintRow(table,getCallback(),ms);	
			}
			liquidationScrollPanel.setWidget(container);
			tabPanel.add(liquidationScrollPanel, AON.MSG.liquidacion());
		} catch (Exception e) {
			getCallback().showError(e.getMessage());
		}
	}
	
	private void defineTable(FlexTable table) {
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		
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
	
	private void paintHeader(FlexTable table) {
		int row = table.getRowCount();
		table.setWidget(row, 0, new Label(AON.MSG.concept()));
		table.getFlexCellFormatter().setStyleName(row, 0,AON.CSS.aonBold() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonTextCenter() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderTop() );

		table.setWidget(row, 1, new Label(AON.MSG.receivers()));
		table.getFlexCellFormatter().setStyleName(row, 1,AON.CSS.aonBold() );
		table.getFlexCellFormatter().addStyleName(row, 1,AON.CSS.aonTextCenter() );
		table.getFlexCellFormatter().addStyleName(row, 1,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 1,AON.CSS.aonBorderTop() );
		table.getFlexCellFormatter().setColSpan(row, 1, 2);

		table.setWidget(row, 2, new Label(AON.MSG.perceptions()));
		table.getFlexCellFormatter().setStyleName(row, 2,AON.CSS.aonBold() );
		table.getFlexCellFormatter().addStyleName(row, 2,AON.CSS.aonTextCenter() );
		table.getFlexCellFormatter().addStyleName(row, 2,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 2,AON.CSS.aonBorderTop() );
		table.getFlexCellFormatter().setColSpan(row, 2, 2);

		table.setWidget(row, 3, new Label(AON.MSG.retentionAccountShort()));
		table.getFlexCellFormatter().setStyleName(row, 3,AON.CSS.aonBold() );
		table.getFlexCellFormatter().addStyleName(row, 3,AON.CSS.aonTextCenter() );
		table.getFlexCellFormatter().addStyleName(row, 3,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 3,AON.CSS.aonBorderTop() );
		table.getFlexCellFormatter().setColSpan(row, 3, 2);
		
		table.setWidget(row, 4, new Label("Inf."));
		table.getFlexCellFormatter().setStyleName(row, 4,AON.CSS.aonBold() );
		table.getFlexCellFormatter().addStyleName(row, 4,AON.CSS.aonTextCenter() );
		table.getFlexCellFormatter().addStyleName(row, 4,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 4,AON.CSS.aonBorderTop() );
	}
		
	protected void paintEmptyRow(FlexTable table) {
		int row = table.getRowCount();
		table.setWidget(row, 0, new Label());
		table.getFlexCellFormatter().setColSpan(row, 0, COL_NUMBER);
	}

	private void paintRow(FlexTable table,final Model111Callback callback, IModelScript<Mod111Key> script) {
		if (script.hasGraphicParticularity()) {
			paintParticularyRow(table,callback,script);		
		} else {
			int row = table.getRowCount();
			paintLabel(table, row,script);
			if (script.getKeys() == null) {
				table.getFlexCellFormatter().setColSpan(row, 0, COL_NUMBER);	
			} else {
				int len = 1;
				if (script.getKeys().length==1) len = 5;
				else if (script.getKeys().length==2) len = 3;
				table.getFlexCellFormatter().setColSpan(row, 0, len);
				int col = 1;
				for (Mod111Key key : script.getKeys()) {
					col = paintBox( table, row, col, key );
					col = paintField( table, row, col, callback, script, key );
				}
				paintInfoCol(table, row,col,callback,script);	
			}
		}
	}
	
	protected void paintParticularyRow(FlexTable table, final Model111Callback callback, IModelScript<Mod111Key> script) {
		
	}
	
	protected void paintLabel( FlexTable table, int row, IModelScript<Mod111Key> script) {
		paintLabel(table, row, script.getLabel(), script.isTitle());
	}
	protected void paintLabel(FlexTable table,int row,String labelText) {
		paintLabel(table, row, labelText, false);
	}
	protected void paintLabel(FlexTable table,int row,String labelText,boolean title) {
		Label label = new Label();
		if (AonStringUtils.length(labelText) > MAX_LABEL_LENGTH) {
			label.setTitle(labelText);	
			labelText = AonStringUtils.abbreviate(labelText, MAX_LABEL_LENGTH);
		}
		label.setText(labelText);
		table.setWidget(row, 0, label);
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		if (title) {
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBold() );
		} else {
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		}
	}

	private int paintBox(FlexTable table, int row, int col, Mod111Key key) {
		table.setWidget(row, col, new AonBoxLabel(key.getBox()));
		return ++col;
	}

	private int paintField(FlexTable table, int row, int col, final Model111Callback callback, IModelScript<Mod111Key> script, final Mod111Key key) {
		final FiscalModelDetail det1 = getModel().ensureDetail(key);
		final AonDoubleBox input = new AonDoubleBox();
		fieldsMap.put(key, input);
		input.setEnabled(model.isEditable() && script.isEnabled()); 
		input.setValue(det1.getAmount());
		if (AonMathUtils.isNotZero(det1.getAdjustAmount())) {
			input.addStyleName(AON.CSS.aonChanged());
			input.setTitle(AON.MSG.difCalc(
					AON.FMT.format(det1.getResultAmount()),
					AON.FMT.format(AonMathUtils.round( det1.getAdjustAmount() * -1))));
		}
		input.addValueChangeHandler(event -> {
			if (event.getValue() == null) input.setValue(0.0, false);
			double result = getModel().getResultAmount(key);
			double adjust = getModel().getAdjustAmount(key);
			double amount = input.getValue();
			if (AonMathUtils.isNotZero(result - adjust - amount)) {
				getModel().ensureDetail(key).setAdjustAmount( result - amount);	
			}
			getModel().ensureDetail(key).setAmount(input.getValue());
			if (AonMathUtils.isNotZero(getModel().ensureDetail(key).getAdjustAmount())) {
				input.addStyleName(AON.CSS.aonChanged());
				input.setTitle(AON.MSG.difCalc(
					AON.FMT.format(getModel().ensureDetail(key).getResultAmount()),
					AON.FMT.format(AonMathUtils.round( getModel().ensureDetail(key).getAdjustAmount() * -1))));
			} else {
				input.removeStyleName(AON.CSS.aonChanged());
			}
			if (input.isEnabled()) {
				calculateAndRefresh( callback );
			}
			markAsDirty();
		});
		table.setWidget(row, col, input);
		return ++col;
	}
	
	private void paintInfoCol(FlexTable table, int row, int col, final Model111Callback callback, final IModelScript<Mod111Key> script) {
		FlowPanel buttonContainer = new FlowPanel();
		for (final FiscalModelKeyInfo infoKey : script.getInfoKeys()) {
			
			buttonContainer.setStyleName(AON.CSS.aonNowrap());
			infoKey.visit(new IFiscalModelKeyInfoVisitor<Void>() {

				private void showComputeKeyInfo(AonTableButton button) {
					button.setEnabled(false);
					Model111.SERVICE.getInfo(callback.getOptions().getOccam(),getModel(),script, infoKey, new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							button.setEnabled(true);
							callback.showError(AON.MSG.errorMessage());
						}
	
						@Override
						public void onSuccess(String result) {
							FlowPanel gridContainer = new FlowPanel();
							Mod111Key key = script.getKeys()[0];
							JsIRPFComputeKeyInfo info = JsonUtils.safeEval(result);
							JsIRPFComputeKeyInfoGridPanel grid = new JsIRPFComputeKeyInfoGridPanel();
							grid.setTitle(AON.MSG.calcDetail());
							grid.setSubTitle(key.getBoxFormatted() + " - " + script.getLabel());
							grid.addContent(info);
							gridContainer.add(grid);
							callback.showInfoPanelWidget(gridContainer);
							button.setEnabled(true);
						}
					});
				}
				
				private void showInvoiceIrpfBreakdownInfo(AonTableButton button) {
					button.setEnabled(false);
					Model111.SERVICE.getInfo(callback.getOptions().getOccam(),getModel(),script, infoKey, new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							callback.showError(AON.MSG.errorMessage());
							button.setEnabled(true);
						}
	
						@Override
						public void onSuccess(String result) {
							JsIRPFBreakdownInvoiceGridPanel grid = new JsIRPFBreakdownInvoiceGridPanel();
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
				
				private void showSalaryIrpfBreakdownInfo(AonTableButton button) {
					button.setEnabled(false);
					Model111.SERVICE.getInfo(callback.getOptions().getOccam(),getModel(),script, infoKey, new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							callback.showError(AON.MSG.errorMessage());
							button.setEnabled(true);
						}
	
						@Override
						public void onSuccess(String result) {
							JsIRPFBreakdownSalaryGridPanel grid = new JsIRPFBreakdownSalaryGridPanel();
							grid.setTitle("N\u00D3MINAS QUE AFECTAN A LA CONFECCI\u00D3N DEL MODELO "
									+ getModel().getModelFullName());
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
					Model111.SERVICE.getInfo(callback.getOptions().getOccam(),getModel(),script, infoKey, new AsyncCallback<String>() {
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
								Mod111Key key = script.getKeys()[i];
								JsIRPFComputeInfoGridPanel grid = new JsIRPFComputeInfoGridPanel() {

									@Override
									protected String resolveKey(String keyString) {
										Mod111Key key = Mod111Key.valueOf(keyString);
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
				
				private AonTableButton addButton() {
					final AonTableButton button = new AonTableButton(infoKey.getLabel(),AON.CSS.aonIconHelp());
					buttonContainer.add(button);
					return button;
				}
				
				@Override
				public Void visitInvoice() {
					if (getModel().isAlcatrazBound()) {
						final AonTableButton button = addButton();
						button.addClickHandler(event -> showInvoiceIrpfBreakdownInfo(button));
						addExcelButton().addClickHandler(event -> showExcelInfo(script, false));
					}
					return null;
				}

				@Override 
				public Void visitModelInvoiceIrpfBreakdown() {
					return visitInvoice();
				}
				
				@Override 
				public Void visitSalary() { 
					final AonTableButton button = new AonTableButton(infoKey.getLabel(),AON.CSS.aonIconInfo());
					buttonContainer.add(button);
					button.addClickHandler(event -> showSalaryIrpfBreakdownInfo(button));
					return null; 
				}
				@Override 
				public Void visitModelSalaryIrpfBreakdown() {
					return visitSalary();
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
				
				private AonTableButton addExcelButton() {
					final AonTableButton button = new AonTableButton(infoKey.getLabel() + " (Excel)" ,AON.CSS.aonIconExcel());
					button.setTabIndex(-2);
					buttonContainer.add(button);
					return button;
				}
				
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
				@Override public Void visitActAccount() { return null; }
				@Override public Void visitTitle() { return null; }
				@Override public Void visitIrpfActivity() { return null; }
				@Override public Void visitCorporate() { return null; }
			});
			table.setWidget(row, col, buttonContainer);
		}
	}

	public void calculateAndRefresh(final Model111Callback callback) {
		Model111.SERVICE.calculate(callback.getOptions().getOccam(),getModel(),
				new AsyncCallback<Mod111>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.showError(AON.MSG.errorMessage());
					}

					@Override
					public void onSuccess(Mod111 result) {
						populate(result);
					}
			
				}
			);	
	}
	
	protected void submitForm(String action) {
		diskForm.setMethod(FormPanel.METHOD_POST);
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		diskForm.clear();
		FlowPanel diskPanel = new FlowPanel();
		diskPanel.add(mod111Hidden);
		diskPanel.add(domainIdHidden);
		diskPanel.add(domainNameHidden);
		diskPanel.add(userHidden);
		diskForm.add(diskPanel);
		mod111Hidden.setValue(String.valueOf(getModel().getId()));
		domainIdHidden.setValue(String.valueOf(getCallback().getOptions().getDomain()));
		domainNameHidden.setValue(getCallback().getOptions().getDomainName());
		userHidden.setValue(getCallback().getOptions().getUser());
		diskForm.submit();
	}
	
	private void paintIdentificationTab(TabLayoutPanel tabPanel) {
		identificationData = new AonFiscalModelIdentificationPanel<>( getModel() );
		identificationData.addValueChangeHandler(event -> {
			toolbarPanel.setTitle(AonStringUtils.join(getModel().getDocument(),AonStringUtils.SPACE,getModel().getFullName()));
			markAsDirty();			
		});
		tabPanel.add(identificationData, AON.MSG.identification());
	}

	protected void decorateAdministrationTab() {
		if (admonPanel != null) {
			admonPanel.manageLinks();
		}
	}

	protected void paintDeclarationTab(TabLayoutPanel tabPanel) {
		ScrollPanel declarationScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		AonDisplayTable table = new AonDisplayTable();
		table.addStyleName(AON.CSS.aonBlockCenter());
		
		receiptBox = new AonTextBox();
		receiptBox.setVisibleLength(15);
		receiptBox.setMaxLength(13);
		receiptBox.setValue( getModel().getNumber() );
		receiptBox.setEnabled(model.isEditable());
		receiptBox.addValueChangeHandler( event -> {
			getModel().setNumber(receiptBox.getValue());
			markAsDirty();
		});

		table.addRow()
			.addCell( new Label(AON.MSG.receipt()), AON.CSS.aonTableLabel())
			.addCell(receiptBox);
		
		if (getModel().isReplacedNumberAvailable()) {
			final AonTextBox previousReceiptBox = new AonTextBox();
			previousReceiptBox.setVisibleLength(15);
			previousReceiptBox.setMaxLength(13);
			previousReceiptBox.setValue( getModel().getReplacedNumber() );
			previousReceiptBox.setEnabled(model.isEditable());
			previousReceiptBox.addValueChangeHandler( event -> {
				getModel().setReplacedNumber(previousReceiptBox.getValue());
				markAsDirty();
			});
			table.addRow()
				.addCell( new Label(AON.MSG.previousReceipt()), AON.CSS.aonTableLabel())
				.addCell(previousReceiptBox);
		}	
		container.add(table);
		declarationScrollPanel.setWidget(container);
		tabPanel.add(declarationScrollPanel, AON.MSG.declaration());
	}

	protected void decorateDeclarationTab() {
		if (receiptBox != null) {
			receiptBox.setValue( getModel().getNumber() );
		}
	}

	private void showInvoice(JsIRPFBreakdown br) {
		int invoiceId = br.getInvoice();
		Model111.SERVICE.getInvoice(getCallback().getOptions().getOccam(), invoiceId,new AsyncCallback<Invoice>() {
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

	private void showExcelInfo(IModelScript<Mod111Key> script, boolean prorrated) {
		Mod111Key key = Arrays.stream(script.getKeys())
				.filter( Objects::nonNull )
				.findAny()
				.orElse(null);
		if (key != null) {
			diskForm.setMethod(FormPanel.METHOD_POST);
			diskForm.setAction(GWT.getHostPageBaseURL() + MODEL111_BOX_INFO);
			diskForm.clear();
			FlowPanel diskPanel = new FlowPanel();
			diskPanel.add(mod111Hidden);
			diskPanel.add(domainIdHidden);
			diskPanel.add(domainNameHidden);
			diskPanel.add(userHidden);
			diskPanel.add(mod111BoxHidden);
			diskForm.add(diskPanel);
			mod111Hidden.setValue(String.valueOf(getModel().getId()));
			domainIdHidden.setValue(String.valueOf(getCallback().getOptions().getDomain()));
			domainNameHidden.setValue(getCallback().getOptions().getDomainName());
			userHidden.setValue(getCallback().getOptions().getUser());
			mod111BoxHidden.setValue(key.toString());
			diskForm.submit();
		}
	}
	
	protected void paintAdministrationTab(TabLayoutPanel tabPanel) {}

	
}
