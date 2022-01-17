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
			@Override public String getSenderName() {return "Ausarta Prima S.L";}
			@Override public String getReceiverDocument() {return "B01487271";}
			@Override public String getReceiverName() {return "AON Solutions, S.L.";}
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
			@Override public String getSenderName() {return "BNP PARIBAS LEASE GROUP S.A.";}
			@Override public String getReceiverDocument() {return "B01487271";}
			@Override public String getReceiverName() {return "AON Solutions, S.L.";}
			@Override public int getTaxNumber() {return 1;}
			@Override public Double getTaxBase21() {return 99.95;}
			@Override public Double getTaxQuota21() {return 20.99;}
			@Override public Double getTotal() {return 120.94;}
			// -------------------
			@Override public InvoiceType getInvoiceType() {return InvoiceType.EXPENSES;}
		},
		AON_03_BIP_DRIVE ("/net/aonsolutions/aon/tedi/test/pdf/AON-03-BIP-DRIVE.pdf") {
			@Override public Date getDate() {return Date.from(LocalDateTime.of(2020, 10, 31, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "CI0003829550-1020"; }
			@Override public String getSenderDocument() {return "A86969607";}
			@Override public String getSenderName() {return "Bip&Drive, E.D.E., S.A.";}
			@Override public String getReceiverDocument() {return "B01487271";}
			@Override public String getReceiverName() {return "AON Solutions, S.L.";}
			@Override public int getTaxNumber() {return 1;}
			@Override public Double getTaxBase21() {return 1.74;}
			@Override public Double getTaxQuota21() {return 0.37;}
			@Override public Double getTotal() {return 2.11;}
			// -------------------
			@Override public InvoiceType getInvoiceType() {return InvoiceType.EXPENSES;}
		},
		AON_04_BIP_DRIVE ("/net/aonsolutions/aon/tedi/test/pdf/AON-04-BIP-DRIVE.pdf") {
			@Override public Date getDate() {return Date.from(LocalDateTime.of(2020, 9, 30, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "CI0003726395-0920"; }
			@Override public String getSenderDocument() {return "A86969607";}
			@Override public String getSenderName() {return "Bip&Drive, E.D.E., S.A.";}
			@Override public String getReceiverDocument() {return "B01487271";}
			@Override public String getReceiverName() {return "AON Solutions, S.L.";}
			@Override public int getTaxNumber() {return 1;}
			@Override public Double getTaxBase21() {return 38.63;}
			@Override public Double getTaxQuota21() {return 8.11;}
			@Override public Double getTotal() {return 46.74;}
			// -------------------
			@Override public InvoiceType getInvoiceType() {return InvoiceType.EXPENSES;}
		},
		AON_05_TRANSLOGIA("/net/aonsolutions/aon/tedi/test/pdf/AON-05-TRANSLOGIA.pdf") {
			@Override public Date getDate() {return Date.from(LocalDateTime.of(2020, 2, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "CENIT/000023"; }
			@Override public String getSenderDocument() {return "B66941873";}
			@Override public String getSenderName() {return "TRANSLOGIA Development, S.L.";}
			@Override public String getReceiverDocument() {return "B01487271";}
			@Override public String getReceiverName() {return "AON Solutions, S.L.";}
			@Override public int getTaxNumber() {return 1;}
			@Override public Double getTaxBase21() {return 2050.0;}
			@Override public Double getTaxQuota21() {return 430.50;}
			@Override public Double getTotal() {return 2480.5;}
			// -------------------
			@Override public InvoiceType getInvoiceType() {return InvoiceType.EXPENSES;}
		},
		AON_06_TRANSLOGIA("/net/aonsolutions/aon/tedi/test/pdf/AON-06-TRANSLOGIA.pdf") {
			@Override public Date getDate() {return Date.from(LocalDateTime.of(2020, 8, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "CENIT/000035"; }
			@Override public String getSenderDocument() {return "B66941873";}
			@Override public String getSenderName() {return "TRANSLOGIA Development, S.L.";}
			@Override public String getReceiverDocument() {return "B01487271";}
			@Override public String getReceiverName() {return "AON Solutions, S.L.";}
			@Override public int getTaxNumber() {return 2;}
			@Override public Double getTaxBase21() {return 2050.0;}
			@Override public Double getTaxQuota21() {return 430.50;}
			@Override public Double getIRPFPercent() {return 19.0;}
			@Override public Double getIRPFBase() {return 2050.0;}
			@Override public Double getIRPFQuota() {return 389.5;}
			@Override public Double getTotal() {return 2091.00;}
			// -------------------
			@Override public InvoiceType getInvoiceType() {return InvoiceType.EXPENSES;}
		},
		AON_07_TOLEDO("/net/aonsolutions/aon/tedi/test/pdf/AON-07-TOLEDO.pdf") {
			@Override public Date getDate() {return Date.from(LocalDateTime.of(2020, 10, 13, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "C/023452"; }
			@Override public String getSenderDocument() {return "B82435074";}
			@Override public String getSenderName() {return "TOLEDO Y ASOCIADOS ASESORIA Y GESTION, S.L.";}
			@Override public String getReceiverDocument() {return "B01487271";}
			@Override public String getReceiverName() {return "AON Solutions, S.L.";}
			@Override public int getTaxNumber() {return 1;}
			@Override public Double getTaxBase21() {return 185.0;}
			@Override public Double getTaxQuota21() {return 38.85;}
			@Override public Double getTotal(){ return 223.85; }
			// -------------------
			@Override public InvoiceType getInvoiceType() {return InvoiceType.EXPENSES;}
		},
		AON_08_VODAFONE ("/net/aonsolutions/aon/tedi/test/pdf/AON-08-VODAFONE.pdf") {
			@Override public Date getDate() {return Date.from(LocalDateTime.of(2020, 11, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "ZB20-000748377"; }
			@Override public String getSenderDocument() {return "B87539284";}
			@Override public String getSenderName() {return "Vodafone Servicios, S.L.U.";}
			@Override public String getReceiverDocument() {return "B01487271";}
			@Override public String getReceiverName() {return "AON Solutions, S.L.";}
			@Override public int getTaxNumber() {return 1;}
			@Override public Double getTaxBase21() {return 87.19;}
			@Override public Double getTaxQuota21() {return 18.31;}
			@Override public Double getTotal(){ return 105.50; }
			// -------------------
			@Override public InvoiceType getInvoiceType() {return InvoiceType.EXPENSES;}
		},
		AON_09_VUELING ("/net/aonsolutions/aon/tedi/test/pdf/AON-09-VUELING.pdf") {
			@Override public Date getDate() {return Date.from(LocalDateTime.of(2020, 10, 9, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "C202000000327308"; }
			@Override public String getSenderDocument() {return "A63422141";}
			@Override public String getSenderName() {return "Vueling Airlines, S.A.";}
			@Override public String getReceiverDocument() {return "B01487271";}
			@Override public String getReceiverName() {return "AON Solutions, S.L.";}
			@Override public int getTaxNumber() {return 1;}
			@Override public Double getTaxBase10() {return 149.07;}
			@Override public Double getTaxQuota10() {return 14.91;}
			@Override public Double getTotal(){ return 163.98; }
			// -------------------
			@Override public InvoiceType getInvoiceType() {return InvoiceType.EXPENSES;}
		},
		AON_2021_02_03_AMAZON ("/net/aonsolutions/aon/tedi/test/pdf/AON-2021-02-03-AMAZON.pdf") {
			@Override public Date getDate() {return Date.from(LocalDateTime.of(2021, 2, 3, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "EUINES21-1701"; }
			@Override public String getSenderDocument() {return "W0185696B";}
			@Override public String getSenderName() {return "AMAZON WEB SERVICES EMEA SARL";}
			@Override public String getReceiverDocument() {return "B01487271";}
			@Override public String getReceiverName() {return "AON Solutions, S.L.";}
			@Override public int getTaxNumber() {return 1;}
			@Override public Double getTaxBase0() {return 1609.64;}
			@Override public Double getTaxQuota0() {return 0.0;}
			@Override public Double getTotal(){ return 1609.64; }
			// -------------------
			@Override public InvoiceType getInvoiceType() {return InvoiceType.EXPENSES;}
		},
		AON_2020_12_31_BK ("/net/aonsolutions/aon/tedi/test/pdf/AON-2020-12-31-BK.pdf") {
			@Override public Date getDate() {return Date.from(LocalDateTime.of(2020, 12, 31, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return null; }
			@Override public String getSenderDocument() {return "A01314319";}
			@Override public String getSenderName() {return "BK Consulting abogados y asesores S.A.";}
			@Override public String getReceiverDocument() {return "B01487271";}
			@Override public String getReceiverName() {return "AON Solutions, S.L.";}
			@Override public int getTaxNumber() {return 1;}
			@Override public Double getTaxBase0() {return 927.96;}
			@Override public Double getTaxQuota0() {return 0.0;}
			@Override public Double getTotal(){ return 927.96; }
			// -------------------
			@Override public InvoiceType getInvoiceType() {return InvoiceType.EXPENSES;}
		},
		AON_2019_01_02_UDAPA ("/net/aonsolutions/aon/tedi/test/pdf/AON_2019_01_02_UDAPA.pdf") {
			@Override public Date getDate() {return Date.from(LocalDateTime.of(2019, 01, 02, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return null; }
			@Override public String getSenderDocument() {return "B01487271";}
			@Override public String getSenderName() {return "AON Solutions, S.L.";}
			@Override public String getReceiverDocument() {return "F01131978";}
			@Override public String getReceiverName() {return "UDAPA, S.COOP.";}
			@Override public int getTaxNumber() {return 1;}
			@Override public Double getTaxBase21() {return 175.00;}
			@Override public Double getTaxQuota21() {return 36.75;}
			@Override public Double getTotal(){ return 211.75; }
			// -------------------
			@Override public InvoiceType getInvoiceType() {return InvoiceType.SALES;}
		},
		AON_2021_11_03_LEIRE ("/net/aonsolutions/aon/tedi/test/pdf/AON_2021_11_03_LEIRE.pdf") {
			@Override public Date getDate() {return Date.from(LocalDateTime.of(2021, 11, 03, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return null; }
			@Override public String getSenderDocument() {return "78919924R";}
			@Override public String getSenderName() {return "Leire Borrachero García";}
			@Override public String getReceiverDocument() {return "47812786H";}
			@Override public String getReceiverName() {return "Jessica Santiago Coto";}
			@Override public int getTaxNumber() {return 2;}
			@Override public Double getTaxBase21() {return 385.68;}
			@Override public Double getTaxQuota21() {return 80.99;}
			@Override public Double getIRPFBase() {return 385.68;}
			@Override public Double getIRPFPercent() {return 15.0;}
			@Override public Double getIRPFQuota() {return 57.85;}
			@Override public Double getTotal(){ return 408.82; }
			
			// -------------------
			@Override public InvoiceType getInvoiceType() {return null;}
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
		public abstract String getSenderName();
		public abstract String getReceiverDocument();
		public abstract String getReceiverName();
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

		public Double getIRPFBase() {return null;}
		public Double getIRPFPercent() {return null;}
		public Double getIRPFQuota() {return null;}

		public InvoiceType  getInvoiceType() {return null;}
		public String getSeries() {return null;}
		public Integer getNumber() {return 0;}
		
	}
