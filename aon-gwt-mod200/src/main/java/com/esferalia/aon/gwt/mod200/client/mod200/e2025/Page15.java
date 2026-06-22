// APLICACION DE RESULTADOS, PRESENTACION DE DOCUMENTOS
package com.esferalia.aon.gwt.mod200.client.mod200.e2025;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.mod200.client.mod200.e2025.Model2002025.Model2002025PageCallback;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025Key;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

public class Page15 extends PageAbs {

	public Page15( Model2002025PageCallback callback ) {
		super(callback);
	}

	@Override
	protected void initializeTable() {
		
		basePanel.clear();
		
		// APLICACION DE RESULTADOS
		
		basePanel.add(getTitle(AON.MSG.incomeDistribution()));
		
		// Base de reparto
		
		basePanel.add(getSubtitle(AON.MSG.distributionBases()));		
		addTable("", Mod2002025Constants.INCOME_DISTRIBUTION_KEYS_1);
		
		// Aplicación
		
		basePanel.add(getSubtitle(AON.MSG.aplication()));
		
		FlexTable table1 = addTable();

		int row = 0;
		for (final Mod2002025Key key : Mod2002025Constants.INCOME_DISTRIBUTION_KEYS_2) {
			if (callback.getMod200Object().isVisible(key)) {
				row = paintKey(table1, key, row);
				if (key == Mod2002025Key.ID1270 || key == Mod2002025Key.ID1271 || key == Mod2002025Key.ID1522) {
					table1.getFlexCellFormatter().addStyleName((row-1), 0, AON.AON_CSS.aonPadding2Left());
				}				
			}
		}
		
		// PRESENTACIÓN DE DOCUMENTACIÓN PREVIA EN LA SEDE ELECTRÓNICA
		
		// Documentación presentada por el Anexo III (Ajustes y deducciones)
		AonTextBox nrsAnexoIII = new AonTextBox();
		nrsAnexoIII.setVisibleLength(22);
		nrsAnexoIII.setMaxLength(22);
		nrsAnexoIII.setValue(callback.getMod200Object().getMod200().getNrsAnexoIII());
		nrsAnexoIII.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setNrsAnexoIII(nrsAnexoIII.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(nrsAnexoIII);
		
		// Documentación presentada por el Anexo IV (Personal investigador) 
		AonTextBox nrsAnexoIV = new AonTextBox();
		nrsAnexoIV.setVisibleLength(22);
		nrsAnexoIV.setMaxLength(22);
		nrsAnexoIV.setValue(callback.getMod200Object().getMod200().getNrsAnexoIV());
		nrsAnexoIV.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setNrsAnexoIV(nrsAnexoIV.getValue());			
			callback.markAsDirty();
		});
		otherInputs.add(nrsAnexoIV);
		
		// Documentación presentada por el Anexo V (RIC: Inversiones anticipadas)
		AonTextBox nrsAnexoVric = new AonTextBox();
		nrsAnexoVric.setVisibleLength(22);
		nrsAnexoVric.setMaxLength(22);
		nrsAnexoVric.setValue(callback.getMod200Object().getMod200().getNrsAnexoVric());
		nrsAnexoVric.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setNrsAnexoVric(nrsAnexoVric.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(nrsAnexoVric);
		
		// Documentación presentada por el Anexo VI (RIIB: Inversiones anticipadas)
		AonTextBox nrsAnexoVI = new AonTextBox();
		nrsAnexoVI.setVisibleLength(22);
		nrsAnexoVI.setMaxLength(22);
		nrsAnexoVI.setValue(callback.getMod200Object().getMod200().getNrsAnexoVI());
		nrsAnexoVI.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setNrsAnexoVI(nrsAnexoVI.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(nrsAnexoVI);		

		// Documento normalizado presentado por el Anexo V Orden HAP/871/2016 (Art. 16.4 RIS)
		AonTextBox nrsAnexoV = new AonTextBox();
		nrsAnexoV.setVisibleLength(22);
		nrsAnexoV.setMaxLength(22);
		nrsAnexoV.setValue(callback.getMod200Object().getMod200().getNrsAnexoV());
		nrsAnexoV.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setNrsAnexoV(nrsAnexoV.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(nrsAnexoV);
		
		// Número de justificante identificativo de la declaración informativa de ayudas Régimen Económico y Fiscal de Canarias
		AonTextBox justCanarias = new AonTextBox();
		justCanarias.setVisibleLength(22);
		justCanarias.setMaxLength(13);
		justCanarias.setValue(callback.getMod200Object().getMod200().getJustCanarias());
		justCanarias.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setJustCanarias(justCanarias.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(justCanarias);

		// Número de justificante identificativo de la declaración informativa de ayudas Régimen Económico y Fiscal de Illes Balears
		AonTextBox justBaleares = new AonTextBox();
		justBaleares.setVisibleLength(22);
		justBaleares.setMaxLength(13);
		justBaleares.setValue(callback.getMod200Object().getMod200().getJustBaleares());
		justBaleares.addValueChangeHandler(event -> {
			callback.getMod200Object().getMod200().setJustBaleares(justBaleares.getValue());
			callback.markAsDirty();
		});
		otherInputs.add(justBaleares);
		
		// Número de justificante identificativo autoliquidación de la prestación patrimonial por conversión de activos (DA 13ª LIS)
		AonTextBox justActivos = new AonTextBox();
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
		
		paintDescription(table2, "Documentaci\u00F3n presentada por el Anexo VI (RIIB: Inversiones anticipadas)", ++row, 0, false);
		table2.setWidget(row, 1, nrsAnexoVI);
		
		paintDescription(table2, "Documento normalizado presentado por el Anexo V Orden HAP/871/2016 (Art. 16.4 RIS)", ++row, 0, false);
		table2.setWidget(row, 1, nrsAnexoV);
		
		paintDescription(table2, AON.MSG.justCanarias(), ++row, 0, false);
		table2.setWidget(row, 1, justCanarias);

		paintDescription(table2, "N\u00FAmero de justificante identificativo de la declaraci\u00F3n informativa de ayudas R\u00E9gimen Econ\u00F3mico y Fiscal de Illes Balears", ++row, 0, false);
		table2.setWidget(row, 1, justBaleares);
		
		paintDescription(table2, AON.MSG.justActivos(), ++row, 0, false);
		table2.setWidget(row, 1, justActivos);
		
	}

}