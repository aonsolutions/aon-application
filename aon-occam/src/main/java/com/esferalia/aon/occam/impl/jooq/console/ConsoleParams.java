package com.esferalia.aon.occam.impl.jooq.console;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Schema;


public class ConsoleParams {

	private DSLContext dslContext;
	private String hostName;
	private String user;
	private String password;
	private String database;
	private String port;
	private String domainName;
	private String newDomainName;
	private Integer newDomain;
	private Integer domain;
	private Integer parent;
	private boolean inhertitanceEnabled;
	
	private PrintStream	printer;
	private Schema schema;
	private Map<String,ScriptTable> script;
	private List<String> errors;
	
	private int totalCount;
	private int totalProgress;

	private int partialCount;
	private int partialProgress;

	public DSLContext getDslContext() {
		return dslContext;
	}

	public ConsoleParams setDslContext(DSLContext dslContext) {
		this.dslContext = dslContext;
		return this;
	}

	public String getHostName() {
		return hostName;
	}

	public ConsoleParams setHostName(String hostName) {
		this.hostName = hostName;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public ConsoleParams setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getDatabase() {
		return database;
	}

	public ConsoleParams setDatabase(String database) {
		this.database = database;
		return this;
	}

	public String getPort() {
		return port;
	}

	public ConsoleParams setPort(String port) {
		this.port = port;
		return this;
	}

	public String getDomainName() {
		return domainName;
	}

	public ConsoleParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	public String getNewDomainName() {
		return newDomainName;
	}

	public ConsoleParams setNewDomainName(String newDomainName) {
		this.newDomainName = newDomainName;
		return this;
	}

	public String getUser() {
		return user;
	}

	public ConsoleParams setUser(String user) {
		this.user = user;
		return this;
	}

	public String getUrl() {
		return "jdbc:mysql://" + hostName + ":" + port + "/" + database;
	}

	public Integer getDomain() {
		return domain;
	}

	public ConsoleParams setDomain(Integer domain) {
		this.domain = domain;
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

	
	public Schema getSchema() {
		return schema;
	}

	public ConsoleParams setSchema(Schema schema) {
		this.schema = schema;
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

	public int addPartialCount() {
		this.partialProgress = partialProgress + 1;
		return this.partialProgress;
	}
	
	
	
}
