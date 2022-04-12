// ECPN: ESTADO DE INGRESOS Y GASTOS RECONOCIDOS
package com.esferalia.aon.gwt.mod200.client.mod200.e2020;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.Model2002020.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200.IMod200Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020.EcpnType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020Key;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class Page06 extends PageAbs {
	
	private FlexTable table;
	
	public Page06( Model200PageCallback callback ) {
		super(callback);
		ScrollPanel container = new ScrollPanel();
		container.setStyleName(AON.AON_CSS.aonScrollArea());
		FlowPanel baseContainerPanel = new FlowPanel();
		baseContainerPanel.setStyleName(AON.AON_CSS.aonFiscalContainer());
		
		FlowPanel groupPanel= new FlowPanel();
		groupPanel.setStyleName(AON.AON_CSS.aonGroup());
			
				FlowPanel groupHeaderPanel = new FlowPanel();
				groupHeaderPanel.setStyleName(AON.AON_CSS.aonGroupTitle());
				groupHeaderPanel.add (new InlineLabel(AON.MSG.patrimonioIngresos())); 
				groupPanel.add(groupHeaderPanel);
				
				FlowPanel groupBodyPanel = new FlowPanel();
				groupBodyPanel.setStyleName(AON.AON_CSS.aonGroupBody());
				table = new FlexTable();
				groupBodyPanel.add(table);
				groupPanel.add(groupBodyPanel);
		
		baseContainerPanel.add(groupPanel);	
		container.add(baseContainerPanel);
		initWidget(container);
		initializeTable();		
	}
	
	@Override
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		
		ColumnFormatter cf = table.getColumnFormatter();
		cf.setWidth(1, "200px");

		int row = 0;
		for (Mod2002020Key key : Mod2002020Constants.ECPN_INCOME_KEYS) {
			if (callback.getMod200Object().isVisible(key) ) {
				row = paintKey(table,key,row);
			}
		}
	}
	
	@Override
	protected void populate() {}
	
	@Override
	protected boolean isDisabled(IMod200Key key) {
		if (callback.getMod200Object().getMod200().getEcpnType() == EcpnType.NORMAL) {
			if (key == Mod2002020Key.T0336 || key == Mod2002020Key.T0346) {
				return true;
			}
		}
		return super.isDisabled(key);
	}

	@Override
	protected boolean isAvailable() {
		return super.isAvailable()
  		  && ( (callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0075)) ||
  			   (callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0076)) ||
  			   (callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0077)) );  				  
	}
}
