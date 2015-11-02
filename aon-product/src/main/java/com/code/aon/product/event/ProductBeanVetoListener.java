package com.code.aon.product.event;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.config.Domain;
import com.code.aon.product.Product;
import com.code.aon.product.enumeration.ProductKind;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.esferalia.aon.entity.IEntityAlias;

public class ProductBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	Product to = (Product) evt.getTo();
    	checkProduct(to);
    	checkValidCode(to, false);
    }

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Product to = (Product) evt.getTo();
    	checkProduct(to);
		checkValidCode(to, true);
	}

	private void checkProduct(Product product) {
		if (product.getKind() == null) {
			product.setKind(ProductKind.SALE_PURCHASE);
		}
		if (product.isSerializable() && !product.isInventoriable()) {
    		product.setInventoriable(true);
    	}
    	if (product.isInventoriable() && product.isComposition()) {
    		product.setComposition(false);
    	}
    	if (!product.isComposition() && product.isCompositionPrice()) {
    		product.setCompositionPrice(false);
    	}
	}

    private void checkValidCode(Product to, boolean update) throws ManagerBeanVetoListenerException {
		try {
			IManagerBean productBean = BeanManager.getManagerBean(Product.class);
			boolean checkInDomain = true;
			if (update) {
				String code = getCode(productBean, to);
				checkInDomain = !StringUtils.equals(to.getCode(), code);
			}
			if (checkInDomain) {
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(productBean.getFieldName(IEntityAlias.PRODUCT_CODE), to.getCode());
				List<ITransferObject> productList = productBean.getList(criteria);
				if (!productList.isEmpty()) {
					Product duplicate = (Product)productList.get(0);
					throw new ManagerBeanVetoListenerException("Ya existe un Producto con el mismo Código (" + duplicate.getName() + ")");	
				}				
			}

			int currentDomain = DomainManager.getCurrentDomain();
			IManagerBean domainBean = BeanManager.getManagerBean(Domain.class);
			Domain domain = (Domain)domainBean.get(currentDomain);
			if (domain != null && domain.isDomainManagement()) {
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(domainBean.getFieldName(IEntityAlias.DOMAIN_PARENT_ID), currentDomain);
				criteria.addEqualExpression(domainBean.getFieldName(IEntityAlias.DOMAIN_ENABLE_HEREDITY), true);
				List<ITransferObject> domainList = domainBean.getList(criteria);
				for (ITransferObject ito : domainList) {
					Domain child = (Domain)ito;
					Criteria childCriteria = new Criteria();
					childCriteria.addEqualExpression(productBean.getFieldName(IEntityAlias.PRODUCT_DOMAIN), child.getId());
					childCriteria.addEqualExpression(productBean.getFieldName(IEntityAlias.PRODUCT_CODE), to.getCode());
					childCriteria.setSkipDomainFilter(true);
					if (productBean.getCount(childCriteria) > 0) {
						throw new ManagerBeanVetoListenerException("Ya existe un Producto con el mismo Código en el Dominio: " + child.getName());							
					}
				}
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException("No se pudo chequear la existencia del Producto.");
		}
	}

	private String getCode(IManagerBean productBean, Product product) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(productBean.getFieldName(IEntityAlias.PRODUCT_ID), product.getId());
		ProjectionList projectionList = new ProjectionList();
		projectionList.add(Projection.property(productBean.getFieldName(IEntityAlias.PRODUCT_CODE)));
		List<?> productList = productBean.getList(projectionList, criteria);
		return (String)productList.get(0);		
	}

}