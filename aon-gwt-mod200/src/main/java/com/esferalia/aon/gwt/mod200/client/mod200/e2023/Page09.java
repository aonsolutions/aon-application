// LIQUIDACION (II): BASE IMPONIBLE, TIPO DE GRAVAMEN, CUOTA INTEGRA
package com.esferalia.aon.gwt.mod200.client.mod200.e2023;

import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0006;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0013;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0022;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0063;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0071;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0083;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0085;
import static com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key.C0088;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2023.Model2002023.Model2002023PageCallback;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023LQ1032Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023LQ1033_1Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023LQ1033_2Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023LQ243Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023LQ547Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023LQ554Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023LQ561Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023LQ579Key;
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
	
	private static final String FOOTER_547 = "(*) S\u00F3lo debe cumplimentarse esta fila si la entidad tiene bases imponibles negativas por otro per\u00EDodo impositivo iniciado tambi\u00E9n en 2023, pero inferior a 12 meses y previo al ejercicio declarado.";
	private static final String FOOTER_561 = "(*) S\u00F3lo debe cumplimentarse si la entidad tiene cuotas negativas por otro per\u00EDodo impositivo iniciado tambi\u00E9n en 2023, pero inferior a 12 meses y previo al ejercicio declarado.";
	private static final String FOOTER_1032 = "(*) S\u00F3lo debe cumplimentarse si la entidad tiene reservas pendientes de integrar en un per\u00EDodo impositivo anterior iniciado en 2023.";
	private static final String FOOTER_1033 = "(*) S\u00F3lo debe cumplimentarse si la entidad tiene reducciones pendientes de integrar correspondientes a un per\u00EDodo impositivo anterior iniciado en 2023.";
	
	public Page09( Model2002023PageCallback callback ) {
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
		for (Mod2002023Key key : Mod2002023Constants.LIQUIDATION_II_KEYS) {
			if (callback.getMod200Object().isVisible(key)) {
				
				// Casillas que llevan antes un titulo del apartado (las casillas del apartado se ponen un poco mas a la izquierda)
				
				if (key == Mod2002023Key.LQ578) {
					paintDescription(table, "Entidades navieras en r\u00E9gimen de tributaci\u00F3n en funci\u00F3n del tonelaje", row,0, true);
					table.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFiscalPaddingLeft());					
					row++;
					margin = true;
				}
				if (key == Mod2002023Key.LQ1029) {
					paintDescription(table, "Entidades que forman parte de grupos de consolidaci\u00F3n fiscal", row,0, true);
					table.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFiscalPaddingLeft());
					row++;
					margin = true;
				}
				if (key == Mod2002023Key.LQ550TG || key == Mod2002023Key.LQ550T0 ) {
					margin = true;
				}
				
				if (key == Mod2002023Key.LQ541 || key == Mod2002023Key.LQ1887 || key == Mod2002023Key.LQ1576) {
					paintDescription(table, "R\u00E9gimen especial de buques y empresas navieras en Canarias", row,0, true);					                        
					table.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFiscalPaddingLeft());
					row++;
					margin = true;
				}
				
				if (key == Mod2002023Key.LQ1033) {
					paintDescription(table, "S\u00F3lo entidades de reducida dimensi\u00F3n", row,0, true);
					table.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFiscalPaddingLeft());
					row++;
					margin = true;
				}
				if (key == Mod2002023Key.LQ553 || key == Mod2002023Key.LQ560) {
					paintDescription(table, "S\u00F3lo sociedades cooperativas", row,0, true);
					table.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFiscalPaddingLeft());
					row++;
					margin = true;
				}
				if (key == Mod2002023Key.LQ555) {
					paintDescription(table, "S\u00F3lo agrupaciones espa\u00F1olas de inter\u00E9s econ\u00F3mico y UTES", row,0, true);
					table.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFiscalPaddingLeft());
					row++;
					margin = true;
				}
				if (key == Mod2002023Key.LQ559) {
					paintDescription(table, "S\u00F3lo entidades ZEC", row,0, true);
					table.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFiscalPaddingLeft());
					row++;
					margin = true;
				}
				if (key == Mod2002023Key.LQ520 || key == Mod2002023Key.LQ521) {
					if (paintSocimi) {
						paintDescription(table, "S\u00F3lo SOCIMIS", row,0, true);
						table.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFiscalPaddingLeft());
						row++;
						paintSocimi = false;
					}					
					margin = true;
				}
				if (key == Mod2002023Key.LQ545) {
					paintDescription(table, "Rentas que no limitan la compensaci\u00F3n de bases imponibles", row,0, true);
					table.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFiscalPaddingLeft());
					row++;
					margin = true;
				}
				
				// Casillas que se ponen mas a la derecha
				if (key == Mod2002023Key.LQ547 || key == Mod2002023Key.LQ550 || key == Mod2002023Key.LQ552 || key == Mod2002023Key.LQ558 || 
					key == Mod2002023Key.LQ562 || key == Mod2002023Key.LQ1032 || key == Mod2002023Key.LQ1330) {
					margin = false;
				}
								
				// Pintar la casilla
				row = paintKey(table,key,row);
				
				if (margin) {
					table.getCellFormatter().addStyleName(row-1, 0, AON.AON_CSS.aonPaddingLeft20());
				}				
				
				// Casillas que llevan desglose
				
				if (key == Mod2002023Key.LQ1890
						&& callback.getMod200Object().getMod200().isNotChecked(Mod2002023Key.C0009) 
						&& callback.getMod200Object().getMod200().isNotChecked(Mod2002023Key.C0010)) {
					row = paintKeyBreakdownLink(table, row, Mod2002023Key.LQ1887, Mod2002023LQ243Key.values(), HEADERS_4, FOOTER_547);
				}
				
     			if (key == Mod2002023Key.LQ554) {
					row = paintKeyBreakdownLink(table, row, Mod2002023Key.LQ554, Mod2002023LQ554Key.values(), HEADERS_1);
				}
				if (key == Mod2002023Key.LQ561) {
					row = paintKeyBreakdownLink(table, row, Mod2002023Key.LQ561, Mod2002023LQ561Key.values(), HEADERS_2, FOOTER_561);
				}
    			if (key == Mod2002023Key.LQ579) {
					row = paintKeyBreakdownLink(table, row, Mod2002023Key.LQ579, Mod2002023LQ579Key.values(), null);
				}
    			if (key == Mod2002023Key.LQ1032 
					&& callback.getMod200Object().getMod200().isNotChecked(Mod2002023Key.C0009) 
					&& callback.getMod200Object().getMod200().isNotChecked(Mod2002023Key.C0010)) {
					row = paintKeyBreakdownLink(table, row, Mod2002023Key.LQ1032, Mod2002023LQ1032Key.values(), HEADERS_3, FOOTER_1032);
				}
    			if (key == Mod2002023Key.LQ547 && !callback.getMod200Object().getMod200().isCooperativa()) {
					row = paintKeyBreakdownLink(table, row, Mod2002023Key.LQ547, Mod2002023LQ547Key.values(), HEADERS_4, FOOTER_547);    				
				} 
    			if (key == Mod2002023Key.LQ1034 && callback.getMod200Object().getMod200().isChecked(Mod2002023Key.C0006)) {
					row = paintKeyBreakdownLinkLQ1033(table, row);
				}
			}
		}
	}
	
	protected int paintKeyBreakdownLinkLQ1033(FlexTable tab, int row) {
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
				,"Importe minoraci\u00F3n BI en el per\u00EDodo/pendiente de adicionar a inicio del per\u00EDodo"
				,"Importe adicionado a base imponible en el per\u00EDodo"
				,"Importe BI integrado en la declaraci\u00F3n por incumplimiento de requisitos"
				,"Importe pendiente de adicionar en per\u00EDodos futuros"
				});
		int r = 2;
		int col = 0;
		for (IMod200KeysProvider key : Mod2002023LQ1033_1Key.values()) {
			Label desc = new Label(key.getDescription() );
			desc.setStyleName(AON.AON_CSS.aonMarginLeft());
			if ("Total".equals(key.getDescription()))
				desc.addStyleName(AON.AON_CSS.aonBold());
			tableDetail.setWidget(r, 0, desc);
			tableDetail.getFlexCellFormatter().setStyleName(r, 0, AON.AON_CSS.aonFiscalBorderBottom());
			col = 1;
			for (final IMod200Key k : key.getKeys() ) {
				if (k != null && k!= Mod2002023Key.LQ1033){
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
		for (IMod200KeysProvider key : Mod2002023LQ1033_2Key.values()) {
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
		
		breakdown.addClickHandler(event -> {
			container.setVisible( !container.isVisible() );
			for ( int i = 0 ; i < tab.getCellCount(boxRow); i++) {
				tab.getCellFormatter().getElement(boxRow , i).getStyle().setBackgroundColor(container.isVisible()?backgroundColor:"#FFFFFF");	
			}
			container.getElement().getStyle().setBackgroundColor(container.isVisible()?backgroundColor:"#FFFFFF");
		});		
		
		paintFooterNote(container, FOOTER_1033);
		
		return ++row;
	}
	
	@Override
	protected boolean isDisabled(IMod200Key key) {
		if ((key == Mod2002023Key.LQ1032 || key == Mod2002023Key.LQ1887 || key == Mod2002023Key.LQ1890) && 
			(callback.getMod200Object().getMod200().isChecked(Mod2002023Key.C0009) || callback.getMod200Object().getMod200().isChecked(Mod2002023Key.C0010))) {
			return false;
		}
		if (key == Mod2002023Key.LQ562 
				&& (callback.getMod200Object().getMod200().isChecked(Mod2002023Key.C0030) 
				|| callback.getMod200Object().getMod200().isChecked(Mod2002023Key.C0047))) {
			return false;
		}
		// Si marca la casilla 00022 combinada con la clave 00006, 00013, 00085, 00063, 00071, 00083 y/ó 00088 de caracteres, la casilla 00562 quedará abierta (en blanco) para su cumplimentación manual
		if (isChecked(C0022) && (isChecked(C0006) || isChecked(C0013) || isChecked(C0085) || isChecked(C0063) || isChecked(C0071) || isChecked(C0083) || isChecked(C0088))) {
			return false;
		}
		
		if ((key == Mod2002023Key.LQ520 || key == Mod2002023Key.LQ521) && 
			(callback.getMod200Object().getMod200().isChecked(Mod2002023Key.C0064))) {
			return true;
		}
		return super.isDisabled(key);
	}

}
