package com.code.aon.ui.fiscal.event;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.fiscal.Mod347Detail;
import com.code.aon.registry.RegistryDocument;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class Mod347ControllerDetailListener  extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IController c = event.getController();
		Mod347Detail  detail = (Mod347Detail) c.getTo();
		detail.setSheet("D");
		check(detail);
	}
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		IController c = event.getController();
		Mod347Detail  detail = (Mod347Detail) c.getTo();
		check(detail);
	}
	
	private void check(Mod347Detail detail) throws ControllerListenerException {
		double sum = CommonUtil.round(
				detail.getFirstQuarterAmount()
				+detail.getSecondQuarterAmount()
				+detail.getThirdQuarterAmount()
				+detail.getFourthQuarterAmount());
		double amount = detail.getAmount(); 
		if (sum != amount) {
			throw new ControllerListenerException("La suma de las cantidades trimestrales no coincide con el total anual");
		}
		
		sum = CommonUtil.round(
				detail.getAssetFirstQuarterAmount()
				+detail.getAssetSecondQuarterAmount()
				+detail.getAssetThirdQuarterAmount()
				+detail.getAssetFourthQuarterAmount());
		amount = detail.getAssetAmount(); 
		if (sum != amount) {
			throw new ControllerListenerException("La suma de las cantidades trimestrales "
					+ "de las operaciones de inmuebles sujetas a IVA no coincide con el total anual correspondiente.");
		}
		
		RegistryDocument rd = new RegistryDocument(detail.getDocument());
		if (!rd.isValid()) {
			AonUtil.addInfoMessage("Los datos se han guardado aunque el NIF/DNI " + detail.getDocument() + " no es válido. "
					+ "Recuerde modificar el dato antes de generar el fichero para Hacienda.");
		}
		
	}
	
}
