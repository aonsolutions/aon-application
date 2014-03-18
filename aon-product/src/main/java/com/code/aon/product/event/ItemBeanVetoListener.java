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
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.esferalia.aon.entity.IEntityAlias;

public class ItemBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Item item = (Item)evt.getTo();
		checkValidCode(item, false);
		checkItem(item);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Item item = (Item)evt.getTo();
		checkValidCode(item, true);
		checkItem(item);
	}

	private void checkItem(Item item) {
    	if (StringUtils.isEmpty(item.getProduct().getCode())) {
    		item.getProduct().setCode(item.getId().toString());
    	}
    	if (item.getProduct().isInventoriable()) {
    		item.getProduct().setComposition(false);
    	}
    	if (!item.getProduct().isComposition()) {
    		item.getProduct().setCompositionPrice(false);
    	}
	}

	private String getBarcode( IManagerBean itemBean, Item item ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_ID),item.getId());
		ProjectionList pl = new ProjectionList();
		pl.add(Projection.property(itemBean.getFieldName(IEntityAlias.ITEM_BARCODE)));
		List<?> list = itemBean.getList(pl, criteria);
		return (String) list.get(0);		
	}
	
    private void checkValidCode(Item to, boolean update) throws ManagerBeanVetoListenerException {
    	if ( StringUtils.isEmpty(to.getBarcode()) ) {
    		return;
    	}
		try {
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			boolean checkInDomain = true;
			if ( update ) {
				String barcode = getBarcode(itemBean, to);
				checkInDomain = ! StringUtils.equals(to.getBarcode(), barcode);
			}
			if ( checkInDomain ) {
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_BARCODE),to.getBarcode());
				List<ITransferObject> list = itemBean.getList(criteria);
				if (! list.isEmpty()) {
					Item duplicate = (Item) list.get(0);
					throw new ManagerBeanVetoListenerException(
							"Ya existe un producto con el mismo código de barras (" + duplicate.getFullName() + ")");	
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
					childCriteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_DOMAIN),child.getId());
					childCriteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_BARCODE),to.getBarcode());
					childCriteria.setSkipDomainFilter(true);
					if ( itemBean.getCount(childCriteria) > 0 ) {
						throw new ManagerBeanVetoListenerException("Ya existe un producto con el mismo código de barras "
								+ " en el dominio '"+child.getName()+" " + child.getDescription() +"'");							
					}
				}
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException("No se pudo chequear la existencia del producto.");
		}
	}
	
}