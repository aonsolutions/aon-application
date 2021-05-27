package com.code.aon.ui.fiscal.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.fiscal.Mod347Detail;
import com.code.aon.registry.RegistryDocument;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class Mod347ControllerDetailAssetListener  extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		IController c = event.getController();
		Mod347Detail  detail = (Mod347Detail) c.getTo();
		if (StringUtils.isBlank(detail.getAssetLocation())) {
			detail.setAssetLocation("1");	
		}
		check(detail);
	}
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IController c = event.getController();
		Mod347Detail  detail = (Mod347Detail) c.getTo();
		detail.setSheet("I");
		check(detail);
	}
	
	private void check(Mod347Detail detail) throws ControllerListenerException {
		RegistryDocument rd = new RegistryDocument(detail.getDocument());
		if (!rd.isValid()) {
			AonUtil.addInfoMessage("Los datos se han guardado aunque el NIF/DNI " + detail.getDocument() + " no es válido. "
					+ "Recuerde modificar el dato antes de generar el fichero para Hacienda.");
		}
		
	}
	
	
}
