package com.esferalia.aon.gwt.fiscal.client.mod347;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.common.client.widget.AuditDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347.IModel347Callback;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347.Model347Callback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

abstract class Model347Base extends DockLayoutPanel {

	static final String MODEL347_PRINT = "/aon_gwt_fiscal/Model347Print";
	static final String MODEL347_FILE = "/aon_gwt_fiscal/Model347File";
	
	protected static class MediumLabel extends InlineLabel {
		private MediumLabel(String label) {
			super();
			if (AonStringUtils.length(label) > 35) {
				setText(AonStringUtils.abbreviate(label, 35));
				setTitle(label);
			} else {
				setText(label);
			}
			setStyleName(AON.AON_CSS.aonFontMedium());
		}
	}

	protected interface IModel347Declared extends IsWidget {
		Integer getSelectedDeclaredIndex();
	}
	protected interface IModel347Asset extends IsWidget {
		Integer getSelectedAssetIndex();
	}
	
	interface TabLabelTemplate extends SafeHtmlTemplates {
		@Template("<span class=\"{1} aon-padding-right aon-padding-left-20\" style=\"width: auto !important\">{0}</span>")
		SafeHtml render(String label, String iconStyle);
	}
	protected static final TabLabelTemplate TAB_TEMPLATE = GWT.create(TabLabelTemplate.class);

	protected class Model347BaseCallback implements IModel347Callback {
		private Model347Callback cbk;
		
		int tabPanelIndex; // Lleva la pestaña seleccionada en todo momento
		
		protected Model347BaseCallback( Model347Callback cbk ) {
			this.cbk = cbk;
		}
		public Mod347 getMod347() {
			return Model347Base.this.getMod347();
		}
		@Override
		public void onAccept(Mod347 mod347) {
			cbk.onAccept(mod347);
		}
		@Override	
		public void onCancel() {
			cbk.onCancel();
		}
		@Override	
		public void onSelect(Mod347 mod347, Integer selectedIndexDeclared, Integer selectedIndexAsset, int tabPanelIndex ) {
			cbk.onSelect(mod347, selectedIndexDeclared, selectedIndexAsset, tabPanelIndex );
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
	}
	
	private Mod347 mod347;
	private Model347BaseCallback callback;
	private boolean dirty;

	protected InlineLabel documentLabel = new InlineLabel();
	protected InlineLabel nameLabel = new InlineLabel();
	protected FlowPanel  dirtyPanel = new FlowPanel ();
	protected InlineLabel statusLabel = new InlineLabel();
	protected InlineLabel replacedLabel = new InlineLabel();
	protected Button commentsButton = new Button();

	protected final Button newButton = new Button();
	protected final Button saveButton = new Button();
	protected final Button cancelButton = new Button();		
	protected final Button deleteButton = new Button();
	protected final Button markAsPendingButton = new Button();
	protected final Button markAsFinishedButton = new Button();
	protected final Button markAsSentButton = new Button();
	protected final Button auditButton = new Button();
	
	protected FormPanel diskForm = new FormPanel("_blank");
	protected Hidden mod347Hidden = new Hidden("mod347");
	protected Hidden domainIdHidden = new Hidden("domainId");
	protected Hidden domainNameHidden = new Hidden("domainName");
	
	private IModel347Declared declaredManager;
	private IModel347Asset assetManager;
	
	public Model347Base(Mod347 mod347,Model347Callback cbk) {
		super(Unit.PX);
		select( mod347 );
		
		addNorth(getToolbarPanel(), 25);
		
		SimplePanel modelPanel = new SimplePanel();
		FiscalModelUtils.paintHeaderTable(modelPanel, this.mod347 );
		addNorth(modelPanel, 65);
		
		ScrollPanel headerPanel = new ScrollPanel();
		headerPanel.setStyleName(AON.AON_CSS.aonScrollArea());
		headerPanel.setWidget( getDeclarationHeaderTable());
		addNorth(headerPanel, 45);
		
		this.callback = new Model347BaseCallback(cbk);

		setStyleName(AON.AON_CSS.aonSelector());
	}

