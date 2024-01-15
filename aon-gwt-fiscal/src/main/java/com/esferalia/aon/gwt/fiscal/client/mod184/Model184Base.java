package com.esferalia.aon.gwt.fiscal.client.mod184;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAuditDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184.Model184Callback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

abstract class Model184Base extends DockLayoutPanel {

	private static final String WIDTH_200PX = "200px";
	static final String MODEL184_FILE = "/aon_gwt_fiscal/ms/Model184File";
	
	protected interface IModel184Income extends IsWidget {
		Integer getSelectedIncomeIndex();
	}
	protected interface IModel184Partner extends IsWidget {
		Integer getSelectedPartnerIndex();
	}
	
	private Mod184 mod184;
	private Model184Callback callback;
	private boolean dirty;

	protected FiscalModelAdmonPanel<Mod184, Model184ModuleOptions> admonPanel;
	protected AonTextBox receiptBox;

	protected final AonToolbar toolbarPanel = new AonToolbar(); 
	protected final AonToolbarButton newButton = new AonToolbarButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
	protected final AonToolbarButton saveButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave());
	protected final AonToolbarButton cancelButton = new AonToolbarButton(AON.MSG.cancelAction(),AON.CSS.aonIconBack());
	protected final AonToolbarButton deleteButton = new AonToolbarButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
	protected final AonToolbarButton resetButton = new AonToolbarButton(AON.MSG.resetAction(),AON.CSS.aonIconRefresh());
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

	private IModel184Income  incomeManager;
	private IModel184Partner partnerManager;
	
	protected Model184Base(Model184Callback cbk,Mod184 mod184) {
		super(Unit.PX);
		this.callback = cbk;
		
		select( mod184 );
		
		AonFiscalModelHeader modelHeader = new AonFiscalModelHeader(this.mod184);
		addNorth(modelHeader, AonFiscalModelHeader.HEIGTH);
		addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		addNorth(getDeclarationToolbarPanel(), AonToolbar.HEIGTH);
		
		setStyleName(AON.CSS.aonSelector());
	}

	public Model184Callback getCallback() {
		return callback;
	}
	protected Mod184 getModel() {
		return mod184;
	}
	public void setModel(Mod184 mod184) {
		this.mod184 = mod184;
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
	protected void select(Mod184 mod184) {
		setModel(mod184);
		refreshToolbarState();
		styleStatusLabel();
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
		toolbarPanel.add(duplicateButton);		

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
				Model184.SERVICE.saveComments( getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod184>() {
					@Override
					public void onSuccess(Mod184 result) {
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
		
		return toolbarPanel;
	}
	
	protected void save() {
		save(null);
	}
	protected void save(AsyncCallback<Mod184> cbk) {
		if (getModel().getYear() == 0) {
			throw new IllegalArgumentException(AON.MSG.requiredField(AON.MSG.fiscalYear()));
		}
		saveButton.setEnabled(false);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model184.SERVICE.save(getCallback().getOptions().getOccam(),
				getModel(), new AsyncCallback<Mod184>() {
					@Override
					public void onSuccess(Mod184 result) {
						popup.hide();
						getCallback().onSelect( result, incomeManager.getSelectedIncomeIndex(), partnerManager.getSelectedPartnerIndex() );
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
						getCallback().getOptions().getExternalCallback().onExit(mod184);
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
				getCallback().getOptions().getExternalCallback().onExit(mod184);
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
				Model184.SERVICE.delete(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						deleteButton.setEnabled(true);
						getCallback().onRemove(mod184);
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
		Model184.SERVICE.changeStatus(getCallback().getOptions().getOccam(), getModel(), FiscalStatus.FINISHED, new AsyncCallback<Mod184>() {
			@Override
			public void onSuccess(Mod184 result) {
				getCallback().onSelect(result , incomeManager.getSelectedIncomeIndex(), partnerManager.getSelectedPartnerIndex() );
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
		Model184.SERVICE.changeStatus(getCallback().getOptions().getOccam(), getModel(), FiscalStatus.SENT, new AsyncCallback<Mod184>() {
			@Override
			public void onSuccess(Mod184 result) {
				callback.onSelect(result, incomeManager.getSelectedIncomeIndex(), partnerManager.getSelectedPartnerIndex());
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
		Model184.SERVICE.changeStatus(getCallback().getOptions().getOccam(), getModel(), FiscalStatus.PENDING, new AsyncCallback<Mod184>() {
			@Override
			public void onSuccess(Mod184 result) {
				callback.onSelect(result, incomeManager.getSelectedIncomeIndex(), partnerManager.getSelectedPartnerIndex());
			}

			@Override
			public void onFailure(Throwable caught) {
				markAsPendingButton.setEnabled(true);
				callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
			}
		});
	}
	
	private void audit() {
		AonAuditDialog dialog = new AonAuditDialog();
		dialog.show(getModel());
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

	protected void paintIncomeTab(TabLayoutPanel tabPanel, Integer selectedIndex) {
		// Evaluar lo diferentes paneles por administraciuon y/o ejercicio.
		if (getModel().getYear() == 2014) {
			incomeManager = new Model184Income2014( getCallback() , getModel(), selectedIndex );	
		} else if (getModel().getYear() == 2015) {
			incomeManager = new Model184Income2015( getCallback() , getModel(), selectedIndex );
		} else if (getModel().getYear() > 2015 && getModel().getYear() < 2019) {
			incomeManager = new Model184Income2016( getCallback() , getModel(), selectedIndex );
		} else {
			incomeManager = new Model184Income2019( getCallback() , getModel(), selectedIndex );
		}
		tabPanel.add( (Widget) incomeManager,  AON.MSG.entityIncomes());
	}

	protected void paintPartnersTab(TabLayoutPanel tabPanel, Integer selectedIndex ) {
		// Evaluar lo diferentes paneles por administraciuon y/o ejercicio. 
		if (getModel().getYear() == 2014) {
			partnerManager = new Model184Partner2014( getCallback() , getModel(), selectedIndex );
		} else if (getModel().getYear() == 2015) {
			partnerManager = new Model184Partner2015( getCallback() , getModel(), selectedIndex );
		} else if (getModel().getYear() > 2015 && getModel().getYear() < 2019) {
			partnerManager = new Model184Partner2016( getCallback() , getModel(), selectedIndex );
		} else if (getModel().getYear() >= 2019 && getModel().getYear() <= 2021) {
			partnerManager = new Model184Partner2019( getCallback() , getModel(), selectedIndex );
		} else if (getModel().getYear() == 2022) {
			partnerManager = new Model184Partner2022( getCallback() , getModel(), selectedIndex );
		} else {
			partnerManager = new Model184Partner2023( getCallback() , getModel(), selectedIndex );
		}
		tabPanel.add( (Widget) partnerManager,  AON.MSG.entityPartners());
	}

	
	protected void paintEntityTab(TabLayoutPanel tabPanel) {
		ScrollPanel entityScrollPanel = new ScrollPanel();
		entityScrollPanel.setStyleName(AON.CSS.aonWidthAll());
		entityScrollPanel.addStyleName(AON.CSS.aonScrollArea());
		
		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.CSS.aonWidthAll());
		tab.addStyleName(AON.CSS.aonNowrap());
		
		tab.getColumnFormatter().setWidth(0, "150px");
		tab.getColumnFormatter().setWidth(1, WIDTH_200PX);
		tab.getColumnFormatter().setWidth(2, WIDTH_200PX);
		tab.getColumnFormatter().setWidth(3, "auto");

		tab.getCellFormatter().setStyleName(0, 0, AON.CSS.aonBorderBottom());
		tab.getCellFormatter().addStyleName(0, 0, AON.CSS.aonBold());
		tab.getFlexCellFormatter().setColSpan(0, 0, 4);
		tab.setWidget(0, 0, new InlineLabel(AON.MSG.localEntities()));

		tab.setWidget(1, 0, new Model184SmallerLabel(AON.MSG.entityType()));
		ListBox entityType = new ListBox();
		entityType.addItem(" - ","");
		entityType.addItem("1 - Sociedad civil.","1");
		entityType.addItem("2 - Comunidad de bienes.","2");
		entityType.addItem("3 - Herencia yacente.","3");
		entityType.addItem("4 - Comunidad de propietarios.","4");
		entityType.addItem("5 - Otros","5");
		entityType.setWidth(WIDTH_200PX);
		entityType.setSelectedIndex(AonNumberUtils.toint(getModel().getEntityType()));
		entityType.addChangeHandler(event -> {
			getModel().setEntityType(entityType.getSelectedValue());
			markAsDirty();
		});
		tab.setWidget(1, 1, entityType);
		
		tab.setWidget(1, 2, new Model184SmallerLabel(AON.MSG.mainActivity()));
		ListBox mainActivity = new ListBox();
		mainActivity.addItem(" - ","");
		mainActivity.addItem("1 - Actividad empresarial.","1");
		mainActivity.addItem("2 - Actividad profesional.","2");
		mainActivity.addItem("3 - Tenencia y administraci\u00F3n de bienes inmuebles.","3");
		mainActivity.addItem("4 - Tenencia y administraci\u00F3n de valores o activos financieros.","4");
		mainActivity.addItem("5 - Otras.","5");
		mainActivity.setWidth("250px");
		mainActivity.setSelectedIndex(AonNumberUtils.toint(getModel().getMainActivity()));
		mainActivity.addChangeHandler(event -> {
			getModel().setMainActivity(mainActivity.getSelectedValue());
			markAsDirty();
		});
		tab.setWidget(1, 3, mainActivity);
		
		tab.getCellFormatter().setStyleName(2, 0, AON.CSS.aonBorderBottom());
		tab.getCellFormatter().addStyleName(2, 0, AON.CSS.aonBold());
		tab.getFlexCellFormatter().setColSpan(2, 0, 4);
		tab.setWidget(2, 0, new InlineLabel(AON.MSG.foreignEntities()));
		
		tab.setWidget(3, 0, new Model184SmallerLabel(AON.MSG.entityType()));
		ListBox foreignEntityType = new ListBox();
		foreignEntityType.addItem(" - ","");
		foreignEntityType.addItem("1- Corporaci\u00F3n, asociaci\u00F3n o ente con personalidad jur\u00EDdica propia.","1");
		foreignEntityType.addItem("2- Corporaci\u00F3n o ente independiente pero sin personalidad jur\u00EDdica propia.","2");
		foreignEntityType.addItem("3- Conjunto unitario de bienes pertenecientes a dos o m\u00E1s personas en com\u00FAn sin personalidad jur\u00EDdica propia.","3");
		foreignEntityType.addItem("4- Otras","4");
		foreignEntityType.setWidth(WIDTH_200PX);
		foreignEntityType.setSelectedIndex(AonNumberUtils.toint(getModel().getForeignEntityType()));
		foreignEntityType.addChangeHandler(event -> {
			getModel().setForeignEntityType(foreignEntityType.getSelectedValue());
			markAsDirty();
		});
		tab.setWidget(3, 1, foreignEntityType);

		tab.setWidget(3, 2, new Model184SmallerLabel(AON.MSG.entityType()));
		ListBox foreignObject = new ListBox();
		foreignObject.addItem(" - ","");
		foreignObject.addItem("A - Actividad nat. empresarial.","A");
		foreignObject.addItem("B - Actividad nat. profesional.","B");
		foreignObject.setWidth(WIDTH_200PX);
		foreignObject.setSelectedIndex(AonNumberUtils.toint(getModel().getForeignObject()));
		foreignObject.addChangeHandler(event -> {
			getModel().setForeignEntityType(foreignObject.getSelectedValue());
			markAsDirty();
		});
		tab.setWidget(3, 3, foreignObject);
		
		tab.setWidget(4, 0, new Model184SmallerLabel(AON.MSG.country()));
		CountryListBox country = new CountryListBox();
		country.setValue( Country.safeValueOf( getModel().getCountry() ));
		country.addChangeHandler(event -> {
			getModel().setCountry(country.getSelectedValue());
			markAsDirty();
		});
		tab.setWidget(4, 1, country);
		
		tab.setWidget(4, 2, new Model184SmallerLabel(AON.MSG.residentPercent()));
		AonDoubleBox residentPercent = new AonDoubleBox();
		residentPercent.setValue(getModel().getResidentPercent());
		residentPercent.addValueChangeHandler(event -> {
			getModel().setResidentPercent(residentPercent.getValue());
			markAsDirty();
		});
		tab.setWidget(4, 3, residentPercent);
		
		CheckBox taxIS = new CheckBox(AON.MSG.isTax());
		taxIS.setValue(getModel().isTaxIS());
		taxIS.addClickHandler(event -> {
			getModel().setTaxIS(taxIS.getValue());
			markAsDirty();
		});
		tab.getFlexCellFormatter().setColSpan(5, 0, 2);
		tab.setWidget(5, 0, taxIS);
		
		tab.setWidget(5, 1, new Model184SmallerLabel(AON.MSG.netAmount()));
		AonDoubleBox netSalesAmount = new AonDoubleBox();
		netSalesAmount.setValue(getModel().getNetSalesAmount());
		netSalesAmount.addValueChangeHandler(event -> {
			getModel().setNetSalesAmount(netSalesAmount.getValue());
			markAsDirty();
		});
		tab.setWidget(5, 2, netSalesAmount);
		
		tab.setWidget(6, 0, new Model184SmallerLabel(AON.MSG.lrDocument()));
		AonDocumentTextBox lrDocument = new AonDocumentTextBox();
		lrDocument.setValue(getModel().getLrDocument());
		lrDocument.addValueChangeHandler(event -> {
			getModel().setLrDocument(lrDocument.getValue());
			markAsDirty();
		});
		tab.setWidget(6, 1, lrDocument);
		
		tab.setWidget(6, 2, new Model184SmallerLabel(AON.MSG.lrName()));
		AonTextBox lrName = new AonTextBox();
		lrName.setMaxLength(40);
		lrName.setVisibleLength(40);
		lrName.setValue(getModel().getLrName());
		lrName.addValueChangeHandler(event -> {
			getModel().setLrName(lrName.getValue());
			markAsDirty();
		});
		tab.setWidget(6, 3, lrName);

		entityScrollPanel.setWidget(tab);
		tabPanel.add(entityScrollPanel, AON.MSG.entity() );
	}

	
}
