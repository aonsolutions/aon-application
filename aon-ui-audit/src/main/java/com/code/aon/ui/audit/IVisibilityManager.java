package com.code.aon.ui.audit;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.code.aon.audit.IAction;
import com.code.aon.audit.ProfileActionDenied;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.config.User;

public interface IVisibilityManager {
	
	boolean isRenderAccountingModule();
	
	boolean isRenderDocumentModule();
	
	boolean isRenderPayrollConfig();
	
	boolean isDeniedModule( String name );
	
	boolean isDenied( ApplicationOption option );
	
	boolean isDenied( String action );

	Collection<ApplicationOption> getDeniedOptions();
	
	List<Module> getVisibleModules();
	
	Map<String,IAction> getUserDeniedActions( User user );
	
	Map<String,ProfileActionDenied> getProfileDeniedActions( User user );
	
	List<ApplicationOption> getOptions( Map<String,? extends IAction> deniedActions, Map<String,ApplicationCategory> deniedModules );
	
	Set<Module> getEnabledModules( User user, boolean addExtraModules );
	
	Map<String, ApplicationCategory> getDeniedModules( User user, boolean addExtraModules );
	
	void enableOnly( String[] categories, String[] groups, String ... disableOptionIds );
	
}