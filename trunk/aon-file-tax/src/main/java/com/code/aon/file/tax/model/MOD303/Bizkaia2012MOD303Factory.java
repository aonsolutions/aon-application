
package com.code.aon.file.tax.model.MOD303;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.file.format.core.DiskRegisterLoader;
import com.code.aon.file.format.model.AbstractFileFiller;
import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.tax.model.MOD303.data.Breakdown;
import com.code.aon.file.tax.model.MOD303.data.Declaration;

public class Bizkaia2012MOD303Factory implements IMOD303Factory {
	@Override
	public List<Exception> createDocument(List<Declaration> declarations, Writer out) {
		PrintWriter pw = (out instanceof PrintWriter)?(PrintWriter) out: new PrintWriter(out);
		List<Exception> exceptions = new LinkedList<Exception>();
		try {
			Bizkaia2012MOD303 fileFiller = new Bizkaia2012MOD303(declarations,pw);
			exceptions.addAll(fileFiller.create());
			pw.flush();
		} catch (Throwable e) {
			if (e instanceof Exception) {
				exceptions.add((Exception) e);	
			} else {
				exceptions.add(new UnexpectedException( e.getMessage() ));
			}
			
		}
		return exceptions;
	}
	
	private class Bizkaia2012MOD303 extends AbstractFileFiller {
		private static final String DECLARATION = "Declaration";
		private static final String HEADER = "Header";
		private static final String HEADER_METADATA = "/com/code/aon/file/tax/model/MOD303/xml/2012_BIZKAIA_Header.xml";
		private static final String BANK_DATA = "BankData";
		private static final String BANK_DATA_METADATA = "/com/code/aon/file/tax/model/MOD303/xml/2012_BIZKAIA_BankData.xml";
		private static final String DEPONENT = "Deponent";
		private static final String DEPONENT_METADATA = "/com/code/aon/file/tax/model/MOD303/xml/2012_BIZKAIA_Deponent.xml";
		private static final String ENTRY = "Entry_";
		private static final String ENTRY_MR_METADATA = "/com/code/aon/file/tax/model/MOD303/xml/2012_BIZKAIA_MR.xml";
		private static final String ENTRY_EP_METADATA = "/com/code/aon/file/tax/model/MOD303/xml/2012_BIZKAIA_EP.xml";
		private static final String ENTRY_P2_METADATA = "/com/code/aon/file/tax/model/MOD303/xml/2012_BIZKAIA_P2.xml";
		private static final String ENTRY_P3_METADATA = "/com/code/aon/file/tax/model/MOD303/xml/2012_BIZKAIA_P3.xml";
		private static final String ENTRY_FE_METADATA = "/com/code/aon/file/tax/model/MOD303/xml/2012_BIZKAIA_FE.xml";
		private static final String ENTRY_N0_METADATA = "/com/code/aon/file/tax/model/MOD303/xml/2012_BIZKAIA_N0.xml";
		private static final String ENTRY_D2_METADATA = "/com/code/aon/file/tax/model/MOD303/xml/2012_BIZKAIA_D2.xml";
		private static final String ENTRY_D3_METADATA = "/com/code/aon/file/tax/model/MOD303/xml/2012_BIZKAIA_D3.xml";
		private static final String ENTRY_TX_METADATA = "/com/code/aon/file/tax/model/MOD303/xml/2012_BIZKAIA_TX.xml";
		private static final String ENTRY_IM_METADATA = "/com/code/aon/file/tax/model/MOD303/xml/2012_BIZKAIA_IM.xml";
		
		private List<Declaration> declarations;
		
		public Bizkaia2012MOD303(List<Declaration> declarations, PrintWriter out) throws FileNotFoundException, UnsupportedEncodingException {
			super(out);
			if (declarations == null || declarations.size() == 0)  {
				throw new IllegalArgumentException("Declaration can not be null!");
			}
			this.declarations = declarations;
			DiskRegisterLoader.load(MOD303.class.getResourceAsStream(HEADER_METADATA), manager);
			DiskRegisterLoader.load(MOD303.class.getResourceAsStream(BANK_DATA_METADATA), manager);
			DiskRegisterLoader.load(MOD303.class.getResourceAsStream(DEPONENT_METADATA), manager);
			DiskRegisterLoader.load(MOD303.class.getResourceAsStream(ENTRY_MR_METADATA), manager);
			DiskRegisterLoader.load(MOD303.class.getResourceAsStream(ENTRY_EP_METADATA), manager);
			DiskRegisterLoader.load(MOD303.class.getResourceAsStream(ENTRY_P2_METADATA), manager);
			DiskRegisterLoader.load(MOD303.class.getResourceAsStream(ENTRY_P3_METADATA), manager);
			DiskRegisterLoader.load(MOD303.class.getResourceAsStream(ENTRY_FE_METADATA), manager);
			DiskRegisterLoader.load(MOD303.class.getResourceAsStream(ENTRY_N0_METADATA), manager);
			DiskRegisterLoader.load(MOD303.class.getResourceAsStream(ENTRY_D2_METADATA), manager);
			DiskRegisterLoader.load(MOD303.class.getResourceAsStream(ENTRY_D3_METADATA), manager);
			DiskRegisterLoader.load(MOD303.class.getResourceAsStream(ENTRY_TX_METADATA), manager);
			DiskRegisterLoader.load(MOD303.class.getResourceAsStream(ENTRY_IM_METADATA), manager);
		}
		
