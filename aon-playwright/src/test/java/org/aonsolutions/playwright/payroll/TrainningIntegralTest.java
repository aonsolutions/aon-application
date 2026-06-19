package org.aonsolutions.playwright.payroll;

import com.microsoft.playwright.options.SelectOption;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Locale;

public class TrainningIntegralTest extends BasePlaywrightTestCase {

    public static final String INTEGRATION_PAYROLL_URL = "integration.test.trainning.payroll.url";

    @BeforeAll
    public static void setUp() throws Exception {
        String url = System.getProperty(INTEGRATION_PAYROLL_URL);
        String user = System.getProperty("integration.test.user");
        String password = System.getProperty("integration.test.password");
        setup(url, user, password);
        wait4Id("formacion_y_aprendizaje");
    }

    @Test
    public void TestQuote() throws Exception {
        close("cotizacion_formacion_y_el_aprendizaje");
        open("cotizacion_formacion_y_el_aprendizaje");
        wait4Id("finiquito_formacion,_aprendizaje");

        draft("FINIQUITO FORMACIóN, APRENDIZAJE");
        calculate(Calendar.MARCH, 2016);
        assertText("common_contingency", "6,18");
        calculate(Calendar.APRIL, 2017);
        assertText("common_contingency", "6,67");
        calculate(Calendar.DECEMBER, 2017);
        assertText("common_contingency", "6,67");
        calculate(Calendar.SEPTEMBER, 2021);
        assertText("common_contingency", "9,10");
        calculate(Calendar.SEPTEMBER, 2025);
        assertText("common_contingency", "11,16");
        calculate(Calendar.SEPTEMBER, 2026);
        assertText("common_contingency", "11,51");

        draft("FORMACIóN Y EL, APRENDIZAJE");
        calculate(Calendar.JANUARY, 2024);
        assertText("common_contingency", "10,69");
        assertText("unemployment", "20,51");
        assertText("job_training", "0,26");
        check("costsCheck-input");
        wait4Id("common_contingency_cost");
        assertText("common_contingency_cost", "53,61");
        assertText("unemployment_cost", "72,76");
        assertText("job_training_cost", "2,00");
        assertText("fogasa_cost", "4,07");
        assertText("it_cost", "3,82");
        assertText("ims_cost", "3,56");
        uncheck("costsCheck-input");
        calculate(Calendar.JANUARY, 2025);
        assertText("common_contingency", "11,16");
        assertText("unemployment", "21,41");
        assertText("job_training", "0,27");
        check("costsCheck-input");
        wait4Id("common_contingency_cost");
        assertText("common_contingency_cost", "55,97");
        assertText("unemployment_cost", "75,96");
        assertText("job_training_cost", "2,09");
        assertText("fogasa_cost", "4,25");
        assertText("it_cost", "3,99");
        assertText("ims_cost", "3,72");
        uncheck("costsCheck-input");
        calculate(Calendar.JANUARY, 2026);
        assertText("common_contingency", "11,51");
        assertText("unemployment", "22,08");
        assertText("job_training", "0,28");
        check("costsCheck-input");
        wait4Id("common_contingency_cost");
        assertText("common_contingency_cost", "57,72");
        assertText("unemployment_cost", "78,34");
        assertText("job_training_cost", "2,16");
        assertText("fogasa_cost", "4,38");
        assertText("it_cost", "4,12");
        assertText("ims_cost", "3,83");
        uncheck("costsCheck-input");

        draft("BECARIO, EL");
        calculate(Calendar.JANUARY, 2024);
        assertValue("cgcBaseLabel", "1.323,00");
        assertValue("cgpBaseLabel", "1.323,00");
        assertText("common_contingency", "10,69");
        assertText("job_training", "0,26");
        assertNotElement("mei");
        check("costsCheck-input");
        wait4Id("it_cost");
        assertText("common_contingency_cost", "53,61");
        assertText("job_training_cost", "2,00");
        assertText("fogasa_cost", "4,07");
        assertText("it_cost", "3,82");
        assertText("ims_cost", "3,56");
        assertNotElement("mei_cost");
        uncheck("costsCheck-input");
        calculate(Calendar.JANUARY, 2025);
        assertValue("cgcBaseLabel", "1.381,20");
        assertValue("cgpBaseLabel", "1.381,20");
        assertText("common_contingency", "11,16");
        assertText("job_training", "0,27");
        assertNotElement("mei");
        check("costsCheck-input");
        wait4Id("it_cost");
        assertText("common_contingency_cost", "55,97");
        assertText("job_training_cost", "2,09");
        assertText("fogasa_cost", "4,25");
        assertText("it_cost", "3,99");
        assertText("ims_cost", "3,72");
        assertNotElement("mei_cost");
        uncheck("costsCheck-input");
        calculate(Calendar.JANUARY, 2026);
        assertValue("cgcBaseLabel", "1.424,40");
        assertValue("cgpBaseLabel", "1.424,40");
        assertText("common_contingency", "11,51");
        assertText("job_training", "0,28");
        assertNotElement("mei");
        check("costsCheck-input");
        wait4Id("it_cost");
        assertText("common_contingency_cost", "57,72");
        assertText("job_training_cost", "2,16");
        assertText("fogasa_cost", "4,38");
        assertText("it_cost", "4,12");
        assertText("ims_cost", "3,83");
        assertNotElement("mei_cost");
        uncheck("costsCheck-input");

        draft("FORMACION Y APRENDIZAJE, IT");
        calculate(Calendar.OCTOBER, 2018);
        assertText("common_contingency", "6,94");
        assertText("unemployment", "13,32");
        assertValue("cgcBaseLabel", "858,60");
        assertValue("cgpBaseLabel", "858,60");
        calculate(Calendar.OCTOBER, 2019);
        assertText("common_contingency", "8,49");
        assertText("unemployment", "16,28");
        assertNotElement("job_training");
        assertValue("cgcBaseLabel", "1.050,00");
        assertValue("cgpBaseLabel", "1.050,00");
    }

