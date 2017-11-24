package com.esferalia.aon.gwt.fiscal.client.mod184;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184.Model184Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model184GIPUZKOA extends Model184Base {

	private static final int PERCEPTORS_TAB = 1;

	public Model184GIPUZKOA(Mod184 mod184,Model184Callback cbk,Integer selectedIndex) {
		super(mod184, cbk);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintDeclarationTab(tabPanel);
		paintEntityTab(tabPanel);
		paintIncomeTab(tabPanel, selectedIndex);
		paintPartnersTab(tabPanel, selectedIndex);
		paintAdministrationTab(tabPanel);
		
		tabPanel.selectTab(PERCEPTORS_TAB, false);
		
	}

	private void paintAdministrationTab(TabLayoutPanel tabPanel) {
		FlowPanel panel = new FlowPanel();
		panel.add(getAdministrationPanel());
		panel.add(getInformationPanel());
		tabPanel.add(panel,TAB_TEMPLATE.render("Agencia Tributaria", FiscalModelUtils.getAdministrationIconBW(getMod184().getAdministration())));
	}

	@Override
	protected LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Informaci\u00F3n general."
				,"http://www2.gipuzkoa.net/wps/portal/!ut/p/b1/hZPZkqIwGIWfZR7AImERuAyrKIksAYEbywVZ1EaBbpanH6zqruqertHkIpXK-f6TnCRMwsQiJ4jinJUkJmKSt91Hke3aonrbXR7zZL6FhNWRAhGQoCYAS_cE5Lsht-bZSRBPAvCfhsA_vLdUJ54u1cBSgEi_-CeCH7wpUASsQHMMAiFnLIV_-d-CZ_7hK_-Qfcp7wgvee-7PwRf8JHiR_4aJAL_1S-mGhzayR_WDlqM7EBr3mOoAlzHExHaOoRcoCKmZi4yJSZ5uawE_Bc-u9dXFJq-ST15FQxbVNWWWTMJyn50Jp7O6pdRVK__aBWXokkNWUQk6dOjex6ANyCEno9-G04hBLKl8lmuOj2WL7B0_PD4y6Pv4ZDPx5C5-ezf6ipvclyufCyUITI6hn8EON2s8j14JBujrYMQAjFALhqbUfBKc--aoEKJLAxCDDpcrjkxreHS5Vqafoav4BgPncY7sUu2nPxXSeJJVVqdb_impZrDYDufUcd7RoHL5PkGoYJU-tfeb0fA0geCP2xse9DpNZ3sBZWWszLC4KY-LbEl6c6ZmiQfDyy6SbD526RmPb2G1RalI58YepSd9FyiYOuxBYxf1oq_NRhPXY70QxyR_3ysnRTRy0ob33aY1ZCPLS9mlRxKPxyot18osPtQ7Ft6BuO5WjWzAxrf0NrAKPiRN09_tpXtYiKdaZY_Y0ps_v4L9-SEp_z1YirfY5zxsFxcyEtMh5Cs01HJalPwqpmFjKuZgaFhzoK7Ao1gUj4paoU7T8fYeF4Ukls7VPvr9kIOwdqkjy3O9cg_nunM3JspbYwSx1uVRUDRxIpoHt86ok4FI27BXPuqzFQ160vGsuOjym9lUgXbn7vX9eg7kfG1uZyo_v6-MreHJZavhG-OnDXNNLsaj2XNP9yQi1MIHOZET-gsVpg8k/dl4/d5/L0lJSklKSUpKZ0EhIS9JTmpBQUF4QUFFU29BQ0ltWWchIS80SmtHUW9RdHV5RWQtVVlRL1o2XzFOMkVBQjFBMDhSSkMwSUVUSkNVSUIwNzMxL1o3XzFOMkVBQjFBMEc1VEEwSVVEUEZOMTFKMDY1LzA!/?contenido=hweogasunaeslbr%2Fogasuna%2Fhweimpuestos-040sta%2Fhweimpuestos-040-020sta%2Fhweinformativosanuales-040-020-180sta%2Fhwemodelos-040-020-180sta%2F184%2Ffacdf5a1-ee8e-4079-ab67-606b0ae40b61"));
		return list;
	}
	
}
