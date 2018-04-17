package com.esferalia.aon.gwt.fiscal.client.mod349;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod349.Model349.Model349Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model349GIPUZKOA extends Model349Base {

	private static final int OPERATORS_TAB = 1;
	
	public Model349GIPUZKOA(Mod349 mod349,Model349Callback cbk,Integer selectedIndex) {
		super(mod349, cbk);
		
		TabLayoutPanel tabPanel = new TabLayoutPanel(26, Unit.PX);
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonScrollArea());
		centerPanel.setWidget(tabPanel);
		add(centerPanel);
		
		paintDeclarationTab(tabPanel);
		paintOperatorsTab(tabPanel,selectedIndex);
		paintAdministrationTab(cbk,tabPanel);
		
		tabPanel.selectTab(OPERATORS_TAB, false);		
		
	}

	private void paintAdministrationTab(Model349Callback cbk,TabLayoutPanel tabPanel) {
		FlowPanel panel = new FlowPanel();
		panel.add(getAdministrationPanel(cbk));
		panel.add(getInformationPanel());
		tabPanel.add(panel,TAB_TEMPLATE.render("Foru Aldundia / Diputaci\u00F3n Foral", FiscalModelUtils.getAdministrationIconBW(getMod349().getAdministration())));
	}
	
	@Override
	protected LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Informaci\u00F3n general."
				,"https://egoitza.gipuzkoa.eus/es/listado-tramites?p_p_id=TramitesServiciosPortlet_WAR_LEEsedeElectronicaportlet&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&_TramitesServiciosPortlet_WAR_LEEsedeElectronicaportlet_idTramite=817&_TramitesServiciosPortlet_WAR_LEEsedeElectronicaportlet_myaction=detalleTramite"));
		list.add(new Pair<String, String>("Formulario PDF." 
				,"http://www2.gipuzkoa.net/wps/portal/!ut/p/b1/hVTZsqo6FPyW_QEWCSDDIzIoQyJDQOHFQmUjk3gFRfj6g1X73HvqWBeSBypV3elevRahIiqkASvSPMcuGWpPRdf4maVxm9XXuHyfI-4AMa1KKygBASpLoKvuUvKcgNmy9AgIRwD4nyWBv_iuIY98Ysi-vgI8-c2fAEzx3eUMfwRM8LHAT_PfgCn9YM5_QE_lZ1iA2lGhrH3c8gGbTJGfS3G6ihU3wx8BU3wGzvBHwMwU7ahoUmIDfwBTgzY3atFcFdFczHhTVwllUBHN_GwqAOzByYWuNr2q8_PAwae0JgK0Sd89Br_18emCB68Nxi8CoSCz6UWxPSTq-Gh7wdlfSdLrFX5bVDiq8_-pr1WTGdUN02MCAYI1QxFqP4p5eX_Th2Jwc9DRxIcGvqsdzH2ACibAfvFqsIuboushCTDiDYTIDjbqa0vw3j4H7ii4Es-2Gb3rSMv6OP7lwTGEK6XWO011bWGZwCeQVMa6MhLKH-AV1U7a3rKF0rp-5V_C1jBy9ET8kIkDv-YcXadlkePlHOWp3J7zbeofhyQw_NJmLau5vVAIxL7V5fsm6KSr3dfSKtCyRjQk4e66Ccjug1jlx47bPNIOr_gu4U-w4cwLYutwecmz63MfJ7xR0dymGy5F9c0_d9uN5cX2QVmbIV81GQ29wixvYR_C6iGXsXkxcukmp4V8DXZpurDpg69tH6Xjp19fH2EviQR0X7E1DCGjEfbPsAk6II9xkZWVeMBrG2PyE6TUMso-oryk-biQtXmgw62tmKpDg_EdIVQoaTNNFj6bPLgmKVoLk7CDyo4mytHFRLObswyx4ppQGRtvmZ2nhB1RNAud__UW_ANfn1OlIG0s1EZQ0zkgm-DtS9Hez9A0bHdPmvpxPyWUc4pPl8RKnklpx2lCeT-OwX079Iw-gCc2wTXIwWuLCRoIqAfgA4gUyxnHE3qigbHaQ6iCwfMB8IpmwEVNe2f7t3O1EdwbVUWlNi6rz7JsUbjqt5LEi-Ow76Aw7kWoS19fvwB64Hmx/"));
		return list;
	}
	
}
