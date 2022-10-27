package es.aonsolutions.aio.test.config;

import org.junit.Test;

import com.code.aon.account.Account;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

import es.aonsolutions.aio.test.util.Asserts;

public class AccountListTest  {
	
	@Test
	public void testListAccount() throws Exception {
		IManagerBean bean = BeanManager.getManagerBean(Account.class);
		Criteria c = new Criteria();
		c.addEqualExpression( bean.getFieldName(IEntityAlias.ACCOUNT_DOMAIN) , 400);
		Asserts.assertNotEmptyCollection( "Empty account list", bean.getList(c));
		
	}


}
