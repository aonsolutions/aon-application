package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.cms.Faq;
import com.code.aon.cms.FaqCategory;
import com.code.aon.cms.FaqCategoryDetail;
import com.code.aon.cms.FaqDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;

public class FaqCategoryHandler {

	private String label;

	private String url;

	private ArrayList<FaqHandler> list;
	
	public FaqCategoryHandler (FaqCategoryDetail fcd) {
		label = fcd.getLabel();
		String faq = Templates.FAQ.getHtmlName();
		faq = faq.replaceAll("%NAME%", fcd.getFaqCategory().getAlias());
		url = faq;
		list = getFaqList(fcd.getFaqCategory());
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

	private ArrayList<FaqHandler> getFaqList(FaqCategory fc) {
		ArrayList<FaqHandler> list = new ArrayList<FaqHandler>();
		List<ITransferObject> ld;
		try {
			IManagerBean bean = BeanManager.getManagerBean(Faq.class);
			IManagerBean detailBean = BeanManager.getManagerBean(FaqDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.FAQ_FAQ_CATEGORY_ID), fc.getId());
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.FAQ_ACTIVE), true);
			criteria.addOrder(bean.getFieldName(ICMSAlias.FAQ_POSITION));
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			Criteria detailCriteria;
			Faq f;
			FaqDetail fd;
			for (int i = 0; i < l.size(); i++) {
				f = (Faq)l.get(i);
				detailCriteria = new Criteria();
				detailCriteria.addEqualExpression(detailBean.getFieldName(ICMSAlias.FAQ_DETAIL_FAQ_ID), f.getId());
				detailCriteria.addEqualExpression(detailBean.getFieldName(ICMSAlias.FAQ_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				ld = (List<ITransferObject>)detailBean.getList(detailCriteria);
				if (ld.size() > 0) {
					fd = (FaqDetail)ld.get(0);
					FaqHandler fh = new FaqHandler(fd);
					list.add(fh);
				}
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		} finally {
			ld = null;
		}
		return list;
	}
	
}
