package com.esferalia.aon.gwt.api.client;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class AonJsArray<T extends JavaScriptObject> extends JsArray<T>{
	  
	protected AonJsArray() {
	
	}
	
	public final native AonJsArray<T> concat(AonJsArray<T> array) /*-{
		return this.concat(array);
	}-*/;
	
	public final LinkedList<T> toLinkedList(){
		LinkedList<T> linkedList = new LinkedList<T>();
		for(Integer index = 0; index < this.length(); index++){
			linkedList.add(this.get(index));
		}
		return linkedList;
	}
	
	public final Stream<T> stream(){
		return toLinkedList().stream();
	}
}
