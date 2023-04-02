package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod303Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod303ActivityModule;
import com.esferalia.aon.occam.api.model.fiscal.modules.Module;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2018;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2018.Epigraph;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

class Model303AEATActivity2020 extends DockLayoutPanel implements HasValueChangeHandlers<Mod303Activity> {

	private boolean lastPeriod;
	
	final TabLayoutPanel tabLayoutPanel = new TabLayoutPanel(26, Unit.PX);
	final ScrollPanel resultPanel = new ScrollPanel();
	final Label epigraph = new Label();
	final Label epigraphLabel = new Label();
	
	private AonIntegerBox tem = new AonIntegerBox();
	private AonIntegerBox dia = new AonIntegerBox();
	private AonIntegerBox emp = new AonIntegerBox();
	private ListBox    lor = new ListBox();
	private ListBox    cov = new ListBox();
	 
	private Label description0 = new Label();
	private AonDoubleBox value0 = new AonDoubleBox(6);
	private Label unit0 = new Label();
	private AonDoubleBox factor0 = new AonDoubleBox(6);
	private AonDoubleBox result0 = new AonDoubleBox(6);

	private Label description1 = new Label();
	private AonDoubleBox value1 = new AonDoubleBox(6);
	private Label unit1 = new Label();
	private AonDoubleBox factor1 = new AonDoubleBox(6);
	private AonDoubleBox result1 = new AonDoubleBox(6);

	private Label description2 = new Label();
	private AonDoubleBox value2 = new AonDoubleBox(6);
	private Label unit2 = new Label();
	private AonDoubleBox factor2 = new AonDoubleBox(6);
	private AonDoubleBox result2 = new AonDoubleBox(6);

	private Label description3 = new Label();
	private AonDoubleBox value3 = new AonDoubleBox(6);
	private Label unit3 = new Label();
	private AonDoubleBox factor3 = new AonDoubleBox(6);
	private AonDoubleBox result3 = new AonDoubleBox(6);

	private Label description4 = new Label();
	private AonDoubleBox value4 = new AonDoubleBox(6);
	private Label unit4 = new Label();
	private AonDoubleBox factor4 = new AonDoubleBox(6);
	private AonDoubleBox result4 = new AonDoubleBox(6);

	private Label description5 = new Label();
	private AonDoubleBox value5 = new AonDoubleBox(6);
	private Label unit5 = new Label();
	private AonDoubleBox factor5 = new AonDoubleBox(6);
	private AonDoubleBox result5 = new AonDoubleBox(6);

	private Label description6 = new Label();
	private AonDoubleBox value6 = new AonDoubleBox(6);
	private Label unit6 = new Label();
	private AonDoubleBox factor6 = new AonDoubleBox(6);
	private AonDoubleBox result6 = new AonDoubleBox(6);

	private AonDoubleBox dev = new AonDoubleBox();
	private AonDoubleBox red = new AonDoubleBox();
	
	private AonDoubleBox ind = new AonDoubleBox();
	private AonDoubleBox por = new AonDoubleBox();
	private AonDoubleBox ing = new AonDoubleBox();
	
	private AonDoubleBox sopx = new AonDoubleBox();
	private AonDoubleBox sopy = new AonDoubleBox();
	private AonDoubleBox sop = new AonDoubleBox();
	private AonDoubleBox ict = new AonDoubleBox();
	private AonDoubleBox res = new AonDoubleBox();
	private AonDoubleBox pcm = new AonDoubleBox();
	private AonDoubleBox dvc = new AonDoubleBox();
	private AonDoubleBox cmn = new AonDoubleBox();
	private AonDoubleBox cad = new AonDoubleBox();


	public static interface IMod303ActivityCallback {
		Mod303 getMod303();
		Mod303Activity getActivity();
		void onAccept(Mod303Activity act);
		void onCancel();
		void onRemove();
	}
	
