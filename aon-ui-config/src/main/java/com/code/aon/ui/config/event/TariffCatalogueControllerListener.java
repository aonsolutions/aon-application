package com.code.aon.ui.config.event;

import static com.code.aon.ui.common.ICommonMessages.CATALOGUE_DEFINED_FOR_TARIFF_ERROR;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Catalogue;
import com.code.aon.config.Tariff;
import com.code.aon.config.TariffCatalogue;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.TariffCatalogueController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class TariffCatalogueControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    @Override
    public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		TariffCatalogueController controller = (TariffCatalogueController)event.getController();
    	Tariff tariff = (Tariff)controller.getMasterController().getTo();
    	TariffCatalogue tariffCatalogue = (TariffCatalogue)controller.getTo();
    	try {
            ConfigCollectionsController collections = (ConfigCollectionsController)AonUtil.getRegisteredBean(ConfigConstants.CONFIG_COLLECTIONS);
        	List<?> catalogues = (tariff.isPurchase()) ? collections.getPurchaseCatalogues() : collections.getSalesCatalogues();
        	if (catalogues.size() > 0) {
        		Catalogue catalogue = (Catalogue)((SelectItem)catalogues.get(0)).getValue();
        		tariffCatalogue.setCatalogue(catalogue);
        	}
       		tariffCatalogue.setTariff(tariff);
        } catch (ManagerBeanException e) {
            throw new ControllerListenerException(e.getMessage(), e);
        }
    }

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		TariffCatalogue tariffCatalogue = (TariffCatalogue)event.getController().getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(TariffCatalogue.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TARIFF_CATALOGUE_TARIFF_ID), tariffCatalogue.getTariff().getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TARIFF_CATALOGUE_CATALOGUE_ID), tariffCatalogue.getCatalogue().getId());
			if (bean.getCount(criteria) > 0) {
				throw new ControllerListenerException(AonUtil.getMessage(CATALOGUE_DEFINED_FOR_TARIFF_ERROR));
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

}