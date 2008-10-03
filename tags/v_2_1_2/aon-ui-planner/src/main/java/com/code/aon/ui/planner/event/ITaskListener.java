package com.code.aon.ui.planner.event;

import com.code.aon.common.ManagerBeanException;

import com.code.aon.planner.ITask;

import com.code.aon.ql.util.ExpressionException;

public interface ITaskListener {
	
	void update( ITask task ) throws ExpressionException, ManagerBeanException;

}
