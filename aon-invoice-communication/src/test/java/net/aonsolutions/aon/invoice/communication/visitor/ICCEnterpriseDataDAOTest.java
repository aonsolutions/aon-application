package net.aonsolutions.aon.invoice.communication.visitor;
 
import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_ADMINISTRATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.EnterpriseData;
import com.esferalia.aon.occam.impl.jooq.dao.EnterpriseDataDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

import net.aonsolutions.aon.verifactu.AbstractVerifactuTest;
import net.aonsolutions.aon.verifactu.Environment;


class ICCEnterpriseDataDAOTest extends AbstractVerifactuTest {

	@Override protected Environment getEnvironment() { return ICC_CONFIG_ENV; }
	
	private void reset() {
		int count = getCtx().getDslContext()
			.delete(ENTERPRISE_DATA)
			.where(ENTERPRISE_DATA.DOMAIN.eq(getDomainId()))
			.and(ENTERPRISE_DATA.NAME.in( InvoiceCommunicationDAO.SUPPORTED_TYPES))
			.execute();
		getCtx().log().info("Configuración borrada para el dominio {0}: {1} registros eliminados", getDomainId(), count);
	}
	
	@Test
	void test_empty_date() {
		reset();
		AONContext ctx = getCtx();
		EnterpriseData toSave = new EnterpriseData()
			.setName( ICC_ADMINISTRATION.name() );
		AonCoreException e = assertThrows( AonCoreException.class, () -> EnterpriseDataDAO.save( ctx, toSave));
		assertEquals( AonError.EMPTY_START_DATE.getMessage() , e.getMessage() );
	}
	
	private Pair<Pair<String, String>, Pair<String, String>> pair(String s1, String s2, String p1, String p2) {
		Pair<String, String> s = Pair.<String, String>of(s1, s2);
		Pair<String, String> p = Pair.<String, String>of(p1, p2);
		return Pair.of(s, p);
	}

	@Test
	void test_overlap() {
		AONContext ctx = getCtx();
		String pattern = "dd/MM/yyyy";
		List<Pair<Pair<String, String>, Pair<String, String>>> pairs = List.of(
			pair("01/01/2024", "31/01/2024"	, "15/01/2024", "15/02/2024"),
			pair("01/01/2024", "31/01/2024"	, "15/12/2023", "15/01/2024"),
			pair("01/01/2024", "31/01/2024"	, "15/12/2023", "15/02/2024"),
			pair("01/01/2024", null			, "15/02/2024", null		), 
			pair("01/01/2024", null			, "15/12/2023", null		),
			pair("01/01/2024", null			, "15/12/2023", "15/02/2024")
		);
		
		for (Pair<Pair<String, String>, Pair<String, String>> p : pairs) {
			reset();
			System.out.println(
				"pair...: " + AonStringUtils.rightPad( p.getLeft().getLeft(), 15) + AonStringUtils.rightPad( p.getLeft().getRight(), 15) + AonStringUtils.rightPad( p.getRight().getLeft(), 15) + AonStringUtils.rightPad( p.getRight().getRight(), 15)
					); 
			Date start1 = AonDateUtils.parse(p.getLeft().getLeft(), pattern);
			Date end1 = AonDateUtils.parse(p.getLeft().getRight(), pattern);
			EnterpriseData first = new EnterpriseData()
				.setName( ICC_ADMINISTRATION.name() )
				.setStartDate( start1 )
				.setEndDate( end1 ); 
			EnterpriseDataDAO.save( ctx, first); 
			assertNotNull(first); 
			assertNotNull(first.getId());
			
			Date start2 = AonDateUtils.parse(p.getRight().getLeft(), pattern);
			Date end2 = AonDateUtils.parse(p.getRight().getRight(), pattern);
			EnterpriseData second = new EnterpriseData()
				.setName( ICC_ADMINISTRATION.name() )
				.setStartDate( start2 )
				.setEndDate( end2 )
			; 
			AonCoreException e = assertThrows( AonCoreException.class, () -> EnterpriseDataDAO.save( ctx, second));
			assertEquals( AonError.ACCOUNT_PERIOD_END_OVERLAP.format(second.getName()) , e.getMessage() );
			
		}
	}

