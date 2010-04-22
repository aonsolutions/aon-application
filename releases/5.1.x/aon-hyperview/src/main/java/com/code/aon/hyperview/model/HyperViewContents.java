package com.code.aon.hyperview.model;

import java.util.LinkedList;
import java.util.List;

/**
 * @author ecastellano
 *
 */
public class HyperViewContents {

	private List<IHyperViewContent> contents = new LinkedList<IHyperViewContent>();
	
	public void addContent(IHyperViewContent content){
		contents.add( content );
	}
	
	public List<IHyperViewContent> getContents() {
		return contents;
	}
	
}
