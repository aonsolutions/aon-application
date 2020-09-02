var PDF = require('pdfkit');

module.exports.newStandardPayroll = function(payrolls, stream){
	//Initialize pdf object
	var pdf = new PDF({
        size: [595.28, 841.89],
        margins: { // by default, all are 72
            top: 5,
            bottom: 5,
            left: 5,
            right: 5
        }
    });

    pdf.pipe(stream);

    //PDF Styles
    var pdfWidth = pdf.page.width -
        pdf.page.margins.left -
        pdf.page.margins.right;

    //Variables y constants
    var t0p = 0;
    var left = 0;
    var right = 0;

    textFooterSize = 7.5;
    textFooterFont = 'Helvetica';

    textSize = 8;
    textFont = 'Helvetica';

    titleTextSize = 8;
    titleTextFont = 'Helvetica-Bold';

    headingSize = 4;
    headingFont = 'Helvetica-Bold';

    signature = 585;

    var salary_perceptions_codes = [0001];
    var extra_hours_codes = [0002, 0003];
    var extra_perks_codes = [0004, 0005];
    var spices_salary_codes = [0013, 0014, 0015, 0016, 0017, 0018, 0019, 0020, 0021, 0022, 0023, 0024, 0025, 0026];

    //SET BLANKs IMAGES
    // var blank_image = 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAIBAQIBAQICAgICAgICAwUDAwMDAwYEBAMFBwYHBwcGBwcICQsJCAgKCAcHCg0KCgsMDAwMBwkODw0MDgsMDAz/2wBDAQICAgMDAwYDAwYMCAcIDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAz/wAARCAABAAEDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9/KKKKAP/2Q==';
    // payroll.logo = blank_image;
    // payroll.signature_logo = blank_image;
    // payroll.logoEnterprise = blank_image;
    // payroll.logoEnterprise2 = blank_image;

    //Aux Methods
    String.prototype.splice = function(idx, rem, str) {
        return this.slice(0, idx) + str + this.slice(idx + Math.abs(rem));
    };
    
    var checkDates = function(accrualType, accrualTypeExpression) {
    	startDate = new Intl.DateTimeFormat('en-GB').format(accrualType.startDate);
        endDate = new Intl.DateTimeFormat('en-GB').format(accrualType.endDate);
        
        var diffDates = parseInt((accrualType.endDate-accrualType.startDate)/1000/60/60/24);
        
        if (null != accrualType.startDate && null != accrualType.endDate && startDate != endDate && diffDates > 1 && diffDates < 28) {
            return accrualTypeExpression + parseDDMMDate(startDate, endDate);
        }
        return accrualTypeExpression;
    }

    var parseDDMMDate = function(startDate, endDate) {
        /*
    	startDay = startDate.getDate();
        startMonth = startDate.getMonth() + 1;

        if (startDay < 10)
            startDay = "0" + startDay;

        if (startMonth < 10)
            startMonth = "0" + startMonth;

        endDay = endDate.getDate();
        endMonth = endDate.getMonth() + 1;

        if (endDay < 10)
            endDay = "0" + endDay;

        if (endMonth < 10)
            endMonth = "0" + endMonth;
        */
    	
    	startDay = startDate.split("\/")[0];
    	startMonth = startDate.split("\/")[1];
    	
    	endDay = endDate.split("\/")[0];
    	endMonth = endDate.split("\/")[1];

        return " ( " + startDay + "/" + startMonth + " - " + endDay + "/" + endMonth + " )";
    }

    var checkAccrualName = function(accrualName) {
        if (accrualName) {
        	if(accrualName == "Prestaciones e indemnizaciones a la Seguridad Social")
        		return "00. PRESTACIONES E INDEMNIZACIONES A LA SS";
        	
            type = accrualName.split(" ")[0];
            if (type == "01")
                return "01 RETRIBUCIONES SALARIALES".splice(2, 0, ". ");

            return accrualName.splice(2, 0, ". ");
        }
        return accrualName;
    }

    var formatFirstPartAddress = function(address) {
        if (address.includes("("))
            return address.split("(")[0];
        return address;
    }

    var formatSecondPartAddress = function(address, province) {
        if (address.includes("(")) {
            zip = address.split("(")[1].split(")")[0];
            city = address.split(")")[1];

            return zip + " " + city + " " + province;
        }
        return province;
    }

    var formatInputData = function(data, parseData) {
        if (data == null || data == undefined) {
            if (parseData == 'string')
                return '';
            else if (parseData == 'number')
                return '';
        } else {
            var typeData = typeof data;
            //POSIBLE IMPLEMENTACION PARA ALGO MAS INTELIGENTE
            if (typeData == 'number' && data == 0) {
                return '';
            } else if (typeData == 'number' && isNaN(data)) {
                return '';
            }
            return data;
        }
    }

    var parseExpression = function(expression) {
        if (null != expression && undefined != expression && expression.includes("]"))
            return expression.split("]")[1].trim();
        return expression.trim();
    }

    var formatAmount = function(amount) {
        const config = {
            minimumFractionDigits: 2
        }

        return (amount && amount != '' && amount != null && amount != 0) ? new Intl.NumberFormat("de-DE", config).format(amount.toFixed(2)) : '';
    }

    var formatPercent = function(percent) {
        if ('string' == typeof percent) {
            if (percent.includes(" %")) {
                percentInt = parseFloat(percent.split(" %")[0].trim());
                return (percentInt && percentInt != '' && percentInt != null && percentInt != 0) ? percentInt.toFixed(2) + " %" : '';
            }
            return percent;
        } else
            return (percent && percent != '' && percent != null && percent != 0) ? percent.toFixed(2) + " %" : '';
    }

    var formatMoney = function(amount) {
        const config = {
            style: "currency",
            currency: "EUR",
            minimumFractionDigits: 2,
            currencyDisplay: "symbol"
        }
        return (amount && amount != '' && amount != null) ? new Intl.NumberFormat("de-DE", config).format(amount.toFixed(2)) : '0,00 €';
    }

    var numberOffset = function(number) {
        if (number != '' && 'string' != typeof number) {
            var formatNumber = formatAmount(number);
            var split = formatNumber.split(',');
            switch (split[0].length) {
                case 1:
                    return 0;
                case 2:
                    return -4;
                case 3:
                    return -8;
                case 4:
                    return -11;
                case 5:
                    return -15;
                case 6:
                    return -19;
                default:
                    return 0;
            }
        } else {
            return 0;
        }
    }

    var parseDate = function(date) {
        return new Intl.DateTimeFormat('en-GB').format(date);
    }

    var formatDate = function(date) {
        var split = date.split('/');
        return split[0] + ' de ' + getStrMonth(split[1]) + ' de ' + split[2];
    }

    var getStrMonth = function(numberMonth) {
        switch (numberMonth) {
            case '1':
                return 'enero';
            case '2':
                return 'febrero';
            case '3':
                return 'marzo';
            case '4':
                return 'abril';
            case '5':
                return 'mayo';
            case '6':
                return 'junio';
            case '7':
                return 'julio';
            case '8':
                return 'agosto';
            case '9':
                return 'septiembre';
            case '10':
                return 'octubre';
            case '11':
                return 'noviembre';
            case '12':
                return 'diciembre';
            default:
                return 'error';
        }
    }


  //Main Methods
    var newHearderTittle = function(payroll) {
        titleSize = 10;
        titleFont = 'Helvetica-Bold';

        pdf.lineWidth(1);

        //Start drawing PDF
        pdf
            .moveDown(0.5)
            .font(titleFont)
            .fontSize(titleSize)
            .text('RECIBO INDIVIDUAL JUSTIFICATIVO DEL PAGO DE SALARIO', { align: 'center' })
            .moveDown(1);
    }

    var newEnterpriseBox = function(payroll) {
        enterpriseTop = 30;
        enterpriseHeight = 90;
        topLeftCorner = 10;
        boxWidth = 290;

        title = 30;
        firstColumn = 25;
        secondColumn = 180;

        paddingLeft = 20;

        // -----------------------------------------------------------------------------------------------------------------
        // -------------------------------------------- ENTERPRISE ---------------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------
        pdf
            .moveTo(topLeftCorner, enterpriseTop).lineTo(topLeftCorner + 5, enterpriseTop).stroke() //Horizontal t0p line
            .font(textFont)
            .fontSize(textSize)
            .text('Empresa', topLeftCorner + 15, enterpriseTop - 3)
            .moveTo(topLeftCorner + 70, enterpriseTop).lineTo(boxWidth, enterpriseTop).stroke()
            .font(titleFont)
            .fontSize(titleTextSize)
            .text(formatInputData(payroll.enterprise.name, 'string'), firstColumn, enterpriseTop + 12)

        .font(textFont)
            .fontSize(textSize)
            .text(formatFirstPartAddress(payroll.enterprise.address), firstColumn + paddingLeft, enterpriseTop + 24)
            .text(formatSecondPartAddress(payroll.enterprise.address, payroll.enterprise.city.toUpperCase()), firstColumn + paddingLeft, enterpriseTop + 36)

        .font(headingFont)
            .text('CIF: ', firstColumn, enterpriseTop + 48)
            .font(textFont)
            .text(formatInputData(payroll.enterprise.cif, 'string'), firstColumn + paddingLeft, enterpriseTop + 48)

        .font(headingFont)
            .text('CCC: ', secondColumn, enterpriseTop + 48)
            .font(textFont)
            .text(formatInputData(payroll.enterprise.ccc, 'string'), secondColumn + paddingLeft + 5, enterpriseTop + 48)

        .moveTo(topLeftCorner, enterpriseTop).lineTo(topLeftCorner, enterpriseHeight).stroke() //Vertical left line
            .moveTo(boxWidth, enterpriseTop).lineTo(boxWidth, enterpriseHeight).stroke() //Vertical right line
            .moveTo(topLeftCorner, enterpriseHeight).lineTo(boxWidth, enterpriseHeight).stroke(); //Horizontal bottom line

    }

    var newEmployeeBox = function(payroll) {
        enterpriseTop = 30;
        employeeHeight = 90;
        topLeftCorner = 295;
        boxWidth = 290;

        firstColumn = 310;
        secondColumn = 340;
        thirdColumn = 410;
        fourthColumn = 485;
        fifthColumn = 530;
        sixthColumn = 550;

        paddingLeft = 25;

        // -----------------------------------------------------------------------------------------------------------------
        // -------------------------------------------- EMPLOYEE -----------------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------

        pdf
            .moveTo(topLeftCorner, enterpriseTop).lineTo(topLeftCorner + 5, enterpriseTop).stroke() //Horizontal t0p line
            .font(textFont)
            .text('Trabajador', topLeftCorner + 15, enterpriseTop - 3)
            .moveTo(topLeftCorner + 70, enterpriseTop).lineTo(topLeftCorner + boxWidth, enterpriseTop).stroke()
            .font(titleFont)
            .fontSize(titleTextSize)
            .text(formatInputData(payroll.employee.fullname, 'string'), firstColumn, enterpriseTop + 12)

        .text('NIF: ', firstColumn, enterpriseTop + 24)
            .font(textFont)
            .text(formatInputData(payroll.employee.nif, 'string'), secondColumn, enterpriseTop + 24)

        .font(headingFont)
            .text('Fecha antigüedad: ', thirdColumn, enterpriseTop + 24)
            .font(textFont)
            .text(parseDate(payroll.employee.seniority_date), fourthColumn, enterpriseTop + 24)

        .font(headingFont)
            .text('Nº S.S.: ', firstColumn, enterpriseTop + 36)
            .font(textFont)
            .text(formatInputData(payroll.employee.ss, 'string'), secondColumn, enterpriseTop + 36)

        .font(headingFont)
            .text('Grupo cotización: ', thirdColumn, enterpriseTop + 36)
            .font(textFont)
            .text(formatInputData(payroll.employee.quote_group, 'string'), fourthColumn, enterpriseTop + 36)

        .font(headingFont)
            .text('TC2: ', fifthColumn, enterpriseTop + 36)
            .font(textFont)
            .text(formatInputData(payroll.employee.contract_type, 'string'), sixthColumn, enterpriseTop + 36)

        .font(headingFont)
            .text('Grupo profesional: ', firstColumn, enterpriseTop + 48)
            .font(textFont)
            .text(formatInputData(payroll.employee.professional_group, 'number'), secondColumn + 45, enterpriseTop + 48)

        .moveTo(topLeftCorner, enterpriseTop).lineTo(topLeftCorner, employeeHeight).stroke() //Vertical left line
            .moveTo(topLeftCorner + boxWidth, enterpriseTop).lineTo(topLeftCorner + boxWidth, employeeHeight).stroke() //Vertical right line
            .moveTo(topLeftCorner, employeeHeight).lineTo(topLeftCorner + boxWidth, employeeHeight).stroke() //Horizontal bottom line
            .moveDown(1);
    }

    var newSettlementBox = function(payroll) {
        // -----------------------------------------------------------------------------------------------------------------
        // --------------------------------------------- SETTLEMENT --------------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------
        topLeftCorner = 10;
        settlementTop = 95;
        settlementHeight = 16;
        boxWidth = 585;

        firstColumn = 25;
        secondColumn = 515;

        start_date = formatDate(payroll.settlement.start_date);
        end_date = formatDate(payroll.settlement.end_date);

        pdf
            .moveTo(topLeftCorner, settlementTop).lineTo(boxWidth, settlementTop).stroke() //Horizontal t0p line
            .font(headingFont)
            .text('Periodo de liquidación: ', firstColumn, settlementTop + 5)
            .font(textFont)
            .text('del ' + start_date + ' al ' + end_date, firstColumn + 90, settlementTop + 5);

        pdf
            .font(headingFont)
            .text('Total días: ', secondColumn, settlementTop + 5)
            .font(textFont)
            .text(payroll.settlement.total_days, secondColumn + 45, settlementTop + 5)

        .moveTo(topLeftCorner, settlementTop).lineTo(topLeftCorner, settlementTop + settlementHeight).stroke() //Vertical left line
            .moveTo(boxWidth, settlementTop).lineTo(boxWidth, settlementTop + settlementHeight).stroke() //Vertical right line
            .moveTo(topLeftCorner, settlementTop + settlementHeight).lineTo(boxWidth, settlementTop + settlementHeight).stroke(); //Horizontal bottom line

    }

    var newFooterBox = function(payroll) {
        // -----------------------------------------------------------------------------------------------------------------
        // -------------------------------------------- FOOTER -------------------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------
        topLeftCorner = 10;
        boxWidth = 585;

        title = 30;
        firstColumn = 40;
        secondColumn = 180;
        thirdColumn = 290;
        fourthColumn = 395;
        fifthColumn = 455;
        sixthColumn = 530;

        paddingLeft = 8;

        footer = 665;

        pdf
            .moveTo(topLeftCorner, footer).lineTo(boxWidth, footer).undash().stroke() //Horizontal t0p line
            .font(textFooterFont)
            .fontSize(textFooterSize)
            .text('DETERMINACIÓN DE LAS BASES DE COTIZACIÓN A LA SEGURIDAD SOCIAL Y CONCEPTOS DE RECAUDACIÓN CONJUNTAS Y DE ' +
                'LA BASE SUJETA A ', title, footer + 5)
            .font(textFooterFont)
            .text('RETENCIÓN DEL IRPF Y APORTACIÓN DE LA EMPRESA', title, footer + 15)
            .font(textFooterFont)
            .text('1. Contingencias comunes', firstColumn, footer + 25, { continued: true })
            .font(textFooterFont)
            .text('BASE', thirdColumn + 10, footer + 25, { continued: true })
            .font(textFooterFont)
            .text('TIPO', thirdColumn + 60, footer + 25, { continued: true })
            .font(textFooterFont)
            .text('AP. EMPRESA', thirdColumn + 90, footer + 25)

        .font(textFooterFont)
            .text('Importe remuneración mensual', firstColumn + 8, footer + 35)
            .font(textFooterFont)
            .text(formatMoney(payroll.footer_ss_quotation.common_contingency.monthly_remuneration), thirdColumn + numberOffset(payroll.footer_ss_quotation.common_contingency.monthly_remuneration), footer + 35)
            .lineWidth(0.1)
            .moveTo(firstColumn + paddingLeft, footer + 43).lineTo(thirdColumn - 30, footer + 43).dash(1, { space: 2 }).stroke()
            .moveTo(thirdColumn - 30, footer + 43).lineTo(thirdColumn + 23, footer + 43).undash().stroke()

        .font(textFooterFont)
            .text('Importe prorrata de pagas extraordinarias', firstColumn + paddingLeft, footer + 47)
            .font(textFooterFont)
            .text(formatMoney(payroll.footer_ss_quotation.common_contingency.extraordinary_pay_packet), thirdColumn + numberOffset(payroll.footer_ss_quotation.common_contingency.extraordinary_pay_packet), footer + 47)
            .moveTo(firstColumn + paddingLeft, footer + 55).lineTo(thirdColumn - 30, footer + 55).dash(1, { space: 2 }).stroke()
            .moveTo(thirdColumn - 30, footer + 55).lineTo(thirdColumn + 23, footer + 55).undash().stroke()

        .font(textFooterFont)
            .text(formatMoney(payroll.footer_ss_quotation.common_contingency.base), fourthColumn + numberOffset(payroll.footer_ss_quotation.common_contingency.base), footer + 41)
            .font(textFooterFont)
            .text(formatPercent(payroll.footer_ss_quotation.common_contingency.type_percent), fifthColumn + numberOffset(payroll.footer_ss_quotation.common_contingency.type_percent), footer + 41)
            .font(textFooterFont)
            .text(formatMoney(payroll.footer_ss_quotation.common_contingency.company_input), sixthColumn + numberOffset(payroll.footer_ss_quotation.common_contingency.company_input), footer + 41)
            .moveTo(thirdColumn + 70, footer + 49).lineTo(thirdColumn + 130, footer + 49).stroke()
            .moveTo(fourthColumn + 50, footer + 49).lineTo(thirdColumn + 265, footer + 49).stroke()

        .font(textFooterFont)
            .text('2. Contingencias profesionales', firstColumn, footer + 75)
            .font(textFooterFont)
            .text('conceptos de recaudación conjunta', firstColumn + paddingLeft, footer + 85)

        .font(textFooterFont)
            .text('AT Y EP', secondColumn, footer + 60)
            .moveTo(secondColumn, footer + 68).lineTo(secondColumn + 135, footer + 68).dash(1, { space: 2 }).stroke()
            .font(textFooterFont)
            .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_at), fifthColumn + numberOffset(payroll.footer_ss_quotation.professional_contingency.type_percent_at), footer + 60)
            .font(textFooterFont)
            .text(formatMoney(payroll.footer_ss_quotation.professional_contingency.company_input_at), sixthColumn + numberOffset(payroll.footer_ss_quotation.professional_contingency.company_input_at), footer + 60)
            .moveTo(fourthColumn + 50, footer + 68).lineTo(thirdColumn + 265, footer + 68).undash().stroke()

        .font(textFooterFont)
            .text('Desempleo', secondColumn, footer + 72)
            .moveTo(secondColumn, footer + 80).lineTo(secondColumn + 135, footer + 80).dash(1, { space: 2 }).stroke()
            .font(textFooterFont)
            .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_unemployment), fifthColumn + numberOffset(payroll.footer_ss_quotation.professional_contingency.type_percent_unemployment), footer + 72)
            .font(textFooterFont)
            .text(formatMoney(payroll.footer_ss_quotation.professional_contingency.company_input_unemployment), sixthColumn + numberOffset(payroll.footer_ss_quotation.professional_contingency.company_input_unemployment), footer + 72)
            .moveTo(fourthColumn + 50, footer + 80).lineTo(thirdColumn + 265, footer + 80).undash().stroke()

        .font(textFooterFont)
            .text(formatMoney(payroll.footer_ss_quotation.professional_contingency.base), fourthColumn + numberOffset(payroll.footer_ss_quotation.professional_contingency.base), footer + 78)
            .moveTo(thirdColumn + 70, footer + 86).lineTo(thirdColumn + 130, footer + 86).stroke()

        .font(textFooterFont)
            .text('Fromación profesional', secondColumn, footer + 84)
            .moveTo(secondColumn, footer + 92).lineTo(secondColumn + 135, footer + 92).dash(1, { space: 2 }).stroke()
            .font(textFooterFont)
            .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_professional_formation), fifthColumn + numberOffset(payroll.footer_ss_quotation.professional_contingency.type_percent_professional_formation), footer + 84)
            .font(textFooterFont)
            .text(formatMoney(payroll.footer_ss_quotation.professional_contingency.company_input_professional_formation), sixthColumn + numberOffset(payroll.footer_ss_quotation.professional_contingency.company_input_professional_formation), footer + 84)
            .moveTo(fourthColumn + 50, footer + 92).lineTo(thirdColumn + 265, footer + 92).undash().stroke()

        .font(textFooterFont)
            .text('Fondo de Garantía Salarial', secondColumn, footer + 96)
            .moveTo(secondColumn, footer + 104).lineTo(secondColumn + 135, footer + 104).dash(1, { space: 2 }).stroke()
            .font(textFooterFont)
            .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_salary_warranty), fifthColumn + numberOffset(payroll.footer_ss_quotation.professional_contingency.type_percent_salary_warranty), footer + 96)
            .font(textFooterFont)
            .text(formatMoney(payroll.footer_ss_quotation.professional_contingency.company_input_salary_warranty), sixthColumn + numberOffset(payroll.footer_ss_quotation.professional_contingency.company_input_salary_warranty), footer + 96)
            .moveTo(fourthColumn + 50, footer + 104).lineTo(thirdColumn + 265, footer + 104).undash().stroke()

        .font(textFooterFont)
            .text('3. Cotización adicional por horas extraordinarias', firstColumn, footer + 108)
            .font(textFooterFont)
            .text('Fuerza mayor', secondColumn, footer + 118)
            .text(formatMoney(payroll.footer_ss_quotation.aditional_quotation.base_overwhelming_force), fifthColumn + numberOffset(payroll.footer_ss_quotation.aditional_quotation.base_overwhelming_force), footer + 118)
            .text(formatMoney(payroll.footer_ss_quotation.aditional_quotation.company_input_overwhelming_force), sixthColumn + numberOffset(payroll.footer_ss_quotation.aditional_quotation.company_input_overwhelming_force), footer + 118)
            .moveTo(secondColumn, footer + 126).lineTo(thirdColumn + 70, footer + 126).dash(1, { space: 2 }).stroke()
            .moveTo(thirdColumn + 70, footer + 126).lineTo(thirdColumn + 130, footer + 126).undash().stroke()
            .moveTo(fourthColumn + 50, footer + 126).lineTo(thirdColumn + 265, footer + 126).stroke()

        .font(textFooterFont)
            .text('No estructurales', secondColumn, footer + 130)
            .text(formatMoney(payroll.footer_ss_quotation.aditional_quotation.base_non_structural), fifthColumn + numberOffset(payroll.footer_ss_quotation.aditional_quotation.base_non_structural), footer + 130)
            .text(formatMoney(payroll.footer_ss_quotation.aditional_quotation.company_input_non_structural), sixthColumn + numberOffset(payroll.footer_ss_quotation.aditional_quotation.company_input_non_structural), footer + 130)
            .moveTo(secondColumn, footer + 138).lineTo(thirdColumn + 70, footer + 138).dash(1, { space: 2 }).stroke()
            .moveTo(thirdColumn + 70, footer + 138).lineTo(thirdColumn + 130, footer + 138).undash().stroke()
            .moveTo(fourthColumn + 50, footer + 138).lineTo(thirdColumn + 265, footer + 138).stroke();

        taxesS = getDeduction(payroll, "Especie");
        if (!taxesS.value || taxesS.value == 0) {
            pdf
                .font(textFooterFont)
                .text('4. Base sujeta a retención del IRPF', firstColumn, footer + 142)
                .text(formatMoney(payroll.footer_ss_quotation.base_irpf), fourthColumn + numberOffset(payroll.footer_ss_quotation.base_irpf), footer + 142)
                .moveTo(firstColumn + paddingLeft, footer + 150).lineTo(thirdColumn + 70, footer + 150).dash(1, { space: 2 }).stroke()
                .moveTo(thirdColumn + 70, footer + 150).lineTo(thirdColumn + 130, footer + 150).undash().stroke();

            pdf
                .text('Total aportaciones', firstColumn + paddingLeft, footer + 154)
                .text(formatMoney(payroll.footer_ss_quotation.total_company), sixthColumn + numberOffset(payroll.footer_ss_quotation.total_company), footer + 154)
                .moveTo(firstColumn + paddingLeft, footer + 162).lineTo(sixthColumn - 20, footer + 162).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 20, footer + 162).lineTo(thirdColumn + 265, footer + 162).undash().stroke();

            pdf
                .lineWidth(1)
                .moveTo(topLeftCorner, footer).lineTo(topLeftCorner, footer + 167).stroke() //Vertical left line
                .moveTo(boxWidth, footer).lineTo(boxWidth, footer + 167).stroke() //Vertical right line
                .moveTo(topLeftCorner, footer + 167).lineTo(boxWidth, footer + 167).stroke(); //Horizontal bottom line
        } else {
            taxesD = getDeduction(payroll, "Dinerario");
            pdf
                .font(textFooterFont)
                .text('4. Base sujeta a retención del IRPF: ' + formatMoney(taxesS.amount) + '€  en especie + ' + formatMoney(taxesD.amount) + "€  en retribuciones dinerarias", firstColumn, footer + 142)
                .text(formatMoney(payroll.footer_ss_quotation.base_irpf), fourthColumn + numberOffset(payroll.footer_ss_quotation.base_irpf), footer + 142)
                .moveTo(firstColumn + paddingLeft, footer + 150).lineTo(secondColumn - 20, footer + 150).dash(1, { space: 2 }).stroke()
                .moveTo(secondColumn - 20, footer + 150).lineTo(thirdColumn + 130, footer + 150).undash().stroke()
                .text('Total aportaciones', firstColumn, footer + 154)
                .text(formatMoney(payroll.footer_ss_quotation.total_company), sixthColumn + numberOffset(payroll.footer_ss_quotation.total_company), footer + 154)
                .moveTo(firstColumn + paddingLeft, footer + 162).lineTo(sixthColumn - 20, footer + 162).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 20, footer + 162).lineTo(thirdColumn + 265, footer + 162).undash().stroke();

            pdf
                .lineWidth(1)
                .moveTo(topLeftCorner, footer).lineTo(topLeftCorner, footer + 170).stroke() //Vertical left line
                .moveTo(boxWidth, footer).lineTo(boxWidth, footer + 170).stroke() //Vertical right line
                .moveTo(topLeftCorner, footer + 170).lineTo(boxWidth, footer + 170).stroke(); //Horizontal bottom line
        }
    }

    var newSignatureDate = function(payroll) {
        // -----------------------------------------------------------------------------------------------------------------
        // ------------------------------------------ SIGNATURE / DATE -----------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------
        topLeftCorner = 10;
        boxWidth = 585;
        signatureTop = 470;
        accrualHeigt = 545;
        accrualTop = 115;

        firstColumn = 50;
        secondColumn = 230;
        thirdColumn = 250;
        fourthColumn = 400;
        fifthColumn = 220;
        sixthColumn = 450;
        seventhColumn = 535;

        pdf
            .fontSize(titleSize)
            .font(titleFont)
            .text('LÍQUIDO TOTAL A PERCIBIR (A-B)', secondColumn, accrualTop + signatureTop)
            .text(formatMoney(payroll.liquid_perceive), seventhColumn + numberOffset(payroll.liquid_perceive), accrualTop + signatureTop)
            .moveTo(secondColumn, accrualTop + signatureTop + 10).lineTo(seventhColumn - 26, accrualTop + signatureTop + 10).dash(1, { space: 2 }).stroke()
            .moveTo(seventhColumn - 26, accrualTop + signatureTop + 10).lineTo(seventhColumn + 35, accrualTop + signatureTop + 10).undash().stroke();

        pdf
            .font(textFooterFont)
            .fontSize(textFooterSize)
            .text('Firma y sello de la empresa', firstColumn, accrualTop + signatureTop + 50)
            .image(payroll.signature_logo, firstColumn + 10, accrualTop + signatureTop, { scale: 0.06 })

        .text(`RECIBÍ (${end_date}) :`, thirdColumn, accrualTop + signatureTop + 24)
            .fontSize(textFooterSize)
            .text(formatInputData(payroll.employee.fullname, 'string'), fourthColumn, accrualTop + signatureTop + 50)

        .lineWidth(1)
            .moveTo(topLeftCorner, accrualTop).lineTo(topLeftCorner, accrualTop + accrualHeigt).stroke() //Vertical left line
            .moveTo(boxWidth, accrualTop).lineTo(boxWidth, accrualTop + accrualHeigt).stroke() //Vertical right line
            .moveTo(topLeftCorner, accrualTop + accrualHeigt).lineTo(boxWidth, accrualTop + accrualHeigt).stroke(); //Horizontal bottom line
    }

    var newAccrual = function(payroll) {
        // -----------------------------------------------------------------------------------------------------------------
        // --------------------------------------------- ACCRUAL -----------------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------
        topLeftCorner = 10;
        boxWidth = 585;
        accrualTop = 115;

        firstColumn = 25;
        secondColumn = 30;
        thirdColumn = 60;
        fourthColumn = 85;
        fifthColumn = 220;
        sixthColumn = 450;
        seventhColumn = 535;

        pdf
            .moveTo(topLeftCorner, accrualTop).lineTo(boxWidth, accrualTop).stroke()
            .font(headingFont)
            .fontSize(titleSize)
            .text('I. DEVENGOS', firstColumn, accrualTop + 12)
            .text('TOTALES', seventhColumn - 10, accrualTop + 12)
            .fontSize(textSize)
            .lineWidth(0.1);

        newLine = 12;
        paddinBottom = 25;
        accrualsNumb = 0;

        actualLine = accrualTop + paddinBottom;
        totalAccrualLine = actualLine;

        for (i = 0; i < payroll.accruals.length; i++) {
            accrual = payroll.accruals[i];
            if (accrual.types.length > 0) {
                totalAccrualLine += 12;
                accrualsNumb += accrual.types.length + 4;
                pdf
                    .font(titleFont)
                    .text(checkAccrualName(accrual.accrual_name), secondColumn, actualLine);

                titlePos = actualLine;
                actualLine += 5;
                totalAccrualLine += 5;

                craTotal = 0;
                for (j = 0; j < accrual.types.length; j++) {
                    actualLine = actualLine + 12;
                    totalAccrualLine += 12;
                    type = accrual.types[j];
                    craTotal += type.type_value;
                    pdf
                        .font(textFont)
                        .text(formatMoney(type.type_value), thirdColumn + numberOffset(type.type_value), actualLine)
                        .text("  por " + checkDates(type, parseExpression(type.type_expression)), fourthColumn, actualLine);
                }

                pdf
                    .text(formatMoney(craTotal), sixthColumn + numberOffset(craTotal), titlePos)
                    .moveTo(secondColumn, titlePos + 10).lineTo(sixthColumn - 25, titlePos + 10).dash(1, { space: 2 }).stroke()
                    .moveTo(sixthColumn - 25, titlePos + 10).lineTo(sixthColumn + 25, titlePos + 10).undash().stroke();

                actualLine = actualLine + 12;
            }
        }

        totalAccrualLine += 10;
        console.error('totalAccrualLine : ' + totalAccrualLine);


        pdf
            .fontSize(titleSize)
            .font(titleFont)
            .text('A. TOTAL DEVENGADO', fifthColumn, totalAccrualLine)
            .text(formatMoney(payroll.total_accrual), seventhColumn + numberOffset(payroll.total_accrual), totalAccrualLine)
            .moveTo(fifthColumn, totalAccrualLine + 10).lineTo(seventhColumn - 25, totalAccrualLine + 10).dash(1, { space: 2 }).stroke()
            .moveTo(seventhColumn - 25, totalAccrualLine + 10).lineTo(seventhColumn + 35, totalAccrualLine + 10).undash().stroke();
    }

    var getDeduction = function(payroll, nameDeduction) {
        for (i = 0; i < payroll.deductions.length; i++) {
            deduction = payroll.deductions[i];
            if (deduction.name == nameDeduction) {
                return deduction;
            }
        }
        return {
            types: [],
            value: 0

        };
        //return undefined;
    }

    var getDeductions = function(payroll, namesDeductions) {
        deductions = [];
        for (i = 0; i < payroll.deductions.length; i++) {
            deduction = payroll.deductions[i];
            if (namesDeductions.includes(deduction.type_name)) {
                deductions.push(deduction);
            }
        }
        return deductions;
    }

    var newDeductionFirstPage = function(payroll) {
        // -----------------------------------------------------------------------------------------------------------------
        // --------------------------------------------- DEDUCTION ---------------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------
        deductionTop = totalAccrualLine + 20;

        firstColumn = 25;
        secondColumn = 30;
        thirdColumn = 60;
        fourthColumn = 85;
        fifthColumn = 117;
        sixthColumn = 448;
        seventhColumn = 535;

        totalColumn = 220;

        common_contingency = getDeduction(payroll, "CGC");
        unemployment = getDeduction(payroll, "DESMPL");
        professional_formation = getDeduction(payroll, "FP");
        extra_hours_e = getDeduction(payroll, "Horas Extraordinarias Fuerza Mayor");
        extra_hours_ne = getDeduction(payroll, "Resto Horas Extraordinarias");
        taxesD = getDeduction(payroll, "Dinerario");
        taxesS = getDeduction(payroll, "Especie");
        advances = getDeductions(payroll, ["Anticipo"]);
        spices = getDeduction(payroll, "Spices");
        other_deductions = getDeductions(payroll, ["Otras deducciones", "Embargo"]);
        irpf = getDeduction(payroll, "IRPF");

        existingTaxes = 0;

        if (totalAccrualLine <= 550) {
            total_aportation = common_contingency.value + unemployment.value + professional_formation.value + extra_hours_e.value + extra_hours_ne.value;
            // ----------------------------------------- APORTACIONES ----------------------------------------------------
            pdf
                .font(headingFont)
                .text('II. DEDUCCIONES', firstColumn, deductionTop)
                .fontSize(textSize)
                .text('1. Aportaciones del trabajador a las cotizaciones de la SS y conceptos de recaudación', secondColumn, deductionTop + 12)
                .font(textFont)
                .text(formatMoney(total_aportation), sixthColumn + numberOffset(total_aportation), deductionTop + 12)
                .moveTo(secondColumn, deductionTop + 22).lineTo(sixthColumn - 25, deductionTop + 22).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 25, deductionTop + 22).lineTo(sixthColumn + 25, deductionTop + 22).undash().stroke();

            deductionTop += 5;

            if (common_contingency.value && common_contingency.value > 0) {
                pdf
                    .font(textFont)
                    .text(formatMoney(common_contingency.value), thirdColumn + numberOffset(common_contingency.value), deductionTop + 24)
                    .text("  por un ", fourthColumn, deductionTop + 24)
                    .text(formatPercent(common_contingency.percent), fifthColumn + numberOffset(common_contingency.percent), deductionTop + 24)
                    .text("  de Contigencias comunes", fourthColumn + 60, deductionTop + 24);
            } else {
                deductionTop -= 12;
            }

            if (unemployment.value && unemployment.value > 0) {
                pdf
                    .text(formatMoney(unemployment.value), thirdColumn + numberOffset(unemployment.value), deductionTop + 36)
                    .text("  por un ", fourthColumn, deductionTop + 36)
                    .text(formatPercent(unemployment.percent), fifthColumn + numberOffset(unemployment.percent), deductionTop + 36)
                    .text("  de Desempleo", fourthColumn + 60, deductionTop + 36);
            } else {
                deductionTop -= 12;
            }

            if (unemployment.value && unemployment.value > 0) {
                pdf
                    .text(formatMoney(professional_formation.value), thirdColumn + numberOffset(professional_formation.value), deductionTop + 48)
                    .text("  por un ", fourthColumn, deductionTop + 48)
                    .text(formatPercent(professional_formation.percent), fifthColumn + numberOffset(professional_formation.percent), deductionTop + 48)
                    .text("  de Fromación profesional", fourthColumn + 60, deductionTop + 48);
            } else {
                deductionTop -= 12;
            }



            if (extra_hours_e.value && extra_hours_e.value > 0) {
                pdf
                    .text("  por Horas extraordinarias (Estruc.)", fourthColumn, deductionTop + 60)
                    .text(formatMoney(extra_hours_e.value), thirdColumn + numberOffset(extra_hours_e.value), deductionTop + 60);
            } else {
                deductionTop -= 12;
            }
            if (extra_hours_ne.value && extra_hours_ne.value > 0) {
                pdf
                    .text("  por Horas extraordinarias (No Estruc.)", fourthColumn, deductionTop + 72)
                    .text(formatMoney(extra_hours_ne.value), thirdColumn + numberOffset(extra_hours_ne.value), deductionTop + 72);
            } else {
                deductionTop -= 12;
            }

            // ----------------------------------------- IMPUESTOS ----------------------------------------------------
            taxes = taxesD.value + taxesS.value;
            pdf
                .font(headingFont)
                .text("2. Impuestos sobre la renta de personas físicas (I.R.P.F.)", secondColumn, deductionTop + 84)
                .font(textFont)
                //                .text(formatPercent(irpf.percent), sixthColumn - 130 + numberOffset(irpf.percent), deductionTop + 84)
                .text(formatMoney(irpf.value), sixthColumn + numberOffset(irpf.value), deductionTop + 84)
                .moveTo(secondColumn, deductionTop + 94).lineTo(sixthColumn - 25, deductionTop + 94).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 25, deductionTop + 94).lineTo(sixthColumn + 25, deductionTop + 94).undash().stroke();

            deductionTop += 5;

            if (taxesD.value && taxesD.value > 0) {
                pdf
                    .text(formatMoney(taxesD.amount), thirdColumn + numberOffset(taxesD.amount), deductionTop + 96)
                    .text("  por un ", fourthColumn, deductionTop + 96)
                    .text(formatPercent(taxesD.percent), fifthColumn + numberOffset(taxesD.percent), deductionTop + 96)
                    .text("  de Retribuciones dinerarias", fourthColumn + 60, deductionTop + 96);
            } else {
                deductionTop -= 12;
            }
            if (taxesS.value && taxesS.value > 0) {
                pdf
                    .text(formatMoney(taxesS.amount), thirdColumn + numberOffset(taxesS.amount), deductionTop + 108)
                    .text("  por un ", fourthColumn, deductionTop + 108)
                    .text(formatPercent(taxesS.percent), fifthColumn + numberOffset(taxesS.percent), deductionTop + 108)
                    .text("  de Retribuciones en especie", fourthColumn + 60, deductionTop + 108);
            } else {
                deductionTop -= 12;
            }

            // ----------------------------------------- ANTICIPOS ----------------------------------------------------
            advancesTOTAL = 0;
            for (var i = 0; i < advances.length; i++) {
                type = advances[i];
                advancesTOTAL += type.value;
            }

            pdf
                .font(headingFont)
                .text("3. Anticipos", secondColumn, deductionTop + 120 + existingTaxes)
                .font(textFont)
                .text(formatMoney(advancesTOTAL), sixthColumn + numberOffset(advancesTOTAL), deductionTop + 120 + existingTaxes)
                .moveTo(secondColumn, deductionTop + 130 + existingTaxes).lineTo(sixthColumn - 25, deductionTop + 130 + existingTaxes).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 25, deductionTop + 130 + existingTaxes).lineTo(sixthColumn + 25, deductionTop + 130 + existingTaxes).undash().stroke();

            deductionTop += 5;

            // ----------------------------------------- ESPECIES ----------------------------------------------------

            spicesTOTAL = 0;

            pdf
                .font(headingFont)
                .text("4. Valor de los productos recibidos en especie", secondColumn, deductionTop + 132 + existingTaxes)
                .font(textFont);


            nextLine = deductionTop + 150 + existingTaxes;

            for (i = 0; i < payroll.payments.length; i++) {
                payment = payroll.payments[i];

                if (payment.code == 13) {
                    spicesTOTAL += payment.amount;
                    pdf
                        .text(formatMoney(payment.amount), thirdColumn + numberOffset(payment.amount), nextLine)
                        .text(parseExpression(payment.description), fourthColumn + 3, nextLine);

                    nextLine += 12;
                }
            }
            pdf
                .text(formatMoney(spicesTOTAL), sixthColumn + numberOffset(spicesTOTAL), deductionTop + 132 + existingTaxes)
                .moveTo(secondColumn, deductionTop + 142 + existingTaxes).lineTo(sixthColumn - 25, deductionTop + 142 + existingTaxes).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 25, deductionTop + 142 + existingTaxes).lineTo(sixthColumn + 25, deductionTop + 142 + existingTaxes).undash().stroke();

            deductionTop = nextLine;
            // ------------------------------------- OTRAS DEDUCCIONES -----------------------------------------------
            pdf
                .font(headingFont)
                .text("5. Otras deducciones", secondColumn, deductionTop + existingTaxes)
                .font(textFont);

            other_deductionsTOTAL = 0;
            newLine = 15;
            deductionsNumb = 0;

            for (var i = 0; i < other_deductions.length; i++) {
                type = other_deductions[i];
                other_deductionsTOTAL += type.value;
                deductionsNumb += other_deductions.length + 4;
                pdf
                    .text("  por " + type.description, fourthColumn, deductionTop + existingTaxes + newLine * (i + 1))
                    .text(formatMoney(type.value), thirdColumn + numberOffset(type.value), deductionTop + existingTaxes + newLine * (i + 1) + existingTaxes);
            }

            pdf
                .font(textFont)
                .text(formatMoney(other_deductionsTOTAL), sixthColumn + numberOffset(other_deductionsTOTAL), deductionTop + existingTaxes)
                .moveTo(secondColumn, deductionTop + 10 + existingTaxes).lineTo(sixthColumn - 25, deductionTop + 10 + existingTaxes).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 25, deductionTop + 10 + existingTaxes).lineTo(sixthColumn + 25, deductionTop + 10 + existingTaxes).undash().stroke();


            // ----------------------------------------- TOTAL A DEDUCIR ------------------------------------------------
            totalAccrualLine = deductionTop + existingTaxes + newLine * (deductionsNumb) + 30;

            pdf
                .fontSize(titleSize)
                .font(titleFont)
                .text('B. TOTAL A DEDUCIR', totalColumn, totalAccrualLine)
                .text(formatMoney(payroll.total_deductions), seventhColumn + numberOffset(payroll.total_deductions), totalAccrualLine)
                .moveTo(totalColumn, totalAccrualLine + 10).lineTo(seventhColumn - 25, totalAccrualLine + 10).dash(1, { space: 2 }).stroke()
                .moveTo(seventhColumn - 25, totalAccrualLine + 10).lineTo(seventhColumn + 35, totalAccrualLine + 10).undash().stroke();

        } else {
            // ----------------------------------------- TOTAL A DEDUCIR ------------------------------------------------
            pdf
                .fontSize(titleSize)
                .font(titleFont)
                .text('B. TOTAL A DEDUCIR (Página siguiente)', totalColumn, deductionTop)
                .text(formatMoney(payroll.total_deductions), seventhColumn + numberOffset(payroll.total_deductions), deductionTop)
                .moveTo(totalColumn, deductionTop + 10).lineTo(seventhColumn - 25, deductionTop + 10).dash(1, { space: 2 }).stroke()
                .moveTo(seventhColumn - 25, deductionTop + 10).lineTo(seventhColumn + 35, deductionTop + 10).undash().stroke();
        }
    }

    var newDeductionSecondPage = function(payroll) {
        console.error('newDeductionSecondPage totalAccrualLine : ' + totalAccrualLine);
        if (totalAccrualLine > 550) {
            //NEW PAGE
            pdf.addPage();
            //PDF Styles

            newHearderTittle(payroll);
            newEnterpriseBox(payroll);
            newEmployeeBox(payroll);
            newSettlementBox(payroll);

            deductionTop = 125;

            firstColumn = 25;
            secondColumn = 30;
            thirdColumn = 60;
            fourthColumn = 85;
            fifthColumn = 117;
            sixthColumn = 448;
            seventhColumn = 535;

            totalColumn = 220;

            topLeftCorner = 10;
            boxWidth = 585;
            accrualTop = 115;

            common_contingency = getDeduction(payroll, "CGC");
            unemployment = getDeduction(payroll, "DESMPL");
            professional_formation = getDeduction(payroll, "FP");
            extra_hours_e = getDeduction(payroll, "Horas Extraordinarias Fuerza Mayor");
            extra_hours_ne = getDeduction(payroll, "Resto Horas Extraordinarias");
            taxesD = getDeduction(payroll, "Dinerario");
            taxesS = getDeduction(payroll, "Especie");
            advances = getDeduction(payroll, "Anticipo");
            spices = getDeduction(payroll, "Valor de productos en especie");
            other_deductions = getDeduction(payroll, "Otras deducciones");
            irpf = getDeduction(payroll, "IRPF");

            pdf
                .moveTo(topLeftCorner, accrualTop).lineTo(boxWidth, accrualTop).stroke();

            total_aportation = common_contingency.value + unemployment.value + professional_formation.value + extra_hours_e.value + extra_hours_ne.value;
            // ----------------------------------------- APORTACIONES ----------------------------------------------------
            pdf
                .font(headingFont)
                .text('II. DEDUCCIONES', firstColumn, deductionTop)
                .fontSize(textSize)
                .text('1. Aportaciones del trabajador a las cotizaciones de la SS y conceptos de recaudación', secondColumn, deductionTop + 12)
                .font(textFont)
                .text(formatMoney(total_aportation), sixthColumn + numberOffset(total_aportation), deductionTop + 12)
                .moveTo(secondColumn, deductionTop + 22).lineTo(sixthColumn - 25, deductionTop + 22).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 25, deductionTop + 22).lineTo(sixthColumn + 25, deductionTop + 22).undash().stroke();

            deductionTop += 5;

            pdf
                .font(textFont)
                .text(formatMoney(common_contingency.value), thirdColumn + numberOffset(common_contingency.value), deductionTop + 24)
                .text("  por un ", fourthColumn, deductionTop + 24)
                .text(formatPercent(common_contingency.percent), fifthColumn + numberOffset(common_contingency.percent), deductionTop + 24)
                .text("  de Contigencias comunes", fourthColumn + 60, deductionTop + 24)
                .text(formatMoney(unemployment.value), thirdColumn + numberOffset(unemployment.value), deductionTop + 36)
                .text("  por un ", fourthColumn, deductionTop + 36)
                .text(formatPercent(unemployment.percent), fifthColumn + numberOffset(unemployment.percent), deductionTop + 36)
                .text("  de Desempleo", fourthColumn + 60, deductionTop + 36)
                .text(formatMoney(professional_formation.value), thirdColumn + numberOffset(professional_formation.value), deductionTop + 48)
                .text("  por un ", fourthColumn, deductionTop + 48)
                .text(formatPercent(professional_formation.percent), fifthColumn + numberOffset(professional_formation.percent), deductionTop + 48)
                .text("  de Fromación profesional", fourthColumn + 60, deductionTop + 48);
            if (extra_hours_e.value && extra_hours_e.value > 0) {
                pdf
                    .text("  por Horas extraordinarias (Estruc.)", fourthColumn, deductionTop + 60)
                    .text(formatMoney(extra_hours_e.value), thirdColumn + numberOffset(extra_hours_e.value), deductionTop + 60);
            } else {
                deductionTop -= 12;
            }
            if (extra_hours_ne.value && extra_hours_ne.value > 0) {
                pdf
                    .text("  por Horas extraordinarias (No Estruc.)", fourthColumn, deductionTop + 72)
                    .text(formatMoney(extra_hours_ne.value), thirdColumn + numberOffset(extra_hours_ne.value), deductionTop + 72);
            } else {
                deductionTop -= 12;
            }

            // ----------------------------------------- IMPUESTOS ----------------------------------------------------
            taxes = taxesD.value + taxesS.value;
            pdf
                .font(headingFont)
                .text("2. Impuestos sobre la renta de personas físicas (I.R.P.F.)", secondColumn, deductionTop + 84)
                .font(textFont)
                //                .text(formatPercent(irpf.percent), sixthColumn - 130 + numberOffset(irpf.percent), deductionTop + 84)
                .text(formatMoney(irpf.value), sixthColumn + numberOffset(irpf.value), deductionTop + 84)
                .moveTo(secondColumn, deductionTop + 94).lineTo(sixthColumn - 25, deductionTop + 94).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 25, deductionTop + 94).lineTo(sixthColumn + 25, deductionTop + 94).undash().stroke();

            deductionTop += 5;

            if (taxesD.value && taxesD.value > 0) {
                pdf
                    .text(formatMoney(taxesD.value), thirdColumn + numberOffset(taxesD.value), deductionTop + 96)
                    .text("  por un ", fourthColumn, deductionTop + 96)
                    .text(formatPercent(taxesD.percent), fifthColumn + numberOffset(taxesD.percent), deductionTop + 96)
                    .text("  de Retribuciones dinerarias", fourthColumn + 60, deductionTop + 96);
            } else {
                deductionTop -= 12;
            }
            if (taxesS.value && taxesS.value > 0) {
                pdf
                    .text(formatMoney(taxesS.value), thirdColumn + numberOffset(taxesS.value), deductionTop + 108)
                    .text("  por un ", fourthColumn, deductionTop + 108)
                    .text(formatPercent(taxesS.percent), fifthColumn + numberOffset(taxesS.percent), deductionTop + 108)
                    .text("  de Retribuciones en especie", fourthColumn + 60, deductionTop + 108);
            } else {
                deductionTop -= 12;
            }

            // ----------------------------------------- ANTICIPOS ----------------------------------------------------
            advancesTOTAL = 0;
            for (var i = 0; i < advances.types.length; i++) {
                type = advances.types[i];
                advancesTOTAL += type.value;
            }

            pdf
                .font(headingFont)
                .text("3. Anticipos", secondColumn, deductionTop + 120 + existingTaxes)
                .font(textFont)
                .text(formatMoney(advancesTOTAL), fifthColumn + numberOffset(advancesTOTAL), deductionTop + 120 + existingTaxes)
                .moveTo(secondColumn, deductionTop + 130 + existingTaxes).lineTo(sixthColumn - 25, deductionTop + 130 + existingTaxes).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 25, deductionTop + 130 + existingTaxes).lineTo(sixthColumn + 25, deductionTop + 130 + existingTaxes).undash().stroke();

            deductionTop += 5;

            // ----------------------------------------- ESPECIES ----------------------------------------------------
            spicesTOTAL = 0;
            for (var i = 0; i < spices.types.length; i++) {
                type = spices.types[i];
                spicesTOTAL += type.value;
            }

            pdf
                .font(headingFont)
                .text("4. Valor de los productos recibidos en especie", secondColumn, deductionTop + 132 + existingTaxes)
                .font(textFont)
                .text(formatAmount(spicesTOTAL), fifthColumn + numberOffset(spicesTOTAL), deductionTop + 132 + existingTaxes)
                .moveTo(secondColumn, deductionTop + 142 + existingTaxes).lineTo(sixthColumn - 25, deductionTop + 142 + existingTaxes).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 25, deductionTop + 142 + existingTaxes).lineTo(sixthColumn + 25, deductionTop + 142 + existingTaxes).undash().stroke();

            deductionTop += 5;
            // ------------------------------------- OTRAS DEDUCCIONES -----------------------------------------------
            pdf
                .font(headingFont)
                .text("5. Otras deducciones", secondColumn, deductionTop + 144 + existingTaxes)
                .font(textFont);

            other_deductionsTOTAL = 0;
            newLine = 12;
            deductionsNumb = 0;

            for (var i = 0; i < other_deductions.types.length; i++) {
                type = other_deductions.types[i];
                other_deductionsTOTAL += type.value;
                deductionsNumb += other_deductions.types.length + 4;
                pdf
                    .text("  por " + type.name, fourthColumn, deductionTop + 144 + newLine * (i + 1))
                    .text(formatMoney(type.value), thirdColumn + numberOffset(type.value), deductionTop + 144 + newLine * (i + 1) + existingTaxes);
            }

            pdf
                .font(textFont)
                .text(formatAmount(other_deductionsTOTAL), fifthColumn + numberOffset(other_deductionsTOTAL), deductionTop + 144 + existingTaxes)
                .moveTo(secondColumn, deductionTop + 154 + existingTaxes).lineTo(sixthColumn - 25, deductionTop + 154 + existingTaxes).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 25, deductionTop + 154 + existingTaxes).lineTo(sixthColumn + 25, deductionTop + 154 + existingTaxes).undash().stroke();

            // ----------------------------------------- TOTAL A DEDUCIR ------------------------------------------------
            totalAccrualLine = accrualTop + newLine * (deductionsNumb) + 50;

            pdf
                .fontSize(titleSize)
                .font(titleFont)
                .text('A. TOTAL DEVENGADO (página anterior)', totalColumn, deductionTop + totalAccrualLine + existingTaxes)
                .text(formatMoney(payroll.total_accrual), seventhColumn + numberOffset(payroll.total_accrual), deductionTop + totalAccrualLine + existingTaxes)
                .moveTo(totalColumn, deductionTop + totalAccrualLine + 10 + existingTaxes).lineTo(seventhColumn - 25, deductionTop + totalAccrualLine + 10 + existingTaxes).dash(1, { space: 2 }).stroke()
                .moveTo(seventhColumn - 25, deductionTop + totalAccrualLine + 10 + existingTaxes).lineTo(seventhColumn + 35, deductionTop + totalAccrualLine + 10 + existingTaxes).undash().stroke();

            pdf
                .fontSize(titleSize)
                .font(titleFont)
                .text('B. TOTAL DEDUCIR', totalColumn, deductionTop + totalAccrualLine + 17 + existingTaxes)
                .text(formatMoney(payroll.total_deductions), seventhColumn + numberOffset(payroll.total_deductions), deductionTop + totalAccrualLine + 17 + existingTaxes)
                .moveTo(totalColumn, deductionTop + totalAccrualLine + 27 + existingTaxes).lineTo(seventhColumn - 25, deductionTop + totalAccrualLine + 27 + existingTaxes).dash(1, { space: 2 }).stroke()
                .moveTo(seventhColumn - 25, deductionTop + totalAccrualLine + 27 + existingTaxes).lineTo(seventhColumn + 35, deductionTop + totalAccrualLine + 27 + existingTaxes).undash().stroke();

            newSignatureDate(payroll);
            newFooterBox(payroll);
        }
    }

    //Main
    for (let i = 0; i < payrolls.length; i++) {
        const payroll = payrolls[i];
        newHearderTittle(payroll);
        newEnterpriseBox(payroll);
        newEmployeeBox(payroll);
        newSettlementBox(payroll);
        newAccrual(payroll);
        newDeductionFirstPage(payroll);
        newSignatureDate(payroll);
        newFooterBox(payroll);
        newDeductionSecondPage(payroll);

        if ((i + 1) != payrolls.length)
            pdf.addPage();
    }

    //End PDF
    pdf.end();

}