	public Model347BaseCallback getCallback() {
		return callback;
	}
	protected Mod347 getMod347() {
		return mod347;
	}
	public void setMod347(Mod347 mod347) {
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
	protected void select( Mod347 mod347) {
		setMod347(mod347);
		refreshToolbarState();
	}

	private Widget getToolbarPanel() {
		FlowPanel toolbarPanel = new FlowPanel();
		toolbarPanel.setStyleName(AON.AON_CSS.aonFindingTitleToolbar());
		toolbarPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		FlexTable toolbar = new FlexTable();
		toolbar.setCellPadding(0);
		toolbar.setCellSpacing(0);
		toolbar.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.setStyleName(AON.AON_CSS.aonFindingTitleInternal());
		toolbar.setWidget(0, 0, titlePanel);
		toolbar.setWidget(0, 0, new Label( "Modelo 347."));
		toolbar.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonFindingToolbar());
		
		newButton.setText(AON.MSG.newAction());
		newButton.setTitle(newButton.getText());
		newButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		newButton.addStyleName(AON.AON_CSS.aonIconReset());
		newButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				callback.onNew();
			}
		});
		buttonContainer.add(newButton);
				
		saveButton.setText(AON.MSG.saveAction());
		saveButton.setTitle(newButton.getText());
		saveButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		saveButton.addStyleName(AON.AON_CSS.aonIconSave());
		saveButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (getMod347().getYear() == 0) {
					throw new IllegalArgumentException(AON.MSG.requiredField(AON.MSG.fiscalYear()));
				}
				final PopupPanel popup = new PopupPanel(false, true);
				Label label = new Label(AON.MSG.processing());
				label.addStyleName(AON.AON_CSS.aonTimer());
				popup.add(label);
				popup.setGlassEnabled(true);
				popup.setAnimationEnabled(true);
				popup.center();
				Model347.SERVICE.saveMod347(Model347.getCurrentDomainName(), Model347.getCurrentDomain(),
						getMod347(), new AsyncCallback<Mod347>() {
							@Override
							public void onSuccess(Mod347 result) {
								popup.hide();
								callback.onSelect(result, declaredManager.getSelectedDeclaredIndex(), assetManager.getSelectedAssetIndex(), callback.tabPanelIndex );								
							}

							@Override
							public void onFailure(Throwable caught) {
								popup.hide();
								callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
							}
						});
			}
		});
		buttonContainer.add(saveButton);
		
		cancelButton.setText(AON.MSG.cancelAction());
		cancelButton.setTitle(cancelButton.getText());
		cancelButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		cancelButton.addStyleName(AON.AON_CSS.aonIconCancel());
		cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cancelButton.setEnabled(false);
				if (isDirty()) {
					ConfirmDialog cd = new ConfirmDialog();
					cd.confirm(AON.MSG.confirmDeclarationCancelAction(), new ConfirmDialogCallback() {

						@Override
						public void onAccept() {
							callback.onCancel();
						}

						@Override
						public void onCancel() {
							cancelButton.setEnabled(true);
						}
					});
				} else {
					callback.onCancel();
				}
			}
		});
		buttonContainer.add(cancelButton);

		deleteButton.setText(AON.MSG.deleteAction());
		deleteButton.setTitle(deleteButton.getText());
		deleteButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		deleteButton.addStyleName(AON.AON_CSS.aonIconDelete());
		deleteButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				deleteButton.setEnabled(false);
				ConfirmDialog cd = new ConfirmDialog();
				cd.confirm(AON.MSG.confirmDeclarationDeleteAction(), new ConfirmDialogCallback() {

					@Override
					public void onAccept() {
						Model347.SERVICE.deleteMod347(Model347.getCurrentDomainName(),
								Model347.getCurrentDomain(), getMod347(), new AsyncCallback<Void>() {
							@Override
							public void onSuccess(Void result) {
								deleteButton.setEnabled(true);
								callback.onCancel();
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
		});
		buttonContainer.add(deleteButton);
		
		markAsFinishedButton.setText(AON.MSG.finish());
		markAsFinishedButton.setTitle(markAsFinishedButton.getText());
		markAsFinishedButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		markAsFinishedButton.addStyleName(AON.AON_CSS.aonIconPointLightGreen());
		markAsFinishedButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				markAsFinishedButton.setEnabled(false);
				Model347.SERVICE.changeStatusMod347(Model347.getCurrentDomainName(), getMod347(), FiscalStatus.FINISHED, new AsyncCallback<Mod347>() {
					@Override
					public void onSuccess(Mod347 result) {
						callback.onSelect(result , 0, 0, 0 );
					}

					@Override
					public void onFailure(Throwable caught) {
						markAsFinishedButton.setEnabled(true);
						callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
					}
				});
			}
		});
		buttonContainer.add(markAsFinishedButton);

		markAsSentButton.setText(AON.MSG.markAsSent());
		markAsSentButton.setTitle(markAsSentButton.getText());
		markAsSentButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		markAsSentButton.addStyleName(AON.AON_CSS.aonIconPointGreen());
		markAsSentButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				markAsSentButton.setEnabled(false);
				Model347.SERVICE.changeStatusMod347(Model347.getCurrentDomainName(), getMod347(), FiscalStatus.SENT, new AsyncCallback<Mod347>() {
					@Override
					public void onSuccess(Mod347 result) {
						callback.onSelect(result , 0, 0, 0 );
					}

					@Override
					public void onFailure(Throwable caught) {
						markAsSentButton.setEnabled(true);
						callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
					}
				});
			}
		});
		buttonContainer.add(markAsSentButton);

		markAsPendingButton.setText(AON.MSG.reopen());
		markAsPendingButton.setTitle(markAsPendingButton.getText());
		markAsPendingButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		markAsPendingButton.addStyleName(AON.AON_CSS.aonIconPointOrange());
		markAsPendingButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				markAsPendingButton.setEnabled(false);
				Model347.SERVICE.changeStatusMod347(Model347.getCurrentDomainName(), getMod347(), FiscalStatus.PENDING, new AsyncCallback<Mod347>() {
					@Override
					public void onSuccess(Mod347 result) {
						callback.onSelect(result , 0, 0, 0 );
					}

					@Override
					public void onFailure(Throwable caught) {
						markAsPendingButton.setEnabled(true);
						callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
					}
				});
			}
		});
		buttonContainer.add(markAsPendingButton);
		
		auditButton.setText(AON.MSG.audit());
		auditButton.setTitle(auditButton.getText());
		auditButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		auditButton.addStyleName(AON.AON_CSS.aonIconAudit());
		auditButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				audit();
			}
		});
		buttonContainer.add(auditButton);

		toolbarPanel.add(toolbar);
		
		FlowPanel formContainer = new FlowPanel();
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(mod347Hidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formContainer.add(diskForm);
		toolbarPanel.add(formContainer);
		
		return toolbarPanel;
	}
	
	private void audit() {
		AuditDialog dialog = new AuditDialog();
		dialog.show(getMod347());
	}
	
	protected void submitForm(String action) {
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		mod347Hidden.setValue(String.valueOf(getMod347().getId()));
		domainIdHidden.setValue(String.valueOf(Model347.getCurrentDomain()));
		domainNameHidden.setValue(Model347.getCurrentDomainName());
		diskForm.submit();
	}
	
	protected void identificationLabelChanged() {
		documentLabel.setText(getMod347().getDocument());
		nameLabel.setText(getMod347().getName());
	}

	protected void styleDirtyLabel() {
		dirtyPanel.clear();
		if ( isDirty()) {
			InlineLabel dirtyLabel = new InlineLabel("[*]");
			dirtyLabel.setStyleName(AON.AON_CSS.aonColorRed());
			dirtyPanel.add(dirtyLabel);
		}
	}
	
	private void styleCommentsButton() {
		if (AonStringUtils.isEmpty(mod347.getComments())) {
			commentsButton.addStyleName(AON.AON_CSS.aonIconComment());
			commentsButton.removeStyleName(AON.AON_CSS.aonIconCommentRed());
		} else {
			commentsButton.addStyleName(AON.AON_CSS.aonIconCommentRed());
			commentsButton.removeStyleName(AON.AON_CSS.aonIconComment());
		}
		commentsButton.setTitle(mod347.getComments());
	}
	
	private void styleStatusLabel(Mod347 mod) {
		statusLabel.setText(mod.getStatus().getName());
		statusLabel.setStyleName(FiscalModelUtils.getStatusIconStyle(mod.getStatus()));
		statusLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
	}

	private void refreshToolbarState() {
		newButton.setVisible(!getMod347().isNew());
		saveButton.setVisible(!getMod347().isFinished() && !getMod347().isSent());
		deleteButton.setVisible(!getMod347().isNew() && !getMod347().isFinished() && !getMod347().isSent());
		cancelButton.setVisible(true);
		markAsPendingButton.setVisible(!getMod347().isNew() &&
			(getMod347().getStatus() == FiscalStatus.FINISHED 
			|| getMod347().getStatus() == FiscalStatus.BATCHED
			|| getMod347().getStatus() == FiscalStatus.SENT
			|| getMod347().getStatus() == FiscalStatus.BLOCKED));
		markAsFinishedButton.setVisible(!getMod347().isNew() &&
			(getMod347().getStatus() == FiscalStatus.PENDING 
			|| getMod347().getStatus() == FiscalStatus.MISSING));
		markAsSentButton.setVisible(!getMod347().isNew() &&
			(getMod347().getStatus() == FiscalStatus.FINISHED));
		
		auditButton.setVisible(!getMod347().isNew());
	}

	protected void paintDeclarationTab(TabLayoutPanel tabPanel) {
		ScrollPanel declarationScrollPanel = new ScrollPanel();
		declarationScrollPanel.setStyleName(AON.AON_CSS.aonWidthAll());
		declarationScrollPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		
		FlexTable table = new FlexTable();
		table.setStyleName(AON.AON_CSS.aonPanelGrid());
		table.addStyleName(AON.AON_CSS.aonWidth98Percent());
		table.addStyleName(AON.AON_CSS.aonBlockCenter());
		table.getColumnFormatter().setWidth(0, "300px");
		
		table.getColumnFormatter().setWidth(1, "auto");
		
		table.getColumnFormatter().setStyleName(1, AON.AON_CSS.aonPanelGridEven());
		
		// Documento
		table.setWidget( 0, 0, new InlineLabel(AON.MSG.document()));
		table.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonPanelGridOdd());
		DocumentTextBox document = new DocumentTextBox();
		document.setValue(getMod347().getDocument());
		// FALTA - Modificar el NIF influye en las lineas, ya que los nif que empiezan
		// por "H", solo cumplimentan el importe total y no los trimestres, si se modifica
	    // y se cambia de H a no H o viceversa, entonces no se traslada a las lineas automaticamente
		// por que habria que cambiar todas las lineas, para quitar los trimestres y poner los totales
		// o viceversa, creo que es mejor no dejarlo modificar por lo que conlleva
		document.setEnabled(false);
		document.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod347().setDocument(document.getValue());
				identificationLabelChanged();
				markAsDirty();
			}
		});
		table.setWidget(0, 1, document);
		table.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonPanelGridEven());
		
		// Nombre		
		table.setWidget( 1, 0, new InlineLabel(AON.MSG.enterpriseName()));
		table.getCellFormatter().setStyleName(1,0, AON.AON_CSS.aonPanelGridOdd());
		TextBox name = new TextBox();
		name.setStyleName(AON.AON_CSS.aonInputText());
		name.setVisibleLength(45);
		name.setMaxLength(45);
		name.setValue(getMod347().getName());
		name.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod347().setName(name.getValue());
				identificationLabelChanged();
				markAsDirty();
			}
		});
		table.setWidget(1, 1, name);
		table.getCellFormatter().setStyleName(1,1, AON.AON_CSS.aonPanelGridEven());
		
		// Teléfono de contacto
		table.setWidget( 2, 0, new InlineLabel(AON.MSG.contactPhone()));
		table.getCellFormatter().setStyleName(2,0, AON.AON_CSS.aonPanelGridOdd());
		TextBox contactPhone = new TextBox();
		contactPhone.setStyleName(AON.AON_CSS.aonInputText());
		contactPhone.setMaxLength(9);
		contactPhone.setVisibleLength(10);
		contactPhone.setValue(getMod347().getContactPhone());
		contactPhone.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod347().setContactPhone(contactPhone.getValue());
				markAsDirty();
			}
		});
		table.setWidget(2, 1, contactPhone);
		table.getCellFormatter().setStyleName(2,1, AON.AON_CSS.aonPanelGridEven());
		
		// Persona de contacto
		table.setWidget( 3, 0, new InlineLabel(AON.MSG.contactPerson()));
		table.getCellFormatter().setStyleName(3,0, AON.AON_CSS.aonPanelGridOdd());
		TextBox contactPerson = new TextBox();
		contactPerson.setStyleName(AON.AON_CSS.aonInputText());
		contactPerson.setMaxLength(40);
		contactPerson.setVisibleLength(30);
		contactPerson.setValue(getMod347().getContactPerson());
		contactPerson.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod347().setContactPerson(contactPerson.getValue());
				markAsDirty();
			}
		});
		table.setWidget(3, 1, contactPerson);
		table.getCellFormatter().setStyleName(3,1, AON.AON_CSS.aonPanelGridEven());
		
		// Email persona de contacto
		table.setWidget( 4, 0, new InlineLabel(AON.MSG.contactMail()));
		table.getCellFormatter().setStyleName(4,0, AON.AON_CSS.aonPanelGridOdd());
		TextBox contactMail = new TextBox();
		contactMail.setStyleName(AON.AON_CSS.aonInputText());
		contactMail.setMaxLength(50);
		contactMail.setVisibleLength(50);
		contactMail.setValue(getMod347().getContactMail());
		contactMail.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod347().setContactMail(contactMail.getValue());
				markAsDirty();
			}
		});
		table.setWidget(4, 1, contactMail);
		table.getCellFormatter().setStyleName(4,1, AON.AON_CSS.aonPanelGridEven());
		
		// Número declaración anterior 
		table.setWidget( 5, 0, new InlineLabel(AON.MSG.previousDeclaration()));
		table.getCellFormatter().setStyleName(5,0, AON.AON_CSS.aonPanelGridOdd());
		TextBox replaced = new TextBox();
		replaced.setStyleName(AON.AON_CSS.aonInputText());
		replaced.setMaxLength(13);
		replaced.setVisibleLength(13);
		replaced.setEnabled(getMod347().isComplementary() || getMod347().isReplacement());
		replaced.setValue(getMod347().getReplacedNumber());
		replaced.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod347().setReplacedNumber(replaced.getValue());
				markAsDirty();
			}
		});
		table.setWidget(5, 1, replaced);
		table.getCellFormatter().setStyleName(5,1, AON.AON_CSS.aonPanelGridEven());
	
		// NIF Representante Legal
		table.setWidget( 6, 0, new InlineLabel("NIF Representante Legal"));
		table.getCellFormatter().setStyleName(6,0, AON.AON_CSS.aonPanelGridOdd());
		DocumentTextBox representativeDocument = new DocumentTextBox();
		representativeDocument.setValue(getMod347().getRepresentativeDocument());
		representativeDocument.setMaxLength(9);
		representativeDocument.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod347().setRepresentativeDocument(representativeDocument.getValue());
				identificationLabelChanged();
				markAsDirty();
			}
		});
		table.setWidget(6, 1, representativeDocument);
		table.getCellFormatter().setStyleName(6,1, AON.AON_CSS.aonPanelGridEven());
		
		declarationScrollPanel.setWidget(table);
		tabPanel.add(declarationScrollPanel, TAB_TEMPLATE.render(AON.MSG.declaration(), AON.AON_CSS.aonIconModel()));
	}
	
	private Widget getDeclarationHeaderTable() {
		FlexTable table = new FlexTable();
		table.setStyleName(AON.AON_CSS.aonPanelGrid());
		table.addStyleName(AON.AON_CSS.aonWidthAll());
		table.addStyleName(AON.AON_CSS.aonBlockCenter());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().setWidth(1, "150px");
		table.getColumnFormatter().setWidth(2, "150px");
		table.getColumnFormatter().setWidth(3, "150px");
		table.getColumnFormatter().setWidth(4, "110px");
		
		FlowPanel namePanel = new FlowPanel();
		namePanel.setStyleName(AON.AON_CSS.aonFontBig());
		namePanel.addStyleName(AON.AON_CSS.aonTextCenter());
		documentLabel.setText(getMod347().getDocument());
		namePanel.add(documentLabel);
		nameLabel.setStyleName(AON.AON_CSS.aonMarginLeft());
		nameLabel.setText(getMod347().getName());
		namePanel.add(nameLabel);
		
		table.setWidget(0, 0, namePanel);
		table.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonNowrap());

		if (getMod347().isReplacement()) {
			replacedLabel.setText("Sustit.");
			replacedLabel.setStyleName(AON.AON_CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
		}
		if (getMod347().isComplementary()) {
			replacedLabel.setText("Complem.");
			replacedLabel.setStyleName(AON.AON_CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
		}
		table.setWidget(0, 1, replacedLabel);
		table.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonTextCenter());
		
		styleDirtyLabel();
		table.setWidget(0, 2, dirtyPanel);
		table.getCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 2, AON.AON_CSS.aonTextCenter());
		
		styleStatusLabel(getMod347());
		table.setWidget(0, 3, statusLabel);
		table.getCellFormatter().setStyleName(0, 3, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 3, AON.AON_CSS.aonNowrap());
		table.getCellFormatter().addStyleName(0, 3, AON.AON_CSS.aonTextCenter());
		
		FlowPanel commentsPanel = new FlowPanel();
		commentsButton.setStyleName(AON.AON_CSS.aonIconCommandButton());
		commentsButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final AonToast toast = new AonToast();
				FlowPanel commentPanel = new FlowPanel();
				commentPanel.setStyleName( FiscalModelUtils.getAdministrationBG(getMod347().getAdministration()) );
				commentPanel.setStyleName(AON.AON_CSS.aonHeightAll());
				commentPanel.addStyleName(AON.AON_CSS.aonTextCenter());
				TextArea comment = new TextArea();
				comment.addValueChangeHandler(new ValueChangeHandler<String>() {
					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						getMod347().setComments(event.getValue());
						styleCommentsButton();
						Model347.SERVICE.saveCommentsMod347(Model303.getCurrentDomainName(), getMod347(), new AsyncCallback<Mod347>() {
							@Override
							public void onSuccess(Mod347 result) {
								toast.hide();
							}

							@Override
							public void onFailure(Throwable caught) {
								toast.hide();
								callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
							}
						});
					}
				});
				comment.setText(mod347.getComments());
				comment.setWidth("90%");
				comment.setHeight("5em");
				commentPanel.add(comment);
				toast.show(AON.MSG.comments(), commentPanel);
			}
		});
		commentsPanel.add(commentsButton);
		commentsPanel.add(new InlineLabel(AON.MSG.comments()));
		table.setWidget(0, 4, commentsPanel);
		styleCommentsButton();
		table.getCellFormatter().setStyleName(0, 4, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 4, AON.AON_CSS.aonTextCenter());
		return table;
		
	}
	
	protected FlowPanel getInformationPanel() {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.addStyleName(AON.AON_CSS.aonWidthAll());
		panel.addStyleName(AON.AON_CSS.aonMarginTop());
		panel.addStyleName(AON.AON_CSS.aonPaddingTop());
		panel.addStyleName(AON.AON_CSS.aonPaddingLeft());
		 
		FlexTable tab = new FlexTable();
		tab.getColumnFormatter().setWidth(0, "30px");
		tab.getColumnFormatter().setWidth(1
				, "auto");
		tab.setStyleName(AON.AON_CSS.aonWidth90Percent());
		tab.addStyleName(AON.AON_CSS.aonBlockCenter());
		tab.addStyleName(AON.AON_CSS.aonPanelGrid());
		Label title = new Label("Informaci\u00F3n \u00FAtil para la confecci\u00F3n del modelo");
		tab.getFlexCellFormatter().setColSpan(0, 0, 2);
		tab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonMarginTop());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(getMod347().getAdministration()));
		tab.setWidget(0, 0, title);
		
		int row = 1;
		for (Pair<String, String> pair : getInformationLinks()) {
			Label icon = new Label();
			icon.addStyleName(FiscalModelUtils.getAdministrationIcon(getMod347().getAdministration()));
			tab.setWidget(row, 0, icon );
			tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());

			FlowPanel p = new FlowPanel();
			p.setStyleName(AON.AON_CSS.aonPadding2());
			Anchor a = new Anchor(pair.getLeft(),pair.getRight(), "_blank");
			a.setStyleName(AON.AON_CSS.aonPaddingLeft());
			p.add(a);
			tab.setWidget(row, 1, p );
			tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
			row++;
		}
		panel.add(tab);
		return panel;
	}

	protected FlowPanel getAdministrationPanel() {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.addStyleName(AON.AON_CSS.aonWidthAll());
		panel.addStyleName(AON.AON_CSS.aonMarginTop());
		panel.addStyleName(AON.AON_CSS.aonPaddingTop());
		panel.addStyleName(AON.AON_CSS.aonPaddingLeft());
		 
		FlexTable tab = new FlexTable();
		tab.getColumnFormatter().setWidth(0, "30px");
		tab.getColumnFormatter().setWidth(1, "auto");
		tab.setStyleName(AON.AON_CSS.aonWidth90Percent());
		tab.addStyleName(AON.AON_CSS.aonBlockCenter());
		tab.addStyleName(AON.AON_CSS.aonPanelGrid());
		Label title = new Label("Presentaci\u00F3n del modelo");
		tab.getFlexCellFormatter().setColSpan(0, 0, 2);
		tab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonMarginTop());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(getMod347().getAdministration()));
		tab.setWidget(0, 0, title);
		
		// FALTA - Habilitar cuando esté disponible
		
