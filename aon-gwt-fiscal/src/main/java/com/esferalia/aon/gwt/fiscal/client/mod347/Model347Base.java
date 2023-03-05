package com.esferalia.aon.gwt.fiscal.client.mod347;

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
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347.Model347Callback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.type.Administration;
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
import com.google.gwt.user.client.ui.Widget;

abstract class Model347Base extends DockLayoutPanel {

	static final String MODEL347_FILE = "/aon_gwt_fiscal/ms/Model347File";
	static final String MODEL347_PRINT = "/aon_gwt_fiscal/ms/Model347Print";
	static final String MODEL347_MERGE_MAIL = "/aon_gwt_fiscal/ms/Model347MergeMail";

	protected interface IModel347Declared extends IsWidget {
		Integer getSelectedDeclaredIndex();
	}

	protected interface IModel347Asset extends IsWidget {
		Integer getSelectedAssetIndex();
	}

	private Mod347 mod347;
	private Model347Callback callback;
	private boolean dirty;

	protected FiscalModelAdmonPanel<Mod347, Model347ModuleOptions> admonPanel;
	protected AonTextBox receiptBox;

	protected final AonToolbar toolbarPanel = new AonToolbar(); 
	protected final AonToolbarButton newButton = new AonToolbarButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
	protected final AonToolbarButton saveButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave());
	protected final AonToolbarButton cancelButton = new AonToolbarButton(AON.MSG.cancelAction(),AON.CSS.aonIconBack());
	protected final AonToolbarButton deleteButton = new AonToolbarButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
	protected final AonToolbarButton resetButton = new AonToolbarButton(AON.MSG.resetAction(),AON.CSS.aonIconRefresh());
	protected final AonToolbarButton printButton = new AonToolbarButton(AON.MSG.draft(),AON.CSS.aonIconExcel());
	protected final AonToolbarButton mailMergeButton = new AonToolbarButton("CSV (Combinar corresp.)",AON.CSS.aonIconEmail());
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
	protected Hidden mod347Hidden = new Hidden("mod347");
	protected Hidden domainIdHidden = new Hidden("domainId");
	protected Hidden domainNameHidden = new Hidden("domainName");
	protected Hidden userHidden = new Hidden("user");

	private IModel347Declared declaredManager;
	private IModel347Asset assetManager;
	private Integer tabPanelIndex;

	Model347Base(Model347Callback cbk, Mod347 mod347) {
		super(Unit.PX);
		this.callback = cbk;

		select(mod347);

		AonFiscalModelHeader modelHeader = new AonFiscalModelHeader(this.mod347);
		addNorth(modelHeader, AonFiscalModelHeader.HEIGTH);
		addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		addNorth(getDeclarationToolbarPanel(), AonToolbar.HEIGTH);
		
		setStyleName(AON.CSS.aonSelector());
	}

	public Model347Callback getCallback() {
		return callback;
	}
	protected Mod347 getModel() {
		return mod347;
	}
	public void setModel(Mod347 mod347) {
		this.mod347 = mod347;
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
	protected void select(Mod347 mod347) {
		setModel(mod347);
		refreshToolbarState();
		styleStatusLabel();
	}
	
	private Integer getTabPanelIndex() {
		return tabPanelIndex;
	}
	public void setTabPanelIndex(Integer tabPanelIndex) {
		this.tabPanelIndex = tabPanelIndex;
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
		
		resetButton.addClickHandler( event -> reset());
		toolbarPanel.add(resetButton);		
		
		duplicateButton.addClickHandler( event -> getCallback().onDuplicate(getCallback().getOptions(),getModel().getId()));
		toolbarPanel.add(duplicateButton);		

		printButton.addClickHandler( event ->  print());
		toolbarPanel.add(printButton);
		
		mailMergeButton.addClickHandler( event ->  mailMerge());
		toolbarPanel.add(mailMergeButton);

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
				Model347.SERVICE.saveComments( getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod347>() {
					@Override
					public void onSuccess(Mod347 result) {
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

	private void cancel() {
		cancelButton.setEnabled(false);
		if (isDirty()) {
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.confirmDeclarationCancelAction(), new AonConfirmDialogCallback() {
				
				@Override
				public void onAccept() {
					getCallback().onCancel( getModel() );
				}
				
				@Override
				public void onCancel() {
					cancelButton.setEnabled(true);
				}
			});
		} else {
			getCallback().onCancel( getModel() );
		}
	}
	protected void save() {
		save(null);
	}
	protected void save(AsyncCallback<Mod347> cbk) {
		if (getModel().getYear() == 0) {
			throw new IllegalArgumentException(AON.MSG.requiredField(AON.MSG.fiscalYear()));
		}
		saveButton.setEnabled(false);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model347.SERVICE.save(getCallback().getOptions().getOccam(),
				getModel(), new AsyncCallback<Mod347>() {
					@Override
					public void onSuccess(Mod347 result) {
						popup.hide();
						getCallback().onSelect( result, declaredManager.getSelectedDeclaredIndex(),
							assetManager.getSelectedAssetIndex(), getTabPanelIndex() );
						if (cbk != null) cbk.onSuccess(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
					}
				});
	}
	
	private void delete() {
		deleteButton.setEnabled(false);
		AonConfirmDialog cd = new AonConfirmDialog();
		cd.confirm(AON.MSG.confirmDeclarationDeleteAction(), new AonConfirmDialogCallback() {

			@Override
			public void onAccept() {
				Model347.SERVICE.delete(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						deleteButton.setEnabled(true);
						getCallback().onRemove(mod347);
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
	
	private void reset() {
		resetButton.setEnabled(false);
		AonConfirmDialog cd = new AonConfirmDialog();
		cd.confirm(AON.MSG.confirmDeclarationinitializationAction(), new AonConfirmDialogCallback() {

			@Override
			public void onAccept() {
				Model347.SERVICE.reset(getCallback().getOptions().getOccam(),getModel(),
						new AsyncCallback<Mod347>() {
							@Override
							public void onSuccess(Mod347 m347) {
								setDirty( true );
								getCallback().onSelect( m347, declaredManager.getSelectedDeclaredIndex(),
									assetManager.getSelectedAssetIndex(), getTabPanelIndex() );
							}

							@Override
							public void onFailure(Throwable caught) {
								getCallback().showError(AON.MSG.unableToInitializeDeclaration(caught.getMessage()));
								
							}
						});
			}
			@Override
			public void onCancel() {
				resetButton.setEnabled(true);
			}
		});
	}

	private void print() {
		if (isDirty()) {
			new AonConfirmDialog().confirm(AON.MSG.draftPrint(),AON.MSG.draftPrintNote() 
					, new AonConfirmDialogCallback() {
					
					@Override
					public void onAccept() {
						submitForm(MODEL347_PRINT);
					}
	
					@Override
					public void onCancel() {
						// Nothing
					}
				});
		} else {
			submitForm(MODEL347_PRINT);
		}
	}
	
	private void mailMerge() {
		if (isDirty()) {
			new AonConfirmDialog().confirm("Combinar correspondencia",
					"La generaci\u00F3n del archivo para combinar, se realiza a partir de los datos guardados. Aseg\u00FArese de haber guardado la declaraci\u00F3n.",
					new AonConfirmDialogCallback() {

						@Override
						public void onAccept() {
							submitForm(MODEL347_MERGE_MAIL);
						}

						@Override
						public void onCancel() {
							// Nothing
						}
					});
		} else {
			submitForm(MODEL347_MERGE_MAIL);
		}
	}

	private void audit() {
		AonAuditDialog dialog = new AonAuditDialog();
		dialog.show(getModel());
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
		Model347.SERVICE.changeStatus(getCallback().getOptions().getOccam(), getModel(), FiscalStatus.FINISHED, new AsyncCallback<Mod347>() {
			@Override
			public void onSuccess(Mod347 result) {
				getCallback().onSelect(result , declaredManager.getSelectedDeclaredIndex(),
						assetManager.getSelectedAssetIndex(), getTabPanelIndex());
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
		Model347.SERVICE.changeStatus(getCallback().getOptions().getOccam(), getModel(), FiscalStatus.SENT, new AsyncCallback<Mod347>() {
			@Override
			public void onSuccess(Mod347 result) {
				callback.onSelect(result, declaredManager.getSelectedDeclaredIndex(),
						assetManager.getSelectedAssetIndex(), getTabPanelIndex());
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
		Model347.SERVICE.changeStatus(getCallback().getOptions().getOccam(), getModel(), FiscalStatus.PENDING, new AsyncCallback<Mod347>() {
			@Override
			public void onSuccess(Mod347 result) {
				callback.onSelect(result, declaredManager.getSelectedDeclaredIndex(),
						assetManager.getSelectedAssetIndex(), getTabPanelIndex());
			}

			@Override
			public void onFailure(Throwable caught) {
				markAsPendingButton.setEnabled(true);
				callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
			}
		});
	}

	protected void submitForm(String action) {
		diskForm.setMethod(FormPanel.METHOD_POST);
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		diskForm.clear();
		FlowPanel diskPanel = new FlowPanel();
		diskPanel.add(mod347Hidden);
		diskPanel.add(domainIdHidden);
		diskPanel.add(domainNameHidden);
		diskPanel.add(userHidden);
		diskForm.add(diskPanel);
		mod347Hidden.setValue(String.valueOf(getModel().getId()));
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
		printButton.setVisible(!getModel().isNew());
		mailMergeButton.setVisible(!getModel().isNew());
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

		int row = 0;

		// Documento
		table.setWidget(row, 0, new InlineLabel(AON.MSG.document()));
		table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
		AonDocumentTextBox document = new AonDocumentTextBox();
		document.setValue(getModel().getDocument());
		// Modificar el NIF influye en las lineas, ya que los nif que empiezan
		// por "H", solo cumplimentan el importe total y no los trimestres, si se
		// modifica y se cambia de H a no H o viceversa, entonces no se traslada a las lineas
		// automaticamente por que habria que cambiar todas las lineas, para quitar los trimestres y
		// poner los totales o viceversa, creo que es mejor no dejarlo modificar por lo que conlleva
		// Febrero 2018 - Dejo que se pueda modificar, porque esta habiendo muchos problemas
		// cuando el NIF o demas datos generales, o bien no estaban creados antes, o se
		// genero el modelo con el modelo viejo
		document.addValueChangeHandler(event -> {
			getModel().setDocument(document.getValue());
			identificationLabelChanged();
			markAsDirty();
		});
		table.setWidget(row, 1, document);

		// Nombre
		row++;
		table.setWidget(row, 0, new InlineLabel(AON.MSG.enterpriseName()));
		table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
		AonTextBox name = new AonTextBox();
		name.setStyleName(AON.CSS.aonInputText());
		name.setVisibleLength(45);
		name.setMaxLength(45);
		name.setValue(getModel().getName());
		name.addValueChangeHandler(event -> {
			getModel().setName(name.getValue());
			identificationLabelChanged();
			markAsDirty();
		});
		table.setWidget(row, 1, name);

		// Datos persona de contacto (Todos excepto Gipuzkoa)
		if (getModel().getAdministration() != Administration.GIPUZKOA) {

			// Teléfono de contacto
			row++;
			table.setWidget(row, 0, new InlineLabel(AON.MSG.contactPhone()));
			table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
			AonTextBox contactPhone = new AonTextBox();
			contactPhone.setStyleName(AON.CSS.aonInputText());
			contactPhone.setMaxLength(9);
			contactPhone.setVisibleLength(10);
			contactPhone.setValue(getModel().getContactPhone());
			contactPhone.addValueChangeHandler(event -> {
				getModel().setContactPhone(contactPhone.getValue());
				markAsDirty();
			});
			table.setWidget(row, 1, contactPhone);

			// Persona de contacto
			row++;
			table.setWidget(row, 0, new InlineLabel(AON.MSG.contactPerson()));
			table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
			AonTextBox contactPerson = new AonTextBox();
			contactPerson.setStyleName(AON.CSS.aonInputText());
			contactPerson.setMaxLength(40);
			contactPerson.setVisibleLength(30);
			contactPerson.setValue(getModel().getContactPerson());
			contactPerson.addValueChangeHandler(event -> {
				getModel().setContactPerson(contactPerson.getValue());
				markAsDirty();
			});
			table.setWidget(row, 1, contactPerson);

			// Email persona de contacto
			row++;
			table.setWidget(row, 0, new InlineLabel(AON.MSG.contactMail()));
			table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
			AonTextBox contactMail = new AonTextBox();
			contactMail.setStyleName(AON.CSS.aonInputText());
			contactMail.setMaxLength(50);
			contactMail.setVisibleLength(50);
			contactMail.setValue(getModel().getContactMail());
			contactMail.addValueChangeHandler(event -> {
				getModel().setContactMail(contactMail.getValue());
				markAsDirty();
			});
			table.setWidget(row, 1, contactMail);
		}

		// Número declaración anterior (Todos excepto Bizkaia y Gipuzkoa)
		if (getModel().getAdministration() != Administration.BIZKAIA
				&& getModel().getAdministration() != Administration.GIPUZKOA) {
			row++;
			table.setWidget(row, 0, new InlineLabel(AON.MSG.receipt()));
			table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
			AonTextBox decNumber = new AonTextBox();
			decNumber.setStyleName(AON.CSS.aonInputText());
			decNumber.setMaxLength(13);
			decNumber.setVisibleLength(13);
			decNumber.setValue(getModel().getNumber());
			decNumber.addValueChangeHandler(event -> {
				getModel().setNumber(decNumber.getValue());
				markAsDirty();
			});
			table.setWidget(row, 1, decNumber);

			row++;
			table.setWidget(row, 0, new InlineLabel(AON.MSG.previousDeclaration()));
			table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
			AonTextBox replaced = new AonTextBox();
			replaced.setStyleName(AON.CSS.aonInputText());
			replaced.setMaxLength(13);
			replaced.setVisibleLength(13);
			replaced.setValue(getModel().getReplacedNumber());
			replaced.setEnabled(
					((getModel().isComplementary() && getModel().getAdministration() != Administration.NAVARRA)
							|| getModel().isReplacement())); // Complementaria o sustitutiva (Navarra solo sustitutiva)
			replaced.addValueChangeHandler(event -> {
				getModel().setReplacedNumber(replaced.getValue());
				markAsDirty();
			});
			table.setWidget(row, 1, replaced);
		}

		// NIF Representante Legal (Todos excepto Gipuzkoa)
		if (getModel().getAdministration() != Administration.GIPUZKOA) {
			row++;
			table.setWidget(row, 0, new InlineLabel("NIF Representante Legal"));
			table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
			AonDocumentTextBox representativeDocument = new AonDocumentTextBox();
			representativeDocument.setValue(getModel().getRepresentativeDocument());
			representativeDocument.setMaxLength(9);
			representativeDocument.addValueChangeHandler(event -> {
				getModel().setRepresentativeDocument(representativeDocument.getValue());
				identificationLabelChanged();
				markAsDirty();
			});
			table.setWidget(row, 1, representativeDocument);
		}

		declarationScrollPanel.setWidget(table);
		tabPanel.add(declarationScrollPanel, AON.MSG.declaration());
	}

	protected void decorateDeclarationTab() {
		if (receiptBox != null) {
			receiptBox.setValue( getModel().getNumber() );
		}
	}

	// ************************************************************************
	// ************************************************************************

	protected void paintDeclaredTab(TabLayoutPanel tabPanel, Integer selectedIndex) {
		// Evaluar lo diferentes paneles por administraciuon y/o ejercicio.
		if (getModel().getYear() >= 2014) {
			declaredManager = new Model347Declared2014(getCallback(), getModel(), selectedIndex);
		}
		tabPanel.add((Widget) declaredManager, "Declarados");
	}

	protected void paintAssetsTab(TabLayoutPanel tabPanel, Integer selectedIndex) {
		// Evaluar lo diferentes paneles por administraciuon y/o ejercicio.
		if (getModel().getYear() >= 2014) {
			assetManager = new Model347Asset2014(getCallback(), getModel(), selectedIndex);
		}
		tabPanel.add((Widget) assetManager, "Inmuebles");
	}

	// Añade un manejador que hace que cada vez que cambiemos de pestaña se guarde
	// la pestaña seleccionada
	// Se utilizará si pulsamos el botón guardar, para que cuando se repinte la
	// pantalla vuelva a la pestaña donde estabamos
	protected void addTabPanelSelectionHandler(TabLayoutPanel tabPanel) {

		tabPanel.addSelectionHandler(event -> {
			setTabPanelIndex(tabPanel.getSelectedIndex());
			getCallback().cleanInfoPanel();
		});

	}

}
