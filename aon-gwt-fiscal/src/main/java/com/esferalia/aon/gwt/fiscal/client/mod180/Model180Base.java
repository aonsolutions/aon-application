package com.esferalia.aon.gwt.fiscal.client.mod180;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.common.client.widget.AuditDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod180.Model180.IModel180Callback;
import com.esferalia.aon.gwt.fiscal.client.mod180.Model180.Model180Callback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
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

abstract class Model180Base extends DockLayoutPanel {

	static final String MODEL180_PRINT = "/aon_gwt_fiscal/ms/Model180Print";
	private static final String MODEL180_CERTIFICATE_PRINT = "/aon_gwt_fiscal/ms/Model180CertificatePrint";
	static final String MODEL180_FILE = "/aon_gwt_fiscal/ms/Model180File";
	static final String MODEL180_DRAFT = "/aon_gwt_fiscal/ms/Model180Draft";
	
	protected interface IModel180Detail extends IsWidget {
		Integer getSelectedPerceptorIndex();
	}
	interface TabLabelTemplate extends SafeHtmlTemplates {
		@Template("<span class=\"{1} aon-padding-right aon-padding-left-20\" style=\"width: auto !important\">{0}</span>")
		SafeHtml render(String label, String iconStyle);
	}
	protected static final TabLabelTemplate TAB_TEMPLATE = GWT.create(TabLabelTemplate.class);

	protected class Model180BaseCallback implements IModel180Callback {
		private Model180Callback cbk;
		
		protected Model180BaseCallback( Model180Callback cbk ) {
			this.cbk = cbk;
		}
		public Mod180 getMod180() {
			return Model180Base.this.getMod180();
		}
		@Override
		public void onAccept(Mod180 mod180) {
			cbk.onAccept(mod180);
		}
		@Override	
		public void onCancel() {
			cbk.onCancel();
		}
		@Override	
		public void onSelect(Model180ModuleOptions options, Mod180 mod180, Integer selectedIndex) {
			cbk.onSelect(options, mod180, selectedIndex);
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
		public void onNew(Model180ModuleOptions options) {
			cbk.onNew(options);
		}
//		@Override
//		public String getDomainName() {
//			return cbk.getDomainName();
//		}
//		@Override
//		public String getUser() {
//			return cbk.getUser();
//		}
//		@Override
//		public int getDomain() {
//			return cbk.getDomain();
//		}
	}
	
	
	private Mod180 mod180;
	private Model180BaseCallback callback;
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
	protected final Button duplicateButton = new Button();
	protected final Button auditButton = new Button();
	protected final Button draftButton = new Button();
	
	protected FormPanel diskForm = new FormPanel("_blank");
	protected Hidden mod180Hidden = new Hidden("mod180");
	protected Hidden domainIdHidden = new Hidden("domainId");
	protected Hidden domainNameHidden = new Hidden("domainName");
	protected Hidden userHidden = new Hidden("user");
	
	private IModel180Detail detailManager;
	
	public Model180Base(Mod180 mod180,Model180ModuleOptions options,Model180Callback cbk) {
		super(Unit.PX);
		select( options, mod180 );
		
		addNorth(getToolbarPanel(options,cbk), 25);
		
		SimplePanel modelPanel = new SimplePanel();
		FiscalModelUtils.paintHeaderTable(modelPanel, this.mod180 );
		addNorth(modelPanel, 65);
		
		ScrollPanel headerPanel = new ScrollPanel();
		headerPanel.setStyleName(AON.AON_CSS.aonScrollArea());
		headerPanel.setWidget( getDeclarationHeaderTable(options,cbk));
		addNorth(headerPanel, 45);
		
		this.callback = new Model180BaseCallback(cbk);


		setStyleName(AON.AON_CSS.aonSelector());
	}

