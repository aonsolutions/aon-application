var PDF = require('pdfkit');

module.exports.newStandardPayroll = function(payroll, stream){
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
  var t0p = 0;
  var left = 0;
  var right = 0;
  
  textSize = 8;
  textFont = 'Helvetica';

  titleSize = 8;
  titleFont = 'Helvetica-Bold';

  headingSize = 4;
  headingFont = 'Helvetica-Bold';

  signature = 455;

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
      else if(typeData == 'number' && isNaN(data)){
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
      if(taxesS.value == 0){
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
    paddingTopLine = 10;
    paddingTopTT = 11;
    paddingLeftValue = 333;
    paddingLeftTotal = 503;

    pdf
      .moveTo(x, y).lineTo(accrualWidth, y).stroke()
      .font(headingFont)
      .text('I. DEVENGOS' , accrualX , accrualY = accrualY + paddingTopTL)
      .text('TOTALES', accrualX + paddingLeftTotal, accrualY)
      .lineWidth(0.1);

    y = y + 20;
    firstColumn = 50;
    secondColumn = 100;
    thirdColumn = 440;
    quarterColumn = 440;

    for(i = 0; i < payroll.accruals.length; i++){
      accrual = payroll.accruals[i];
      if(accrual.types.length > 0){
	      pdf
	        .font(titleFont)
	        .text(accrual.accrual_name , firstColumn , y);
	      craY = y;
	      y = y + 15;
	      craTotal = 0;
	      for(j = 0; j < accrual.types.length; j++){
	        type = accrual.types[j];
	        craTotal += type.type_value;
	        pdf
	          .font(textFont)
	          .text(formatMoney(type.type_value), firstColumn + numberOffset(type.type_value) , y)
	          .text("  por " + type.type_expression , secondColumn , y);
	
	        y = y + 10;
	      }
	      y = y + 2;
	      pdf
	        .text(formatAmount(craTotal) , quarterColumn + numberOffset(craTotal) , craY)
	        .moveTo(firstColumn, craY + 10).lineTo(quarterColumn + 45,  craY + 10).stroke();
      }
    }

    pdf
      .fontSize(titleSize)
      .font(titleFont)
      .text('A. TOTAL DEVENGADO' , accrualX + 150 , accrualY = y + 5)
      .text(formatAmount(payroll.total_accrual) , accrualX + paddingLeftTotal + numberOffset(payroll.total_accrual), accrualY)
      .moveTo(accrualX + 150 , accrualY + paddingTopLine).lineTo(accrualX + 545 , accrualY + paddingTopLine).stroke();
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
  console.log('newSettlementBox');
  newSettlementBox();
  console.log('newAccrual');
  newAccrual();
  console.log('newDeductionFirstPage');
  newDeductionFirstPage();
  console.log('newSignatureDate');
  newSignatureDate();
  console.log('newFooterBox');
  newFooterBox();
  console.log('newDeductionSecondPage');
  newDeductionSecondPage();

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
      if(taxesS == undefined || taxesS.value == 0){
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

module.exports.salaryRecibeCRA = function(payroll, stream){
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
	  return (amount  && amount != '' && amount != null && amount != 0 && 'string' != typeof amount) ? amount.toFixed(2) : '';
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
    firstColumn = 70;
    secondColumn = 140;
    thirdColumn = 440;
    quarterColumn = 520;

    for(i = 0; i < payroll.accruals.length; i++){
      accrual = payroll.accruals[i];
      if(accrual.types.length > 0){
	      pdf
	        .font(titleFont)
	        .text(accrual.accrual_name , secondColumn , y);
	      y = y + 10;
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
        pdf
          .text(deduction.name , secondColumn + 8 , y)
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
        pdf
          .text(deduction.name , secondColumn + 8 , y)
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
