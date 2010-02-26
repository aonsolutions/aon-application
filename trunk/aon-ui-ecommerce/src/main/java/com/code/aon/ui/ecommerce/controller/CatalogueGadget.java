package com.code.aon.ui.ecommerce.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ebackoffice.Eccatalogue;
import com.code.aon.ebackoffice.dao.IEbackofficeAlias;
import com.code.aon.product.CatalogueCategory;
import com.code.aon.product.CatalogueItem;
import com.code.aon.product.Item;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.ecommerce.util.ECommerceUtil;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;

public class CatalogueGadget {

	private List<ITransferObject> list;
	private DataModel model;

	public DataModel getModel() {
		if (model == null || isAonEbackoffice()==true) {
			model = new ListDataModel(getList());
		}
		setList(null);
		return model;
	}

	public void setModel(DataModel model) {
		this.model = model;
	}

	public List<ITransferObject> getList() {
		try {
			if (list == null) {
				IManagerBean bean = BeanManager.getManagerBean(Eccatalogue.class);
				list = bean.getList(catalogueCriteriaBuilder());
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
			String msg = "La búsqueda falló";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
		return list;
	}

	// un poco feo pero hace lo que debe
	private Criteria catalogueCriteriaBuilder() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Eccatalogue.class);
		Date currentDate = Calendar.getInstance().getTime();
		Criteria criteria = new Criteria();
		Criteria startCriteria = new Criteria();
		Criteria endCriteria = new Criteria();
		Criteria nullCriteria = new Criteria();
		startCriteria.addLessThanOrEqualExpression(bean.getFieldName(IEbackofficeAlias.ECCATALOGUE_CATALOGUE_START_DATE), currentDate);
		endCriteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEbackofficeAlias.ECCATALOGUE_CATALOGUE_END_DATE), currentDate);
		nullCriteria.addNullExpression(bean.getFieldName(IEbackofficeAlias.ECCATALOGUE_CATALOGUE_END_DATE));
		criteria.addExpression(endCriteria.getExpression());
		criteria.addOrExpression(nullCriteria.getExpression());
		criteria.addExpression(startCriteria.getExpression());
		criteria.addEqualExpression(bean.getFieldName(IEbackofficeAlias.ECCATALOGUE_VISIBLE), true);
		return criteria;
	}

	public void setList(List<ITransferObject> list) {
		this.list = list;
	}

	public void onSelect(ActionEvent event) {
		ShopItemsController shop = ECommerceUtil.getShopItems();
		shop.resetCriteria(buildItemCriteria());
		shop.onSearch(null);
		ShopController sc = (ShopController) AonUtil.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER);
		sc.setBackView( sc.getContentView() );
		sc.setContentView( ViewEnum.ITEM_LIST );
		// Establece como titulo la cagegoria seleccionada
		Eccatalogue ecCat = (Eccatalogue) getModel().getRowData();
		shop.setSelectionTitle(ecCat.getCatalogue().getName());
	}

	private Criteria buildItemCriteria() {
		Criteria criteria;
		try {
			criteria = new Criteria();
			criteria.addExpression(buildCatalogueItemCriteria().getExpression());
			criteria.addOrExpression(buildCatalogueCategoryCriteria().getExpression());
		} catch (ManagerBeanException e) {
			String message = "Error al realizar la búsqueda";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message, e);
		}
		return criteria;
	}
	
	private Criteria buildCatalogueItemCriteria() throws ManagerBeanException {
			Eccatalogue ecCat = (Eccatalogue) getModel().getRowData();
			String identifier = BeanManager.getManagerBean(Item.class)
					.getFieldName(IECommerceConstants.ITEM_ALIAS);
			Criteria criteria = null;
			IManagerBean bean = BeanManager.getManagerBean(CatalogueItem.class);
			Criteria crit = new Criteria();
			crit.addEqualExpression(bean.getFieldName(IProductAlias.CATALOGUE_ITEM_CATALOGUE_ID) , ecCat.getCatalogue().getId());
			List<ITransferObject> cata = bean.getList(crit); 
			for (ITransferObject to : cata) {
				CatalogueItem c = (CatalogueItem) to;
				if (criteria == null) {
					criteria = new Criteria();
					criteria.addEqualExpression(identifier, c.getItem()
							.getId());
				} else {
					try {
						criteria.addOrExpression(identifier, c.getItem()
								.getId().toString());
					} catch (ExpressionException e) {
						e.printStackTrace();
					}
				}
			}
			if (criteria == null) {
				criteria = new Criteria();
				criteria.addEqualExpression(identifier, null);
			}
			return criteria;
	}

	private Criteria buildCatalogueCategoryCriteria() throws ManagerBeanException{
			Eccatalogue ecCat = (Eccatalogue) getModel().getRowData();
			String identifier = BeanManager.getManagerBean(Item.class)
					.getFieldName(IProductAlias.ITEM_PRODUCT_CATEGORY_ID);
			Criteria criteria = null;
			IManagerBean bean = BeanManager.getManagerBean(CatalogueCategory.class);
			Criteria crit = new Criteria();
			crit.addEqualExpression(bean.getFieldName(IProductAlias.CATALOGUE_CATEGORY_CATALOGUE_ID) , ecCat.getCatalogue().getId());
			List<ITransferObject> cata = bean.getList(crit); 
			for (ITransferObject to : cata) {
				CatalogueCategory c = (CatalogueCategory) to;
				if (criteria == null) {
					criteria = new Criteria();
					criteria.addEqualExpression(identifier, c.getCategory().getId());
				} else {
					try {
						criteria.addOrExpression(identifier, c.getCategory().getId().toString());
					} catch (ExpressionException e) {
						e.printStackTrace();
					}
				}
			}
			if (criteria == null) {
				criteria = new Criteria();
				criteria.addEqualExpression(identifier, null);
			}
			return criteria;
	}
	
	public void paint(OutputStream out, Object data) throws IOException {
		out.write(((Eccatalogue)getList().get(0)).getCatalogueImg());
	}
	
	public void paintIcon(OutputStream out, Object data) {
		Integer id = (Integer) data;
		for (ITransferObject to : getList()) {
			Eccatalogue ecCatalogue = (Eccatalogue)to;  
			if (id.equals(ecCatalogue.getId()) && ecCatalogue.getCatalogueIcon()!=null) {
				try {
					out.write(ecCatalogue.getCatalogueIcon());
				} catch (IOException e) {
					// Nada. La foto no se ve y punto.
				}
			}
		}
	}
	
	public boolean isIcon() {
		return false; 
//		return (getActiveConfig().getHeaderImg()!=null); 
	}
	
	public boolean isAonEbackoffice(){
//		aonEbackoffice=Boolean.parseBoolean(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(IECommerceConstants.AON_EBACKOFFICE));
//		return aonEbackoffice;
		return Boolean.parseBoolean(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(IECommerceConstants.AON_EBACKOFFICE));
	}
	
	

}
