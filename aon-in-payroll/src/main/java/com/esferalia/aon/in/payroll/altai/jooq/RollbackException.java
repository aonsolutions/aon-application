package com.esferalia.aon.in.payroll.altai.jooq;

public class RollbackException extends RuntimeException {
	public RollbackException() {
		super("ROLLBACK");
	}
}