module.exports.standardPayroll = function(payroll, stream){
  //Initialize pdf object
  var pdf = new(PDF);
  pdf.pipe(stream);

  //PDF Styles
  var pdfWidth = pdf.page.width
		 - pdf.page.margins.left
     - pdf.page.margins.right
	;

  pdf.page.width = pdf.page.width + 60; //Ancho para que no haya salto de linea
  pdf.page.margins = {t0p: 50, bottom: 0, left: 72, right: 72}; //Margenes del documento

  //Variables y constants
	textSize = 6;
	textFont = 'Helvetica';

	titleSize = 8;
	titleFont = 'Helvetica-Bold';

	headingSize = 4;
	headingFont = 'Helvetica-Bold';

  signature = 450;

  var salary_perceptions_codes = [0001];
  var extra_hours_codes = [0002, 0003];
  var extra_perks_codes = [0004, 0005];
  var spices_salary_codes = [0013, 0014, 0015, 0016, 0017, 0018, 0019, 0020, 0021, 0022, 0023, 0024, 0025, 0026];


  //Aux Methods
  var formatInputData = function (data, parseData){
    if(data == null || data == undefined){
      if(parseData == 'string')
        return '';
      else if(parseData == 'number')
        return '';
    }else{
      var typeData = typeof data;
      //POSIBLE IMPLEMENTACION PARA ALGO MAS INTELIGENTE
      if(typeData == 'number' && data == 0){
        return '';
      }
      return data;
    }
  }

	var formatAmount = function(amount) {
		return (amount && amount != '' && amount != null && amount != 0) ? amount.toFixed(2) : '';
	}

  var formatPercent = function(percent) {
	  if('string' == typeof percent)
	  	return percent;
	  else
	  	return (percent && percent != '' && percent != null && percent != 0) ? percent.toFixed(2)+" %" : '';
  }

  var formatMoney = function(amount) {
		return (amount && amount != '' && amount != null && amount != 0) ? amount.toFixed(2)+" €" : '';
	}

  var numberOffset = function(number) {
    if(number != '' && 'string' != typeof number){
      var formatNumber = formatAmount(number);
      var split = formatNumber.split('.');
      switch (split[0].length) {
        case 1:
            return 27;
        case 2:
            return 22;
        case 3:
            return 18;
        case 4:
            return 13;
        case 5:
            return 9;
        case 6:
            return 5;
        default:
            return 0;
      }
    }else{
      return 0;
    }
  }

  var formatDate = function(date) {
		var split = date.split('/');
    return split[0] + ' de ' + getStrMonth(split[1]) + ' de ' + split[2];
	}

  var getStrMonth = function (numberMonth) {
    switch (numberMonth) {
      case '1':
          return 'enero';
      case '2':
          return 'febrero';
      case '3':
          return 'marzo';
      case '4':
          return 'abril';
      case '5':
          return 'mayo';
      case '6':
          return 'junio';
      case '7':
          return 'julio';
      case '8':
          return 'agosto';
      case '9':
          return 'septiembre';
      case '10':
          return 'octubre';
      case '11':
          return 'noviembre';
      case '12':
          return 'diciembre';
      default:
          return 'error';
    }
  }


  //Main Methods
  var newHearderTittle = function(){
    //PDF drawing position using for lines, starting on the t0p of the page
    y = pdf.y;
  	x = pdf.x-50;
    originalX = pdf.x-50;
    originalY = pdf.y;
    ox = pdf.x
  	t0p = 2;
  	left = 8;
  	width = (pdfWidth-30) * 1/2;
    pdf.lineWidth(1);

    paddingTopTL = 14;  //Padding-t0p text / line
    paddingTop = 4;     //Padding-t0p line / text, text / text
    paddingLeft1 = 8;   //Padding-left first text
    paddingLeft2 = 16;  //Padding-left second text
    paddingLeft3 = 20;  //Padding-left third text
    paddingLeft4 = 60;
    paddingLeft5 = 80;
    marginTL = 2;       //Margin bettwen text / line, line / text, line / line

    //Start drawing PDF
    pdf
  	  .font(titleFont)
  		.fontSize(titleSize)
  		.text('RECIBO INDIVIDUAL JUSTIFICATIVO DEL PAGO DE SALARIO', x+155, 20)
  		.moveDown(1);
  }

  var newEnterpriseBox = function(){
    y = pdf.y-8;
    oy = y;

    rightEnterpriseX = ox + width;      //Position X for right line enterprise box
    maxRightWidth = x + pdfWidth + 100; //Position X for right line employee, salary, footer box
    sizVertivalLineEE = 20;             //Size vertical line for enterprise, employee box

    // -----------------------------------------------------------------------------------------------------------------
    // -------------------------------------------- ENTERPRISE ---------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    pdf
      .moveTo(x, y).lineTo(rightEnterpriseX, y).stroke()      //Horizontal t0p line

      .font(headingFont)
      .text('Empresa: ', x + paddingLeft1, y = y + paddingTop)
      .text(formatInputData(payroll.enterprise.name, 'string'), x + paddingLeft4, y)

      .text('Domicilio: ', x + paddingLeft1, y = pdf.y + paddingTop)
      .font(textFont)
      .text(formatInputData(payroll.enterprise.address, 'string'), x + paddingLeft4, y)
      .text(formatInputData(payroll.enterprise.locality , 'string'), x + paddingLeft4, y = y + paddingTop)

      .font(headingFont)
      .text('CIF: ' , x + paddingLeft1 , y = pdf.y + paddingTop*3)
      .font(textFont)
      .text(formatInputData(payroll.enterprise.cif, 'string'), x + paddingLeft4, y)

      .font(headingFont)
      .text('CCC: ' , x + paddingLeft1, y = pdf.y + paddingTop*3)
      .font(textFont)
      .text(formatInputData(payroll.enterprise.ccc, 'string'), x + paddingLeft4, y)

      .moveTo(x, oy).lineTo(x, y + sizVertivalLineEE).stroke()                                     //Vertical left line
  	  .moveTo(rightEnterpriseX, oy).lineTo(rightEnterpriseX, y + sizVertivalLineEE).stroke()       //Vertical right line
      .moveTo(x, y + sizVertivalLineEE).lineTo(rightEnterpriseX, y + sizVertivalLineEE).stroke();  //Horizontal bottom line

  }

  var newEmployeeBox = function(){
    // -----------------------------------------------------------------------------------------------------------------
    // -------------------------------------------- EMPLOYEE -----------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    leftEmployeeX = ox + width + left + 5 ;   //Position X for left line employee box
    employeeY = oy + t0p + 2;                 //Start y for employee box
    employeeTop = 12;                         //Padding-t0p bettwen text

    pdf
      .moveTo(leftEmployeeX - left, oy).lineTo(maxRightWidth, oy).stroke()   //Horizontal t0p line

      .font(headingFont)
      .text('Trabajador: ' , leftEmployeeX, employeeY)
      .text(formatInputData(payroll.employee.fullname, 'string'), leftEmployeeX + paddingLeft4, employeeY)

      .text('NIF: ', leftEmployeeX, employeeY = employeeY + employeeTop)
      .font(textFont)
      .text(formatInputData(payroll.employee.nif, 'string'), leftEmployeeX + paddingLeft4, employeeY)

      .font(headingFont)
      .text('Nº S.S.: ', leftEmployeeX, employeeY = employeeY + employeeTop)
      .font(textFont)
      .text(formatInputData(payroll.employee.ss, 'number'), leftEmployeeX + paddingLeft4, employeeY)

      .font(headingFont)
      .text('Grupo profesional: ', leftEmployeeX, employeeY = employeeY + employeeTop)
      .font(textFont)
      .text(formatInputData(payroll.employee.professional_group, 'string'), leftEmployeeX + paddingLeft5, employeeY)

      .font(headingFont)
      .text('Grupo cotización: ', leftEmployeeX, employeeY = employeeY + employeeTop)
      .font(textFont)
      .text(formatInputData(payroll.employee.quote_group, 'string'), leftEmployeeX + paddingLeft5, employeeY)

      .font(headingFont)
      .text('Fecha antigüedad: ', leftEmployeeX + 165, employeeY)
      .font(textFont)
      .text(formatInputData(payroll.employee.seniority_date, 'string'), leftEmployeeX + 160 + paddingLeft5, employeeY)

      .moveTo(leftEmployeeX - left, oy).lineTo(leftEmployeeX - left, y + sizVertivalLineEE).stroke()              //Vertical left line
      .moveTo(maxRightWidth, oy).lineTo(maxRightWidth, y + sizVertivalLineEE).stroke()                            //Vertical right line
      .moveTo(leftEmployeeX - left, y + sizVertivalLineEE).lineTo(maxRightWidth, y + sizVertivalLineEE).stroke()  //Horizontal bottom line
      .moveDown(1);
  }

  var newSettlementBox = function(){
    // -----------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- SETTLEMENT --------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    start_date = formatDate(payroll.settlement.start_date);
    end_date = formatDate(payroll.settlement.end_date);
    settlementWidth = x + pdfWidth + 100;
    settlementTop = 4;
    settlementX = ox - 50 + left;
    settlementY = y + t0p + 22;
    settlementYHeight = settlementY + 15;

    pdf
      .moveTo(x, settlementY).lineTo(settlementWidth, settlementY).stroke()   //Horizontal t0p line

      .font(headingFont)
      .text('Periodo de liquidación: ' , settlementX, settlementY + settlementTop, {continued: true})
      .font(textFont)
      .text('del '+ start_date +' al '+ end_date, {continued: true})

      .font(headingFont)
      .text('Total días: ', settlementX + 255 , settlementY + settlementTop, {continued: true})
      .font(textFont)
      .text(payroll.settlement.total_days)

      .moveTo(x, settlementY).lineTo(x, settlementYHeight).stroke()                               //Vertical left line
      .moveTo(settlementWidth, settlementY).lineTo(settlementWidth, settlementYHeight).stroke()   //Vertical right line
      .moveTo(x, settlementYHeight).lineTo(settlementWidth, settlementYHeight).stroke();          //Horizontal bottom line
  }

  var newFooterBox = function(){
    // -----------------------------------------------------------------------------------------------------------------
    // -------------------------------------------- FOOTER -------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    footer = 518;
    accrualWidth = x + pdfWidth + 100;
    pdf
      .moveTo(originalX, originalY+footer).lineTo(originalX+pdfWidth+100, originalY+footer).undash().stroke()     //Horizontal t0p line
      .font(textFont)
      .text('DETERMINACIÓN DE LAS BASES DE COTIZACIÓN A LA SEGURIDAD SOCIAL Y CONCEPTOS DE RECAUDACIÓN CONJUNTAS Y DE ' +
          'LA BASE' , originalX + left , originalY + footer + t0p + 2)
      .font(textFont)
      .text('SUJETA A RETENCIÓN DEL IRPF Y APORTACIÓN DE LA EMPRESA' , originalX + left , originalY + footer + t0p + 12)
      .font(textFont)
      .text('1. Contingencias comunes' , originalX + left + 5 , originalY + footer + t0p + 25, {continued: true})
      .font(textFont)
      .text('BASE', originalX + left + 280 , originalY + footer + t0p + 25, {continued: true})
      .font(textFont)
      .text('TIPO', originalX + left + 325 , originalY + footer + t0p + 25, {continued: true})
      .font(textFont)
      .text('AP. EMPRESA', originalX + left + 355 , originalY + footer + t0p + 25)

      .font(textFont)
      .text('Importe remuneración mensual' , originalX + left + 10 , originalY + footer + t0p + 39)
      .font(textFont)
      .text(formatAmount(payroll.footer_ss_quotation.common_contingency.monthly_remuneration), originalX + left + 250 + numberOffset(payroll.footer_ss_quotation.common_contingency.monthly_remuneration) , originalY + footer + t0p + 39)
      .lineWidth(0.1)
      .moveTo(originalX + left + 10 , originalY + footer + t0p + 49).lineTo(originalX + left + 300 , originalY + footer + t0p + 49).stroke()

      .font(textFont)
      .text('Importe prorrata de pagas extraordinarias' , originalX + left + 10 , originalY + footer + t0p + 53)
      .font(textFont)
      .text(formatAmount(payroll.footer_ss_quotation.common_contingency.extraordinary_pay_packet), originalX + left + 250 + numberOffset(payroll.footer_ss_quotation.common_contingency.extraordinary_pay_packet), originalY + footer + t0p + 53)
      .moveTo(originalX + left + 10 , originalY + footer + t0p + 63).lineTo(originalX + left + 300 , originalY + footer + t0p + 63).stroke()

      .font(textFont)
      .text(formatAmount(payroll.footer_ss_quotation.common_contingency.base), originalX + left + 353 + numberOffset(payroll.footer_ss_quotation.common_contingency.base) , originalY + footer + t0p + 46)
      .font(textFont)
      .text(formatPercent(payroll.footer_ss_quotation.common_contingency.type_percent), originalX + left + 408 + numberOffset(payroll.footer_ss_quotation.common_contingency.type_percent) , originalY + footer + t0p + 46)
      .font(textFont)
      .text(formatAmount(payroll.footer_ss_quotation.common_contingency.company_input) , originalX + left + 497 + numberOffset(payroll.footer_ss_quotation.common_contingency.company_input), originalY + footer + t0p + 46)
      .moveTo(originalX + left + 310 , originalY + footer + t0p + 56).lineTo(originalX + left + 550 , originalY + footer + t0p + 56).stroke()

      .font(textFont)
      .text('2. Contingencias profesionales' , originalX + left + 5 , originalY + footer + t0p + 80)
      .font(textFont)
      .text('conceptos de recaudación' , originalX + left + 5 , originalY + footer + t0p + 90)
      .font(textFont)
      .text('conjunta' , originalX + left + 5 , originalY + footer + t0p + 100)

      .font(textFont)
      .text('AT Y EP' , originalX + left + 160 , originalY + footer + t0p + 67)
      .moveTo(originalX + left + 160 , originalY + footer + t0p + 77).lineTo(originalX + left + 301 , originalY + footer + t0p + 77).stroke()
      .font(textFont)
      .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_at), originalX + left + 408 + numberOffset(payroll.footer_ss_quotation.professional_contingency.type_percent_at), originalY + footer + t0p + 67)
      .font(textFont)
      .text(formatAmount(payroll.footer_ss_quotation.professional_contingency.company_input_at), originalX + left + 497 +  numberOffset(payroll.footer_ss_quotation.professional_contingency.company_input_at), originalY + footer + t0p + 67)
      .moveTo(originalX + left + 410 , originalY + footer + t0p + 77).lineTo(originalX + left + 551 , originalY + footer + t0p + 77).stroke()

      .font(textFont)
      .text('Desempleo' , originalX + left + 160 , originalY + footer + t0p + 81)
      .moveTo(originalX + left + 160 , originalY + footer + t0p + 91).lineTo(originalX + left + 301 , originalY + footer + t0p + 91).stroke()
      .font(textFont)
      .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_unemployment), originalX + left + 408 + numberOffset(payroll.footer_ss_quotation.professional_contingency.type_percent_unemployment), originalY + footer + t0p + 81)
      .font(textFont)
      .text(formatAmount(payroll.footer_ss_quotation.professional_contingency.company_input_unemployment), originalX + left + 497 +  numberOffset(payroll.footer_ss_quotation.professional_contingency.company_input_unemployment), originalY + footer + t0p + 81)
      .moveTo(originalX + left + 410 , originalY + footer + t0p + 91).lineTo(originalX + left + 551 , originalY + footer + t0p + 91).stroke()

      .font(textFont)
      .text(formatAmount(payroll.footer_ss_quotation.professional_contingency.base), originalX + left + 353 + numberOffset(payroll.footer_ss_quotation.professional_contingency.base), originalY + footer + t0p + 88)
      .moveTo(originalX + left + 310 , originalY + footer + t0p + 98).lineTo(originalX + left + 400 , originalY + footer + t0p + 98).stroke()

      .font(textFont)
      .text('Fromación profesional' , originalX + left + 160 , originalY + footer + t0p + 95)
      .moveTo(originalX + left + 160 , originalY + footer + t0p + 105).lineTo(originalX + left + 301 , originalY + footer + t0p + 105).stroke()
      .font(textFont)
      .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_professional_formation), originalX + left + 408 + numberOffset(payroll.footer_ss_quotation.professional_contingency.type_percent_professional_formation), originalY + footer + t0p + 95)
      .font(textFont)
      .text(formatAmount(payroll.footer_ss_quotation.professional_contingency.company_input_professional_formation), originalX + left + 497 +  numberOffset(payroll.footer_ss_quotation.professional_contingency.company_input_professional_formation), originalY + footer + t0p + 95)
      .moveTo(originalX + left + 410 , originalY + footer + t0p + 105).lineTo(originalX + left + 551 , originalY + footer + t0p + 105).stroke()

      .font(textFont)
      .text('Fondo de Garantía Salarial' , originalX + left + 160 , originalY + footer + t0p + 109)
      .moveTo(originalX + left + 160 , originalY + footer + t0p + 119).lineTo(originalX + left + 301 , originalY + footer + t0p + 119).stroke()
      .font(textFont)
      .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_salary_warranty), originalX + left + 408 + numberOffset(payroll.footer_ss_quotation.professional_contingency.type_percent_salary_warranty), originalY + footer + t0p + 109)
      .font(textFont)
      .text(formatAmount(payroll.footer_ss_quotation.professional_contingency.company_input_salary_warranty), originalX + left + 497 +  numberOffset(payroll.footer_ss_quotation.professional_contingency.company_input_salary_warranty), originalY + footer + t0p + 109)
      .moveTo(originalX + left + 410 , originalY + footer + t0p + 119).lineTo(originalX + left + 551 , originalY + footer + t0p + 119).stroke()

      .font(textFont)
      .text('3. Cotización adicional por horas extraordinarias' , originalX + left + 5 , originalY + footer + t0p + 123)
      .font(textFont)
      .text('Fuerza mayor' , originalX + left + 160 , originalY + footer + t0p + 137)
      .text(formatAmount(payroll.footer_ss_quotation.aditional_quotation.base_overwhelming_force), originalX + left + 353 + numberOffset(payroll.footer_ss_quotation.aditional_quotation.base_overwhelming_force), originalY + footer + t0p + 137)
      .text(formatAmount(payroll.footer_ss_quotation.aditional_quotation.company_input_overwhelming_force), originalX + left + 497 +  numberOffset(payroll.footer_ss_quotation.aditional_quotation.company_input_overwhelming_force), originalY + footer + t0p + 137)
      .moveTo(originalX + left + 160 , originalY + footer + t0p + 147).lineTo(originalX + left + 400 , originalY + footer + t0p + 147).stroke()
      .moveTo(originalX + left + 410 , originalY + footer + t0p + 147).lineTo(originalX + left + 551 , originalY + footer + t0p + 147).stroke()

      .font(textFont)
      .text('No estructurales' , originalX + left + 160 , originalY + footer + t0p + 151)
      .text(formatAmount(payroll.footer_ss_quotation.aditional_quotation.base_non_structural), originalX + left + 353 + numberOffset(payroll.footer_ss_quotation.aditional_quotation.base_non_structural), originalY + footer + t0p + 151)
      .text(formatAmount(payroll.footer_ss_quotation.aditional_quotation.company_input_non_structural), originalX + left + 497 +  numberOffset(payroll.footer_ss_quotation.aditional_quotation.company_input_non_structural), originalY + footer + t0p + 151)
      .moveTo(originalX + left + 160 , originalY + footer + t0p + 161).lineTo(originalX + left + 400 , originalY + footer + t0p + 161).stroke()
      .moveTo(originalX + left + 410 , originalY + footer + t0p + 161).lineTo(originalX + left + 551 , originalY + footer + t0p + 161).stroke();

      taxesS = getDeduction("Especie");
      if(!taxesS.value || taxesS.value == 0){
        pdf
        .font(textFont)
        .text('4. Base sujeta a retención del IRPF' , originalX + left + 5 , originalY + footer + t0p + 165)
        .text(formatAmount(payroll.footer_ss_quotation.base_irpf), originalX + left + 353 + numberOffset(payroll.footer_ss_quotation.base_irpf), originalY + footer + t0p + 165)
        .moveTo(originalX + left + 13 , originalY + footer + t0p + 175).lineTo(originalX + left + 400 , originalY + footer + t0p + 175).stroke();
      }else{
        taxesD = getDeduction("Dinerario");
        pdf
          .font(textFont)
          .text('4. Base sujeta a retención del IRPF: ' + taxesS.value + '€  en especie + ' +  taxesD.value + "€  en retribuciones dinerarias", originalX + left + 5 , originalY + footer + t0p + 165)
          .text(formatAmount(payroll.footer_ss_quotation.base_irpf), originalX + left + 353 + numberOffset(payroll.footer_ss_quotation.base_irpf), originalY + footer + t0p + 165)
          .moveTo(originalX + left + 13 , originalY + footer + t0p + 175).lineTo(originalX + left + 400 , originalY + footer + t0p + 175).stroke()
          .text('Total aportaciones' , originalX + left + 415 , originalY + footer + t0p + 165)
          .text(formatAmount(payroll.footer_ss_quotation.total_company), originalX + left + 497 +  numberOffset(payroll.footer_ss_quotation.total_company), originalY + footer + t0p + 165)
          .moveTo(originalX + left + 410 , originalY + footer + t0p + 175).lineTo(originalX + left + 551 , originalY + footer + t0p + 175).stroke();
      }

      pdf
        .lineWidth(1)
        .moveTo(originalX, originalY+footer).lineTo(originalX, originalY+footer+180).stroke()                //Vertical left line
        .moveTo(accrualWidth, originalY+footer).lineTo(accrualWidth, originalY+footer+180).stroke()          //Vertical right line
        .moveTo(originalX, originalY+footer+180).lineTo(accrualWidth, originalY+footer+180).stroke();        //Horizontal bottom line
  }

  var newSignatureDate = function(){
    // -----------------------------------------------------------------------------------------------------------------
    // ------------------------------------------ SIGNATURE / DATE -----------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    paddingSignature = 15;
    paddingDate = 10;
    centerTableHeight = 468;

    pdf
      .fontSize(titleSize)
      .text('Firma y sello de la empresa' , originalX + 160 , originalY + signature)
      .image(payroll.signature_logo, originalX + 160, originalY + signature + paddingSignature, {scale: 0.06})

      .text(end_date,  originalX + 310 , originalY + signature)
      .fontSize(titleSize)
      .text('RECIBÍ' , originalX + 310 , originalY + signature + paddingDate)

      .lineWidth(1)
      .moveTo(originalXCenterTable, originalYCenterTable).lineTo(originalXCenterTable, originalYCenterTable + centerTableHeight).stroke()                //Vertical left line
      .moveTo(accrualWidth, originalYCenterTable).lineTo(accrualWidth, originalYCenterTable + centerTableHeight).stroke()                                //Vertical right line
      .moveTo(originalXCenterTable, originalYCenterTable + centerTableHeight).lineTo(accrualWidth, originalYCenterTable + centerTableHeight).stroke();   //Horizontal bottom line
  }

  var getAccrual = function(accrualName){
    for(i = 0; i < payroll.accruals.length; i++){
      accrual = payroll.accruals[i];
      if(accrual.accrual_name == accrualName)
        return accrual;
    }
    return undefined;
  }

  var newAccrual = function(){
    // -----------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- ACCRUAL -----------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    y = settlementY+19;
    accrualY = settlementY+10;
    accrualX = x + left;
    accrualWidth = x + pdfWidth + 100;
    originalXCenterTable = x;
    originalYCenterTable = y;
    paddingTopLine = 8;
    paddingTopTT = 11;
    paddingLeftValue = 333;
    paddingLeftTotal = 503;

    pdf
      .moveTo(x, y).lineTo(accrualWidth, y).stroke()
      .font(headingFont)
      .text('I. DEVENGOS' , accrualX , accrualY = accrualY + paddingTopTL)
      .text('TOTALES', accrualX + paddingLeftTotal, accrualY)

      .font(textFont)
      .text('1. Percepciones salariales' , accrualX + paddingLeft1 , accrualY = accrualY + paddingTopTL);

    for(var i = 0; i < payroll.accruals.length; i++){
      for(var j = 0; j < payroll.accruals[i].types.length; j++){
        if(salary_perceptions_codes.includes(payroll.accruals[i].types[j].code)){
          type = payroll.accruals[i].types[j];
          pdf
            .text(type.type_expression , accrualX + paddingLeft2,  accrualY = accrualY + paddingTopTT)
            .text(formatAmount(type.type_value) , accrualX + paddingLeftValue + numberOffset(type.type_value) , accrualY)
            .lineWidth(0.1)
            .moveTo(accrualX + paddingLeft2, accrualY + paddingTopLine).lineTo(accrualX + 376, accrualY + paddingTopLine).stroke();
        }
      }
    }

    pdf
      .text('Complementos salariales' , accrualX + paddingLeft2 + 5, accrualY = accrualY + paddingTopTL);

    complements_count = 0;
    for(var i = 0; i < payroll.accruals.length; i++){
      for(var j = 0; j < payroll.accruals[i].types.length; j++){
        if(!salary_perceptions_codes.includes(payroll.accruals[i].types[j].code) &&
           !extra_hours_codes.includes(payroll.accruals[i].types[j].code) &&
           !extra_perks_codes.includes(payroll.accruals[i].types[j].code) &&
           !spices_salary_codes.includes(payroll.accruals[i].types[j].code)){
          type = payroll.accruals[i].types[j];
          complements_count++;
          pdf
            .text(type.type_expression ,accrualX + paddingLeft3, accrualY = accrualY + paddingTopTT)
            .text(formatAmount(type.type_value) , accrualX + paddingLeftValue + numberOffset(type.type_value), accrualY)
            .moveTo(x + left + 20, accrualY + paddingTopLine).lineTo(accrualX + 376, accrualY + paddingTopLine).stroke();
        }
      }
    }

    if(complements_count == 0){
      pdf
        .moveTo(x + left + 20, accrualY + paddingTopLine).lineTo(accrualX + 376, accrualY + paddingTopLine).stroke();
    }

    pdf
      .text('Horas extraordinarias' , accrualX + paddingLeft2, accrualY = accrualY + paddingTopTL);

    extra_hours_total = 0;
    for(var i = 0; i < payroll.accruals.length; i++){
      for(var j = 0; j < payroll.accruals[i].types.length; j++){
        if(extra_hours_codes.includes(payroll.accruals[i].types[j].code)){
          type = payroll.accruals[i].types[j];
          extra_hours_total += type.type_value;
        }
      }
    }

    if(extra_hours_total == 0){
      pdf
        .moveTo(accrualX + paddingLeft2, accrualY + paddingTopLine).lineTo(accrualX + 376, accrualY + paddingTopLine).stroke();
    }else{
      pdf
        .text(formatAmount(extra_hours_total) , accrualX + paddingLeftValue + numberOffset(extra_hours_total), accrualY)
        .moveTo(accrualX + paddingLeft2, accrualY + paddingTopLine).lineTo(accrualX + 376, accrualY + paddingTopLine).stroke();
    }

    pdf
      .text('Gratificaciones extraordinarias' , accrualX + paddingLeft2, accrualY = accrualY + paddingTopTL);

    extra_perks_total = 0;
    for(var i = 0; i < payroll.accruals.length; i++){
      for(var j = 0; j < payroll.accruals[i].types.length; j++){
        if(extra_perks_codes.includes(payroll.accruals[i].types[j].code)){
          type = payroll.accruals[i].types[j];
          extra_perks_total += type.type_value;
        }
      }
    }

    if(extra_perks_total == 0){
      pdf
        .moveTo(accrualX + paddingLeft2, accrualY + paddingTopLine).lineTo(accrualX + 376, accrualY + paddingTopLine).stroke();
    }else{
      pdf
        .text(formatAmount(extra_perks_total) , accrualX + paddingLeftValue + numberOffset(extra_perks_total), accrualY)
        .moveTo(accrualX + paddingLeft2, accrualY + paddingTopLine).lineTo(accrualX + 376, accrualY + paddingTopLine).stroke();
    }

    pdf
      .text('Salario en especie' , accrualX + paddingLeft2, accrualY = accrualY + paddingTopTL);

    spices_salary_total = 0;
    for(var i = 0; i < payroll.accruals.length; i++){
      for(var j = 0; j < payroll.accruals[i].types.length; j++){
        if(spices_salary_codes.includes(payroll.accruals[i].types[j].code)){
          type = payroll.accruals[i].types[j];
          spices_salary_total += type.type_value;
        }
      }
    }

    if(spices_salary_total == 0){
      pdf
        .moveTo(accrualX + paddingLeft2, accrualY + paddingTopLine).lineTo(accrualX + 376, accrualY + paddingTopLine).stroke();
    }else{
      pdf
        .text(formatAmount(spices_salary_total) , accrualX + paddingLeftValue + numberOffset(spices_salary_total), accrualY)
        .moveTo(accrualX + paddingLeft2, accrualY + paddingTopLine).lineTo(accrualX + 376, accrualY + paddingTopLine).stroke();
    }

    suplies = getAccrual("Indemnizaciones o suplidos");

    pdf
      .text('2. Percepciones no salariales' , accrualX + paddingLeft1 , accrualY = accrualY + paddingTopTL)
      .text('Indemnizaciones o suplidos' , accrualX + paddingLeft2, accrualY = accrualY + paddingTopTT);

    if(suplies == undefined){
      pdf
        .moveTo(accrualX + paddingLeft2, accrualY + paddingTopLine).lineTo(accrualX + 376, accrualY + paddingTopLine).stroke();
    }else{
      for(i = 0; i < suplies.types.length; i++){
        suply = suplies.types[i];
        pdf
          .text(suply.type_expression , accrualX + paddingLeft3, accrualY = accrualY + paddingTopTT)
          .text(formatAmount(suply.type_value) , accrualX + paddingLeftValue + numberOffset(suply.type_value), accrualY)
          .moveTo(accrualX + paddingLeft3, accrualY + paddingTopLine).lineTo(accrualX + 376, accrualY + paddingTopLine).stroke();
      }
    }

    compensations_SS = getAccrual('Prestaciones e indemnizaciones a la Seguridad Social');
    pdf
      .text('Prestaciones e indemnizaciones a la Seguridad Social' , accrualX + paddingLeft2, accrualY = accrualY + paddingTopTL);

    if(compensations_SS == undefined){
      pdf
        .moveTo(accrualX + paddingLeft2, accrualY + paddingTopLine).lineTo(accrualX + 376, accrualY + paddingTopLine).stroke();
    }else{
      for(i = 0; i < compensations_SS.types.length; i++){
        compensation = compensations_SS.types[i];
        pdf
          .text(compensation.type_expression , accrualX + paddingLeft3, accrualY = accrualY + paddingTopTT)
          .text(formatAmount(compensation.type_value) , accrualX + paddingLeftValue + numberOffset(compensation.type_value), accrualY)
          .moveTo(accrualX + paddingLeft3, accrualY + paddingTopLine).lineTo(accrualX + 376, accrualY + paddingTopLine).stroke();
      }
    }

    other_perceptions = getAccrual('Otras percepciones no salariales');
    pdf
      .text('Otras percepciones no salariales' , accrualX + paddingLeft2, accrualY = accrualY + paddingTopTL);

    if(other_perceptions == undefined){
      pdf
        .moveTo(accrualX + paddingLeft2, accrualY + paddingTopLine).lineTo(accrualX + 376, accrualY + paddingTopLine).stroke();
    }else{
      for(i = 0; i < other_perceptions.types.length; i++){
        perception = other_perceptions.types[i];
        pdf
          .text(perception.type_expression , accrualX + paddingLeft3, accrualY = accrualY + paddingTopTT)
          .text(formatAmount(perception.type_value) , accrualX + paddingLeftValue + numberOffset(perception.type_value), accrualY)
          .moveTo(accrualX + paddingLeft3, accrualY + paddingTopLine).lineTo(accrualX + 376, accrualY + paddingTopLine).stroke();
      }
    }

    pdf
      .fontSize(titleSize)
      .font(titleFont)
      .text('A. TOTAL DEVENGADO' , accrualX + 150 , accrualY = accrualY + paddingTopTL)
      .text(formatAmount(payroll.total_accrual) , accrualX + paddingLeftTotal + numberOffset(payroll.total_accrual), accrualY)
      .moveTo(accrualX + 150 , accrualY + paddingTopLine).lineTo(accrualX + 545 , accrualY + paddingTopLine).stroke();

    y = accrualY - 5;
  }

  var getDeduction = function(nameDeduction){
    for(i = 0; i < payroll.deductions.length; i++){
      deduction = payroll.deductions[i];
      if(deduction.name == nameDeduction){
        return deduction;
      }
    }
    return {
    	types : []
    };
    //return undefined;
  }

  var newDeductionFirstPage = function(){
    // -----------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- DEDUCTION ---------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    y = y + 15;
    deductionY = y + 15;
    deductionOY = deductionY;
    deductionX = x + left;
    paddingLeftPercent = 250;

    y = y + 30;
    tittle = 50;
    firstColumn = 50;
    secondColumn = 100;
    thirdColumn = 300;
    quarterColumn = 440;

    common_contingency = getDeduction("Contingencias Comunes");
    unemployment = getDeduction("Desempleo");
    professional_formation = getDeduction("Formación Profesional");
    extra_hours_e = getDeduction("Horas Extraordinarias Fuerza Mayor");
    extra_hours_ne = getDeduction("Resto Horas Extraordinarias");
    taxesD = getDeduction("Dinerario");
    taxesS = getDeduction("Especie");
    advances = getDeduction("Anticipos");
    spices = getDeduction("Valor de productos en especie");
    other_deductions = getDeduction("Otras deducciones");

    if(deductionOY <= 307){
      // ----------------------------------------- APORTACIONES ----------------------------------------------------
      pdf
        .font(headingFont)
        .text('II. DEDUCCIONES', deductionX, deductionY)
        .text('1. Aportaciones del trabajador a las cotizaciones de la Seguridad Social y conceptos de recaudación' , 50, deductionY = deductionY + paddingTopTL)
        .font(textFont)
        .text(formatAmount(payroll.total_contributions), quarterColumn + numberOffset(payroll.total_contributions), deductionY)
        .moveTo(50, deductionY + 10).lineTo(quarterColumn + 45,  deductionY + 10).stroke();

      pdf
        .font(textFont)
        .text(formatMoney(common_contingency.value), firstColumn + numberOffset(common_contingency.value), y = y + 15)
        .text("  por Contigencias comunes", secondColumn, y)
        .text(formatPercent(common_contingency.percent), thirdColumn + numberOffset(common_contingency.percent), y)
        .text(formatMoney(unemployment.value), firstColumn + numberOffset(unemployment.value), y = y + 10)
        .text("  por Desempleo", secondColumn, y)
        .text(formatPercent(unemployment.percent), thirdColumn + numberOffset(unemployment.percent), y)
        .text(formatMoney(professional_formation.value), firstColumn + numberOffset(professional_formation.value), y = y + 10)
        .text("  por Fromación profesional", secondColumn, y)
        .text(formatPercent(professional_formation.percent), thirdColumn + numberOffset(professional_formation.percent), y)
        .text("  por Horas extraordinarias (Estruc.)", secondColumn, y = y + 10)
        .text(formatMoney(extra_hours_e.value), firstColumn + numberOffset(extra_hours_e.value), y)
        .text("  por Horas extraordinarias (No Estruc.)", secondColumn, y = y + 10)
        .text(formatMoney(extra_hours_ne.value), firstColumn + numberOffset(extra_hours_ne.value), y);

      // ----------------------------------------- IMPUESTOS ----------------------------------------------------
      taxes = taxesD.value + taxesS.value;
      pdf
        .font(headingFont)
        .text("2. Impuestos sobre la renta de personas físicas (I.R.P.F.)", tittle, y = y + 12)
        .font(textFont)
        .text(formatAmount(taxes), quarterColumn + numberOffset(taxes), y)
        .moveTo(tittle, y = y + 10).lineTo(quarterColumn + 45,  y).stroke()
        .text("  por Retribuciones dinerarias", secondColumn, y = y + 5)
        .text(formatMoney(taxesD.value), firstColumn + numberOffset(taxesD.value), y)
        .text(formatPercent(taxesD.percent), thirdColumn + numberOffset(professional_formation.percent), y)
        .text("  por Retribuciones en especie", secondColumn, y = y + 10)
        .text(formatMoney(taxesS.value), firstColumn + numberOffset(taxesS.value), y)
        .text(formatPercent(taxesD.percent), thirdColumn + numberOffset(professional_formation.percent), y);

      // ----------------------------------------- ANTICIPOS ----------------------------------------------------
      advancesTOTAL = 0;
      for(var i = 0; i < advances.types.length; i++){
        type = advances.types[i];
        advancesTOTAL += type.value;
      }

      pdf
        .font(headingFont)
        .text("3. Anticipos", tittle, y = y + 12)
        .font(textFont)
        .text(formatAmount(advancesTOTAL), quarterColumn + numberOffset(advancesTOTAL), y)
        .moveTo(tittle, y + 10).lineTo(quarterColumn + 45,  y + 10).stroke();

      // ----------------------------------------- ESPECIES ----------------------------------------------------
      spicesTOTAL = 0;
      for(var i = 0; i < spices.types.length; i++){
        type = spices.types[i];
        spicesTOTAL += type.value;
      }

      pdf
        .font(headingFont)
        .text("4. Valor de los productos recibidos en especie", tittle, y = y + 12)
        .font(textFont)
        .text(formatAmount(spicesTOTAL), quarterColumn + numberOffset(spicesTOTAL), y)
        .moveTo(tittle, y + 10).lineTo(quarterColumn + 45,  y + 10).stroke();

      // ------------------------------------- OTRAS DEDUCCIONES -----------------------------------------------
      pdf
        .font(headingFont)
        .text("5. Otras deducciones", tittle, y = y + 12)
        .font(textFont);

      other_deductionsY = y;
      other_deductionsTOTAL = 0;
      y = y + 5;

      for(var i = 0; i < other_deductions.types.length; i++){
        type = other_deductions.types[i];
        other_deductionsTOTAL += type.value;

        pdf
          .text("  por " + type.name, secondColumn, y = y + 10)
          .text(formatMoney(type.value), firstColumn + numberOffset(type.value), y);
      }

      pdf
        .font(textFont)
        .text(formatAmount(other_deductionsTOTAL), quarterColumn + numberOffset(other_deductionsTOTAL), other_deductionsY)
        .moveTo(tittle, other_deductionsY + 10).lineTo(quarterColumn + 45,  other_deductionsY + 10).stroke();


      // ----------------------------------------- TOTAL A DEDUCIR ------------------------------------------------
      pdf
        .fontSize(titleSize)
        .font(titleFont)
        .text('B. TOTAL DEDUCIR' ,  deductionX + 150, y = y + 15)
        .text(formatAmount(payroll.total_deductions) , deductionX + paddingLeftTotal + numberOffset(payroll.total_deductions), y)
        .moveTo(deductionX + 150 , y = y + 10).lineTo(deductionX + 545 , y).stroke();

      pdf
        .text('LÍQUIDO TOTAL A PERCIBIR (A-B)' , deductionX + 160, y = y + 10)
        .text(formatAmount(payroll.liquid_perceive) , deductionX + paddingLeftTotal + numberOffset(payroll.liquid_perceive), y)
        .moveTo(deductionX + 160 , y = y + 10).lineTo(deductionX + 545 , y).stroke();

        signature = 455;
    }else{
      // ----------------------------------------- TOTAL A DEDUCIR ------------------------------------------------
      pdf
        .fontSize(titleSize)
        .font(titleFont)
        .text('B. TOTAL DEDUCIR (página siguiente)' ,  deductionX + 150, y = y - 20)
        .text(formatAmount(payroll.total_deductions) , deductionX + paddingLeftTotal + numberOffset(payroll.total_deductions), y)
        .moveTo(deductionX + 150 , y = y + 10).lineTo(deductionX + 545 , y).stroke();

      pdf
        .text('LÍQUIDO TOTAL A PERCIBIR (A-B)' , deductionX + 160, y = y + 10)
        .text(formatAmount(payroll.liquid_perceive) , deductionX + paddingLeftTotal + numberOffset(payroll.liquid_perceive), y)
        .moveTo(deductionX + 160 , y = y + 10).lineTo(deductionX + 545 , y).stroke();
    }
  }

  var newDeductionSecondPage = function(){
    if(deductionOY > 307){
      //NEW PAGE
      pdf.addPage();
      //PDF Styles
      var pdfWidth = pdf.page.width
         - pdf.page.margins.left
         - pdf.page.margins.right
      ;

      pdf.page.width = pdf.page.width + 60; //Ancho para que no haya salto de linea
      pdf.page.margins = {t0p: 50, bottom: 0, left: 72, right: 72}; //Margenes del documento

      newHearderTittle();
      newEnterpriseBox();
      newEmployeeBox();
      newSettlementBox();

      y = accrualY + 15;
      deductionY = originalYCenterTable + 5;
      deductionX = x + left;
      paddingLeftPercent = 250;

      pdf
        .moveTo(x, deductionY - 5).lineTo(accrualWidth, deductionY - 5).stroke();

      y = deductionY + 15;
      tittle = 50;
      firstColumn = 50;
      secondColumn = 100;
      thirdColumn = 300;
      quarterColumn = 440;

      common_contingency = getDeduction("Contingencias Comunes");
      unemployment = getDeduction("Desempleo");
      professional_formation = getDeduction("Formación Profesional");
      extra_hours_e = getDeduction("Horas Extraordinarias Fuerza Mayor");
      extra_hours_ne = getDeduction("Resto Horas Extraordinarias");
      taxesD = getDeduction("Dinerario");
      taxesS = getDeduction("Especie");
      advances = getDeduction("Anticipos");
      spices = getDeduction("Valor de productos en especie");
      other_deductions = getDeduction("Otras deducciones");

      // ----------------------------------------- APORTACIONES ----------------------------------------------------
      pdf
        .font(headingFont)
        .text('II. DEDUCCIONES', deductionX, deductionY)
        .text('TOTALES', deductionX + paddingLeftTotal, deductionY)
        .text('1. Aportaciones del trabajador a las cotizaciones de la Seguridad Social y conceptos de recaudación' , 50, deductionY = deductionY + paddingTopTL)
        .font(textFont)
        .text(formatAmount(payroll.total_contributions), quarterColumn + numberOffset(payroll.total_contributions), deductionY)
        .moveTo(50, deductionY + 10).lineTo(quarterColumn + 45,  deductionY + 10).stroke();

      pdf
        .font(textFont)
        .text(formatMoney(common_contingency.value), firstColumn + numberOffset(common_contingency.value), y = y + 15)
        .text("  por Contigencias comunes", secondColumn, y)
        .text(formatPercent(common_contingency.percent), thirdColumn + numberOffset(common_contingency.percent), y)
        .text(formatMoney(unemployment.value), firstColumn + numberOffset(unemployment.value), y = y + 10)
        .text("  por Desempleo", secondColumn, y)
        .text(formatPercent(unemployment.percent), thirdColumn + numberOffset(unemployment.percent), y)
        .text(formatMoney(professional_formation.value), firstColumn + numberOffset(professional_formation.value), y = y + 10)
        .text("  por Fromación profesional", secondColumn, y)
        .text(formatPercent(professional_formation.percent), thirdColumn + numberOffset(professional_formation.percent), y)
        .text("  por Horas extraordinarias (Estruc.)", secondColumn, y = y + 10)
        .text(formatMoney(extra_hours_e.value), firstColumn + numberOffset(extra_hours_e.value), y)
        .text("  por Horas extraordinarias (No Estruc.)", secondColumn, y = y + 10)
        .text(formatMoney(extra_hours_ne.value), firstColumn + numberOffset(extra_hours_ne.value), y);

      // ----------------------------------------- IMPUESTOS ----------------------------------------------------
      taxes = taxesD.value + taxesS.value;
      pdf
        .font(headingFont)
        .text("2. Impuestos sobre la renta de personas físicas (I.R.P.F.)", tittle, y = y + 12)
        .font(textFont)
        .text(formatAmount(taxes), quarterColumn + numberOffset(taxes), y)
        .moveTo(tittle, y = y + 10).lineTo(quarterColumn + 45,  y).stroke()
        .text("  por Retribuciones dinerarias", secondColumn, y = y + 5)
        .text(formatMoney(taxesD.value), firstColumn + numberOffset(taxesD.value), y)
        .text(formatPercent(taxesD.percent), thirdColumn + numberOffset(professional_formation.percent), y)
        .text("  por Retribuciones en especie", secondColumn, y = y + 10)
        .text(formatMoney(taxesS.value), firstColumn + numberOffset(taxesS.value), y)
        .text(formatPercent(taxesD.percent), thirdColumn + numberOffset(professional_formation.percent), y);

      // ----------------------------------------- ANTICIPOS ----------------------------------------------------
      advancesTOTAL = 0;
      for(var i = 0; i < advances.types.length; i++){
        type = advances.types[i];
        advancesTOTAL += type.value;
      }

      pdf
        .font(headingFont)
        .text("3. Anticipos", tittle, y = y + 12)
        .font(textFont)
        .text(formatAmount(advancesTOTAL), quarterColumn + numberOffset(advancesTOTAL), y)
        .moveTo(tittle, y + 10).lineTo(quarterColumn + 45,  y + 10).stroke();

      // ----------------------------------------- ESPECIES ----------------------------------------------------
      spicesTOTAL = 0;
      for(var i = 0; i < spices.types.length; i++){
        type = spices.types[i];
        spicesTOTAL += type.value;
      }

      pdf
        .font(headingFont)
        .text("4. Valor de los productos recibidos en especie", tittle, y = y + 12)
        .font(textFont)
        .text(formatAmount(spicesTOTAL), quarterColumn + numberOffset(spicesTOTAL), y)
        .moveTo(tittle, y + 10).lineTo(quarterColumn + 45,  y + 10).stroke();

      // ------------------------------------- OTRAS DEDUCCIONES -----------------------------------------------
      pdf
        .font(headingFont)
        .text("5. Otras deducciones", tittle, y = y + 12)
        .font(textFont);

      other_deductionsY = y;
      other_deductionsTOTAL = 0;
      y = y + 5;

      for(var i = 0; i < other_deductions.types.length; i++){
        type = other_deductions.types[i];
        other_deductionsTOTAL += type.value;

        pdf
          .text("  por " + type.name, secondColumn, y = y + 10)
          .text(formatMoney(type.value), firstColumn + numberOffset(type.value), y);
      }

      pdf
        .font(textFont)
        .text(formatAmount(other_deductionsTOTAL), quarterColumn + numberOffset(other_deductionsTOTAL), other_deductionsY)
        .moveTo(tittle, other_deductionsY + 10).lineTo(quarterColumn + 45,  other_deductionsY + 10).stroke();


      // ----------------------------------------- TOTAL A DEDUCIR ------------------------------------------------
      pdf
        .fontSize(titleSize)
        .font(titleFont)
        .text('A. TOTAL DEVENGADO (página anterior)' , deductionX + 150, y = y + 15)
        .text(formatAmount(payroll.total_accrual) , deductionX + paddingLeftTotal + numberOffset(payroll.total_accrual), y)
        .moveTo(accrualX + 150 , y = y + 10).lineTo(accrualX + 545 , y).stroke();

      pdf
        .fontSize(titleSize)
        .font(titleFont)
        .text('B. TOTAL DEDUCIR' ,  deductionX + 150, y = y + 10)
        .text(formatAmount(payroll.total_deductions) , deductionX + paddingLeftTotal + numberOffset(payroll.total_deductions), y)
        .moveTo(deductionX + 150 , y = y + 10).lineTo(deductionX + 545 , y).stroke();

      pdf
        .text('LÍQUIDO TOTAL A PERCIBIR (A-B)' , deductionX + 160, y = y + 10)
        .text(formatAmount(payroll.liquid_perceive) , deductionX + paddingLeftTotal + numberOffset(payroll.liquid_perceive), y)
        .moveTo(deductionX + 160 , y = y + 10).lineTo(deductionX + 545 , y).stroke();

      signature = 455;

      newSignatureDate();
      newFooterBox();
    }
  }

  //Main
  newHearderTittle();
  newEnterpriseBox();
  newEmployeeBox();
  newSettlementBox();
  newAccrual();
  newDeductionFirstPage();
  newSignatureDate();
  newFooterBox();
  newDeductionSecondPage();

  //End PDF
  pdf.end();

}

