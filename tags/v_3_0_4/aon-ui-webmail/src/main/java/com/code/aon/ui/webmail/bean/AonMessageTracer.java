package com.code.aon.ui.webmail.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;

/**
 * Used to store message information.
 */
public class AonMessageTracer {

	private Message message;

	private void trace_Text(Object mimepart) throws MessagingException,
			IOException {
		String cadena = AonMessageUtils.parse_cid((String) mimepart);
		cadena = AonMessageUtils.parse_tags(cadena);
		cadena = AonMessageUtils.parse_cr(cadena);
		SelectedPart += cadena;
	}

	private void trace_HTML(Object mimepart) throws MessagingException,
			IOException {
		String cadena = AonMessageUtils.parse_cid((String) mimepart);
		SelectedPart += cadena;
	}

	private String trace_alternative(Object mimepart)
			throws MessagingException, IOException {
		String data = "";
		if (mimepart instanceof Multipart) {
			Multipart multipart = (Multipart) mimepart;
			for (int i = 0; i < multipart.getCount(); i++) {
				if (mimepart instanceof Multipart) {
					data = traceAll(mimepart);
				} else if (mimepart instanceof String) {
					data = (String) multipart.getBodyPart(i).getContent();
					if (multipart.getBodyPart(i).isMimeType("text/plain")) {
						data = AonMessageUtils.parse_tags(data);
						data = AonMessageUtils.parse_cr(data);
					}
					data = AonMessageUtils.parse_cid(data);
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
				if (multipart.getBodyPart(i).isMimeType("multipart/alternative")) {
					Multipart multipart2 = (Multipart) multipart.getBodyPart(i)
							.getContent();
					if (multipart2.getBodyPart(i).isMimeType("text/*")) {
						int numPartsAltRel = multipart2.getCount();
						for (int j = 0; j < numPartsAltRel; ++j) {
							data = (String) multipart2.getBodyPart(j).getContent();
							if (multipart2.getBodyPart(i).isMimeType("text/plain")) {
							}
						}
						data = AonMessageUtils.parse_cid(data);
					} else {
						if (multipart.getBodyPart(i).isMimeType("application/*")) {
							com.sun.mail.util.BASE64DecoderStream b64ds = (com.sun.mail.util.BASE64DecoderStream) multipart
									.getBodyPart(i).getContent();
						}
					}
				} else {
					if (multipart.getBodyPart(i).isMimeType("text/html")
							|| multipart.getBodyPart(i).isMimeType("text/plain")) {
						String tmpdata = (String) multipart.getBodyPart(i)
								.getContent();
						if (multipart.getBodyPart(i).isMimeType("text/plain")) {
							tmpdata = AonMessageUtils.parse_tags(tmpdata);
							tmpdata = AonMessageUtils.parse_cr(tmpdata);
						}
						if (multipart.getBodyPart(i).getFileName() == null) {
							data = tmpdata;
						} else {
						}
					}
					return data;
				}
			}			
		} else if (mimepart instanceof String) {
			data = AonMessageUtils.parse_cid((String) mimepart);
			data = AonMessageUtils.parse_tags(data);
			data = AonMessageUtils.parse_cr(data);
		}
		return data;
	}

	private String trace_related(Object mimepart) throws MessagingException,
			IOException {
		String data = "";
		if (mimepart instanceof Multipart) {
			Multipart multipart = (Multipart) mimepart;
			for (int i = 0; i < multipart.getCount(); i++) {
				if (multipart.getBodyPart(i).isMimeType("multipart/alternative")) {
					Multipart multipart2 = (Multipart) multipart.getBodyPart(i)
							.getContent();
					if (multipart2.getBodyPart(i).isMimeType("text/*")) {
						int numPartsAltRel = multipart2.getCount();
						for (int j = 0; j < numPartsAltRel; ++j) {
							data = (String) multipart2.getBodyPart(j).getContent();
							if (multipart2.getBodyPart(i).isMimeType("text/plain")) {
							}
						}
						data = AonMessageUtils.parse_cid(data);
					}
				} else {
					if (multipart.getBodyPart(i).isMimeType("text/html")
							|| multipart.getBodyPart(i).isMimeType("text/plain")) {
						String tmpdata = (String) multipart.getBodyPart(i)
								.getContent();
						tmpdata = AonMessageUtils.parse_cid(tmpdata);
						if (multipart.getBodyPart(i).isMimeType("text/plain")) {
							tmpdata = AonMessageUtils.parse_tags(tmpdata);
							tmpdata = AonMessageUtils.parse_cr(tmpdata);
						}
						if (multipart.getBodyPart(i).getFileName() == null) {
							data = tmpdata;
						} else {
						}
					}
					return data;
				}
			}			
		} else if (mimepart instanceof String) {
			data = AonMessageUtils.parse_cid((String) mimepart);
			data = AonMessageUtils.parse_tags(data);
			data = AonMessageUtils.parse_cr(data);
		}
		return data;
	}

	private String traceAll(Object mimepart) throws MessagingException, IOException {
		String data = "";
		if (mimepart instanceof Multipart) {
			Multipart multipart = (Multipart) mimepart;
			for (int i = 0; i < multipart.getCount(); i++) {
				if (multipart.getBodyPart(i).isMimeType("multipart/mixed")) {
					data += trace_mixed(multipart.getBodyPart(i).getContent());
				} else {
					if (multipart.getBodyPart(i).isMimeType(
							"multipart/alternative")) {
						data += trace_alternative(multipart.getBodyPart(i)
								.getContent());
					} else {
						if (multipart.getBodyPart(i).isMimeType(
								"multipart/related")) {
							data += trace_related(multipart.getBodyPart(i)
									.getContent());
						} else {
							if (multipart.getBodyPart(i)
									.isMimeType("text/html")
									|| multipart.getBodyPart(i).isMimeType(
											"text/plain")) {
								String tmpdata = (String) multipart
										.getBodyPart(i).getContent();
								tmpdata = AonMessageUtils.parse_cid(tmpdata);
								if (multipart.getBodyPart(i).isMimeType(
										"text/plain")) {
									tmpdata = AonMessageUtils.parse_tags(tmpdata);
									tmpdata = AonMessageUtils.parse_cr(tmpdata);
								}
								if (multipart.getBodyPart(i).getFileName() == null) {
									data = tmpdata;
								} else {
								}
							}
						}
					}
				}
			}
		}
		return data;
	}
	
	private void trace(Object mimepart) throws MessagingException, IOException {
		if (mimepart instanceof Multipart) {
			Multipart multipart = (Multipart) mimepart;
			String data = "";
			data = traceAll(multipart);
			SelectedPart += data;
		} else if (mimepart instanceof String) {
			String cadena = AonMessageUtils.parse_cid((String) mimepart);
			cadena = AonMessageUtils.parse_tags(cadena);
			cadena = AonMessageUtils.parse_cr(cadena);
			SelectedPart += cadena;
		} else {
		}
	}

	public String getBodyHTML() throws MessagingException, java.io.IOException {
		SelectedPart = "";
		relateds = new ArrayList<BodyPart>();
		Object content = message.getContent();
		if (message.isMimeType("text/html")) {
			if (message.getFileName() == null) {
				trace_HTML(content);
			}
		} else if (message.isMimeType("text/plain")) {
			trace_Text(content);
		} else {
			trace(content);
		}
		parseRelateds(SelectedPart);
		SelectedPart = AonMessageUtils.extractBodyInnerHTML(new StringBuffer(SelectedPart)).toString();
		return SelectedPart;
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

	private String SelectedPart;

	private ArrayList<BodyPart> relateds;
	
	/**
	 * @return the relateds
	 */
	public ArrayList<BodyPart> getRelateds() {
		return relateds;
	}
	
	private void parseRelateds(String msgText) throws IOException, MessagingException{
		List cids = AonMessageUtils.getAllCid(msgText);
		Object obj = message.getContent();
		if (obj instanceof MimeMultipart){
			MimeMultipart parts = (MimeMultipart) obj;
			Iterator iter = cids.iterator();
			while (iter.hasNext()){
				String cid = "<"+iter.next()+">";
				BodyPart part = parts.getBodyPart(cid);
				relateds.add(part);
			}
		}
	}

	public void writeTo(OutputStream os) {
		try {
			message.writeTo(os);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
