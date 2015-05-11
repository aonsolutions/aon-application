package com.esferalia.aon.gwt.fiscal.client.tree;

public interface ITreeNodeCallback<T> {
	void delete(T t);
	void changeLabel(T t);
}
