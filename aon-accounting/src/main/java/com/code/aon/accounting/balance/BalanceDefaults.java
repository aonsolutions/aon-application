package com.code.aon.accounting.balance;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.accounting.Balance;
import com.code.aon.accounting.BalanceDetail;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.BalanceType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;

public class BalanceDefaults {
	
	private static final String CLOSING_BALANCE_FILE = "closing_balance.txt";
	private static final String OPERATING_BALANCE_FILE = "operating_balance.txt";
	private static final String ABBREVIATED_CLOSING_BALANCE_FILE = "abbreviated_closing_balance.txt";
	private static final String ABBREVIATED_OPERATING_BALANCE_FILE = "abbreviated_operating_balance.txt";
	private static final String ABBREVIATED_PATRIMONY_BALANCE_FILE = "abbreviated_patrimony_balance.txt";
	
	public Balance reloadBalance(Balance balance) throws ManagerBeanException{
		try {
			IManagerBean bean = BeanManager.getManagerBean(Balance.class);
			IManagerBean detailBean = BeanManager.getManagerBean(BalanceDetail.class);
			Integer id = balance.getId();
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(detailBean.getFieldName(IAccountingAlias.BALANCE_DETAIL_BALANCE_ID) , id);
			List<ITransferObject> list = detailBean.getList(criteria);
			for (ITransferObject to : list) {
				detailBean.remove(to);
			}
			balance = loadBalance(balance);
			balance = (Balance) bean.update(balance);
			insertDetails(balance);
			return balance;
		} catch (IOException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		}
	}

	private void insertDetails(Balance balance) throws ManagerBeanException, IOException {
		if (balance.getId() == 1) {
			loadClosingBalance(balance);
		} else if (balance.getId() == 2) {
			loadOperatingBalance(balance);
		} else if (balance.getId() == 3) {
			loadAbbreviatedClosingBalance(balance);
		} else if (balance.getId() == 4) {
			loadAbbreviatedOperatingBalance(balance);
		} else if (balance.getId() == 5) {
			loadAbbreviatedPatrimonyBalance(balance);
		}
		
	}

	private Balance loadBalance(Balance balance) {
		if (balance.getId() == 1) {
			balance.setName("BALANCE DE SITUACIÓN");
			balance.setRemovable(false);
			balance.setType(BalanceType.CLOSING);
			return balance;
		} else if (balance.getId() == 2) { 
			balance.setName("CUENTA DE EXPLOTACIÓN");
			balance.setRemovable(false);
			balance.setType(BalanceType.OPERATING);
			return balance;
		} else if (balance.getId() == 3) { 
			balance.setName("BALANCE DE SITUACIÓN (ABREVIADO)");
			balance.setRemovable(false);
			balance.setType(BalanceType.CLOSING);
			return balance;
		} else if (balance.getId() == 4) { 
			balance.setName("CUENTA DE EXPLOTACIÓN (ABREVIADA)");
			balance.setRemovable(false);
			balance.setType(BalanceType.OPERATING);
			return balance;
		} else if (balance.getId() == 5) {
			balance.setName("ESTADO DE CAMBIOS EN EL PATRIMONIO NETO (ABREVIADO)");
			balance.setRemovable(false);
			balance.setType(BalanceType.PATRIMONY);
			return balance;
		}
		return null;
	}
	
	private void loadAbbreviatedPatrimonyBalance(Balance balance) throws ManagerBeanException, IOException {
		loadFromFile(balance,ABBREVIATED_PATRIMONY_BALANCE_FILE);
	}
	private void loadAbbreviatedOperatingBalance(Balance balance) throws ManagerBeanException, IOException {
		loadFromFile(balance,ABBREVIATED_OPERATING_BALANCE_FILE);
	}

	private void loadAbbreviatedClosingBalance(Balance balance) throws ManagerBeanException, IOException {
		loadFromFile(balance,ABBREVIATED_CLOSING_BALANCE_FILE);
	}

	private void loadOperatingBalance(Balance balance) throws ManagerBeanException, IOException {
		loadFromFile(balance,OPERATING_BALANCE_FILE);
	}

	private void loadClosingBalance(Balance balance) throws ManagerBeanException, IOException {
		loadFromFile(balance,CLOSING_BALANCE_FILE);
	}

	private void loadFromFile(Balance balance, String file) throws ManagerBeanException, IOException {
		IManagerBean detailBean = BeanManager.getManagerBean(BalanceDetail.class);
		InputStream in = BalanceDefaults.class.getResourceAsStream(file);
		InputStreamReader isr = new InputStreamReader(in); 
		LineNumberReader reader = new LineNumberReader( isr );
		while (reader.ready()) {
			String line = reader.readLine();
			System.out.println( line );
			String[] tokens = StringUtils.splitPreserveAllTokens(line, '|');
			BalanceDetail bd = new BalanceDetail();
			bd.setBalance(balance);
			bd.setCode(tokens[0]);
			bd.setDescription(tokens[1]);
			bd.setAccounts(tokens[2]);
			bd.setSortKey( Integer.parseInt(tokens[3]));
			bd.setTitle( Boolean.parseBoolean(tokens[4]));
			bd.setInternalCalculation( Boolean.parseBoolean(tokens[5]));
			bd.setVisible( Boolean.parseBoolean(tokens[6]));
			bd.setZeroFlag( Boolean.parseBoolean(tokens[7]));
			bd.setCreditNature( Boolean.parseBoolean(tokens[8]));
			detailBean.insert(bd);			
		}
	}
	
	public static void main(String[] args) throws ManagerBeanException, IOException {
		BalanceDefaults bd = new BalanceDefaults();
//		bd.loadFromFile(null,ABBREVIATED_PATRIMONY_BALANCE_FILE);
//		bd.loadFromFile(null,ABBREVIATED_OPERATING_BALANCE_FILE);
//		bd.loadFromFile(null,ABBREVIATED_CLOSING_BALANCE_FILE);
//		bd.loadFromFile(null,OPERATING_BALANCE_FILE);
		bd.loadFromFile(null,CLOSING_BALANCE_FILE);
	}
}
