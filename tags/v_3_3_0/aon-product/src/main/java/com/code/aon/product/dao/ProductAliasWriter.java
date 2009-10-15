package com.code.aon.product.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.product.Brand;
import com.code.aon.product.Catalogue;
import com.code.aon.product.CatalogueCategory;
import com.code.aon.product.CatalogueItem;
import com.code.aon.product.Item;
import com.code.aon.product.ItemAttachment;
import com.code.aon.product.ItemPos;
import com.code.aon.product.Product;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.ProductCategoryGroup;
import com.code.aon.product.ProductCategoryTree;
import com.code.aon.product.Tariff;
import com.code.aon.product.TariffCatalogue;

/**
 * @author Consulting & Development. ecastellano - 22/01/2007
 *
 */
public class ProductAliasWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/AON-PROJECT/aon-product/src/main/java/com/code/aon/product/dao/IProductAlias.java");
		String[] classes = new String[13]; 
		classes[0] = Brand.class.getName();
		classes[1] = Catalogue.class.getName();
		classes[2] = CatalogueCategory.class.getName();
		classes[3] = CatalogueItem.class.getName();
		classes[4] = Item.class.getName();
		classes[5] = ItemAttachment.class.getName();
		classes[6] = ItemPos.class.getName();
		classes[7] = Product.class.getName();
		classes[8] = ProductCategory.class.getName();
		classes[9] = ProductCategoryGroup.class.getName();
		classes[10] = ProductCategoryTree.class.getName();
		classes[11] = Tariff.class.getName();
		classes[12] = TariffCatalogue.class.getName();
		HibernateUtil.getSessionFactory();
		AliasWriter writer = new AliasWriter("com.code.aon.product.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}