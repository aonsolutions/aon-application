package com.esferalia.aon.shared.commons;

import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.enumeration.AccountPeriodStatus;
import com.code.aon.common.enumeration.SecurityLevel;
import com.esferalia.aon.master.impl.server.sql.AonDAOError;
import com.esferalia.aon.master.impl.server.sql.AonDAOException;

public class AonEnumUtils {

	public static Byte getByte(Enum<?> enume) {
		return 	(enume == null) ? null : (byte) enume.ordinal();
	}

	public static boolean getBoolean(Byte value) throws AonDAOException {
		if (value == null) return false;
		return (value == 1);
	}

	public static AccountPeriodStatus getAccountPeriodStatus(Byte value) throws AonDAOException {
		if (value == null) {
			throw new AonDAOException(AonDAOError.ACCOUNT_PERIOD_INVALID_STATUS,"null"); 
		}
		try {
			return AccountPeriodStatus.values()[ value ];
		} catch (IndexOutOfBoundsException e) {
			throw new AonDAOException(AonDAOError.ACCOUNT_PERIOD_INVALID_STATUS, value);
		}
	}
	
	
	public static AccountEntryType getAccountEntryType(Byte value) throws AonDAOException {
		if (value == null) {
			throw new AonDAOException(AonDAOError.ACCOUNT_ENTRY_INVALID_STATUS,"null"); 
		}
		try {
			return AccountEntryType.values()[ value ];
		} catch (IndexOutOfBoundsException e) {
			throw new AonDAOException(AonDAOError.ACCOUNT_ENTRY_INVALID_STATUS, value);
		}
	}

	public static SecurityLevel getSecurityLevel(Byte value) {
		if (value == null) return null;
		try {
			return SecurityLevel.values()[ value ];
		} catch (IndexOutOfBoundsException e) {
			throw new AonDAOException(AonDAOError.ACCOUNT_ENTRY_INVALID_SEC_LEVEL, value);
		}
	}
	
}