	public Model180BaseCallback getCallback() {
		return callback;
	}
	protected Mod180 getMod180() {
		return mod180;
	}
	public void setMod180(Mod180 mod180) {
		this.mod180 = mod180;
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
	protected void select(Model180ModuleOptions options,Mod180 mod180) {
		setMod180(mod180);
		refreshToolbarState(options);
	}

	
	private Widget getToolbarPanel(Model180ModuleOptions options,Model180Callback cbk) {
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
		toolbar.setWidget(0, 0, new Label( "Modelo 180."));
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
				callback.onNew(options);
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
				if (getMod180().getYear() == 0) {
					throw new IllegalArgumentException(AON.MSG.requiredField(AON.MSG.fiscalYear()));
				}
				final PopupPanel popup = new PopupPanel(false, true);
				Label label = new Label(AON.MSG.processing());
				label.addStyleName(AON.AON_CSS.aonTimer());
				popup.add(label);
				popup.setGlassEnabled(true);
				popup.setAnimationEnabled(true);
				popup.center();
				Model180.SERVICE.saveMod180(options.getDomainName(), options.getUser(), options.getDomain(),
						getMod180(), new AsyncCallback<Mod180>() {
							@Override
							public void onSuccess(Mod180 result) {
								popup.hide();
								callback.onSelect(options, result, detailManager.getSelectedPerceptorIndex() );
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
		if (options.isBackButtonVisible() && options.hasExternalCallback()) {
			cancelButton.setText(AON.MSG.backAction());
		}
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
							if (options.isBackButtonVisible() && options.hasExternalCallback()) {
								options.getExternalCallback().onExit();
							} else {
								callback.onCancel();
							}
						}

						@Override
						public void onCancel() {
							cancelButton.setEnabled(true);
						}
					});
				} else {
					if (options.isBackButtonVisible() && options.hasExternalCallback()) {
						options.getExternalCallback().onExit();
					} else {
						callback.onCancel();
					}
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
						Model180.SERVICE.deleteMod180(options.getDomainName(), options.getUser(), options.getDomain(), getMod180(), new AsyncCallback<Void>() {
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
				Model180.SERVICE.changeStatusMod180(options.getDomainName(), options.getUser(), getMod180(), FiscalStatus.FINISHED, new AsyncCallback<Mod180>() {
					@Override
					public void onSuccess(Mod180 result) {
						callback.onSelect(options, result , detailManager.getSelectedPerceptorIndex() );
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
				Model180.SERVICE.changeStatusMod180(options.getDomainName(), options.getUser(), getMod180(), FiscalStatus.SENT, new AsyncCallback<Mod180>() {
					@Override
					public void onSuccess(Mod180 result) {
						callback.onSelect(options, result, detailManager.getSelectedPerceptorIndex());
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
				Model180.SERVICE.changeStatusMod180(options.getDomainName(), options.getUser(), getMod180(), FiscalStatus.PENDING, new AsyncCallback<Mod180>() {
					@Override
					public void onSuccess(Mod180 result) {
						callback.onSelect(options, result, detailManager.getSelectedPerceptorIndex());
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
		
		duplicateButton.setText(AON.MSG.duplicate());
		duplicateButton.setTitle(duplicateButton.getText());
		duplicateButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		duplicateButton.addStyleName(AON.AON_CSS.aonIconDuplicate());
		duplicateButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				duplicateButton.setEnabled(false);
				ConfirmDialog cd = new ConfirmDialog();
				String msg = "Desea duplicar el modelo para el ejercicio " + (mod180.getYear() + 1 ) + "?";
				cd.confirm(msg, new ConfirmDialogCallback() {
					
					@Override
					public void onCancel() {}
							
					@Override
					public void onAccept() {
						Model180.SERVICE.duplicateNextYear(options.getDomainName(), options.getUser(), options.getDomain(), 
								mod180.getId(), new AsyncCallback<Mod180>() {
							@Override
							public void onSuccess(Mod180 result) {
								callback.onCancel();
							}

							@Override
							public void onFailure(Throwable caught) {
								duplicateButton.setEnabled(true);
								callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
							}
						});
					}
				}); 
			}
		});
		buttonContainer.add(duplicateButton);

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
								submitForm(options,MODEL180_DRAFT);
							}
			
							@Override
							public void onCancel() {
								// Nothing
							}
						});
				} else {
					submitForm(options,MODEL180_DRAFT);
				}
			}
		});
		buttonContainer.add(draftButton);

		toolbarPanel.add(toolbar);
		
		FlowPanel formContainer = new FlowPanel();
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(mod180Hidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		formContainer.add(diskForm);
		toolbarPanel.add(formContainer);
		
		return toolbarPanel;
	}
	
	private void audit() {
		AuditDialog dialog = new AuditDialog();
		dialog.show(getMod180());
	}
	
	protected void submitForm(Model180ModuleOptions options, String action) {
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		mod180Hidden.setValue(String.valueOf(getMod180().getId()));
		domainNameHidden.setValue(options.getDomainName());
		domainIdHidden.setValue(String.valueOf(options.getDomain()));
		userHidden.setValue(options.getUser());
		diskForm.submit();
	}
	
	protected void identificationLabelChanged() {
		documentLabel.setText(getMod180().getDocument());
		nameLabel.setText(getMod180().getName());
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
		if (AonStringUtils.isEmpty(mod180.getComments())) {
			commentsButton.addStyleName(AON.AON_CSS.aonIconComment());
			commentsButton.removeStyleName(AON.AON_CSS.aonIconCommentRed());
		} else {
			commentsButton.addStyleName(AON.AON_CSS.aonIconCommentRed());
			commentsButton.removeStyleName(AON.AON_CSS.aonIconComment());
		}
		commentsButton.setTitle(mod180.getComments());
	}
	
	private void styleStatusLabel(Mod180 mod) {
		statusLabel.setText(mod.getStatus().getName());
		statusLabel.setStyleName(FiscalModelUtils.getStatusIconStyle(mod.getStatus()));
		statusLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
	}

	private void refreshToolbarState(Model180ModuleOptions options) {
		newButton.setVisible(!getMod180().isNew() && !options.isBackButtonVisible() && !options.hasExternalCallback());
		saveButton.setVisible(!getMod180().isFinished() && !getMod180().isSent());
		deleteButton.setVisible(!getMod180().isNew() && !getMod180().isFinished() && !getMod180().isSent());
		cancelButton.setVisible(true);
		markAsPendingButton.setVisible(!getMod180().isNew() &&
			(getMod180().getStatus() == FiscalStatus.FINISHED 
			|| getMod180().getStatus() == FiscalStatus.BATCHED
			|| getMod180().getStatus() == FiscalStatus.SENT
			|| getMod180().getStatus() == FiscalStatus.BLOCKED));
		markAsFinishedButton.setVisible(!getMod180().isNew() &&
			(getMod180().getStatus() == FiscalStatus.PENDING 
			|| getMod180().getStatus() == FiscalStatus.MISSING));
		markAsSentButton.setVisible(!getMod180().isNew() &&
			(getMod180().getStatus() == FiscalStatus.FINISHED));
		duplicateButton.setVisible(!getMod180().isNew());
		auditButton.setVisible(!getMod180().isNew());
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
		
		
		table.setWidget( 0, 0, new InlineLabel(AON.MSG.document()));
		table.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonPanelGridOdd());
		DocumentTextBox document = new DocumentTextBox();
		document.setValue(getMod180().getDocument());
		document.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod180().setDocument(document.getValue());
				identificationLabelChanged();
				markAsDirty();
			}
		});
		table.setWidget(0, 1, document);
		table.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonPanelGridEven());
		
