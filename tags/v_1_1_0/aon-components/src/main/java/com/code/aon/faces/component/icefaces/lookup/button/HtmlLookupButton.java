package com.code.aon.faces.component.icefaces.lookup.button;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.code.aon.faces.component.icefaces.lookup.ILookupComponent;
import com.code.aon.faces.component.icefaces.lookup.ILookupTags;
import com.icesoft.faces.component.ext.HtmlCommandButton;

public class HtmlLookupButton extends HtmlCommandButton implements ILookupTags, ILookupComponent {

    /**
     * String constant component type
     */
    public static final String COMPONENT_TYPE =
            "com.code.aon.faces.HtmlLookupButton";
    /**
     * String constant renderer type
     */
    public static final String RENDERER_TYPE = "com.code.aon.faces.Button";
   	
    private LookupButtonType actionType;
    
    private ValueBinding property;
    
    private ValueBinding lookup;
    
	private Object[] _state;    
    
    /**
     * default no args constructor
     */
    public HtmlLookupButton() {
        setRendererType(RENDERER_TYPE);
    }
    
    public void setActionType(LookupButtonType type) {
    	this.actionType = type;
	}

	public LookupButtonType getActionType() {
    	if (null != this.actionType) {
            return this.actionType;
        }
        ValueBinding _vb = getValueBinding(ACTION_TYPE);
        return (_vb != null) ? (LookupButtonType) _vb.getValue(getFacesContext()) : null;
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
  		actionType = (LookupButtonType) this._state[3];  		
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
  			_state = new Object[4];  
  		}  
  		_state[0] = super.saveState(_context);  
  		_state[1] = lookup;
  		_state[2] = property;
  		_state[3] = actionType;  
  		return _state;  
  	}
	
}
