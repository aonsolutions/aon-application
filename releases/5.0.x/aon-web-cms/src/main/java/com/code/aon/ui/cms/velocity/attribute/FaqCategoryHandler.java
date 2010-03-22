package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;

import com.code.aon.cms.FaqCategoryDetail;
import com.code.aon.cms.enumeration.Templates;

public class FaqCategoryHandler {

	private String label;

	private String url;

	private ArrayList<FaqHandler> list;
	
	public FaqCategoryHandler (FaqCategoryDetail fcd, ArrayList<FaqHandler> list) {
		this.label = fcd.getLabel();
		String faq = Templates.FAQ.getHtmlName();
		faq = faq.replaceAll("%NAME%", fcd.getFaqCategory().getAlias());
		this.url = faq;
		this.list = list;
	}

	public String getLabel() {
		return label;
	}

	public String getUrl() {
		return url;
	}

	public ArrayList<FaqHandler> getList() {
		return list;
	}

}
