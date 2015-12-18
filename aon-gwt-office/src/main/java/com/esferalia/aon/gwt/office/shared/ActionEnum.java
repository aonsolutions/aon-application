package com.esferalia.aon.gwt.office.shared;

import java.io.Serializable;

public enum ActionEnum implements Serializable {
	
	loaduser("loaduser"),
	addrepo("addrepo"),
	loadorgrepo("loadorgrepo"),
	loaduserepo("loaduserepo"),
	loadnamerepo("loadnamerepo"),
	saverepo("saverepo"),
	deleterepo("deleterepo"),
	loadopenissues("loadopenissues"),
	loadclosedissues("loadclosedissues"),
	loadallissues("loadallissues"),
	createissue("createissue"),
	editissue("editissue"),
	addissuelabel("addissuelabel"),
	deleteissue("deleteissue"),
	getcomments("getcomments"),
	addcomment("addcomment"),
	editcomment("editcomment"),
	deletecomment("deletecomment"),
	loadlabels("loadlabels"),
	createlabel("createlabel"),
	savelabel("savelabel"),
	deletelabel("deletelabel")
	;
	
	private String description;
	
	private ActionEnum(String description) {
		this.description = description;
	}
	
	public String get() {
		return this.description;
	}

}
