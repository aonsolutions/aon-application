package com.code.aon.ui.marketing.controller;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MESSAGE;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.marketing.Newsletter;
import com.code.aon.marketing.NewsletterDetail;
import com.code.aon.marketing.enumeration.NewsType;
import com.code.aon.marketing.enumeration.NewsletterLayout;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * Controller used in the offer maintenance.
 */
public class NewsletterController extends BasicController {

	private static final Logger LOGGER = LoggerFactory.getLogger(NewsletterController.class.getName());

	private IControllerListener imageFilter;
	
	private IControllerListener newsFilter;
	
	private List<MimeType> getImageMimeTypes() {
		List<MimeType> list = new LinkedList<MimeType>();
		for( MimeType mimeType : MimeType.values() ) {
			if ( mimeType.getName().startsWith("image/") ) {
				list.add(mimeType);
			}
		}
		return list;
	}
	
	public IControllerListener getImageFilter() {
		if ( this.imageFilter == null ) {
			this.imageFilter = new ControllerAdapter() {
				@Override
				public void beforeModelInitialized(ControllerEvent event)
						throws ControllerListenerException {
					IController controller = event.getController();
					try {					
						String alias = controller.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_MIME_TYPE);
						controller.getCriteria().addInExpression(alias, getImageMimeTypes());
					} catch (ManagerBeanException e) {
						LOGGER.error("Error filtering documents", e);
					}
				}
			};
		}
		return this.imageFilter;
	}
	
	public void onSendEmail( ActionEvent event ) throws ManagerBeanException {
		MessageController controller = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		controller.onNewMessage(event);
		controller.setShowNewMessageWindow(true);
		initController(controller, (Newsletter) getTo());
	}

	public static void initController( MessageController controller, Newsletter newsletter ) {
		controller.setAppendSignature(false);
		controller.setShowTemplates(false);
		StringBuffer sb = new StringBuffer();
		addNews( newsletter, sb );
		String body = TemplateController.getMessageBody( newsletter.getTemplate(), sb.toString());
		controller.updateMessageBody(body);
		if (! StringUtils.isEmpty(newsletter.getSubject()) ) {
			controller.setSubject(newsletter.getSubject());	
		}		
	}
	
	private static void addNews( Newsletter newsletter, StringBuffer sb ) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(NewsletterDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.NEWSLETTER_DETAIL_NEWSLETTER_ID), newsletter.getId());
			criteria.addOrder(bean.getFieldName(IEntityAlias.NEWSLETTER_DETAIL_POSITION));
			NewsletterLayout layout = newsletter.getLayout();
			boolean alternate = (layout == NewsletterLayout.ALTERNATE_ALIGNED_IMAGE);
			if ( newsletter.isHighlightFirst() ) {
				layout = NewsletterLayout.FULL_WIDTH_IMAGE;
			}
			HtmlGenerator hg = new HtmlGenerator(sb);
			for( ITransferObject to : bean.getList(criteria) ) {
				NewsletterDetail nd = (NewsletterDetail) to;
				hg.addNews(nd.getNews(), newsletter.getTemplate(), layout);
				if ( alternate ) {
					if ( layout == NewsletterLayout.LEFT_ALIGNED_IMAGE) {
						layout = NewsletterLayout.RIGHT_ALIGNED_IMAGE;
					} else {
						layout = NewsletterLayout.LEFT_ALIGNED_IMAGE;
					}
				} else {
					layout = newsletter.getLayout();
				}
			}
		} catch ( ManagerBeanException e ) {
			LOGGER.error( e.getMessage(), e);
		}
	}

	public IControllerListener getNewsFilter() {
		if ( this.newsFilter == null ) {
			this.newsFilter = new ControllerAdapter() {
				@Override
				public void beforeModelInitialized(ControllerEvent event)
						throws ControllerListenerException {
					IController controller = event.getController();
					try {					
						Criteria criteria = controller.getCriteria();
						criteria.addEqualExpression(controller.getFieldName(IEntityAlias.NEWS_TYPE), NewsType.NEWS);
					} catch (ManagerBeanException e) {
						LOGGER.error("Error filtering news", e);
					}
				}
			};
		}
		return this.newsFilter;
	}
	
}