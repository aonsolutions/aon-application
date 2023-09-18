package solutions.aon.aws.ses;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class SESMessage {

	private static final String DEFAULT_FROM = "no-reply@aon.solutions";
	private static final String DEFAULT_ALIAS = "AON SOLUTIONS S.L.";
	
	private String alias;
	private String from;
	private String subject;
	private String body;
	private List<String> to;
	private List<String> bcc;
	private List<String> cc;
	private String replyTo;
	private List<File> files;

	public String getAlias() {
		return alias != null ? alias : DEFAULT_ALIAS;
	}

	public SESMessage setAlias(String alias) {
		this.alias = alias.replace(",", "").replace(";", "");
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

	public List<String> getTo() {
		if(to == null)
			to = new LinkedList<>();
		return to;
	}

	public SESMessage setTo(List<String> to) {
		this.to = to;
		return this;
	}
	
	public SESMessage setTo(String to) {
		LinkedList<String> toList = new LinkedList<>();
		if(to.contains(";")) {
			String[] arr = to.split(";");
			for (String str : arr) {
				toList.add(str);
			}
		} else toList.add(to);
		this.to = toList;
		return this;
	}
	
	public SESMessage addTo(String to) {
		getTo().add(to);
		return this;
	}

	public List<String> getBcc() {
		if(bcc == null)
			bcc = new LinkedList<>();
		return bcc;
	}

	public SESMessage setBcc(List<String> bcc) {
		this.bcc = bcc;
		return this;
	}
	
	public SESMessage setBcc(String bcc) {
		LinkedList<String> bccList = new LinkedList<>();
		if(bcc.contains(";")) {
			String[] arr = bcc.split(";");
			for (String str : arr) {
				bccList.add(str);
			}
		} else bccList.add(bcc);
		this.bcc = bccList;
		return this;
	}
	
	public SESMessage addBcc(String bcc) {
		getBcc().add(bcc);
		return this;
	}
	
	public List<String> getCc() {
		if(cc == null)
			cc = new LinkedList<>();
		return cc;
	}

	public SESMessage setCc(List<String> cc) {
		this.cc = cc;
		return this;
	}

	public SESMessage setCc(String cc) {
		LinkedList<String> list = new LinkedList<>();
		if(cc.contains(";")) {
			String[] arr = cc.split(";");
			for (String str : arr) {
				list.add(str);
			}
		} else list.add(cc);
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
	
	public boolean isReplyTo() {
		return getReplyTo() != null
			&& !getReplyTo().isEmpty()
			&& !getReplyTo().isBlank();
	}

	public List<File> getFiles() {
		if(files == null)
			files = new LinkedList<>();
		return files;
	}

	public SESMessage setFiles(List<File> files) {
		this.files = files;
		return this;
	}
	
	public SESMessage setFile(File file) {
		LinkedList<File> list = new LinkedList<>();
		list.add(file);
		this.files = list;
		return this;
	}
	public boolean hasAttach() {
		return !getFiles().isEmpty();
	}
}
