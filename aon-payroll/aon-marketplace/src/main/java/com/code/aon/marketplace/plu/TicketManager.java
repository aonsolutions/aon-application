package com.code.aon.marketplace.plu;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TicketManager{

	private Map<String,TicketSet> ticketsets;
	
	public TicketManager(){
		super();
		this.ticketsets= new HashMap<String,TicketSet>();
	}
	
	public void addTicket(Ticket ticket){
		TicketSet set = ticketsets.get(ticket.getCode());
		if (set == null) {
			set = new TicketSet(ticket.getSaleDate());
			ticketsets.put(set.getCode(), set);
		}
		set.getTicketlist().add(ticket);
	}
	
	public TicketSet getTicketSet(String code){
		return ticketsets.get(code);
	}
	
	@SuppressWarnings("unchecked")
	public Iterator<Object> getAll() {
		Collection<TicketSet> valuesC = ticketsets.values();
		Object[] tArray = valuesC.toArray();
		Arrays.sort(tArray, new Comparator() {
				public int compare(final Object o1, final Object o2){
					TicketSet t1 = (TicketSet)o1;
					TicketSet t2 = (TicketSet)o2;
					final String s1 = t1.getCode();
					final String s2 = t2.getCode();
					return s2.compareToIgnoreCase(s1);
				}
			}
		);
		return (Arrays.asList(tArray)).iterator();
		//return ticketsets.values();
	}
}
