package com.esferalia.aon.gwt.fiscal.client.mod111;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model110Gipuzkoa extends Model111Base {
	
	public Model110Gipuzkoa(IFiscalModelCallback<Mod111> callback) {
		super(callback);
	}
	
	@Override
	public Widget getInfoPanel(Mod111 mod111) {

		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.add(getAnchorPanel(mod111
				,"Informaci\u00F3n tributaria"
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
