package com.code.aon.faces.component.richfaces.lookup.inputText;

import static com.code.aon.faces.component.richfaces.IRichFacesTags.MIN_HEIGHT;
import static com.code.aon.faces.component.richfaces.IRichFacesTags.MIN_WIDTH;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jakarta.el.ValueExpression;
import javax.faces.context.FacesContext;

import com.code.aon.faces.component.richfaces.lookup.HtmlLookupBasicInput;
import com.code.aon.faces.component.richfaces.lookup.ILookupWindowComponent;

public class HtmlLookupInputText extends HtmlLookupBasicInput implements ILookupWindowComponent {

	/** The map of join value bindings. */
	private List<JoinProperty> joinProperties;
	
    private String selectReRender;
    
    private String windowCloseFocus;

    private String windowTitle;
    
    private String minWidth;
    
    private String minHeight;
    
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
	
	public String getSelectReRender() {
		return selectReRender;
	}

	public void setSelectReRender(String selectReRender) {
		this.selectReRender = selectReRender;
	}

	public String getWindowCloseFocus() {
		return windowCloseFocus;
	}

	public void setWindowCloseFocus(String windowCloseFocus) {
		this.windowCloseFocus = windowCloseFocus;
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
  		windowCloseFocus = (String) this._state[2];
  		selectReRender = (String) this._state[3];
  		windowTitle = (String) this._state[4];
  		minWidth = (String) this._state[5];
  		minHeight = (String) this._state[6];
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
  		_state[1] = joinProperties;
  		_state[2] = windowCloseFocus;
  		_state[3] = selectReRender;
  		_state[4] = windowTitle;
  		_state[5] = minWidth;
  		_state[6] = minHeight;
  		
  		return _state;  
  	}
	
}
