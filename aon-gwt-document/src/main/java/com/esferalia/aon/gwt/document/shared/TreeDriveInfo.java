package com.esferalia.aon.gwt.document.shared;

import java.util.Vector;

public class TreeDriveInfo {

	private FileInfo parent;
	private Vector<TreeDriveInfo> sons;
	
	public FileInfo getParent() {
		return parent;
	}
	public void setParent(FileInfo parent) {
		this.parent = parent;
	}
	public Vector<TreeDriveInfo> getSons() {
		return sons;
	}
	public void setSons(Vector<TreeDriveInfo> sons) {
		this.sons = sons;
	}
	
	
	
}
