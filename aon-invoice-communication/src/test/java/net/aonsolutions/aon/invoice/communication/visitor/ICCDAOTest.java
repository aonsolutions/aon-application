package net.aonsolutions.aon.invoice.communication.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationParams;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.ExemptType;
import com.esferalia.aon.occam.impl.jooq.dao.ICCDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@DisplayName("InvoiceCommunicationDAO")
class ICCDAOTest extends ICCAbstractEnablingTest {

    private Date today;
    private Date lastMonthFirstDay;

    @BeforeEach
    void setUpDates() {
        today = AonDateUtils.today();
        lastMonthFirstDay = AonDateUtils.getMonthFirstDay(AonDateUtils.addMonths(today, -1));
        resetAndGetIcc();
    }

    // =========================================================
    // CommunicationDataFiller
    // =========================================================
    @Nested
    @DisplayName("CommunicationDataFiller.apply()")
    class CommunicationDataFillerTest {

        @Test
        @DisplayName("Dirty flags must be false after read")
        void apply_testDirtyFlag() {
            InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactu(getCtx(), getDomainId()
        		, getCD( Administration.COMMON_TERRITORY ).setStartDate(lastMonthFirstDay ).setTest(true))
            ;
            printIcc(icc);
            assertFalse( icc.dataStream().anyMatch( d -> d.isDirty() ));
        }

        @Test
        @DisplayName("Expresion JSON con test=true, isTest() debe ser true")
        void apply_jsonWithTestTrue_setsTestFlag() {
            InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactu(getCtx(), getDomainId()
        		, getCD( Administration.COMMON_TERRITORY ).setStartDate(lastMonthFirstDay ).setTest(true))
            ;
            printIcc(icc);

            assertTrue(icc.getNoVerifactuData().isPresent());
            CommunicationData ed = icc.getNoVerifactuData().get();
            assertTrue(AonStringUtils.isNotBlank(ed.getExpression()));
            assertTrue(ed.isTest());
        }

        @Test
        @DisplayName("Expresion JSON con exemptType valido, exemptType debe setearse")
        void apply_jsonWithValidExemptType_setsExemptType() {
            InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactu(getCtx(), getDomainId()
    			, getCD( Administration.COMMON_TERRITORY ).setStartDate(lastMonthFirstDay ).setExemptType(ExemptType.NO_SOFTWARE))
    		;
            printIcc(icc);

            assertTrue(icc.getNoVerifactuData().isPresent());
            CommunicationData ed = icc.getNoVerifactuData().get();
            assertTrue(ed.getExemptType().isPresent());
            assertEquals(ExemptType.NO_SOFTWARE, ed.getExemptType().get());
            assertFalse(ed.isTest());
        }

        @Test
        @DisplayName("Expresion JSON con test=true y exemptType, ambos campos deben setearse")
        void apply_jsonWithTestAndExemptType_setsBothFields() {
            InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactu(getCtx(), getDomainId()
        		, getCD( Administration.COMMON_TERRITORY ).setStartDate(lastMonthFirstDay ).setExemptType(ExemptType.AUTHORIZATION).setTest(true))
			;
            printIcc(icc);

            assertTrue(icc.getNoVerifactuData().isPresent());
            CommunicationData ed = icc.getNoVerifactuData().get();
            assertTrue(ed.isTest());
            assertTrue(ed.getExemptType().isPresent());
            assertEquals(ExemptType.AUTHORIZATION, ed.getExemptType().get());
        }

        @Test
        @DisplayName("Sin expresion (enable sin test ni exemptType), isTest false y exemptType null")
        void apply_noExpression_defaultValues() {
            InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactu(getCtx(), getDomainId()
        		, getCD( Administration.COMMON_TERRITORY ).setStartDate(lastMonthFirstDay ))
    		;
            printIcc(icc);

            assertTrue(icc.getNoVerifactuData().isPresent());
            CommunicationData ed = icc.getNoVerifactuData().get();
            assertFalse(ed.isTest());
            assertTrue(ed.getExemptType().isEmpty());
        }
    }

    // =========================================================
    // getAttachUrl
    // =========================================================
    @Nested
    @DisplayName("getAttachUrl()")
    class GetAttachUrlTest {

        private java.lang.reflect.Method getAttachUrl;

        @BeforeEach
        void setUp() throws Exception {
            getAttachUrl = InvoiceCommunicationDAO.class
                .getDeclaredMethod("getAttachUrl", String.class, Integer.class, Integer.class);
            getAttachUrl.setAccessible(true);
        }

        @Test
        @DisplayName("attachId null retorna null")
        void nullAttachId_returnsNull() throws Exception {
            String result = (String) getAttachUrl.invoke(null, "myDomain", 1, null);
            assertNull(result);
        }

        @Test
        @DisplayName("attachId valido, URL comienza con ms/api/file/")
        void validAttachId_urlHasCorrectPrefix() throws Exception {
            String result = (String) getAttachUrl.invoke(null, "myDomain", getDomainId(), 99);
            assertNotNull(result);
            assertTrue(result.startsWith("ms/api/file/"));
        }

