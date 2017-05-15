package com.esferalia.aon.gwt.fiscal.client.mod200.e2016;

import java.io.Serializable;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2016.Model2002016.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.IMod200KeysProvider;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016CorrectionKey;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Page08 extends PageAbs {
	
	interface Page8Binder extends
			UiBinder<Widget, Page08> {
	}
	
	private static enum CorrectionKey implements Serializable,IMod200KeysProvider {
		 DC01(true ,Mod2002016Key.DC001,Mod2002016Key.DC002,null               ,null               ,"Correcciones Permanentes")
		,DC02(true ,Mod2002016Key.DC003,Mod2002016Key.DC004,Mod2002016Key.DC005,Mod2002016Key.DC006,"Correcciones temporarias con origen en el ejercicio")
		,DC03(false,Mod2002016Key.DC007,Mod2002016Key.DC008,Mod2002016Key.DC009,Mod2002016Key.DC010,"Por amortizaciones")
		,DC04(false,Mod2002016Key.DC011,Mod2002016Key.DC012,Mod2002016Key.DC013,Mod2002016Key.DC014,"Por deterioros de valor")
		,DC05(false,Mod2002016Key.DC015,Mod2002016Key.DC016,Mod2002016Key.DC017,Mod2002016Key.DC018,"Por pensiones")
		,DC06(false,Mod2002016Key.DC019,Mod2002016Key.DC020,Mod2002016Key.DC021,Mod2002016Key.DC022,"Por fondo de comercio")
		,DC07(false,Mod2002016Key.DC023,Mod2002016Key.DC024,Mod2002016Key.DC025,Mod2002016Key.DC026,"Resto")
		,DC08(true ,Mod2002016Key.DC027,Mod2002016Key.DC028,Mod2002016Key.DC029,Mod2002016Key.DC030,"Correcc. temporarias con origen en ejerc. anteriores")
		,DC09(false,Mod2002016Key.DC031,Mod2002016Key.DC032,Mod2002016Key.DC033,Mod2002016Key.DC034,"Por amortizaciones")
		,DC10(false,Mod2002016Key.DC035,Mod2002016Key.DC036,Mod2002016Key.DC037,Mod2002016Key.DC038,"Por deterioros de valor")
		,DC11(false,Mod2002016Key.DC039,Mod2002016Key.DC040,Mod2002016Key.DC041,Mod2002016Key.DC042,"Por pensiones")
		,DC12(false,Mod2002016Key.DC043,Mod2002016Key.DC044,Mod2002016Key.DC045,Mod2002016Key.DC046,"Por fondo de comercio")
		,DC13(false,Mod2002016Key.DC047,Mod2002016Key.DC048,Mod2002016Key.DC049,Mod2002016Key.DC050,"Resto")
		,DC14(true ,Mod2002016Key.DC051,Mod2002016Key.DC052,Mod2002016Key.DC053,Mod2002016Key.DC054,"TOTAL correcc. al resultado")
		;

		private boolean title;
		private Mod2002016Key[] keys;
		private String description;
		
		private CorrectionKey(boolean title,Mod2002016Key k1,Mod2002016Key k2,
				Mod2002016Key k3,Mod2002016Key k4,String description) {
			this.title = title;
			this.description = description; 
			this.keys = new Mod2002016Key[]{k1,k2,k3,k4};
		}
		public String getDescription() {
			return description;
		}
		public Mod2002016Key[] getKeys() {
			return keys;
		}
		public boolean isTitle() {
			return title;
		}
	}


	
	private static final Page8Binder page8Binder = GWT
			.create(Page8Binder.class);
	
	@UiField(provided = true)
	FlexTable table1;

	public Page08( Model200PageCallback callback ) {
		super(callback);
		table1 = new FlexTable();
		Widget ui = page8Binder.createAndBindUi(this);
		initWidget(ui);
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
		
		paintKeyDescription(table, Mod2002016Key.LQ500, row, 0);
		paintEmptyCell(table, row, 1);
		paintKeyField(table, Mod2002016Key.LQ500, row, 2);
		++row;

		paintKeyDescription(table, Mod2002016Key.LQ301, row, 0);
		paintKeyField(table, Mod2002016Key.LQ301, row, 1);
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonWidth150());
		paintKeyField(table, Mod2002016Key.LQ302, row, 2);
		table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonWidth150());
		++row;

		paintKeyDescription(table, Mod2002016Key.LQ501, row, 0);
		paintEmptyCell(table, row, 1);
		paintKeyField(table, Mod2002016Key.LQ501, row, 2);
		++row;
		
		paintKeyDescription(table, Mod2002016Key.LQ1230, row, 0);
		paintKeyField(table, Mod2002016Key.LQ1230, row, 1);
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonWidth150());
		paintKeyField(table, Mod2002016Key.LQ1231, row, 2);
		table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonWidth150());
		++row;

		paintEmptyCell(table, row, 0);
		paintEmptyCell(table, row, 1);
		paintEmptyCell(table, row, 2);
		++row;
		
		desc = new Label(AON.MSG.liquidation1Label2());
		desc.setStyleName(AON.AON_CSS.aonBold());
		table.setWidget(row, 0, desc);
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonTextUnderline());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
		table.getFlexCellFormatter().setColSpan(row, 0, 0);
		table.setWidget(row, 1, new Label(AON.MSG.increase()));
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonTextUnderline());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table.setWidget(row, 2, new Label(AON.MSG.decrease()));
		table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonTextUnderline());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		++row;
		
		for (Mod2002016CorrectionKey ck : Mod2002016CorrectionKey.values()) {
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
		}
		
		paintKeyDescription(table, Mod2002016Key.I0417, row, 0);
		paintKeyField(table, Mod2002016Key.I0417, row, 1);
		paintKeyField(table, Mod2002016Key.D0418, row, 2);
		++row;
		
		row = 0;
		paintTitle(table1, AON.MSG.yearCorrections(), row, 1, false);
		table1.getFlexCellFormatter().setColSpan(row, 1, 2);
		paintTitle(table1, AON.MSG.pendingCorrections(), row, 2, false);
		table1.getFlexCellFormatter().setColSpan(row, 2, 2);
		
		++row;
		paintTitle(table1, AON.MSG.fiscalCorrections(), row, 0, false);
		paintTitle(table1, AON.MSG.increase(), row, 1, false);
		paintTitle(table1, AON.MSG.decrease(), row, 2, false);
		paintTitle(table1, AON.MSG.increase(), row, 3, false);
		paintTitle(table1, AON.MSG.decrease(), row, 4, false);
		++row;
		
		for (CorrectionKey ck : CorrectionKey.values()) {
			paintDescription(table1, ck.getDescription(), row, 0, ck.isTitle());
			int col = 1; 
			for (Mod2002016Key key : ck.getKeys() ) {
				if (key != null) {
					paintKeyField(table1,key,row,col);	
				}
				++col;
			}
			++row;
		}
	}
	
	@Override
	protected void populate() {}
}
