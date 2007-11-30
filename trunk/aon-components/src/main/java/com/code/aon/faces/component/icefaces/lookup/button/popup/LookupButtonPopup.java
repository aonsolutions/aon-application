package com.code.aon.faces.component.icefaces.lookup.button.popup;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.code.aon.faces.component.icefaces.lookup.ILookupTags;
import com.code.aon.faces.component.icefaces.lookup.button.LookupButtonType;

public class LookupButtonPopup extends UIComponentBase implements ILookupTags {

	public static final String COMPONENT_TYPE = "com.code.aon.faces.LookupButtonPopup";

	public static final String DEFAULT_RENDERER_TYPE = "com.code.aon.faces.LookupButtonPopup";

	public static final String COMPONENT_FAMILY = "com.code.aon.faces.LookupButtonPopupFamily";

	private ValueBinding lookup;

	private Object[] _state;

	public LookupButtonPopup() {
		super.setRendererType(DEFAULT_RENDERER_TYPE);
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
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

	/**
	 * <p>
	 * Gets the state of the instance as a <code>Serializable</code> Object.
	 * </p>
	 * 
	 * @param context
	 * @return Object values[]
	 */
	public void restoreState(FacesContext context, Object value) {
		this._state = (Object[]) value;
		super.restoreState(context, this._state[0]);
		lookup = (ValueBinding) this._state[1];
	}

	/**
	 * <p>
	 * Gets the state of the instance as a <code>Serializable</code> Object.
	 * </p>
	 * 
	 * @param context
	 * @return Object values[]
	 */
	public Object saveState(FacesContext _context) {
		if (_state == null) {
			_state = new Object[2];
		}
		_state[0] = super.saveState(_context);
		_state[1] = lookup;
		return _state;
	}

}
