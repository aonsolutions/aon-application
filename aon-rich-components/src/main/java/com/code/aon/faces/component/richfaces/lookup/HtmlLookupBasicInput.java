package com.code.aon.faces.component.richfaces.lookup;

import static com.code.aon.faces.component.richfaces.IRichFacesTags.PROPERTY;

import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;

import com.code.aon.faces.controller.RichLookupBean;
import com.code.aon.ui.form.event.IControllerListener;

public class HtmlLookupBasicInput extends HtmlInputText implements ILookupConstants, ILookupComponent {

    private ValueExpression property;
    
    private IControllerListener controllerListener;
    
    private RichLookupBean lookup;
    
    private String lookupProperty;
    
    private MethodExpression lookupChangeListener;    
    
	private Object[] _state;    
    
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
	
	public IControllerListener getControllerListener() {
    	if (null != this.controllerListener) {
            return this.controllerListener;
        }
    	ValueExpression vb = getValueExpression(CONTROLLER_LISTENER);
    	return (vb != null) ? (IControllerListener)vb.getValue(getFacesContext().getELContext()) : null;
	}

	public void setControllerListener(IControllerListener controllerListener) {
		this.controllerListener = controllerListener;
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

	public boolean isResolved() {
		return getLookup().isResolved( this );
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
  		lookupChangeListener = (MethodExpression) this._state[3];  	
  		lookupProperty = (String) this._state[4];  		
  		controllerListener = (IControllerListener) this._state[5]; 		
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
  		_state[3] = lookupChangeListener;  
  		_state[4] = lookupProperty;
  		_state[5] = controllerListener;
  		
  		return _state;  
  	}
	
}
