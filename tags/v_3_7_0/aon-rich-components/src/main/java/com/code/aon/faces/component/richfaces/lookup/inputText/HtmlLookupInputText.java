package com.code.aon.faces.component.richfaces.lookup.inputText;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;

import com.code.aon.faces.component.richfaces.lookup.ILookupComponent;
import com.code.aon.faces.component.richfaces.lookup.ILookupTags;
import com.code.aon.faces.controller.RichLookupBean;

public class HtmlLookupInputText extends HtmlInputText implements ILookupTags, ILookupComponent {

    /**
     * String constant component type
     */
    public static final String COMPONENT_TYPE =
            "com.code.aon.faces.HtmlLookupInputText";
    
    private ValueExpression property;
    
    private RichLookupBean lookup;
    
    private String lookupProperty;
    
    private MethodExpression lookupChangeListener;    
    
	/** The map of join value bindings. */
	private Map<String,ValueExpression> joinBindingsMap;
    
	private Object[] _state;    
    
    /**
     * default no args constructor
     */
    public HtmlLookupInputText() {
        this.joinBindingsMap = Collections.emptyMap();
    }
    
	public RichLookupBean getLookup() {
    	if (null != this.lookup) {
            return this.lookup;
        }
    	ValueExpression _vb = getValueExpression(LOOKUP);
        return (_vb != null) ? (RichLookupBean) _vb.getValue(getFacesContext().getELContext()) : null;
	}

	public void setLookup(RichLookupBean lookup) {
		this.lookup = lookup;
	}

	public ValueExpression getProperty() {
    	if (null != this.property) {
            return this.property;
        }
        return getValueExpression(PROPERTY);
	}

	public void setProperty(ValueExpression property) {
		this.property = property;
	}
	
	public MethodExpression getLookupChangeListener() {
		return this.lookupChangeListener;
	}

	public void setLookupChangeListener(MethodExpression lookupChangeListener) {
		this.lookupChangeListener = lookupChangeListener;
	}

	public String getLookupProperty() {
    	if (null != this.lookupProperty) {
            return this.lookupProperty;
        }
    	ValueExpression _vb = getValueExpression(LOOKUP_PROPERTY);
        return ((_vb != null) ? (String)_vb.getValue(getFacesContext().getELContext()) : null);
	}	

	public void setLookupProperty(String lookupProperty) {
		this.lookupProperty = lookupProperty;
	}
	
	public void addJoinProperty( String alias, ValueExpression ve ) {
		if ( this.joinBindingsMap.isEmpty() ) {
			this.joinBindingsMap = new HashMap<String, ValueExpression>();
		}
		this.joinBindingsMap.put( alias, ve );
	}
	
	/**
	 * Gets the join bindings map.
	 * 
	 * @return the join bindings map
	 */
	public Map<String, ValueExpression> getJoinBindingsMap() {
		return this.joinBindingsMap;
	}
	
	/**
	 * Sets the join bindings map.
	 * 
	 * @param joinBindingsMap the join bindings map
	 */
	public void setJoinBindingsMap(Map<String, ValueExpression> joinBindingsMap) {
		this.joinBindingsMap = joinBindingsMap;
	}

	/**
     * <p>Gets the state of the instance as a <code>Serializable</code>
     * Object.</p>
     *
     * @param context
     * @return Object values[]
     */
  	public void restoreState(FacesContext context, Object value) {  
  		this._state = (Object[]) value;  
  		super.restoreState(context, this._state[0]);  
  		lookup = (RichLookupBean) this._state[1];
  		property = (ValueExpression) this._state[2];
  		joinBindingsMap = (Map<String, ValueExpression>) this._state[3];
  		lookupChangeListener = (MethodExpression) this._state[4];  	
  		lookupProperty = (String) this._state[5];  		
  	}  
   
    /**
     * <p>Gets the state of the instance as a <code>Serializable</code>
     * Object.</p>
     *
     * @param context
     * @return Object values[]
     */
  	public Object saveState(FacesContext _context) {  
  		if (_state == null) {  
  			_state = new Object[6];  
  		}  
  		_state[0] = super.saveState(_context);  
  		_state[1] = lookup;
  		_state[2] = property;
  		_state[3] = joinBindingsMap;
  		_state[4] = lookupChangeListener;  
  		_state[5] = lookupProperty;
  		
  		return _state;  
  	}
	
}
