package net.aonsolutions.aon.tbai.beans.invoice.parts.breakdowns;

import java.util.Optional;

public class Delivery {

	private Subject 	subject;
	private NoSubject 	noSubject;
	
	public Delivery(Subject subject, NoSubject noSubject) {
		this.subject = subject;
		this.noSubject = noSubject;
	}

	public Optional<Subject> getSubject() {return Optional.of(subject);}
	public void setSubject(Subject subject) {this.subject = subject;}

	public Optional<NoSubject> getNoSubject() {return Optional.of(noSubject);}
	public void setNoSubject(NoSubject noSubject) {this.noSubject = noSubject;}	
	
	
}
