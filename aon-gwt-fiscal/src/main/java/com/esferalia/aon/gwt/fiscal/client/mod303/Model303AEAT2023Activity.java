package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.Arrays;
import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonGroupPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303AEAT2023SimplifiedRegimeActivities.IModel303AEATActivityCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod303Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod303ActivityDesk;
import com.esferalia.aon.occam.api.model.fiscal.Mod303ActivityModule;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

class Model303AEAT2023Activity extends DockLayoutPanel implements HasValueChangeHandlers<Mod303Activity> {

	private AonToolbar toolbar;
	private AonTableButton epigraphsButton;
	private AonToolbarButton removeButton; 
	private Label toolbarLabel; 

	final SimpleLayoutPanel contentContainer;
	final TabLayoutPanel tabLayoutPanel = new TabLayoutPanel(26, Unit.PX);
	
	private AonIntegerBox tem = new AonIntegerBox();
	private AonIntegerBox dia = new AonIntegerBox();
	private AonIntegerBox emp = new AonIntegerBox();
	private ListBox    lor = new ListBox();
	//private ListBox    cov = new ListBox();
	
	private InlineLabel staffLabel = new InlineLabel(); 
	private InlineLabel deskLabel = new InlineLabel(); 

	private class Model303AEAT2023ActivityModule {
		private final int index;
		private Label description = new Label();
		private AonDoubleBox value = new AonDoubleBox(6);
		private Label unit = new Label();
		private AonDoubleBox factor = new AonDoubleBox(6);
		private AonDoubleBox result = new AonDoubleBox(6);
		
		private Model303AEAT2023ActivityModule( int index) {
			this.index = index;
		}
	}
	
	Model303AEAT2023ActivityModule[] modules = new Model303AEAT2023ActivityModule[] {
			new Model303AEAT2023ActivityModule(0),
			new Model303AEAT2023ActivityModule(1),
			new Model303AEAT2023ActivityModule(2),
			new Model303AEAT2023ActivityModule(3),
			new Model303AEAT2023ActivityModule(4),
			new Model303AEAT2023ActivityModule(5),
			new Model303AEAT2023ActivityModule(6),
	};
	
	private class Model303AEAT2023ActivityDesk {
		private final int index;
		
		private AonIntegerBox capacity = new AonIntegerBox(4);
		private AonIntegerBox desk = new AonIntegerBox(6);
		private AonIntegerBox days = new AonIntegerBox(6);
		
		private Model303AEAT2023ActivityDesk( int index) {
			this.index = index;
		}
	}
	
	Model303AEAT2023ActivityDesk[] desks = new Model303AEAT2023ActivityDesk[] {
			new Model303AEAT2023ActivityDesk(0),
			new Model303AEAT2023ActivityDesk(1),
			new Model303AEAT2023ActivityDesk(2),
			new Model303AEAT2023ActivityDesk(3),
	};

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
	
	private AonDoubleBox may19Hours = new AonDoubleBox(); 		//Mayores de 19 años
	private AonDoubleBox men19Hours = new AonDoubleBox(); 		//Menores de 19 años y trabajadores con contratos de aprendizaje o formación, que no sean discapacitados.
	private AonDoubleBox disHours = new AonDoubleBox(); 		//Discapacitados con grado de minusvalía igual o superior al 33 por 100
	private AonDoubleBox yearHours = new AonDoubleBox(); 		//Horas anuales
	
	private AonDoubleBox ownerHours = new AonDoubleBox(); 		//Horas anuales del titular. (máximo 1.800 horas)
	private CheckBox ownerDis = new CheckBox(); 			//Indique si el titular es discapacitado en grado igual o superior al 33%
	private AonDoubleBox spouseHours = new AonDoubleBox(); 		//Horas anuales del cónyuge. (máximo 1.800 horas)
	private AonDoubleBox childMen18Hours = new AonDoubleBox(); 	//Horas anuales de los hijos menores de 18 años.



