package net.aonsolutions.aon.tbai.exceptions.xml;

import net.aonsolutions.aon.tbai.exceptions.TbaiException;

public class XMLSendingException extends TbaiException{
	private static final long serialVersionUID = 83773194357799371L;
	public XMLSendingException() {}
	public XMLSendingException(String message, Throwable cause) {super(message, cause);}
	public XMLSendingException(String message) {super(message);}
	public XMLSendingException(Throwable cause) {super(cause);}
}
