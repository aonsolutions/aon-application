package com.esferalia.aon.gwt.fiscal.client.mod111;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model111Gipuzkoa extends Model110Gipuzkoa {

	public Model111Gipuzkoa(IFiscalModelCallback<Mod111> callback) {
		super(callback);
	}

	
	@Override
	public Widget getInfoPanel(Mod111 mod111) {

		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.add(getAnchorPanel(mod111,
				"Informaci\u00F3n tributaria"
				, "http://www2.gipuzkoa.net/wps/portal/!ut/p/b1/hZDbjqpAEEW_Z"
				+ "T6ANNCK-NjcBhBa7ki_EJCLiICMFxq_fpxkTk5ykqNVT5XsVbVrAwKS1VL"
				+ "kBW7Fgx0gfXZv6uzaDH12-pmJkHKYV5HEIVbklCVrqN4S-W4Et4sfIHkK2"
				+ "P8UYv_hPVN-8oEph4bEroI__AvBKx5yIAY7dpH6R_Fsz9ed9ZDvwfHhsvg"
				+ "R8naAKFYy7oIDp4i8UEISVlkcv7n5XPrm5xiQl7Z07lfwKpZ3wZB3n2N96"
				+ "EpgAsLD3wZRnkCsDMakGn5FBuYUw0WzLvNCYqXbVeOjg5Wp1mDerfQTmf2"
				+ "YEMWqiJrwYm_2-sCg2im1xchZe6ZGThKSi17OsPAHyxb5uUz5S0NJDmk3T"
				+ "MU-9ZC_UA6axNTC6l4v1VHGtd9NOV53pDiJTlNjUlt7_VH5DPzsqKKalqL"
				+ "TnpPq-7A1rx3MKYtW6m2cEioWSqWmYX1OxfacR5RzZR5zRjiOX1ljQXQ-F"
				+ "pvWck_BLq4FR2RaLbSaeHQ_PoBfXkDyTGv1N61PxdZYI3RsTjMEVt6wIAC"
				+ "7IJmpPKBJQWG6yd3bVx_GvfdotPFqGErcWOWximHklvZW2U5LQVCglhUZz"
				+ "txi344NnfgWE3kjevQrOidbLXNx2M9B165PG3tO20qd2toQy8KkKiK3Wz7"
				+ "wV9GAIs3WrWEGmrPbS7QaDhQ9TXfkpFkz03r6BA8D-vgGuLUUYg!!/dl4/"
				+ "d5/L2dBISEvZ0FBIS9nQSEh/pw/Z7_1N2EAB1A0GDMF0IUPM1FI60CK0/a"
				+ "ct/id=0/315509423821/-/"));
		return panel;
	}
	
}
