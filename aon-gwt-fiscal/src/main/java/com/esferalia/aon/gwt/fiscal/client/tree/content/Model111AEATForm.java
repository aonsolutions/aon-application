package com.esferalia.aon.gwt.fiscal.client.tree.content;

import java.util.LinkedHashMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

public class Model111AEATForm extends Model111Form{

	public Model111AEATForm() {
		super();
	}
/*
	,CT_H1 ("111-CT-H1" ,null,Administration.COMMON_TERRITORY,"I. Rendimientos del trabajo")
	,CT_H11("111-CT-H11",null,Administration.COMMON_TERRITORY,"Rendimientos dinerarios")
	,CT_C01("111-CT-01" ,null,Administration.COMMON_TERRITORY,"01 - N\u00AA de Perceptores")
	,CT_C02("111-CT-02" ,null,Administration.COMMON_TERRITORY,"02 - Importe de las percepciones")
	,CT_C03("111-CT-03" ,null,Administration.COMMON_TERRITORY,"03 - Importe de las retenciones")
	,CT_H12("111-CT-H12",null,Administration.COMMON_TERRITORY,"Rendimientos en especie")
	,CT_C04("111-CT-04" ,null,Administration.COMMON_TERRITORY,"04 - N\u00AA de Perceptores")
	,CT_C05("111-CT-05" ,null,Administration.COMMON_TERRITORY,"05 - Valor percepciones en especie")
	,CT_C06("111-CT-06" ,null,Administration.COMMON_TERRITORY,"06 - Importe de los ingresos a cuenta")
	,CT_H2 ("111-CT-H2" ,null,Administration.COMMON_TERRITORY,"II. Rendimientos de actividades econ\u00F3micas")
	,CT_H21("111-CT-H21",null,Administration.COMMON_TERRITORY,"Rendimientos dinerarios")
	,CT_C07("111-CT-07" ,null,Administration.COMMON_TERRITORY,"07 - N\u00AA de Perceptores")
	,CT_C08("111-CT-08" ,null,Administration.COMMON_TERRITORY,"08 - Importe de las percepciones")
	,CT_C09("111-CT-09" ,null,Administration.COMMON_TERRITORY,"09 - Importe de las retenciones")
	,CT_H22("111-CT-H22",null,Administration.COMMON_TERRITORY,"Rendimientos en especie")
	,CT_C10("111-CT-10" ,null,Administration.COMMON_TERRITORY,"10 - N\u00AA de Perceptores")
	,CT_C11("111-CT-11" ,null,Administration.COMMON_TERRITORY,"11 - Valor percepciones en especie")
	,CT_C12("111-CT-12" ,null,Administration.COMMON_TERRITORY,"12 - Importe de los ingresos a cuenta")
	,CT_H3 ("111-CT-H3" ,null,Administration.COMMON_TERRITORY,"III. Premios por la participaci\u00F3n en juegos, concursos, rifas o combinaciones aleatorias")
	,CT_H31("111-CT-H31",null,Administration.COMMON_TERRITORY,"Premios dinerarios")
	,CT_C13("111-CT-13" ,null,Administration.COMMON_TERRITORY,"13 - N\u00AA de Perceptores")
	,CT_C14("111-CT-14" ,null,Administration.COMMON_TERRITORY,"14 - Importe de las percepciones")
	,CT_C15("111-CT-15" ,null,Administration.COMMON_TERRITORY,"15 - Importe de las retenciones")
	,CT_H32("111-CT-H32",null,Administration.COMMON_TERRITORY,"Premios en especie")
	,CT_C16("111-CT-16" ,null,Administration.COMMON_TERRITORY,"16 - N\u00AA de Perceptores")
	,CT_C17("111-CT-17" ,null,Administration.COMMON_TERRITORY,"17 - Valor percepciones en especie")
	,CT_C18("111-CT-18" ,null,Administration.COMMON_TERRITORY,"18 - Importe de los ingresos a cuenta")
	,CT_CH4("111-CT-H4" ,null,Administration.COMMON_TERRITORY,"IV. Ganancias patrimoniales derivadas de los aprovechamientos forestales de los vecinos en los montes p\u00FAblicos")
	,CT_H41("111-CT-H41",null,Administration.COMMON_TERRITORY,"Percepciones dinerarias")
	,CT_C19("111-CT-19" ,null,Administration.COMMON_TERRITORY,"19 - N\u00AA de Perceptores")
	,CT_C20("111-CT-20" ,null,Administration.COMMON_TERRITORY,"20 - Importe de las percepciones")
	,CT_C21("111-CT-21" ,null,Administration.COMMON_TERRITORY,"21 - Importe de las retenciones")
	,CT_H42("111-CT-H42",null,Administration.COMMON_TERRITORY,"Percepciones en especie")
	,CT_C22("111-CT-22" ,null,Administration.COMMON_TERRITORY,"22 - N\u00AA de Perceptores")
	,CT_C23("111-CT-23" ,null,Administration.COMMON_TERRITORY,"23 - Valor percepciones en especie")
	,CT_C24("111-CT-24" ,null,Administration.COMMON_TERRITORY,"24 - Importe de los ingresos a cuenta")
	,CT_CT5("111-CT-H5" ,null,Administration.COMMON_TERRITORY,"V. Contraprestaciones por la cesi\u00F3n de derechos de imagen, ingresos a cuenta previstos en el art\u00EDculo 92.8 de la Ley del Impuesto ")
	,CT_H51("111-CT-H51",null,Administration.COMMON_TERRITORY,"Contrapartidas dinerarias o en especie")
	,CT_C25("111-CT-25" ,null,Administration.COMMON_TERRITORY,"25 - N\u00AA de Perceptores")
	,CT_C26("111-CT-26" ,null,Administration.COMMON_TERRITORY,"26 - Contraprestaciones satisfechas")
	,CT_C27("111-CT-27" ,null,Administration.COMMON_TERRITORY,"27 - Importe de los ingresos a cuenta")
	,CT_H6 ("111-CT-H6" ,null,Administration.COMMON_TERRITORY,"Total liquidaci\u00F3n")
	,CT_C28("111-CT-28" ,null,Administration.COMMON_TERRITORY,"28 - Suma de retenciones e ingresos a cuenta")
	,CT_C29("111-CT-29" ,null,Administration.COMMON_TERRITORY,"29 - A deducir (exclusivamentes en caso de decl. complm.): Resultados a ingresar de anteriores declaraciones por el mismo concepto, ejercicio y periodo.")
	,CT_C30("111-CT-30" ,null,Administration.COMMON_TERRITORY,"30 - Resultado a ingresar")
 */
	
