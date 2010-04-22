package com.code.aon.hyperview.model;

import java.util.List;

import com.code.aon.hyperview.enumeration.LayoutType;

public class HyperViewContentsContainer {

	private HyperViewContents contents;

	private HyperViewLayout layout;

	public HyperViewContents getContents() {
		return contents;
	}

	public void setContents(HyperViewContents contents) {
		this.contents = contents;
	}

	public List getContentsList() {
		return contents.getContents();
	}

	public HyperViewLayout getLayout() {
		if (layout == null) {
			layout = new HyperViewLayout();
			layout.setType(LayoutType.COLUMN );
		}
		return layout;
	}

	public void setLayout(HyperViewLayout layout) {
		this.layout = layout;
	}
	
	public boolean isColumnLayoutContent() {
		return (getLayout().getType()==LayoutType.COLUMN);
	}
	
	public boolean isRowLayoutContent() {
		return (getLayout().getType()==LayoutType.ROW);
	}
	
}