module.exports.standardTwoColumnsPayroll = function(payroll, stream){
  //Initialize pdf object
  var pdf = new(PDF);
  pdf.pipe(stream);

  //PDF Styles
  var pdfWidth = pdf.page.width
		 - pdf.page.margins.left
     - pdf.page.margins.right
	;

  pdf.page.width = pdf.page.width + 60; //Ancho para que no haya salto de linea
  pdf.page.margins = {t0p: 50, bottom: 0, left: 72, right: 72}; //Margenes del documento

  //Variables y constants
	textSize = 6;
	textFont = 'Helvetica';

	titleSize = 8;
	titleFont = 'Helvetica-Bold';

	headingSize = 4;
	headingFont = 'Helvetica-Bold';

  signature = 450;

  //Aux Methods
	var formatText = function( text ) {
		return text ? text.toUpperCase() : ' ';
	}

	var formatAmount = function( amount ) {
		return amount ? amount.toFixed(2) : '0.00';
	}

  var formatPercent = function( number ) {
    return number ? number.toFixed(2) : '0.00';
	}

  var formatDate = function( date ) {
		var split = date.split('/');
    return split[0] + ' de ' + getStrMonth(split[1]) + ' de ' + split[2];
	}

  var getStrMonth = function ( month ) {
    switch (month) {
      case '1':
          return 'enero';
      case '2':
          return 'febrero';
      case '3':
          return 'marzo';
      case '4':
          return 'abril';
      case '5':
          return 'mayo';
      case '6':
          return 'junio';
      case '7':
          return 'julio';
      case '8':
          return 'agosto';
      case '9':
          return 'septiembre';
      case '10':
          return 'octubre';
      case '11':
          return 'noviembre';
      case '12':
          return 'diciembre';
      default:
          return 'error';
    }
  }

  var numberOffset = function( number ) {
    var split = number.split('.');
    switch (split[0].length) {
      case 1:
          return 27;
      case 2:
          return 22;
      case 3:
          return 18;
      case 4:
          return 13;
      case 5:
          return 9;
      case 6:
          return 5;
      default:
          return 0;
    }
  }

  var newHearderTittle = function(){
    //PDF drawing position using for lines, starting on the t0p of the page
    y = pdf.y;
  	x = pdf.x-50;
    originalX = pdf.x-50;
    originalY = pdf.y;
    ox = pdf.x
  	t0p = 2;
  	left = 8;
  	width = (pdfWidth-30) * 1/2;
    pdf.lineWidth(1);

    paddingTopTL = 14;  //Padding-t0p text / line
    paddingTop = 4;     //Padding-t0p line / text, text / text
    paddingLeft1 = 8;   //Padding-left first text
    paddingLeft2 = 16;  //Padding-left second text
    paddingLeft3 = 20;  //Padding-left third text
    marginTL = 2;       //Margin bettwen text / line, line / text, line / line

    //Start drawing PDF
    pdf
  	  .font(titleFont)
  		.fontSize(titleSize)
  		.text('RECIBO INDIVIDUAL JUSTIFICATIVO DEL PAGO DE SALARIO', x+155, 0)
  		.moveDown(1);
  }

  var newEnterpriseBox = function(){
    y = pdf.y-8;
    oy = y;

    rightEnterpriseX = ox + width;      //Position X for right line enterprise box
    maxRightWidth = x + pdfWidth + 100; //Position X for right line employee, salary, footer box
    sizVertivalLineEE = 10;             //Size vertical line for enterprise, employee box

    // -----------------------------------------------------------------------------------------------------------------
    // -------------------------------------------- ENTERPRISE ---------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    pdf
      .moveTo(x, y).lineTo(rightEnterpriseX, y).stroke()      //Horizontal t0p line

      .font(headingFont)
      .text('Empresa: ', x + paddingLeft1, y + paddingTop , {continued: true})
      .text(payroll.enterprise.name)

      .text('Domicilio: ', x + paddingLeft1, y = pdf.y + paddingTop, {continued: true})
      .font(textFont)
      .text(payroll.enterprise.address)
      .text(payroll.enterprise.locality , x + paddingLeft1 + 41)

      .font(headingFont)
      .text('CIF: ' , x + paddingLeft1 , y = pdf.y + paddingTop , {continued: true})
      .font(textFont)
      .text(payroll.enterprise.cif)

      .font(headingFont)
      .text('CCC: ' , x + paddingLeft1, y = pdf.y + paddingTop, {continued: true})
      .font(textFont)
      .text(payroll.enterprise.ccc)

      .moveTo(x, oy).lineTo(x, y + sizVertivalLineEE).stroke()                                     //Vertical left line
  		.moveTo(rightEnterpriseX, oy).lineTo(rightEnterpriseX, y + sizVertivalLineEE).stroke()       //Vertical right line
      .moveTo(x, y + sizVertivalLineEE).lineTo(rightEnterpriseX, y + sizVertivalLineEE).stroke();  //Horizontal bottom line

  }

  var newEmployeeBox = function(){
    // -----------------------------------------------------------------------------------------------------------------
    // -------------------------------------------- EMPLOYEE -----------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    leftEmployeeX = ox + width + left + 5 ;   //Position X for left line employee box
    employeeY = oy + t0p + 2;                 //Start y for employee box
    employeeTop = 12;                         //Padding-t0p bettwen text

    pdf
      .moveTo(leftEmployeeX - left, oy).lineTo(maxRightWidth, oy).stroke()   //Horizontal t0p line

      .font(headingFont)
      .text('Trabajador: ' , leftEmployeeX, employeeY, {continued: true})
      .text(payroll.employee.fullname)

      .text('NIF: ', leftEmployeeX, employeeY = employeeY + employeeTop, {continued: true})
      .font(textFont)
      .text(payroll.employee.nif)

      .font(headingFont)
      .text('Nº S.S.: ', leftEmployeeX, employeeY = employeeY + employeeTop, {continued: true})
      .font(textFont)
      .text(payroll.employee.ss)

      .font(headingFont)
      .text('Grupo profesional: ', leftEmployeeX, employeeY = employeeY + employeeTop, {continued: true})
      .font(textFont)
      .text(payroll.employee.professional_group)

      .font(headingFont)
      .text('Grupo cotización: ', leftEmployeeX, employeeY = employeeY + employeeTop, {continued: true})
      .font(textFont)
      .text(payroll.employee.quote_group)

      .font(headingFont)
      .text('Fecha antigüedad: ', leftEmployeeX + 152, employeeY , {continued: true})
      .font(textFont)
      .text(payroll.employee.seniority_date)

      .moveTo(leftEmployeeX - left, oy).lineTo(leftEmployeeX - left, y + sizVertivalLineEE).stroke()              //Vertical left line
      .moveTo(maxRightWidth, oy).lineTo(maxRightWidth, y + sizVertivalLineEE).stroke()                            //Vertical right line
      .moveTo(leftEmployeeX - left, y + sizVertivalLineEE).lineTo(maxRightWidth, y + sizVertivalLineEE).stroke()  //Horizontal bottom line
      .moveDown(1);
  }

  var newSettlementBox = function(){
    // -----------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- SETTLEMENT --------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    start_date = formatDate(payroll.settlement.start_date);
    end_date = formatDate(payroll.settlement.end_date);
    settlementWidth = x + pdfWidth + 100;
    settlementTop = 4;
    settlementX = ox - 50 + left;
    settlementY = y + t0p + 12;
    settlementYHeight = settlementY + 15;

    pdf
      .moveTo(x, settlementY).lineTo(settlementWidth, settlementY).stroke()   //Horizontal t0p line

      .font(headingFont)
      .text('Periodo de liquidación: ' , settlementX, settlementY + settlementTop, {continued: true})
      .font(textFont)
      .text('del '+ start_date +' al '+ end_date, {continued: true})

      .font(headingFont)
      .text('Total días: ', settlementX + 238 , settlementY + settlementTop, {continued: true})
      .font(textFont)
      .text(payroll.settlement.total_days)

      .moveTo(x, settlementY).lineTo(x, settlementYHeight).stroke()                               //Vertical left line
      .moveTo(settlementWidth, settlementY).lineTo(settlementWidth, settlementYHeight).stroke()   //Vertical right line
      .moveTo(x, settlementYHeight).lineTo(settlementWidth, settlementYHeight).stroke();          //Horizontal bottom line
  }

  var newFooterBox = function(){
    // -----------------------------------------------------------------------------------------------------------------
    // -------------------------------------------- FOOTER -------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    footer = 538;
    pdf
      .moveTo(originalX, originalY+footer).lineTo(originalX+pdfWidth+100, originalY+footer).undash().stroke()     //Horizontal t0p line
      .font(textFont)
      .text('DETERMINACIÓN DE LAS BASES DE COTIZACIÓN A LA SEGURIDAD SOCIAL Y CONCEPTOS DE RECAUDACIÓN CONJUNTAS Y DE ' +
          'LA BASE' , originalX + left , originalY + footer + t0p + 2)
      .font(textFont)
      .text('SUJETA A RETENCIÓN DEL IRPF Y APORTACIÓN DE LA EMPRESA' , originalX + left , originalY + footer + t0p + 12)
      .font(textFont)
      .text('1. Contingencias comunes' , originalX + left + 5 , originalY + footer + t0p + 25, {continued: true})
      .font(textFont)
      .text('BASE', originalX + left + 280 , originalY + footer + t0p + 25, {continued: true})
      .font(textFont)
      .text('TIPO', originalX + left + 325 , originalY + footer + t0p + 25, {continued: true})
      .font(textFont)
      .text('AP. EMPRESA', originalX + left + 355 , originalY + footer + t0p + 25)

      .font(textFont)
      .text('Importe remuneración mensual' , originalX + left + 10 , originalY + footer + t0p + 39)
      .font(textFont)
      .text(formatAmount(payroll.footer_ss_quotation.common_contingency.monthly_remuneration), originalX + left + 250 + numberOffset(formatAmount(payroll.footer_ss_quotation.common_contingency.monthly_remuneration)) , originalY + footer + t0p + 39)
      .lineWidth(0.1)
      .moveTo(originalX + left + 10 , originalY + footer + t0p + 49).lineTo(originalX + left + 300 , originalY + footer + t0p + 49).stroke()

      .font(textFont)
      .text('Importe prorrata de pagas extraordinarias' , originalX + left + 10 , originalY + footer + t0p + 53)
      .font(textFont)
      .text(formatAmount(payroll.footer_ss_quotation.common_contingency.extraordinary_pay_packet), originalX + left + 250 + numberOffset(formatAmount(payroll.footer_ss_quotation.common_contingency.extraordinary_pay_packet)), originalY + footer + t0p + 53)
      .moveTo(originalX + left + 10 , originalY + footer + t0p + 63).lineTo(originalX + left + 300 , originalY + footer + t0p + 63).stroke()

      .font(textFont)
      .text(formatAmount(payroll.footer_ss_quotation.common_contingency.base), originalX + left + 353 + numberOffset(formatAmount(payroll.footer_ss_quotation.common_contingency.base)) , originalY + footer + t0p + 46)
      .font(textFont)
      .text(formatPercent(payroll.footer_ss_quotation.common_contingency.type_percent) + ' %' , originalX + left + 408 + numberOffset(formatPercent(payroll.footer_ss_quotation.common_contingency.type_percent)) , originalY + footer + t0p + 46)
      .font(textFont)
      .text(formatAmount(payroll.footer_ss_quotation.common_contingency.company_input) , originalX + left + 497 + numberOffset(formatAmount(payroll.footer_ss_quotation.common_contingency.company_input)), originalY + footer + t0p + 46)
      .moveTo(originalX + left + 310 , originalY + footer + t0p + 56).lineTo(originalX + left + 550 , originalY + footer + t0p + 56).stroke()

      .font(textFont)
      .text('2. Contingencias profesionales' , originalX + left + 5 , originalY + footer + t0p + 80)
      .font(textFont)
      .text('conceptos de recaudación' , originalX + left + 5 , originalY + footer + t0p + 90)
      .font(textFont)
      .text('conjunta' , originalX + left + 5 , originalY + footer + t0p + 100)

      .font(textFont)
      .text('AT Y EP' , originalX + left + 160 , originalY + footer + t0p + 67)
      .moveTo(originalX + left + 160 , originalY + footer + t0p + 77).lineTo(originalX + left + 301 , originalY + footer + t0p + 77).stroke()
      .font(textFont)
      .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_at) + ' %', originalX + left + 408 + numberOffset(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_at)), originalY + footer + t0p + 67)
      .font(textFont)
      .text(formatAmount(payroll.footer_ss_quotation.professional_contingency.company_input_at), originalX + left + 497 +  numberOffset(formatAmount(payroll.footer_ss_quotation.professional_contingency.company_input_at)), originalY + footer + t0p + 67)
      .moveTo(originalX + left + 410 , originalY + footer + t0p + 77).lineTo(originalX + left + 551 , originalY + footer + t0p + 77).stroke()

      .font(textFont)
      .text('Desempleo' , originalX + left + 160 , originalY + footer + t0p + 81)
      .moveTo(originalX + left + 160 , originalY + footer + t0p + 91).lineTo(originalX + left + 301 , originalY + footer + t0p + 91).stroke()
      .font(textFont)
      .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_unemployment) + ' %', originalX + left + 408 + numberOffset(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_unemployment)), originalY + footer + t0p + 81)
      .font(textFont)
      .text(formatAmount(payroll.footer_ss_quotation.professional_contingency.company_input_unemployment), originalX + left + 497 +  numberOffset(formatAmount(payroll.footer_ss_quotation.professional_contingency.company_input_unemployment)), originalY + footer + t0p + 81)
      .moveTo(originalX + left + 410 , originalY + footer + t0p + 91).lineTo(originalX + left + 551 , originalY + footer + t0p + 91).stroke()

      .font(textFont)
      .text(formatAmount(payroll.footer_ss_quotation.professional_contingency.base), originalX + left + 353 + numberOffset(formatAmount(payroll.footer_ss_quotation.professional_contingency.base)), originalY + footer + t0p + 88)
      .moveTo(originalX + left + 310 , originalY + footer + t0p + 98).lineTo(originalX + left + 400 , originalY + footer + t0p + 98).stroke()

      .font(textFont)
      .text('Fromación profesional' , originalX + left + 160 , originalY + footer + t0p + 95)
      .moveTo(originalX + left + 160 , originalY + footer + t0p + 105).lineTo(originalX + left + 301 , originalY + footer + t0p + 105).stroke()
      .font(textFont)
      .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_professional_formation) + ' %', originalX + left + 408 + numberOffset(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_professional_formation)), originalY + footer + t0p + 95)
      .font(textFont)
      .text(formatAmount(payroll.footer_ss_quotation.professional_contingency.company_input_professional_formation), originalX + left + 497 +  numberOffset(formatAmount(payroll.footer_ss_quotation.professional_contingency.company_input_professional_formation)), originalY + footer + t0p + 95)
      .moveTo(originalX + left + 410 , originalY + footer + t0p + 105).lineTo(originalX + left + 551 , originalY + footer + t0p + 105).stroke()

      .font(textFont)
      .text('Fondo de Garantía Salarial' , originalX + left + 160 , originalY + footer + t0p + 109)
      .moveTo(originalX + left + 160 , originalY + footer + t0p + 119).lineTo(originalX + left + 301 , originalY + footer + t0p + 119).stroke()
      .font(textFont)
      .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_salary_warranty) + ' %', originalX + left + 408 + numberOffset(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_salary_warranty)), originalY + footer + t0p + 109)
      .font(textFont)
      .text(formatAmount(payroll.footer_ss_quotation.professional_contingency.company_input_salary_warranty), originalX + left + 497 +  numberOffset(formatAmount(payroll.footer_ss_quotation.professional_contingency.company_input_salary_warranty)), originalY + footer + t0p + 109)
      .moveTo(originalX + left + 410 , originalY + footer + t0p + 119).lineTo(originalX + left + 551 , originalY + footer + t0p + 119).stroke()

      .font(textFont)
      .text('3. Cotización adicional por horas extraordinarias' , originalX + left + 5 , originalY + footer + t0p + 123)
      .font(textFont)
      .text('Fuerza mayor' , originalX + left + 160 , originalY + footer + t0p + 137)
      .text(formatAmount(payroll.footer_ss_quotation.aditional_quotation.base_overwhelming_force), originalX + left + 353 + numberOffset(formatAmount(payroll.footer_ss_quotation.aditional_quotation.base_overwhelming_force)), originalY + footer + t0p + 137)
      .text(formatAmount(payroll.footer_ss_quotation.aditional_quotation.company_input_overwhelming_force), originalX + left + 497 +  numberOffset(formatAmount(payroll.footer_ss_quotation.aditional_quotation.company_input_overwhelming_force)), originalY + footer + t0p + 137)
      .moveTo(originalX + left + 160 , originalY + footer + t0p + 147).lineTo(originalX + left + 400 , originalY + footer + t0p + 147).stroke()
      .moveTo(originalX + left + 410 , originalY + footer + t0p + 147).lineTo(originalX + left + 551 , originalY + footer + t0p + 147).stroke()

      .font(textFont)
      .text('No estructurales' , originalX + left + 160 , originalY + footer + t0p + 151)
      .text(formatAmount(payroll.footer_ss_quotation.aditional_quotation.base_non_structural), originalX + left + 353 + numberOffset(formatAmount(payroll.footer_ss_quotation.aditional_quotation.base_non_structural)), originalY + footer + t0p + 151)
      .text(formatAmount(payroll.footer_ss_quotation.aditional_quotation.company_input_non_structural), originalX + left + 497 +  numberOffset(formatAmount(payroll.footer_ss_quotation.aditional_quotation.company_input_non_structural)), originalY + footer + t0p + 151)
      .moveTo(originalX + left + 160 , originalY + footer + t0p + 161).lineTo(originalX + left + 400 , originalY + footer + t0p + 161).stroke()
      .moveTo(originalX + left + 410 , originalY + footer + t0p + 161).lineTo(originalX + left + 551 , originalY + footer + t0p + 161).stroke()

      .font(textFont)
      .text('4. Base sujeta a retención del IRPF' , originalX + left + 5 , originalY + footer + t0p + 165)
      .text(formatAmount(payroll.footer_ss_quotation.base_irpf), originalX + left + 353 + numberOffset(formatAmount(payroll.footer_ss_quotation.base_irpf)), originalY + footer + t0p + 165)
      .moveTo(originalX + left + 13 , originalY + footer + t0p + 175).lineTo(originalX + left + 400 , originalY + footer + t0p + 175).stroke()
      .lineWidth(1)
      .moveTo(originalX, originalY+footer).lineTo(originalX, originalY+footer+180).stroke()                //Vertical left line
      .moveTo(accrualWidth, originalY+footer).lineTo(accrualWidth, originalY+footer+180).stroke()          //Vertical right line
      .moveTo(originalX, originalY+footer+180).lineTo(accrualWidth, originalY+footer+180).stroke();        //Horizontal bottom line
  }

  var newSignatureDate = function(){
    // -----------------------------------------------------------------------------------------------------------------
    // ------------------------------------------ SIGNATURE / DATE -----------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    paddingSignature = 15;
    paddingDate = 10;
    centerTableHeight = 509;
    signature = 488;

    pdf
      .fontSize(titleSize)
      .text('Firma y sello de la empresa' , originalX + 160 , originalY + signature)
      .image(payroll.signature_logo, originalX + 160, originalY + signature + paddingSignature, {scale: 0.06})

      .text(end_date,  originalX + 310 , originalY + signature)
      .fontSize(titleSize)
      .text('RECIBÍ' , originalX + 310 , originalY + signature + paddingDate)

      .lineWidth(1)
      .moveTo(originalXCenterTable, originalYCenterTable).lineTo(originalXCenterTable, originalYCenterTable + centerTableHeight).stroke()                //Vertical left line
      .moveTo(accrualWidth, originalYCenterTable).lineTo(accrualWidth, originalYCenterTable + centerTableHeight).stroke()                                //Vertical right line
      .moveTo(originalXCenterTable, originalYCenterTable + centerTableHeight).lineTo(accrualWidth, originalYCenterTable + centerTableHeight).stroke();   //Horizontal bottom line
  }

  var getAccrual = function(accrualName){
    for(i = 0; i < payroll.accruals.length; i++){
      accrual = payroll.accruals[i];
      if(accrual.accrual_name == accrualName)
        return accrual;
    }
    return undefined;
  }

  var newAccrual = function(){
    // -----------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- ACCRUAL -----------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    y = settlementY+19;
    accrualY = settlementY+10;
    accrualX = x + left;
    accrualWidth = x + pdfWidth + 100;
    originalXCenterTable = x;
    originalYCenterTable = y;

    pdf
      .moveTo(x, y).lineTo(accrualWidth, y).stroke()
      .moveTo(accrualWidth/2, y).lineTo(accrualWidth/2, 545).stroke();

    firstPaddingX = x + 10;
    secondPaddingX = x + 20;
    thirdPaddingX = x + 220;
    y = y + 10;
    deductionY = y; //GUARDAMOS LA Y ORIGIANAL PARA EMPEZAR BIEN EN LA COLUMNA DE DEDUCCIONES

    pdf
      .font(headingFont)
      .text("I. DEVENGOS" , firstPaddingX , y);

    y = y + 15;

    for(i = 0; i < payroll.accruals.length; i++){
      accrual = payroll.accruals[i];
      pdf
        .font(titleFont)
        .text(accrual.accrual_name.toUpperCase() , secondPaddingX , y);
      y = y + 10;
      for(j = 0; j < accrual.types.length; j++){
        type = accrual.types[j];
        pdf
          .font(textFont)
          .text(type.type_expression , secondPaddingX + 8 , y)
          .text(formatAmount(type.type_value) , thirdPaddingX + numberOffset(formatAmount(type.type_value)) , y);
        y = y + 10;
      }
    }

    accrualTotalPadding = x + 90;
    y = y + 15;

    pdf
      .font(headingFont)
      .text("A. TOTAL DEVENGADO" , accrualTotalPadding , y)
      .text(formatAmount(payroll.total_accrual) , thirdPaddingX + numberOffset(formatAmount(payroll.total_accrual)) , y);;

  }

  var getDeduction = function(nameDeduction){
    for(i = 0; i < payroll.deductions.length; i++){
      deduction = payroll.deductions[i];
      if(deduction.name == nameDeduction){
        return deduction;
      }
    }
    return {
    	types : []
    };
    //return undefined;
  }

  var newDeduction = function(){
    // -----------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- DEDUCTION ---------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    y = deductionY;
    deductionPaddingX = x + 283;

    common_contingency = getDeduction("Contingencias comunes");
    unemployment = getDeduction("Desempleo");
    professional_formation = getDeduction("Formación profesional");
    extra_hours_e = getDeduction("Fuerza mayor o estructurales (HE)");
    extra_hours_ne = getDeduction("No estructurales (HE)");
    taxes = getDeduction("Impuesto sobre la renta de personas físicas");
    advances = getDeduction("Anticipos");
    spices = getDeduction("Valor de los productos recibidos en especie");
    other_deductions = getDeduction("Otras deducciones");

    pdf
      .text("II. DEDUCCIONES", deductionPaddingX, y);

    deductionPaddingX = deductionPaddingX + 10;

    pdf
      .text("1. Aportaciones del trabajador a las cotizaciones de la Seguridad Social", deductionPaddingX, y = y + 10)
      .text("y conceptos de recaudación conjunta", deductionPaddingX, y = y + 10);

    deductionFirstColumn = deductionPaddingX + 10;
    deductionSecondColumn = deductionFirstColumn + 60;
    deductionThirdColumn = deductionFirstColumn + 80;
    deductionFourColumn = deductionFirstColumn + 210;

    pdf
      .font(textFont)
      .text(formatAmount(common_contingency.percent) + " %", deductionFirstColumn + numberOffset(formatAmount(common_contingency.percent)), y = y + 15)
      .text("Contigencias comunes", deductionSecondColumn, y)
      .text(formatAmount(common_contingency.value), deductionFourColumn + numberOffset(formatAmount(common_contingency.value)), y)
      .text(formatAmount(unemployment.percent) + " %", deductionFirstColumn + numberOffset(formatAmount(unemployment.percent)), y = y + 10)
      .text("Desempleo", deductionSecondColumn, y)
      .text(formatAmount(unemployment.value), deductionFourColumn + numberOffset(formatAmount(unemployment.value)), y)
      .text(formatAmount(professional_formation.percent) + " %", deductionFirstColumn + numberOffset(formatAmount(professional_formation.percent)), y = y + 10)
      .text("Fromación profesional", deductionSecondColumn, y)
      .text(formatAmount(professional_formation.value), deductionFourColumn + numberOffset(formatAmount(professional_formation.value)), y);

    pdf
      .font(headingFont)
      .text("Horas extraordinarias", deductionFirstColumn + 10, y = y + 10)
      .font(textFont)
      .text("Fuerza mayor o estructurales", deductionSecondColumn, y = y + 10)
      .text(formatAmount(extra_hours_e.value), deductionFourColumn + numberOffset(formatAmount(extra_hours_e.value)), y)
      .text("No estructurales", deductionSecondColumn, y = y + 10)
      .text(formatAmount(extra_hours_ne.value), deductionFourColumn + numberOffset(formatAmount(extra_hours_ne.value)), y)
      .font(headingFont)
      .text("TOTAL APORTACIONES", deductionSecondColumn, y = y + 12)
      .font(textFont)
      .text(formatAmount(payroll.total_contributions), deductionFourColumn + numberOffset(formatAmount(payroll.total_contributions)), y);

    pdf
      .font(headingFont)
      .text("2. Impuestos sobre la renta de personas físicas (I.R.P.F.)", deductionPaddingX, y = y + 12)
      .font(textFont)
      .text(formatAmount(taxes.value), deductionFourColumn + numberOffset(formatAmount(taxes.value)), y);

    pdf
      .font(headingFont)
      .text("3. Anticipos", deductionPaddingX, y = y + 12)
      .font(textFont)
      .text(formatAmount(advances.value), deductionFourColumn + numberOffset(formatAmount(advances.value)), y);

    pdf
      .font(headingFont)
      .text("4. Valor de los productos recibidos en especie", deductionPaddingX, y = y + 12)
      .font(textFont)
      .text(formatAmount(spices.value), deductionFourColumn + numberOffset(formatAmount(spices.value)), y);

    pdf
      .font(headingFont)
      .text("5. Otras deducciones", deductionPaddingX, y = y + 12)
      .font(textFont)
      .text(formatAmount(other_deductions.value), deductionFourColumn + numberOffset(formatAmount(other_deductions.value)), y);

    pdf
      .font(headingFont)
      .text("B. TOTAL A DEDUCIR", deductionSecondColumn, y = y + 15)
      .text(formatAmount(payroll.total_deductions), deductionFourColumn + numberOffset(formatAmount(payroll.total_deductions)), y);

    pdf
      .font(headingFont)
      .text("LÍQUIDO TOTAL A PERCIBIR (A - B)", deductionPaddingX, y = y + 270)
      .text(formatAmount(payroll.liquid_perceive), deductionFourColumn + numberOffset(formatAmount(payroll.liquid_perceive)), y)
      .moveTo(deductionThirdColumn + 45, y = y + 8).lineTo(deductionFourColumn + 45, y).stroke();

  }

  //Main
  newHearderTittle();
  newEnterpriseBox();
  newEmployeeBox();
  newSettlementBox();
  newAccrual();
  newDeduction();
  newSignatureDate();
  newFooterBox();

  //End PDF
  pdf.end();

}

