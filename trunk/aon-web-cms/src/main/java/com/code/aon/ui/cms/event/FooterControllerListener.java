package com.code.aon.ui.cms.event;

import java.util.List;

import com.code.aon.cms.Footer;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class FooterControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		try {
			Footer footer = (Footer)event.getController().getTo();
			IManagerBean bean = BeanManager.getManagerBean(Footer.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.FOOTER_DEFAULT_), true);
			List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
			if (list.size() == 0) {
				footer.setDefault_(true);
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}
	
}
