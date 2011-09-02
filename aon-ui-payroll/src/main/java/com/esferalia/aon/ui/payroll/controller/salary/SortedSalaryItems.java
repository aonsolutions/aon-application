package com.esferalia.aon.ui.payroll.controller.salary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.salary.ISalaryItem;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.payment.IPayment;



public class SortedSalaryItems<T extends Enum<T> & IResourceable> {
	
	
	public static class  DisplaySalaryItem<T extends Enum<T> & IResourceable> {
		
		private ISalaryItem<T> newSalaryItem;
		private ISalaryItem<T> oldSalaryItem;

		public DisplaySalaryItem(ISalaryItem<T> newSalaryItem, ISalaryItem<T> oldSalaryItem ) {
			this.newSalaryItem = newSalaryItem;
			this.oldSalaryItem = oldSalaryItem;
		}
		
		public T getType() {
			return newSalaryItem != null ? 
					newSalaryItem.getType() : 
					oldSalaryItem.getType();
		}

		
		public String getName() {
			return newSalaryItem != null ? 
					newSalaryItem.getName() : 
					oldSalaryItem.getName();
		}
		
		public Double getAmount(){
			return newSalaryItem != null ? 
					CommonUtil.truncate(newSalaryItem.getAmount()) : null;
		}

		public String getDescription(){
			return newSalaryItem != null ? 
					newSalaryItem.getDescription() : 
					oldSalaryItem.getDescription();
		}

		public Double getOldAmount(){
			return oldSalaryItem != null ? 
					oldSalaryItem.getAmount() : null;
		}
		
		public Double getDifference() {
			Double amount = getAmount();
			if ( amount == null ) {
				amount = 0.00;
			}
			Double oldAmount = getOldAmount();
			if ( oldAmount == null ) {
				oldAmount = 0.00;
			}
			
			return Math.abs( oldAmount - amount );
		}
		
		@Override
		public String toString() {
			return String.format("%s [%f,%f]: %s.", getName(), getAmount(), getOldAmount(), getDescription() );
		}
		
		@Override
		public boolean equals(Object obj) {
			if ( obj != null && obj instanceof DisplaySalaryItem<?>   ){
				DisplaySalaryItem<?> item = ( DisplaySalaryItem<?> ) obj;
				return equalsX(this.newSalaryItem, item.newSalaryItem) && 
					equalsX(this.oldSalaryItem, item.oldSalaryItem);
			}
			return false;
		}
		
		private static boolean equalsX ( Object obj1, Object obj2 ) {
			if ( obj1 == obj2 ) {
				return true;
			}
			if ( obj1 == null || obj2 == null ) {
				return false;
			}
			return obj1.equals(obj2);
		}
	}

	private Map<String, CollectionWrapper<DisplaySalaryItem<T>>> salaryItemsMap;
	
	public SortedSalaryItems() {
		this.salaryItemsMap = new HashMap<String, CollectionWrapper<DisplaySalaryItem<T>>>();
	}
	
	public SortedSalaryItems(T types [] ) {
		this();
		for (T type : types) {
			salaryItemsMap.put(type.name(), 
					new CollectionWrapper<SortedSalaryItems.DisplaySalaryItem<T>>(type));
		}
	}

	public void setPayments(Collection<? extends ISalaryItem<T>> newSalaryItems, 
			Collection< ? extends ISalaryItem<T>> oldSalaryItems) {
		
		List<ISalaryItem<T>> newSalaryItemsList = new ArrayList<ISalaryItem<T>>(newSalaryItems);
		List<ISalaryItem<T>> oldSalaryItemsList = new ArrayList<ISalaryItem<T>>(oldSalaryItems);
		
		FullSalaryItemComparator<T> comparator = 
			new FullSalaryItemComparator<T>();
		
		Collections.sort(newSalaryItemsList, comparator);
		Collections.sort(oldSalaryItemsList, comparator);
		
		setPayments(newSalaryItemsList.listIterator(), oldSalaryItemsList.listIterator());
		
	}
	