module.exports.newClassicPayroll = function(payrolls, stream){
	
	//Initialize pdf object
    var pdf = new PDF({
        size: [595.28, 841.89],
        margins: { // by default, all are 72
            top: 5,
            bottom: 5,
            left: 5,
            right: 5
        }
    });

    pdf.pipe(stream);

    //PDF Styles
    var pdfWidth = pdf.page.width -
        pdf.page.margins.left -
        pdf.page.margins.right;

    //Variables y constants
    var t0p = 0;
    var left = 0;
    var right = 0;

    textFooterSize = 7.5;
    textFooterFont = 'Helvetica';

    textSize = 8;
    textFont = 'Helvetica';

    titleTextSize = 8;
    titleTextFont = 'Helvetica-Bold';

    headingSize = 4;
    headingFont = 'Helvetica-Bold';

    signature = 585;

    var salary_perceptions_codes = [1, 49, 50, 55];
    var extra_hours_codes = [2, 3];
    var extra_perks_codes = [4, 5];
    var spices_salary_codes = [13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26];

    //SET BLANKs IMAGES
    // var blank_image = 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAIBAQIBAQICAgICAgICAwUDAwMDAwYEBAMFBwYHBwcGBwcICQsJCAgKCAcHCg0KCgsMDAwMBwkODw0MDgsMDAz/2wBDAQICAgMDAwYDAwYMCAcIDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAz/wAARCAABAAEDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9/KKKKAP/2Q==';
    // payroll.logo = blank_image;
    // payroll.signature_logo = blank_image;
    // payroll.logoEnterprise = blank_image;
    // payroll.logoEnterprise2 = blank_image;

    //Aux Methods
    String.prototype.splice = function(idx, rem, str) {
        return this.slice(0, idx) + str + this.slice(idx + Math.abs(rem));
    };

    var checkDates = function(accrualType, accrualTypeExpression) {
    	startDate = new Intl.DateTimeFormat('en-GB').format(accrualType.startDate);
        endDate = new Intl.DateTimeFormat('en-GB').format(accrualType.endDate);
        
        var diffDates = parseInt((accrualType.endDate-accrualType.startDate)/1000/60/60/24);
        
        if (null != accrualType.startDate && null != accrualType.endDate && startDate != endDate && diffDates > 1 && diffDates < 28) {
            return accrualTypeExpression + parseDDMMDate(startDate, endDate);
        }
        return accrualTypeExpression;
    }

    var parseDDMMDate = function(startDate, endDate) {
        /*
    	startDay = startDate.getDate();
        startMonth = startDate.getMonth() + 1;

        if (startDay < 10)
            startDay = "0" + startDay;

        if (startMonth < 10)
            startMonth = "0" + startMonth;

        endDay = endDate.getDate();
        endMonth = endDate.getMonth() + 1;

        if (endDay < 10)
            endDay = "0" + endDay;

        if (endMonth < 10)
            endMonth = "0" + endMonth;
        */
    	
    	startDay = startDate.split("\/")[0];
    	startMonth = startDate.split("\/")[1];
    	
    	endDay = endDate.split("\/")[0];
    	endMonth = endDate.split("\/")[1];

        return " ( " + startDay + "/" + startMonth + " - " + endDay + "/" + endMonth + " )";
    }

    var checkAccrualName = function(accrualName) {
        if (accrualName) {
            type = accrualName.split(" ")[0];
            if (type == "01")
                return "01 RETRIBUCIONES SALARIALES".splice(2, 0, ". ");

            return accrualName.splice(2, 0, ". ");
        }
        return accrualName;
    }

    var formatFirstPartAddress = function(address) {
        if (address.includes("("))
            return address.split("(")[0];
        return address;
    }

    var formatSecondPartAddress = function(address, province) {
        if (address.includes("(")) {
            zip = address.split("(")[1].split(")")[0];
            city = address.split(")")[1];

            return zip + " " + city + " " + province;
        }
        return province;
    }

    var formatInputData = function(data, parseData) {
        if (data == null || data == undefined) {
            if (parseData == 'string')
                return '';
            else if (parseData == 'number')
                return '';
        } else {
            var typeData = typeof data;
            //POSIBLE IMPLEMENTACION PARA ALGO MAS INTELIGENTE
            if (typeData == 'number' && data == 0) {
                return '';
            } else if (typeData == 'number' && isNaN(data)) {
                return '';
            }
            return data;
        }
    }

    var parseExpression = function(expression) {
        if (null != expression && undefined != expression && expression.includes("]"))
            return expression.split("]")[1].trim();
        return expression.trim();
    }

    var formatAmount = function(amount) {
        const config = {
            minimumFractionDigits: 2
        }

        return (amount && amount != '' && amount != null && amount != 0) ? new Intl.NumberFormat("de-DE", config).format(amount.toFixed(2)) : '';
    }

    var formatPercent = function(percent) {
        if ('string' == typeof percent) {
            if (percent.includes(" %")) {
                percentInt = parseFloat(percent.split(" %")[0].trim());
                return (percentInt && percentInt != '' && percentInt != null && percentInt != 0) ? percentInt.toFixed(2) + " %" : '';
            }
            return percent;
        } else
            return (percent && percent != '' && percent != null && percent != 0) ? percent.toFixed(2) + " %" : '';
    }

    var formatMoney = function(amount) {
        const config = {
            style: "currency",
            currency: "EUR",
            minimumFractionDigits: 2,
            currencyDisplay: "symbol"
        }
        return (amount && amount != '' && amount != null) ? new Intl.NumberFormat("de-DE", config).format(amount.toFixed(2)) : '';
    }

    var numberOffset = function(number) {
        if (number != '' && 'string' != typeof number) {
            var formatNumber = formatAmount(number);
            var split = formatNumber.split(',');
            switch (split[0].length) {
                case 1:
                    return 0;
                case 2:
                    return -4;
                case 3:
                    return -8;
                case 4:
                    return -11;
                case 5:
                    return -15;
                case 6:
                    return -19;
                default:
                    return 0;
            }
        } else {
            return 0;
        }
    }

    var parseDate = function(date) {
        return new Intl.DateTimeFormat('en-GB').format(date);
    }

    var formatDate = function(date) {
        var split = date.split('/');
        return split[0] + ' de ' + getStrMonth(split[1]) + ' de ' + split[2];
    }

    var getStrMonth = function(numberMonth) {
        switch (numberMonth) {
            case '1':
                return 'enero';
            case '2':
                return 'febrero';
            case '3':
                return 'marzo';
            case '4':
                return 'abril';
            case '5':
                return 'mayo';
            case '6':
                return 'junio';
            case '7':
                return 'julio';
            case '8':
                return 'agosto';
            case '9':
                return 'septiembre';
            case '10':
                return 'octubre';
            case '11':
                return 'noviembre';
            case '12':
                return 'diciembre';
            default:
                return 'error';
        }
    }


  //Main Methods
    var newHearderTittle = function(payroll) {
        titleSize = 10;
        titleFont = 'Helvetica-Bold';

        pdf.lineWidth(1);

        //Start drawing PDF
        pdf
            .moveDown(0.5)
            .font(titleFont)
            .fontSize(titleSize)
            .text('RECIBO INDIVIDUAL JUSTIFICATIVO DEL PAGO DE SALARIO', { align: 'center' })
            .moveDown(1);
    }

    var newEnterpriseBox = function(payroll) {
        enterpriseTop = 30;
        enterpriseHeight = 90;
        topLeftCorner = 10;
        boxWidth = 290;

        title = 30;
        firstColumn = 25;
        secondColumn = 180;

        paddingLeft = 20;

        // -----------------------------------------------------------------------------------------------------------------
        // -------------------------------------------- ENTERPRISE ---------------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------
        pdf
            .moveTo(topLeftCorner, enterpriseTop).lineTo(topLeftCorner + 5, enterpriseTop).stroke() //Horizontal t0p line
            .font(textFont)
            .fontSize(textSize)
            .text('Empresa', topLeftCorner + 15, enterpriseTop - 3)
            .moveTo(topLeftCorner + 70, enterpriseTop).lineTo(boxWidth, enterpriseTop).stroke()
            .font(titleFont)
            .fontSize(titleTextSize)
            .text(payroll.enterprise.name, firstColumn, enterpriseTop + 12)

        .font(textFont)
            .fontSize(textSize)
            .text(formatFirstPartAddress(payroll.enterprise.address), firstColumn + paddingLeft, enterpriseTop + 24)
            .text(formatSecondPartAddress(payroll.enterprise.address, payroll.enterprise.city.toUpperCase()), firstColumn + paddingLeft, enterpriseTop + 36)

        .font(headingFont)
            .text('CIF: ', firstColumn, enterpriseTop + 48)
            .font(textFont)
            .text(payroll.enterprise.cif, firstColumn + paddingLeft, enterpriseTop + 48)

        .font(headingFont)
            .text('CCC: ', secondColumn, enterpriseTop + 48)
            .font(textFont)
            .text(payroll.enterprise.ccc, secondColumn + paddingLeft + 5, enterpriseTop + 48)

        .moveTo(topLeftCorner, enterpriseTop).lineTo(topLeftCorner, enterpriseHeight).stroke() //Vertical left line
            .moveTo(boxWidth, enterpriseTop).lineTo(boxWidth, enterpriseHeight).stroke() //Vertical right line
            .moveTo(topLeftCorner, enterpriseHeight).lineTo(boxWidth, enterpriseHeight).stroke(); //Horizontal bottom line

    }

    var newEmployeeBox = function(payroll) {
        enterpriseTop = 30;
        employeeHeight = 90;
        topLeftCorner = 295;
        boxWidth = 290;

        firstColumn = 310;
        secondColumn = 340;
        thirdColumn = 410;
        fourthColumn = 485;
        fifthColumn = 530;
        sixthColumn = 550;

        paddingLeft = 25;

        // -----------------------------------------------------------------------------------------------------------------
        // -------------------------------------------- EMPLOYEE -----------------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------

        pdf
            .moveTo(topLeftCorner, enterpriseTop).lineTo(topLeftCorner + 5, enterpriseTop).stroke() //Horizontal t0p line
            .font(textFont)
            .text('Trabajador', topLeftCorner + 15, enterpriseTop - 3)
            .moveTo(topLeftCorner + 70, enterpriseTop).lineTo(topLeftCorner + boxWidth, enterpriseTop).stroke()
            .font(titleFont)
            .fontSize(titleTextSize)
            .text(payroll.employee.fullname, firstColumn, enterpriseTop + 12)

        .text('NIF: ', firstColumn, enterpriseTop + 24)
            .font(textFont)
            .text(payroll.employee.nif, secondColumn, enterpriseTop + 24)

        .font(headingFont)
            .text('Fecha antigüedad: ', thirdColumn, enterpriseTop + 24)
            .font(textFont)
            .text(parseDate(payroll.employee.seniority_date), fourthColumn, enterpriseTop + 24)

        .font(headingFont)
            .text('Nº S.S.: ', firstColumn, enterpriseTop + 36)
            .font(textFont)
            .text(payroll.employee.ss, secondColumn, enterpriseTop + 36)

        .font(headingFont)
            .text('Grupo cotización: ', thirdColumn, enterpriseTop + 36)
            .font(textFont)
            .text(payroll.employee.quote_group, fourthColumn, enterpriseTop + 36)

        .font(headingFont)
            .text('TC2: ', fifthColumn, enterpriseTop + 36)
            .font(textFont)
            .text(payroll.employee.contract_type, sixthColumn, enterpriseTop + 36)

        .font(headingFont)
            .text('Grupo profesional: ', firstColumn, enterpriseTop + 48)
            .font(textFont)
            .text(payroll.employee.professional_group, secondColumn + 45, enterpriseTop + 48)

        .moveTo(topLeftCorner, enterpriseTop).lineTo(topLeftCorner, employeeHeight).stroke() //Vertical left line
            .moveTo(topLeftCorner + boxWidth, enterpriseTop).lineTo(topLeftCorner + boxWidth, employeeHeight).stroke() //Vertical right line
            .moveTo(topLeftCorner, employeeHeight).lineTo(topLeftCorner + boxWidth, employeeHeight).stroke() //Horizontal bottom line
            .moveDown(1);
    }

    var newSettlementBox = function(payroll) {
        // -----------------------------------------------------------------------------------------------------------------
        // --------------------------------------------- SETTLEMENT --------------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------
        topLeftCorner = 10;
        settlementTop = 95;
        settlementHeight = 16;
        boxWidth = 585;

        firstColumn = 25;
        secondColumn = 515;

        start_date = formatDate(payroll.settlement.start_date);
        end_date = formatDate(payroll.settlement.end_date);

        pdf
            .moveTo(topLeftCorner, settlementTop).lineTo(boxWidth, settlementTop).stroke() //Horizontal t0p line
            .font(headingFont)
            .text('Periodo de liquidación: ', firstColumn, settlementTop + 5)
            .font(textFont)
            .text('del ' + start_date + ' al ' + end_date, firstColumn + 90, settlementTop + 5);

        pdf
            .font(headingFont)
            .text('Total días: ', secondColumn, settlementTop + 5)
            .font(textFont)
            .text(payroll.settlement.total_days, secondColumn + 45, settlementTop + 5)

        .moveTo(topLeftCorner, settlementTop).lineTo(topLeftCorner, settlementTop + settlementHeight).stroke() //Vertical left line
            .moveTo(boxWidth, settlementTop).lineTo(boxWidth, settlementTop + settlementHeight).stroke() //Vertical right line
            .moveTo(topLeftCorner, settlementTop + settlementHeight).lineTo(boxWidth, settlementTop + settlementHeight).stroke(); //Horizontal bottom line

    }

    var newFooterBox = function(payroll) {
        // -----------------------------------------------------------------------------------------------------------------
        // -------------------------------------------- FOOTER -------------------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------
        topLeftCorner = 10;
        boxWidth = 585;

        title = 30;
        firstColumn = 40;
        secondColumn = 180;
        thirdColumn = 290;
        fourthColumn = 395;
        fifthColumn = 455;
        sixthColumn = 530;

        paddingLeft = 8;

        footer = 665;

        pdf
            .moveTo(topLeftCorner, footer).lineTo(boxWidth, footer).undash().stroke() //Horizontal t0p line
            .font(textFooterFont)
            .fontSize(textFooterSize)
            .text('DETERMINACIÓN DE LAS BASES DE COTIZACIÓN A LA SEGURIDAD SOCIAL Y CONCEPTOS DE RECAUDACIÓN CONJUNTAS Y DE ' +
                'LA BASE SUJETA A ', title, footer + 5)
            .font(textFooterFont)
            .text('RETENCIÓN DEL IRPF Y APORTACIÓN DE LA EMPRESA', title, footer + 15)
            .font(textFooterFont)
            .text('1. Contingencias comunes', firstColumn, footer + 25, { continued: true })
            .font(textFooterFont)
            .text('BASE', thirdColumn + 10, footer + 25, { continued: true })
            .font(textFooterFont)
            .text('TIPO', thirdColumn + 60, footer + 25, { continued: true })
            .font(textFooterFont)
            .text('AP. EMPRESA', thirdColumn + 90, footer + 25)

        .font(textFooterFont)
            .text('Importe remuneración mensual', firstColumn + 8, footer + 35)
            .font(textFooterFont)
            .text(formatMoney(payroll.footer_ss_quotation.common_contingency.monthly_remuneration), thirdColumn + numberOffset(payroll.footer_ss_quotation.common_contingency.monthly_remuneration), footer + 35)
            .lineWidth(0.1)
            .moveTo(firstColumn + paddingLeft, footer + 43).lineTo(thirdColumn - 30, footer + 43).dash(1, { space: 2 }).stroke()
            .moveTo(thirdColumn - 30, footer + 43).lineTo(thirdColumn + 23, footer + 43).undash().stroke()

        .font(textFooterFont)
            .text('Importe prorrata de pagas extraordinarias', firstColumn + paddingLeft, footer + 47)
            .font(textFooterFont)
            .text(formatMoney(payroll.footer_ss_quotation.common_contingency.extraordinary_pay_packet), thirdColumn + numberOffset(payroll.footer_ss_quotation.common_contingency.extraordinary_pay_packet), footer + 47)
            .moveTo(firstColumn + paddingLeft, footer + 55).lineTo(thirdColumn - 30, footer + 55).dash(1, { space: 2 }).stroke()
            .moveTo(thirdColumn - 30, footer + 55).lineTo(thirdColumn + 23, footer + 55).undash().stroke()

        .font(textFooterFont)
            .text(formatMoney(payroll.footer_ss_quotation.common_contingency.base), fourthColumn + numberOffset(payroll.footer_ss_quotation.common_contingency.base), footer + 41)
            .font(textFooterFont)
            .text(formatPercent(payroll.footer_ss_quotation.common_contingency.type_percent), fifthColumn + numberOffset(payroll.footer_ss_quotation.common_contingency.type_percent), footer + 41)
            .font(textFooterFont)
            .text(formatMoney(payroll.footer_ss_quotation.common_contingency.company_input), sixthColumn + numberOffset(payroll.footer_ss_quotation.common_contingency.company_input), footer + 41)
            .moveTo(thirdColumn + 70, footer + 49).lineTo(thirdColumn + 130, footer + 49).stroke()
            .moveTo(fourthColumn + 50, footer + 49).lineTo(thirdColumn + 265, footer + 49).stroke()

        .font(textFooterFont)
            .text('2. Contingencias profesionales', firstColumn, footer + 75)
            .font(textFooterFont)
            .text('conceptos de recaudación conjunta', firstColumn + paddingLeft, footer + 85)

        .font(textFooterFont)
            .text('AT Y EP', secondColumn, footer + 60)
            .moveTo(secondColumn, footer + 68).lineTo(secondColumn + 135, footer + 68).dash(1, { space: 2 }).stroke()
            .font(textFooterFont)
            .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_at), fifthColumn + numberOffset(payroll.footer_ss_quotation.professional_contingency.type_percent_at), footer + 60)
            .font(textFooterFont)
            .text(formatMoney(payroll.footer_ss_quotation.professional_contingency.company_input_at), sixthColumn + numberOffset(payroll.footer_ss_quotation.professional_contingency.company_input_at), footer + 60)
            .moveTo(fourthColumn + 50, footer + 68).lineTo(thirdColumn + 265, footer + 68).undash().stroke()

        .font(textFooterFont)
            .text('Desempleo', secondColumn, footer + 72)
            .moveTo(secondColumn, footer + 80).lineTo(secondColumn + 135, footer + 80).dash(1, { space: 2 }).stroke()
            .font(textFooterFont)
            .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_unemployment), fifthColumn + numberOffset(payroll.footer_ss_quotation.professional_contingency.type_percent_unemployment), footer + 72)
            .font(textFooterFont)
            .text(formatMoney(payroll.footer_ss_quotation.professional_contingency.company_input_unemployment), sixthColumn + numberOffset(payroll.footer_ss_quotation.professional_contingency.company_input_unemployment), footer + 72)
            .moveTo(fourthColumn + 50, footer + 80).lineTo(thirdColumn + 265, footer + 80).undash().stroke()

        .font(textFooterFont)
            .text(formatMoney(payroll.footer_ss_quotation.professional_contingency.base), fourthColumn + numberOffset(payroll.footer_ss_quotation.professional_contingency.base), footer + 78)
            .moveTo(thirdColumn + 70, footer + 86).lineTo(thirdColumn + 130, footer + 86).stroke()

        .font(textFooterFont)
            .text('Fromación profesional', secondColumn, footer + 84)
            .moveTo(secondColumn, footer + 92).lineTo(secondColumn + 135, footer + 92).dash(1, { space: 2 }).stroke()
            .font(textFooterFont)
            .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_professional_formation), fifthColumn + numberOffset(payroll.footer_ss_quotation.professional_contingency.type_percent_professional_formation), footer + 84)
            .font(textFooterFont)
            .text(formatMoney(payroll.footer_ss_quotation.professional_contingency.company_input_professional_formation), sixthColumn + numberOffset(payroll.footer_ss_quotation.professional_contingency.company_input_professional_formation), footer + 84)
            .moveTo(fourthColumn + 50, footer + 92).lineTo(thirdColumn + 265, footer + 92).undash().stroke()

        .font(textFooterFont)
            .text('Fondo de Garantía Salarial', secondColumn, footer + 96)
            .moveTo(secondColumn, footer + 104).lineTo(secondColumn + 135, footer + 104).dash(1, { space: 2 }).stroke()
            .font(textFooterFont)
            .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_salary_warranty), fifthColumn + numberOffset(payroll.footer_ss_quotation.professional_contingency.type_percent_salary_warranty), footer + 96)
            .font(textFooterFont)
            .text(formatMoney(payroll.footer_ss_quotation.professional_contingency.company_input_salary_warranty), sixthColumn + numberOffset(payroll.footer_ss_quotation.professional_contingency.company_input_salary_warranty), footer + 96)
            .moveTo(fourthColumn + 50, footer + 104).lineTo(thirdColumn + 265, footer + 104).undash().stroke()

        .font(textFooterFont)
            .text('3. Cotización adicional por horas extraordinarias', firstColumn, footer + 108)
            .font(textFooterFont)
            .text('Fuerza mayor', secondColumn, footer + 118)
            .text(formatMoney(payroll.footer_ss_quotation.aditional_quotation.base_overwhelming_force), fifthColumn + numberOffset(payroll.footer_ss_quotation.aditional_quotation.base_overwhelming_force), footer + 118)
            .text(formatMoney(payroll.footer_ss_quotation.aditional_quotation.company_input_overwhelming_force), sixthColumn + numberOffset(payroll.footer_ss_quotation.aditional_quotation.company_input_overwhelming_force), footer + 118)
            .moveTo(secondColumn, footer + 126).lineTo(thirdColumn + 70, footer + 126).dash(1, { space: 2 }).stroke()
            .moveTo(thirdColumn + 70, footer + 126).lineTo(thirdColumn + 130, footer + 126).undash().stroke()
            .moveTo(fourthColumn + 50, footer + 126).lineTo(thirdColumn + 265, footer + 126).stroke()

        .font(textFooterFont)
            .text('No estructurales', secondColumn, footer + 130)
            .text(formatMoney(payroll.footer_ss_quotation.aditional_quotation.base_non_structural), fifthColumn + numberOffset(payroll.footer_ss_quotation.aditional_quotation.base_non_structural), footer + 130)
            .text(formatMoney(payroll.footer_ss_quotation.aditional_quotation.company_input_non_structural), sixthColumn + numberOffset(payroll.footer_ss_quotation.aditional_quotation.company_input_non_structural), footer + 130)
            .moveTo(secondColumn, footer + 138).lineTo(thirdColumn + 70, footer + 138).dash(1, { space: 2 }).stroke()
            .moveTo(thirdColumn + 70, footer + 138).lineTo(thirdColumn + 130, footer + 138).undash().stroke()
            .moveTo(fourthColumn + 50, footer + 138).lineTo(thirdColumn + 265, footer + 138).stroke();

        taxesS = getDeduction(payroll, "Especie");
        if (!taxesS.value || taxesS.value == 0) {
            pdf
                .font(textFooterFont)
                .text('4. Base sujeta a retención del IRPF', firstColumn, footer + 142)
                .text(formatMoney(payroll.footer_ss_quotation.base_irpf), fourthColumn + numberOffset(payroll.footer_ss_quotation.base_irpf), footer + 142)
                .moveTo(firstColumn + paddingLeft, footer + 150).lineTo(thirdColumn + 70, footer + 150).dash(1, { space: 2 }).stroke()
                .moveTo(thirdColumn + 70, footer + 150).lineTo(thirdColumn + 130, footer + 150).undash().stroke();

            pdf
                .text('Total aportaciones', firstColumn + paddingLeft, footer + 154)
                .text(formatMoney(payroll.footer_ss_quotation.total_company), sixthColumn + numberOffset(payroll.footer_ss_quotation.total_company), footer + 154)
                .moveTo(firstColumn + paddingLeft, footer + 162).lineTo(sixthColumn - 20, footer + 162).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 20, footer + 162).lineTo(thirdColumn + 265, footer + 162).undash().stroke();

            pdf
                .lineWidth(1)
                .moveTo(topLeftCorner, footer).lineTo(topLeftCorner, footer + 167).stroke() //Vertical left line
                .moveTo(boxWidth, footer).lineTo(boxWidth, footer + 167).stroke() //Vertical right line
                .moveTo(topLeftCorner, footer + 167).lineTo(boxWidth, footer + 167).stroke(); //Horizontal bottom line
        } else {
            taxesD = getDeduction(payroll, "Dinerario");
            pdf
                .font(textFooterFont)
                .text('4. Base sujeta a retención del IRPF: ' + formatMoney(taxesS.amount) + '€  en especie + ' + formatMoney(taxesD.amount) + "€  en retribuciones dinerarias", firstColumn, footer + 142)
                .text(formatMoney(payroll.footer_ss_quotation.base_irpf), fourthColumn + numberOffset(payroll.footer_ss_quotation.base_irpf), footer + 142)
                .moveTo(firstColumn + paddingLeft, footer + 150).lineTo(secondColumn - 20, footer + 150).dash(1, { space: 2 }).stroke()
                .moveTo(secondColumn - 20, footer + 150).lineTo(thirdColumn + 130, footer + 150).undash().stroke()
                .text('Total aportaciones', firstColumn, footer + 154)
                .text(formatMoney(payroll.footer_ss_quotation.total_company), sixthColumn + numberOffset(payroll.footer_ss_quotation.total_company), footer + 154)
                .moveTo(firstColumn + paddingLeft, footer + 162).lineTo(sixthColumn - 20, footer + 162).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 20, footer + 162).lineTo(thirdColumn + 265, footer + 162).undash().stroke();

            pdf
                .lineWidth(1)
                .moveTo(topLeftCorner, footer).lineTo(topLeftCorner, footer + 170).stroke() //Vertical left line
                .moveTo(boxWidth, footer).lineTo(boxWidth, footer + 170).stroke() //Vertical right line
                .moveTo(topLeftCorner, footer + 170).lineTo(boxWidth, footer + 170).stroke(); //Horizontal bottom line
        }
    }

    var newSignatureDate = function(payroll) {
        // -----------------------------------------------------------------------------------------------------------------
        // ------------------------------------------ SIGNATURE / DATE -----------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------
        topLeftCorner = 10;
        boxWidth = 585;
        signatureTop = 470;
        accrualHeigt = 545;
        accrualTop = 115;

        firstColumn = 50;
        secondColumn = 230;
        thirdColumn = 250;
        fourthColumn = 400;
        fifthColumn = 220;
        sixthColumn = 450;
        seventhColumn = 535;

        pdf
            .fontSize(titleSize)
            .font(titleFont)
            .text('LÍQUIDO TOTAL A PERCIBIR (A-B)', secondColumn, accrualTop + signatureTop)
            .text(formatMoney(payroll.liquid_perceive), seventhColumn + numberOffset(payroll.liquid_perceive), accrualTop + signatureTop)
            .moveTo(secondColumn, accrualTop + signatureTop + 10).lineTo(seventhColumn - 26, accrualTop + signatureTop + 10).dash(1, { space: 2 }).stroke()
            .moveTo(seventhColumn - 26, accrualTop + signatureTop + 10).lineTo(seventhColumn + 35, accrualTop + signatureTop + 10).undash().stroke();

        pdf
            .font(textFooterFont)
            .fontSize(textFooterSize)
            .text('Firma y sello de la empresa', firstColumn, accrualTop + signatureTop + 50)
            .image(payroll.signature_logo, firstColumn + 10, accrualTop + signatureTop, { scale: 0.06 })

        .text(`RECIBÍ (${end_date}) :`, thirdColumn, accrualTop + signatureTop + 24)
            .fontSize(textFooterSize)
            .text(formatInputData(payroll.employee.fullname, 'string'), fourthColumn, accrualTop + signatureTop + 50)

        .lineWidth(1)
            .moveTo(topLeftCorner, accrualTop).lineTo(topLeftCorner, accrualTop + accrualHeigt).stroke() //Vertical left line
            .moveTo(boxWidth, accrualTop).lineTo(boxWidth, accrualTop + accrualHeigt).stroke() //Vertical right line
            .moveTo(topLeftCorner, accrualTop + accrualHeigt).lineTo(boxWidth, accrualTop + accrualHeigt).stroke(); //Horizontal bottom line
    }

    var getAccrual = function(payroll, accrualName) {
        for (i = 0; i < payroll.accruals.length; i++) {
            accrual = payroll.accruals[i];
            if (accrual.accrual_name == accrualName)
                return accrual;
        }
        return undefined;
    }

    var newAccrual = function(payroll) {
        // -----------------------------------------------------------------------------------------------------------------
        // --------------------------------------------- ACCRUAL -----------------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------

        topLeftCorner = 10;
        boxWidth = 585;
        accrualTop = 115;

        firstColumn = 25;
        secondColumn = 30;
        thirdColumn = 50;
        fourthColumn = 65;
        fifthColumn = 220;
        sixthColumn = 450;
        seventhColumn = 535;

        pdf
            .moveTo(topLeftCorner, accrualTop).lineTo(boxWidth, accrualTop).stroke()
            .font(headingFont)
            .fontSize(titleSize)
            .text('I. DEVENGOS', firstColumn, accrualTop + 12)
            .text('TOTALES', seventhColumn - 10, accrualTop + 12)
            .fontSize(textSize)
            .lineWidth(0.1);

        newLine = 12;
        paddinBottom = 25;
        accrualsNumb = 0;

        actualLine = accrualTop + paddinBottom;
        totalAccrualLine = actualLine;

        pdf
            .moveTo(topLeftCorner, accrualTop).lineTo(boxWidth, accrualTop).stroke()
            .font(headingFont)
            .fontSize(titleSize)
            .text('I. DEVENGOS', firstColumn, accrualTop + 12)
            .text('TOTALES', seventhColumn - 10, accrualTop + 12)
            .fontSize(textSize)
            .lineWidth(0.1)

        .font(headingFont)
            .fontSize(textSize)
            .text('1. Percepciones salariales', secondColumn, actualLine);

        for (var i = 0; i < payroll.accruals.length; i++) {
            for (var j = 0; j < payroll.accruals[i].types.length; j++) {
                if (salary_perceptions_codes.includes(payroll.accruals[i].types[j].code) && payroll.accruals[i].types[j].type_expression.includes("SALARIO BASE")) {
                    actualLine += 12;
                    type = payroll.accruals[i].types[j];
                    pdf
                        .fontSize(textSize)
                        .font(textFont)
                        .text(checkDates(type, parseExpression(type.type_expression)), thirdColumn, actualLine)
                        .text(formatMoney(type.type_value), sixthColumn + numberOffset(type.type_value), actualLine)
                        .moveTo(thirdColumn, actualLine + 8).lineTo(sixthColumn - 25, actualLine + 8).dash(1, { space: 2 }).stroke()
                        .moveTo(sixthColumn - 25, actualLine + 8).lineTo(sixthColumn + 25, actualLine + 8).undash().stroke();
                }
            }
        }

        actualLine += 12;

        pdf
            .fontSize(textSize)
            .font(textFont)
            .text('Complementos salariales', thirdColumn, actualLine);

        for (var i = 0; i < payroll.accruals.length; i++) {
            for (var j = 0; j < payroll.accruals[i].types.length; j++) {
                if (salary_perceptions_codes.includes(payroll.accruals[i].types[j].code) && !payroll.accruals[i].types[j].type_expression.includes("SALARIO BASE") && payroll.accruals[i].types[j].type_value > 0) {

                    type = payroll.accruals[i].types[j];
                    actualLine += 12;

                    pdf
                        .fontSize(textSize)
                        .font(textFont)
                        .text(checkDates(type, parseExpression(type.type_expression)), fourthColumn, actualLine)
                        .text(formatMoney(type.type_value), sixthColumn + numberOffset(type.type_value), actualLine)
                        .moveTo(fourthColumn, actualLine + 8).lineTo(sixthColumn - 25, actualLine + 8).dash(1, { space: 2 }).stroke()
                        .moveTo(sixthColumn - 25, actualLine + 8).lineTo(sixthColumn + 25, actualLine + 8).undash().stroke();
                }
            }
        }

        actualLine += 12;

        pdf
            .fontSize(textSize)
            .font(textFont)
            .text('Horas extraordinarias', thirdColumn, actualLine);

        extra_hours_total = 0;
        for (var i = 0; i < payroll.accruals.length; i++) {
            for (var j = 0; j < payroll.accruals[i].types.length; j++) {
                if (extra_hours_codes.includes(payroll.accruals[i].types[j].code)) {
                    type = payroll.accruals[i].types[j];
                    extra_hours_total += type.type_value;
                }
            }
        }

        if (extra_hours_total != 0) {
            pdf
                .text(formatMoney(extra_hours_total), sixthColumn + numberOffset(extra_hours_total), actualLine);
        }

        pdf
            .moveTo(thirdColumn, actualLine + 8).lineTo(sixthColumn - 25, actualLine + 8).dash(1, { space: 2 }).stroke()
            .moveTo(sixthColumn - 25, actualLine + 8).lineTo(sixthColumn + 25, actualLine + 8).undash().stroke();

        actualLine += 12;

        pdf
            .fontSize(textSize)
            .font(textFont)
            .text('Gratificaciones extraordinarias', thirdColumn, actualLine);

        extra_perks_total = 0;
        for (var i = 0; i < payroll.accruals.length; i++) {
            for (var j = 0; j < payroll.accruals[i].types.length; j++) {
                if (extra_perks_codes.includes(payroll.accruals[i].types[j].code)) {
                    type = payroll.accruals[i].types[j];
                    extra_perks_total += type.type_value;
                }
            }
        }

        if (extra_perks_total != 0) {
            pdf
                .text(formatMoney(extra_perks_total), sixthColumn + numberOffset(extra_perks_total), actualLine);
        }

        pdf
            .moveTo(thirdColumn, actualLine + 8).lineTo(sixthColumn - 25, actualLine + 8).dash(1, { space: 2 }).stroke()
            .moveTo(sixthColumn - 25, actualLine + 8).lineTo(sixthColumn + 25, actualLine + 8).undash().stroke();

        actualLine += 12;

        pdf
            .fontSize(textSize)
            .font(textFont)
            .text('Salario en especie', thirdColumn, actualLine);

        spices_salary_total = 0;
        for (var i = 0; i < payroll.accruals.length; i++) {
            for (var j = 0; j < payroll.accruals[i].types.length; j++) {
                if (spices_salary_codes.includes(payroll.accruals[i].types[j].code)) {
                    type = payroll.accruals[i].types[j];
                    spices_salary_total += type.type_value;
                }
            }
        }

        if (spices_salary_total != 0) {
            pdf
                .text(formatMoney(spices_salary_total), sixthColumn + numberOffset(spices_salary_total), actualLine);
        }

        pdf
            .moveTo(thirdColumn, actualLine + 8).lineTo(sixthColumn - 25, actualLine + 8).dash(1, { space: 2 }).stroke()
            .moveTo(sixthColumn - 25, actualLine + 8).lineTo(sixthColumn + 25, actualLine + 8).undash().stroke();

        actualLine += 12;

        pdf
            .font(headingFont)
            .fontSize(textSize)
            .text('2. Percepciones no salariales', secondColumn, actualLine);


        suplies = getAccrual(payroll, "Indemnizaciones o suplidos");

        actualLine += 12;

        pdf
            .font(textFont)
            .text('Indemnizaciones o suplidos', thirdColumn, actualLine);

        if (undefined != suplies && suplies.types.length != 0) {

            for (i = 0; i < suplies.types.length; i++) {
                actualLine += 12;
                suply = suplies.types[i];
                pdf
                    .text(checkDates(suply, parseExpression(suply.type_expression)), fourthColumn, actualLine)
                    .text(formatMoney(suply.type_value), sixthColumn + numberOffset(suply.type_value), actualLine)
                    .moveTo(fourthColumn, actualLine + 8).lineTo(sixthColumn - 25, actualLine + 8).dash(1, { space: 2 }).stroke()
                    .moveTo(sixthColumn - 25, actualLine + 8).lineTo(sixthColumn + 25, actualLine + 8).undash().stroke();
            }

        }

        compensations_SS = getAccrual(payroll, 'Prestaciones e indemnizaciones a la Seguridad Social');

        actualLine += 12;

        pdf
            .text('Prestaciones e indemnizaciones a la Seguridad Social', thirdColumn, actualLine);

        if (undefined != compensations_SS && compensations_SS.types.length != 0) {

            for (i = 0; i < compensations_SS.types.length; i++) {
                actualLine += 12;
                compensation = compensations_SS.types[i];
                pdf
                    .text(checkDates(compensation, parseExpression(compensation.type_expression)), fourthColumn, actualLine)
                    .text(formatMoney(compensation.type_value), sixthColumn + numberOffset(compensation.type_value), actualLine)
                    .moveTo(fourthColumn, actualLine + 8).lineTo(sixthColumn - 25, actualLine + 8).dash(1, { space: 2 }).stroke()
                    .moveTo(sixthColumn - 25, actualLine + 8).lineTo(sixthColumn + 25, actualLine + 8).undash().stroke();
            }

        }

        other_perceptions = getAccrual(payroll, 'Otras percepciones no salariales');

        actualLine += 12;

        pdf
            .text('Otras percepciones no salariales', thirdColumn, actualLine);

        if (undefined != other_perceptions && other_perceptions.types.length != 0) {

            for (i = 0; i < other_perceptions.types.length; i++) {
                actualLine += 12;
                perception = other_perceptions.types[i];
                pdf
                    .text(perception.type_expression, fourthColumn, actualLine)
                    .text(formatMoney(perception.type_value), sixthColumn + numberOffset(perception.type_value), actualLine)
                    .moveTo(fourthColumn, actualLine + 8).lineTo(sixthColumn - 25, actualLine + 8).dash(1, { space: 2 }).stroke()
                    .moveTo(sixthColumn - 25, actualLine + 8).lineTo(sixthColumn + 25, actualLine + 8).undash().stroke();
            }

        }

        totalAccrualLine = actualLine + 20;
        console.error('totalAccrualLine : ' + totalAccrualLine);


        pdf
            .fontSize(titleSize)
            .font(titleFont)
            .text('A. TOTAL DEVENGADO', fifthColumn, totalAccrualLine)
            .text(formatMoney(payroll.total_accrual), seventhColumn + numberOffset(payroll.total_accrual), totalAccrualLine)
            .moveTo(fifthColumn, totalAccrualLine + 10).lineTo(seventhColumn - 25, totalAccrualLine + 10).dash(1, { space: 2 }).stroke()
            .moveTo(seventhColumn - 25, totalAccrualLine + 10).lineTo(seventhColumn + 35, totalAccrualLine + 10).undash().stroke();
    }

    var getDeduction = function(payroll, nameDeduction) {
        for (i = 0; i < payroll.deductions.length; i++) {
            deduction = payroll.deductions[i];
            if (deduction.name == nameDeduction) {
                return deduction;
            }
        }
        return {
            types: [],
            value: 0

        };
        //return undefined;
    }

    var getDeductions = function(payroll, namesDeductions) {
        deductions = [];
        for (i = 0; i < payroll.deductions.length; i++) {
            deduction = payroll.deductions[i];
            if (namesDeductions.includes(deduction.type_name)) {
                deductions.push(deduction);
            }
        }
        return deductions;
    }

    var newDeductionFirstPage = function(payroll) {
        // -----------------------------------------------------------------------------------------------------------------
        // --------------------------------------------- DEDUCTION ---------------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------
        deductionTop = totalAccrualLine + 20;

        firstColumn = 25;
        secondColumn = 30;
        thirdColumn = 60;
        fourthColumn = 85;
        fifthColumn = 117;
        sixthColumn = 448;
        seventhColumn = 535;

        totalColumn = 220;

        common_contingency = getDeduction(payroll, "CGC");
        unemployment = getDeduction(payroll, "DESMPL");
        professional_formation = getDeduction(payroll, "FP");
        extra_hours_e = getDeduction(payroll, "Horas Extraordinarias Fuerza Mayor");
        extra_hours_ne = getDeduction(payroll, "Resto Horas Extraordinarias");
        taxesD = getDeduction(payroll, "Dinerario");
        taxesS = getDeduction(payroll, "Especie");
        advances = getDeductions(payroll, ["Anticipo"]);
        spices = getDeduction(payroll, "Spices");
        other_deductions = getDeductions(payroll, ["Otras deducciones", "Embargo"]);
        irpf = getDeduction(payroll, "IRPF");

        existingTaxes = 0;

        if (totalAccrualLine <= 550) {
            total_aportation = common_contingency.value + unemployment.value + professional_formation.value + extra_hours_e.value + extra_hours_ne.value;
            // ----------------------------------------- APORTACIONES ----------------------------------------------------
            pdf
                .font(headingFont)
                .text('II. DEDUCCIONES', firstColumn, deductionTop)
                .fontSize(textSize)
                .text('1. Aportaciones del trabajador a las cotizaciones de la SS y conceptos de recaudación', secondColumn, deductionTop + 12)
                .font(textFont)
                .text(formatMoney(total_aportation), sixthColumn + numberOffset(total_aportation), deductionTop + 12)
                .moveTo(secondColumn, deductionTop + 22).lineTo(sixthColumn - 25, deductionTop + 22).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 25, deductionTop + 22).lineTo(sixthColumn + 25, deductionTop + 22).undash().stroke();

            deductionTop += 5;

            if (common_contingency.value && common_contingency.value > 0) {
                pdf
                    .font(textFont)
                    .text(formatMoney(common_contingency.value), thirdColumn + numberOffset(common_contingency.value), deductionTop + 24)
                    .text("  por un ", fourthColumn, deductionTop + 24)
                    .text(formatPercent(common_contingency.percent), fifthColumn + numberOffset(common_contingency.percent), deductionTop + 24)
                    .text("  de Contigencias comunes", fourthColumn + 60, deductionTop + 24);
            } else {
                deductionTop -= 12;
            }

            if (unemployment.value && unemployment.value > 0) {
                pdf
                    .text(formatMoney(unemployment.value), thirdColumn + numberOffset(unemployment.value), deductionTop + 36)
                    .text("  por un ", fourthColumn, deductionTop + 36)
                    .text(formatPercent(unemployment.percent), fifthColumn + numberOffset(unemployment.percent), deductionTop + 36)
                    .text("  de Desempleo", fourthColumn + 60, deductionTop + 36);
            } else {
                deductionTop -= 12;
            }

            if (unemployment.value && unemployment.value > 0) {
                pdf
                    .text(formatMoney(professional_formation.value), thirdColumn + numberOffset(professional_formation.value), deductionTop + 48)
                    .text("  por un ", fourthColumn, deductionTop + 48)
                    .text(formatPercent(professional_formation.percent), fifthColumn + numberOffset(professional_formation.percent), deductionTop + 48)
                    .text("  de Fromación profesional", fourthColumn + 60, deductionTop + 48);
            } else {
                deductionTop -= 12;
            }



            if (extra_hours_e.value && extra_hours_e.value > 0) {
                pdf
                    .text("  por Horas extraordinarias (Estruc.)", fourthColumn, deductionTop + 60)
                    .text(formatMoney(extra_hours_e.value), thirdColumn + numberOffset(extra_hours_e.value), deductionTop + 60);
            } else {
                deductionTop -= 12;
            }
            if (extra_hours_ne.value && extra_hours_ne.value > 0) {
                pdf
                    .text("  por Horas extraordinarias (No Estruc.)", fourthColumn, deductionTop + 72)
                    .text(formatMoney(extra_hours_ne.value), thirdColumn + numberOffset(extra_hours_ne.value), deductionTop + 72);
            } else {
                deductionTop -= 12;
            }

            // ----------------------------------------- IMPUESTOS ----------------------------------------------------
            taxes = taxesD.value + taxesS.value;
            pdf
                .font(headingFont)
                .text("2. Impuestos sobre la renta de personas físicas (I.R.P.F.)", secondColumn, deductionTop + 84)
                .font(textFont)
                //                .text(formatPercent(irpf.percent), sixthColumn - 130 + numberOffset(irpf.percent), deductionTop + 84)
                .text(formatMoney(irpf.value), sixthColumn + numberOffset(irpf.value), deductionTop + 84)
                .moveTo(secondColumn, deductionTop + 94).lineTo(sixthColumn - 25, deductionTop + 94).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 25, deductionTop + 94).lineTo(sixthColumn + 25, deductionTop + 94).undash().stroke();

            deductionTop += 5;

            if (taxesD.value && taxesD.value > 0) {
                pdf
                    .text(formatMoney(taxesD.amount), thirdColumn + numberOffset(taxesD.amount), deductionTop + 96)
                    .text("  por un ", fourthColumn, deductionTop + 96)
                    .text(formatPercent(taxesD.percent), fifthColumn + numberOffset(taxesD.percent), deductionTop + 96)
                    .text("  de Retribuciones dinerarias", fourthColumn + 60, deductionTop + 96);
            } else {
                deductionTop -= 12;
            }
            if (taxesS.value && taxesS.value > 0) {
                pdf
                    .text(formatMoney(taxesS.amount), thirdColumn + numberOffset(taxesS.amount), deductionTop + 108)
                    .text("  por un ", fourthColumn, deductionTop + 108)
                    .text(formatPercent(taxesS.percent), fifthColumn + numberOffset(taxesS.percent), deductionTop + 108)
                    .text("  de Retribuciones en especie", fourthColumn + 60, deductionTop + 108);
            } else {
                deductionTop -= 12;
            }

            // ----------------------------------------- ANTICIPOS ----------------------------------------------------
            advancesTOTAL = 0;
            for (var i = 0; i < advances.length; i++) {
                type = advances[i];
                advancesTOTAL += type.value;
            }

            pdf
                .font(headingFont)
                .text("3. Anticipos", secondColumn, deductionTop + 120 + existingTaxes)
                .font(textFont)
                .text(formatMoney(advancesTOTAL), sixthColumn + numberOffset(advancesTOTAL), deductionTop + 120 + existingTaxes)
                .moveTo(secondColumn, deductionTop + 130 + existingTaxes).lineTo(sixthColumn - 25, deductionTop + 130 + existingTaxes).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 25, deductionTop + 130 + existingTaxes).lineTo(sixthColumn + 25, deductionTop + 130 + existingTaxes).undash().stroke();

            deductionTop += 5;

            // ----------------------------------------- ESPECIES ----------------------------------------------------

            spicesTOTAL = 0;

            pdf
                .font(headingFont)
                .text("4. Valor de los productos recibidos en especie", secondColumn, deductionTop + 132 + existingTaxes)
                .font(textFont);


            nextLine = deductionTop + 150 + existingTaxes;

            for (i = 0; i < payroll.payments.length; i++) {
                payment = payroll.payments[i];

                if (payment.code == 13) {
                    spicesTOTAL += payment.amount;
                    pdf
                        .text(formatMoney(payment.amount), thirdColumn + numberOffset(payment.amount), nextLine)
                        .text(parseExpression(payment.description), fourthColumn + 3, nextLine);

                    nextLine += 12;
                }
            }
            pdf
                .text(formatMoney(spicesTOTAL), sixthColumn + numberOffset(spicesTOTAL), deductionTop + 132 + existingTaxes)
                .moveTo(secondColumn, deductionTop + 142 + existingTaxes).lineTo(sixthColumn - 25, deductionTop + 142 + existingTaxes).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 25, deductionTop + 142 + existingTaxes).lineTo(sixthColumn + 25, deductionTop + 142 + existingTaxes).undash().stroke();

            deductionTop = nextLine;
            // ------------------------------------- OTRAS DEDUCCIONES -----------------------------------------------
            pdf
                .font(headingFont)
                .text("5. Otras deducciones", secondColumn, deductionTop + existingTaxes)
                .font(textFont);

            other_deductionsTOTAL = 0;
            newLine = 13;
            deductionsNumb = 0;

            for (var i = 0; i < other_deductions.length; i++) {
                type = other_deductions[i];
                other_deductionsTOTAL += type.value;
                deductionsNumb += other_deductions.length + 4;
                pdf
                    .text("  por " + type.description, fourthColumn, deductionTop + existingTaxes + newLine * (i + 1))
                    .text(formatMoney(type.value), thirdColumn + numberOffset(type.value), deductionTop + existingTaxes + newLine * (i + 1) + existingTaxes);
            }

            pdf
                .font(textFont)
                .text(formatMoney(other_deductionsTOTAL), sixthColumn + numberOffset(other_deductionsTOTAL), deductionTop + existingTaxes)
                .moveTo(secondColumn, deductionTop + 10 + existingTaxes).lineTo(sixthColumn - 25, deductionTop + 10 + existingTaxes).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 25, deductionTop + 10 + existingTaxes).lineTo(sixthColumn + 25, deductionTop + 10 + existingTaxes).undash().stroke();


            // ----------------------------------------- TOTAL A DEDUCIR ------------------------------------------------
            totalAccrualLine = deductionTop + existingTaxes + newLine * (deductionsNumb) + 30;

            pdf
                .fontSize(titleSize)
                .font(titleFont)
                .text('B. TOTAL A DEDUCIR', totalColumn, totalAccrualLine)
                .text(formatMoney(payroll.total_deductions), seventhColumn + numberOffset(payroll.total_deductions), totalAccrualLine)
                .moveTo(totalColumn, totalAccrualLine + 10).lineTo(seventhColumn - 25, totalAccrualLine + 10).dash(1, { space: 2 }).stroke()
                .moveTo(seventhColumn - 25, totalAccrualLine + 10).lineTo(seventhColumn + 35, totalAccrualLine + 10).undash().stroke();

        } else {
            // ----------------------------------------- TOTAL A DEDUCIR ------------------------------------------------
            pdf
                .fontSize(titleSize)
                .font(titleFont)
                .text('B. TOTAL A DEDUCIR (Página siguiente)', totalColumn, deductionTop)
                .text(formatMoney(payroll.total_deductions), seventhColumn + numberOffset(payroll.total_deductions), deductionTop)
                .moveTo(totalColumn, deductionTop + 10).lineTo(seventhColumn - 25, deductionTop + 10).dash(1, { space: 2 }).stroke()
                .moveTo(seventhColumn - 25, deductionTop + 10).lineTo(seventhColumn + 35, deductionTop + 10).undash().stroke();
        }
    }

    var newDeductionSecondPage = function(payroll) {
        console.error('newDeductionSecondPage totalAccrualLine : ' + totalAccrualLine);
        if (totalAccrualLine > 550) {
            //NEW PAGE
            pdf.addPage();
            //PDF Styles

            newHearderTittle(payroll);
            newEnterpriseBox(payroll);
            newEmployeeBox(payroll);
            newSettlementBox(payroll);

            deductionTop = 125;

            firstColumn = 25;
            secondColumn = 30;
            thirdColumn = 60;
            fourthColumn = 85;
            fifthColumn = 117;
            sixthColumn = 448;
            seventhColumn = 535;

            totalColumn = 220;

            topLeftCorner = 10;
            boxWidth = 585;
            accrualTop = 115;

            common_contingency = getDeduction(payroll, "CGC");
            unemployment = getDeduction(payroll, "DESMPL");
            professional_formation = getDeduction(payroll, "FP");
            extra_hours_e = getDeduction(payroll, "Horas Extraordinarias Fuerza Mayor");
            extra_hours_ne = getDeduction(payroll, "Resto Horas Extraordinarias");
            taxesD = getDeduction(payroll, "Dinerario");
            taxesS = getDeduction(payroll, "Especie");
            advances = getDeduction(payroll, "Anticipos");
            spices = getDeduction(payroll, "Valor de productos en especie");
            other_deductions = getDeduction(payroll, "Otras deducciones");
            irpf = getDeduction(payroll, "IRPF");

            pdf
                .moveTo(topLeftCorner, accrualTop).lineTo(boxWidth, accrualTop).stroke();

            total_aportation = common_contingency.value + unemployment.value + professional_formation.value + extra_hours_e.value + extra_hours_ne.value;
            // ----------------------------------------- APORTACIONES ----------------------------------------------------
            pdf
                .font(headingFont)
                .text('II. DEDUCCIONES', firstColumn, deductionTop)
                .fontSize(textSize)
                .text('1. Aportaciones del trabajador a las cotizaciones de la SS y conceptos de recaudación', secondColumn, deductionTop + 12)
                .font(textFont)
                .text(formatMoney(total_aportation), sixthColumn + numberOffset(total_aportation), deductionTop + 12)
                .moveTo(secondColumn, deductionTop + 22).lineTo(sixthColumn - 25, deductionTop + 22).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 25, deductionTop + 22).lineTo(sixthColumn + 25, deductionTop + 22).undash().stroke();

            deductionTop += 5;

            pdf
                .font(textFont)
                .text(formatMoney(common_contingency.value), thirdColumn + numberOffset(common_contingency.value), deductionTop + 24)
                .text("  por un ", fourthColumn, deductionTop + 24)
                .text(formatPercent(common_contingency.percent), fifthColumn + numberOffset(common_contingency.percent), deductionTop + 24)
                .text("  de Contigencias comunes", fourthColumn + 60, deductionTop + 24)
                .text(formatMoney(unemployment.value), thirdColumn + numberOffset(unemployment.value), deductionTop + 36)
                .text("  por un ", fourthColumn, deductionTop + 36)
                .text(formatPercent(unemployment.percent), fifthColumn + numberOffset(unemployment.percent), deductionTop + 36)
                .text("  de Desempleo", fourthColumn + 60, deductionTop + 36)
                .text(formatMoney(professional_formation.value), thirdColumn + numberOffset(professional_formation.value), deductionTop + 48)
                .text("  por un ", fourthColumn, deductionTop + 48)
                .text(formatPercent(professional_formation.percent), fifthColumn + numberOffset(professional_formation.percent), deductionTop + 48)
                .text("  de Fromación profesional", fourthColumn + 60, deductionTop + 48);
            if (extra_hours_e.value && extra_hours_e.value > 0) {
                pdf
                    .text("  por Horas extraordinarias (Estruc.)", fourthColumn, deductionTop + 60)
                    .text(formatMoney(extra_hours_e.value), thirdColumn + numberOffset(extra_hours_e.value), deductionTop + 60);
            } else {
                deductionTop -= 12;
            }
            if (extra_hours_ne.value && extra_hours_ne.value > 0) {
                pdf
                    .text("  por Horas extraordinarias (No Estruc.)", fourthColumn, deductionTop + 72)
                    .text(formatMoney(extra_hours_ne.value), thirdColumn + numberOffset(extra_hours_ne.value), deductionTop + 72);
            } else {
                deductionTop -= 12;
            }

            // ----------------------------------------- IMPUESTOS ----------------------------------------------------
            taxes = taxesD.value + taxesS.value;
            pdf
                .font(headingFont)
                .text("2. Impuestos sobre la renta de personas físicas (I.R.P.F.)", secondColumn, deductionTop + 84)
                .font(textFont)
                //                .text(formatPercent(irpf.percent), sixthColumn - 130 + numberOffset(irpf.percent), deductionTop + 84)
                .text(formatMoney(irpf.value), sixthColumn + numberOffset(irpf.value), deductionTop + 84)
                .moveTo(secondColumn, deductionTop + 94).lineTo(sixthColumn - 25, deductionTop + 94).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 25, deductionTop + 94).lineTo(sixthColumn + 25, deductionTop + 94).undash().stroke();

            deductionTop += 5;

            if (taxesD.value && taxesD.value > 0) {
                pdf
                    .text(formatMoney(taxesD.value), thirdColumn + numberOffset(taxesD.value), deductionTop + 96)
                    .text("  por un ", fourthColumn, deductionTop + 96)
                    .text(formatPercent(taxesD.percent), fifthColumn + numberOffset(taxesD.percent), deductionTop + 96)
                    .text("  de Retribuciones dinerarias", fourthColumn + 60, deductionTop + 96);
            } else {
                deductionTop -= 12;
            }
            if (taxesS.value && taxesS.value > 0) {
                pdf
                    .text(formatMoney(taxesS.value), thirdColumn + numberOffset(taxesS.value), deductionTop + 108)
                    .text("  por un ", fourthColumn, deductionTop + 108)
                    .text(formatPercent(taxesS.percent), fifthColumn + numberOffset(taxesS.percent), deductionTop + 108)
                    .text("  de Retribuciones en especie", fourthColumn + 60, deductionTop + 108);
            } else {
                deductionTop -= 12;
            }

            // ----------------------------------------- ANTICIPOS ----------------------------------------------------
            advancesTOTAL = 0;
            for (var i = 0; i < advances.types.length; i++) {
                type = advances.types[i];
                advancesTOTAL += type.value;
            }

            pdf
                .font(headingFont)
                .text("3. Anticipos", secondColumn, deductionTop + 120 + existingTaxes)
                .font(textFont)
                .text(formatMoney(advancesTOTAL), fifthColumn + numberOffset(advancesTOTAL), deductionTop + 120 + existingTaxes)
                .moveTo(secondColumn, deductionTop + 130 + existingTaxes).lineTo(sixthColumn - 25, deductionTop + 130 + existingTaxes).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 25, deductionTop + 130 + existingTaxes).lineTo(sixthColumn + 25, deductionTop + 130 + existingTaxes).undash().stroke();

            deductionTop += 5;

            // ----------------------------------------- ESPECIES ----------------------------------------------------
            spicesTOTAL = 0;
            for (var i = 0; i < spices.types.length; i++) {
                type = spices.types[i];
                spicesTOTAL += type.value;
            }

            pdf
                .font(headingFont)
                .text("4. Valor de los productos recibidos en especie", secondColumn, deductionTop + 132 + existingTaxes)
                .font(textFont)
                .text(formatAmount(spicesTOTAL), fifthColumn + numberOffset(spicesTOTAL), deductionTop + 132 + existingTaxes)
                .moveTo(secondColumn, deductionTop + 142 + existingTaxes).lineTo(sixthColumn - 25, deductionTop + 142 + existingTaxes).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 25, deductionTop + 142 + existingTaxes).lineTo(sixthColumn + 25, deductionTop + 142 + existingTaxes).undash().stroke();

            deductionTop += 5;
            // ------------------------------------- OTRAS DEDUCCIONES -----------------------------------------------
            pdf
                .font(headingFont)
                .text("5. Otras deducciones", secondColumn, deductionTop + 144 + existingTaxes)
                .font(textFont);

            other_deductionsTOTAL = 0;
            newLine = 12;
            deductionsNumb = 0;

            for (var i = 0; i < other_deductions.types.length; i++) {
                type = other_deductions.types[i];
                other_deductionsTOTAL += type.value;
                deductionsNumb += other_deductions.types.length + 4;
                pdf
                    .text("  por " + type.name, fourthColumn, deductionTop + 144 + newLine * (i + 1))
                    .text(formatMoney(type.value), thirdColumn + numberOffset(type.value), deductionTop + 144 + newLine * (i + 1) + existingTaxes);
            }

            pdf
                .font(textFont)
                .text(formatAmount(other_deductionsTOTAL), fifthColumn + numberOffset(other_deductionsTOTAL), deductionTop + 144 + existingTaxes)
                .moveTo(secondColumn, deductionTop + 154 + existingTaxes).lineTo(sixthColumn - 25, deductionTop + 154 + existingTaxes).dash(1, { space: 2 }).stroke()
                .moveTo(sixthColumn - 25, deductionTop + 154 + existingTaxes).lineTo(sixthColumn + 25, deductionTop + 154 + existingTaxes).undash().stroke();

            // ----------------------------------------- TOTAL A DEDUCIR ------------------------------------------------
            totalAccrualLine = accrualTop + newLine * (deductionsNumb) + 50;

            pdf
                .fontSize(titleSize)
                .font(titleFont)
                .text('A. TOTAL DEVENGADO (página anterior)', totalColumn, deductionTop + totalAccrualLine + existingTaxes)
                .text(formatMoney(payroll.total_accrual), seventhColumn + numberOffset(payroll.total_accrual), deductionTop + totalAccrualLine + existingTaxes)
                .moveTo(totalColumn, deductionTop + totalAccrualLine + 10 + existingTaxes).lineTo(seventhColumn - 25, deductionTop + totalAccrualLine + 10 + existingTaxes).dash(1, { space: 2 }).stroke()
                .moveTo(seventhColumn - 25, deductionTop + totalAccrualLine + 10 + existingTaxes).lineTo(seventhColumn + 35, deductionTop + totalAccrualLine + 10 + existingTaxes).undash().stroke();

            pdf
                .fontSize(titleSize)
                .font(titleFont)
                .text('B. TOTAL DEDUCIR', totalColumn, deductionTop + totalAccrualLine + 17 + existingTaxes)
                .text(formatMoney(payroll.total_deductions), seventhColumn + numberOffset(payroll.total_deductions), deductionTop + totalAccrualLine + 17 + existingTaxes)
                .moveTo(totalColumn, deductionTop + totalAccrualLine + 27 + existingTaxes).lineTo(seventhColumn - 25, deductionTop + totalAccrualLine + 27 + existingTaxes).dash(1, { space: 2 }).stroke()
                .moveTo(seventhColumn - 25, deductionTop + totalAccrualLine + 27 + existingTaxes).lineTo(seventhColumn + 35, deductionTop + totalAccrualLine + 27 + existingTaxes).undash().stroke();

            newSignatureDate(payroll);
            newFooterBox(payroll);
        }
    }

    //Main
    for (let i = 0; i < payrolls.length; i++) {
        const payroll = payrolls[i];
        
        newHearderTittle(payroll);
        newEnterpriseBox(payroll);
        newEmployeeBox(payroll);
        newSettlementBox(payroll);
        newAccrual(payroll);
        newDeductionFirstPage(payroll);
        newSignatureDate(payroll);
        newFooterBox(payroll);
        newDeductionSecondPage(payroll);

        if ((i + 1) != payrolls.length)
            pdf.addPage();
    }

    //End PDF
    pdf.end();

}

