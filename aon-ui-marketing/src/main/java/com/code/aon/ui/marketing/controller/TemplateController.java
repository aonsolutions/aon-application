package com.code.aon.ui.marketing.controller;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MESSAGE;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.Template;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * Controller used in the offer maintenance.
 */
public class TemplateController extends BasicController {
	
	public void onSendEmail( ActionEvent event ) throws ManagerBeanException {
		MessageController controller = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		controller.onNewMessage(event);
		controller.setShowNewMessageWindow(true);
		initController(controller, (Template) getTo());
	}

	public static void initController( MessageController controller, Template template ) {
		initController(controller, template, null);
	}
	
	public static void initController( MessageController controller, Template template, String body ) {
		controller.setAppendSignature(false);
		if ( (template != null) && ! StringUtils.isEmpty(template.getSubject()) ) {
			controller.setSubject(template.getSubject());	
		}
		controller.updateMessageBody(getMessageBody(template, body));
	}
	
	public static String getMessageBody( Template template, String body ) {
		StringBuffer sb = new StringBuffer();
		addHeader( template, sb );
		sb.append( StringUtils.defaultString(body, "<p><br/></p>") );
		addFooter( template, sb );
		return sb.toString();				
	}

	public static void addHeader( Template template, StringBuffer sb ) {
		boolean nullTemplate = (template == null) || (template.getId() == null);
		sb.append("<table style=\"width:100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0");
		if (! (nullTemplate || StringUtils.isEmpty(template.getBackgroundColor())) ) {
			sb.append("\" bgcolor=\"");
			sb.append(template.getBackgroundColor());
			
		}
		sb.append("\"><tbody><tr><td align=\"center\">");		
		if (! nullTemplate ) {
			RegistryAttachment ht = template.getHeaderTemplate();
			if ( (ht != null) && (ht.getId() != null) ) {
				if (! ArrayUtils.isEmpty(ht.getData()) ) {
					sb.append( new String(ht.getData()) );
				}		
			}
		}		
	}

	public static void addFooter( Template template, StringBuffer sb ) {
		if ( (template != null) && (template.getId() != null) ) {
			RegistryAttachment ft = template.getFooterTemplate();
			if ( (ft != null) && (ft.getId() != null) ) {
				if (! ArrayUtils.isEmpty(ft.getData()) ) {
					sb.append( new String(ft.getData()) );
				}		
			}
		}
		sb.append("</td></tr></tbody></table>");		
	}
	
	private Criteria getTemplateCriteria() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getFieldName(IEntityAlias.TEMPLATE_ACTIVE), Boolean.TRUE);
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, getFieldName(IEntityAlias.TEMPLATE_SCOPE_ID));
		criteria.addOrder(getFieldName(IEntityAlias.TEMPLATE_NAME));
		return criteria;
	}
	
	public int getTemplateCount() throws ManagerBeanException {
		return getManagerBean().getCount(getTemplateCriteria());
	}

	public void onTemplateChanged(ValueChangeEvent event) throws ManagerBeanException {
		if(event.getNewValue() != null) {
			Integer id = (Integer) event.getNewValue();
			Template template = (Template) getManagerBean().get(id);
			MessageController controller = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
			controller.updateMessageBody(getMessageBody(template, null));
		}
	}	

	public List<SelectItem> getTemplates() throws ManagerBeanException {
		List<SelectItem> templates = new LinkedList<SelectItem>();
		Iterator<?> iter = getManagerBean().getList(getTemplateCriteria()).iterator();
		while(iter.hasNext()){
			Template template = (Template) iter.next();
			SelectItem item = new SelectItem(template.getId(), template.getName());
			templates.add(item);
		}
		return templates;
	}
	
}