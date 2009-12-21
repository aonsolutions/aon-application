package com.code.aon.ql.ast;

import java.io.Serializable;

/**
 * 
 * Element able to be visited by a visitor whom realize operations with this
 * class without modifying its structure.
 * 
 * @author Consulting & Development. Aimar Tellitu - 21-jul-2005
 * @since 1.0
 * 
 */
public interface Criterion extends Serializable {
	/**
	 * Visitor pattern implementaion. In this method implementaion must invoke
	 * corresponding method on the given <code>CriterionVisitor<code>.
	 *  
	 * @param visitor Visitor to be visited.
	 */
	void accept(CriterionVisitor visitor);
}