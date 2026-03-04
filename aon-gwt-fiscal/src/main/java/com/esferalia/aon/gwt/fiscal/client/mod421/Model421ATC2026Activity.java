package com.esferalia.aon.gwt.fiscal.client.mod421;


import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.mod421.Model421ATC2026SimplifiedRegimeActivities.IModel421ATCActivityCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod421Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod421ActivityModule;
import com.esferalia.aon.occam.api.model.fiscal.modules.IEpigraphCanarias;
import com.esferalia.aon.occam.api.model.fiscal.modules.Module;
import com.esferalia.aon.occam.api.model.fiscal.modules.ModulesCanarias2026;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

class Model421ATC2026Activity extends DockLayoutPanel implements HasValueChangeHandlers<Mod421Activity> {

	private static final Logger LOGGER = Logger.getLogger(Model421ATC2026Activity.class.getName());	
	
	private AonToolbar toolbar;
	private AonTableButton epigraphsButton;
	private AonToolbarButton removeButton; 
	private Label toolbarLabel; 

	final SimpleLayoutPanel contentContainer;
	final TabLayoutPanel tabLayoutPanel = new TabLayoutPanel(26, Unit.PX);
	
	private AonIntegerBox tem = new AonIntegerBox(); // Actividades de temporada: Días ejercicio anterior
	private AonIntegerBox dia = new AonIntegerBox(); // Días del trimestre o días ejercicio en actividades de temporada

	private class Model421ATC2026ActivityModule {
		private final int index;
		private Label description = new Label();
		private AonDoubleBox value = new AonDoubleBox(6);
		private Label unit = new Label();
		private AonDoubleBox factor = new AonDoubleBox(6,4);
		private AonDoubleBox result = new AonDoubleBox(6);
		
		private Model421ATC2026ActivityModule(int index) {
			this.index = index;
		}
	}
	
	Model421ATC2026ActivityModule[] modules = new Model421ATC2026ActivityModule[] {
			new Model421ATC2026ActivityModule(0),
			new Model421ATC2026ActivityModule(1),
			new Model421ATC2026ActivityModule(2),
			new Model421ATC2026ActivityModule(3),
			new Model421ATC2026ActivityModule(4),
			new Model421ATC2026ActivityModule(5),
			new Model421ATC2026ActivityModule(6),
	};
	
	private AonDoubleBox dev = new AonDoubleBox();  // Cuota devengada operaciones corrientes
	private AonDoubleBox ict = new AonDoubleBox();  // Indice corrector actividades de temporada
	private AonDoubleBox por = new AonDoubleBox();  // Porcentaje de ingreso a cuenta (1T/2T/3T)
	private AonDoubleBox ing = new AonDoubleBox();  // Ingreso a cuenta (1T/2T/3T)
	private AonDoubleBox sop1 = new AonDoubleBox(); // 1% de la cuota devengada por operaciones corrientes (4T)
	private AonDoubleBox sopR = new AonDoubleBox(); // Resto de cuotas soportadas (4T)
	private AonDoubleBox sop = new AonDoubleBox();  // Total cuotas soportadas operaciones corrientes (4T)
	private AonDoubleBox res = new AonDoubleBox();  // RESULTADO (4T)
	private AonDoubleBox pcm = new AonDoubleBox();  // Porcentaje cuota mínima (4T)
	private AonDoubleBox cmn = new AonDoubleBox();  // Cuota mínima (4T)
	private AonDoubleBox cad = new AonDoubleBox();  // Cuota anual derivada régimen simplificado (4T)
	
	protected Model421ATC2026Activity(final IModel421ATCActivityCallback<Mod421Activity> callback) {
		super(Unit.PX);
		
		setStyleName(AON.CSS.aonSelector());
		addStyleName(AON.CSS.aonBackgroundLigthBlue());

		addNorth(getToolbar( callback ), AonToolbar.HEIGTH);
		
		contentContainer = new SimpleLayoutPanel();
		add(contentContainer);
		
		if ( callback.getActivity().isNotEmpty() ) {
			contentContainer.setWidget(getActivityData( callback ));	
		} else {
			contentContainer.setWidget(getActivitySelectionPanel(callback));
		}
		
	}
	
	private Widget getActivityData(IModel421ATCActivityCallback<Mod421Activity> callback) {
		populate(callback.getActivity(), callback.getModel().isEditable());		
		tabLayoutPanel.clear();
		tabLayoutPanel.add(getAdditionalDataPanel(callback), AON.MSG.additionalData());
		tabLayoutPanel.add(getModulesPanel(callback), AON.MSG.modules());
		tabLayoutPanel.add(getResultPanel(callback), AON.MSG.result());
		tabLayoutPanel.selectTab(1, false);
		return tabLayoutPanel;
	}

