package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.occam.api.model.fiscal.Mod303Activity;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class Model303AEATActivityTable extends FlowPanel implements HasSelectionHandlers<Mod303Activity> {
	
	private boolean lastPeriod;
	private AonDisplayGrid grid;
	
	public Model303AEATActivityTable(boolean lastPeriod) {
		setStyleName(AON.CSS.aonWidthAll());
		
		this.lastPeriod = lastPeriod;
		grid = new AonDisplayGrid();
		grid.addStyleName(AON.CSS.aonNoPadding());
		grid.addStyleName(AON.CSS.aonBlockCenter());
		grid.addStyleName(AON.CSS.aonWidthAlmostAll());

		add(grid);
	}
	
	protected void paint( LinkedList<Mod303Activity> activities ) {
		grid.clear();
		Label actLabel = new Label( AON.MSG.activity() );
		Label labelC = new Label( AON.MSG.quota() + " [C]");
		Label labelE = new Label( AON.MSG.percentAbbr() + " [E]");
		Label labelF = new Label( AON.MSG.incomeAbbr() + " [F]");
		Label labelI = new Label( AON.MSG.result() + " [I]");
		Label labelL = new Label( AON.MSG.page6I() + " [L]");
		Label labelM = new Label( AON.MSG.derQuota() + " [M]");
		grid.addHeaderRow()
			.addCell( actLabel )
			.addCell( labelC , AON.CSS.aonTextRight(),AON.CSS.aonWidth80())
			.addCellIf( !lastPeriod, labelE , AON.CSS.aonTextRight(),AON.CSS.aonWidth80())
			.addCellIf( !lastPeriod, labelF , AON.CSS.aonTextRight(),AON.CSS.aonWidth80())
			.addCellIf( lastPeriod , labelI , AON.CSS.aonTextRight(),AON.CSS.aonWidth80())
			.addCellIf( lastPeriod , labelL , AON.CSS.aonTextRight(),AON.CSS.aonWidth80())
			.addCellIf( lastPeriod , labelM , AON.CSS.aonTextRight(),AON.CSS.aonWidth80())
		;
		
		for (Mod303Activity act : activities) {
			AonDisplayGridRow actRow = grid.addRow();
			actRow
				.addCell( new Label( AonStringUtils.abbreviate(act.getFullDescription(), 100) ), AON.CSS.aonFlexGrow1() )
				.addCell( new Label( AON.FMT.format(act.getDev()) ), AON.CSS.aonTextRight() , AON.CSS.aonTextRight())
				.addCellIf( !lastPeriod, new Label( AON.FMT.format(act.getPor()) ), AON.CSS.aonTextRight())
				.addCellIf( !lastPeriod, new Label( AON.FMT.format(act.getIng()) ), AON.CSS.aonTextRight())
				.addCellIf( lastPeriod, new Label( AON.FMT.format(act.getRes()) ), AON.CSS.aonTextRight())
				.addCellIf( lastPeriod, new Label( AON.FMT.format(act.getCmn()) ), AON.CSS.aonTextRight())
				.addCellIf( lastPeriod, new Label( AON.FMT.format(act.getCad()) ), AON.CSS.aonTextRight())
			;
			actRow.addClickHandler(event -> SelectionEvent.<Mod303Activity>fire(Model303AEATActivityTable.this, act) );
		}
		
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod303Activity> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}	
}
