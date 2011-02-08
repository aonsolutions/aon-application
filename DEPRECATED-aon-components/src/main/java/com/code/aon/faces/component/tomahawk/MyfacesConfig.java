/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.code.aon.faces.component.tomahawk;

import javax.faces.context.ExternalContext;
import javax.servlet.ServletContext;

/**
 * Holds all configuration init parameters (from web.xml) that are independent
 * from the core implementation. The parameters in this class are available to
 * all shared, component and implementation classes.
 * See RuntimeConfig for configuration infos that come from the faces-config
 * files and are needed by the core implementation.
 *
 * MyfacesConfig is meant for components that implement some of the extended features
 * of MyFaces. Anyhow, using the MyFaces JSF implementation is no precondition for using
 * MyfacesConfig in custom components. Upon using another JSF implementation
 * (or omitting the extended init parameters) all config properties will simply have
 * their default values.
 *
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class MyfacesConfig
{
    private static final String APPLICATION_MAP_PARAM_NAME = MyfacesConfig.class.getName();

    public static final String  INIT_PARAM_RESOURCE_VIRTUAL_PATH = "org.apache.myfaces.RESOURCE_VIRTUAL_PATH";
    public static final String  INIT_PARAM_RESOURCE_VIRTUAL_PATH_DEFAULT = "/faces/myFacesExtensionResource";

    private static final String  INIT_PARAM_PRETTY_HTML = "org.apache.myfaces.PRETTY_HTML";
    private static final boolean INIT_PARAM_PRETTY_HTML_DEFAULT = true;

    private static final String  INIT_PARAM_ALLOW_JAVASCRIPT = "org.apache.myfaces.ALLOW_JAVASCRIPT";
    private static final boolean INIT_PARAM_ALLOW_JAVASCRIPT_DEFAULT = true;

    private static final String  INIT_PARAM_DETECT_JAVASCRIPT = "org.apache.myfaces.DETECT_JAVASCRIPT";
    private static final boolean INIT_PARAM_DETECT_JAVASCRIPT_DEFAULT = false;

    private static final String  INIT_PARAM_AUTO_SCROLL = "org.apache.myfaces.AUTO_SCROLL";
    private static final boolean INIT_PARAM_AUTO_SCROLL_DEFAULT = false;

    private static final String INIT_PARAM_ADD_RESOURCE_CLASS = "org.apache.myfaces.ADD_RESOURCE_CLASS";
    private static final String INIT_PARAM_ADD_RESOURCE_CLASS_DEFAULT = "org.apache.myfaces.renderkit.html.util.DefaultAddResource";

    private static final String  INIT_CHECK_EXTENSIONS_FILTER = "org.apache.myfaces.CHECK_EXTENSIONS_FILTER";
    private static final boolean INIT_CHECK_EXTENSIONS_FILTER_DEFAULT = true;

    private static final String INIT_READONLY_AS_DISABLED_FOR_SELECT = "org.apache.myfaces.READONLY_AS_DISABLED_FOR_SELECTS";
    private static final boolean INIT_READONLY_AS_DISABLED_FOR_SELECT_DEFAULT = true;

    
    private boolean _prettyHtml;
    private boolean _detectJavascript;
    private boolean _allowJavascript;
    private boolean _autoScroll;
    private String _addResourceClass;
    private String _resourceVirtualPath;
    private boolean _checkExtensionsFilter;
    private boolean _readonlyAsDisabledForSelect;

    private static final boolean TOMAHAWK_AVAILABLE;
    private static final boolean MYFACES_IMPL_AVAILABLE;
    private static final boolean RI_IMPL_AVAILABLE;
    
    static
    {
    	boolean tomahawkAvailable;
    	try
		{
			ClassUtils.classForName("org.apache.myfaces.webapp.filter.ExtensionsFilter");
			tomahawkAvailable = true;
		}
		catch (ClassNotFoundException e)
		{
			tomahawkAvailable = false;
		}
		TOMAHAWK_AVAILABLE = tomahawkAvailable;
    }

    static
    {
    	boolean myfacesImplAvailable;
    	try
		{
			ClassUtils.classForName("org.apache.myfaces.application.ApplicationImpl");
			myfacesImplAvailable = true;
		}
		catch (ClassNotFoundException e)
		{
			myfacesImplAvailable = false;
		}
		MYFACES_IMPL_AVAILABLE = myfacesImplAvailable;
    }

    static
    {
    	boolean riImplAvailable;
    	try
		{
			ClassUtils.classForName("com.sun.faces.application.ApplicationImpl");
			riImplAvailable = true;
		}
		catch (ClassNotFoundException e)
		{
			riImplAvailable = false;
		}
		RI_IMPL_AVAILABLE = riImplAvailable;
    }
    
    public static MyfacesConfig getCurrentInstance(ExternalContext extCtx)
    {
        MyfacesConfig myfacesConfig = (MyfacesConfig)extCtx
                                        .getApplicationMap().get(APPLICATION_MAP_PARAM_NAME);
        if (myfacesConfig != null) return myfacesConfig;

        myfacesConfig = new MyfacesConfig();
        extCtx.getApplicationMap().put(APPLICATION_MAP_PARAM_NAME, myfacesConfig);

        myfacesConfig.setPrettyHtml(getBooleanInitParameter(extCtx, INIT_PARAM_PRETTY_HTML,
                                                            INIT_PARAM_PRETTY_HTML_DEFAULT));
        myfacesConfig.setAllowJavascript(getBooleanInitParameter(extCtx, INIT_PARAM_ALLOW_JAVASCRIPT,
                                                                 INIT_PARAM_ALLOW_JAVASCRIPT_DEFAULT));

        myfacesConfig.setReadonlyAsDisabledForSelect(getBooleanInitParameter(extCtx, INIT_READONLY_AS_DISABLED_FOR_SELECT,
                                                                 INIT_READONLY_AS_DISABLED_FOR_SELECT_DEFAULT));

        if (TOMAHAWK_AVAILABLE)
        {
            myfacesConfig.setDetectJavascript(getBooleanInitParameter(extCtx, INIT_PARAM_DETECT_JAVASCRIPT,
                INIT_PARAM_DETECT_JAVASCRIPT_DEFAULT));
	        myfacesConfig.setAutoScroll(getBooleanInitParameter(extCtx, INIT_PARAM_AUTO_SCROLL,
	                INIT_PARAM_AUTO_SCROLL_DEFAULT));
	        myfacesConfig.setAddResourceClass(getStringInitParameter(extCtx, INIT_PARAM_ADD_RESOURCE_CLASS,
	                                                                 INIT_PARAM_ADD_RESOURCE_CLASS_DEFAULT));
            myfacesConfig.setResourceVirtualPath(getStringInitParameter(extCtx, INIT_PARAM_RESOURCE_VIRTUAL_PATH,
                    INIT_PARAM_RESOURCE_VIRTUAL_PATH_DEFAULT));

            myfacesConfig.setCheckExtensionsFilter(getBooleanInitParameter(extCtx, INIT_CHECK_EXTENSIONS_FILTER,
	        		INIT_CHECK_EXTENSIONS_FILTER_DEFAULT));
        }
        else
        {
        }

        if(RI_IMPL_AVAILABLE)
        {
        }

        if(MYFACES_IMPL_AVAILABLE)
        {
        }

        if(RI_IMPL_AVAILABLE && MYFACES_IMPL_AVAILABLE)
        {
        }
                
        return myfacesConfig;
    }

    private static boolean getBooleanInitParameter(ExternalContext externalContext,
                                                   String paramName,
                                                   boolean defaultValue)
    {
        String strValue = externalContext.getInitParameter(paramName);
        if (strValue == null)
        {
            return defaultValue;
        }
        else if (strValue.equalsIgnoreCase("true") || strValue.equalsIgnoreCase("on") || strValue.equalsIgnoreCase("yes"))
        {
            return true;
        }
        else if (strValue.equalsIgnoreCase("false") || strValue.equalsIgnoreCase("off") || strValue.equalsIgnoreCase("no"))
        {
            return false;
        }
        else
        {
            return defaultValue;
        }
    }

    private static String getStringInitParameter(ExternalContext externalContext,
                                                 String paramName,
                                                 String defaultValue)
    {
        String strValue = externalContext.getInitParameter(paramName);
        if (strValue == null)
        {
            return defaultValue;
        }
        else
        {
            return strValue;
        }
    }

     private void setResourceVirtualPath( String resourceVirtualPath )
     {
         this._resourceVirtualPath = resourceVirtualPath;
     }

     public String getResourceVirtualPath()
     {
         return this._resourceVirtualPath;
     }

    public boolean isPrettyHtml()
    {
        return _prettyHtml;
    }

    private void setPrettyHtml(boolean prettyHtml)
    {
        _prettyHtml = prettyHtml;
    }

    public boolean isDetectJavascript()
    {
        return _detectJavascript;
    }

    private void setDetectJavascript(boolean detectJavascript)
    {
        _detectJavascript = detectJavascript;
    }

    private void setReadonlyAsDisabledForSelect(boolean readonlyAsDisabledForSelect)
    {
        _readonlyAsDisabledForSelect = readonlyAsDisabledForSelect;
    }

    public boolean isReadonlyAsDisabledForSelect()
    {
        return _readonlyAsDisabledForSelect;
    }

    public boolean isTomahawkAvailable()
    {
        return TOMAHAWK_AVAILABLE;
    }

    public boolean isMyfacesImplAvailable()
    {
        return MYFACES_IMPL_AVAILABLE;
    }

    public boolean isRiImplAvailable()
    {
        return RI_IMPL_AVAILABLE;
    }

    /**
     * Do not use this in renderers directly!
     * You should use {@link org.apache.myfaces.shared_tomahawk.renderkit.html.util.JavascriptUtils#isJavascriptAllowed}
     * to determine if javascript is allowed or not.
     */
    public boolean isAllowJavascript()
    {
        return _allowJavascript;
    }

    private void setAllowJavascript(boolean allowJavascript)
    {
        _allowJavascript = allowJavascript;
    }

    public boolean isAutoScroll()
    {
        return _autoScroll;
    }

    private void setAutoScroll(boolean autoScroll)
    {
        _autoScroll = autoScroll;
    }

    private void setAddResourceClass(String addResourceClass)
    {
        _addResourceClass = addResourceClass;
    }

    public String getAddResourceClass()
    {
        return _addResourceClass;
    }

    /**
     * ExtensionFilter needs access to AddResourceClass init param without having
     * an ExternalContext at hand.
     */
    public static String getAddResourceClassFromServletContext(ServletContext servletContext)
    {
        String addResourceClass = servletContext.getInitParameter(INIT_PARAM_ADD_RESOURCE_CLASS);
        if (addResourceClass == null)
        {
            return INIT_PARAM_ADD_RESOURCE_CLASS_DEFAULT;
        }
        else
        {
            return addResourceClass;
        }
    }

    /**
     * Should the environment be checked so that the ExtensionsFilter will work properly. 
     */
	public boolean isCheckExtensionsFilter()
	{
		return _checkExtensionsFilter;
	}

	public void setCheckExtensionsFilter(boolean extensionsFilter)
	{
		_checkExtensionsFilter = extensionsFilter;
	}
}
