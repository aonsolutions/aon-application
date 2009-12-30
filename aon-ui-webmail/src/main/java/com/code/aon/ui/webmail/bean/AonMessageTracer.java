package com.code.aon.ui.webmail.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;

import com.sun.mail.util.BASE64DecoderStream;

/**
 * Used to store message information.
 */
public class AonMessageTracer implements IMimeType {

	private static final Logger LOGGER = Logger.getLogger(AonMessageTracer.class.getName());

	private Message message;

	private ArrayList<BodyPart> relateds;
	
	private String traceText(Object mimepart) throws MessagingException, IOException {
		String value = AonMessageUtils.parse_cid((String) mimepart);
		value = AonMessageUtils.parse_tags(value);
		value = AonMessageUtils.parse_cr(value);
		return value;
	}

	private String traceHTML(Object mimepart) throws MessagingException, IOException {
		return AonMessageUtils.parse_cid((String) mimepart);
	}

	private String trace_alternative(Object mimepart)
			throws MessagingException, IOException {
		String data = "";
		if (mimepart instanceof Multipart) {
			Multipart multipart = (Multipart) mimepart;
			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				if (mimepart instanceof Multipart) {
					data = traceAll((Multipart) mimepart).toString();
				} else if (mimepart instanceof String) {
					data = (String) bodyPart.getContent();
					if (bodyPart.isMimeType(TEXT_PLAIN)) {
						data = AonMessageUtils.parse_tags(data);
						data = AonMessageUtils.parse_cr(data);
					}
					data = AonMessageUtils.parse_cid(data);
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
						data = AonMessageUtils.parse_cid(data);
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
			data = AonMessageUtils.parse_cid((String) mimepart);
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
						data = AonMessageUtils.parse_cid(data);
					}
				} else if (bodyPart.isMimeType(TEXT_HTML) || bodyPart.isMimeType(TEXT_PLAIN)) {
					String tmpdata = (String) bodyPart.getContent();
					tmpdata = AonMessageUtils.parse_cid(tmpdata);
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
			data = AonMessageUtils.parse_cid((String) mimepart);
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
				tmpdata = AonMessageUtils.parse_cid(tmpdata);
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
			value = AonMessageUtils.parse_cid((String) mimepart);
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
		return AonMessage.getMessageEnvelope(message, content, null);
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
		relateds = new ArrayList<BodyPart>();
		String data = traceContent(message);
		parseRelateds(data);
		return AonMessageUtils.extractInnerHTML(data);
	}

	/**
	 * Returns the javax.mail.Message object.
	 */
	public Message getMessage() {
		return message;
	}

	/**
	 * Method for mapping a message to this MessageInfo class.
	 */
	public void setMessage(Message message) {
		this.message = message;
	}

	/**
	 * @return the relateds
	 */
	public ArrayList<BodyPart> getRelateds() {
		return relateds;
	}

	private void parseRelateds(String msgText) throws IOException, MessagingException {
		List<String> cids = AonMessageUtils.getAllCid(msgText);
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

}
