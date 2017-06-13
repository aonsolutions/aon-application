package com.esferalia.aon.gwt.fiscal.client.mod200.e2016;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2016.Model2002016.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class Page18 extends PageAbs {

	FlexTable table1;
	FlexTable table2;
	FlexTable table3;
	
	
	public Page18( Model200PageCallback callback ) {
		super(callback);
		
		table1 = new FlexTable();
		table2 = new FlexTable();
		table3 = new FlexTable();
		
		ScrollPanel container = new ScrollPanel();
		container.setStyleName(AON.AON_CSS.aonScrollArea());
		FlowPanel baseContainerPanel = new FlowPanel();
		baseContainerPanel.setStyleName(AON.AON_CSS.aonFiscalContainer());
		
			FlowPanel gr1= new FlowPanel();
			gr1.setStyleName(AON.AON_CSS.aonGroup());
			
				FlowPanel grHeader1 = new FlowPanel();
				grHeader1.setStyleName(AON.AON_CSS.aonGroupTitle());
				grHeader1.add (new InlineLabel(AON.MSG.bussinessAmount1())); 
				gr1.add(grHeader1);
				
				FlowPanel grBody1 = new FlowPanel();
				grBody1.setStyleName(AON.AON_CSS.aonGroupBody());
				grBody1.add(table);
				gr1.add(grBody1);
		baseContainerPanel.add(gr1);	
			
			FlowPanel gr2= new FlowPanel();
			gr2.setStyleName(AON.AON_CSS.aonGroup());
			
				FlowPanel grHeader2 = new FlowPanel();
				grHeader2.setStyleName(AON.AON_CSS.aonGroupTitle());
				grHeader2.add (new InlineLabel(AON.MSG.bussinessAmount2())); 
				gr2.add(grHeader2);
				
				FlowPanel grBody2 = new FlowPanel();
				grBody2.setStyleName(AON.AON_CSS.aonGroupBody());
				grBody2.add(table1);
				gr2.add(grBody2);
		baseContainerPanel.add(gr2);	
			
			FlowPanel gr3= new FlowPanel();
			gr3.setStyleName(AON.AON_CSS.aonGroup());
			
				FlowPanel grHeader3 = new FlowPanel();
				grHeader3.setStyleName(AON.AON_CSS.aonGroupTitle());
				grHeader3.add (new InlineLabel(AON.MSG.bussinessAmount3())); 
				gr3.add(grHeader3);
				
				FlowPanel grBody3 = new FlowPanel();
				grBody3.setStyleName(AON.AON_CSS.aonGroupBody());
				grBody3.add(table2);
				gr3.add(grBody3);
		baseContainerPanel.add(gr3);	
			
		container.add(baseContainerPanel);
		initWidget(container);
	}

	@Override
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(1, "200px");
		paintKey(table, Mod2002016Key.CN987, 0);
		
		
		table1.setWidth("100%");
		table1.setCellSpacing(0);
		table1.getColumnFormatter().setWidth(1, "200px");
		paintKey(table1, Mod2002016Key.CN988, 0);
		paintKey(table1, Mod2002016Key.CNEST, 1);
		
		table2.setWidth("100%");
		table2.setCellSpacing(0);
		table2.getColumnFormatter().setWidth(1, "200px");
		paintKey(table2, Mod2002016Key.CN989, 0);
	}

	@Override
	public void dump() {
		super.dump();
	}

	@Override
	protected void populate() {}
}
