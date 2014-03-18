package com.code.aon.ui.warehouse.event;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.WarehouseTransfer;
import com.esferalia.aon.entity.IEntityAlias;

public class WarehouseTransferSearchListener extends ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
    private Warehouse sourceWarehouse;
    
    private Warehouse targetWarehouse;
    
	public Warehouse getSourceWarehouse() {
		return sourceWarehouse;
	}

	public void setSourceWarehouse(Warehouse sourceWarehouse) {
		this.sourceWarehouse = sourceWarehouse;
	}

	public Warehouse getTargetWarehouse() {
		return targetWarehouse;
	}

	public void setTargetWarehouse(Warehouse targetWarehouse) {
		this.targetWarehouse = targetWarehouse;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setSourceWarehouse((Warehouse)BeanManager.getManagerBean(Warehouse.class).createNewTo());
		setTargetWarehouse((Warehouse)BeanManager.getManagerBean(Warehouse.class).createNewTo());
	}
	
	@Override
	protected void completeCriteria(Criteria criteria)
			throws ManagerBeanException, ExpressionException {
		if( getSourceWarehouse()!=null && getSourceWarehouse().getId()!=null ){
			String alias = this.getFieldName(IEntityAlias.WAREHOUSE_TRANSFER_SOURCE_WAREHOUSE_ID);
			criteria.addEqualExpression(alias, getSourceWarehouse().getId());
		} else {
			String alias = this.getFieldName(IEntityAlias.WAREHOUSE_TRANSFER_ID);
			Collection<Integer> ids = getScopeWarehousesTransferList(IEntityAlias.WAREHOUSE_TRANSFER_SOURCE_WAREHOUSE_WORK_PLACE_SCOPE_ID);
			ids.addAll(getNullWarehousesTransferList(IEntityAlias.WAREHOUSE_TRANSFER_SOURCE_WAREHOUSE));
			if(ids.isEmpty()){
				ids.add(-1);
			}
			criteria.addInExpression(alias, ids);
		}
		if( getTargetWarehouse()!=null && getTargetWarehouse().getId()!=null ){
			String alias = this.getFieldName(IEntityAlias.WAREHOUSE_TRANSFER_TARGET_WAREHOUSE_ID);
			criteria.addEqualExpression(alias, getTargetWarehouse().getId());
		} else {
			String alias = this.getFieldName(IEntityAlias.WAREHOUSE_TRANSFER_ID);
			Collection<Integer> ids = getScopeWarehousesTransferList(IEntityAlias.WAREHOUSE_TRANSFER_TARGET_WAREHOUSE_WORK_PLACE_SCOPE_ID);
			ids.addAll(getNullWarehousesTransferList(IEntityAlias.WAREHOUSE_TRANSFER_TARGET_WAREHOUSE));
			if(ids.isEmpty()){
				ids.add(-1);
			}
			criteria.addInExpression(alias, ids);
		}
	}
	
	private Collection<Integer> getNullWarehousesTransferList(String alias) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(WarehouseTransfer.class);
		Criteria criteria = new Criteria();
		criteria.addNullExpression(bean.getFieldName(alias));
		List<Integer> ids = new LinkedList<Integer>();
		for(ITransferObject to: bean.getList(criteria)){
			WarehouseTransfer wt = (WarehouseTransfer) to;
			ids.add(wt.getId());
		}
		return ids;
	}
	
	private Collection<Integer> getScopeWarehousesTransferList(String alias) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(WarehouseTransfer.class);
		Criteria criteria = new Criteria();
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, bean.getFieldName(alias));
		List<Integer> ids = new LinkedList<Integer>();
		for(ITransferObject to: bean.getList(criteria)){
			WarehouseTransfer wt = (WarehouseTransfer) to;
			ids.add(wt.getId());
		}
		return ids;
	}
	
}