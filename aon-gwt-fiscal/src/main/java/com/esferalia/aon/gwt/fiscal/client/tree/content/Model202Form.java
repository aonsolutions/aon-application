package com.esferalia.aon.gwt.fiscal.client.tree.content;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.Cnae2009Panel;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IbanTextBox;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.esferalia.aon.gwt.fiscal.client.tree.IFiscalTreeContent;
import com.esferalia.aon.gwt.fiscal.client.tree.ITreeNodeCallback;
import com.esferalia.aon.gwt.fiscal.client.tree.node.TreeNode;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.CNAE2009;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Model202Form extends ResizeComposite implements IFiscalTreeContent<Mod202>{

	static class CorrectionRow {
		String label;
		Mod202Key decrease;
		Mod202Key increase;
		private CorrectionRow(String label,Mod202Key increase, Mod202Key decrease) {
			this.label = label; 
			this.increase = increase; 
			this.decrease = decrease; 
		}
	}
	
	interface Model202FormBinder extends
			UiBinder<Widget, Model202Form> {
	}
	private static Mod202Key[] additionalDataKeys2015 = new Mod202Key[]{
			Mod202Key.X01,Mod202Key.X02,Mod202Key.X03,Mod202Key.X04,Mod202Key.X05,
			Mod202Key.X06,Mod202Key.X07,Mod202Key.X10,Mod202Key.X08,Mod202Key.X09,
			Mod202Key.X00};
	private static Mod202Key[] additionalDataKeys2016 = new Mod202Key[]{
			Mod202Key.X01,Mod202Key.X02,Mod202Key.X03,Mod202Key.X04,Mod202Key.X05,
			Mod202Key.X06,Mod202Key.X07,Mod202Key.X08,Mod202Key.X09,Mod202Key.X00};
	
	private static Mod202Key[] computeADataKeys = new Mod202Key[]{
		Mod202Key.C01,Mod202Key.C02,Mod202Key.C03};
	private static CorrectionRow[] correctionsKeys = new CorrectionRow[]{
		new CorrectionRow(AON.MSG.mod202Correction1(),Mod202Key.C05,Mod202Key.C06),
		new CorrectionRow(AON.MSG.mod202Correction2(),Mod202Key.C36,Mod202Key.C37),
		new CorrectionRow(AON.MSG.mod202Correction3(),Mod202Key.C07,Mod202Key.C08),
		new CorrectionRow(AON.MSG.mod202Correction4(),Mod202Key.C38,Mod202Key.C39)};
	private static Mod202Key[] computeBDataKeys2015 = new Mod202Key[]{
			Mod202Key.C09,Mod202Key.C43,Mod202Key.C13,Mod202Key.C44,Mod202Key.C14
		};
	private static Mod202Key[] computeBDataKeys2016 = new Mod202Key[]{
			Mod202Key.C13,Mod202Key.C44,Mod202Key.C14
		};
	private static Mod202Key[] computeB1DataKeys = new Mod202Key[]{
		Mod202Key.C16,Mod202Key.C17,Mod202Key.C47,Mod202Key.C40};
	private static Mod202Key[] computeB2DataKeys = new Mod202Key[]{
		Mod202Key.C50,Mod202Key.C42};
	private static Mod202Key[] computeB21DataKeys2015 = new Mod202Key[]{
			Mod202Key.C26,Mod202Key.C27,Mod202Key.C28,Mod202Key.C29,Mod202Key.C30 
			,Mod202Key.C31,Mod202Key.C32,Mod202Key.C33,Mod202Key.C34};
	private static Mod202Key[] computeB21DataKeys2016 = new Mod202Key[]{
			Mod202Key.C26,Mod202Key.C27,Mod202Key.C28,Mod202Key.C29,Mod202Key.C30 
			,Mod202Key.C31,Mod202Key.C32,Mod202Key.C34};

	private EnumMap<Mod202Key, CheckBox> checks = new EnumMap<Mod202Key, CheckBox>(Mod202Key.class);
	private EnumMap<Mod202Key, DoubleBox> inputs = new EnumMap<Mod202Key, DoubleBox>(Mod202Key.class);	

	private static final Model202FormBinder panelBinder = GWT.create(Model202FormBinder.class);
	
	Mod202 mod202;
	ITreeNodeCallback<Mod202> callback;
	 
	@UiField
	Button saveButton;
	@UiField
	Button deleteButton;
	@UiField
	Button finishButton;
	@UiField
	Button reopenButton;
	@UiField
	Button calculateButton;
	@UiField
	Button generateFileButton;
//	@UiField
//	Button printButton;
	
	@UiField
	TextBox document;
	
	@UiField
	TextBox name;
	
	@UiField
	TextBox surname;
	
	@UiField
	Label status;

	@UiField
	ListBox period;
	
	@UiField
	Label year;
	
	@UiField
	TextBox cnae;
	@UiField
	Label cnaeDescription;
	@UiField
	Button cnaeButton;
	@UiField
	DateBoxEx initialDate;
	
	@UiField
	SimplePanel headerPanel;

	@UiField
	FlowPanel tablePanel;
	
	@UiField
	Cnae2009Panel cnaePanel;
	
	TextBox typeX08; 
	ListBox listBox;
	ListBox calculationBox;
	
	@UiField
	Panel formContainer;
	FormPanel diskForm;
	Hidden modIdHidden;
	Hidden domainIdHidden;
	Hidden domainNameHidden;
	
	private boolean changeDisplayStyleName;
	private int changeDisplayMillis = 4000;

	public Model202Form() {
		Widget ui = panelBinder.createAndBindUi(this);
		initWidget(ui);
		
		cnaePanel = new Cnae2009Panel( new Cnae2009Panel.SelectionCallBack() {
			@Override
			public void onSelect(CNAE2009 selected) {
				cnae.setValue( selected.getCode());
				cnaeDescription.setText( selected.getDescription() );
			}
			@Override
			public void onClose() {
				// Nothing
			}
		});

		diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		modIdHidden = new Hidden("modId");
		formFlowPanel.add(modIdHidden);
		domainIdHidden = new Hidden("domainId");
		formFlowPanel.add(domainIdHidden);
		domainNameHidden = new Hidden("domainName");
		formFlowPanel.add(domainNameHidden);
		formContainer.add(diskForm);

		period.addItem("1P",Integer.toString( Period.T1.ordinal() ));
		period.addItem("2P",Integer.toString( Period.T2.ordinal() ));
		period.addItem("3P",Integer.toString( Period.T3.ordinal() ));
	}
	
	@Override
	public void select(Mod202 mod202) {
		if ( mod202.getId() != null) {
			FiscalTree.FISCAL_SERVICE.getMod202(FiscalTree.getCurrentDomainName(), 
					mod202.getDomain(), mod202.getId()
					,new AsyncCallback<Mod202>() {

						@Override
						public void onSuccess(Mod202 result) {
							populate(result);
						}
						@Override
						public void onFailure(Throwable caught) {
							PopupPanel box = DialogMessages.alertErrorWidget(caught.getMessage());
							box.center();
							box.show();
							
						}
			});
		} else {
			populate(mod202);
		}
	}
	
	@Override
	public void setCallback(ITreeNodeCallback<Mod202> callback) {
		this.callback = callback;
	}
	
	private void populate(Mod202 m202) {
		changeDisplayStyleName = false;
		this.mod202 = m202;

		year.setText( Integer.toString( mod202.getYear() ));
		period.setItemSelected(mod202.getPeriod().ordinal() - Period.T1.ordinal(),true);

		enableToolbarItems();
		
		document.setEnabled(false);
		name.setEnabled(false);
		surname.setEnabled(false);
		
		document.setValue(mod202.getDocument());
		name.setValue(mod202.getName());
		surname.setValue(mod202.getSurname());
		period.setEnabled( mod202.getId() == null );
		status.setText(mod202.isFinished()?AON.MSG.finished():AON.MSG.pending() );
		status.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		status.addStyleName(mod202.isFinished()?AON.AON_CSS.aonIconLock():AON.AON_CSS.aonIconUnlock()) ;
		
		this.cnae.setValue( mod202.getCnae() );
		CNAE2009 cnae = CNAE2009.valueOfCode(mod202.getCnae());
		this.cnae.setEnabled(isEnabled(mod202));
		this.cnaeDescription.setText( cnae==null?null:cnae.getDescription() );
		
		this.initialDate.setValue(mod202.getInitialDate());
		this.initialDate.setEnabled(isEnabled(mod202));
		
		paintHeaderTable(mod202);	
		paintAdditionData();
		paintComputeATable();
		paintComputeBTable();
		paintComputeB2Table();
		enableByCalculationMode();
	}
	
	private void paintHeaderTable(final FiscalModel fm) {
		headerPanel.setStyleName(AON.AON_CSS.aonWidthAll());
		
		FlexTable headerTable = new FlexTable();
		headerTable.setStyleName(AON.AON_CSS.aonFiscalModelTable());
		
		Label image = new Label("");
		image.setStyleName(TreeNode.getAdministrationImage(fm.getAdministration()));
		
		headerTable.setWidget(0, 0, image);
		headerTable.getFlexCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderImage());
		
		headerTable.setWidget(0, 1, new Label( AON.MSG.fiscalModelDescriptionlong(fm.getModel())));
		headerTable.getFlexCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		headerTable.getFlexCellFormatter().addStyleName(0, 1, TreeNode.getAdministrationBG(fm.getAdministration()));

		headerTable.setWidget(0, 2, new Label(fm.getModel().getName()));
		headerTable.getFlexCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(0, 2, TreeNode.getAdministrationBG(fm.getAdministration()));
		headerPanel.setWidget(headerTable);
	}

	private void enableByCalculationMode() {
		int i = calculationBox.getSelectedIndex();
		if (i == 0 ) {
			for (Mod202Key key : Mod202Key.MOD_B_KEYS) {
				if (inputs.containsKey(key)) inputs.get(key).setEnabled(false);	
			}
			for (Mod202Key key : Mod202Key.MOD_B1_KEYS) {
				if (inputs.containsKey(key)) inputs.get(key).setEnabled(false);
			}
			for (Mod202Key key : Mod202Key.MOD_B2_KEYS) {
				if (inputs.containsKey(key)) inputs.get(key).setEnabled(false);
			}
		}

		if (i == 1) {
			for (Mod202Key key : Mod202Key.MOD_A_KEYS) {
				if (inputs.containsKey(key)) inputs.get(key).setEnabled(false);
			}
			for (Mod202Key key : Mod202Key.MOD_B2_KEYS) {
				if (inputs.containsKey(key)) inputs.get(key).setEnabled(false);
			}
		}
		if (i == 2) {
			for (Mod202Key key : Mod202Key.MOD_A_KEYS) {
				if (inputs.containsKey(key)) inputs.get(key).setEnabled(false);
			}
			for (Mod202Key key : Mod202Key.MOD_B1_KEYS) {
				if (inputs.containsKey(key)) inputs.get(key).setEnabled(false);
			}
		}
	}

	private void paintAdditionData() {
		FlexTable table = new FlexTable();
		table.setStyleName(AON.AON_CSS.aonFiscalModelDataTable());
		table.setCellSpacing(0);
		int row = 0;
		table.setWidget(row, 0, new Label( AON.MSG.additionalData() ));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableTitle());
		table.getFlexCellFormatter().setColSpan(row, 0, 4);
		Mod202Key[] keys = mod202.getYear()<2016?additionalDataKeys2015:additionalDataKeys2016;
		for (Mod202Key key : keys) {
			row++;
			if (key == Mod202Key.X08) {
				FlowPanel p = new FlowPanel();
				p.setStyleName(AON.AON_CSS.aonNowrap());
				InlineLabel l = new InlineLabel( key.getDescription() );
				p.add(l);
				final TextBox typeX08 = new TextBox();
				this.typeX08 = typeX08;
				typeX08.setVisibleLength(6);
				typeX08.setValue(mod202.getDescription(Mod202Key.X08));
				typeX08.setEnabled(isEnabled(key));
				typeX08.setStyleName(AON.AON_CSS.aonInputText());
				typeX08.addStyleName(AON.AON_CSS.aonMarginLeft());
				p.add(typeX08);
				typeX08.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						mod202.putDescription(Mod202Key.X08,typeX08.getValue());
					}
				});
				table.setWidget(row, 0, p);
			} else if (key == Mod202Key.X09) {
				FlowPanel p = new FlowPanel();
				InlineLabel l = new InlineLabel( key.getDescription() );
				p.add(l);
				p.setStyleName(AON.AON_CSS.aonNowrap());
				final ListBox listBox = new ListBox();
				this.listBox = listBox;
				listBox.addItem("No Consta", "0");
				listBox.addItem(">= 10 Millones y < 20 Millones", "1");
				listBox.addItem(">= 20 Millones y < 60 Millones", "2");
				listBox.addItem(">= 60 Millones", "3");
				listBox.setSelectedIndex((int) mod202.getAmount(Mod202Key.X09));
				listBox.setEnabled(isEnabled(key));
				listBox.setStyleName(AON.AON_CSS.aonInputText());
				listBox.addStyleName(AON.AON_CSS.aonMarginLeft());
				p.add(listBox);
				listBox.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						mod202.putAmount(Mod202Key.X09,listBox.getSelectedIndex());
					}
				});
				table.setWidget(row, 0, p);
			} else if (key == Mod202Key.X00) {
				FlowPanel p = new FlowPanel();
				InlineLabel l = new InlineLabel( key.getDescription() );
				p.add(l);
				p.setStyleName(AON.AON_CSS.aonNowrap());
				final ListBox calcBox = new ListBox();
				this.calculationBox = calcBox;
				calcBox.addItem(AON.MSG.calculation0(), "0");
				calcBox.addItem(AON.MSG.calculation1(), "1");
				calcBox.addItem(AON.MSG.calculation2(), "2");
				calcBox.setSelectedIndex((int) mod202.getAmount(Mod202Key.X00));
				calcBox.setEnabled(isEnabled(key));
				calcBox.setStyleName(AON.AON_CSS.aonInputText());
				calcBox.addStyleName(AON.AON_CSS.aonMarginLeft());
				p.add(calcBox);
				calcBox.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						mod202.putAmount(Mod202Key.X00,calcBox.getSelectedIndex());
						calculate();
					}
				});
				table.setWidget(row, 0, p);
			} else {
				CheckBox checkBox = new CheckBox(key.getDescription());
				checkBox.setValue(mod202.getAmount(key)==1);
				checkBox.setEnabled(isEnabled(key));
				checkBox.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						calculate();
					}
				});
				checks.put(key, checkBox);
				table.setWidget(row, 0, checkBox);
			}
			table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
			table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
		}
		tablePanel.add(table);
	}

	private void paintComputeATable() {
		FlexTable table = new FlexTable();
		table.setStyleName(AON.AON_CSS.aonFiscalModelDataTable());
		table.setCellSpacing(0);
		int row = 0;
		table.setWidget(row, 0, new Label( AON.MSG.liquidacion() ));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableTitle());
		table.getFlexCellFormatter().setColSpan(row, 0, 4);
		row++;
		table.setWidget(row, 0, new Label( AON.MSG.mod202Compute1() ));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableTitle());
		table.getFlexCellFormatter().setColSpan(row, 0, 4);
		for (Mod202Key key : computeADataKeys) {
			row++;
			table.setWidget(row, 0, new Label( key.getDescription()));
			table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
			table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
			table.getFlexCellFormatter().setColSpan(row, 0, 3);
			FlowPanel p = getInputPanel(key);
			table.setWidget(row, 1, p );
			table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());
		}
		tablePanel.add(table);
	}

	private void paintComputeBTable() {
		FlexTable table = new FlexTable();
		table.setStyleName(AON.AON_CSS.aonFiscalModelDataTable());
		table.setCellSpacing(0);
		int row = 0;
		table.setWidget(row, 0, new Label( AON.MSG.mod202Compute2() ));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableTitle());
		table.getFlexCellFormatter().setColSpan(row, 0, 3);
		row++;
		table.setWidget(row, 0, new Label( Mod202Key.C04.getDescription()));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
		table.getFlexCellFormatter().setColSpan(row, 0, 3);
		FlowPanel p = getInputPanel(Mod202Key.C04);
		table.setWidget(row, 1, p );
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());
		table.getFlexCellFormatter().setColSpan(row, 0, 2);
		row++;
		table.setWidget(row, 0, new Label( AON.MSG.mod202Compute21() ));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableTitle());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonWidthAuto());
		table.setWidget(row, 1, new Label( AON.MSG.increase() ));
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableTitle());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonWidth190());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 2, new Label( AON.MSG.decrease()));
		table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonFiscalModelDataTableTitle());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonWidth190());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		for (CorrectionRow cr : correctionsKeys) {
			row++;
			table.setWidget(row, 0, new Label( cr.label));
			table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
			table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
			if (cr.increase == Mod202Key.C36) {
				table.setWidget(row, 1, new Label(""));	
			} else {
				table.setWidget(row, 1, getInputPanel(cr.increase) );
				table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());
			}
			table.setWidget(row, 2, getInputPanel(cr.decrease));
			table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonFiscalModelDataTableData());
		}
		Mod202Key[] keys = mod202.getYear()<2016?computeBDataKeys2015:computeBDataKeys2016;
		for (Mod202Key key : keys) {
			row++;
			table.setWidget(row, 0, new Label( key.getDescription()));
			table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
			table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
			table.setWidget(row, 1, getInputPanel(key));
			table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());
			table.getFlexCellFormatter().setColSpan(row, 1, 2);
		}
		row++;
		table.setWidget(row, 0, new Label( "" ));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableTitle());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonWidthAuto());
		table.setWidget(row, 1, new Label( AON.MSG.increase() ));
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableTitle());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonWidth190());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 2, new Label( AON.MSG.decrease()));
		table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonFiscalModelDataTableTitle());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonWidth190());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		row++;
		table.setWidget(row, 0, new Label( AON.MSG.mod202Correction5() ));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
		table.setWidget(row, 1, getInputPanel(Mod202Key.C45) );
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());
		table.setWidget(row, 2, getInputPanel(Mod202Key.C46));
		table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonFiscalModelDataTableData());
		row++;
		table.setWidget(row, 0, new Label( AON.MSG.mod202Compute3() ));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableTitle());
		table.getFlexCellFormatter().setColSpan(row, 0, 3);
		for (Mod202Key key : computeB1DataKeys) {
			row++;
			table.setWidget(row, 0, new Label( key.getDescription()));
			table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
			table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
			table.setWidget(row, 1, getInputPanel(key));
			table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());
			table.getFlexCellFormatter().setColSpan(row, 1, 2);
		}

		row++;
		table.setWidget(row, 0, new Label( "" ));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableTitle());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonWidthAuto());
		table.setWidget(row, 1, new Label( AON.MSG.increase() ));
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableTitle());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonWidth190());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 2, new Label( AON.MSG.decrease()));
		table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonFiscalModelDataTableTitle());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonWidth190());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		row++;
		table.setWidget(row, 0, new Label( AON.MSG.mod202Correction6() ));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
		table.setWidget(row, 1, getInputPanel(Mod202Key.C48) );
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());
		table.setWidget(row, 2, getInputPanel(Mod202Key.C49));
		table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonFiscalModelDataTableData());
		row++;
		table.setWidget(row, 0, new Label( Mod202Key.C18.getDescription()));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
		table.setWidget(row, 1, getInputPanel(Mod202Key.C18));
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());
		table.getFlexCellFormatter().setColSpan(row, 1, 2);
		tablePanel.add(table);
	}
	
	private void paintComputeB2Table() {
		FlexTable table = new FlexTable();
		table.setStyleName(AON.AON_CSS.aonFiscalModelDataTable());
		table.setCellSpacing(0);
		int row = 0;
		table.setWidget(row, 0, new Label( AON.MSG.mod202Compute4() ));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableTitle());
		table.getFlexCellFormatter().setColSpan(row, 0, 5);
		
		row++;
		table.setWidget(row, 0, new Label( Mod202Key.C19.getDescription()));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
		table.setWidget(row, 1, getInputPanel(Mod202Key.C19));
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());
		table.getFlexCellFormatter().setColSpan(row, 2, 2);
		table.setWidget(row, 3, new Label( Mod202Key.C22.getDescription()));
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonPaddingLeft());

		row++;
		table.setWidget(row, 0, new Label( Mod202Key.C20.getDescription()));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
		table.setWidget(row, 1, getInputPanel(Mod202Key.C20));
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());
		table.setWidget(row, 2, new Label( Mod202Key.C21.getDescription()));
		table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonFiscalModelDataTableDesc());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonPaddingLeft());
		table.setWidget(row, 3, getInputPanel(Mod202Key.C21));
		table.getFlexCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonFiscalModelDataTableData());
		table.setWidget(row, 4, getInputPanel(Mod202Key.C22));
		table.getFlexCellFormatter().setStyleName(row, 4, AON.AON_CSS.aonFiscalModelDataTableData());
		
		row++;
		table.setWidget(row, 0, new Label( Mod202Key.C23.getDescription()));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
		table.setWidget(row, 1, getInputPanel(Mod202Key.C23));
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());
		table.setWidget(row, 2, new Label( Mod202Key.C24.getDescription()));
		table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonFiscalModelDataTableDesc());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonPaddingLeft());
		table.setWidget(row, 3, getInputPanel(Mod202Key.C24));
		table.getFlexCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonFiscalModelDataTableData());
		table.setWidget(row, 4, getInputPanel(Mod202Key.C25));
		table.getFlexCellFormatter().setStyleName(row, 4, AON.AON_CSS.aonFiscalModelDataTableData());
		
		for (Mod202Key key : computeB2DataKeys) {
			row++;
			table.setWidget(row, 0, new Label( key.getDescription()));
			table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
			table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
			table.getFlexCellFormatter().setColSpan(row, 0, 4);
			table.setWidget(row, 1, getInputPanel(key));
			table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());
		}
		row++;
		table.setWidget(row, 0, new Label( "" ));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableTitle());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonWidthAuto());
		table.getFlexCellFormatter().setColSpan(row, 0, 3);
		table.setWidget(row, 1, new Label( AON.MSG.increase() ));
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableTitle());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonWidth190());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 2, new Label( AON.MSG.decrease()));
		table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonFiscalModelDataTableTitle());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonWidth190());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		row++;
		table.setWidget(row, 0, new Label( AON.MSG.mod202Correction6() ));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
		table.getFlexCellFormatter().setColSpan(row, 0, 3);
		table.setWidget(row, 1, getInputPanel(Mod202Key.C51) );
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());
		table.setWidget(row, 2, getInputPanel(Mod202Key.C52));
		table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonFiscalModelDataTableData());
		Mod202Key[] keys = mod202.getYear()<2016?computeB21DataKeys2015:computeB21DataKeys2016;
		for (Mod202Key key : keys) {
			row++;
			table.setWidget(row, 0, new Label( key.getDescription()));
			table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
			table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
			table.getFlexCellFormatter().setColSpan(row, 0, 4);
			table.setWidget(row, 1, getInputPanel(key));
			table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());
		}
		tablePanel.add(table);
	}
	

	private FlowPanel getInputPanel(Mod202Key key) {
		FlowPanel p = new FlowPanel();
		p.setStyleName(AON.AON_CSS.aonNowrap());
		InlineLabel l = new InlineLabel( AonNumberUtils.toString( key.getBox()) );
		l.setStyleName(AON.AON_CSS.aonFiscalModelDataTableBox());
		p.add(l);
		DoubleBox doubleBox = new DoubleBox();
		doubleBox.setValue(mod202.getAmount(key));
		p.add(doubleBox);
		inputs.put(key, doubleBox);
		doubleBox.setReadOnly( 
			key == Mod202Key.C03 || key == Mod202Key.C38
		 || key == Mod202Key.C39 || key == Mod202Key.C13
		 || key == Mod202Key.C16 || key == Mod202Key.C17
		 || key == Mod202Key.C18 || key == Mod202Key.C19 
		 || key == Mod202Key.C21 || key == Mod202Key.C22 
		 || key == Mod202Key.C23 || key == Mod202Key.C24 
		 || key == Mod202Key.C25 || key == Mod202Key.C26 
		 || key == Mod202Key.C32 || key == Mod202Key.C33 
		 || key == Mod202Key.C34
				);
		doubleBox.setEnabled(isEnabled(key));
		doubleBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				calculate();
			}
		});
		return p;
	}


	private boolean isEnabled(Mod202Key key) {
		if (mod202.isFinished()) {
			return false;	
		}
		return key.isEnabled();
	}

	private boolean isEnabled(Mod202 mod202) {
		return ( !mod202.isFinished() && mod202.getYear() >= 2015 );
	}


	@UiHandler("calculateButton")
	void onCalculateButtonClick(ClickEvent event) {
		if (Window.confirm( AON.MSG.calculateAction())) {
			calculate();
		}
	}
	
	private void calculate() {
		prepareForSend();
		FiscalTree.FISCAL_SERVICE.calculateMod202(FiscalTree.getCurrentDomainName()
				,mod202
				,new AsyncCallback<Mod202>() {

					@Override
					public void onSuccess(Mod202 result) {
						changeDisplayStyleName = true;
						receiveModel(result);
						changeDisplayStyleName = false;
					}
					@Override
					public void onFailure(Throwable caught) {
						changeDisplayStyleName = false;
						PopupPanel box = DialogMessages.alertErrorWidget(caught.getMessage());
						box.center();
						box.show();
					}

		});
	}

	private void prepareForSend() {
		mod202.setPeriod( Period.values()[ period.getSelectedIndex() + Period.T1.ordinal() ] );
		mod202.setCnae( cnae.getValue() );
		mod202.setInitialDate(this.initialDate.getValue());
		
		for (Mod202Key key : checks.keySet()) {
			mod202.putAmount(key, checks.get(key).getValue()?1:0);
		}
		for (Mod202Key key : inputs.keySet()) {
			mod202.putAmount(key, inputs.get(key).getValue());
		}
	}

	private boolean shouldDisplayChange(Double oldText,Double newText) {
		return changeDisplayStyleName  
				&& changeDisplayMillis != 0 
				&& oldText != newText
				&& (oldText == null || !oldText.equals(newText));
	}

	private void receiveModel(Mod202 result) {
		this.mod202 = result;
		enableToolbarItems();		

		this.cnae.setValue( mod202.getCnae() );
		CNAE2009 cn = CNAE2009.valueOfCode(mod202.getCnae());
		this.cnaeDescription.setText( cn==null?null:cn.getDescription() );
		this.initialDate.setValue(mod202.getInitialDate());
		
		for (Mod202Key key : checks.keySet()) {
			checks.get(key).setValue(result.getAmount(key)==1);
			checks.get(key).setEnabled(isEnabled(key));
		}
		for (final Mod202Key key : inputs.keySet()) {
			Double oldValue = inputs.get(key).getValue();
			Double newValue = result.getAmount(key);
			inputs.get(key).setValue(newValue);
			if (shouldDisplayChange(oldValue, newValue)) {
				inputs.get(key).addStyleName(AON.AON_CSS.aonValueChanged());
				if (changeDisplayMillis > 0)
					new Timer() {
						@Override
						public void run() {
							inputs.get(key).removeStyleName(AON.AON_CSS.aonValueChanged());
						}
					}.schedule(changeDisplayMillis);
			}
			inputs.get(key).setEnabled(isEnabled(key));
		}
		period.setItemSelected(mod202.getPeriod().ordinal() - Period.T1.ordinal(),true);
		
		status.setText(mod202.isFinished()?AON.MSG.finished():AON.MSG.pending() );
		status.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		status.addStyleName(mod202.isFinished()?AON.AON_CSS.aonIconLock():AON.AON_CSS.aonIconUnlock()) ;
		
		enableByCalculationMode();		
		typeX08.setEnabled(isEnabled(mod202)); 
		listBox.setEnabled(isEnabled(mod202));
		calculationBox.setEnabled(isEnabled(mod202));
		cnae.setEnabled(isEnabled(mod202));
		initialDate.setEnabled(isEnabled(mod202));
	}
	
	private void enableToolbarItems() {
		boolean nevv = (mod202.getId() == null);
		saveButton.setEnabled(isEnabled(mod202));
		deleteButton.setEnabled(!nevv && isEnabled(mod202));
		calculateButton.setEnabled(isEnabled(mod202));
		finishButton.setVisible(!mod202.isFinished());
		reopenButton.setVisible(mod202.isFinished());
		generateFileButton.setVisible(!nevv && mod202.isFinished());
//		printButton.setVisible(!nevv); 
	}

	@UiHandler("finishButton")
	void onFinishButtonClick(ClickEvent event) {
		if (Window.confirm( AON.MSG.finish() )) {
			if ( mod202.getResult() <= 0 ) {
				mod202.putDescription(Mod202Key.P01, "N");
			} 
			final CustomDialog detailDialog = new CustomDialog();
			detailDialog.setVisible(false);
			detailDialog.setAnimationEnabled(true);
			detailDialog.setGlassEnabled(true);
			detailDialog.setModal(true);
			detailDialog.setCaption( AON.MSG.finish() );
			FlexTable table = new FlexTable();
			table.setStyleName(AON.AON_CSS.aonPanelGrid());
			table.addStyleName(AON.AON_CSS.aonWidthAll());
			table.addStyleName(AON.AON_CSS.aonMarginTop());
			detailDialog.add( table );
			
			final IbanTextBox iban = new IbanTextBox(getSuggestOracle());
			iban.setValue( mod202.getIban() );
			iban.addSelectionHandler( new SelectionHandler<Suggestion>() {
				@Override
				public void onSelection(SelectionEvent<Suggestion> event) {
					Suggestion suggestion = event.getSelectedItem();
					iban.setValue(suggestion.getReplacementString());
				}
			});
			table.setWidget(0, 0, new Label(AON.MSG.iban()));
			table.getFlexCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridOdd());
			table.getFlexCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonWidthAuto());
			table.setWidget(0, 1, iban);
			table.getFlexCellFormatter().setStyleName(0,1, AON.AON_CSS.aonPanelGridEven());
			
			FlowPanel buttons = new FlowPanel();
			buttons.setStyleName(AON.AON_CSS.aonWidthAll()); 
			buttons.addStyleName(AON.AON_CSS.aonTextCenter());
			Button accept = new Button();
			accept.setText(AON.MSG.accept());
			accept.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					detailDialog.hide();
					prepareForSend();
					mod202.setStatus(FiscalStatus.FINISHED);
					mod202.putDescription(Mod202Key.P01, "U");
					mod202.setIban( iban.getValue() );
					save();
				}
			});
			buttons.add(accept);
			Button cancel = new Button();
			cancel.addStyleName(AON.AON_CSS.aonMarginLeft());
			cancel.setText(AON.MSG.cancelAction());
			cancel.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					detailDialog.hide();
				}
			});
			buttons.add(cancel);
			table.setWidget(1, 0, buttons);
			table.getFlexCellFormatter().setColSpan(1, 0, 2);
			detailDialog.center();
			detailDialog.show();
		}
	}

	@UiHandler("reopenButton")
	void onReopenButtonClick(ClickEvent event) {
		if (Window.confirm( AON.MSG.reopen() )) {
			prepareForSend();
			mod202.setStatus(FiscalStatus.PENDING);
			save();
		}
	}

	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		if (Window.confirm( AON.MSG.saveAction())) {
			prepareForSend();
			save();
		}
	}
	
	@UiHandler("deleteButton")
	void onDeleteButtonClick(ClickEvent event) {
		if (Window.confirm( AON.MSG.deleteAction())) {
			FiscalTree.FISCAL_SERVICE.deleteMod202(FiscalTree.getCurrentDomainName()
					,mod202
					,new AsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							callback.delete(mod202);
						}
						@Override
						public void onFailure(Throwable caught) {
							PopupPanel box = DialogMessages.alertErrorWidget(caught.getMessage());
							box.center();
							box.show();
						}
			});
			
		}
	}

	private void save() {
		FiscalTree.FISCAL_SERVICE.saveMod202(FiscalTree.getCurrentDomainName()
				,mod202
				,new AsyncCallback<Mod202>() {

					@Override
					public void onSuccess(Mod202 result) {
						receiveModel(result);
					}
					@Override
					public void onFailure(Throwable caught) {
						PopupPanel box = DialogMessages.alertErrorWidget(caught.getMessage());
						box.center();
						box.show();
					}
		});
	}
	
	@UiHandler("generateFileButton")
	void onGenerateFileButtonClick(ClickEvent event) {
		Window.alert(
				  "Se va a proceder a la generaci\u00F3n de un fichero\n"
				+ "con los datos de la declaraci\u00F3n, para su \n"
				+ "presentaci\u00F3n en Hacienda.\n\n"
				+ "Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n\n"
				+ "El fichero se genera a partir de los datos guardados.");
		diskForm.setAction(GWT.getHostPageBaseURL() +"/aon_gwt_fiscal/Model202File");
		modIdHidden.setValue( String.valueOf(mod202.getId()) );
		domainIdHidden.setValue(String.valueOf(FiscalTree.getCurrentDomain()));
		domainNameHidden.setValue(FiscalTree.getCurrentDomainName());
		diskForm.submit();
	}

