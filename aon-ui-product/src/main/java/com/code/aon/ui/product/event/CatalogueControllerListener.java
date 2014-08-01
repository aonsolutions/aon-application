package com.code.aon.ui.product.event;

import static com.code.aon.ui.common.ICommonMessages.PRODUCT_CATALOGUE_DATES_ERROR;

import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Catalogue;
import com.code.aon.config.TariffCatalogue;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CatalogueControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Date endDate = ((Catalogue) this.getController().getTo()).getEndDate();
		if (endDate != null) {
			Date startDate = ((Catalogue) this.getController().getTo()).getStartDate();
			if (endDate.before(startDate)) {
				((Catalogue)this.getController().getTo()).setEndDate(null);
				throw new ControllerListenerException(AonUtil.getMessage(PRODUCT_CATALOGUE_DATES_ERROR));
			}
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Date endDate = ((Catalogue) this.getController().getTo()).getEndDate();
		if (endDate != null) {
			Date startDate = ((Catalogue) this.getController().getTo()).getStartDate();
			if (endDate.before(startDate)) {
				((Catalogue)this.getController().getTo()).setEndDate(null);
				throw new ControllerListenerException(AonUtil.getMessage(PRODUCT_CATALOGUE_DATES_ERROR));
			}
		}
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		Catalogue catalogue = (Catalogue) this.getController().getTo();
		try {
			IManagerBean tariffCatalogueBean = BeanManager.getManagerBean(TariffCatalogue.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(tariffCatalogueBean.getFieldName(IEntityAlias.TARIFF_CATALOGUE_CATALOGUE_ID),catalogue.getId());
			for (ITransferObject ito : tariffCatalogueBean.getList(criteria)) {
				TariffCatalogue tariffCatalogue = (TariffCatalogue)ito;
				tariffCatalogueBean.remove(tariffCatalogue);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

}