package com.esferalia.aon.gwt.document.shared;

import java.util.Vector;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Tags implements IsSerializable{
	
	TagList tags;
	String tagsStr;
	
	public TagList getTags() {
		return tags;
	}
	public void setTags(TagList tags) {
		this.tags = tags;
	}
	public String getTagsStr() {
		return tagsStr;
	}
	public void setTagsStr(String tagsStr) {
		this.tagsStr = tagsStr;
	}
	
	public static Boolean contain(String tag, Vector<Tag> tags){
		for (Tag t : tags) {
			if (t.getName().equals(tag)){
				return true;
			}
		}
		return false;
	}
}
