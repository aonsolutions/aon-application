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
import com.esferalia.aon.entity.IEntityAlias;

public class ItemBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Item item = (Item)evt.getTo();
		checkItem(item);

		try {
			checkValidSerialNumber(item, null);
			checkValidBarCode(item, null);
			checkValidDetails(item, null);
		} catch(ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Item item = (Item)evt.getTo();
		checkItem(item);

		try {
			Item itemDB = (Item)BeanManager.getManagerBean(Item.class).get(item.getId());
			checkValidSerialNumber(item, itemDB);
			checkValidBarCode(item, itemDB);
			checkValidDetails(item, itemDB);
		} catch(ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

	private void checkItem(Item item) {
    	if (StringUtils.isEmpty(item.getProduct().getCode())) {
    		item.getProduct().setCode(item.getProduct().getId().toString());
    	}
    	if (item.getProduct().isSerializable() && !item.getProduct().isInventoriable()) {
    		item.getProduct().setInventoriable(true);
    	}
    	if (item.getProduct().isInventoriable() && item.getProduct().isComposition()) {
    		item.getProduct().setComposition(false);
    	}
    	if (!item.getProduct().isComposition() && item.getProduct().isCompositionPrice()) {
    		item.getProduct().setCompositionPrice(false);
    	}
	}

    private void checkValidSerialNumber(Item to, Item itemDB) throws ManagerBeanException {
    	if (StringUtils.isNotEmpty(to.getSerialNumber())) {
			boolean checkInDomain = true;
			if (itemDB != null) {
				checkInDomain = !StringUtils.equals(to.getSerialNumber(), itemDB.getSerialNumber());
			}
			if (checkInDomain) {
				IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
				Criteria criteria = new Criteria();
				if (itemDB != null) {
					criteria.addNotEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_ID), to.getId());
				}
				criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_ID), to.getProduct().getId());
				criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_SERIAL_NUMBER), to.getSerialNumber());
		    	if (StringUtils.isNotEmpty(to.getDetail())) {
					criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_DETAIL), to.getDetail());
		    	}
		    	if (StringUtils.isNotEmpty(to.getDetail2())) {
					criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_DETAIL2), to.getDetail2());
		    	}
		    	if (StringUtils.isNotEmpty(to.getDetail3())) {
					criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_DETAIL3), to.getDetail3());
		    	}
				List<ITransferObject> itemList = itemBean.getList(criteria);
				if (!itemList.isEmpty()) {
					Item duplicate = (Item)itemList.get(0);
					throw new ManagerBeanException("Ya existe el Número de Serie (" + duplicate.getSerialNumber() + ")");	
				}				
			}

    	}
	}

    private void checkValidBarCode(Item to, Item itemDB) throws ManagerBeanException {
    	if (StringUtils.isNotEmpty(to.getBarcode())) {
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			boolean checkInDomain = true;
			if (itemDB != null) {
				checkInDomain = !StringUtils.equals(to.getBarcode(), itemDB.getBarcode());
			}
			if (checkInDomain) {
				Criteria criteria = new Criteria();
				if (itemDB != null) {
					criteria.addNotEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_ID), to.getId());
				}
				criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_BARCODE), to.getBarcode());
				List<ITransferObject> itemList = itemBean.getList(criteria);
				if (!itemList.isEmpty()) {
					Item duplicate = (Item)itemList.get(0);
					throw new ManagerBeanException("Ya existe un Producto con el mismo Código de Barras (" + duplicate.getFullName() + ")");	
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
					childCriteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_DOMAIN), child.getId());
					childCriteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_BARCODE), to.getBarcode());
					childCriteria.setSkipDomainFilter(true);
					if (itemBean.getCount(childCriteria) > 0) {
						throw new ManagerBeanException("Ya existe un Producto con el mismo Código de Barras en el Dominio " + child.getName());							
					}
				}
			}
    	}
	}

    private void checkValidDetails(Item to, Item itemDB) throws ManagerBeanException {
    	if (StringUtils.isNotEmpty(to.getDetail())) {
			boolean checkInDomain = true;
			if (itemDB != null) {
				checkInDomain = !StringUtils.equals(to.getDetail(), itemDB.getDetail()) || !StringUtils.equals(to.getDetail2(), itemDB.getDetail2()) 
									|| !StringUtils.equals(to.getDetail3(), itemDB.getDetail3());
			}
			if (checkInDomain) {
				IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
				Criteria criteria = new Criteria();
				if (itemDB != null) {
					criteria.addNotEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_ID), to.getId());
				}
				criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_ID), to.getProduct().getId());
				criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_DETAIL), to.getDetail());
		    	if (StringUtils.isNotEmpty(to.getDetail2())) {
					criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_DETAIL2), to.getDetail2());
		    	}
		    	if (StringUtils.isNotEmpty(to.getDetail3())) {
					criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_DETAIL3), to.getDetail3());
		    	}
		    	if (StringUtils.isNotEmpty(to.getSerialNumber())) {
					criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_SERIAL_NUMBER), to.getSerialNumber());
		    	} else {
					criteria.addNullExpression(itemBean.getFieldName(IEntityAlias.ITEM_SERIAL_NUMBER));
		    	}
		    	List<ITransferObject> itemList = itemBean.getList(criteria);
				if (!itemList.isEmpty()) {
					Item duplicate = (Item)itemList.get(0);
					String message = "Ya existe el Detalle (" + duplicate.getDetails() + ")";
			    	if (StringUtils.isNotEmpty(to.getSerialNumber())) {
			    		message += " para el Numero de Serie: " + to.getSerialNumber();
			    	}
					throw new ManagerBeanException(message);	
				}				
			}
    	}
	}

}