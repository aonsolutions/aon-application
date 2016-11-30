package com.code.aon.webservice.github;

public enum GithubAction {
	
	OPENED("opened"),
	CLOSED("closed"),
	REOPENED("reopened"),
	COMMENT_CREATED("created"),
	COMMENT_EDITED("edited"),
	COMMENT_DELETED("deleted"),
	ASSIGNED("assigned"),
	UNASSIGNED("unassigned"),
	LABELED("labeled"),
	UNLABELED("unlabeled"),
	MILESTONED("milestoned"),
	DEMILESTONED("demilestoned");
	
	private String name;
	
	private GithubAction(String name) {
		this.name = name;
	}
	
	public Byte value() {
		return (byte) this.ordinal();
	}

	public String getName(){
		return name;
	}
}
