package com.esferalia.aon.occam.impl.jooq.console;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;

import com.esferalia.aon.occam.api.model.Occam;


public class ConsoleParams {

	private ConsoleConnectionParams fromConnection;
	private ConsoleConnectionParams toConnection;
	private ConsoleIDsTableInfo idsTableInfo;
	
	private boolean validate;
	private boolean mustFlatten;
	
	private PrintStream	printer;
	private Map<String,ScriptTable> script;
	private List<String> errors;
	
	private int totalCount;
	private int totalProgress;

	private int partialCount;
	private int partialProgress;

	public ConsoleConnectionParams getFromConnection() {
		return fromConnection;
	}
	public ConsoleParams setFromConnection(ConsoleConnectionParams fromConnection) {
		this.fromConnection = fromConnection;
		return this;
	}
	
	public ConsoleConnectionParams getToConnection() {
		return toConnection;
	}
	public ConsoleParams setToConnection(ConsoleConnectionParams toConnection) {
		this.toConnection = toConnection;
		return this;
	}
	public ConsoleConnectionParams ensureToConnection() {
		return getToConnection() != null?getToConnection():getFromConnection();	
	}
	
	public Occam getOccam(ConsoleConnectionParams conn) {
		return new Occam()
			.setDomainName( conn.getFullDomain().getName())
			.setDomain( conn.getFullDomain().getId());
	}
	
	public Occam getFromOccam() {
		return getOccam( getFromConnection() );
	}
	
	public Occam getToOccam() {
		return getOccam( ensureToConnection() );
	}
	
	public ConsoleIDsTableInfo getIdsTableInfo() {
		return idsTableInfo;
	}
	public ConsoleParams setIdsTableInfo(ConsoleIDsTableInfo idsTableInfo) {
		this.idsTableInfo = idsTableInfo;
		return this;
	}

	public boolean isValidate() {
		return validate;
	}
	public ConsoleParams setValidate(boolean validate) {
		this.validate = validate;
		return this;
	}
	
	public boolean mustFlatten() {
		return mustFlatten;
	}
	public ConsoleParams setMustFlatten(boolean mustFlatten) {
		this.mustFlatten = mustFlatten;
		return this;
	}

	public Map<String, ScriptTable> getScript() {
		return script;
	}
	public ConsoleParams setScript(Map<String, ScriptTable> script) {
		this.script = script;
		return this;
	}
	
	public PrintStream getPrinter() {
		return printer;
	}
	public ConsoleParams setPrinter(PrintStream printer) {
		this.printer = printer;
		return this;
	}

	public List<String> getErrors() {
		if (errors == null) {
			setErrors(new LinkedList<>());
		}
		return errors;
	}

	public ConsoleParams setErrors(List<String> errors) {
		this.errors = errors;
		return this;
	}
	public ConsoleParams addError(String error) {
		getErrors().add(error);
		return this;
	}
	public boolean hasErrors() {
		return !getErrors().isEmpty(); 
	}

	public int getTotalCount() {
		return totalCount;
	}

	public ConsoleParams setTotalCount(int totalCount) {
		this.totalCount = totalCount;
		return this;
	}

	public int getTotalProgress() {
		return totalProgress;
	}

	public ConsoleParams setTotalProgress(int totalProgress) {
		this.totalProgress = totalProgress;
		return this;
	}

	public int getPartialCount() {
		return partialCount;
	}

	public ConsoleParams setPartialCount(int partialCount) {
		this.partialCount = partialCount;
		return this;
	}

	public int getPartialProgress() {
		return partialProgress;
	}

	public ConsoleParams setPartialProgress(int partialProgress) {
		this.partialProgress = partialProgress;
		return this;
	}

	public int addPartialProgress() {
		this.partialProgress = partialProgress + 1;
		return this.partialProgress;
	}
	
	public DSLContext getFromDslContext() {
		return getFromConnection().getAONContext().getDslContext();
	}
	public DSLContext getToDslContext() {
		return ensureToConnection().getAONContext().getDslContext();
	}
	
}