module.exports.salaryRecibeCRA = function(payrolls, stream){
  //Initialize pdf object
	var pdf = new PDF({
        size: [595.28, 841.89],
        margins: { // by default, all are 72
            top: 5,
            bottom: 5,
            left: 5,
            right: 5
        }
    });

    pdf.pipe(stream);

    //PDF Styles
    var pdfWidth = pdf.page.width -
        pdf.page.margins.left -
        pdf.page.margins.right;

    //Fonts
    textFooterSize = 7.9;
    textFooterFont = 'Helvetica';

    textSize = 8;
    textFont = 'Helvetica';

    titleTextSize = 8;
    titleTextFont = 'Helvetica-Bold';

    headingSize = 4;
    headingFont = 'Helvetica-Bold';

    titleSize = 10;
    titleFont = 'Helvetica-Bold';

    signature = 585;

    var salary_perceptions_codes = [0001];
    var extra_hours_codes = [0002, 0003];
    var extra_perks_codes = [0004, 0005];
    var spices_salary_codes = [0013, 0014, 0015, 0016, 0017, 0018, 0019, 0020, 0021, 0022, 0023, 0024, 0025, 0026];

    //Aux Methods
    String.prototype.splice = function(idx, rem, str) {
        return this.slice(0, idx) + str + this.slice(idx + Math.abs(rem));
    };
    
    var checkDates = function(accrualType, accrualTypeExpression) {
    	startDate = new Intl.DateTimeFormat('en-GB').format(accrualType.startDate);
        endDate = new Intl.DateTimeFormat('en-GB').format(accrualType.endDate);
        
        var diffDates = parseInt((accrualType.endDate-accrualType.startDate)/1000/60/60/24);
        
        if (null != accrualType.startDate && null != accrualType.endDate && startDate != endDate && diffDates > 1 && diffDates < 28) {
            return accrualTypeExpression + parseDDMMDate(startDate, endDate);
        }
        return accrualTypeExpression;
    }

    var parseDDMMDate = function(startDate, endDate) {
        /*
    	startDay = startDate.getDate();
        startMonth = startDate.getMonth() + 1;

        if (startDay < 10)
            startDay = "0" + startDay;

        if (startMonth < 10)
            startMonth = "0" + startMonth;

        endDay = endDate.getDate();
        endMonth = endDate.getMonth() + 1;

        if (endDay < 10)
            endDay = "0" + endDay;

        if (endMonth < 10)
            endMonth = "0" + endMonth;
        */
    	
    	startDay = startDate.split("\/")[0];
    	startMonth = startDate.split("\/")[1];
    	
    	endDay = endDate.split("\/")[0];
    	endMonth = endDate.split("\/")[1];

        return " ( " + startDay + "/" + startMonth + " - " + endDay + "/" + endMonth + " )";
    }

    var checkAccrualName = function(accrualName) {
        if (accrualName) {
        	if(accrualName == "Prestaciones e indemnizaciones a la Seguridad Social")
        		return "00. PRESTACIONES E INDEMNIZACIONES A LA SS";
        	
            type = accrualName.split(" ")[0];
            if (type == "01")
                return "01 RETRIBUCIONES SALARIALES".splice(2, 0, ". ");

            return accrualName.splice(2, 0, ". ");
        }
        return accrualName;
    }

    var formatFirstPartAddress = function(address) {
        if (address.includes("("))
            return address.split("(")[0];
        return address;
    }

    var formatSecondPartAddress = function(address, province) {
        if (address.includes("(")) {
            zip = address.split("(")[1].split(")")[0];
            city = address.split(")")[1];

            return zip + " " + city + " " + province;
        }
        return province;
    }

    var formatInputData = function(data, parseData) {
        if (data == null || data == undefined) {
            if (parseData == 'string')
                return '';
            else if (parseData == 'number')
                return '';
        } else {
            var typeData = typeof data;
            //POSIBLE IMPLEMENTACION PARA ALGO MAS INTELIGENTE
            if (typeData == 'number' && data == 0) {
                return '';
            } else if (typeData == 'number' && isNaN(data)) {
                return '';
            }
            return data;
        }
    }

    var parseExpression = function(expression) {
    	 if (null != expression && undefined != expression && expression.includes("]"))
            expression = expression.split("]")[1].trim();
    	 if(null != expression && undefined != expression && expression.length > 52)
     		expression = expression.substring(0,52);
        
        return expression.trim();
    }

    var formatAmount = function(amount) {
        const config = {
            minimumFractionDigits: 2
        }

        return (amount && amount != '' && amount != null && amount != 0) ? new Intl.NumberFormat("de-DE", config).format(amount.toFixed(2)) : '';
    }

    var formatPercent = function(percent) {
        if ('string' == typeof percent) {
            if (percent.includes(" %")) {
                percentInt = parseFloat(percent.split(" %")[0].trim());
                return (percentInt && percentInt != '' && percentInt != null && percentInt != 0) ? percentInt.toFixed(2) + " %" : '';
            }
            return percent;
        } else
            return (percent && percent != '' && percent != null && percent != 0) ? percent.toFixed(2) + " %" : '';
    }

    var formatMoney = function(amount) {
        const config = {
            style: "currency",
            currency: "EUR",
            minimumFractionDigits: 2,
            currencyDisplay: "symbol"
        }
        return (amount && amount != '' && amount != null) ? new Intl.NumberFormat("de-DE", config).format(amount.toFixed(2)) : '';
    }

    var numberOffset = function(number) {
        if (number != '' && 'string' != typeof number) {
            var formatNumber = formatAmount(number);
            var split = formatNumber.split(',');
            switch (split[0].length) {
                case 1:
                    return 1;
                case 2:
                    return -4;
                case 3:
                    return -8;
                case 4:
                    return -11;
                case 5:
                    return -15;
                case 6:
                    return -19;
                default:
                    return 0;
            }
        } else {
            var formatNumber = formatPercent(number);
            var split = formatNumber.split('.');
            switch (split[0].length) {
                case 1:
                    return 0;
                case 2:
                    return -4;
                case 3:
                    return -8;
                case 4:
                    return -11;
                case 5:
                    return -15;
                case 6:
                    return -19;
                default:
                    return 0;
            }
        }
    }

    var parseDeductionName = function(deductionName) {
        switch (deductionName) {
            case "CGC":
                return "Contingencias Comunes";
            case "Contingencias Comunes":
                return "Contingencias Comunes";
            case "DESMPL":
                return "Desempleo";
            case "Desempleo":
                return "Desempleo";
            case "FP":
                return "Formación Profesional";
            case "Formación Profesional":
                return "Formación Profesional";
            case "Especie":
                return "Retribuciones en especie";
            case "Dinerario":
                return "Retribuciones dinerarias";
            case "IRPF":
                return "IRPF";
            case "Anticipo":
                return "Anticipo";
            case "Embargos":
                return "Embargos";
            default:
                return deductionName;
        }
    }

    var parseDate = function(date) {
        return new Intl.DateTimeFormat('en-GB').format(date);
    }

    var formatDate = function(date) {
        var split = date.split('/');
        return split[0] + ' de ' + getStrMonth(split[1]) + ' de ' + split[2];
    }

    var getStrMonth = function(numberMonth) {
        switch (numberMonth) {
            case '1':
                return 'enero';
            case '2':
                return 'febrero';
            case '3':
                return 'marzo';
            case '4':
                return 'abril';
            case '5':
                return 'mayo';
            case '6':
                return 'junio';
            case '7':
                return 'julio';
            case '8':
                return 'agosto';
            case '9':
                return 'septiembre';
            case '10':
                return 'octubre';
            case '11':
                return 'noviembre';
            case '12':
                return 'diciembre';
            default:
                return 'error';
        }
    }


    var addMiddleBar = function(value) {
        if (null == value || value == 0)
            return "";
        else
            return "  |";
    }


    //Main Methods
    var newHearderTittle = function(payroll) {
        //PDF settings
        pdf.lineWidth(1);

        firstColumn = 100;
        secondColumn = 400;

        if (payroll.logo != undefined) {
            pdf
                .image(payroll.logo, firstColumn, 10, { scale: 0.1 });
        }

        pdf
            .font(titleFont)
            .fontSize(titleSize)
            .text('RECIBO DE SALARIO', secondColumn, 12);

    }

    var newEnterpriseBox = function(payroll) {
        enterpriseTop = 65;
        enterpriseHeight = enterpriseTop + 84;
        topLeftCorner = 10;
        boxWidth = 290;

        firstColumn = 25;
        secondColumn = 40;
        thirdColumn = 160;
        fourthColumn = 175;

        textSize = 8;
        textFont = 'Helvetica';

        headingSize = 6;
        headingFont = 'Helvetica-Bold';

        // -----------------------------------------------------------------------------------------------------------------
        // -------------------------------------------- ENTERPRISE ---------------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------
        pdf
            .moveTo(topLeftCorner, enterpriseTop).lineTo(boxWidth, enterpriseTop).stroke() //Horizontal t0p line

        .font(textFont)
            .fontSize(headingSize)
            .text('EMPRESA', firstColumn, enterpriseTop + 6)
            .font(headingFont)
            .fontSize(textSize)
            .text(formatInputData(payroll.enterprise.name, 'string'), secondColumn, enterpriseTop + 16)
            .moveTo(topLeftCorner, enterpriseTop + 28).lineTo(boxWidth, enterpriseTop + 28).stroke()

        .font(textFont)
            .fontSize(headingSize)
            .text('DOMICILIO', firstColumn, enterpriseTop + 34)
            .font(textFont)
            .fontSize(textSize)
            .text(formatInputData(payroll.enterprise.address, 'string') + ", " + formatInputData(payroll.enterprise.city.toUpperCase(), 'string'), secondColumn, enterpriseTop + 44)
            .moveTo(topLeftCorner, enterpriseTop + 56).lineTo(boxWidth, enterpriseTop + 56).stroke()

        .font(textFont)
            .fontSize(headingSize)
            .text('CIF', firstColumn, enterpriseTop + 62)
            .text('CCC', thirdColumn, enterpriseTop + 62)
            .font(textFont)
            .fontSize(textSize)
            .text(formatInputData(payroll.enterprise.cif, 'string'), secondColumn, enterpriseTop + 72)
            .text(formatInputData(payroll.enterprise.ccc, 'number'), fourthColumn, enterpriseTop + 72)
            .moveTo(thirdColumn - 15, enterpriseTop + 56).lineTo(thirdColumn - 15, enterpriseTop + 84).stroke() //Vertical right line

        .moveTo(topLeftCorner, enterpriseTop).lineTo(topLeftCorner, enterpriseHeight).stroke() //Vertical left line
            .moveTo(boxWidth, enterpriseTop).lineTo(boxWidth, enterpriseHeight).stroke() //Vertical right line
            .moveTo(topLeftCorner, enterpriseHeight).lineTo(boxWidth, enterpriseHeight).stroke(); //Horizontal bottom line

    }

    var newEmployeeBox = function(payroll) {
        employeeTop = 30;
        employeeHeight = enterpriseTop + 50;
        topLeftCorner = 295;
        boxWidth = 290;

        firstColumn = topLeftCorner + 15;
        secondColumn = topLeftCorner + 30;
        thirdColumn = topLeftCorner + 90;
        fourthColumn = topLeftCorner + 105;
        fifthColumn = topLeftCorner + 180;
        sixthColumn = topLeftCorner + 195;

        textSize = 8;
        textFont = 'Helvetica';

        headingSize = 6;
        headingFont = 'Helvetica-Bold';

        // -----------------------------------------------------------------------------------------------------------------
        // -------------------------------------------- EMPLOYEE -----------------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------
        pdf
            .moveTo(topLeftCorner, employeeTop).lineTo(topLeftCorner + boxWidth, employeeTop).stroke() //Horizontal t0p line

        .font(textFont)
            .fontSize(headingSize)
            .text('TRABAJADOR/A', firstColumn, employeeTop + 6)
            .font(headingFont)
            .fontSize(textSize)
            .text(formatInputData(payroll.employee.fullname, 'string'), secondColumn, employeeTop + 16)
            .moveTo(topLeftCorner, employeeTop + 28).lineTo(topLeftCorner + boxWidth, employeeTop + 28).stroke()

        .font(textFont)
            .fontSize(headingSize)
            .text('NIF', firstColumn, employeeTop + 34)
            .text('ANTIGÜEDAD', thirdColumn, employeeTop + 34)
            .text('Nº S.S.', fifthColumn, employeeTop + 34)
            .font(textFont)
            .fontSize(textSize)
            .text(formatInputData(payroll.employee.nif, 'string'), secondColumn, employeeTop + 44)
            .text(formatInputData(payroll.employee.ss, 'number'), sixthColumn, employeeTop + 44)
            .text(parseDate(payroll.employee.seniority_date), fourthColumn, employeeTop + 44)

        .moveTo(thirdColumn - 15, employeeTop + 28).lineTo(thirdColumn - 15, employeeTop + 56).stroke() //Vertical center line
            .moveTo(fifthColumn - 15, employeeTop + 28).lineTo(fifthColumn - 15, employeeTop + 56).stroke() //Vertical center line
            .moveTo(topLeftCorner, employeeTop + 56).lineTo(topLeftCorner + boxWidth, employeeTop + 56).stroke()

        .font(textFont)
            .fontSize(headingSize)
            .text('G. COTIZ.', firstColumn, employeeTop + 62)
            .text('TC2', thirdColumn - 20, employeeTop + 62)
            .text('CETOGRÍA', fifthColumn - 50, employeeTop + 62)
            .font(textFont)
            .fontSize(textSize)
            .text(formatInputData(payroll.employee.quote_group, 'string'), secondColumn, employeeTop + 72)
            .text(formatInputData(payroll.employee.contract_type, 'string'), fourthColumn - 20, employeeTop + 72)
            .text(formatInputData(payroll.employee.professional_group, 'string'), sixthColumn - 50, employeeTop + 72)

        .moveTo(thirdColumn - 25, employeeTop + 56).lineTo(thirdColumn - 25, employeeTop + 84).stroke() //Vertical center line
            .moveTo(fifthColumn - 60, employeeTop + 56).lineTo(fifthColumn - 60, employeeTop + 84).stroke() //Vertical center line
            .moveTo(topLeftCorner, employeeTop + 84).lineTo(topLeftCorner + boxWidth, employeeTop + 84).stroke()
            .moveTo(topLeftCorner, employeeTop).lineTo(topLeftCorner, employeeHeight).stroke()
            .moveTo(topLeftCorner + boxWidth, employeeTop).lineTo(topLeftCorner + boxWidth, employeeHeight).stroke();
    }

    var newSettlementBox = function(payroll) {
        settlementTop = 119;
        settlementHeight = 30;
        topLeftCorner = 295;
        boxWidth = 290;

        firstColumn = topLeftCorner + 15;
        secondColumn = topLeftCorner + 30;
        thirdColumn = topLeftCorner + 210;
        fourthColumn = topLeftCorner + 225;

        textSize = 8;
        textFont = 'Helvetica';

        headingSize = 6;
        headingFont = 'Helvetica-Bold';

        // -----------------------------------------------------------------------------------------------------------------
        // --------------------------------------------- SETTLEMENT --------------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------

        pdf
            .moveTo(topLeftCorner, settlementTop).lineTo(topLeftCorner + boxWidth, settlementTop).stroke() //Horizontal t0p line
            .font(textFont)
            .fontSize(headingSize)
            .text('PERIODO DE LIQUIDACIÓN', firstColumn, settlementTop + 6).font(textFont)
            .text('TOTAL DÍAS', thirdColumn, settlementTop + 6).font(textFont)
            .font(textFont)
            .fontSize(textSize)
            .text(formatInputData(payroll.settlement.start_date, 'string') + '  -  ' + formatInputData(payroll.settlement.end_date, 'string'), secondColumn, settlementTop + 16)
            .text(formatInputData(payroll.settlement.total_days, 'number'), fourthColumn, settlementTop + 16)

        .moveTo(thirdColumn - 15, settlementTop).lineTo(thirdColumn - 15, settlementTop + settlementHeight).stroke() //Vertical center line

        .moveTo(topLeftCorner, settlementTop).lineTo(topLeftCorner, settlementTop + settlementHeight).stroke() //Vertical right line
            .moveTo(topLeftCorner + boxWidth, settlementTop).lineTo(topLeftCorner + boxWidth, settlementTop + settlementHeight).stroke() //Vertical right line
            .moveTo(topLeftCorner, settlementTop + settlementHeight).lineTo(topLeftCorner + boxWidth, settlementTop + settlementHeight).stroke();
    }

    var newCentralBox = function(payroll) {
        centralBoxTop = 155;
        centralBoxHeight = 670;
        topLeftCorner = 10;
        boxWidth = 575;

        // -----------------------------------------------------------------------------------------------------------------
        // --------------------------------------------- CENTRAL BOX -------------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------

        pdf
            .moveTo(topLeftCorner, centralBoxTop).lineTo(topLeftCorner + boxWidth, centralBoxTop).stroke() //Horizontal t0p line
            .moveTo(topLeftCorner, centralBoxTop).lineTo(topLeftCorner, centralBoxTop + centralBoxHeight).stroke()
            .moveTo(topLeftCorner + boxWidth, centralBoxTop).lineTo(topLeftCorner + boxWidth, centralBoxTop + centralBoxHeight).stroke()
            .moveTo(topLeftCorner, centralBoxTop + centralBoxHeight).lineTo(topLeftCorner + boxWidth, centralBoxTop + centralBoxHeight).stroke();

    }

    newCentralInnerBox = function(payroll) {
        centralBoxTop = 165;
        centralBoxHeight = 380;
        topLeftCorner = 22;
        boxWidth = 550;

        firstColumn = 40;
        secondColumn = 210;
        thirdColumn = 420;
        fourthColumn = 495;

        // -----------------------------------------------------------------------------------------------------------------
        // --------------------------------------------- CENTRAL BOX -------------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------

        pdf
            .moveTo(topLeftCorner, centralBoxTop).lineTo(topLeftCorner + boxWidth, centralBoxTop).stroke() //Horizontal t0p line
            .moveTo(topLeftCorner, centralBoxTop).lineTo(topLeftCorner, centralBoxTop + centralBoxHeight).stroke()
            .moveTo(topLeftCorner + boxWidth, centralBoxTop).lineTo(topLeftCorner + boxWidth, centralBoxTop + centralBoxHeight).stroke()
            .moveTo(topLeftCorner, centralBoxTop + centralBoxHeight).lineTo(topLeftCorner + boxWidth, centralBoxTop + centralBoxHeight).stroke()
            .text('CUANTÍA', firstColumn, centralBoxTop + 4)
            .text('CONCEPTO', secondColumn, centralBoxTop + 4)
            .text('DEVENGO', thirdColumn, centralBoxTop + 4)
            .text('DEDUCCIONES', fourthColumn, centralBoxTop + 4)
            .moveTo(topLeftCorner, centralBoxTop + 15).lineTo(topLeftCorner + boxWidth, centralBoxTop + 15).stroke() //Horizontal t0p line;

        .moveTo(firstColumn + 50, centralBoxTop).lineTo(firstColumn + 50, centralBoxTop + centralBoxHeight).stroke()
            .moveTo(thirdColumn - 20, centralBoxTop).lineTo(thirdColumn - 20, centralBoxTop + centralBoxHeight).stroke()
            .moveTo(thirdColumn + 60, centralBoxTop).lineTo(thirdColumn + 60, centralBoxTop + centralBoxHeight).stroke();



    }

    var newFooterBox = function(payroll) {
        padding = 13;
        footerTop = 550;
        footerHeight = 200;
        topLeftCorner = 10 + padding;
        boxWidth = 548;

        titleFirstColumn = topLeftCorner + 40;
        titleSecondColumn = topLeftCorner + 125;
        titleThirdColumn = topLeftCorner + 240;
        titleFourthColumn = topLeftCorner + 430;

        firstColumn = topLeftCorner + 15;
        secondColumn = topLeftCorner + 40;
        thirdColumn = topLeftCorner + 110;
        fourthColumn = topLeftCorner + 120;
        fifthColumn = topLeftCorner + 225;
        sixthColumn = topLeftCorner + 250;
        seventColumn = topLeftCorner + 390;
        eigthColumn = topLeftCorner + 407;
        ninColumn = topLeftCorner + 472;
        tenthColumn = topLeftCorner + 507;

        textSize = 8;
        textFont = 'Helvetica';

        headingSize = 6;
        headingFont = 'Helvetica-Bold';

        // -----------------------------------------------------------------------------------------------------------------
        // -------------------------------------------- FOOTER -------------------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------
        pdf
            .font(textFont)
            .fontSize(textSize)
            .text('BASES', titleFirstColumn, footerTop)
            .text('AP. EMPRESA', titleSecondColumn, footerTop)
            .text('DESGLOSE', titleThirdColumn, footerTop)
            .text('TOTALES', titleFourthColumn, footerTop);

        pdf
            .text('1. Cont. Comunes', firstColumn, footerTop + 16)
            .text('Remuneración Total', fifthColumn, footerTop + 16)
            .text('Devengos', seventColumn, footerTop + 16)
            .text('Deducciones', ninColumn, footerTop + 16)
            .moveTo(topLeftCorner, footerTop + 10).lineTo(topLeftCorner + 320, footerTop + 10).stroke()
            .moveTo(seventColumn - 15, footerTop + 10).lineTo(topLeftCorner + boxWidth, footerTop + 10).stroke();

        pdf
            .text(formatMoney(payroll.footer_ss_quotation.common_contingency.base), secondColumn + numberOffset(payroll.footer_ss_quotation.common_contingency.base), footerTop + 26)
            .text(formatMoney(payroll.footer_ss_quotation.common_contingency.monthly_remuneration), sixthColumn + numberOffset(payroll.footer_ss_quotation.common_contingency.monthly_remuneration), footerTop + 26)
            .text(formatMoney(payroll.total_accrual), eigthColumn + numberOffset(payroll.total_accrual), footerTop + 26)
            .text(formatMoney(payroll.total_deductions), tenthColumn + numberOffset(payroll.total_deductions), footerTop + 26)
            .text(formatPercent(payroll.footer_ss_quotation.common_contingency.type_percent) + addMiddleBar(payroll.footer_ss_quotation.common_contingency.type_percent), fourthColumn + numberOffset(payroll.footer_ss_quotation.common_contingency.type_percent), footerTop + 34)
            .text(formatMoney(payroll.footer_ss_quotation.common_contingency.company_input), fourthColumn + 45 + numberOffset(payroll.footer_ss_quotation.common_contingency.company_input), footerTop + 34)

        .moveTo(fourthColumn + 80, footerTop + 38).lineTo(topLeftCorner + 320, footerTop + 38).stroke()
            .moveTo(seventColumn - 15, footerTop + 38).lineTo(topLeftCorner + boxWidth, footerTop + 38).stroke()

        .text('Prorrata Pagas Extr.', fifthColumn, footerTop + 44)
            .text('Líquido a percibir', ninColumn, footerTop + 44)
            .text(formatMoney(payroll.footer_ss_quotation.common_contingency.extraordinary_pay_packet), sixthColumn + numberOffset(payroll.footer_ss_quotation.common_contingency.extraordinary_pay_packet), footerTop + 54)
            .text(formatMoney(payroll.liquid_perceive), tenthColumn + numberOffset(payroll.liquid_perceive), footerTop + 54)

        .moveTo(topLeftCorner, footerTop + 66).lineTo(fifthColumn + 95, footerTop + 66).stroke()
            .moveTo(ninColumn - 15, footerTop + 66).lineTo(topLeftCorner + boxWidth, footerTop + 66).stroke();

        pdf
            .text('2. Cont. Profesionales', firstColumn, footerTop + 74)

        .text('AT/EP', thirdColumn, footerTop + 74)
            .text(formatMoney(payroll.footer_ss_quotation.professional_contingency.base), secondColumn + numberOffset(payroll.footer_ss_quotation.professional_contingency.base), footerTop + 84)
            .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_at) + addMiddleBar(payroll.footer_ss_quotation.professional_contingency.type_percent_at), fourthColumn + numberOffset(payroll.footer_ss_quotation.professional_contingency.type_percent_at), footerTop + 84)
            .text(formatMoney(payroll.footer_ss_quotation.professional_contingency.company_input_at), fourthColumn + 45 + numberOffset(payroll.footer_ss_quotation.professional_contingency.company_input_at), footerTop + 84)
            .moveTo(thirdColumn - 10, footerTop + 94).lineTo(fourthColumn + 80, footerTop + 94).stroke()

        .text('Desempleo', thirdColumn, footerTop + 102)
            .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_unemployment) + addMiddleBar(payroll.footer_ss_quotation.professional_contingency.type_percent_unemployment), fourthColumn + numberOffset(payroll.footer_ss_quotation.professional_contingency.type_percent_unemployment), footerTop + 112)
            .text(formatMoney(payroll.footer_ss_quotation.professional_contingency.company_input_unemployment), fourthColumn + 45 + numberOffset(payroll.footer_ss_quotation.professional_contingency.company_input_unemployment), footerTop + 112)
            .moveTo(thirdColumn - 10, footerTop + 122).lineTo(fourthColumn + 80, footerTop + 122).stroke()

        .text('Formación profesional', thirdColumn, footerTop + 130)
            .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_professional_formation) + addMiddleBar(payroll.footer_ss_quotation.professional_contingency.type_percent_professional_formation), fourthColumn + numberOffset(payroll.footer_ss_quotation.professional_contingency.type_percent_professional_formation), footerTop + 140)
            .text(formatMoney(payroll.footer_ss_quotation.professional_contingency.company_input_professional_formation), fourthColumn + 45 + numberOffset(payroll.footer_ss_quotation.professional_contingency.company_input_professional_formation), footerTop + 140)
            .moveTo(thirdColumn - 10, footerTop + 150).lineTo(fourthColumn + 80, footerTop + 150).stroke()

        .text('FOGASA', thirdColumn, footerTop + 158)
            .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_salary_warranty) + addMiddleBar(payroll.footer_ss_quotation.professional_contingency.type_percent_salary_warranty), fourthColumn + numberOffset(payroll.footer_ss_quotation.professional_contingency.type_percent_salary_warranty), footerTop + 168)
            .text(formatMoney(payroll.footer_ss_quotation.professional_contingency.company_input_salary_warranty), fourthColumn + 45 + numberOffset(payroll.footer_ss_quotation.professional_contingency.company_input_salary_warranty), footerTop + 168);

        pdf
            .moveTo(topLeftCorner, footerTop + 178).lineTo(topLeftCorner + 320, footerTop + 178).stroke()
            .text('3. Horas Extras', firstColumn, footerTop + 188)
            .text('No Estructurales', thirdColumn, footerTop + 188)
            .text('Base HE (NE)', fifthColumn, footerTop + 188)
            .text(formatMoney(payroll.footer_ss_quotation.aditional_quotation.company_input_overwhelming_force), fourthColumn + numberOffset(payroll.footer_ss_quotation.aditional_quotation.company_input_overwhelming_force), footerTop + 198)
            .text(formatMoney(payroll.footer_ss_quotation.aditional_quotation.base_overwhelming_force), sixthColumn + numberOffset(payroll.footer_ss_quotation.aditional_quotation.base_overwhelming_force), footerTop + 198)

        .moveTo(thirdColumn - 10, footerTop + 208).lineTo(topLeftCorner + 320, footerTop + 208).stroke()
            .text('Estruc./Fuerza Mayor', thirdColumn, footerTop + 218)
            .text('Base HE (E/FM)', fifthColumn, footerTop + 218)
            .text(formatMoney(payroll.footer_ss_quotation.aditional_quotation.company_input_non_structural), fourthColumn + numberOffset(payroll.footer_ss_quotation.aditional_quotation.company_input_non_structural), footerTop + 228)
            .text(formatMoney(payroll.footer_ss_quotation.aditional_quotation.base_non_structural), sixthColumn + numberOffset(payroll.footer_ss_quotation.aditional_quotation.base_non_structural), footerTop + 228);

        pdf
            .moveTo(topLeftCorner, footerTop + 238).lineTo(topLeftCorner + 320, footerTop + 238).stroke()
            .text('4. IRPF', firstColumn, footerTop + 248)
            .text(formatAmount(payroll.footer_ss_quotation.base_irpf), secondColumn + numberOffset(payroll.footer_ss_quotation.base_irpf), footerTop + 258)
            .moveTo(topLeftCorner, footerTop + 268).lineTo(thirdColumn - 10, footerTop + 268).stroke();

        // Vertical lines
        pdf
            .moveTo(topLeftCorner, footerTop + 10).lineTo(topLeftCorner, footerTop + 268).stroke()
            .moveTo(thirdColumn - 10, footerTop + 10).lineTo(thirdColumn - 10, footerTop + 268).stroke()
            .moveTo(fourthColumn + 80, footerTop + 10).lineTo(fourthColumn + 80, footerTop + 238).stroke()
            .moveTo(fifthColumn + 95, footerTop + 10).lineTo(fifthColumn + 95, footerTop + 66).stroke()
            .moveTo(fifthColumn + 95, footerTop + 178).lineTo(fifthColumn + 95, footerTop + 238).stroke()

        .moveTo(seventColumn - 15, footerTop + 10).lineTo(seventColumn - 15, footerTop + 38).stroke()
            .moveTo(ninColumn - 15, footerTop + 10).lineTo(ninColumn - 15, footerTop + 66).stroke()
            .moveTo(topLeftCorner + boxWidth, footerTop + 10).lineTo(topLeftCorner + boxWidth, footerTop + 66).stroke();
    }

    var newSignatureDate = function(payroll) {
        // -----------------------------------------------------------------------------------------------------------------
        // ------------------------------------------ SIGNATURE / DATE -----------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------
        signatureTop = 640;
        firstColumn = 350;
        secondColumn = 400;

        start_date = formatDate(payroll.settlement.start_date);
        end_date = formatDate(payroll.settlement.end_date);

        pdf
            .font(textFooterFont)
            .fontSize(textFooterSize)
            .text('Firma y sello de la empresa', firstColumn, signatureTop)
            .image(payroll.signature_logo, firstColumn + 10, signatureTop + 20, { scale: 0.06 })

        .text(`RECIBÍ (${end_date}) :`, secondColumn, signatureTop + 85)
            .fontSize(textFooterSize)
            .text(formatInputData(payroll.employee.fullname, 'string'), secondColumn, signatureTop + 160);

    }

    var newAccrual = function(payroll) {
        // -----------------------------------------------------------------------------------------------------------------
        // --------------------------------------------- ACCRUAL -----------------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------
        accrualTop = 190;

        firstColumn = 70;
        secondColumn = 110;
        thirdColumn = 430;
        quarterColumn = 520;

        for (i = 0; i < payroll.accruals.length; i++) {
            accrual = payroll.accruals[i];
            if (accrual.types.length > 0) {
                pdf
                    .font(titleFont)
                    .text(checkAccrualName(accrual.accrual_name), secondColumn, accrualTop);

                accrualTop += 12;

                for (j = 0; j < accrual.types.length; j++) {
                    type = accrual.types[j];
                    pdf
                        .font(textFont)
                        .text(checkDates(type, parseExpression(type.type_expression)), secondColumn + 15, accrualTop)
                        .text(formatMoney(type.type_value), thirdColumn + numberOffset(type.type_value), accrualTop);

                    accrualTop += 12;
                }
            }
        }
    }

    var newDeduction = function(payroll) {
        // -----------------------------------------------------------------------------------------------------------------
        // --------------------------------------------- DEDUCTION ---------------------------------------------------------
        // -----------------------------------------------------------------------------------------------------------------
        deductionTop = accrualTop + 12;
        firstColumn = 50;
        secondColumn = 110;
        quarterColumn = 530;

        totalIRPF = 0;
        //Get total IRPF
        for (i = 0; i < payroll.payments.length; i++) {
            payment = payroll.payments[i];
            if (payment.amount != 0 && payment.code == 13) {
                totalIRPF += payment.amount;
            }
        }

        for (i = 0; i < payroll.deductions.length; i++) {
            deduction = payroll.deductions[i];
            if (deduction.value != 0) {
                if (deduction.name == "Especie" || deduction.name == "Dinerario")
                    continue;
                if (deduction.name == "IRPF") {
                    totalIRPF += deduction.amount;
                    deduction.amount = totalIRPF;
                }

                pdf
                    .font(textFont);
                
                if (deduction.percent && deduction.percent.length < 10) {
                    pdf
                        .text(formatPercent(deduction.percent), firstColumn + numberOffset(deduction.percent), deductionTop);
                }
                
                if(deduction.name){
//                	console.log("Name : " +  deduction.name + " -> Type name : " + deduction.type_name);
                	pdf
                    .text(parseDeductionName(deduction.name), secondColumn, deductionTop)
                    .text(formatMoney(deduction.amount), quarterColumn + numberOffset(deduction.amount), deductionTop);
                }else{
//                	console.log("Name : " +  deduction.name + " -> Type name : " + deduction.type_name);
                	pdf
                    .text(parseDeductionName(deduction.type_name), secondColumn, deductionTop)
                    .text(formatMoney(deduction.amount), quarterColumn + numberOffset(deduction.amount), deductionTop);
                }
                
                deductionTop += 12;

            }
        }
    }

    //Main
    for (let i = 0; i < payrolls.length; i++) {
        const payroll = payrolls[i];
	    newHearderTittle(payroll);
	    newEnterpriseBox(payroll);
	    newEmployeeBox(payroll);
	    newSettlementBox(payroll);
	    newCentralBox(payroll);
	    newCentralInnerBox(payroll);
	    newAccrual(payroll);
	    newDeduction(payroll);
	    newSignatureDate(payroll);
	    newFooterBox(payroll);
	    
	    if ((i + 1) != payrolls.length)
            pdf.addPage();
    }

    //End PDF
    pdf.end();

}

