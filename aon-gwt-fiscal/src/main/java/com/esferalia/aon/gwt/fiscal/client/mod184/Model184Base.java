package com.esferalia.aon.gwt.fiscal.client.mod184;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.common.client.widget.AuditDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184.IModel184Callback;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184.Model184Callback;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

abstract class Model184Base extends DockLayoutPanel {

	static final String MODEL184_PRINT = "/aon_gwt_fiscal/Model184Print";
	static final String MODEL184_FILE = "/aon_gwt_fiscal/Model184File";
	
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

	protected interface IModel184Income extends IsWidget {
		Integer getSelectedIncomeIndex();
	}
	protected interface IModel184Partner extends IsWidget {
		Integer getSelectedPartnerIndex();
	}
	
	interface TabLabelTemplate extends SafeHtmlTemplates {
		@Template("<span class=\"{1} aon-padding-right aon-padding-left-20\" style=\"width: auto !important\">{0}</span>")
		SafeHtml render(String label, String iconStyle);
	}
	protected static final TabLabelTemplate TAB_TEMPLATE = GWT.create(TabLabelTemplate.class);

	protected class Model184BaseCallback implements IModel184Callback {
		private Model184Callback cbk;
		
		protected Model184BaseCallback( Model184Callback cbk ) {
			this.cbk = cbk;
		}
		public Mod184 getMod184() {
			return Model184Base.this.getMod184();
		}
		@Override
		public void onAccept(Mod184 mod184) {
			cbk.onAccept(mod184);
		}
		@Override	
		public void onCancel() {
			cbk.onCancel();
		}
		@Override	
		public void onSelect(Mod184 result, Integer selectedIncomeIndex, Integer selectedPartnerIndex, Integer tabIndex) {
			cbk.onSelect(result, selectedIncomeIndex,selectedPartnerIndex,tabIndex);
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
	
	
	private Mod184 mod184;
	private Model184BaseCallback callback;
	private boolean dirty;
	protected Integer selectedTab;

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
	protected Hidden mod184Hidden = new Hidden("mod184");
	protected Hidden domainIdHidden = new Hidden("domainId");
	protected Hidden domainNameHidden = new Hidden("domainName");
	
	private IModel184Income  incomeManager;
	private IModel184Partner partnerManager;
	
	public Model184Base(Mod184 mod184,Model184Callback cbk) {
		super(Unit.PX);
		select( mod184 );
		
		addNorth(getToolbarPanel(), 25);
		
		SimplePanel modelPanel = new SimplePanel();
		FiscalModelUtils.paintHeaderTable(modelPanel, this.mod184 );
		addNorth(modelPanel, 65);
		
		ScrollPanel headerPanel = new ScrollPanel();
		headerPanel.setStyleName(AON.AON_CSS.aonScrollArea());
		headerPanel.setWidget( getDeclarationHeaderTable());
		addNorth(headerPanel, 45);
		
		this.callback = new Model184BaseCallback(cbk);


		setStyleName(AON.AON_CSS.aonSelector());
	}

	public Model184BaseCallback getCallback() {
		return callback;
	}
	protected Mod184 getMod184() {
		return mod184;
	}
	public void setMod184(Mod184 mod184) {
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
	protected void select( Mod184 mod184) {
		setMod184(mod184);
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
		toolbar.setWidget(0, 0, new Label( "Modelo 184."));
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
				if (getMod184().getYear() == 0) {
					throw new IllegalArgumentException(AON.MSG.requiredField(AON.MSG.fiscalYear()));
				}
				final PopupPanel popup = new PopupPanel(false, true);
				Label label = new Label(AON.MSG.processing());
				label.addStyleName(AON.AON_CSS.aonTimer());
				popup.add(label);
				popup.setGlassEnabled(true);
				popup.setAnimationEnabled(true);
				popup.center();
				Model184.SERVICE.saveMod184(Model184.getCurrentDomainName(), Model184.getCurrentDomain(),
						getMod184(), new AsyncCallback<Mod184>() {
							@Override
							public void onSuccess(Mod184 result) {
								popup.hide();
								callback.onSelect(result, incomeManager.getSelectedIncomeIndex(), partnerManager.getSelectedPartnerIndex(), getSelectedTab() );
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
						Model184.SERVICE.deleteMod184(Model184.getCurrentDomainName(),
								Model184.getCurrentDomain(), getMod184(), new AsyncCallback<Void>() {
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
				Model184.SERVICE.changeStatusMod184(Model184.getCurrentDomainName(), getMod184(), FiscalStatus.FINISHED, new AsyncCallback<Mod184>() {
					@Override
					public void onSuccess(Mod184 result) {
						callback.onSelect(result , null, null, null );
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
				Model184.SERVICE.changeStatusMod184(Model184.getCurrentDomainName(), getMod184(), FiscalStatus.SENT, new AsyncCallback<Mod184>() {
					@Override
					public void onSuccess(Mod184 result) {
						callback.onSelect(result , null, null , null);
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
				Model184.SERVICE.changeStatusMod184(Model184.getCurrentDomainName(), getMod184(), FiscalStatus.PENDING, new AsyncCallback<Mod184>() {
					@Override
					public void onSuccess(Mod184 result) {
						callback.onSelect(result , null, null , null);
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
		formFlowPanel.add(mod184Hidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formContainer.add(diskForm);
		toolbarPanel.add(formContainer);
		
		return toolbarPanel;
	}
	
	private void audit() {
		AuditDialog dialog = new AuditDialog();
		dialog.show(getMod184());
	}
	
	protected void submitForm(String action) {
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		mod184Hidden.setValue(String.valueOf(getMod184().getId()));
		domainIdHidden.setValue(String.valueOf(Model184.getCurrentDomain()));
		domainNameHidden.setValue(Model184.getCurrentDomainName());
		diskForm.submit();
	}
	
	protected void identificationLabelChanged() {
		documentLabel.setText(getMod184().getDocument());
		nameLabel.setText(getMod184().getName());
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
		if (AonStringUtils.isEmpty(mod184.getComments())) {
			commentsButton.addStyleName(AON.AON_CSS.aonIconComment());
			commentsButton.removeStyleName(AON.AON_CSS.aonIconCommentRed());
		} else {
			commentsButton.addStyleName(AON.AON_CSS.aonIconCommentRed());
			commentsButton.removeStyleName(AON.AON_CSS.aonIconComment());
		}
		commentsButton.setTitle(mod184.getComments());
	}
	
	private void styleStatusLabel(Mod184 mod) {
		statusLabel.setText(mod.getStatus().getName());
		statusLabel.setStyleName(FiscalModelUtils.getStatusIconStyle(mod.getStatus()));
		statusLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
	}

	private void refreshToolbarState() {
		newButton.setVisible(!getMod184().isNew());
		saveButton.setVisible(!getMod184().isFinished() && !getMod184().isSent());
		deleteButton.setVisible(!getMod184().isNew() && !getMod184().isFinished() && !getMod184().isSent());
		cancelButton.setVisible(true);
		markAsPendingButton.setVisible(!getMod184().isNew() &&
			(getMod184().getStatus() == FiscalStatus.FINISHED 
			|| getMod184().getStatus() == FiscalStatus.BATCHED
			|| getMod184().getStatus() == FiscalStatus.SENT
			|| getMod184().getStatus() == FiscalStatus.BLOCKED));
		markAsFinishedButton.setVisible(!getMod184().isNew() &&
			(getMod184().getStatus() == FiscalStatus.PENDING 
			|| getMod184().getStatus() == FiscalStatus.MISSING));
		markAsSentButton.setVisible(!getMod184().isNew() &&
			(getMod184().getStatus() == FiscalStatus.FINISHED));
		
		auditButton.setVisible(!getMod184().isNew());
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
		document.setValue(getMod184().getDocument());
		document.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod184().setDocument(document.getValue());
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
		name.setValue(getMod184().getName());
		name.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod184().setName(name.getValue());
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
		contactPerson.setValue(getMod184().getContactPerson());
		contactPerson.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod184().setContactPerson(contactPerson.getValue());
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
		contactPhone.setValue(getMod184().getContactPhone());
		contactPhone.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod184().setContactPhone(contactPhone.getValue());
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
		contactMail.setValue(getMod184().getContactMail());
		contactMail.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod184().setContactMail(contactMail.getValue());
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
		receipt.setValue(getMod184().getReceipt());
		receipt.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod184().setReceipt(receipt.getValue());
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
		replaced.setEnabled(getMod184().isComplementary() || getMod184().isReplacement());
		replaced.setValue(getMod184().getReplacedReceipt());
		replaced.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod184().setReplacedReceipt(replaced.getValue());
				markAsDirty();
			}
		});
		table.setWidget(6, 1, replaced);
		table.getCellFormatter().setStyleName(6,1, AON.AON_CSS.aonPanelGridEven());
		
		declarationScrollPanel.setWidget(table);
		tabPanel.add(declarationScrollPanel, TAB_TEMPLATE.render(AON.MSG.declaration(), AON.AON_CSS.aonIconModel()));
	}
	protected void paintEntityTab(TabLayoutPanel tabPanel) {
		ScrollPanel entityScrollPanel = new ScrollPanel();
		entityScrollPanel.setStyleName(AON.AON_CSS.aonWidthAll());
		entityScrollPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		
		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonWidthAll());
		tab.addStyleName(AON.AON_CSS.aonNowrap());
		
		tab.getColumnFormatter().setWidth(0, "150px");
		tab.getColumnFormatter().setWidth(1, "200px");
		tab.getColumnFormatter().setWidth(2, "200px");
		tab.getColumnFormatter().setWidth(3, "auto");

		tab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonBorderBottom());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonBold());
		tab.getFlexCellFormatter().setColSpan(0, 0, 4);
		tab.setWidget(0, 0, new InlineLabel(AON.MSG.localEntities()));

		tab.setWidget(1, 0, new MediumLabel(AON.MSG.entityType()));
		ListBox entityType = new ListBox();
		entityType.addItem(" - ","");
		entityType.addItem("1 - Sociedad civil.","1");
		entityType.addItem("2 - Comunidad de bienes.","2");
		entityType.addItem("3 - Herencia yacente.","3");
		entityType.addItem("4 - Comunidad de propietarios.","4");
		entityType.addItem("5 - Otros","5");
		entityType.setWidth("200px");
		entityType.setSelectedIndex(AonNumberUtils.toint(getMod184().getEntityType()));
		entityType.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				getMod184().setEntityType(entityType.getSelectedValue());
				markAsDirty();
			}
		});
		tab.setWidget(1, 1, entityType);
		
		tab.setWidget(1, 2, new MediumLabel(AON.MSG.mainActivity()));
		ListBox mainActivity = new ListBox();
		mainActivity.addItem(" - ","");
		mainActivity.addItem("1 - Actividad empresarial.","1");
		mainActivity.addItem("2 - Actividad profesional.","2");
		mainActivity.addItem("3 - Tenencia y administraci\u00F3n de bienes inmuebles.","3");
		mainActivity.addItem("4 - Tenencia y administraci\u00F3n de valores o activos financieros.","4");
		mainActivity.addItem("5 - Otras.","5");
		mainActivity.setWidth("250px");
		mainActivity.setSelectedIndex(AonNumberUtils.toint(getMod184().getMainActivity()));
		mainActivity.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				getMod184().setMainActivity(mainActivity.getSelectedValue());
				markAsDirty();
			}
		});
		tab.setWidget(1, 3, mainActivity);
		
		tab.getCellFormatter().setStyleName(2, 0, AON.AON_CSS.aonBorderBottom());
		tab.getCellFormatter().addStyleName(2, 0, AON.AON_CSS.aonBold());
		tab.getFlexCellFormatter().setColSpan(2, 0, 4);
		tab.setWidget(2, 0, new InlineLabel(AON.MSG.foreignEntities()));
		
		tab.setWidget(3, 0, new MediumLabel(AON.MSG.entityType()));
		ListBox foreignEntityType = new ListBox();
		foreignEntityType.addItem(" - ","");
		foreignEntityType.addItem("1- Corporaci\u00F3n, asociaci\u00F3n o ente con personalidad jur\u00EDdica propia.","1");
		foreignEntityType.addItem("2- Corporaci\u00F3n o ente independiente pero sin personalidad jur\u00EDdica propia.","2");
		foreignEntityType.addItem("3- Conjunto unitario de bienes pertenecientes a dos o m\u00E1s personas en com\u00FAn sin personalidad jur\u00EDdica propia.","3");
		foreignEntityType.addItem("4- Otras","4");
		foreignEntityType.setWidth("200px");
		foreignEntityType.setSelectedIndex(AonNumberUtils.toint(getMod184().getForeignEntityType()));
		foreignEntityType.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				getMod184().setForeignEntityType(foreignEntityType.getSelectedValue());
				markAsDirty();
			}
		});
		tab.setWidget(3, 1, foreignEntityType);

		tab.setWidget(3, 2, new MediumLabel(AON.MSG.entityType()));
		ListBox foreignObject = new ListBox();
		foreignObject.addItem(" - ","");
		foreignObject.addItem("A - Actividad nat. empresarial.","A");
		foreignObject.addItem("B - Actividad nat. profesional.","B");
		foreignObject.setWidth("200px");
		foreignObject.setSelectedIndex(AonNumberUtils.toint(getMod184().getForeignObject()));
		foreignObject.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				getMod184().setForeignEntityType(foreignObject.getSelectedValue());
				markAsDirty();
			}
		});
		tab.setWidget(3, 3, foreignObject);
		
