package com.code.aon.faces.component.richfaces.lookup.button;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import org.ajax4jsf.component.html.HtmlAjaxCommandButton;

import com.code.aon.faces.component.richfaces.lookup.ILookupComponent;
import com.code.aon.faces.component.richfaces.lookup.ILookupTags;
import com.code.aon.faces.controller.RichLookupBean;
import com.code.aon.ui.form.event.IControllerListener;

public class HtmlLookupButton extends HtmlAjaxCommandButton implements ILookupTags, ILookupComponent {

    /**
     * String constant component type
     */
    public static final String COMPONENT_TYPE =
            "com.code.aon.faces.HtmlLookupButton";
    
	private static final String DEFAULT_MIN_WIDTH = "500";
	
	private static final String DEFAULT_MIN_HEIGHT = "300";    
   	
    private LookupButtonType actionType;
    
    private ValueExpression property;
    
    private IControllerListener controllerListener;
    
    private RichLookupBean lookup;
    
    private String lookupProperty;
    
    private MethodExpression lookupChangeListener;
    
    private String windowTitle;
    
    private String selectReRender;
    
    private String minWidth;
    
    private String minHeight;
    
	private Object[] _state;    
    
    public void setActionType(LookupButtonType type) {
    	this.actionType = type;
	}

	public LookupButtonType getActionType() {
    	if (null != this.actionType) {
            return this.actionType;
        }
    	ValueExpression _vb = getValueExpression(ACTION_TYPE);
        return (_vb != null) ? (LookupButtonType) _vb.getValue(getFacesContext().getELContext()) : null;
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

	public MethodExpression getLookupChangeListener() {
		return this.lookupChangeListener;
	}

	public void setLookupChangeListener(MethodExpression lookupChangeListener) {
		this.lookupChangeListener = lookupChangeListener;
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
	
	public String getWindowTitle() {
    	if (null != this.windowTitle) {
            return this.windowTitle;
        }
    	ValueExpression _vb = getValueExpression(WINDOW_TITLE);
        return ((_vb != null) ? (String)_vb.getValue(getFacesContext().getELContext()) : null);
	}

	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
	}

	public String getSelectReRender() {
    	if (null != this.selectReRender) {
            return this.selectReRender;
        }
    	ValueExpression _vb = getValueExpression(SELECT_RE_RENDER);
        return ((_vb != null) ? (String)_vb.getValue(getFacesContext().getELContext()) : null);
	}

	public void setSelectReRender(String selectReRender) {
		this.selectReRender = selectReRender;
	}
	
	public String getMinWidth() {
    	if (null != this.minWidth) {
            return this.minWidth;
        }
    	ValueExpression _vb = getValueExpression(MIN_WIDTH);
        return ((_vb != null) ? (String)_vb.getValue(getFacesContext().getELContext()) : DEFAULT_MIN_WIDTH);
	}

	public void setMinWidth(String minWidth) {
		this.minWidth = minWidth;
	}

	public String getMinHeight() {
    	if (null != this.minHeight) {
            return this.minHeight;
        }
    	ValueExpression _vb = getValueExpression(MIN_HEIGHT);
        return ((_vb != null) ? (String)_vb.getValue(getFacesContext().getELContext()) : DEFAULT_MIN_HEIGHT);
	}

	public void setMinHeight(String minHeight) {
		this.minHeight = minHeight;
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
  		actionType = (LookupButtonType) this._state[3];  		
  		lookupChangeListener = (MethodExpression) this._state[4];  	
  		windowTitle = (String) this._state[5];
  		selectReRender = (String) this._state[6];
  		minWidth = (String) this._state[7];
  		minHeight = (String) this._state[8];
  		lookupProperty = (String) this._state[9];
  		controllerListener = (IControllerListener) this._state[10];
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
  			_state = new Object[11];  
  		}  
  		_state[0] = super.saveState(_context);  
  		_state[1] = lookup;
  		_state[2] = property;
  		_state[3] = actionType;  
  		_state[4] = lookupChangeListener;  
  		_state[5] = windowTitle;
  		_state[6] = selectReRender;
  		_state[7] = minWidth;
  		_state[8] = minHeight;
  		_state[9] = lookupProperty;
  		_state[10] = controllerListener;
  		return _state;  
  	}
	
}
