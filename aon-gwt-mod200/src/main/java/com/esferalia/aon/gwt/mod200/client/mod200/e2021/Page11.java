// LIQUIDACION (IV): OTRAS DEDUCCIONES, CUOTA LIQUIDA POSITIVA
package com.esferalia.aon.gwt.mod200.client.mod200.e2021;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2021.Model2002021.Model200PageCallback;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN082Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN1040Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN1041Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN565Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN584Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN585Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN588Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN590Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Key;
import com.google.gwt.user.client.ui.FlexTable;

public class Page11 extends PageAbs {

	private static final String[] HEADERS_1 = new String[] {
			null,
			AON.MSG.pendingDeduction(),
			AON.MSG.current(),
			AON.MSG.futurePending()
	};
	
	private static final String[] HEADERS_2 = new String[] {
			null,
			AON.MSG.pendingDeduction(),
			AON.MSG.reducedDeduction(),
			AON.MSG.current(),
			"Importe abonado por insuficiencia de cuota",
			"Deducci\u00F3n resto del grupo"
	};
	
	private static final String[] HEADERS_3 = new String[] {
			null,
			AON.MSG.deductionTaxablebase(),
			"Importe generado/pendiente al principio del periodo",
			AON.MSG.liquiMsg31(),
			AON.MSG.pendingAmount()
	};
	
	private static final String FOOTER_1 = "(*) S\u00F3lo debe cumplimentarse si tiene deducciones pendientes de aplicar correspondientes a un per\u00EDodo impositivo anterior iniciado en 2021.";
	private static final String FOOTER_588_1 = "En este apartado no se deben indicar las deducciones por producciones cinematogr\u00E1ficas extranjeras (art. 36.2 LIS) que se declaran en las casillas [01039] de la p\u00E1g. 14 y, en su caso, en la casilla [01042] de la p\u00E1g. 14 bis.";
	private static final String FOOTER_588_2 = "(****) Programas cuya vigencia se inicia a partir de 2022: S\u00F3lo debe cumplimentarse esta fila si la entidad tiene un per\u00EDodo impositivo que no coincida con el a\u00F1o natural y ha realizado gastos con derecho a deducci\u00F3n a partir de 2022.";
	private static final String FOOTER_082_1 = "Entre otros requisitos, ser\u00E1 necesario que transcurra, al menos, uno a\u00F1o desde la finalizaci\u00F3n del per\u00EDodo impositivo en que se gener\u00F3 la deducci\u00F3n, sin que la misma haya sido objeto de aplicaci\u00F3n.";

	public Page11( Model200PageCallback callback ) {
		super(callback);
		addBasePanel();
		initializeTable();
	}
	
	@Override
	protected void initializeTable() {
		
		basePanel.clear();
		
		basePanel.add(getTitle(AON.MSG.otherDeductions()));
		
		FlexTable table = addTable();
		
		int row = 0;
		for (final Mod2002021Key key : Mod2002021Constants.LIQUIDATION_IV_KEYS) {
			row = paintKey(table,key,row);
			if (callback.getMod200Object().isVisible(key)) {
				if (key == Mod2002021Key.BN585) {
					row = paintKeyBreakdownLink(table, row, Mod2002021Key.BN585, Mod2002021BN585Key.values(), HEADERS_1, FOOTER_1);
				} 
				if (key == Mod2002021Key.BN584) {
					row = paintKeyBreakdownLink(table, row, Mod2002021Key.BN584, Mod2002021BN584Key.values(), HEADERS_1, FOOTER_1);
				} 
				if (key == Mod2002021Key.BN588) {
					row = paintKeyBreakdownLink(table,row,Mod2002021Key.BN588,Mod2002021BN588Key.values(),HEADERS_1, FOOTER_588_1, FOOTER_1, FOOTER_588_2);
				} 
				if (key == Mod2002021Key.BN082) {
					row = paintKeyBreakdownLink(table,row,Mod2002021Key.BN082,Mod2002021BN082Key.values(),HEADERS_2, FOOTER_082_1, FOOTER_1);
				}
				
				// FALTA - ESTE DESGLOSE ESTE AÑO TIENE DOS APARTADOS, VER SI AL FINAL DEJAN LOS DOS APARTADOS PARA 
				// VER COMO SE PONE
				if (key == Mod2002021Key.BN565) {
					row = paintKeyBreakdownLink(table,row,Mod2002021Key.BN565,Mod2002021BN565Key.values(),HEADERS_1, FOOTER_1);
				}
				//----------
				
				if (key == Mod2002021Key.BN590) {
					row = paintKeyBreakdownLink(table,row,Mod2002021Key.BN590,Mod2002021BN590Key.values(),HEADERS_1, FOOTER_1);
				}
				if (key == Mod2002021Key.BN1040) {
					row = paintKeyBreakdownLink(table,row,Mod2002021Key.BN1040,Mod2002021BN1040Key.values(),HEADERS_3, FOOTER_1);
				}
				if (key == Mod2002021Key.BN1041) {
					row = paintKeyBreakdownLink(table,row,Mod2002021Key.BN1041,Mod2002021BN1041Key.values(),HEADERS_3, FOOTER_1);
				}
				
				// FALTA - COMPROBAR QUE ESTAS DOS CASILLAS ADICIONALES SIGUEN APARECIENDO EN EL DISEÑO DEL REGISTRO
				// PUES EN EL MODELO NO ESTAN
				if (key == Mod2002021Key.BN1039) {
					FlexTable table2 = new FlexTable();
					table2.setWidth("100%");
					table2.setCellSpacing(0);
					table2.getColumnFormatter().setWidth(1, "150px");
					table2.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());					
					paintKey(table2,Mod2002021Key.BN1039M,0);
					table.setWidget(row, 0, table2);
					row++;
				}				
				if (key == Mod2002021Key.BN2314) {
					FlexTable table2 = new FlexTable();
					table2.setWidth("100%");
					table2.setCellSpacing(0);
				   	table2.getColumnFormatter().setWidth(1, "150px");
					table2.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());					
					paintKey(table2,Mod2002021Key.BN2314M,0);
					table.setWidget(row, 0, table2);
					row++;
				}
				//----------
				
			}
		}
	}
	
	@Override
	protected void populate() {		
	}
	
}
