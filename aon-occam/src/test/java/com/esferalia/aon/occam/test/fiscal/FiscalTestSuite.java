package com.esferalia.aon.occam.test.fiscal;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.test.fiscal.mod111.Mod111TestSuite;
import com.esferalia.aon.occam.test.fiscal.mod115.Mod115TestSuite;
import com.esferalia.aon.occam.test.fiscal.mod123.Mod123TestSuite;
import com.esferalia.aon.occam.test.fiscal.mod130.Mod130TestSuite;
import com.esferalia.aon.occam.test.fiscal.mod190.Mod190TestSuite;
import com.esferalia.aon.occam.test.fiscal.mod303.Mod303TestSuite;
import com.esferalia.aon.occam.test.fiscal.mod349.Mod349TestSuite;
import com.esferalia.aon.occam.test.fiscal.mod390hf.Mod390HFTestSuite;
import com.esferalia.aon.occam.test.fiscal.model.FiscalModelTestSuite;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@RunWith(Suite.class)
@SuiteClasses({
	FiscalModelTestSuite.class,
	Mod111TestSuite.class,
	Mod115TestSuite.class,
	Mod123TestSuite.class,
	Mod303TestSuite.class,
	Mod390HFTestSuite.class,
	Mod130TestSuite.class,
	
	Mod190TestSuite.class,
	
	Mod349TestSuite.class,
	
//	Mod131TestSuite.class,
//	Mod202TestSuite.class,
})
public class FiscalTestSuite {

	private static final int lineSize = 126;
	private static NumberFormat FMT = DecimalFormat.getInstance();

	public static <T extends IFiscalModel> String toString( T mod ) {
		return AonStringUtils.leftPad(mod.getAdministration().getDescription(), 20)
			+ " Modelo "
			+ AonStringUtils.rightPad(mod.getModelFullName(), 30)
			+ AonStringUtils.leftPad(FMT.format(AonNumberUtils.zeroIfNull(mod.getDeclarationResult())),25)
			+ AonStringUtils.leftPad(mod.getStatus().getName(),35);
	}
	
	public static <T extends IFiscalModel> T printModel( T mod ) {
		System.out.println( "\t" + toString(mod));
		return mod; 
	}

	public static AccountEntry print( AccountEntry entry ) {
		System.out.println(AonStringUtils.repeat(AonStringUtils.HYPHEN, lineSize));
		System.out.println( toString(entry)); 
		System.out.println(AonStringUtils.repeat(AonStringUtils.DOT , lineSize));
		double sumD = 0.0;
		double sumC = 0.0;
		for (AccountEntryDetail aed : entry.getDetails()) {
			System.out.println( toString(aed));
			sumD = AonMathUtils.sum(sumD, aed.getDebit());	
			sumC = AonMathUtils.sum(sumC, aed.getCredit());
		}
		System.out.println(AonStringUtils.leftPad(AonStringUtils.repeat(AonStringUtils.DOT, lineSize / 4 ),lineSize));
		System.out.println(toString(sumD,sumC));
		System.out.println(AonStringUtils.repeat(AonStringUtils.HYPHEN, lineSize));
		System.out.println();
		return entry; 
	}
	
	public static String toString(AccountEntry entry) {
		StringBuilder buf = new StringBuilder();
		buf.append(AonStringUtils.repeat(AonStringUtils.SPACE, 30));
		buf.append(AonStringUtils.repeat(AonStringUtils.SPACE, 8));
		buf.append("Fecha");
		buf.append(AonStringUtils.COLON);
		buf.append(AonStringUtils.SPACE);
		buf.append(new SimpleDateFormat("dd/MM/yyyy").format(entry.getEntryDate()));
		buf.append(AonStringUtils.SPACE);
		buf.append("Diario");
		buf.append(AonStringUtils.COLON);
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.rightPad(entry.getJournal()==null?"????":""+entry.getJournal(),10));
		buf.append(AonStringUtils.leftPad(entry.getEntryType().getDescription(), lineSize - buf.length()));
		return buf.toString();
	}
	
	public static String toString(double deb, double cre) {
		return toString(null,null,null,deb,cre,null,null);
	}
	
	
	public static String toString(AccountEntryDetail detail) {
		return toString(detail.getAccountCode()
			,detail.getAccountDescription()
			,detail.getConcept()
			,detail.getDebit()		
			,detail.getCredit()
			,detail.getBalancingAccountCode()
			,detail.getDocumentNumber());
	}
	
	public static String toString(String ac,String ad,String c,double deb,double cre,String bc,String dn) {
		StringBuffer buf = new StringBuffer();
		buf.append(AonStringUtils.rightPad(AonStringUtils.defaultString(ac),10));
		buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate(AonStringUtils.defaultString(ad), 40), 41));
		buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate( AonStringUtils.defaultString(c), 40), 41));
		buf.append(AonStringUtils.leftPad(FMT.format(deb),17));		
		buf.append(AonStringUtils.leftPad(FMT.format(cre),17));
		buf.append(AonStringUtils.center(AonStringUtils.defaultString(bc),11));
		buf.append(AonStringUtils.rightPad(AonStringUtils.defaultString(dn), 20));
		return buf.toString();
	}
}
