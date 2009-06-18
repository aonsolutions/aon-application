package com.code.aon.product;

import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;

public class ProductCategorySearchTest {

	public static void test1() throws ManagerBeanException {
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		// criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_PRODUCT_ID), new Integer(2));
		// criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_PRODUCT_CATEGORY_NAME), "categoría1");
		criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_PRODUCT_CATEGORY_ID), new Integer(1));		
		Iterator iter = itemBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Item item = (Item)iter.next();
			System.out.println(item.getProduct().getName());
		}
	}

	public static void test2() throws ManagerBeanException {
		IManagerBean productBean = BeanManager.getManagerBean(Product.class);
		Criteria criteria = new Criteria();
		// criteria.addEqualExpression("product.category.group.id", new Integer(2));
		String fieldName = productBean.getFieldName(IProductAlias.PRODUCT_PRODUCT_CATEGORY_ID);
		criteria.addEqualExpression( fieldName, new Integer(2));
		Iterator iter = productBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Product product = (Product)iter.next();
			System.out.println(product.getName());
		}
	}
	
	/**
	 * @param args
	 * @throws ManagerBeanException 
	 */
	public static void main(String[] args) throws ManagerBeanException {
		test1();
	}
}
