var PDF = require('pdfkit');
var WRITTEN_NUMBER = require('written-number');

module.exports.a3Letter = function (settlement, stream) {
	var pdf = new PDF();
	pdf.pipe(stream);

	var pdfWidth = pdf.page.width
		- pdf.page.margins.left
		- pdf.page.margins.right
		;

	var formatText = function( text ) {
		return text ? text.toUpperCase() : ' ';
	}

	var formatAmount = function( amount ) {
		return amount ? amount.toFixed(2) : '0.00';
	}

	var textSize = 8;
	var textFont = 'Times-Roman';
	// textFont = 'Helvetica';
	// textFont = 'Courier';
	var titleSize = 12;
	var titleFont = 'Times-Bold';
	// titleFont = 'Helvetica-Bold';
	// titleFont = 'Courier-Bold';
	var headingSize = 8;
	var headingFont = 'Times-Bold';
	// headingFont = 'Helvetica-Bold';
	// headingFont = 'Courier-Bold';

	pdf
	  .font(titleFont)
		.fontSize(titleSize)
		.text('DOCUMENTO DE LIQUIDACIÓN Y FINIQUITO', { align: 'center'} )
		.moveDown(1);

	pdf
		.font(headingFont)
		.fontSize(headingSize)
		.text('DATOS DE LA EMPRESA')
		.moveDown(0.5);

	var y = pdf.y;
	var x = pdf.x;
	var top = 3;
	var left = 4;

	var width = pdfWidth * 5/8;
	pdf
		.moveTo(x,o=y).lineTo(x+pdfWidth, y).stroke()
		.font(headingFont)
		.text('EMPRESA: ' , x + left , y + top , {continued: true})
		.font(textFont)
		.text(formatText(settlement.enterprise.name))
		.font(headingFont)
		.text('N.I.F.: ', x + width + left, y + top , {continued: true})
		.font(textFont)
		.text(formatText(settlement.enterprise.cif))
		.moveTo(x, y = pdf.y).lineTo(x+pdfWidth, y).stroke()

		.font(headingFont)
		.text('DOMICILIO: ', x + left, y + top, {continued: true})
		.font(textFont)
		.text(formatText(settlement.enterprise.address))
		.font(headingFont)
		.text('LOCALIDAD: ',x + width  + left, y + top, {continued: true})
		.font(textFont)
		.text(formatText(settlement.enterprise.city))
		.moveTo(x, y = pdf.y).lineTo(x+pdfWidth, y).stroke()

		.moveTo(x,o).lineTo(x,y).stroke()
		.moveTo(x+width,o).lineTo(x+width,y).stroke()
		.moveTo(x+pdfWidth,o).lineTo(x+pdfWidth,y).stroke()

		.moveDown(1);


	pdf
		.font(headingFont)
		.fontSize(headingSize)
		.text('DATOS DEL TRABAJADOR', x)
		.moveDown(0.5);

		y = pdf.y;

	pdf
		.moveTo(x,o=y).lineTo(x+pdfWidth, y).stroke()
		.font(headingFont)
		.text('APELLIDOS Y NOMBRE: ' , x + left , y + top , {continued: true})
		.font(textFont)
		.text(formatText(settlement.employee.fullname))
		.font(headingFont)
		.text('N.I.F.: ', x + width + left, y + top , {continued: true})
		.font(textFont)
		.text(formatText(settlement.employee.nif))
		.moveTo(x, y = pdf.y).lineTo(x+pdfWidth, y).stroke()

		.font(headingFont)
		.text('DOMICILIO: ', x + left, y + top, {continued: true})
		.font(textFont)
		.text(formatText(settlement.employee.address))
		.font(headingFont)
		.text('LOCALIDAD: ',x + width  + left, y + top, {continued: true})
		.font(textFont)
		.text(formatText(settlement.employee.city))
		.moveTo(x, y = pdf.y).lineTo(x+pdfWidth, y).stroke()

		.font(headingFont)
		.text('MOTIVO BAJA: ', x + left, y + top, {continued: true})
		.font(textFont)
		.text(formatText(settlement.reason))
		.font(headingFont)
		.text('CATEGORIA: ', x + width + left, y + top, {continued: true})
		.font(textFont)
		.text(formatText(settlement.employee.category))
		.moveTo(x, y = pdf.y).lineTo(x+pdfWidth, y).stroke()

		.moveTo(x,o).lineTo(x,y).stroke()
		.moveTo(x+width,o).lineTo(x+width,y).stroke()
		.moveTo(x+pdfWidth,o).lineTo(x+pdfWidth,y).stroke()

		.moveDown(1);

	y = pdf.y;

	pdf
		.font(textFont)
		.text('El suscrito trabajador cesa en la prestación de sus servicios por cuenta'
		+' de la empresa y recibe en este acto la liquidación de sus partes proporcionales'
		+' en la cuantía y detalle que se expresan al pie, con cuyo percibo reconoce'
		+' hallarse saldado y finiquitado por todos los conceptos con la referida empresa,'
		+' por lo que se compromete a nada más pedir ni reclamar.', x, y, {align:'justify'})
		.moveDown(1);

	y = pdf.y;

	pdf
		.font(headingFont)
		.moveTo(x,o=y).lineTo(x+pdfWidth, y).stroke()
		.text('DESGLOSE DE LA LIQUIDACIÓN', x, y + top, {align:'center'});

	y = pdf.y;

	pdf
		.moveTo(x,o).lineTo(x,y).stroke()
		.moveTo(x+pdfWidth,o).lineTo(x+pdfWidth,y).stroke()

	pdf
		.moveTo(x,o=y).lineTo(x+pdfWidth, y).stroke()
		.font(headingFont)
		.text('UNIDAD' , x + left , y + top)
		.text('CONCEPTOS' , x + width/6 + left , y + top)
		.text('DEVENGOS' , x + width + left , y + top)
		.text('DEDUCCIONES' , x + width + (pdfWidth - width)/2 + left , y + top)
		.moveTo(x, y = pdf.y).lineTo(x+pdfWidth, y).stroke()

	pdf.moveDown(1);

	amountOptions = {width: (pdfWidth - width)/2 - 2*left, align: 'right'};

	var payments = settlement.payments.filter( function ( p ) { return p.amount > 0.00;  } )

	pdf.font(textFont);
	for ( var i = 0; i <  payments.length; i++ ) {
		y = pdf.y;
		pdf
			.text( payments[i].units || ' ' , x + left, y + top )
		var yy = pdf.y
		pdf
			.text( formatText(payments[i].description) , x + width/6 + left, y + top, { width: width - width/6})
		pdf
			.text( formatAmount(payments[i].amount) , x + width + left, y + top + ( yy != pdf.y ? pdf.y - yy  :0 ), amountOptions )
		;
	}

	var deductions = settlement.deductions.filter( function ( p ) { return p.amount > 0.00; } )

	pdf.moveDown(1);

	for ( var i = 0; i <  deductions.length; i++ ) {
		y = pdf.y;
		pdf
			.text( deductions[i].units  || ' ', x + left, y + top )
			.text( formatText(deductions[i].description ), x + width/6 + left, y + top)
			.text( formatAmount(deductions[i].amount), x + width + (pdfWidth - width)/2 + left, y + top,  amountOptions)
		;
	}
	pdf.moveDown(25 - (deductions.length + payments.length) );

	y = pdf.y;

	pdf
		.moveTo(x+width/6,o).lineTo(x+width/6,y).stroke();

	pdf
		.moveTo(x, y).lineTo(x+pdfWidth, y).stroke()
		.font(headingFont)
		.text('TOTALES', x +left , y + top)
		.font(textFont)
		.text( formatAmount(settlement.payment), x + width + left, y + top,  amountOptions)
		.text( formatAmount(settlement.deduction), x + width + (pdfWidth - width)/2 + left, y + top,  amountOptions);

	y = pdf.y;

	pdf
	.moveTo(x+width,o).lineTo(x+width,y).stroke();

	pdf
		.moveTo(x, y).lineTo(x+pdfWidth, y).stroke()
		.font(headingFont)
		.text('IMPORTE LÍQUIDO A RECIBIR', x +left , y + top)
		.font(textFont)
		.text( formatAmount(settlement.net), x + width + (pdfWidth - width)/2 + left, y + top,  amountOptions);

  y = pdf.y;

	pdf
		.moveTo(x,o).lineTo(x,y).stroke()
		.moveTo(x+width + (pdfWidth - width)/2,o).lineTo(x+width + (pdfWidth - width)/2,y).stroke()
		.moveTo(x+pdfWidth,o).lineTo(x+pdfWidth,y).stroke()
		.moveTo(x,y).lineTo(x+pdfWidth,y).stroke();

	pdf.moveDown(1);

	pdf
		.text('En ' + formatText(settlement.place)
		+ ', a '
	 	+ settlement.date.toLocaleDateString('es-ES', {day: 'numeric'})
		+ ' de '
		+ settlement.date.toLocaleDateString('es-ES', {month:'long'}).toUpperCase()
		+ ' de '
		+ settlement.date.toLocaleDateString('es-ES', {year: 'numeric'})
	 , x);

	pdf.moveDown(2);

	pdf
	.text('Recibí:', x)

	pdf.moveDown(0.5)

	y = pdf.y;
  o = y

	pdf
	.text('A los efectos oportunos declaro que he firmado esta liquidación en'
	+' presencia de un representante de los trabajadores.'
	, x + left , y + top , {width: pdfWidth / 3 - 2*left , align: 'justify'})
	.text('A los efectos oportunos declaro que no he hecho uso de la posibilidad'
	+' de la presencia de un representante de los trabajadores.'
	, x + pdfWidth / 3 + left  , y + top, {width: pdfWidth / 3 - 2*left , align: 'justify'})
	.text('A los efectos oportunos declaro en la empresa no existe representante'
	+' de los trabajadores.'
	, x + 2 * pdfWidth / 3 + left  , y + top, {width: pdfWidth / 3 - 2*left , align: 'justify'})

	pdf.moveDown(3)

	y = pdf.y;

	pdf
	.font(headingFont)
	.text('FIRMADO'
	, x + left, y )
	.text('FIRMADO'
	, x + pdfWidth / 3 + left, y)
	.text('FIRMADO'
	, x + 2 * pdfWidth / 3 + left, y)

	y = pdf.y;

	pdf
	.moveTo(x,o).lineTo(x+pdfWidth,o).stroke()
	.moveTo(x,o).lineTo(x,y).stroke()
	.moveTo( x + pdfWidth / 3,o).lineTo( x + pdfWidth / 3,y).stroke()
	.moveTo( x + 2 * pdfWidth / 3,o).lineTo( x + 2 * pdfWidth / 3,y).stroke()
	.moveTo(x+pdfWidth,o).lineTo(x+pdfWidth,y).stroke()
	.moveTo(x,y).lineTo(x+pdfWidth,y).stroke()



	pdf.end();

};