	protected Model303AEAT2023Activity(final IModel303AEATActivityCallback<Mod303Activity> callback) {
		super(Unit.PX);
		
		setStyleName(AON.CSS.aonSelector());
		addStyleName(AON.CSS.aonBackgroundLigthBlue());

		lor.addItem("-");
		lor.addItem("Act. realz exclusivamente en Lorca.");
		lor.addItem("Act. realz. Lorca y otros municipios.");
		lor.setWidth("140px");

//		cov.addItem("-");
//		cov.addItem("SI");
//		cov.setWidth("40px");

		addNorth(getToolbar( callback ), AonToolbar.HEIGTH);
		
		contentContainer = new SimpleLayoutPanel();
		add(contentContainer);
		
		if ( callback.getActivity().isNotEmpty() ) {
			contentContainer.setWidget(getActivityData( callback ));	
		} else {
			contentContainer.setWidget(getActivitySelectionPanel(callback));
		}
		
	}
	
	private Widget getActivityData(IModel303AEATActivityCallback<Mod303Activity> callback) {
		populate(callback.getActivity());		
		tabLayoutPanel.clear();
		tabLayoutPanel.add(getAdditionalDataPanel(callback), AON.MSG.additionalData());
		tabLayoutPanel.add(getModulesPanel(callback), AON.MSG.modules());
		if (staffModuleIndex(callback) != -1) {
			tabLayoutPanel.add(getModulesStaffDataPanel(callback), "Inf. M\u00F3dulo \"Personal\"");
		}
		if (deskModuleIndex(callback) != -1) {
			tabLayoutPanel.add(getModulesDeskDataPanel(callback), "Inf. M\u00F3dulo \"Mesas\"");
		}
		tabLayoutPanel.add(getResultPanel(callback), AON.MSG.result());
		
		tabLayoutPanel.addSelectionHandler( e -> {
			if ( ( AonNumberUtils.equals(e.getSelectedItem(),2) &&  staffModuleIndex(callback) == -1) 
			  || ( AonNumberUtils.equals(e.getSelectedItem(),3) &&  deskModuleIndex(callback) == -1 )) {
				AonMessageDialog.show("Aviso", "No procede");
				tabLayoutPanel.selectTab(1, false);
			}
		});

		tabLayoutPanel.selectTab(1, false);
		
		return tabLayoutPanel;
	}

	private AonToolbar getToolbar(IModel303AEATActivityCallback<Mod303Activity> callback) {
		toolbar = new AonToolbar( "" );

		removeButton = new AonToolbarButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
		removeButton.addClickHandler( event -> AonConfirmDialog.showConfirm(AON.MSG.confirmDeleteAction(), () -> callback.onRemove()));
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

	private String getTitle(Mod303Activity act) {
		return act.isNotEmpty()
			?(act.getEpigraph()+ " - " + AonStringUtils.abbreviate(act.getDescription(),80))
			:"Nueva actividad";
	}

	private Widget getActivitySelectionPanel(IModel303AEATActivityCallback<Mod303Activity> cbk) {
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonScrollArea());
		
		final Model303AEAT2023ActivitySelection activitySelection = new Model303AEAT2023ActivitySelection();
		activitySelection.addSelectionHandler( event -> checkAccept(cbk, event.getSelectedItem()));
		
		scroll.setWidget(activitySelection);
		return scroll;
	}

	private void checkAccept(IModel303AEATActivityCallback<Mod303Activity> callback, final Epigraph selected) {
		if (callback.getActivity().isNotEmpty()) {
			AonConfirmDialog.showConfirm(AON.MSG.epigrapChanged(), () -> accept(callback, selected));
		} else {
			accept(callback, selected);
		}
	}
	
