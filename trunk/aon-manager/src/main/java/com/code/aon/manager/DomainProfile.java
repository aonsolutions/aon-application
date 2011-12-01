/*
 * Created on 10-mar-2005
 *
 */
package com.code.aon.manager;

import static com.code.aon.ldap.IAonObjectClasses.DOMAIN_APPLICATION_PROFILE;
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
@EntryObject(mainObjectClass=DOMAIN_APPLICATION_PROFILE, objectClasses={TOP, GROUP_OF_NAMES,PROFILE})
public class DomainProfile extends BasicProfile {

	private static final long serialVersionUID = 4862547728621636239L;

}