//		int row = 1;
//
//		Label icon1 = new Label();
//		icon1.addStyleName(FiscalModelUtils.getAdministrationIcon(getMod347().getAdministration()));
//		tab.setWidget(row, 0, icon1 );
//		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
//		FlowPanel p1 = new FlowPanel();
//		p1.setStyleName(AON.AON_CSS.aonPadding2());
//		Button button1 = new Button("Descargar fichero para su presentaci\u00F3n");
//		button1.setStyleName(AON.AON_CSS.aonPaddingLeft());
//		button1.addStyleName(AON.AON_CSS.aonBorderNone());
//		button1.addStyleName(AON.AON_CSS.aonEvenBackground());
//		button1.addStyleName(AON.AON_CSS.aonClickable());
//		button1.addClickHandler( new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				if (getMod347().isFinished() || getMod347().isSent()) {
//					submitForm(MODEL347_FILE);
//				} else {
//					getCallback().showError("Para generar el fichero debe finalizar la confecci\u00F3n del modelo.");
//				}
//			}
//		});
//		p1.add(button1);
//		tab.setWidget(row, 1, p1 );
//		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
//		
//		if (mod347.getAdministration() == Administration.COMMON_TERRITORY) {
//			row++;
//			Label icon3 = new Label();
//			icon3.addStyleName(FiscalModelUtils.getAdministrationIcon(getMod347().getAdministration()));
//			tab.setWidget(row, 0, icon3);
//			tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
//			FlowPanel p3 = new FlowPanel();
//			p3.setStyleName(AON.AON_CSS.aonPadding2());
//			Button button3 = new Button("Validar e imprimir (PDF) via Agencia Tributaria (a partir de los datos guardados).");
//			button3.setStyleName(AON.AON_CSS.aonPaddingLeft());
//			button3.addStyleName(AON.AON_CSS.aonBorderNone());
//			button3.addStyleName(AON.AON_CSS.aonEvenBackground());
//			button3.addStyleName(AON.AON_CSS.aonClickable());
//			button3.addClickHandler(new ClickHandler() {
//				@Override
//				public void onClick(ClickEvent event) {
//					submitForm(MODEL347_PRINT);
//				}
//			});
//			p3.add(button3);
//			tab.setWidget(row, 1, p3);
//			tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
//		}
		
		//row++;
		panel.add(tab);
		
		return panel;
	}

	protected void paintDeclaredTab(TabLayoutPanel tabPanel, Integer selectedIndex) {
		// Evaluar lo diferentes paneles por administraciuon y/o ejercicio.
		if (getCallback().getMod347().getYear() >= 2014) {
			declaredManager = new Model347Declared2014( getCallback() , selectedIndex );
		}
		tabPanel.add( (Widget) declaredManager,  TAB_TEMPLATE.render("Declarados", AON.AON_CSS.aonIconInvoice()) );
	}

	protected void paintAssetsTab(TabLayoutPanel tabPanel, Integer selectedIndex) {
		// Evaluar lo diferentes paneles por administraciuon y/o ejercicio. 
		if (getCallback().getMod347().getYear() >= 2014) {
			assetManager = new Model347Asset2014( getCallback() , selectedIndex );
		}
		tabPanel.add( (Widget) assetManager,  TAB_TEMPLATE.render("Inmuebles", AON.AON_CSS.aonIconInvoice()) );
	}
	
	// Añade un manejador que hace que cada vez que cambiemos de pestaña se guarde la pestaña seleccionada
	// Se utilizará si pulsamos el botón guardar, para que cuando se repinte la pantalla vuelva a la pestaña donde estabamos
	protected void addTabPanelSelectionHandler(TabLayoutPanel tabPanel) {

		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				getCallback().tabPanelIndex = tabPanel.getSelectedIndex();
			}
			
		});
		
	}

	protected abstract LinkedList<Pair<String, String>> getInformationLinks();
	
	
}
