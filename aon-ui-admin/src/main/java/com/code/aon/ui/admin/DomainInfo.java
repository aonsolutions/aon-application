package com.code.aon.ui.admin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
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
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.config.Domain;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.registry.controller.DocumentManager;
import com.code.aon.ui.util.AonUtil;

public class DomainInfo implements Serializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(DomainInfo.class);
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
	
	private static final String MAX_TOTAL_DOCUMENT_SIZE = "maxTotalDocumentSize";

	private static final String DOMAIN_MANAGEMENT = "domainManagement";

	private static final String MODULES = "modules";
	
	private static final String DISPLAY_MODULES = "displayModules";

	private static final String NUMBER_OF_USERS = "numberOfUsers";

	private static final String TYPE = "type";
	
	private static final String USER = "user";
	
	private static final String TEDI_CENTER = "tediCenter";
	
	private static final String NAME = "name";
	
	private static final String PARENT = "parent";
	
	private static final String AUTO_UPDATE = "autoUpdate";
	
	private static final String PAYER = "payer";
	
	private static final String COMMERCIAL = "commercial";
	
	private String name;
	
	private DomainInfoType infoType;
	
	private String parent;
	
	private String payer;
	
	private DomainType type;
	
	private String user;
	
	private Integer numberOfUsers;
	
	private Integer maxTotalDocumentSize;
	
	private boolean domainManagement;

	private List<Module> bookinModules;
	
	private List<Module> displayModules;
	
	private Date date;
	
	private boolean tediCenter;
	
	private boolean autoUpdate;
	
	public DomainInfo() {
		type = DomainType.ENTERPRISE;
		infoType = DomainInfoType.MODIFICATION;
		numberOfUsers = 0;
		maxTotalDocumentSize = DocumentManager.MINIMUM_MAX_TOTAL_DOCUMENT_SIZE;
		bookinModules = Collections.emptyList();
		displayModules = Collections.emptyList();
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
		String nameValue = properties.getProperty(NAME);
		if (! StringUtils.isEmpty(nameValue) ) {
			this.name = StringUtils.trimToNull(nameValue);
		}
		String parentValue = properties.getProperty(PARENT);
		if (! StringUtils.isEmpty(parentValue) ) {
			this.parent = StringUtils.trimToNull(parentValue);
		}
		String payerValue = properties.getProperty(PAYER);
		if (! StringUtils.isEmpty(payerValue) ) {
			this.payer = StringUtils.trimToNull(payerValue);
		}
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
		String bookingModulesValue = properties.getProperty(MODULES);
		if (! StringUtils.isEmpty(bookingModulesValue) ) {
			this.bookinModules = new LinkedList<Module>();
			for( String value : StringUtils.split(bookingModulesValue) ) {
				this.bookinModules.add(value.equalsIgnoreCase(COMMERCIAL) ? Module.CRM : Module.valueOf(value));
			}
		}
		String displayModulesValue = properties.getProperty(DISPLAY_MODULES);
		if (! StringUtils.isEmpty(displayModulesValue) ) {
			this.displayModules = new LinkedList<Module>();
			for( String value : StringUtils.split(displayModulesValue) ) {
				this.displayModules.add(value.equalsIgnoreCase(COMMERCIAL) ? Module.CRM : Module.valueOf(value));
			}
		}

		String tediCenterValue = properties.getProperty(TEDI_CENTER);
		if(BooleanUtils.toBoolean(tediCenterValue)) {
			this.tediCenter = true;
		}
		
		String autoUpdateValue = properties.getProperty(AUTO_UPDATE);
		if ( BooleanUtils.toBoolean(autoUpdateValue) ) {
			this.autoUpdate = true;
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
			array[i] = this.bookinModules.contains(Module.values()[i]);
		}
		return array;
	}

	public String getModuleList() {
		Set<String> modules = new TreeSet<String>();
		Locale locale = AonUtil.getCurrentLocale();
		for( Module module : this.bookinModules ) {
			String name = null;
			if ( isDomainManagement() && (module == Module.PAYROLL_PORTAL) ) {
				name = AonUtil.getMessage(ICommonMessages.ADMIN_GLOBAL_PORTAL_ACCESS);
			} else {
				name = module.getName(locale);
			}
			modules.add( name );
		}
		for( Module module : this.displayModules ) {
			modules.add( module.getName(locale) );
		}
		return StringUtils.join(modules, ", ");
	}	
	
	public List<Module> getBookingModules() {
		return bookinModules;
	}

	private List<Module> getModules(List<DomainModuleInfo> moduleInfos) {
		List<Module> list = new LinkedList<Module>();
		for( DomainModuleInfo dim : moduleInfos ) {
			if ( dim.isChecked() ) {
				list.add(dim.getModule());	
			}
		}
		return list;
	}
	
	public void setBookingModules(List<DomainModuleInfo> moduleInfos) {
		this.bookinModules = getModules(moduleInfos);
	}
	
	public List<Module> getDisplayModules() {
		return displayModules;
	}

	public void setDisplayModules(List<DomainModuleInfo> moduleInfos) {
		this.displayModules = getModules(moduleInfos);
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
	
	public boolean isTediCenter() {
		return tediCenter;
	} 
	
	public void setTediCenter(boolean tediCenter) {
		this.tediCenter = tediCenter;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}
	
	public void setInfoType(DomainInfoType infoType) {
		this.infoType = infoType;
	}

	public DomainInfoType getInfoType() {
		return infoType;
	}
	
	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}
	
	public String getPayer() {
		return payer;
	}

	public void setPayer(String payer) {
		this.payer = payer;
	}

	private void diff( StringBuffer sb, String message, boolean newValue ) {
		diff( sb, message, newValue ? "+" : "-" );
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
			diffList( sb, ICommonMessages.DOMAIN_MODULES, getBookingModules(), di.getBookingModules() );
		}
		if (! StringUtils.equals(getPayer(), di.getPayer()) ) {
			diff( sb, ICommonMessages.PAYER_DOMAIN, (di.getPayer()!=null?di.getPayer():"-") );
		} 
		if(isTediCenter() != di.isTediCenter()) {
			diff(sb, ICommonMessages.EXTERNAL_TEDI_CENTER, di.isTediCenter());
		}
		return sb.toString();
	}
	
	public byte[] getData() {
		Properties properties = new Properties();
		if (! StringUtils.isEmpty(name) ) {
			properties.setProperty(NAME, name);	
		}
		if (! StringUtils.isEmpty(parent) ) {
			properties.setProperty(PARENT, parent);	
		}
		if (! StringUtils.isEmpty(payer) ) {
			properties.setProperty(PAYER, payer);	
		}
		if ( type != null ) {
			properties.setProperty(TYPE, type.toString());	
		}		
		if ( numberOfUsers != null ) {
			properties.setProperty(NUMBER_OF_USERS, numberOfUsers.toString());	
		}
		if ( maxTotalDocumentSize != null ) {
			properties.setProperty(MAX_TOTAL_DOCUMENT_SIZE, maxTotalDocumentSize.toString());	
		}
		properties.setProperty(DOMAIN_MANAGEMENT, Boolean.toString(domainManagement));
		if (! StringUtils.isEmpty(user)) {
			properties.setProperty(USER, user);	
		}
		if (! bookinModules.isEmpty() ) {
			String modulesValue = StringUtils.join(bookinModules, " ");
			properties.setProperty(MODULES, modulesValue);			
		}
		if (! displayModules.isEmpty() ) {
			String modulesValue = StringUtils.join(displayModules, " ");
			properties.setProperty(DISPLAY_MODULES, modulesValue);			
		}
		if( tediCenter ) {
			properties.setProperty(TEDI_CENTER, Boolean.TRUE.toString());
		}
		if ( autoUpdate ) {
			properties.setProperty(AUTO_UPDATE, Boolean.TRUE.toString());
		}
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
		if ( ra.getCreationDate() != null ) {
			di.setDate( ra.getCreationDate() );
		} else {
			if ( NumberUtils.isDigits(ra.getDescription()) ) {
				try {
					di.setDate(DomainInfo.DATE_FORMAT.parse(ra.getDescription()));
				} catch (ParseException e) {
					LOGGER.error(e.getMessage(), e);
				}				
			}
		}
		if ( di.getDate() == null ) {
			di.setDate(ra.getAttachDate());	
		}
		if ( ra.getRegistryAttachmentType() == RegistryAttachmentType.DOMAIN_INSERT_HISTORY ) {
			di.setInfoType(DomainInfoType.INSERT);
		} else if ( ra.getRegistryAttachmentType() == RegistryAttachmentType.DOMAIN_REMOVE_HISTORY ) {
			di.setInfoType(DomainInfoType.REMOVE);
		}
		return di;
	}
	
	public static DomainInfo getDomainInfo( Domain domain, BookingInfo bookingInfo ) {
		DomainInfo di = new DomainInfo();
		di.setUser(AonUtil.getAuthPrincipal().getShortName());
		di.setName(domain.getName());
		Domain parent = domain.getParent();
		if ( parent != null && parent.getId() != null ) {
			di.setParent(parent.getName());
		}
		Domain payer = bookingInfo.getPayerDomain();
		if ( payer != null && payer.getId() != null ) {
			di.setPayer(payer.getName());
		}
		di.setType(domain.getType());
		di.setNumberOfUsers(domain.getMaxDefinedUsers());
		di.setMaxTotalDocumentSize(domain.getMaxTotalDocumentSize());
		di.setDomainManagement(domain.isDomainManagement());
		di.setBookingModules(bookingInfo.getBookingModules());
		di.setDisplayModules(bookingInfo.getDisplayModules());
		di.setTediCenter(bookingInfo.isTediCenter());
		return di;
	}
	
	
}