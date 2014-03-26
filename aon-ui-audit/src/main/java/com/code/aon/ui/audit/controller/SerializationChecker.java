package com.code.aon.ui.audit.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;

public class SerializationChecker {

	private static final Logger LOGGER = LoggerFactory.getLogger(SerializationChecker.class);

	private final static String[] SKIP_MANAGED_BEAN_SERIALIZATION = {
		IAuditConstants.ACTION_DENIED_CONTROLLER_NAME,
		IAuditConstants.ACTION_FAVORITE_CONTROLLER_NAME,
	};

	private final static Class<?>[] COMMON_CLASSES = {
		Boolean.class, Byte.class, Character.class, Short.class, Integer.class,
		Long.class, Double.class, Float.class, String.class, Object.class
	};
	
	private Set<Class<?>> checkedClasses;
	
	public void check( List<URL> list ) {
		this.checkedClasses = new HashSet<Class<?>>();
		for( URL url : list ) {
			checkSessionManagedBeans(url);
		}
	}

	private void checkSessionManagedBeans( URL url ) {
		String beanNameValue = null;
        try {
        	LOGGER.info("Faces config URL: {}", url);
    		Document document = MenuParser.getDocument(url);
    		if ( document != null ) {
    			List<Element> list = document.selectNodes("/faces-config/*[name()='managed-bean']");
    			for ( Element element : list ) {
    				Element scope = element.element("managed-bean-scope");
    				if ( scope != null ) {
    					String scopeValue = StringUtils.trimToNull(scope.getText());
    					if ( "session".equals(scopeValue) ) {
    						Element beanName = element.element("managed-bean-name");
    						beanNameValue = StringUtils.trim(beanName.getText());    						
    						Element beanClass = element.element("managed-bean-class");
    						String beanClassValue = StringUtils.trim(beanClass.getText());
							checkSessionManagedBean(beanNameValue, beanClassValue);    						
    					}	
    				}
    	        }		
    		}		
        } catch (Exception e) {
            LOGGER.error("Error checking " + beanNameValue + " in " + url, e);
        }				
	}

	private Class<?> getClass( String name ) {
		Class<?> _class = null;
		try {
			_class = Class.forName(name);
		} catch ( Throwable th ) {
			LOGGER.error( "Error loading class {}", name );
		}		
		return _class;
	}
	
	private void checkSessionManagedBean(String beanName, String beanClass) {
		LOGGER.info( "Checking managed bean {} - {}", beanName, beanClass );
		Class<?> _class = getClass(beanClass);
		if (_class != null ) {
			testClass(_class);
			if ( isAonClass(_class) && !ArrayUtils.contains(SKIP_MANAGED_BEAN_SERIALIZATION,beanName) ) {
				testSerialization(beanName, _class);	
			}
		} else {
			LOGGER.error( "Error loading class {}-{}", beanName, beanClass );
		}
	}

	private boolean isSerializable( Class<?> _class ) {
		return isImplements(_class, Serializable.class);
	}

	@SuppressWarnings("rawtypes")
	private boolean isImplements( Class<?> _class, Class<?> _interface ) {
		List interfaces = ClassUtils.getAllInterfaces(_class);
		return interfaces.contains(_interface);
	}
	
	private boolean isAonClass( Class<?> _class ) {
		String name = _class.getName();
		return name.startsWith("com.code") || name.startsWith("com.esferalia");
	}
	
