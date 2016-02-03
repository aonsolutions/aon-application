package com.esferalia.aon.gwt.fiscal.client.mod111;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model715Navarra extends Model111Base {

	
	public Model715Navarra(IFiscalModelCallback<Mod111> callback) {
		super(callback);
	}
	
	@Override
	public Widget getInfoPanel(Mod111 mod111) {

		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.add(getAnchorPanel(mod111,
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
