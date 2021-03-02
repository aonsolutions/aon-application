package net.aonsolutions.aon.tbai.beans.invoice.parts.breakdowns;

import java.util.Optional;

public class Subject {

	private Exempted 	exempted;
	private NoExempted 	no_exempted;
	
	public Subject(Exempted exempted, NoExempted no_exempted) {
		this.exempted = exempted;
		this.no_exempted = no_exempted;
	}
	
	public Optional<Exempted> getExempted() {return Optional.ofNullable(exempted);}
	public void setExempted(Exempted exempted) {this.exempted = exempted;}

	public Optional<NoExempted> getNo_exempted() {return Optional.ofNullable(no_exempted);}
	public void setNo_exempted(NoExempted no_exempted) {this.no_exempted = no_exempted;}
	
}
