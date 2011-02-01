package com.code.aon.ui.ecommerce.controller;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.ecommerce.util.ECommerceUtil;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;

public class SearchGadget {
	
	public SearchGadget() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		String txt = ResourceBundle.getBundle(BASE_NAME,locale).getString("aon_ecommerce_search_gadget_text");
		setSearch(txt);
	}

	private static final String BASE_NAME = "com.code.aon.ui.ecommerce.i18n.messages";
	private String search;

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public void onSearch(ActionEvent event) throws ManagerBeanException,
			ExpressionException {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		String txt = ResourceBundle.getBundle(BASE_NAME,locale).getString("aon_ecommerce_search_gadget_text");
		String identifier = BeanManager.getManagerBean(Item.class)
				.getFieldName(IECommerceConstants.ITEM_NAME_ALIAS);

		Criteria c = new Criteria();
		if (getSearch() != null && !getSearch().equals("") && !getSearch().equals(txt)) {
			c.addExpression(identifier, "*" + getSearch() + "*");
		}
		search(c);
		//setSearch(null);
		onTextOutput(null);

		ShopController sc = (ShopController) AonUtil.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER);
		sc.setBackView( sc.getContentView() );
		sc.setContentView( ViewEnum.ITEM_LIST );

		setSearch(txt);
	}

	public void search(Criteria criteria) {
		ShopItemsController shop = ECommerceUtil.getShopItems();
		shop.resetCriteria(criteria);
		shop.onSearch(null);
		System.out.println();
		// Establece como titulo la cagegoria seleccionada
		shop.setSelectionTitle("Resultado de la busqueda personalizada");
	}
	
	public void onTextInput(ActionEvent event){
		setSearch(null);
	}
	public void onTextOutput(ActionEvent event){
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		String txt = ResourceBundle.getBundle(BASE_NAME,locale).getString("aon_ecommerce_search_gadget_text");
		setSearch(txt);
	}
}