	private AonToolbar getToolbar(IModel421ATCActivityCallback<Mod421Activity> callback) {
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

		epigraphsButton.setVisible(callback.getActivity().isNotEmpty() && callback.getModel().isEditable());
		removeButton.setVisible(callback.getActivity().isNotEmpty() && callback.getModel().isEditable());

		return toolbar;
	}

	private String getTitle(Mod421Activity act) {
		LOGGER.info("Model421ATC2023Activity getTitle. selected: " + ModulesCanarias2026.EpigraphCanarias.getEpigraph(act.getEpigraph(), act.getSpecialEpigraph()));
		return act.isNotEmpty()
			? (act.getEpigraph() + " - " + AonStringUtils.abbreviate(act.getDescription(),80))
			: "Nueva actividad";
	}

	private Widget getActivitySelectionPanel(IModel421ATCActivityCallback<Mod421Activity> cbk) {
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonScrollArea());
		
		final Model421ATC2026ActivitySelection activitySelection = new Model421ATC2026ActivitySelection();
		activitySelection.addSelectionHandler( event -> checkAccept(cbk, event.getSelectedItem()));
		scroll.setWidget(activitySelection);
		
		return scroll;
	}

	private void checkAccept(IModel421ATCActivityCallback<Mod421Activity> callback, final IEpigraphCanarias selected) {
		if (callback.getActivity().isNotEmpty()) {
			AonConfirmDialog.showConfirm(AON.MSG.epigrapChanged(), () -> accept(callback, selected));
		} else {
			accept(callback, selected);
		}
	}
	
	private void accept(IModel421ATCActivityCallback<Mod421Activity> callback, final IEpigraphCanarias selected) {
		callback.getActivity().initialize();
		callback.getActivity().setEpigraph(selected.getEpigraph());
		callback.getActivity().setSpecialEpigraph(selected.getSpecialEpigraph());
		callback.getActivity().setDescription(selected.getDescription());
		callback.getActivity().setPor(selected.getPorcIng());
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
		for (Module mod : selected.getIgicModules()) {
			callback.getActivity().getModules().add(new Mod421ActivityModule()
				.setDescription(mod.getKey().getDescription())
				.setValue(0.0)
				.setUnit(mod.getUnit())
				.setFactor(mod.getAmount())
				.setResult(0.0)
				);
		}
		contentContainer.setWidget(getActivityData( callback ));
		callback.onAccept();
	}

	public void populate(Mod421Activity act, boolean isEditable) {
		epigraphsButton.setVisible(act.isNotEmpty() && isEditable);
		removeButton.setVisible(act.isNotEmpty() && isEditable);
		toolbarLabel.setText(getTitle( act ));

		tem.setValue(act.getTem(),false,true);
		dia.setValue(act.getDia(),false,true);
		
		Arrays.stream(modules).forEach(m -> populateModule(act,m) );
		
		dev.setValue(act.getDev(),false,true);
		ict.setValue(act.getIct(),false,true);
		por.setValue(act.getPor(),false,true);
		ing.setValue(act.getIng(),false,true);
		
		sop1.setValue(act.getSop1(),false,true);
		sopR.setValue(act.getSopR(),false,true);
		sop.setValue(act.getSop(),false,true);
		res.setValue(act.getRes(),false,true);
		pcm.setValue(act.getPcm(),false,true);
		cmn.setValue(act.getCmn(),false,true);
		cad.setValue(act.getCad(),false,true);
		
		// Controlar si el modelo es editable, para habilitar o no los campos editables
		tem.setEnabled(isEditable);
		dia.setEnabled(isEditable);
		sopR.setEnabled(isEditable);
		Arrays.stream(modules).forEach(m -> m.value.setEnabled(AonStringUtils.isNotBlank(m.description.getText()) && isEditable));
		
	}
	
	private void populateModule(Mod421Activity act, Model421ATC2026ActivityModule m) {
		boolean filled = act.getModules().size() > m.index;
		Mod421ActivityModule mod = filled ? act.getModules().get(m.index) : new Mod421ActivityModule();
		m.description.setText(mod.getDescription());
		m.value.setValue(mod.getValue(),false,true);
		m.unit.setText(mod.getUnit());
		m.factor.setValue(mod.getFactor(),false,true);
		m.result.setValue(mod.getResult(),false,true);
		m.value.setEnabled(filled);
	}
	
	private Widget getAdditionalDataPanel(IModel421ATCActivityCallback<Mod421Activity> callback) {
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonScrollArea());
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginLeft());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		
		String labelText = callback.getModel().isLastPeriod() ? "Actividad de Temporada: N\u00BA de d\u00EDas de ejercicio de la actividad" : "Actividad de Temporada: N\u00BA de d\u00EDas de ejercicio de la actividad en el a\u00F1o anterior";
		
		tem.addValueChangeHandler(event -> {
			if (tem.getValue() == null) tem.setValue(0,false);
			callback.getActivity().setTem(tem.getValue());
			ValueChangeEvent.<Mod421Activity>fire(Model421ATC2026Activity.this, callback.getActivity());
		});
		tab.addRow()
			.addCell( new Label(labelText), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth600() )
			.addCell( tem);

		dia.addValueChangeHandler(event -> {
			if (dia.getValue() == null) dia.setValue(0,false);
			callback.getActivity().setDia(dia.getValue());
			ValueChangeEvent.<Mod421Activity>fire(Model421ATC2026Activity.this, callback.getActivity());
		});
		tab.addRow()
		.addCell( new Label(AON.MSG.irpfActivityDiaTrim()), AON.CSS.aonBorderBottom() )
		.addCell( dia );
		
		scroll.setWidget(tab);
		return scroll;
	}
		
	private Widget getModulesPanel(IModel421ATCActivityCallback<Mod421Activity> callback) {
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
	
	private void paintModuleRow(IModel421ATCActivityCallback<Mod421Activity> callback, AonDisplayTable tab, Model421ATC2026ActivityModule m) {
		m.value.addValueChangeHandler(event -> {
			if (m.value.getValue() == null) m.value.setValue(0.0,false);
			callback.getActivity().getModules().get(m.index).setValue(m.value.getValue());
			ValueChangeEvent.<Mod421Activity>fire(Model421ATC2026Activity.this, callback.getActivity());
		});
		m.factor.setEnabled(false);
		m.result.setEnabled(false);		

		tab.addRow()
			.addCell(m.description)
			.addCell(m.value, AON.CSS.aonTextRight())
			.addCell(m.unit)
			.addCell(m.factor, AON.CSS.aonTextRight())
			.addCell(m.result, AON.CSS.aonTextRight())
			;
	}
	
	private Widget getResultPanel(IModel421ATCActivityCallback<Mod421Activity> callback) {
		ScrollPanel scroll = new ScrollPanel();
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginLeft());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		
		dev.setEnabled(false);
		tab.addRow()
			.addCell( new Label(AON.MSG.page6C()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
			.addCell( dev );
		
		if (!callback.getModel().isLastPeriod()) {
			
			// 1T/2T/3T
			
			ict.setEnabled(false);
			tab.addRow()
				.addCell( new Label(AON.MSG.tempIndex()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell( ict );

			por.setEnabled(false);
			tab.addRow()
				.addCell( new Label(AON.MSG.incomePercent()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell( por );

			ing.setEnabled(false);
			tab.addRow()
				.addCell( new Label(AON.MSG.income()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell( ing );
			
		} else {
			
			// 4T
			
			sop1.setEnabled(false);
			sop1.addStyleName(AON.CSS.aonMarginRight());
			tab.addRow()
				.addCell( new Label(AON.MSG.devQuota1()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell( sop1 );
			
			sopR.addStyleName(AON.CSS.aonMarginRight());
			sopR.addValueChangeHandler(event -> {
				if (sopR.getValue() == null) sopR.setValue(0.0,false);
				callback.getActivity().setSopR(sopR.getValue());
				ValueChangeEvent.<Mod421Activity>fire(Model421ATC2026Activity.this, callback.getActivity());
			});
			tab.addRow()
				.addCell( new Label(AON.MSG.sopQuotaRest()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell( sopR );
			
			sop.setEnabled(false);
			tab.addRow()
				.addCell( new Label("Total " + AON.MSG.page6D()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell( sop );
			
			ict.setEnabled(false);
			tab.addRow()
				.addCell( new Label(AON.MSG.tempIndex()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell( ict );

			res.setEnabled(false);
			tab.addRow()
				.addCell( new Label(AON.MSG.result()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell( res );
			
			pcm.setEnabled(false);
			tab.addRow()
				.addCell( new Label(AON.MSG.page6G()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell( pcm );

			cmn.setEnabled(false);
			tab.addRow()
				.addCell( new Label(AON.MSG.page6I()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell( cmn );

			cad.setEnabled(false);
			tab.addRow()
				.addCell( new Label(AON.MSG.yearSimplifiedQuota()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell( cad );
		}
		
		scroll.setWidget(tab);
		return scroll;

	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Mod421Activity> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}
	
    public static boolean isLeapYear(int year) {    	
        return ((year & 3) == 0) && ((year % 100) != 0 || (year % 400) == 0);
    }
	
}