	private void testSerialization(String beanName, Class<?> beanClass) {
		Object object = null;
		try {
			object = AonUtil.getRegisteredBean(beanName);
		} catch (Throwable e) {
			LOGGER.error( "Error creating object of " + beanClass, e);
		}
		if ( object != null ) {
			if ( object instanceof BasicController ) {
				testBasicController( beanName, (BasicController) object );
			}
			byte[] data = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ObjectOutputStream oos = new ObjectOutputStream(baos);
			    oos.writeObject(object);
			    oos.close();
			    data = baos.toByteArray();
			} catch (Throwable e) {
				LOGGER.error( "Error serializing " + beanClass, e);
			}
			if (! ArrayUtils.isEmpty(data) ) {
				ByteArrayInputStream bais = new ByteArrayInputStream(data);
				try {				
					ObjectInputStream ois = new ObjectInputStream(bais);
					ois.readObject();
				} catch (Throwable e) {
					LOGGER.error( "Error unserializing " + beanClass, e);
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private List<Class<?>> getClasses( ParameterizedType type ) {
		List<Class<?>> list = new LinkedList<Class<?>>();
	    Type rawType = type.getRawType();
	    if ( (rawType != null) && (rawType instanceof Class) ) {
	    	list.add( (Class) rawType );
	    }
	    for( Type argType : type.getActualTypeArguments() ) {
	    	if ( argType instanceof Class ) {
	    		list.add( (Class) argType );
	    	} else if ( argType instanceof ParameterizedType ) {
	    		list.addAll(getClasses((ParameterizedType)argType));
	    	} else {
	    		LOGGER.debug("Not checked type: {}", argType);
	    	}
	    }
		return list;
	}
	
	private List<Class<?>> addClass( List<Class<?>> list, Class<?> _class ) {
		List<Class<?>> result = list;
		if ( isTesteableClass(_class, true) && ! this.checkedClasses.contains(_class) ) {
			if ( result.isEmpty() ) {
				result = new LinkedList<Class<?>>();
			}
			result.add(_class);
		}
		return result;
	}
	
	private List<Class<?>> getClasses( Field field ) {
		List<Class<?>> list = Collections.emptyList();
		Class<?> _class = field.getType();
		if ( _class.isArray() ) {
			list = addClass(list, _class.getComponentType());
		} else {
			Type genericFieldType = field.getGenericType();
			if (genericFieldType instanceof ParameterizedType) {
				for( Class<?> c : getClasses((ParameterizedType)genericFieldType) ) {
					list = addClass(list, c);
				}
		    } else {
		    	list = addClass(list, _class);
		    }			
		}
		return list;
	}
	
	private boolean isTesteableClass( Class<?> _class, boolean testAbstract ) {
		if ( _class.isPrimitive() || _class.isEnum() || _class.isInterface() ) {
			return false;
		}
		if ( testAbstract && Modifier.isAbstract(_class.getModifiers()) ) {
			return false;
		}
		if ( ArrayUtils.contains(COMMON_CLASSES, _class) ) {
			return false;
		}			
		return true;		
	}
	
	private boolean isTesteableField( Field field ) {
		int modifiers = field.getModifiers();
		if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) ) {
			return false;
		}		
		return ! getClasses(field).isEmpty();
	}
	
	private List<Field> getFields( Class<?> beanClass ) {
		List<Field> list = new LinkedList<Field>();
		for( Field field : beanClass.getDeclaredFields() ) {
			if ( isTesteableField(field) ) {
				list.add(field);
			}
		}
		for( Field field : beanClass.getFields() ) {
			if ( isTesteableField(field) ) {
				list.add(field);
			}
		}
		return list;
	}
	
	private void testFieldSerialization( Field field ) {
		List<Class<?>> classes = getClasses(field);
        LOGGER.debug("Field {}, {}", field.getName(), classes );
		for( Class<?> _class : classes ) {
			testClass(_class);
		}
	}
	
	private long getSerialVersionUID( Class<?> _class ) {
		long uid = -1;
		try {
			ObjectStreamClass osc = ObjectStreamClass.lookup(_class);
			if ( osc != null ) {
				uid = osc.getSerialVersionUID();	
			} else {
				LOGGER.error( "Error getting serialVersionUID of {}" + _class );		
			}
		} catch ( Throwable th ) {
			LOGGER.error( "Error getting serialVersionUID of " + _class, th);
		}
		return uid;
	}
	
	private void testClass(Class<?> _class) {
		if ( this.checkedClasses.contains(_class) ) {
			return;
		}
		this.checkedClasses.add(_class);		
		if ( isSerializable(_class) ) {
			List<Field> fields = getFields(_class);
			if (! fields.isEmpty() ) {
				LOGGER.info("Class {}, {} fields", _class, fields.size());
				for( Field field : fields ) {
					testFieldSerialization(field);		
				}			
			}
			if ( isAonClass(_class) ) {
				long uid = getSerialVersionUID(_class);
				if ( AonVersion.SERIAL_VERSION_UID != uid ) {
					LOGGER.error( "WRONG serialVersionUID in {}", _class );
				}			
				Class<?> superClass = _class.getSuperclass();
				if ( isTesteableClass(superClass, false) && isAonClass(superClass) ) {
					testClass(superClass);
				}
			}					
		} else {
			LOGGER.error( "NOT SERIALIZABLE {}", _class );
		}
	}
	
	private void testBasicController(String beanName, BasicController controller) {
		String pojo = controller.getPojo();
		if (! StringUtils.isEmpty(pojo) ) {
			Class<?> _class = getClass(pojo);
			if ( _class != null ) {
				testClass(_class);	
			}	
		} else {
			LOGGER.error("ERROR Basic Controller {} without pojo, {} ", beanName, controller.getClass());
		}
		if ( controller.getListeners() != null ) {
			for( IControllerListener listener : controller.getListeners() ) {
				testClass(listener.getClass());
			}
		}
	}
	
}