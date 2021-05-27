package com.code.aon.ui.product.util.print;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.customer.Customer;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.enumeration.RegistryMode;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

import net.sf.jasperreports.engine.JRDefaultScriptlet;


public class ProductReportScriptlet extends JRDefaultScriptlet implements Serializable {
		
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductReportScriptlet.class.getName());
	
	public String getCode(Item item, Customer customer){
		try {
			if(item.getProduct().isSerializable()){
				item = item.getProduct().getBaseItem();
			}
			IManagerBean bean = BeanManager.getManagerBean(RegistryItem.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ITEM_ITEM_ID), item.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ITEM_REGISTRY_ID), customer.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ITEM_TYPE), RegistryMode.CUSTOMER);
			criteria.addOrder(bean.getFieldName(IEntityAlias.REGISTRY_ITEM_PRIORITY), true);
			List<ITransferObject> list = bean.getList(criteria);
			if(list !=null && list.size()>0){
				return ((RegistryItem)list.get(0)).getCode();
			}
		} catch (Exception e) {
			String msg = "Se ha producido un error, vuelva a intentarlo pasados unos segundos";
			AonUtil.addErrorMessage(msg);
			LOGGER.error(msg,e);
		}
		return item.getProduct().getCode();
	}

}