	private void accept(IModel303AEATActivityCallback<Mod303Activity> callback, final Epigraph selected) {
		callback.getActivity().initialize();
		callback.getActivity().setEpigraph(selected.getEpigraph());
		callback.getActivity().setDescription(selected.getDescription());
		callback.getActivity().setPor(selected.getVatPorc());
		callback.getActivity().setMaxImport(selected.getLimExceso());
		callback.getActivity().setPcm(selected.getPorcMin());
		if (callback.getActivity().getDia() == 0) {
			@SuppressWarnings("deprecation")
			Date start = new Date( callback.getModel().getYear(),
					callback.getModel().getPeriod().getStartMonth(),
					1);
			@SuppressWarnings("deprecation")
			Date end = new Date( callback.getModel().getYear(),
					callback.getModel().getPeriod().getDueMonth(),
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
		contentContainer.setWidget(getActivityData( callback ));
		callback.onAccept();
	}

	public void populate(Mod303Activity act) {
		epigraphsButton.setVisible(act.isNotEmpty());
		removeButton.setVisible(act.isNotEmpty());
		toolbarLabel.setText(getTitle( act ));

		tem.setValue(act.getTem(),false,true);
		emp.setValue(act.getEmp(),false,true);
		dia.setValue(act.getDia(),false,true);
		lor.setSelectedIndex(act.getLor());
//		cov.setSelectedIndex(act.getCov());
		
		Arrays.stream(modules).forEach(m -> populateModule(act,m) );
		Arrays.stream(desks).forEach(d -> populateDesk(act,d) );
		
		dev.setValue(act.getDev(),false,true);
		red.setValue(act.getRed(),false,true);
		ind.setValue(act.getInd(),false,true);
		por.setValue(act.getPor(),false,true);
		ing.setValue(act.getIng(),false,true);
		
		sopx.setValue(act.getSopx(),false,true);
		sopy.setValue(act.getSopy(),false,true);
		sop.setValue(act.getSop(),false,true);
		ict.setValue(act.getIct(),false,true);
		res.setValue(act.getRes(),false,true);
		pcm.setValue(act.getPcm(),false,true);
		dvc.setValue(act.getDvc(),false,true);
		cmn.setValue(act.getCmn(),false,true);
		cad.setValue(act.getCad(),false,true);
		
		may19Hours.setValue(act.getMay19Hours(),false,true);
		men19Hours.setValue(act.getMen19Hours(),false,true);
		disHours.setValue(act.getDisHours(),false,true);
		yearHours.setValue(act.getYearHours(),false,true);
		ownerHours.setValue(act.getOwnerHours(),false,true);
		ownerDis.setValue(act.isOwnerDis(),false);
		spouseHours.setValue(act.getSpouseHours(),false,true);
		childMen18Hours.setValue(act.getChildMen18Hours(),false,true);
		
	}
	private void populateModule(Mod303Activity act, Model303AEAT2023ActivityModule m) {
		boolean filled = act.getModules().size() > m.index;
		Mod303ActivityModule mod = filled?act.getModules().get(m.index):new Mod303ActivityModule();
		m.description.setText(mod.getDescription());
		m.value.setValue(mod.getValue(),false,true);
		m.unit.setText(mod.getUnit());
		m.factor.setValue(mod.getFactor(),false,true);
		m.result.setValue(mod.getResult(),false,true);
		m.value.setEnabled(filled);
	}
	
	private void populateDesk(Mod303Activity act, Model303AEAT2023ActivityDesk d) {
		boolean filled = act.getModules().size() > d.index;
		Mod303ActivityDesk desk = filled?act.getDesks().get(d.index):new Mod303ActivityDesk();
		d.capacity.setValue(desk.getDeskCapacity());
		d.desk.setValue(desk.getDesks());
		d.days.setValue(desk.getDeskDays());
	}

	private Widget getAdditionalDataPanel(IModel303AEATActivityCallback<Mod303Activity> callback) {
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonScrollArea());
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginLeft());
		tab.addStyleName(AON.CSS.aonMarginBottom());

		tem.addValueChangeHandler(event -> {
			if (tem.getValue() == null) tem.setValue(0,false);
			callback.getActivity().setTem(tem.getValue());
			ValueChangeEvent.<Mod303Activity>fire(Model303AEAT2023Activity.this, callback.getActivity());
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.irpfActivityTem()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth600() )
			.addCell( tem);
		
		emp.addValueChangeHandler(event ->  {
			if (emp.getValue() == null) emp.setValue(0,false);
			callback.getActivity().setEmp(emp.getValue());
			ValueChangeEvent.<Mod303Activity>fire(Model303AEAT2023Activity.this, callback.getActivity());
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.irpfActivityEmp()), AON.CSS.aonBorderBottom() )
			.addCell( emp);

		lor.addChangeHandler(event-> {
			callback.getActivity().setLor(lor.getSelectedIndex());
			ValueChangeEvent.<Mod303Activity>fire(Model303AEAT2023Activity.this, callback.getActivity());
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.irpfActivityLor()), AON.CSS.aonBorderBottom() )
			.addCell( lor );

//		cov.addChangeHandler(event  -> {
//			callback.getActivity().setCov(cov.getSelectedIndex());
//			ValueChangeEvent.<Mod303Activity>fire(Model303AEAT2023Activity.this, callback.getActivity());
//		});
//		tab.addRow()
//			.addCell( new Label(AON.MSG.covidReduction()), AON.CSS.aonBorderBottom() )
//			.addCell( cov );

		dia.addValueChangeHandler(event -> {
			if (dia.getValue() == null) dia.setValue(0,false);
			callback.getActivity().setDia(dia.getValue());
			ValueChangeEvent.<Mod303Activity>fire(Model303AEAT2023Activity.this, callback.getActivity());
		});
		tab.addRow()
		.addCell( new Label(AON.MSG.irpfActivityDiaTrim()), AON.CSS.aonBorderBottom() )
		.addCell( dia );
		
		scroll.setWidget(tab);
		return scroll;
	}

	private Widget getModulesStaffDataPanel(IModel303AEATActivityCallback<Mod303Activity> callback) {
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel flowPanel = new FlowPanel();
		
		AonDisplayTable tab0 = new AonDisplayTable();
		tab0.addStyleName(AON.CSS.aonWidthAll());
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginLeft());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		
		tab.addRow()
			.addCell( new Label("Horas anuales"), AON.CSS.aonBold(), AON.CSS.aonWidthAuto() )
			.addCell( new Label("") , AON.CSS.aonWidth150());

		may19Hours.addValueChangeHandler(event -> {
			if (may19Hours.getValue() == null) may19Hours.setValue(0.0,false);
			callback.getActivity().setMay19Hours(may19Hours.getValue());
			calculateStaff( callback  );
			ValueChangeEvent.<Mod303Activity>fire(Model303AEAT2023Activity.this, callback.getActivity());
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.may19Hours()), AON.CSS.aonBorderBottom() )
			.addCell( may19Hours);
		
		men19Hours.addValueChangeHandler(event -> {
			if (men19Hours.getValue() == null) men19Hours.setValue(0.0,false);
			callback.getActivity().setMen19Hours(men19Hours.getValue());
			calculateStaff( callback  );
			ValueChangeEvent.<Mod303Activity>fire(Model303AEAT2023Activity.this, callback.getActivity());
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.men19Hours()), AON.CSS.aonBorderBottom() )
			.addCell( men19Hours);

		disHours.addValueChangeHandler(event -> {
			if (disHours.getValue() == null) disHours.setValue(0.0,false);
			callback.getActivity().setDisHours(disHours.getValue());
			calculateStaff( callback  );
			ValueChangeEvent.<Mod303Activity>fire(Model303AEAT2023Activity.this, callback.getActivity());
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.disHours()), AON.CSS.aonBorderBottom() )
			.addCell( disHours);

		yearHours.addValueChangeHandler(event -> {
			if (yearHours.getValue() == null) yearHours.setValue(0.0,false);
			callback.getActivity().setYearHours(yearHours.getValue());
			calculateStaff( callback  );
			ValueChangeEvent.<Mod303Activity>fire(Model303AEAT2023Activity.this, callback.getActivity());
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.yearHours()), AON.CSS.aonBorderBottom() )
			.addCell( yearHours);

		
		AonDisplayTable tab1 = new AonDisplayTable();
		tab1.addStyleName(AON.CSS.aonMarginLeft());
		tab1.addStyleName(AON.CSS.aonMarginBottom());
		
		tab1.addRow()
			.addCell( new Label("Horas anuales"), AON.CSS.aonBold(), AON.CSS.aonWidthAuto() )
			.addCell( new Label("") , AON.CSS.aonWidth150());
		ownerHours.addValueChangeHandler(event -> {
			if (ownerHours.getValue() == null) ownerHours.setValue(0.0,false);
			callback.getActivity().setOwnerHours(ownerHours.getValue());
			calculateStaff( callback  );
			ValueChangeEvent.<Mod303Activity>fire(Model303AEAT2023Activity.this, callback.getActivity());
		});
		tab1.addRow()
			.addCell( new Label(AON.MSG.ownerHours()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidthAuto() )
			.addCell( ownerHours, AON.CSS.aonWidth150());

		ownerDis.setText(AON.MSG.ownerDis());
		ownerDis.addClickHandler(event -> {
			if (ownerDis.getValue() == null) ownerDis.setValue(false,false);
			callback.getActivity().setOwnerDis(ownerDis.getValue());
			calculateStaff( callback  );
			ValueChangeEvent.<Mod303Activity>fire(Model303AEAT2023Activity.this, callback.getActivity());
		});
		tab1.addRow()
			.addCell( ownerDis, AON.CSS.aonBorderBottom() )
			.addCell( new Label() );

		spouseHours.addValueChangeHandler(event -> {
			if (spouseHours.getValue() == null) spouseHours.setValue(0.0,false);
			callback.getActivity().setSpouseHours(spouseHours.getValue());
			calculateStaff( callback  );
			ValueChangeEvent.<Mod303Activity>fire(Model303AEAT2023Activity.this, callback.getActivity());
		});
		tab1.addRow()
			.addCell( new Label(AON.MSG.spouseHours()), AON.CSS.aonBorderBottom() )
			.addCell( spouseHours);

		childMen18Hours.addValueChangeHandler(event -> {
			if (childMen18Hours.getValue() == null) childMen18Hours.setValue(0.0,false);
			callback.getActivity().setChildMen18Hours(childMen18Hours.getValue());
			calculateStaff( callback  );
			ValueChangeEvent.<Mod303Activity>fire(Model303AEAT2023Activity.this, callback.getActivity());
		});
		tab1.addRow()
			.addCell( new Label(AON.MSG.childMen18Hours()), AON.CSS.aonBorderBottom() )
			.addCell( childMen18Hours);

		tab0.addRow()
			.addCell(AonGroupPanel.get(AON.MSG.header1Table3(), tab),AON.CSS.aonWidthHalf())
			.addCell(AonGroupPanel.get(AON.MSG.noSalariedStaff(), tab1),AON.CSS.aonWidthHalf());
		
		flowPanel.add(tab0);
		
		FlowPanel footer = new FlowPanel();
		footer.setStyleName(AON.CSS.aonTextCenter());
		InlineLabel unitLabel = new InlineLabel("Unidades: ");
		unitLabel.setStyleName(AON.CSS.aonPaddingRight());
		
		staffLabel.setStyleName(AON.CSS.aonBold());
		
		footer.add(unitLabel);
		footer.add(staffLabel);
		flowPanel.add(footer);
		
		Label msg2 = new Label(AON.MSG.actMsg2());
		flowPanel.add(msg2);
		
		scroll.setWidget(flowPanel);
		return scroll;
	}

	private Widget getModulesDeskDataPanel(IModel303AEATActivityCallback<Mod303Activity> callback) {
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		
		FlowPanel tabContainer = new FlowPanel();
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginLeft());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		
		tab.addHeaderRow()
			.addCell( new Label("Capacidad"), AON.CSS.aonWidth100(), AON.CSS.aonTextRight())
			.addCell( new Label("Mesas"), AON.CSS.aonWidth100(), AON.CSS.aonTextRight())
			.addCellIf(callback.getModel().isLastPeriod(),new Label("D\u00EDas"), AON.CSS.aonWidth100(), AON.CSS.aonTextRight())
		;	
	
		Arrays.stream(desks).forEach(d -> paintDeskRow(callback,tab,d) );

		tabContainer.add(tab);
		container.add(tabContainer);
		
		
		FlowPanel footer = new FlowPanel();
		footer.setStyleName(AON.CSS.aonPaddingLeft());
		footer.addStyleName(AON.CSS.aonMarginTop());
		InlineLabel unitLabel = new InlineLabel("Unidades: ");
		unitLabel.setStyleName(AON.CSS.aonPaddingRight());
		
		deskLabel.setStyleName(AON.CSS.aonBold());
		
		footer.add(unitLabel);
		footer.add(deskLabel);
		container.add(footer);
		
		fillStaffLabel( callback );
		fillDeskLabel( callback );
		
		scroll.setWidget(container);
		return scroll;
	}
	