	@Test
	void test_no_overlap() {
		AONContext ctx = getCtx();
		String pattern = "dd/MM/yyyy";
		List<Pair<Pair<String, String>, Pair<String, String>>> pairs = List.of(
			    pair("01/01/2024", "01/01/2024", "02/01/2024", "02/01/2024"),	// Primer rango y segundo rango días seguidos
			    pair("01/01/2024", "10/01/2024", "11/01/2024", "20/01/2024"),	// Primer rango y segundo rango no se solapan
			    pair("21/01/2024", "25/01/2024", "26/01/2024", "31/01/2024"),	// Segundo rango empieza justo después del primero
			    pair("01/02/2024", "05/02/2024", "10/02/2024", "15/02/2024"),	// Rangos completamente separados
			    pair("01/02/2024", "10/03/2024", "11/03/2024", "20/03/2024"),			// Primer rango abierto al principio, segundo rango posterior
			    pair("21/03/2024", "25/03/2024", "26/03/2024", null),			// Primer rango posterior, segundo rango abierto al final
			    pair("01/04/2024", "05/04/2024", "06/04/2024", "10/04/2024")	// Ambos rangos cerrados, separados
			);	
		
		for (Pair<Pair<String, String>, Pair<String, String>> p : pairs) {
			reset();
			System.out.println(
				"pair...: " + AonStringUtils.rightPad( p.getLeft().getLeft(), 15) + AonStringUtils.rightPad( p.getLeft().getRight(), 15) + AonStringUtils.rightPad( p.getRight().getLeft(), 15) + AonStringUtils.rightPad( p.getRight().getRight(), 15)
					); 
			Date start1 = AonDateUtils.parse(p.getLeft().getLeft(), pattern);
			Date end1 = AonDateUtils.parse(p.getLeft().getRight(), pattern);
			EnterpriseData first = new EnterpriseData()
				.setName( ICC_ADMINISTRATION.name() )
				.setStartDate( start1 )
				.setEndDate( end1 ); 
			EnterpriseDataDAO.save( ctx, first); 
			assertNotNull(first); 
			assertNotNull(first.getId());
			
			Date start2 = AonDateUtils.parse(p.getRight().getLeft(), pattern);
			Date end2 = AonDateUtils.parse(p.getRight().getRight(), pattern);
			EnterpriseData second = new EnterpriseData()
				.setName( ICC_ADMINISTRATION.name() )
				.setStartDate( start2 )
				.setEndDate( end2 )
			; 
			EnterpriseDataDAO.save( ctx, second);
			
		}
	}
	
	
	@Test
	void test_simple() {
		reset();
		Date today = AonDateUtils.today();
		EnterpriseData toSave = EnterpriseDataDAO.save(getCtx(),
			new EnterpriseData()
				.setName( ICC_ADMINISTRATION.name() )
				.setStartDate( today )
		);
		Optional<EnterpriseData> saved = EnterpriseDataDAO.get( getCtx(), getDomainId(), toSave.getId());
		assertNotNull(saved);
		assertTrue(saved.isPresent());
		EnterpriseData ed = saved.get();
		assertEquals(ICC_ADMINISTRATION.name(), ed.getName());
		assertEquals(ICC_ADMINISTRATION, ed.getDataName());
		assertNull(ed.getExpression());
		assertNotNull(ed.getStartDate());
		assertTrue( AonDateUtils.isSameDay( today, ed.getStartDate()));
		assertNull(ed.getEndDate());
		assertNotNull(ed.getCreationDate());
		assertNotNull(ed.getCreationUser());
		assertEquals(getUser(), ed.getCreationUser());
	}

	
}
