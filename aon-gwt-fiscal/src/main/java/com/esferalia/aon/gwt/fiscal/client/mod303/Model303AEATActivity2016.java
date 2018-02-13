package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.occam.api.model.fiscal.Mod303Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod303ActivityModule;
import com.esferalia.aon.occam.api.model.fiscal.modules.Module;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2016.Epigraph;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model303AEATActivity2016 extends DockLayoutPanel implements HasValueChangeHandlers<Mod303Activity> {

	private boolean lastPeriod;
	
	final TabLayoutPanel tabLayoutPanel = new TabLayoutPanel(26, Unit.PX);
	final ScrollPanel resultPanel = new ScrollPanel();
	final Label epigraph = new Label();
	final Label epigraphLabel = new Label();
	
	private IntegerBox tem = new IntegerBox();
	private IntegerBox dia = new IntegerBox();
	private IntegerBox emp = new IntegerBox();
	private ListBox    lor = new ListBox();
	 
	private Label description0 = new Label();
	private DoubleBox value0 = new DoubleBox(6);
	private Label unit0 = new Label();
	private DoubleBox factor0 = new DoubleBox(6);
	private DoubleBox result0 = new DoubleBox(6);

	private Label description1 = new Label();
	private DoubleBox value1 = new DoubleBox(6);
	private Label unit1 = new Label();
	private DoubleBox factor1 = new DoubleBox(6);
	private DoubleBox result1 = new DoubleBox(6);

	private Label description2 = new Label();
	private DoubleBox value2 = new DoubleBox(6);
	private Label unit2 = new Label();
	private DoubleBox factor2 = new DoubleBox(6);
	private DoubleBox result2 = new DoubleBox(6);

	private Label description3 = new Label();
	private DoubleBox value3 = new DoubleBox(6);
	private Label unit3 = new Label();
	private DoubleBox factor3 = new DoubleBox(6);
	private DoubleBox result3 = new DoubleBox(6);

	private Label description4 = new Label();
	private DoubleBox value4 = new DoubleBox(6);
	private Label unit4 = new Label();
	private DoubleBox factor4 = new DoubleBox(6);
	private DoubleBox result4 = new DoubleBox(6);

	private Label description5 = new Label();
	private DoubleBox value5 = new DoubleBox(6);
	private Label unit5 = new Label();
	private DoubleBox factor5 = new DoubleBox(6);
	private DoubleBox result5 = new DoubleBox(6);

	private Label description6 = new Label();
	private DoubleBox value6 = new DoubleBox(6);
	private Label unit6 = new Label();
	private DoubleBox factor6 = new DoubleBox(6);
	private DoubleBox result6 = new DoubleBox(6);

	private DoubleBox dev = new DoubleBox();
	private DoubleBox red = new DoubleBox();
	
	private DoubleBox ind = new DoubleBox();
	private DoubleBox por = new DoubleBox();
	private DoubleBox ing = new DoubleBox();
	
	private DoubleBox sopx = new DoubleBox();
	private DoubleBox sopy = new DoubleBox();
	private DoubleBox sop = new DoubleBox();
	private DoubleBox ict = new DoubleBox();
	private DoubleBox res = new DoubleBox();
	private DoubleBox pcm = new DoubleBox();
	private DoubleBox dvc = new DoubleBox();
	private DoubleBox cmn = new DoubleBox();
	private DoubleBox cad = new DoubleBox();


	public static interface IMod303ActivityCallback {
		Mod303Activity getActivity();
		void onAccept(Mod303Activity act);
		void onCancel();
		void onRemove();
	}
	
	public Model303AEATActivity2016(final IMod303ActivityCallback callback, boolean lastPeriod) {
		super(Unit.PX);
		this.lastPeriod = lastPeriod;
		setStyleName(AON.AON_CSS.aonSelector());
		setWidth("700px");
		setHeight("620px");

		lor.addItem("-");
		lor.addItem("Act. realz exclusivamente en Lorca.");
		lor.addItem("Act. realz. Lorca y otros municipios.");
		lor.setWidth("140px");

		FlowPanel headerPanel = new FlowPanel();
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
		toolbar.setWidget(0, 0, new Label(AON.MSG.activity()));
		toolbar.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonFindingTitle());
		toolbar.setWidget(0, 1, new Label(AON.MSG.additionalData()));
		toolbar.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonFindingToolbar());
		final Button accept = new Button();
		accept.setText(AON.MSG.saveAction());
		accept.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		accept.addStyleName(AON.AON_CSS.aonIconSave());
		accept.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				LinkedList<Mod303ActivityModule> modules = new LinkedList<Mod303ActivityModule>();
				modules.add(new Mod303ActivityModule().setDescription(description0.getText()).setValue(value0.getValue()).setUnit(unit0.getText()).setFactor(factor0.getValue()).setResult(result0.getValue()));				
				modules.add(new Mod303ActivityModule().setDescription(description1.getText()).setValue(value1.getValue()).setUnit(unit1.getText()).setFactor(factor1.getValue()).setResult(result1.getValue()));				
				modules.add(new Mod303ActivityModule().setDescription(description2.getText()).setValue(value2.getValue()).setUnit(unit2.getText()).setFactor(factor2.getValue()).setResult(result2.getValue()));				
				modules.add(new Mod303ActivityModule().setDescription(description3.getText()).setValue(value3.getValue()).setUnit(unit3.getText()).setFactor(factor3.getValue()).setResult(result3.getValue()));				
				modules.add(new Mod303ActivityModule().setDescription(description4.getText()).setValue(value4.getValue()).setUnit(unit4.getText()).setFactor(factor4.getValue()).setResult(result4.getValue()));				
				modules.add(new Mod303ActivityModule().setDescription(description5.getText()).setValue(value5.getValue()).setUnit(unit5.getText()).setFactor(factor5.getValue()).setResult(result5.getValue()));				
				modules.add(new Mod303ActivityModule().setDescription(description6.getText()).setValue(value6.getValue()).setUnit(unit6.getText()).setFactor(factor6.getValue()).setResult(result6.getValue()));				
				callback.onAccept(
					new Mod303Activity()
						.setEpigraph(epigraph.getText())
						.setDescription(epigraphLabel.getText())
						.setTem(tem.getValue())
						.setDia(dia.getValue())
						.setEmp(emp.getValue())
						.setLor(lor.getSelectedIndex())
						.setDev(dev.getValue())
						.setRed(red.getValue())
						.setInd(ind.getValue())
						.setPor(por.getValue())
						.setIng(ing.getValue())
						.setSopx(sopx.getValue())
						.setSopy(sopy.getValue())
						.setSop(sop.getValue())
						.setIct(ict.getValue())
						.setRes(res.getValue())
						.setPcm(pcm.getValue())
						.setDvc(dvc.getValue())
						.setCmn(cmn.getValue())
						.setCad(cad.getValue())
						.setModules(modules)						
						);
			}
		});
		buttonContainer.add(accept);
		final Button cancel = new Button();
		cancel.setText(AON.MSG.cancelAction());
		cancel.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		cancel.addStyleName(AON.AON_CSS.aonIconCancel());
		cancel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				callback.onCancel();
			}
		});
		buttonContainer.add(cancel);
		
		final Button remove = new Button();
		remove.setText(AON.MSG.deleteAction());
		remove.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		remove.addStyleName(AON.AON_CSS.aonIconDelete());
		remove.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ConfirmDialog dialog = new ConfirmDialog();
				dialog.confirm(AON.MSG.confirmDeleteAction(), new ConfirmDialogCallback() {
					@Override
					public void onCancel() {
					}
					
					@Override
					public void onAccept() {
						callback.onRemove();
					}
				});
			}
		});
		buttonContainer.add(remove);
		
		toolbarPanel.add(toolbar);
		headerPanel.add(toolbarPanel);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.setStyleName(AON.AON_CSS.aonPadding());
		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonWidthAll());
		tab.addStyleName(AON.AON_CSS.aonDataTable());
		tab.addStyleName(AON.AON_CSS.aonBorderBottom());
		epigraph.setStyleName(AON.AON_CSS.aonBold());
		epigraph.setStyleName(AON.AON_CSS.aonFontBig());
		tab.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonWidth80());
		tab.setWidget(0, 0, epigraph);
		Button showEpigraphs = new Button();
		showEpigraphs.setStyleName(AON.AON_CSS.aonIconLoupe());
		showEpigraphs.addStyleName(AON.AON_CSS.aonBorderNone());
		tab.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonWidth20());
		tab.setWidget(0, 1, showEpigraphs);
		
		epigraphLabel.setStyleName(AON.AON_CSS.aonFontBig());
		epigraphLabel.setStyleName(AON.AON_CSS.aonNowrap());
		tab.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonWidthAuto());
		tab.setWidget(0, 2, epigraphLabel);
		
		final Model303AEATActivity2016Panel epigraphPanel = new Model303AEATActivity2016Panel(new Model303AEATActivity2016Panel.SelectionCallBack() {
			
			@Override
			public void onSelect(Epigraph selected) {
				if (AonStringUtils.isNotBlank( callback.getActivity().getEpigraph())) {
					ConfirmDialog dialog = new ConfirmDialog();
					dialog.confirm(AON.MSG.newEpigrapSelected(), new ConfirmDialogCallback() {
						
						@Override
						public void onCancel() {}
						
						@Override
						public void onAccept() {
							accept(selected);
						}
					});
				} else {
					accept(selected);
				}
			}
			
			@Override
			public void onClose() {}
			
			private void accept(final Epigraph selected) {
				callback.getActivity().initialize();
				callback.getActivity().setEpigraph(selected.getEpigraph());
				callback.getActivity().setDescription(selected.getDescription());
				callback.getActivity().setPor(selected.getVatPorc());
				callback.getActivity().setMaxImport(selected.getLimExceso());
				callback.getActivity().setPcm(selected.getPorcMin());
				for (Module mod : selected.getVATModules()) {
					callback.getActivity().getModules().add(new Mod303ActivityModule()
						.setDescription(mod.getKey().getDescription())
						.setValue(0.0)
						.setUnit(mod.getUnit())
						.setFactor(mod.getAmount())
						.setResult(0.0)
						.setSalariedStaff(mod.isSalariedStaff())
						.setNoSalariedStaff(mod.isNoSalariedStaff()));
				}
				ValueChangeEvent.<Mod303Activity>fire(Model303AEATActivity2016.this, callback.getActivity());
			}
		});
		
		showEpigraphs.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				epigraphPanel.onShow();
			}
		});
		flowPanel.add(tab);
		headerPanel.add(flowPanel);
		
		populateActivity(callback.getActivity());		
		addNorth(headerPanel, 80);
		
		ScrollPanel additionalDataPanel = new ScrollPanel();
		additionalDataPanel.setWidget(getAdditionalDataPanel(callback, callback.getActivity()));
		tabLayoutPanel.add(additionalDataPanel, Model303Base.TAB_TEMPLATE.render(AON.MSG.additionalData(), AON.AON_CSS.aonFiscalModelDataTable()));

		ScrollPanel modulesPanel = new ScrollPanel();
		modulesPanel.setWidget(getModulesPanel(callback,callback.getActivity()));
		tabLayoutPanel.add(modulesPanel, Model303Base.TAB_TEMPLATE.render(AON.MSG.modules(), AON.AON_CSS.aonIconModel()));

		addNorth(tabLayoutPanel, 230);
		tabLayoutPanel.selectTab(1);
		
		resultPanel.setWidget(getResultPanel(callback,callback.getActivity()));		
		add(resultPanel);
		
		onResize();
	}
	
	public void populateActivity(Mod303Activity act) {
		epigraph.setText(act.getEpigraph());
		epigraphLabel.setText(AonStringUtils.abbreviate(act.getDescription(),100));
		epigraphLabel.setTitle(act.getDescription());
		tabLayoutPanel.setVisible(AonStringUtils.isNotBlank( act.getEpigraph() ) );
		resultPanel.setVisible(AonStringUtils.isNotBlank( act.getEpigraph() ) );
		tem.setValue(act.getTem());
		emp.setValue(act.getEmp());
		dia.setValue(act.getDia());
		lor.setSelectedIndex(act.getLor());
		
		populateModule(act,0,description0,value0,unit0,factor0,result0);
		populateModule(act,1,description1,value1,unit1,factor1,result1);
		populateModule(act,2,description2,value2,unit2,factor2,result2);
		populateModule(act,3,description3,value3,unit3,factor3,result3);
		populateModule(act,4,description4,value4,unit4,factor4,result4);
		populateModule(act,5,description5,value5,unit5,factor5,result5);
		populateModule(act,6,description6,value6,unit6,factor6,result6);
		
		dev.setValue(act.getDev());
		red.setValue(act.getRed());
		ind.setValue(act.getInd());
		por.setValue(act.getPor());
		ing.setValue(act.getIng());
		
		sopx.setValue(act.getSopx());
		sopy.setValue(act.getSopy());
		sop.setValue(act.getSop());
		ict.setValue(act.getIct());
		res.setValue(act.getRes());
		pcm.setValue(act.getPcm());
		dvc.setValue(act.getDvc());
		cmn.setValue(act.getCmn());
		cad.setValue(act.getCad());
		
	}
	
	private void populateModule(Mod303Activity act, int i, Label description, DoubleBox value, Label unit,
			DoubleBox factor, DoubleBox result) {
		boolean filled = act.getModules().size() > i;
		Mod303ActivityModule mod = filled?act.getModules().get(i):new Mod303ActivityModule();
		description.setText(mod.getDescription());
		value.setValue(mod.getValue());
		unit.setText(mod.getUnit());
		factor.setValue(mod.getFactor());
		result.setValue(mod.getResult());
		value.setEnabled(filled);
	}

	private Widget getAdditionalDataPanel(IMod303ActivityCallback callback, Mod303Activity act) {
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.AON_CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingRight() );

		table.getColumnFormatter().setWidth(1, "140px");
		int row = 0;
		
		tem.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				if (tem.getValue() == null) tem.setValue(0,false);
				act.setTem(tem.getValue());
				ValueChangeEvent.<Mod303Activity>fire(Model303AEATActivity2016.this, act);
			}
		});
		table.setWidget(row, 0, new Label(AON.MSG.irpfActivityTem()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		table.setWidget(row, 1, tem);
		++row;
		
		emp.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				if (emp.getValue() == null) emp.setValue(0,false);
				act.setEmp(emp.getValue());
				ValueChangeEvent.<Mod303Activity>fire(Model303AEATActivity2016.this, act);
			}
		});
		table.setWidget(row, 0, new Label(AON.MSG.irpfActivityEmp()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		table.setWidget(row, 1, emp);
		++row;

		lor.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				act.setLor(lor.getSelectedIndex());
				red.setEnabled(lor.getSelectedIndex() != 1);
				if (lor.getSelectedIndex() != 1) {
					red.setValue(0.0, true);	
				}
				ValueChangeEvent.<Mod303Activity>fire(Model303AEATActivity2016.this, act);
			}
		});
		table.setWidget(row, 0, new Label(AON.MSG.irpfActivityLor()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		table.setWidget(row, 1, lor);
		++row;

		dia.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				if (dia.getValue() == null) dia.setValue(0,false);
				act.setDia(dia.getValue());
				ValueChangeEvent.<Mod303Activity>fire(Model303AEATActivity2016.this, act);
			}
		});
		table.setWidget(row, 0, new Label(AON.MSG.irpfActivityDiaTrim()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		table.setWidget(row, 1, dia);
		++row;

		return table;
	}

	private Widget getModulesPanel(IMod303ActivityCallback callback, Mod303Activity act) {
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.AON_CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().setWidth(1, "100px");
		table.getColumnFormatter().setWidth(2, "150px");
		table.getColumnFormatter().setWidth(3, "100px");
		table.getColumnFormatter().setWidth(4, "100px");
		
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		
		int row = 0;
		table.getCellFormatter().setStyleName(row,0, AON.AON_CSS.aonFiscalModelDataTableHeader() );
		table.setWidget(row, 0, new Label(AON.MSG.description()));
		table.getCellFormatter().setStyleName(row,1, AON.AON_CSS.aonFiscalModelDataTableHeader() );
		table.getCellFormatter().addStyleName(row,1, AON.AON_CSS.aonTextRight() );
		table.setWidget(row, 1, new Label(AON.MSG.amount()));
		table.getCellFormatter().setStyleName(row,2, AON.AON_CSS.aonFiscalModelDataTableHeader() );
		table.setWidget(row, 2, new Label(AON.MSG.unit()));
		table.getCellFormatter().setStyleName(row,3, AON.AON_CSS.aonFiscalModelDataTableHeader() );
		table.getCellFormatter().addStyleName(row,3, AON.AON_CSS.aonTextRight() );
		table.setWidget(row, 3, new Label(AON.MSG.factor()));
		table.getCellFormatter().setStyleName(row,4, AON.AON_CSS.aonFiscalModelDataTableHeader() );
		table.getCellFormatter().addStyleName(row,4, AON.AON_CSS.aonTextRight() );
		table.setWidget(row, 4, new Label(AON.MSG.result()));
		++row;
		
		paintModuleRow(callback,act,table,0,description0,value0,unit0,factor0,result0);
		paintModuleRow(callback,act,table,1,description1,value1,unit1,factor1,result1);
		paintModuleRow(callback,act,table,2,description2,value2,unit2,factor2,result2);
		paintModuleRow(callback,act,table,3,description3,value3,unit3,factor3,result3);
		paintModuleRow(callback,act,table,4,description4,value4,unit4,factor4,result4);
		paintModuleRow(callback,act,table,5,description5,value5,unit5,factor5,result5);
		paintModuleRow(callback,act,table,6,description6,value6,unit6,factor6,result6);
		row = table.getRowCount();
		
		
		return table;
	}
	
	private void paintModuleRow(IMod303ActivityCallback callback, Mod303Activity act,FlexTable table, final int index, Label description, 
			DoubleBox value, Label unit, DoubleBox factor, DoubleBox result) {
		int row = table.getRowCount(); 
		table.setWidget(row, 0, description);
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFontSmall());
		value.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				if (value.getValue() == null) value.setValue(0.0,false);
				act.getModules().get(index).setValue(value.getValue());
				ValueChangeEvent.<Mod303Activity>fire(Model303AEATActivity2016.this, act);
			}
		});
		table.setWidget(row, 1, value);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonTextRight());
		table.setWidget(row, 2, unit);
		table.getCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonFontSmall());
		table.setWidget(row, 3, factor);
		factor.setEnabled(false);
		table.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonTextRight());
		table.setWidget(row, 4, result);
		result.setEnabled(false);
		table.getCellFormatter().setStyleName(row, 4, AON.AON_CSS.aonTextRight());
	}
	
	private Widget getResultPanel(IMod303ActivityCallback callback, Mod303Activity act) {
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.AON_CSS.aonMarginBottom());
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().setWidth(1, "20px");
		table.getColumnFormatter().setWidth(2, "150px");
		
		int row = 0;
		
		dev.setEnabled(false);
		table.setWidget(row, 0, new Label(AON.MSG.page6C()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		table.setWidget(row, 1, new BoxLabel("C"));
		table.setWidget(row, 2, dev);
		++row;
		
		red.setEnabled(act.getLor() != 1);
		red.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				if (red.getValue() == null) red.setValue(0.0,false);
				act.setRed(red.getValue());
				ValueChangeEvent.<Mod303Activity>fire(Model303AEATActivity2016.this, act);
			}
		});
		table.setWidget(row, 0, new Label(AON.MSG.reductions()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		table.setWidget(row, 1, new BoxLabel("D"));
		table.setWidget(row, 2, red);
		++row;
		
		if (!lastPeriod) {
			ind.setEnabled(false);
			table.setWidget(row, 0, new Label(AON.MSG.tempIndex()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
			table.setWidget(row, 1, new BoxLabel("Z"));
			table.setWidget(row, 2, ind);
			++row;

			por.setEnabled(false);
			table.setWidget(row, 0, new Label(AON.MSG.incomePercent()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
			table.setWidget(row, 1, new BoxLabel("E"));
			table.setWidget(row, 2, por);
			++row;

			ing.setEnabled(false);
			table.setWidget(row, 0, new Label(AON.MSG.income()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
			table.setWidget(row, 1, new BoxLabel("F"));
			table.setWidget(row, 2, ing);
			++row;
		} else {
			sopx.setEnabled(false);
			sopx.addStyleName(AON.AON_CSS.aonMarginRight());
			table.setWidget(row, 0, new Label(AON.MSG.devQuota1()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
			table.setWidget(row, 2, sopx);
			++row;
			
			
			sopy.addStyleName(AON.AON_CSS.aonMarginRight());
			sopy.addValueChangeHandler(new ValueChangeHandler<Double>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					if (sopy.getValue() == null) sopy.setValue(0.0,false);
					act.setSopy(sopy.getValue());
					ValueChangeEvent.<Mod303Activity>fire(Model303AEATActivity2016.this, act);
				}
			});
			table.setWidget(row, 0, new Label(AON.MSG.sopQuotaRest()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
			table.setWidget(row, 2, sopy);
			++row;
			
			sop.setEnabled(false);
			table.setWidget(row, 0, new Label(AON.MSG.page6D()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
			table.setWidget(row, 1, new BoxLabel("G"));
			table.setWidget(row, 2, sop);
			++row;
			
			ict.setEnabled(false);
			table.setWidget(row, 0, new Label(AON.MSG.tempIndex()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
			table.setWidget(row, 1, new BoxLabel("H"));
			table.setWidget(row, 2, ict);
			++row;

			res.setEnabled(false);
			table.setWidget(row, 0, new Label(AON.MSG.result()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
			table.setWidget(row, 1, new BoxLabel("I"));
			table.setWidget(row, 2, res);
			++row;
			
			pcm.setEnabled(false);
			table.setWidget(row, 0, new Label(AON.MSG.page6G()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
			table.setWidget(row, 1, new BoxLabel("J"));
			table.setWidget(row, 2, pcm);
			++row;

			dvc.addValueChangeHandler(new ValueChangeHandler<Double>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					if (dvc.getValue() == null) dvc.setValue(0.0,false);
					act.setDvc(dvc.getValue());
					ValueChangeEvent.<Mod303Activity>fire(Model303AEATActivity2016.this, act);
				}
			});
			table.setWidget(row, 0, new Label(AON.MSG.page6H()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
			table.setWidget(row, 1, new BoxLabel("K"));
			table.setWidget(row, 2, dvc);
			++row;
			
			cmn.setEnabled(false);
			table.setWidget(row, 0, new Label(AON.MSG.page6I()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
			table.setWidget(row, 1, new BoxLabel("L"));
			table.setWidget(row, 2, cmn);
			++row;

			cad.setEnabled(false);
			table.setWidget(row, 0, new Label(AON.MSG.yearSimplifiedQuota()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
			table.setWidget(row, 1, new BoxLabel("M"));
			table.setWidget(row, 2, cad);
			++row;
		}
		return table;
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Mod303Activity> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}
}
