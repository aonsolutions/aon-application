package es.aonsolutions.aio.test.config;


import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.code.aon.account.Account;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.util.AppParamUtil;
import com.esferalia.aon.watson.util.AonNumberUtils;

import es.aonsolutions.aio.test.AonHibernateTestBasic;
import es.aonsolutions.aio.test.AonHibernateTestFaker;


class AppParamTest extends AonHibernateTestBasic {
	
	@Test
	void testAccDefaultChargedVatAcc( ) throws Exception {
		ApplicationParameter param = checkIfExistAccountParam( "477", AppParam.ACC_DEFAULT_CHARGED_VAT_ACC );
		assertNotNull( param , "Parámetro null");
	}
	
	@Test
	void testAccDefaultPaidVatAcc() throws Exception {
		ApplicationParameter param = checkIfExistAccountParam( "472", AppParam.ACC_DEFAULT_CHARGED_VAT_ACC );
		assertNotNull( param , "Parámetro null");
	}
	@Test
	void testAccDefDuaVatAcc() throws Exception {
		ApplicationParameter param = checkIfExistAccountParam( "472", AppParam.ACC_DEF_DUA_VAT_ACC );
		assertNotNull( param , "Parámetro null" );
	}
	@Test
	void testAccVatNegativeAdjustAcc() throws Exception {
		ApplicationParameter param = checkIfExistAccountParam( "633", AppParam.ACC_VAT_NEGATIVE_ADJUST_ACC );
		assertNotNull( param , "Parámetro null");
	}
	
	private ApplicationParameter checkIfExistAccountParam(String prefix, AppParam parameter) throws Exception {
		ApplicationParameter param = AppParamUtil.getParameter( parameter );
		if ( param == null) {
			return insertAccountParam( prefix, parameter);
		}
		return param;
	}

	private ApplicationParameter insertAccountParam(String prefix, AppParam parameter) throws Exception {
		Account account = AonHibernateTestFaker.getAccount(prefix);
		assertNotNull( account , "Parámetro null");
		ApplicationParameter appParam = new ApplicationParameter();
		appParam.setName( parameter.toString() );
		appParam.setValue( AonNumberUtils.toString(account.getId()));
		return AppParamUtil.insertParameter( appParam );
	}
	
}
