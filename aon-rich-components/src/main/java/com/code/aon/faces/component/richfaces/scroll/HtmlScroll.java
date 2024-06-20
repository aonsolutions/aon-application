package com.code.aon.faces.component.richfaces.scroll;

import java.io.IOException;
import java.util.Arrays;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;

import org.ajax4jsf.component.html.HtmlAjaxOutputPanel;
import org.ajax4jsf.renderkit.RendererUtils;

import jakarta.el.ELException;
import jakarta.el.ValueExpression;

public class HtmlScroll extends HtmlAjaxOutputPanel {
	
	private static class ScrollRenderer extends Renderer {

		private Renderer renderer;
		
		public static final String[] ATTRIBUTES = {
				"onscroll"

		};
		
		
		public ScrollRenderer(Renderer renderer) {
			super();
			this.renderer = renderer;
		}

		@Override
		public void decode(FacesContext context, UIComponent component) {
			this.renderer.decode(context, component);
		}

		@Override
		public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
			this.renderer.encodeBegin(context, component);
			RendererUtils.getInstance().encodeAttributesFromArray(context, component, ATTRIBUTES);
		}

		@Override
		public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
			this.renderer.encodeChildren(context, component);
		}

		@Override
		public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
			this.renderer.encodeEnd(context, component);
		}

		@Override
		public String convertClientId(FacesContext context, String clientId) {
			return this.renderer.convertClientId(context, clientId);
		}

		@Override
		public boolean getRendersChildren() {
			return this.renderer.getRendersChildren();
		}

		@Override
		public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue)
				throws ConverterException {
			return this.renderer.getConvertedValue(context, component, submittedValue);
		}
		
	}
	
	/*
	* The client-side script method to be called when a key is pressed over the element and released
	*/
	private  String _onscroll = null;


	public HtmlScroll() {
		super();
	}
	
	public String getOnscroll(){
		if (this._onscroll != null) {
			return this._onscroll;
		}
		ValueExpression ve = getValueExpression("onscroll");
		if (ve != null) {
		    String value = null;
		    
		    try {
				value = (String) ve.getValue(getFacesContext().getELContext());
		    } catch (ELException e) {
				throw new FacesException(e);
		    }
		    
		    return value;
		} 

	    return null;
		

	}

	public void setOnscroll(String _onscroll){
	this._onscroll = _onscroll;
	}
	
	@Override
	public Object saveState(FacesContext context){
	Object [] state = (Object []) super.saveState(context);
	state = Arrays.copyOf(state, state.length + 1);
	state[state.length - 1 ] = _onscroll;
	return state;
	}

	@Override
	public void restoreState(FacesContext context, Object state){
		Object[] states = (Object[]) state;
		super.restoreState(context, states);
		_onscroll = (String)states[states.length -1 ];
	}
	
	@Override
	protected Renderer getRenderer(FacesContext context) {
		Renderer renderer = super.getRenderer(context);
		return new ScrollRenderer(renderer);
	}
	
	@Override
	public void setValueExpression(String name, ValueExpression binding) {
		super.setValueExpression(name, binding);
	}
	
}
