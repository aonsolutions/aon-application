package net.aonsolutions.occam.test.dao;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.server.AonObjectUtils;

import net.aonsolutions.occam.api.invoice.Invoice;
import net.aonsolutions.occam.dao.DAOUtils;
import net.aonsolutions.occam.dao.InvoiceDAO;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class InvoiceDAOTest extends AbstractOccamTest {

	@Test()
	void selectOneTest() {
		Optional<Invoice> domain = InvoiceDAO.get(ctx,p -> p.withDomain().eq( DOMAIN_ID ));
		assertTrue(domain.isPresent());
	}

	@Test()
	void selectNoneTest() {
		Optional<Invoice> domain = InvoiceDAO.get(ctx,p -> p.withId().eq( Integer.MIN_VALUE ));
		assertFalse(domain.isPresent());
	}

	@Test()
	void selectDirtyTest() {
		Optional<Invoice> domain = InvoiceDAO.get(ctx,p -> p.withDomain().eq( DOMAIN_ID ));
		assertTrue(domain.isPresent());
		assertFalse(domain.get().isDirty(), "Dirty flag not set" );
	}

	@Test()
	void emptyFilterTest() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> InvoiceDAO.get(ctx, null));
		assertEquals(DAOUtils.NULL_FILTER_MSG, e.getMessage());
	}

	@Test()
	void selectNoBuilderNoFacturyStreamTest() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> InvoiceDAO.getStream(ctx,p -> p.withDomain().eq( DOMAIN_ID ), null));
		assertEquals(DAOUtils.NULL_FACTORY_MSG, e.getMessage());
	}

	@Test()
	void selectNoBuilderStreamTest() {
		Stream<Invoice> domain = InvoiceDAO.getStream(ctx,p -> p.withDomain().eq( DOMAIN_ID ));
		assertTrue(domain.findAny().isPresent());
	}

	@Test()
	void selectStreamTest() {
		Stream<Invoice> domain = InvoiceDAO.getStream(ctx
			,p -> p.withDomain().eq( DOMAIN_ID )
		);
		assertTrue(domain.findAny().isPresent());
	}
	
	@Test
	void streamLimitTest() {
		long max = InvoiceDAO.getStream(ctx, p -> p.withRegistryName().like("a%"))
			.limit(10)
			.count();
		int rows = AonRandom.getInt(0, (int) max);
		long count = InvoiceDAO.getStream(ctx, p -> p.withRegistryName().like("a%")
			,b -> b.limit(0, rows))
		.count();
		assertEquals(count, rows, "Limit not working" );
	}
	
	@Test
	void filterTest() {
		Optional<Invoice> optInvoice = InvoiceDAO.get(ctx
			,p -> p.withDomain().eq( DOMAIN_ID )
			,b -> b.full());
		assertTrue(optInvoice.isPresent());
		Invoice expected = optInvoice.get();
		
		Optional<Invoice> optActual = InvoiceDAO.get(ctx
			,p -> p.withId().eq( expected.getId() )
			.and(p.withDomain().eq( expected.getDomain() ))
			.and(p.withType().eq( AonEnumUtils.getByte(expected.getType()) ))
			.and(p.withSeries().eq( expected.getSeries() ))
			.and(p.withNumber().eq( expected.getNumber() ))
			.and(p.withReferenceCode().eq( expected.getReferenceCode() ))
			.and(p.withIssueDate().eq( AonDateUtils.toSql(expected.getIssueDate())))
			.and(p.withTaxDate().eq( AonDateUtils.toSql(expected.getTaxDate())))
			.and(p.withConfidential().eq( AonEnumUtils.getByte( expected.isConfidential())))
			.and(p.withRegistry().eq( expected.getRegistry() ))
			.and(p.withRegistryDocument().eq( expected.getRegistryDocument() ))
			.and(p.withRegistryDocumentType().eq( AonEnumUtils.getByte(expected.getRegistryDocumentType())))
			.and(p.withRegistryDocumentCountry().eq( AonObjectUtils.ifPresent( expected.getRegistryDocumentCountry(), c -> c.value() ) ))
			.and(p.withRegistryName().eq( expected.getRegistryName() ))
			.and(p.withActivity().eq( AonObjectUtils.ifOptionalPresent( expected.getActivity(), a -> a.getId()) ))
			.and(p.withActivityDescription().eq( AonObjectUtils.ifOptionalPresent( expected.getActivity(), a -> a.getDescription()) ))
			.and(p.withActivityEpigraph().eq( AonObjectUtils.ifOptionalPresent( expected.getActivity(), a -> a.getEpigraph()) ))
			.and(p.withCreationUser().eq( expected.getCreationUser() ))
			.and(p.withCreationDate().eq( AonDateUtils.toTimestamp(expected.getCreationDate() )))
			.and(p.withModificationUser().eq( expected.getModificationUser() ))
			.and(p.withModificationDate().eq( AonDateUtils.toTimestamp(expected.getModificationDate() )))
			,b -> b.full()
		);
		assertTrue(optActual.isPresent(),"Invoice not found!");
		Asserts.assertEqualsInvoice(expected, optActual.get());
	}

}
