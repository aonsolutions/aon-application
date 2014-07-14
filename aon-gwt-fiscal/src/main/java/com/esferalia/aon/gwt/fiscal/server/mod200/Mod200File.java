package com.esferalia.aon.gwt.fiscal.server.mod200;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.gwt.common.shared.CompanyAdministrator;
import com.esferalia.aon.gwt.common.shared.CompanyParticipation;
import com.esferalia.aon.gwt.common.shared.LegalRepresentative;
import com.esferalia.aon.gwt.common.shared.Secretary;
import com.esferalia.aon.gwt.fiscal.shared.mod200.DoubleVariable;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key;

public class Mod200File {
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final DateFormat DATE_FORMAT2 = new SimpleDateFormat("ddMMyy");
	private static final String EMPTY_DATE = "00000000";
	private static final CompanyAdministrator EMPTY_COMPANY_ADMINISTRATOR = new CompanyAdministrator();
	private static final CompanyParticipation EMPTY_COMPANY_PARTICIPATION = new CompanyParticipation();
	
	private Mod200 mod200;
	private Map<String,Double> keys;
	
	private String pages;
	private Secretary secretary;
	private List<LegalRepresentative> legalRepresentatives;	 
	
	public Mod200File(Mod200 mod200) {
		this.mod200 = mod200;
		if (this.mod200.getSecretary() == null) {
			this.secretary = new Secretary();
		} else {
			this.secretary = this.mod200.getSecretary(); 
		}
		legalRepresentatives = this.mod200.getRepresentatives();
		if (legalRepresentatives == null) {
			legalRepresentatives = new LinkedList<LegalRepresentative>();
		}
		for (int i=0; i < 3; i++) {
			if (i >= this.mod200.getRepresentatives().size()) {
				legalRepresentatives.add(new LegalRepresentative());		
			}
		}
	}

	public Mod200 getMod200() {
		return mod200;
	}
	public int getYear() {
		return getMod200().getYear();
	}
	public String getStartDate() {
		if (getMod200().getPeriodStart() != null ) {
			return DATE_FORMAT.format(getMod200().getPeriodStart());
		}
		return EMPTY_DATE;
	}
	public String getStartDate2() {
		if (getMod200().getPeriodStart() != null ) {
			return DATE_FORMAT2.format(getMod200().getPeriodStart());
		}
		return EMPTY_DATE;
	}
	public String getEndDate() {
		if (getMod200().getPeriodEnd() != null ) {
			return DATE_FORMAT.format(getMod200().getPeriodEnd());
		}
		return EMPTY_DATE;
	}
	public String getEndDate2() {
		if (getMod200().getPeriodEnd() != null ) {
			return DATE_FORMAT2.format(getMod200().getPeriodEnd());
		}
		return EMPTY_DATE;
	}
	
	public int getPeriodType() {
		return getMod200().getPeriodType();
	}
	
	public int getCnae() {
		// TODO
		String cnae = StringUtils.replace(getMod200().getCnae(), AonUtil.DOT, AonUtil.EMPTY); 
		return ensureZero(cnae);
	}
	public String getDocument() {
		return getMod200().getEnterpriseDocument();
	}

	public String getName() {
		return getMod200().getEnterpriseName();
	}

	public String getPhone1() {
		return getMod200().getEnterprisePhone1();
	}

	public String getPhone2() {
		return getMod200().getEnterprisePhone2();
	}
	
	public int getBalance() {
		return getMod200().getBalanceType().ordinal() + 1;
	}

	public int getPyg() {
		return getMod200().getPygType().ordinal() + 1;
	}
	
	public int getComplementary() {
		return getMod200().isComplementary()?1:0;
	}
	public String getComplementaryReceipt() {
		return AonUtil.leftPad(getMod200().getComplementaryReceipt(), 13, AonUtil.DOT);
	}
	public String getFiscalGroup() {
		return getMod200().getFiscalGroup();
	}
	public String getDominantDocument() {
		return getMod200().getDominantDocument();
	}
	public String getIrnr() {
		if (getMod200().getSecretary() != null &&  getMod200().getSecretary().getIrnr() != null ) {
			return DATE_FORMAT.format(getMod200().getSecretary().getIrnr());
		}
		return EMPTY_DATE;
	}
	public String getNotaryDate1() {
		return getNotaryDate(0);	}
	public String getNotaryDate2() {
		return getNotaryDate(1);
	}
	public String getNotaryDate3() {
		return getNotaryDate(1);
	}
	public String getNotaryDate(int index) {
		if (getMod200().getRepresentatives().get(index).getNotaryDate() != null ) {
			return DATE_FORMAT.format(getMod200().getRepresentatives().get(index).getNotaryDate());
		}
		return EMPTY_DATE;
	}
	public CompanyAdministrator getCompanyAdministrator(int index) {
		if (getMod200().getAdministrators() != null && getMod200().getAdministrators().size() > index) {
			return getMod200().getAdministrators().get(index);
		}
		return EMPTY_COMPANY_ADMINISTRATOR;
	}
	public CompanyAdministrator getAdm0() {
		return getCompanyAdministrator(0);
	}
	public CompanyAdministrator getAdm1() {
		return getCompanyAdministrator(1);
	}
	public CompanyAdministrator getAdm2() {
		return getCompanyAdministrator(2);
	}
	public CompanyAdministrator getAdm3() {
		return getCompanyAdministrator(3);
	}
	public CompanyAdministrator getAdm4() {
		return getCompanyAdministrator(4);
	}
	public CompanyAdministrator getAdm5() {
		return getCompanyAdministrator(5);
	}
	
