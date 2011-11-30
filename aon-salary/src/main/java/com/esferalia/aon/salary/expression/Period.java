package com.esferalia.aon.salary.expression;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.util.CommonUtil;

public class Period implements Comparable<Period>{

	private final Date start;
	private final Date end;
	
	public static Date max(Date a, Date b ) {
		return compare(a, b) > 0 ? a : b;
	}
	
	public static Date min(Date a, Date b ) {
		return compare(a, b) < 0 ? a : b;
	}

	public static int compare(Date a, Date b ) {
		if (a == null ) {
			return b == null ? 0 : 1; 
		}
		return b == null ? -1 : a.compareTo(b);
	}

	/**
     * @throws IllegalArgumentException cuando start >= end
     */
	public Period(Date start, Date end ) {
		Date a = start != null ? DateUtils.truncate(start, Calendar.DAY_OF_MONTH) : null;
		Date b = end != null ? DateUtils.truncate(end, Calendar.DAY_OF_MONTH) : null;
	   	if (compare(a, b) > 0) {
    		throw new IllegalArgumentException(
    			"start : " + start + " must be <= than end : " + end
    		);
	   	}
		this.start = start; // a
		this.end = end;     // b
	}
	
	public Date getStart() {
		return start;
	}
	
	public Date getEnd() {
		return end;
	}
	
	public List<Period> sub(Period p ) {
		
		List<Period> sub = 
				new LinkedList<Period>();
		
		Period intersect = intersect(p);
		
		if ( intersect == null ){
			sub.add(this);
			return sub;
		}
		
		if ( intersect.contains(this) ){
			return sub;
		}
		
		if ( compare ( start, intersect.start ) == 0 ){
			sub.add(new Period(next(intersect.end), end ));
			return sub;
		}

		sub.add(new Period(start, prev(intersect.start)));
		
		if ( compare(intersect.end, end ) < 0 ) {
			sub.add(new Period(next(intersect.end), end ));
		}

		return sub;
		
	}
	
	
	public boolean contains(Date date) {
		return compare(this.start, date ) <= 0 &&  
			compare(this.end, date ) >= 0 ;
	}

	public boolean contains(Period p) {
		return compare(this.start, p.start ) <= 0 &&  
			compare(this.end, p.end ) >= 0 ;
	}
	
	public boolean intersects(Period p) {
    	Date maxStart = max ( this.start, p.start );
    	Date minEnd = min ( this.end, p.end );
    	return compare( maxStart, minEnd) <= 0; 
	}

	public Period intersect(Period p) {
    	Date maxStart = max ( this.start, p.start );
    	Date minEnd = min ( this.end, p.end );
    	if ( compare( maxStart, minEnd) > 0 ) {
    		return null ;
    	}
    	return new Period( maxStart, minEnd );
    }

	
    @Override
	public int compareTo(Period p) {
		int startComp = compare(this.start, p.start);
		return startComp != 0 ? startComp : compare(this.end, p.end);
	}
    
    
    
	public static 	List<Period> intersect(List<Period> a, List<Period> b ){
		if ( a == null || b == null )
			return null;
		
		List<Period> periods = new LinkedList<Period>();
		
		Iterator<Period> aIterator = a.iterator();
		Iterator<Period> bIterator = b.iterator();
		
		Period aPeriod = null;
		Period bPeriod = null;
		while ( aIterator.hasNext() || bIterator.hasNext() ){
			
			int ends = compareEnds(aPeriod, bPeriod );
			if ( ends <= 0 ) {
				if ( !aIterator.hasNext() )
					break;
				aPeriod =  aIterator.next();
			}
			if ( ends >= 0 ) {
				if ( !bIterator.hasNext() )
					break;
				bPeriod =  bIterator.next();
			}
			
			Period intersectPeriod = aPeriod.intersect(bPeriod);
			if ( intersectPeriod != null ) {
				periods.add( intersectPeriod );
			}
		}
		
		return periods;
	}
	
	@Override
	public boolean equals(Object obj) {
		Period other = (Period) obj;
		return compare ( start, other.start ) == 0 && 
				compare ( end, other.end ) == 0;
	}	

    
    private static int compareEnds(Period a, Period b ) {
    	if ( b == null ){
    		return a == null ? 0 : 1; 
    	}
    	return  compare(a.end, b.end);
    		
    }

    private static Date next(Date date) {
    	return add(date, 1 );
    }

    private static Date prev(Date date) {
    	return add(date, -1 );
    }

    private static Date add(Date date, int amount) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	calendar.add(Calendar.DAY_OF_MONTH, amount);
    	return calendar.getTime();
    }
}
