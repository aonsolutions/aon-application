package com.code.aon.ui.marketing.controller;

import static com.code.aon.marketing.enumeration.NewsletterLayout.FULL_WIDTH_IMAGE;
import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;
import static com.code.aon.ui.marketing.servlet.RSSServlet.SERVLET_PATH;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MESSAGE;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.faces.event.ActionEvent;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.IAttachment;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.marketing.News;
import com.code.aon.marketing.enumeration.NewsType;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;

/**
 * Controller used in the offer maintenance.
 */
public class NewsController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(NewsController.class.getName());

	public static final String NEWS_PREFFIX = "news-";
	
	public static final String NEWS_REGEX = NEWS_PREFFIX + "(\\d+)\\." + MimeType.MIME_HTML.getExtension();
	
	private NewsType type;
	
	public void onSendEmail(ActionEvent event) {
		MessageController controller = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		controller.onPrepareEmailWindow(event);
		if ( controller.isShowNewMessageWindow() ) {			
			controller.onNewMessage(event);
			controller.setShowTemplates(false);
			initController(controller, (News) getTo());
		}
	}

	public static void initController(MessageController controller, News news) {
		boolean hasTemplate = news.getTemplate()!=null && news.getTemplate().getId()!=null;
		HtmlGenerator hg = new HtmlGenerator();
		if (news.getType() == NewsType.MESSAGE) {
			if ( hasTemplate ) {
				hg.addNews(news);
				TemplateController.initController(controller, news.getTemplate(), hg.getString());
			} else {
				controller.updateMessageBody(news.getContent());
			}
			IAttachment attach = news.getRegistryAttachment();
			if ( attach!=null && attach.getId()!=null ) {
				AonFile aonFile = getAonFile(attach);
				if (aonFile != null) {
					controller.addAttachment(aonFile);
				}
			}
			controller.setSubject(news.getTitle());
		} else if (news.getType() == NewsType.NEWS) {
			hg.addNews(news);
			if ( hasTemplate ) {
				TemplateController.initController(controller, news.getTemplate(), hg.getString());	
			} else {
				controller.updateMessageBody(hg.getString());
			}
		}
	}

	private static AonFile getAonFile(IAttachment attach) {
		String fileName = attach.getDescription();
		String extension = (attach.getMimeType() != null) ? "." + attach.getMimeType().getExtension() : "";
		try {
			File file = File.createTempFile(fileName, extension);
			byte[] data = DownloadUtil.getData(attach);
			FileUtils.writeByteArrayToFile(file, data);
			AonFile aonFile = new AonFile();
			aonFile.setFile(file);
			aonFile.setFileName(fileName + extension);
			aonFile.setMimeType(attach.getMimeType());
			return aonFile;
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	public String getDownloadURL() {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		try {
			return getDownloadURL((News) getTo(), ds.getDomainURL());
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}		
		return null;
	}

	public static String getDownloadURL( News news, String urlPreffix ) {
		StringBuffer url = new StringBuffer();
		url.append( urlPreffix ).append( SERVLET_PATH ).append( NEWS_PREFFIX );
		url.append( news.getId().toString() );
		url.append('.').append( MimeType.MIME_HTML.getExtension() );
		return url.toString();
	}
	
	public static void writeHtml( News news, String urlPreffix, PrintWriter writer ) {
		writer.println("<html>");
		writer.println("<head>");
		writer.println("<title>" + news.getTitle() + "</title>");
		writer.println("</head>");
		writer.println("<body>");
		HtmlGenerator hg = new HtmlGenerator(urlPreffix);
		hg.addNews(news.getTemplate(), news, FULL_WIDTH_IMAGE, false);
		String content = TemplateController.getMessageBody(news.getTemplate(), hg.getString());
		writer.println(content);
		writer.println("</body>");
		writer.println("</html>");
	}
	
	@Override
	public void onReset(ActionEvent event) {
		super.onReset(event);
		News news = (News) getTo();
		news.setType(this.type);
	}

	public void onSetTypeMessage( ActionEvent event ) {
		setType(NewsType.MESSAGE);
	}

	public void onSetTypeNews( ActionEvent event ) {
		setType(NewsType.NEWS);
	}
	
	public NewsType getType() {
		return type;
	}

	public void setType(NewsType type) {
		this.type = type;
	}
	
	public String getLabel() {
		String key = type == NewsType.MESSAGE ? ICommonMessages.MESSAGES : ICommonMessages.MARKETING_NEWSS;
		return AonUtil.getMessage(key);
	}
	
	public void onViewChannel(ActionEvent event) {
		ChannelController channelController = (ChannelController)FormUtil.getController("channel");
		channelController.onSearch(null);
		channelController.setExternalBackAction(getBeanName() + "_form");	
	}
	
}