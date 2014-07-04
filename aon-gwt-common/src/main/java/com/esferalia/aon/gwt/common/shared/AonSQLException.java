package com.esferalia.aon.gwt.common.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class AonSQLException extends Exception implements Serializable, IsSerializable {

	public AonSQLException() {
		super();
	}

	public AonSQLException(String msg) {
		super(msg);
	}

	public AonSQLException(Throwable e) {
		super(e);
	}

	public AonSQLException(String msg,Throwable e) {
		super(msg,e);
	}
}