	private static LinkedHashMap<String,LinkedHashMap<String, Mod111Key[]>> KEYS = 
			new LinkedHashMap<String, LinkedHashMap<String, Mod111Key[]>>();
	static {
		KEYS.put(AON.MSG.mod111Header1(), new LinkedHashMap<String, Mod111Key[]>());
		KEYS.get(AON.MSG.mod111Header1()).put(AON.MSG.moneyYield() 
				,new Mod111Key[]{Mod111Key.CT_C01,Mod111Key.CT_C02,Mod111Key.CT_C03});
		KEYS.get(AON.MSG.mod111Header1()).put(AON.MSG.inKindYield()
				,new Mod111Key[]{Mod111Key.CT_C04,Mod111Key.CT_C05,Mod111Key.CT_C06});
		KEYS.put(AON.MSG.mod111Header2(), new LinkedHashMap<String, Mod111Key[]>());
		KEYS.get(AON.MSG.mod111Header2()).put(AON.MSG.moneyYield() 
				,new Mod111Key[]{Mod111Key.CT_C07,Mod111Key.CT_C08,Mod111Key.CT_C09});
		KEYS.get(AON.MSG.mod111Header2()).put(AON.MSG.inKindYield()
				,new Mod111Key[]{Mod111Key.CT_C10,Mod111Key.CT_C11,Mod111Key.CT_C12});
		KEYS.put(AON.MSG.mod111Header3(), new LinkedHashMap<String, Mod111Key[]>());
		KEYS.get(AON.MSG.mod111Header3()).put(AON.MSG.moneyYield() 
				,new Mod111Key[]{Mod111Key.CT_C13,Mod111Key.CT_C14,Mod111Key.CT_C15});
		KEYS.get(AON.MSG.mod111Header3()).put(AON.MSG.inKindYield()
				,new Mod111Key[]{Mod111Key.CT_C16,Mod111Key.CT_C17,Mod111Key.CT_C18});
		KEYS.put(AON.MSG.mod111Header4(), new LinkedHashMap<String, Mod111Key[]>());
		KEYS.get(AON.MSG.mod111Header4()).put(AON.MSG.moneyYield() 
				,new Mod111Key[]{Mod111Key.CT_C19,Mod111Key.CT_C20,Mod111Key.CT_C21});
		KEYS.get(AON.MSG.mod111Header4()).put(AON.MSG.inKindYield()
				,new Mod111Key[]{Mod111Key.CT_C22,Mod111Key.CT_C23,Mod111Key.CT_C24});
		KEYS.put(AON.MSG.mod111Header5(), new LinkedHashMap<String, Mod111Key[]>());
		KEYS.get(AON.MSG.mod111Header5()).put(AON.MSG.moneyInKindReturn()
				,new Mod111Key[]{Mod111Key.CT_C25,Mod111Key.CT_C26,Mod111Key.CT_C27});
	}
	private static Mod111Key[] TOTAL = new Mod111Key[]{Mod111Key.CT_C28,Mod111Key.CT_C29,Mod111Key.CT_C30};
	
	
	@Override
	protected void paintTable(final FiscalModel fm) {
		FlexTable table = new FlexTable();
		table.setStyleName(AON.AON_CSS.aonFiscalModelDataTable());
		table.setCellSpacing(0);
		
		int row = 0;
		table.setWidget(row, 0, new Label( "" ));
		table.setWidget(row, 1, new Label( AON.MSG.receivers() ));
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonItalic());
		