	protected Model303AEATActivity2020(final IMod303ActivityCallback callback, boolean lastPeriod) {
		super(Unit.PX);
		this.lastPeriod = lastPeriod;
		setStyleName(AON.CSS.aonSelector());
		setWidth("700px");
		setHeight("620px");

		lor.addItem("-");
		lor.addItem("Act. realz exclusivamente en Lorca.");
		lor.addItem("Act. realz. Lorca y otros municipios.");
		lor.setWidth("140px");

		cov.addItem("-");
		cov.addItem("SI");
		cov.setWidth("40px");

		
		AonToolbar toolbarPanel = new AonToolbar("");
		addNorth(toolbarPanel, AonToolbar.HEIGTH);

		final AonToolbarButton accept = new AonToolbarButton(AON.MSG.saveAction(),AON.CSS.aonIconAccept());
		accept.addClickHandler( event -> {
				LinkedList<Mod303ActivityModule> modules = new LinkedList<>();
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
						.setCov(cov.getSelectedIndex())
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
		});
		toolbarPanel.add(accept);
		
		final AonToolbarButton cancel = new AonToolbarButton(AON.MSG.cancelAction(),AON.CSS.aonIconCancel());
		cancel.addClickHandler(event ->  callback.onCancel());
		toolbarPanel.add(cancel);
		 
		final AonToolbarButton remove = new AonToolbarButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
		remove.addClickHandler( event -> AonConfirmDialog.showConfirm(AON.MSG.confirmDeleteAction(), () -> callback.onRemove()));
		toolbarPanel.add(remove);
				
		FlowPanel epigraphContainerPanel = new FlowPanel();
		epigraphContainerPanel.setStyleName(AON.CSS.aonPadding());
		epigraphContainerPanel.addStyleName(AON.CSS.aonWidthAlmostAll());
		epigraphContainerPanel.addStyleName(AON.CSS.aonBlockCenter());
		epigraphContainerPanel.addStyleName(AON.CSS.aonBorder());
		epigraphContainerPanel.addStyleName(AON.CSS.aonFlexBlock());
		epigraphContainerPanel.addStyleName(AON.CSS.aonMarginBottom());
		epigraphContainerPanel.addStyleName(AON.CSS.aonBackgroundLigthGray());
		
		epigraph.setStyleName(AON.CSS.aonBold());
		epigraph.addStyleName(AON.CSS.aonFontLarger());
		epigraph.getElement().getStyle().setWidth(60, Unit.PX);
		epigraphContainerPanel.add(epigraph);

		AonTableButton showEpigraphs = new AonTableButton(AON.MSG.epigraph() ,AON.CSS.aonIconSearch());
		epigraphContainerPanel.add(showEpigraphs);
		
		epigraphLabel.setStyleName(AON.CSS.aonFontLarger());
		epigraphLabel.addStyleName(AON.CSS.aonNowrap());
		epigraphLabel.addStyleName(AON.CSS.aonFlexGrow1());
		epigraphLabel.addStyleName(AON.CSS.aonMarginLeft());
		epigraphContainerPanel.add(epigraphLabel);
		
		addNorth(epigraphContainerPanel, 40);

		final Model303AEATActivity2018Panel epigraphPanel = new Model303AEATActivity2018Panel();
		epigraphPanel.addSelectionHandler( new SelectionHandler<Modules2018.Epigraph>() {
			
			@Override
			public void onSelection(SelectionEvent<Epigraph> event) {
				Epigraph selected = event.getSelectedItem();
				if (AonStringUtils.isNotBlank( callback.getActivity().getEpigraph())) {
					AonConfirmDialog dialog = new AonConfirmDialog();
					dialog.confirm(AON.MSG.newEpigrapSelected(), new AonConfirmDialogCallback() {
						
						@Override
						public void onCancel() {
							// Nothing
						}
						
						@Override
						public void onAccept() {
							accept(selected);
						}
					});
				} else {
					accept(selected);
				}
			}
			
			private void accept(final Epigraph selected) {
				callback.getActivity().initialize();
				callback.getActivity().setEpigraph(selected.getEpigraph());
				callback.getActivity().setDescription(selected.getDescription());
				callback.getActivity().setPor(selected.getVatPorc());
				callback.getActivity().setMaxImport(selected.getLimExceso());
				callback.getActivity().setPcm(selected.getPorcMin());
				if (callback.getActivity().getDia() == 0) {
					@SuppressWarnings("deprecation")
					Date start = new Date( callback.getMod303().getYear(),
							callback.getMod303().getPeriod().getStartMonth(),
							1);
					@SuppressWarnings("deprecation")
					Date end = new Date( callback.getMod303().getYear(),
							callback.getMod303().getPeriod().getDueMonth(),
							1); 
					CalendarUtil.addMonthsToDate(end,1);
					callback.getActivity().setDia(  CalendarUtil.getDaysBetween(start, end) );
				}
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
				ValueChangeEvent.<Mod303Activity>fire(Model303AEATActivity2020.this, callback.getActivity());
			}
			
		});
		showEpigraphs.addClickHandler(event ->  epigraphPanel.onShow());
		
		addNorth(epigraphContainerPanel, 40);
		
		populateActivity(callback.getActivity());		
		
		ScrollPanel additionalDataPanel = new ScrollPanel();
		additionalDataPanel.setWidget(getAdditionalDataPanel(callback.getActivity()));
		tabLayoutPanel.add(additionalDataPanel, AON.MSG.additionalData());

		ScrollPanel modulesPanel = new ScrollPanel();
		modulesPanel.setWidget(getModulesPanel(callback.getActivity()));
		tabLayoutPanel.add(modulesPanel, AON.MSG.modules());

		addNorth(tabLayoutPanel, 240);
		tabLayoutPanel.selectTab(1);
		
		resultPanel.setWidget(getResultPanel(callback.getActivity()));		
		add(resultPanel);
		
		onResize();
	}
	
