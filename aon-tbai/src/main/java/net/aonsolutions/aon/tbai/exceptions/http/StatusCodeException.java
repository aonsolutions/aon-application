package net.aonsolutions.aon.tbai.exceptions.http;

import net.aonsolutions.aon.tbai.exceptions.TbaiException;

public class StatusCodeException extends TbaiException{

	private int code;
	public StatusCodeException(int code,String message) {
		super(message);
		this.code = code;		
	}
}
