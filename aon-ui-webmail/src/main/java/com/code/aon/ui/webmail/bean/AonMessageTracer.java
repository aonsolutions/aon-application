package com.code.aon.ui.webmail.bean;

import static com.code.aon.ui.common.ICommonMessages.CC_MESSAGE;
import static com.code.aon.ui.common.ICommonMessages.DATE;
import static com.code.aon.ui.common.ICommonMessages.FROM_MESSAGE;
import static com.code.aon.ui.common.ICommonMessages.SUBJECT;
import static com.code.aon.ui.common.ICommonMessages.TO_MESSAGE;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.WebmailException;
import com.code.aon.webmail.bean.AonMessage;
import com.code.aon.webmail.bean.AonMessageUtils;
import com.code.aon.webmail.bean.IMimeType;

/**
 * Used to store message information.
 */
public class AonMessageTracer implements IMimeType {

	private final static Logger LOGGER = LoggerFactory.getLogger(BeanManager.class);

	private static final Pattern CID_PATTERN = Pattern.compile(
			"=\\s*[\"\']?(cid:[^ >\"\']+)", Pattern.CASE_INSENSITIVE);
	
	private Message message;
	
	public AonMessageTracer(Message message) {
		this.message = message;
	}

	private boolean isContentText(Part part) throws MessagingException {
		return part.isMimeType(TEXT_ANY) && (part.getFileName() == null);
	}
	
	private String traceText(Part part) throws MessagingException, IOException {
		return traceText(part, part.getContent());
	}
	
	private String traceText(Part part, Object content) throws MessagingException {
		if (content instanceof String) {
			String data = (String) content; 
			if (part.isMimeType(TEXT_PLAIN)) {
				data = AonMessageUtils.parse_tags(data);
				data = AonMessageUtils.parse_cr(data);
			} else if (part.isMimeType(TEXT_HTML)) {
				data = parseCids( data );
			}
			return data;
		} else {
			LOGGER.warn( "Text/* String content expected: {}", part );
		}
		return null;
	}

