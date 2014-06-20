package com.code.aon.ui.marketing.controller;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MESSAGE;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.CSSUnit;
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
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Double width;
	
	private CSSUnit cssUnit;
	
	public void onSendEmail( ActionEvent event ) {
		MessageController controller = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		controller.onPrepareEmailWindow(event);
		if ( controller.isShowNewMessageWindow() ) {	
			controller.onNewMessage(event);
			controller.setShowTemplates(false);
			initController(controller, (Template) getTo());
		}
	}

	public static void initController( MessageController controller, Template template ) {
		initController(controller, template, null);
	}
	
	public static void initController( MessageController controller, Template template, String body ) {
		controller.setAppendSignature(false);
		if ( template!=null && !StringUtils.isEmpty(template.getSubject()) ) {
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
		boolean nullTemplate = template==null || template.getId()==null;
		sb.append("<div style=\"text-align: center;");
		if (! (nullTemplate || StringUtils.isEmpty(template.getBackgroundColor())) ) {
			sb.append("background-color:");
			sb.append(template.getBackgroundColor());	
		}
		sb.append("\">");
		sb.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" style=\"margin: 0 auto");
		if ( !nullTemplate && !StringUtils.isEmpty(template.getWidth()) ) {
			sb.append(";width: ").append(template.getWidth());			
		}		
		sb.append(";\">");
		sb.append("<tbody><tr><td align=\"center\">");		
		if (! nullTemplate ) {
			RegistryAttachment ht = template.getHeaderTemplate();
			if ( ht != null && ht.getId()!=null && (ht.getSize() > 0) ) {
				sb.append( new String(ht.getData()) );	
			}
		}		
	}

	public static void addFooter( Template template, StringBuffer sb ) {
		if ( template!=null && template.getId()!=null ) {
			RegistryAttachment ft = template.getFooterTemplate();
			if ( ft!=null && ft.getId()!=null && (ft.getSize() > 0) ) {
				sb.append( new String(ft.getData()) );	
			}
		}
		sb.append("</td></tr></tbody></table></div>");		
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

	public void onClearBackgroundColor( ActionEvent event ) {
		Template template = (Template) getTo();
		template.setBackgroundColor(null);
	}

	public void onClearTitleColor( ActionEvent event ) {
		Template template = (Template) getTo();
		template.setTitleColor(null);
	}

	public Double getWidth() {
		return width;
	}

	public void setWidth(Double width) {
		this.width = width;
	}

	public CSSUnit getCssUnit() {
		return cssUnit;
	}

	public void setCssUnit(CSSUnit cssUnit) {
		this.cssUnit = cssUnit;
	}
	
}