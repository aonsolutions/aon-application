package com.code.aon.company;

import com.code.aon.company.resources.Employee;

public interface IEntityVisitor {

    /**
     * Visit WorkPlace entity.
     * 
     * @param workPlace
     */
    void visitWorkPlace(WorkPlace workPlace);

    /**
     * Visit WorkActivity entity.
     * 
     * @param activity
     */
    void visitWorkActivity(WorkActivity activity);

    /**
     * Visit Employee entity.
     * 
     * @param employee
     */
    void visitEmployee(Employee employee);

}