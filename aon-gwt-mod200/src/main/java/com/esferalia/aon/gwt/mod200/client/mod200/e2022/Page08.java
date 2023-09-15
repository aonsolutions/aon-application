// LIQUIDACION (I): RESULTADO PYG, CORRECCIONES AL RESULTADO CONTABLE
package com.esferalia.aon.gwt.mod200.client.mod200.e2022;

import java.io.Serializable;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2022.Model2002022.Model2002022PageCallback;
import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022CorrectionKey;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key;
import com.google.gwt.user.client.ui.FlexTable;

public class Page08 extends PageAbs {
	
	private static final String[] HEADERS = new String[] {
			 "Detalle de las Correcciones"
			,"Aumentos"
		 	,"Disminuciones"		 	
	};
	
	private static final String[] HEADERS2 = new String[] {
			 "Detalle de las Correcciones"
			,"Aumentos futuros"
		 	,"Disminuciones futuras"		 	
	};
	
	private enum CorrectionKey implements Serializable,IMod200KeysProvider {
		 DC01(false,Mod2002022Key.DC2305,Mod2002022Key.DC2306,"Saldo pendiente de correcciones temporarias a principio de ejercicio")
		,DC02(false,Mod2002022Key.DC2301,Mod2002022Key.DC2302,"Correcciones del ejercicio: Correcciones permanentes (excluida correcci\u00F3n I. Sociedades)")
		,DC03(false,Mod2002022Key.DC2303,Mod2002022Key.DC2304,"Correcciones del ejercicio: Correcciones temporarias con origen en el ejercicio")
		,DC04(false,Mod2002022Key.DC2307,Mod2002022Key.DC2308,"Correcciones del ejercicio: Correcciones temporarias con origen en ejercicios anteriores")
		,DC05(true ,Mod2002022Key.I0417B,Mod2002022Key.D0418B,"Total correcciones al resultado de la cuenta de p\u00E9rdidas y ganancias del ejercicio")
		,DC06(false,Mod2002022Key.DC2309,Mod2002022Key.DC2310,"Saldo pendiente de correcciones temporarias a fin de ejercicio ")
		;

		private boolean title;
		private Mod2002022Key[] keys;
		private String description;
		
		private CorrectionKey(boolean title,Mod2002022Key k1,Mod2002022Key k2,String description) {
			this.title = title;
			this.description = description; 
			this.keys = new Mod2002022Key[]{k1,k2};
		}
		public String getDescription() {
			return description;
		}
		public Mod2002022Key[] getKeys() {
			return keys;
		}
		public boolean isTitle() {
			return title;
		}
	}
	
	public Page08( Model2002022PageCallback callback ) {
		super(callback);
	}

	@Override
	protected void initializeTable() {
		
		basePanel.clear();
		
		int row = 0;
		
		basePanel.add(getTitle(AON.MSG.liquidation1Label1()));
		
		FlexTable tab1 = addTable();
		tab1.getFlexCellFormatter().setColSpan(row, 0, 3);
		
		paintKeyDescription(tab1, Mod2002022Key.LQ500, row, 0);
		paintEmptyCell(tab1, row, 1);
		paintEmptyCell(tab1, row, 2);
		paintKeyField(tab1, Mod2002022Key.LQ500, row, 3, true);
		++row;
		
		paintEmptyCell(tab1, row, 0);
		addHeaderCell(tab1, row, 1, HEADERS[1]);
		addHeaderCell(tab1, row, 2, HEADERS[2]);
		paintEmptyCell(tab1, row, 3);
		++row;

		paintKeyDescription(tab1, Mod2002022Key.LQ301, row, 0);
		paintKeyField(tab1, Mod2002022Key.LQ301, row, 1);
		paintKeyField(tab1, Mod2002022Key.LQ302, row, 2);
		paintEmptyCell(tab1, row, 3);
		++row;

		tab1.getFlexCellFormatter().setColSpan(row, 0, 3);
		paintKeyDescription(tab1, Mod2002022Key.LQ501, row, 0);
		paintEmptyCell(tab1, row, 1);
		paintEmptyCell(tab1, row, 2);
		paintKeyField(tab1, Mod2002022Key.LQ501, row, 3, true);
		++row;
		
		if (callback.getMod200Object().getMod200().isChecked(Mod2002022Key.C0009) || callback.getMod200Object().getMod200().isChecked(Mod2002022Key.C0010) ) {
			paintEmptyCell(tab1, row, 0);
			addHeaderCell(tab1, row, 1, HEADERS[1]);
			addHeaderCell(tab1, row, 2, HEADERS[2]);
			paintEmptyCell(tab1, row, 3);
			++row;
			paintDescription(tab1, Mod2002022Key.LQ1230.getDescription(), row, 0, false, 137);
			paintKeyField(tab1, Mod2002022Key.LQ1230, row, 1);
			paintKeyField(tab1, Mod2002022Key.LQ1231, row, 2);
			paintEmptyCell(tab1, row, 3);
		}
		
		basePanel.add(getTitle(AON.MSG.liquidation1Label2()));
		
		FlexTable tab2 = addTable();
		
		row = 0;
		paintEmptyCell(tab2, row, 0);
		addHeaderCell(tab2, row, 1, HEADERS[1], false);
		addHeaderCell(tab2, row, 2, HEADERS[2], false);
		row++;
		
		for (Mod2002022CorrectionKey ck : Mod2002022CorrectionKey.values()) {
			paintDescription(tab2, ck.getDescription(), row, 0, isTitle(ck.isIncreaseEnabled()?ck.getIncrease():ck.getDecrease()));
			if (ck.isIncreaseEnabled()) {
				paintKeyField(tab2, ck.getIncrease(), row, 1);		
			} else {
				paintEmptyCell(tab2, row, 1);		
			}
			if (ck.isDecreaseEnabled()) {
				paintKeyField(tab2, ck.getDecrease(), row, 2);		
			} else {
				paintEmptyCell(tab2, row, 2);		
			}
			++row; 
			
			// Detalle de determinadas casillas de correcciones al resultado contable
			if (ck.getDetail() != null) 
				row = paintKeyBreakdownLink(tab2, row, null, ck.getDetail(), HEADERS);
		}
		
		paintKeyDescription(tab2, Mod2002022Key.I0417, row, 0);
		paintKeyField(tab2, Mod2002022Key.I0417, row, 1, true);
		paintKeyField(tab2, Mod2002022Key.D0418, row, 2, true);
		
		// Detalle de Correcciones (Totales)
		basePanel.add(getTitle(AON.MSG.liquidation1Label2()));
		
		FlexTable tab3 = addTable();
		
		row = 0;
		for (CorrectionKey ck : CorrectionKey.values()) {
			if (row==0 || row==7) {			 
				addHeaderCell(tab3, row, 1, HEADERS2[1]);
				addHeaderCell(tab3, row, 2, HEADERS2[2]);
				++row;
			} else if (row==2) {
				addHeaderCell(tab3, row, 1, HEADERS[1]);
				addHeaderCell(tab3, row, 2, HEADERS[2]);
				++row;
			}
			paintDescription(tab3, ck.getDescription(), row, 0, ck.isTitle());
			int col = 1; 
			for (Mod2002022Key key : ck.getKeys() ) {
				if (key != null) {
					paintKeyField(tab3,key,row,col, true);	
				}
				++col;
			}
			++row;
		}
	}
	
}
