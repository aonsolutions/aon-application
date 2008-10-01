package com.code.aon.ui.form;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Controller used to handle the request.
 */
public class RequestController {
	
	/** Constant EXCLUDE_PARAMETERS_STRING_ARRAY. */
	private static final String[] EXCLUDE_PARAMETERS_STRING_ARRAY = {
		"_SUBMIT"
		,"_link_hidden_"
		,"jsf_tree_64"
		,"jsf_viewid"
		,"jsf_state_64"
	};
	
	/**
	 * Gets the request parameters.
	 * 
	 * @return the request parameters
	 */
	public List<RequestParameter> getRequestParameters() {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		Map parameters = ec.getRequestParameterMap();
		Set names = parameters.keySet();
		List<RequestParameter> list = new LinkedList<RequestParameter>();
		if (names != null && !names.isEmpty()) {
			Iterator iter = names.iterator();
			while (iter.hasNext()) {
				String name = (String) iter.next();
				if (!isSystemParameter(name)) {
					String value = (String) parameters.get(name);
					RequestParameter rp = new RequestParameter();
					rp.setName(name);
			        if ( isValueReference(value) ) {
			            FacesContext facesContext = FacesContext.getCurrentInstance();
			            ValueBinding vb = facesContext.getApplication().createValueBinding(value);
			            value = (String) vb.getValue(facesContext);
			        }
					rp.setValue(value);
					list.add(rp);
				}
			}
		}
		return list;
	}
	
	
	/**
	 * Checks if is value reference.
	 * 
	 * @param value the value
	 * 
	 * @return true, if is value reference
	 */
	private boolean isValueReference(String value) {
        return value.indexOf("#{") != -1 && value.indexOf("#{") < value.indexOf('}');
	}


	/**
	 * Checks if is system parameter.
	 * 
	 * @param name the name
	 * 
	 * @return true, if is system parameter
	 */
	private boolean isSystemParameter(String name) {
		for (String token:EXCLUDE_PARAMETERS_STRING_ARRAY) {
			if (name.indexOf( token ) > -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the BIRT report parameters.
	 * 
	 * @return the BIRT report parameters
	 */
	public List<RequestParameter> getBIRTReportParameters() {
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		Map parameters = ctx.getRequestParameterMap();
		Set names = parameters.keySet();
		List<RequestParameter> list = new LinkedList<RequestParameter>();
		if (names != null && !names.isEmpty()) {
			Iterator iter = names.iterator();
			while (iter.hasNext()) {
				String name = (String) iter.next();
				String value = (String) parameters.get(name);
				if (name.startsWith("__")) {
					RequestParameter rp = new RequestParameter();
					rp.setName(name);
					rp.setValue(value);
					list.add(rp);
				}
			}
		}
		return list;
	}

	/**
	 * RequestParameter represents a parameter within the request.
	 */
	public class RequestParameter {
		
		/** Name. */
		private String name;

		/** Value. */
		private String value;

		/**
		 * Gets the name.
		 * 
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Sets the name.
		 * 
		 * @param name the name
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Gets the value.
		 * 
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * Sets the value.
		 * 
		 * @param value the value
		 */
		public void setValue(String value) {
			this.value = value;
		}

	}
}

