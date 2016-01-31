package com.esferalia.aon.gwt.fiscal.client.mod111;

import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.SALARY;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod111KeyInfo;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model111AEAT extends Model111Base {
	private static final String MONEY  = "Rendimientos dinerarios";
	private static final String INKIND = "Rendimientos en especie";
	private static final String CASH = "Rendimientos en met\u00E1lico";
	private static final String BOTH = "Contraprestaciones dinerarias o en especie";
	
	private static enum ModelScript implements IModelScript {
		 CT_01 ("I. Rendimientos del trabajo",null,DISABLED, null)
		,CT_02 (MONEY ,new Mod111Key[]{Mod111Key.CT_C01,Mod111Key.CT_C02,Mod111Key.CT_C03},ENABLED,SALARY)
		,CT_03 (INKIND,new Mod111Key[]{Mod111Key.CT_C04,Mod111Key.CT_C05,Mod111Key.CT_C06},ENABLED,SALARY)
		,CT_04 ("II. Rendimientos de actividades econ\u00F3micas",null,DISABLED,null)
		,CT_05 (MONEY ,new Mod111Key[]{Mod111Key.CT_C07,Mod111Key.CT_C08,Mod111Key.CT_C09},ENABLED,INVOICE)
		,CT_06 (INKIND,new Mod111Key[]{Mod111Key.CT_C10,Mod111Key.CT_C11,Mod111Key.CT_C12},ENABLED,NONE)
		,CT_07 ("III. Premios por la participaci\u00F3n en juegos, concursos, rifas o combinaciones aleatorias",null,DISABLED,null)
		,CT_08 (CASH  ,new Mod111Key[]{Mod111Key.CT_C13,Mod111Key.CT_C14,Mod111Key.CT_C15},ENABLED,NONE)
		,CT_09 (INKIND,new Mod111Key[]{Mod111Key.CT_C16,Mod111Key.CT_C17,Mod111Key.CT_C18},ENABLED,NONE)
		,CT_10 ("IV. Ganancias patrimoniales derivadas de los aprovechamientos forestales de los vecinos en los montes p\u00FAblicos",null,DISABLED,null)
		,CT_11 (MONEY ,new Mod111Key[]{Mod111Key.CT_C19,Mod111Key.CT_C20,Mod111Key.CT_C21},ENABLED,NONE)
		,CT_12 (INKIND,new Mod111Key[]{Mod111Key.CT_C22,Mod111Key.CT_C23,Mod111Key.CT_C24},ENABLED,NONE)
		,CT_13 ("V. Contraprestaciones por la cesi\u00F3n de derechos de imagen, ingresos a cuenta previstos en el art\u00EDculo 92.8 de la Ley del Impuesto ",null,DISABLED,null)
		,CT_14 (BOTH  ,new Mod111Key[]{Mod111Key.CT_C25,Mod111Key.CT_C26,Mod111Key.CT_C27},ENABLED,NONE)
		,CT_15 ("Suma de retenciones e ingresos a cuenta",new Mod111Key[]{Mod111Key.CT_C28},DISABLED,COMPUTE)
		,CT_16 ("A deducir. Resultados a ingresar de anteriores autoliquidaciones por el mismo concepto, ejercicio y periodo."
					  ,new Mod111Key[]{Mod111Key.CT_C29},ENABLED,COMPUTE)
		,CT_17 ("Resultado a ingresar",new Mod111Key[]{Mod111Key.CT_C30},DISABLED,COMPUTE)
		;
		
		private String label;
		private Mod111Key[] keys;
		private boolean enabled;
		private Mod111KeyInfo infoKey;
		
		private ModelScript(String label, Mod111Key[] keys,boolean enabled,Mod111KeyInfo infoKey) {
			this.label = label;
			this.keys = keys;
			this.enabled = enabled;
			this.infoKey = infoKey;
		}

		@Override
		public String getLabel() {
			return label;
		}
		@Override
		public Mod111Key[] getKeys() {
			return keys;
		}
		@Override
		public boolean isEnabled() {
			return enabled;
		}
		@Override
		public Mod111KeyInfo getInfoKey() {
			return infoKey;
		};
	}
	
	public Model111AEAT(IFiscalModelCallback<Mod111> callback) {
		super(callback);
	}

	protected void paintDeclaration(final Mod111 mod111) {
		if (getTable().getRowCount() > 0) {
			getTable().removeAllRows();
		}
		
		defineTable();
		paintHeader();

		for (ModelScript ms : ModelScript.values()) {
			paintRow(mod111,ms);	
		}
	}

	@Override
	public Widget getInfoPanel() {

		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.add(getAnchorPanel(
				"Tr\u00E1mites. [link]"
				,"https://www.agenciatributaria.gob.es/AEAT.sede/tramitacion/GH01.shtml"));
		panel.add(getAnchorPanel(
				 "Informaci\u00F3n general. [link]" 
				,"http://www.agenciatributaria.es/AEAT.internet/GH01/informacion.shtml"));
		panel.add(getAnchorPanel(
				"Ficha. [link]"
				,"https://www.agenciatributaria.gob.es/AEAT.sede/procedimientos/GH01.shtml"));
		return panel;
	}

	private FlowPanel getAnchorPanel(String label, String href) {
		FlowPanel p = new FlowPanel();
		p.setStyleName(AON.AON_CSS.aonPadding2());
		Anchor a = new Anchor(label,href,"_blank");
		a.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		a.addStyleName(AON.AON_CSS.aonIconAeat());
		p.add(a);
		return p;
	}
}
