// LIQUIDACION (I): RESULTADO PYG, CORRECCIONES AL RESULTADO CONTABLE
package com.esferalia.aon.gwt.mod200.client.mod200.e2020;

import java.io.Serializable;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.Model2002020.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.DoubleVariable2020;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.IMod200KeysProvider;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020CorrectionKey;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class Page08 extends PageAbs {
	
	interface Page8Binder extends
			UiBinder<Widget, Page08> {
	}
	
	private static final String[] HEADERS = new String[]{"Detalle de las Correcciones"
			,"Aumentos"
		 	,"Disminuciones"		 	
	};
	
	private static final String[] HEADERS2 = new String[]{"Detalle de las Correcciones"
			,"Aumentos futuros"
		 	,"Disminuciones futuras"		 	
	};
	
	private static enum CorrectionKey implements Serializable,IMod200KeysProvider {
		 DC01(false,Mod2002020Key.DC2305,Mod2002020Key.DC2306,"Saldo pendiente de correcciones temporarias a principio de ejercicio")
		,DC02(false,Mod2002020Key.DC2301,Mod2002020Key.DC2302,"Correcciones del ejercicio: Correcciones permanentes (excluida correcci\u00F3n I. Sociedades)")
		,DC03(false,Mod2002020Key.DC2303,Mod2002020Key.DC2304,"Correcciones del ejercicio: Correcciones temporarias con origen en el ejercicio")
		,DC04(false,Mod2002020Key.DC2307,Mod2002020Key.DC2308,"Correcciones del ejercicio: Correcciones temporarias con origen en ejercicios anteriores")
		,DC05(true ,Mod2002020Key.I0417B,Mod2002020Key.D0418B,"Total correcciones al resultado de la cuenta de p\u00E9rdidas y ganancias del ejercicio")
		,DC06(false,Mod2002020Key.DC2309,Mod2002020Key.DC2310,"Saldo pendiente de correcciones temporarias a fin de ejercicio ")
		;

		private boolean title;
		private Mod2002020Key[] keys;
		private String description;
		
		private CorrectionKey(boolean title,Mod2002020Key k1,Mod2002020Key k2,String description) {
			this.title = title;
			this.description = description; 
			this.keys = new Mod2002020Key[]{k1,k2};
		}
		public String getDescription() {
			return description;
		}
		public Mod2002020Key[] getKeys() {
			return keys;
		}
		public boolean isTitle() {
			return title;
		}
	}
	
	private static final Page8Binder page8Binder = GWT
			.create(Page8Binder.class);
	
	@UiField(provided = true)
	FlexTable table;
	@UiField(provided = true)
	FlexTable table1;
	
	ListBox opeVol;

	public Page08( Model200PageCallback callback ) {
		super(callback);
		table = new FlexTable();
		table1 = new FlexTable();
		Widget ui = page8Binder.createAndBindUi(this);
		initWidget(ui);
		initializeTable();
	}

	@Override
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		
		table1.setWidth("100%");
		table1.setCellSpacing(0);

		int row = 0;
		
		Label desc = new Label(AON.MSG.liquidation1Label1());
		desc.setStyleName(AON.AON_CSS.aonBold());
		table.setWidget(row, 0, desc);
		table.getFlexCellFormatter().setColSpan(row, 0, 3);
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonTextUnderline());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
		++row;
		++row;
		
		paintKeyDescription(table, Mod2002020Key.LQ500, row, 0);
		paintEmptyCell(table, row, 1);
		paintKeyField(table, Mod2002020Key.LQ500, row, 2);
		++row;

		paintKeyDescription(table, Mod2002020Key.LQ301, row, 0);
		paintKeyField(table, Mod2002020Key.LQ301, row, 1);
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonWidth150());
		paintKeyField(table, Mod2002020Key.LQ302, row, 2);
		table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonWidth150());
		++row;

		paintKeyDescription(table, Mod2002020Key.LQ501, row, 0);
		paintEmptyCell(table, row, 1);
		paintKeyField(table, Mod2002020Key.LQ501, row, 2);
		++row;
		
		if (callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0009) || callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0010) ) {
			paintKeyDescription(table, Mod2002020Key.LQ1230, row, 0);
			paintKeyField(table, Mod2002020Key.LQ1230, row, 1);
			table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonWidth150());
			paintKeyField(table, Mod2002020Key.LQ1231, row, 2);
			table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonWidth150());
			++row;
		}
		
		// Volumen de operaciones
		desc = new Label("Cifra de negocios");
		desc.setStyleName(AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonTextUnderline());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingTop());
		table.setWidget(row, 0, desc);
		++row;
		
		desc = new Label("Importe neto de la cifra de negocios durante los doce meses anteriores a la fecha de inicio del periodo impositivo");
		table.setWidget(row, 0, desc);
		++row;
		
		opeVol = new ListBox();
		opeVol.addItem("0 - No consta");
		opeVol.addItem("1 - Inferior a 20 millones de euros");
		opeVol.addItem("2 - Al menos 20 millones de euros pero inferior a 60 millones de euros");
		opeVol.addItem("3 - Al menos 60 millones de euros");
		table.setWidget(row, 0, opeVol);
		++row;
	
		// Detalle de Correcciones 
		desc = new Label(AON.MSG.liquidation1Label2());
		desc.setStyleName(AON.AON_CSS.aonBold());
		table.setWidget(row, 0, desc);
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonTextUnderline());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingTop());
		table.getFlexCellFormatter().setColSpan(row, 0, 0);
		table.setWidget(row, 1, new Label(AON.MSG.increase()));		
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonTextUnderline());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonPaddingTop());
		table.setWidget(row, 2, new Label(AON.MSG.decrease()));		
		table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonTextUnderline());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonPaddingTop());
		++row;
		
		for (Mod2002020CorrectionKey ck : Mod2002020CorrectionKey.values()) {
			paintDescription(table, ck.getDescription(), row,0,isTitle(ck.isIncreaseEnabled()?ck.getIncrease():ck.getDecrease()));
			if (ck.isIncreaseEnabled()) {
				paintKeyField(table, ck.getIncrease(), row, 1);		
			} else {
				paintEmptyCell(table, row, 1);		
			}
			if (ck.isDecreaseEnabled()) {
				paintKeyField(table, ck.getDecrease(), row, 2);		
			} else {
				paintEmptyCell(table, row, 2);		
			}
			++row; 
			
			// Detalle de determinadas casillas de correcciones al resultado contable
			if (ck.getDetail() != null) 
				row = paintKeyBreakdownLink(table,row,null,ck.getDetail(),HEADERS);
		}
		
		paintKeyDescription(table, Mod2002020Key.I0417, row, 0);
		paintKeyField(table, Mod2002020Key.I0417, row, 1);
		paintKeyField(table, Mod2002020Key.D0418, row, 2);
		++row;

		// Detalle de Correcciones (Totales)
		row = 0;
		for (CorrectionKey ck : CorrectionKey.values()) {
			if (row==0 || row==7) {
				paintTitle(table1, HEADERS2[1], row, 1, false);
				paintTitle(table1, HEADERS2[2], row, 2, false);
				++row;
			} else if (row==2) {
				paintTitle(table1, HEADERS[1], row, 1, false);
				paintTitle(table1, HEADERS[2], row, 2, false);
				++row;
			}
			paintDescription(table1, ck.getDescription(), row, 0, ck.isTitle());
			int col = 1; 
			for (Mod2002020Key key : ck.getKeys() ) {
				if (key != null) {
					paintKeyField(table1,key,row,col);	
				}
				++col;
			}
			++row;
		}
	}
	
	@Override
	protected void populate() {
		DoubleVariable2020 bv = new DoubleVariable2020( Mod2002020Key.VOLOPE );
		bv.setValue( (double)opeVol.getSelectedIndex() );
		callback.getMod200Object().getMod200().addVariable(bv);
	}
	
	@Override
	public void dump() {
		super.dump();
		DoubleVariable2020 dv = callback.getMod200Object().getMod200().getKeysMap().get(Mod2002020Key.VOLOPE);
		int index = 0;
		if (dv != null) {
			index = dv.getValue().intValue();
		}
		opeVol.setSelectedIndex(index);
	}
	
}
