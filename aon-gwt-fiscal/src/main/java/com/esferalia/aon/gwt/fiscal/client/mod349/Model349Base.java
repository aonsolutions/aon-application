package com.esferalia.aon.gwt.fiscal.client.mod349;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.common.client.widget.AuditDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod349.Model349.IModel349Callback;
import com.esferalia.aon.gwt.fiscal.client.mod349.Model349.Model349Callback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
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

abstract class Model349Base extends DockLayoutPanel {

	static final String MODEL349_PRINT = "/aon_gwt_fiscal/ms/Model349Print";
	static final String MODEL349_FILE = "/aon_gwt_fiscal/ms/Model349File";
	static final String MODEL349_DRAFT = "/aon_gwt_fiscal/ms/Model349Draft";
	
	protected interface IModel349Detail extends IsWidget {
		Integer getSelectedOperatorIndex(); 		 
	}
	
	interface TabLabelTemplate extends SafeHtmlTemplates {
		@Template("<span class=\"{1} aon-padding-right aon-padding-left-20\" style=\"width: auto !important\">{0}</span>")
		SafeHtml render(String label, String iconStyle);
	}
	protected static final TabLabelTemplate TAB_TEMPLATE = GWT.create(TabLabelTemplate.class);

	protected class Model349BaseCallback implements IModel349Callback {
		private Model349Callback cbk;
		
		protected Model349BaseCallback( Model349Callback cbk ) {
			this.cbk = cbk;
		}
		public Mod349 getMod349() {
			return Model349Base.this.getMod349();
		}
		@Override
		public void onAccept(Mod349 mod349) {
			cbk.onAccept(mod349);
		}
		@Override	
		public void onCancel() {
			cbk.onCancel();
		}		
		@Override
		public void onSelect(Mod349 mod349, Integer selectedIndex) {
			cbk.onSelect(mod349, selectedIndex);
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
		public void showBreakdownPanel(String htmlText) {
			cbk.showBreakdownPanel(htmlText);
		}
		@Override
		public void cleanBreakdownPanel() {
			cbk.cleanBreakdownPanel();
		}
		@Override
		public String getDomainName() {
			return cbk.getDomainName();
		}
		@Override
		public String getUser() {
			return cbk.getUser();
		}
		@Override
		public int getDomain() {
			return cbk.getDomain();
		}
	}
	
	private Mod349 mod349;
	private Model349BaseCallback callback;
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
	protected final Button draftButton = new Button();
	
	protected FormPanel diskForm = new FormPanel("_blank");
	protected Hidden mod349Hidden = new Hidden("mod349");
	protected Hidden domainIdHidden = new Hidden("domainId");
	protected Hidden domainNameHidden = new Hidden("domainName");
	protected Hidden userHidden = new Hidden("user");
	
	private IModel349Detail detailManager; 
	
	public Model349Base(Mod349 mod349,Model349Callback cbk) {
		super(Unit.PX);
		select( mod349 );
		
		addNorth(getToolbarPanel(cbk), 25);
		
		SimplePanel modelPanel = new SimplePanel();
		FiscalModelUtils.paintHeaderTable(modelPanel, this.mod349 );
		addNorth(modelPanel, 65);
		
		ScrollPanel headerPanel = new ScrollPanel();
		headerPanel.setStyleName(AON.AON_CSS.aonScrollArea());
		headerPanel.setWidget( getDeclarationHeaderTable(cbk));
		addNorth(headerPanel, 45);
		
		this.callback = new Model349BaseCallback(cbk);

		setStyleName(AON.AON_CSS.aonSelector());
	}

