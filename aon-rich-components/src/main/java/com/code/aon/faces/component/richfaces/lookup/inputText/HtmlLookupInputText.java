package com.code.aon.faces.component.richfaces.lookup.inputText;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;

import com.code.aon.faces.component.richfaces.lookup.HtmlLookupBasicInput;

public class HtmlLookupInputText extends HtmlLookupBasicInput {

	/** The map of join value bindings. */
	private List<JoinProperty> joinProperties;
    
	private Object[] _state;    
    
    /**
     * default no args constructor
     */
    public HtmlLookupInputText() {
        this.joinProperties = Collections.emptyList();
    }
	
	public void addJoinProperty( JoinProperty joinProperty ) {
		if ( this.joinProperties.isEmpty() ) {
			this.joinProperties = new LinkedList<JoinProperty>();
		}
		this.joinProperties.add( joinProperty );
	}
	
	/**
	 * Gets the join properties list.
	 * 
	 * @return the join properties list
	 */
	public List<JoinProperty> getJoinProperties() {
		return joinProperties;
	}

	/**
	 * Sets the join properties list.
	 * 
	 * @param joinProperties the join properties list
	 */
	public void setJoinProperties(List<JoinProperty> joinProperties) {
		this.joinProperties = joinProperties;
	}

	/**
     * <p>Gets the state of the instance as a <code>Serializable</code>
     * Object.</p>
     *
     * @param context
     * @return Object values[]
     */
  	@SuppressWarnings("unchecked")
	public void restoreState(FacesContext context, Object value) {  
  		this._state = (Object[]) value;  
  		super.restoreState(context, this._state[0]);  
  		joinProperties = (List<JoinProperty>) this._state[1];
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
  		_state[1] = joinProperties;
  		
  		return _state;  
  	}
	
}