module.exports.salaryRecibe = function(payroll, stream){
  //Initialize pdf object
  var pdf = new(PDF);
  pdf.pipe(stream);

  //PDF Styles
  var pdfWidth = pdf.page.width
		 - pdf.page.margins.left
     - pdf.page.margins.right
	;

  pdf.page.width = pdf.page.width + 60; //Ancho para que no haya salto de linea
  pdf.page.margins = {t0p: 70, bottom: 10, left: 72, right: 72}; //Margenes del documento

  //Variables y constants
	textSize = 6;
	textFont = 'Helvetica';

	titleSize = 8;
	titleFont = 'Helvetica-Bold';

	headingSize = 10;
	headingFont = 'Helvetica-Bold';

  paddingTop = 4;     //Padding-t0p line / text, text / text
  paddingLeft1 = 8;   //Padding-left first text
  paddingLeft2 = 16;  //Padding-left second text
  paddingLeft3 = 24;  //Padding-left forth text
  width = (pdfWidth-30) * 1/2;


  //Aux Methods
  var formatInputData = function (data, parseData){
    if(data == null || data == undefined){
      if(parseData == 'string')
        return '';
      else if(parseData == 'number')
        return '';
    }else{
      var typeData = typeof data;
      //POSIBLE IMPLEMENTACION PARA ALGO MAS INTELIGENTE
      if(typeData == 'number' && data == 0){
        return '';
      }
      return data;
    }
  }

  var formatPercent = function(percent) {
	  if('string' == typeof percent)
	  	return percent;
	  else
	  	return (percent && percent != '' && percent != null && percent != 0) ? percent.toFixed(2)+" %" : '';
  }
  
  var formatAmount = function(amount) {
	  return (amount && amount != '' && amount != null && amount != 0 && 'string' != typeof amount) ? amount.toFixed(2) : '';
  }

  var numberOffset = function(number) {
    if(number != '' && 'string' != typeof number){
      var formatNumber = formatAmount(number);
      var split = formatNumber.split('.');
      switch (split[0].length) {
        case 1:
            return 27;
        case 2:
            return 22;
        case 3:
            return 18;
        case 4:
            return 13;
        case 5:
            return 9;
        case 6:
            return 5;
        default:
            return 0;
      }
    }else{
      return 0;
    }
  }

  var formatDate = function(date) {
		var split = date.split('/');
    return split[0] + ' de ' + getStrMonth(split[1]) + ' de ' + split[2];
	}

  var getStrMonth = function (numberMonth) {
    switch (numberMonth) {
      case '1':
          return 'enero';
      case '2':
          return 'febrero';
      case '3':
          return 'marzo';
      case '4':
          return 'abril';
      case '5':
          return 'mayo';
      case '6':
          return 'junio';
      case '7':
          return 'julio';
      case '8':
          return 'agosto';
      case '9':
          return 'septiembre';
      case '10':
          return 'octubre';
      case '11':
          return 'noviembre';
      case '12':
          return 'diciembre';
      default:
          return 'error';
    }
  }


  //Main Methods
  var newHearderTittle = function(){
    //PDF drawing position using for lines, starting on the t0p of the page
    y = pdf.y;
  	x = pdf.x-50;
    originalX = pdf.x-50;
    originalY = pdf.y;
  	pdf.lineWidth(0.1);

    if(payroll.logo != undefined){
      pdf
        .image(payroll.logo, x+95, 20, {scale: 0.06})
        .font(titleFont)
    		.fontSize(headingSize)
        .text('RECIBO DE SALARIO', x+370, 20)
        .moveDown(1);
    }else{
      //Start drawing PDF
      pdf
    	  .font(titleFont)
    		.fontSize(headingSize)
    		.text('RECIBO DE SALARIO', x+80, 35)
    		.moveDown(1);
    }

  }

  var newEnterpriseBox = function(){
    if(payroll.logo == undefined)
      y = pdf.y;
    else
      y = pdf.y + 15;

    oy = y;
    centerBoxText = 90;
    rightEnterpriseX = 72 + width;      //Position X for right line enterprise box
    sizVertivalLineEE = 10;             //Size vertical line for enterprise, employee box

    // -----------------------------------------------------------------------------------------------------------------
    // -------------------------------------------- ENTERPRISE ---------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    pdf
      .moveTo(x, y).lineTo(rightEnterpriseX, y).stroke()      //Horizontal t0p line

      .font(textFont)
      .fontSize(textSize)
      .text('EMPRESA', x + paddingLeft1, y = y + paddingTop)
      .font(headingFont)
      .fontSize(titleSize)
      .text(formatInputData(payroll.enterprise.name, 'string'), x + centerBoxText, y = y + paddingTop)
      .moveTo(x, y = y + paddingTop + 10).lineTo(rightEnterpriseX, y).stroke()

      .font(textFont)
      .fontSize(textSize)
      .text('DOMICILIO', x + paddingLeft1, y = y + paddingTop)
      .fontSize(titleSize)
      .text(formatInputData(payroll.enterprise.address, 'string') + ", " + formatInputData(payroll.enterprise.locality, 'string'), x + paddingLeft3, y = y + paddingTop + 8)
      .moveTo(x, y = y + paddingTop + 15).lineTo(rightEnterpriseX, y).stroke()

      .font(textFont)
      .fontSize(textSize)
      .text('CIF', x + paddingLeft1, y = y + paddingTop).font(textFont)
      .text('CCC', x + paddingLeft1 + 130, y).font(textFont)
      .fontSize(titleSize)
      .text(formatInputData(payroll.enterprise.cif, 'string'), x + paddingLeft3 + 10, y = y + paddingTop + 8)
      .text(formatInputData(payroll.enterprise.ccc, 'number'), x + paddingLeft3 + 145, y)

      .moveTo(x, oy).lineTo(x, y + sizVertivalLineEE + 2).stroke()                                     //Vertical left line
      .moveTo(x + 130, y - paddingTop*2 - 8).lineTo(x + 130, y + sizVertivalLineEE + 2).stroke()       //Vertical center line
  		.moveTo(rightEnterpriseX, oy).lineTo(rightEnterpriseX, y + sizVertivalLineEE + 2).stroke()       //Vertical right line
      .moveTo(x, y + sizVertivalLineEE + 2).lineTo(rightEnterpriseX, y + sizVertivalLineEE + 2).stroke();  //Horizontal bottom line

  }

  var newEmployeeBox = function(){
    // -----------------------------------------------------------------------------------------------------------------
    // -------------------------------------------- EMPLOYEE -----------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    left = 8;
    leftEmployeeX = 72 + width + left + 5 ;   //Position X for left line employee box
    employeeY = 44;                           //Start y for employee box
    centerBoxText = 80;
    maxRightWidth = x + pdfWidth + 100; //Position X for right line employee, salary, footer box

    pdf
      .moveTo(leftEmployeeX - left, 35).lineTo(maxRightWidth, 35).stroke()   //Horizontal t0p line

      .font(textFont)
      .fontSize(textSize)
      .text('TRABAJADOR/A', leftEmployeeX, y = employeeY)
      .font(headingFont)
      .fontSize(titleSize)
      .text(formatInputData(payroll.employee.fullname, 'string'), leftEmployeeX + centerBoxText, y = y + paddingTop)
      .moveTo(leftEmployeeX - left, y = y + paddingTop + 8).lineTo(maxRightWidth, y).stroke()

      .font(textFont)
      .fontSize(textSize)
      .text('NIF', leftEmployeeX, y = y + paddingTop).font(textFont)
      .text('Nº S.S.',leftEmployeeX + 150, y).font(textFont)
      .fontSize(titleSize)
      .text(formatInputData(payroll.employee.nif, 'string'), leftEmployeeX + paddingLeft3 + 10, y = y + paddingTop + 3)
      .text(formatInputData(payroll.employee.ss, 'number'), leftEmployeeX + paddingLeft3 + 155, y)
      .moveTo(leftEmployeeX - left, y = y + paddingTop + 8).lineTo(maxRightWidth, y).stroke()
      .moveTo(leftEmployeeX + 130, y - paddingTop*2 - 15).lineTo(leftEmployeeX + 130, y).stroke()       //Vertical center line

      .font(textFont)
      .fontSize(textSize)
      .text('G. COTIZ.', leftEmployeeX, y = y + paddingTop).font(textFont)
      .text('GRUPO PROFESIONAL',leftEmployeeX + 50, y).font(textFont)
      .text('ANTIGÜEDAD',leftEmployeeX + 220, y).font(textFont)
      .fontSize(titleSize)
      .text(formatInputData(payroll.employee.quote_group, 'string'), leftEmployeeX + paddingLeft2, y = y + paddingTop + 5)
      .text(formatInputData(payroll.employee.professional_group, 'string'), leftEmployeeX + paddingLeft3 + 90, y)
      .text(formatInputData(payroll.employee.seniority_date, 'string'), leftEmployeeX + paddingLeft3 + 210, y)
      .moveTo(leftEmployeeX - left, y = y + paddingTop + 8).lineTo(maxRightWidth, y).stroke()
      .moveTo(leftEmployeeX + 40, y - paddingTop*2 - 17).lineTo(leftEmployeeX + 40, y).stroke()
      .moveTo(leftEmployeeX + 210, y - paddingTop*2 - 17).lineTo(leftEmployeeX + 210, y).stroke()

      .moveTo(leftEmployeeX - left, 35).lineTo(leftEmployeeX - left, y).stroke()              //Vertical left line
      .moveTo(maxRightWidth, 35).lineTo(maxRightWidth, y).stroke()                            //Vertical right line
      .moveDown(1);
  }

  var newSettlementBox = function(){
    // -----------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- SETTLEMENT --------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    leftEmployeeX = 72 + width + left + 5 ;   //Position X for left line employee box
    settlementY = 8;                           //Start y for employee box
    centerBoxText = 80;
    settlementWidth = x + pdfWidth + 100;

    pdf
      .moveTo(leftEmployeeX - left, y = y + settlementY).lineTo(maxRightWidth, y).stroke()   //Horizontal t0p line
      .font(textFont)
      .fontSize(textSize)
      .text('PERIODO DE LIQUIDACIÓN', leftEmployeeX, y = y + paddingTop).font(textFont)
      .text('TOTAL DÍAS',leftEmployeeX + 210, y).font(textFont)
      .fontSize(titleSize)
      .text(formatInputData(payroll.settlement.start_date, 'string') +'  -  '+ formatInputData(payroll.settlement.end_date, 'string'), leftEmployeeX + paddingLeft3 + 10, y = y + paddingTop + 8)
      .text(formatInputData(payroll.settlement.total_days, 'number'), leftEmployeeX + paddingLeft3 + 220, y)
      .moveTo(leftEmployeeX - left, y = y + paddingTop + 8).lineTo(maxRightWidth, y).stroke()
      .moveTo(leftEmployeeX + 190, y - paddingTop*2 - 20).lineTo(leftEmployeeX + 190, y).stroke()       //Vertical center line

      .moveTo(leftEmployeeX - left, y - paddingTop*2 - 20).lineTo(leftEmployeeX - left, y).stroke()         //Vertical right line
      .moveTo(settlementWidth, y - paddingTop*2 - 20).lineTo(settlementWidth, y).stroke()         //Vertical right line
      .moveDown(1);
  }

  var newFooterBox = function(){
    // -----------------------------------------------------------------------------------------------------------------
    // -------------------------------------------- FOOTER -------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    accrualWidth = x + pdfWidth + 100;
    t0p = 2;
    originalX = 22;
    originalY = 72;
    footer = 450;
    pdf
      .font(textFont)
      .text('BASES' , originalX + 50 , y = originalY + footer + t0p + 12)
      .text('AP. EMPRESA' , originalX + 120 , y)
      .text('DESGLOSE' , originalX + 230 , y)
      .text('TOTALES' , originalX + 455 , y)

      .text('1. Cont. Comunes' , originalX + 15, y = originalY + footer + t0p + 25)
      .text('Remuneración Total' , originalX + 210, y)
      .text('Devengos' , originalX + 400, y)
      .text('Deducciones' , originalX + 480, y)
      .moveTo(originalX + 10 , y - 3).lineTo(originalX + 305 , y - 3).stroke()
      .moveTo(originalX + 390 , y - 3).lineTo(originalX + 555 , y - 3).stroke()

      .text(formatAmount(payroll.footer_ss_quotation.common_contingency.base), originalX + 35 + numberOffset(payroll.footer_ss_quotation.common_contingency.base) , y = y + 10)
      .text(formatAmount(payroll.footer_ss_quotation.common_contingency.monthly_remuneration), originalX + 223 + numberOffset(payroll.footer_ss_quotation.common_contingency.monthly_remuneration) , y)
      .text(formatAmount(payroll.total_accrual), originalX + 425 + numberOffset(payroll.total_accrual) , y)
      .text(formatAmount(payroll.total_deductions), originalX + 510 + numberOffset(payroll.total_deductions) , y)

      .text(formatPercent(payroll.footer_ss_quotation.common_contingency.type_percent), originalX + 95 + numberOffset(payroll.footer_ss_quotation.common_contingency.type_percent) , y = y + 8)
      .text("|", originalX + 150, y)
      .text(formatAmount(payroll.footer_ss_quotation.common_contingency.company_input), originalX + 135 + numberOffset(payroll.footer_ss_quotation.common_contingency.company_input) , y)

      .moveTo(originalX + 200 , y = y + 3).lineTo(originalX + 305 , y).stroke()
      .moveTo(originalX + 390 , y).lineTo(originalX + 555 , y).stroke()

      .text('Prorrata Pagas Extr.' , originalX + 210, y = y + 3)
      .text('Líquido a percibir' , originalX + 480, y)
      .text(formatAmount(payroll.footer_ss_quotation.common_contingency.extraordinary_pay_packet), originalX + 223 + numberOffset(payroll.footer_ss_quotation.common_contingency.extraordinary_pay_packet) , y = y + 10)
      .text(formatAmount(payroll.liquid_perceive), originalX + 510 + numberOffset(payroll.liquid_perceive) , y)

      .moveTo(originalX + 10 , y = y + 10).lineTo(originalX + 305 , y).stroke()
      .moveTo(originalX + 472 , y).lineTo(originalX + 555 , y).stroke()

      .text('2. Cont. Profesionales' , originalX + 15, y = y + 3)

      .text('AT/EP' , originalX + 110, y)
      .text(formatAmount(payroll.footer_ss_quotation.professional_contingency.base), originalX + 35 + numberOffset(payroll.footer_ss_quotation.professional_contingency.base) , y = y + 10)
      .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_at), originalX + 95 + numberOffset(payroll.footer_ss_quotation.professional_contingency.type_percent_at) , y)
      .text("|", originalX + 150, y)
      .text(formatAmount(payroll.footer_ss_quotation.professional_contingency.company_input_at), originalX + 135 + numberOffset(payroll.footer_ss_quotation.professional_contingency.company_input_at) , y)
      .moveTo(originalX + 105 , y = y + 10).lineTo(originalX + 200 , y).stroke()

      .text('Desempleo' , originalX + 110, y = y + 3)
      .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_unemployment), originalX + 95 + numberOffset(payroll.footer_ss_quotation.professional_contingency.type_percent_unemployment) , y = y + 10)
      .text("|", originalX + 150, y)
      .text(formatAmount(payroll.footer_ss_quotation.professional_contingency.company_input_unemployment), originalX + 135 + numberOffset(payroll.footer_ss_quotation.professional_contingency.company_input_unemployment) , y)
      .moveTo(originalX + 105 , y = y + 10).lineTo(originalX + 200 , y).stroke()

      .text('Formación profesional' , originalX + 110, y = y + 3)
      .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_professional_formation), originalX + 95 + numberOffset(payroll.footer_ss_quotation.professional_contingency.type_percent_professional_formation) , y = y + 10)
      .text("|", originalX + 150, y)
      .text(formatAmount(payroll.footer_ss_quotation.professional_contingency.company_input_professional_formation), originalX + 135 + numberOffset(payroll.footer_ss_quotation.professional_contingency.company_input_professional_formation) , y)
      .moveTo(originalX + 105 , y = y + 10).lineTo(originalX + 200 , y).stroke()

      .text('FOGASA' , originalX + 110, y = y + 3)
      .text(formatPercent(payroll.footer_ss_quotation.professional_contingency.type_percent_salary_warranty), originalX + 95 + numberOffset(payroll.footer_ss_quotation.professional_contingency.type_percent_salary_warranty) , y = y + 10)
      .text("|", originalX + 150, y)
      .text(formatAmount(payroll.footer_ss_quotation.professional_contingency.company_input_salary_warranty), originalX + 135 + numberOffset(payroll.footer_ss_quotation.professional_contingency.company_input_salary_warranty) , y)

      .moveTo(originalX + 10 , y = y + 10).lineTo(originalX + 305 , y).stroke()
      .text('3. Horas Extras' , originalX + 15, y = y + 3)
      .text('No Estructurales' , originalX + 110, y)
      .text('Base HE (NE)' , originalX + 210, y)
      .text(formatAmount(payroll.footer_ss_quotation.aditional_quotation.company_input_overwhelming_force), originalX + 95 + numberOffset(payroll.footer_ss_quotation.aditional_quotation.company_input_overwhelming_force) , y = y + 10)
      .text(formatAmount(payroll.footer_ss_quotation.aditional_quotation.base_overwhelming_force), originalX + 223 + numberOffset(payroll.footer_ss_quotation.aditional_quotation.base_overwhelming_force) , y)

      .moveTo(originalX + 105 , y = y + 10).lineTo(originalX + 305 , y).stroke()
      .text('Estruc./Fuerza Mayor' , originalX + 110, y = y + 3)
      .text('Base HE (E/FM)' , originalX + 210, y)
      .text(formatAmount(payroll.footer_ss_quotation.aditional_quotation.company_input_non_structural), originalX + 95 + numberOffset(payroll.footer_ss_quotation.aditional_quotation.company_input_non_structural) , y = y + 10)
      .text(formatAmount(payroll.footer_ss_quotation.aditional_quotation.base_non_structural), originalX + 223 + numberOffset(payroll.footer_ss_quotation.aditional_quotation.base_non_structural) , y)

      .moveTo(originalX + 10 , y = y + 10).lineTo(originalX + 305 , y).stroke()
      .text('4. IRPF' , originalX + 15, y = y + 3)
      .text(formatAmount(payroll.footer_ss_quotation.base_irpf), originalX + 35 + numberOffset(payroll.footer_ss_quotation.base_irpf) , y = y + 10)
      .moveTo(originalX + 10 , y = y + 10).lineTo(originalX + 105 , y).stroke()

      .moveTo(originalX + 10 , originalY + footer + t0p + 22).lineTo(originalX + 10 , y).stroke()
      .moveTo(originalX + 105 , originalY + footer + t0p + 22).lineTo(originalX + 105 , y).stroke()
      .moveTo(originalX + 200 , originalY + footer + t0p + 22).lineTo(originalX + 200 , y - 23).stroke()
      .moveTo(originalX + 305 , originalY + footer + t0p + 22).lineTo(originalX + 305 , y - 161).stroke()
      .moveTo(originalX + 305 , y - 69).lineTo(originalX + 305 , y - 23).stroke()
      .moveTo(originalX + 390 , originalY + footer + t0p + 22).lineTo(originalX + 390 , y - 184).stroke()
      .moveTo(originalX + 472 , originalY + footer + t0p + 22).lineTo(originalX + 472 , y - 161).stroke()
      .moveTo(originalX + 555 , originalY + footer + t0p + 22).lineTo(originalX + 555 , y - 161).stroke()

      .moveTo(originalX, originalY+footer+240).lineTo(accrualWidth, originalY+footer+240).stroke();        //Horizontal bottom line
  }

  var newSignatureDate = function(){
    // -----------------------------------------------------------------------------------------------------------------
    // ------------------------------------------ SIGNATURE / DATE -----------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    end_date = formatDate(payroll.settlement.end_date);
    paddingSignature = 15;
    paddingDate = 10;
    signatureX = 340;
    signatureY = 610;
    signatureDateX = 450;
    signatureDateY = 680;

    pdf
      .fontSize(titleSize)
      .text('SELLO Y FIRMA DE LA EMPRESA' , signatureX , signatureY);

    if(payroll.signature_logo != undefined){
      pdf
        .image(payroll.signature_logo, signatureX, signatureY + paddingSignature, {scale: 0.06});
    }

    pdf
      .text(end_date,  signatureDateX , signatureDateY)
      .fontSize(titleSize)
      .text('RECIBÍ' , signatureDateX , signatureDateY + paddingDate);

  }

  var newAccrual = function(){
    // -----------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- ACCRUAL -----------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    originalX = 22;
    originalY = 72;
    y = settlementY+50;
    accrualWidth = x + pdfWidth + 100;

    //LINEAS DEL RECTANGULO DEVENGOS Y DEDUCCIONES
    pdf
      .moveTo(x, y = y + 95).lineTo(accrualWidth, y).stroke()
      .moveTo(x, y).lineTo(x, originalY + 690).stroke()
      .moveTo(accrualWidth, y).lineTo(accrualWidth, originalY + 690).stroke()

      .moveTo(x + 10, y = y + 10).lineTo(x + 558, y).stroke()
      .moveTo(x + 10, y).lineTo(x + 10, y + 360).stroke()
      .moveTo(x + 558, y).lineTo(x + 558, y + 360).stroke()
      .moveTo(x + 10, y + 360).lineTo(x + 558, y + 360).stroke();

    pdf
      .font(textFont)
      .text('CUANTÍA' , originalX + 40 , y = 167)
      .text('CONCEPTO' , originalX + 220 , y)
      .text('DEVENGOS' , originalX + 410 , y)
      .text('DEDUCCIONES' , originalX + 485 , y)
      .moveTo(x + 10, y + 10).lineTo(x + 558, y + 10).stroke()
      .moveTo(x + 110, y - 5).lineTo(x + 110, y + 355).stroke()
      .moveTo(x + 390, y - 5).lineTo(x + 390, y + 355).stroke()
      .moveTo(x + 472, y - 5).lineTo(x + 472, y + 355).stroke();

    y = y + 20;
    secondColumn = 140;
    thirdColumn = 440;;

    for(i = 0; i < payroll.accruals.length; i++){
      accrual = payroll.accruals[i];
      for(j = 0; j < accrual.types.length; j++){
        type = accrual.types[j];
        pdf
          .font(textFont)
          .text(type.type_expression , secondColumn + 8 , y)
          .text(formatAmount(type.type_value) , thirdColumn + numberOffset(type.type_value) , y);
        y = y + 10;
      }
    }
  }

  var newDeduction = function(){
    // -----------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- DEDUCTION ---------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    y = y + 20;
    firstColumn = 70;
    secondColumn = 140;
    quarterColumn = 520;

    for(i = 0; i < payroll.deductions.length; i++){
      deduction = payroll.deductions[i];
      var total = deduction.value;
      if(deduction.value != 0){
        if(deduction.types){
          var total = 0;
          for(j = 0; j < deduction.types.length; j++){
            total += deduction.types[j].value;
          }
        }
        pdf
          .font(textFont);
        if(deduction.percent){
          pdf
            .text(formatPercent(deduction.percent), firstColumn + numberOffset(deduction.percent), y);
        }
        
        name = deduction.name;
        
        if(!name || null == name || undefined == name){
        	name = deduction.description;
//        	console.log("NAME : " + name);
        }
        
        pdf
          .text(name , secondColumn + 8 , y)
          .text(formatAmount(total) , quarterColumn + numberOffset(total) , y);
        y = y + 10;
      }
    }
  }

  //Main
  newHearderTittle();
  newEnterpriseBox();
  newEmployeeBox();
  newSettlementBox();
  newAccrual();
  newDeduction();
  newSignatureDate();
  newFooterBox();

  //End PDF
  pdf.end();

}
