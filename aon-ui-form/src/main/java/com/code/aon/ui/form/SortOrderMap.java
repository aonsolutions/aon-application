package com.code.aon.ui.form;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Set;

import javax.faces.FacesException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.richfaces.model.Ordering;

import com.code.aon.AonVersion;
import com.code.aon.common.ICriteriaProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Order;

/**
 * The Class SortOrderMap.
 */
public class SortOrderMap extends AbstractMap<String,Ordering> implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private ICriteriaProvider provider;
	
	private boolean updated;
	
	private Criteria _criteria;

	/**
	 * Instantiates a new sort order map.
	 * 
	 * @param provider the provider
	 */
	public SortOrderMap(ICriteriaProvider provider) {
		this.provider = provider;
	}
	
	/**
	 * Instantiates a new sort order map.
	 */
	public SortOrderMap() {
		_criteria = new Criteria();
	}

	private Criteria getCriteria() {
		try {
			if ( provider != null ) {
				return provider.getCriteria();	
			}
		} catch (ManagerBeanException e) {
			throw new FacesException(e.getMessage(), e); 
		}
		return _criteria;
	}
	
	private String[] getAliases( String key ) {
		String[] aliases = StringUtils.split( (String) key, ',' );
		if ( aliases != null ) {
			for( int i = 0; i < aliases.length; i++ ) {
				aliases[i] = StringUtils.trim(aliases[i]);
			}
		}
		return aliases;
	}	
	
	@Override
	public Ordering get(Object key) {
		Criteria  criteria = getCriteria();
		if ( criteria.hasOrders() ) {
			String[] aliases = getAliases( (String) key );
			if (! ArrayUtils.isEmpty(aliases) ) {
				Order order = criteria.getOrderByList().get(aliases[0]);
				if ( order != null ) {
					return order.isAscending() ? Ordering.ASCENDING : Ordering.DESCENDING;
				}					
			}
		}
		return Ordering.UNSORTED;
	}
	
	private void updateOrder( Criteria criteria, String alias, Ordering value ) {
		int index = -1;
		if ( criteria.hasOrders() ) {
			index = criteria.getOrderByList().indexOf(alias);
		}
		if ( value == Ordering.UNSORTED ) {
			if ( index != -1 ) {
				criteria.getOrderByList().remove(index);
			}
		} else {
			boolean ascending = ( value == Ordering.ASCENDING );
			if ( index != -1 ) {
				Order order = criteria.getOrderByList().getOrders().get(index);
				if ( order.isAscending() != ascending ) {
					Order newOrder = new Order(order.getExpression(), ascending );
					criteria.getOrderByList().getOrders().set(index, newOrder);
				}
			} else {
				criteria.addOrder(alias, ascending);
			}
		}
	}
	
	@Override
	public Ordering put(String key, Ordering value) {
		Criteria  criteria = getCriteria();
		String[] aliases = getAliases( key );
		for( String alias : aliases ) {
			updateOrder(criteria, alias, value);
		}
		updated = true;			
		return value;
	}

	@Override
	public Set<java.util.Map.Entry<String, Ordering>> entrySet() {
		return null;
	}

	/**
	 * Checks if is updated.
	 * 
	 * @return true, if is updated
	 */
	public boolean isUpdated() {
		return updated;
	}

	/**
	 * Sets the updated.
	 * 
	 * @param updated the new updated
	 */
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
	
}
