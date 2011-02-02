package com.code.aon.common.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ClassUtils;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.ComponentType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

import com.code.aon.common.dao.hibernate.HibernateUtil;

/**
 * This Class manages operations of IDAO constants.
 * 
 * @author Consulting & Development.
 * 
 */
public class DAOConstantsResolver {

	private Configuration configuration;

	/**
	 * The Constructor.
	 */
	public DAOConstantsResolver() {
	}

	/**
	 * Constructor using Hibernate configuration.
	 * 
	 * @param configuration
	 */
	public DAOConstantsResolver(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Create a new DAO Constants.
	 */
	public void createDAOConstants() {
		Iterator i = configuration.getClassMappings();
		while (i.hasNext()) {
			PersistentClass persistentClass = (PersistentClass) i.next();
			createDAOConstant(persistentClass);
		}
	}

	private void createDAOConstant(PersistentClass persistentClass) {
		if (persistentClass.isInherited()) {
			createDAOConstant(persistentClass.getSuperclass());
		}
		String pojo = persistentClass.getEntityName();
		DAOConstantsEntry entry = DAOConstants.createDAOConstant(pojo);
		if (entry == null) {
			entry = resolve(persistentClass);
			DAOConstants.addBeanEntry(entry);
		}
	}

	private PropertyInfo getPropertyInfo(String pojo) {
		PropertyInfo info = null;
		if ( this.configuration != null ) {
			PersistentClass pc = this.configuration.getClassMapping(pojo);
			info = new PropertyInfo( pc.getIdentifierProperty() );
		} else {
			ClassMetadata cm = HibernateUtil.getSessionFactory().getClassMetadata(pojo);
			info = new PropertyInfo( cm.getIdentifierPropertyName(), cm.getIdentifierType() );
		}
		return info;
	}

	private String getParent(PersistentClass pc) {
		if (pc.isInherited()) {
			return pc.getSuperclass().getEntityName();
		}
		return null;
	}

	private Set<String> getParentProperties(PersistentClass pc) {
		Set<String> result;
		if (pc.isInherited()) {
			result = new HashSet<String>();
			Iterator i = pc.getSuperclass().getPropertyIterator();
			while (i.hasNext()) {
				result.add(((Property) i.next()).getName());
			}
		} else {
			result = Collections.emptySet();
		}
		return result;
	}

	private void compositeAlias(List<AliasEntry> list, String aliasPreffix,
			String hibernatePreffix, PropertyInfo component) {
		for( PropertyInfo pi : component.getComponentInfo() ) {
			String alias = aliasPreffix + "_" + pi.getName();
			String hibernate = hibernatePreffix + "." + pi.getName();
			list.add(new AliasEntry(alias, hibernate, pi.getType()));
		}
	}
	
	/**
	 * Gets the POJO's identifier alias entry list.
	 * 
	 * @param className
	 *            the class name of the POJO.
	 * 
	 * @return the identifier alias entry list
	 */
	public List<AliasEntry> getIdentifierAliasEntryList(String className) {
		List<AliasEntry> list = new ArrayList<AliasEntry>();
		ClassMetadata cm = HibernateUtil.getSessionFactory().getClassMetadata(
				className);
		PropertyInfo id = new PropertyInfo( cm.getIdentifierPropertyName(), cm.getIdentifierType() );
		calculateAlias(list, ClassUtils.getShortClassName(className), id);
		return list;
	}

	private void calculateAlias(List<AliasEntry> list, String className,
			PropertyInfo propertyInfo) {
		String aliasPreffix = className + "_" + propertyInfo.getName();
		String hibernatePreffix = className + "." + propertyInfo.getName();
		if ( propertyInfo.isComposite() ) {
			compositeAlias(list, aliasPreffix, hibernatePreffix, propertyInfo);
		} else {
			Type type = propertyInfo.getType();
			if (type.isEntityType()) {
				EntityType et = (EntityType) type;
				PropertyInfo idInfo = getPropertyInfo(et.getAssociatedEntityName());
				String aliasId = aliasPreffix + "_" + idInfo.getName();
				String hibernateId = hibernatePreffix + "." + idInfo.getName();
				if ( idInfo.isComposite() ) {
					compositeAlias(list, aliasId, hibernateId, idInfo);
				} else {
					list.add(new AliasEntry(aliasId, hibernateId, et));
				}
			} else if (!type.isCollectionType()) {
				list.add(new AliasEntry(aliasPreffix, hibernatePreffix, type));
			}
		}
	}
	
	private List<AliasEntry> getAliasEntries(PersistentClass pc) {
		List<AliasEntry> list = new ArrayList<AliasEntry>();
		String className = ClassUtils.getShortClassName(pc.getEntityName());
		if (!pc.isInherited()) {
			PropertyInfo id = new PropertyInfo(pc.getIdentifierProperty());
			calculateAlias( list, className, id );
		}
		Set<String> parentProperties = getParentProperties(pc);
		Iterator i = pc.getPropertyIterator();
		while (i.hasNext()) {
			PropertyInfo property = new PropertyInfo( (Property) i.next() );
			if (!parentProperties.contains(property.getName())) {
				calculateAlias(list, className, property);
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	private DAOConstantsEntry resolve(PersistentClass pc) {
		String pojo = pc.getEntityName();
		DAOConstantsEntry entry = new DAOConstantsEntry(pojo, getParent(pc));
		List<AliasEntry> list = getAliasEntries(pc);
		Collections.sort(list);
		int i = 0;
		String[] aliasNames = new String[list.size()];
		String[] hibernateNames = new String[list.size()];
		for (AliasEntry ae : list) {
			aliasNames[i] = ae.getAlias();
			hibernateNames[i++] = ae.getHibernate();
		}
		entry.setBeanAliasNames(aliasNames);
		entry.setHibernateNames(hibernateNames);
		return entry;
	}

	/**
	 * The Class PropertyInfo.
	 */
	public class PropertyInfo {

		private String name;

		private Type type;
		
		private Property property;

		/**
		 * The Constructor.
		 * 
		 * @param type the type
		 * @param name the name
		 */
		public PropertyInfo(String name, Type type) {
			this.name = name;
			this.type = type;
		}

		/**
		 * The Constructor. 
		 * 
		 * @param property
		 */
		public PropertyInfo(Property property) {
			this.property = property;
		}



		/**
		 * Gets the name.
		 * 
		 * @return the name
		 */
		public String getName() {
			return (this.property != null) ? this.property.getName() : name;
		}

		/**
		 * Gets the type.
		 * 
		 * @return the type
		 */
		public Type getType() {
			return (this.property != null) ? this.property.getType() : type;
		}

		/**
		 * Checks if is composite.
		 * 
		 * @return true, if is composite
		 */
		public boolean isComposite() {
			return (this.property != null) ? this.property.isComposite() : type.isComponentType();			
		}
		
		/**
		 * Gets the component info.
		 * 
		 * @return the component info
		 */
		public List<PropertyInfo> getComponentInfo() {
			List<PropertyInfo> list = new ArrayList<PropertyInfo>();
			if ( this.property != null ) {
				Component component = (Component) property.getValue();
				Iterator propertyIterator = component.getPropertyIterator();
				while ( propertyIterator.hasNext() ) {
					Property property = (Property) propertyIterator.next();
					list.add( new PropertyInfo(property) );
				}
			} else {
				ComponentType ct = (ComponentType) type;
				for (int i = 0; i < ct.getPropertyNames().length; i++) {
					String name = ct.getPropertyNames()[i];
					Type type = ct.getSubtypes()[i];
					list.add(new PropertyInfo(name, type));
				}
			}
			return list;
		}
		
	}

}
