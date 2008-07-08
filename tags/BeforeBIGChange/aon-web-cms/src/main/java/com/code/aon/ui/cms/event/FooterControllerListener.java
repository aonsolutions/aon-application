package com.code.aon.ui.cms.event;

import java.util.List;

import com.code.aon.cms.Footer;
import com.code.aon.cms.Section;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.FooterController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class FooterControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try {
			Criteria criteria = event.getController().getCriteria();
			IManagerBean bean = BeanManager.getManagerBean(Footer.class);
			criteria.addOrder(bean.getFieldName(ICMSAlias.FOOTER_DEFAULT_),false);
			criteria.addOrder(bean.getFieldName(ICMSAlias.FOOTER_ALIAS));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

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
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		FooterController c = (FooterController)event.getController();
		try {
			c.onSelectBannerCategories(null);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		} catch (ExpressionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		FooterController c = (FooterController)event.getController();
		try {
			c.onSelectBannerCategories(null);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		} catch (ExpressionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		FooterController controller = (FooterController)event.getController();
		Footer footer  = (Footer)controller.getTo();
		try {
			boolean dependences = false;
			String dependences_msg = "DEPENDENCES TO REMOVE. ";
			
			IManagerBean bean = BeanManager.getManagerBean(Section.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SECTION_FOOTER_ID),footer.getId());
			List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
			for (int i = 0; i < list.size(); i++) {
				dependences = true;
				Section section = (Section)list.get(i);
				int id = section.getId();
				String name = section.getAlias();
				dependences_msg += "Section "+id+"-"+name+"; ";
			}
			
			if (dependences){
				throw new ControllerListenerException(dependences_msg);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

}