		public ArrayList<Exception> create() {
			Map<String,Object> properties = new HashMap<String,Object>();
			try {
				for (Declaration declaration : declarations) {
					properties.put(DECLARATION, declaration);
					createLine(HEADER,properties);
					if (StringUtils.isNotBlank(declaration.getCcc1())) {
						createLine(BANK_DATA,properties);	
					}
					createLine(DEPONENT,properties);
					List<Entry> entries = getEntries(declaration);
					for (Entry entry: entries) {
						String id = ENTRY + entry.getType().toString();
						properties.put("Entries", entry);
						createLine(id,properties);
					}
				}
				getOutput().flush();
			} catch (Throwable ex) {
				if ( ex instanceof Fd0Exception ) {
					getExceptions().add ((Exception) ex);
				} 
				else {
					ex.printStackTrace();
					Fd0Exception e = new Fd0Exception( ex.getMessage(),"");
					getExceptions().add (e);
				}
			}
			return getExceptions();
		}

		private List<Entry> getEntries(Declaration d) {
			List<Entry> entries = new LinkedList<Entry>();
			String c = (d.isComplementary()?"X":" "); 
			entries.add( new Entry().setCode(01).setType(EntryType.MR).setMark(c).setText(c) );
			c = (d.isTaxRefundRegistry()?"X":" ");
			entries.add( new Entry().setCode( 2).setType(EntryType.MR).setMark(c).setText(c) );
			
			// *************
			// IVA DEVENGADO
			// *************
			String[] percents = new String[]{"4.0","8.0","18.0"};
			int[] keys = new int[]{3,5,7};
			for (int i = 0; i < percents.length; i++ ) {
				Breakdown bd = d.getOutputVat().get(percents[i]);
				if (bd != null) {
					int key = keys[i];  
					entries.add( new Entry().setCode(key).setType(EntryType.IM).setAmountD( bd.getTaxableBase() ));
					entries.add( new Entry().setCode(++key).setType(EntryType.IM).setAmountD( bd.getQuota() ));
				}
			}
			
			percents = new String[]{"0.5","1.0","4.0","1.75"};
			keys = new int[]{9,11,13,15};
			for (int i = 0; i < percents.length; i++ ) {
				Breakdown bd = d.getSurcharge().get(percents[i]);
				if (bd != null) {
					int key = keys[i];  
					entries.add( new Entry().setCode(key).setType(EntryType.IM).setAmountD( bd.getTaxableBase() ));
					entries.add( new Entry().setCode(++key).setType(EntryType.IM).setAmountD( bd.getQuota() ));
				}
			}
			
			entries.add( new Entry().setCode(17).setType(EntryType.IM).setAmountD( d.getBaseIntracommunitary() ));
			entries.add( new Entry().setCode(18).setType(EntryType.IM).setAmountD( d.getQuotaIntracommunitary() ));

			entries.add( new Entry().setCode(19).setType(EntryType.IM).setAmountD( d.getBaseInvPasive() ));
			entries.add( new Entry().setCode(20).setType(EntryType.IM).setAmountD( d.getQuotaInvPasive() ));
			
			entries.add( new Entry().setCode(21).setType(EntryType.IM).setAmountD( d.getBaseModifications() ));
			entries.add( new Entry().setCode(22).setType(EntryType.IM).setAmountD( d.getQuotaModifications() ));
			
			entries.add( new Entry().setCode(23).setType(EntryType.IM).setAmountD( d.getOutputTotal() ));
			
			// *************
			// IVA DEDUCIBLE
			// *************
			entries.add( new Entry().setCode(24).setType(EntryType.IM).setAmountD( d.getInnerOperationsTotalQuota() ));
			entries.add( new Entry().setCode(25).setType(EntryType.IM).setAmountD( d.getImportedOperationsTotalQuota() ));
			entries.add( new Entry().setCode(26).setType(EntryType.IM).setAmountD( d.getIntracommunitaryOperationsTotalQuota() ));
			entries.add( new Entry().setCode(27).setType(EntryType.IM).setAmountD( d.getAgriculturalRegimeCompensation() ));
			entries.add( new Entry().setCode(28).setType(EntryType.IM).setAmountD( d.getInvestmentNormalization() ));
			entries.add( new Entry().setCode(29).setType(EntryType.IM).setAmountD( d.getProrataNormalization() ));
			entries.add( new Entry().setCode(30).setType(EntryType.IM).setAmountD( d.getDeductTotal() ));
			entries.add( new Entry().setCode(31).setType(EntryType.IM).setAmountD( d.getDifference() ));
			
			// ***********
			// LIQUIDACION
			// ***********
			entries.add( new Entry().setCode(32).setType(EntryType.P3).setPercent3( d.getBizkaiaPercent() ));
			entries.add( new Entry().setCode(33).setType(EntryType.IM).setAmountD( d.getQuota() ));
			entries.add( new Entry().setCode(34).setType(EntryType.IM).setAmountD( d.getPreviousYearCompensateQuota() ));
			entries.add( new Entry().setCode(35).setType(EntryType.IM).setAmountD( d.getRegularizationResult() ));
			entries.add( new Entry().setCode(36).setType(EntryType.IM).setAmountD( d.getResult() ));
			
			// *********
			// RESULTADO
			// *********
			c = (d.isWithoutActivity()?"X":" ");
			entries.add( new Entry().setCode(37).setType(EntryType.MR).setMark(c).setText(c) );
			entries.add( new Entry().setCode(38).setType(EntryType.IM).setAmountD( d.getCompensate() ));
			entries.add( new Entry().setCode(39).setType(EntryType.IM).setAmountD( d.getPayBack() ));
			entries.add( new Entry().setCode(40).setType(EntryType.IM).setAmountD( d.getDeposit() ));
			
			entries.add( new Entry().setCode(41).setType(EntryType.IM).setAmountD( d.getPreviousDeposit() ));			
			entries.add( new Entry().setCode(42).setType(EntryType.IM).setAmountD( d.getPreviousPayBack() ));			
			entries.add( new Entry().setCode(43).setType(EntryType.IM).setAmountD( d.getTotalDebt() ));
			
			percents = new String[]{"4.0","8.0","18.0","?"};
			keys = new int[]{50,53,56,59};
			for (int i = 0; i < percents.length; i++ ) {
				Breakdown bd = d.getInnerAssetPurchases().get(percents[i]);
				if (bd != null) {
					int key = keys[i];  
					entries.add( new Entry().setCode(key).setType(EntryType.IM).setAmountD( bd.getTaxableBase() ));
					entries.add( new Entry().setCode(++key).setType(EntryType.IM).setAmountD( bd.getQuota() ));
					entries.add( new Entry().setCode(++key).setType(EntryType.IM).setAmountD( bd.getDeductibleQuota() ));
				}
			}
			entries.add( new Entry().setCode(65).setType(EntryType.IM).setAmountD( d.getBaseInnerAssetPurchases() ));			
			entries.add( new Entry().setCode(66).setType(EntryType.IM).setAmountD( d.getQuotaInnerAssetPurchases() ));			
			entries.add( new Entry().setCode(67).setType(EntryType.IM).setAmountD( d.getDeductibleQuotaInnerAssetPurchases() ));
			
			percents = new String[]{"4.0","8.0","18.0","?"};
			keys = new int[]{68,71,74,77};
			for (int i = 0; i < percents.length; i++ ) {
				Breakdown bd = d.getExpenses().get(percents[i]);
				if (bd != null) {
					int key = keys[i];  
					entries.add( new Entry().setCode(key).setType(EntryType.IM).setAmountD( bd.getTaxableBase() ));
					entries.add( new Entry().setCode(++key).setType(EntryType.IM).setAmountD( bd.getQuota() ));
					entries.add( new Entry().setCode(++key).setType(EntryType.IM).setAmountD( bd.getDeductibleQuota() ));
				}
			}
			entries.add( new Entry().setCode(80).setType(EntryType.IM).setAmountD( d.getBaseExpenses() ));			
			entries.add( new Entry().setCode(81).setType(EntryType.IM).setAmountD( d.getQuotaExpenses() ));			
			entries.add( new Entry().setCode(82).setType(EntryType.IM).setAmountD( d.getDeductibleQuotaExpenses() ));
			
			percents = new String[]{"4.0","8.0","18.0","?"};
			keys = new int[]{83,86,89,92};
			for (int i = 0; i < percents.length; i++ ) {
				Breakdown bd = d.getInvestmentAsset().get(percents[i]);
				if (bd != null) {
					int key = keys[i];  
					entries.add( new Entry().setCode(key).setType(EntryType.IM).setAmountD( bd.getTaxableBase() ));
					entries.add( new Entry().setCode(++key).setType(EntryType.IM).setAmountD( bd.getQuota() ));
					entries.add( new Entry().setCode(++key).setType(EntryType.IM).setAmountD( bd.getDeductibleQuota() ));
				}
			}
			entries.add( new Entry().setCode(95).setType(EntryType.IM).setAmountD( d.getBaseInvestmentAsset()));			
			entries.add( new Entry().setCode(96).setType(EntryType.IM).setAmountD( d.getQuotaInvestmentAsset() ));			
			entries.add( new Entry().setCode(97).setType(EntryType.IM).setAmountD( d.getDeductibleQuotaInvestmentAsset() ));

			entries.add( new Entry().setCode(98).setType(EntryType.IM).setAmountD( d.getBaseTotalAddInfo()));			
			entries.add( new Entry().setCode(99).setType(EntryType.IM).setAmountD( d.getQuotaTotalAddInfo() ));			
			entries.add( new Entry().setCode(100).setType(EntryType.IM).setAmountD( d.getDeductibleQuotaTotalAddInfo() ));
			
			c = (d.isGeneralProrataApplied()?"X":" ");
			entries.add( new Entry().setCode(101).setType(EntryType.MR).setMark(c).setText(c) );
			c = (d.isSpecialProrataApplied()?"X":" ");
			entries.add( new Entry().setCode(102).setType(EntryType.MR).setMark(c).setText(c) );
			entries.add( new Entry().setCode(103).setType(EntryType.P3).setPercent3( d.getProrata() ));			
			
			entries.add( new Entry().setCode(104).setType(EntryType.IM).setAmountD( d.getExportationTotal() ));
			entries.add( new Entry().setCode(105).setType(EntryType.IM).setAmountD( d.getIntracommunitaryDeliveries() ));
			entries.add( new Entry().setCode(106).setType(EntryType.IM).setAmountD( d.getNonTaxableTotal() ));
			entries.add( new Entry().setCode(107).setType(EntryType.IM).setAmountD( d.getInvSujPasNotIncluded() ));
			entries.add( new Entry().setCode(108).setType(EntryType.IM).setAmountD( d.getPresIntraServices() ));
			
			return entries;
		}
		
	}

