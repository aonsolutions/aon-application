package com.code.aon.ui.fiscal.event;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.fiscal.Mod347Detail;
import com.code.aon.fiscal.enumeration.Mod347Type;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryDocument;
import com.code.aon.ui.fiscal.controller.mod347.Mod347DetailAssetController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class Mod347ControllerDetailListener extends ControllerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		IController c = event.getController();
		Mod347Detail detail = (Mod347Detail) c.getTo();
		detail.setSheet("D");
		check(detail);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		IController c = event.getController();
		Mod347Detail detail = (Mod347Detail) c.getTo();
		check(detail);
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		IController c = event.getController();
		Mod347Detail detail = (Mod347Detail) c.getTo();
		checkRental(detail);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		IController c = event.getController();
		Mod347Detail detail = (Mod347Detail) c.getTo();
		checkRental(detail);
	}

	private void check(Mod347Detail detail) throws ControllerListenerException {
		double sum = CommonUtil.round(detail.getFirstQuarterAmount()
				+ detail.getSecondQuarterAmount()
				+ detail.getThirdQuarterAmount()
				+ detail.getFourthQuarterAmount());
		double amount = detail.getAmount();
		if (sum != amount) {
			throw new ControllerListenerException(
					"La suma de las cantidades trimestrales no coincide con el total anual");
		}

		sum = CommonUtil.round(detail.getAssetFirstQuarterAmount()
				+ detail.getAssetSecondQuarterAmount()
				+ detail.getAssetThirdQuarterAmount()
				+ detail.getAssetFourthQuarterAmount());
		amount = detail.getAssetAmount();
		if (sum != amount) {
			throw new ControllerListenerException(
					"La suma de las cantidades trimestrales "
							+ "de las operaciones de inmuebles sujetas a IVA no coincide con el total anual correspondiente.");
		}

		RegistryDocument rd = new RegistryDocument(detail.getDocument());
		if (!rd.isValid()) {
			AonUtil.addInfoMessage("Los datos se han guardado aunque el NIF/DNI "
					+ detail.getDocument()
					+ " no es válido. "
					+ "Recuerde modificar el dato antes de generar el fichero para Hacienda.");
		}

	}

	private void checkRental(Mod347Detail detail)
			throws ControllerListenerException {
		if (Mod347Type.B == detail.getType()
				&& detail.isBusinessPremiseRental()) {
			try {
				IManagerBean bean = BeanManager
						.getManagerBean(Mod347Detail.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.MOD347DETAIL_MOD347_ID), detail.getMod347().getId());
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.MOD347DETAIL_SHEET), "I");
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.MOD347DETAIL_DOCUMENT), detail.getDocument());
				int count = bean.getCount(criteria);
				if (count == 0) {
					Mod347Detail rent = new Mod347Detail();
					rent.setMod347(detail.getMod347());
					rent.setSheet("I");
					rent.setDocument(detail.getDocument());
					rent.setRegistry(detail.getRegistry());
					rent.setName(detail.getName());
					rent.setAmount(detail.getAmount());
					rent.setAssetLocation("1");
					bean.insert(rent);
					Mod347DetailAssetController c = (Mod347DetailAssetController) AonUtil
							.getRegisteredBean("mod347DetailAsset");
					c.initializeModel();
					AonUtil.addInfoMessage("Se ha generado un registro de inmueble para este declarado. Complete los datos obligatorios.");
				}
			} catch (ManagerBeanException e) {
				AonUtil.addErrorMessage("No se pudo crear automaticamente el registro de inmueble");
			}
		}
	}

}
