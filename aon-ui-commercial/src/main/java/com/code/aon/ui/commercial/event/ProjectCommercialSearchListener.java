package com.code.aon.ui.commercial.event;

import com.code.aon.AonVersion;
import com.code.aon.commercial.Target;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.seller.Seller;
import com.code.aon.ui.commercial.controller.ProjectCommercialExportGwtController;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ProjectCommercialSearchListener extends ControllerSearchListener {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Target target;
	private Seller seller;
		
	private ProjectCommercialExportGwtController getProjectCommercialExportGwtController() {
		return (ProjectCommercialExportGwtController) AonUtil.getRegisteredBean("projectCommercialExport");
	}
	
	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		getProjectCommercialExportGwtController().setTarget(target.getId());
		this.target = target;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		getProjectCommercialExportGwtController().setSeller(seller.getId());
		this.seller = seller;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setTarget((Target)BeanManager.getManagerBean(Target.class).createNewTo());
		setSeller((Seller)BeanManager.getManagerBean(Seller.class).createNewTo());
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		System.out.println(criteria.getExpression());
		if ( getTarget()!=null && getTarget().getId()!=null ) {
			String alias = getFieldName(IEntityAlias.PROJECT_COMMERCIAL_TARGET_ID);
			criteria.addEqualExpression(alias, getTarget().getId());			
		}
		if ( getSeller()!=null && getSeller().getId()!=null ) {
			String alias = getFieldName(IEntityAlias.PROJECT_COMMERCIAL_SELLER_ID);
			criteria.addEqualExpression(alias, getSeller().getId());			
		}
	}

}