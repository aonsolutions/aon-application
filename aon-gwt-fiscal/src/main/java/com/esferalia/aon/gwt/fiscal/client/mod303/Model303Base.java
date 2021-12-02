package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.EnumMap;
import java.util.Map.Entry;

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
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303FinishDeclarationPopup.FinishDeclarationPopupCallback;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303IdentificationData.IModel303IdentificationDataCallback;
import com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public abstract class Model303Base extends DockLayoutPanel  {

	protected static final String MODEL303_FILE = "/aon_gwt_fiscal/ms/Model303File";
	private static final String MODEL303_PRINT = "/aon_gwt_fiscal/ms/Model303Print";

	protected static final String WIDTH_150PX = "150px";
	protected static final String WIDTH_140PX = "140px";

	protected static final boolean ENABLED = true;
	protected static final boolean DISABLED = false;
	protected static final boolean HAS_INFO = true;
	protected static final boolean HAS_NOT_INFO = false;
	
	private static final int MAX_LABEL_LENGTH = 100;
	
	private Mod303 mod303;
	private Model303Callback callback;
	private EnumMap<Mod303Key,AonDoubleBox> fieldsMap;
	private boolean dirty;
	
	protected FiscalModelAdmonPanel<Mod303, Model303ModuleOptions> admonPanel;
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
	protected Hidden mod303Hidden = new Hidden("mod303");
	protected Hidden domainIdHidden = new Hidden("domainId");
	protected Hidden domainNameHidden = new Hidden("domainName");
	protected Hidden userHidden = new Hidden("user");
	
	protected Model303Base(Mod303 mod303,Model303Callback cbk) {
		super(Unit.PX);
		
		this.callback = cbk;
		
		select( mod303 );
		
		AonFiscalModelHeader modelHeader = new AonFiscalModelHeader(this.mod303);
		addNorth(modelHeader, AonFiscalModelHeader.HEIGTH);
		addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		addNorth(getDeclarationToolbarPanel(), AonToolbar.HEIGTH);
		
		fieldsMap = new EnumMap<>(Mod303Key.class);
		
		
		setStyleName(AON.CSS.aonSelector());
	}
	
	public Model303Callback getCallback() {
		return callback;
	}
	protected Mod303 getModel() {
		return mod303;
	}
	public void setModel(Mod303 mod303) {
		this.mod303 = mod303;
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
			commentPanel.setStyleName( FiscalModelUtils.getAdministrationBackgroundStyle(mod303.getAdministration()) );
			commentPanel.addStyleName(AON.CSS.aonHeightAll());
			commentPanel.addStyleName(AON.CSS.aonTextCenter());
			TextArea comment = new TextArea();
			comment.addValueChangeHandler(event1 -> {
				mod303.setComments(event1.getValue());
				styleCommentsButton();
				Model303.service.saveComments( getCallback().getOptions().getOccam(), mod303, new AsyncCallback<Mod303>() {
					@Override
					public void onSuccess(Mod303 result) {
						toast.hide();
					}

					@Override
					public void onFailure(Throwable caught) {
						toast.hide();
						getCallback().showError(AON.MSG.unableToSaveDeclaration(caught.getMessage()));
					}
				});
			});
			comment.setText(mod303.getComments());
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
						getCallback().getOptions().getExternalCallback().onExit(mod303);
					} else {
						getCallback().onCancel(mod303);
					}
				}
				@Override
				public void onCancel() {
					cancelButton.setEnabled(true);
				}
			});
		} else {
			if (getCallback().getOptions().isBackButtonVisible() && getCallback().getOptions().hasExternalCallback()) {
				getCallback().getOptions().getExternalCallback().onExit(mod303);
			} else {
				getCallback().onCancel(mod303);
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

	protected void select( Mod303 mod303) {
		setModel(mod303);
		refreshToolbarState();
		styleStatusLabel(mod303);
	}
	
	protected void selectAndPopulate( Mod303 mod303) {
		select(mod303);
		populate(mod303);
		decorateDeclarationTab();
		decorateAdministrationTab();
	}
	
	private void refreshToolbarState() {
		toolbarPanel.setTitle(AonStringUtils.join(mod303.getDocument(),AonStringUtils.SPACE,mod303.getFullName()));
		resetButton.setVisible(!mod303.isNew() && !mod303.isFinished() && !mod303.isSent());
		auditButton.setVisible(!mod303.isNew());
		newButton.setVisible(!mod303.isNew() && !getCallback().getOptions().isBackButtonVisible() && !getCallback().getOptions().hasExternalCallback());
		cancelButton.setVisible(true);
		saveButton.setVisible(!mod303.isFinished() && !mod303.isSent());
		deleteButton.setVisible(!mod303.isNew() && !mod303.isFinished() && !mod303.isSent());
		printButton.setVisible(!mod303.isNew());
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

			if (key.isDiffEnabled() && AonMathUtils.isNotZero(mod303.ensureDetail(key).getAdjustAmount())) {
				input.addStyleName(AON.CSS.aonChanged());
				input.setTitle(AON.MSG.difCalc(AON.FMT.format(mod303.ensureDetail(key).getResultAmount()),
						AON.FMT.format(AonMathUtils.round( mod303.ensureDetail(key).getAdjustAmount() * -1))));
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
					Model303.service.getInfo(getCallback().getOptions().getOccam(),
							mod303,script, infoKey,new AsyncCallback<String>() {
						
						@Override
						public void onFailure(Throwable caught) {
							getCallback().showError(AON.MSG.errorMessage());
						}
						
						@Override
						public void onSuccess(String result) {
							getCallback().showInfoPanel(result);
						}
					}
				));
				buttonContainer.add(button);
			}
		}
		table.setWidget(row, col, buttonContainer);
	}
	
	protected void populate(Mod303 mod303) {
		for (Entry<Mod303Key, AonDoubleBox> entry : fieldsMap.entrySet()) {
			double d1 = mod303.getAmount(entry.getKey());
			double d2 = entry.getValue().getValue();
			if (!AonNumberUtils.equals(d1, d2)) {
				entry.getValue().setValue(d1,true,true);
			}
		}
	}
	
	public void calculateAndRefresh(AsyncCallback<Mod303> cbk) {
		Model303.service.calculate(getCallback().getOptions().getOccam(), this.mod303,
				new AsyncCallback<Mod303>() {

					@Override
					public void onFailure(Throwable caught) {
						getCallback().showError(AON.MSG.errorMessage());
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
	protected void save(AsyncCallback<Mod303> cbk) {
		saveButton.setEnabled(false);
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		Model303.service.save(getCallback().getOptions().getOccam(), this.mod303, new AsyncCallback<Mod303>() {
					@Override
					public void onSuccess(Mod303 result) {
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
				Model303.service.delete(getCallback().getOptions().getOccam(),mod303, new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						deleteButton.setEnabled(true);
						getCallback().onRemove(mod303);
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
		Model303.service.initializeForFinish(getCallback().getOptions().getOccam(),mod303,
				new AsyncCallback<Mod303>() {
					@Override
					public void onSuccess(Mod303 result) {
						setDirty(false);
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
		Model303FinishDeclarationPopup finalizeDialog = new Model303FinishDeclarationPopup(this.mod303, getCallback(), new FinishDeclarationPopupCallback() {
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
		Model303.service.markAsCustomerCheck(getCallback().getOptions().getOccam(), mod303, new AsyncCallback<Mod303>() {
					@Override
					public void onSuccess(Mod303 result) {
						setDirty(false);
						selectAndPopulate(result);
						popup.hide();
						markAsFinishedButton.setEnabled(true);
						showPaymentInfo(mod303);
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
		Model303.service.markAsFinished(getCallback().getOptions().getOccam(), mod303, new AsyncCallback<Mod303>() {
					@Override
					public void onSuccess(Mod303 result) {
						setDirty(false);
						selectAndPopulate(result);
						popup.hide();
						markAsFinishedButton.setEnabled(true);
						showPaymentInfo(mod303);
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
		if (mod303.isSent() && AonStringUtils.isNotBlank(mod303.getNumber())) {
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
		Model303.service.markAsPending(getCallback().getOptions().getOccam(), mod303, new AsyncCallback<Mod303>() {
					@Override
					public void onSuccess(Mod303 result) {
						setDirty(false);
						selectAndPopulate(result);
						popup.hide();
						markAsPendingButton.setEnabled(true);
						showPaymentInfo(mod303);
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
		Model303.service.markAsSent(getCallback().getOptions().getOccam(), mod303, new AsyncCallback<Mod303>() {
					@Override
					public void onSuccess(Mod303 result) {
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
		dialog.show(mod303);
	}

	protected class Model303IdentificationDataCallback implements IModel303IdentificationDataCallback {

		@Override public boolean isFinished() 		{ return getModel().isFinished() || getModel().isSent();}
		
		@Override public String getDocument() 		{return getModel().getDocument();			}
		@Override public String getName() 			{return getModel().getName(); 				}
		@Override public String getSurname() 		{return getModel().getSurname();			}
		@Override public String getPhone() 			{return getModel().getPhone();				}
		@Override public String getStreetInitial() 	{return getModel().getStreetInitial();		}
		@Override public String getStreetName() 	{return getModel().getStreetName();		}
		@Override public String getStreetNumber() 	{return getModel().getStreetNumber();		}
		@Override public String getStreetStair() 	{return getModel().getStreetStair();		}
		@Override public String getStreetFloor() 	{return getModel().getStreetFloor();		}
		@Override public String getStreetDoor() 	{return getModel().getStreetDoor();		}
		@Override public String getTown() 			{return getModel().getTown();				}
		@Override public String getProvince() 		{return getModel().getProvince();			}
		@Override public String getZip() 			{return getModel().getZip();				}
		@Override public String getContactPerson() 	{return getModel().getContactPerson();		}
		@Override public String getContactPhone() 	{return getModel().getContactPhone();		}
		@Override public String getContactCellular(){return getModel().getContactCellular();	}
		@Override public String getContactEmail() 	{return getModel().getContactEmail();		}
	
		@Override public void documentChanged(String value) {
			getModel().setDocument(value);
			identificationLabelChanged();
			markAsDirty();			
		}
		@Override public void nameChanged(String value) {
			getModel().setName(value);
			identificationLabelChanged();
			markAsDirty();			
		}
		@Override public void surnameChanged(String value) {
			getModel().setSurname(value);
			identificationLabelChanged();
			markAsDirty();			
		}
		@Override public void phoneChanged(String value) {
			getModel().setPhone(value);
			markAsDirty();			
		}
		@Override public void streetInitialChanged(String value) {
			getModel().setStreetInitial(value);
			markAsDirty();			
		}
		@Override public void streetNameChanged(String value) {
			getModel().setStreetName(value);
			markAsDirty();			
		}
		@Override public void streetNumberChanged(String value) {
			getModel().setStreetNumber(value);
			markAsDirty();			
		}
		@Override public void streetStairChanged(String value) {
			getModel().setStreetStair(value);
			markAsDirty();			
		}
		@Override public void streetFloorChanged(String value) {
			getModel().setStreetFloor(value);
			markAsDirty();			
		}
		@Override public void streetDoorChanged(String value) {
			getModel().setStreetDoor(value);
			markAsDirty();			
		}
		@Override public void townChanged(String value) {
			getModel().setTown(value);
			markAsDirty();			
		}
		@Override public void provinceChanged(String value) {
			getModel().setProvince(value);
			markAsDirty();			
		}
		@Override public void zipChanged(String value) {
			getModel().setZip(value);
			markAsDirty();			
		}
		@Override public void contactPersonChanged(String value) {
			getModel().setContactPerson(value);
			markAsDirty();			
		}
		@Override public void contactPhoneChanged(String value) {
			getModel().setContactPhone(value);
			markAsDirty();			
		}
		@Override public void contactCellularChanged(String value) {
			getModel().setContactCellular(value);
			markAsDirty();			
		}
		@Override public void contactMailChanged(String value) {
			getModel().setContactEmail(value);
			markAsDirty();			
		}
		
		private void identificationLabelChanged() {
			toolbarPanel.setTitle(AonStringUtils.join(mod303.getDocument(),AonStringUtils.SPACE,mod303.getFullName()));
		}
		
	}
	
	private void submitForm(String action) {
		diskForm.setMethod(FormPanel.METHOD_POST);
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		diskForm.clear();
		FlowPanel diskPanel = new FlowPanel();
		diskPanel.add(mod303Hidden);
		diskPanel.add(domainIdHidden);
		diskPanel.add(domainNameHidden);
		diskPanel.add(userHidden);
		diskForm.add(diskPanel);
		mod303Hidden.setValue(String.valueOf(getModel().getId()));
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

		if (mod303.isReplacement()) {
			replacedLabel.setText(AON.MSG.replacement());
			replacedLabel.setStyleName(AON.CSS.aonMarginLeft());
			replacedLabel.addStyleName(AON.CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.CSS.aonLabelWithIcon());
		}
		if (mod303.isComplementary()) {
			replacedLabel.setText( AON.MSG.complementary());
			replacedLabel.setStyleName(AON.CSS.aonMarginLeft());
			replacedLabel.addStyleName(AON.CSS.aonIconChecked());
			replacedLabel.addStyleName(AON.CSS.aonLabelWithIcon());
		}
		marksPanels.add(replacedLabel);

		if (AonMathUtils.isNotZero(mod303.getProratePercent()) &&  !AonMathUtils.equals(mod303.getProratePercent(), 100.0)) {
			prorataLabel.setStyleName(AON.CSS.aonMarginLeft());
			prorataLabel.setText(AON.MSG.prorrata() + ": " + mod303.getProratePercent() + "%");
			prorataLabel.addStyleName(AON.CSS.aonBold());
		}
		marksPanels.add(replacedLabel);
		marksPanels.add(prorataLabel);
		
		styleDirtyLabel();
		styleStatusLabel(this.mod303);
		
		decToolbar.getMessagePanel().add(marksPanels);
		
		decToolbar.setTitle(statusLabel);
		return decToolbar;
	}
	
	protected void styleDirtyLabel() {
		dirtyLabel.setVisible(isDirty());
		diffLabel.setVisible(!this.mod303.isDiffCalculationDisabled()); 		
		
		boolean adjusted = false;
		for (FiscalModelDetail det : this.mod303.getMap().values()) {
			if (AonMathUtils.isNotZero( det.getAdjustAmount())) {
				adjusted = true;
				break;
			}
		}
		adjLabel.setVisible(adjusted);
	}
	
	protected void styleStatusLabel(Mod303 mod) {
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
		if (AonStringUtils.isEmpty(mod303.getComments())) {
			commentsButton.addStyleName(AON.CSS.aonIconNoComments());
			commentsButton.removeStyleName(AON.CSS.aonIconComments());
		} else {
			commentsButton.addStyleName(AON.CSS.aonIconComments());
			commentsButton.removeStyleName(AON.CSS.aonIconNoComments());
		}
		commentsButton.setTitle(mod303.getComments());
	}
	
	protected void showPaymentInfo(Mod303 mod) {
		if (paymentContainer != null) {
			this.remove(paymentContainer);
			this.forceLayout();
		}
		if (mod.isFinished() || mod.isSent()) {
			StringBuilder buff = new StringBuilder(AON.MSG.result());
			buff.append(AonStringUtils.SPACE);
			buff.append(AON.FMT.format(mod.getResult()));
			if (mod.getDeclarationType() != null) {
				buff.append(AonStringUtils.SPACE);
				buff.append(mod.getDeclarationType().getDescription());
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

	private void onReset() {
		resetButton.setEnabled(false);
		AonConfirmDialog cd = new AonConfirmDialog();
		cd.confirm(AON.MSG.confirmDeclarationinitializationAction(), new AonConfirmDialogCallback() {

			@Override
			public void onAccept() {
				Model303.service.reset(getCallback().getOptions().getOccam(),getModel(),
						new AsyncCallback<Mod303>() {
							@Override
							public void onSuccess(Mod303 m303) {
								setDirty( true );
								selectAndPopulate(m303);
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

	protected void decorateDeclarationTab() {
		// Redefine if needed
	}

	protected void decorateAdministrationTab() {
		if (admonPanel != null) {
			admonPanel.manageLinks();
		}
	}

}
