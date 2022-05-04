package com.esferalia.aon.occam.api.model.task;

public enum TaskWorkflowType  {

	OPEN("opened"),
	CLOSE("closed"),
	REOPEN("reopened"),
	DUPLICATE("duplicate"),
	LIBERATE("liberate"),
	DELETE("deleted"),
	RESTORE("restore"),
	COMMENT("comment"),
	ASSIGN("assigned"),
	CONNECTED("connected");
	
	String event;
	
	private TaskWorkflowType(String event) {
		this.event = event;
	}

	public String getEvent() {
		return event;
	}
		
	public String getName() {
    	return this.toString().toLowerCase();
    }
	
    public byte value() {
    	return (byte) this.ordinal();
	}
    
    public static TaskWorkflowType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
    
	public static TaskWorkflowType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= TaskWorkflowType.values().length) return null;
		return TaskWorkflowType.values()[i];
	}
	
	public static TaskWorkflowType safeValueOf(String value) {
		for (TaskWorkflowType tht : TaskWorkflowType.values()) {
			if(tht.name().equalsIgnoreCase(value) || tht.getEvent().equalsIgnoreCase(value)) {
				return tht;
			}
		}
		return null;
	}
    
}