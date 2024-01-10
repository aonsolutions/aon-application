package com.esferalia.aon.gwt.fiscal.client.mod190;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAuditDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
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
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;

abstract class Model190Base extends DockLayoutPanel {

	private static final String MODEL190_DRAFT = "/aon_gwt_fiscal/ms/Model190Draft";
	protected static final String MODEL190_FILE = "/aon_gwt_fiscal/ms/Model190File";
	private static final String MODEL190_CERTIFICATE_PRINT = "/aon_gwt_fiscal/ms/Model190CertificatePrint";

	protected interface IModel190Detail extends IsWidget {
		Integer getSelectedPerceptorIndex();
	}

	private Mod190 mod190;
	private Model190Callback callback;
	private boolean dirty;

	protected FiscalModelAdmonPanel<Mod190, Model190ModuleOptions> admonPanel;
	protected AonTextBox receiptBox;

	protected final AonToolbar toolbarPanel = new AonToolbar();
	protected final AonToolbarButton newButton = new AonToolbarButton(AON.MSG.newAction(), AON.CSS.aonIconAdd());
	protected final AonToolbarButton saveButton = new AonToolbarButton(AON.MSG.saveAction(), AON.CSS.aonIconSave());
	protected final AonToolbarButton cancelButton = new AonToolbarButton(AON.MSG.cancelAction(), AON.CSS.aonIconBack());
	protected final AonToolbarButton deleteButton = new AonToolbarButton(AON.MSG.deleteAction(),
			AON.CSS.aonIconDelete());
	protected final AonToolbarButton resetButton = new AonToolbarButton(AON.MSG.resetAction(),
			AON.CSS.aonIconRefresh());
	protected final AonToolbarButton printButton = new AonToolbarButton(AON.MSG.draft(), AON.CSS.aonIconExcel());
	protected final AonToolbarButton validateButton = new AonToolbarButton("Validar importes contra n\u00F3minas",
			AON.CSS.aonIconValid());
	protected final AonToolbarButton markAsPendingButton = new AonToolbarButton(AON.MSG.reopen(),
			AON.CSS.aonIconModelReopen());
	protected final AonToolbarButton markAsFinishedButton = new AonToolbarButton(AON.MSG.finish(),
			AON.CSS.aonIconModelFinish());
	protected final AonToolbarButton markAsSentButton = new AonToolbarButton(AON.MSG.markAsSent(),
			AON.CSS.aonIconModelSent());
	protected final AonToolbarButton duplicateButton = new AonToolbarButton(AON.MSG.duplicate(), AON.CSS.aonIconCopy());
	protected final AonToolbarButton commentsButton = new AonToolbarButton(AON.MSG.comments(),
			AON.CSS.aonIconNoComments());
	protected final AonToolbarButton certificateButton = new AonToolbarButton(AON.MSG.mod190CertificatePrint(),
			AON.CSS.aonIconPdf());
	protected final AonToolbarButton auditButton = new AonToolbarButton(AON.MSG.audit(), AON.CSS.aonIconAudit());

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

	protected Model190Base(Model190Callback cbk, Mod190 mod190) {
		super(Unit.PX);
		this.callback = cbk;

		select(mod190);

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
		refreshToolbarState();
		styleStatusLabel();
	}

