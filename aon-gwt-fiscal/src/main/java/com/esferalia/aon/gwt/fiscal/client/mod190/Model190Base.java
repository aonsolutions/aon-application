package com.esferalia.aon.gwt.fiscal.client.mod190;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAuditDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190.Model190Callback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;

abstract class Model190Base extends DockLayoutPanel {

	private static final String MODEL190_DRAFT = "/aon_gwt_fiscal/ms/Model190Draft";
	protected static final String MODEL190_FILE = "/aon_gwt_fiscal/ms/Model190File";
	
//	private static final String MODEL190_PRINT = "/aon_gwt_fiscal/ms/Model190Print";
// 	private static final String MODEL190_CERTIFICATE_PRINT = "/aon_gwt_fiscal/ms/Model190CertificatePrint";
	
	protected interface IModel190Detail extends IsWidget {
		Integer getSelectedPerceptorIndex();
	}
/*
	protected class Model190BaseCallback implements IModel190Callback {
		private Model190Callback cbk;
		
		protected Model190BaseCallback( Model190Callback cbk ) {
			this.cbk = cbk;
		}
		public Mod190 getModel() {
			return Model190Base.this.getModel();
		}
		@Override
		public void onAccept(Mod190 mod190) {
			cbk.onAccept(mod190);
		}
		@Override	
		public void onCancel() {
			cbk.onCancel();
		}
		@Override	
		public void onSelect(Model190ModuleOptions options,Mod190 mod190, Integer selectedIndex) {
			cbk.onSelect(options, mod190, selectedIndex);
		}
		@Override
		public void showError(String msg) {
			cbk.showError(msg);
		}
		@Override
		public void cleanErrorPanel() {
			cbk.cleanErrorPanel();
		}
		@Override
		public void onNew() {
			cbk.onNew();
		}
		@Override
		public void onReset(Model190ModuleOptions options, Mod190 mod190) {
			cbk.onReset(options, mod190);
		}
		@Override
		public void onDuplicate(Model190ModuleOptions options, int id) {
			cbk.onDuplicate( options, id );
		}
		@Override
		public Model190ModuleOptions getOptions() {
			return cbk.getOptions();
		}

	}
*/	
	
	private Mod190 mod190;
	private Model190Callback callback;
	private boolean dirty;

	protected FiscalModelAdmonPanel<Mod190, Model190ModuleOptions> admonPanel;
	protected AonTextBox receiptBox;

