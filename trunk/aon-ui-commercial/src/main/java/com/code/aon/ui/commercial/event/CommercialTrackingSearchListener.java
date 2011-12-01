package com.code.aon.ui.commercial.event;

import com.code.aon.commercial.ProjectCommercial;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.seller.Seller;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class CommercialTrackingSearchListener extends ControllerSearchListener {

	private Seller seller;
	
	private ProjectCommercial project;
		
	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}
	
	public ProjectCommercial getProject() {
		return project;
	}

	public void setProject(ProjectCommercial project) {
		this.project = project;
	}

	@Override
	protected void init() throws ManagerBeanException {
		IManagerBean sellerBean = BeanManager.getManagerBean(Seller.class);
		setSeller( (Seller) sellerBean.createNewTo() );
		IManagerBean projectBean = BeanManager.getManagerBean(ProjectCommercial.class);
		setProject( (ProjectCommercial) projectBean.createNewTo() );
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if ( (getSeller() != null) && (getSeller().getId() != null) ) {
			String alias = getFieldName(ICommercialAlias.COMMERCIAL_TRACKING_SELLER_ID);
			criteria.addEqualExpression(alias, getSeller().getId());			
		}
		if ( (getProject() != null) && (getProject().getId() != null) ) {
			String alias = getFieldName(ICommercialAlias.COMMERCIAL_TRACKING_PROJECT_ID);
			criteria.addEqualExpression(alias, getProject().getId());			
		}
	}
	
}