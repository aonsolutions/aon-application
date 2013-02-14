package com.code.aon.ql.ast;

import com.code.aon.ql.Criteria;
import com.code.aon.ql.ProjectionList;

/**
 * Unary expresion that contains a subquery.
 * 
 * @author esferalia Networks. Aimar Tellitu - 20-jul-2012
 * @since 1.0
 *  
 */
public interface SubQueryExpression extends Expression {

    /**
     * Returns the pojo of this <code>Expression</code>.
     * 
     * @return The pojo of this <code>Expression</code>.
     */
    String getPojo();
	
    /**
     * Returns the criteria of this <code>Expression</code>.
     * 
     * @return The criteria of this <code>Expression</code>.
     */
    Criteria getCriteria();

    /**
     * Returns the projection list of this <code>Expression</code>.
     * 
     * @return The projection list of this <code>Expression</code>.
     */
    ProjectionList getProjectionList();
    
}