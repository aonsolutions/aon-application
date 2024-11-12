package com.esferalia.aon.gwt.fiscal.client.mod369;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAuditDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToastModel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod369.Model369.Model369Callback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod369;
import com.esferalia.aon.occam.api.model.fiscal.Mod369Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod369DetailCorrection;
import com.esferalia.aon.occam.api.model.fiscal.Mod369DetailOther;
import com.esferalia.aon.occam.api.model.fiscal.Mod369PayType;
import com.esferalia.aon.occam.api.model.fiscal.Mod369Regime;
import com.esferalia.aon.occam.api.model.fiscal.Mod369VatType;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

abstract class Model369Base extends DockLayoutPanel {

//	private static final String WIDTH_200PX = "200px";
	static final String MODEL369_FILE = "/aon_gwt_fiscal/ms/Model369File";
	
	protected interface IModel369Detail extends IsWidget {
		Integer getSelectedDetailIndex();
	}
	protected interface IModel369Correction extends IsWidget {
		Integer getSelectedPartnerIndex();
	}
	
	private Mod369 mod369;
	private Model369Callback callback;
	private boolean dirty;

	protected FiscalModelAdmonPanel<Mod369, Model369ModuleOptions> admonPanel;
//	protected AonTextBox receiptBox;

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
//	protected final AonToolbarButton certificateButton = new AonToolbarButton(AON.MSG.printCertificate(),AON.CSS.aonIconPdf());
	
	protected final AonToolbar decToolbar = new AonToolbar();
	protected final InlineLabel dirtyLabel = new InlineLabel();
//	protected final InlineLabel replacedLabel = new InlineLabel();
	protected final Label statusLabel = new Label();
	
	protected FormPanel diskForm = new FormPanel("_blank");
	protected Hidden mod369Hidden = new Hidden("mod369");
	protected Hidden domainIdHidden = new Hidden("domainId");
	protected Hidden domainNameHidden = new Hidden("domainName");
	protected Hidden userHidden = new Hidden("user");

//	private IModel369Detail detailManager;
//	private IModel369Correction partnerManager;
	private AonToastModel toast = null;
	
	protected Model369Base(Model369Callback cbk, Mod369 mod369) {
		super(Unit.PX);
		this.callback = cbk;
		
		select( mod369 );
		
		AonFiscalModelHeader modelHeader = new AonFiscalModelHeader(this.mod369);
//		modelHeader.getModelNameCell().add(new Label(this.mod369.getRegime().getDescription()));
		
		addNorth(modelHeader, AonFiscalModelHeader.HEIGTH);
		addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		addNorth(getDeclarationToolbarPanel(), AonToolbar.HEIGTH);
		
		setStyleName(AON.CSS.aonSelector());
	}

