package net.aonsolutions.aon.tbai.emision._beans.breakdowns;

import java.util.Optional;

public class National implements Breakdown{

	private Subject 	subject;
	private NoSubject 	noSubject;
	
	public National(Subject subject, NoSubject noSubject) {
		this.subject = subject;
		this.noSubject = noSubject;
	}

	public Optional<Subject> getSubject() {return Optional.ofNullable(subject);}
	public void setSubject(Subject subject) {this.subject = subject;}

	public Optional<NoSubject> getNoSubject() {return Optional.ofNullable(noSubject);}
	public void setNoSubject(NoSubject noSubject) {this.noSubject = noSubject;}	
	
}
