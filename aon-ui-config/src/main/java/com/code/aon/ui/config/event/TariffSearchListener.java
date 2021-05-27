package com.code.aon.ui.config.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Catalogue;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListenerEx;

public class TariffSearchListener extends ControllerSearchListenerEx {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Catalogue catalogue;

	public Catalogue getCatalogue() {
		return catalogue;
	}
	public void setCatalogue(Catalogue catalogue) {
		this.catalogue = catalogue;
	}	

	@Override
	protected void init() throws ManagerBeanException {
		setCatalogue((Catalogue)BeanManager.getManagerBean(Catalogue.class).createNewTo());
	}

	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (getCatalogue() != null && getCatalogue().getId() != null) {
			criteria.addEqualExpression(getController().resolveAlias("Tariff_catalogues_catalogue_id"), getCatalogue().getId());
		}
	}

}