	public Model369Callback getCallback() {
		return callback;
	}
	protected Mod369 getModel() {
		return mod369;
	}
	public void setModel(Mod369 mod369) {
		this.mod369 = mod369;
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
		refreshToolbarState();
	}
	protected void select(Mod369 mod369) {
		setModel(mod369);
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
		
//		certificateButton.addClickHandler(event -> certificate());
//		toolbarPanel.add(certificateButton);

		commentsButton.addClickHandler( event -> {
			if (toast == null || toast.getParent() == null) {
				toast  = new AonToastModel(this);
				FlowPanel commentPanel = new FlowPanel();
				commentPanel.setStyleName( FiscalModelUtils.getAdministrationBackgroundStyle(getModel().getAdministration()) );
				commentPanel.addStyleName(AON.CSS.aonHeightAll());
				commentPanel.addStyleName(AON.CSS.aonTextCenter());
				TextArea comment = new TextArea();
				comment.addValueChangeHandler(event1 -> {
					getModel().setComments(event1.getValue());
					styleCommentsButton();
					Model369.SERVICE.saveComments( getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod369>() {
						@Override
						public void onSuccess(Mod369 result) {
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
	
	protected void save() {
		save(null);
	}
	protected void save(AsyncCallback<Mod369> cbk) {
		if (getModel().getYear() == 0) {
			throw new IllegalArgumentException(AON.MSG.requiredField(AON.MSG.fiscalYear()));
		}
		saveButton.setEnabled(false);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model369.SERVICE.save(getCallback().getOptions().getOccam(),
				getModel(), new AsyncCallback<Mod369>() {
					@Override
					public void onSuccess(Mod369 result) {
						popup.hide();
						//getCallback().onSelect( result, detailManager.getSelectedDetailIndex() );
						getCallback().onSelect( result );
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
						getCallback().getOptions().getExternalCallback().onExit(mod369);
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
				getCallback().getOptions().getExternalCallback().onExit(mod369);
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
				Model369.SERVICE.delete(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						deleteButton.setEnabled(true);
						getCallback().onRemove(mod369);
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
//		dirtyLabel.getElement().getStyle().setWidth(10, Unit.PX);
//		dirtyLabel.getElement().getStyle().setHeight(10, Unit.PX);
		marksPanels.add(dirtyLabel);
		
//		if (getModel().isReplacement()) {
//			replacedLabel.setText(AON.MSG.replacement());
//			replacedLabel.setStyleName(AON.CSS.aonMarginLeft());
//			replacedLabel.addStyleName(AON.CSS.aonIconChecked());
//			replacedLabel.addStyleName(AON.CSS.aonLabelWithIcon());
//		}
//		if (getModel().isComplementary()) {
//			replacedLabel.setText( AON.MSG.complementary());
//			replacedLabel.setStyleName(AON.CSS.aonMarginLeft());
//			replacedLabel.addStyleName(AON.CSS.aonIconChecked());
//			replacedLabel.addStyleName(AON.CSS.aonLabelWithIcon());
//		}
//		marksPanels.add(replacedLabel);

		styleDirtyLabel();
		styleStatusLabel();
		
		decToolbar.getMessagePanel().add(marksPanels);
		
		decToolbar.setTitle(statusLabel);
		return decToolbar;
	}
	
	
	private void markAsFinished() {
		markAsFinishedButton.setEnabled(false);
		Model369.SERVICE.changeStatus(getCallback().getOptions().getOccam(), getModel(), FiscalStatus.FINISHED, new AsyncCallback<Mod369>() {
			@Override
			public void onSuccess(Mod369 result) {
				//getCallback().onSelect(result , detailManager.getSelectedDetailIndex() );
				getCallback().onSelect(result);
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
		Model369.SERVICE.changeStatus(getCallback().getOptions().getOccam(), getModel(), FiscalStatus.SENT, new AsyncCallback<Mod369>() {
			@Override
			public void onSuccess(Mod369 result) {
				//callback.onSelect(result, detailManager.getSelectedDetailIndex());
				callback.onSelect(result);
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
		Model369.SERVICE.changeStatus(getCallback().getOptions().getOccam(), getModel(), FiscalStatus.PENDING, new AsyncCallback<Mod369>() {
			@Override
			public void onSuccess(Mod369 result) {
//				callback.onSelect(result, detailManager.getSelectedDetailIndex());
				callback.onSelect(result);
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
		diskPanel.add(mod369Hidden);
		diskPanel.add(domainIdHidden);
		diskPanel.add(domainNameHidden);
		diskPanel.add(userHidden);
		diskForm.add(diskPanel);
		mod369Hidden.setValue(String.valueOf(getModel().getId()));
		domainIdHidden.setValue(String.valueOf(getCallback().getOptions().getDomain()));
		domainNameHidden.setValue(getCallback().getOptions().getDomainName());
		userHidden.setValue(getCallback().getOptions().getUser());
		diskForm.submit();
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
		//toolbarPanel.setTitle(AonStringUtils.join(getModel().getDocument(),AonStringUtils.SPACE,getModel().getFullName()));
		identificationLabelChanged();
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
		
		// Habilitar/Desabilitar botones según si se ha modificado algo en el modelo
		markAsPendingButton.setEnabled(!isDirty());
		markAsFinishedButton.setEnabled(!isDirty());
		markAsSentButton.setEnabled(!isDirty());
	}
	
	private void identificationLabelChanged() {
		toolbarPanel.setTitle(AonStringUtils.join(Country.safeIso2(getModel().getCountry()),AonStringUtils.SPACE,getModel().getDocument(),AonStringUtils.SPACE,getModel().getName()));
		//toolbarPanel.setTitle("REGIMEN DE LA UNION   --   " + AonStringUtils.join(Country.safeIso2(getModel().getCountry()),AonStringUtils.SPACE,getModel().getDocument(),AonStringUtils.SPACE,getModel().getName()));
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
		
		// Regimen
		
		table.setWidget(row, 0, new InlineLabel("R\u00E9gimen"));
		table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
				
		ListBox regimeList = new ListBox();
		for (Mod369Regime p : Mod369Regime.values()) {
			regimeList.addItem(p.getDescription(), Integer.toString(p.ordinal()));	
		}
		regimeList.setSelectedIndex(getModel().getRegime().value());
		regimeList.setEnabled(false); // El Régimen no se deja modificar una vez creado el modelo
		table.setWidget(row, 1, regimeList);
		row++;
		
		// Declarante - País
		
		table.setWidget(row, 0, new InlineLabel(AON.MSG.country()));
		table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
		
		CountryListBox countryList = new CountryListBox();
		countryList.setWidth("120px");
		countryList.setValue(getModel().getCountry());
		countryList.setEnabled(getModel().isEditable());
		countryList.addChangeHandler(event -> {
			getModel().setCountry(countryList.getValue());
			identificationLabelChanged();
			markAsDirty();
		});
		
		table.setWidget(row, 1, countryList);
		row++;		
		
		// Declarante - NIF
		
		table.setWidget(row, 0, new InlineLabel(AON.MSG.document()));
		table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
		
		AonDocumentTextBox document = new AonDocumentTextBox();
		document.setVisibleLength(15);
		document.setValue(getModel().getDocument());
		document.setEnabled(getModel().isEditable());
		document.addValueChangeHandler( event -> {
			getModel().setDocument(document.getValue());
			identificationLabelChanged();
			markAsDirty();
		});
		table.setWidget(row, 1, document);
		row++;
		
		// Declarante - Nombre
		
		table.setWidget(row, 0, new InlineLabel("Apellidos y Nombre o raz\u00F3n social"));
		table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
		
		AonTextBox name = new AonTextBox();
		name.setVisibleLength(80);
		name.setMaxLength(125);
		name.setValue(getModel().getName());
		name.setEnabled(getModel().isEditable());
		name.addValueChangeHandler( event -> {
			getModel().setName(name.getValue());
			identificationLabelChanged();
			markAsDirty();
		});
		table.setWidget(row, 1, name);
		row++;
		
		if (getModel().getRegime() == Mod369Regime.UNION || getModel().getRegime() == Mod369Regime.IMPORT) {
		
			// Ejercicio y periodo. Fecha desde (solo Régimen de la Unión o Régimen de Importación)
			
			table.setWidget(row, 0, new InlineLabel("Periodo: Fecha desde"));
			table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
			
			AonDateBox fromDateBox = new AonDateBox();
			fromDateBox.setValue(getModel().getFromDate());
			fromDateBox.setEnabled(getModel().isEditable());
			fromDateBox.addValueChangeHandler( event -> {
				getModel().setFromDate(fromDateBox.getValue());
				markAsDirty();
			});
			table.setWidget(row, 1, fromDateBox);
			row++;
			
			// Ejercicio y período. Fecha hasta (solo Régimen de la Unión o Régimen de Importación)
			
			table.setWidget(row, 0, new InlineLabel("Periodo: Fecha hasta"));
			table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
			
			AonDateBox toDateBox = new AonDateBox();
			toDateBox.setValue(getModel().getToDate());
			toDateBox.setEnabled(getModel().isEditable());
			toDateBox.addValueChangeHandler( event -> {
				getModel().setToDate(toDateBox.getValue());
				markAsDirty();
			});
			table.setWidget(row, 1, toDateBox);
			row++;
			
		}
		
		if (getModel().getRegime() == Mod369Regime.OUTSIDE || getModel().getRegime() == Mod369Regime.IMPORT) {
		
			// Número de operador en el régimen (solo Régimen Exterior o Régimen de Importación)
			
			table.setWidget(row, 0, new InlineLabel("N\u00FAmero de operador en el r\u00E9gimen"));
			table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
			
			AonTextBox operatorNumber = new AonTextBox();
			operatorNumber.setVisibleLength(15);
			operatorNumber.setMaxLength(15);
			operatorNumber.setValue(getModel().getOperatorNumber());
			operatorNumber.setEnabled(getModel().isEditable());
			operatorNumber.addValueChangeHandler( event -> {
				getModel().setOperatorNumber(operatorNumber.getValue());
				markAsDirty();
			});
			table.setWidget(row, 1, operatorNumber);
			row++;
		
		}
		
		if (getModel().getRegime() == Mod369Regime.IMPORT) {
		
			// Actúa a través de intermediario (solo Régimen de Importación)
			
			table.setWidget(row, 0, new InlineLabel("Act\u00FAa a trav\u00E9s de intermediario"));
			table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
			
			AonTextBox intermediaryNumber = new AonTextBox();
			CheckBox intermediary = new CheckBox();
			intermediary.setValue(getModel().isIntermediary());
			intermediary.setEnabled(getModel().isEditable());
			intermediary.addValueChangeHandler(event -> {
				getModel().setIntermediary(intermediary.getValue());
				markAsDirty();
				intermediaryNumber.setEnabled(intermediary.getValue());
				if (!intermediary.getValue().booleanValue()) {
					intermediaryNumber.setValue("",true);					
				}			
			});
			table.setWidget(row, 1, intermediary);
			row++;
			
			// Nº de identificación del intermediario (NIOSSIn) (solo Régimen de Importación)
			
			table.setWidget(row, 0, new InlineLabel("N\u00FAmero de identificaci\u00F3n del intermediario"));
			table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
	
			intermediaryNumber.setEnabled(getModel().isIntermediary());
			intermediaryNumber.setVisibleLength(15);
			intermediaryNumber.setMaxLength(15);
			intermediaryNumber.setValue(getModel().getIntermediaryNumber());
			intermediaryNumber.setEnabled(getModel().isEditable());
			intermediaryNumber.addValueChangeHandler( event -> {
				getModel().setIntermediaryNumber(intermediaryNumber.getValue());
				markAsDirty();
			});
			table.setWidget(row, 1, intermediaryNumber);
			row++;
		
		}

		// Declaración sin actividad
		
		table.setWidget(row, 0, new InlineLabel("Declaraci\u00F3n sin actividad"));
		table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
		
		CheckBox withoutActivity = new CheckBox();
		withoutActivity.setValue(getModel().isWithoutActivity());
		withoutActivity.setEnabled(getModel().isEditable());
		withoutActivity.addValueChangeHandler(event -> {
			getModel().setWithoutActivity(withoutActivity.getValue());
			markAsDirty();
		});
		table.setWidget(row, 1, withoutActivity);
		row++;
		
		// Tipo de pago
		
		table.setWidget(row, 0, new InlineLabel("Tipo de Pago"));
		table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
				
		ListBox payTypeList = new ListBox();
		for (Mod369PayType p : Mod369PayType.values()) {
			payTypeList.addItem(p.getDescription(), Integer.toString(p.ordinal()));	
		}
		payTypeList.setSelectedIndex(getModel().getPayType().value());
		payTypeList.setEnabled(getModel().isEditable());
		payTypeList.addChangeHandler(event -> {
			getModel().setPayType(Mod369PayType.safeValueOf(payTypeList.getSelectedIndex()));
			markAsDirty();
		});
		table.setWidget(row, 1, payTypeList);
		row++;
		
		// NRC Pago
		
		table.setWidget(row, 0, new InlineLabel("NRC Pago"));
		table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());

		AonTextBox nrc = new AonTextBox();
		nrc.setVisibleLength(22);
		nrc.setMaxLength(22);
		nrc.setValue(getModel().getNrc());
		nrc.setEnabled(getModel().isEditable());
		nrc.addValueChangeHandler( event -> {
			getModel().setNrc(nrc.getValue());
			markAsDirty();
		});
		table.setWidget(row, 1, nrc);
		row++;
		
		// Importe pagado
		
		table.setWidget(row, 0, new InlineLabel("Importe pagado"));
		table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
		
		AonDoubleBox amountPaid = new AonDoubleBox(17);
		amountPaid.setValue(getModel().getAmountPaid());
		amountPaid.setEnabled(getModel().isEditable());
		amountPaid.addValueChangeHandler(event -> {
			if (amountPaid.getValue() == null) amountPaid.setValue(0.0,false);
			getModel().setAmountPaid(amountPaid.getValue());
			markAsDirty();
		});
		table.setWidget(row, 1, amountPaid);

//		table.setWidget( 2, 0, new InlineLabel(AON.MSG.contactPerson()));
//		table.getCellFormatter().setStyleName(2,0, AON.CSS.aonTableLabel());
//		AonTextBox contactPerson = new AonTextBox();
//		contactPerson.setMaxLength(40);
//		contactPerson.setVisibleLength(30);
//		contactPerson.setValue(getModel().getContactPerson());
//		contactPerson.addValueChangeHandler( event -> {
//			getModel().setContactPerson(contactPerson.getValue());
//			markAsDirty();
//		});
//		table.setWidget(2, 1, contactPerson);

//		table.setWidget( 3, 0, new InlineLabel(AON.MSG.contactPhone()));
//		table.getCellFormatter().setStyleName(3,0, AON.CSS.aonTableLabel());
//		AonTextBox contactPhone = new AonTextBox();
//		contactPhone.setMaxLength(9);
//		contactPhone.setVisibleLength(10);
//		contactPhone.setValue(getModel().getContactPhone());
//		contactPhone.addValueChangeHandler( event -> {
//			getModel().setContactPhone(contactPhone.getValue());
//			markAsDirty();
//		});
//		table.setWidget(3, 1, contactPhone);
		

//		table.setWidget( 4, 0, new InlineLabel(AON.MSG.contactMail()));
//		table.getCellFormatter().setStyleName(4,0, AON.CSS.aonTableLabel());
//		AonTextBox contactMail = new AonTextBox();
//		contactMail.setMaxLength(50);
//		contactMail.setVisibleLength(50);
//		contactMail.setValue(getModel().getContactMail());
//		contactMail.addValueChangeHandler( event -> {
//			getModel().setContactMail(contactMail.getValue());
//			markAsDirty();
//		});
//		table.setWidget(4, 1, contactMail);
		
//		table.setWidget( 5, 0, new InlineLabel(AON.MSG.receipt()));
//		table.getCellFormatter().setStyleName(5,0, AON.CSS.aonTableLabel());
//		receiptBox = new AonTextBox();
//		receiptBox.setMaxLength(13);
//		receiptBox.setVisibleLength(13);
//		receiptBox.setEnabled(getModel().isAEAT());
//		receiptBox.setValue(getModel().getReceipt());
//		receiptBox.addValueChangeHandler(event -> {
//			getModel().setReceipt(receiptBox.getValue());
//			markAsDirty();
//		});
//		table.setWidget(5, 1, receiptBox);

//		table.setWidget( 6, 0, new InlineLabel(AON.MSG.previousDeclaration()));
//		table.getCellFormatter().setStyleName(6,0, AON.CSS.aonTableLabel());
//		AonTextBox replaced = new AonTextBox();
//		replaced.setMaxLength(13);
//		replaced.setVisibleLength(13);
//		replaced.setEnabled(getModel().isAEAT() && (getModel().isComplementary() || getModel().isReplacement()));
//		replaced.setValue(getModel().getReplacedReceipt());
//		replaced.addValueChangeHandler(event -> {
//			getModel().setReplacedReceipt(replaced.getValue());
//			markAsDirty();
//		});
//		table.setWidget(6, 1, replaced);
		
		declarationScrollPanel.setWidget(table);
		tabPanel.add(declarationScrollPanel, AON.MSG.declaration());
	}
	
//	protected void decorateDeclarationTab() {
//		if (receiptBox != null) {
//			receiptBox.setValue( getModel().getReceipt() );
//		}
//	}

//	protected void paintDetailTab(TabLayoutPanel tabPanel, Integer selectedIndex) {		
//		detailManager = new Model369Detail(getCallback(), getModel(), selectedIndex );
//		tabPanel.add( (Widget) detailManager, "Detail3");  
//	}

//	protected void paintPartnersTab(TabLayoutPanel tabPanel, Integer selectedIndex ) {
//		// Evaluar lo diferentes paneles por administraciuon y/o ejercicio. 
//		partnerManager = new Model369Correction( getCallback() , getModel(), selectedIndex );
//		tabPanel.add( (Widget) partnerManager,  AON.MSG.entityPartners());
//	}

	
//	protected void paintEntityTab(TabLayoutPanel tabPanel) {
//		ScrollPanel entityScrollPanel = new ScrollPanel();
//		entityScrollPanel.setStyleName(AON.CSS.aonWidthAll());
//		entityScrollPanel.addStyleName(AON.CSS.aonScrollArea());
//		
//		FlexTable tab = new FlexTable();
//		tab.setStyleName(AON.CSS.aonWidthAll());
//		tab.addStyleName(AON.CSS.aonNowrap());
//		
//		tab.getColumnFormatter().setWidth(0, "150px");
//		tab.getColumnFormatter().setWidth(1, WIDTH_200PX);
//		tab.getColumnFormatter().setWidth(2, WIDTH_200PX);
//		tab.getColumnFormatter().setWidth(3, "auto");
//
//		tab.getCellFormatter().setStyleName(0, 0, AON.CSS.aonBorderBottom());
//		tab.getCellFormatter().addStyleName(0, 0, AON.CSS.aonBold());
//		tab.getFlexCellFormatter().setColSpan(0, 0, 4);
//		tab.setWidget(0, 0, new InlineLabel(AON.MSG.localEntities()));
//
//		tab.setWidget(1, 0, new Model369SmallerLabel(AON.MSG.entityType()));
//		ListBox entityType = new ListBox();
//		entityType.addItem(" - ","");
//		entityType.addItem("1 - Sociedad civil.","1");
//		entityType.addItem("2 - Comunidad de bienes.","2");
//		entityType.addItem("3 - Herencia yacente.","3");
//		entityType.addItem("4 - Comunidad de propietarios.","4");
//		entityType.addItem("5 - Otros","5");
//		entityType.setWidth(WIDTH_200PX);
//		entityType.setSelectedIndex(AonNumberUtils.toint(getModel().getEntityType()));
//		entityType.addChangeHandler(event -> {
//			getModel().setEntityType(entityType.getSelectedValue());
//			markAsDirty();
//		});
//		tab.setWidget(1, 1, entityType);
//		
//		tab.setWidget(1, 2, new Model369SmallerLabel(AON.MSG.mainActivity()));
//		ListBox mainActivity = new ListBox();
//		mainActivity.addItem(" - ","");
//		mainActivity.addItem("1 - Actividad empresarial.","1");
//		mainActivity.addItem("2 - Actividad profesional.","2");
//		mainActivity.addItem("3 - Tenencia y administraci\u00F3n de bienes inmuebles.","3");
//		mainActivity.addItem("4 - Tenencia y administraci\u00F3n de valores o activos financieros.","4");
//		mainActivity.addItem("5 - Otras.","5");
//		mainActivity.setWidth("250px");
//		mainActivity.setSelectedIndex(AonNumberUtils.toint(getModel().getMainActivity()));
//		mainActivity.addChangeHandler(event -> {
//			getModel().setMainActivity(mainActivity.getSelectedValue());
//			markAsDirty();
//		});
//		tab.setWidget(1, 3, mainActivity);
//		
//		tab.getCellFormatter().setStyleName(2, 0, AON.CSS.aonBorderBottom());
//		tab.getCellFormatter().addStyleName(2, 0, AON.CSS.aonBold());
//		tab.getFlexCellFormatter().setColSpan(2, 0, 4);
//		tab.setWidget(2, 0, new InlineLabel(AON.MSG.foreignEntities()));
//		
//		tab.setWidget(3, 0, new Model369SmallerLabel(AON.MSG.entityType()));
//		ListBox foreignEntityType = new ListBox();
//		foreignEntityType.addItem(" - ","");
//		foreignEntityType.addItem("1- Corporaci\u00F3n, asociaci\u00F3n o ente con personalidad jur\u00EDdica propia.","1");
//		foreignEntityType.addItem("2- Corporaci\u00F3n o ente independiente pero sin personalidad jur\u00EDdica propia.","2");
//		foreignEntityType.addItem("3- Conjunto unitario de bienes pertenecientes a dos o m\u00E1s personas en com\u00FAn sin personalidad jur\u00EDdica propia.","3");
//		foreignEntityType.addItem("4- Otras","4");
//		foreignEntityType.setWidth(WIDTH_200PX);
//		foreignEntityType.setSelectedIndex(AonNumberUtils.toint(getModel().getForeignEntityType()));
//		foreignEntityType.addChangeHandler(event -> {
//			getModel().setForeignEntityType(foreignEntityType.getSelectedValue());
//			markAsDirty();
//		});
//		tab.setWidget(3, 1, foreignEntityType);
//
//		tab.setWidget(3, 2, new Model369SmallerLabel(AON.MSG.entityType()));
//		ListBox foreignObject = new ListBox();
//		foreignObject.addItem(" - ","");
//		foreignObject.addItem("A - Actividad nat. empresarial.","A");
//		foreignObject.addItem("B - Actividad nat. profesional.","B");
//		foreignObject.setWidth(WIDTH_200PX);
//		foreignObject.setSelectedIndex(AonNumberUtils.toint(getModel().getForeignObject()));
//		foreignObject.addChangeHandler(event -> {
//			getModel().setForeignEntityType(foreignObject.getSelectedValue());
//			markAsDirty();
//		});
//		tab.setWidget(3, 3, foreignObject);
//		
//		tab.setWidget(4, 0, new Model369SmallerLabel(AON.MSG.country()));
//		CountryListBox country = new CountryListBox();
//		country.setValue( Country.safeValueOf( getModel().getCountry() ));
//		country.addChangeHandler(event -> {
//			getModel().setCountry(country.getSelectedValue());
//			markAsDirty();
//		});
//		tab.setWidget(4, 1, country);
//		
//		tab.setWidget(4, 2, new Model369SmallerLabel(AON.MSG.residentPercent()));
//		AonDoubleBox residentPercent = new AonDoubleBox();
//		residentPercent.setValue(getModel().getResidentPercent());
//		residentPercent.addValueChangeHandler(event -> {
//			getModel().setResidentPercent(residentPercent.getValue());
//			markAsDirty();
//		});
//		tab.setWidget(4, 3, residentPercent);
//		
//		CheckBox taxIS = new CheckBox(AON.MSG.isTax());
//		taxIS.setValue(getModel().isTaxIS());
//		taxIS.addClickHandler(event -> {
//			getModel().setTaxIS(taxIS.getValue());
//			markAsDirty();
//		});
//		tab.getFlexCellFormatter().setColSpan(5, 0, 2);
//		tab.setWidget(5, 0, taxIS);
//		
//		tab.setWidget(5, 1, new Model369SmallerLabel(AON.MSG.netAmount()));
//		AonDoubleBox netSalesAmount = new AonDoubleBox();
//		netSalesAmount.setValue(getModel().getNetSalesAmount());
//		netSalesAmount.addValueChangeHandler(event -> {
//			getModel().setNetSalesAmount(netSalesAmount.getValue());
//			markAsDirty();
//		});
//		tab.setWidget(5, 2, netSalesAmount);
//		
//		tab.setWidget(6, 0, new Model369SmallerLabel(AON.MSG.lrDocument()));
//		AonDocumentTextBox lrDocument = new AonDocumentTextBox();
//		lrDocument.setValue(getModel().getLrDocument());
//		lrDocument.addValueChangeHandler(event -> {
//			getModel().setLrDocument(lrDocument.getValue());
//			markAsDirty();
//		});
//		tab.setWidget(6, 1, lrDocument);
//		
//		tab.setWidget(6, 2, new Model369SmallerLabel(AON.MSG.lrName()));
//		AonTextBox lrName = new AonTextBox();
//		lrName.setMaxLength(40);
//		lrName.setVisibleLength(40);
//		lrName.setValue(getModel().getLrName());
//		lrName.addValueChangeHandler(event -> {
//			getModel().setLrName(lrName.getValue());
//			markAsDirty();
//		});
//		tab.setWidget(6, 3, lrName);
//
//		entityScrollPanel.setWidget(tab);
//		tabPanel.add(entityScrollPanel, AON.MSG.entity() );
//	}
	
	protected void paintDetailsTab(TabLayoutPanel tabPanel) {
		
		ScrollPanel detailScrollPanel = new ScrollPanel();
		detailScrollPanel.getElement().getStyle().setProperty("max-width", "1000px");
//		detailScrollPanel.setStyleName(AON.CSS.aonWidthAll());
//		detailScrollPanel.addStyleName(AON.CSS.aonScrollArea());
		
		FlowPanel basePanel = new FlowPanel();
//		basePanel.setStyleName(AON.CSS.aonMarginLeft());
		basePanel.addStyleName(AON.CSS.aonPaddingBottom());
//		basePanel.getElement().getStyle().setProperty("max-width", "1000px");
//		paint(basePanel);		
//		detailScrollPanel.setWidget(basePanel);
		
		if (getModel().getRegime() == Mod369Regime.IMPORT) {
			// Régimen de importación: Importaciones de bienes de menos de 150 euros
			basePanel.add(paintDetailsPanel(new FlowPanel(), getModel().getDetails3(), "Importaciones de bienes de menos de 150 \u20AC"));
		} else if (getModel().getRegime() == Mod369Regime.OUTSIDE) {
			// Régimen Exterior a la Unión: Prestaciones de servicios
			basePanel.add(paintDetailsPanel(new FlowPanel(), getModel().getDetails3(), "Prestaciones de servicios"));
		} else {
			// Régimen de la Unión: Prestaciones de Servicio España, Entregas de Bienes España 
			basePanel.add(paintDetailsPanel(new FlowPanel(), getModel().getDetails3(), "Prestaciones de servicios desde el EMID Espa\u00F1a y desde establecimientos permanentes situados fuera de la UE"));
			basePanel.add(paintDetailsPanel(new FlowPanel(), getModel().getDetails4(), "Entregas de bienes expedidos o transportados desde EMID Espa\u00F1a"));
		}
		
		detailScrollPanel.setWidget(basePanel);		
		tabPanel.add(detailScrollPanel, getModel().getRegime() == Mod369Regime.UNION ? "Operaciones desde EMID Espa\u00F1a" : "Operaciones");
	}
	
	private FlowPanel paintDetailsPanel(FlowPanel panel, LinkedList<Mod369Detail> details, String title) {
		
//		FlowPanel panel = new FlowPanel();
	    panel.setStyleName(AON.CSS.aonWidthAlmostAll());
		panel.addStyleName(AON.CSS.aonMarginLeft());
		panel.addStyleName(AON.CSS.aonPaddingBottom());
//		panel.getElement().getStyle().setProperty("max-width", "1000px");
		panel.clear();

		panel.add(getDetailTitle(title));
		AonDisplayTable tab2 = addDetailTable(panel, "C\u00F3digo pa\u00EDs EM de consumo", "Tipo (%) de IVA", "Tipo de IVA", "Base imponible", "Cuota IVA");	
		
		for (int i = 0; i < details.size(); i++) {
			final int idx = i;

			// País de consumo
			CountryListBox countryList = new CountryListBox();
			countryList.setWidth("170px");			
			countryList.setSelectedIndex(0);			
			countryList.setValue(details.get(idx).getCountry());
			countryList.setEnabled(getModel().isEditable());
			countryList.addChangeHandler(event -> {
				details.get(idx).setCountry(countryList.getValue());
				markAsDirty();				
			});
			
			// Porcentaje de IVA
			AonDoubleBox percent = new AonDoubleBox();
			percent.setMaxLength(5);
			percent.setVisibleLength(5);
			percent.setValue(details.get(idx).getVatPercent());
			percent.setEnabled(getModel().isEditable());
			percent.addValueChangeHandler(event -> {
				details.get(idx).setVatPercent(percent.getValue());
				markAsDirty();
			});			
			
			// Tipo de IVA 
			ListBox vatTypeList = new ListBox();
			for (Mod369VatType p : Mod369VatType.values()) {
				vatTypeList.addItem(p.getDescription(), Integer.toString(p.ordinal()));	
			}
			vatTypeList.setSelectedIndex(details.get(idx).getVatType().value());
			vatTypeList.setEnabled(getModel().isEditable());
			vatTypeList.addChangeHandler(event -> {
				details.get(idx).setVatType(Mod369VatType.safeValueOf(vatTypeList.getSelectedIndex()));
				markAsDirty();
			});
			
			// Base imponible
			AonDoubleBox base = new AonDoubleBox();
			base.setMaxLength(17);
			base.setVisibleLength(17);
			base.setValue(details.get(idx).getBase());
			base.setEnabled(getModel().isEditable());
			base.addValueChangeHandler(event -> {
				details.get(idx).setBase(base.getValue());
				markAsDirty();
			});
			
			// Cuota de IVA			
			AonDoubleBox quota = new AonDoubleBox();
			quota.setMaxLength(17);
			quota.setVisibleLength(17);
			quota.setValue(details.get(idx).getQuota());
			quota.setEnabled(getModel().isEditable());
			quota.addValueChangeHandler(event -> {
				details.get(idx).setQuota(quota.getValue());
				markAsDirty();
			});			
			
			// Boton borrar linea
			//AonTableButton deleteRowButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			AonTableButton deleteRowButton = details.get(idx).isDeleted() ? new AonTableButton(AON.MSG.restoreAction(),AON.CSS.aonIconRestore()) : new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteRowButton.setEnabled(getModel().isEditable());
			deleteRowButton.addClickHandler(event -> {
				details.get(idx).setDeleted(!details.get(idx).isDeleted());
//				details.remove(idx);
				paintDetailsPanel(panel, details, title); 
				markAsDirty();
			});
			
			// No se borran las líneas en pantalla, simplemente se dejan inactivas, con posibilidad de restaurarla
			if (details.get(idx).isDeleted()) {
				countryList.setEnabled(false);
				percent.setEnabled(false);
				vatTypeList.setEnabled(false);
				base.setEnabled(false);
				quota.setEnabled(false);
			}			

			tab2.addRow()
				.addCell(countryList)
				.addCell(percent)
				.addCell(vatTypeList)
				.addCell(base)
				.addCell(quota)
				.addCell(deleteRowButton);
		}
		
		// Botón añadir linea
		AonTableButton addButton = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButton.addStyleName(AON.CSS.aonMarginTop());
		addButton.addStyleName(AON.CSS.aonMarginLeft());
		addButton.setEnabled(getModel().isEditable());
		addButton.addClickHandler(event -> {
			details.add((new Mod369Detail()).setVatType(Mod369VatType.STANDARD));
			paintDetailsPanel(panel, details, title);
		});
		panel.add(addButton);
		
		return panel;
		
	}
	
	protected void paintDetailsOtherTab(TabLayoutPanel tabPanel) {
		
		// Esta pestaña solo aparece para el Régimen de la Unión
		if (getModel().getRegime() != Mod369Regime.UNION) {
			return;
		}
		
		ScrollPanel detailScrollPanel = new ScrollPanel();
		detailScrollPanel.getElement().getStyle().setProperty("max-width", "1000px");
//		detailScrollPanel.setStyleName(AON.CSS.aonWidthAll());
//		detailScrollPanel.addStyleName(AON.CSS.aonScrollArea());
		
		FlowPanel basePanel = new FlowPanel();
//		basePanel.setStyleName(AON.CSS.aonMarginLeft());
		basePanel.addStyleName(AON.CSS.aonPaddingBottom());
//		basePanel.getElement().getStyle().setProperty("max-width", "1000px");
//		paint(basePanel);		
//		detailScrollPanel.setWidget(basePanel);
		
		// Régimen de la Unión: Prestaciones de servicios otros, Entregas de bienes otros 
		basePanel.add(paintDetailsOtherPanel(new FlowPanel(), getModel().getDetails5(), "Prestaciones de servicios desde establecimientos permanentes en otros EM distintos de Espa\u00F1a", "C\u00F3digo Pa\u00EDs EM del EP", "NIVA del EP"));
		basePanel.add(paintDetailsOtherPanel(new FlowPanel(), getModel().getDetails6(), "Entregas de bienes expedidos o transportados desde otros EM distintos de Espa\u00F1a", "C\u00F3digo pa\u00EDs de env\u00EDo", "NIVA/Otro c\u00F3digo identificativo fiscal"));
		
		detailScrollPanel.setWidget(basePanel);		
		tabPanel.add(detailScrollPanel, "Operaciones desde otros EM");
	}
	
	private FlowPanel paintDetailsOtherPanel(FlowPanel panel, LinkedList<Mod369DetailOther> details, String title, String titleCol1, String titleCol2) {
		
//		FlowPanel panel = new FlowPanel();
	    panel.setStyleName(AON.CSS.aonWidthAlmostAll());
		panel.addStyleName(AON.CSS.aonMarginLeft());
		panel.addStyleName(AON.CSS.aonPaddingBottom());
//		panel.getElement().getStyle().setProperty("max-width", "1000px");
		panel.clear();

		panel.add(getDetailTitle(title));
		AonDisplayTable tab2 = addDetailTable(panel, titleCol1, titleCol2, "C\u00F3digo pa\u00EDs EM de consumo", "Tipo (%) IVA", "Tipo de IVA", "Base imponible", "Cuota IVA");	
		
		for (int i = 0; i < details.size(); i++) {
			final int idx = i;
			
			// País de envío
			CountryListBox otherCountryList = new CountryListBox();
			otherCountryList.setWidth("170px");			
			otherCountryList.setSelectedIndex(0);			
			otherCountryList.setValue(details.get(idx).getOtherCountry());
			otherCountryList.setEnabled(getModel().isEditable());
			otherCountryList.addChangeHandler(event -> {
				details.get(idx).setOtherCountry(otherCountryList.getValue());
				markAsDirty();				
			});
			
			// NIVA/Otro código identificativo
			AonDocumentTextBox otherDocument = new AonDocumentTextBox(false);
			otherDocument.setVisibleLength(15);
			otherDocument.setValue(details.get(idx).getOtherDocument());
			otherDocument.setEnabled(getModel().isEditable());
			otherDocument.addValueChangeHandler( event -> {
				details.get(idx).setOtherDocument(otherDocument.getValue());
				markAsDirty();
			});

			// País de consumo
			CountryListBox countryList = new CountryListBox();
			countryList.setWidth("170px");			
			countryList.setSelectedIndex(0);			
			countryList.setValue(details.get(idx).getCountry());
			countryList.setEnabled(getModel().isEditable());
			countryList.addChangeHandler(event -> {
				details.get(idx).setCountry(countryList.getValue());
				markAsDirty();				
			});
			
			// Porcentaje de IVA
			AonDoubleBox percent = new AonDoubleBox();
			percent.setMaxLength(5);
			percent.setVisibleLength(5);
			percent.setValue(details.get(idx).getVatPercent());
			percent.setEnabled(getModel().isEditable());
			percent.addValueChangeHandler(event -> {
				details.get(idx).setVatPercent(percent.getValue());
				markAsDirty();
			});			
			
			// Tipo de IVA 
			ListBox vatTypeList = new ListBox();
			for (Mod369VatType p : Mod369VatType.values()) {
				vatTypeList.addItem(p.getDescription(), Integer.toString(p.ordinal()));	
			}
			vatTypeList.setSelectedIndex(details.get(idx).getVatType().value());
			vatTypeList.setEnabled(getModel().isEditable());
			vatTypeList.addChangeHandler(event -> {
				details.get(idx).setVatType(Mod369VatType.safeValueOf(vatTypeList.getSelectedIndex()));
				markAsDirty();
			});
			
			// Base imponible
			AonDoubleBox base = new AonDoubleBox();
			base.setMaxLength(17);
			base.setVisibleLength(17);
			base.setValue(details.get(idx).getBase());
			base.setEnabled(getModel().isEditable());
			base.addValueChangeHandler(event -> {
				details.get(idx).setBase(base.getValue());
				markAsDirty();
			});
			
			// Cuota de IVA			
			AonDoubleBox quota = new AonDoubleBox();
			quota.setMaxLength(17);
			quota.setVisibleLength(17);
			quota.setValue(details.get(idx).getQuota());
			quota.setEnabled(getModel().isEditable());
			quota.addValueChangeHandler(event -> {
				details.get(idx).setQuota(quota.getValue());
				markAsDirty();
			});			
			
			// Boton borrar linea
			//AonTableButton deleteRowButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			AonTableButton deleteRowButton = details.get(idx).isDeleted() ? new AonTableButton(AON.MSG.restoreAction(),AON.CSS.aonIconRestore()) : new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteRowButton.setEnabled(getModel().isEditable());
			deleteRowButton.addClickHandler(event -> {
				details.get(idx).setDeleted(!details.get(idx).isDeleted());
//				details.remove(idx);
				paintDetailsOtherPanel(panel, details, title, titleCol1, titleCol2); 
				markAsDirty();
			});
			
			// No se borran las líneas en pantalla, simplemente se dejan inactivas, con posibilidad de restaurarla
			if (details.get(idx).isDeleted()) {
				otherCountryList.setEnabled(false);
				otherDocument.setEnabled(false);
				countryList.setEnabled(false);
				percent.setEnabled(false);
				vatTypeList.setEnabled(false);
				base.setEnabled(false);
				quota.setEnabled(false);
			}			

			tab2.addRow()
				.addCell(otherCountryList)
				.addCell(otherDocument)
				.addCell(countryList)
				.addCell(percent)
				.addCell(vatTypeList)
				.addCell(base)
				.addCell(quota)
				.addCell(deleteRowButton);
		}
		
		// Botón añadir linea
		AonTableButton addButton = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButton.addStyleName(AON.CSS.aonMarginTop());
		addButton.addStyleName(AON.CSS.aonMarginLeft());
		addButton.setEnabled(getModel().isEditable());
		addButton.addClickHandler(event -> {
			details.add((new Mod369DetailOther()).setVatType(Mod369VatType.STANDARD));
			paintDetailsOtherPanel(panel, details, title, titleCol1, titleCol2);
		});
		panel.add(addButton);
		
		return panel;
		
	}
	
	protected void paintCorrectionsTab(TabLayoutPanel tabPanel) {
		
		ScrollPanel detailScrollPanel = new ScrollPanel();
		detailScrollPanel.getElement().getStyle().setProperty("max-width", "1000px");
		
		detailScrollPanel.setWidget(paintCorrectionsPanel(new FlowPanel()));
				
		tabPanel.add(detailScrollPanel, "Correcciones");
	}	
	
	private FlowPanel paintCorrectionsPanel(FlowPanel panel) {
		
//		FlowPanel panel = new FlowPanel();
	    panel.setStyleName(AON.CSS.aonWidthAlmostAll());
		panel.addStyleName(AON.CSS.aonMarginLeft());
		panel.addStyleName(AON.CSS.aonPaddingBottom());
//		panel.getElement().getStyle().setProperty("max-width", "1000px");
		panel.clear();

		String title = "Correcciones de declaraciones de per\u00EDodos anteriores (m\u00E1x. 3 a\u00F1os)";
		panel.add(getDetailTitle(title));
		AonDisplayTable tab2 = addDetailTable(panel, "C\u00F3digo pa\u00EDs EM de consumo", "Ejercicio", "Per\u00EDodo", "Cuota IVA corregida");	
		
		for (int i = 0; i < getModel().getCorrections().size(); i++) {
			final int idx = i;

			// País de consumo
			CountryListBox countryList = new CountryListBox();
			countryList.setWidth("170px");			
			countryList.setSelectedIndex(0);			
			countryList.setValue(getModel().getCorrections().get(idx).getCountry());
			countryList.setEnabled(getModel().isEditable());
			countryList.addChangeHandler(event -> {
				getModel().getCorrections().get(idx).setCountry(countryList.getValue());
				markAsDirty();				
			});
			
			// Ejercicio
			AonIntegerBox yearBox = new AonIntegerBox();
			yearBox.setMaxLength(4);
			yearBox.setVisibleLength(4);
			yearBox.setValue(getModel().getCorrections().get(idx).getYear());
			yearBox.setEnabled(getModel().isEditable());
			yearBox.addValueChangeHandler(event -> {
				getModel().getCorrections().get(idx).setYear(yearBox.getValue()==null?0:yearBox.getValue());
				markAsDirty();
			});
			
			// Periodo
			PeriodListBox periodList = new PeriodListBox();
			periodList.setValue(getModel().getCorrections().get(idx).getPeriod());
			periodList.setEnabled(getModel().isEditable());
			periodList.addChangeHandler( event -> {
				getModel().getCorrections().get(idx).setPeriod(periodList.getValue());
				markAsDirty();			
			});
			
			// Cuota de IVA corregida			
			AonDoubleBox quota = new AonDoubleBox();
			quota.setMaxLength(17);
			quota.setVisibleLength(17);
			quota.setValue(getModel().getCorrections().get(idx).getQuota());
			quota.setEnabled(getModel().isEditable());
			quota.addValueChangeHandler(event -> {
				getModel().getCorrections().get(idx).setQuota(quota.getValue());
				markAsDirty();
			});			
			
			// Boton borrar/restaurar linea
			AonTableButton deleteRowButton = getModel().getCorrections().get(idx).isDeleted() ? new AonTableButton(AON.MSG.restoreAction(),AON.CSS.aonIconRestore()) : new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteRowButton.setEnabled(getModel().isEditable());
			deleteRowButton.addClickHandler(event -> {
				getModel().getCorrections().get(idx).setDeleted(!getModel().getCorrections().get(idx).isDeleted());
//				details.remove(idx);
				paintCorrectionsPanel(panel); 
				markAsDirty();
			});
			
			// No se borran las líneas en pantalla, simplemente se dejan inactivas, con posibilidad de restaurarla
			if (getModel().getCorrections().get(idx).isDeleted()) {
				countryList.setEnabled(false);
				yearBox.setEnabled(false);
				periodList.setEnabled(false);
				quota.setEnabled(false);		
			}			

			tab2.addRow()
				.addCell(countryList)
				.addCell(yearBox)
				.addCell(periodList)
				.addCell(quota)
				.addCell(deleteRowButton);
		}
		
		// Botón añadir linea
		AonTableButton addButton = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButton.addStyleName(AON.CSS.aonMarginTop());
		addButton.addStyleName(AON.CSS.aonMarginLeft());
		addButton.setEnabled(getModel().isEditable());
		addButton.addClickHandler(event -> {
			getModel().getCorrections().add(new Mod369DetailCorrection());
			paintCorrectionsPanel(panel);
		});
		panel.add(addButton);
		
		return panel;
		
	}
	
	private Label getDetailTitle(String text) {
		Label title = new Label(text);
		title.setStyleName(AON.CSS.aonMarginTop());
		title.addStyleName(AON.CSS.aonMarginBottom());
		title.addStyleName(AON.CSS.aonBold());
		//title.addStyleName(AON.CSS.aonTextUppercase());
		title.addStyleName(AON.CSS.aonFontMedium());		
		title.addStyleName(AON.CSS.aonWidthAlmostAll());
		title.addStyleName(AON.CSS.aonBlockCenter());
		title.addStyleName(AON.CSS.aonBorderBottom());
		return title;
	}
	
	private AonDisplayTable addDetailTable(FlowPanel panel, String... headers) {
		AonDisplayTable tab = new AonDisplayTable();
		tab.getElement().getStyle().setProperty("margin-left", "1%");		
		
		if (headers.length > 0) {
			AonDisplayTableRow row = tab.addRow();
			for (String s : headers) {
				Label l = new Label(s);
				//l.addStyleName(AON.CSS.aonMarginRight());				
				row.addCell(l, AON.CSS.aonBold(), AON.CSS.aonBorderBottom(), AON.CSS.aonTextCenter());
			}				
			row.addCell(new Label(""), AON.CSS.aonBorderBottom()); // Ultima cabecera, para el icono de borrar línea
		}
		
		panel.add(tab);
		return tab;
	}
	
}
