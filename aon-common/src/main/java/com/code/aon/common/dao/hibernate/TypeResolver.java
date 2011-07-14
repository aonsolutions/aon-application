package com.code.aon.common.dao.hibernate;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.AssociationType;
import org.hibernate.type.CharacterType;
import org.hibernate.type.ClobType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.CustomType;
import org.hibernate.type.IdentifierType;
import org.hibernate.type.NullableType;
import org.hibernate.type.StringType;
import org.hibernate.type.TextType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypeResolver {

	private final static Logger LOGGER = LoggerFactory.getLogger(TypeResolver.class);
	
	private SessionFactory sessionFactory;
	
	private ClassMetadata classMetdata;
	
    public TypeResolver(String pojo, SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.classMetdata = sessionFactory.getClassMetadata(pojo);
	}

    public TypeResolver( String pojo ) {
		String factoryName = HibernateUtil.getSessionFactoryName(pojo);
		this.sessionFactory = HibernateUtil.getSessionFactory(factoryName);
		this.classMetdata = sessionFactory.getClassMetadata(pojo);
	}
    
	public ClassMetadata getClassMetdata() {
		return classMetdata;
	}

	private Type getType(ComponentType componentType, String property) {
        String[] names = componentType.getPropertyNames();
        for (int i = 0; i < names.length; i++) {
            if (property.equals(names[i])) {
                return componentType.getSubtypes()[i];
            }
        }
        return null;
    }

    private Type getType(AssociationType associationType, String property) {
		String entityName = associationType.getAssociatedEntityName( (SessionFactoryImpl) sessionFactory );
        ClassMetadata cmd =	sessionFactory.getClassMetadata( entityName );
        return getType(cmd, property);
    }
	
    private Type getType(ClassMetadata cmd, String property) {
        Type type = null;
        String moreProperty = null;
        int pos = StringUtils.indexOfAny( property, HibernateRenderer.SEPARATORS );
        if (pos != -1) {
            moreProperty = property.substring(pos + 1);
            property = property.substring(0, pos);
        }
        try {
            String idName = cmd.getIdentifierPropertyName();
            if (property.equals(idName)) {
                type = cmd.getIdentifierType();
            } else {
                type = cmd.getPropertyType(property);
            }
            if (type != null) {
                if (type.isComponentType()) {
                    type = getType((ComponentType) type, moreProperty);
                } else if (type.isAssociationType()) {
                    type = getType((AssociationType) type, moreProperty);
                }
            }
        } catch (HibernateException he) {
            LOGGER.error("Error obteniendo el Type de la propiedad " + property, he);
        }
        return type;
    }    
	
	public Type getType( String property ) {
		String propertyName = property.substring(StringUtils.indexOfAny( property, HibernateRenderer.SEPARATORS ) + 1);		
		Type type = getType(this.classMetdata, propertyName);
		if ( (type != null) && ((type instanceof IdentifierType) || (type instanceof NullableType))) {
			return type;
		}
		return null;
	}

	public ClassMetadata getEntityMetaData( AssociationType associationType ) {
		ClassMetadata cmd = null;
		if ( associationType != null ) {
			String entityName = associationType.getAssociatedEntityName( (SessionFactoryImpl) sessionFactory );
			cmd = sessionFactory.getClassMetadata( entityName );
		}
		return cmd;
	}

	public boolean isString( Type type ) {
		if ( type instanceof CustomType ) {
			CustomType ct = (CustomType) type;
			return String.class.isAssignableFrom(ct.getReturnedClass());
		}
		return (type instanceof StringType) || (type instanceof TextType) ||
			(type instanceof ClobType) || (type instanceof CharacterType);
	}
}
