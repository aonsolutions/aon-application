package com.esferalia.aon.gwt.fiscal.client.mod200.e2016;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2016.Model2002016.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.IMod200KeysProvider;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016LQ1032Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016LQ1033_1Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016LQ1033_2Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016LQ547Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016LQ554Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016LQ561Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016LQ579Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Page09 extends PageAbs {
	
	interface PageBinder extends
			UiBinder<Widget, Page09> {
	}

	private static final PageBinder pageBinder = GWT
			.create(PageBinder.class);
	private static final String[] HEADERS_2 = new String[]{"",
		 AON.MSG.previousPending()
		,AON.MSG.current()
		,AON.MSG.futurePending()		
	};
	private static final String[] HEADERS_1 = new String[]{""
		,AON.MSG.cooperativeResult()
	 	,AON.MSG.extraCooperativeResult()
	};
	private static final String[] HEADERS_3 = new String[]{""
			,"Derecho a reducir la B.I. generado en el per\u00EDodo/pendiente de aplicar a inicio del per\u00EDodo"
		 	,"Reducci\u00F3n B.I. aplicada"
		 	,"Reducci\u00F3n B.I. pendiente de aplicar en per\u00EDodos futuros"
		};
	
	public Page09( Model200PageCallback callback ) {
		super(callback);
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		initializeTable();
	}
	
	@Override
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		
		ColumnFormatter cf = table.getColumnFormatter();
		cf.setWidth(1, "200px");

		int row = 0;
		boolean margin = false;
		for (Mod2002016Key key : Mod2002016Constants.LIQUIDATION_II_KEYS) {
			if (callback.getMod200Object().isVisible(key)) {
				if (key == Mod2002016Key.LQ578) {
					paintDescription(table, "Entidades navieras en r\u00E9gimen de tributaci\u00F3n en funci\u00F3n del tonelaje", row,0, true);
					row++;
					margin = true;
				}
				if (key == Mod2002016Key.LQ1029) {
					paintDescription(table, "Entidades que forman parte de grupos de consolidaci\u00F3n fiscal", row,0, true);
					row++;
					margin = true;
				}
				if (key == Mod2002016Key.LQ1033) {
					paintDescription(table, "S\u00F3lo entidades de reducida dimensi\u00F3n", row,0, true);
					row++;
					margin = true;
				}
				if (key == Mod2002016Key.LQ553) {
					paintDescription(table, "S\u00F3lo sociedades cooperativas", row,0, true);
					row++;
					margin = true;
				}
				if (key == Mod2002016Key.LQ555) {
					paintDescription(table, "S\u00F3lo agrupaciones espa\u00F1olas de inter\u00E9s econ\u00F3mico y UTES", row,0, true);
					row++;
					margin = true;
				}
				if (key == Mod2002016Key.LQ559) {
					paintDescription(table, "S\u00F3lo entidades ZEC", row,0, true);
					row++;
					margin = true;
				}
				if (key == Mod2002016Key.LQ520) {
					paintDescription(table, "S\u00F3lo SOCIMIS", row,0, true);
					row++;
					margin = true;
				}
				if (key == Mod2002016Key.LQ545) {
					paintDescription(table, "Rentas que no limitan la compensaci\u00F3n de bases imponibles y cuotas negativas", row,0, true);
					row++;
					margin = true;
				}
				if (key == Mod2002016Key.LQ560) {
					paintDescription(table, "S\u00F3lo sociedades cooperativas", row,0, true);
					row++;
					margin = true;
				}
				if (key == Mod2002016Key.LQ550 || key == Mod2002016Key.LQ558 || key == Mod2002016Key.LQ562) {
					margin = false;
				}
								
				row = paintKey(key,row);
				if (margin) {
					table.getCellFormatter().addStyleName(row-1, 0, AON.AON_CSS.aonPaddingLeft());
				}
/**/			if (key == Mod2002016Key.LQ554) {
					row = paintKeyBreakdownLink(table,row,AON.MSG.cooperativeRegime()
							,Mod2002016LQ554Key.values(),HEADERS_1);
				}
				if (key == Mod2002016Key.LQ561) {
					row = paintKeyBreakdownLink(table,row,Mod2002016Key.LQ561.getDescription()
							,Mod2002016LQ561Key.values(),HEADERS_2);
				}
/**/			if (key == Mod2002016Key.LQ579) {
					row = paintKeyBreakdownLink(table,row,Mod2002016Key.LQ579.getDescription()
							,Mod2002016LQ579Key.values(),null);
				}
/**/			if (key == Mod2002016Key.LQ1032) {
					row = paintKeyBreakdownLink(table,row,Mod2002016Key.LQ1032.getDescription()
							,Mod2002016LQ1032Key.values(),HEADERS_3);
				}
/**/			if (key == Mod2002016Key.LQ547) {
					row = paintKeyBreakdownLink(table,row,Mod2002016Key.LQ547.getDescription()
							,Mod2002016LQ547Key.values(),HEADERS_2);
				}
/**/			if (key == Mod2002016Key.LQ1033) {
					row = paintKeyBreakdownLinkLQ1033(table,row,Mod2002016Key.LQ1033.getDescription());
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
		container.setVisible(false);
		final String backgroundColor = "#E0FFFF";
		Label label1 = new Label("Reducci\u00F3n en base imponible");
		label1.setStyleName(AON.AON_CSS.aonBold());
		label1.addStyleName(AON.AON_CSS.aonTextUnderline());
		container.add(label1);
		FlexTable tableDetail = getFlexTable(container,row,label, new String[]{
				 "Ejercicio de generaci\u00F3n"
				,"Importe minoraci\u00F3n B.I. en el per\u00EDodo/pen diente de adicionar a inicio del per\u00EDodo"
				,"Importe adicionado a base imponible en el per\u00EDodo"
				,"Importe pendiente de adicionar en per\u00EDodos futuros"
				}				
				);
		int r = 1;
		int col = 0;
		for (IMod200KeysProvider key : Mod2002016LQ1033_1Key.values()) {
			Label desc = new Label(key.getDescription() );
			tableDetail.setWidget(r, 0, desc);
			tableDetail.getFlexCellFormatter().setStyleName(r, 0, AON.AON_CSS.aonFiscalBorderBottom());
			col = 1;
			for (final Mod2002016Key k : key.getKeys() ) {
				if (k != null){
					paintKeyField(tableDetail, k, r, col, 9);
				}
				++col;
			}
			++r;
		}
		Label label2 = new Label("Dotaci\u00F3n de la reserva");
		label2.setStyleName(AON.AON_CSS.aonBold());
		label2.addStyleName(AON.AON_CSS.aonTextUnderline());
		container.add(label2);
		FlexTable tableDetail2 = getFlexTable(container,row,label, new String[]{
				 "Ejercicio de generaci\u00F3n"
				,"Importe reserva a dotar"
				,"Importe reserva dotada"
				,"Importe reserva pendiente dotaci\u00F3n"
				,"Reserva dispuesta"				
				}				
				);
		r = 1;
		col = 0;
		for (IMod200KeysProvider key : Mod2002016LQ1033_2Key.values()) {
			Label desc = new Label(key.getDescription() );
			tableDetail2.setWidget(r, 0, desc);
			tableDetail2.getFlexCellFormatter().setStyleName(r, 0, AON.AON_CSS.aonFiscalBorderBottom());
			col = 1;
			for (final Mod2002016Key k : key.getKeys() ) {
				if (k != null){
					paintKeyField(tableDetail2, k, r, col, 9);
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
		
		return ++row;
	}
	
	@Override
	protected void populate() {}
}