module.exports.defLetter = function (settlement, stream) {

	var pdf = new PDF();
	pdf.pipe(stream);

	var width = pdf.page.width
				- pdf.page.margins.left
				- pdf.page.margins.right
				;

	var letter = {
		textSize : 11,
		textFont : 'Times-Roman',
		titleSize : 11,
		titleFont : 'Times-Bold',
		x : [
			pdf.page.margins.left,
			pdf.page.margins.left + width * 3.00/4
		],
		options : [
			{ width: width * 3/4 },
			{ width: width * 1/4 }
		],
		text : function (col, row, text, options)  {
			//default optios parameter

			options = options || {};

			//merge col's options & options
			for (var attr in this.options[col]){
				options[attr] = options[attr] || this.options[col][attr];
			}

			var x = this.x[col];
			var y = pdf.page.margins.top
								+ row * pdf.currentLineHeight(true);

			pdf
			.font(this.textFont)
			.fontSize(this.textSize)
			.text(text, x, y, options);

			return this;
		},
		title : function (col, row, text, options)  {
			//default optios parameter
			options = options || {};
			//merge col's options & options

			for (var attr in this.options[col]){
				options[attr] = options[attr] || this.options[col][attr];
			}

			var x = this.x[col];
			var y = pdf.page.margins.top
								+ row * pdf.currentLineHeight(true);
			pdf
			.font(this.titleFont)
			.fontSize(this.titleSize)
			.text(text, x, y, options);

			pdf
			.font(this.textFont)
			.fontSize(this.textSize);

			return this;
		},
		line : function (col, row, options)  {
			//default optios parameter
			options = options || {};
			options.top = options.top || 0;
			options.left = options.left || 0;

			var y = pdf.page.margins.top
								+ row * pdf.currentLineHeight(true)
								+ options.top
								;

			pdf
				.moveTo(this.x[col] + options.left,y )
				.lineTo(this.x[col] + this.options[col].width,y);

			if ( options.dash )
				pdf.dash(options.dash);
			else
				pdf.undash();

			pdf.stroke();

			return this;
		},
		image(col, row, image, options) {
			options = options || {};
			options.top = options.top || 0;

			var y = pdf.page.margins.top
								+ row * pdf.currentLineHeight(true)
								+ options.top;
			pdf.image(image, this.x[col], y , options);
			return this;
		}

	};


	letter.image( 0, 0,settlement.logo, {width: 100});


	var row;
	letter
		.title(0,row=5, '- FINIQUITO a favor del trabajador:')
		.text(0,row+=2, settlement.employee.fullname ).title(1,row, 'N.I.F.:' + settlement.employee.nif )
		.text(0,row+=2, 'que causa baja en la empresa:' )
		.text(0,row+=2, settlement.enterprise.name ).title(1,row, 'N.I.F.:' + settlement.enterprise.cif)
		.text(0,row+=2, 'con fecha: ' + settlement.date.toLocaleDateString('es-ES') )
		.text(0,row+=2,'según el siguiente desglose:')
	;

	letter
		.title(0,row+=2,'CONCEPTO',{indent:25})
		.title(1,row,'IMPORTE',{align:'right'})
	;

	for ( i = 0 ; i < settlement.payments.length; i++ ) {
		var payment = settlement.payments[i];
		letter.text(0, row+=1, payment.description,{indent:25}).text(1, row,payment.amount,{align:'right'});
	}

	letter
		.title(0,row+=2,'TOTAL',{indent:25}).title(1,row,settlement.payment,{align:'right'}).line(1,row, {top:-2})
		.line(0,row+=1, {left:25, dash:1})
	;

	letter
		.title(0,row+=2,'DEDUCCIONES',{indent:25}).title(1,row,'IMPORTE',{align:'right'})
	;
	for ( i = 0 ; i < settlement.deductions.length; i++ ) {
		var deduction = settlement.deductions[i];
		letter.text(0, row+=1, deduction.description,{indent:25}).text(1, row, deduction.amount,{align:'right'});
	}

	letter
		.title(0,row+=2,'TOTAL',{indent:25}).title(1,row,settlement.deduction,{align:'right'}).line(1,row, {top:-1})
		.line(0,row+=1, {left:25, dash:1})

		.title(0,row+=2,'LÍQUIDO',{indent:25}).title(1,row,settlement.net,{align:'right'}).line(1,row, {top: -1})
		.line(0,row+=1, {left:25, dash:1})
	;

	letter
		.title(0,row+=2, '-- o --', { align: 'center', width: width});

	var decimal = settlement.net % 1;
	var integer = settlement.net - decimal;


	letter
		.text(0,row+=2, '- He recibido la cantidad de:')
		.title(0,row+=1, WRITTEN_NUMBER( integer, {lang: 'es'}).toUpperCase()
				+ (decimal ? ' CON ' + WRITTEN_NUMBER( decimal * 100, {lang: 'es'}) :'' ).toUpperCase() + ' euros ' )
		.text(0,row+=1, 'como saldo a mi favor, segun liquidación precedente, considerandome \
totalmente remunerado hasta el día de fecha que causo baja sin tener derecho a \
ninguna reclamación posterior, y dando por finiquitado mi contrato con la citada Empresa.'
			, {  width: width, align: 'justify'})
	;

	letter
		.text(0,row+=5, 'En ' + settlement.place + ',a '
				+ settlement.date.toLocaleDateString('es-ES', {month:'long', year: 'numeric', day: 'numeric'})
			,{align: 'right', width: width})
		.text(0,row+=2, 'NO EXISTE REPRESENTACIÓN LEGAL DE LOS TRABAJADORES',{width:width})
		.text(0,row+=2, 'RECIBÍ', {align: 'center', width:width})
	;

	pdf.end();
};