	private void paintDeskRow(IModel303AEATActivityCallback<Mod303Activity> callback, AonDisplayTable tab, Model303AEAT2023ActivityDesk d) {
		d.capacity.addValueChangeHandler(event -> {
			if (d.capacity.getValue() == null) d.capacity.setValue(0,false);
			callback.getActivity().getDesks().get(d.index).setDeskCapacity(d.capacity.getValue());
			calculateDesks(callback);
			ValueChangeEvent.<Mod303Activity>fire(Model303AEAT2023Activity.this, callback.getActivity());
		});
		d.desk.addValueChangeHandler(event -> {
			if (d.desk.getValue() == null) d.desk.setValue(0,false);
			callback.getActivity().getDesks().get(d.index).setDesks(d.desk.getValue());
			calculateDesks(callback);
			ValueChangeEvent.<Mod303Activity>fire(Model303AEAT2023Activity.this, callback.getActivity());
		});
		d.days.addValueChangeHandler(event -> {
			if (d.days.getValue() == null) d.days.setValue(0,false);
			callback.getActivity().getDesks().get(d.index).setDeskDays(d.days.getValue());
			calculateDesks(callback);
			ValueChangeEvent.<Mod303Activity>fire(Model303AEAT2023Activity.this, callback.getActivity());
		});

		tab.addRow()
			.addCell(d.capacity, AON.CSS.aonTextRight())
			.addCell(d.desk, AON.CSS.aonTextRight())
			.addCellIf(callback.getModel().isLastPeriod(),d.days, AON.CSS.aonTextRight())
			;
	}


