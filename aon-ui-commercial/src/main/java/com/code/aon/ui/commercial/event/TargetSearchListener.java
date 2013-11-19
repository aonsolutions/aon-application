package com.code.aon.ui.commercial.event;


import org.apache.commons.lang.ArrayUtils;

import com.code.aon.commercial.enumeration.TargetStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.MarketingAction;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.commercial.controller.CommercialCollectionsController;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class TargetSearchListener extends RegistrySearchListener implements ICommercialConstants {

	private TargetStatus[] targetStatuses;
	
	private Item item;

	private MarketingAction action;
	
	public MarketingAction getAction() {
		return action;
	}

	public void setAction(MarketingAction action) {
		this.action = action;
	}

	public TargetStatus[] getTargetStatuses() {
		return targetStatuses;
	}

	public void setTargetStatuses(TargetStatus[] targetStatuses) {
		this.targetStatuses = targetStatuses;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item registryItem) {
		this.item = registryItem;
	}

	@Override
	protected void init() throws ManagerBeanException {
		TargetStatus[] defaultTargetStatus = {TargetStatus.ACTIVE};
		setTargetStatuses(defaultTargetStatus);
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		setItem( (Item) itemBean.createNewTo() );
		CommercialCollectionsController collections = (CommercialCollectionsController) AonUtil.getRegisteredBean(ICommercialConstants.COLLECTIONS_CONTROLLER_NAME);
		collections.refreshActivities();
		setAction( (MarketingAction) BeanManager.getManagerBean(MarketingAction.class).createNewTo() );		
		super.init();
	}
	
	public void addActionSubQuery(MarketingAction action, Criteria criteria) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ActionTarget.class);			
		Criteria subCriteria = new Criteria();
		subCriteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACTION_TARGET_ACTION_ID), action.getId());
		String idAlias = bean.getFieldName(IEntityAlias.ACTION_TARGET_TARGET_ID);
		ProjectionList pl = new ProjectionList( Projection.property(idAlias) );
		Expression exp = ExpressionUtilities.getSubQueryExpression(ActionTarget.class, subCriteria, pl);
		criteria.addInExpression(getFieldName(IEntityAlias.TARGET_ID), exp);			
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if (!ArrayUtils.isEmpty(getTargetStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.TARGET_STATUS);
			addEnumToCriteria(criteria, status, getTargetStatuses());
		}
		if ( getItem()!=null && getItem().getId()!=null ) {
			String item = getController().resolveAlias("Registry_items_item_id");
			criteria.addEqualExpression(item, getItem().getId());			
		}
		if ( getAction()!=null && getAction().getId()!=null ) {
			addActionSubQuery(getAction(), criteria);			
		}		
		super.completeCriteria( criteria );
	}

}