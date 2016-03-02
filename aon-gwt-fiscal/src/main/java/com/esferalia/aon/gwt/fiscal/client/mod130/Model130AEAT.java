package com.esferalia.aon.gwt.fiscal.client.mod130;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Model130AEAT extends Model130Base {
	
	
	public Model130AEAT(IFiscalModelCallback<Mod130> callback) {
		super(callback);
	}

	@Override
	public Widget getInfoPanel(Mod130 mod130) {

		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.add(getAnchorPanel( mod130
				,"Tr\u00E1mites."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/tramitacion/G601.shtml"));
		panel.add(getAnchorPanel( mod130
				,"Informaci\u00F3n general." 
				,"https://www.agenciatributaria.gob.es/AEAT.sede/Ayuda/G601.shtml"));
		panel.add(getAnchorPanel(mod130
				,"Ficha."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/procedimientos/G601.shtml"));
		return panel;
	}
	
	@Override
	protected void paintParticularyRow(Mod130 mod130, IModelScript<Mod130Key> script) {
		if (script.getKeys() == null) return;
		if (script.getKeys()[0] == Mod130Key.P0) {
			paintRowP00(mod130,script);
		} else if (script.getKeys()[0] == Mod130Key.P1) {
			paintRowP01(mod130,script);
		} else if (script.getKeys()[0] == Mod130Key.P2) {
			paintRowP02(mod130,script);
		} 
	}
	
	private void paintRowP00(Mod130 mod130, IModelScript<Mod130Key> script) {
		int row = getTable().getRowCount();
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextRight() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingRight() );
		getTable().setWidget(row, 1, new Label());
		final FiscalModelDetail p1 = mod130.ensureDetail(Mod130Key.P0);
		String labelText = IRPFRegime.NORMAL.getDescription();
		if (p1 != null && p1.getAmount() == 1) {
			labelText = IRPFRegime.SIMPLIFIED.getDescription();	
		}
		getTable().setWidget(row, 2, new Label( labelText ));
	}

	private void paintRowP01(Mod130 mod130, IModelScript<Mod130Key> script) {
		int row = getTable().getRowCount();
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextRight() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingRight() );
		getTable().setWidget(row, 1, new Label());
		final FiscalModelDetail p1 = mod130.ensureDetail(Mod130Key.P1);
		getTable().setWidget(row, 2, new Label( AON.FMT.format(p1.getAmount()) + "%"));
	}

	private void paintRowP02(Mod130 mod130, IModelScript<Mod130Key> script) {
		int row = getTable().getRowCount();
		final FiscalModelDetail p2 = mod130.ensureDetail(Mod130Key.P2);
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextRight() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingRight() );
		getTable().setWidget(row, 1, new Label());
		final Label wP2 = new Label(p2.getAmount()==1?AON.MSG.yes():AON.MSG.no());
		getTable().setWidget(row, 2, wP2 );
	}
	

}
