package com.code.aon.accounting;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.esferalia.aon.entity.master.BalanceDetailDB;

@Entity
@Table(name="balance_detail")
@Deprecated
public class BalanceDetail extends BalanceDetailDB {
		
	private static final long serialVersionUID = 1L;

	@Transient
	public int getLevel() {
		if ( !StringUtils.isEmpty(getCode())) {
			try {
				int c = Integer.parseInt(getCode());
				if (c%10000 == 0) return 0;
				if (c%1000 == 0) return 1;
				if (c%100 == 0) return 2;
				if (c%10 == 0) return 3;
			} catch (NumberFormatException e) {
				// Nothing.
			}
		}
		return 0;
	}
	
}