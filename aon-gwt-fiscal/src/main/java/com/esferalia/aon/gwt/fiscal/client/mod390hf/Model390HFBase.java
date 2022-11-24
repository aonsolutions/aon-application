package com.esferalia.aon.gwt.fiscal.client.mod390hf;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.Objects;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.AonInvoiceViewer;
import com.esferalia.aon.gwt.fiscal.client.invoice.vat.JsVatComputeInfo;
import com.esferalia.aon.gwt.fiscal.client.invoice.vat.JsVatComputeInfoGridPanel;
import com.esferalia.aon.gwt.fiscal.client.invoice.vat.JsVatComputeKeyInfo;
import com.esferalia.aon.gwt.fiscal.client.invoice.vat.JsVatComputeKeyInfoGridPanel;
import com.esferalia.aon.gwt.fiscal.client.invoice.vat.JsVatContext;
import com.esferalia.aon.gwt.fiscal.client.invoice.vat.JsVatContextBreakdownGridPanel;
import com.esferalia.aon.gwt.fiscal.client.mod390hf.Model390HF.Model390HFCallback;
import com.esferalia.aon.gwt.fiscal.client.mod390hf.Model390HFFinishDeclarationPopup.FinishDeclarationPopupCallback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelIdentificationPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelKeyInfoVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;

public abstract class Model390HFBase extends DockLayoutPanel  {

	protected static final String MODEL390HF_FILE = "/aon_gwt_fiscal/Model390HFFile";
	private static final  String MODEL390HF_PRINT = "/aon_gwt_fiscal/Model390HFPrint";

	protected static final String WIDTH_250PX = "250px";
	protected static final String WIDTH_150PX = "150px";
	protected static final String WIDTH_140PX = "140px";
	

	protected static final boolean ENABLED = true;
	protected static final boolean DISABLED = false;
	protected static final boolean HAS_INFO = true;
	protected static final boolean HAS_NOT_INFO = false;
	
	private static final int MAX_LABEL_LENGTH = 100;
	
	private Mod390HF mod390HF;
	private Model390HFCallback callback;
	private EnumMap<Mod390Key,AonDoubleBox> fieldsMap;
	private boolean dirty;
	
	protected AonFiscalModelIdentificationPanel<Mod390HF> identificationData;
	protected FiscalModelAdmonPanel<Mod390HF, Model390HFModuleOptions> admonPanel;
	