	private AonToolbar getToolbarPanel() {

		if (getCallback().getOptions().isBackButtonVisible() && getCallback().getOptions().hasExternalCallback()) {
			cancelButton.setText(AON.MSG.backAction());
			cancelButton.setTitle(AON.MSG.backAction());
		}
		cancelButton.addClickHandler(event -> cancel());
		toolbarPanel.add(cancelButton);

		newButton.addClickHandler(event -> getCallback().onNew());
		toolbarPanel.add(newButton);

		saveButton.addClickHandler(event -> save());
		toolbarPanel.add(saveButton);

		deleteButton.addClickHandler(event -> delete());
		toolbarPanel.add(deleteButton);

		resetButton.addClickHandler(event -> getCallback().onReset(getCallback().getOptions(), getModel()));
		toolbarPanel.add(resetButton);

		duplicateButton
				.addClickHandler(event -> getCallback().onDuplicate(getCallback().getOptions(), getModel().getId()));
		toolbarPanel.add(duplicateButton);

		printButton.addClickHandler(event -> print());
		toolbarPanel.add(printButton);

		validateButton.addClickHandler(event -> validate());
		toolbarPanel.add(validateButton);

		certificateButton.addClickHandler(event -> certificate());
		toolbarPanel.add(certificateButton);

		commentsButton.addClickHandler(event -> {
			final AonToast toast = new AonToast();
			FlowPanel commentPanel = new FlowPanel();
			commentPanel
					.setStyleName(FiscalModelUtils.getAdministrationBackgroundStyle(getModel().getAdministration()));
			commentPanel.addStyleName(AON.CSS.aonHeightAll());
			commentPanel.addStyleName(AON.CSS.aonTextCenter());
			TextArea comment = new TextArea();
			comment.addValueChangeHandler(event1 -> {
				getModel().setComments(event1.getValue());
				styleCommentsButton();
				Model190.SERVICE.saveComments(getCallback().getOptions().getOccam(), getModel(),
						new AsyncCallback<Mod190>() {
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
		toolbarPanel.add(commentsButton);
		styleCommentsButton();

		auditButton.addClickHandler(event -> audit());
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
		popup.add(new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model190.SERVICE.save(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod190>() {
			@Override
			public void onSuccess(Mod190 result) {
				popup.hide();
				getCallback().onSelect(result, detailManager.getSelectedPerceptorIndex());
				if (cbk != null)
					cbk.onSuccess(result);
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
					if (getCallback().getOptions().isBackButtonVisible()
							&& getCallback().getOptions().hasExternalCallback()) {
						getCallback().getOptions().getExternalCallback().onExit(mod190);
					} else {
						getCallback().onCancel(getModel());
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
				getCallback().onCancel(getModel());
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
						getCallback().onRemove(mod190);
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
		markAsFinishedButton.addClickHandler(event -> markAsFinished());
		decToolbar.add(markAsFinishedButton);

		markAsSentButton.setText(markAsSentButton.getTitle());
		markAsSentButton.addClickHandler(event -> markAsSent());
		decToolbar.add(markAsSentButton);

		markAsPendingButton.setText(markAsPendingButton.getTitle());
		markAsPendingButton.addClickHandler(event -> markAsPending());
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
			replacedLabel.setText(AON.MSG.complementary());
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
		Model190.SERVICE.changeStatus(getCallback().getOptions().getOccam(), getModel(), FiscalStatus.FINISHED,
				new AsyncCallback<Mod190>() {
					@Override
					public void onSuccess(Mod190 result) {
						getCallback().onSelect(result, detailManager.getSelectedPerceptorIndex());
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
		Model190.SERVICE.changeStatus(getCallback().getOptions().getOccam(), getModel(), FiscalStatus.SENT,
				new AsyncCallback<Mod190>() {
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
		Model190.SERVICE.changeStatus(getCallback().getOptions().getOccam(), getModel(), FiscalStatus.PENDING,
				new AsyncCallback<Mod190>() {
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
			new AonConfirmDialog().confirm(AON.MSG.draftPrint(), AON.MSG.draftPrintNote(),
					new AonConfirmDialogCallback() {

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

	private void certificate() {
		if (isDirty()) {
			new AonConfirmDialog().confirm(AON.MSG.mod190CertificatePrint(), AON.MSG.mod190CertificatePrintNote(),
					new AonConfirmDialogCallback() {

						@Override
						public void onAccept() {
							submitForm(MODEL190_CERTIFICATE_PRINT);
						}

						@Override
						public void onCancel() {
							// Nothing
						}
					});
		} else {
			submitForm(MODEL190_CERTIFICATE_PRINT);
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
		statusLabel.getElement().getStyle()
				.setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB(getModel().getStatus()));
		statusLabel.getElement().getStyle().setColor(FiscalModelUtils.getStatusFrgColorRGB(getModel().getStatus()));
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
		toolbarPanel.setTitle(
				AonStringUtils.join(getModel().getDocument(), AonStringUtils.SPACE, getModel().getFullName()));
		newButton.setVisible(!getModel().isNew() && !getCallback().getOptions().isBackButtonVisible()
				&& !getCallback().getOptions().hasExternalCallback());
		saveButton.setVisible(!getModel().isFinished() && !getModel().isSent());
		deleteButton.setVisible(!getModel().isNew() && !getModel().isFinished() && !getModel().isSent());
		resetButton.setVisible(!getModel().isNew() && !getModel().isFinished() && !getModel().isSent());
		cancelButton.setVisible(true);
		markAsPendingButton.setVisible(!getModel().isNew() && (getModel().getStatus() == FiscalStatus.FINISHED
				|| getModel().getStatus() == FiscalStatus.BATCHED || getModel().getStatus() == FiscalStatus.SENT
				|| getModel().getStatus() == FiscalStatus.BLOCKED));
		markAsFinishedButton.setVisible(!getModel().isNew()
				&& (getModel().getStatus() == FiscalStatus.PENDING || getModel().getStatus() == FiscalStatus.MISSING));
		markAsSentButton.setVisible(!getModel().isNew() && (getModel().getStatus() == FiscalStatus.FINISHED));
		duplicateButton.setVisible(!getModel().isNew());
		printButton.setVisible(!getModel().isNew());
		validateButton.setVisible(!getModel().isNew());
		auditButton.setVisible(!getModel().isNew());
	}

	private void identificationLabelChanged() {
		toolbarPanel.setTitle(
				AonStringUtils.join(getModel().getDocument(), AonStringUtils.SPACE, getModel().getFullName()));
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

		table.setWidget(0, 0, new InlineLabel(AON.MSG.document()));
		table.getCellFormatter().setStyleName(0, 0, AON.CSS.aonTableLabel());
		AonDocumentTextBox document = new AonDocumentTextBox();
		document.setValue(getModel().getDocument());
		document.addValueChangeHandler(event -> {
			getModel().setDocument(document.getValue());
			identificationLabelChanged();
			markAsDirty();
		});
		table.setWidget(0, 1, document);

		table.setWidget(1, 0, new InlineLabel(AON.MSG.enterpriseName()));
		table.getCellFormatter().setStyleName(1, 0, AON.CSS.aonTableLabel());
		AonTextBox name = new AonTextBox();
		name.setVisibleLength(45);
		name.setMaxLength(45);
		name.setValue(getModel().getName());
		name.addValueChangeHandler(event -> {
			getModel().setName(name.getValue());
			identificationLabelChanged();
			markAsDirty();
		});
		table.setWidget(1, 1, name);

		table.setWidget(2, 0, new InlineLabel(AON.MSG.contactPerson()));
		table.getCellFormatter().setStyleName(2, 0, AON.CSS.aonTableLabel());
		AonTextBox contactPerson = new AonTextBox();
		contactPerson.setMaxLength(40);
		contactPerson.setVisibleLength(30);
		contactPerson.setValue(getModel().getContactPerson());
		contactPerson.addValueChangeHandler(event -> {
			getModel().setContactPerson(contactPerson.getValue());
			markAsDirty();
		});
		table.setWidget(2, 1, contactPerson);

		table.setWidget(3, 0, new InlineLabel(AON.MSG.contactPhone()));
		table.getCellFormatter().setStyleName(3, 0, AON.CSS.aonTableLabel());
		AonTextBox contactPhone = new AonTextBox();
		contactPhone.setMaxLength(9);
		contactPhone.setVisibleLength(10);
		contactPhone.setValue(getModel().getContactPhone());
		contactPhone.addValueChangeHandler(event -> {
			getModel().setContactPhone(contactPhone.getValue());
			markAsDirty();
		});
		table.setWidget(3, 1, contactPhone);

		table.setWidget(4, 0, new InlineLabel(AON.MSG.contactMail()));
		table.getCellFormatter().setStyleName(4, 0, AON.CSS.aonTableLabel());
		AonTextBox contactMail = new AonTextBox();
		contactMail.setMaxLength(50);
		contactMail.setVisibleLength(50);
		contactMail.setValue(getModel().getContactMail());
		contactMail.addValueChangeHandler(event -> {
			getModel().setContactMail(contactMail.getValue());
			markAsDirty();
		});
		table.setWidget(4, 1, contactMail);

		table.setWidget(5, 0, new InlineLabel(AON.MSG.receipt()));
		table.getCellFormatter().setStyleName(5, 0, AON.CSS.aonTableLabel());
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

		table.setWidget(6, 0, new InlineLabel(AON.MSG.previousDeclaration()));
		table.getCellFormatter().setStyleName(6, 0, AON.CSS.aonTableLabel());
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
			receiptBox.setValue(getModel().getReceipt());
		}
	}

	private void validate() {
		Model190.SERVICE.validateSalaries(getCallback().getOptions().getOccam(), getModel(),
				new AsyncCallback<LinkedList<Mod190Detail>>() {
					@Override
					public void onSuccess(LinkedList<Mod190Detail> list) {
						if (list == null || list.isEmpty()) {
							Label noData = new Label(
									"No se han encontrado descuadres con lo declarado en n\u00F3minas");
							noData.setStyleName(AON.CSS.aonTextCenter());
							noData.addStyleName(AON.CSS.aonMarginTop());
							noData.addStyleName(AON.CSS.aonBold());
							getCallback().showInfoPanel(noData);
						} else {
							paintValidateSalaries(list);
						}

					}

					@Override
					public void onFailure(Throwable caught) {
						getCallback().showError(caught.getMessage());
					}
				});
	}

	private enum SettleField {
		PERCEPTION {
			@Override
			void settle(Mod190Detail source, Mod190Detail target) {
				target.setPerception(AonMathUtils.round(target.getPerception() + getPercetionDiff(source)));
			}
		},
		PERCEPTION_IL {
			@Override
			void settle(Mod190Detail source, Mod190Detail target) {
				target.setPerceptionIL(AonMathUtils.round(target.getPerceptionIL() + getPercetionDiff(source)));
			}
		},
		IN_KIND_PERCEPTION {
			@Override
			void settle(Mod190Detail source, Mod190Detail target) {
				target.setInKindPerception(AonMathUtils.round(target.getInKindPerception() + getPercetionDiff(source)));
			}
		},
		IN_KIND_PERCEPTION_IL {
			@Override
			void settle(Mod190Detail source, Mod190Detail target) {
				target.setInKindPerceptionIL(
						AonMathUtils.round(target.getInKindPerceptionIL() + getPercetionDiff(source)));
			}
		},
		RETENTION {
			@Override
			void settle(Mod190Detail source, Mod190Detail target) {
				target.setRetention(AonMathUtils.round(target.getRetention() + getRetentionDiff(source)));
			}
		},
		RETENTION_IL {
			@Override
			void settle(Mod190Detail source, Mod190Detail target) {
				target.setRetentionIL(AonMathUtils.round(target.getRetentionIL() + getRetentionDiff(source)));
			}
		},
		IN_KIND_DEPOSIT {
			@Override
			void settle(Mod190Detail source, Mod190Detail target) {
				target.setInKindDeposit(AonMathUtils.round(target.getInKindDeposit() + getRetentionDiff(source)));
			}
		},
		IN_KIND_DEPOSIT_IL {
			@Override
			void settle(Mod190Detail source, Mod190Detail target) {
				target.setInKindDepositIL(AonMathUtils.round(target.getInKindDepositIL() + getRetentionDiff(source)));
			}
		},;

		abstract void settle(Mod190Detail source, Mod190Detail target);

		private static double getPercetionDiff(Mod190Detail detail) {
			return AonMathUtils.round(detail.getSalaryPerception() - detail.getPerception() - detail.getPerceptionIL()
					- detail.getInKindPerception() - detail.getInKindPerceptionIL());
		}

		private static double getRetentionDiff(Mod190Detail detail) {
			return AonMathUtils.round(detail.getSalaryRetention() - detail.getRetention() - detail.getRetentionIL()
					- detail.getInKindDeposit() - detail.getInKindDepositIL());
		}

	}

	private void paintValidateSalaries(LinkedList<Mod190Detail> list) {
		FlowPanel gridContainer = new FlowPanel();
		
		FlowPanel header = new FlowPanel();
		header.setStyleName( AON.CSS.aonMarginTop()); 
		header.addStyleName( AON.CSS.aonTextCenter());
		
		InlineLabel issueDate = new InlineLabel("Validar por fecha de emisi\u00F3n");
		issueDate.setStyleName( AON.CSS.aonClickableLabel());
		issueDate.addStyleName( AON.CSS.aonLabelWithIcon());
		issueDate.addStyleName( AON.CSS.aonIconValid());
		issueDate.addStyleName( AON.CSS.aonMarginRight());
		issueDate.getElement().getStyle().setPaddingLeft(25.0, Unit.PX);
		issueDate.addClickHandler(e -> { 
				getModel().setUseChargeDate( false );
				validate();
		});
		header.add(issueDate);

		InlineLabel chargeDate = new InlineLabel("Validar por fecha de cargo");
		chargeDate.setStyleName( AON.CSS.aonClickableLabel());
		chargeDate.addStyleName( AON.CSS.aonLabelWithIcon());
		chargeDate.addStyleName( AON.CSS.aonIconValid());
		chargeDate.addStyleName( AON.CSS.aonMarginLeft());
		chargeDate.getElement().getStyle().setPaddingLeft(25.0, Unit.PX);
		chargeDate.addClickHandler(e -> { 
				getModel().setUseChargeDate( true );
				validate();
		});
		header.add(chargeDate);
		
		gridContainer.add(header);
		
		AonDisplayGrid grid = new AonDisplayGrid();
		grid.addStyleName(AON.CSS.aonBlockCenter());
		grid.addStyleName(AON.CSS.aonMarginTop());
		grid.addStyleName(AON.CSS.aonMarginBottom());
		grid.addHeaderRow().addCell(new Label("Documento - Nombre"), AON.CSS.aonWidthAuto())
				.addCell(new Label("Percep. N\u00F3minas"), AON.CSS.aonWidth80())
				.addCell(new Label("Per. 190"), AON.CSS.aonWidth80())
				.addCell(new Label("Per. IL"), AON.CSS.aonWidth80())
				.addCell(new Label("Per. Esp."), AON.CSS.aonWidth80())
				.addCell(new Label("Per. Esp. IL"), AON.CSS.aonWidth80())
				.addCell(new Label("Percep. Dif."), AON.CSS.aonWidth80()).addCell(new Label(), AON.CSS.aonWidth20())
				.addCell(new Label("Retenc. N\u00F3minas"), AON.CSS.aonWidth80())
				.addCell(new Label("Ret."), AON.CSS.aonWidth80()).addCell(new Label("Ret. IL"), AON.CSS.aonWidth80())
				.addCell(new Label("Ret. Esp."), AON.CSS.aonWidth80())
				.addCell(new Label("Ret. Esp. IL"), AON.CSS.aonWidth80())
				.addCell(new Label("Retenc. Dif."), AON.CSS.aonWidth80());
		for (Mod190Detail detail : list) {
			double perDif = AonMathUtils.round(detail.getSalaryPerception() - detail.getPerception()
					- detail.getPerceptionIL() - detail.getInKindPerception() - detail.getInKindPerceptionIL());

			Label perceptionLabel = new Label(AON.FMT.format(detail.getPerception()));
			Label perceptionILLabel = new Label(AON.FMT.format(detail.getPerceptionIL()));
			Label inKindPerceptionLabel = new Label(AON.FMT.format(detail.getInKindPerception()));
			Label inKindPerceptionILLabel = new Label(AON.FMT.format(detail.getInKindPerceptionIL()));
			if (AonMathUtils.isNotZero(perDif)) {

				perceptionLabel.setTitle("Asignar diferencia a \"Percepciones \u00EDntegras\"");
				perceptionLabel.setStyleName(AON.CSS.aonTextUnderline());
				perceptionLabel.addStyleName(AON.CSS.aonClickable());
				perceptionLabel.addClickHandler(event -> tryTochangeMod190Detail(detail, SettleField.PERCEPTION));

				perceptionILLabel.setTitle("Asignar diferencia a \"Percepciones \u00EDntegras incapacidad laboral\"");
				perceptionILLabel.setStyleName(AON.CSS.aonTextUnderline());
				perceptionILLabel.addStyleName(AON.CSS.aonClickable());
				perceptionILLabel.addClickHandler(event -> tryTochangeMod190Detail(detail, SettleField.PERCEPTION_IL));

				inKindPerceptionLabel.setTitle("Asignar diferencia a \"Valoraci\u00F3n en especie\"");
				inKindPerceptionLabel.setStyleName(AON.CSS.aonTextUnderline());
				inKindPerceptionLabel.addStyleName(AON.CSS.aonClickable());
				inKindPerceptionLabel
						.addClickHandler(event -> tryTochangeMod190Detail(detail, SettleField.IN_KIND_PERCEPTION));

				inKindPerceptionILLabel
						.setTitle("Asignar diferencia a \"Valoraci\u00F3n en especie incapacidad laboral\"");
				inKindPerceptionILLabel.setStyleName(AON.CSS.aonTextUnderline());
				inKindPerceptionILLabel.addStyleName(AON.CSS.aonClickable());
				inKindPerceptionILLabel
						.addClickHandler(event -> tryTochangeMod190Detail(detail, SettleField.IN_KIND_PERCEPTION_IL));
			}

			double retDif = AonMathUtils.round(detail.getSalaryRetention() - detail.getRetention()
					- detail.getRetentionIL() - detail.getInKindDeposit() - detail.getInKindDepositIL());
			Label retentionLabel = new Label(AON.FMT.format(detail.getRetention()));
			Label retentionILLabel = new Label(AON.FMT.format(detail.getRetentionIL()));
			Label inKindDepositLabel = new Label(AON.FMT.format(detail.getInKindDeposit()));
			Label inKindDepositILLabel = new Label(AON.FMT.format(detail.getInKindDepositIL()));
			if (AonMathUtils.isNotZero(retDif)) {

				retentionLabel.setTitle("Asignar diferencia a \"Retenciones\"");
				retentionLabel.setStyleName(AON.CSS.aonTextUnderline());
				retentionLabel.addStyleName(AON.CSS.aonClickable());
				retentionLabel.addClickHandler(event -> tryTochangeMod190Detail(detail, SettleField.RETENTION));

				retentionILLabel.setTitle("Asignar diferencia a \"Retenciones incapacidad laboral\"");
				retentionILLabel.setStyleName(AON.CSS.aonTextUnderline());
				retentionILLabel.addStyleName(AON.CSS.aonClickable());
				retentionILLabel.addClickHandler(event -> tryTochangeMod190Detail(detail, SettleField.RETENTION_IL));

				inKindDepositLabel.setTitle("Asignar diferencia a \"Ingr. a cta. efectuados en especie\"");
				inKindDepositLabel.setStyleName(AON.CSS.aonTextUnderline());
				inKindDepositLabel.addStyleName(AON.CSS.aonClickable());
				inKindDepositLabel
						.addClickHandler(event -> tryTochangeMod190Detail(detail, SettleField.IN_KIND_DEPOSIT));

				inKindDepositILLabel
						.setTitle("Asignar diferencia a \"Ingr. a cta. efectuados en especie incapacidad laboral\"");
				inKindDepositILLabel.setStyleName(AON.CSS.aonTextUnderline());
				inKindDepositILLabel.addStyleName(AON.CSS.aonClickable());
				inKindDepositILLabel
						.addClickHandler(event -> tryTochangeMod190Detail(detail, SettleField.IN_KIND_DEPOSIT_IL));
			}

			AonDisplayGridRow row = grid.addRow();
			String name = detail.getDocument() + " " + detail.getName();
			if (detail.getAccrualYear() != 0) {
				name = name + " (" + detail.getAccrualYear() + ")";
			}
			row.addCell(new Label(name))
					.addCell(new Label(AON.FMT.format(detail.getSalaryPerception())), AON.CSS.aonTextRight(),
							AON.CSS.aonBackgroundLigthYellow())
					.addCell(perceptionLabel, AON.CSS.aonTextRight(), AON.CSS.aonBackgroundLigthBlue())
					.addCell(perceptionILLabel, AON.CSS.aonTextRight(), AON.CSS.aonBackgroundLigthBlue())
					.addCell(inKindPerceptionLabel, AON.CSS.aonTextRight(), AON.CSS.aonBackgroundLigthBlue())
					.addCell(inKindPerceptionILLabel, AON.CSS.aonTextRight(), AON.CSS.aonBackgroundLigthBlue())
					.addCell(new Label(AON.FMT.format(perDif)), AON.CSS.aonTextRight(),
							AON.CSS.aonBackgroundLigthGray(),
							AonMathUtils.isNotZero(perDif) ? AON.CSS.aonColorRed() : AON.CSS.aonNowrap(),
							AonMathUtils.isNotZero(perDif) ? AON.CSS.aonBold() : AON.CSS.aonNowrap())
					.addCell(new Label())
					.addCell(new Label(AON.FMT.format(detail.getSalaryRetention())), AON.CSS.aonTextRight(),
							AON.CSS.aonBackgroundLigthYellow())
					.addCell(retentionLabel, AON.CSS.aonTextRight(), AON.CSS.aonBackgroundLigthBlue())
					.addCell(retentionILLabel, AON.CSS.aonTextRight(), AON.CSS.aonBackgroundLigthBlue())
					.addCell(inKindDepositLabel, AON.CSS.aonTextRight(), AON.CSS.aonBackgroundLigthBlue())
					.addCell(inKindDepositILLabel, AON.CSS.aonTextRight(), AON.CSS.aonBackgroundLigthBlue()).addCell(
							new Label(AON.FMT.format(retDif)), AON.CSS.aonTextRight(), AON.CSS.aonBackgroundLigthGray(),
							AonMathUtils.isNotZero(retDif) ? AON.CSS.aonColorRed() : AON.CSS.aonNowrap(),
							AonMathUtils.isNotZero(retDif) ? AON.CSS.aonBold() : AON.CSS.aonNowrap());
		}
		gridContainer.add(grid);
		getCallback().showInfoPanel(gridContainer);
	}

	private void tryTochangeMod190Detail(Mod190Detail detail, SettleField field) {
		LinkedList<Mod190Detail> selected = new LinkedList<>();
		for (Mod190Detail det : getModel().getDetails()) {
			double ret = AonMathUtils.round(
					det.getRetention() + det.getInKindDeposit() + det.getRetentionIL() + det.getInKindDepositIL());
			if (AonStringUtils.equals(detail.getDocument(), det.getDocument())
				&& AonMathUtils.equals(detail.getAccrualYear(), det.getAccrualYear())
				&& AonMathUtils.isNotZero(ret)) {
				selected.add(det);
			}
		}
		if (selected.size() == 1) {
			changeMod190Detail(detail, selected.get(0), field);
		} else {
			selectOne(detail, selected, field);
		}
	}

	private void selectOne(Mod190Detail detail, LinkedList<Mod190Detail> selected, SettleField field) {
		AonCustomPopup popup = new AonCustomPopup();
		popup.setWidth("500px");
		popup.setHeight("500px");
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.setCaption("Seleccione la l\u00EDnea a que aplicar la correcci\u00F3n");

		ScrollPanel selectionPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		selectionPanel.setWidget(container);

		AonDisplayGrid grid = new AonDisplayGrid();
		grid.addStyleName(AON.CSS.aonBlockCenter());
		grid.addStyleName(AON.CSS.aonMarginTop());
		grid.addStyleName(AON.CSS.aonMarginBottom());
		grid.addHeaderRow()
			.addCell(new Label("Cl."), AON.CSS.aonWidth120())
			.addCell(new Label("Sub."), AON.CSS.aonWidth120())
			.addCell(new Label("Ej. Dev."), AON.CSS.aonWidth120())
			.addCell(new Label("Percep"), AON.CSS.aonWidth120())
			.addCell(new Label("Retenc."), AON.CSS.aonWidthAuto());
		for (Mod190Detail det : selected) {
			AonDisplayGridRow row = grid.addRow();
			row.addCell(new Label(det.getKey()))
				.addCell(new Label(det.getSubKey()))
				.addCell(new Label(det.getAccrualYear()==0?"":""+det.getAccrualYear()))
				.addCell(new Label(AON.FMT.format(detail.getPerception())), AON.CSS.aonTextRight())
				.addCell(new Label(AON.FMT.format(detail.getRetention())), AON.CSS.aonTextRight());
			row.addClickHandler(event -> {
				popup.hide();
				changeMod190Detail(detail, det, field);
			});
		}
		container.add(grid);

		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonPadding());
		buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.CSS.aonTextCenter());
		Button cancelBtn = new Button();
		cancelBtn.setStyleName(AON.CSS.aonCancelButton());
		cancelBtn.addStyleName(AON.CSS.aonMarginLeft());
		cancelBtn.setText(AON.MSG.cancelAction());
		cancelBtn.addClickHandler(event -> popup.hide());
		buttonsPanel.add(cancelBtn);
		container.add(buttonsPanel);

		popup.add(selectionPanel);
		popup.center();
		popup.show();
	}

	private void changeMod190Detail(Mod190Detail detail, Mod190Detail toUpdate, SettleField field) {
		field.settle(detail, toUpdate);
		markAsDirty();
		validate();
	}
}
