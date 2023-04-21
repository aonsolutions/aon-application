// LIQUIDACION (II): BASE IMPONIBLE, TIPO DE GRAVAMEN, CUOTA INTEGRA
package com.esferalia.aon.gwt.mod200.client.mod200.e2022;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2022.Model2002022.Model200PageCallback;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LQ1032Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LQ1033_1Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LQ1033_2Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LQ243Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LQ547Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LQ554Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LQ561Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LQ579Key;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class Page09 extends PageAbs {
	
	private static final String[] HEADERS_2 = new String[] {
		"",
		AON.MSG.previousPending(),
		AON.MSG.current(),
		AON.MSG.futurePending()		
	};
	
	private static final String[] HEADERS_1 = new String[] {
		AON.MSG.cooperativeRegime(),
		AON.MSG.cooperativeResult(),
		AON.MSG.extraCooperativeResult()
	};
	
	private static final String[] HEADERS_3 = new String[] {
		"",
		"Derecho a reducir la B.I. generado en el per\u00EDodo/pendiente de aplicar a inicio del per\u00EDodo",
		"Reducci\u00F3n B.I. aplicada",
		"Reducci\u00F3n B.I. pendiente de aplicar en per\u00EDodos futuros"
	};
	
	private static final String[] HEADERS_4 = new String[] {
		"",
		"Pendiente de aplicaci\u00F3n a principio del periodo/generada en el periodo",
		AON.MSG.current(),
		AON.MSG.futurePending()		
	};
	
	private static final String FOOTER_547 = "(*) S\u00F3lo debe cumplimentarse esta fila si la entidad tiene bases imponibles negativas por otro per\u00EDodo impositivo iniciado tambi\u00E9n en 2022, pero inferior a 12 meses y previo al ejercicio declarado.";
	private static final String FOOTER_561 = "(*) S\u00F3lo debe cumplimentarse si la entidad tiene cuotas negativas por otro per\u00EDodo impositivo iniciado tambi\u00E9n en 2022, pero inferior a 12 meses y previo al ejercicio declarado.";
	private static final String FOOTER_1032 = "(*) S\u00F3lo debe cumplimentarse si la entidad tiene reservas pendientes de integrar en un per\u00EDodo impositivo anterior iniciado en 2022.";
	private static final String FOOTER_1033 = "(*) S\u00F3lo debe cumplimentarse si la entidad tiene reducciones pendientes de integrar correspondientes a un per\u00EDodo impositivo anterior iniciado en 2022.";
	
	public Page09( Model200PageCallback callback ) {
		super(callback);
	}
	
	@Override
	protected void initializeTable() {
		
		basePanel.clear();
		
		basePanel.add(getTitle("BASE IMPONIBLE. CUOTA INTEGRA"));
		
		FlexTable table = addTable();

		boolean paintSocimi = true;
		int row = 0;
		boolean margin = false;
		for (Mod2002022Key key : Mod2002022Constants.LIQUIDATION_II_KEYS) {
			if (callback.getMod200Object().isVisible(key)) {			
				if (key == Mod2002022Key.LQ578) {
					paintDescription(table, "Entidades navieras en r\u00E9gimen de tributaci\u00F3n en funci\u00F3n del tonelaje", row,0, true);
					table.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFiscalPaddingLeft());					
					row++;
					margin = true;
				}
				if (key == Mod2002022Key.LQ1029) {
					paintDescription(table, "Entidades que forman parte de grupos de consolidaci\u00F3n fiscal", row,0, true);
					table.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFiscalPaddingLeft());
					row++;
					margin = true;
				}
				if (key == Mod2002022Key.LQ550TG || key == Mod2002022Key.LQ550T0 ) {
					margin = true;
				}
				
				if (key == Mod2002022Key.LQ541 || key == Mod2002022Key.LQ1887) {
					paintDescription(table, "R\u00E9gimen especial de entidades navieras en Canarias", row,0, true);
					table.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFiscalPaddingLeft());
					row++;
					margin = true;
				}
				
				if (key == Mod2002022Key.LQ1033) {
					paintDescription(table, "S\u00F3lo entidades de reducida dimensi\u00F3n", row,0, true);
					table.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFiscalPaddingLeft());
					row++;
					margin = true;
				}
				if (key == Mod2002022Key.LQ553) {
					paintDescription(table, "S\u00F3lo sociedades cooperativas", row,0, true);
					table.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFiscalPaddingLeft());
					row++;
					margin = true;
				}
				if (key == Mod2002022Key.LQ555) {
					paintDescription(table, "S\u00F3lo agrupaciones espa\u00F1olas de inter\u00E9s econ\u00F3mico y UTES", row,0, true);
					table.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFiscalPaddingLeft());
					row++;
					margin = true;
				}
				if (key == Mod2002022Key.LQ559) {
					paintDescription(table, "S\u00F3lo entidades ZEC", row,0, true);
					table.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFiscalPaddingLeft());
					row++;
					margin = true;
				}
				if (key == Mod2002022Key.LQ520 || key == Mod2002022Key.LQ521) {
					if (paintSocimi) {
						paintDescription(table, "S\u00F3lo SOCIMIS", row,0, true);
						table.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFiscalPaddingLeft());
						row++;
						paintSocimi = false;
					}					
					margin = true;
				}
				if (key == Mod2002022Key.LQ545) {
					paintDescription(table, "Rentas que no limitan la compensaci\u00F3n de bases imponibles", row,0, true);
					table.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFiscalPaddingLeft());
					row++;
					margin = true;
				}
				if (key == Mod2002022Key.LQ1576) {
					paintDescription(table, "R\u00E9gimen especial de entidades navieras en Canarias", row, 0, true);
					table.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFiscalPaddingLeft());
					row++;
					margin = true;
				}
				if (key == Mod2002022Key.LQ560) {
					paintDescription(table, "S\u00F3lo sociedades cooperativas", row,0, true);
					table.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFiscalPaddingLeft());
					row++;
					margin = true;
				}				
				if (key == Mod2002022Key.LQ547 || key == Mod2002022Key.LQ550 || key == Mod2002022Key.LQ552 || key == Mod2002022Key.LQ558 || key == Mod2002022Key.LQ562 || key == Mod2002022Key.LQ1032 || key == Mod2002022Key.LQ1330) {
					margin = false;
				}
								
				row = paintKey(table,key,row);
				
				if (margin) {
					table.getCellFormatter().addStyleName(row-1, 0, AON.AON_CSS.aonPaddingLeft20());
				}
				
				// FALTA - DESGLOSES CASILLAS 1887 Y 1890 - ESTE DESGLOSE TIENE 2 SUBTOTALES Y UN TOTAL
				if (key == Mod2002022Key.LQ1890) {
					row = paintKeyBreakdownLink(table, row, Mod2002022Key.LQ1887, Mod2002022LQ243Key.values(), HEADERS_4, FOOTER_547);
				}
				
     			if (key == Mod2002022Key.LQ554) {
					row = paintKeyBreakdownLink(table, row, Mod2002022Key.LQ554, Mod2002022LQ554Key.values(), HEADERS_1);
				}
				if (key == Mod2002022Key.LQ561) {
					row = paintKeyBreakdownLink(table, row, Mod2002022Key.LQ561, Mod2002022LQ561Key.values(), HEADERS_2, FOOTER_561);
				}
    			if (key == Mod2002022Key.LQ579) {
					row = paintKeyBreakdownLink(table, row, Mod2002022Key.LQ579, Mod2002022LQ579Key.values(), null);
				}
    			if (key == Mod2002022Key.LQ1032 
					&& callback.getMod200Object().getMod200().isNotChecked(Mod2002022Key.C0009) 
					&& callback.getMod200Object().getMod200().isNotChecked(Mod2002022Key.C0010)) {
					row = paintKeyBreakdownLink(table, row, Mod2002022Key.LQ1032, Mod2002022LQ1032Key.values(), HEADERS_3, FOOTER_1032);
				}
    			if (key == Mod2002022Key.LQ547 && !callback.getMod200Object().getMod200().isCooperativa()) {
					row = paintKeyBreakdownLink(table, row, Mod2002022Key.LQ547, Mod2002022LQ547Key.values(), HEADERS_4, FOOTER_547);    				
				}    			
    			if (key == Mod2002022Key.LQ1034) {
					row = paintKeyBreakdownLinkLQ1033(table, row, Mod2002022Key.LQ1033.getDescription());
				}
			}
		}
	}
	
	protected int paintKeyBreakdownLinkLQ1033(final FlexTable tab,int row,final String label) {
		final int boxRow = row-1;
		final int boxCell = tab.getCellCount(boxRow) - 1;
		
		FlowPanel panel  = (FlowPanel) tab.getWidget( boxRow , boxCell );
		panel.addStyleName(AON.AON_CSS.aonNowrap());
		Button breakdown = new Button();
		breakdown.setStyleName(AON.AON_CSS.aonIconModel());
		breakdown.addStyleName(AON.AON_CSS.aonBorderNone());
		breakdown.addStyleName(AON.AON_CSS.aonCursorPointer());
		breakdown.addStyleName(AON.AON_CSS.aonMarginRight());
		breakdown.setTitle(AON.MSG.breakdown());
		panel.insert(breakdown,0);
		
		final FlowPanel container = new FlowPanel();
		container.addStyleName(AON.CSS.aonPaddingBottom());
		container.setVisible(false);
		final String backgroundColor = "#E0FFFF";
		
		FlexTable tableDetail = getFlexTable(container, new String[]{
				 "REDUCCION EN BASE IMPONIBLE  -  Ejercicio de generaci\u00F3n"
				,"Importe minoraci\u00F3n B.I. en el per\u00EDodo/pendiente de adicionar a inicio del per\u00EDodo"
				,"Importe adicionado a base imponible en el per\u00EDodo"
				,"Importe integrado en la declaraci\u00F3n por incumplimiento de requisitos"
				,"Importe pendiente de adicionar en per\u00EDodos futuros"
				});
		int r = 2;
		int col = 0;
		for (IMod200KeysProvider key : Mod2002022LQ1033_1Key.values()) {
			Label desc = new Label(key.getDescription() );
			desc.setStyleName(AON.AON_CSS.aonMarginLeft());
			if ("Total".equals(key.getDescription()))
				desc.addStyleName(AON.AON_CSS.aonBold());
			tableDetail.setWidget(r, 0, desc);
			tableDetail.getFlexCellFormatter().setStyleName(r, 0, AON.AON_CSS.aonFiscalBorderBottom());
			col = 1;
			for (final IMod200Key k : key.getKeys() ) {
				if (k != null && k!= Mod2002022Key.LQ1033){
					paintKeyField(tableDetail, k, r, col, 9, false);
				}
				++col;
			}
			++r;
		}
		paintFooterNote(container, FOOTER_1033);
				
		FlexTable tableDetail2 = getFlexTable(container, new String[]{
				 "DOTACION DE LA RESERVA  -  Ejercicio de generaci\u00F3n"
				,"Importe reserva a dotar"
				,"Importe reserva dotada"
				,"Importe reserva pendiente dotaci\u00F3n"
				,"Reserva dispuesta"				
				});
		tableDetail2.addStyleName(AON.CSS.aonPaddingTop());
		r = 1;
		col = 0;
		for (IMod200KeysProvider key : Mod2002022LQ1033_2Key.values()) {
			Label desc = new Label(key.getDescription() );
			if ("Total".equals(key.getDescription()))
				desc.setStyleName(AON.AON_CSS.aonBold());
			tableDetail2.setWidget(r, 0, desc);
			tableDetail2.getFlexCellFormatter().setStyleName(r, 0, AON.AON_CSS.aonFiscalBorderBottom());
			col = 1;
			for (final IMod200Key k : key.getKeys() ) {
				if (k != null){
					paintKeyField(tableDetail2, k, r, col, 9, false);
				}
				++col;
			}
			++r;
		}
		
		tab.setWidget(row, 0, container);
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		
		breakdown.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				container.setVisible( !container.isVisible() );
				for ( int i = 0 ; i < tab.getCellCount(boxRow); i++) {
					tab.getCellFormatter().getElement(boxRow , i).getStyle().setBackgroundColor(
							container.isVisible()?backgroundColor:"#FFFFFF");	
				}
				container.getElement().getStyle().setBackgroundColor(
						container.isVisible()?backgroundColor:"#FFFFFF");
			}
			
		});
		
		paintFooterNote(container, FOOTER_1033);
		
		return ++row;
	}
	
	@Override
	protected boolean isDisabled(IMod200Key key) {
		if (key == Mod2002022Key.LQ1032 
				&& (callback.getMod200Object().getMod200().isChecked(Mod2002022Key.C0009) 
				|| callback.getMod200Object().getMod200().isChecked(Mod2002022Key.C0010))) {
			return false;
		}
		if (key == Mod2002022Key.LQ562 
				&& (callback.getMod200Object().getMod200().isChecked(Mod2002022Key.C0030) 
				|| callback.getMod200Object().getMod200().isChecked(Mod2002022Key.C0047))) {
			return false;
		}
		if ((key == Mod2002022Key.LQ520 || key == Mod2002022Key.LQ521) && 
			(callback.getMod200Object().getMod200().isChecked(Mod2002022Key.C0064))) {
			return true;
		}
		return super.isDisabled(key);
	}
	
}
