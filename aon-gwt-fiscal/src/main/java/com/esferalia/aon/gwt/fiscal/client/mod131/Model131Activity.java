package com.esferalia.aon.gwt.fiscal.client.mod131;

import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod131ActivityModule;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model131Activity extends ScrollPanel {
	
//	@UiField TextBox epigraph;
//	@UiField TextBox description;
	@UiField DoubleBox com;
	@UiField IntegerBox tem;
	@UiField IntegerBox nue;
	@UiField CheckBox ceu;
	@UiField CheckBox loc;
	@UiField IntegerBox veh;
	@UiField CheckBox cap;
	@UiField IntegerBox mun;
	@UiField IntegerBox emp;
	@UiField IntegerBox lor;
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

	interface Model131ActivityBinder extends UiBinder<Widget, Model131Activity> {}
	private static final Model131ActivityBinder BINDER = 
			GWT.create(Model131ActivityBinder.class);

	public Model131Activity(final Mod131Activity act) {
		setWidth("700px");
		setHeight("498px");
		Widget ui = BINDER.createAndBindUi(this);
		populate(act);
		setWidget(ui);
	}

	private void populate(Mod131Activity act) {
		com.setValue(act.getCom());
		tem.setValue(act.getTem());
		nue.setValue(act.getNue());
		ceu.setValue(act.isCeu());
		loc.setValue(act.isLoc());
		veh.setValue(act.getVeh());
		cap.setValue(act.isCap());
		mun.setValue(act.getMun());
		emp.setValue(act.getEmp());
		lor.setValue(act.getLor());
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

}
