package com.code.aon.ql;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.ql.ast.Criterion;
import com.code.aon.ql.ast.CriterionVisitor;

/**
 * Class for wrapping the expressions used in the order section.
 * 
 * @author Consulting & Development. Aimar Tellitu - 26-mar-2007
 * @since 1.0
 *  
 */
public class ProjectionList implements Criterion {

	private List<Projection> projections;
	
	/**
	 * Default Constructor.
	 */
	public ProjectionList() {
		this.projections = new ArrayList<Projection>();
	}

	/**
	 * The Constructor.
	 * 
	 * @param projection the projection
	 */
	public ProjectionList( Projection projection ) {
		this();
		add(projection);
	}
	
	/**
	 * Adds the given <code>Projection</code> to the projection list.
	 * 
	 * @param projection
	 *            The item to be added to the list.
	 */
	public void add(Projection projection) {
		if (! this.projections.contains(projection) ) {
			this.projections.add(projection);			
		}
	}
	
    /**
     * Returns <tt>true</tt> if this list contains no elements.
     *
     * @return <tt>true</tt> if this list contains no elements.
     */
	public boolean isEmpty() {
		return this.projections.isEmpty();
	}
	
	/**
	 * Returns the <code>Projection</code> list.
	 * 
	 * @return The <code>Projection</code> list.
	 */
	public List<Projection> getProjections() {
		return projections;
	}
	
    public void accept(CriterionVisitor visitor) {
        visitor.visitProjectionList(this);
    }

}
