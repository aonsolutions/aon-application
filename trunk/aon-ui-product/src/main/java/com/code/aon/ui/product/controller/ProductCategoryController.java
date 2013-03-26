package com.code.aon.ui.product.controller;

import java.util.Arrays;
import java.util.Comparator;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.product.ProductCategory;
import com.code.aon.ui.form.BasicController;

public class ProductCategoryController extends BasicController {

	@Override
	public void onAccept(ActionEvent event) {
		ProductCategory pc = (ProductCategory) getTo();
		final String[] details =  new String[]{pc.getDetail(), pc.getDetail2(), pc.getDetail3()};
		Arrays.sort(details, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				Integer i1 = StringUtils.isEmpty(o1) ? 100 : ArrayUtils.indexOf(details, o1);
				Integer i2 = StringUtils.isEmpty(o2) ? 100 : ArrayUtils.indexOf(details, o2);
				return i1.compareTo(i2);
			}
		});
		pc.setDetail(details[0]);
		pc.setDetail2(details[1]);
		pc.setDetail3(details[2]);
		super.onAccept(event);
	}
	
}