	public Model349BaseCallback getCallback() {
		return callback;
	}
	protected Mod349 getMod349() {
		return mod349;
	}
	public void setMod349(Mod349 mod349) {
		this.mod349 = mod349;
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
	protected void select( Mod349 mod349) {
		setMod349(mod349);
		refreshToolbarState();
	}

	
	private Widget getToolbarPanel(Model349Callback cbk) {
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
		toolbar.setWidget(0, 0, new Label( "Modelo 349."));
		toolbar.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonFindingToolbar());
		
		// Botón Nuevo
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
		
		// Botón Guardar
		saveButton.setText(AON.MSG.saveAction());
		saveButton.setTitle(saveButton.getText());
		saveButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		saveButton.addStyleName(AON.AON_CSS.aonIconSave());
		saveButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (getMod349().getYear() == 0) {
					throw new IllegalArgumentException(AON.MSG.requiredField(AON.MSG.fiscalYear()));
				}
				final PopupPanel popup = new PopupPanel(false, true);
				Label label = new Label(AON.MSG.processing());
				label.addStyleName(AON.AON_CSS.aonTimer());
				popup.add(label);
				popup.setGlassEnabled(true);
				popup.setAnimationEnabled(true);
				popup.center();
				Model349.SERVICE.saveMod349(cbk.getDomainName(),cbk.getUser(),cbk.getDomain(),
						getMod349(), new AsyncCallback<Mod349>() {
							@Override
							public void onSuccess(Mod349 result) {
								popup.hide();
								callback.onSelect(result, detailManager.getSelectedOperatorIndex());
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
		
		// Botón Cancelar
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

		// Botón Borrar
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
						Model349.SERVICE.deleteMod349(cbk.getDomainName(),cbk.getUser(),cbk.getDomain(), getMod349(), new AsyncCallback<Void>() {
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
		
		// Botón Marcar Finalizado
		markAsFinishedButton.setText(AON.MSG.finish());
		markAsFinishedButton.setTitle(markAsFinishedButton.getText());
		markAsFinishedButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		markAsFinishedButton.addStyleName(AON.AON_CSS.aonIconPointLightGreen());
		markAsFinishedButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				markAsFinishedButton.setEnabled(false);
				Model349.SERVICE.changeStatusMod349(cbk.getDomainName(),cbk.getUser(),getMod349(), FiscalStatus.FINISHED, new AsyncCallback<Mod349>() {
					@Override
					public void onSuccess(Mod349 result) {						
						callback.onSelect(result , detailManager.getSelectedOperatorIndex() ); 
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

		// Botón Marcar Enviado
		markAsSentButton.setText(AON.MSG.markAsSent());
		markAsSentButton.setTitle(markAsSentButton.getText());
		markAsSentButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		markAsSentButton.addStyleName(AON.AON_CSS.aonIconPointGreen());
		markAsSentButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				markAsSentButton.setEnabled(false);
				Model349.SERVICE.changeStatusMod349(cbk.getDomainName(),cbk.getUser(), getMod349(), FiscalStatus.SENT, new AsyncCallback<Mod349>() {
					@Override
					public void onSuccess(Mod349 result) {						
						callback.onSelect(result, detailManager.getSelectedOperatorIndex());
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

		// Botón Marcar Pendiente
		markAsPendingButton.setText(AON.MSG.reopen());
		markAsPendingButton.setTitle(markAsPendingButton.getText());
		markAsPendingButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		markAsPendingButton.addStyleName(AON.AON_CSS.aonIconPointOrange());
		markAsPendingButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				markAsPendingButton.setEnabled(false);
				Model349.SERVICE.changeStatusMod349(cbk.getDomainName(),cbk.getUser(), getMod349(), FiscalStatus.PENDING, new AsyncCallback<Mod349>() {
					@Override
					public void onSuccess(Mod349 result) {						
						callback.onSelect(result, detailManager.getSelectedOperatorIndex());
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
		
		// Botón Auditoría
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
		
		draftButton.setText(AON.MSG.draft());
		draftButton.setTitle(draftButton.getText());
		draftButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		draftButton.addStyleName(AON.AON_CSS.aonIconExcel());
		draftButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (isDirty()) {
					new ConfirmDialog().confirm(AON.MSG.draftPrint(),AON.MSG.draftPrintNote() 
							, new ConfirmDialogCallback() {
							
							@Override
							public void onAccept() {
								submitForm(cbk,MODEL349_DRAFT);
							}
			
							@Override
							public void onCancel() {
								// Nothing
							}
						});
				} else {
					submitForm(cbk,MODEL349_DRAFT);
				}
			}
		});
		buttonContainer.add(draftButton);

		toolbarPanel.add(toolbar);
		
		FlowPanel formContainer = new FlowPanel();
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(mod349Hidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		formContainer.add(diskForm);
		toolbarPanel.add(formContainer);
		
		return toolbarPanel;
	}
	
	private void audit() {
		AuditDialog dialog = new AuditDialog();
		dialog.show(getMod349());
	}
	
	protected void submitForm(Model349Callback cbk,String action) {
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		mod349Hidden.setValue(String.valueOf(getMod349().getId()));
		domainIdHidden.setValue(String.valueOf(cbk.getDomain()));
		domainNameHidden.setValue(cbk.getDomainName());
		userHidden.setValue(cbk.getUser());
		diskForm.submit();
	}
	
	protected void identificationLabelChanged() {
		documentLabel.setText(getMod349().getDocument());
		nameLabel.setText(getMod349().getName());
	}

	protected void styleDirtyLabel() {
		dirtyPanel.clear();
		
		// Indicar que el modelo se ha modificado
		if (isDirty()) {
			InlineLabel dirtyLabel = new InlineLabel("[*]");
			dirtyLabel.setStyleName(AON.AON_CSS.aonColorRed());
			dirtyPanel.add(dirtyLabel);
		}
		
		// Indicar que el modelo ha sido calculado por diferencias
		if (this.mod349.isDiffEnabled()) {
			InlineLabel diffLabel = new InlineLabel("[DIF.]");
			diffLabel.setStyleName(AON.AON_CSS.aonMarginLeft5());
			diffLabel.setTitle("C\u00E1lculo por diferencia habilitado");
			dirtyPanel.add(diffLabel);
		}
		
		// Indicar si se han realizado algún ajuste o introducción manual del importe a declarar
		// se asume eso si el importe a declarar es distinto de acumulado-declarado
		boolean adjusted = false;
		for (Mod349Detail det : this.mod349.getDetails() ) {			
			if (det.isAdjusted()) {
				adjusted = true;
				break;
			}
		}
		if (adjusted) {
			InlineLabel adjLabel = new InlineLabel("[AJUSTES]");
			adjLabel.setStyleName(AON.AON_CSS.aonMarginLeft5());
			adjLabel.addStyleName(AON.AON_CSS.aonColoRoyalblue());
			adjLabel.setTitle("Ajustes realizados o introducci\u00F3n manual de datos");
			dirtyPanel.add(adjLabel);
		}
	}
	
	private void styleCommentsButton() {
		if (AonStringUtils.isEmpty(mod349.getComments())) {
			commentsButton.addStyleName(AON.AON_CSS.aonIconComment());
			commentsButton.removeStyleName(AON.AON_CSS.aonIconCommentRed());
		} else {
			commentsButton.addStyleName(AON.AON_CSS.aonIconCommentRed());
			commentsButton.removeStyleName(AON.AON_CSS.aonIconComment());
		}
		commentsButton.setTitle(mod349.getComments());
	}
	
	private void styleStatusLabel(Mod349 mod) {
		statusLabel.setText(mod.getStatus().getName());
		statusLabel.setStyleName(FiscalModelUtils.getStatusIconStyle(mod.getStatus()));
		statusLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
	}

	private void refreshToolbarState() {
		newButton.setVisible(!getMod349().isNew());
		saveButton.setVisible(!getMod349().isFinished() && !getMod349().isSent());
		deleteButton.setVisible(!getMod349().isNew() && !getMod349().isFinished() && !getMod349().isSent());
		cancelButton.setVisible(true);
		markAsPendingButton.setVisible(!getMod349().isNew() &&
			(getMod349().getStatus() == FiscalStatus.FINISHED 
			|| getMod349().getStatus() == FiscalStatus.BATCHED
			|| getMod349().getStatus() == FiscalStatus.SENT
			|| getMod349().getStatus() == FiscalStatus.BLOCKED));
		markAsFinishedButton.setVisible(!getMod349().isNew() &&
			(getMod349().getStatus() == FiscalStatus.PENDING 
			|| getMod349().getStatus() == FiscalStatus.MISSING));
		markAsSentButton.setVisible(!getMod349().isNew() &&
			(getMod349().getStatus() == FiscalStatus.FINISHED));
		
		auditButton.setVisible(!getMod349().isNew());
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
		document.setValue(getMod349().getDocument());
		document.setMaxLength(9);
		document.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod349().setDocument(document.getValue());
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
		name.setVisibleLength(40);
		name.setMaxLength(40);
		name.setValue(getMod349().getName());
		name.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod349().setName(name.getValue());
				identificationLabelChanged();
				markAsDirty();
			}
		});
		table.setWidget(1, 1, name);
		table.getCellFormatter().setStyleName(1,1, AON.AON_CSS.aonPanelGridEven());
		
		// Teléfono Persona de Contacto
		table.setWidget( 2, 0, new InlineLabel(AON.MSG.contactPhone()));
		table.getCellFormatter().setStyleName(2,0, AON.AON_CSS.aonPanelGridOdd());
		TextBox contactPhone = new TextBox();
		contactPhone.setStyleName(AON.AON_CSS.aonInputText());
		contactPhone.setMaxLength(9);
		contactPhone.setVisibleLength(10);
		contactPhone.setValue(getMod349().getContactPhone());
		contactPhone.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod349().setContactPhone(contactPhone.getValue());
				markAsDirty();
			}
		});
		table.setWidget(2, 1, contactPhone);
		table.getCellFormatter().setStyleName(2,1, AON.AON_CSS.aonPanelGridEven());
		
		// Nombre Persona de Contacto
		table.setWidget( 3, 0, new InlineLabel(AON.MSG.contactPerson()));
		table.getCellFormatter().setStyleName(3,0, AON.AON_CSS.aonPanelGridOdd());
		TextBox contactPerson = new TextBox();
		contactPerson.setStyleName(AON.AON_CSS.aonInputText());
		contactPerson.setMaxLength(40);
		contactPerson.setVisibleLength(40);
		contactPerson.setValue(getMod349().getContactPerson());
		contactPerson.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod349().setContactPerson(contactPerson.getValue());
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
		contactMail.setValue(getMod349().getContactMail());
		contactMail.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod349().setContactMail(contactMail.getValue());
				markAsDirty();
			}
		});
		table.setWidget(4, 1, contactMail);
		table.getCellFormatter().setStyleName(4,1, AON.AON_CSS.aonPanelGridEven());		
		
		// Número declaración anterior
		// Solo se habilita si es Territorio Común o Alava y está marcado complementaria o sustitutiva
		// o bien es Navarra y está marcado sustitutiva (complementaria en Navarra no lleva numero declaración anterior)
		// además ni Gipuzcoa ni Bizkaia llevan numero de declaración anterior		
		table.setWidget( 5, 0, new InlineLabel(AON.MSG.previousDeclaration()));
		table.getCellFormatter().setStyleName(5,0, AON.AON_CSS.aonPanelGridOdd());
		TextBox replaced = new TextBox();
		replaced.setStyleName(AON.AON_CSS.aonInputText());
		replaced.setMaxLength(13);
		replaced.setVisibleLength(13);
		replaced.setEnabled( ((getMod349().getAdministration() == Administration.COMMON_TERRITORY || getMod349().getAdministration() == Administration.ALAVA) && (getMod349().isComplementary() || getMod349().isReplacement())) ||
				             (getMod349().getAdministration() == Administration.NAVARRA && getMod349().isReplacement()));		
		replaced.setValue(getMod349().getReplacedNumber());
		replaced.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod349().setReplacedNumber(replaced.getValue());
				markAsDirty();
			}
		});
		table.setWidget(5, 1, replaced);
		table.getCellFormatter().setStyleName(5,1, AON.AON_CSS.aonPanelGridEven());		
		
		// Cambio en la periodicidad
		table.setWidget( 6, 0, new InlineLabel("Indicador cambio periodicidad"));
		table.getCellFormatter().setStyleName(6,0, AON.AON_CSS.aonPanelGridOdd());
		CheckBox periodicity = new CheckBox();		
		periodicity.setValue(getMod349().isPeriodicityChange());
		periodicity.addClickHandler(new ClickHandler() {			
			
			@Override
			public void onClick(ClickEvent event) {
				getMod349().setPeriodicityChange(periodicity.getValue());
				markAsDirty();				
			}
		});   
		table.setWidget(6, 1, periodicity);
		table.getCellFormatter().setStyleName(6,1, AON.AON_CSS.aonPanelGridEven());
		
		// NIF Representante Legal
		table.setWidget( 7, 0, new InlineLabel("NIF Representante Legal"));
		table.getCellFormatter().setStyleName(7,0, AON.AON_CSS.aonPanelGridOdd());
		DocumentTextBox representativeDocument = new DocumentTextBox();
		representativeDocument.setValue(getMod349().getRepresentativeDocument());
		representativeDocument.setMaxLength(9);
		representativeDocument.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod349().setRepresentativeDocument(representativeDocument.getValue());
				identificationLabelChanged();
				markAsDirty();
			}
		});
		table.setWidget(7, 1, representativeDocument);
		table.getCellFormatter().setStyleName(7,1, AON.AON_CSS.aonPanelGridEven());
		
		declarationScrollPanel.setWidget(table);
		tabPanel.add(declarationScrollPanel, TAB_TEMPLATE.render(AON.MSG.declaration(), AON.AON_CSS.aonIconModel()));
	}
	
	private Widget getDeclarationHeaderTable(Model349Callback cbk) {
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
		documentLabel.setText(getMod349().getDocument());
		namePanel.add(documentLabel);
		nameLabel.setStyleName(AON.AON_CSS.aonMarginLeft());
		nameLabel.setText(getMod349().getName());
		namePanel.add(nameLabel);
		
		table.setWidget(0, 0, namePanel);
		table.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonNowrap());

		if (getMod349().isReplacement()) {
			replacedLabel.setText("Sustit.");
			replacedLabel.setStyleName(AON.AON_CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
		}
		if (getMod349().isComplementary()) {
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
		
		styleStatusLabel(getMod349());
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
				commentPanel.setStyleName( FiscalModelUtils.getAdministrationBG(getMod349().getAdministration()) );
				commentPanel.setStyleName(AON.AON_CSS.aonHeightAll());
				commentPanel.addStyleName(AON.AON_CSS.aonTextCenter());
				TextArea comment = new TextArea();
				comment.addValueChangeHandler(new ValueChangeHandler<String>() {
					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						getMod349().setComments(event.getValue());
						styleCommentsButton();
						Model349.SERVICE.saveCommentsMod349(cbk.getDomainName(),cbk.getUser(), getMod349(), new AsyncCallback<Mod349>() {
							@Override
							public void onSuccess(Mod349 result) {
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
				comment.setText(mod349.getComments());
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
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(getMod349().getAdministration()));
		tab.setWidget(0, 0, title);
		
		int row = 1;
		for (Pair<String, String> pair : getInformationLinks()) {
			Label icon = new Label();
			icon.addStyleName(FiscalModelUtils.getAdministrationIcon(getMod349().getAdministration()));
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

	protected FlowPanel getAdministrationPanel(Model349Callback cbk) {
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
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(getMod349().getAdministration()));
		tab.setWidget(0, 0, title);
		
		int row = 1;

		Label icon1 = new Label();
		icon1.addStyleName(FiscalModelUtils.getAdministrationIcon(getMod349().getAdministration()));
		tab.setWidget(row, 0, icon1 );
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		FlowPanel p1 = new FlowPanel();
		p1.setStyleName(AON.AON_CSS.aonPadding2());
		Button button1 = new Button("Descargar fichero para su presentaci\u00F3n");
		button1.setStyleName(AON.AON_CSS.aonPaddingLeft());
		button1.addStyleName(AON.AON_CSS.aonBorderNone());
		button1.addStyleName(AON.AON_CSS.aonEvenBackground());
		button1.addStyleName(AON.AON_CSS.aonClickable());
		button1.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (getMod349().isFinished() || getMod349().isSent()) {
					submitForm(cbk,MODEL349_FILE);
				} else {
					getCallback().showError("Para generar el fichero debe finalizar la confecci\u00F3n del modelo.");
				}
			}
		});
		p1.add(button1);
		tab.setWidget(row, 1, p1 );
		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		
		// Solo si es Territorio Común está el enlace para validar e imprimir borrador
		if (getMod349().getAdministration() == Administration.COMMON_TERRITORY) {
			row++;
			Label icon3 = new Label();
			icon3.addStyleName(FiscalModelUtils.getAdministrationIcon(getMod349().getAdministration()));
			tab.setWidget(row, 0, icon3 );
			tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
			FlowPanel p3 = new FlowPanel();
			p3.setStyleName(AON.AON_CSS.aonPadding2());
			Button button3 = new Button("Servicio de validaci\u00F3n y prueba (Borrador PDF) via Agencia Tributaria (a partir de los datos guardados).");
			button3.setStyleName(AON.AON_CSS.aonPaddingLeft());
			button3.addStyleName(AON.AON_CSS.aonBorderNone());
			button3.addStyleName(AON.AON_CSS.aonEvenBackground());
			button3.addStyleName(AON.AON_CSS.aonClickable());
			button3.addClickHandler( new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					// Servicio de validación y prueba, controlar ejercicio, solo a partir de 2014 (incluido)
					if (getMod349().getYear() >= 2014) {
						submitForm(cbk,MODEL349_PRINT);
					} else {
						getCallback().showError("Servicio de validaci\u00F3n y prueba no disponible para el ejercicio del modelo.");
					}
				}
			});
			p3.add(button3);
			tab.setWidget(row, 1, p3 );
			tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		}		
		
		panel.add(tab);
		
		return panel;
	}

	protected void paintOperatorsTab(TabLayoutPanel tabPanel, Integer selectedIndex) {		
		// Evaluar lo diferentes paneles por administraciuon y/o ejercicio.		 
		detailManager = new Model349Detail( getCallback() , selectedIndex );		
		tabPanel.add( (Widget) detailManager,  TAB_TEMPLATE.render("Relaci\u00F3n de Operaciones", AON.AON_CSS.aonIconInvoice()) ); 
	}

	protected abstract LinkedList<Pair<String, String>> getInformationLinks();
	
	
}
