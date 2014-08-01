package com.code.aon.warehouse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.InventoryDB;

@Entity
@Table(name="inventory")
public class Inventory extends InventoryDB {

	private static final Logger LOGGER = LoggerFactory.getLogger(Inventory.class.getName());
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Set<InventoryDetail> details = new HashSet<InventoryDetail>();

	@OneToMany(mappedBy = "inventory", cascade={CascadeType.REMOVE})
	public Set<InventoryDetail> getDetails() {
		return details;
	}
	public void setDetails(Set<InventoryDetail> details) {
		this.details = details;
	}
	
	@Transient
	public boolean isClosed() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Inventory.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVENTORY_WAREHOUSE_ID), getWarehouse().getId());
			criteria.addOrder(bean.getFieldName(IEntityAlias.INVENTORY_INVENTORY_DATE), false);
	        List<ITransferObject> list = bean.getList(criteria, 0, 1);
	        if (! list.isEmpty() ) {
	        	Inventory inventory = (Inventory) list.get(0);
	        	return ! ObjectUtils.equals(getId(), inventory.getId());
	        }
		} catch (ManagerBeanException e) {
			LOGGER.error("Error checking if inventory is closed", e);
		}
		return false;
	}
	
}