    @Test
    public void TestEnAlternancia() throws Exception {
        close("cotizacion_formacion_y_el_aprendizaje");
        open("cotizacion_formacion_y_el_aprendizaje");
        wait4Id("formacion_aprendizaje,_alternancia_(superior)");

        draft("FORMACIóN APRENDIZAJE, ALTERNANCIA (SUPERIOR)");
        calculate(Calendar.MAY, 2023);
        double cgcBase = getValue("cgcBaseLabel");
        assertText("common_contingency", 10.18);
        assertText("unemployment", 19.53);
        assertText("job_training", 0.25);
        assertText("mei", 1.26);

        calculate(Calendar.JUNE, 2023);
        double totalPayment = getValue("totalPaymentLabel");
        assertValue("cgcBaseLabel", totalPayment);
        assertValue("cgpBaseLabel", totalPayment);
        assertText("common_contingency", 10.18);
        assertText("unemployment", 19.53);
        assertText("job_training", 0.25);
        assertText("mei", 1.26);
        if (!isDisplayed("it_cost"))
            check("costsCheck-input");
        wait4Id("it_cost");
        assertText("common_contingency_cost", "51,06");
        assertText("job_training_cost", "1,90");
        assertText("fogasa_cost", "3,88");
        assertText("it_cost", "3,93");
        assertText("ims_cost", "3,10");
        assertText("unemployment_cost", 69.30);
        uncheck("costsCheck-input");

        draft("FORMACION APRENDIZAJE, ALTERNANCIA (EXCESO 1)");
        calculate(Calendar.MAY, 2024);
        locateById("editor-cotiza_exceso").selectOption(new SelectOption().setLabel("SI"));
        assertElement("non_structural_overtime");
        Assertions.assertEquals(3, countById("common_contingency"));
        Assertions.assertEquals(3, countById("unemployment"));
        Assertions.assertEquals(3, countById("job_training"));
        calculate(Calendar.JUNE, 2024);
        assertNotElement("editor-cotiza_exceso");
        Assertions.assertEquals(3, countById("common_contingency"));
        Assertions.assertEquals(3, countById("unemployment"));
        Assertions.assertEquals(3, countById("job_training"));
        calculate(Calendar.JANUARY, 2025);
        assertNotElement("editor-cotiza_exceso");
        Assertions.assertEquals(1, countById("common_contingency"));
        Assertions.assertEquals(1, countById("unemployment"));
        Assertions.assertEquals(1, countById("job_training"));
        calculate(Calendar.FEBRUARY, 2025);
        assertNotElement("editor-cotiza_exceso");
        Assertions.assertEquals(3, countById("common_contingency"));
        Assertions.assertEquals(3, countById("unemployment"));
        Assertions.assertEquals(3, countById("job_training"));
        calculate(Calendar.JANUARY, 2026);
        assertNotElement("editor-cotiza_exceso");
        Assertions.assertEquals(3, countById("common_contingency"));
        Assertions.assertEquals(3, countById("unemployment"));
        Assertions.assertEquals(3, countById("job_training"));

        draft("FORMACION APRENDIZAJE, ALTERNANCIA (EXCESO 2)");
        calculate(Calendar.MAY, 2024);
        locateById("editor-cotiza_exceso").selectOption(new SelectOption().setLabel("SI"));
        Assertions.assertEquals(3, countById("common_contingency"));
        Assertions.assertEquals(3, countById("unemployment"));
        Assertions.assertEquals(3, countById("job_training"));
        calculate(Calendar.JUNE, 2024);
        assertNotElement("editor-cotiza_exceso");
        Assertions.assertEquals(3, countById("common_contingency"));
        Assertions.assertEquals(3, countById("unemployment"));
        Assertions.assertEquals(3, countById("job_training"));
        calculate(Calendar.JANUARY, 2025);
        assertNotElement("editor-cotiza_exceso");
        Assertions.assertEquals(3, countById("common_contingency"));
        Assertions.assertEquals(3, countById("unemployment"));
        Assertions.assertEquals(3, countById("job_training"));
        calculate(Calendar.JANUARY, 2026);
        assertNotElement("editor-cotiza_exceso");
        Assertions.assertEquals(3, countById("common_contingency"));
        Assertions.assertEquals(3, countById("unemployment"));
        Assertions.assertEquals(3, countById("job_training"));

        draft("FORMACION APRENDIZAJE, ALTERNANCIA (EXCESO 3)");
        calculate(Calendar.MAY, 2024);
        locateById("editor-cotiza_exceso").selectOption(new SelectOption().setLabel("SI"));
        assertElement("non_structural_overtime");
        Assertions.assertEquals(1, countById("common_contingency"));
        Assertions.assertEquals(1, countById("unemployment"));
        Assertions.assertEquals(1, countById("job_training"));
        calculate(Calendar.JUNE, 2024);
        assertNotElement("editor-cotiza_exceso");
        assertElement("non_structural_overtime");
        Assertions.assertEquals(1, countById("common_contingency"));
        Assertions.assertEquals(1, countById("unemployment"));
        Assertions.assertEquals(1, countById("job_training"));
        calculate(Calendar.JANUARY, 2025);
        assertNotElement("editor-cotiza_exceso");
        assertElement("non_structural_overtime");
        Assertions.assertEquals(1, countById("common_contingency"));
        Assertions.assertEquals(1, countById("unemployment"));
        Assertions.assertEquals(1, countById("job_training"));
        calculate(Calendar.JANUARY, 2026);
        assertNotElement("editor-cotiza_exceso");
        assertElement("non_structural_overtime");
        Assertions.assertEquals(1, countById("common_contingency"));
        Assertions.assertEquals(1, countById("unemployment"));
        Assertions.assertEquals(1, countById("job_training"));

        draft("FORMACION APRENDIZAJE, ALTERNANCIA (PARCIAL)");
        calculate(Calendar.AUGUST, 2023);
        assertValue("cgcBaseLabel", 1260.00);
        assertValue("cgpBaseLabel", 1260.00);
        assertText("common_contingency", 10.18);
        assertText("unemployment", 19.53);
        assertText("job_training", 0.25);
        assertText("mei", 1.26);
        double totalDeduction = getText("totalDeductionLabel");
        double totalEnterprise = getText("totalEnterpriseLabel");
        calculate(Calendar.SEPTEMBER, 2023);
        assertValue("cgcBaseLabel", 1260.00);
        assertValue("cgpBaseLabel", 1260.00);
        assertText("common_contingency", 10.18);
        assertText("unemployment", 19.53);
        assertText("job_training", 0.25);
        assertText("mei", 1.26);
        assertText("totalDeductionLabel", totalDeduction - 28.00);
        assertText("totalEnterpriseLabel", totalEnterprise - 91.00);
        calculate(Calendar.JANUARY, 2024);
        assertValue("cgcBaseLabel", 1323.00);
        assertValue("cgpBaseLabel", 1323.00);
        assertText("common_contingency", 10.69);
        assertText("unemployment", 20.51);
        assertText("job_training", 0.26);
        assertText("mei", 1.59);
        calculate(Calendar.JANUARY, 2025);
        assertValue("cgcBaseLabel", 1381.20);
        assertValue("cgpBaseLabel", 1381.20);
        assertText("common_contingency", 11.16);
        assertText("unemployment", 21.41);
        assertText("job_training", 0.27);
        assertText("mei", 1.80);
        assertText("meiPercentLabel", "0,13 %");

        calculate(Calendar.JANUARY, 2026);
        check("costsCheck-input");
        assertValue("cgcBaseLabel", 1424.40);
        assertValue("cgpBaseLabel", 1424.40);
        assertText("common_contingency", 11.51);
        assertText("unemployment", 22.08);
        assertText("job_training", 0.28);
        assertText("mei", BigDecimal.valueOf(1424.40 * 0.15 / 100.00).setScale(2, RoundingMode.HALF_UP).doubleValue());
        assertText("meiPercentLabel", "0,15 %");
        assertText("mei_ePercentLabel", "0,75 %");
        uncheck("costsCheck-input");

        draft("FORMACIóN ALTERNANCIA, PLAN DE PENSIONES");
        calculate(Calendar.SEPTEMBER, 2025);
        check("costsCheck-input");
        assertNotElement("red_ppe_ePercentLabel");
        assertElement("editor-reduccion_aportacion_empresa_ppe");
        uncheck("costsCheck-input");
    }

