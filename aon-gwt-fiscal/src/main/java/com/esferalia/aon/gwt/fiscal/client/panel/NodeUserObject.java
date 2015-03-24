package com.esferalia.aon.gwt.fiscal.client.panel;

class NodeUserObject<T> {
	T userObject;
	NodeType<T> nodeType;

	public NodeUserObject(NodeType<T> nodeType, T userObject) {
		this.userObject = userObject;
		this.nodeType = nodeType;
	}

	public NodeType<T> getNodeType() {
		return nodeType;
	}

	public T getUserObject() {
		return userObject;
	}
}
