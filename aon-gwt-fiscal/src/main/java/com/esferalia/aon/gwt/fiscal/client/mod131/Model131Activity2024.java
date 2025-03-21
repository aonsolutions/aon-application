package com.esferalia.aon.gwt.fiscal.client.mod131;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.mod131.Model1312024Activities.IModel131ActivityCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod131ActivityModule;
import com.esferalia.aon.occam.api.model.fiscal.modules.IEpigraph;
import com.esferalia.aon.occam.api.model.fiscal.modules.Module;
import com.esferalia.aon.occam.api.model.fiscal.modules.ModuleInfo;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2018.Epigraph;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model131Activity2024 extends DockLayoutPanel implements HasValueChangeHandlers<Mod131Activity> {
	
	private static final Logger LOGGER = Logger.getLogger(Model131Activity2024.class.getName());
	private final Label epigraph = new Label();
	private final Label epigraphLabel = new Label();
	
	final SimpleLayoutPanel contentContainer;
	final TabLayoutPanel tabLayoutPanel = new TabLayoutPanel(26, Unit.PX);
	
	private CheckBox dis = new CheckBox();
	private AonDoubleBox com = new AonDoubleBox(6);
	private AonIntegerBox tem = new AonIntegerBox(6);
	private AonIntegerBox nue = new AonIntegerBox(6);
	private CheckBox ceu = new CheckBox();
	private CheckBox loc = new CheckBox();
	private AonIntegerBox veh = new AonIntegerBox(6);
	private CheckBox cap = new CheckBox();
	private CheckBox tns = new CheckBox(); 
	private CheckBox tss = new CheckBox();
	private ListBox mun = new ListBox();
	private AonIntegerBox emp = new AonIntegerBox(6);
	private ListBox lor = new ListBox();
	private ListBox pal = new ListBox();
	private ListBox dana = new ListBox();
	private ListBox bat = new ListBox();
	private AonDoubleBox prc = new AonDoubleBox(6);

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

	private AonDoubleBox[] values = new AonDoubleBox[] { value0, value1, value2, value3, value4, value5 };
	
	private Label description6 = new Label();
	private AonDoubleBox value6 = new AonDoubleBox(6);
	private Label unit6 = new Label();
	private AonDoubleBox factor6 = new AonDoubleBox(6);
	private AonDoubleBox result6 = new AonDoubleBox(6);

	private AonDoubleBox rnp0 = new AonDoubleBox(6);
	private AonDoubleBox rnp = new AonDoubleBox(6);
	private AonDoubleBox iem = new AonDoubleBox(6);
	private AonDoubleBox iin = new AonDoubleBox(6);
	private AonDoubleBox rnm = new AonDoubleBox(6);
	private AonDoubleBox ic1 = new AonDoubleBox(6);
	private AonDoubleBox ic2 = new AonDoubleBox(6);
	private AonDoubleBox ic3 = new AonDoubleBox(6);
	private AonDoubleBox ic4 = new AonDoubleBox(6);
	private AonDoubleBox ic5 = new AonDoubleBox(6);
	private AonDoubleBox rpf = new AonDoubleBox(6);
	private AonDoubleBox rlo = new AonDoubleBox(6);
	private AonDoubleBox rpa = new AonDoubleBox(6);
	private AonDoubleBox danaReduction = new AonDoubleBox(6);
	
	private AonDoubleBox rdr = new AonDoubleBox(6);
	private AonIntegerBox dia = new AonIntegerBox(6);
	private AonDoubleBox net = new AonDoubleBox(6);
	private AonDoubleBox por = new AonDoubleBox(6);
	private AonDoubleBox res = new AonDoubleBox(6);
	
	//Módulo Personal asalariado o Personal asalariado de fabricación
	private AonIntegerBox may19Hours = new AonIntegerBox(); 		//Mayores de 19 años
	private AonIntegerBox men19Hours = new AonIntegerBox(); 		//Menores de 19 años y trabajadores con contratos de aprendizaje o formación, que no sean discapacitados.
	private AonIntegerBox disHours = new AonIntegerBox(); 		//Discapacitados con grado de minusvalía igual o superior al 33 por 100
	private AonIntegerBox yearHours = new AonIntegerBox(); 		//Horas anuales
	
	//Módulo Resto personal asalariado
	private AonIntegerBox rsMay19Hours = new AonIntegerBox();//Horas anuales - Mayores de 19 años
	private AonIntegerBox rsMen19Hours = new AonIntegerBox();//Horas anuales - Menores de 19 años y trabajadores con contratos de aprendizaje o formación que no sean discapacitados
	private AonIntegerBox rsDisHours = new AonIntegerBox();	//Horas anuales - Discapacitados con grado de minusvalía igual o superior al 33 por 100
	private AonIntegerBox rsYearHours = new AonIntegerBox();	//Horas anuales - Horas anuales fijadas en el convenio colectivo vigente
	
	//Módulo Personal Empleado - Personal No Asalariado 
	private AonIntegerBox ownerHours = new AonIntegerBox(); 		//Horas anuales del titular. (máximo 1.800 horas)
	private AonIntegerBox spouseHours = new AonIntegerBox(); 		//Horas anuales del cónyuge. (máximo 1.800 horas)
	private AonIntegerBox childMen18Hours = new AonIntegerBox(); 	//Horas anuales de los hijos menores de 18 años.

	private AonIntegerBox desks1 = new AonIntegerBox();				// Mesas - Mesas
	private AonIntegerBox deskCapacity1 = new AonIntegerBox();		// Mesas - Capacidad
	private AonIntegerBox desks2 = new AonIntegerBox();				// Mesas - Mesas
	private AonIntegerBox deskCapacity2 = new AonIntegerBox();		// Mesas - Capacidad
	private AonIntegerBox desks3 = new AonIntegerBox();				// Mesas - Mesas
	private AonIntegerBox deskCapacity3 = new AonIntegerBox();		// Mesas - Capacidad
	private AonIntegerBox desks4 = new AonIntegerBox();				// Mesas - Mesas
	private AonIntegerBox deskCapacity4 = new AonIntegerBox();		// Mesas - Capacidad
	private AonToolbarButton removeButton;
	private AonTableButton epigraphsButton;
	private Label toolbarLabel;

	public Model131Activity2024(final IModel131ActivityCallback callback) {
		super(Unit.PX);
		setStyleName(AON.CSS.aonSelector());
		
		mun.addItem("Hasta 2.000 habitantes.");
		mun.addItem("Desde 2.001 hasta 5.000 habitantes.");
		mun.addItem("Desde 5.001 hasta 10.000 habitantes.");
		mun.addItem("Desde 10.001 hasta 50.000 habitantes.");
		mun.addItem("Desde 50.001 hasta 100.000 habitantes.");
		mun.addItem("M\u00E1s de 100.000 habitantes.");
		mun.addItem("Madrid o Barcelona.");
		mun.setWidth("260px");
		
		lor.addItem("-");
		lor.addItem("Actividad realizada exclusivamente en Lorca.");
		lor.addItem("Actividad realizada en Lorca y otros municipios.");
		lor.setWidth("300px");
		
		pal.addItem("-");
		pal.addItem("Actividad realizada exclusivamente en la isla de la Palma.");
		pal.addItem("Actividad realizada en la isla de la Palma y otros municipios.");
		pal.setWidth("370px");
		
		dana.addItem("-");
		dana.addItem("Exclusivamente en municipios afectados por la DANA");
		dana.addItem("En municipios afectados por la DANA y en otros municipios");
		dana.setWidth("370px");
		
		bat.addItem("-");
		bat.addItem("Una batea y ning\u00FAn barco");
		bat.addItem("Una batea y un barco de menos de 15 TRB");
		bat.addItem("Una batea y un barco de 15 a 30 TRB");
		bat.addItem("Una batea y un barco de m\u00E1s de 30 TRB");
		bat.addItem("Dos bateas y ning\u00FAn barco");
		bat.addItem("Dos bateas y un barco de menos de 15 TRB");
		bat.addItem("Otros: n\u00FAmero de bateas, barcos o TRB distintos de los anteriores");
		bat.setWidth("370px");

		addNorth(getToolbar( callback ), AonToolbar.HEIGTH);		

		contentContainer = new SimpleLayoutPanel();
		add(contentContainer);
		
		if ( callback.getActivity().isNotEmpty() ) {
			contentContainer.setWidget(getActivityData( callback ));	
		} else {
			contentContainer.setWidget(getActivitySelectionPanel(callback));
		}
	}
	
	private AonToolbar getToolbar(IModel131ActivityCallback callback) {
		AonToolbar toolbar = new AonToolbar( "" );

		removeButton = new AonToolbarButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
		removeButton.addClickHandler( event -> AonConfirmDialog.showConfirm(AON.MSG.confirmDeleteAction(), callback::onRemove));
		toolbar.add(removeButton);
		
		epigraphsButton = new AonTableButton("Cambiar actividad" ,AON.CSS.aonIconRefresh());
		epigraphsButton.addClickHandler(event ->  contentContainer.setWidget(getActivitySelectionPanel(callback)) );
		toolbar.add(epigraphsButton);

		toolbarLabel = new Label( getTitle( callback.getActivity() ) );
		toolbarLabel.setStyleName(AON.CSS.aonBold());
		toolbarLabel.addStyleName(AON.CSS.aonFontMedium());
		toolbarLabel.addStyleName(AON.CSS.aonPaddingLeft());
		
		toolbar.add(toolbarLabel);

		epigraphsButton.setVisible(callback.getActivity().isNotEmpty());
		removeButton.setVisible(callback.getActivity().isNotEmpty());

		return toolbar;
	}
	
	private String getTitle(Mod131Activity act) {
		return act.isNotEmpty()
			?(act.getEpigraph()+ " - " + AonStringUtils.abbreviate(act.getDescription(),80))
			:"Nueva actividad";
	}
	
	private Widget getActivitySelectionPanel(IModel131ActivityCallback cbk) {
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonScrollArea());
		
		final Model131ActivitySelection2024 activitySelection = new Model131ActivitySelection2024();
		activitySelection.addSelectionHandler( event -> checkAccept(cbk, event.getSelectedItem()));
		
		scroll.setWidget(activitySelection);
		return scroll;
	}
	
	private void checkAccept(IModel131ActivityCallback callback, final Epigraph selected) {
		if (callback.getActivity().isNotEmpty()) {
			AonConfirmDialog.showConfirm(AON.MSG.epigrapChanged(), () -> accept(callback, selected));
		} else {
			accept(callback, selected);
		}
	}
	
	private void accept(IModel131ActivityCallback callback, final IEpigraph selected) {
		callback.getActivity().initialize();
		callback.getActivity().setEpi(selected);
		callback.getActivity().setEpigraph(selected.getEpigraph());
		callback.getActivity().setDescription(selected.getDescription());
		callback.getActivity().setMaxImport(selected.getLimExceso());
		int i = 0;
		for (Module mod : selected.getIRPFModules()) {
			callback.getActivity().getModules().set(i,
				new Mod131ActivityModule()
					.setDescription(mod.getKey().getDescription())
					.setValue(0.0)
					.setUnit(mod.getUnit())
					.setFactor(mod.getAmount())
					.setResult(0.0)
					.setSalariedStaff(mod.isSalariedStaff())
					.setNoSalariedStaff(mod.isNoSalariedStaff()));
			i++;
		}
		contentContainer.setWidget(getActivityData( callback ));		
		calculate(callback);
	}

	private Widget getActivityData(IModel131ActivityCallback callback) {
		populate( callback.getActivity() );		
		tabLayoutPanel.clear();
		
		tabLayoutPanel.add(getAdditionalDataPanel(callback), AON.MSG.additionalData());
		// 1
		tabLayoutPanel.add(getModulesPanel(callback), AON.MSG.modules());

		if (staffModuleIndex(callback) != -1) {
			tabLayoutPanel.add(getModulesStaffDataPanel(callback), "Inf. M\u00F3dulo \"Personal asalariado\"");
		}
		if (rsStaffModuleIndex(callback) != -1) {
			tabLayoutPanel.add(getModulesRsStaffDataPanel(callback), "Inf. M\u00F3dulo \"Resto personal asalariado\"");
		}
		if (noStaffModuleIndex(callback) != -1) {
			tabLayoutPanel.add(getModulesNoStaffDataPanel(callback), "Inf. M\u00F3dulo \"Personal no asalariado\"");
		}
		if (deskModuleIndex(callback) != -1) {
			tabLayoutPanel.add(getModulesDeskDataPanel(callback), "Inf. M\u00F3dulo \"Mesas\"");
		}
		
		tabLayoutPanel.add(getResultPanel(callback), AON.MSG.result());
		
		tabLayoutPanel.selectTab(1, false);
		
		return tabLayoutPanel;
	}

	protected void populate(Mod131Activity act) {
		LOGGER.info("Model131Activity2024 populate");
		
		epigraphsButton.setVisible(act.isNotEmpty());
		removeButton.setVisible(act.isNotEmpty());
		toolbarLabel.setText(getTitle( act ));
		
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
		
		mun.setSelectedIndex(act.getMun());
		emp.setValue(act.getEmp());
		lor.setSelectedIndex(act.getLor());
		pal.setSelectedIndex(act.getPal());
		dana.setSelectedIndex(act.getDana());
		bat.setSelectedIndex(act.getBat());
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
		
		description6.setText(mod.getDescription());
		value6.setValue(mod.getValue());
		unit6.setText(mod.getUnit());
		factor6.setValue(mod.getFactor());
		result6.setValue(mod.getResult());
		
		rnp0.setValue(act.getRnp());
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
		rpa.setValue(act.getRpa());
		danaReduction.setValue(act.getDanaReduction());
		rdr.setValue(act.getRdr());
		dia.setValue(act.getDia());
		net.setValue(act.getNet());
		por.setValue(act.getPor());
		res.setValue(act.getRes());
		
		may19Hours.setValue(act.getMay19Hours());
		men19Hours.setValue(act.getMen19Hours());
		disHours.setValue(act.getDisHours());
		yearHours.setValue(act.getYearHours());
		
		rsMay19Hours.setValue(act.getRsMay19Hours());
		rsMen19Hours.setValue(act.getRsMen19Hours());
		rsDisHours.setValue(act.getRsDisHours());
		rsYearHours.setValue(act.getRsYearHours());

		ownerHours.setValue(act.getOwnerHours());
		spouseHours.setValue(act.getSpouseHours());
		childMen18Hours.setValue(act.getChildMen18Hours());

		desks1.setValue(act.getDesks1());
		deskCapacity1.setValue(act.getDeskCapacity1());
		desks2.setValue(act.getDesks2());
		deskCapacity2.setValue(act.getDeskCapacity2());
		desks3.setValue(act.getDesks3());
		deskCapacity3.setValue(act.getDeskCapacity3());
		desks4.setValue(act.getDesks4());
		deskCapacity4.setValue(act.getDeskCapacity4());

		enableFields(act);
	}
	
	private void enableFields(Mod131Activity act) {
		if (act.getEpi() == Epigraph.E____) {
			bat.setEnabled(true);
			loc.setEnabled(false);
			veh.setEnabled(false);
			cap.setEnabled(false);
			tns.setEnabled(false);
			tss.setEnabled(false);
			mun.setEnabled(false);
		} else if (act.getEpi() == Epigraph.E_722A 
			|| act.getEpi() == Epigraph.E_722B
			|| act.getEpi() == Epigraph.E_757
			) {
			bat.setEnabled(false);
			tss.setEnabled(true);
			tns.setEnabled(true);
		} else {
			bat.setEnabled(false);
			tss.setEnabled(false);
			tns.setEnabled(false);
		}
		cap.setEnabled( act.getVeh() > 0 );
		rlo.setEnabled( lor.getSelectedIndex() == 2 );
		rpa.setEnabled( pal.getSelectedIndex() == 2 );
		danaReduction.setEnabled( dana.getSelectedIndex() == 2 );
	}
	
	private Widget getAdditionalDataPanel(IModel131ActivityCallback callback) {
		ScrollPanel scroll = new ScrollPanel();
		AonDisplayTable table = new AonDisplayTable();
		table.addStyleName(AON.CSS.aonMarginLeft());
		table.addStyleName(AON.CSS.aonMarginRight());
		table.addStyleName(AON.CSS.aonMarginBottom());
		
		
		dis.addClickHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityDis()),AON.CSS.aonBold(),AON.CSS.aonWidth600() )
			.addCell(dis);
		
		com.addValueChangeHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityCom()),AON.CSS.aonBold() )
			.addCell(com);

		tem.addValueChangeHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityTem()),AON.CSS.aonBold() )
			.addCell(tem);

		nue.addValueChangeHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityNue()),AON.CSS.aonBold() )
			.addCell(nue);

		ceu.addValueChangeHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityCeu()),AON.CSS.aonBold() )
			.addCell(ceu);

		loc.addValueChangeHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityLoc()),AON.CSS.aonBold() )
			.addCell(loc);
		
		veh.addValueChangeHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityVeh()),AON.CSS.aonBold() )
			.addCell(veh);
		
		cap.addValueChangeHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityCap()),AON.CSS.aonBold() )
			.addCell(cap);
		
		tns.addClickHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityTns()),AON.CSS.aonBold() )
			.addCell(tns);
		
		tss.addClickHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityTss()),AON.CSS.aonBold() )
			.addCell(tss);
		
		mun.addChangeHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityMun()),AON.CSS.aonBold() )
			.addCell(mun);
		
		emp.addValueChangeHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityEmp()),AON.CSS.aonBold() )
			.addCell(emp);

		lor.setEnabled(callback.getActivity().getPal() != 1 && callback.getActivity().getDana() != 1);
		lor.addChangeHandler(event -> {
			callback.getActivity().setLor(lor.getSelectedIndex());
			if (callback.getActivity().getLor() == 1) {
				callback.getActivity().setPal(0);
				callback.getActivity().setDana(0);
				pal.setSelectedIndex(0);
				dana.setSelectedIndex(0);
				pal.setEnabled(false);
				dana.setEnabled(false);
			} else {
				pal.setEnabled(true);
				dana.setEnabled(true);
				callback.getActivity().setRlo(0.0);
			}
			onFieldChange(callback);
		});
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityLor()),AON.CSS.aonBold() )
			.addCell(lor);

		pal.setEnabled(callback.getActivity().getLor() != 1 && callback.getActivity().getDana() != 1);
		pal.addChangeHandler(event -> {
			callback.getActivity().setPal(pal.getSelectedIndex());
			if (callback.getActivity().getPal() == 1) {
				callback.getActivity().setLor(0);
				callback.getActivity().setDana(0);
				lor.setSelectedIndex(0);
				dana.setSelectedIndex(0);
				lor.setEnabled(false);
				dana.setEnabled(false);
			} else {
				lor.setEnabled(true);
				dana.setEnabled(true);
				callback.getActivity().setRpa(0.0);
			}
			onFieldChange(callback);	
		});
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityPal()),AON.CSS.aonBold() )
			.addCell(pal);
		
		// DANA solo a partir del ultimo trimestre de 2024
		if (callback.getModel().getYear() > 2024 || (callback.getModel().getYear() == 2024 && callback.getModel().isLastPeriod())) {
			dana.setEnabled(callback.getActivity().getLor() != 1 && callback.getActivity().getPal() != 1);
			dana.addChangeHandler(event -> {
				callback.getActivity().setDana(dana.getSelectedIndex());
				if (callback.getActivity().getDana() == 1) {
					callback.getActivity().setLor(0);
					callback.getActivity().setPal(0);
					lor.setSelectedIndex(0);
					pal.setSelectedIndex(0);
					lor.setEnabled(false);
					pal.setEnabled(false);
				} else {
					lor.setEnabled(true);
					pal.setEnabled(true);
					callback.getActivity().setDanaReduction(0.0);
				}
				onFieldChange(callback);	
			});
			table.addRow()
				.addCell(new Label("Actividad realizada en municipios afectados por la DANA 2024"),AON.CSS.aonBold() )
				.addCell(dana);
		}

		bat.addChangeHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityBat()),AON.CSS.aonBold() )
			.addCell(bat);
		
		prc.addValueChangeHandler(event -> onFieldChange(callback));
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityPrc()),AON.CSS.aonBold() )
			.addCell(prc);
		scroll.setWidget( table );
		return scroll;
	}

	private Widget getModulesPanel(IModel131ActivityCallback callback) {
		ScrollPanel scroll = new ScrollPanel();
		AonDisplayTable table = new AonDisplayTable();
		table.addStyleName(AON.CSS.aonMarginLeft());
		table.addStyleName(AON.CSS.aonMarginRight());
		table.addStyleName(AON.CSS.aonMarginBottom());
		
		table.addRow()
			.addCell(new Label(AON.MSG.description()), AON.CSS.aonDisplayGridHeaderCell(), AON.CSS.aonFlexGrow1())
			.addCell(new Label(AON.MSG.amount()), AON.CSS.aonDisplayGridHeaderCell())
			.addCell(new Label(AON.MSG.unit()), AON.CSS.aonDisplayGridHeaderCell())
			.addCell(new Label(AON.MSG.factor()), AON.CSS.aonDisplayGridHeaderCell())
			.addCell(new Label(AON.MSG.result()), AON.CSS.aonDisplayGridHeaderCell());

		factor0.setEnabled(false);
		result0.setEnabled(false);
		factor1.setEnabled(false);
		result1.setEnabled(false);
		factor2.setEnabled(false);
		result2.setEnabled(false);
		factor3.setEnabled(false);
		result3.setEnabled(false);
		factor4.setEnabled(false);
		result4.setEnabled(false);
		factor5.setEnabled(false);
		result5.setEnabled(false);
		factor6.setEnabled(false);
		result6.setEnabled(false);
		rnp0.setEnabled(false);
		
		value0.addValueChangeHandler(event -> onFieldChange(callback));
		value1.addValueChangeHandler(event -> onFieldChange(callback));
		value2.addValueChangeHandler(event -> onFieldChange(callback));
		value3.addValueChangeHandler(event -> onFieldChange(callback));
		value4.addValueChangeHandler(event -> onFieldChange(callback));
		value5.addValueChangeHandler(event -> onFieldChange(callback));
		value6.addValueChangeHandler(event -> onFieldChange(callback));
		
		table.addRow()
			.addCell(description0)
			.addCell(value0)
			.addCell(unit0)
			.addCell(factor0)
			.addCell(result0);

		table.addRow()
			.addCell(description1)
			.addCell(value1)
			.addCell(unit1)
			.addCell(factor1)
			.addCell(result1);
		
		table.addRow()
			.addCell(description2)
			.addCell(value2)
			.addCell(unit2)
			.addCell(factor2)
			.addCell(result2);
		
		table.addRow()
			.addCell(description3)
			.addCell(value3)
			.addCell(unit3)
			.addCell(factor3)
			.addCell(result3);

		table.addRow()
			.addCell(description4)
			.addCell(value4)
			.addCell(unit4)
			.addCell(factor4)
			.addCell(result4);

		table.addRow()
			.addCell(description5)
			.addCell(value5)
			.addCell(unit5)
			.addCell(factor5)
			.addCell(result5);


		table.addRow()
			.addCell(description6)
			.addCell(value6)
			.addCell(unit6)
			.addCell(factor6)
			.addCell(result6);
		
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityRnp()), AON.CSS.aonTableLabel())
			.addCell(new Label())
			.addCell(new Label())
			.addCell(new Label())
			.addCell(rnp0, AON.CSS.aonBold());

		scroll.setWidget( table );
		return scroll;
	}

	private Widget getModulesStaffDataPanel(IModel131ActivityCallback callback) {
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel flowPanel = new FlowPanel();
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginLeft());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		
		may19Hours.addValueChangeHandler(event -> {
			if (may19Hours.getValue() == null) may19Hours.setValue(0,false);
			callback.getActivity().setMay19Hours(may19Hours.getValue());
			calculateStaff( callback  );
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.may19Hours()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth300() )
			.addCell( may19Hours);
		
		men19Hours.addValueChangeHandler(event -> {
			if (men19Hours.getValue() == null) men19Hours.setValue(0,false);
			callback.getActivity().setMen19Hours(men19Hours.getValue());
			calculateStaff( callback  );
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.men19Hours()), AON.CSS.aonBorderBottom() )
			.addCell( men19Hours);

		disHours.addValueChangeHandler(event -> {
			if (disHours.getValue() == null) disHours.setValue(0,false);
			callback.getActivity().setDisHours(disHours.getValue());
			calculateStaff( callback  );
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.disHours()), AON.CSS.aonBorderBottom() )
			.addCell( disHours);

		yearHours.addValueChangeHandler(event -> {
			if (yearHours.getValue() == null) yearHours.setValue(0,false);
			callback.getActivity().setYearHours(yearHours.getValue());
			calculateStaff( callback  );
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.yearHours()), AON.CSS.aonBorderBottom() )
			.addCell( yearHours);
		flowPanel.add(tab);
		
		scroll.setWidget(flowPanel);
		return scroll;
	}
	
	private Widget getModulesRsStaffDataPanel(IModel131ActivityCallback callback) {
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel flowPanel = new FlowPanel();
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginLeft());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		
		rsMay19Hours.addValueChangeHandler(event -> {
			if (rsMay19Hours.getValue() == null) rsMay19Hours.setValue(0,false);
			callback.getActivity().setRsMay19Hours(rsMay19Hours.getValue());
			calculateRsStaff( callback );
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.may19Hours()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth300() )
			.addCell( rsMay19Hours);
		
		rsMen19Hours.addValueChangeHandler(event -> {
			if (rsMen19Hours.getValue() == null) rsMen19Hours.setValue(0,false);
			callback.getActivity().setRsMen19Hours(rsMen19Hours.getValue());
			calculateRsStaff( callback  );
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.men19Hours()), AON.CSS.aonBorderBottom() )
			.addCell( rsMen19Hours);

		rsDisHours.addValueChangeHandler(event -> {
			if (rsDisHours.getValue() == null) rsDisHours.setValue(0,false);
			callback.getActivity().setRsDisHours(rsDisHours.getValue());
			calculateRsStaff( callback  );
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.disHours()), AON.CSS.aonBorderBottom() )
			.addCell( rsDisHours);

		rsYearHours.addValueChangeHandler(event -> {
			if (rsYearHours.getValue() == null) rsYearHours.setValue(0,false);
			callback.getActivity().setRsYearHours(rsYearHours.getValue());
			calculateRsStaff( callback  );
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.yearHours()), AON.CSS.aonBorderBottom() )
			.addCell( rsYearHours);
		flowPanel.add(tab);
		
		scroll.setWidget(flowPanel);
		return scroll;
	}

	private Widget getModulesNoStaffDataPanel(IModel131ActivityCallback callback) {
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel flowPanel = new FlowPanel();
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginLeft());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		
		ownerHours.addValueChangeHandler(event -> {
			if (ownerHours.getValue() == null) ownerHours.setValue(0,false);
			callback.getActivity().setOwnerHours(ownerHours.getValue());
			calculateNoStaff( callback  );
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.ownerHours()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidthAuto() )
			.addCell( ownerHours, AON.CSS.aonWidth150());

		spouseHours.addValueChangeHandler(event -> {
			if (spouseHours.getValue() == null) spouseHours.setValue(0,false);
			callback.getActivity().setSpouseHours(spouseHours.getValue());
			calculateNoStaff( callback  );
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.spouseHours()), AON.CSS.aonBorderBottom() )
			.addCell( spouseHours);

		childMen18Hours.addValueChangeHandler(event -> {
			if (childMen18Hours.getValue() == null) childMen18Hours.setValue(0,false);
			callback.getActivity().setChildMen18Hours(childMen18Hours.getValue());
			calculateNoStaff( callback  );
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.childMen18Hours()), AON.CSS.aonBorderBottom() )
			.addCell( childMen18Hours);

		flowPanel.add(tab);
		scroll.setWidget(flowPanel);
		return scroll;
	}

	private void calculateStaff(IModel131ActivityCallback callback ) {
		int staffIndex = staffModuleIndex(callback);
		if (staffIndex != -1) {
			if ( AonMathUtils.isZero( callback.getActivity().getYearHours() )) {
				yearHours.setValue(1800 , false);
				callback.getActivity().setYearHours( 1800 );
			}
			double v0 = AonMathUtils.floor((double) callback.getActivity().getMay19Hours() / callback.getActivity().getYearHours());
			double v1 = AonMathUtils.floor(((double) callback.getActivity().getMen19Hours() / callback.getActivity().getYearHours()) * 0.60);
			double v2 = AonMathUtils.floor(((double) callback.getActivity().getDisHours() / callback.getActivity().getYearHours()) * 0.40);
			double value = AonMathUtils.round(AonMathUtils.floor(v0 + v1 + v2, 2));
			if (staffIndex >= 0 && staffIndex <= 5 )  {
				values[staffIndex].setValue(value, false);
				onFieldChange( callback );
			}
		} else {
			AonMessageDialog.show("Aviso", "No procede");
		}
	}
	
	private void calculateRsStaff(IModel131ActivityCallback callback ) {
		int rsStaffIndex = rsStaffModuleIndex(callback);
		if (rsStaffIndex != -1) {
			if ( AonMathUtils.isZero( callback.getActivity().getRsYearHours() )) {
				rsYearHours.setValue(1800 , false);
				callback.getActivity().setRsYearHours( 1800 );
			}
			double v0 = AonMathUtils.floor(callback.getActivity().getRsMay19Hours() / callback.getActivity().getRsYearHours());
			double v1 = AonMathUtils.floor(((double) callback.getActivity().getRsMen19Hours() / callback.getActivity().getRsYearHours()) * 0.60);
			double v2 = AonMathUtils.floor(((double) callback.getActivity().getRsDisHours() / callback.getActivity().getRsYearHours()) * 0.40);
			double value = AonMathUtils.round(AonMathUtils.floor(v0 + v1 + v2, 2));
			if (rsStaffIndex >= 0 && rsStaffIndex <= 5 )  {
				values[rsStaffIndex].setValue(value, false);
				onFieldChange( callback );
			}
		} else {
			AonMessageDialog.show("Aviso", "No procede");
		}
	}
	
	private void calculateNoStaff(IModel131ActivityCallback callback ) {
		int noStaffIndex = noStaffModuleIndex(callback);
		if (noStaffIndex != -1) {
			int yh = 1800;
			double v3 = AonMathUtils.floor(callback.getActivity().getOwnerHours() / yh);
			double v4 = AonMathUtils.floor((((double) callback.getActivity().getSpouseHours() / yh) * 0.50));
			double v5 = AonMathUtils.floor((((double) callback.getActivity().getChildMen18Hours() / yh) * 0.50));
			double value = AonMathUtils.round(AonMathUtils.floor(v3 + v4 + v5 , 2));
			if (noStaffIndex >= 0 && noStaffIndex <= 5 )  {
				values[noStaffIndex].setValue(value, false);
				onFieldChange( callback );
			}
		} else {
			AonMessageDialog.show("Aviso", "No procede");
		}
	}
	

	private int staffModuleIndex( IModel131ActivityCallback callback ) {
		for (int x = 0; x < callback.getActivity().getModules().size(); x++) {
			String desc = callback.getActivity().getModules().get(x).getDescription();
			boolean hasStaff =  AonStringUtils.equals(desc,ModuleInfo.M01.getDescription())
				|| AonStringUtils.equals(desc,ModuleInfo.M15.getDescription())
				|| AonStringUtils.equals(desc,ModuleInfo.M26.getDescription())
				|| AonStringUtils.equals(desc,ModuleInfo.M27.getDescription())
				|| AonStringUtils.equals(desc,ModuleInfo.M56.getDescription())
				|| AonStringUtils.equals(desc,ModuleInfo.M59.getDescription())
				|| AonStringUtils.equals(desc,ModuleInfo.M62.getDescription());
			if (hasStaff) return x;
		}
		return -1;
	}
	
	private int rsStaffModuleIndex( IModel131ActivityCallback callback ) {
		for (int x = 0; x < callback.getActivity().getModules().size(); x++) {
			String desc = callback.getActivity().getModules().get(x).getDescription();
			boolean hasStaff =  AonStringUtils.equals(desc,ModuleInfo.M16.getDescription());
			if (hasStaff) return x;
		}
		return -1;
	}

	private int noStaffModuleIndex( IModel131ActivityCallback callback ) {
		for (int x = 0; x < callback.getActivity().getModules().size(); x++) {
			String desc = callback.getActivity().getModules().get(x).getDescription();
			boolean hasStaff =  AonStringUtils.equals(desc,ModuleInfo.M02.getDescription());
			if (hasStaff) return x;
		}
		return -1;
	}

	private void onFieldChange( IModel131ActivityCallback callback ) {
		com.setValue(AonNumberUtils.zeroIfNull(com.getValue()),false);
		tem.setValue(AonNumberUtils.zeroIfNull(tem.getValue()),false);
		if (tem.getValue() < 0) tem.setValue(0,false);
		if (tem.getValue() > 180) tem.setValue(180,false);
		nue.setValue(AonNumberUtils.zeroIfNull(nue.getValue()),false);
		veh.setValue(AonNumberUtils.zeroIfNull(veh.getValue()),false);
		emp.setValue(AonNumberUtils.zeroIfNull(emp.getValue()),false);
		prc.setValue(AonNumberUtils.zeroIfNull(prc.getValue()),false);
		value0.setValue(AonNumberUtils.zeroIfNull(value0.getValue()),false);
		value1.setValue(AonNumberUtils.zeroIfNull(value1.getValue()),false);
		value2.setValue(AonNumberUtils.zeroIfNull(value2.getValue()),false);
		value3.setValue(AonNumberUtils.zeroIfNull(value3.getValue()),false);
		value4.setValue(AonNumberUtils.zeroIfNull(value4.getValue()),false);
		value5.setValue(AonNumberUtils.zeroIfNull(value5.getValue()),false);
		value6.setValue(AonNumberUtils.zeroIfNull(value6.getValue()),false);
		iin.setValue(AonNumberUtils.zeroIfNull(iin.getValue()),false);
		dia.setValue(AonNumberUtils.zeroIfNull(dia.getValue()),false);
		rlo.setValue(AonNumberUtils.zeroIfNull(rlo.getValue()),false);
		rpa.setValue(AonNumberUtils.zeroIfNull(rpa.getValue()),false);
		danaReduction.setValue(AonNumberUtils.zeroIfNull(danaReduction.getValue()),false);
		callback.getActivity().setCom(com.getValue());
		callback.getActivity().setTem(tem.getValue());
		callback.getActivity().setNue(nue.getValue());
		callback.getActivity().setVeh(veh.getValue());
		callback.getActivity().setEmp(emp.getValue());
		callback.getActivity().setMun(mun.getSelectedIndex());
		callback.getActivity().setLor(lor.getSelectedIndex());
		callback.getActivity().setPal(pal.getSelectedIndex());
		callback.getActivity().setDana(dana.getSelectedIndex());
		callback.getActivity().setBat(bat.getSelectedIndex());
		callback.getActivity().setPrc(prc.getValue());
		callback.getActivity().setDis(dis.getValue());
		callback.getActivity().setCeu(ceu.getValue());
		callback.getActivity().setLoc(loc.getValue());
		callback.getActivity().setCap(cap.getValue());
		callback.getActivity().setTns(tns.getValue());
		callback.getActivity().setTss(tss.getValue());
		if  (!callback.getActivity().getModules().isEmpty())
			callback.getActivity().getModules().get(0).setValue(value0.getValue());
		if  (callback.getActivity().getModules().size() > 1)
			callback.getActivity().getModules().get(1).setValue(value1.getValue());
		if  (callback.getActivity().getModules().size() > 2)
			callback.getActivity().getModules().get(2).setValue(value2.getValue());
		if  (callback.getActivity().getModules().size() > 3)
			callback.getActivity().getModules().get(3).setValue(value3.getValue());
		if  (callback.getActivity().getModules().size() > 4)
			callback.getActivity().getModules().get(4).setValue(value4.getValue());
		if  (callback.getActivity().getModules().size() > 5)
			callback.getActivity().getModules().get(5).setValue(value5.getValue());
		if  (callback.getActivity().getModules().size() > 6)
			callback.getActivity().getModules().get(6).setValue(value6.getValue());
		callback.getActivity().setIin(iin.getValue());
		callback.getActivity().setDia(dia.getValue());
		
		callback.getActivity().setMay19Hours(may19Hours.getValue()); 
		callback.getActivity().setMen19Hours(men19Hours.getValue());
		callback.getActivity().setDisHours(disHours.getValue());
		callback.getActivity().setYearHours(yearHours.getValue());
		callback.getActivity().setRsMay19Hours(rsMay19Hours.getValue());
		callback.getActivity().setRsMen19Hours(rsMen19Hours.getValue());
		callback.getActivity().setRsDisHours(rsDisHours.getValue());
		callback.getActivity().setRsYearHours(rsYearHours.getValue());
		callback.getActivity().setOwnerHours(ownerHours.getValue());
		callback.getActivity().setSpouseHours(spouseHours.getValue());
		callback.getActivity().setChildMen18Hours(childMen18Hours.getValue());
		callback.getActivity().setDesks1(desks1.getValue());
		callback.getActivity().setDeskCapacity1(deskCapacity1.getValue());
		callback.getActivity().setDesks2(desks2.getValue());
		callback.getActivity().setDeskCapacity2(deskCapacity2.getValue());
		callback.getActivity().setDesks3(desks3.getValue());
		callback.getActivity().setDeskCapacity3(deskCapacity3.getValue());
		callback.getActivity().setDesks4(desks4.getValue());
		callback.getActivity().setDeskCapacity4(deskCapacity4.getValue());
		
		calculate( callback );
	}

	private Widget getResultPanel(IModel131ActivityCallback callback) {
		ScrollPanel scroll = new ScrollPanel();
		AonDisplayTable table = new AonDisplayTable();
		table.addStyleName(AON.CSS.aonMarginLeft());
		table.addStyleName(AON.CSS.aonMarginRight());
		table.addStyleName(AON.CSS.aonMarginBottom());

		rnp.setEnabled(false);
		iem.setEnabled(false);
		rnm.setEnabled(false);
		ic1.setEnabled(false);
		ic2.setEnabled(false);
		ic3.setEnabled(false);
		ic4.setEnabled(false);
		ic5.setEnabled(false);
		rpf.setEnabled(false);
		rdr.setEnabled(false);
		net.setEnabled(false);
		por.setEnabled(false);
		res.setEnabled(false);
		
		iin.addValueChangeHandler(event -> onFieldChange(callback));
		dia.addValueChangeHandler(event -> onFieldChange(callback));
		rlo.addValueChangeHandler(event -> {
			if (rlo.getValue() == null) 
				rlo.setValue(0.0,false);
			callback.getActivity().setRlo(rlo.getValue());
			onFieldChange(callback);
		});
		rpa.addValueChangeHandler(event -> {
			if (rpa.getValue() == null) 
				rpa.setValue(0.0,false);
			callback.getActivity().setRpa(rpa.getValue());
			onFieldChange(callback);
		});
		
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityRnp()),AON.CSS.aonBold(),AON.CSS.aonWidth600() )
			.addCell(rnp);
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityIem()),AON.CSS.aonBold())
			.addCell(iem);
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityIin()),AON.CSS.aonBold())
			.addCell(iin);
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityRnm()),AON.CSS.aonBold())
			.addCell(rnm);
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityIc1()),AON.CSS.aonItalic(),AON.CSS.aonPaddingLeft())
			.addCell(ic1);
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityIc2()),AON.CSS.aonItalic(),AON.CSS.aonPaddingLeft())
			.addCell(ic2);
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityIc3()),AON.CSS.aonItalic(),AON.CSS.aonPaddingLeft())
			.addCell(ic3);
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityIc4()),AON.CSS.aonItalic(),AON.CSS.aonPaddingLeft())
			.addCell(ic4);
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityIc5()),AON.CSS.aonItalic(),AON.CSS.aonPaddingLeft())
			.addCell(ic5);
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityRpf()),AON.CSS.aonBold())
			.addCell(rpf);
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityRlo()),AON.CSS.aonBold())
			.addCell(rlo);
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityRpa()),AON.CSS.aonBold())
			.addCell(rpa);
		
		// DANA solo a partir del ultimo trimestre de 2024
		if (callback.getModel().getYear() > 2024 || (callback.getModel().getYear() == 2024 && callback.getModel().isLastPeriod())) {
			danaReduction.addValueChangeHandler(event -> {
				if (danaReduction.getValue() == null) 
					danaReduction.setValue(0.0,false);
				callback.getActivity().setDanaReduction(danaReduction.getValue());
				onFieldChange(callback);	
			});
			table.addRow()
				.addCell(new Label("Reducci\u00F3n por actividad realizada en municipios afectados por la DANA"),AON.CSS.aonBold())
				.addCell(danaReduction);
		}
		
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityRdr()),AON.CSS.aonBold())
			.addCell(rdr);
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityDia()),AON.CSS.aonBold())
			.addCell(dia);
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityNet()),AON.CSS.aonBold())
			.addCell(net);
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityPor()),AON.CSS.aonBold())
			.addCell(por);
		table.addRow()
			.addCell(new Label(AON.MSG.irpfActivityRes()),AON.CSS.aonBold())
			.addCell(res);
		scroll.setWidget(table);
		return scroll;
	}

	private Widget getModulesDeskDataPanel(IModel131ActivityCallback callback) {
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		
		FlowPanel tabContainer = new FlowPanel();
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginLeft());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		
		tab.addHeaderRow()
			.addCell( new Label("Capacidad"), AON.CSS.aonWidth100(), AON.CSS.aonTextRight())
			.addCell( new Label("N\u00BA Mesas"), AON.CSS.aonWidth100(), AON.CSS.aonTextRight())
		;	
	
		deskCapacity1.addValueChangeHandler(event -> {
			if (deskCapacity1.getValue() == null) deskCapacity1.setValue(0,false);
			callback.getActivity().setDeskCapacity1(deskCapacity1.getValue());
			calculateDesks(callback);
		});
		desks1.addValueChangeHandler(event -> {
			if (desks1.getValue() == null) desks1.setValue(0,false);
			callback.getActivity().setDesks1(desks1.getValue());
			calculateDesks(callback);
		});

		tab.addRow()
			.addCell(deskCapacity1, AON.CSS.aonTextRight())
			.addCell(desks1, AON.CSS.aonTextRight())
		;

		deskCapacity2.addValueChangeHandler(event -> {
			if (deskCapacity2.getValue() == null) deskCapacity2.setValue(0,false);
			callback.getActivity().setDeskCapacity2(deskCapacity2.getValue());
			calculateDesks(callback);
		});
		desks2.addValueChangeHandler(event -> {
			if (desks2.getValue() == null) desks2.setValue(0,false);
			callback.getActivity().setDesks2(desks2.getValue());
			calculateDesks(callback);
		});

		tab.addRow()
			.addCell(deskCapacity2, AON.CSS.aonTextRight())
			.addCell(desks2, AON.CSS.aonTextRight())
			;

		deskCapacity3.addValueChangeHandler(event -> {
			if (deskCapacity3.getValue() == null) deskCapacity3.setValue(0,false);
			callback.getActivity().setDeskCapacity3(deskCapacity3.getValue());
			calculateDesks(callback);
		});
		desks3.addValueChangeHandler(event -> {
			if (desks3.getValue() == null) desks3.setValue(0,false);
			callback.getActivity().setDesks3(desks3.getValue());
			calculateDesks(callback);
		});

		tab.addRow()
			.addCell(deskCapacity3, AON.CSS.aonTextRight())
			.addCell(desks3, AON.CSS.aonTextRight())
			;

		deskCapacity4.addValueChangeHandler(event -> {
			if (deskCapacity4.getValue() == null) deskCapacity4.setValue(0,false);
			callback.getActivity().setDeskCapacity4(deskCapacity4.getValue());
			calculateDesks(callback);
		});
		desks4.addValueChangeHandler(event -> {
			if (desks4.getValue() == null) desks4.setValue(0,false);
			callback.getActivity().setDesks4(desks4.getValue());
			calculateDesks(callback);
		});

		tab.addRow()
			.addCell(deskCapacity4, AON.CSS.aonTextRight())
			.addCell(desks4, AON.CSS.aonTextRight())
			;

		tabContainer.add(tab);
		container.add(tabContainer);
		
		scroll.setWidget(container);
		return scroll;
	}
	
	private int deskModuleIndex(IModel131ActivityCallback callback ) {
		for (int x = 0; x < callback.getActivity().getModules().size(); x++) {
			String desc = callback.getActivity().getModules().get(x).getDescription();
			boolean hasDesk =  AonStringUtils.equals(desc,ModuleInfo.M04.getDescription())
				|| AonStringUtils.equals(desc,ModuleInfo.M51.getDescription());
			if (hasDesk) return x;
		}
		return -1;
	}
		
	private void calculateDesks(IModel131ActivityCallback callback ) {
		int deskIndex = deskModuleIndex(callback);
		if (deskIndex == -1) {
			AonMessageDialog.show("Aviso", "No procede");
		} else {
			double dfactor1 = AonMathUtils.round(callback.getActivity().getDeskCapacity1() / 4.0);
			double v1 = (callback.getActivity().getDesks1() * dfactor1);
				
			double dfactor2 = AonMathUtils.round(callback.getActivity().getDeskCapacity2() / 4.0);
			double v2 = (callback.getActivity().getDesks2() * dfactor2);

			double dfactor3 = AonMathUtils.round(callback.getActivity().getDeskCapacity3() / 4.0);
			double v3 = (callback.getActivity().getDesks3() * dfactor3);

			double dfactor4 = AonMathUtils.round(callback.getActivity().getDeskCapacity4() / 4.0);
			double v4 = (callback.getActivity().getDesks4() * dfactor4);

			double value = AonMathUtils.round(v1 + v2 + v3 + v4);
			if (deskIndex >= 0 && deskIndex <= 5 )  {
				values[deskIndex].setValue(value, false);
				onFieldChange( callback );
			}
		}
	}
	
	private void calculate(IModel131ActivityCallback callback) {
		Model131.SERVICE.calculateActivity( 
			callback.getOptions().getOccam(), 
			callback.getModel(), callback.getActivity(), new AsyncCallback<Mod131Activity>() {
				
				@Override
				public void onSuccess(Mod131Activity result) {
					for (int i = 0 ; i < callback.getActivity().getModules().size(); i++) {
						callback.getActivity().getModules().get(i).setResult(result.getModules().get(i).getResult());	
					}
					callback.getActivity().setPrc(result.getPrc());
					callback.getActivity().setRnp(result.getRnp());
					callback.getActivity().setIem(result.getIem());
					callback.getActivity().setRnm(result.getRnm());
					callback.getActivity().setIc1(result.getIc1());
					callback.getActivity().setIc2(result.getIc2());
					callback.getActivity().setIc3(result.getIc3());
					callback.getActivity().setIc4(result.getIc4());
					callback.getActivity().setIc5(result.getIc5());
					callback.getActivity().setRpf(result.getRpf());
					callback.getActivity().setRlo(result.getRlo());
					callback.getActivity().setRpa(result.getRpa());
					callback.getActivity().setDanaReduction(result.getDanaReduction());
					callback.getActivity().setRdr(result.getRdr());
					callback.getActivity().setNet(result.getNet());
					callback.getActivity().setPor(result.getPor());
					callback.getActivity().setRes(result.getRes());
					populate(callback.getActivity());
					ValueChangeEvent.<Mod131Activity>fire(Model131Activity2024.this, callback.getActivity());
				}
				
				@Override
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
				}
			});
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Mod131Activity> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

}
