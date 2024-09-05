package es.aonsolutions.aio.test.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.code.aon.account.Account;
import com.code.aon.account.util.AccountUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.entity.IEntityAlias;

import es.aonsolutions.aio.test.AonHibernateTestBasic;
import es.aonsolutions.aio.test.util.Asserts;


class AccountTest extends AonHibernateTestBasic {
	
	private static final String ACCOUNT_DESCRIPTION = "Generada desde hibernate test";

	@Test
	void testInsertAccount() throws Exception {
		AccountUtil au = new AccountUtil();
		
		Account account = new Account();
		account.setCode( au.obtainNextAccountId("600"));
		account.setDescription(ACCOUNT_DESCRIPTION);
		account.setDomain( getDomain() );
		
		IManagerBean bean = BeanManager.getManagerBean(Account.class);
		assertDoesNotThrow( () -> bean.insert(account),"Fallo al insertar cuenta contable" );

		Criteria c = new Criteria();
		c.addExpression( ExpressionUtilities.getEqualExpression( bean.getFieldName(IEntityAlias.ACCOUNT_DESCRIPTION), ACCOUNT_DESCRIPTION)) ;
		c.addOrder( bean.getFieldName(IEntityAlias.ACCOUNT_ID) , false );
		List<ITransferObject> accounts = bean.getList(c,0,1);
		Asserts.assertNotEmptyCollection( "Empty account list", accounts );
		Account acc = (Account) accounts.get(0);
		Assertions.assertEquals( ACCOUNT_DESCRIPTION, acc.getDescription(), "Account not last inserted");
		
		
	}


}
