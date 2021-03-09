package net.aonsolutions.aon.tbai.exceptions.response;

import net.aonsolutions.aon.tbai.exceptions.TbaiException;

public class TbaiResponseException extends TbaiException{
	
	private Integer code;

	public TbaiResponseException(Integer code,String message) {
		super(message);
		this.code = code;
	}

	public Integer getCode() {return code;}
}
