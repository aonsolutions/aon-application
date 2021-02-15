package net.aonsolutions.aon.tedi.test.pdf;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.InvoiceType;

public enum TestTemplates {

		AON_01_AUSARTA ("/net/aonsolutions/aon/tedi/test/pdf/AON-01-AUSARTA.pdf") {
			@Override public Date getDate() {return Date.from(LocalDateTime.of(2020, 10, 2, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference() {return "PR2020023542";}
			@Override public String getSenderDocument() {return "B95868188";}
			@Override public String getReceiverDocument() {return "B01487271";}
			@Override public int getTaxNumber() {return 1;}
			@Override public Double getTaxBase21() {return 530.39;}
			@Override public Double getTaxQuota21() {return 111.38;}
			@Override public Double getTotal() {return 641.77;}
			// -------------------
			@Override public InvoiceType getInvoiceType() {return InvoiceType.EXPENSES;}
		},
		AON_02_BNP ("/net/aonsolutions/aon/tedi/test/pdf/AON-02-BNP.pdf") {
			@Override public Date getDate() {return Date.from(LocalDateTime.of(2020, 10, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "FBF71670"; }
			@Override public String getSenderDocument() {return "W0013547E";}
			@Override public String getReceiverDocument() {return "B01487271";}
			@Override public int getTaxNumber() {return 1;}
			@Override public Double getTaxBase21() {return 99.95;}
			@Override public Double getTaxQuota21() {return 20.99;}
			@Override public Double getTotal() {return 120.94;}
		},
		AON_03_BIP_DRIVE ("/net/aonsolutions/aon/tedi/test/pdf/AON-03-BIP-DRIVE.pdf") {
			@Override public Date getDate() {return Date.from(LocalDateTime.of(2020, 10, 31, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "CI0003829550-1020"; }
			@Override public String getSenderDocument() {return "A86969607";}
			@Override public String getReceiverDocument() {return "B01487271";}
			@Override public int getTaxNumber() {return 1;}
			@Override public Double getTaxBase21() {return 1.74;}
			@Override public Double getTaxQuota21() {return 0.37;}
			@Override public Double getTotal() {return 2.11;}
		},
		AON_04_BIP_DRIVE ("/net/aonsolutions/aon/tedi/test/pdf/AON-04-BIP-DRIVE.pdf") {
			@Override public Date getDate() {return Date.from(LocalDateTime.of(2020, 9, 30, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "CI0003726395-0920"; }
			@Override public String getSenderDocument() {return "A86969607";}
			@Override public String getReceiverDocument() {return "B01487271";}
			@Override public int getTaxNumber() {return 1;}
			@Override public Double getTaxBase21() {return 38.63;}
			@Override public Double getTaxQuota21() {return 8.11;}
			@Override public Double getTotal() {return 46.74;}
		},
		AON_05_TRANSLOGIA("/net/aonsolutions/aon/tedi/test/pdf/AON-05-TRANSLOGIA.pdf") {
			@Override public Date getDate() {return Date.from(LocalDateTime.of(2020, 2, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "CENIT/000023"; }
			@Override public String getSenderDocument() {return "B66941873";}
			@Override public String getReceiverDocument() {return "B01487271";}
			@Override public int getTaxNumber() {return 1;}
			@Override public Double getTaxBase21() {return 2050.0;}
			@Override public Double getTaxQuota21() {return 430.50;}
			@Override public Double getTotal() {return 2480.5;}
		},
		AON_06_TRANSLOGIA("/net/aonsolutions/aon/tedi/test/pdf/AON-06-TRANSLOGIA.pdf") {
			@Override public Date getDate() {return Date.from(LocalDateTime.of(2020, 8, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "CENIT/000035"; }
			@Override public String getSenderDocument() {return "B66941873";}
			@Override public String getReceiverDocument() {return "B01487271";}
			@Override public int getTaxNumber() {return 2;}
			@Override public Double getTaxBase21() {return 2050.0;}
			@Override public Double getTaxQuota21() {return 430.50;}
			@Override public Double getTotal() {return 2091.00;}
		},
		AON_07_TOLEDO("/net/aonsolutions/aon/tedi/test/pdf/AON-07-TOLEDO.pdf") {
			@Override public Date getDate() {return Date.from(LocalDateTime.of(2020, 10, 13, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "C/023452"; }
			@Override public String getSenderDocument() {return "B82435074";}
			@Override public String getReceiverDocument() {return "B01487271";}
			@Override public int getTaxNumber() {return 1;}
			@Override public Double getTaxBase21() {return 185.0;}
			@Override public Double getTaxQuota21() {return 38.85;}
			@Override public Double getTotal(){ return 223.85; }
		},
		AON_08_VODAFONE ("/net/aonsolutions/aon/tedi/test/pdf/AON-08-VODAFONE.pdf") {
			@Override public Date getDate() {return Date.from(LocalDateTime.of(2020, 11, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "ZB20-000748377"; }
			@Override public String getSenderDocument() {return "B87539284";}
			@Override public String getReceiverDocument() {return "B01487271";}
			@Override public int getTaxNumber() {return 1;}
			@Override public Double getTaxBase21() {return 87.19;}
			@Override public Double getTaxQuota21() {return 18.31;}
			@Override public Double getTotal(){ return 105.50; }
		},
		AON_09_VUELING ("/net/aonsolutions/aon/tedi/test/pdf/AON-09-VUELING.pdf") {
			@Override public Date getDate() {return Date.from(LocalDateTime.of(2020, 10, 9, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "C202000000327308"; }
			@Override public String getSenderDocument() {return "A63422141";}
			@Override public String getReceiverDocument() {return "B01487271";}
			@Override public int getTaxNumber() {return 1;}
			@Override public Double getTaxBase10() {return 149.07;}
			@Override public Double getTaxQuota10() {return 14.91;}
			@Override public Double getTotal(){ return 163.98; }
		},
		AON_2021_02_03_AMAZON ("/net/aonsolutions/aon/tedi/test/pdf/AON-2021-02-03-AMAZON.pdf") {
			@Override public Date getDate() {return Date.from(LocalDateTime.of(2021, 2, 3, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "EUINES21-1701"; }
			@Override public String getSenderDocument() {return "W0185696B";}
			@Override public String getReceiverDocument() {return "B01487271";}
			@Override public int getTaxNumber() {return 1;}
			@Override public Double getTaxBase0() {return 1609.64;}
			@Override public Double getTaxQuota0() {return 0.0;}
			@Override public Double getTotal(){ return 1609.64; }
		},
		AON_2020_12_31_BK ("/net/aonsolutions/aon/tedi/test/pdf/AON-2020-12-31-BK.pdf") {
			@Override public Date getDate() {return Date.from(LocalDateTime.of(2020, 12, 31, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return null; }
			@Override public String getSenderDocument() {return "A01314319";}
			@Override public String getReceiverDocument() {return "B01487271";}
			@Override public int getTaxNumber() {return 1;}
			@Override public Double getTaxBase0() {return 927.96;}
			@Override public Double getTaxQuota0() {return 0.0;}
			@Override public Double getTotal(){ return 927.96; }
		},
		AON_2021_02_01_RCR ("/net/aonsolutions/aon/tedi/test/pdf/AON-2021-02-01-RCR.pdf") {
			@Override public Date getDate() {return Date.from(LocalDateTime.of(2021, 2, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "PO03384/21"; }
			@Override public String getSenderDocument() {return "B02230407";}
			@Override public String getReceiverDocument() {return "B98267552";}
			@Override public int getTaxNumber() {return 1;}
			@Override public Double getTaxBase21() {return 21.0;}
			@Override public Double getTaxQuota21() {return 4.41;}
			@Override public Double getTotal(){ return 25.41; }
		},
		
		
		;

		private String file;
		
		private TestTemplates(String file) {
			this.file = file;
		}
		
		public String getFile() {
			return file;
		}
		
		public abstract Date getDate();
		public abstract String getReference();
		public abstract String getSenderDocument();
		public abstract String getReceiverDocument();
		public abstract int getTaxNumber();
		public abstract Double getTotal();
		
		public Double getTaxBase21() {return null;}
		public Double getTaxQuota21() {return null;}
		public Double getTaxBase10() {return null;}
		public Double getTaxQuota10() {return null;}
		public Double getTaxBase4() {return null;}
		public Double getTaxQuota4() {return null;}
		public Double getTaxBase0() {return null;}
		public Double getTaxQuota0() {return null;}

		public InvoiceType  getInvoiceType() {return null;}
		public String getSeries() {return null;}
		public Integer getNumber() {return 0;}
		
	}
