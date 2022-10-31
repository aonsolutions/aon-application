package es.aonsolutions.aio.test.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.config.Tax;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.config.enumeration.WithholdingType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.watson.server.AonDateUtils;

import es.aonsolutions.aio.test.AonHibernateTestBasic;


class TaxTest extends AonHibernateTestBasic {
	
	@Test
	void testInsertTax_vat04() throws Exception {
		if (!exists(TaxType.VAT, 4)) {
			Tax iva = new Tax();
			iva.setName( "IVA SUPER REDUCIDO");
			iva.setType( TaxType.VAT );
			iva.setPercentage( 4.0 );
			iva.setSurcharge( 0.5 );
			iva.setStartDate( AonDateUtils.getYearFirstDay( 2015 ));
			iva.setVatDeductionType(VatDeductionType.WITHOUT_RIGHT);
			iva.setWithholdingType( null );
			IManagerBean bean = BeanManager.getManagerBean(Tax.class);
			assertDoesNotThrow( () -> bean.insert(iva),"Fallo al insertar impuesto IVA - 4" );
		}
	}
	
	@Test
	void testInsertTax_vat05() throws Exception {
		if (!exists(TaxType.VAT,5)) {
			Tax iva = new Tax();
			iva.setName( "IVA SUPER REDUCIDO (5)");
			iva.setType( TaxType.VAT );
			iva.setPercentage( 5.0 );
			iva.setSurcharge( 1.2 );
			iva.setStartDate( AonDateUtils.getYearFirstDay( 2015 ));
			iva.setVatDeductionType(VatDeductionType.WITHOUT_RIGHT);
			iva.setWithholdingType( null );
			IManagerBean bean = BeanManager.getManagerBean(Tax.class);
			assertDoesNotThrow( () -> bean.insert(iva),"Fallo al insertar impuesto IVA - 5" );
		}
	}
	
	@Test
	void testInsertTax_vat10() throws Exception {
		if (!exists(TaxType.VAT,10)) {
			Tax iva = new Tax();
			iva.setName( "IVA REDUCIDO");
			iva.setType( TaxType.VAT );
			iva.setPercentage( 10.0 );
			iva.setSurcharge( 1.4 );
			iva.setStartDate( AonDateUtils.getYearFirstDay( 2015 ));
			iva.setVatDeductionType(VatDeductionType.WITHOUT_RIGHT);
			iva.setWithholdingType( null );
			IManagerBean bean = BeanManager.getManagerBean(Tax.class);
			assertDoesNotThrow( () -> bean.insert(iva),"Fallo al insertar impuesto IVA - 10" );
		}
	}
	
	@Test
	void testInsertTax_vat21() throws Exception {
		if (!exists(TaxType.VAT,21)) {
			Tax iva = new Tax();
			iva.setName( "IVA GENERAL");
			iva.setType( TaxType.VAT );
			iva.setPercentage( 21.0 );
			iva.setSurcharge( 5.2 );
			iva.setStartDate( AonDateUtils.getYearFirstDay( 2015 ));
			iva.setVatDeductionType(VatDeductionType.WITHOUT_RIGHT);
			iva.setWithholdingType( null );
			IManagerBean bean = BeanManager.getManagerBean(Tax.class);
			assertDoesNotThrow( () -> bean.insert(iva),"Fallo al insertar impuesto IVA - 21" );
		}
	}

	@Test
	void testInsertTax_irpf15() throws Exception {
		if (!exists(TaxType.RETENTION,15)) {
			Tax iva = new Tax();
			iva.setName( "IRPF PROFESIONAL");
			iva.setType( TaxType.RETENTION );
			iva.setPercentage( 15.0 );
			iva.setSurcharge( 0 );
			iva.setStartDate( AonDateUtils.getYearFirstDay( 2015 ));
			iva.setVatDeductionType(  null );
			iva.setWithholdingType( WithholdingType.PROFESSIONAL );
			IManagerBean bean = BeanManager.getManagerBean(Tax.class);
			assertDoesNotThrow( () -> bean.insert(iva),"Fallo al insertar impuesto RET - 15" );
		}
	}

	@Test
	void testInsertTax_irpf19() throws Exception {
		if (!exists(TaxType.RETENTION,19)) {
			Tax iva = new Tax();
			iva.setName( "IRPF ALQUILER");
			iva.setType( TaxType.RETENTION );
			iva.setPercentage( 19.0 );
			iva.setSurcharge( 0 );
			iva.setStartDate( AonDateUtils.getYearFirstDay( 2015 ));
			iva.setVatDeductionType(  null );
			iva.setWithholdingType( WithholdingType.RENTING );
			IManagerBean bean = BeanManager.getManagerBean(Tax.class);
			assertDoesNotThrow( () -> bean.insert(iva),"Fallo al insertar impuesto RET - 19" );
		}
	}

	private boolean exists( TaxType type, double percent ) throws Exception {
		IManagerBean bean = BeanManager.getManagerBean(Tax.class);
		Criteria c = new Criteria();
		c.addExpression( ExpressionUtilities.getEqualExpression( bean.getFieldName(IEntityAlias.TAX_TYPE), type)) ;
		c.addExpression( ExpressionUtilities.getEqualExpression( bean.getFieldName(IEntityAlias.TAX_PERCENTAGE), percent )) ;
		int count = bean.getCount(c);
		return count>0;
	}


}