	private CompanyParticipation getCompanyParticipation(List<CompanyParticipation> participationsOut, int index) {
		if (getMod200().getParticipationsOut() != null && getMod200().getParticipationsOut().size() > index) {
			return getMod200().getParticipationsOut().get(index);
		}
		return EMPTY_COMPANY_PARTICIPATION;
	}
	public CompanyParticipation getParOut0() {
		return getCompanyParticipation(getMod200().getParticipationsOut(),0);
	}
	public CompanyParticipation getParOut1() {
		return getCompanyParticipation(getMod200().getParticipationsOut(),1);
	}
	public CompanyParticipation getParOut2() {
		return getCompanyParticipation(getMod200().getParticipationsOut(),2);
	}
	public CompanyParticipation getParOut3() {
		return getCompanyParticipation(getMod200().getParticipationsOut(),3);
	}
	public CompanyParticipation getParIn0() {
		return getCompanyParticipation(getMod200().getParticipationsIn(),0);
	}
	public CompanyParticipation getParIn1() {
		return getCompanyParticipation(getMod200().getParticipationsIn(),1);
	}
	public CompanyParticipation getParIn2() {
		return getCompanyParticipation(getMod200().getParticipationsIn(),2);
	}
	public CompanyParticipation getParIn3() {
		return getCompanyParticipation(getMod200().getParticipationsIn(),3);
	}
	public CompanyParticipation getParIn4() {
		return getCompanyParticipation(getMod200().getParticipationsIn(),4);
	}
	public CompanyParticipation getParIn5() {
		return getCompanyParticipation(getMod200().getParticipationsIn(),5);
	}
	
	public double getSum1(){
		return 0.0;
	}
	public double getSum2(){
		return 0.0;
	}


	public Map<String, Double> getKeys() {
		if (keys == null) {
			keys = new Map<String, Double>() {

				@Override
				public void clear() {
					throw new UnsupportedOperationException();
				}

				@Override
				public boolean containsKey(Object arg0) {
					return getMod200().getKeysMap().containsKey(arg0);
				}

				@Override
				public boolean containsValue(Object arg0) {
					return getMod200().getKeysMap().containsValue(arg0);
				}

				@Override
				public Set<java.util.Map.Entry<String, Double>> entrySet() {
					throw new UnsupportedOperationException();
				}

				@Override
				public Double get(Object obj) {
					String keyString = (String) obj;
					Mod200Key key = Mod200Key.valueOf(keyString);
					DoubleVariable dv = getMod200().getKeysMap().get(key);
					Double ret = null;
					if (dv != null) {
						ret = AonUtil.round( dv.getValue());
					}
					return ret;  
				}

				@Override
				public boolean isEmpty() {
					return getMod200().getKeysMap().isEmpty();
				}

				@Override
				public Set<String> keySet() {
					throw new UnsupportedOperationException();
				}

				@Override
				public Double put(String arg0, Double arg1) {
					throw new UnsupportedOperationException();
				}

				@Override
				public void putAll(Map<? extends String, ? extends Double> arg0) {
					throw new UnsupportedOperationException();
				}

				@Override
				public Double remove(Object arg0) {
					throw new UnsupportedOperationException();
				}

				@Override
				public int size() {
					return getMod200().getKeysMap().size();
				}

				@Override
				public Collection<Double> values() {
					throw new UnsupportedOperationException();
				}
				
			};
		}
		return keys;
	}
	
	public String getPages() {
		return pages;
	}
	public Secretary getSecretary() {
		return secretary;
	}

	public List<LegalRepresentative> getLegalRepresentatives() {
		return legalRepresentatives;
	}
	
	private int ensureZero(String value) {
		if (AonUtil.isEmpty(value)) {
			return AonUtil.ZERO;
		}
		if (AonUtil.isNumeric(value)) {
			return Integer.parseInt(value);
		}
		throw new IllegalArgumentException("'" + value + "' no es un número válido.");
	}

}
