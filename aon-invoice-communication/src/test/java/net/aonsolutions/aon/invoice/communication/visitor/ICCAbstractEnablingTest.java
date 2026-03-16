package net.aonsolutions.aon.invoice.communication.visitor;
 
import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.text.MessageFormat;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.verifactu.AbstractVerifactuTest;
import net.aonsolutions.aon.verifactu.Environment;


abstract class ICCAbstractEnablingTest extends AbstractVerifactuTest {

	@Override protected Environment getEnvironment() { return ICC_CONFIG_ENV; }
	
	protected void printIcc(InvoiceCommunicationConfiguration icc) {
		System.out.println( " ------- InvoiceCommunicationConfiguration" );
		icc.dataStream()
			.forEach( ed -> {
				String startDate = MessageFormat.format("{0,date,dd/MM/yyyy}", ed.getStartDate());
				String endDate = ed.getEndDate() == null
					? "--/--/----"
					:MessageFormat.format("{0,date,dd/MM/yyyy}", ed.getEndDate());
				System.out.println( (ed.isDirty() ? "(*)" : "  ") 
					+ " - "
					+ AonStringUtils.rightPad( "(" + (ed.getId() == null ? "" : AonNumberUtils.toString(ed.getId())) + ")", 10)
					+ AonStringUtils.rightPad(startDate, 15)
					+ AonStringUtils.rightPad(endDate, 15)
					+ AonStringUtils.rightPad( AonStringUtils.defaultIfBlank(ed.getName()), 25)
					+ AonStringUtils.rightPad( AonStringUtils.defaultIfBlank(ed.getExpression()) , 20)
				);
			}
			);
		System.out.println( " ------- " );
		System.out.println( );
	}
	
	protected InvoiceCommunicationConfiguration resetAndGetIcc() {
		int count = getCtx().getDslContext()
			.delete(ENTERPRISE_DATA)
			.where(ENTERPRISE_DATA.DOMAIN.eq(getDomainId()))
			.and(ENTERPRISE_DATA.NAME.in( InvoiceCommunicationDAO.SUPPORTED_TYPES))
			.execute();
		getCtx().log().info("Configuración borrada para el dominio {0}: {1} registros eliminados", getDomainId(), count);
		InvoiceCommunicationConfiguration icc = new InvoiceCommunicationConfiguration();
		assertNotNull(icc);
		assertFalse( icc.hasCommunication() );
		return icc;
	}
	
}