	public Map<String, CollectionWrapper<DisplaySalaryItem<T>>> getPayments(){
		return salaryItemsMap;
	}
	
	
	private void setPayments(Iterator<ISalaryItem<T>> newSalaryItems, Iterator<ISalaryItem<T>> oldSalaryItems ) {
		SalaryItemComparator<T> comparator = new SalaryItemComparator<T>();
		
		ISalaryItem<T> newSalaryItem = newSalaryItems.hasNext() ? newSalaryItems.next() : null;
		ISalaryItem<T> oldSalaryItem = oldSalaryItems.hasNext() ? oldSalaryItems.next() : null;
		
		while ( newSalaryItem != null || oldSalaryItem != null )  {
			int compare = comparator.compare(newSalaryItem, oldSalaryItem);
			if ( compare == 0  ) {
				addBoth(newSalaryItem, oldSalaryItem);
				newSalaryItem = newSalaryItems.hasNext() ? newSalaryItems.next() : null;
				oldSalaryItem = oldSalaryItems.hasNext() ? oldSalaryItems.next() : null;
			}
			else if ( compare < 0 ) {
				addNew(newSalaryItem);
				newSalaryItem = newSalaryItems.hasNext() ? newSalaryItems.next() : null;
			}
			else {
				addOld(oldSalaryItem);
				oldSalaryItem = oldSalaryItems.hasNext() ? oldSalaryItems.next() : null;
			}
		} ;
	}
	
	private void addNew(ISalaryItem<T> newSalaryItem) {
		add ( new DisplaySalaryItem<T>(newSalaryItem,null));
	}

	private void addOld(ISalaryItem<T> oldSalaryItem) {
		add ( new DisplaySalaryItem<T>(null,oldSalaryItem));
		
	}

	private void addBoth(ISalaryItem<T> newSalaryItem, ISalaryItem<T> oldSalaryItem ) {
		add ( new DisplaySalaryItem<T>(newSalaryItem,oldSalaryItem));
	}
	
	private void add( DisplaySalaryItem<T> displaySalaryItem ) {
		T type = displaySalaryItem.getType();
		String key = type.name();
		CollectionWrapper<DisplaySalaryItem<T>> collectionWrapper = 
			salaryItemsMap.get(key);
		if ( collectionWrapper == null ) {
			collectionWrapper = 
				new CollectionWrapper<SortedSalaryItems.DisplaySalaryItem<T>>(type);
			salaryItemsMap.put(key, collectionWrapper);
		}
		collectionWrapper.add(displaySalaryItem);
		
	}
	
	private static class SalaryItemComparator<T extends Enum<T> & IResourceable> implements Comparator<ISalaryItem<T>> {
		@Override
		public int compare(ISalaryItem<T> o1, ISalaryItem<T> o2) {
			
			if ( o1 == null ) {
				return 1;
			}
			
			if ( o2 == null ) {
				return -1; 
			}
			int compare = compare(o1.getType(), o2.getType());
			if ( compare != 0 ) {
				return compare ;
			}
			
			compare = compare(o1.getName(), o2.getName()); 
			if ( compare != 0 ) {
				return compare ;
			}

			compare = compare(o1.getDescription(), o2.getDescription()); 
			
			return compare ;

		}
		
		private int compare(T e1, T e2 ) {
			return e1 == e2 ? 0 : ( e1.compareTo(e2) );
		}

		private int compare(String s1, String s2 ) {
			if ( s1 == s2 ) {
				return 0;
			}

			if ( s1 == null ) {
				return -1;
			}
			
			if ( s2 == null ) {
				return 1;
			}
			
			return s1.compareTo(s2);
		}
	}
	
	private static class FullSalaryItemComparator<T extends Enum<T> & IResourceable> extends SalaryItemComparator<T> {
		@Override
		public int compare(ISalaryItem<T> o1, ISalaryItem<T> o2) {
			int compare = super.compare(o1, o2);
			if ( compare != 0 ) {
				return compare ;
			}
			return ( int ) ( o1.getAmount() - o2.getAmount() );
		}
	}
	
}
