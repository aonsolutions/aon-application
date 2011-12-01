package com.code.aon.faces.component.richfaces.lookup.suggestText;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import com.code.aon.faces.component.richfaces.lookup.HtmlLookupBasicInput;

public class HtmlLookupSuggestText extends HtmlLookupBasicInput {

    private String suggestAlias;
    
    private Boolean matchBeginOnly;    
    
	private Object[] _state;    

	public String getSuggestAlias() {
    	if (null != this.suggestAlias) {
            return this.suggestAlias;
        }
    	ValueExpression _vb = getValueExpression(SUGGEST_ALIAS);
        return ((_vb != null) ? (String)_vb.getValue(getFacesContext().getELContext()) : null);
	}	

	public void setSuggestAlias(String suggestAlias) {
		this.suggestAlias = suggestAlias;
	}

	public Boolean getMatchBeginOnly() {
    	if (null != this.matchBeginOnly) {
            return this.matchBeginOnly;
        }
    	ValueExpression _vb = getValueExpression(MATCH_BEGIN_ONLY);
        return ((_vb != null) ? (Boolean)_vb.getValue(getFacesContext().getELContext()) : Boolean.FALSE);
	}	

	public void setMatchBeginOnly(Boolean matchBeginOnly) {
		this.matchBeginOnly = matchBeginOnly;
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
  		suggestAlias = (String) this._state[1];
  		matchBeginOnly = (Boolean) this._state[2];
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
  			_state = new Object[3];  
  		}  
  		_state[0] = super.saveState(_context);  
  		_state[1] = suggestAlias;
  		_state[2] = matchBeginOnly;
  		
  		return _state;  
  	}
	
}
