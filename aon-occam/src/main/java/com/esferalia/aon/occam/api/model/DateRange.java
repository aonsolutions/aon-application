package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.watson.server.AonDateUtils;

public class DateRange implements Serializable {
	
	private static final long serialVersionUID = 4011730153730627017L;
	
	private Date from;
	private Date to;
	
	public Date getFrom() {
		return from;
	}
	public DateRange setFrom(Date from) {
		this.from = from;
		return this;
	}
	
	public Date getTo() {
		return to;
	}
	public DateRange setTo(Date to) {
		this.to = to;
		return this;
	}
	
	public java.sql.Date getSqlFrom() {
		return AonDateUtils.toSql( from );
	}
	public java.sql.Date getSqlTo() {
		return AonDateUtils.toSql( to );
	}
	
	public static DateRange of(Date from, Date to) {
		return new DateRange()
			.setFrom(from)
			.setTo(to);
	}

}
