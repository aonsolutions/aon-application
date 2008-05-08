package com.code.aon.hyperview.model;

public class HyperViewCompositeView extends HyperViewContentsContainer  implements IHyperViewContent {

	private String label;
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label=label;
	}
	
	public boolean isFormView() {
		return false;
	}

	public boolean isListView() {
		return false;
	}

	public boolean isContentCompositeView() {
		return true;
	}

	public boolean isHyperLinkView() {
		return false;
	}
	
	public boolean isImageView() {
		return false;
	}
}
