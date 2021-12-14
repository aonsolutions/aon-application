package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.occam.api.model.fiscal.Mod303ActivityFarmer;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

class Model303AEATActivityFarmerTable extends FlowPanel implements HasSelectionHandlers<Mod303ActivityFarmer> {
	
	private boolean lastPeriod;
	private AonDisplayGrid grid;

	public Model303AEATActivityFarmerTable(boolean lastPeriod) {
		setStyleName(AON.CSS.aonWidthAll());
		
		this.lastPeriod = lastPeriod;
		grid = new AonDisplayGrid();
		grid.addStyleName(AON.CSS.aonNoPadding());
		grid.addStyleName(AON.CSS.aonBlockCenter());
		grid.addStyleName(AON.CSS.aonWidthAlmostAll());

		add(grid);
	}
	
	protected void paint( LinkedList<Mod303ActivityFarmer> activities ) {
		grid.clear();

		Label actLabel = new Label( AON.MSG.activity() );
		Label labelVol = new Label( AON.MSG.operationsVolume());
		Label labelInd = new Label( AON.MSG.f03Msg());
		Label labelCuo = new Label( AON.MSG.quota());
		Label labelSop = new Label( AON.MSG.page6DAbbr());
		Label labelCad = new Label( AON.MSG.derQuota() + " [B]");
		Label labelPor = new Label( AON.MSG.percentAbbr());
		Label labelRes = new Label( AON.MSG.result() + " [A]");
		
		grid.addHeaderRow()
			.addCell( actLabel )
			.addCell( labelVol , AON.CSS.aonTextRight(),AON.CSS.aonWidth80())
			.addCell( labelInd , AON.CSS.aonTextRight(),AON.CSS.aonWidth80())
			.addCell( labelCuo , AON.CSS.aonTextRight(),AON.CSS.aonWidth80())
			.addCellIf(lastPeriod, labelSop , AON.CSS.aonTextRight(),AON.CSS.aonWidth80())
			.addCellIf(lastPeriod, labelCad , AON.CSS.aonTextRight(),AON.CSS.aonWidth80())
			.addCellIf(!lastPeriod, labelPor , AON.CSS.aonTextRight(),AON.CSS.aonWidth80())
			.addCellIf(!lastPeriod, labelRes , AON.CSS.aonTextRight(),AON.CSS.aonWidth80())
		;

		for (Mod303ActivityFarmer act : activities) {
			AonDisplayGridRow actRow = grid.addRow();
			actRow
				.addCell( new Label( AonStringUtils.abbreviate(act.getFullDescription(), 80) ), AON.CSS.aonFlexGrow1() )
				.addCell( new Label( AON.FMT.format(act.getVol()) ), AON.CSS.aonTextRight())
				.addCell( new Label( AonNumberUtils.toString( act.getInd() )) , AON.CSS.aonTextRight())
				.addCell( new Label(AON.FMT.format(act.getCuo()) ),AON.CSS.aonTextRight())
				.addCellIf(lastPeriod, new Label(AON.FMT.format(act.getSop())), AON.CSS.aonTextRight() , AON.CSS.aonTextRight())
				.addCellIf(lastPeriod, new Label(AON.FMT.format(act.getCad())), AON.CSS.aonTextRight() , AON.CSS.aonTextRight())
				.addCellIf(!lastPeriod, new Label(AON.FMT.format(act.getPor())), AON.CSS.aonTextRight() , AON.CSS.aonTextRight())
				.addCellIf(!lastPeriod, new Label(AON.FMT.format(act.getIng())), AON.CSS.aonTextRight() , AON.CSS.aonTextRight())
			;
			actRow.addClickHandler(event -> SelectionEvent.<Mod303ActivityFarmer>fire(Model303AEATActivityFarmerTable.this, act) );
		}
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod303ActivityFarmer> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}	

}
