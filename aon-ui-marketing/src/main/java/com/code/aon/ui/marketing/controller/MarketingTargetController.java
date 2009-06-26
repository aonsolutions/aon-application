package com.code.aon.ui.marketing.controller;

import java.io.Serializable;
import java.util.List;

import com.code.aon.commercial.Target;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.MarketingTarget;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ui.commercial.controller.TargetController;

/**
 * Controller used in the offer maintenance.
 */
public class MarketingTargetController extends TargetController {
	
	private IManagerBean mtBean;
	
	@Override
	protected String resolveAlias( String alias ) {
		String newKey = alias;
		if ( newKey.startsWith("Target_") ) {
			newKey = newKey.replace("Target_", "MarketingTarget_target_");
		}			
		return super.resolveAlias( newKey );
	}

	public MarketingTarget getMT() {
		return (MarketingTarget) super.getTo();
	}
	
	@Override
	public ITransferObject getTo() {
		MarketingTarget mt = getMT();
		return mt != null ? mt.getTarget() : null;
	}
	
	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if ( this.mtBean == null ) {
			IManagerBean targetBean = BeanManager.getManagerBean(Target.class);	
			this.mtBean = new MarketingTargetManagedBean( super.getManagerBean(), targetBean );
		}
		return this.mtBean;
	}

	public class MarketingTargetManagedBean implements IManagerBean {
		
		private IManagerBean bean;
		
		private IManagerBean targetBean;
		
		public MarketingTargetManagedBean(IManagerBean bean, IManagerBean targetBean) throws ManagerBeanException {
			this.bean = bean;
			this.targetBean = targetBean;
		}

		@Override
		public ITransferObject get(Serializable pk) throws ManagerBeanException {
			return bean.get(pk);
		}

		@Override
		public int getCount(Criteria criteria) throws ManagerBeanException {
			return bean.getCount(criteria);
		}

		@Override
		public String getFieldName(String alias) throws ManagerBeanException {
			String newAlias = alias.replace("Target_", "MarketingTarget_target_");
			return bean.getFieldName(newAlias);
		}

		@Override
		public Serializable getId(ITransferObject to)
				throws ManagerBeanException {
			return bean.getId(to);
		}

		@Override
		public List<ITransferObject> getList(Criteria criteria, int offset,
				int count) throws ManagerBeanException {
			return bean.getList(criteria, offset, count);
		}

		@Override
		public List<ITransferObject> getList(Criteria criteria)
				throws ManagerBeanException {
			return bean.getList(criteria);
		}

		@Override
		public List getList(ProjectionList projectionList, Criteria criteria)
				throws ManagerBeanException {
			return bean.getList(projectionList, criteria);
		}

		@Override
		public Class getPOJOClass() {
			return bean.getPOJOClass();
		}

		@Override
		public Object getUniqueResult(Projection projection, Criteria criteria)
				throws ManagerBeanException {
			return bean.getUniqueResult(projection, criteria);
		}

		@Override
		public void setId(ITransferObject to, Serializable id)
				throws ManagerBeanException {
			if ( to instanceof Target) {
				targetBean.setId(to, id);
			} else {
				bean.setId(to, id);	
			}
		}

		@Override
		public void setProperty(ITransferObject to, String propertyName,
				Object value) throws ManagerBeanException {
			if ( to instanceof Target) {
				targetBean.setProperty(to, propertyName, value);
			} else {
				bean.setProperty(to, propertyName, value);	
			}
		}

		@Override
		public ITransferObject createNewTo() throws ManagerBeanException {
			return bean.createNewTo();
		}

		@Override
		public void initializePOJO(ITransferObject to)
				throws ManagerBeanException {
			bean.initializePOJO(to);
		}

		private ITransferObject getMarketingTarget( ITransferObject to ) {
			MarketingTarget mt = getMT();
			mt.setTarget( (Target) to );
			return mt;
		}
		
		@Override
		public ITransferObject insert(ITransferObject to)
				throws ManagerBeanException {
			return getMarketingTarget(targetBean.insert(to));
		}

		@Override
		public ITransferObject insertOrUpdate(ITransferObject to)
				throws ManagerBeanException {
			return getMarketingTarget(targetBean.insertOrUpdate(to));
		}

		@Override
		public boolean remove(ITransferObject to) throws ManagerBeanException {
			return targetBean.remove(to);
		}

		@Override
		public ITransferObject update(ITransferObject to)
				throws ManagerBeanException {
			return getMarketingTarget(targetBean.update(to));
		}
		
	}
		
}