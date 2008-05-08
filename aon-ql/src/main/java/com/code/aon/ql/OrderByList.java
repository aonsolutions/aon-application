package com.code.aon.ql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.code.aon.ql.ast.Criterion;
import com.code.aon.ql.ast.CriterionVisitor;

/**
 * Class for wrapping the expressions used in the order section.
 * 
 * @author Consulting & Development. Aimar Tellitu - 21-jul-2005
 * @since 1.0
 *  
 */
public class OrderByList implements Criterion {

	private List<Order> orders;
	
	/**
	 * Default Constructor.
	 */
	public OrderByList() {
		this.orders = new ArrayList<Order>();
	}

	/**
	 * Adds the given <code>Order</code> to the order list.
	 * 
	 * @param order
	 *            The item to be added to the list.
	 */
	public void addOrder(Order order) {
		if (! this.orders.contains(order) ) {
			this.orders.add(order);			
		}
	}
	
	/**
	 * Returns the <code>Order</code> list.
	 * @return The <code>Order</code> list.
	 */
	public List<Order> getOrders() {
		return orders;
	}
	
    /* (non-Javadoc)
     * @see com.code.aon.ql.ast.Criterion#accept(com.code.aon.ql.ast.CriterionVisitor)
     */
    public void accept(CriterionVisitor visitor) {
        visitor.visitOrderByList(this);
    }

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
        Iterator<Order> i = orders.iterator();
        boolean hasNext = i.hasNext();
        while (hasNext) {
        	buf.append( i.next() );
            hasNext = i.hasNext();
            if (hasNext)
                buf.append(", ");
        }		
		return buf.toString();
	}
    
}
