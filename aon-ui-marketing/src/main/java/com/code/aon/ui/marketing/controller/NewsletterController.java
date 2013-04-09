package com.code.aon.ui.marketing.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MESSAGE;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.Newsletter;
import com.code.aon.marketing.NewsletterDetail;
import com.code.aon.marketing.Template;
import com.code.aon.marketing.enumeration.NewsletterLayout;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * Controller used in the offer maintenance.
 */
public class NewsletterController extends BasicController {

	private static final Logger LOGGER = LoggerFactory.getLogger(NewsletterController.class.getName());
	
	public void onSendEmail( ActionEvent event ) throws ManagerBeanException {
		MessageController controller = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		controller.onNewMessage(event);
		controller.setShowNewMessageWindow(true);
		initController(controller, (Newsletter) getTo());
	}

	public static void initController( MessageController controller, Newsletter newsletter ) {
		controller.setSkipSignature(true);
		controller.setSubject(newsletter.getName());		
		StringBuffer sb = new StringBuffer();
		addHeader( newsletter, sb );
		addNews( newsletter, sb );
		addFooter( newsletter, sb );
		controller.updateMessageBody(sb.toString());
	}
	
	private static void addHeader( Newsletter newsletter, StringBuffer sb ) {
		sb.append("<table style=\"width:100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0");
		if (! StringUtils.isEmpty(newsletter.getBackgroundColor()) ) {
			sb.append("\" bgcolor=\"");
			sb.append(newsletter.getBackgroundColor());
			
		}
		sb.append("\"><tbody><tr><td align=\"center\">");		
		Template t1 = newsletter.getHeaderTemplate();
		if ( (t1 != null) && (t1.getId() != null) ) {
			if (! StringUtils.isEmpty(t1.getData()) ) {
				sb.append( t1.getData() );
			}		
		}		
	}

	private static void addFooter( Newsletter newsletter, StringBuffer sb ) {
		Template t2 = newsletter.getFooterTemplate();
		if ( (t2 != null) && (t2.getId() != null) ) {
			if (! StringUtils.isEmpty(t2.getData()) ) {
				sb.append( t2.getData() );
			}		
		}
		sb.append("</td></tr></tbody></table>");		
	}
	
	private static void addNews( Newsletter newsletter, StringBuffer sb ) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(NewsletterDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.NEWSLETTER_DETAIL_NEWSLETTER_ID), newsletter.getId());
			criteria.addOrder(bean.getFieldName(IEntityAlias.NEWSLETTER_DETAIL_POSITION));
			NewsletterLayout layout = newsletter.getLayout();
			boolean alternate = (layout == NewsletterLayout.ALTERNATE_ALIGNED_IMAGE);
			for( ITransferObject to : bean.getList(criteria) ) {
				if ( alternate ) {
					if ( layout == NewsletterLayout.LEFT_ALIGNED_IMAGE) {
						layout = NewsletterLayout.RIGHT_ALIGNED_IMAGE;
					} else {
						layout = NewsletterLayout.LEFT_ALIGNED_IMAGE;
					}
				}
				addNewsletterDetail( (NewsletterDetail) to, layout, sb );
			}
		} catch ( ManagerBeanException e ) {
			LOGGER.error( e.getMessage(), e);
		}
	}
	
	private static void addTitle( NewsletterDetail nd, StringBuffer sb ) {
		sb.append("<p align=\"left\" style=\"font-size:18px;font-weight:bold");
		if (! StringUtils.isEmpty(nd.getNewsletter().getTitleColor()) ) {
			sb.append(";color:");
			sb.append(nd.getNewsletter().getTitleColor());
		}
		sb.append(";\">");
		sb.append(nd.getNews().getTitle());
		sb.append("</p>");		
	}

	private static void addImage( String url, StringBuffer sb ) {
		sb.append("<img border=\"0\" styles=\"display:block;\" src=\"");
		sb.append( url );
		sb.append("\">");		
	}

	private static void addContent( NewsletterDetail nd, StringBuffer sb ) {
		sb.append("<div align=\"left\">");
		sb.append(nd.getNews().getContent());
		sb.append("</div>");		
	}
	
	private static String getImageURL( NewsletterDetail nd ) {
		RegistryAttachment ra = nd.getNews().getRegistryAttachment();
		if ( (ra != null) && (ra.getId() != null) ) {
			DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
			try {
				return ds.getDomainURL() + ra.getDownloadURL();
			} catch (ManagerBeanException e) {
				LOGGER.error( e.getMessage(), e );
			}
		}
		return null;
	}

	private static void addFullWidthImage( NewsletterDetail nd, StringBuffer sb ) {
		sb.append("<tr><td>");
		addTitle(nd, sb);
		sb.append("</td></tr>");
		String url = getImageURL(nd);
		if ( url != null ) {
			sb.append("<tr><td>");
			addImage(url, sb);
			sb.append("</td></tr>");					
		}
		sb.append("<tr><td>");
		addContent(nd, sb);
		sb.append("</td></tr>");
	}

	private static void addLeftAlignedImage( NewsletterDetail nd, StringBuffer sb ) {
		sb.append("<tr><td>");
		addTitle(nd, sb);
		String url = getImageURL(nd);
		if ( url != null ) {
			sb.append("<table  align=\"left\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody>");
			sb.append("<tr><td>");
			addImage(url, sb);
			sb.append("</td><td width=\"15\"/></tr>");
			sb.append("<tr><td width=\"15\" height=\"10\"></tr>");
			sb.append("</tbody></table>");
		}
		addContent(nd, sb);
		sb.append("</td></tr>");
	}

	private static void addRightAlignedImage( NewsletterDetail nd, StringBuffer sb ) {
		sb.append("<tr><td>");
		addTitle(nd, sb);
		String url = getImageURL(nd);
		if ( url != null ) {
			sb.append("<table  align=\"right\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody>");
			sb.append("<tr><td width=\"15\"/><td>");
			addImage(url, sb);
			sb.append("</td></tr>");
			sb.append("<tr><td width=\"15\" height=\"10\"></tr>");
			sb.append("</tbody></table>");
		}
		addContent(nd, sb);
		sb.append("</td></tr>");
	}
	
	private static void addNewsletterDetail( NewsletterDetail nd, NewsletterLayout layout, StringBuffer sb ) {
		sb.append("<table style=\"width: 667px;\"  cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody>");
		switch ( layout ) {
			case FULL_WIDTH_IMAGE:
				addFullWidthImage(nd, sb);
				break;
			case LEFT_ALIGNED_IMAGE:
				addLeftAlignedImage(nd, sb);
				break;
			case RIGHT_ALIGNED_IMAGE:
				addRightAlignedImage(nd, sb);
				break;
		}
		sb.append("<tr><td height=\"10\"></td></tr>");
		sb.append("</tbody></table>");
	}
	
}