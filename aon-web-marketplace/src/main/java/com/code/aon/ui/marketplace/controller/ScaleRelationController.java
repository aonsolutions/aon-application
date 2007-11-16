package com.code.aon.ui.marketplace.controller;

import java.sql.SQLException;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketplace.Scale;
import com.code.aon.marketplace.ScaleRelation;
import com.code.aon.marketplace.dao.IMarketplaceAlias;
import com.code.aon.marketplace.enumeration.ScaleModel;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.GridController;
import com.code.aon.ui.menu.jsf.MenuEvent;

/**
 * Controller used in the tax maintenance.
 */
public class ScaleRelationController extends GridController {

	private String scale_id;

	private int scale_model;

	/**
	 * The empty constructor.
	 */
	public ScaleRelationController() {
	}
	
	/**
	 * On reset. Method launched by the menu
	 * 
	 * @param event the event
	 */
	@SuppressWarnings("unused")
	public void onReset(MenuEvent event) {
		super.onReset(null);
	}

	public void onLoad(ActionEvent event) throws ManagerBeanException, ExpressionException {
		IManagerBean scaleBean = BeanManager.getManagerBean(Scale.class);
		List<ITransferObject> scaleLst = scaleBean.getList(null);
		Scale s = new Scale();
		if (scaleLst.size()>0) {
			s = (Scale)scaleLst.get(0);
			this.scale_model = s.getScaleModel().ordinal();
		}
		else this.scale_model = ScaleModel.NONE.ordinal();
		
		setCriteria(null);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(this.getFieldName(IMarketplaceAlias.SCALE_RELATION_TYPE), "C");
		criteria.addExpression(this.getFieldName(IMarketplaceAlias.SCALE_RELATION_SCALE_MODEL), "" + this.scale_model);
		setCriteria(criteria);
		onSearch(event);
	}
	
    /**
     * On accept. Sets the current tax to null after adding it.
     * 
     * @param event the event
     */
    @Override
	public void onAccept(ActionEvent event) {
		ScaleRelation sr = (ScaleRelation)getTo();
		String scale_id1 = this.scale_id.substring(0, this.scale_id.indexOf(" -"));
		String scale_id2 = this.scale_id.substring(this.scale_id.indexOf("- ") + 2, this.scale_id.length());
		sr.setScale_id1(scale_id1);
		sr.setScale_id2(scale_id2);
		sr.setType("C");
		sr.setScaleModel(ScaleModel.values()[this.scale_model]);
		
    	super.accept(event);
		super.resetTo();
	}

    /**
     * On accept. Sets the current tax to null after adding it.
     * 
     * @param event the event
     * @throws ManagerBeanException 
     */
    @Override
	public void onSelect(ActionEvent event) {
		try {
			ScaleRelation sr = (ScaleRelation)this.getModel().getRowData();
			this.scale_id = "" + sr.getScale_id1() + " - " + sr.getScale_id2();
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
    	super.onSelect(event);
	}

	/**
	 * On remove. Sets the current productCategory to null after removing it.
	 * 
	 * @param event the event
	 */
	@Override
	public void onRemove(ActionEvent event) {
		remove( event );
		resetTo();
	}
	
	public String getScale_id() {
		return scale_id;
	}

	public void setScale_id(String scale_id) {
		this.scale_id = scale_id;
	}

	public String getScaleRelationDescription() throws ManagerBeanException, SQLException {
		ScaleRelation sr = (ScaleRelation)this.getModel().getRowData();
		String id1 = sr.getScale_id1();
		String id2 = sr.getScale_id2();
	    FacesContext ctx = FacesContext.getCurrentInstance();
	    ValueBinding vb = ctx.getApplication().createValueBinding( "#{scale}" );
		ScaleController sc = (ScaleController) vb.getValue(ctx);
		return sc.getScaleRelationDescription(id1, id2);
	}
	
	public String getAonRelationDescription() throws ManagerBeanException, SQLException {
		ScaleRelation sr = (ScaleRelation)this.getModel().getRowData();
		int id = sr.getAon_id().intValue();
	    FacesContext ctx = FacesContext.getCurrentInstance();
	    ValueBinding vb = ctx.getApplication().createValueBinding( "#{scale}" );
		ScaleController sc = (ScaleController) vb.getValue(ctx);
		return sc.getAonRelationDescription(id);
	}
	
}