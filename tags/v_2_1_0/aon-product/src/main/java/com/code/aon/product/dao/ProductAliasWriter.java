package com.code.aon.product.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.product.Brand;
import com.code.aon.product.Item;
import com.code.aon.product.ItemAttachment;
import com.code.aon.product.ItemPos;
import com.code.aon.product.ItemTariff;
import com.code.aon.product.Product;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.ProductCategoryGroup;
import com.code.aon.product.ProductCategoryTree;
import com.code.aon.product.Tariff;
import com.code.aon.product.Tax;
import com.code.aon.product.TaxDetail;

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
		File file = new File("/PROYECTOS/aon-product/src/com/code/aon/product/dao/IProductAlias.java");
//		File file = new File("c:/IProductAlias.java");
		String[] classes = new String[12]; 
		classes[0] = Brand.class.getName();
		classes[1] = Item.class.getName();
		classes[2] = ItemAttachment.class.getName();
		classes[3] = ItemPos.class.getName();
		classes[4] = ItemTariff.class.getName();
		classes[5] = Product.class.getName();
		classes[6] = ProductCategory.class.getName();
		classes[7] = ProductCategoryGroup.class.getName();
		classes[8] = ProductCategoryTree.class.getName();
		classes[9] = Tariff.class.getName();
		classes[10] = Tax.class.getName();
		classes[11] = TaxDetail.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.product.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}