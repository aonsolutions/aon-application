package com.esferalia.aon.gwt.fiscal.client.mod190;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.common.client.widget.AuditDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190.IModel190Callback;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190.Model190Callback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
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

abstract class Model190Base extends DockLayoutPanel {

	static final String MODEL190_PRINT = "/aon_gwt_fiscal/ms/Model190Print";
	private static final String MODEL190_CERTIFICATE_PRINT = "/aon_gwt_fiscal/ms/Model190CertificatePrint";
	static final String MODEL190_FILE = "/aon_gwt_fiscal/ms/Model190File";
	
	protected interface IModel190Detail extends IsWidget {
		Integer getSelectedPerceptorIndex();
	}
	interface TabLabelTemplate extends SafeHtmlTemplates {
		@Template("<span class=\"{1} aon-padding-right aon-padding-left-20\" style=\"width: auto !important\">{0}</span>")
		SafeHtml render(String label, String iconStyle);
	}
	protected static final TabLabelTemplate TAB_TEMPLATE = GWT.create(TabLabelTemplate.class);

	protected class Model190BaseCallback implements IModel190Callback {
		private Model190Callback cbk;
		
		protected Model190BaseCallback( Model190Callback cbk ) {
			this.cbk = cbk;
		}
		public Mod190 getMod190() {
			return Model190Base.this.getMod190();
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
		public void onSelect(Mod190 mod190, Integer selectedIndex) {
			cbk.onSelect(mod190, selectedIndex);
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
	
	
	private Mod190 mod190;
	private Model190BaseCallback callback;
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
	
	protected FormPanel diskForm = new FormPanel("_blank");
	protected Hidden mod190Hidden = new Hidden("mod190");
	protected Hidden domainIdHidden = new Hidden("domainId");
	protected Hidden domainNameHidden = new Hidden("domainName");
	protected Hidden userHidden = new Hidden("user");
	
	private IModel190Detail detailManager;
	
	public Model190Base(Mod190 mod190,Model190Callback cbk) {
		super(Unit.PX);
		select( mod190 );
		
		addNorth(getToolbarPanel(cbk), 25);
		
		SimplePanel modelPanel = new SimplePanel();
		FiscalModelUtils.paintHeaderTable(modelPanel, this.mod190 );
		addNorth(modelPanel, 65);
		
		ScrollPanel headerPanel = new ScrollPanel();
		headerPanel.setStyleName(AON.AON_CSS.aonScrollArea());
		headerPanel.setWidget( getDeclarationHeaderTable(cbk));
		addNorth(headerPanel, 45);
		
		this.callback = new Model190BaseCallback(cbk);


		setStyleName(AON.AON_CSS.aonSelector());
	}
	
	public IModel190Detail getDetailManager() {
		return detailManager;
	}
	public void setDetailManager(IModel190Detail detailManager) {
		this.detailManager = detailManager;
	}

	public Model190BaseCallback getCallback() {
		return callback;
	}
	protected Mod190 getMod190() {
		return mod190;
	}
	public void setMod190(Mod190 mod190) {
		this.mod190 = mod190;
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
	protected void select( Mod190 mod190) {
		setMod190(mod190);
		refreshToolbarState();
	}

	
	private Widget getToolbarPanel(Model190Callback cbk) {
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
		toolbar.setWidget(0, 0, new Label( "Modelo 190."));
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
				if (getMod190().getYear() == 0) {
					throw new IllegalArgumentException(AON.MSG.requiredField(AON.MSG.fiscalYear()));
				}
				final PopupPanel popup = new PopupPanel(false, true);
				Label label = new Label(AON.MSG.processing());
				label.addStyleName(AON.AON_CSS.aonTimer());
				popup.add(label);
				popup.setGlassEnabled(true);
				popup.setAnimationEnabled(true);
				popup.center();
				Model190.SERVICE.save(cbk.getDomainName(), cbk.getUser(), cbk.getDomain(),
						getMod190(), new AsyncCallback<Mod190>() {
							@Override
							public void onSuccess(Mod190 result) {
								popup.hide();
								callback.onSelect(result, detailManager.getSelectedPerceptorIndex() );
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
						Model190.SERVICE.delete(cbk.getDomainName(), cbk.getUser(), cbk.getDomain(), getMod190(), new AsyncCallback<Void>() {
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
				Model190.SERVICE.changeStatus(cbk.getDomainName(), cbk.getUser(), getMod190(), FiscalStatus.FINISHED, new AsyncCallback<Mod190>() {
					@Override
					public void onSuccess(Mod190 result) {
						callback.onSelect(result , detailManager.getSelectedPerceptorIndex() );
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
				Model190.SERVICE.changeStatus(cbk.getDomainName(), cbk.getUser(), getMod190(), FiscalStatus.SENT, new AsyncCallback<Mod190>() {
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
				Model190.SERVICE.changeStatus(cbk.getDomainName(), cbk.getUser(), getMod190(), FiscalStatus.PENDING, new AsyncCallback<Mod190>() {
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
				String msg = "Desea duplicar el modelo para el ejercicio " + (mod190.getYear() + 1 ) + "?";
				cd.confirm(msg, new ConfirmDialogCallback() {
					
					@Override
					public void onCancel() {}
							
					@Override
					public void onAccept() {
						Model190.SERVICE.duplicateNextYear(cbk.getDomainName(), cbk.getUser(), cbk.getDomain(), 
								mod190.getId(), new AsyncCallback<Mod190>() {
							@Override
							public void onSuccess(Mod190 result) {
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

		toolbarPanel.add(toolbar);
		
		FlowPanel formContainer = new FlowPanel();
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(mod190Hidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);		
		formContainer.add(diskForm);
		toolbarPanel.add(formContainer);
		
		return toolbarPanel;
	}
	
	private void audit() {
		AuditDialog dialog = new AuditDialog();
		dialog.show(getMod190());
	}
	
	protected void submitForm(Model190Callback cbk, String action) {
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		mod190Hidden.setValue(String.valueOf(getMod190().getId()));
		domainIdHidden.setValue(String.valueOf(cbk.getDomain()));
		domainNameHidden.setValue(cbk.getDomainName());
		userHidden.setValue(cbk.getDomainName());
		diskForm.submit();
	}
	
	protected void identificationLabelChanged() {
		documentLabel.setText(getMod190().getDocument());
		nameLabel.setText(getMod190().getName());
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
		if (AonStringUtils.isEmpty(mod190.getComments())) {
			commentsButton.addStyleName(AON.AON_CSS.aonIconComment());
			commentsButton.removeStyleName(AON.AON_CSS.aonIconCommentRed());
		} else {
			commentsButton.addStyleName(AON.AON_CSS.aonIconCommentRed());
			commentsButton.removeStyleName(AON.AON_CSS.aonIconComment());
		}
		commentsButton.setTitle(mod190.getComments());
	}
	
	private void styleStatusLabel(Mod190 mod) {
		statusLabel.setText(mod.getStatus().getName());
		statusLabel.setStyleName(FiscalModelUtils.getStatusIconStyle(mod.getStatus()));
		statusLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
	}

	private void refreshToolbarState() {
		newButton.setVisible(!getMod190().isNew());
		saveButton.setVisible(!getMod190().isFinished() && !getMod190().isSent());
		deleteButton.setVisible(!getMod190().isNew() && !getMod190().isFinished() && !getMod190().isSent());
		cancelButton.setVisible(true);
		markAsPendingButton.setVisible(!getMod190().isNew() &&
			(getMod190().getStatus() == FiscalStatus.FINISHED 
			|| getMod190().getStatus() == FiscalStatus.BATCHED
			|| getMod190().getStatus() == FiscalStatus.SENT
			|| getMod190().getStatus() == FiscalStatus.BLOCKED));
		markAsFinishedButton.setVisible(!getMod190().isNew() &&
			(getMod190().getStatus() == FiscalStatus.PENDING 
			|| getMod190().getStatus() == FiscalStatus.MISSING));
		markAsSentButton.setVisible(!getMod190().isNew() &&
			(getMod190().getStatus() == FiscalStatus.FINISHED));
		duplicateButton.setVisible(!getMod190().isNew());
		auditButton.setVisible(!getMod190().isNew());
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
		document.setValue(getMod190().getDocument());
		document.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod190().setDocument(document.getValue());
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
		name.setValue(getMod190().getName());
		name.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod190().setName(name.getValue());
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
		contactPerson.setValue(getMod190().getContactPerson());
		contactPerson.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod190().setContactPerson(contactPerson.getValue());
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
		contactPhone.setValue(getMod190().getContactPhone());
		contactPhone.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod190().setContactPhone(contactPhone.getValue());
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
		contactMail.setValue(getMod190().getContactMail());
		contactMail.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod190().setContactMail(contactMail.getValue());
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
		receipt.setEnabled(getMod190().isAEAT());
		receipt.setValue(getMod190().getReceipt());
		receipt.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod190().setReceipt(receipt.getValue());
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
		replaced.setEnabled(getMod190().isAEAT() && (getMod190().isComplementary() || getMod190().isReplacement()));
		replaced.setValue(getMod190().getReplacedReceipt());
		replaced.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod190().setReplacedReceipt(replaced.getValue());
				markAsDirty();
			}
		});
		table.setWidget(6, 1, replaced);
		table.getCellFormatter().setStyleName(6,1, AON.AON_CSS.aonPanelGridEven());
		
		declarationScrollPanel.setWidget(table);
		tabPanel.add(declarationScrollPanel, TAB_TEMPLATE.render(AON.MSG.declaration(), AON.AON_CSS.aonIconModel()));
	}
	
	private Widget getDeclarationHeaderTable(Model190Callback cbk) {
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
		documentLabel.setText(getMod190().getDocument());
		namePanel.add(documentLabel);
		nameLabel.setStyleName(AON.AON_CSS.aonMarginLeft());
		nameLabel.setText(getMod190().getName());
		namePanel.add(nameLabel);
		
		table.setWidget(0, 0, namePanel);
		table.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonNowrap());

		if (getMod190().isReplacement()) {
			replacedLabel.setText("Sustit.");
			replacedLabel.setStyleName(AON.AON_CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
		}
		if (getMod190().isComplementary()) {
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
		
		styleStatusLabel(getMod190());
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
				commentPanel.setStyleName( FiscalModelUtils.getAdministrationBG(getMod190().getAdministration()) );
				commentPanel.setStyleName(AON.AON_CSS.aonHeightAll());
				commentPanel.addStyleName(AON.AON_CSS.aonTextCenter());
				TextArea comment = new TextArea();
				comment.addValueChangeHandler(new ValueChangeHandler<String>() {
					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						getMod190().setComments(event.getValue());
						styleCommentsButton();
						Model190.SERVICE.saveComments(cbk.getDomainName(), cbk.getUser(), getMod190(), new AsyncCallback<Mod190>() {
							@Override
							public void onSuccess(Mod190 result) {
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
				comment.setText(mod190.getComments());
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
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(getMod190().getAdministration()));
		tab.setWidget(0, 0, title);
		
		int row = 1;
		for (Pair<String, String> pair : getInformationLinks()) {
			Label icon = new Label();
			icon.addStyleName(FiscalModelUtils.getAdministrationIcon(getMod190().getAdministration()));
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

	protected FlowPanel getAdministrationPanel(Model190Callback cbk) {
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
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(getMod190().getAdministration()));
		tab.setWidget(0, 0, title);
		
		int row = 1;

		Label icon1 = new Label();
		icon1.addStyleName(FiscalModelUtils.getAdministrationIcon(getMod190().getAdministration()));
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
				if (getMod190().isFinished() || getMod190().isSent()) {
					submitForm(cbk,MODEL190_FILE);
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
		icon3.addStyleName(FiscalModelUtils.getAdministrationIcon(getMod190().getAdministration()));
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
				submitForm(cbk,MODEL190_PRINT);
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
		tab2.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(getMod190().getAdministration()));
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
				if (getMod190().isFinished() || getMod190().isSent()) {
					submitForm(cbk,MODEL190_CERTIFICATE_PRINT);
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

	protected abstract void paintPerceptorsTab(TabLayoutPanel tabPanel, Integer selectedIndex);
	protected abstract LinkedList<Pair<String, String>> getInformationLinks();
	
	
}
