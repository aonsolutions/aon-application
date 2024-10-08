package com.code.aon.faces.component.richfaces.jsf.ui;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;

import jakarta.el.ValueExpression;

class DelegateUIComponent extends UIComponentBase {
	
	

    private UIComponent htmlDropDownMenu;
	
	public DelegateUIComponent(UIComponent htmlDropDownMenu) {
		this.htmlDropDownMenu = htmlDropDownMenu;
	}

	public int hashCode() {
		return htmlDropDownMenu.hashCode();
	}

	@Override
	public Object saveState(FacesContext context) {
		return htmlDropDownMenu.saveState(context);
	}

	@Override
	public void restoreState(FacesContext context, Object state) {
		htmlDropDownMenu.restoreState(context, state);
	}

	@Override
	public Map<String, Object> getAttributes() {
		return htmlDropDownMenu.getAttributes();
	}

	@Override
	public boolean equals(Object obj) {
		return htmlDropDownMenu.equals(obj);
	}

	@Override
	public boolean isTransient() {
		return htmlDropDownMenu.isTransient();
	}

	@Override
	public void setTransient(boolean newTransientValue) {
		htmlDropDownMenu.setTransient(newTransientValue);
	}

	@Override
	public ValueBinding getValueBinding(String name) {
		return htmlDropDownMenu.getValueBinding(name);
	}

	@Override
	public void setValueBinding(String name, ValueBinding binding) {
		htmlDropDownMenu.setValueBinding(name, binding);
	}

	@Override
	public ValueExpression getValueExpression(String name) {
		return htmlDropDownMenu.getValueExpression(name);
	}

	@Override
	public void setValueExpression(String name, ValueExpression binding) {
		htmlDropDownMenu.setValueExpression(name, binding);
	}

	@Override
	public String toString() {
		return htmlDropDownMenu.toString();
	}

	@Override
	public String getClientId(FacesContext context) {
		return htmlDropDownMenu.getClientId(context);
	}

	@Override
	public String getContainerClientId(FacesContext context) {
		return htmlDropDownMenu.getContainerClientId(context);
	}

	@Override
	public String getFamily() {
		return htmlDropDownMenu.getFamily();
	}

	@Override
	public String getId() {
		return htmlDropDownMenu.getId();
	}

	@Override
	public void setId(String id) {
		htmlDropDownMenu.setId(id);
	}

	@Override
	public UIComponent getParent() {
		return htmlDropDownMenu.getParent();
	}

	@Override
	public void setParent(UIComponent parent) {
		htmlDropDownMenu.setParent(parent);
	}

	@Override
	public boolean isRendered() {
		return htmlDropDownMenu.isRendered();
	}

	@Override
	public void setRendered(boolean rendered) {
		htmlDropDownMenu.setRendered(rendered);
	}

	@Override
	public String getRendererType() {
		return htmlDropDownMenu.getRendererType();
	}

	@Override
	public void setRendererType(String rendererType) {
		htmlDropDownMenu.setRendererType(rendererType);
	}

	@Override
	public boolean getRendersChildren() {
		return htmlDropDownMenu.getRendersChildren();
	}

	@Override
	public List<UIComponent> getChildren() {
		return htmlDropDownMenu.getChildren();
	}

	@Override
	public int getChildCount() {
		return htmlDropDownMenu.getChildCount();
	}

	public UIComponent findComponent(String expr) {
		return htmlDropDownMenu.findComponent(expr);
	}

	public boolean invokeOnComponent(FacesContext context, String clientId, ContextCallback callback)
			throws FacesException {
		return htmlDropDownMenu.invokeOnComponent(context, clientId, callback);
	}

	public Map<String, UIComponent> getFacets() {
		return htmlDropDownMenu.getFacets();
	}

	public int getFacetCount() {
		return htmlDropDownMenu.getFacetCount();
	}

	public UIComponent getFacet(String name) {
		return htmlDropDownMenu.getFacet(name);
	}

	public Iterator<UIComponent> getFacetsAndChildren() {
		return htmlDropDownMenu.getFacetsAndChildren();
	}

	public void broadcast(FacesEvent event) throws AbortProcessingException {
		htmlDropDownMenu.broadcast(event);
	}

	public void decode(FacesContext context) {
		htmlDropDownMenu.decode(context);
	}

	public void encodeBegin(FacesContext context) throws IOException {
		htmlDropDownMenu.encodeBegin(context);
	}

	public void encodeChildren(FacesContext context) throws IOException {
		htmlDropDownMenu.encodeChildren(context);
	}

	public void encodeEnd(FacesContext context) throws IOException {
		htmlDropDownMenu.encodeEnd(context);
	}

	public void encodeAll(FacesContext context) throws IOException {
		htmlDropDownMenu.encodeAll(context);
	}

	public void queueEvent(FacesEvent event) {
		htmlDropDownMenu.queueEvent(event);
	}

	public void processRestoreState(FacesContext context, Object state) {
		htmlDropDownMenu.processRestoreState(context, state);
	}

	public void processDecodes(FacesContext context) {
		htmlDropDownMenu.processDecodes(context);
	}

	public void processValidators(FacesContext context) {
		htmlDropDownMenu.processValidators(context);
	}

	public void processUpdates(FacesContext context) {
		htmlDropDownMenu.processUpdates(context);
	}

	public Object processSaveState(FacesContext context) {
		return htmlDropDownMenu.processSaveState(context);
	}

	
	
}