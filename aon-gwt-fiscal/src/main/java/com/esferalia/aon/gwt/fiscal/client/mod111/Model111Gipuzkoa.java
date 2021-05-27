package com.esferalia.aon.gwt.fiscal.client.mod111;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.watson.util.Pair;

public class Model111Gipuzkoa extends Model110Gipuzkoa {

	public Model111Gipuzkoa(IFiscalModelCallback<Mod111> callback, AonData aonData) {
		super(callback, aonData);
	}
	
	@Override
	public LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Informaci\u00F3n tributaria"
				, "http://www2.gipuzkoa.net/wps/portal/!ut/p/b1/hZDbrqJAEEW_xQ8gTTco-"
				+"Njc5NIgyEXoFwKCiNzk4FH068dJzmSSSUarnipZu6r2BhQkUBQFxCMerkEMaJ_d6iq71"
				+"kOftb9nukqhg1QsQcyKUFmyhrpbYt-LuC2PXkDyAtj_FGb_0e9M-aUPTDk0JFYI_ujfA"
				+"O_0HAR7ELN86p_Fi_24xuQp34Lz02OdZ4jsAM-OksHJCdwi2oUSlhyVdfYfbr6WfvC8B"
				+"_TtWzr8Ad7F8ikY-sm5ow9dCUxAEffTIMoTzlEG464a_pEOTLvn-Hpd5oXESt9XDUUnk"
				+"qlkMG8k3WCzHxOqkCNVEyT2Zq8PDK7cUuNHSA5Mhd0kpJNePrjCH4gtokeZoqmeac7N3"
				+"XAvDukO-7xy0iSmWgm3aqmOslP53T131h0tWtGtK4dW5KA_jz7DbbpZUU2i6HMPpeo2b"
				+"M1rx-UziwX1e7wns1goRzUNq0sqNpc8mqEnIwca4Th-ZTXh8OVcWA3x2iDeVytXZBotJ"
				+"PV-9BYL4JcTSF5pCX_T2ii2xhqha0PNWLGyxYIAxHEyE3nClapuXUc9SUJhnlebpWd5z"
				+"vmSVhc3XsLIPzVN6Wuegb2pLBSrU5Ujspspv-1QWydnq4XBlmw8dk7u4XTgrJIKtqUEv"
				+"T8rKLhnbaSFXUa9rE_5r6sXbh9oqa04se_XoKOtRh5Ms9Pv3GnAi1_Jvcx0/dl4/d5/L"
				+"0lJSklKSUpKZ0EhIS9JTmpBQUF4QUFFU29BQ0ltWWchIS80SmtHUW9RdHV5RWQtVVlRL"
				+"1o2XzFOMkVBQjFBMDhSSkMwSUVUSkNVSUIwNzMxL1o3XzFOMkVBQjFBMEc1VEEwSVVEU"
				+"EZOMTFKMDY1LzA!/?contenido=hweogasunaeslbr%2Fogasuna%2Fhweimpuestos-"
				+"040sta%2Fhweimpuestos-040-020sta%2Fhweretenciones-040-020-170sta%2Fh"
				+"wemodelos-040-020-170sta%2Fhwem111-040-020-170sta%2F25001a81-bc1a-492"
				+"a3-b838-f5d543cbf4ca" ));
		return list;
	}
}
