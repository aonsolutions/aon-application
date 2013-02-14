package com.code.aon.entity.test;

import com.code.aon.common.ManagerBeanException;

public interface ITestEntity<E> {
	
	public void list() throws ManagerBeanException;		
	public void insert() throws ManagerBeanException;		
	public void update() throws ManagerBeanException;		
	public void remove() throws ManagerBeanException;
	public E getEntity() throws ManagerBeanException;

}