    @Test
    public void TestSettle() throws Exception {
        close("cotizacion_formacion_y_el_aprendizaje");
        open("cotizacion_formacion_y_el_aprendizaje");
        wait4Id("finiquito_formacion,_aprendizaje");

        draft("FINIQUITO FORMACIóN, APRENDIZAJE");
        try {
            settle(Calendar.getInstance().getTime());
        } catch (Exception err) {
            int year = Calendar.getInstance().get(Calendar.YEAR);
            int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
            wait4Regex("periodLabel",
                    String.format(new Locale("es", "ES"), "3/3/2016 - [0-9]+/%2$d/%1$d", year, month));
        }
        setValue("editor-dias_vacaciones_no_disfrutados", "1");
        assertText("unemployment", "22,08");

        draft("FINIQUITO FORMACION, ALTERNANCIA");
        Calendar calendar = Calendar.getInstance();
        calendar.set(2023, Calendar.JULY, 15, 0, 0, 0);
        settle(calendar.getTime());
        double irpf = getText("irpf");
        assertText("totalDeductionLabel", irpf);
        assertText("totalEnterpriseLabel", 0.00);
    }

    @Test
    public void TestMEI() throws Exception {
        close("cotizacion_formacion_y_el_aprendizaje");
        open("cotizacion_formacion_y_el_aprendizaje");
        wait4Id("mecanismo,_equidad");

        draft("MECANISMO, EQUIDAD");
        calculate(Calendar.MARCH, 2023);
        assertText("mei", "1,26");
        check("costsCheck-input");
        assertText("mei_cost", "6,30");
        uncheck("costsCheck-input");

        calculate(Calendar.JANUARY, 2024);
        assertText("mei", "1,59");
        check("costsCheck-input");
        assertText("mei_cost", "7,67");
        uncheck("costsCheck-input");

        calculate(Calendar.JANUARY, 2025);
        assertText("mei", "1,80");
        assertText("meiPercentLabel", "0,13 %");
        check("costsCheck-input");
        assertText("mei_cost", "9,25");
        assertText("mei_ePercentLabel", "0,67 %");
        uncheck("costsCheck-input");

        calculate(Calendar.JANUARY, 2026);
        assertText("mei", "2,14");
        assertText("meiPercentLabel", "0,15 %");
        check("costsCheck-input");
        assertText("mei_cost", "10,68");
        assertText("mei_ePercentLabel", "0,75 %");
        uncheck("costsCheck-input");

        draft("FORMACIóN APRENDIZAJE, ALTERNANCIA ( MES INCOMPLETO )");
        calculate(Calendar.APRIL, 2026);
        assertText("mei", "2,14");
        assertText("meiPercentLabel", "0,15 %");
        check("costsCheck-input");
        assertText("mei_cost", "10,68");
        assertText("mei_ePercentLabel", "0,75 %");
        uncheck("costsCheck-input");
    }
}
