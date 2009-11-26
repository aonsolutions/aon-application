package com.code.aon.faces.component.richfaces.lookup.inputText;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;

import com.code.aon.faces.component.richfaces.lookup.ILookupComponent;
import com.code.aon.faces.component.richfaces.lookup.ILookupTags;

public class HtmlLookupInputText extends HtmlInputText implements ILookupTags, ILookupComponent {

    /**
     * String constant component type
     */
    public static final String COMPONENT_TYPE =
            "com.code.aon.faces.HtmlLookupInputText";
    
    private ValueBinding property;
    
    private ValueBinding lookup;
    
    private String lookupProperty;
    
    private MethodBinding lookupChangeListener;    
    
	/** The map of join value bindings. */
	private Map<String,ValueBinding> joinBindingsMap;
    
	private Object[] _state;    
    
    /**
     * default no args constructor
     */
    public HtmlLookupInputText() {
        this.joinBindingsMap = Collections.emptyMap();
    }
    
	public ValueBinding getLookup() {
    	if (null != this.lookup) {
            return this.lookup;
        }
        ValueBinding _vb = getValueBinding(LOOKUP);
        return (_vb != null) ? (ValueBinding) _vb.getValue(getFacesContext()) : null;
	}

	public void setLookup(ValueBinding lookup) {
		this.lookup = lookup;
	}

	public ValueBinding getProperty() {
    	if (null != this.property) {
            return this.property;
        }
        return getValueBinding(PROPERTY);
	}

	public void setProperty(ValueBinding property) {
		this.property = property;
	}
	
	public MethodBinding getLookupChangeListener() {
		return this.lookupChangeListener;
	}

	public void setLookupChangeListener(MethodBinding lookupChangeListener) {
		this.lookupChangeListener = lookupChangeListener;
	}

	public String getLookupProperty() {
    	if (null != this.lookupProperty) {
            return this.lookupProperty;
        }
        ValueBinding _vb = getValueBinding(LOOKUP_PROPERTY);
        return ((_vb != null) ? (String)_vb.getValue(getFacesContext()) : null);
	}	

	public void setLookupProperty(String lookupProperty) {
		this.lookupProperty = lookupProperty;
	}
	
	public void addJoinProperty( String alias, ValueBinding vb ) {
		if ( this.joinBindingsMap.isEmpty() ) {
			this.joinBindingsMap = new HashMap<String, ValueBinding>();
		}
		this.joinBindingsMap.put( alias, vb );
	}
	
	/**
	 * Gets the join bindings map.
	 * 
	 * @return the join bindings map
	 */
	public Map<String, ValueBinding> getJoinBindingsMap() {
		return this.joinBindingsMap;
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
  		lookup = (ValueBinding) this._state[1];
  		property = (ValueBinding) this._state[2];
  		joinBindingsMap = (Map<String, ValueBinding>) this._state[3];
  		lookupChangeListener = (MethodBinding) this._state[4];  	
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