	private Widget getModulesPanel(IModel303AEATActivityCallback<Mod303Activity> callback) {
		ScrollPanel scroll = new ScrollPanel();
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginLeft());
		tab.addStyleName(AON.CSS.aonMarginBottom());

		tab.addHeaderRow()
			.addCell( new Label(AON.MSG.description()), AON.CSS.aonWidthAuto())
			.addCell( new Label(AON.MSG.amount()), AON.CSS.aonWidth100(), AON.CSS.aonTextRight())
			.addCell( new Label(AON.MSG.unit()), AON.CSS.aonWidth150() )
			.addCell( new Label(AON.MSG.factor()), AON.CSS.aonWidth100(), AON.CSS.aonTextRight())
			.addCell( new Label(AON.MSG.result()), AON.CSS.aonWidth100(), AON.CSS.aonTextRight())
		;	
		
		Arrays.stream(modules).forEach(m -> paintModuleRow(callback,tab,m) );

		scroll.setWidget(tab);
		return scroll;
	}
	
	private void paintModuleRow(IModel303AEATActivityCallback<Mod303Activity> callback, AonDisplayTable tab, Model303AEAT2023ActivityModule m) {
		m.value.addValueChangeHandler(event -> {
			if (m.value.getValue() == null) m.value.setValue(0.0,false);
			callback.getActivity().getModules().get(m.index).setValue(m.value.getValue());
			ValueChangeEvent.<Mod303Activity>fire(Model303AEAT2023Activity.this, callback.getActivity());
		});
		m.factor.setReadOnly(true);
		m.result.setReadOnly(true);

		tab.addRow()
			.addCell(m.description)
			.addCell(m.value, AON.CSS.aonTextRight())
			.addCell(m.unit)
			.addCell(m.factor, AON.CSS.aonTextRight())
			.addCell(m.result, AON.CSS.aonTextRight())
			;
	}
	
	private Widget getResultPanel(IModel303AEATActivityCallback<Mod303Activity> callback) {
		ScrollPanel scroll = new ScrollPanel();
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginLeft());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		
		dev.setEnabled(false);
		tab.addRow()
			.addCell( new Label(AON.MSG.page6C()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
			.addCell( new AonBoxLabel("C") )
			.addCell( dev );

		
		red.setEnabled(callback.getActivity().getLor() != 1);
		red.addValueChangeHandler(event -> {
			if (red.getValue() == null) red.setValue(0.0,false);
			callback.getActivity().setRed(red.getValue());
			ValueChangeEvent.<Mod303Activity>fire(Model303AEAT2023Activity.this, callback.getActivity());
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.reductions()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
			.addCell( new AonBoxLabel("D") )
			.addCell( red );

		
		if (!callback.getModel().isLastPeriod()) {
			ind.setEnabled(false);
			tab.addRow()
				.addCell( new Label(AON.MSG.tempIndex()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell( new AonBoxLabel("Z") )
				.addCell( ind );

			por.setEnabled(false);
			tab.addRow()
				.addCell( new Label(AON.MSG.incomePercent()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell( new AonBoxLabel("E") )
				.addCell( por );

			ing.setEnabled(false);
			tab.addRow()
				.addCell( new Label(AON.MSG.income()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell( new AonBoxLabel("F") )
				.addCell( ing );
		} else {
			sopx.setEnabled(false);
			sopx.addStyleName(AON.CSS.aonMarginRight());
			tab.addRow()
				.addCell( new Label(AON.MSG.devQuota1()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell( new Label("") )
				.addCell( sopx );
			
			sopy.addStyleName(AON.CSS.aonMarginRight());
			sopy.addValueChangeHandler(event -> {
				if (sopy.getValue() == null) sopy.setValue(0.0,false);
				callback.getActivity().setSopy(sopy.getValue());
				ValueChangeEvent.<Mod303Activity>fire(Model303AEAT2023Activity.this, callback.getActivity());
			});
			tab.addRow()
				.addCell( new Label(AON.MSG.sopQuotaRest()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell( new Label("") )
				.addCell( sopy );
			
			sop.setEnabled(false);
			tab.addRow()
				.addCell( new Label(AON.MSG.page6D()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell( new AonBoxLabel("G") )
				.addCell( sop );
			
			ict.setEnabled(false);
			tab.addRow()
				.addCell( new Label(AON.MSG.tempIndex()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell( new AonBoxLabel("H") )
				.addCell( ict );

			res.setEnabled(false);
			tab.addRow()
				.addCell( new Label(AON.MSG.result()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell( new AonBoxLabel("I") )
				.addCell( res );
			
			pcm.setEnabled(false);
			tab.addRow()
				.addCell( new Label(AON.MSG.page6G()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell( new AonBoxLabel("J") )
				.addCell( pcm );

			dvc.addValueChangeHandler(event -> {
				if (dvc.getValue() == null) dvc.setValue(0.0,false);
				callback.getActivity().setDvc(dvc.getValue());
				ValueChangeEvent.<Mod303Activity>fire(Model303AEAT2023Activity.this, callback.getActivity());
			});
			tab.addRow()
				.addCell( new Label(AON.MSG.page6H()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell( new AonBoxLabel("K") )
				.addCell( dvc );
			
			cmn.setEnabled(false);
			tab.addRow()
				.addCell( new Label(AON.MSG.page6I()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell( new AonBoxLabel("L") )
				.addCell( cmn );

			cad.setEnabled(false);
			tab.addRow()
				.addCell( new Label(AON.MSG.yearSimplifiedQuota()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell( new AonBoxLabel("M") )
				.addCell( cad );
		}
		
		scroll.setWidget(tab);
		return scroll;

	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Mod303Activity> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}
	
	private void calculateStaff(IModel303AEATActivityCallback<Mod303Activity> callback ) {
		int staffIndex = staffModuleIndex(callback);
		if (staffIndex != -1) {
			if ( AonMathUtils.isZero( callback.getActivity().getYearHours() )) {
				callback.getActivity().setYearHours( 1800 );
			}
			double v0 = AonMathUtils.floor(callback.getActivity().getMay19Hours() / callback.getActivity().getYearHours());
			double v1 = AonMathUtils.floor(callback.getActivity().getMen19Hours() / callback.getActivity().getYearHours() * 0.60);
			double v2 = AonMathUtils.floor(callback.getActivity().getDisHours() / callback.getActivity().getYearHours() * 0.40);
			double v3 = AonMathUtils.floor(callback.getActivity().getOwnerHours() / callback.getActivity().getYearHours());
			if (callback.getActivity().isOwnerDis()) {
				v3 = AonMathUtils.floor(callback.getActivity().getOwnerHours() / callback.getActivity().getYearHours() * 0.75);	
			}
			double v4 = AonMathUtils.floor(callback.getActivity().getSpouseHours() / callback.getActivity().getYearHours() * 0.50);
			double v5 = AonMathUtils.floor(callback.getActivity().getChildMen18Hours() / callback.getActivity().getYearHours() * 0.50);
			double value = AonMathUtils.round(AonMathUtils.floor(v0 + v1 + v2 + v3 + v4 + v5 , 2));
			callback.getActivity().getModules().get( staffIndex ).setValue( value );
			fillStaffLabel( callback );
		} else {
			AonMessageDialog.show("Aviso", "No procede");
		}
	}
	
	private int staffModuleIndex( IModel303AEATActivityCallback<Mod303Activity> callback ) {
		for (int x = 0; x < callback.getActivity().getModules().size(); x++) {
			String desc = callback.getActivity().getModules().get(x).getDescription();
			boolean hasStaff =  AonStringUtils.equals(desc,ModuleInfo.M01.getDescription())
				|| AonStringUtils.equals(desc,ModuleInfo.M15.getDescription())
				|| AonStringUtils.equals(desc,ModuleInfo.M16.getDescription())
				|| AonStringUtils.equals(desc,ModuleInfo.M26.getDescription())
				|| AonStringUtils.equals(desc,ModuleInfo.M27.getDescription())
				|| AonStringUtils.equals(desc,ModuleInfo.M56.getDescription())
				|| AonStringUtils.equals(desc,ModuleInfo.M59.getDescription())
				|| AonStringUtils.equals(desc,ModuleInfo.M62.getDescription());
			if (hasStaff) return x;
		}
		return -1;
	}
	
	private void calculateDesks(IModel303AEATActivityCallback<Mod303Activity> callback ) {
		int deskIndex = deskModuleIndex(callback);
		if (deskIndex == -1) {
			AonMessageDialog.show("Aviso", "No procede");
		} else {
			double value = 0.0;
			for (Mod303ActivityDesk desk : callback.getActivity().getDesks()) {
				if ( callback.getModel().isLastPeriod()
					&& AonMathUtils.isZero(desk.getDeskDays())
					&& (AonMathUtils.isNotZero(desk.getDeskCapacity())
					 || AonMathUtils.isNotZero(desk.getDesks()) ) ) {
						desk.setDeskDays( 365 );
				}
				double daysFactor = (callback.getModel().isLastPeriod())
					?(desk.getDeskDays() / 365.0)
					:1.0;
				double factor = AonMathUtils.round(desk.getDeskCapacity() / 4.0);
				double v = (desk.getDesks() * factor * daysFactor);
				value = value + v;
			}
			value = AonMathUtils.round(value);
			callback.getActivity().getModules().get( deskIndex ).setValue( value );
			fillDeskLabel(callback);
		}
	}

	private int deskModuleIndex(IModel303AEATActivityCallback<Mod303Activity> callback ) {
		for (int x = 0; x < callback.getActivity().getModules().size(); x++) {
			String desc = callback.getActivity().getModules().get(x).getDescription();
			boolean hasDesk =  AonStringUtils.equals(desc,ModuleInfo.M04.getDescription())
				|| AonStringUtils.equals(desc,ModuleInfo.M51.getDescription());
			if (hasDesk) return x;
		}
		return -1;
	}

	private void fillDeskLabel(IModel303AEATActivityCallback<Mod303Activity> callback) {
		int deskIndex = deskModuleIndex(callback);
		if (deskIndex == -1) {
			deskLabel.setText("");
		} else {
			double v = callback.getActivity().getModules().get( deskIndex ).getValue();
			deskLabel.setText( AON.FMT.format(v) );
		}
		
	}

	private void fillStaffLabel(IModel303AEATActivityCallback<Mod303Activity> callback) {
		int staffIndex = staffModuleIndex(callback);
		if (staffIndex == -1) {
			staffLabel.setText("");
		} else {
			double v = callback.getActivity().getModules().get( staffIndex ).getValue();
			staffLabel.setText( AON.FMT.format(v) );
		}
		
	}
}
