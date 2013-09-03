package com.code.aon.ui.finance.event;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.PosCatalogue;
import com.code.aon.product.Catalogue;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.product.controller.IItemConstants;
import com.code.aon.ui.product.controller.ProductCollectionsController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PosCatalogueControllerListener extends ControllerAdapter implements IFinanceConstants, IFinanceMessages {

    @Override
    public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		PosCatalogue posCatalogue = (PosCatalogue)event.getController().getTo();
    	try {
            ProductCollectionsController collections = (ProductCollectionsController)AonUtil.getRegisteredBean(IItemConstants.PRODUCT_COLLECTIONS);
        	List<?> catalogues = collections.getCatalogues();
        	if (catalogues.size() > 0) {
        		Catalogue catalogue = (Catalogue)((SelectItem)catalogues.get(0)).getValue();
        		posCatalogue.setCatalogue(catalogue);
        	}
        } catch (ManagerBeanException e) {
            throw new ControllerListenerException(e.getMessage(), e);
        }
    }

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		PosCatalogue posCatalogue = (PosCatalogue)event.getController().getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(PosCatalogue.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.POS_CATALOGUE_POS_ID), posCatalogue.getPos().getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.POS_CATALOGUE_CATALOGUE_ID), posCatalogue.getCatalogue().getId());
			if (bean.getCount(criteria) > 0) {
				throw new ControllerListenerException(AonUtil.getMessage(BUNDLE_KEY, FINANCE_POS_CATALOGUE_ERROR));
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}


}
