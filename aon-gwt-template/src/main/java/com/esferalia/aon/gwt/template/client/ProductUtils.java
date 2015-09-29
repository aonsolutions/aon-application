package com.esferalia.aon.gwt.template.client;

import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.Dialog;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;

public class ProductUtils {
	
	public static final String PRODUCT_NAME = "Nombre";
	public static final String PRODUCT_CODE = "C\u00f3digo";
	public static final String PRODUCT_PRICE_COST = "Precio Coste";
	public static final String PRODUCT_SALE_BASE = "Precio Venta Base";
	public static final String PRODUCT_CATEGORY = "Categor\u00eda";
	public static final String PRODUCT_BRAND = "Marca";
	public static final String PRODUCT_TAG = "Etiqueta";
	public static final String PRODUCT_TYPE = "Tipo";
	public static final String PRODUCT_VAT = "IVA";
	public static final String PRODUCT_IRPF = "IRPF";
	public static final String PRODUCT_INVENTORIABLE = "Inventoriable";
	public static final String PRODUCT_COMPOSED_PRODUCT = "Producto Compuesto";
	public static final String PRODUCT_COMPOSITION_PRICE = "Precio Composici\u00f3n";
	public static final String PRODUCT_STATUS = "Estado";
	public static final String PRODUCT_BARCODE = "C\u00f3digo de Barras";
	public static final String PRODUCT_DESCRIPTION = "Descripci\u00f3n";
	public static final String PRODUCT_DETAIL1 = "Detalle 1";
	public static final String PRODUCT_DETAIL2 = "Detalle 2";
	public static final String PRODUCT_DETAIL3 = "Detalle 3";
	
	public static Vector<String> productList(){
		Vector<String> v = new Vector<String>();
		v.add(PRODUCT_NAME);
		v.add(PRODUCT_CODE);
		v.add(PRODUCT_PRICE_COST);
		v.add(PRODUCT_SALE_BASE);
		v.add(PRODUCT_CATEGORY);
		v.add(PRODUCT_BRAND);
		v.add(PRODUCT_TAG);
		v.add(PRODUCT_TYPE);
		v.add(PRODUCT_VAT);
		v.add(PRODUCT_IRPF);
		v.add(PRODUCT_INVENTORIABLE);
		v.add(PRODUCT_COMPOSED_PRODUCT);
		v.add(PRODUCT_COMPOSITION_PRICE);
		v.add(PRODUCT_STATUS);
		v.add(PRODUCT_BARCODE);
		v.add(PRODUCT_DESCRIPTION);
		v.add(PRODUCT_DETAIL1);
		v.add(PRODUCT_DETAIL2);
		v.add(PRODUCT_DETAIL3);
		return v;
	}
	
	public static Vector<String> productOptionalList(){
		Vector<String> v = new Vector<String>();
		v.add(PRODUCT_CATEGORY);
		v.add(PRODUCT_BRAND);
		v.add(PRODUCT_TAG);
		v.add(PRODUCT_TYPE);
		v.add(PRODUCT_VAT);
		v.add(PRODUCT_IRPF);
		v.add(PRODUCT_INVENTORIABLE);
		v.add(PRODUCT_COMPOSED_PRODUCT);
		v.add(PRODUCT_COMPOSITION_PRICE);
		v.add(PRODUCT_STATUS);
		v.add(PRODUCT_BARCODE);
		v.add(PRODUCT_DESCRIPTION);
		v.add(PRODUCT_DETAIL1);
		v.add(PRODUCT_DETAIL2);
		v.add(PRODUCT_DETAIL3);
		return v;
	}
	
	public static Boolean productCheck(Dialog dialog, FlexTable flex_table) {
		if(!dialog.getType().equals("new") && !dialog.getType().equals("edit")){
			return false;
		}
		Integer num = 0;
		for(Integer i = 2; i< flex_table.getRowCount();i++){
			ListBox l = (ListBox) flex_table.getWidget(i, 1);
			if(estaProduct(l.getItemText(l.getSelectedIndex()))){
				num++;
			}
		}
		return num == 4;
	}
	
	public static Boolean estaProduct(String s) {
		switch (s) {
		case PRODUCT_NAME: return true;
		case PRODUCT_CODE: return true;
		case PRODUCT_PRICE_COST: return true;
		case PRODUCT_SALE_BASE: return true;
		}
		return false;
	}
	
}