		table.setWidget( 1, 0, new InlineLabel(AON.MSG.enterpriseName()));
		table.getCellFormatter().setStyleName(1,0, AON.AON_CSS.aonPanelGridOdd());
		TextBox name = new TextBox();
		name.setStyleName(AON.AON_CSS.aonInputText());
		name.setVisibleLength(45);
		name.setMaxLength(45);
		name.setValue(getMod180().getName());
		name.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod180().setName(name.getValue());
				identificationLabelChanged();
				markAsDirty();
			}
		});
		table.setWidget(1, 1, name);
		table.getCellFormatter().setStyleName(1,1, AON.AON_CSS.aonPanelGridEven());
		
		table.setWidget( 2, 0, new InlineLabel(AON.MSG.contactPerson()));
		table.getCellFormatter().setStyleName(2,0, AON.AON_CSS.aonPanelGridOdd());
		TextBox contactPerson = new TextBox();
		contactPerson.setStyleName(AON.AON_CSS.aonInputText());
		contactPerson.setMaxLength(40);
		contactPerson.setVisibleLength(30);
		contactPerson.setValue(getMod180().getContactPerson());
		contactPerson.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod180().setContactPerson(contactPerson.getValue());
				markAsDirty();
			}
		});
		table.setWidget(2, 1, contactPerson);
		table.getCellFormatter().setStyleName(2,1, AON.AON_CSS.aonPanelGridEven());

		table.setWidget( 3, 0, new InlineLabel(AON.MSG.contactPhone()));
		table.getCellFormatter().setStyleName(3,0, AON.AON_CSS.aonPanelGridOdd());
		TextBox contactPhone = new TextBox();
		contactPhone.setStyleName(AON.AON_CSS.aonInputText());
		contactPhone.setMaxLength(9);
		contactPhone.setVisibleLength(10);
		contactPhone.setValue(getMod180().getContactPhone());
		contactPhone.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod180().setContactPhone(contactPhone.getValue());
				markAsDirty();
			}
		});
		table.setWidget(3, 1, contactPhone);
		table.getCellFormatter().setStyleName(3,1, AON.AON_CSS.aonPanelGridEven());
		

		table.setWidget( 4, 0, new InlineLabel(AON.MSG.contactMail()));
		table.getCellFormatter().setStyleName(4,0, AON.AON_CSS.aonPanelGridOdd());
		TextBox contactMail = new TextBox();
		contactMail.setStyleName(AON.AON_CSS.aonInputText());
		contactMail.setMaxLength(50);
		contactMail.setVisibleLength(50);
		contactMail.setValue(getMod180().getContactMail());
		contactMail.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod180().setContactMail(contactMail.getValue());
				markAsDirty();
			}
		});
		table.setWidget(4, 1, contactMail);
		table.getCellFormatter().setStyleName(4,1, AON.AON_CSS.aonPanelGridEven());
		
		table.setWidget( 5, 0, new InlineLabel(AON.MSG.receipt()));
		table.getCellFormatter().setStyleName(5,0, AON.AON_CSS.aonPanelGridOdd());
		TextBox receipt = new TextBox();
		receipt.setStyleName(AON.AON_CSS.aonInputText());
		receipt.setMaxLength(13);
		receipt.setVisibleLength(13);
		receipt.setValue(getMod180().getReceipt());
		receipt.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod180().setReceipt(receipt.getValue());
				markAsDirty();
			}
		});
		table.setWidget(5, 1, receipt);
		table.getCellFormatter().setStyleName(5,1, AON.AON_CSS.aonPanelGridEven());
		
		table.setWidget( 6, 0, new InlineLabel(AON.MSG.previousDeclaration()));
		table.getCellFormatter().setStyleName(6,0, AON.AON_CSS.aonPanelGridOdd());
		TextBox replaced = new TextBox();
		replaced.setStyleName(AON.AON_CSS.aonInputText());
		replaced.setMaxLength(13);
		replaced.setVisibleLength(13);
		replaced.setEnabled(getMod180().isComplementary() || getMod180().isReplacement());
		replaced.setValue(getMod180().getReplacedReceipt());
		replaced.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod180().setReplacedReceipt(replaced.getValue());
				markAsDirty();
			}
		});
		table.setWidget(6, 1, replaced);
		table.getCellFormatter().setStyleName(6,1, AON.AON_CSS.aonPanelGridEven());

		declarationScrollPanel.setWidget(table);
		tabPanel.add(declarationScrollPanel, TAB_TEMPLATE.render(AON.MSG.declaration(), AON.AON_CSS.aonIconModel()));
	}
	
	private Widget getDeclarationHeaderTable(Model180ModuleOptions options, Model180Callback cbk) {
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
		documentLabel.setText(getMod180().getDocument());
		namePanel.add(documentLabel);
		nameLabel.setStyleName(AON.AON_CSS.aonMarginLeft());
		nameLabel.setText(getMod180().getName());
		namePanel.add(nameLabel);
		
		table.setWidget(0, 0, namePanel);
		table.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonNowrap());

		if (getMod180().isReplacement()) {
			replacedLabel.setText("Sustit.");
			replacedLabel.setStyleName(AON.AON_CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
		}
		if (getMod180().isComplementary()) {
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
		
		styleStatusLabel(getMod180());
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
				commentPanel.setStyleName( FiscalModelUtils.getAdministrationBG(getMod180().getAdministration()) );
				commentPanel.setStyleName(AON.AON_CSS.aonHeightAll());
				commentPanel.addStyleName(AON.AON_CSS.aonTextCenter());
				TextArea comment = new TextArea();
				comment.addValueChangeHandler(new ValueChangeHandler<String>() {
					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						getMod180().setComments(event.getValue());
						styleCommentsButton();
						Model180.SERVICE.saveCommentsMod180(options.getDomainName(), options.getUser(), getMod180(), new AsyncCallback<Mod180>() {
							@Override
							public void onSuccess(Mod180 result) {
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
				comment.setText(mod180.getComments());
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
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(getMod180().getAdministration()));
		tab.setWidget(0, 0, title);
		
		int row = 1;
		for (Pair<String, String> pair : getInformationLinks()) {
			Label icon = new Label();
			icon.addStyleName(FiscalModelUtils.getAdministrationIcon(getMod180().getAdministration()));
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

	protected FlowPanel getAdministrationPanel(Model180ModuleOptions options, Model180Callback cbk) {
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
		Label title = new Label("Presentaci\u00F3n del modelo");
		tab.getFlexCellFormatter().setColSpan(0, 0, 2);
		tab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonMarginTop());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(getMod180().getAdministration()));
		tab.setWidget(0, 0, title);
		
		int row = 1;

		Label icon1 = new Label();
		icon1.addStyleName(FiscalModelUtils.getAdministrationIcon(getMod180().getAdministration()));
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
				if (getMod180().isFinished() || getMod180().isSent()) {
					submitForm(options, MODEL180_FILE);
				} else {
					getCallback().showError("Para generar el fichero debe finalizar la confecci\u00F3n del modelo.");
				}
			}
		});
		p1.add(button1);
		tab.setWidget(row, 1, p1 );
		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;
		
		Label icon3 = new Label();
		icon3.addStyleName(FiscalModelUtils.getAdministrationIcon(getMod180().getAdministration()));
		tab.setWidget(row, 0, icon3 );
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		FlowPanel p3 = new FlowPanel();
		p3.setStyleName(AON.AON_CSS.aonPadding2());
		Button button3 = new Button("Validar e imprimir (PDF) via Agencia Tributaria (a partir de los datos guardados).");
		button3.setStyleName(AON.AON_CSS.aonPaddingLeft());
		button3.addStyleName(AON.AON_CSS.aonBorderNone());
		button3.addStyleName(AON.AON_CSS.aonEvenBackground());
		button3.addStyleName(AON.AON_CSS.aonClickable());
		button3.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				submitForm(options,MODEL180_PRINT);
			}
		});
		p3.add(button3);
		tab.setWidget(row, 1, p3 );
		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;
		panel.add(tab);
		
		FlexTable tab2 = new FlexTable();
		tab2.getColumnFormatter().setWidth(0, "30px");
		tab2.getColumnFormatter().setWidth(1, "auto");
		tab2.setStyleName(AON.AON_CSS.aonWidth90Percent());
		tab2.addStyleName(AON.AON_CSS.aonBlockCenter());
		tab2.addStyleName(AON.AON_CSS.aonPanelGrid());
		tab2.addStyleName(AON.AON_CSS.aonMarginTop());
		Label title2 = new Label("Otros");
		tab2.getFlexCellFormatter().setColSpan(0, 0, 2);
		tab2.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		tab2.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonMarginTop());
		tab2.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		tab2.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(getMod180().getAdministration()));
		tab2.setWidget(0, 0, title2);
		
		row = 1;
		Label icon2 = new Label();
		icon2.addStyleName(AON.AON_CSS.aonIconPdf());
		tab2.setWidget(row, 0, icon2 );
		tab2.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		FlowPanel p2 = new FlowPanel();
		p2.setStyleName(AON.AON_CSS.aonPadding2());
		Button button2 = new Button("Certificado de retenciones e ingresos a cuenta del I.R.P.F.");
		button2.setStyleName(AON.AON_CSS.aonPaddingLeft());
		button2.addStyleName(AON.AON_CSS.aonBorderNone());
		button2.addStyleName(AON.AON_CSS.aonEvenBackground());
		button2.addStyleName(AON.AON_CSS.aonClickable());
		button2.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (getMod180().isFinished() || getMod180().isSent()) {
					submitForm(options,MODEL180_CERTIFICATE_PRINT);
				} else {
					getCallback().showError("Para imprimir los certificados, debe finalizar la confecci\u00F3n del modelo.");
				}
			}
		});
		p2.add(button2);
		tab2.setWidget(row, 1, p2 );
		tab2.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;
		
		panel.add(tab2);
		
		return panel;
	}

	protected void paintPerceptorsTab(TabLayoutPanel tabPanel, Integer selectedIndex) {
		// Evaluar lo diferentes paneles por administraciuon y/o ejercicio.
		if ( getCallback().getMod180().getYear() < 2020 ) {
			detailManager = new Model180Detail2017( getCallback() , selectedIndex );
		} else {
			detailManager = new Model180Detail2020( getCallback() , selectedIndex );
		}
		tabPanel.add( (Widget) detailManager,  TAB_TEMPLATE.render(AON.MSG.receiverList(), AON.AON_CSS.aonIconInvoice()) );
		
	}

	protected abstract LinkedList<Pair<String, String>> getInformationLinks();
	
	
}
