package com.esferalia.aon.gwt.fiscal.client.mod111;


import static com.esferalia.aon.occam.api.model.type.Mod111KeyInfo.NONE;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod111KeyInfo;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model715Navarra extends Model111Base {

	public static enum ModelScript implements IModelScript {
		 AR_CX13 ("Deuda tributaria a ingresar",new Mod111Key[]{Mod111Key.NF_A1},ENABLED,NONE)
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
	
	public Model715Navarra(IFiscalModelCallback<Mod111> callback) {
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
				"ORDEN FORAL 25/2011, de 28 de febrero"
				,"https://www.google.es/url?sa=t&rct=j&q=&"
			   + "esrc=s&source=web&cd=4&ved=0ahUKEwiO87Og"
			   + "0dTKAhVBThoKHV_1AoMQFggyMAM&url=http%3A%"
			   + "2F%2Fwww.navarra.es%2FNR%2Frdonlyres%2FB"
			   + "7305C90-D15E-4A60-9D19-3E3A036718FA%2F0%"
			   + "2FVigorOF201125Modelos715..&usg=AFQjCNH0"
			   + "d4ARWDMfqAhYCNpId15zDLYdLw&sig2=4-Tp8h4p"
			   + "FY0lfkJVJuRvTA&cad=rja"));
		return panel;
	}

}
