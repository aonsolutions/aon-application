// REGIMEN ESPECIAL CANARIAS
package com.esferalia.aon.gwt.mod200.client.mod200.e2020;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.Model2002020.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200.IMod200Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020RIC_1Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020RIC_2Key;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class Page16 extends PageAbs {

	private FlexTable table1;
	private FlexTable table2;
	private FlexTable table3;

	public Page16( Model200PageCallback callback ) {
		super(callback);

		ScrollPanel container = new ScrollPanel();
		container.setStyleName(AON.AON_CSS.aonScrollArea());
		FlowPanel baseContainerPanel = new FlowPanel();
		baseContainerPanel.setStyleName(AON.AON_CSS.aonFiscalContainer());
		
			FlowPanel groupPanel = new FlowPanel();
			groupPanel.setStyleName(AON.AON_CSS.aonGroup());
			
				FlowPanel groupHeaderPanel = new FlowPanel();
				groupHeaderPanel.setStyleName(AON.AON_CSS.aonGroupTitle());
				groupHeaderPanel.add (new InlineLabel(AON.MSG.canariasRegime())); 
				groupPanel.add(groupHeaderPanel);
				
				FlowPanel groupBodyPanel = new FlowPanel();
				groupBodyPanel.setStyleName(AON.AON_CSS.aonGroupBody());
				table1 = new FlexTable();
				table2 = new FlexTable();
				table3 = new FlexTable();
				
				groupBodyPanel.add(table1);
				groupBodyPanel.add(table2);				
				groupBodyPanel.add(table3);
				
				groupPanel.add(groupBodyPanel);
				
			baseContainerPanel.add(groupPanel);
		container.add(baseContainerPanel);
		initWidget(container);
		initializeTable();
	}

	@Override
	protected void initializeTable() {
		
		// RIC
		table1.setWidth("100%");
		table1.setCellSpacing(0);
		table1.getColumnFormatter().setWidth(0, "auto");
		table1.getColumnFormatter().setWidth(1, "180px");
		table1.getColumnFormatter().setWidth(2, "180px");
		table1.getColumnFormatter().setWidth(3, "180px");
		table1.getColumnFormatter().setWidth(4, "180px");
		table1.getColumnFormatter().setWidth(5, "180px");
		
		int row = 0;
		addHeaderCell(table1,row, 2,"Aplicado/materializado en esta liquidaci\u00F3n");
		table1.getFlexCellFormatter().setColSpan(row, 2, 3);
		row++;
		addHeaderCell(table1,row, 0,"");
		addHeaderCell(table1,row, 1,"Pendiente de materializar RIC a principio de per\u00EDodo"); 
		addHeaderCell(table1,row, 2,"Inversiones previstas letras A y B, art. 27.4 Ley 19/1994");
		addHeaderCell(table1,row, 3,"Inversiones previstas letras B bis, C y D, art. 27.4 Ley 19/1994");
		addHeaderCell(table1,row, 4,"Inversiones anticipadas consideradas materializaci\u00F3n de la RIC en esta liquidaci\u00F3n");
		addHeaderCell(table1,row, 5,"Pendiente de materializar RIC al final de per\u00EDodo");	
		row++;
		paintKeysProvider(Mod2002020RIC_1Key.values(), table1, row);
		
		// Importe de la dotación RIC con cargo a beneficios de 2020 (Casilla 927)		
		table2.setCellSpacing(20);
		table2.getColumnFormatter().setWidth(0, "400px");
		table2.getColumnFormatter().setWidth(1, "200px");
		row = 0;
		paintKey(table2,Mod2002020Key.RC927,row);
		
		// Inversiones anticipadas
		table3.setWidth("100%");
		table3.setCellSpacing(0);
		table3.getColumnFormatter().setWidth(0, "auto");
		table3.getColumnFormatter().setWidth(1, "200px");
		table3.getColumnFormatter().setWidth(2, "200px");
		table3.getColumnFormatter().setWidth(3, "200px");
		table3.getColumnFormatter().setWidth(4, "200px");
		row = 0;
		addHeaderCell(table3,row, 0,"");
		addHeaderCell(table3,row, 1,"Pendiente de dotar RIC a principio de per\u00EDodo");
		addHeaderCell(table3,row, 2,"Inversiones previstas letras A y B, art. 27.4 Ley 19/1994");
		addHeaderCell(table3,row, 3,"Inversiones previstas letras B bis, C y D, art. 27.4 Ley 19/1994");
		addHeaderCell(table3,row, 4,"Pendiente de dotar RIC al final de per\u00EDodo");
		row++;		
		paintKeysProvider(Mod2002020RIC_2Key.values(), table3, row);
		
	}
	
	private void addHeaderCell(FlexTable table, int row, int col, String msg) {
		table.setWidget(row, col, new Label( msg ));
		table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
		table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonFontSmall());
	}

	@Override
	protected void populate() {
	}
	
	@Override
	protected boolean isAvailable() {
		boolean av = super.isAvailable()
  		  && (callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0029));
		return av;
	}
	
	@Override
	protected boolean isDisabled(IMod200Key key) {
		return super.isDisabled(key);
	}
	
}