	private String trace_alternative(Object mimepart)
			throws MessagingException, IOException {
		String data = "";
		if (mimepart instanceof Multipart) {
			Multipart multipart = (Multipart) mimepart;
			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				if (bodyPart.isMimeType(MULTIPART_ANY)) {
					data = traceAll( (Multipart) bodyPart.getContent() );
				} else if ( isContentText(bodyPart) ) {
					data = traceText(bodyPart);
				} else {
					LOGGER.warn( "Multipart/alternative unexpected content: {}", bodyPart.getContentType() );					
				}
			}
		}
		return data;
	}

	private String trace_mixed(Part part) throws MessagingException,
			IOException {
		String data = "";
		Object content = part.getContent();
		if (content instanceof Multipart) {
			Multipart multipart = (Multipart) content;
			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				if (bodyPart.isMimeType(MULTIPART_ALTERNATIVE)) {
					Multipart multipart2 = (Multipart) bodyPart.getContent();
					if (multipart2.getBodyPart(i).isMimeType(TEXT_ANY)) {
						int numPartsAltRel = multipart2.getCount();
						for (int j = 0; j < numPartsAltRel; ++j) {
							data = (String) multipart2.getBodyPart(j).getContent();
						}
						data = parseCids(data);
					}
				} else if ( isContentText(bodyPart) ) {
					return traceText(bodyPart);
				} else {
					LOGGER.warn( "Multipart/mixed unexpected content: {}", bodyPart.getContentType() );					
				}
			}
		} else if ( isContentText(part) ) {
			return traceText(part, content);
		} else {
			LOGGER.error( "Multipart/mixed unexpected content: {}", part );
		}
		return data;
	}

	private String trace_related(Part part) throws MessagingException,
			IOException {
		String data = "";
		Object content = part.getContent();
		if (content instanceof Multipart) {
			Multipart multipart = (Multipart) content;
			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				if (bodyPart.isMimeType(MULTIPART_ALTERNATIVE)) {
					Multipart multipart2 = (Multipart) bodyPart.getContent();
					if (multipart2.getBodyPart(i).isMimeType(TEXT_ANY)) {
						int numPartsAltRel = multipart2.getCount();
						for (int j = 0; j < numPartsAltRel; ++j) {
							data = (String) multipart2.getBodyPart(j).getContent();
						}
						data = parseCids(data);
					}
				} else if ( isContentText(bodyPart) ) {
					return traceText(bodyPart);
				} else {
					LOGGER.warn( "Multipart/related unexpected content: {}", bodyPart.getContentType() );										
				}
			}
		} else if ( isContentText(part) ) {
			return traceText(part, content);
		} else {
			LOGGER.error( "Multipart/related unexpected content: {}", part );
		}
		return data;
	}

	private String traceAll(Multipart multipart) throws MessagingException, IOException {
		StringBuffer data = new StringBuffer();
		for (int i = 0; i < multipart.getCount(); i++) {
			BodyPart bodyPart = multipart.getBodyPart(i);
			if (bodyPart.isMimeType(MULTIPART_MIXED)) {
				data.append( trace_mixed(bodyPart) );
			} else if (bodyPart.isMimeType(MULTIPART_ALTERNATIVE)) {
				data.append( trace_alternative(bodyPart.getContent()) );
			} else if (bodyPart.isMimeType(MULTIPART_RELATED)) {
				data.append( trace_related(bodyPart) );
			} else if (bodyPart.isMimeType(MESSAGE_RFC822)) {
				data.append( traceMessage((Message) bodyPart.getContent()) );
			} else if ( isContentText(bodyPart) ) {
				data = new StringBuffer( traceText(bodyPart) );
			} else {
				LOGGER.warn( "Multipart/* unexpected content: {}", bodyPart.getContentType() );
			}
		}
		return data.toString();
	}

	private String traceMessage( Message message ) throws IOException, MessagingException {
		String content = AonMessageUtils.extractBodyInnerHTML(traceContent(message));
		return getMessageEnvelope(message, content, null);
	}
		
	private String traceContent( Message message ) throws IOException, MessagingException {
		String value = null;
		if (message.isMimeType(MULTIPART_ANY)) {
			value = traceAll( (Multipart) message.getContent() );
		} else if (message.isMimeType(TEXT_ANY)) {
			value = traceText( message );
		} else {
			LOGGER.error( "Unexpected content: {}", message.getContentType() );
		}		
		return value;
	}
	
	public String getBodyHTML() throws MessagingException, IOException {
		String data = traceContent(message);
		return AonMessageUtils.extractInnerHTML(data);
	}

    private String getURLPreffix() {
		FacesContext context = FacesContext.getCurrentInstance();
		String contextPath = context.getExternalContext().getRequestContextPath(); 
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		StringBuffer url = request.getRequestURL();
		int pos = url.indexOf( contextPath );
		String preffix = url.substring(0, pos + contextPath.length()+1 );
		return preffix;
	}
	
    private String parseCids(String content){
		Matcher tagMatcher = CID_PATTERN.matcher(content);
		if ( tagMatcher.find() ) {
			StringBuffer sb = new StringBuffer(content);
			String preffix = getURLPreffix();
			int offset = 0;
			do {
				String fullCid = tagMatcher.group(1);
				String cid = fullCid.substring(4);
				String newText = preffix + cid + ".cid";
				int start = tagMatcher.start(1) + offset;
				int end = tagMatcher.end(1) + offset;
				sb.replace( start, end, newText);
				offset += (newText.length() - fullCid.length());
			} while( tagMatcher.find() );
			return sb.toString();
		}
		return content;
    }
    
	public static String getMessageEnvelope( Message message, String content, String headerId ) {
		try {			
			return getMessageEnvelope(new AonMessage(message), content, headerId);
		} catch (WebmailException e) {
			LOGGER.error("Error getting message envelope", e);
		}	
		return null;
	}

	public static String getMessageEnvelope( AonMessage message, String content, String headerId ) throws WebmailException {
		StringBuffer sb = new StringBuffer();
		sb.append( "<br/>" );
		if ( headerId != null ) {
			sb.append( "<BLOCKQUOTE style='PADDING-RIGHT: 0px; PADDING-LEFT: 10px; MARGIN-LEFT: 5px; BORDER-LEFT: #000000 2px solid; MARGIN-RIGHT: 0px'>" );
		}
		sb.append("<font face='arial' size='2' >");
		if ( headerId != null ) {
			sb.append("----------").append( AonUtil.getMessage(headerId) ).append("----------");
		}
		sb.append( "<DIV style='BACKGROUND: #e4e4e4'>" );
		String from = message.getSender();
		sb.append( "<b>" ).append(AonUtil.getMessage(FROM_MESSAGE)).append(":</b> ").append(from).append( "</DIV>" );
		if ( headerId == null ) {
			String to = message.getRecipientsTo();
			sb.append( "<b>" ).append(AonUtil.getMessage(TO_MESSAGE)).append(":</b> ").append(to).append( "</DIV>" );
		}
		sb.append( "<b>" ).append(AonUtil.getMessage(DATE)).append(":</b> ").append( message.getSentDate() );
		String cc = message.getRecipientsCc();
		if (! StringUtils.isEmpty(cc) ) {
			sb.append( "<br/><b>" ).append(AonUtil.getMessage(CC_MESSAGE)).append(":</b> ").append( cc );
		}
		String subject = message.getDisplaySubject();
		sb.append( "<br/><b>" ).append(AonUtil.getMessage(SUBJECT)).append(":</b> ").append( subject );
   		sb.append( "</font><br/><br/>" );
   		sb.append( content );
		if ( headerId != null ) {
			sb.append( "</BLOCKQUOTE><br/>" );
		}
		return sb.toString();
	}
    
}