		tab.setWidget(4, 0, new MediumLabel(AON.MSG.country()));
		CountryListBox country = new CountryListBox();
		country.setValue( Country.safeValueOf( getMod184().getCountry() ));
		country.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				getMod184().setCountry(country.getSelectedValue());
				markAsDirty();
			}
		});
		tab.setWidget(4, 1, country);
		
		tab.setWidget(4, 2, new MediumLabel(AON.MSG.residentPercent()));
		DoubleBox residentPercent = new DoubleBox();
		residentPercent.setValue(getMod184().getResidentPercent());
		residentPercent.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				getMod184().setResidentPercent(residentPercent.getValue());
				markAsDirty();
			}
		});
		tab.setWidget(4, 3, residentPercent);
		
		CheckBox taxIS = new CheckBox(AON.MSG.isTax());
		taxIS.setValue(getMod184().isTaxIS());
		taxIS.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				getMod184().setTaxIS(taxIS.getValue());
				markAsDirty();
			}
		});
		tab.getFlexCellFormatter().setColSpan(5, 0, 2);
		tab.setWidget(5, 0, taxIS);
		
		tab.setWidget(5, 1, new MediumLabel(AON.MSG.netAmount()));
		DoubleBox netSalesAmount = new DoubleBox();
		netSalesAmount.setValue(getMod184().getNetSalesAmount());
		netSalesAmount.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				getMod184().setNetSalesAmount(netSalesAmount.getValue());
				markAsDirty();
			}
		});
		tab.setWidget(5, 2, netSalesAmount);
		
		tab.setWidget(6, 0, new MediumLabel(AON.MSG.lrDocument()));
		DocumentTextBox lrDocument = new DocumentTextBox();
		lrDocument.setValue(getMod184().getLrDocument());
		lrDocument.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod184().setLrDocument(lrDocument.getValue());
				markAsDirty();
			}
		});
		tab.setWidget(6, 1, lrDocument);
		
		tab.setWidget(6, 0, new MediumLabel(AON.MSG.lrName()));
		TextBox lrName = new TextBox();
		lrName.setStyleName(AON.AON_CSS.aonInputText());
		lrName.setMaxLength(40);
		lrName.setVisibleLength(40);
		lrName.setValue(getMod184().getLrName());
		lrName.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getMod184().setLrName(lrName.getValue());
				markAsDirty();
			}
		});
		tab.setWidget(6, 1, lrDocument);

		entityScrollPanel.setWidget(tab);
		tabPanel.add(entityScrollPanel, TAB_TEMPLATE.render(AON.MSG.entity(), AON.AON_CSS.aonIconModel()));
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
		documentLabel.setText(getMod184().getDocument());
		namePanel.add(documentLabel);
		nameLabel.setStyleName(AON.AON_CSS.aonMarginLeft());
		nameLabel.setText(getMod184().getName());
		namePanel.add(nameLabel);
		
		table.setWidget(0, 0, namePanel);
		table.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		table.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonNowrap());

		if (getMod184().isReplacement()) {
			replacedLabel.setText("Sustit.");
			replacedLabel.setStyleName(AON.AON_CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
		}
		if (getMod184().isComplementary()) {
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
		
		styleStatusLabel(getMod184());
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
				commentPanel.setStyleName( FiscalModelUtils.getAdministrationBG(getMod184().getAdministration()) );
				commentPanel.setStyleName(AON.AON_CSS.aonHeightAll());
				commentPanel.addStyleName(AON.AON_CSS.aonTextCenter());
				TextArea comment = new TextArea();
				comment.addValueChangeHandler(new ValueChangeHandler<String>() {
					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						getMod184().setComments(event.getValue());
						styleCommentsButton();
						Model184.SERVICE.saveCommentsMod184(Model303.getCurrentDomainName(), getMod184(), new AsyncCallback<Mod184>() {
							@Override
							public void onSuccess(Mod184 result) {
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
				comment.setText(mod184.getComments());
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
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(getMod184().getAdministration()));
		tab.setWidget(0, 0, title);
		
		int row = 1;
		for (Pair<String, String> pair : getInformationLinks()) {
			Label icon = new Label();
			icon.addStyleName(FiscalModelUtils.getAdministrationIcon(getMod184().getAdministration()));
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
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(getMod184().getAdministration()));
		tab.setWidget(0, 0, title);
		
		int row = 1;

		Label icon1 = new Label();
		icon1.addStyleName(FiscalModelUtils.getAdministrationIcon(getMod184().getAdministration()));
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
				if (getMod184().isFinished() || getMod184().isSent()) {
					submitForm(MODEL184_FILE);
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
		icon3.addStyleName(FiscalModelUtils.getAdministrationIcon(getMod184().getAdministration()));
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
				submitForm(MODEL184_PRINT);
			}
		});
		p3.add(button3);
		tab.setWidget(row, 1, p3 );
		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;
		panel.add(tab);
		
		return panel;
	}

	protected void paintIncomeTab(TabLayoutPanel tabPanel, Integer selectedIndex) {
		// Evaluar lo diferentes paneles por administraciuon y/o ejercicio.
		if (getCallback().getMod184().getYear() == 2014) {
			incomeManager = new Model184Income2014( getCallback() , selectedIndex );	
		} else if (getCallback().getMod184().getYear() == 2015) {
			incomeManager = new Model184Income2015( getCallback() , selectedIndex );
		} else {
			incomeManager = new Model184Income2016( getCallback() , selectedIndex );
		}
		tabPanel.add( (Widget) incomeManager,  TAB_TEMPLATE.render(AON.MSG.entityIncomes(), AON.AON_CSS.aonIconInvoice()) );
	}

	protected void paintPartnersTab(TabLayoutPanel tabPanel, Integer selectedIndex) {
		// Evaluar lo diferentes paneles por administraciuon y/o ejercicio. 
		if (getCallback().getMod184().getYear() == 2014) {
			partnerManager = new Model184Partner2014( getCallback() , selectedIndex );
		} else if (getCallback().getMod184().getYear() == 2015) {
			partnerManager = new Model184Partner2015( getCallback() , selectedIndex );
		} else {
			partnerManager = new Model184Partner2016( getCallback() , selectedIndex );
		}
		tabPanel.add( (Widget) partnerManager,  TAB_TEMPLATE.render(AON.MSG.entityPartners(), AON.AON_CSS.aonIconEmployee()) );
	}
	protected Integer getSelectedTab() {
		return selectedTab;
	}
	protected void setSelectedTab(Integer selectedTab) {
		this.selectedTab = selectedTab;
	}
	
	protected abstract LinkedList<Pair<String, String>> getInformationLinks();
	
}
