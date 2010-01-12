package com.code.aon.faces.component.richfaces.lookup.button;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;

import org.ajax4jsf.component.html.HtmlAjaxCommandButton;

import com.code.aon.faces.component.richfaces.IRichFacesTags;
import com.code.aon.faces.component.richfaces.lookup.ILookupComponent;
import com.code.aon.faces.component.richfaces.lookup.ILookupTags;

public class HtmlLookupButton extends HtmlAjaxCommandButton implements ILookupTags, ILookupComponent {

    /**
     * String constant component type
     */
    public static final String COMPONENT_TYPE =
            "com.code.aon.faces.HtmlLookupButton";
   	
    private LookupButtonType actionType;
    
    private ValueBinding property;
    
    private ValueBinding lookup;
    
    private MethodBinding valueChangeListener;
    
    private String windowTitle;
    
    private String selectReRender;
    
	private Object[] _state;    
    
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

	public MethodBinding getValueChangeListener() {
		return this.valueChangeListener;
	}

	public void setValueChangeListener(MethodBinding valueChangeListener) {
		this.valueChangeListener = valueChangeListener;
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

	public String getWindowTitle() {
    	if (null != this.windowTitle) {
            return this.windowTitle;
        }
        ValueBinding _vb = getValueBinding(WINDOW_TITLE);
        return ((_vb != null) ? (String)_vb.getValue(getFacesContext()) : null);
	}

	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
	}

	public String getSelectReRender() {
    	if (null != this.selectReRender) {
            return this.selectReRender;
        }
        ValueBinding _vb = getValueBinding(SELECT_RE_RENDER);
        return ((_vb != null) ? (String)_vb.getValue(getFacesContext()) : null);
	}

	public void setSelectReRender(String selectReRender) {
		this.selectReRender = selectReRender;
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
  		valueChangeListener = (MethodBinding) this._state[4];  	
  		windowTitle = (String) this._state[5];
  		selectReRender = (String) this._state[6];
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
  			_state = new Object[7];  
  		}  
  		_state[0] = super.saveState(_context);  
  		_state[1] = lookup;
  		_state[2] = property;
  		_state[3] = actionType;  
  		_state[4] = valueChangeListener;  
  		_state[5] = windowTitle;
  		_state[6] = selectReRender;
  		return _state;  
  	}
	
}
