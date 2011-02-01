package com.code.aon.hyperview.model;

public interface IHyperViewContent {
	
	String getLabel();

	boolean isFormView();
	
	boolean isListView();
	
	boolean isContentCompositeView();

	boolean isHyperLinkView();
	
	boolean isImageView();
}
