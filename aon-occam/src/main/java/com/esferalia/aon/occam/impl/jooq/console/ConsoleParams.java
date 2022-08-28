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
	
	private String domainName;
	private Integer domain;
	private String user;
	
	private Integer parent;
	private boolean inhertitanceEnabled;
	private String newDomainName;
	private Integer newDomain;
	
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

	public String getDomainName() {
		return domainName;
	}
	public ConsoleParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public ConsoleParams setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getUser() {
		return user;
	}
	public ConsoleParams setUser(String user) {
		this.user = user;
		return this;
	}
	
	public Occam getOccam() {
		return new Occam()
			.setDomainName( getDomainName())
			.setDomain(getDomain())
			.setUser(getUser());
	}
	
	public String getNewDomainName() {
		return newDomainName;
	}
	public ConsoleParams setNewDomainName(String newDomainName) {
		this.newDomainName = newDomainName;
		return this;
	}

	public Integer getParent() {
		return parent;
	}
	public ConsoleParams setParent(Integer parent) {
		this.parent = parent;
		return this;
	}
	
	public boolean isInhertitanceEnabled() {
		return inhertitanceEnabled;
	}
	public ConsoleParams setInhertitanceEnabled(boolean inhertitanceEnabled) {
		this.inhertitanceEnabled = inhertitanceEnabled;
		return this;
	}

	public Integer getNewDomain() {
		return newDomain;
	}
	public ConsoleParams setNewDomain(Integer newDomain) {
		this.newDomain = newDomain;
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
		return getFromConnection().getDslContext();
	}
	public DSLContext getToDslContext() {
		return ensureToConnection().getDslContext();
	}
	
}
