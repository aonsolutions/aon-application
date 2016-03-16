package com.esferalia.aon.occam.api.model.office;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class NoticeContainer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int count = 0;
	public List<Notice> notices;
	
	public NoticeContainer() {
		
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	public void setNotices(List<Notice> notices) {
		this.notices = notices;
	}

	public int getCount() {
		return count;
	}
	
	public List<Notice> getNotices() {
		if (notices == null)
			return new LinkedList<Notice>();
		return notices;
	}
}