	protected final AonToolbar toolbarPanel = new AonToolbar(); 
	protected final AonToolbarButton newButton = new AonToolbarButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
	protected final AonToolbarButton saveButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave());
	protected final AonToolbarButton cancelButton = new AonToolbarButton(AON.MSG.cancelAction(),AON.CSS.aonIconBack());
	protected final AonToolbarButton deleteButton = new AonToolbarButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
	protected final AonToolbarButton resetButton = new AonToolbarButton(AON.MSG.resetAction(),AON.CSS.aonIconRefresh());
	protected final AonToolbarButton printButton = new AonToolbarButton(AON.MSG.draft(),AON.CSS.aonIconExcel());
	protected final AonToolbarButton markAsPendingButton = new AonToolbarButton(AON.MSG.reopen(),AON.CSS.aonIconModelReopen());
	protected final AonToolbarButton markAsFinishedButton = new AonToolbarButton(AON.MSG.finish(),AON.CSS.aonIconModelFinish());
	protected final AonToolbarButton markAsSentButton = new AonToolbarButton(AON.MSG.markAsSent(),AON.CSS.aonIconModelSent());
	protected final AonToolbarButton commentsButton = new AonToolbarButton(AON.MSG.comments(), AON.CSS.aonIconNoComments());
	protected final AonToolbarButton auditButton = new AonToolbarButton(AON.MSG.audit(),AON.CSS.aonIconAudit());
	
	private FlowPanel paymentContainer;
	
	protected final AonToolbar decToolbar = new AonToolbar();
	protected final InlineLabel dirtyLabel = new InlineLabel();
	protected final InlineLabel diffLabel = new InlineLabel();
	protected final InlineLabel adjLabel = new InlineLabel();
	protected final InlineLabel replacedLabel = new InlineLabel();
	protected final InlineLabel prorataLabel = new InlineLabel();
	protected final Label statusLabel = new Label();
	
	protected FormPanel diskForm = new FormPanel("_blank");
	protected Hidden mod390HFHidden = new Hidden("mod390HF");
	protected Hidden domainIdHidden = new Hidden("domainId");
	protected Hidden domainNameHidden = new Hidden("domainName");
	protected Hidden userHidden = new Hidden("user");
	
	protected Model390HFBase(Mod390HF mod390HF,Model390HFCallback cbk) {
		super(Unit.PX);
		
		this.callback = cbk;
		
		select( mod390HF );
		
		AonFiscalModelHeader modelHeader = new AonFiscalModelHeader(this.mod390HF);
		addNorth(modelHeader, AonFiscalModelHeader.HEIGTH);
		addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		addNorth(getDeclarationToolbarPanel(), AonToolbar.HEIGTH);
		
		fieldsMap = new EnumMap<>(Mod390Key.class);
		
		
		setStyleName(AON.CSS.aonSelector());
	}
	
	public Model390HFCallback getCallback() {
		return callback;
	}
	protected Mod390HF getModel() {
		return mod390HF;
	}
	public void setModel(Mod390HF mod390HF) {
		this.mod390HF = mod390HF;
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
		
		resetButton.addClickHandler( event -> onReset());
		toolbarPanel.add(resetButton);		
		
		printButton.addClickHandler( event ->  print());
		toolbarPanel.add(printButton);
		
		commentsButton.addClickHandler( event -> {
			final AonToast toast = new AonToast();
			FlowPanel commentPanel = new FlowPanel();
			commentPanel.setStyleName( FiscalModelUtils.getAdministrationBackgroundStyle(mod390HF.getAdministration()) );
			commentPanel.addStyleName(AON.CSS.aonHeightAll());
			commentPanel.addStyleName(AON.CSS.aonTextCenter());
			TextArea comment = new TextArea();
			comment.addValueChangeHandler(event1 -> {
				mod390HF.setComments(event1.getValue());
				styleCommentsButton();
				Model390HF.MOD_SERVICE.saveComments( getCallback().getOptions().getOccam(), mod390HF, new AsyncCallback<Mod390HF>() {
					@Override
					public void onSuccess(Mod390HF result) {
						toast.hide();
					}

					@Override
					public void onFailure(Throwable caught) {
						toast.hide();
						getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
					}
				});
			});
			comment.setText(mod390HF.getComments());
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
					if (getCallback().getOptions().isBackButtonVisible() && getCallback().getOptions().hasExternalCallback()) {
						getCallback().getOptions().getExternalCallback().onExit(mod390HF);
					} else {
						getCallback().onCancel(mod390HF);
					}
				}
				@Override
				public void onCancel() {
					cancelButton.setEnabled(true);
				}
			});
		} else {
			if (getCallback().getOptions().isBackButtonVisible() && getCallback().getOptions().hasExternalCallback()) {
				getCallback().getOptions().getExternalCallback().onExit(mod390HF);
			} else {
				getCallback().onCancel(mod390HF);
			}
		}
	}

	private void print() {
		if (isDirty()) {
			new AonConfirmDialog().confirm(AON.MSG.draftPrint(),AON.MSG.draftPrintNote() 
					, new AonConfirmDialogCallback() {
				
				@Override
				public void onAccept() {
					submitForm(MODEL390HF_PRINT);
				}
				
				@Override
				public void onCancel() {
					// Nothing
				}
			});
		} else {
			submitForm(MODEL390HF_PRINT);
		}
	}

	protected void select( Mod390HF mod390HF) {
		setModel(mod390HF);
		refreshToolbarState();
		styleStatusLabel(mod390HF);
	}
	
	protected void selectAndPopulate( Mod390HF mod390HF) {
		select(mod390HF);
		populate(mod390HF);
		decorateDeclarationTab();
		decorateAdministrationTab();
	}
	
	private void refreshToolbarState() {
		toolbarPanel.setTitle(AonStringUtils.join(mod390HF.getDocument(),AonStringUtils.SPACE,mod390HF.getFullName()));
		resetButton.setVisible(!mod390HF.isNew() && !mod390HF.isFinished() && !mod390HF.isSent());
		auditButton.setVisible(!mod390HF.isNew());
		newButton.setVisible(!mod390HF.isNew() && !getCallback().getOptions().isBackButtonVisible() && !getCallback().getOptions().hasExternalCallback());
		cancelButton.setVisible(true);
		saveButton.setVisible(!mod390HF.isFinished() && !mod390HF.isSent());
		deleteButton.setVisible(!mod390HF.isNew() && !mod390HF.isFinished() && !mod390HF.isSent());
		printButton.setVisible(!mod390HF.isNew());
		markAsPendingButton.setVisible(!mod390HF.isNew() &&
				(mod390HF.getStatus() == FiscalStatus.FINISHED 
				|| mod390HF.getStatus() == FiscalStatus.BATCHED
				|| mod390HF.getStatus() == FiscalStatus.SENT
				|| mod390HF.getStatus() == FiscalStatus.CUSTOMER_CHECK
				|| mod390HF.getStatus() == FiscalStatus.BLOCKED));
		markAsFinishedButton.setVisible(!mod390HF.isNew() &&
				(mod390HF.getStatus() == FiscalStatus.PENDING
				|| mod390HF.getStatus() == FiscalStatus.CUSTOMER_CHECK
				|| mod390HF.getStatus() == FiscalStatus.MISSING));
		markAsSentButton.setVisible(!mod390HF.isNew() &&
				(mod390HF.getStatus() == FiscalStatus.FINISHED));
	}

	protected EnumMap<Mod390Key, AonDoubleBox> getFieldsMap() {
		return fieldsMap;
	}
	
	void paintIdentificationTab(TabLayoutPanel tabPanel) {
//		Model390HFIdentificationData identificationData = new Model390HFIdentificationData( new Model390HFIdentificationDataCallback()) ;
//		tabPanel.add(identificationData, AON.MSG.identification() );
		identificationData = new AonFiscalModelIdentificationPanel<>( getModel() ) ;
		identificationData.addValueChangeHandler(event -> {
			toolbarPanel.setTitle(AonStringUtils.join(getModel().getDocument(),AonStringUtils.SPACE,getModel().getFullName()));
			markAsDirty();			
		});
		tabPanel.add(identificationData, AON.MSG.identification());
	}
	
	protected void paintAdditionalData(FlexTable table) {
		
	}
	protected void paintDeclaration(FlexTable table, IModelScript<Mod390Key>[] script, int colsNumber) {
		if (table.getRowCount() > 0) {
			table.removeAllRows();
		}
		paintScript(table, script, colsNumber);
	}
	
	protected void paintScript(FlexTable table, IModelScript<Mod390Key>[] script, int colsNumber) {
		for (IModelScript<Mod390Key> ms : script) {
			paintRow(table,ms,colsNumber);	
		}
	}

	protected void paintRow(FlexTable table,IModelScript<Mod390Key> script, int colsNumber) {
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
				for (Mod390Key key : script.getKeys()) {
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
	
	protected int paintBox(FlexTable table, int row, int col, Mod390Key key) {
		if (AonStringUtils.isNumeric(key.getBoxCode())) {
			table.setWidget(row, col, new AonBoxLabel(key.getBoxCode()));
		} else {
			table.setWidget(row, col, new Label());
		}
		return ++col;
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
	
	protected void paintParticularyRow(FlexTable table, IModelScript<Mod390Key> script) {
		
	}
	protected void paintLabel(FlexTable table,int row,IModelScript<Mod390Key> script) {
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
	
	
	protected int paintField(FlexTable table, int row, int col, IModelScript<Mod390Key> script, final Mod390Key key) {
		return paintField(table, row, col, key, script.getFieldSize(key), script.isEnabled( key ));
	}
	protected int paintField(FlexTable table, int row, int col, final Mod390Key key, int fieldSize, boolean enabled) {
		final FiscalModelDetail det1 = getModel().ensureDetail(key);
		final AonDoubleBox input = new AonDoubleBox(fieldSize);
		fieldsMap.put(key, input);
		input.setEnabled(enabled); 
		input.addStyleName(AON.CSS.aonPaddingLeft());
		input.setValue(det1.getAmount());
		input.addValueChangeHandler(event -> {
			if (input.getValue() == null) input.setValue(0.0,false);
			getModel().ensureDetail(key).setAmount(input.getValue());
			if (input.isEnabled()) {
				calculateAndRefresh();
			}
			markAsDirty();
		});
		table.setWidget(row, col, input);
		return ++col;
	}
	
	protected void paintTextBox(FlexTable table, final Mod390Key key, int fieldSize, boolean enabled) {
		int row = table.getRowCount();
		paintLabel(table, row, key.getDescription());
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		
		final AonTextBox textBox = new AonTextBox();
		textBox.setVisibleLength(fieldSize+1);
		textBox.setMaxLength(fieldSize);
		textBox.setEnabled(enabled);
		if (AonStringUtils.isNotEmpty( getModel().ensureDetail(key).getDescription() ) ) {
			textBox.setValue( getModel().ensureDetail(key).getDescription() , false );
		}
		textBox.addValueChangeHandler( event -> {
			getModel().ensureDetail(key).setDescription(textBox.getValue());
			markAsDirty();
		});
		table.setWidget(row, 1, textBox);
	}

	protected void paintWithoutActivityCheck(FlexTable table) {
		int row = table.getRowCount();
		paintLabel(table, row, AON.MSG.withoutActivity());
		final CheckBox check = new CheckBox();
		check.setValue(getModel().isWithoutActivity());
		check.addClickHandler(event -> {
			getModel().setWithoutActivity(check.getValue());
			markAsDirty();
		});
		table.setWidget(row, 1, check);
	}

	protected CheckBox paintCheck(Mod390Key key, FlexTable table) {
		int row = table.getRowCount();
		paintLabel(table, row, key.getDescription());
		final CheckBox check = new CheckBox();
		check.setValue(getModel().ensureDetail(key).getAmount() == 1);
		check.addClickHandler(event -> {
			getModel().ensureDetail(key).setAmount(check.getValue().booleanValue()?1.0:0.0);
			markAsDirty();
		});
		table.setWidget(row, 1, check);
		return check;
	}
	
	protected void paintDate(Mod390Key key, FlexTable table) {
		int row = table.getRowCount();
		paintLabel(table, row, key.getDescription());
		
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		final AonDateBox dateBox = new AonDateBox();
		if (AonStringUtils.isNotEmpty( getModel().ensureDetail(key).getDescription() ) ) {
			dateBox.setValue( dateBox.parse(getModel().ensureDetail(key).getDescription() , false) );
		}
		dateBox.addValueChangeHandler( event -> {
			getModel().ensureDetail(key).setDescription(dateBox.format());
			markAsDirty();
		});
		table.setWidget(row, 1, dateBox);
	}

	protected void paintListBox(ListBox listBox, Mod390Key key, FlexTable table) {
		int row = table.getRowCount();
		paintLabel(table, row, key.getDescription());
		
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		listBox.setSelectedIndex( (int) getModel().ensureDetail(key).getAmount() );
		listBox.addChangeHandler( event -> {
			getModel().ensureDetail(key).setAmount(listBox.getSelectedIndex());
			markAsDirty();
		});
		table.setWidget(row, 1, listBox);
	}
	
	private void submitForm(String action) {
		diskForm.setMethod(FormPanel.METHOD_POST);
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		diskForm.clear();
		FlowPanel diskPanel = new FlowPanel();
		diskPanel.add(mod390HFHidden);
		diskPanel.add(domainIdHidden);
		diskPanel.add(domainNameHidden);
		diskPanel.add(userHidden);
		diskForm.add(diskPanel);
		mod390HFHidden.setValue(String.valueOf(getModel().getId()));
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
		dirtyLabel.getElement().getStyle().setWidth(10, Unit.PX);
		dirtyLabel.getElement().getStyle().setHeight(10, Unit.PX);
		marksPanels.add(dirtyLabel);
		
		diffLabel.setStyleName(AON.CSS.aonMarginLeft());
		diffLabel.addStyleName(AON.CSS.aonLabelWithIcon());
		diffLabel.addStyleName(AON.CSS.aonIconDiff());
		diffLabel.setTitle("C\u00E1lculo por diferencia habilitado");
		marksPanels.add(diffLabel);

		adjLabel.setStyleName(AON.CSS.aonMarginLeft());
		adjLabel.addStyleName(AON.CSS.aonIconLabel());
		adjLabel.addStyleName(AON.CSS.aonIconWrench());
		adjLabel.addStyleName(AON.CSS.aonColorBlue());
		adjLabel.setTitle("Ajustes realizados");
		marksPanels.add(adjLabel);

		if (mod390HF.isReplacement()) {
			replacedLabel.setText(AON.MSG.replacement());
			replacedLabel.setStyleName(AON.CSS.aonMarginLeft());
			replacedLabel.addStyleName(AON.CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.CSS.aonLabelWithIcon());
		}
		if (mod390HF.isComplementary()) {
			replacedLabel.setText( AON.MSG.complementary());
			replacedLabel.setStyleName(AON.CSS.aonMarginLeft());
			replacedLabel.addStyleName(AON.CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.CSS.aonLabelWithIcon());
		}
		marksPanels.add(replacedLabel);

		if ( mod390HF.hasProrate()) {
			prorataLabel.setStyleName(AON.CSS.aonMarginLeft());
			prorataLabel.setText(AON.MSG.prorrata() + ": " + mod390HF.getProratePercent() + "%");
			prorataLabel.addStyleName(AON.CSS.aonBold());
		}
		marksPanels.add(replacedLabel);
		marksPanels.add(prorataLabel);
		
		styleDirtyLabel();
		styleStatusLabel(this.mod390HF);
		
		decToolbar.getMessagePanel().add(marksPanels);
		
		decToolbar.setTitle(statusLabel);
		return decToolbar;
	}
	
	protected void styleDirtyLabel() {
		dirtyLabel.setVisible(isDirty());
		diffLabel.setVisible(!this.mod390HF.isDiffCalculationDisabled()); 		
		
		boolean adjusted = false;
		for (FiscalModelDetail det : this.mod390HF.getMap().values()) {
			if (AonMathUtils.isNotZero( det.getAdjustAmount())) {
				adjusted = true;
				break;
			}
		}
		adjLabel.setVisible(adjusted);
	}
	
	protected void styleStatusLabel(Mod390HF mod) {
		statusLabel.setText(mod.getStatus().getName());
		statusLabel.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB( mod.getStatus() ));
		statusLabel.getElement().getStyle().setColor(FiscalModelUtils.getStatusFrgColorRGB( mod.getStatus() ));
		statusLabel.setStyleName(AON.CSS.aonToolbarTitle());
		statusLabel.addStyleName(AON.CSS.aonPaddingLeft());
		statusLabel.addStyleName(AON.CSS.aonPaddingRight());
		statusLabel.addStyleName(AON.CSS.aonTextCenter());
		statusLabel.addStyleName(AON.CSS.aonBorder());
		statusLabel.addStyleName(AON.CSS.aonNowrap());
	}
	
	private void styleCommentsButton() {
		if (AonStringUtils.isEmpty(mod390HF.getComments())) {
			commentsButton.addStyleName(AON.CSS.aonIconNoComments());
			commentsButton.removeStyleName(AON.CSS.aonIconComments());
		} else {
			commentsButton.addStyleName(AON.CSS.aonIconComments());
			commentsButton.removeStyleName(AON.CSS.aonIconNoComments());
		}
		commentsButton.setTitle(mod390HF.getComments());
	}
	
	protected void showPaymentInfo(Mod390HF mod) {
		if (paymentContainer != null) {
			this.remove(paymentContainer);
			this.forceLayout();
		}
		if (mod.isFinished() || mod.isSent()) {
			StringBuilder buff = new StringBuilder(AON.MSG.result());
			buff.append(AonStringUtils.SPACE);
			buff.append(AON.FMT.format(mod.getDeclarationResult()));
			if (mod.getDeclarationResultType() != null) {
				buff.append(AonStringUtils.SPACE);
				buff.append(mod.getDeclarationResultType().getDescription());
			}
			if (mod.getFinance() != null && mod.getFinance().getBankAccount() != null && AonStringUtils.isNotBlank(mod.getFinance().getBankAccount().getIban())) {
				buff.append(AonStringUtils.SPACE);
				buff.append(AonStringUtils.defaultString(mod.getFinance().getBankAccount().getIban()));
				buff.append(AonStringUtils.SPACE);
				buff.append(AonStringUtils.defaultString(mod.getFinance().getBankAlias()));
			}
			paymentContainer = new FlowPanel();
			paymentContainer.setStyleName(AON.CSS.aonWidthAll());
			Label label = new Label( buff.toString() );
			label.setStyleName(AON.CSS.aonWidthAll());
			label.addStyleName(AON.CSS.aonTextCenter());
			label.addStyleName(AON.CSS.aonBold());
			paymentContainer.add(label);
			this.insert( paymentContainer, Direction.NORTH, 30, decToolbar);
			this.forceLayout();
		}
	}

	protected void decorateDeclarationTab() {
		// Redefine if needed
	}

	protected void decorateAdministrationTab() {
		if (admonPanel != null) {
			admonPanel.manageLinks();
		}
	}

	protected void save() {
		save(null);
	}
	protected void save(AsyncCallback<Mod390HF> cbk) {
		saveButton.setEnabled(false);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model390HF.MOD_SERVICE.save(getCallback().getOptions().getOccam(), this.mod390HF, new AsyncCallback<Mod390HF>() {
					@Override
					public void onSuccess(Mod390HF result) {
						setDirty( false );
						selectAndPopulate(result);
						popup.hide();
						saveButton.setEnabled(true);
						if (cbk != null) cbk.onSuccess(result);
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
				Model390HF.MOD_SERVICE.delete(getCallback().getOptions().getOccam(),mod390HF, new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						deleteButton.setEnabled(true);
						getCallback().onRemove(mod390HF);
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
		Model390HF.MOD_SERVICE.initializeForFinish(getCallback().getOptions().getOccam(),mod390HF,
				new AsyncCallback<Mod390HF>() {
					@Override
					public void onSuccess(Mod390HF result) {
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
		Model390HFFinishDeclarationPopup finalizeDialog = new Model390HFFinishDeclarationPopup(this.mod390HF, getCallback(), new FinishDeclarationPopupCallback() {
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
		Model390HF.MOD_SERVICE.markAsCustomerCheck(getCallback().getOptions().getOccam(), mod390HF, new AsyncCallback<Mod390HF>() {
					@Override
					public void onSuccess(Mod390HF result) {
						setDirty(false);
						selectAndPopulate(result);
						popup.hide();
						markAsFinishedButton.setEnabled(true);
						showPaymentInfo(mod390HF);
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
		Model390HF.MOD_SERVICE.markAsFinished(getCallback().getOptions().getOccam(), mod390HF, new AsyncCallback<Mod390HF>() {
					@Override
					public void onSuccess(Mod390HF result) {
						setDirty(false);
						selectAndPopulate(result);
						popup.hide();
						markAsFinishedButton.setEnabled(true);
						showPaymentInfo(mod390HF);
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
		if (mod390HF.isSent() && AonStringUtils.isNotBlank(mod390HF.getNumber())) {
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
		Model390HF.MOD_SERVICE.markAsPending(getCallback().getOptions().getOccam(), mod390HF, new AsyncCallback<Mod390HF>() {
					@Override
					public void onSuccess(Mod390HF result) {
						setDirty(false);
						selectAndPopulate(result);
						popup.hide();
						markAsPendingButton.setEnabled(true);
						showPaymentInfo(mod390HF);
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
		Model390HF.MOD_SERVICE.markAsSent(getCallback().getOptions().getOccam(), mod390HF, new AsyncCallback<Mod390HF>() {
					@Override
					public void onSuccess(Mod390HF result) {
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

	private void audit() {
		AonAuditDialog dialog = new AonAuditDialog();
		dialog.show(mod390HF);
	}

	private void paintInfoCol(FlexTable table, int row, int col, final IModelScript<Mod390Key> script) {
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
					
					Model390HF.MOD_SERVICE.getInfo(callback.getOptions().getOccam(),getModel(),script, infoKey, new AsyncCallback<String>() {
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
							JsVatComputeKeyInfo info = JsonUtils.safeEval(result);
							JsVatComputeKeyInfoGridPanel grid = new JsVatComputeKeyInfoGridPanel();
							grid.setTitle(AON.MSG.calcDetail());
							grid.setSubTitle(AonStringUtils.join(
								Arrays.stream(script.getKeys())
									.filter( Objects::nonNull )
									.map( Mod390Key::getBoxFormatted )
									.reduce("", String::concat)
								, " " 
								, script.getLabel()));
							grid.addContent(info);
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
					Model390HF.MOD_SERVICE.getInfo(callback.getOptions().getOccam(),getModel(),script, infoKey, new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							popup.hide();
							callback.showError(AON.MSG.errorMessage());
							button.setEnabled(true);
						}
	
						@Override
						public void onSuccess(String result) {
							popup.hide();
							JsVatContextBreakdownGridPanel grid = new JsVatContextBreakdownGridPanel( prorrated && getModel().hasProrate() );
							grid.addSelectionHandler(event -> showInvoice(event.getSelectedItem()));
							grid.setTitle(AON.MSG.modelRelatedInvoices(getModel().getModelFullName()));
							grid.setSubTitle(AonStringUtils.join(
								Arrays.stream(script.getKeys())
									.filter( Objects::nonNull )
									.map( Mod390Key::getBoxFormatted )
									.reduce("", String::concat)
								, " " 
								, script.getLabel()));
							JavaScriptObject arrayObject = JsonUtils.safeEval(result);
							JsArray<JsVatContext> array = arrayObject.cast();
							for (int i = 0; i < array.length(); i++) {
								grid.addRow(array.get(i));
							}
							grid.addFooterRow();
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
					Model390HF.MOD_SERVICE.getInfo(callback.getOptions().getOccam(),getModel(),script, infoKey, new AsyncCallback<String>() {
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
								Mod390Key key = Mod390Key.valueOf(computeInfo.getKey());
								JsVatComputeInfoGridPanel grid = new JsVatComputeInfoGridPanel() {

									@Override
									protected String resolveKey(String keyString) {
										Mod390Key key = Mod390Key.valueOf(keyString);
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
					}
					return null;
				}
				
				@Override 
				public Void visitProrratedModelInvoiceVatBreakdown() {
					if (!getModel().isManualDeclaration()) {
						final AonTableButton button = addButton();
						button.addClickHandler(event -> showInvoiceVatBreakdownInfo(button,true));
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

	protected void populate(Mod390HF mod390HF) {
		for (Entry<Mod390Key, AonDoubleBox> entry : fieldsMap.entrySet()) {
			double d1 = mod390HF.getAmount(entry.getKey());
			double d2 = entry.getValue().getValue();
			if (!AonNumberUtils.equals(d1, d2)) {
				entry.getValue().setValue(d1,false,true);
			}
		}
	}
	
	public void calculateAndRefresh(AsyncCallback<Mod390HF> cbk) {
		Model390HF.MOD_SERVICE.calculate(getCallback().getOptions().getOccam(), this.mod390HF,
				new AsyncCallback<Mod390HF>() {

					@Override
					public void onFailure(Throwable caught) {
						getCallback().showError(AON.MSG.errorMessage());
					}

					@Override
					public void onSuccess(Mod390HF result) {
						populate(result);
						if (cbk != null) cbk.onSuccess(result);
					}
			
				}
			);	
	}

	private void onReset() {
		resetButton.setEnabled(false);
		AonConfirmDialog cd = new AonConfirmDialog();
		cd.confirm(AON.MSG.confirmDeclarationinitializationAction(), new AonConfirmDialogCallback() {

			@Override
			public void onAccept() {
				Model390HF.MOD_SERVICE.reset(getCallback().getOptions().getOccam(),getModel(),
						new AsyncCallback<Mod390HF>() {
							@Override
							public void onSuccess(Mod390HF m390HF) {
								setDirty( true );
								selectAndPopulate(m390HF);
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

	public void calculateAndRefresh() {
		calculateAndRefresh(null);
	}

	private void showInvoice(JsVatContext vt) {
		int invoiceId = vt.getInvoice();
		Model390HF.MOD_SERVICE.getInvoice(getCallback().getOptions().getOccam(), invoiceId,new AsyncCallback<Invoice>() {
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

}
