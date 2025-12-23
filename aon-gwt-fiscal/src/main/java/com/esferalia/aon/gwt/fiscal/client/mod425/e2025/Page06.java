// OPERACIONES ESPECIFICAS, OPERATIONES RECC, OPERACIONES REPEP
package com.esferalia.aon.gwt.fiscal.client.mod425.e2025;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod425.e2025.Model4252025.Model4252025Callback;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025Description;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class Page06 extends PageAbs {

	// Operaciones esecíficas
	private AonDoubleBox box120 = new AonDoubleBox(); // 120 Operaciones en régimen general
	private AonDoubleBox box121 = new AonDoubleBox(); // 121 Operaciones a las que habiéndoles sido aplicado el régimen especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el artículo 18 Ley 20/1991
	private AonDoubleBox box122 = new AonDoubleBox(); // 122 Exportaciones definitivas y operaciones asimiladas a la exportación
	private AonDoubleBox box123 = new AonDoubleBox(); // 123 Operaciones relativas a áreas exentas
	private AonDoubleBox box124 = new AonDoubleBox(); // 124 Operaciones interiores exentas por el artículo 25 de la Ley 19/1994 realizadas por el sujeto pasivo
	private AonDoubleBox box125 = new AonDoubleBox(); // 125 Otras operaciones exentas con derecho a deducción
	private AonDoubleBox box126 = new AonDoubleBox(); // 126 Operaciones exentas sin derecho a deducción
	private AonDoubleBox box127 = new AonDoubleBox(); // 127 Operaciones en régimen simplificado
	private AonDoubleBox box128 = new AonDoubleBox(); // 128 Operaciones no sujetas por reglas de localización o con inversión del sujeto pasivo
	private AonDoubleBox box129 = new AonDoubleBox(); // 129 Operaciones en régimen especial de la agricultura, ganadería y pesca 
	private AonDoubleBox box130 = new AonDoubleBox(); // 130 Operaciones en regímenes especiales de bienes usados, objetos de arte, antigüedades o colección
	private AonDoubleBox box131 = new AonDoubleBox(); // 131 Operaciones en régimen especial de agencias de viajes
	private AonDoubleBox box132 = new AonDoubleBox(); // 132 Entregas de bienes inmuebles y operaciones financieras no habituales
	private AonDoubleBox box133 = new AonDoubleBox(); // 133 Entregas de bienes de inversión para el transmitente
	private AonDoubleBox box134 = new AonDoubleBox(); // 134 Total volumen de operaciones
	private AonDoubleBox box135 = new AonDoubleBox(); // 135 Importaciones de bienes de inversión exentos por el artículo 25 de la Ley 19/1994
	private AonDoubleBox box136 = new AonDoubleBox(); // 136 Cuotas de I.G.I.C. soportado no deducible
	private AonDoubleBox box137 = new AonDoubleBox(); // 137 Otras operaciones no sujetas con derecho a deducción (artículo 29.4.1ªg) Ley 20/1991)
	
	// Exclusivamente para aquellos sujetos pasivos acogidos al régimen especial de criterio de caja y para aquellos que sean destinatarios de operaciones afectadas por el mismo
	private AonDoubleBox box138 = new AonDoubleBox(); // 138 Importes de las entregas de bienes y prestaciones de servicios a las que Base Cuota habiéndoles aplicado el régimen especial de criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el art. 18 de la Ley 20/1991 - Base
	private AonDoubleBox box139 = new AonDoubleBox(); // 139 Importes de las entregas de bienes y prestaciones de servicios a las que Base Cuota habiéndoles aplicado el régimen especial de criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el art. 18 de la Ley 20/1991 - Cuota
	private AonDoubleBox box140 = new AonDoubleBox(); // 140 Importes de las adquisiciones de bienes y servicios a las que sea de aplicación o afecte el régimen especial del criterio de caja conforme a la regla general de devengo contenida en el art. 18 de la Ley 20/1991 - Base
	private AonDoubleBox box141 = new AonDoubleBox(); // 141 Importes de las adquisiciones de bienes y servicios a las que sea de aplicación o afecte el régimen especial del criterio de caja conforme a la regla general de devengo contenida en el art. 18 de la Ley 20/1991 - Cuota

	// Declaración informativa del volumen de operaciones en el régimen especial del pequeño empresario o profesional
	private AonDoubleBox box142 = new AonDoubleBox(); // 142 Importe de operaciones habituales u ocasionales sujetas al IGIC exentas por Régimen especial del pequeño empresario o profesional
	private AonDoubleBox box143 = new AonDoubleBox(); // 143 Importe de operaciones sujetas al IGIC exentas por Régimen especial del comerciante minorista
	private AonDoubleBox box144 = new AonDoubleBox(); // 144 Importe de entregas de bienes y prestaciones de servicios no sujetas al IGIC imputables a la sede de la actividad económica situada en Canarias
	private AonDoubleBox box145 = new AonDoubleBox(); // 145 Importe de entregas de bienes y prestaciones de servicios no sujetas al IGIC imputables a otras sedes o establecimientos situados fuera de Canarias
	private AonDoubleBox box146 = new AonDoubleBox(); // 146 Importe en el supuesto de transmisión de la totalidad o parte del patrimonio empresarial o profesional
	private AonDoubleBox box147 = new AonDoubleBox(); // 147 Total volumen de operaciones en el REPEP

	Page06(Model4252025Callback callback) {
		super(callback);
		paint();
		setValue();
	}

	@Override
	protected void setValue() {
		box120.setValue(getModel().getBox120());
		box121.setValue(getModel().getBox121());
		box122.setValue(getModel().getBox122());
		box123.setValue(getModel().getBox123());
		box124.setValue(getModel().getBox124());
		box125.setValue(getModel().getBox125());
		box126.setValue(getModel().getBox126());
		box127.setValue(getModel().getBox127());
		box128.setValue(getModel().getBox128());
		box129.setValue(getModel().getBox129());
		box130.setValue(getModel().getBox130());
		box131.setValue(getModel().getBox131());
		box132.setValue(getModel().getBox132());
		box133.setValue(getModel().getBox133());
		box134.setValue(getModel().getBox134());
		box135.setValue(getModel().getBox135());
		box136.setValue(getModel().getBox136());
		box137.setValue(getModel().getBox137());
		box138.setValue(getModel().getBox138());
		box139.setValue(getModel().getBox139());
		box140.setValue(getModel().getBox140());
		box141.setValue(getModel().getBox141());
		box142.setValue(getModel().getBox142());		
		box143.setValue(getModel().getBox143());
		box144.setValue(getModel().getBox144());
		box145.setValue(getModel().getBox145());
		box146.setValue(getModel().getBox146());
		box147.setValue(getModel().getBox147());
	}

	private void paint() {
		
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel basePanel = new FlowPanel();
		scroll.add(basePanel);
		setWidget(scroll);

		box120.addValueChangeHandler(event -> {
			if (box120.getValue() == null) box120.setValue(0.0, false);
			getModel().setBox120(box120.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		
		box121.addValueChangeHandler(event -> {
			if (box121.getValue() == null) box121.setValue(0.0, false);
			getModel().setBox121(box121.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		
		box122.addValueChangeHandler(event -> {
			if (box122.getValue() == null) box122.setValue(0.0, false);
			getModel().setBox122(box122.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		
		box123.addValueChangeHandler(event -> {
			if (box123.getValue() == null) box123.setValue(0.0, false);
			getModel().setBox123(box123.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		
		box124.addValueChangeHandler(event -> {
			if (box124.getValue() == null) box124.setValue(0.0, false);
			getModel().setBox124(box124.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		
		box125.addValueChangeHandler(event -> {
			if (box125.getValue() == null) box125.setValue(0.0, false);
			getModel().setBox125(box125.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		
		box126.addValueChangeHandler(event -> {
			if (box126.getValue() == null) box126.setValue(0.0, false);
			getModel().setBox126(box126.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		
		box127.addValueChangeHandler(event -> {
			if (box127.getValue() == null) box127.setValue(0.0, false);
			getModel().setBox127(box127.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		
		box128.addValueChangeHandler(event -> {
			if (box128.getValue() == null) box128.setValue(0.0, false);
			getModel().setBox128(box128.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		
		box129.addValueChangeHandler(event -> {
			if (box129.getValue() == null) box129.setValue(0.0, false);
			getModel().setBox129(box129.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
				
		box130.addValueChangeHandler(event -> {
			if (box130.getValue() == null) box130.setValue(0.0, false);
			getModel().setBox130(box130.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		
		box131.addValueChangeHandler(event -> {
			if (box131.getValue() == null) box131.setValue(0.0, false);
			getModel().setBox131(box131.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		box132.addValueChangeHandler(event -> {
			if (box132.getValue() == null) box132.setValue(0.0, false);
			getModel().setBox132(box132.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		box133.addValueChangeHandler(event -> {
			if (box133.getValue() == null) box133.setValue(0.0, false);
			getModel().setBox133(box133.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		box135.addValueChangeHandler(event -> {
			if (box135.getValue() == null) box135.setValue(0.0, false);
			getModel().setBox135(box135.getValue());
			markAsDirty();
		});
		box136.addValueChangeHandler(event -> {
			if (box136.getValue() == null) box136.setValue(0.0, false);
			getModel().setBox136(box136.getValue());
			markAsDirty();
		});
		box137.addValueChangeHandler(event -> {
			if (box137.getValue() == null) box137.setValue(0.0, false);
			getModel().setBox137(box137.getValue());
			markAsDirty();
		});
		box138.addValueChangeHandler(event -> {
			if (box138.getValue() == null) box138.setValue(0.0, false);
			getModel().setBox138(box138.getValue());
			markAsDirty();
		});
		box139.addValueChangeHandler(event -> {
			if (box139.getValue() == null) box139.setValue(0.0, false);
			getModel().setBox139(box139.getValue());
			markAsDirty();
		});
		box140.addValueChangeHandler(event -> {
			if (box140.getValue() == null) box140.setValue(0.0, false);
			getModel().setBox140(box140.getValue());
			markAsDirty();
		});
		box141.addValueChangeHandler(event -> {
			if (box141.getValue() == null) box141.setValue(0.0, false);
			getModel().setBox141(box141.getValue());
			markAsDirty();
		});
		box142.addValueChangeHandler(event -> {
			if (box142.getValue() == null) box142.setValue(0.0, false);
			getModel().setBox142(box142.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		box143.addValueChangeHandler(event -> {
			if (box143.getValue() == null) box143.setValue(0.0, false);
			getModel().setBox143(box143.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		box144.addValueChangeHandler(event -> {
			if (box144.getValue() == null) box144.setValue(0.0, false);
			getModel().setBox144(box144.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		box145.addValueChangeHandler(event -> {
			if (box145.getValue() == null) box145.setValue(0.0, false);
			getModel().setBox145(box145.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		box146.addValueChangeHandler(event -> {
			if (box146.getValue() == null) box146.setValue(0.0, false);
			getModel().setBox146(box146.getValue());
			calculateAndRefresh();
			markAsDirty();
		});

		// Casillas deshabilitadas
		box134.setEnabled(false);
		box147.setEnabled(false);
		
		// Operaciones esecíficas
		
		AonDisplayTable tab1 = addTable(basePanel, getTitle(AON.MSG.specificOperations()));
		addRow(tab1, Mod4252025Description.BOX_120_TEXT, 120, box120);
		addRow(tab1, Mod4252025Description.BOX_121_TEXT, 121, box121);
		addRow(tab1, Mod4252025Description.BOX_122_TEXT, 122, box122);
		addRow(tab1, Mod4252025Description.BOX_123_TEXT, 123, box123);
		addRow(tab1, Mod4252025Description.BOX_124_TEXT, 124, box124);
		addRow(tab1, Mod4252025Description.BOX_125_TEXT, 125, box125);
		addRow(tab1, Mod4252025Description.BOX_126_TEXT, 126, box126);
		addRow(tab1, Mod4252025Description.BOX_127_TEXT, 127, box127);
		addRow(tab1, Mod4252025Description.BOX_128_TEXT, 128, box128);
		addRow(tab1, Mod4252025Description.BOX_129_TEXT, 129, box129);
		addRow(tab1, Mod4252025Description.BOX_130_TEXT, 130, box130);
		addRow(tab1, Mod4252025Description.BOX_131_TEXT, 131, box131);
		addRow(tab1, Mod4252025Description.BOX_132_TEXT, 132, box132);
		addRow(tab1, Mod4252025Description.BOX_133_TEXT, 133, box133);
		addRow(tab1, Mod4252025Description.BOX_134_TEXT, 134, box134); 
		addRow(tab1, Mod4252025Description.BOX_135_TEXT, 135, box135);
		addRow(tab1, Mod4252025Description.BOX_136_TEXT, 136, box136);
		addRow(tab1, Mod4252025Description.BOX_137_TEXT, 137, box137);
		
		// Exclusivamente para aquellos sujetos pasivos acogidos al régimen especial de criterio de caja y para aquellos que sean destinatarios de operaciones afectadas por el mismo
		
		AonDisplayTable tab2 = addTable(basePanel);
		tab2.addStyleName(AON.CSS.aonMarginTop());
		tab2.addRow()
			.addCell(new Label(AON.MSG.accrualRegimeOperations()), AON.CSS.aonMarginTop(), AON.CSS.aonBold(), AON.CSS.aonTextUppercase(), AON.CSS.aonFontMedium(), AON.CSS.aonBorderBottom())
			.addCell(new Label(""), AON.CSS.aonBorderBottom())
			.addCell(new Label("Base"), AON.CSS.aonMarginTop(), AON.CSS.aonFontSmall(), AON.CSS.aonTextCenter(), AON.CSS.aonBorderBottom())
			.addCell(new Label(""), AON.CSS.aonBorderBottom())
			.addCell(new Label("Cuota"), AON.CSS.aonMarginTop(),AON.CSS.aonFontSmall(), AON.CSS.aonTextCenter(), AON.CSS.aonBorderBottom());
		addRow(tab2, Mod4252025Description.BOX_138_139_TEXT, 138, box138, box139);
		addRow(tab2, Mod4252025Description.BOX_140_141_TEXT, 140, box140, box141);
		
		// Declaración informativa del volumen de operaciones en el régimen especial del pequeño empresario o profesional		
		
		AonDisplayTable tab3 = addTable(basePanel, getTitle("Declaraci\u00F3n informativa del volumen de operaciones en el r\u00E9gimen especial del peque\u00F1o empresario o profesional"));
		addRow(tab3, Mod4252025Description.BOX_142_TEXT, 142, box142);
		addRow(tab3, Mod4252025Description.BOX_143_TEXT, 143, box143);
		addRow(tab3, Mod4252025Description.BOX_144_TEXT, 144, box144);
		addRow(tab3, Mod4252025Description.BOX_145_TEXT, 145, box145);
		addRow(tab3, Mod4252025Description.BOX_146_TEXT, 146, box146);
		addRow(tab3, Mod4252025Description.BOX_147_TEXT, 147, box147); 
		
	}

}
