package com.esferalia.aon.gwt.fiscal.client.mod421;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAuditDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToastModel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelEmailUtils;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.AonInvoiceViewer;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.SessionLog;
import com.esferalia.aon.gwt.fiscal.client.invoice.vat.JsVatComputeInfo;
import com.esferalia.aon.gwt.fiscal.client.invoice.vat.JsVatComputeInfoGridPanel;
import com.esferalia.aon.gwt.fiscal.client.invoice.vat.JsVatComputeKeyInfo;
import com.esferalia.aon.gwt.fiscal.client.invoice.vat.JsVatComputeKeyInfoGridPanel;
import com.esferalia.aon.gwt.fiscal.client.invoice.vat.JsVatContext;
import com.esferalia.aon.gwt.fiscal.client.invoice.vat.JsVatContextBreakdownGridPanel;
import com.esferalia.aon.gwt.fiscal.client.mod421.Model421.Model421Callback;
import com.esferalia.aon.gwt.fiscal.client.mod421.Model421FinishDeclarationPopup.FinishDeclarationPopupCallback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelIdentificationPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelKeyInfoVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod421Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public abstract class Model421Base extends DockLayoutPanel  {

	private static final Logger LOGGER = Logger.getLogger(Model421Base.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	private static final String MODEL421_PRINT = "/aon_gwt_fiscal/ms/Model421Print";
	private static final String MODEL421_BOX_INFO = "/aon_gwt_fiscal/ms/Model421BoxInfoPrint";

	protected static final String WIDTH_150PX = "150px";
	protected static final String WIDTH_140PX = "140px";

	protected static final boolean ENABLED = true;
	protected static final boolean DISABLED = false;
	protected static final boolean HAS_INFO = true;
	protected static final boolean HAS_NOT_INFO = false;
	
	private static final int MAX_LABEL_LENGTH = 150;
	
	private Mod421 md421;
	private Model421Callback callback;
	private EnumMap<Mod421Key,AonDoubleBox> fieldsMap;
	private HashSet<Mod421Key> disabledFields;
	private boolean dirty;
	
	protected FiscalModelAdmonPanel<Mod421, Model421ModuleOptions> admonPanel;
	private AonFiscalModelIdentificationPanel<Mod421> identificationData;
	
	protected final AonToolbar toolbarPanel = new AonToolbar(); 
	protected final AonToolbarButton newButton = new AonToolbarButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
	protected final AonToolbarButton saveButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave());
	protected final AonToolbarButton cancelButton = new AonToolbarButton(AON.MSG.cancelAction(),AON.CSS.aonIconBack());
	protected final AonToolbarButton deleteButton = new AonToolbarButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
	protected final AonToolbarButton printButton = new AonToolbarButton(AON.MSG.draft(),AON.CSS.aonIconExcel());
	protected final AonToolbarButton markAsPendingButton = new AonToolbarButton(AON.MSG.reopen(),AON.CSS.aonIconModelReopen());
	protected final AonToolbarButton markAsFinishedButton = new AonToolbarButton(AON.MSG.finish(),AON.CSS.aonIconModelFinish());
	protected final AonToolbarButton markAsSentButton = new AonToolbarButton(AON.MSG.markAsSent(),AON.CSS.aonIconModelSent());
	protected final AonToolbarButton commentsButton = new AonToolbarButton(AON.MSG.comments(), AON.CSS.aonIconNoComments());
	protected final AonToolbarButton auditButton = new AonToolbarButton(AON.MSG.audit(),AON.CSS.aonIconAudit());
	
	protected final AonToolbarButton recordButton = new AonToolbarButton(AON.MSG.record(),AON.CSS.aonIconAccountingRecord());
	protected final AonToolbarButton unrecordButton = new AonToolbarButton(AON.MSG.unrecord(),AON.CSS.aonIconAccountingUnrecord());
	protected final AonToolbarButton viewEntryButton = new AonToolbarButton(AON.MSG.editAccountEntry(),AON.CSS.aonIconAccounting());
	
	private FlowPanel paymentContainer;
	
	protected final AonToolbar decToolbar = new AonToolbar();
	protected final InlineLabel dirtyLabel = new InlineLabel();
	protected final InlineLabel diffLabel = new InlineLabel();
	protected final InlineLabel invoicesUnboundLabel = new InlineLabel();
	protected final InlineLabel manualLabel = new InlineLabel();
	protected final InlineLabel adjLabel = new InlineLabel();
	protected final InlineLabel replacedLabel = new InlineLabel();
	protected final Label statusLabel = new Label();
	
	protected FormPanel diskForm = new FormPanel("_blank");
	protected Hidden mod421Hidden = new Hidden("mod421");
	protected Hidden domainIdHidden = new Hidden("domainId");
	protected Hidden domainNameHidden = new Hidden("domainName");
	protected Hidden userHidden = new Hidden("user");
	protected Hidden mod421BoxHidden = new Hidden("mod421Box");
	private AonToastModel toast = null;
	
	protected Model421Base(Mod421 mod421,Model421Callback cbk) {
		super(Unit.PX);
		
		this.callback = cbk;
		
		select( mod421 );
		
		AonFiscalModelHeader modelHeader = new AonFiscalModelHeader(getModel());
		addNorth(modelHeader, AonFiscalModelHeader.HEIGTH);
		addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		addNorth(getDeclarationToolbarPanel(), AonToolbar.HEIGTH);
		
		fieldsMap = new EnumMap<>(Mod421Key.class);
		disabledFields = new HashSet<>();
		
		setStyleName(AON.CSS.aonSelector());
	}
	
	public Model421Callback getCallback() {
		return callback;
	}
	protected Mod421 getModel() {
		return md421;
	}
	public void setModel(Mod421 mod421) {
		this.md421 = mod421;
	}

	private AonToolbar getToolbarPanel() {
		 
		if (getCallback().getOptions().isBackButtonVisible() && getCallback().getOptions().hasExternalCallback()) {
			cancelButton.setText(AON.MSG.backAction());
			cancelButton.setTitle(AON.MSG.backAction());
		}
		cancelButton.addClickHandler(event -> cancel() );
		toolbarPanel.add(cancelButton);

		newButton.addClickHandler( event -> getCallback().onNew() );
		toolbarPanel.add(newButton);

		saveButton.addClickHandler(event -> save());
		toolbarPanel.add(saveButton);
		
		deleteButton.addClickHandler(event -> delete());
		toolbarPanel.add(deleteButton);
		
		printButton.addClickHandler( event -> print());
		toolbarPanel.add(printButton);
		
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
					Model421.service.saveComments( getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod421>() {
						@Override
						public void onSuccess(Mod421 result) {
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
		
		recordButton.addClickHandler( event -> doRecord());
		recordButton.addStyleName(AON.CSS.aonMarginLeft());
//		toolbarPanel.add(recordButton);

		unrecordButton.addClickHandler( event -> unRecord());
		unrecordButton.addStyleName(AON.CSS.aonMarginLeft());
//		toolbarPanel.add(unrecordButton);

		viewEntryButton.addClickHandler( event -> editEntry());
//		toolbarPanel.add(viewEntryButton);
		
		toolbarPanel.add(diskForm);

		return toolbarPanel;
	}
	
	private void doCancel() {
		getCallback().removeTabWidget(AON.MSG.accountEntry());
		getCallback().onCancel(getModel());
	}
	
	private void cancel() {
		cancelButton.setEnabled(false);
		if (isDirty()) {
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.confirmDeclarationCancelAction(), new AonConfirmDialogCallback() {

				@Override
				public void onAccept() {
					doCancel();
				}
				@Override
				public void onCancel() {
					cancelButton.setEnabled(true);
				}
			});
		} else {
			doCancel();
		}
	}

	private void print() {
		if (isDirty()) {
			new AonConfirmDialog().confirm(AON.MSG.draftPrint(),AON.MSG.draftPrintNote() 
					, new AonConfirmDialogCallback() {
				
				@Override
				public void onAccept() {
					submitForm(MODEL421_PRINT);
				}
				
				@Override
				public void onCancel() {
					// Nothing
				}
			});
		} else {
			submitForm(MODEL421_PRINT);
		}
	}

	protected void select( Mod421 mod421) {
		setModel(mod421);
		refreshToolbarState();
		styleStatusLabel();
	}
	
	protected void selectAndPopulate( Mod421 mod421) {
		select(mod421);
		populate(mod421);
		decorateDeclarationTab();
		decorateAdministrationTab();
	}
	
	protected void populate(Mod421 mod421) {
		identificationData.populate(mod421);
		for (Entry<Mod421Key, AonDoubleBox> entry : fieldsMap.entrySet()) {
			double d1 = mod421.getAmount(entry.getKey());
			double d2 = entry.getValue().getValue();
			entry.getValue().setEnabled(mod421.isEditable() && !disabledFields.contains(entry.getKey()));
			if (!AonNumberUtils.equals(d1, d2)) {
				entry.getValue().setValue(d1,false,true);
			}
		}
	}
	
	private void refreshToolbarState() {
		toolbarPanel.setTitle(AonStringUtils.join(getModel().getDocument(),AonStringUtils.SPACE,getModel().getFullName()));
		boolean canBeSaved = !getModel().isFinished() && !getModel().isSent() && !getModel().isRecorded(); 
		auditButton.setVisible(!getModel().isNew());
		newButton.setVisible(!getModel().isNew() && !getCallback().getOptions().isBackButtonVisible() && !getCallback().getOptions().hasExternalCallback());
		cancelButton.setVisible(true);
		saveButton.setVisible(canBeSaved);
		recordButton.setVisible((getModel().isFinished() || getModel().isSent()) && !getModel().isRecorded());
		unrecordButton.setVisible((getModel().isFinished() || getModel().isSent()) && getModel().isRecorded());
		viewEntryButton.setVisible((getModel().isFinished() || getModel().isSent()) && getModel().isRecorded() && viewEntryButton.isEnabled() );
		deleteButton.setVisible(!getModel().isNew() && canBeSaved);
		printButton.setVisible(!getModel().isNew());
		markAsPendingButton.setVisible(!getModel().isNew() && !getModel().isRecorded() && FiscalModelUtils.canChangeStatus(getModel(), FiscalStatus.PENDING));
		markAsFinishedButton.setVisible(!getModel().isNew() && FiscalModelUtils.canChangeStatus(getModel(), FiscalStatus.FINISHED));
		markAsSentButton.setVisible(!getModel().isNew() && FiscalModelUtils.canChangeStatus(getModel(), FiscalStatus.SENT));
	}

	protected EnumMap<Mod421Key, AonDoubleBox> getFieldsMap() {
		return fieldsMap;
	}
	protected EnumMap<Mod421Key, AonDoubleBox> addField(Mod421Key key, AonDoubleBox box, boolean enabled) {
		if (!enabled) {
			disabledFields.add(key);
		}
		fieldsMap.put(key,box);
		return fieldsMap;
	}
	
	protected void paintAdditionalData(FlexTable table) {
		
	}
	
	protected void paintDeclaration(FlexTable table, IModelScript<Mod421Key>[] script, int colsNumber) {
		if (table.getRowCount() > 0) {
			table.removeAllRows();
		}
		paintScript(table, script, colsNumber);
	}
	
	protected void paintScript(FlexTable table, IModelScript<Mod421Key>[] script, int colsNumber) {
		for (IModelScript<Mod421Key> ms : script) {
			paintRow(table,ms,colsNumber);	
		}
	}

	protected void paintEmptyRow(FlexTable table) {
		int row = table.getRowCount();
		table.setWidget(row, 0, new Label());
	}
	
	protected int paintEmptyCol(FlexTable table,int row, int col, int colspan) {
		table.setWidget(row, col, new Label());
		table.getFlexCellFormatter().addStyleName(row, col,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().setColSpan(row, col, colspan);
		return ++col;
	}

	protected void paintRow(FlexTable table,IModelScript<Mod421Key> script, int colsNumber) {
		if (script.hasGraphicParticularity()) {
			paintParticularyRow(table,script);		
		} else {
			int row = table.getRowCount();
			paintLabel(table,row,script);
			if (script.getKeys() == null) {
				table.getFlexCellFormatter().setColSpan(row, 0, colsNumber);	
			} else {
				table.getFlexCellFormatter().setColSpan(row, 0, (colsNumber -  ( ( script.getKeys().length * 2) + 1) ) );
				int col = 1;
				for (Mod421Key key : script.getKeys()) {
					if (key == null) {
						col = paintEmptyCol(table,row, col, 2);
					} else {
						col = paintBox(table, row, col, key );
						col = paintField(table, row, col, script, key );
					}
				}
				paintInfoCol(table,row,col,script);	
			}
		}
	}
	
	protected void paintParticularyRow(FlexTable table, IModelScript<Mod421Key> script) {
		
	}
	
	protected void paintLabel(FlexTable table,int row,IModelScript<Mod421Key> script) {
		paintLabel(table, row, script.getLabel(), script.isTitle());
	}
	protected void paintLabel(FlexTable table,int row,String labelText) {
		paintLabel(table, row, labelText, false);
	}
	protected void paintLabel(FlexTable table,int row,String labelText,boolean title) {
		Label label = new Label();
		if (AonStringUtils.length(labelText) > MAX_LABEL_LENGTH) {
			label.setTitle(labelText);	
			labelText = AonStringUtils.abbreviate(labelText, MAX_LABEL_LENGTH);
		}
		label.setText(labelText);
		table.setWidget(row, 0, label);
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		if (title) {
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBold() );
		} else {
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		}
	}
	
	private int paintBox(FlexTable table, int row, int col, Mod421Key key) {
		if (AonStringUtils.isNumeric(key.getBoxCode())) {
			table.setWidget(row, col, new AonBoxLabel(key.getBoxCode()));
		} else {
			table.setWidget(row, col, new Label());
		}
		return ++col;
	}
	
	protected int paintField(FlexTable table, int row, int col, IModelScript<Mod421Key> script, final Mod421Key key) {
		return paintField(table, row, col, key, script.getFieldSize(key), script.isEnabled( key ));
	}
	protected int paintField(FlexTable table, int row, int col, final Mod421Key key, int fieldSize, boolean enabled) {
		final FiscalModelDetail det1 = getModel().ensureDetail(key);
		final AonDoubleBox input = new AonDoubleBox(fieldSize);
		addField(key, input,enabled);
		input.setEnabled(getModel().isEditable() && enabled);
		input.setValue(det1.getAmount());
		if (!getModel().isManualDeclaration() && AonMathUtils.isNotZero(det1.getAdjustAmount())) {
			input.addStyleName(AON.CSS.aonChanged());
			input.setTitle(AON.MSG.difCalc(
					AON.FMT.format(det1.getResultAmount()),
					AON.FMT.format(AonMathUtils.round( det1.getAdjustAmount() * -1))));
		}
		input.addValueChangeHandler(event -> {
			if (input.getValue() == null) 
				input.setValue(0.0,false);
			double result = getModel().getResultAmount(key);
			double adjust = getModel().getAdjustAmount(key);
			double amount = input.getValue();
			if (AonMathUtils.isNotZero(result - adjust - amount)) {
				getModel().ensureDetail(key).setAdjustAmount( result - amount);	
			}
			getModel().ensureDetail(key).setAmount(input.getValue());

			if (!getModel().isManualDeclaration() && AonMathUtils.isNotZero(getModel().ensureDetail(key).getAdjustAmount())) {
				input.addStyleName(AON.CSS.aonChanged());
				input.setTitle(AON.MSG.difCalc(AON.FMT.format(getModel().ensureDetail(key).getResultAmount()),
						AON.FMT.format(AonMathUtils.round( getModel().ensureDetail(key).getAdjustAmount() * -1))));
			} else {
				input.removeStyleName(AON.CSS.aonChanged());
			}
			
			if (input.isEnabled()) {
				calculateAndRefresh();
			}
			markAsDirty();
		});
		table.setWidget(row, col, input);
		return ++col;
	}
	
	protected CheckBox paintWithoutActivityCheck(FlexTable table) {
		int row = table.getRowCount();
		paintLabel(table, row, AON.MSG.withoutActivity());
		final CheckBox check = new CheckBox();
		check.setValue(getModel().isWithoutActivity());
		check.setEnabled(getModel().isEditable());
		check.addClickHandler( event -> {
			getModel().setWithoutActivity(check.getValue());
				markAsDirty();
		});
		table.setWidget(row, 1, check);
		return check;
	}
	
	protected AonTextBox paintAdministrationCode(FlexTable table) {
		int row = table.getRowCount();
		paintLabel(table, row, "C\u00F3digo Administraci\u00F3n Tributaria");
		final AonTextBox textBox = new AonTextBox();
		textBox.setVisibleLength(5);
		textBox.setMaxLength(5);
		textBox.setEnabled(getModel().isEditable());
		if (AonStringUtils.isNotEmpty(getModel().getAdmonAeat())) {
			textBox.setValue(getModel().getAdmonAeat(), false);
		}
		textBox.addValueChangeHandler( event -> {
			getModel().setAdmonAeat(textBox.getValue());
			markAsDirty();
		});
		table.setWidget(row, 1, textBox);
		return null;
	}

	protected CheckBox paintCheck(Mod421Key key, FlexTable table) {
		int row = table.getRowCount();
		paintLabel(table, row, key.getDescription());
		final CheckBox check = new CheckBox();
		check.setValue(getModel().ensureDetail(key).getAmount() == 1);
		check.setEnabled(getModel().isEditable());
		check.addClickHandler( event -> {
			getModel().ensureDetail(key).setAmount((check.getValue() != null && check.getValue()) ? 1.0 : 0.0);
			markAsDirty();
		});
		table.setWidget(row, 1, check);
		return check;
	}
	
	protected AonDateBox paintDate(Mod421Key key, FlexTable table) {
		int row = table.getRowCount();
		paintLabel(table, row, key.getDescription());
		
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		final AonDateBox dateBox = new AonDateBox();
		if (AonStringUtils.isNotEmpty( getModel().ensureDetail(key).getDescription() ) ) {
			dateBox.setValue( dateBox.parse(getModel().ensureDetail(key).getDescription() , false) );
		}
		dateBox.setEnabled(getModel().isEditable());
		dateBox.addValueChangeHandler( event -> {
			getModel().ensureDetail(key).setDescription(dateBox.format());
			markAsDirty();
		});
		table.setWidget(row, 1, dateBox);
		return dateBox;
	}

	protected void paintTextBox(FlexTable table, final Mod421Key key, int fieldSize, boolean enabled) {
		int row = table.getRowCount();
		paintLabel(table, row, key.getDescription());
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		
		final AonTextBox textBox = new AonTextBox();
		textBox.setVisibleLength(fieldSize+1);
		textBox.setMaxLength(fieldSize);
		textBox.setEnabled(enabled);
		textBox.setEnabled(enabled && getModel().isEditable());
		if (AonStringUtils.isNotEmpty( getModel().ensureDetail(key).getDescription() ) ) {
			textBox.setValue( getModel().ensureDetail(key).getDescription() , false );
		}
		textBox.addValueChangeHandler( event ->  {
			getModel().ensureDetail(key).setDescription(textBox.getValue());
			markAsDirty();
		});
		table.setWidget(row, 1, textBox);
	}

	protected void paintListBox(ListBox listBox, Mod421Key key, FlexTable table) {
		int row = table.getRowCount();
		paintLabel(table, row, key.getDescription());
		
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		listBox.setSelectedIndex( (int) getModel().ensureDetail(key).getAmount() );
		listBox.setEnabled(getModel().isEditable());
		listBox.addChangeHandler( event -> {
			getModel().ensureDetail(key).setAmount(listBox.getSelectedIndex());
			markAsDirty();
		});
		table.setWidget(row, 1, listBox);
	}

	protected FlowPanel addGroupPanel(String label, Widget w) {
		FlowPanel groupPanel = new FlowPanel();
		groupPanel.setStyleName(AON.CSS.aonGroup());
		
			FlowPanel groupHeaderPanel = new FlowPanel();
			groupHeaderPanel.setStyleName(AON.CSS.aonGroupTitle());
			groupHeaderPanel.add (new InlineLabel(label)); 
			groupPanel.add(groupHeaderPanel);
			
			FlowPanel groupBodyPanel = new FlowPanel();
			groupBodyPanel.setStyleName(AON.CSS.aonGroupBody());
			groupBodyPanel.add(w);
			groupPanel.add(groupBodyPanel);
		return groupPanel;
	}

	private void paintInfoCol(FlexTable table, int row, int col, final IModelScript<Mod421Key> script) {
		
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.CSS.aonNowrap());
		for (final FiscalModelKeyInfo infoKey : script.getInfoKeys()) {
			infoKey.visit(new IFiscalModelKeyInfoVisitor<Void>() {

				private void showComputeKeyInfo(AonTableButton button) {
					button.setEnabled(false);
					final PopupPanel popup = new PopupPanel(false, true);
					popup.add(new AonSplash());
					popup.setGlassEnabled(true);
					popup.setAnimationEnabled(true);
					popup.center();
					
					Model421.service.getInfo(callback.getOptions().getOccam(),getModel(),script, infoKey, new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							popup.hide();
							button.setEnabled(true);
							callback.showError(AON.MSG.errorMessage());
						}
	
						@Override
						public void onSuccess(String result) {
							popup.hide();
							FlowPanel gridContainer = new FlowPanel();
							JsVatComputeKeyInfoGridPanel grid = new JsVatComputeKeyInfoGridPanel();
							grid.setTitle(AON.MSG.calcDetail());
							grid.setSubTitle(AonStringUtils.join(
								Arrays.stream(script.getKeys())
									.filter( Objects::nonNull )
									.map( Mod421Key::getBoxFormatted )
									.reduce("", String::concat)
								, " " 
								, script.getLabel()));
							
							try {
								JsVatComputeKeyInfo info = JsonUtils.safeEval(result);
								grid.addContent(info);
							} catch (Exception e) {
								grid.addContent(result);
							}
							gridContainer.add(grid);
							callback.showInfoPanelWidget(gridContainer);
							button.setEnabled(true);
						}
					});
				}
				
				private void showDiffInfo(AonTableButton button) {
					button.setEnabled(false);
					final PopupPanel popup = new PopupPanel(false, true);
					popup.add(new AonSplash());
					popup.setGlassEnabled(true);
					popup.setAnimationEnabled(true);
					popup.center();
					Model421.service.getInfo(callback.getOptions().getOccam(),getModel(),script, FiscalModelKeyInfo.DIFF_INVOICE, new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							popup.hide();
							callback.showError(AON.MSG.errorMessage());
							button.setEnabled(true);
						}
	
						@Override
						public void onSuccess(String result) {
							popup.hide();
							FlowPanel gridContainer = new FlowPanel();
							JsVatComputeKeyInfoGridPanel grid = new JsVatComputeKeyInfoGridPanel();
							grid.setTitle(AON.MSG.calcDetail());
							grid.setSubTitle(AonStringUtils.join(
								Arrays.stream(script.getKeys())
									.filter( Objects::nonNull )
									.map( Mod421Key::getBoxFormatted )
									.reduce("", String::concat)
								, " " 
								, script.getLabel()));
							grid.addContent(result);
							gridContainer.add(grid);
							callback.showInfoPanelWidget(gridContainer);
							button.setEnabled(true);
						}
					});
				}
				
				private void showInvoiceVatBreakdownInfo(AonTableButton button, boolean prorrated) {
					button.setEnabled(false);
					final PopupPanel popup = new PopupPanel(false, true);
					popup.add(new AonSplash());
					popup.setGlassEnabled(true);
					popup.setAnimationEnabled(true);
					popup.center();
					Model421.service.getInfo(callback.getOptions().getOccam(),getModel(),script, infoKey, new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							popup.hide();
							callback.showError(AON.MSG.errorMessage());
							button.setEnabled(true);
						}
	
						@Override
						public void onSuccess(String result) {
							popup.hide();
							JsVatContextBreakdownGridPanel grid = new JsVatContextBreakdownGridPanel();
							grid.addSelectionHandler(event -> showInvoice(event.getSelectedItem()));
							grid.setTitle(AON.MSG.modelRelatedInvoices(getModel().getModelFullName()));
							if ( !getModel().hasInvoicesBound() ) {
								grid.setRemarks("Modelo sin facturas vinculadas. Se muestran los datos relativos al periodo que abarca el modelo.");
							}
							grid.setSubTitle(AonStringUtils.join(
								Arrays.stream(script.getKeys())
									.filter( Objects::nonNull )
									.map( Mod421Key::getBoxFormatted )
									.reduce("", String::concat)
								, " " 
								, script.getLabel()));
							JavaScriptObject arrayObject = JsonUtils.safeEval(result);
							JsArray<JsVatContext> array = arrayObject.cast();
							grid.render(array);
							callback.showInfoPanelWidget(grid);
							button.setEnabled(true);
						}
					});
				}

				private void showComputeInfo(AonTableButton button) {
					button.setEnabled(false);
					final PopupPanel popup = new PopupPanel(false, true);
					popup.add(new AonSplash());
					popup.setGlassEnabled(true);
					popup.setAnimationEnabled(true);
					popup.center();
					Model421.service.getInfo(callback.getOptions().getOccam(),getModel(),script, infoKey, new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							popup.hide();
							callback.showError(AON.MSG.errorMessage());
							button.setEnabled(true);
						}
	
						@Override
						public void onSuccess(String result) {
							popup.hide();
							FlowPanel gridContainer = new FlowPanel();
							JavaScriptObject arrayObject = JsonUtils.safeEval(result);
							JsArray<JsVatComputeInfo> array = arrayObject.cast();
							for (int i = 0; i < array.length(); i++) {
								JsVatComputeInfo computeInfo = array.get(i);
								Mod421Key key = Mod421Key.valueOf(computeInfo.getKey());
								JsVatComputeInfoGridPanel grid = new JsVatComputeInfoGridPanel() {

									@Override
									protected String resolveKey(String keyString) {
										Mod421Key key = Mod421Key.valueOf(keyString);
										return key.getBoxAsString();
									}
									
								};

								grid.setTitle(AON.MSG.calcDetail());
								grid.setSubTitle(key.getBoxFormatted() + " - " + script.getLabel());
								grid.addContent(computeInfo);
								gridContainer.add(grid);
							}
							callback.showInfoPanelWidget(gridContainer);
							button.setEnabled(true);
						}
					});
				}
				
				private AonTableButton addExcelButton() {
					final AonTableButton button = new AonTableButton(infoKey.getLabel() + " (Excel)" ,AON.CSS.aonIconExcel());
					button.setTabIndex(-2);
					buttonContainer.add(button);
					return button;
				}

				private Optional<AonTableButton>  addDiffButton() {
					if (!getModel().isManualDeclaration() && getModel().isDiffCalculationEnabled()) {
						boolean diffKey = Arrays.stream(script.getKeys())
								.filter(Objects::nonNull)
								.anyMatch(Mod421Key::isDiffEnabled);
						if ( diffKey ) {
							final AonTableButton button = new AonTableButton(infoKey.getLabel(),AON.CSS.aonIconDiff());
							button.setTabIndex(-2);
							buttonContainer.add(button);
							return Optional.of(button);
						}
					}
					return Optional.empty();
				}
				
				private AonTableButton addButton() {
					final AonTableButton button = new AonTableButton(infoKey.getLabel(),AON.CSS.aonIconHelp());
					button.setTabIndex(-2);
					buttonContainer.add(button);
					return button;
				}
				
				@Override 
				public Void visitModelInvoiceVatBreakdown() {
					if (!getModel().isManualDeclaration()) {
						final AonTableButton button = addButton();
						button.addClickHandler(event -> showInvoiceVatBreakdownInfo(button, false));
						addExcelButton().addClickHandler(event -> showExcelInfo(script, false));
						addDiffButton().ifPresent( diffButton -> diffButton.addClickHandler(event -> showDiffInfo(diffButton)) ); 
					}
					return null;
				}
				
				@Override 
				public Void visitProrratedModelInvoiceVatBreakdown() {
					if (!getModel().isManualDeclaration()) {
						final AonTableButton button = addButton();
						button.addClickHandler(event -> showInvoiceVatBreakdownInfo(button,true));
						addExcelButton().addClickHandler(event -> showExcelInfo(script, true));
						addDiffButton().ifPresent( diffButton -> diffButton.addClickHandler(event -> showDiffInfo(diffButton)) );
					}
					return null;
				}
				@Override 
				public Void visitModelInVatAccrualInvoice() {
					return visitModelInvoiceVatBreakdown();	
				}
				
				@Override 
				public Void visitModelOutVatAccrualInvoice() {
					return visitModelInvoiceVatBreakdown();
				}


				@Override public Void visitCompute() { 
					final AonTableButton button = addButton();
					button.addClickHandler(event -> showComputeInfo(button));
					return null; 
				}
				@Override 
				public Void visitComputeKey() {
					if (!getModel().isManualDeclaration()) {
						final AonTableButton button = addButton();
						button.addClickHandler(event -> showComputeKeyInfo(button));
					}
					return null; 
				}
				
				@Override public Void visitInvoice() {return null;}
				@Override public Void visitModelInvoiceIrpfBreakdown() {return null;}
				@Override public Void visitSalary() { return null; }
				@Override public Void visitModelSalaryIrpfBreakdown() { return null; }
				@Override public Void visitDiffInvoice() {return null;}
				@Override public Void visitDiffSalary() { return null;}
				@Override public Void visitNone() { return null; }
				@Override public Void visitInAccrualInvoice() { return null; }
				@Override public Void visitOutAccrualInvoice() { return null; }
				@Override public Void visitDiffInAccrualInvoice() { return null; }
				@Override public Void visitDiffOutAccrualInvoice() { return null; }
				@Override public Void visitActAccount() { return null; }
				@Override public Void visitTitle() { return null; }
				@Override public Void visitIrpfActivity() { return null; }
				@Override public Void visitCorporate() { return null; }
			});
		}
		table.setWidget(row, col, buttonContainer);
	}
	
	public void calculateAndRefresh(AsyncCallback<Mod421> cbk) {
		Model421.service.calculate(getCallback().getOptions().getOccam(), getModel(),
				new AsyncCallback<Mod421>() {

					@Override
					public void onFailure(Throwable caught) {
						getCallback().showError(AON.MSG.errorMessage());
					}

					@Override
					public void onSuccess(Mod421 result) {
						selectAndPopulate(result);
						if (cbk != null) cbk.onSuccess(result);
					}
			
				}
			);	
	}

	public void calculateAndRefresh() {
		calculateAndRefresh(null);
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

	protected void save() {
		save(null);
	}
	protected void save(AsyncCallback<Mod421> cbk) {
		saveButton.setEnabled(false);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		getCallback().hideError();
		Model421.service.save(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod421>() {
					@Override
					public void onSuccess(Mod421 result) {
						setDirty( false );
						selectAndPopulate(result);
						popup.hide();
						saveButton.setEnabled(true);
						if (cbk != null)
							cbk.onSuccess(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						saveButton.setEnabled(true);
					}
				});
	}
	
	private void delete() {
		deleteButton.setEnabled(false);
		AonConfirmDialog cd = new AonConfirmDialog();
		cd.confirm(AON.MSG.confirmDeclarationDeleteAction(), new AonConfirmDialogCallback() {

			@Override
			public void onAccept() {
				Model421.service.delete(getCallback().getOptions().getOccam(),getModel(), new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						deleteButton.setEnabled(true);
						getCallback().onRemove(getModel());
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
	
	private void onFinalize( ) {
		markAsFinishedButton.setEnabled(false);
		if (isDirty()) {
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.confirmDeclarationSaveAction(), new AonConfirmDialogCallback() {

				@Override
				public void onAccept() {
					save( new AsyncCallback<Mod421>() {

						@Override
						public void onSuccess(Mod421 result) {
							doFinalize();
						}
						@Override
						public void onFailure(Throwable caught) {
							// Nothing
						}
					});
				}
				@Override
				public void onCancel() {
					markAsFinishedButton.setEnabled(true);
				}
			});
		} else {
			doFinalize();
		}
	}
	
	private void doFinalize( ) {
		Model421.service.initializeForFinish(getCallback().getOptions().getOccam(),getModel(),
				new AsyncCallback<Mod421>() {
					@Override
					public void onSuccess(Mod421 result) {
						selectAndPopulate(result);
						showFinalizePopup();
						markAsFinishedButton.setEnabled(true);
					}
	
					@Override
					public void onFailure(Throwable caught) {
						getCallback().showError(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
						markAsFinishedButton.setEnabled(true);
					}
				});
	}
	
	private void showFinalizePopup() {
		Model421FinishDeclarationPopup finalizeDialog = new Model421FinishDeclarationPopup(getModel(), getCallback(), new FinishDeclarationPopupCallback() {
			@Override
			public void onCancel() {
				// nothing
			}
			
			@Override
			public void onAccept() {
				finish();
			}
			@Override
			public void onCustomerCheck() {
				markAsCustomerCheck();
			}
		});
		finalizeDialog.center();
		finalizeDialog.show();
	}
	
	private void markAsCustomerCheck( ) {
		markAsFinishedButton.setEnabled(false);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model421.service.markAsCustomerCheck(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod421>() {
					@Override
					public void onSuccess(Mod421 result) {
						setDirty(false);
						selectAndPopulate(result);
						popup.hide();
						markAsFinishedButton.setEnabled(true);
						showPaymentInfo(getModel());
						FiscalModelEmailUtils.sendEmail(getCallback(), result); // Si ha ido bien el cambio de estado, entonces se envía email de notificación
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						markAsFinishedButton.setEnabled(true);
					}
				});
	}

	private void finish() {
		markAsFinishedButton.setEnabled(false);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add(new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model421.service.markAsFinished(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod421>() {
					@Override
					public void onSuccess(Mod421 result) {
						setDirty(false);
						selectAndPopulate(result);
						popup.hide();
						markAsFinishedButton.setEnabled(true);
						showPaymentInfo(getModel());
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						markAsFinishedButton.setEnabled(true);
					}
				});
	}
	
	private void reopenDeclaration() {
		markAsPendingButton.setEnabled(false);
		if (getModel().isSent() && AonStringUtils.isNotBlank(getModel().getNumber())) {
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.confirmReopenDeclarationAction(), new AonConfirmDialogCallback() {
				
				@Override
				public void onAccept() {
					doReopenDeclaration();
				}
				
				@Override
				public void onCancel() {
					markAsPendingButton.setEnabled(true);
				}
			});
		} else {
			doReopenDeclaration();
		}
	}
	
	private void doReopenDeclaration() {
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add(new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model421.service.markAsPending(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod421>() {
					@Override
					public void onSuccess(Mod421 result) {
						setDirty(false);
						selectAndPopulate(result);
						popup.hide();
						markAsPendingButton.setEnabled(true);
						showPaymentInfo(getModel());
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						getCallback().showError(AON.MSG.unableToReopenDeclaration(caught.getMessage()));
						markAsPendingButton.setEnabled(true);
					}
				});
	}
	
	private void markAsSent() {
		markAsSentButton.setEnabled(false);
		Model421.service.markAsSent(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod421>() {
					@Override
					public void onSuccess(Mod421 result) {
						setDirty(false);
						selectAndPopulate(result);
						markAsSentButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						getCallback().showError(AON.MSG.unableToMarkAsSentDeclaration(caught.getMessage()));
						markAsSentButton.setEnabled(true);
					}
				});
	}
	
	private void doRecord() {
		recordButton.setEnabled(false);
		AonConfirmDialog cd = new AonConfirmDialog();
		cd.confirm(AON.MSG.confirmRecordDeclarationAction(), new AonConfirmDialogCallback() {
			
			@Override
			public void onAccept() {
				Model421.service.doRecord(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod421>() {
					@Override
					public void onSuccess(Mod421 result) {
						setDirty(false);
						selectAndPopulate(result);
						viewEntry();
						recordButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						getCallback().showError(AON.MSG.unableToRecordDeclaration(caught.getMessage()));
						recordButton.setEnabled(true);
					}
				});
			}
			
			@Override
			public void onCancel() {
				recordButton.setEnabled(true);
			}
		});
	}
	
	private void viewEntry() {
		final Widget entryContainer = getCallback().getTabWidget( AON.MSG.accountEntry() );
		if ( entryContainer instanceof HasWidgets) {
			HasWidgets tab = (HasWidgets) entryContainer;
			tab.clear();
			SessionLog entryLog = new SessionLog(new AccountEntryModuleOptions()
				.setDomainName( getCallback().getOptions().getDomainName() )
				.setUser( getCallback().getOptions().getUser() )
				.setDomain( getCallback().getOptions().getDomain())
				.setAccountEntryId( getModel() .getAccountEntry())
				.setTrialBalanceFromPreviewEnabled(false)
				.setExternalCallback( new ModuleCallback() {
					private static final long serialVersionUID = -1649058327545857212L;
					
					@Override 
					public void onFailure(Throwable caught) {
						viewEntryButton.setEnabled(true);
						getCallback().showError(caught.getMessage());
					}
				}),getModel().getAccountEntry());
			entryLog.addSelectionHandler(wrp -> editEntry());
			tab.add(entryLog);
		} else {
			getCallback().showError("Error interno al mostrar el apunte contable.");
		}
		
	}
	
	private void editEntry() {
		final Widget entryContainer = getCallback().getTabWidget( AON.MSG.accountEntry() );
		if ( entryContainer instanceof HasWidgets) {
			HasWidgets tab = (HasWidgets) entryContainer;
			tab.clear();
			AccountEntryModule module = new AccountEntryModule();
			module.onModuleLoad( new AccountEntryModuleOptions()
				.setParentWidget( tab )
				.setDomainName( getCallback().getOptions().getDomainName() )
				.setUser( getCallback().getOptions().getUser() )
				.setDomain( getCallback().getOptions().getDomain())
				.setAccountEntryId( getModel() .getAccountEntry())
				.setSessionLogTabVisible(false)
				.setJournalTabVisible(false)
				.setExtraInfoTabVisible(false)
				.setPreviewTabVisible(false)
				.setDeleteButtonVisible(false)
				.setTrialBalanceFromPreviewEnabled(false)
				.setExternalCallback( new ModuleCallback() {
			
					private static final long serialVersionUID = -1649058327545857212L;
					
					@Override public void onExit() {removeTab(); }
					@Override public void onChange(IAccountEntryWrapper changed) {removeTab();}
					@Override public void onRemove(IAccountEntryWrapper removed) {removeTab();}
					
					private void removeTab() {
						getCallback().removeTabWidget( AON.MSG.accountEntry() );
					}

					@Override
					public void onFailure(Throwable caught) {
						getCallback().showError(caught.getMessage());
					}
					
				}));
		}
	}
		
	private void unRecord() {
		unrecordButton.setEnabled(false);
		AonConfirmDialog cd = new AonConfirmDialog();
		cd.confirm(AON.MSG.confirmUnrecordDeclarationAction(), new AonConfirmDialogCallback() {
			
			@Override
			public void onAccept() {
				Model421.service.unrecord(getCallback().getOptions().getOccam(), getModel(), new AsyncCallback<Mod421>() {
					@Override
					public void onSuccess(Mod421 result) {
						setDirty(false);
						selectAndPopulate(result);
						unrecordButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						getCallback().showError(AON.MSG.unableToUnrecordDeclaration(caught.getMessage()));
						unrecordButton.setEnabled(true);
					}
				});
			}
			
			@Override
			public void onCancel() {
				unrecordButton.setEnabled(true);
			}
		});
	}

	private void audit() {
		AonAuditDialog dialog = new AonAuditDialog();
		dialog.show(getModel());
	}
	
	private void submitForm(String action) {
		diskForm.setMethod(FormPanel.METHOD_POST);
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		diskForm.clear();
		FlowPanel diskPanel = new FlowPanel();
		diskPanel.add(mod421Hidden);
		diskPanel.add(domainIdHidden);
		diskPanel.add(domainNameHidden);
		diskPanel.add(userHidden);
		diskForm.add(diskPanel);
		mod421Hidden.setValue(String.valueOf(getModel().getId()));
		domainIdHidden.setValue(String.valueOf(getCallback().getOptions().getDomain()));
		domainNameHidden.setValue(getCallback().getOptions().getDomainName());
		userHidden.setValue(getCallback().getOptions().getUser());
		diskForm.submit();
	}
	
	private AonToolbar getDeclarationToolbarPanel() {
		
		markAsFinishedButton.setText(markAsFinishedButton.getTitle());
		markAsFinishedButton.addClickHandler( event -> onFinalize());
		decToolbar.add(markAsFinishedButton);

		markAsSentButton.setText(markAsSentButton.getTitle());
		markAsSentButton.addClickHandler( event -> markAsSent());
		decToolbar.add(markAsSentButton);

		markAsPendingButton.setText(markAsPendingButton.getTitle());
		markAsPendingButton.addClickHandler( event -> reopenDeclaration());
		decToolbar.add(markAsPendingButton);

		FlowPanel marksPanels = new FlowPanel();
		marksPanels.setStyleName(AON.CSS.aonFlexBlock());
		
		dirtyLabel.setStyleName(AON.CSS.aonIconLabel());
		dirtyLabel.addStyleName(AON.CSS.aonIconDirty());
		dirtyLabel.setTitle("Cambios sin guardar");
		marksPanels.add(dirtyLabel);
		
		diffLabel.setStyleName(AON.CSS.aonMarginLeft());
		diffLabel.addStyleName(AON.CSS.aonLabelWithIcon());
		diffLabel.addStyleName(AON.CSS.aonIconDiff());
		diffLabel.setTitle("C\u00E1lculo por diferencia habilitado");
		marksPanels.add(diffLabel);
		
		invoicesUnboundLabel.setStyleName(AON.CSS.aonMarginLeft());
		invoicesUnboundLabel.addStyleName(AON.CSS.aonLabelWithIcon());
		invoicesUnboundLabel.addStyleName(AON.CSS.aonIconWarning());
		invoicesUnboundLabel.setTitle("Modelo sin facturas vinculadas");
		marksPanels.add(invoicesUnboundLabel);

		adjLabel.setStyleName(AON.CSS.aonMarginLeft());
		adjLabel.addStyleName(AON.CSS.aonIconLabel());
		adjLabel.addStyleName(AON.CSS.aonIconWrench());
		adjLabel.addStyleName(AON.CSS.aonColorBlue());
		adjLabel.setTitle("Ajustes realizados");
		marksPanels.add(adjLabel);

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
		
		manualLabel.setStyleName(AON.CSS.aonMarginLeft());
		manualLabel.addStyleName(AON.CSS.aonLabelWithIcon());
		manualLabel.addStyleName(AON.CSS.aonIconEditRed());
		manualLabel.setTitle("Declaraci\u00F3n realizada manualmente");
		marksPanels.add(manualLabel);

		styleDirtyLabel();
		styleStatusLabel();
		
		decToolbar.getMessagePanel().add(marksPanels);
		
		decToolbar.setTitle(statusLabel);
		return decToolbar;
	}
	
	protected void styleDirtyLabel() {
		dirtyLabel.setVisible(isDirty());
		diffLabel.setVisible(!getModel().isDiffCalculationDisabled());
		invoicesUnboundLabel.setVisible(!getModel().hasInvoicesBound());
		manualLabel.setVisible(getModel().isManualDeclaration());
		
		boolean adjusted = false;
		if (!getModel().isManualDeclaration()) {
			for (FiscalModelDetail det : getModel().getMap().values()) {
				if (AonMathUtils.isNotZero( det.getAdjustAmount())) {
					adjusted = true;
					break;
				}
			}
		}
		adjLabel.setVisible(adjusted);
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
	
	protected void showPaymentInfo(Mod421 mod) {
		if (paymentContainer != null) {
			this.remove(paymentContainer);
			this.forceLayout();
		}
		if (mod.isFinished() || mod.isSent() || mod.isCustomerCheck()) {
			paymentContainer = new FlowPanel();
			paymentContainer.setStyleName(AON.CSS.aonWidthAll());
			Label label = new Label( FiscalModelUtils.getPaymentInfo(mod) );
			label.setStyleName(AON.CSS.aonWidthAll());
			label.addStyleName(AON.CSS.aonTextCenter());
			label.addStyleName(AON.CSS.aonBold());
			paymentContainer.add(label);
			this.insert( paymentContainer, Direction.NORTH, 30, decToolbar);
			this.forceLayout();
		}
	}

	protected void paintIdentificationTab(TabLayoutPanel tabPanel) {
		identificationData = new AonFiscalModelIdentificationPanel<>( getModel() ) ;
		identificationData.addValueChangeHandler(event -> {
			toolbarPanel.setTitle(AonStringUtils.join(getModel().getDocument(),AonStringUtils.SPACE,getModel().getFullName()));
			markAsDirty();			
		});
		tabPanel.add(identificationData, AON.MSG.identification());
	}

	protected void decorateAdministrationTab() {
		if (admonPanel != null) {
			admonPanel.manageLinks();
		}
	}

	private void showInvoice(JsVatContext vt) {
		int invoiceId = vt.getInvoice();
		Model421.service.getInvoice(getCallback().getOptions().getOccam(), invoiceId,new AsyncCallback<Invoice>() {
			@Override
			public void onSuccess(Invoice inv) {
				AonCustomPopup dialog = new AonCustomPopup();
				dialog.setWidth((Window.getClientWidth() - 100) + "px");
				dialog.setHeight((Window.getClientHeight() - 100) + "px");
				dialog.setAnimationEnabled(true);
				dialog.setGlassEnabled(true);
				dialog.setModal(true);
				dialog.setCaption(AON.MSG.invoice());
				dialog.add(new AonInvoiceViewer(inv));
				dialog.center();
				dialog.show();
			}

			@Override
			public void onFailure(Throwable caught) {
				getCallback().showError(caught.getMessage());
			}
		});
	}
	
	protected void enable( HasEnabled widget ) {
		if (widget != null) widget.setEnabled(getModel().isEditable()); 
	}

	void decorateDeclarationTab() {
		
	}
	
	private void showExcelInfo(IModelScript<Mod421Key> script, boolean prorrated) {
		Mod421Key key = Arrays.stream(script.getKeys())
				.filter( Objects::nonNull )
				.findAny()
				.orElse(null);
		if (key != null) {
			diskForm.setMethod(FormPanel.METHOD_POST);
			diskForm.setAction(GWT.getHostPageBaseURL() + MODEL421_BOX_INFO);
			diskForm.clear();
			FlowPanel diskPanel = new FlowPanel();
			diskPanel.add(mod421Hidden);
			diskPanel.add(domainIdHidden);
			diskPanel.add(domainNameHidden);
			diskPanel.add(userHidden);
			diskPanel.add(mod421BoxHidden);
			diskForm.add(diskPanel);
			mod421Hidden.setValue(String.valueOf(getModel().getId()));
			domainIdHidden.setValue(String.valueOf(getCallback().getOptions().getDomain()));
			domainNameHidden.setValue(getCallback().getOptions().getDomainName());
			userHidden.setValue(getCallback().getOptions().getUser());
			mod421BoxHidden.setValue(key.toString());
			diskForm.submit();
		}
	}
	
}