//	@UiHandler("printButton")
//	void onPrintButtonClick(ClickEvent event) {
//		Window.alert(
//				  "Se va a proceder a la validaci\u00F3n en los servidores de la \n"
//				+ "Agencia Tributaria. En el caso de validaci\u00F3n correcta,la Agencia \n"
//				+ "Tributaria devolver\u00E1 un documento PDF borrador con la declarai\u00F3n\n\n"
//				+ "Aseg\u00FArese de haber guardado la declaraci\u00F3n.\n\n"
//				+ "La petici\u00F3n se genera a partir de los datos guardados.");
//		diskForm.setAction(GWT.getHostPageBaseURL() +"/aon_gwt_fiscal/Model202Print");
//		modIdHidden.setValue( String.valueOf(mod202.getId()) );
//		domainIdHidden.setValue(String.valueOf(FiscalTree.getCurrentDomain()));
//		domainNameHidden.setValue(FiscalTree.getCurrentDomainName());
//		diskForm.submit();
//	}
	
	@UiHandler("period")
	void onChangePeriod(ChangeEvent event) {
		mod202.setPeriod( Period.values()[ period.getSelectedIndex() + Period.T1.ordinal() ] );
		callback.changeLabel(mod202);
	}
	
	@UiHandler("cnaeButton")
	void onClickCnae(ClickEvent event) {
		cnaePanel.onShow();
	}
	
	
	class EnterpriseSuggestOracle extends MultiWordSuggestOracle {
		@Override
		public void requestSuggestions(final Request request,
				final Callback callback) {
			FiscalTree.COMMON_SERVICE.getCompanyBanks(
					FiscalTree.getCurrentDomainName(),
					mod202.getDomain(),
					new AsyncCallback<LinkedList<CompanyBank>>() {

						public void onFailure(Throwable caught) {
							Window.alert("Error while getting suggestions.");
						}

						public void onSuccess(LinkedList<CompanyBank> result) {
							ArrayList<Suggestion> suggestions = new ArrayList<Suggestion>();
							if (result != null) {
								for (final CompanyBank cb : result) {
									suggestions.add(new IbanTextBox.IbanSuggestion(cb));
								}
							}
							Response resp = new Response(suggestions);
							callback.onSuggestionsReady(request, resp);
						}
					});
		}
	}
	
	private SuggestOracle getSuggestOracle() {
		return new EnterpriseSuggestOracle();
	}
}
