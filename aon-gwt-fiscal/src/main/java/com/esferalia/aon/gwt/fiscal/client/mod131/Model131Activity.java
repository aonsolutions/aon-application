package com.esferalia.aon.gwt.fiscal.client.mod131;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.Epigraph2016Panel;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod131ActivityModule;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2016.Epigraph;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Model131Activity extends DockLayoutPanel {
	
	final Label epigraph = new Label();
	final Label epigraphLabel = new Label();
	
	@UiField CheckBox dis;
	@UiField DoubleBox com;
	@UiField IntegerBox tem;
	@UiField IntegerBox nue;
	@UiField CheckBox ceu;
	@UiField CheckBox loc;
	@UiField IntegerBox veh;
	@UiField CheckBox cap;
	@UiField CheckBox tns;
	@UiField CheckBox tss;
	@UiField IntegerBox mun;
	@UiField IntegerBox emp;
	@UiField IntegerBox lor;
	@UiField IntegerBox bat;
	@UiField DoubleBox prc;

	@UiField Label description0;
	@UiField DoubleBox value0;
	@UiField Label unit0;
	@UiField DoubleBox factor0;
	@UiField DoubleBox result0;

	@UiField Label description1;
	@UiField DoubleBox value1;
	@UiField Label unit1;
	@UiField DoubleBox factor1;
	@UiField DoubleBox result1;

	@UiField Label description2;
	@UiField DoubleBox value2;
	@UiField Label unit2;
	@UiField DoubleBox factor2;
	@UiField DoubleBox result2;

	@UiField Label description3;
	@UiField DoubleBox value3;
	@UiField Label unit3;
	@UiField DoubleBox factor3;
	@UiField DoubleBox result3;

	@UiField Label description4;
	@UiField DoubleBox value4;
	@UiField Label unit4;
	@UiField DoubleBox factor4;
	@UiField DoubleBox result4;

	@UiField Label description5;
	@UiField DoubleBox value5;
	@UiField Label unit5;
	@UiField DoubleBox factor5;
	@UiField DoubleBox result5;

	@UiField Label description6;
	@UiField DoubleBox value6;
	@UiField Label unit6;
	@UiField DoubleBox factor6;
	@UiField DoubleBox result6;

	@UiField DoubleBox rnp;
	@UiField DoubleBox iem;
	@UiField DoubleBox iin;
	@UiField DoubleBox rnm;
	@UiField DoubleBox ic1;
	@UiField DoubleBox ic2;
	@UiField DoubleBox ic3;
	@UiField DoubleBox ic4;
	@UiField DoubleBox ic5;
	@UiField DoubleBox rpf;
	@UiField DoubleBox rlo;
	@UiField DoubleBox rdr;
	@UiField IntegerBox dia;
	@UiField DoubleBox net;
	@UiField DoubleBox por;
	@UiField DoubleBox res;

	public static interface IMod131ActivityCallback {
		Mod131Activity getActivity();
		void onAccept();
		void onCancel();
	}
	
	
	private IMod131ActivityCallback callback;
	
	interface Model131ActivityBinder extends UiBinder<Widget, Model131Activity> {}
	private static final Model131ActivityBinder BINDER = 
			GWT.create(Model131ActivityBinder.class);

	public Model131Activity(final IMod131ActivityCallback callback) {
		super(Unit.PX);
		setWidth("700px");
		setHeight("490px");
		Widget ui = BINDER.createAndBindUi(this);
		this.callback = callback;
		populateActivity(this.callback.getActivity());
		addNorth(getHeaderPanel(), 50);
		add(ui);
		onResize();		
	}

	private Widget getHeaderPanel() {
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
		
		final Epigraph2016Panel epigraphPanel = new Epigraph2016Panel( new Epigraph2016Panel.SelectionCallBack() {
			@Override
			public void onSelect(Epigraph selected) {
				epigraph.setText(selected.getEpigraph());
				epigraphLabel.setText(AonStringUtils.abbreviate(selected.getDescription(),100));
				epigraphLabel.setTitle(selected.getDescription());
			}
			@Override
			public void onClose() {
				// Nothing
			}
		});

		showEpigraphs.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				epigraphPanel.onShow();
				
			}
		});
		flowPanel.add(tab);
		return flowPanel;
	}
	
	private void populateActivity(Mod131Activity act) {
		epigraph.setText(act.getEpigraph());
		epigraphLabel.setText(AonStringUtils.abbreviate(act.getDescription(),100));
		epigraphLabel.setTitle(act.getDescription());
		
		dis.setValue(act.isDis());
		com.setValue(act.getCom());
		tem.setValue(act.getTem());
		nue.setValue(act.getNue());
		ceu.setValue(act.isCeu());
		loc.setValue(act.isLoc());
		veh.setValue(act.getVeh());
		cap.setValue(act.isCap());
		tns.setValue(act.isTns());
		tss.setValue(act.isTss());
		
		mun.setValue(act.getMun());
		emp.setValue(act.getEmp());
		lor.setValue(act.getLor());
		bat.setValue(act.getBat());
		prc.setValue(act.getPrc());
		int i = 0;
		Mod131ActivityModule mod = (act.getModules().size() > i)?act.getModules().get(i):new Mod131ActivityModule();
		i++;
		description0.setText(mod.getDescription());
		value0.setValue(mod.getValue());
		unit0.setText(mod.getUnit());
		factor0.setValue(mod.getFactor());
		result0.setValue(mod.getResult());

		mod = (act.getModules().size() > i)?act.getModules().get(i):new Mod131ActivityModule();
		i++;
		description1.setText(mod.getDescription());
		value1.setValue(mod.getValue());
		unit1.setText(mod.getUnit());
		factor1.setValue(mod.getFactor());
		result1.setValue(mod.getResult());
		
		mod = (act.getModules().size() > i)?act.getModules().get(i):new Mod131ActivityModule();
		i++;
		description2.setText(mod.getDescription());
		value2.setValue(mod.getValue());
		unit2.setText(mod.getUnit());
		factor2.setValue(mod.getFactor());
		result2.setValue(mod.getResult());		
		
		mod = (act.getModules().size() > i)?act.getModules().get(i):new Mod131ActivityModule();
		i++;
		description3.setText(mod.getDescription());
		value3.setValue(mod.getValue());
		unit3.setText(mod.getUnit());
		factor3.setValue(mod.getFactor());
		result3.setValue(mod.getResult());
		
		mod = (act.getModules().size() > i)?act.getModules().get(i):new Mod131ActivityModule();
		i++;
		description4.setText(mod.getDescription());
		value4.setValue(mod.getValue());
		unit4.setText(mod.getUnit());
		factor4.setValue(mod.getFactor());
		result4.setValue(mod.getResult());		
		
		mod = (act.getModules().size() > i)?act.getModules().get(i):new Mod131ActivityModule();
		i++;
		description5.setText(mod.getDescription());
		value5.setValue(mod.getValue());
		unit5.setText(mod.getUnit());
		factor5.setValue(mod.getFactor());
		result5.setValue(mod.getResult());		

		mod = (act.getModules().size() > i)?act.getModules().get(i):new Mod131ActivityModule();
		i++;
		description6.setText(mod.getDescription());
		value6.setValue(mod.getValue());
		unit6.setText(mod.getUnit());
		factor6.setValue(mod.getFactor());
		result6.setValue(mod.getResult());
		
		rnp.setValue(act.getRnp());
		iem.setValue(act.getIem());
		iin.setValue(act.getIin());
		rnm.setValue(act.getRnm());
		ic1.setValue(act.getIc1());
		ic2.setValue(act.getIc2());
		ic3.setValue(act.getIc3());
		ic4.setValue(act.getIc4());
		ic5.setValue(act.getIc5());
		rpf.setValue(act.getRpf());
		rlo.setValue(act.getRlo());
		rdr.setValue(act.getRdr());
		dia.setValue(act.getDia());
		net.setValue(act.getNet());
		por.setValue(act.getPor());
		res.setValue(act.getRes());
		
	}
	
	@UiHandler({"dis","ceu","loc","cap","tns","tss"})
	void onFieldClick(ClickEvent event) {
		this.callback.getActivity().setDis(dis.getValue());
		this.callback.getActivity().setCeu(ceu.getValue());
		this.callback.getActivity().setLoc(loc.getValue());
		this.callback.getActivity().setCap(cap.getValue());
		this.callback.getActivity().setTns(tns.getValue());
		this.callback.getActivity().setTss(tss.getValue());
		calculate();
	}
	
	@UiHandler({"com","tem","nue","veh","mun","emp","lor","bat","prc"
		,"value0","value1","value2","value3","value4","value5","value6"
		,"iin","dia"})

	void onFieldChange(ChangeEvent event) {
		this.callback.getActivity().setCom(com.getValue());
		this.callback.getActivity().setTem(tem.getValue());
		this.callback.getActivity().setNue(nue.getValue());
		this.callback.getActivity().setVeh(veh.getValue());
		this.callback.getActivity().setMun(mun.getValue());
		this.callback.getActivity().setEmp(emp.getValue());
		this.callback.getActivity().setLor(lor.getValue());
		this.callback.getActivity().setBat(bat.getValue());
		this.callback.getActivity().setPrc(prc.getValue());
		if  (this.callback.getActivity().getModules().size() > 0)
			this.callback.getActivity().getModules().get(0).setValue(value0.getValue());
		if  (this.callback.getActivity().getModules().size() > 1)
			this.callback.getActivity().getModules().get(1).setValue(value1.getValue());
		if  (this.callback.getActivity().getModules().size() > 2)
			this.callback.getActivity().getModules().get(2).setValue(value2.getValue());
		if  (this.callback.getActivity().getModules().size() > 3)
			this.callback.getActivity().getModules().get(3).setValue(value3.getValue());
		if  (this.callback.getActivity().getModules().size() > 4)
			this.callback.getActivity().getModules().get(4).setValue(value4.getValue());
		if  (this.callback.getActivity().getModules().size() > 5)
			this.callback.getActivity().getModules().get(5).setValue(value5.getValue());
		if  (this.callback.getActivity().getModules().size() > 6)
			this.callback.getActivity().getModules().get(6).setValue(value6.getValue());
		this.callback.getActivity().setIin(iin.getValue());
		this.callback.getActivity().setDia(dia.getValue());
		calculate();
	}

	private void calculate() {
		Model131.fiscalService.calculateMod131Activity(Model131.getCurrentDomainName(), Model131.getCurrentDomain()
				, this.callback.getActivity(), new AsyncCallback<Mod131Activity>() {
					
					@Override
					public void onSuccess(Mod131Activity result) {
						populateActivity(result);						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}
				});
	}
	

}
