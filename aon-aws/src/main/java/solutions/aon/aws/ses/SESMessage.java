package solutions.aon.aws.ses;

import java.io.File;
import java.util.LinkedList;

public class SESMessage {

	private final static String DEFAULT_FROM = "no-reply@aon.solutions";
	private final static String DEFAULT_ALIAS = "AON SOLUTIONS S.L.";
	
	String alias;
	String from;
	String subject;
	String body;
	LinkedList<String> to;
	LinkedList<String> bcc;
	LinkedList<String> cc;
	String replyTo;
	LinkedList<File> files;
	
	public SESMessage() {
	
	}

	public String getAlias() {
		return alias != null ? alias : DEFAULT_ALIAS;
	}

	public SESMessage setAlias(String alias) {
		this.alias = alias;
		return this;
	}

	public String getFrom() {
		return from != null ? from : DEFAULT_FROM;
	}

	public SESMessage setFrom(String from) {
		this.from = from;
		return this;
	}
	
	public String getAliasFrom(){
		return getAlias() + "<" + getFrom() + ">";
	}

	public String getSubject() {
		return subject;
	}

	public SESMessage setSubject(String subject) {
		this.subject = subject;
		return this;
	}

	public String getBody() {
		return body;
	}

	public SESMessage setBody(String body) {
		this.body = body;
		return this;
	}

	public LinkedList<String> getTo() {
		if(to == null)
			to = new LinkedList<>();
		return to;
	}

	public SESMessage setTo(LinkedList<String> to) {
		this.to = to;
		return this;
	}
	
	public SESMessage setTo(String to) {
		LinkedList<String> toList = new LinkedList<>();
		toList.add(to);
		this.to = toList;
		return this;
	}
	
	public SESMessage addTo(String to) {
		getTo().add(to);
		return this;
	}

	public LinkedList<String> getBcc() {
		if(bcc == null)
			bcc = new LinkedList<>();
		return bcc;
	}

	public SESMessage setBcc(LinkedList<String> bcc) {
		this.bcc = bcc;
		return this;
	}
	
	public SESMessage setBcc(String bcc) {
		LinkedList<String> bccList = new LinkedList<>();
		bccList.add(bcc);
		this.bcc = bccList;
		return this;
	}
	
	public SESMessage addBcc(String bcc) {
		getBcc().add(bcc);
		return this;
	}
	
	public LinkedList<String> getCc() {
		if(cc == null)
			cc = new LinkedList<>();
		return cc;
	}

	public SESMessage setCc(LinkedList<String> cc) {
		this.cc = cc;
		return this;
	}

	public SESMessage setCc(String cc) {
		LinkedList<String> list = new LinkedList<>();
		list.add(cc);
		this.cc = list;
		return this;
	}
	
	public SESMessage addCc(String cc) {
		getCc().add(cc);
		return this;
	}
	
	public String getReplyTo() {
		return replyTo;
	}

	public SESMessage setReplyTo(String replyTo) {
		this.replyTo = replyTo;
		return this;
	}
	
	public Boolean isReplyTo() {
		return getReplyTo() != null;
	}

	public LinkedList<File> getFiles() {
		if(files == null)
			files = new LinkedList<>();
		return files;
	}

	public SESMessage setFiles(LinkedList<File> files) {
		this.files = files;
		return this;
	}
	
	public Boolean hasAttach() {
		return !getFiles().isEmpty();
	}
	
}
