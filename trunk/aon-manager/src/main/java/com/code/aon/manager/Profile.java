/*
 * Created on 10-mar-2005
 *
 */
package com.code.aon.manager;

import static com.code.aon.ldap.IAonObjectClasses.GROUP_OF_NAMES;
import static com.code.aon.ldap.IAonObjectClasses.PROFILE;
import static com.code.aon.ldap.IAonObjectClasses.TOP;

import com.code.aon.dao.ldap.annotations.EntryObject;

/**
 * 
 * @author Consulting & Development. Aimar Tellitu - 10-mar-20059
 * @since 1.0
 *
 */
@EntryObject(mainObjectClass=PROFILE, objectClasses={TOP, GROUP_OF_NAMES})
public class Profile extends BasicProfile {

	private static final long serialVersionUID = 6154797870187401429L;

}