	public void populateActivity(Mod303Activity act) {
		epigraph.setText(act.getEpigraph());
		epigraphLabel.setText(AonStringUtils.abbreviate(act.getDescription(),80));
		epigraphLabel.setTitle(act.getDescription());
		tabLayoutPanel.setVisible(AonStringUtils.isNotBlank( act.getEpigraph() ) );
		resultPanel.setVisible(AonStringUtils.isNotBlank( act.getEpigraph() ) );
		tem.setValue(act.getTem());
		emp.setValue(act.getEmp());
		dia.setValue(act.getDia());
		lor.setSelectedIndex(act.getLor());
		cov.setSelectedIndex(act.getCov());
		
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
	
	private void populateModule(Mod303Activity act, int i, Label description, AonDoubleBox value, Label unit,
			AonDoubleBox factor, AonDoubleBox result) {
		boolean filled = act.getModules().size() > i;
		Mod303ActivityModule mod = filled?act.getModules().get(i):new Mod303ActivityModule();
		description.setText(mod.getDescription());
		value.setValue(mod.getValue());
		unit.setText(mod.getUnit());
		factor.setValue(mod.getFactor());
		result.setValue(mod.getResult());
		value.setEnabled(filled);
	}

	private Widget getAdditionalDataPanel(Mod303Activity act) {
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );

		table.getColumnFormatter().setWidth(1, "140px");
		int row = 0;
		
		tem.addValueChangeHandler(event -> {
			if (tem.getValue() == null) tem.setValue(0,false);
			act.setTem(tem.getValue());
			ValueChangeEvent.<Mod303Activity>fire(Model303AEATActivity2020.this, act);
		});
		table.setWidget(row, 0, new Label(AON.MSG.irpfActivityTem()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		table.setWidget(row, 1, tem);
		++row;
		
		emp.addValueChangeHandler(event ->  {
			if (emp.getValue() == null) emp.setValue(0,false);
			act.setEmp(emp.getValue());
			ValueChangeEvent.<Mod303Activity>fire(Model303AEATActivity2020.this, act);
		});
		table.setWidget(row, 0, new Label(AON.MSG.irpfActivityEmp()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		table.setWidget(row, 1, emp);
		++row;

		lor.addChangeHandler(event-> {
			act.setLor(lor.getSelectedIndex());
			ValueChangeEvent.<Mod303Activity>fire(Model303AEATActivity2020.this, act);
		});
		table.setWidget(row, 0, new Label(AON.MSG.irpfActivityLor()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		table.setWidget(row, 1, lor);
		++row;

		cov.addChangeHandler(event  -> {
			act.setCov(cov.getSelectedIndex());
			ValueChangeEvent.<Mod303Activity>fire(Model303AEATActivity2020.this, act);
		});
		table.setWidget(row, 0, new Label(AON.MSG.covidReduction()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		table.setWidget(row, 1, cov);
		++row;

		dia.addValueChangeHandler(event -> {
			if (dia.getValue() == null) dia.setValue(0,false);
			act.setDia(dia.getValue());
			ValueChangeEvent.<Mod303Activity>fire(Model303AEATActivity2020.this, act);
		});
		table.setWidget(row, 0, new Label(AON.MSG.irpfActivityDiaTrim()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		table.setWidget(row, 1, dia);

		return table;
	}

	private Widget getModulesPanel(Mod303Activity act) {
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().setWidth(1, "100px");
		table.getColumnFormatter().setWidth(2, "150px");
		table.getColumnFormatter().setWidth(3, "100px");
		table.getColumnFormatter().setWidth(4, "100px");
		
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingLeft() );
		table.getColumnFormatter().addStyleName(0, AON.CSS.aonPaddingRight() );
		
		int row = 0;
		table.getCellFormatter().setStyleName(row,0, AON.CSS.aonBold() );
		table.getCellFormatter().addStyleName(row,0, AON.CSS.aonBorderBottom());
		table.setWidget(row, 0, new Label(AON.MSG.description()));
		table.getCellFormatter().setStyleName(row,1, AON.CSS.aonBold() );
		table.getCellFormatter().addStyleName(row,1, AON.CSS.aonTextRight() );
		table.getCellFormatter().addStyleName(row,1, AON.CSS.aonBorderBottom());
		table.setWidget(row, 1, new Label(AON.MSG.amount()));
		table.getCellFormatter().setStyleName(row,2, AON.CSS.aonBold() );
		table.getCellFormatter().addStyleName(row,2, AON.CSS.aonBorderBottom());
		table.setWidget(row, 2, new Label(AON.MSG.unit()));
		table.getCellFormatter().setStyleName(row,3, AON.CSS.aonBold() );
		table.getCellFormatter().addStyleName(row,3, AON.CSS.aonTextRight() );
		table.getCellFormatter().addStyleName(row,3, AON.CSS.aonBorderBottom());
		table.setWidget(row, 3, new Label(AON.MSG.factor()));
		table.getCellFormatter().setStyleName(row,4, AON.CSS.aonBold() );
		table.getCellFormatter().addStyleName(row,4, AON.CSS.aonTextRight() );
		table.getCellFormatter().addStyleName(row,4, AON.CSS.aonBorderBottom());
		table.setWidget(row, 4, new Label(AON.MSG.result()));
		
		paintModuleRow(act,table,0,description0,value0,unit0,factor0,result0);
		paintModuleRow(act,table,1,description1,value1,unit1,factor1,result1);
		paintModuleRow(act,table,2,description2,value2,unit2,factor2,result2);
		paintModuleRow(act,table,3,description3,value3,unit3,factor3,result3);
		paintModuleRow(act,table,4,description4,value4,unit4,factor4,result4);
		paintModuleRow(act,table,5,description5,value5,unit5,factor5,result5);
		paintModuleRow(act,table,6,description6,value6,unit6,factor6,result6);
		
		return table;
	}
	
	private void paintModuleRow(Mod303Activity act,FlexTable table, final int index, Label description, 
			AonDoubleBox value, Label unit, AonDoubleBox factor, AonDoubleBox result) {
		value.addValueChangeHandler(event -> {
			if (value.getValue() == null) value.setValue(0.0,false);
			act.getModules().get(index).setValue(value.getValue());
			ValueChangeEvent.<Mod303Activity>fire(Model303AEATActivity2020.this, act);
		});

		int row = table.getRowCount(); 
		table.setWidget(row, 0, description);
		table.getCellFormatter().setStyleName(row,0, AON.CSS.aonBorderBottom());
		table.setWidget(row, 1, value);
		table.getCellFormatter().setStyleName(row, 1, AON.CSS.aonTextRight());
		table.getCellFormatter().addStyleName(row, 1, AON.CSS.aonBorderBottom());
		table.setWidget(row, 2, unit);
		table.getCellFormatter().setStyleName(row, 2, AON.CSS.aonBorderBottom());
		table.setWidget(row, 3, factor);
		factor.setReadOnly(true);
		table.getCellFormatter().setStyleName(row, 3, AON.CSS.aonTextRight());
		table.getCellFormatter().addStyleName(row, 3, AON.CSS.aonBorderBottom());
		table.setWidget(row, 4, result);
		result.setReadOnly(true);
		table.getCellFormatter().setStyleName(row, 4, AON.CSS.aonTextRight());
		table.getCellFormatter().addStyleName(row, 4, AON.CSS.aonBorderBottom());
	}
	
	private Widget getResultPanel(Mod303Activity act) {
		FlexTable table = new FlexTable();
		table.setWidth("100%");
		table.addStyleName(AON.CSS.aonMarginBottom());
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().setWidth(1, "20px");
		table.getColumnFormatter().setWidth(2, "150px");
		
		int row = 0;
		
		dev.setEnabled(false);
		table.setWidget(row, 0, new Label(AON.MSG.page6C()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		table.setWidget(row, 1, new AonBoxLabel("C"));
		table.setWidget(row, 2, dev);
		++row;
		
		red.setEnabled(act.getLor() != 1);
		red.addValueChangeHandler(event -> {
			if (red.getValue() == null) red.setValue(0.0,false);
			act.setRed(red.getValue());
			ValueChangeEvent.<Mod303Activity>fire(Model303AEATActivity2020.this, act);
		});
		table.setWidget(row, 0, new Label(AON.MSG.reductions()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
		table.setWidget(row, 1, new AonBoxLabel("D"));
		table.setWidget(row, 2, red);
		++row;
		
		if (!lastPeriod) {
			ind.setEnabled(false);
			table.setWidget(row, 0, new Label(AON.MSG.tempIndex()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
			table.setWidget(row, 1, new AonBoxLabel("Z"));
			table.setWidget(row, 2, ind);
			++row;

			por.setEnabled(false);
			table.setWidget(row, 0, new Label(AON.MSG.incomePercent()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
			table.setWidget(row, 1, new AonBoxLabel("E"));
			table.setWidget(row, 2, por);
			++row;

			ing.setEnabled(false);
			table.setWidget(row, 0, new Label(AON.MSG.income()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
			table.setWidget(row, 1, new AonBoxLabel("F"));
			table.setWidget(row, 2, ing);
		} else {
			sopx.setEnabled(false);
			sopx.addStyleName(AON.CSS.aonMarginRight());
			table.setWidget(row, 0, new Label(AON.MSG.devQuota1()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
			table.setWidget(row, 2, sopx);
			++row;
			
			
			sopy.addStyleName(AON.CSS.aonMarginRight());
			sopy.addValueChangeHandler(event -> {
				if (sopy.getValue() == null) sopy.setValue(0.0,false);
				act.setSopy(sopy.getValue());
				ValueChangeEvent.<Mod303Activity>fire(Model303AEATActivity2020.this, act);
			});
			table.setWidget(row, 0, new Label(AON.MSG.sopQuotaRest()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
			table.setWidget(row, 2, sopy);
			++row;
			
			sop.setEnabled(false);
			table.setWidget(row, 0, new Label(AON.MSG.page6D()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
			table.setWidget(row, 1, new AonBoxLabel("G"));
			table.setWidget(row, 2, sop);
			++row;
			
			ict.setEnabled(false);
			table.setWidget(row, 0, new Label(AON.MSG.tempIndex()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
			table.setWidget(row, 1, new AonBoxLabel("H"));
			table.setWidget(row, 2, ict);
			++row;

			res.setEnabled(false);
			table.setWidget(row, 0, new Label(AON.MSG.result()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
			table.setWidget(row, 1, new AonBoxLabel("I"));
			table.setWidget(row, 2, res);
			++row;
			
			pcm.setEnabled(false);
			table.setWidget(row, 0, new Label(AON.MSG.page6G()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
			table.setWidget(row, 1, new AonBoxLabel("J"));
			table.setWidget(row, 2, pcm);
			++row;

			dvc.addValueChangeHandler(event -> {
				if (dvc.getValue() == null) dvc.setValue(0.0,false);
				act.setDvc(dvc.getValue());
				ValueChangeEvent.<Mod303Activity>fire(Model303AEATActivity2020.this, act);
			});
			table.setWidget(row, 0, new Label(AON.MSG.page6H()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
			table.setWidget(row, 1, new AonBoxLabel("K"));
			table.setWidget(row, 2, dvc);
			++row;
			
			cmn.setEnabled(false);
			table.setWidget(row, 0, new Label(AON.MSG.page6I()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
			table.setWidget(row, 1, new AonBoxLabel("L"));
			table.setWidget(row, 2, cmn);
			++row;

			cad.setEnabled(false);
			table.setWidget(row, 0, new Label(AON.MSG.yearSimplifiedQuota()));
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingLeft() );
			table.setWidget(row, 1, new AonBoxLabel("M"));
			table.setWidget(row, 2, cad);
		}
		return table;
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Mod303Activity> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}
}
