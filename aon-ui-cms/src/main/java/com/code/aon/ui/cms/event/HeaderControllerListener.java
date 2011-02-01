package com.code.aon.ui.cms.event;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.cms.Header;
import com.code.aon.cms.Section;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.controller.HeaderController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class HeaderControllerListener extends ControllerAdapter {

	private final static Logger LOGGER = LoggerFactory.getLogger(HeaderControllerListener.class);
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try {
			Criteria criteria = event.getController().getCriteria();
			IManagerBean bean = BeanManager.getManagerBean(Header.class);
			criteria.addOrder(bean.getFieldName(ICMSAlias.HEADER_DEFAULT_),false);
			criteria.addOrder(bean.getFieldName(ICMSAlias.HEADER_ALIAS));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		try {
			Header header = (Header)event.getController().getTo();
			IManagerBean bean = BeanManager.getManagerBean(Header.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.HEADER_DEFAULT_), true);
			List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
			if (list.size() == 0) {
				header.setDefault_(true);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		HeaderController controller = (HeaderController)event.getController();
		Header header = (Header)controller.getTo();
		try {
			boolean dependences = false;
			String dependences_msg = "DEPENDENCES TO REMOVE. ";
			
			IManagerBean bean = BeanManager.getManagerBean(Section.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SECTION_HEADER_ID),header.getId());
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
