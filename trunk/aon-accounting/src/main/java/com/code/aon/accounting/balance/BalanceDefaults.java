package com.code.aon.accounting.balance;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.accounting.Balance;
import com.code.aon.accounting.BalanceDetail;
import com.code.aon.accounting.enumeration.BalanceType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class BalanceDefaults {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BalanceDefaults.class.getName()); 

	private static final String CLOSING_BALANCE_FILE = "closing_balance.txt";
	private static final String OPERATING_BALANCE_FILE = "operating_balance.txt";
	private static final String ABBREVIATED_CLOSING_BALANCE_FILE = "abbreviated_closing_balance.txt";
	private static final String ABBREVIATED_OPERATING_BALANCE_FILE = "abbreviated_operating_balance.txt";
	private static final String ABBREVIATED_PATRIMONY_BALANCE_FILE = "abbreviated_patrimony_balance.txt";
	
	public Balance reloadBalance(Balance balance, int type) throws ManagerBeanException{
		//inicio transaccion
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// BEGIN operaciones de la transaccion
				IManagerBean bean = BeanManager.getManagerBean(Balance.class);
				IManagerBean detailBean = BeanManager.getManagerBean(BalanceDetail.class);
				Integer id = balance.getId();
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.BALANCE_DETAIL_BALANCE_ID) , id);
				List<ITransferObject> list = detailBean.getList(criteria);
				for (ITransferObject to : list) {
					detailBean.remove(to);
				}
				balance = loadBalance(balance,type);
				balance = (Balance) HibernateUtil.getSession(sessionName).merge(balance);
				balance = (Balance) bean.update(balance);				
				insertDetails(balance,type);
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);

				return balance;
			} catch (Exception e) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.error(msg, e);
				}
				throw new ManagerBeanException(e.getMessage(),e);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private void insertDetails(Balance balance, int type) throws ManagerBeanException, IOException {
		if (type == 1) {
			loadClosingBalance(balance);
		} else if (type == 2) {
			loadOperatingBalance(balance);
		} else if (type == 3) {
			loadAbbreviatedClosingBalance(balance);
		} else if (type == 4) {
			loadAbbreviatedOperatingBalance(balance);
		} else if (type == 5) {
			loadAbbreviatedPatrimonyBalance(balance);
		}
		
	}

	private Balance loadBalance(Balance balance,int type) {
		if (type == 1) {
			balance.setName("BALANCE DE SITUACIÓN");
			balance.setRemovable(false);
			balance.setType(BalanceType.CLOSING);
			return balance;
		} else if (type == 2) { 
			balance.setName("CUENTA DE EXPLOTACIÓN");
			balance.setRemovable(false);
			balance.setType(BalanceType.OPERATING);
			return balance;
		} else if (type == 3) { 
			balance.setName("BALANCE DE SITUACIÓN (ABREVIADO)");
			balance.setRemovable(false);
			balance.setType(BalanceType.CLOSING);
			return balance;
		} else if (type == 4) { 
			balance.setName("CUENTA DE EXPLOTACIÓN (ABREVIADA)");
			balance.setRemovable(false);
			balance.setType(BalanceType.OPERATING);
			return balance;
		} else if (type == 5) {
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
		InputStreamReader isr = new InputStreamReader(in,Charset.forName("ISO-8859-1")); 
		LineNumberReader reader = new LineNumberReader( isr );
		while (reader.ready()) {
			String line = reader.readLine();
			if (!StringUtils.isEmpty(line)) {
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
	}

}
