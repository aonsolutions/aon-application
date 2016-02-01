package com.esferalia.aon.gwt.fiscal.client.mod111;

import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.SALARY;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod111KeyInfo;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model110Gipuzkoa extends Model111Base {
	
	public static enum ModelScript implements IModelScript {
		
		  GP_CX00 ("Retenciones. Rendimientos del trabajo"
				 ,new Mod111Key[]{Mod111Key.GP_C01,Mod111Key.GP_C02,Mod111Key.GP_C03},ENABLED,SALARY)
		 ,GP_CX01 ("Retenciones. Rendimientos de actividades econ\u00F3micas"
				 ,new Mod111Key[]{Mod111Key.GP_C04,Mod111Key.GP_C05,Mod111Key.GP_C06},ENABLED,INVOICE)
		 ,GP_CX02 ("Retenciones. Rendimientos de activ. agr\u00EDcolas, ganadera y forestales"
				 ,new Mod111Key[]{Mod111Key.GP_C07,Mod111Key.GP_C08,Mod111Key.GP_C09},ENABLED,INVOICE)
		 ,GP_CX03 ("Retenciones. Premios"
				 ,new Mod111Key[]{Mod111Key.GP_C10,Mod111Key.GP_C11,Mod111Key.GP_C12},ENABLED,NONE)
		 ,GP_CX04 ("Total"
				 ,new Mod111Key[]{Mod111Key.GP_C13,Mod111Key.GP_C14},DISABLED,NONE)
		 ,GP_CX05 ("Ingresos a cuenta. Rendimientos del trabajo"
				 ,new Mod111Key[]{Mod111Key.GP_C15,Mod111Key.GP_C16,Mod111Key.GP_C17},ENABLED,NONE)
		 ,GP_CX06 ("Ingresos a cuenta. Rendimientos de actividades econ\u00F3micas" 
				 ,new Mod111Key[]{Mod111Key.GP_C18,Mod111Key.GP_C19,Mod111Key.GP_C20},ENABLED,NONE)
		 ,GP_CX07 ("Ingresos a cuenta. Rendimientos de activ. agr\u00EDcolas, ganadera y forestales" 
				 ,new Mod111Key[]{Mod111Key.GP_C21,Mod111Key.GP_C22,Mod111Key.GP_C23},ENABLED,NONE)
		 ,GP_CX08 ("Ingresos a cuenta. Premios" 
				 ,new Mod111Key[]{Mod111Key.GP_C24,Mod111Key.GP_C25,Mod111Key.GP_C26},ENABLED,NONE)
		 ,GP_CX09 ("Total"
				 ,new Mod111Key[]{Mod111Key.GP_C27,Mod111Key.GP_C28},DISABLED,NONE)
		 ,GP_CX10 ("A ingresar"
				 ,new Mod111Key[]{Mod111Key.GP_C29},DISABLED,NONE)
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
	
	public Model110Gipuzkoa(IFiscalModelCallback<Mod111> callback) {
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
				"Informaci\u00F3n tributaria"
				, "http://www2.gipuzkoa.net/wps/portal/!ut/p/b1/hZDbjqowFIaf"
				+ "xQcgbakHvCxQBKZ2UECkNwQEO6iAjIro0293YrKzMxld62plff86_ECAe"
				+ "ALhaApHGII1EHXalTI9l02dHv7WYpwgrlKiIwK1pWtAhwauETo6nGD0AO"
				+ "IHAH8JAl_qA_WpfwH8r0fm6NFejoi_WOHPoQoisIbDxN9px_ntvGZ3owt"
				+ "29wXk91CdB6TnZopOPPDy1TLUic4p5NGPnT-Gvvk5AuIlYqMn8MqWd8aI"
				+ "d1dwu6kK4AKh4meCVRZjbjbOlTr-VjTKIcLDclpkuQ71y9lSV18spaxxO"
				+ "5bMiFu3sTDZVtBY1Wq3thuFSK-whi1iG0USLw7FyS5uOPcbNtfUW5Gop7"
				+ "IXGe6r5ppvkiXxh-aXpStyPOnkiLYGl351zfi0EvlB80rJhWQb-771FTy"
				+ "repO6zLT7Gumyaz7dc4WzHpIJvbTXuNdyc0uTUB4TbX_MVj1aGCpHTti2"
				+ "32nJMDnu8o89WxyCdSTHnqbsrZCVUbsYDED8cGryz6mZObegE3pzZDlja"
				+ "HxAEIBYB35xSagPKnGw2E3ZL-0rrjo5-AMzRZ9M/dl4/d5/L0lHSkovd0"
				+ "RNQUxrQUVnQSEhLzRKVUUvZXM!/"));
		return panel;
	}
}
