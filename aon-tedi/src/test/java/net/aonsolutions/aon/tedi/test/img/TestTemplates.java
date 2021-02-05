package net.aonsolutions.aon.tedi.test.img;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public enum TestTemplates {

	AON_01_RESTAURANTE_7 ("/net/aonsolutions/aon/tedi/test/img/01_RESTAURANTE_7.jpg") {
		@Override public Date getDate() {return Date.from(LocalDateTime.of(2019, 12, 7, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
		@Override public String getSenderDocument() {return "16272662R";}
		@Override public String getReceiverDocument() {return "F01131978";}
		@Override public int getTaxNumber() {return 1;}
		@Override public Double getTaxBase10() {return 32.55;}
		@Override public Double getTaxQuota10() {return 3.25;}
		@Override public Double getTaxBase21() {return null;}
		@Override public Double getTaxQuota21() {return null;}
		@Override public Double getTotal(){ return 35.8; }
	},
	AON_02_QUINTANAPALLA_AREAS ("/net/aonsolutions/aon/tedi/test/img/02_QUINTANAPALLA_AREAS.jpg") {
		@Override public Date getDate() {return Date.from(LocalDateTime.of(2019, 7, 7, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
		@Override public String getSenderDocument() {return "A08225013";}
		@Override public String getReceiverDocument() {return "F01131978";}
		@Override public int getTaxNumber() {return 1;}
		@Override public Double getTaxBase10() {return 13.50;}
		@Override public Double getTaxQuota10() {return 1.35;}
		@Override public Double getTotal(){ return 14.85; }
	},
	AON_03_ERKIAGA ("/net/aonsolutions/aon/tedi/test/img/03-ERKIAGA.jpg") {
		@Override public Date getDate() {return Date.from(LocalDateTime.of(2019, 11, 16, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
		@Override public String getSenderDocument() {return "J01542877";}
		@Override public String getReceiverDocument() {return "F01131978";}
		@Override public int getTaxNumber() {return 1;}
		@Override public Double getTaxBase10() {return 10.27;}
		@Override public Double getTaxQuota10() {return 1.03;}
		@Override public Double getTotal(){ return 11.30; }
	}
	;

	private String file;
	
	private TestTemplates(String file) {
		this.file = file;
	}
	
	public String getFile() {
		return file;
	}
	
	public abstract Date getDate();
	public abstract String getSenderDocument();
	public abstract String getReceiverDocument();
	public abstract int getTaxNumber();
	public abstract Double getTotal();
	
	public Double getTaxBase0() {return null;}
	public Double getTaxQuota0() {return null;}
	public Double getTaxBase4() {return null;}
	public Double getTaxQuota4() {return null;}
	public Double getTaxBase10() {return null;}
	public Double getTaxQuota10() {return null;}
	public Double getTaxBase21() {return null;}
	public Double getTaxQuota21() {return null;}
		
}
