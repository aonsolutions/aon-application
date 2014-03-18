package com.code.aon.ql.ast.impl;

import com.code.aon.AonVersion;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.CriterionVisitor;
import com.code.aon.ql.ast.SubQueryExpression;

/**
 * Ternary expression for evaluating an <code>Expression</code> against a
 * range of <code>Expression</code>s.
 * 
 * @author esferalia Networks. Aimar Tellitu - 20-jul-2012
 * @since 1.0
 * 
 */
public class SubQueryExpressionImpl implements SubQueryExpression {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private String pojo;

	private Criteria criteria;

	private ProjectionList projectionList;

	public SubQueryExpressionImpl(String pojo, Criteria criteria, ProjectionList projectionList) {
		this.pojo = pojo;
		this.criteria = criteria;
		this.projectionList = projectionList;
	}

	public String getPojo() {
		return pojo;
	}

	public Criteria getCriteria() {
		return criteria;
	}

	public ProjectionList getProjectionList() {
		return projectionList;
	}

	@Override
	public void accept(CriterionVisitor visitor) {
		visitor.visitSubQueryExpression(this);
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("SubQuery[");
		buf.append("criteria=");
		buf.append( criteria );
		if ( this.projectionList != null ) {
			buf.append(",projectionList=");
			buf.append( projectionList );
		}
		buf.append("]");
		return buf.toString();
	}
	
}