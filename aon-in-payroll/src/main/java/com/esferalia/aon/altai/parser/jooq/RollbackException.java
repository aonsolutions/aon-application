package com.esferalia.aon.altai.parser.jooq;

public class RollbackException extends RuntimeException {
	public RollbackException() {
		super("ROLLBACK");
	}
}
