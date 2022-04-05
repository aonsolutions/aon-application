// APLICACION DE RESULTADOS, PRESENTACION DE DOCUMENTOS
package com.esferalia.aon.gwt.mod200.client.mod200.e2020;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.Model2002020.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020Key;
import com.google.gwt.user.client.ui.FlexTable;

public class Page14 extends PageAbs {

	private AonTextBox nrsAnexoIII = new AonTextBox();
	private AonTextBox justCanarias = new AonTextBox();
	private AonTextBox nrsAnexoIV = new AonTextBox();
	private AonTextBox nrsAnexoV = new AonTextBox();
	private AonTextBox nrsAnexoVric = new AonTextBox();
	private AonTextBox justActivos = new AonTextBox();

	public Page14( Model200PageCallback callback ) {
		super(callback);
		addBasePanel();
		initializeTable();
	}

	@Override
	protected void initializeTable() {
		
		basePanel.clear();
		
		basePanel.add(getTitle(AON.MSG.incomeDistribution()));
		
		basePanel.add(getSubtitle(AON.MSG.distributionBases()));
		
		addTable("", Mod2002020Constants.INCOME_DISTRIBUTION_KEYS_1);
		
		basePanel.add(getSubtitle(AON.MSG.aplication()));
		
		FlexTable table1 = addTable();

		int row = 0;
		for (final Mod2002020Key key : Mod2002020Constants.INCOME_DISTRIBUTION_KEYS_2) {
			if (callback.getMod200Object().isVisible(key)) {
				row = paintKey(table1,key,row);
				if (key == Mod2002020Key.ID1270 || key == Mod2002020Key.ID1271 || key == Mod2002020Key.ID1522) {
					table1.getFlexCellFormatter().addStyleName((row-1), 0, AON.AON_CSS.aonPadding2Left());
				}				
			}
		}
		
		basePanel.add(getTitle(AON.MSG.documentsElectronicSede()));
		
		nrsAnexoIII.setVisibleLength(22);
		nrsAnexoIII.setMaxLength(22);
		nrsAnexoIV.setVisibleLength(22);
		nrsAnexoIV.setMaxLength(22);
		nrsAnexoVric.setVisibleLength(22);
		nrsAnexoVric.setMaxLength(22);
		nrsAnexoV.setVisibleLength(22);
		nrsAnexoV.setMaxLength(22);
		justCanarias.setVisibleLength(22);
		justCanarias.setMaxLength(13);
		justActivos.setVisibleLength(22);
		justActivos.setMaxLength(13);
		
		nrsAnexoIII.addValueChangeHandler(event -> {
			callback.markAsDirty();
		});

		nrsAnexoIV.addValueChangeHandler(event -> {
			callback.markAsDirty();
		});

		nrsAnexoVric.addValueChangeHandler(event -> {
			callback.markAsDirty();
		});

		nrsAnexoV.addValueChangeHandler(event -> {
			callback.markAsDirty();
		});

		justCanarias.addValueChangeHandler(event -> {
			callback.markAsDirty();
		});

		justActivos.addValueChangeHandler(event -> {
			callback.markAsDirty();
		});

		FlexTable table2 = addTable();
		
		paintDescription(table2, AON.MSG.nrsAnexoIII(), 0, 0, false);
		table2.setWidget(0, 1, nrsAnexoIII);
		
		paintDescription(table2, AON.MSG.nrsAnexoIV(), 1, 0, false);
		table2.setWidget(1, 1, nrsAnexoIV);
		
		paintDescription(table2, "Documentaci\u00F3n presentada por el Anexo V (RIC: Inversiones anticipadas)", 2, 0, false);
		table2.setWidget(2, 1, nrsAnexoVric);
		
		paintDescription(table2, "Documento normalizado presentado por el Anexo V Orden HAP/871/2016 (Art. 16.4 RIS)", 3, 0, false);
		table2.setWidget(3, 1, nrsAnexoV);

		paintDescription(table2, AON.MSG.justCanarias(), 4, 0, false);
		table2.setWidget(4, 1, justCanarias);
		
		paintDescription(table2, AON.MSG.justActivos(), 5, 0, false);
		table2.setWidget(5, 1, justActivos);
		
	}

	@Override
	public void dump() {
		super.dump();
		nrsAnexoIII.setValue( callback.getMod200Object().getMod200().getNrsAnexoIII());
		justCanarias.setValue( callback.getMod200Object().getMod200().getJustCanarias());
		nrsAnexoIV.setValue( callback.getMod200Object().getMod200().getNrsAnexoIV());
		nrsAnexoV.setValue( callback.getMod200Object().getMod200().getNrsAnexoV());
		nrsAnexoVric.setValue( callback.getMod200Object().getMod200().getNrsAnexoVric());
		justActivos.setValue( callback.getMod200Object().getMod200().getJustActivos());
	}
	
	@Override
	public void populate() {
		callback.getMod200Object().getMod200().setNrsAnexoIII(nrsAnexoIII.getValue());
		callback.getMod200Object().getMod200().setJustCanarias(justCanarias.getValue());
		callback.getMod200Object().getMod200().setNrsAnexoIV(nrsAnexoIV.getValue());
		callback.getMod200Object().getMod200().setNrsAnexoV(nrsAnexoV.getValue());
		callback.getMod200Object().getMod200().setNrsAnexoVric(nrsAnexoVric.getValue());
		callback.getMod200Object().getMod200().setJustActivos(justActivos.getValue());
	}

}
