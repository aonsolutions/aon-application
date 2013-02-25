package com.code.aon.product.event;

import java.util.List;

import org.apache.commons.lang.StringUtils;

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
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.esferalia.aon.entity.IEntityAlias;

public class ProductBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	Product to = (Product) evt.getTo();
    	checkValidCode(to, false);
    }

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Product to = (Product) evt.getTo();
		checkValidCode(to, true);
	}

	private String getCode( IManagerBean productBean, Product product ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(productBean.getFieldName(IEntityAlias.PRODUCT_ID), product.getId());
		ProjectionList pl = new ProjectionList();
		pl.add(Projection.property(productBean.getFieldName(IEntityAlias.PRODUCT_CODE)));
		List<?> list = productBean.getList(pl, criteria);
		return (String) list.get(0);		
	}
    
    private void checkValidCode(Product to, boolean update) throws ManagerBeanVetoListenerException {
		try {
			IManagerBean productBean = BeanManager.getManagerBean(Product.class);
			boolean checkInDomain = true;
			if ( update ) {
				String code = getCode(productBean, to);
				checkInDomain = ! StringUtils.equals(to.getCode(), code);
			}
			if ( checkInDomain ) {
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(productBean.getFieldName(IEntityAlias.PRODUCT_CODE),to.getCode());
				List<ITransferObject> list = productBean.getList(criteria);
				if (! list.isEmpty()) {
					Product duplicate = (Product) list.get(0);
					throw new ManagerBeanVetoListenerException(
							"Ya existe un producto con el mismo código (" + duplicate.getName() + ")");	
				}				
			}
			// Si estamos grabando un producto en un dominio padre, se chequea que no exista el 
			// código en ningún dominio hijo con la propiedad "heridity" habilitada.
			int currentDomain = DomainManager.getCurrentDomain();
			IManagerBean domainBean = BeanManager.getManagerBean(Domain.class);
			Domain domain = (Domain) domainBean.get(currentDomain);
			if ( (domain != null) && domain.isDomainManagement() ) {
				Criteria c = new Criteria();
				c.addEqualExpression(domainBean.getFieldName(IEntityAlias.DOMAIN_PARENT_ID), currentDomain);
				c.addEqualExpression(domainBean.getFieldName(IEntityAlias.DOMAIN_ENABLE_HEREDITY), true);
				List<ITransferObject> domains = domainBean.getList(c);
				for (ITransferObject d : domains) {
					Domain child = (Domain) d;
					Criteria childCriteria = new Criteria();
					childCriteria.addEqualExpression(productBean.getFieldName(IEntityAlias.PRODUCT_DOMAIN),child.getId());
					childCriteria.addEqualExpression(productBean.getFieldName(IEntityAlias.PRODUCT_CODE),to.getCode());
					childCriteria.setSkipDomainFilter(true);
					if ( productBean.getCount(childCriteria) > 0 ) {
						throw new ManagerBeanVetoListenerException("Ya existe un producto con el mismo código "
								+ " en el dominio '"+child.getName()+" " + child.getDescription() +"'");							
					}
				}
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException("No se pudo chequear la existencia del producto.");
		}
	}
    
}