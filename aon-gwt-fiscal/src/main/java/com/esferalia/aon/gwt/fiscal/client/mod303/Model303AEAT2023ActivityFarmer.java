package com.esferalia.aon.gwt.fiscal.client.mod303;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303AEAT2023SimplifiedRegimeActivities.IModel303AEATActivityCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod303ActivityFarmer;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2018.FarmerIVA;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class Model303AEAT2023ActivityFarmer extends DockLayoutPanel implements HasValueChangeHandlers<Mod303ActivityFarmer> {
	
	private SimpleLayoutPanel contentContainer;
	private AonToolbar toolbar;
	private AonTableButton epigraphsButton;
	private AonToolbarButton removeButton; 
	private Label toolbarLabel; 
	
	private AonDoubleBox vol = new AonDoubleBox();
	private AonDoubleBox ind = new AonDoubleBox(AonDoubleBox.VISIBLE_LENGTH,5);
	private AonDoubleBox cuo = new AonDoubleBox();
	private AonDoubleBox por = new AonDoubleBox();
	private AonDoubleBox ing = new AonDoubleBox();
	
	private AonDoubleBox com = new AonDoubleBox();
	private AonDoubleBox dev = new AonDoubleBox();
	private AonDoubleBox tso = new AonDoubleBox();
	private AonDoubleBox sop = new AonDoubleBox();
	private AonDoubleBox cad = new AonDoubleBox();
	
	private ListBox dana = new ListBox();
	private AonDoubleBox danaReduction = new AonDoubleBox();
	
	Model303AEAT2023ActivityFarmer(final IModel303AEATActivityCallback<Mod303ActivityFarmer> cbk) {
		super(Unit.PX);
		setStyleName(AON.CSS.aonSelector());
		addStyleName(AON.CSS.aonBackgroundLigthBlue());
		
		dana.addItem("-");
		dana.addItem("Exclusivamente en municipios afectados por la DANA");
		dana.addItem("En municipios afectados por la DANA y en otros municipios");
		dana.setWidth("370px");
		
		addNorth(getToolbar(cbk), AonToolbar.HEIGTH);
		
		contentContainer = new SimpleLayoutPanel();
		
		add(contentContainer);
		if ( cbk.getActivity().isNotEmpty() ) {
			contentContainer.setWidget(getActivityData( cbk ));	
		} else {
			contentContainer.setWidget(getActivitySelectionPanel(cbk));
		}
	}
	
	private Widget getActivityData(IModel303AEATActivityCallback<Mod303ActivityFarmer> cbk) {
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonScrollArea());
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginLeft());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		
		populate(cbk.getActivity(), cbk.getModel().isEditable());
		
		vol.setEnabled( cbk.getActivity().isNotEmpty() && cbk.getModel().isEditable());
		vol.addValueChangeHandler(event -> {
			if (vol.getValue() == null) vol.setValue(0.0,false);
			cbk.getActivity().setVol(vol.getValue());
			ValueChangeEvent.<Mod303ActivityFarmer>fire(Model303AEAT2023ActivityFarmer.this, cbk.getActivity());
		});
		tab.addRow()
			.addCell( new Label(AON.MSG.operationsVolume()), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
			.addCell( vol);
			
		ind.setEnabled(false);
		tab.addRow()
			.addCell( new Label(AON.MSG.f03Msg()), AON.CSS.aonBorderBottom() )
			.addCell( ind );
		
		cuo.setEnabled(false);
		tab.addRow()
			.addCell( new Label(AON.MSG.f04Msg()), AON.CSS.aonBorderBottom() )
			.addCell( cuo );
		
		// DANA - A partir del ultimo periodo de 2024
		if ((cbk.getModel().getYear() == 2024 && cbk.getModel().isLastPeriod()) || (cbk.getModel().getYear() > 2024)) {
			dana.setEnabled(cbk.getActivity().isNotEmpty() && cbk.getModel().isEditable());
			dana.addChangeHandler(event-> {
				cbk.getActivity().setDana(dana.getSelectedIndex());
				
				// Reducción 25% de la cuota devengada, si exclusivamente en municipios DANA, en caso contrario, se deja a cero y que lo cumplimente el usuario
				if (dana.getSelectedIndex() == 1) {
					cbk.getActivity().setDanaReduction(AonMathUtils.round(cbk.getActivity().getCuo()*25/100));  
				} else {
					cbk.getActivity().setDanaReduction(0.0);  
				}	
				danaReduction.setValue(cbk.getActivity().getDanaReduction(), false, true);
				danaReduction.setEnabled(cbk.getActivity().getDana()==2);
				ValueChangeEvent.<Mod303ActivityFarmer>fire(Model303AEAT2023ActivityFarmer.this, cbk.getActivity());
			});			
			tab.addRow()
				.addCell(new Label("Actividad realizada en municipios afectados por la DANA 2024 "), AON.CSS.aonBorderBottom())
				.addCell(dana);
			
			danaReduction.setEnabled(cbk.getActivity().isNotEmpty() && cbk.getActivity().getDana() == 2 && cbk.getModel().isEditable());
			danaReduction.addValueChangeHandler(event -> {
				if (danaReduction.getValue() == null) 
					danaReduction.setValue(0.0,false);
				cbk.getActivity().setDanaReduction(danaReduction.getValue());
				ValueChangeEvent.<Mod303ActivityFarmer>fire(Model303AEAT2023ActivityFarmer.this, cbk.getActivity());
			});
			tab.addRow()
				.addCell(new Label("Reducci\u00F3n por actividad realizada en municipios afectados por la DANA"), AON.CSS.aonBorderBottom(), AON.CSS.aonWidth400() )
				.addCell(danaReduction);			
		}		
		
		if (!cbk.getModel().isLastPeriod()) {
			por.setEnabled(false);
			tab.addRow()
				.addCell( new Label(AON.MSG.incomePercent()), AON.CSS.aonBorderBottom() )
				.addCell( por );
			
			ing.setEnabled(false);
			tab.addRow()
				.addCell( new Label(AON.MSG.income()), AON.CSS.aonBorderBottom() )
				.addCell( ing );
		} else {
			sop.setEnabled(cbk.getActivity().isNotEmpty() && cbk.getModel().isEditable());
			sop.addValueChangeHandler(event -> {
				if (sop.getValue() == null) sop.setValue(0.0,false);
				cbk.getActivity().setSop(sop.getValue());
				ValueChangeEvent.<Mod303ActivityFarmer>fire(Model303AEAT2023ActivityFarmer.this, cbk.getActivity());
			});
			tab.addRow()
				.addCell( new Label("Cuotas soportadas"), AON.CSS.aonBorderBottom() )
				.addCell( sop );
			
			com.setEnabled(cbk.getActivity().isNotEmpty() && cbk.getModel().isEditable());
			com.addValueChangeHandler(event -> {
				if (com.getValue() == null) com.setValue(0.0,false);
				cbk.getActivity().setCom(com.getValue());
				ValueChangeEvent.<Mod303ActivityFarmer>fire(Model303AEAT2023ActivityFarmer.this, cbk.getActivity());
			});
			tab.addRow()
				.addCell( new Label("Compensaciones satisfechas a sujetos pasivos en R.E.A.G.P."), AON.CSS.aonBorderBottom() )
				.addCell( com );
			
			dev.setEnabled(false);
			tab.addRow()
				.addCell( new Label("1% de la cuota devengada por operaciones corrientes"), AON.CSS.aonBorderBottom() )
				.addCell( dev );
			
			tso.setEnabled(false);
			tab.addRow()
				.addCell( new Label("Total cuotas soportadas"), AON.CSS.aonBorderBottom() )
				.addCell( tso );
			
			cad.setEnabled(false);
			tab.addRow()
				.addCell( new Label(AON.MSG.page6J()), AON.CSS.aonBorderBottom() )
				.addCell( cad );
		}
		if (vol.isEnabled()) {
			vol.selectAll();
			vol.setFocus(true);
		}
		scroll.setWidget(tab);
		return scroll;
	}

	private Widget getToolbar(IModel303AEATActivityCallback<Mod303ActivityFarmer> cbk) {
		toolbar = new AonToolbar( "" );
		
		removeButton = new AonToolbarButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
		removeButton.addClickHandler(event -> AonConfirmDialog.showConfirm(AON.MSG.confirmDeleteAction(), () -> cbk.onRemove()));
		toolbar.add(removeButton);

		epigraphsButton = new AonTableButton("Cambiar actividad" ,AON.CSS.aonIconRefresh());
		epigraphsButton.addClickHandler(event ->  contentContainer.setWidget(getActivitySelectionPanel(cbk)) );
		toolbar.add(epigraphsButton);
		
		toolbarLabel = new Label( getTitle( cbk.getActivity() ) );
		toolbarLabel.setStyleName(AON.CSS.aonBold());
		toolbarLabel.addStyleName(AON.CSS.aonFontMedium());
		toolbarLabel.addStyleName(AON.CSS.aonPaddingLeft());
		
		toolbar.add(toolbarLabel);
		
		epigraphsButton.setVisible(cbk.getActivity().isNotEmpty() && cbk.getModel().isEditable());
		removeButton.setVisible(cbk.getActivity().isNotEmpty() && cbk.getModel().isEditable());

		return toolbar;
	}
	
	void populate(Mod303ActivityFarmer act, boolean isEditable) {
		epigraphsButton.setVisible(act.isNotEmpty() && isEditable);
		removeButton.setVisible(act.isNotEmpty() && isEditable);
		toolbarLabel.setText(getTitle( act ));
		vol.setValue(act.getVol(),false,true);
		ind.setValue(act.getInd(),false,true);
		cuo.setValue(act.getCuo(),false,true);
		por.setValue(act.getPor(),false,true);
		ing.setValue(act.getIng(),false,true);
		sop.setValue(act.getSop(),false,true);
		com.setValue(act.getCom(),false,true);
		dev.setValue(act.getDev(),false,true);
		tso.setValue(act.getTso(),false,true);
		cad.setValue(act.getCad(),false,true);
		dana.setSelectedIndex(act.getDana());
		danaReduction.setValue(act.getDanaReduction(),false,true);
		
		vol.setEnabled(act.isNotEmpty() && isEditable);
		dana.setEnabled(act.isNotEmpty() && isEditable);
		danaReduction.setEnabled(act.isNotEmpty() && act.getDana() == 2  && isEditable);
		sop.setEnabled(act.isNotEmpty() && isEditable);
		com.setEnabled(act.isNotEmpty() && isEditable);
	}
	
	private String getTitle(Mod303ActivityFarmer act) {
		return AonStringUtils.isNotBlank(act.getCode())
			?(act.getCode()+ " - " + AonStringUtils.abbreviate(act.getDescription(),80))
			:"Nueva actividad";
	}

	private Widget getActivitySelectionPanel(IModel303AEATActivityCallback<Mod303ActivityFarmer> cbk) {
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonScrollArea());
		
		final Model303AEAT2023ActivityFarmerSelection activityFarmer = new Model303AEAT2023ActivityFarmerSelection();
		activityFarmer.addSelectionHandler( event -> checkAccept(cbk, event.getSelectedItem()));
		
		scroll.setWidget(activityFarmer);
		return scroll;
	}
	
	private void checkAccept(IModel303AEATActivityCallback<Mod303ActivityFarmer> cbk, final FarmerIVA selected) {
		if (cbk.getActivity().isNotEmpty()) {
			AonConfirmDialog.showConfirm(AON.MSG.epigrapChanged(), () -> accept(cbk, selected));
		} else {
			accept(cbk, selected);
		}
	}

	private void accept(IModel303AEATActivityCallback<Mod303ActivityFarmer> cbk, final FarmerIVA selected) {
		cbk.getActivity().initialize();
		cbk.getActivity().setCode(selected.getCode());
		cbk.getActivity().setDescription(selected.getDescription());
		cbk.getActivity().setInd(selected.getIndiceRendimientoNeto());
		cbk.getActivity().setPor(selected.getPorcentaje());
		contentContainer.setWidget(getActivityData( cbk ));
		cbk.onAccept();
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Mod303ActivityFarmer> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}
	
}
