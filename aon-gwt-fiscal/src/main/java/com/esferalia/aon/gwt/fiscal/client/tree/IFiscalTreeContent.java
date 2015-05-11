package com.esferalia.aon.gwt.fiscal.client.tree;


public interface IFiscalTreeContent<T> {
	void select( T t);
	void setCallback( ITreeNodeCallback<T> callback);
}
