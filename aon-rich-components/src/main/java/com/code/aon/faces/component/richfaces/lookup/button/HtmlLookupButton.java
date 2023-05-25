package com.code.aon.faces.component.richfaces.lookup.button;

import static com.code.aon.faces.component.richfaces.IRichFacesTags.MIN_HEIGHT;
import static com.code.aon.faces.component.richfaces.IRichFacesTags.MIN_WIDTH;
import static com.code.aon.faces.component.richfaces.IRichFacesTags.PROPERTY;

import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;
import javax.faces.context.FacesContext;

import org.ajax4jsf.component.html.HtmlAjaxCommandButton;

import com.code.aon.faces.component.richfaces.lookup.ILookupConstants;
import com.code.aon.faces.component.richfaces.lookup.ILookupWindowComponent;
import com.code.aon.faces.controller.RichLookupBean;
import com.code.aon.ui.form.event.IControllerListener;

public class HtmlLookupButton extends HtmlAjaxCommandButton implements ILookupConstants, ILookupWindowComponent {

    /**
     * String constant component type
     */
    public static final String COMPONENT_TYPE =
            "com.code.aon.faces.HtmlLookupButton";
    
    private LookupButtonType buttonType;
    
    private ValueExpression property;
    
    private IControllerListener controllerListener;
    
    private RichLookupBean lookup;
    
    private String lookupProperty;
    
    private MethodExpression lookupChangeListener;
    
    private String windowTitle;
    
    private String selectReRender;
    
    private String minWidth;
    
    private String minHeight;
    
    private String windowCloseFocus;
    
    private MethodExpression lookupAction;
    
	private Object[] _state;    
	
	public LookupButtonType getButtonType() {
		return buttonType;
	}

	public void setButtonType(LookupButtonType buttonType) {
		this.buttonType = buttonType;
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
	
	public MethodExpression getLookupAction() {
		return lookupAction;
	}

	public void setLookupAction(MethodExpression lookupAction) {
		this.lookupAction = lookupAction;
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
	
	public String getWindowCloseFocus() {
    	if (null != this.windowCloseFocus) {
            return this.windowCloseFocus;
        }
    	ValueExpression _vb = getValueExpression(WINDOW_CLOSE_FOCUS);
        return ((_vb != null) ? (String)_vb.getValue(getFacesContext().getELContext()) : null);
	}

	public void setWindowCloseFocus(String windowCloseFocus) {
		this.windowCloseFocus = windowCloseFocus;
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
  		windowTitle = (String) this._state[4];
  		selectReRender = (String) this._state[5];
  		minWidth = (String) this._state[6];
  		minHeight = (String) this._state[7];
  		lookupProperty = (String) this._state[8];
  		controllerListener = (IControllerListener) this._state[9];
  		windowCloseFocus = (String) this._state[10];
  		buttonType = (LookupButtonType) this._state[11];
  		lookupAction = (MethodExpression) this._state[12];
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
  			_state = new Object[13];  
  		}  
  		_state[0] = super.saveState(_context);  
  		_state[1] = lookup;
  		_state[2] = property;
  		_state[3] = lookupChangeListener;  
  		_state[4] = windowTitle;
  		_state[5] = selectReRender;
  		_state[6] = minWidth;
  		_state[7] = minHeight;
  		_state[8] = lookupProperty;
  		_state[9] = controllerListener;
  		_state[10] = windowCloseFocus;
  		_state[11] = buttonType;
  		_state[12] = lookupAction;
  		return _state;  
  	}
	
}
