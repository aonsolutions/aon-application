package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.EnumMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAuditDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToast;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303FinishDeclarationPopup.FinishDeclarationPopupCallback;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.IModel303Callback;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303IdentificationData.IModel303IdentificationDataCallback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public abstract class Model303Base extends DockLayoutPanel  {
	
	protected class Model303BaseCallback implements IModel303Callback{
		
		private IModel303Callback callback;
		
		public Mod303 getMod303() {
			return Model303Base.this.mod303;
		}
		
		public Model303BaseCallback(IModel303Callback callback) {
			this.callback = callback;
		} 
		@Override
		public void onAccept(Mod303 mod303) {
			this.callback.onAccept(mod303);
		}
		@Override
		public void onCancel() {
			this.callback.onCancel();
		}
		@Override
		public void onNew() {
			this.callback.onNew();
		}
		@Override
		public void onReset(Mod303 mod303) {
			this.callback.onReset(mod303);
		}
		@Override
		public void showBreakdownPanel(String htmlText) {
			this.callback.showBreakdownPanel(htmlText);
		}
		@Override
		public void showVisorAEAT() {
			this.callback.showVisorAEAT();
		}
		@Override
		public void cleanBreakdownPanel() {
			this.callback.cleanBreakdownPanel();
		}
		@Override
		public void cleanErrorPanel() {
			this.callback.cleanErrorPanel();
		}
		@Override
		public void showError(String msg) {
			this.callback.showError(msg);
		}
		@Override
		public Model303ModuleOptions getOptions() {
			return this.callback.getOptions();
		}
		@Override
		public String getDomainName() {
			return this.callback.getDomainName();
		}
		@Override
		public String getUser() {
			return this.callback.getUser();
		}

		@Override
		public int getDomain() {
			return this.callback.getDomain();
		}
	}

	protected static final String DOWNLOAD_FILE_ACTION = "/aon_gwt_fiscal/ms/Model303File";
	private static final String MODEL303_PRINT = "/aon_gwt_fiscal/ms/Model303Print";

	protected static final boolean ENABLED = true;
	protected static final boolean DISABLED = false;
	protected static final boolean HAS_INFO = true;
	protected static final boolean HAS_NOT_INFO = false;
	
	private static final int MAX_LABEL_LENGTH = 100;
	
	private Mod303 mod303;
	private Model303BaseCallback callback;
	private API api;
	private boolean test = false;
	private EnumMap<Mod303Key,AonDoubleBox> fieldsMap;
	private boolean dirty;
	
	protected InlineLabel documentLabel = new InlineLabel();
	protected InlineLabel nameLabel = new InlineLabel();
	protected InlineLabel surnameLabel = new InlineLabel();
	protected FlowPanel paymentInfo = new FlowPanel();
	protected FlowPanel  dirtyPanel = new FlowPanel ();
	protected Label statusLabel = new Label();
	protected InlineLabel replacedLabel = new InlineLabel();
	protected InlineLabel prorataLabel = new InlineLabel();
	protected AonTableButton commentsButton = new AonTableButton(AON.MSG.comments(), AON.CSS.aonIconNoComments());

	protected final AonToolbarButton newButton = new AonToolbarButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
	protected final AonToolbarButton saveButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave());
	protected final AonToolbarButton cancelButton = new AonToolbarButton(AON.MSG.cancelAction(),AON.CSS.aonIconCancel());
	protected final AonToolbarButton deleteButton = new AonToolbarButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
	protected final AonToolbarButton resetButton = new AonToolbarButton(AON.MSG.resetAction(),AON.CSS.aonIconRefresh());
	protected final AonToolbarButton printButton = new AonToolbarButton(AON.MSG.draft(),AON.CSS.aonIconExcel());
	protected final AonToolbarButton markAsPendingButton = new AonToolbarButton(AON.MSG.reopen(),AON.CSS.aonIconModelReopen());
	protected final AonToolbarButton markAsFinishedButton = new AonToolbarButton(AON.MSG.finish(),AON.CSS.aonIconModelFinish());
	protected final AonToolbarButton markAsSentButton = new AonToolbarButton(AON.MSG.markAsSent(),AON.CSS.aonIconModelSent());
	protected final AonToolbarButton auditButton = new AonToolbarButton(AON.MSG.audit(),AON.CSS.aonIconAudit());
	protected FormPanel diskForm = new FormPanel("_blank");
	protected FormPanel aeatForm = new FormPanel("aeatForm");

	protected Hidden mod303Hidden = new Hidden("mod303");
	protected Hidden domainIdHidden = new Hidden("domainId");
	protected Hidden domainNameHidden = new Hidden("domainName");
	protected Hidden userHidden = new Hidden("user");
	
	protected Hidden domainIdAeatHidden = new Hidden("domainId");
	protected Hidden domainNameAeatHidden = new Hidden("domainName");
	protected Hidden userAeatHidden = new Hidden("user");
	protected Hidden modAeatHidden = new Hidden("mod");
	protected Hidden certAeatHidden = new Hidden("cert");
	protected Hidden passAeatHidden = new Hidden("pass");
	protected Hidden nameAeatHidden = new Hidden("name");
	protected Hidden documentAeatHidden = new Hidden("document");
	protected Hidden nrcAeatHidden = new Hidden("nrc");
	protected Hidden testHidden = new Hidden("test");

	interface TabLabelTemplate extends SafeHtmlTemplates {
		@Template ("<span class=\"aon_tab_label {1}\">{0}</span>")
		SafeHtml render(String label, String iconStyle);
	}
	protected static final TabLabelTemplate TAB_TEMPLATE = GWT.create(TabLabelTemplate.class);

	protected Model303Base(Mod303 mod303,Model303Callback cbk, Model303ModuleOptions options) {
		super(Unit.PX);
		select( mod303 , options);
		
		addNorth(getToolbarPanel(options), AonToolbar.HEIGTH);
		
		AonFiscalModelHeader modelHeader = new AonFiscalModelHeader(this.mod303);
		addNorth(modelHeader, AonFiscalModelHeader.HEIGTH);
		
		SimplePanel declarationHeaderPanel = new SimplePanel();
		paintDeclarationHeaderTable(declarationHeaderPanel);
		addNorth(declarationHeaderPanel , 45);
		
		this.api = new API(GWT.getModuleBaseURL(), options.getAonData().getMd5(),
				options.getAonData().getDomain().getName(), options.getAonData().getDomain().getId(),
				options.getAonData().getUser().getLogin());
		fieldsMap = new EnumMap<>(Mod303Key.class);
		this.callback = new Model303BaseCallback(cbk);
		
		setStyleName(AON.CSS.aonSelector());
	}
	
	public Model303BaseCallback getCallback() {
		return callback;
	}
	protected Mod303 getMod303() {
		return mod303;
	}
	public void setMod303(Mod303 mod303) {
		this.mod303 = mod303;
	}
	public API getAPI() {
		return api;
	}
	
	public boolean getTest() {
		return test;
	}

	public void setTest(Boolean test) {
		this.test = test;
	}

	private AonToolbar getToolbarPanel(Model303ModuleOptions options) {
		AonToolbar toolbarPanel = new AonToolbar("IVA. Autoliquidaci\u00F3n."); 
					
		newButton.addClickHandler( event ->  callback.onNew() );
		toolbarPanel.add(newButton);
		
		saveButton.addClickHandler(event ->  save(options));
		toolbarPanel.add(saveButton);
		
		if (options.isBackButtonVisible() && options.hasExternalCallback()) {
			cancelButton.setText(AON.MSG.backAction());
			cancelButton.setTitle(AON.MSG.backAction());
		}
		cancelButton.addClickHandler(event ->  cancel(options) );
		toolbarPanel.add(cancelButton);

		deleteButton.addClickHandler(event -> delete());
		toolbarPanel.add(deleteButton);
		
		resetButton.addClickHandler( event -> callback.onReset(getMod303()));
		toolbarPanel.add(resetButton);		
		
		markAsFinishedButton.addClickHandler( event -> onFinalize(options));
		toolbarPanel.add(markAsFinishedButton);

		markAsSentButton.addClickHandler( event -> markAsSent(options));
		toolbarPanel.add(markAsSentButton);

		markAsPendingButton.addClickHandler( event -> reopenDeclaration( options ));
		toolbarPanel.add(markAsPendingButton);
		
		printButton.addClickHandler( event ->  print());
		toolbarPanel.add(printButton);
		
		auditButton.addClickHandler( event -> audit());
		toolbarPanel.add(auditButton);

		return toolbarPanel;
	}
	
	private void cancel(Model303ModuleOptions options) {
		cancelButton.setEnabled(false);
		if (isDirty()) {
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.confirmDeclarationCancelAction(), new AonConfirmDialogCallback() {

				@Override
				public void onAccept() {
					if (options.isBackButtonVisible() && options.hasExternalCallback()) {
						options.getExternalCallback().onExit(mod303);
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
				options.getExternalCallback().onExit(mod303);
			} else {
				callback.onCancel();
			}
		}
	}

	private void print() {
		if (isDirty()) {
			new AonConfirmDialog().confirm(AON.MSG.draftPrint(),AON.MSG.draftPrintNote() 
					, new AonConfirmDialogCallback() {
				
				@Override
				public void onAccept() {
					submitForm(MODEL303_PRINT);
				}
				
				@Override
				public void onCancel() {
					// Nothing
				}
			});
		} else {
			submitForm(MODEL303_PRINT);
		}
	}

	protected void select( Mod303 mod303, Model303ModuleOptions options) {
		setMod303(mod303);
		refreshToolbarState( options );
		styleStatusLabel(mod303);
	}
	protected void selectAndPopulate( Mod303 mod303,Model303ModuleOptions options) {
		select(mod303,options);
		populate(mod303);
	}
	
	private void refreshToolbarState(Model303ModuleOptions options) {
		deleteButton.setVisible(!mod303.isNew());
		resetButton.setVisible(!mod303.isNew());
		auditButton.setVisible(!mod303.isNew());
		newButton.setVisible(!mod303.isNew() && !options.isBackButtonVisible() && !options.hasExternalCallback());
		cancelButton.setVisible(true);
		saveButton.setVisible(!mod303.isFinished() && !mod303.isSent());
		deleteButton.setVisible(!mod303.isFinished() && !mod303.isSent());
		resetButton.setVisible(!mod303.isFinished() && !mod303.isSent());
		markAsPendingButton.setVisible(!mod303.isNew() &&
				(mod303.getStatus() == FiscalStatus.FINISHED 
				|| mod303.getStatus() == FiscalStatus.BATCHED
				|| mod303.getStatus() == FiscalStatus.SENT
				|| mod303.getStatus() == FiscalStatus.CUSTOMER_CHECK
				|| mod303.getStatus() == FiscalStatus.BLOCKED));
		markAsFinishedButton.setVisible(!mod303.isNew() &&
				(mod303.getStatus() == FiscalStatus.PENDING
				|| mod303.getStatus() == FiscalStatus.CUSTOMER_CHECK
				|| mod303.getStatus() == FiscalStatus.MISSING));
		markAsSentButton.setVisible(!mod303.isNew() &&
				(mod303.getStatus() == FiscalStatus.FINISHED));
	}

	protected EnumMap<Mod303Key, AonDoubleBox> getFieldsMap() {
		return fieldsMap;
	}
	protected void paintAdditionalData(FlexTable table) {
		
	}
	protected void paintDeclaration(FlexTable table, IModelScript<Mod303Key>[] script, int colsNumber) {
		if (table.getRowCount() > 0) {
			table.removeAllRows();
		}
		paintScript(table, script, colsNumber);
	}
	
	protected void paintScript(FlexTable table, IModelScript<Mod303Key>[] script, int colsNumber) {
		for (IModelScript<Mod303Key> ms : script) {
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

	protected void paintRow(FlexTable table,IModelScript<Mod303Key> script, int colsNumber) {
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
				for (Mod303Key key : script.getKeys()) {
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
	
	protected void paintParticularyRow(FlexTable table, IModelScript<Mod303Key> script) {
		
	}
	protected void paintLabel(FlexTable table,int row,IModelScript<Mod303Key> script) {
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
	
	private int paintBox(FlexTable table, int row, int col, Mod303Key key) {
		if (AonStringUtils.isNumeric(key.getBoxCode())) {
			table.setWidget(row, col, new AonBoxLabel(key.getBoxCode()));
		} else {
			table.setWidget(row, col, new Label());
		}
		return ++col;
	}
	
	protected int paintField(FlexTable table, int row, int col, IModelScript<Mod303Key> script, final Mod303Key key) {
		return paintField(table, row, col, key, script.getFieldSize(key), script.isEnabled( key ));
	}
	protected int paintField(FlexTable table, int row, int col, final Mod303Key key, int fieldSize, boolean enabled) {
		final FiscalModelDetail det1 = this.mod303.ensureDetail(key);
		final AonDoubleBox input = new AonDoubleBox(fieldSize);
		fieldsMap.put(key, input);
		input.setEnabled(enabled); 
		input.setValue(det1.getAmount());
		if (key.isDiffEnabled() && AonMathUtils.isNotZero(det1.getAdjustAmount())) {
			input.addStyleName(AON.CSS.aonChanged());
			input.setTitle("Valor calculado ..: " + det1.getResultAmount() 
				+ ". Se ha realizado un ajuste por valor de " + AonMathUtils.round( det1.getAdjustAmount() * -1));
		}
		input.addValueChangeHandler(event -> {
			if (input.getValue() == null) input.setValue(0.0,false);
			if (key.isDiffEnabled()) {
				double result = mod303.getResultAmount(key);
				double adjust = mod303.getAdjustAmount(key);
				double amount = input.getValue();
				if (AonMathUtils.isNotZero(result - adjust - amount)) {
					mod303.ensureDetail(key).setAdjustAmount( result - amount);	
				}
			}
			mod303.ensureDetail(key).setAmount(input.getValue());

			if (key.isDiffEnabled()) {
				if (AonMathUtils.isNotZero(mod303.ensureDetail(key).getAdjustAmount())) {
					input.addStyleName(AON.CSS.aonChanged());
					input.setTitle("Valor calculado ..: " + mod303.ensureDetail(key).getResultAmount() 
						+ ". Se ha realizado un ajuste por valor de " + AonMathUtils.round( mod303.ensureDetail(key).getAdjustAmount() * -1));
				} else {
					input.removeStyleName(AON.CSS.aonChanged());
				}
			}
			
			if (input.isEnabled()) {
				calculateAndRefresh();
			}
			markAsDirty();
		});
		table.setWidget(row, col, input);
		return ++col;
	}
	
	protected void paintWithoutActivityCheck(FlexTable table) {
		int row = table.getRowCount();
		paintLabel(table, row, AON.MSG.withoutActivity());
		final CheckBox check = new CheckBox();
		check.setValue(this.mod303.isWithoutActivity());
		check.addClickHandler( event -> {
				mod303.setWithoutActivity(check.getValue());
				markAsDirty();
		});
		table.setWidget(row, 1, check);
	}

	protected CheckBox paintCheck(Mod303Key key, FlexTable table) {
		int row = table.getRowCount();
		paintLabel(table, row, key.getDescription());
		final CheckBox check = new CheckBox();
		check.setValue(this.mod303.ensureDetail(key).getAmount() == 1);
		check.addClickHandler( event -> {
				mod303.ensureDetail(key).setAmount((check.getValue() != null && check.getValue())?1.0:0.0);
				markAsDirty();
		});
		table.setWidget(row, 1, check);
		return check;
	}
	
	protected void paintDate(Mod303Key key, FlexTable table) {
		int row = table.getRowCount();
		paintLabel(table, row, key.getDescription());
		
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		final AonDateBox dateBox = new AonDateBox();
		if (AonStringUtils.isNotEmpty( mod303.ensureDetail(key).getDescription() ) ) {
			dateBox.setValue( dateBox.parse(mod303.ensureDetail(key).getDescription() , false) );
		}
		dateBox.addValueChangeHandler( event -> {
			mod303.ensureDetail(key).setDescription(dateBox.format());
			markAsDirty();
		});
		table.setWidget(row, 1, dateBox);
	}

	protected void paintTextBox(FlexTable table, final Mod303Key key, int fieldSize, boolean enabled) {
		int row = table.getRowCount();
		paintLabel(table, row, key.getDescription());
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		
		final AonTextBox textBox = new AonTextBox();
		textBox.setVisibleLength(fieldSize+1);
		textBox.setMaxLength(fieldSize);
		textBox.setEnabled(enabled);
		if (AonStringUtils.isNotEmpty( mod303.ensureDetail(key).getDescription() ) ) {
			textBox.setValue( mod303.ensureDetail(key).getDescription() , false );
		}
		textBox.addValueChangeHandler( event ->  {
			mod303.ensureDetail(key).setDescription(textBox.getValue());
			markAsDirty();
		});
		table.setWidget(row, 1, textBox);
	}

	protected void paintListBox(ListBox listBox, Mod303Key key, FlexTable table) {
		int row = table.getRowCount();
		paintLabel(table, row, key.getDescription());
		
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		listBox.setSelectedIndex( (int) mod303.ensureDetail(key).getAmount() );
		listBox.addChangeHandler( event -> {
			mod303.ensureDetail(key).setAmount(listBox.getSelectedIndex());
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

	private void paintInfoCol(FlexTable table, int row, int col, final IModelScript<Mod303Key> script) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.CSS.aonNowrap());
		for (final FiscalModelKeyInfo infoKey : script.getInfoKeys()) {
			if 	(infoKey == FiscalModelKeyInfo.NONE || (infoKey == FiscalModelKeyInfo.DIFF_INVOICE && this.mod303.isDiffCalculationDisabled())) {
				// Nothing
			} else {
				final AonTableButton button = new AonTableButton("");
				button.setTitle(infoKey.getLabel());
				if 	(infoKey == FiscalModelKeyInfo.INVOICE || infoKey == FiscalModelKeyInfo.OUT_ACCRUAL_INVOICE || infoKey == FiscalModelKeyInfo.IN_ACCRUAL_INVOICE) {
					button.addStyleName(AON.CSS.aonIconList());
				} else if (infoKey == FiscalModelKeyInfo.DIFF_INVOICE || infoKey == FiscalModelKeyInfo.DIFF_IN_ACCRUAL_INVOICE || infoKey == FiscalModelKeyInfo.DIFF_OUT_ACCRUAL_INVOICE) {
					button.addStyleName(AON.CSS.aonIconFinanceSettle());
				} else if 	(infoKey == FiscalModelKeyInfo.COMPUTE) {
					button.addStyleName(AON.CSS.aonIconCalc());
				} else if 	(infoKey == FiscalModelKeyInfo.COMPUTE_KEY) {
					button.addStyleName(AON.CSS.aonIconData());
				}
				button.setTabIndex(-2); // NO FOCUS
				button.addClickHandler( event  -> 
					Model303.service.getInfo(callback.getDomainName(), callback.getUser(), callback.getDomain(),
							mod303,script, infoKey,new AsyncCallback<String>() {
						
						@Override
						public void onFailure(Throwable caught) {
							callback.showError(AON.MSG.errorMessage());
						}
						
						@Override
						public void onSuccess(String result) {
							callback.showBreakdownPanel(result);
						}
					}
				));
				buttonContainer.add(button);
			}
		}
		table.setWidget(row, col, buttonContainer);
	}
	
	protected void populate(Mod303 mod303) {
		for (Mod303Key key : fieldsMap.keySet()) {
			double d1 = mod303.getAmount(key);
			double d2 = fieldsMap.get(key).getValue();
			if (!AonNumberUtils.equals(d1, d2)) {
				fieldsMap.get(key).setValue(d1,true,true);
			}
		}
	}
	
	public void calculateAndRefresh(AsyncCallback<Mod303> cbk) {
		Model303.service.calculate(callback.getDomainName(), callback.getUser(), this.mod303,
				new AsyncCallback<Mod303>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.showError(AON.MSG.errorMessage());
					}

					@Override
					public void onSuccess(Mod303 result) {
						populate(result);
						if (cbk != null) cbk.onSuccess(result);
					}
			
				}
			);	
	}

	public void calculateAndRefresh() {
		calculateAndRefresh(null);
	}
	
	private void identificationLabelChanged() {
		documentLabel.setText(mod303.getDocument());
		nameLabel.setText(mod303.getName());
		surnameLabel.setText(mod303.getSurname());
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

	protected void styleDirtyLabel() {
		dirtyPanel.clear();
		if ( isDirty()) {
			InlineLabel dirtyLabel = new InlineLabel("[*]");
			dirtyLabel.setStyleName(AON.CSS.aonColorRed());
			dirtyPanel.add(dirtyLabel);
		}
		if ( !this.mod303.isDiffCalculationDisabled()) {
			InlineLabel diffLabel = new InlineLabel("[DIF.]");
			diffLabel.setStyleName(AON.CSS.aonMarginLeft());
			diffLabel.setTitle("C\u00E1lculo por diferencia habilitado");
			dirtyPanel.add(diffLabel);
		}
		
		boolean adjusted = false;
		for (FiscalModelDetail det : this.mod303.getMap().values()) {
			if (AonMathUtils.isNotZero( det.getAdjustAmount())) {
				adjusted = true;
				break;
			}
		}
		if (adjusted) {
			InlineLabel adjLabel = new InlineLabel("[AJUSTES]");
			adjLabel.setStyleName(AON.CSS.aonMarginLeft());
			adjLabel.addStyleName(AON.CSS.aonColorBlue());
			adjLabel.setTitle("Ajustes realizados");
			dirtyPanel.add(adjLabel);
		}
	}
	protected void save(Model303ModuleOptions options) {
		save(null,options);
	}
	protected void save(AsyncCallback<Mod303> cbk,Model303ModuleOptions options) {
		saveButton.setEnabled(false);
		callback.cleanErrorPanel();
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model303.service.save(callback.getDomainName(), callback.getUser(), this.mod303, new AsyncCallback<Mod303>() {
					@Override
					public void onSuccess(Mod303 result) {
						selectAndPopulate(result,options);
						popup.hide();
						saveButton.setEnabled(true);
						if (cbk != null) cbk.onSuccess(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
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
				Model303.service.delete(callback.getDomainName(), callback.getUser(),mod303, new AsyncCallback<Void>() {
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
	
	private void onFinalize( Model303ModuleOptions options) {
		markAsFinishedButton.setEnabled(false);
		callback.cleanErrorPanel();
		Model303.service.initializeForFinish(callback.getDomainName(), callback.getUser(),mod303,
				new AsyncCallback<Mod303>() {
					@Override
					public void onSuccess(Mod303 result) {
						selectAndPopulate(result,options);
						showFinalizePopup( options );
						markAsFinishedButton.setEnabled(true);
					}
	
					@Override
					public void onFailure(Throwable caught) {
						callback.showError(AON.MSG.unableToReadFiscalParameters(caught.getMessage()));
						markAsFinishedButton.setEnabled(true);
					}
				});
	}
	
	private void showFinalizePopup(final Model303ModuleOptions options) {
		Model303FinishDeclarationPopup finalizeDialog = new Model303FinishDeclarationPopup(this.mod303, options, getCallback(), new FinishDeclarationPopupCallback() {
			@Override
			public void onCancel() {
				// nothing
			}
			
			@Override
			public void onAccept() {
				finish(options);
			}
			@Override
			public void onCustomerCheck() {
				markAsCustomerCheck( options );
			}
		});
		finalizeDialog.center();
		finalizeDialog.show();
	}
	
	private void markAsCustomerCheck( Model303ModuleOptions options) {
		markAsFinishedButton.setEnabled(false);
		callback.cleanErrorPanel();
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model303.service.markAsCustomerCheck(callback.getDomainName(), callback.getUser(), mod303, new AsyncCallback<Mod303>() {
					@Override
					public void onSuccess(Mod303 result) {
						selectAndPopulate(result,options);
						popup.hide();
						markAsFinishedButton.setEnabled(true);
						FiscalModelUtils.fillPaymentInfo(paymentInfo, mod303);
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						markAsFinishedButton.setEnabled(true);
					}
				});
	}

	private void finish(Model303ModuleOptions options) {
		markAsFinishedButton.setEnabled(false);
		callback.cleanErrorPanel();
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add(new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model303.service.markAsFinished(callback.getDomainName(), callback.getUser(), mod303, new AsyncCallback<Mod303>() {
					@Override
					public void onSuccess(Mod303 result) {
						selectAndPopulate(result,options);
						popup.hide();
						markAsFinishedButton.setEnabled(true);
						FiscalModelUtils.fillPaymentInfo(paymentInfo, mod303);
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
						markAsFinishedButton.setEnabled(true);
					}
				});
	}
	private void reopenDeclaration(Model303ModuleOptions options) {
		markAsPendingButton.setEnabled(false);
		callback.cleanErrorPanel();
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add(new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model303.service.markAsPending(callback.getDomainName(), callback.getUser(), mod303, new AsyncCallback<Mod303>() {
					@Override
					public void onSuccess(Mod303 result) {
						selectAndPopulate(result,options);
						popup.hide();
						markAsPendingButton.setEnabled(true);
						FiscalModelUtils.fillPaymentInfo(paymentInfo, mod303);
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						callback.showError(AON.MSG.unableToReopenDeclaration(caught.getMessage()));
						markAsPendingButton.setEnabled(true);
					}
				});
	}
	
	private void markAsSent(Model303ModuleOptions options) {
		markAsSentButton.setEnabled(false);
		callback.cleanErrorPanel();
		Model303.service.markAsSent(callback.getDomainName(), callback.getUser(), mod303, new AsyncCallback<Mod303>() {
					@Override
					public void onSuccess(Mod303 result) {
						selectAndPopulate(result,options);
						markAsSentButton.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.showError(AON.MSG.unableToMarkAsSentDeclaration(caught.getMessage()));
						markAsSentButton.setEnabled(true);
					}
				});
	}

	private void audit() {
		AonAuditDialog dialog = new AonAuditDialog();
		dialog.show(mod303);
	}

	protected class Model303IdentificationDataCallback implements IModel303IdentificationDataCallback {

		@Override public boolean isFinished() 		{ return getMod303().isFinished() || getMod303().isSent();}
		
		@Override public String getDocument() 		{return getMod303().getDocument();			}
		@Override public String getName() 			{return getMod303().getName(); 				}
		@Override public String getSurname() 		{return getMod303().getSurname();			}
		@Override public String getPhone() 			{return getMod303().getPhone();				}
		@Override public String getStreetInitial() 	{return getMod303().getStreetInitial();		}
		@Override public String getStreetName() 	{return getMod303().getStreetName();		}
		@Override public String getStreetNumber() 	{return getMod303().getStreetNumber();		}
		@Override public String getStreetStair() 	{return getMod303().getStreetStair();		}
		@Override public String getStreetFloor() 	{return getMod303().getStreetFloor();		}
		@Override public String getStreetDoor() 	{return getMod303().getStreetDoor();		}
		@Override public String getTown() 			{return getMod303().getTown();				}
		@Override public String getProvince() 		{return getMod303().getProvince();			}
		@Override public String getZip() 			{return getMod303().getZip();				}
		@Override public String getContactPerson() 	{return getMod303().getContactPerson();		}
		@Override public String getContactPhone() 	{return getMod303().getContactPhone();		}
		@Override public String getContactCellular(){return getMod303().getContactCellular();	}
		@Override public String getContactEmail() 	{return getMod303().getContactEmail();		}
	
		@Override public void documentChanged(String value) {
			getMod303().setDocument(value);
			identificationLabelChanged();
			markAsDirty();			
		}
		@Override public void nameChanged(String value) {
			getMod303().setName(value);
			identificationLabelChanged();
			markAsDirty();			
		}
		@Override public void surnameChanged(String value) {
			getMod303().setSurname(value);
			identificationLabelChanged();
			markAsDirty();			
		}
		@Override public void phoneChanged(String value) {
			getMod303().setPhone(value);
			markAsDirty();			
		}
		@Override public void streetInitialChanged(String value) {
			getMod303().setStreetInitial(value);
			markAsDirty();			
		}
		@Override public void streetNameChanged(String value) {
			getMod303().setStreetName(value);
			markAsDirty();			
		}
		@Override public void streetNumberChanged(String value) {
			getMod303().setStreetNumber(value);
			markAsDirty();			
		}
		@Override public void streetStairChanged(String value) {
			getMod303().setStreetStair(value);
			markAsDirty();			
		}
		@Override public void streetFloorChanged(String value) {
			getMod303().setStreetFloor(value);
			markAsDirty();			
		}
		@Override public void streetDoorChanged(String value) {
			getMod303().setStreetDoor(value);
			markAsDirty();			
		}
		@Override public void townChanged(String value) {
			getMod303().setTown(value);
			markAsDirty();			
		}
		@Override public void provinceChanged(String value) {
			getMod303().setProvince(value);
			markAsDirty();			
		}
		@Override public void zipChanged(String value) {
			getMod303().setZip(value);
			markAsDirty();			
		}
		@Override public void contactPersonChanged(String value) {
			getMod303().setContactPerson(value);
			markAsDirty();			
		}
		@Override public void contactPhoneChanged(String value) {
			getMod303().setContactPhone(value);
			markAsDirty();			
		}
		@Override public void contactCellularChanged(String value) {
			getMod303().setContactCellular(value);
			markAsDirty();			
		}
		@Override public void contactMailChanged(String value) {
			getMod303().setContactEmail(value);
			markAsDirty();			
		}
	}
	
	protected FlowPanel getInformationPanel() {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.CSS.aonScrollArea());
		panel.addStyleName(AON.CSS.aonWidthAll());
		panel.addStyleName(AON.CSS.aonMarginTop());
		panel.addStyleName(AON.CSS.aonPaddingTop());
		panel.addStyleName(AON.CSS.aonPaddingLeft());

		Label title = new Label("Informaci\u00F3n \u00FAtil para la confecci\u00F3n del modelo");
		title.setStyleName(AON.CSS.aonMarginTop());
		title.addStyleName(AON.CSS.aonBold());
		title.addStyleName(AON.CSS.aonTextUnderline());
		panel.add(title);
		
		for (Pair<String, String> pair : getInformationLinks()) {
			FlowPanel anchorPanel = new FlowPanel();
			anchorPanel.addStyleName(AON.CSS.aonMarginTop());
			Anchor a = new Anchor(pair.getLeft(),pair.getRight(), "_blank");
			a.setStyleName(AON.CSS.aonLabelWithIcon());
			a.addStyleName(FiscalModelUtils.getAdministrationBWIconStyle(mod303.getAdministration()));
			a.addStyleName(AON.CSS.aonPaddingLeft());
			anchorPanel.add(a);
			panel.add(anchorPanel);
		}
		return panel;
	}
	
	protected void submitForm(String action) {
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		mod303Hidden.setValue(String.valueOf(getMod303().getId()));
		domainIdHidden.setValue(String.valueOf(callback.getDomain()));
		domainNameHidden.setValue(callback.getDomainName());
		userHidden.setValue(callback.getUser());
		diskForm.submit();
	}
	
	protected void submitAEAT(String action) {
		submitAEAT(action, "null", "null", "null", "null", "null");
	}
	
	protected void submitAEAT(String action, String cert, String pass, String document, String name, String nrc) {
		aeatForm.setAction(GWT.getHostPageBaseURL() + action);
		modAeatHidden.setValue(String.valueOf(getMod303().getId()));
		domainIdAeatHidden.setValue(String.valueOf(callback.getDomain()));
		domainNameAeatHidden.setValue(callback.getDomainName());
		userAeatHidden.setValue(callback.getUser());
		certAeatHidden.setValue(cert);
		passAeatHidden.setValue(pass);
		nameAeatHidden.setValue(name);
		documentAeatHidden.setValue(document);
		nrcAeatHidden.setValue(nrc != null ? nrc : "null");
		testHidden.setValue(getTest() ? "1" : "0");

		aeatForm.submit();
	}
	
	private void paintDeclarationHeaderTable(SimplePanel panel) {
		FlowPanel infoPanel = new FlowPanel();
		infoPanel.setStyleName(AON.CSS.aonFlexBlock());
		infoPanel.addStyleName(AON.CSS.aonWidthAlmostAll());
		infoPanel.addStyleName(AON.CSS.aonHeightAll());
		infoPanel.addStyleName(AON.CSS.aonBlockCenter());
		
		FlowPanel longCell = new FlowPanel();
		longCell.setStyleName(AON.CSS.aonNowrap());
		longCell.addStyleName(AON.CSS.aonFlexGrow1());
		longCell.addStyleName(AON.CSS.aonTextCenter());
		
		FlowPanel dNPanel = new FlowPanel();
		longCell.add(dNPanel);
		
		FlowPanel docNamePanel = new FlowPanel();
		docNamePanel.setStyleName(AON.CSS.aonFontLarger());
		docNamePanel.addStyleName(AON.CSS.aonBold());
		docNamePanel.addStyleName(AON.CSS.aonTextCenter());
		documentLabel.setText(this.mod303.getDocument());
		docNamePanel.add(documentLabel);
		nameLabel.setStyleName(AON.CSS.aonMarginLeft());
		nameLabel.setText(this.mod303.getName());
		docNamePanel.add(nameLabel);
		surnameLabel.setStyleName(AON.CSS.aonMarginLeft());
		surnameLabel.setText(this.mod303.getSurname());
		docNamePanel.add(surnameLabel);
		dNPanel.add(docNamePanel);
		
		paymentInfo.setStyleName(AON.CSS.aonFontSmaller());
		paymentInfo.addStyleName(AON.CSS.aonTextCenter());
		FiscalModelUtils.fillPaymentInfo(paymentInfo,mod303);
		dNPanel.add(paymentInfo);
		
		infoPanel.add(longCell);


		replacedLabel.setStyleName(AON.CSS.aonMarginLeft());
		if (mod303.isReplacement()) {
			replacedLabel.setText("Sustit.");
			replacedLabel.setStyleName(AON.CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.CSS.aonIconLabel());
		}
		if (mod303.isComplementary()) {
			replacedLabel.setText("Complem.");
			replacedLabel.setStyleName(AON.CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.CSS.aonIconLabel());
		}
		infoPanel.add(replacedLabel);
		
		prorataLabel.setStyleName(AON.CSS.aonMarginLeft());
		if (AonMathUtils.isNotZero(mod303.getProratePercent()) &&  !AonMathUtils.equals(mod303.getProratePercent(), 100.0)) {
			prorataLabel.setText(AON.MSG.prorrata() + ": " + mod303.getProratePercent() + "%");
			prorataLabel.setStyleName(AON.CSS.aonBold());
		}
		infoPanel.add(prorataLabel);
		
		dirtyPanel.setStyleName(AON.CSS.aonMarginLeft());
		styleDirtyLabel();
		infoPanel.add(dirtyPanel);
		
		styleStatusLabel(this.mod303);
		infoPanel.add(statusLabel);
		
		FlowPanel commentsPanel = new FlowPanel();
		commentsPanel.setStyleName(AON.CSS.aonMarginLeft());
		commentsButton.addClickHandler( event -> {
			final AonToast toast = new AonToast();
			FlowPanel commentPanel = new FlowPanel();
			commentPanel.setStyleName( FiscalModelUtils.getAdministrationBackgroundStyle(mod303.getAdministration()) );
			commentPanel.setStyleName(AON.CSS.aonHeightAll());
			commentPanel.addStyleName(AON.CSS.aonTextCenter());
			TextArea comment = new TextArea();
			comment.addValueChangeHandler(event1 -> {
				mod303.setComments(event1.getValue());
				styleCommentsButton();
				Model303.service.saveComments( callback.getOptions().getDomainName(), callback.getOptions().getUser(), mod303, new AsyncCallback<Mod303>() {
					@Override
					public void onSuccess(Mod303 result) {
						toast.hide();
					}

					@Override
					public void onFailure(Throwable caught) {
						toast.hide();
						callback.showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
					}
				});
			});
			comment.setText(mod303.getComments());
			comment.setWidth("90%");
			comment.setHeight("5em");
			commentPanel.add(comment);
			toast.show(AON.MSG.comments(), commentPanel);
		});
		commentsPanel.add(commentsButton);
		infoPanel.add( commentsPanel );
		
		styleCommentsButton();
		panel.setWidget(infoPanel);
	}
	private void styleStatusLabel(Mod303 mod) {
		statusLabel.setText(mod.getStatus().getName());
		statusLabel.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB( mod.getStatus() ));
		statusLabel.getElement().getStyle().setColor(FiscalModelUtils.getStatusFrgColorRGB( mod.getStatus() ));
		statusLabel.setStyleName(AON.CSS.aonMarginLeft());
		statusLabel.addStyleName(AON.CSS.aonPaddingLeft());
		statusLabel.addStyleName(AON.CSS.aonPaddingRight());
		statusLabel.addStyleName(AON.CSS.aonTextCenter());
		statusLabel.addStyleName(AON.CSS.aonBorder());
		statusLabel.addStyleName(AON.CSS.aonBold());
		statusLabel.addStyleName(AON.CSS.aonNowrap());
	}
	private void styleCommentsButton() {
		if (AonStringUtils.isEmpty(mod303.getComments())) {
			commentsButton.addStyleName(AON.CSS.aonIconNoComments());
			commentsButton.removeStyleName(AON.CSS.aonIconComments());
		} else {
			commentsButton.addStyleName(AON.CSS.aonIconComments());
			commentsButton.removeStyleName(AON.CSS.aonIconNoComments());
		}
		commentsButton.setTitle(mod303.getComments());
	}

	protected abstract LinkedList<Pair<String, String>> getInformationLinks();

}