        @Test
        @DisplayName("attachId valido, Base64 contiene domainName, domainId y attachId")
        void validAttachId_base64ContainsExpectedData() throws Exception {
            String domainName = getCtx().getDomainName();
            Integer domainId  = getDomainId();
            int attachId = 123;

            String result = (String) getAttachUrl.invoke(null, domainName, domainId, attachId);

            String base64Part = result.replace("ms/api/file/", "");
            String decoded = new String(Base64.getDecoder().decode(base64Part), StandardCharsets.UTF_8);

            assertTrue(decoded.contains(domainName));
            assertTrue(decoded.contains(domainId.toString()));
            assertTrue(decoded.contains(String.valueOf(attachId)));
        }

        @Test
        @DisplayName("Mismos parametros producen siempre la misma URL")
        void sameInput_producesSameUrl() throws Exception {
            String domainName = getCtx().getDomainName();
            Integer domainId  = getDomainId();

            String url1 = (String) getAttachUrl.invoke(null, domainName, domainId, 55);
            String url2 = (String) getAttachUrl.invoke(null, domainName, domainId, 55);

            assertEquals(url1, url2);
        }
    }

    // =========================================================
    // saveRequest
    // =========================================================
    @Nested
    @DisplayName("saveRequest() - validaciones de entrada")
    class SaveRequestTest {

        @Test
        @DisplayName("communicationType null lanza AonCoreException")
        void nullCommunicationType_throwsException() {
            AONContext ctx = getCtx();
            AonCoreException ex = assertThrows(AonCoreException.class, () ->
                InvoiceCommunicationDAO.saveRequest(ctx, null, null, new byte[0])
            );
            assertNotNull(ex.getMessage());
        }
    }

    // =========================================================
    // saveResponse
    // =========================================================
    @Nested
    @DisplayName("saveResponse() - validaciones de entrada")
    class SaveResponseTest {

        @Test
        @DisplayName("communicationType null lanza AonCoreException")
        void nullCommunicationType_throwsException() {
            AONContext ctx = getCtx();
            AonCoreException ex = assertThrows(AonCoreException.class, () ->
                InvoiceCommunicationDAO.saveResponse(ctx, null, null, null, new byte[0])
            );
            assertNotNull(ex.getMessage());
        }
    }

    // =========================================================
    // getInvoices
    // =========================================================
    @Nested
    @DisplayName("getInvoices() - validaciones de entrada")
    class GetInvoicesTest {

        @Test
        @DisplayName("params null lanza AonCoreException")
        void nullParams_throwsException() {
            AONContext ctx = getCtx();
            assertThrows(AonCoreException.class, () ->
                InvoiceCommunicationDAO.getInvoices(ctx, null)
            );
        }

        @Test
        @DisplayName("domain null en params lanza AonCoreException")
        void nullDomain_throwsException() {
            AONContext ctx = getCtx();
            InvoiceCommunicationParams params = new InvoiceCommunicationParams();
            params.setCommunicationType(InvoiceCommunicationType.SII);

            assertThrows(AonCoreException.class, () ->
                InvoiceCommunicationDAO.getInvoices(ctx, params)
            );
        }

        @Test
        @DisplayName("communicationType null en params lanza AonCoreException")
        void nullCommunicationType_throwsException() {
            AONContext ctx = getCtx();
            InvoiceCommunicationParams params = new InvoiceCommunicationParams();
            params.setDomain(getDomainId());

            assertThrows(AonCoreException.class, () ->
                InvoiceCommunicationDAO.getInvoices(ctx, params)
            );
        }

        @Test
        @DisplayName("params validos retorna stream no nulo")
        void validParams_returnsStream() {
            InvoiceCommunicationParams params = new InvoiceCommunicationParams();
            params.setDomain(getDomainId());
            params.setCommunicationType(InvoiceCommunicationType.SII);

            assertNotNull(InvoiceCommunicationDAO.getInvoices(getCtx(), params));
        }
    }

    // =========================================================
    // get
    // =========================================================
    @Nested
    @DisplayName("get() - lectura de InvoiceCommunicationConfiguration")
    class GetConfigTest {

        @Test
        @DisplayName("get() con dominio valido retorna configuracion no nula")
        void get_validDomain_returnsConfig() {
            InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(getCtx(), getDomainId());
            assertNotNull(config);
        }

        @Test
        @DisplayName("get() tras reset, configuracion sin comunicaciones activas")
        void get_afterReset_noActiveCommunications() {
            InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(getCtx(), getDomainId());
            assertFalse(config.hasCommunication());
        }

        @Test
        @DisplayName("get() tras enableNoVerifactuTest, configuracion refleja el cambio")
        void get_afterEnable_reflectsChange() {
            ICCDAO.enableNoVerifactu(getCtx(), getDomainId()
        		, getCD( Administration.COMMON_TERRITORY ).setStartDate(lastMonthFirstDay ).setTest(true))
            ;
            InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(getCtx(), getDomainId());

            assertTrue(config.isNoVerifactu(today));
            assertTrue(config.getNoVerifactuData().isPresent());
            assertTrue(config.getNoVerifactuData().get().isTest());
        }
    }
}