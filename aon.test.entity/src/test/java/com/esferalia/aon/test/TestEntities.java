package com.esferalia.aon.test;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import javax.persistence.Transient;

import junit.framework.Assert;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;
import org.junit.Test;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.bean.BeanConfigParser;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.Classpath;
import com.code.aon.entity.test.ITestEntity;


public class TestEntities {
	
	private List<String> entityClasses = new LinkedList<String>();
	private Map<?,?>  map;
	private Stack<String> stack = new Stack<String>();
	private static final String CONFIG_FILE = "bean-config.xml";
	
	@Test
	public void test() throws ManagerBeanException, NotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
/*		
		ClassPool pool = ClassPool.getDefault(); 
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		HibernateUtil.getSessionFactory(sessionFactoryName);
		map = HibernateUtil.getSessionFactory(HibernateUtil.getSessionFactoryName()).getAllClassMetadata();
		addEntity(pool.get("com.code.aon.config.Domain"));
		int size = map.values().size();
		for (Object o : map.values()) {
			ClassMetadata cmd = (ClassMetadata) o;
			if (!entityClasses.contains(cmd.getEntityName())) {
				CtClass entity = pool.get(cmd.getEntityName());
				addEntity(entity);
			}
		}
		
		Assert.assertEquals(size,entityClasses.size());
		
		System.out.println();
		System.out.println("Test Battery order");
		System.out.println("==================");
		int i = 1;
		for (String className:entityClasses) {
			System.out.println(StringUtils.leftPad(""+i, 5) + ".- " + className);
			i++;
		}
        System.out.println();
        
		for (int x = entityClasses.size(); x > 0; x-- ) {
			String name = ClassUtils.getShortClassName(entityClasses.get(x-1));
			if (!"EnterpriseUser".equals(name)) {
				Session session = HibernateUtil.getSession( sessionFactoryName );
				String delete = "delete from " + name;
		        int rows = session.createQuery(delete).executeUpdate();
		        System.out.println( "Rows Deleted (" + name + "): "  +  rows );
			}
		}
        
		loadBeanListeners();
		for (String className:entityClasses) {
			testEntity(className);
		}
*/		
	}

	private void loadBeanListeners() throws IOException, ManagerBeanException {
        System.out.println();
        System.out.println("Bean Listeners registration");
        System.out.println("===========================");
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL[] urls = Classpath.search(cl, "META-INF/", CONFIG_FILE);
        for (int i = 0; i < urls.length; i++) {
        	InputStream is = urls[i].openStream();
	        BeanConfigParser parser = BeanConfigParser.getInstance();
	        parser.parse( is );
	        is.close();
	        System.out.println("Added Bean Config from: " + urls[i]);
        }
        System.out.println();
	}

	private void addEntity(CtClass entity) throws NotFoundException, ClassNotFoundException {
		stack.push(entity.getName());
		CtMethod[] methods = entity.getMethods();
		for (CtMethod method:methods) {
			Object ann = method.getAnnotation(Transient.class);
			if (ObjectUtils.equals(ann, null)) {
				CtClass returnClass = method.getReturnType();
				if ( isAonEntity(returnClass) ) {
					addEntity(returnClass);	
				}
			}
		}
		entityClasses.add(entity.getName());
		stack.pop();
	}
	
	private boolean isAonEntity(CtClass returnClass) {
		if (returnClass.isInterface() 
			|| returnClass.isPrimitive() 
			|| returnClass.isEnum() 
			|| returnClass.getName().startsWith("java.lang.")
			|| stack.contains(returnClass.getName())
			|| entityClasses.contains(returnClass.getName() )) {
			return false;	
		}
		boolean exists = map.containsKey(returnClass.getName()); 
		return exists;
	}
	
	private void testEntity(String className) throws ManagerBeanException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		String simpleName = ClassUtils.getShortClassName(className);
		Class<?> testClass = Class.forName("com.esferalia.aon.test.TestEntity" + simpleName);
		System.out.println();
		System.out.println("\t" + "TestEntity" + simpleName + " loaded!");
		System.out.println();
		ITestEntity<?> test = (ITestEntity<?>) testClass.newInstance();
		test.insert();
	}
	
}
