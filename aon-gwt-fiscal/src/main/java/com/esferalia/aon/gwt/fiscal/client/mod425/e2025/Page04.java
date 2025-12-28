// OPERACIONES REALIZADAS EN REGIMEN SIMPLIFICADO
package com.esferalia.aon.gwt.fiscal.client.mod425.e2025;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod425.e2025.Model4252025.Model4252025Callback;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025Description;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

class Page04 extends PageAbs {

	private SimplifiedRegimePanel activity1;
	private SimplifiedRegimePanel activity2;
	private AonDoubleBox box103 = new AonDoubleBox(); // Total cuota anual derivada del régimen simplificado
	private AonDoubleBox box104 = new AonDoubleBox(); // Cuotas devengadas por entregas o transmisiones de activos fijos y por inversión del sujeto pasivo
	private AonDoubleBox box105 = new AonDoubleBox(); // Cuotas devengadas por arrendamientos de bienes inmuebles
	private AonDoubleBox box106 = new AonDoubleBox(); // Rectificación de cuotas impositivas repercutidas
	private AonDoubleBox box107 = new AonDoubleBox(); // Total cuotas
	private AonDoubleBox box108 = new AonDoubleBox(); // Cuotas deducibles por adquisición o importación de activos fijos
	private AonDoubleBox box109 = new AonDoubleBox(); // Cuotas deducibles por arrendamiento de bienes inmuebles
	private AonDoubleBox box110 = new AonDoubleBox(); // Total cuotas deducibles
	private AonDoubleBox box111 = new AonDoubleBox(); // Resultado régimen simplificado
	
	public Page04(Model4252025Callback callback) {
		super(callback);
		paint();
		setValue();
	}
	
	@Override
	protected void setValue() {
		
		activity1.setValue(getModel().getSimpRegime1());
		activity2.setValue(getModel().getSimpRegime2());		
		box103.setValue(getModel().getBox103(),false);
		box104.setValue(getModel().getBox104(),false);
		box105.setValue(getModel().getBox105(),false);
		box106.setValue(getModel().getBox106(),false);
		box107.setValue(getModel().getBox107(),false);
		box108.setValue(getModel().getBox108(),false);
		box109.setValue(getModel().getBox109(),false);
		box110.setValue(getModel().getBox110(),false);
		box111.setValue(getModel().getBox111(),false);
		
	}

	private void paint() {
		
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel basePanel = new FlowPanel();
		scroll.add(basePanel);
		setWidget(scroll);
		
		basePanel.add(getTitle(AON.MSG.simplifiedRegimeOperations()));
		basePanel.add(getSubtitle("CUOTAS DEL I.G.I.C. DERIVADAS DEL R\u00C9GIMEN SIMPLIFICADO Y DEVENGADAS POR OTRAS OPERACIONES"));
		
		activity1 = new SimplifiedRegimePanel(getModel().getSimpRegime1());
		activity1.addValueChangeHandler(event -> {
			getModel().setSimpRegime1(event.getValue());
			calculateAndRefresh(); 
			markAsDirty();
		});
		
		activity2 = new SimplifiedRegimePanel(getModel().getSimpRegime2());
		activity2.addValueChangeHandler(event -> {
			getModel().setSimpRegime2(event.getValue());
			calculateAndRefresh(); 
			markAsDirty();
		});
		
		box104.addValueChangeHandler(event -> {
			if (box104.getValue() == null) box104.setValue(0.0, false);
			getModel().setBox104(box104.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		box105.addValueChangeHandler(event -> {
			if (box105.getValue() == null) box105.setValue(0.0, false);			
			getModel().setBox105(box105.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		box106.addValueChangeHandler(event -> {
			if (box106.getValue() == null) box106.setValue(0.0, false);
			getModel().setBox106(box106.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		box108.addValueChangeHandler(event -> {
			if (box108.getValue() == null) box108.setValue(0.0, false);
			getModel().setBox108(box108.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		box109.addValueChangeHandler(event -> {
			if (box109.getValue() == null) box109.setValue(0.0, false);
			getModel().setBox109(box109.getValue());
			calculateAndRefresh();
			markAsDirty();
		});
		
		// POR AHORA SE PONEN SOLO 2 ACTIVIDADES PARA EL REGIMEN SIMPLIFICADO, AL IGUAL QUE ESTA EN EL MODELO 390

		AonDisplayTable tab0 = new AonDisplayTable();
		tab0.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab0.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab0);
		tab0.addRow()
			.addCell(activity1)
			.addCell(activity2);
		
		box103.setEnabled(false);
		box107.setEnabled(false);
		box110.setEnabled(false);
		box111.setEnabled(false);
		
		AonDisplayTable tab2 = addTable(basePanel, null);
		addRow(tab2, Mod4252025Description.BOX_103_TEXT, 103, box103);
		addRow(tab2, Mod4252025Description.BOX_104_TEXT, 104, box104);
		addRow(tab2, Mod4252025Description.BOX_105_TEXT, 105, box105);
		addRow(tab2, Mod4252025Description.BOX_106_TEXT, 106, box106);
		addRow(tab2, Mod4252025Description.BOX_107_TEXT, 107, box107);
		
		AonDisplayTable tab3 = addTable(basePanel, getSubtitle("I.G.I.C. DEDUCIBLE POR OTRAS OPERACIONES"));
		addRow(tab3, Mod4252025Description.BOX_108_TEXT, 108, box108);
		addRow(tab3, Mod4252025Description.BOX_109_TEXT, 109, box109);
		addRow(tab3, Mod4252025Description.BOX_110_TEXT, 110, box110);
		
		AonDisplayTable tab4 = addTable(basePanel, getSubsubtitle("RESULTADO DE LAS AUTOLIQUIDACIONES"));
		addRow(tab4, Mod4252025Description.BOX_111_TEXT, 111, box111);
		
	}
	
}
