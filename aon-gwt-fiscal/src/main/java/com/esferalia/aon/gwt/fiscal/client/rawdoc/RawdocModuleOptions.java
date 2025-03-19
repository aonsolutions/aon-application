package com.esferalia.aon.gwt.fiscal.client.rawdoc;

import com.esferalia.aon.gwt.fiscal.client.ModuleOptions;
import com.esferalia.aon.occam.api.model.RawdocParams;

public class RawdocModuleOptions extends  ModuleOptions<RawdocModuleOptions> {

	private static final long serialVersionUID = -8823272010569243856L;
	
	private RawdocParams params;

	public RawdocParams getParams() {
		return params;
	}
	public RawdocModuleOptions setParams(RawdocParams params) {
		this.params = params;
		return this;
	}
	
}