	protected final AonToolbar toolbarPanel = new AonToolbar(); 
	protected final AonToolbarButton newButton = new AonToolbarButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
	protected final AonToolbarButton saveButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave());
	protected final AonToolbarButton cancelButton = new AonToolbarButton(AON.MSG.cancelAction(),AON.CSS.aonIconBack());
	protected final AonToolbarButton deleteButton = new AonToolbarButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
	protected final AonToolbarButton resetButton = new AonToolbarButton(AON.MSG.resetAction(),AON.CSS.aonIconRefresh());
	protected final AonToolbarButton printButton = new AonToolbarButton(AON.MSG.draft(),AON.CSS.aonIconExcel());
	protected final AonToolbarButton markAsPendingButton = new AonToolbarButton(AON.MSG.reopen(),AON.CSS.aonIconModelReopen());
	protected final AonToolbarButton markAsFinishedButton = new AonToolbarButton(AON.MSG.finish(),AON.CSS.aonIconModelFinish());
	protected final AonToolbarButton markAsSentButton = new AonToolbarButton(AON.MSG.markAsSent(),AON.CSS.aonIconModelSent());
	protected final AonToolbarButton duplicateButton = new AonToolbarButton(AON.MSG.duplicate(),AON.CSS.aonIconCopy());
	protected final AonToolbarButton commentsButton = new AonToolbarButton(AON.MSG.comments(), AON.CSS.aonIconNoComments());
	protected final AonToolbarButton auditButton = new AonToolbarButton(AON.MSG.audit(),AON.CSS.aonIconAudit());
	
	protected final AonToolbar decToolbar = new AonToolbar();
	protected final InlineLabel dirtyLabel = new InlineLabel();
	protected final InlineLabel replacedLabel = new InlineLabel();
	protected final Label statusLabel = new Label();

	
	protected FormPanel diskForm = new FormPanel("_blank");
	protected Hidden mod190Hidden = new Hidden("mod190");
	protected Hidden domainIdHidden = new Hidden("domainId");
	protected Hidden domainNameHidden = new Hidden("domainName");
	protected Hidden userHidden = new Hidden("user");
	
	private IModel190Detail detailManager;
	
	protected Model190Base(Model190Callback cbk,Mod190 mod190) {
		super(Unit.PX);
		this.callback = cbk;
		
		select( mod190 );
		
		AonFiscalModelHeader modelHeader = new AonFiscalModelHeader(this.mod190);
		addNorth(modelHeader, AonFiscalModelHeader.HEIGTH);
		addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		addNorth(getDeclarationToolbarPanel(), AonToolbar.HEIGTH);
		
		setStyleName(AON.CSS.aonSelector());
	}
	
	public Model190Callback getCallback() {
		return callback;
	}
	protected Mod190 getModel() {
		return mod190;
	}
	public void setModel(Mod190 mod190) {
		this.mod190 = mod190;
	}

	public IModel190Detail getDetailManager() {
		return detailManager;
	}
	public void setDetailManager(IModel190Detail detailManager) {
		this.detailManager = detailManager;
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
	protected void select(Mod190 mod190) {
		setModel(mod190);
		refreshToolbarState( );
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
		
		resetButton.addClickHandler( event -> getCallback().onReset(getCallback().getOptions(),getModel()));
		toolbarPanel.add(resetButton);		
		
		duplicateButton.addClickHandler( event -> getCallback().onDuplicate(getCallback().getOptions(),getModel().getId()));
		toolbarPanel.add(resetButton);		

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
				Model190.SERVICE.saveComments( getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod190>() {
					@Override
					public void onSuccess(Mod190 result) {
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
	
	protected void save() {
		save(null);
	}
	protected void save(AsyncCallback<Mod190> cbk) {
		if (getModel().getYear() == 0) {
			throw new IllegalArgumentException(AON.MSG.requiredField(AON.MSG.fiscalYear()));
		}
		saveButton.setEnabled(false);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model190.SERVICE.save(getCallback().getOptions().getOccam(),
				getModel(), new AsyncCallback<Mod190>() {
					@Override
					public void onSuccess(Mod190 result) {
						popup.hide();
						getCallback().onSelect( result, detailManager.getSelectedPerceptorIndex() );
						if (cbk != null) cbk.onSuccess(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
					}
				});
	}

	private void cancel() {
		cancelButton.setEnabled(false);
		if (isDirty()) {
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.confirmDeclarationCancelAction(), new AonConfirmDialogCallback() {
				
				@Override
				public void onAccept() {
					if (getCallback().getOptions().isBackButtonVisible() && getCallback().getOptions().hasExternalCallback()) {
						getCallback().getOptions().getExternalCallback().onExit(mod190);
					} else {
						getCallback().onCancel( getModel() );
					}
				}
				
				@Override
				public void onCancel() {
					cancelButton.setEnabled(true);
				}
			});
		} else {
			if (getCallback().getOptions().isBackButtonVisible() && getCallback().getOptions().hasExternalCallback()) {
				getCallback().getOptions().getExternalCallback().onExit(mod190);
			} else {
				getCallback().onCancel( getModel() );
			}
		}
	}
	
	private void delete() {
		deleteButton.setEnabled(false);
		AonConfirmDialog cd = new AonConfirmDialog();
		cd.confirm(AON.MSG.confirmDeclarationDeleteAction(), new AonConfirmDialogCallback() {

			@Override
			public void onAccept() {
				Model190.SERVICE.delete(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						deleteButton.setEnabled(true);
						getCallback().onCancel( getModel() );
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
	
	private AonToolbar getDeclarationToolbarPanel() {
		
		markAsFinishedButton.setText(markAsFinishedButton.getTitle());
		markAsFinishedButton.addClickHandler( event -> markAsFinished());
		decToolbar.add(markAsFinishedButton);

		markAsSentButton.setText(markAsSentButton.getTitle());
		markAsSentButton.addClickHandler( event -> markAsSent());
		decToolbar.add(markAsSentButton);

		markAsPendingButton.setText(markAsPendingButton.getTitle());
		markAsPendingButton.addClickHandler( event -> markAsPending());
		decToolbar.add(markAsPendingButton);

		FlowPanel marksPanels = new FlowPanel();
		marksPanels.setStyleName(AON.CSS.aonFlexBlock());
		
		dirtyLabel.setStyleName(AON.CSS.aonIconLabel());
		dirtyLabel.addStyleName(AON.CSS.aonIconDirty());
		dirtyLabel.setTitle("Cambios sin guardar");
		dirtyLabel.getElement().getStyle().setWidth(10, Unit.PX);
		dirtyLabel.getElement().getStyle().setHeight(10, Unit.PX);
		marksPanels.add(dirtyLabel);
		
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
		styleStatusLabel();
		
		decToolbar.getMessagePanel().add(marksPanels);
		
		decToolbar.setTitle(statusLabel);
		return decToolbar;
	}
	
	
	private void markAsFinished() {
		markAsFinishedButton.setEnabled(false);
		Model190.SERVICE.changeStatus(getCallback().getOptions().getOccam(), getModel(), FiscalStatus.FINISHED, new AsyncCallback<Mod190>() {
			@Override
			public void onSuccess(Mod190 result) {
				getCallback().onSelect(result , detailManager.getSelectedPerceptorIndex() );
			}
			
			@Override
			public void onFailure(Throwable caught) {
				markAsFinishedButton.setEnabled(true);
				callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
			}
		});
	}
	private void markAsSent() {
		markAsSentButton.setEnabled(false);
		Model190.SERVICE.changeStatus(getCallback().getOptions().getOccam(), getModel(), FiscalStatus.SENT, new AsyncCallback<Mod190>() {
			@Override
			public void onSuccess(Mod190 result) {
				callback.onSelect(result, detailManager.getSelectedPerceptorIndex());
			}

			@Override
			public void onFailure(Throwable caught) {
				markAsSentButton.setEnabled(true);
				callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
			}
		});
	}
	private void markAsPending() {
		markAsPendingButton.setEnabled(false);
		Model190.SERVICE.changeStatus(getCallback().getOptions().getOccam(), getModel(), FiscalStatus.PENDING, new AsyncCallback<Mod190>() {
			@Override
			public void onSuccess(Mod190 result) {
				callback.onSelect(result, detailManager.getSelectedPerceptorIndex());
			}

			@Override
			public void onFailure(Throwable caught) {
				markAsPendingButton.setEnabled(true);
				callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
			}
		});
	}
	
	private void print() {
		if (isDirty()) {
			new AonConfirmDialog().confirm(AON.MSG.draftPrint(),AON.MSG.draftPrintNote() 
					, new AonConfirmDialogCallback() {
					
					@Override
					public void onAccept() {
						submitForm(MODEL190_DRAFT);
					}
	
					@Override
					public void onCancel() {
						// Nothing
					}
				});
		} else {
			submitForm(MODEL190_DRAFT);
		}
	}
	
	private void audit() {
		AonAuditDialog dialog = new AonAuditDialog();
		dialog.show(getModel());
	}
	
	protected void submitForm(String action) {
		diskForm.setMethod(FormPanel.METHOD_POST);
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		diskForm.clear();
		FlowPanel diskPanel = new FlowPanel();
		diskPanel.add(mod190Hidden);
		diskPanel.add(domainIdHidden);
		diskPanel.add(domainNameHidden);
		diskPanel.add(userHidden);
		diskForm.add(diskPanel);
		mod190Hidden.setValue(String.valueOf(getModel().getId()));
		domainIdHidden.setValue(String.valueOf(getCallback().getOptions().getDomain()));
		domainNameHidden.setValue(getCallback().getOptions().getDomainName());
		userHidden.setValue(getCallback().getOptions().getUser());
		diskForm.submit();
	}
	
	protected void styleDirtyLabel() {
		dirtyLabel.setVisible(isDirty());
	}
	
	protected void styleStatusLabel() {
		statusLabel.setText(getModel().getStatus().getName());
		statusLabel.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB( getModel().getStatus() ));
		statusLabel.getElement().getStyle().setColor(FiscalModelUtils.getStatusFrgColorRGB( getModel().getStatus() ));
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

	private void refreshToolbarState() {
		toolbarPanel.setTitle(AonStringUtils.join(getModel().getDocument(),AonStringUtils.SPACE,getModel().getFullName()));
		newButton.setVisible(!getModel().isNew() 
				&& !getCallback().getOptions().isBackButtonVisible() 
				&& !getCallback().getOptions().hasExternalCallback());
		saveButton.setVisible(!getModel().isFinished() && !getModel().isSent());
		deleteButton.setVisible(!getModel().isNew() && !getModel().isFinished() && !getModel().isSent());
		resetButton.setVisible(!getModel().isNew() && !getModel().isFinished() && !getModel().isSent());
		cancelButton.setVisible(true);
		markAsPendingButton.setVisible(!getModel().isNew() &&
			(getModel().getStatus() == FiscalStatus.FINISHED 
			|| getModel().getStatus() == FiscalStatus.BATCHED
			|| getModel().getStatus() == FiscalStatus.SENT
			|| getModel().getStatus() == FiscalStatus.BLOCKED));
		markAsFinishedButton.setVisible(!getModel().isNew() &&
			(getModel().getStatus() == FiscalStatus.PENDING 
			|| getModel().getStatus() == FiscalStatus.MISSING));
		markAsSentButton.setVisible(!getModel().isNew() &&
			(getModel().getStatus() == FiscalStatus.FINISHED));
		duplicateButton.setVisible(!getModel().isNew());
		auditButton.setVisible(!getModel().isNew());
	}
	private void identificationLabelChanged() {
		toolbarPanel.setTitle(AonStringUtils.join(getModel().getDocument(),AonStringUtils.SPACE,getModel().getFullName()));
	}

	// *********************************************************************
	// *********************************************************************
	// *********************************************************************


	protected void paintDeclarationTab(TabLayoutPanel tabPanel) {
		ScrollPanel declarationScrollPanel = new ScrollPanel();
		declarationScrollPanel.setStyleName(AON.CSS.aonWidthAll());
		declarationScrollPanel.addStyleName(AON.CSS.aonScrollArea());
		
		FlexTable table = new FlexTable();
		table.setStyleName(AON.CSS.aonTable());
		table.addStyleName(AON.CSS.aonWidthAlmostAll());
		table.addStyleName(AON.CSS.aonBlockCenter());
		table.getColumnFormatter().setWidth(0, "300px");
		
		table.getColumnFormatter().setWidth(1, "auto");
		
		
		
		table.setWidget( 0, 0, new InlineLabel(AON.MSG.document()));
		table.getCellFormatter().setStyleName(0,0, AON.CSS.aonTableLabel());
		AonDocumentTextBox document = new AonDocumentTextBox();
		document.setValue(getModel().getDocument());
		document.addValueChangeHandler( event -> {
			getModel().setDocument(document.getValue());
			identificationLabelChanged();
			markAsDirty();
		});
		table.setWidget(0, 1, document);
		
		table.setWidget( 1, 0, new InlineLabel(AON.MSG.enterpriseName()));
		table.getCellFormatter().setStyleName(1,0, AON.CSS.aonTableLabel());
		AonTextBox name = new AonTextBox();
		name.setStyleName(AON.CSS.aonInputText());
		name.setVisibleLength(45);
		name.setMaxLength(45);
		name.setValue(getModel().getName());
		name.addValueChangeHandler( event -> {
			getModel().setName(name.getValue());
			identificationLabelChanged();
			markAsDirty();
		});
		table.setWidget(1, 1, name);
		
		table.setWidget( 2, 0, new InlineLabel(AON.MSG.contactPerson()));
		table.getCellFormatter().setStyleName(2,0, AON.CSS.aonTableLabel());
		AonTextBox contactPerson = new AonTextBox();
		contactPerson.setStyleName(AON.CSS.aonInputText());
		contactPerson.setMaxLength(40);
		contactPerson.setVisibleLength(30);
		contactPerson.setValue(getModel().getContactPerson());
		contactPerson.addValueChangeHandler( event -> {
			getModel().setContactPerson(contactPerson.getValue());
			markAsDirty();
		});
		table.setWidget(2, 1, contactPerson);

		table.setWidget( 3, 0, new InlineLabel(AON.MSG.contactPhone()));
		table.getCellFormatter().setStyleName(3,0, AON.CSS.aonTableLabel());
		AonTextBox contactPhone = new AonTextBox();
		contactPhone.setStyleName(AON.CSS.aonInputText());
		contactPhone.setMaxLength(9);
		contactPhone.setVisibleLength(10);
		contactPhone.setValue(getModel().getContactPhone());
		contactPhone.addValueChangeHandler( event -> {
			getModel().setContactPhone(contactPhone.getValue());
			markAsDirty();
		});
		table.setWidget(3, 1, contactPhone);
		

		table.setWidget( 4, 0, new InlineLabel(AON.MSG.contactMail()));
		table.getCellFormatter().setStyleName(4,0, AON.CSS.aonTableLabel());
		AonTextBox contactMail = new AonTextBox();
		contactMail.setStyleName(AON.CSS.aonInputText());
		contactMail.setMaxLength(50);
		contactMail.setVisibleLength(50);
		contactMail.setValue(getModel().getContactMail());
		contactMail.addValueChangeHandler( event -> {
			getModel().setContactMail(contactMail.getValue());
			markAsDirty();
		});
		table.setWidget(4, 1, contactMail);
		
		table.setWidget( 5, 0, new InlineLabel(AON.MSG.receipt()));
		table.getCellFormatter().setStyleName(5,0, AON.CSS.aonTableLabel());
		receiptBox = new AonTextBox();
		receiptBox.setStyleName(AON.CSS.aonInputText());
		receiptBox.setMaxLength(13);
		receiptBox.setVisibleLength(13);
		receiptBox.setEnabled(getModel().isAEAT());
		receiptBox.setValue(getModel().getReceipt());
		receiptBox.addValueChangeHandler(event -> {
			getModel().setReceipt(receiptBox.getValue());
			markAsDirty();
		});
		table.setWidget(5, 1, receiptBox);

		table.setWidget( 6, 0, new InlineLabel(AON.MSG.previousDeclaration()));
		table.getCellFormatter().setStyleName(6,0, AON.CSS.aonTableLabel());
		AonTextBox replaced = new AonTextBox();
		replaced.setStyleName(AON.CSS.aonInputText());
		replaced.setMaxLength(13);
		replaced.setVisibleLength(13);
		replaced.setEnabled(getModel().isAEAT() && (getModel().isComplementary() || getModel().isReplacement()));
		replaced.setValue(getModel().getReplacedReceipt());
		replaced.addValueChangeHandler(event -> {
			getModel().setReplacedReceipt(replaced.getValue());
			markAsDirty();
		});
		table.setWidget(6, 1, replaced);
		
		declarationScrollPanel.setWidget(table);
		tabPanel.add(declarationScrollPanel, AON.MSG.declaration());
	}
	
	protected void decorateDeclarationTab() {
		if (receiptBox != null) {
			receiptBox.setValue( getModel().getReceipt() );
		}
	}
	
/*	
	protected FlowPanel getInformationPanel() {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.CSS.aonScrollArea());
		panel.addStyleName(AON.CSS.aonWidthAll());
		panel.addStyleName(AON.CSS.aonMarginTop());
		panel.addStyleName(AON.CSS.aonPaddingTop());
		panel.addStyleName(AON.CSS.aonPaddingLeft());
		 
		FlexTable tab = new FlexTable();
		tab.getColumnFormatter().setWidth(0, "30px");
		tab.getColumnFormatter().setWidth(1
				, "auto");
		tab.setStyleName(AON.CSS.aonWidth90Percent());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		tab.addStyleName(AON.CSS.aonPanelGrid());
		Label title = new Label("Informaci\u00F3n \u00FAtil para la confecci\u00F3n del modelo");
		tab.getFlexCellFormatter().setColSpan(0, 0, 2);
		tab.getCellFormatter().setStyleName(0, 0, AON.CSS.aonPanelGridEven());
		tab.getCellFormatter().addStyleName(0, 0, AON.CSS.aonMarginTop());
		tab.getCellFormatter().addStyleName(0, 0, AON.CSS.aonFiscalModelTableHeaderTitle());
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(getModel().getAdministration()));
		tab.setWidget(0, 0, title);
		
		int row = 1;
		for (Pair<String, String> pair : getInformationLinks()) {
			Label icon = new Label();
			icon.addStyleName(FiscalModelUtils.getAdministrationIconStyle(getModel().getAdministration()));
			tab.setWidget(row, 0, icon );
			tab.getCellFormatter().setStyleName(row, 0, AON.CSS.aonPanelGridEven());

			FlowPanel p = new FlowPanel();
			p.setStyleName(AON.CSS.aonPadding2());
			Anchor a = new Anchor(pair.getLeft(),pair.getRight(), "_blank");
			a.setStyleName(AON.CSS.aonPaddingLeft());
			p.add(a);
			tab.setWidget(row, 1, p );
			tab.getCellFormatter().setStyleName(row, 1, AON.CSS.aonPanelGridEven());
			row++;
		}
		panel.add(tab);
		return panel;
	}

	protected FlowPanel getAdministrationPanel(Model190ModuleOptions options,Model190Callback cbk) {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.CSS.aonScrollArea());
		panel.addStyleName(AON.CSS.aonWidthAll());
		panel.addStyleName(AON.CSS.aonMarginTop());
		panel.addStyleName(AON.CSS.aonPaddingTop());
		panel.addStyleName(AON.CSS.aonPaddingLeft());
		 
		FlexTable tab = new FlexTable();
		tab.getColumnFormatter().setWidth(0, "30px");
		tab.getColumnFormatter().setWidth(1
				, "auto");
		tab.setStyleName(AON.CSS.aonWidth90Percent());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		tab.addStyleName(AON.CSS.aonPanelGrid());
		Label title = new Label("Presentaci\u00F3n del modelo");
		tab.getFlexCellFormatter().setColSpan(0, 0, 2);
		tab.getCellFormatter().setStyleName(0, 0, AON.CSS.aonPanelGridEven());
		tab.getCellFormatter().addStyleName(0, 0, AON.CSS.aonMarginTop());
		tab.getCellFormatter().addStyleName(0, 0, AON.CSS.aonFiscalModelTableHeaderTitle());
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(getModel().getAdministration()));
		tab.setWidget(0, 0, title);
		
		int row = 1;

		Label icon1 = new Label();
		icon1.addStyleName(FiscalModelUtils.getAdministrationIconStyle(getModel().getAdministration()));
		tab.setWidget(row, 0, icon1 );
		tab.getCellFormatter().setStyleName(row, 0, AON.CSS.aonPanelGridEven());
		FlowPanel p1 = new FlowPanel();
		p1.setStyleName(AON.CSS.aonPadding2());
		Button button1 = new Button("Descargar fichero para su presentaci\u00F3n");
		button1.setStyleName(AON.CSS.aonPaddingLeft());
		button1.addStyleName(AON.CSS.aonBorderNone());
		button1.addStyleName(AON.CSS.aonEvenBackground());
		button1.addStyleName(AON.CSS.aonClickable());
		button1.addClickHandler( event -> {
			if (getModel().isFinished() || getModel().isSent()) {
				submitForm(MODEL190_FILE);
			} else {
				getCallback().showError("Para generar el fichero debe finalizar la confecci\u00F3n del modelo.");
			}
		});
		p1.add(button1);
		tab.setWidget(row, 1, p1 );
		tab.getCellFormatter().setStyleName(row, 1, AON.CSS.aonPanelGridEven());
		row++;
		
		Label icon3 = new Label();
		icon3.addStyleName(FiscalModelUtils.getAdministrationIconStyle(getModel().getAdministration()));
		tab.setWidget(row, 0, icon3 );
		tab.getCellFormatter().setStyleName(row, 0, AON.CSS.aonPanelGridEven());
		FlowPanel p3 = new FlowPanel();
		p3.setStyleName(AON.CSS.aonPadding2());
		Button button3 = new Button("Validar e imprimir (PDF) via Agencia Tributaria (a partir de los datos guardados).");
		button3.setStyleName(AON.CSS.aonPaddingLeft());
		button3.addStyleName(AON.CSS.aonBorderNone());
		button3.addStyleName(AON.CSS.aonEvenBackground());
		button3.addStyleName(AON.CSS.aonClickable());
		button3.addClickHandler( event -> submitForm(MODEL190_PRINT));
		p3.add(button3);
		tab.setWidget(row, 1, p3 );
		tab.getCellFormatter().setStyleName(row, 1, AON.CSS.aonPanelGridEven());
		row++;
		panel.add(tab);
		
		FlexTable tab2 = new FlexTable();
		tab2.getColumnFormatter().setWidth(0, "30px");
		tab2.getColumnFormatter().setWidth(1, "auto");
		tab2.setStyleName(AON.CSS.aonWidth90Percent());
		tab2.addStyleName(AON.CSS.aonBlockCenter());
		tab2.addStyleName(AON.CSS.aonPanelGrid());
		tab2.addStyleName(AON.CSS.aonMarginTop());
		Label title2 = new Label("Otros");
		tab2.getFlexCellFormatter().setColSpan(0, 0, 2);
		tab2.getCellFormatter().setStyleName(0, 0, AON.CSS.aonPanelGridEven());
		tab2.getCellFormatter().addStyleName(0, 0, AON.CSS.aonMarginTop());
		tab2.getCellFormatter().addStyleName(0, 0, AON.CSS.aonFiscalModelTableHeaderTitle());
		tab2.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(getModel().getAdministration()));
		tab2.setWidget(0, 0, title2);
		
		row = 1;
		Label icon2 = new Label();
		icon2.addStyleName(AON.CSS.aonIconPdf());
		tab2.setWidget(row, 0, icon2 );
		tab2.getCellFormatter().setStyleName(row, 0, AON.CSS.aonPanelGridEven());
		FlowPanel p2 = new FlowPanel();
		p2.setStyleName(AON.CSS.aonPadding2());
		Button button2 = new Button("Certificado de retenciones e ingresos a cuenta del I.R.P.F.");
		button2.setStyleName(AON.CSS.aonPaddingLeft());
		button2.addStyleName(AON.CSS.aonBorderNone());
		button2.addStyleName(AON.CSS.aonEvenBackground());
		button2.addStyleName(AON.CSS.aonClickable());
		button2.addClickHandler( event -> {
			if (getModel().isFinished() || getModel().isSent()) {
				submitForm(MODEL190_CERTIFICATE_PRINT);
			} else {
				getCallback().showError("Para imprimir los certificados, debe finalizar la confecci\u00F3n del modelo.");
			}
		});
		p2.add(button2);
		tab2.setWidget(row, 1, p2 );
		tab2.getCellFormatter().setStyleName(row, 1, AON.CSS.aonPanelGridEven());
		row++;
		
		panel.add(tab2);
		
		return panel;
	}
	protected abstract LinkedList<Pair<String, String>> getInformationLinks();
	protected abstract void paintPerceptorsTab(TabLayoutPanel tabPanel, Integer selectedIndex);
*/
	
}
