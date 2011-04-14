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
import com.code.aon.product.ItemAlternative;
import com.code.aon.product.ItemAttachment;
import com.code.aon.product.ItemSupplier;
import com.code.aon.product.Product;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.ProductCategoryGroup;
import com.code.aon.product.ProductCategoryTree;
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
		File file = new File("/AON-TRUNK/aon-product/src/main/java/com/code/aon/product/dao/IProductAlias.java");
		String[] classes = new String[] {
			Brand.class.getName(),
			Catalogue.class.getName(),
			CatalogueCategory.class.getName(),
			CatalogueItem.class.getName(),
			Item.class.getName(),
			ItemAlternative.class.getName(),
			ItemAttachment.class.getName(),
			ItemSupplier.class.getName(),
			Product.class.getName(),
			ProductCategory.class.getName(),
			ProductCategoryGroup.class.getName(),
			ProductCategoryTree.class.getName(),
			TariffCatalogue.class.getName(),
		};
		HibernateUtil.getSessionFactory(HibernateUtil.getSessionFactoryName());
		AliasWriter writer = new AliasWriter("com.code.aon.product.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}