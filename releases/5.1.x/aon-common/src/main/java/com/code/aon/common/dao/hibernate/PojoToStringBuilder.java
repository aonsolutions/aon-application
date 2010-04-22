package com.code.aon.common.dao.hibernate;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.EntityMode;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;

import com.code.aon.common.ITransferObject;

/**
 * The Class PojoToStringBuilder.
 */
public class PojoToStringBuilder extends ReflectionToStringBuilder {

	private String[] collections;
	
	private String[] entities;
	
	private EntityMode entityMode = EntityMode.POJO; 
	
    /**
     * <p>
     * Constructor.
     * </p>
     * 
     * <p>
     * This constructor outputs using the default style set with <code>setDefaultStyle</code>.
     * </p>
     * 
     * @param object
     *            the Object to build a <code>toString</code> for, must not be <code>null</code>
     * @throws IllegalArgumentException
     *             if the Object passed in is <code>null</code>
     */
	public PojoToStringBuilder(ITransferObject object) {
		super(object);
		scanPojo(object);
	}

    /**
     * <p>
     * Constructor.
     * </p>
     * 
     * <p>
     * If the style is <code>null</code>, the default style is used.
     * </p>
     * 
     * @param object
     *            the Object to build a <code>toString</code> for, must not be <code>null</code>
     * @param style
     *            the style of the <code>toString</code> to create, may be <code>null</code>
     * @throws IllegalArgumentException
     *             if the Object passed in is <code>null</code>
     */
	public PojoToStringBuilder(ITransferObject object, ToStringStyle style) {
		super(object, style);
		scanPojo(object);
	}
	
    /**
     * <p>
     * Constructor.
     * </p>
     * 
     * <p>
     * If the style is <code>null</code>, the default style is used.
     * </p>
     * 
     * <p>
     * If the buffer is <code>null</code>, a new one is created.
     * </p>
     * 
     * @param object
     *            the Object to build a <code>toString</code> for
     * @param style
     *            the style of the <code>toString</code> to create, may be <code>null</code>
     * @param buffer
     *            the <code>StringBuffer</code> to populate, may be <code>null</code>
     * @throws IllegalArgumentException
     *             if the Object passed in is <code>null</code>
     */
	public PojoToStringBuilder(ITransferObject object, ToStringStyle style,
			StringBuffer buffer) {
		super(object, style, buffer);
		scanPojo(object);
	}
	
	private ClassMetadata getClassMetadata( Object object ) {
		String factoryName = HibernateUtil.getSessionFactoryName();
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory(factoryName);
    	return sessionFactory.getClassMetadata(object.getClass());		
	}
	
	private void scanPojo( Object object ) {
		List<String> collectionList = new LinkedList<String>();
		List<String> entityList = new LinkedList<String>();
    	ClassMetadata cm = getClassMetadata(object);
    	String[] names = cm.getPropertyNames();
    	Type[] types = cm.getPropertyTypes();
    	for( int i = 0; i < types.length; i++ ) {    		
    		Type type = types[i];
    		if ( type.isCollectionType() ) {
    			collectionList.add(names[i]);
    		} else if ( type.isEntityType() || type.isAnyType() ) {
    			entityList.add(names[i]);
    		}
    	}		
    	this.collections = (String[]) collectionList.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    	this.entities = (String[]) entityList.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
	}

	@Override
	protected boolean accept(Field field) {
		if (this.collections != null
			&& Arrays.binarySearch(this.collections, field.getName()) >= 0) {
			// Reject fields from the collections list.
			return false;
        }
        return super.accept(field);
	}

	@Override
	protected Object getValue(Field field) throws IllegalArgumentException,
			IllegalAccessException {
		Object value = super.getValue(field);
		if (this.entities != null && value != null
			&& Arrays.binarySearch(this.entities, field.getName()) >= 0) {
			ClassMetadata cm = getClassMetadata(value);
	    	return cm.getIdentifier(value, getEntityMode());
		}
		return value;
	}

	/**
	 * Gets the entity mode.
	 * 
	 * @return the entity mode
	 */
	public EntityMode getEntityMode() {
		return entityMode;
	}

	/**
	 * Sets the entity mode.
	 * 
	 * @param entityMode the new entity mode
	 */
	public void setEntityMode(EntityMode entityMode) {
		this.entityMode = entityMode;
	}
	
}
