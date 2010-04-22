package com.code.aon.department.ast;

import com.code.aon.department.Department;

public interface INodeVisitor {

    /**
     * Visita el nodo de tipo department
     * @param department Department
     */
    void visitDepartment(Department department);
}