	private enum EntryType {
		MR,EP,P2,P3,FE,N0,D2,D3,TX,IM;
	}
	
	public class Entry {
		private EntryType type;
		private int code;
		private String mark;
		private String epigraph;
		private double percent2;
		private double percent3;
		private String date;
		private int integerNumber;
		private double number2;
		private double number3;
		private String text;
		private long amount;
		
		public EntryType getType() {
			return type;
		}
		public Entry setType(EntryType type) {
			this.type = type;
			return this;
		}
		public String getStringType() {
			return type.toString();
		}
		public int getCode() {
			return code;
		}
		public Entry setCode(int code) {
			this.code = code;
			return this;
		}
		public String getMark() {
			return mark;
		}
		public Entry setMark(String mark) {
			this.mark = mark;
			return this;
		}
		public String getEpigraph() {
			return epigraph;
		}
		public Entry setEpigraph(String epigraph) {
			this.epigraph = epigraph;
			return this;
		}
		public double getPercent2() {
			return percent2;
		}
		public Entry setPercent2(double percent2) {
			this.percent2 = percent2;
			return this;
		}
		public double getPercent3() {
			return percent3;
		}
		public Entry setPercent3(double percent3) {
			this.percent3 = percent3;
			return this;
		}
		public String getDate() {
			return date;
		}
		public Entry setDate(String date) {
			this.date = date;
			return this;
		}
		public int getIntegerNumber() {
			return integerNumber;
		}
		public Entry setIntegerNumber(int integerNumber) {
			this.integerNumber = integerNumber;
			return this;
		}
		public double getNumber2() {
			return number2;
		}
		public Entry setNumber2(double number2) {
			this.number2 = number2;
			return this;
		}
		public double getNumber3() {
			return number3;
		}
		public Entry setNumber3(double number3) {
			this.number3 = number3;
			return this;
		}
		public String getText() {
			return text;
		}
		public Entry setText(String text) {
			this.text = text;
			return this;
		}
		public long getAmount() {
			return amount;
		}
		public Entry setAmount(long amount) {
			this.amount = amount;
			return this;
		}
		public Entry setAmountD(double amount) {
			this.amount = (long) CommonUtil.round((amount * 100),0);
			return this;
		}
		
		
	}
}
