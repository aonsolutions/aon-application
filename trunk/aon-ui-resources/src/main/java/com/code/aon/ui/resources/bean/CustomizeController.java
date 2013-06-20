package com.code.aon.ui.resources.bean;

import static com.code.aon.ui.common.ICommonConstants.HEADER_LOGO_NAME;
import static com.code.aon.ui.common.ICommonConstants.STATUS_FAILED_NAME;
import static com.code.aon.ui.common.ICommonConstants.STATUS_START_NAME;
import static com.code.aon.ui.common.ICommonConstants.STATUS_STOP_NAME;
import static com.code.aon.ui.common.ICommonConstants.TOOLBAR_LOGO_NAME;

import java.sql.Connection;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;

import com.code.aon.ui.util.AonUtil;

public class CustomizeController extends CustomizeBean {

	private static final String RESOURCE_RESOLVER = "aonResource";

	private static final String HEADER_LOGO_DEFAULT = "/images/aon-header/aon-solutions.png";

	private static final String TOOLBAR_LOGO_DEFAULT = "/images/aon-icon/aon-icon-logo.png";
	
	private static final String STATUS_START_DEFAULT = "/images/aon-header/aon-outputConnectionStatus-start.gif";

	private static final String STATUS_STOP_DEFAULT = "/images/aon-header/aon-outputConnectionStatus-stop.png";
	
	private static final String STATUS_FAILED_DEFAULT = "/images/aon-header/aon-outputConnectionStatus-failed.png";
	
	private String headerLogo;
	
	private String toolbarLogo;
	
	private String statusStartStyle;
	
	private String statusStopStyle;
	
	private String statusFailedStyle;
	
	public CustomizeController() {
		initMessages(AonUtil.getCurrentLocale());
		ResourceResolver resolver = (ResourceResolver) AonUtil.getRegisteredBean(RESOURCE_RESOLVER);
		initResources(resolver);
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		initApplicationVersion(ec.getResourceAsStream("META-INF/MANIFEST.MF"));
		init(AonUtil.getServerName());
	}
	
	@Override
	protected void initResources(ResourceResolver resolver) {
		super.initResources(resolver);
		this.headerLogo = resolver.getResolve().get(HEADER_LOGO_DEFAULT);
		this.toolbarLogo = resolver.getResolve().get(TOOLBAR_LOGO_DEFAULT);
		String start = resolver.getResolve().get(STATUS_START_DEFAULT);
		this.statusStartStyle = getBackgroundImageStyle(start);
		String stop = resolver.getResolve().get(STATUS_STOP_DEFAULT);
		this.statusStopStyle = getBackgroundImageStyle(stop);
		String failed = resolver.getResolve().get(STATUS_FAILED_DEFAULT);
		this.statusFailedStyle = getBackgroundImageStyle(failed);
	}

	private String getBackgroundImageStyle( String url ) {
		return "background-image: url(" + url + ");";
	}

	private String getStatusStyle( Connection connection, String name, String _default ) {
		String ref = getImageRef(connection, name);
		if (! StringUtils.isEmpty(ref) ) {
			return getBackgroundImageStyle(ref);	
		}
		return _default;
	}
	
	@Override
	protected void loadValues( Connection connection ) {
		super.loadValues(connection);
		this.headerLogo = StringUtils.defaultIfEmpty(getImageRef(connection, HEADER_LOGO_NAME), this.headerLogo);
		this.toolbarLogo = StringUtils.defaultIfEmpty(getImageRef(connection, TOOLBAR_LOGO_NAME), this.toolbarLogo);
		this.statusStartStyle = getStatusStyle(connection, STATUS_START_NAME, this.statusStartStyle);
		this.statusStopStyle = getStatusStyle(connection, STATUS_STOP_NAME, this.statusStopStyle);
		this.statusFailedStyle = getStatusStyle(connection, STATUS_FAILED_NAME, this.statusFailedStyle);
	}

	public String getHeaderLogo() {
		return headerLogo;
	}

	public String getToolbarLogo() {
		return toolbarLogo;
	}

	public String getStatusStartStyle() {
		return statusStartStyle;
	}

	public String getStatusStopStyle() {
		return statusStopStyle;
	}

	public String getStatusFailedStyle() {
		return statusFailedStyle;
	}
	
}