		table.setWidget(row, 2, new Label( AON.MSG.perceptions() ));
		table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonFiscalModelDataTableData());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonItalic());
		
		table.setWidget(row, 3, new Label( AON.MSG.retentionAccountShort() ));
		table.getFlexCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonFiscalModelDataTableData());
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonItalic());
		
		for (String title : KEYS.keySet()) {
			row++;
			table.setWidget(row, 0, new Label( title ));
			table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableTitle());
			table.getFlexCellFormatter().setColSpan(row, 0, 4);
			for (String subtitle : KEYS.get(title).keySet()) {
				row++;
				table.setWidget(row, 0, new Label( subtitle ));
				table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableSubtitle());
				int col = 0;
				for (Mod111Key key : KEYS.get(title).get(subtitle)) {
					col++;
					FlowPanel p = new FlowPanel();
					p.setStyleName(AON.AON_CSS.aonNowrap());
					InlineLabel l = new InlineLabel( key.getBox() );
					l.setStyleName(AON.AON_CSS.aonFiscalModelDataTableBox());
					p.add(l);
					DoubleBox doubleBox = new DoubleBox();
					doubleBox.setValue(mod111.getAmount(key));
					doubleBox.setEnabled(isEnabled(key));
					p.add(doubleBox);
					table.setWidget(row, col, p );
					table.getFlexCellFormatter().setStyleName(row, col, AON.AON_CSS.aonFiscalModelDataTableData());
				}
			}
		}
		row++;
		table.setWidget(row, 0, new Label( AON.MSG.mod111Header6() ));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableTitle());
		table.getFlexCellFormatter().setColSpan(row, 0, 4);
		row++;
		for (Mod111Key key : TOTAL) {
			row++;
			
			table.setWidget(row, 0, new Label( key.getDescription() ));
			table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
			table.getFlexCellFormatter().setColSpan(row, 0, 3);
			
			FlowPanel p = new FlowPanel();
			p.setStyleName(AON.AON_CSS.aonNowrap());
			InlineLabel l = new InlineLabel( key.getBox() );
			l.setStyleName(AON.AON_CSS.aonFiscalModelDataTableBox());
			p.add(l);
			DoubleBox doubleBox = new DoubleBox();
			doubleBox.setValue(mod111.getAmount(key));
			doubleBox.setEnabled(isEnabled(key));
			p.add(doubleBox);
			table.setWidget(row, 1, p );
			table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());
			
		}
		
		
		tablePanel.setWidget(table);
	}

	private boolean isEnabled(Mod111Key key) {
		return false;	
	} 

}
