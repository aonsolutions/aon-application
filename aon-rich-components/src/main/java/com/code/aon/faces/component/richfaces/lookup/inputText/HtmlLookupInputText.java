package com.code.aon.faces.component.richfaces.lookup.inputText;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import com.code.aon.faces.component.richfaces.lookup.HtmlLookupBasicInput;

public class HtmlLookupInputText extends HtmlLookupBasicInput {

	/** The map of join value bindings. */
	private Map<String,ValueExpression> joinBindingsMap;
    
	private Object[] _state;    
    
    /**
     * default no args constructor
     */
    public HtmlLookupInputText() {
        this.joinBindingsMap = Collections.emptyMap();
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
  		joinBindingsMap = (Map<String, ValueExpression>) this._state[1];
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
  			_state = new Object[2];  
  		}  
  		_state[0] = super.saveState(_context);  
  		_state[1] = joinBindingsMap;
  		
  		return _state;  
  	}
	
}
