package com.code.aon.ui.webmail.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;

import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.bean.AonMessage;
import com.code.aon.webmail.bean.AonMessageUtils;
import com.code.aon.webmail.bean.IMimeType;
import com.sun.mail.util.BASE64DecoderStream;

/**
 * Used to store message information.
 */
public class AonMessageTracer implements IMimeType {

	private static final Logger LOGGER = Logger.getLogger(AonMessageTracer.class.getName());

	private static final Pattern CID_PATTERN = Pattern.compile(
			"(cid:[^\"\']+)(\"|\')", Pattern.CASE_INSENSITIVE);
	
	private Message message;
	
	private Set<String> cids;
	
	private ArrayList<BodyPart> relateds;
	
	public AonMessageTracer(Message message) {
		this.message = message;
	}

	private String traceText(Object mimepart) throws MessagingException, IOException {
		String value = parseCids((String) mimepart);
		value = AonMessageUtils.parse_tags(value);
		value = AonMessageUtils.parse_cr(value);
		return value;
	}

	private String traceHTML(Object mimepart) throws MessagingException, IOException {
		return parseCids((String) mimepart);
	}

	private String trace_alternative(Object mimepart)
			throws MessagingException, IOException {
		String data = "";
		if (mimepart instanceof Multipart) {
			Multipart multipart = (Multipart) mimepart;
			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				Object content = bodyPart.getContent();
				if (content instanceof Multipart) {
					data = traceAll((Multipart) content).toString();
				} else if (content instanceof String) {
					data = (String) content;
					if (bodyPart.isMimeType(TEXT_PLAIN)) {
						data = AonMessageUtils.parse_tags(data);
						data = AonMessageUtils.parse_cr(data);
					}
					data = parseCids(data);
				} else {
					LOGGER.severe( "Multipart/alternative unexpected content: " + bodyPart.getContentType() );					
				}
			}
		}
		return data;
	}

	private String trace_mixed(Object mimepart) throws MessagingException,
			IOException {
		String data = "";
		if (mimepart instanceof Multipart) {
			Multipart multipart = (Multipart) mimepart;
			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				if (bodyPart.isMimeType(MULTIPART_ALTERNATIVE)) {
					Multipart multipart2 = (Multipart) bodyPart.getContent();
					if (multipart2.getBodyPart(i).isMimeType(TEXT_ANY)) {
						int numPartsAltRel = multipart2.getCount();
						for (int j = 0; j < numPartsAltRel; ++j) {
							data = (String) multipart2.getBodyPart(j)
									.getContent();
							if (multipart2.getBodyPart(i)
									.isMimeType(TEXT_PLAIN)) {
							}
						}
						data = parseCids(data);
					} else {
						if (bodyPart.isMimeType(APPLICATION_ANY)) {
							BASE64DecoderStream b64ds = (BASE64DecoderStream) bodyPart
									.getContent();
						}
					}
				} else if (bodyPart.isMimeType(TEXT_HTML) || bodyPart.isMimeType(TEXT_PLAIN)) {
					String tmpdata = (String) bodyPart.getContent();
					if (bodyPart.isMimeType(TEXT_PLAIN)) {
						tmpdata = AonMessageUtils.parse_tags(tmpdata);
						tmpdata = AonMessageUtils.parse_cr(tmpdata);
					}
					if (bodyPart.getFileName() == null) {
						data = tmpdata;
					}
					return data;
				} else {
					LOGGER.severe( "Multipart/mixed unexpected content: " + bodyPart.getContentType() );					
				}
			}
		} else if (mimepart instanceof String) {
			data = parseCids((String) mimepart);
			data = AonMessageUtils.parse_tags(data);
			data = AonMessageUtils.parse_cr(data);
		} else {
			LOGGER.severe( "Multipart/mixed unexpected content: " + mimepart );
		}
		return data;
	}

	private String trace_related(Object mimepart) throws MessagingException,
			IOException {
		String data = "";
		if (mimepart instanceof Multipart) {
			Multipart multipart = (Multipart) mimepart;
			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				if (bodyPart.isMimeType(MULTIPART_ALTERNATIVE)) {
					Multipart multipart2 = (Multipart) bodyPart.getContent();
					if (multipart2.getBodyPart(i).isMimeType(TEXT_ANY)) {
						int numPartsAltRel = multipart2.getCount();
						for (int j = 0; j < numPartsAltRel; ++j) {
							data = (String) multipart2.getBodyPart(j)
									.getContent();
							if (multipart2.getBodyPart(i)
									.isMimeType(TEXT_PLAIN)) {
							}
						}
						data = parseCids(data);
					}
				} else if (bodyPart.isMimeType(TEXT_HTML) || bodyPart.isMimeType(TEXT_PLAIN)) {
					String tmpdata = (String) bodyPart.getContent();
					tmpdata = parseCids(tmpdata);
					if (bodyPart.isMimeType(TEXT_PLAIN)) {
						tmpdata = AonMessageUtils.parse_tags(tmpdata);
						tmpdata = AonMessageUtils.parse_cr(tmpdata);
					}
					if (bodyPart.getFileName() == null) {
						data = tmpdata;
					}
					return data;
				} else {
					LOGGER.severe( "Multipart/related unexpected content: " + bodyPart.getContentType() );										
				}
			}
		} else if (mimepart instanceof String) {
			data = parseCids((String) mimepart);
			data = AonMessageUtils.parse_tags(data);
			data = AonMessageUtils.parse_cr(data);
		} else {
			LOGGER.severe( "Multipart/related unexpected content: " + mimepart );
		}
		return data;
	}

	private String traceAll(Multipart multipart) throws MessagingException, IOException {
		StringBuffer data = new StringBuffer();
		for (int i = 0; i < multipart.getCount(); i++) {
			BodyPart bodyPart = multipart.getBodyPart(i);
			if (bodyPart.isMimeType(MULTIPART_MIXED)) {
				data.append( trace_mixed(bodyPart.getContent()) );
			} else if (bodyPart.isMimeType(MULTIPART_ALTERNATIVE)) {
				data.append( trace_alternative(bodyPart.getContent()) );
			} else if (bodyPart.isMimeType(MULTIPART_RELATED)) {
				data.append( trace_related(bodyPart.getContent()) );
			} else if (bodyPart.isMimeType(MESSAGE_RFC822)) {
				data.append( traceMessage((Message) bodyPart.getContent()) );
			} else if (bodyPart.isMimeType(TEXT_HTML) || bodyPart.isMimeType(TEXT_PLAIN)) {
				String tmpdata = (String) bodyPart.getContent();
				tmpdata = parseCids(tmpdata);
				if (bodyPart.isMimeType(TEXT_PLAIN)) {
					tmpdata = AonMessageUtils.parse_tags(tmpdata);
					tmpdata = AonMessageUtils.parse_cr(tmpdata);
				}
				if (bodyPart.getFileName() == null) {
					data = new StringBuffer( tmpdata );
				}
			} else {
				LOGGER.severe( "Multipart/* unexpected content: " + bodyPart.getContentType() );
			}
		}
		return data.toString();
	}

	private String trace(Object mimepart) throws MessagingException, IOException {
		String value = null;
		if (mimepart instanceof Multipart) {
			value = traceAll( (Multipart) mimepart);
		} else if (mimepart instanceof String) {
			value = parseCids((String) mimepart);
			value = AonMessageUtils.parse_tags(value);
			value = AonMessageUtils.parse_cr(value);
			return value;
		} else {
			LOGGER.severe( "Unexpected content: " + mimepart );
		}
		return value;
	}
	
	private String traceMessage( Message message ) throws IOException, MessagingException {
		String content = AonMessageUtils.extractBodyInnerHTML(traceContent(message));
		return AonMessage.getMessageEnvelope(message, content, null, AonUtil.getCurrentLocale());
	}

	private String traceContent( Message message ) throws IOException, MessagingException {
		String value = null;
		Object content = message.getContent();
		if (message.isMimeType(TEXT_HTML)) {
			if (message.getFileName() == null) {
				value = traceHTML(content);
			}
		} else if (message.isMimeType(TEXT_PLAIN)) {
			value = traceText(content);
		} else {
			value = trace(content);
		}		
		return value;
	}
	
	public String getBodyHTML() throws MessagingException, IOException {
		this.cids = new HashSet<String>();
		String data = traceContent(message);
		parseRelateds();
		return AonMessageUtils.extractInnerHTML(data);
	}

	/**
	 * @return the relateds
	 */
	public ArrayList<BodyPart> getRelateds() {
		if ( relateds == null ) {
			this.cids = new HashSet<String>();
			try {
				traceContent(message);
				parseRelateds();
			} catch (IOException e) {
				LOGGER.log( Level.SEVERE, "Error parsing cids & relateds", e );
			} catch (MessagingException e) {
				LOGGER.log( Level.SEVERE, "Error parsing cids & relateds", e );
			}
		}
		return relateds;
	}

	private void parseRelateds() throws IOException, MessagingException {
		this.relateds = new ArrayList<BodyPart>();
		Object obj = message.getContent();
		if (obj instanceof MimeMultipart) {
			MimeMultipart parts = (MimeMultipart) obj;
			Iterator<String> iter = cids.iterator();
			while (iter.hasNext()) {
				String cid = "<" + iter.next() + ">";
				BodyPart part = parts.getBodyPart(cid);
				relateds.add(part);
			}
		}
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
				this.cids.add( cid );
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
	
}
