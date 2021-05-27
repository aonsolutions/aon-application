package net.aonsolutions.aon.tbai.exceptions.http;

import net.aonsolutions.aon.tbai.exceptions.TbaiException;

public class StatusCodeException extends TbaiException{
	
	private static final long serialVersionUID = 7991944103487902781L;
	private int code;
	
	public StatusCodeException(int code,String message) {
		super(message);
		this.code = code;		
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
	
	
	
}
