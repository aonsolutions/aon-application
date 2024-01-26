// APLICACION DE RESULTADOS, PRESENTACION DE DOCUMENTOS
package com.esferalia.aon.gwt.mod200.client.mod200.e2023;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.mod200.client.mod200.e2023.Model2002023.Model2002023PageCallback;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

public class Page14 extends PageAbs {

	private AonTextBox nrsAnexoIII;
	private AonTextBox justCanarias;
	private AonTextBox nrsAnexoIV;
	private AonTextBox nrsAnexoV;
	private AonTextBox nrsAnexoVric;
	private AonTextBox justActivos;

	public Page14( Model2002023PageCallback callback ) {
		super(callback);
	}

	@Override
	protected void initializeTable() {
		
		basePanel.clear();
		
		// APLICACION DE RESULTADOS
		
		basePanel.add(getTitle(AON.MSG.incomeDistribution()));
		
		// Base de reparto
		
		basePanel.add(getSubtitle(AON.MSG.distributionBases()));		
		addTable("", Mod2002023Constants.INCOME_DISTRIBUTION_KEYS_1);
		
		// Aplicación
		
		basePanel.add(getSubtitle(AON.MSG.aplication()));
		
		FlexTable table1 = addTable();

		int row = 0;
		for (final Mod2002023Key key : Mod2002023Constants.INCOME_DISTRIBUTION_KEYS_2) {
			if (callback.getMod200Object().isVisible(key)) {
				row = paintKey(table1, key, row);
				if (key == Mod2002023Key.ID1270 || key == Mod2002023Key.ID1271 || key == Mod2002023Key.ID1522) {
					table1.getFlexCellFormatter().addStyleName((row-1), 0, AON.AON_CSS.aonPadding2Left());
				}				
			}
		}
		
		// PRESENTACIÓN DE DOCUMENTACIÓN PREVIA EN LA SEDE ELECTRÓNICA
		
		nrsAnexoIII = new AonTextBox();
		nrsAnexoIII.setVisibleLength(22);
		nrsAnexoIII.setMaxLength(22);
		nrsAnexoIII.setValue(callback.getMod200Object().getMod200().getNrsAnexoIII());
		nrsAnexoIII.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setNrsAnexoIII(nrsAnexoIII.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(nrsAnexoIII);
		
		nrsAnexoIV = new AonTextBox();
		nrsAnexoIV.setVisibleLength(22);
		nrsAnexoIV.setMaxLength(22);
		nrsAnexoIV.setValue(callback.getMod200Object().getMod200().getNrsAnexoIV());
		nrsAnexoIV.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setNrsAnexoIV(nrsAnexoIV.getValue());			
			callback.markAsDirty();
		});
		otherInputs.add(nrsAnexoIV);
		
		nrsAnexoVric = new AonTextBox();
		nrsAnexoVric.setVisibleLength(22);
		nrsAnexoVric.setMaxLength(22);
		nrsAnexoVric.setValue(callback.getMod200Object().getMod200().getNrsAnexoVric());
		nrsAnexoVric.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setNrsAnexoVric(nrsAnexoVric.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(nrsAnexoVric);

		nrsAnexoV = new AonTextBox();
		nrsAnexoV.setVisibleLength(22);
		nrsAnexoV.setMaxLength(22);
		nrsAnexoV.setValue(callback.getMod200Object().getMod200().getNrsAnexoV());
		nrsAnexoV.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setNrsAnexoV(nrsAnexoV.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(nrsAnexoV);
		
		justCanarias = new AonTextBox();
		justCanarias.setVisibleLength(22);
		justCanarias.setMaxLength(13);
		justCanarias.setValue(callback.getMod200Object().getMod200().getJustCanarias());
		justCanarias.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setJustCanarias(justCanarias.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(justCanarias);

		justActivos = new AonTextBox();
		justActivos.setVisibleLength(22);
		justActivos.setMaxLength(13);
		justActivos.setValue(callback.getMod200Object().getMod200().getJustActivos());
		justActivos.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setJustActivos(justActivos.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(justActivos);
		
		basePanel.add(getTitle("Presentaci\u00F3n de documentaci\u00F3n previa en la Sede electr\u00F3nica"));		

		FlexTable table2 = addTable();
		
		Label desc = new Label("Consigne el N\u00FAmero de Referencia de Sociedades (NRS):");
		desc.setStyleName(AON.AON_CSS.aonBold());
		table2.setWidget(0, 0, desc);
		
		row = 1;
		
		paintDescription(table2, AON.MSG.nrsAnexoIII(), ++row, 0, false);
		table2.setWidget(row, 1, nrsAnexoIII);
		
		paintDescription(table2, AON.MSG.nrsAnexoIV(), ++row, 0, false);
		table2.setWidget(row, 1, nrsAnexoIV);
		
		paintDescription(table2, "Documentaci\u00F3n presentada por el Anexo V (RIC: Inversiones anticipadas)", ++row, 0, false);
		table2.setWidget(row, 1, nrsAnexoVric);
		
		paintDescription(table2, "Documento normalizado presentado por el Anexo V Orden HAP/871/2016 (Art. 16.4 RIS)", ++row, 0, false);
		table2.setWidget(row, 1, nrsAnexoV);
		
		paintDescription(table2, AON.MSG.justCanarias(), ++row, 0, false);
		table2.setWidget(row, 1, justCanarias);
		
		paintDescription(table2, AON.MSG.justActivos(), ++row, 0, false);
		table2.setWidget(row, 1, justActivos);
		
	}

}


