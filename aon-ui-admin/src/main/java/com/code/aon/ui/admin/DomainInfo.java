package com.code.aon.ui.admin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.enumeration.Module;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.registry.controller.DocumentManager;
import com.code.aon.ui.util.AonUtil;

public class DomainInfo {

	private static final Logger LOGGER = LoggerFactory.getLogger(DomainInfo.class);
	
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
	
	private static final String MAX_TOTAL_DOCUMENT_SIZE = "maxTotalDocumentSize";

	private static final String DOMAIN_MANAGEMENT = "domainManagement";

	private static final String MODULES = "modules";

	private static final String NUMBER_OF_USERS = "numberOfUsers";

	private static final String TYPE = "type";
	
	private static final String USER = "user";
	
	private DomainType type;
	
	private String user;
	
	private Integer numberOfUsers;
	
	private Integer maxTotalDocumentSize;
	
	private boolean domainManagement;

	private List<Module> modules;
	
	private Date date;
	
	public DomainInfo() {
		type = DomainType.ENTERPRISE;
		numberOfUsers = 0;
		maxTotalDocumentSize = DocumentManager.MINIMUM_MAX_TOTAL_DOCUMENT_SIZE;
		modules = Collections.emptyList();
	}
	
	public DomainInfo( byte[] data  ) {
		this();
		Properties properties = new Properties();
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		try {
			properties.load(bis);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		this.user = properties.getProperty(USER);
		String typeValue = properties.getProperty(TYPE);
		if (! StringUtils.isEmpty(typeValue) ) {
			this.type = DomainType.valueOf(typeValue);
		}
		String domainManagementValue = properties.getProperty(DOMAIN_MANAGEMENT);
		if (! StringUtils.isEmpty(domainManagementValue) ) {
			this.domainManagement = Boolean.valueOf(domainManagementValue);
		}
		String numberOfUserValue = properties.getProperty(NUMBER_OF_USERS);
		if ( NumberUtils.isDigits(numberOfUserValue) ) {
			this.numberOfUsers = NumberUtils.toInt(numberOfUserValue);
		}
		String maxTotalDocumentSizeValue = properties.getProperty(MAX_TOTAL_DOCUMENT_SIZE);
		if ( NumberUtils.isDigits(maxTotalDocumentSizeValue) ) {
			this.maxTotalDocumentSize = NumberUtils.toInt(maxTotalDocumentSizeValue);
		}
		String modulesValue = properties.getProperty(MODULES);
		if (! StringUtils.isEmpty(modulesValue) ) {
			this.modules = new LinkedList<Module>();
			for( String value : StringUtils.split(modulesValue) ) {
				this.modules.add(Module.valueOf(value));
			}
		}
	}

	public DomainType getType() {
		return type;
	}

	public void setType(DomainType type) {
		this.type = type;
	}

	public Integer getNumberOfUsers() {
		return numberOfUsers;
	}

	public void setNumberOfUsers(Integer numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}

	public Integer getMaxTotalDocumentSize() {
		return maxTotalDocumentSize;
	}

	public void setMaxTotalDocumentSize(Integer maxTotalDocumentSize) {
		this.maxTotalDocumentSize = maxTotalDocumentSize;
	}

	public boolean isDomainManagement() {
		return domainManagement;
	}

	public void setDomainManagement(boolean domainManagement) {
		this.domainManagement = domainManagement;
	}

	public boolean[] getModuleArray() {
		boolean[] array = new boolean[Module.values().length];
		for( int i = 0; i < array.length; i++ ) {
			array[i] = this.modules.contains(Module.values()[i]);
		}
		return array;
	}

	public String getModuleList() {
		Set<String> modules = new TreeSet<String>();
		Locale locale = AonUtil.getCurrentLocale();
		for( Module module : this.modules ) {
			modules.add( module.getName(locale) );
		}
		return StringUtils.join(modules, ", ");
	}	
	
	public List<Module> getModules() {
		return modules;
	}

	public void setModules(List<Module> modules) {
		this.modules = modules;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	private void diff( StringBuffer sb, String message, Object oldValue, Object newValue ) {
		diff( sb, message, oldValue + " -> "+ newValue );
	}

	private void diff( StringBuffer sb, String message, String differences ) {
		if ( sb.length() > 0 ) {
			sb.append(", ");
		}
		sb.append( AonUtil.getMessage(message) ).append(": ").append( differences );
		sb.append(IOUtils.LINE_SEPARATOR);
	}
	
	private void diff( StringBuffer sb, char sign, List<Module> list1, List<Module> list2 ) {
		Locale locale = AonUtil.getCurrentLocale();
		for( Module module : list1 ) {
			if (! list2.contains(module) ) {
				if ( sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(sign).append(module.getName(locale));
			}
		}				
	}
	
	private void diffList( StringBuffer sb, String message, List<Module> list1, List<Module> list2 ) {
		StringBuffer differences = new StringBuffer();
		diff( differences, '-', list1, list2 );
		diff( differences, '+', list2, list1 );
		diff(sb, message, differences.toString());
	}
	
	public String getDifferences( DomainInfo di ) {
		StringBuffer sb = new StringBuffer();	
		if ( getType() != di.getType() ) {
			Locale locale = AonUtil.getCurrentLocale();
			diff( sb, ICommonMessages.DOMAIN_TYPE, getType().getName(locale), di.getType().getName(locale) );
		}
		if (! ObjectUtils.equals(getNumberOfUsers(), di.getNumberOfUsers()) ) {
			diff( sb, ICommonMessages.DOMAIN_MAX_DEFINED_USERS, getNumberOfUsers(), di.getNumberOfUsers() );
		}
		if (! ObjectUtils.equals(getMaxTotalDocumentSize(), di.getMaxTotalDocumentSize()) ) {
			diff( sb, ICommonMessages.DOMAIN_MAX_TOTAL_DOCUMENT_SIZE, getMaxTotalDocumentSize(), di.getMaxTotalDocumentSize() );
		}
		if ( isDomainManagement() != di.isDomainManagement() ) {
			diff( sb, ICommonMessages.DOMAIN_DOMAIN_MANAGEMENT, isDomainManagement(), di.isDomainManagement() );
		}
		if (! Arrays.equals(getModuleArray(), di.getModuleArray()) ) {
			diffList( sb, ICommonMessages.DOMAIN_MODULES, getModules(), di.getModules() );
		}
		return sb.toString();
	}
	
	public byte[] getData() {
		Properties properties = new Properties();
		properties.setProperty(TYPE, type.toString());
		properties.setProperty(NUMBER_OF_USERS, numberOfUsers.toString());
		properties.setProperty(MAX_TOTAL_DOCUMENT_SIZE, maxTotalDocumentSize.toString());
		properties.setProperty(DOMAIN_MANAGEMENT, Boolean.toString(domainManagement));
		properties.setProperty(USER, user);
		String modulesValue = StringUtils.join(modules, " ");
		properties.setProperty(MODULES, modulesValue);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			properties.store(bos, "");
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return bos.toByteArray();
	}

	public static DomainInfo getDomainInfo( RegistryAttachment ra ) {
		DomainInfo di = new DomainInfo(ra.getData());
		if (! StringUtils.isEmpty(ra.getDescription()) ) {
			try {
				di.setDate(DomainInfo.DATE_FORMAT.parse(ra.getDescription()));
			} catch (ParseException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		if ( di.getDate() == null ) {
			di.setDate(ra.getAttachDate());	
		}
		return di;
	}
	
}