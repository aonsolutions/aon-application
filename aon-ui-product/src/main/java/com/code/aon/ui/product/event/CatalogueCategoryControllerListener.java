package com.code.aon.ui.product.event;

import static com.code.aon.ui.common.ICommonMessages.PRODUCT_DEFINED_FOR_CATALOGUE_ERROR;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.CatalogueCategory;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CatalogueCategoryControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CatalogueCategory catalogueCategory = (CatalogueCategory)event.getController().getTo();
		validateCatalogueCategory(catalogueCategory);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		CatalogueCategory catalogueCategory = (CatalogueCategory)event.getController().getTo();
		validateCatalogueCategory(catalogueCategory);
	}

	private void validateCatalogueCategory(CatalogueCategory catalogueCategory) throws ControllerListenerException {
		try {
			IManagerBean catalogueCategorybean = BeanManager.getManagerBean(CatalogueCategory.class);
			Criteria criteria = new Criteria();
			if (catalogueCategory.getId() != null) {
				criteria.addNotEqualExpression(catalogueCategorybean.getFieldName(IEntityAlias.CATALOGUE_CATEGORY_ID), catalogueCategory.getId());
			}
			criteria.addEqualExpression(catalogueCategorybean.getFieldName(IEntityAlias.CATALOGUE_CATEGORY_CATALOGUE_ID), catalogueCategory.getCatalogue().getId());
			criteria.addEqualExpression(catalogueCategorybean.getFieldName(IEntityAlias.CATALOGUE_CATEGORY_CATEGORY_ID), catalogueCategory.getCategory().getId());
			criteria.addEqualExpression(catalogueCategorybean.getFieldName(IEntityAlias.CATALOGUE_CATEGORY_QUANTITY), catalogueCategory.getQuantity());
			if (catalogueCategorybean.getCount(criteria) > 0) {
				throw new ControllerListenerException(AonUtil.getMessage(PRODUCT_DEFINED_FOR_CATALOGUE_ERROR));
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

}