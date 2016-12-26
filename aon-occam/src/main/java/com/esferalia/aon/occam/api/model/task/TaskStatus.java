package com.esferalia.aon.occam.api.model.task;

public enum TaskStatus {

	DELETED(),
	PENDING,
	IN_PROGRESS,
	FINISHED,
	FAQ;

	public String getName() {
    	return this.toString().toLowerCase();
    }
    
	public String getGwtName() { // PROVISIONAL!!!!
		if(this.equals(FAQ)) return getName();
		if(this.equals(DELETED)) return getName();
		if(this.equals(PENDING) || this.equals(IN_PROGRESS)) return "open";
		else return "closed";
    }
	
	public String getESName() { // PROVISIONAL!!!!
		if(this.equals(FAQ)) return getName();
		if(this.equals(DELETED)) return "BORRADAS";
		if(this.equals(PENDING) || this.equals(IN_PROGRESS)) return "ABIERTAS";
		else return "CERRADAS";
    }
	
    public byte value() {
    	return (byte) this.ordinal();
	}
}
