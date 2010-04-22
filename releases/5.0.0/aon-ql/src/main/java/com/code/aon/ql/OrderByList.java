package com.code.aon.ql;

import java.util.ArrayList;
import java.util.Collection;
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

	private static final long serialVersionUID = 4969072459805125924L;

	private List<Order> orders;
	
	/**
	 * Default Constructor.
	 */
	public OrderByList() {
		this.orders = new ArrayList<Order>(4);
	}
	
	/**
	 * Instantiates a new order by list.
	 * 
	 * @param orders the orders
	 */
	public OrderByList( Collection<Order> orders ) {
		this();
		setOrders(orders);
	}	
	
	/**
	 * Adds the given <code>Order</code> to the order list.
	 * 
	 * @param order
	 *            The item to be added to the list.
	 */
	public void add(Order order) {
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
	
	/**
	 * Sets the orders.
	 * 
	 * @param orders the new orders
	 */
	public void setOrders( Collection<Order> orders ) {
		this.orders.clear();
		this.orders.addAll( orders );
	}
	
	/**
	 * Gets the order.
	 * 
	 * @param id the id
	 * 
	 * @return the index
	 */
	public int indexOf( String id ) {
		for( int i = 0; i < this.orders.size(); i++ ) {
			if ( orders.get(i).getExpression().getName().equals(id) ) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Gets the order.
	 * 
	 * @param id the id
	 * 
	 * @return the order
	 */
	public Order get( String id ) {
		int index = indexOf(id);
		return ( index != -1 ) ? this.orders.get(index) : null;
	}
	
	/**
	 * Removes the order.
	 * 
	 * @param index the index
	 * 
	 * @return the order
	 */
	public Order remove( int index ) {
		return this.orders.remove(index);
	}
	
    /**
     * Returns the number of order.
     *
     * @return the number of order
     */
	public int size() {
		return orders.size();
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
        Iterator<Order> i = getOrders().iterator();
        boolean hasNext = i.hasNext();
        while (hasNext) {
        	buf.append( i.next() );
            hasNext = i.hasNext();
            if (hasNext)
                buf.append(", ");
        }		
		return buf.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		OrderByList obl = (OrderByList) obj;
		if ( size() == obl.size() ) {
			Iterator<Order> it = obl.getOrders().iterator();
			for( Order order : getOrders() ) {
				Order order2 = it.next();
				if (! order.equals(order2) ) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
}
