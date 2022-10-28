package es.aonsolutions.aio.test.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import com.code.aon.account.Account;
import com.code.aon.account.util.AccountUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;

import es.aonsolutions.aio.test.AonHibernateTestBasic;


class AccountTest extends AonHibernateTestBasic {
	
	@Test
	void testInsertAccount() throws Exception {
		AccountUtil au = new AccountUtil();
		
		Account account = new Account();
		account.setCode( au.obtainNextAccountId("600"));
		account.setDescription("Generada desde hibernate test");
		account.setDomain( getDomain() );
		
		IManagerBean bean = BeanManager.getManagerBean(Account.class);
		assertDoesNotThrow( () -> bean.insert(account),"Fallo al insertar cuenta contable" );
	}

//	@Test
//	void testListAccount() throws Exception {
//		IManagerBean bean = BeanManager.getManagerBean(Account.class);
//		Criteria c = new Criteria();
//		c.addEqualExpression( bean.getFieldName(IEntityAlias.ACCOUNT_DOMAIN) , 400);
//		Asserts.assertNotEmptyCollection( "Empty account list", bean.getList(c));
//	}


}
