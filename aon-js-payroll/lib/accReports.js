var PDF = require('pdfkit');

module.exports.journal = function(metadata , flatEntries, stream){

	var journalReport = {
		 defFont : "Courier"
		,defBoldFont : "Courier-Bold"
		,defFontSize : 7
		,defHeaderFontSize : 8
		,pageNumber : 0
		,pdfWidth : 0
		,y : 0
		,pdf : new PDF({ 
			layout 	: "portrait" //"landscape"
			,size	: "A4"
			,margins: { top: 50, left: 20,right: 20, bottom : 30 }
			,autoFirstPage: false
			,info			: {
				 Title   : metadata.title
				,Author  : metadata.author
				,Subject : metadata.subject
			}})
		, cols : [
				 20		// Cuenta contable - código
				,65		// Cuenta contable - descripción
				,280	// Concepto
				,420	// Debe
				,500	// Haber
				,570	// Contrapartida - código	
//				,662	// Contrapartida - decripción
				]
		,formatter   : new Intl.NumberFormat("en", {style : "decimal", minimumFractionDigits: 2})
		,formatNumber: function( number ) {
			return (number && number != '' && number != null && number != 0) ? this.formatter.format(number) : '';	
		}
		,initialize : function() {
			console.log('START JOURNAL REPORT');
			this.pdf.on("pageAdded", () => {
				if (this.pageNumber == 0) {
					this.pdfWidth = this.pdf.page.width - this.pdf.page.margins.left - this.pdf.page.margins.right;
				}
				this.pageNumber++;

				// Se resetea el margen inferior, porque al escribir el footer, se lanza el evento de página otra vez y se bucla.
				// Se almacena porque despues de escribir el footer, se debe restaurar.
				var  preMarginBottom = this.pdf.page.margins.bottom;
				this.pdf.page.margins.bottom = 0;
				
				
				this.pdf
		        	// HEADER
					.font(this.defFont)
					.fontSize(this.defFontSize)
					.text(metadata.company    , this.pdf.page.margins.left , (this.pdf.page.margins.top - 40),{lineBreak: false,})
					.font(this.defBoldFont)
					.fontSize(this.defHeaderFontSize)
		        	.text(metadata.title, this.pdf.page.margins.left, (this.pdf.page.margins.top - 30),{width: this.pdfWidth,align: "center"})
		        	.fontSize(this.defFontSize)
		        	.moveTo(this.pdf.page.margins.left, this.pdf.y + 0.10)
		        	.lineTo( this.pdfWidth + this.pdf.page.margins.left, this.pdf.y + 0.10)
		        	.lineWidth(0.5)
			    	.stroke()
			    	.fontSize(this.defFontSize)
		        	.moveTo(this.cols[0], this.pdf.y)
			    	.text("Cuenta"		, this.cols[0], (this.pdf.page.margins.top - 17),{width: this.cols[1] - this.cols[0]})
			    	.text("Descripción"	, this.cols[1], (this.pdf.page.margins.top - 17),{width: this.cols[2] - this.cols[1]})
			    	.text("Concepto"	, this.cols[2], (this.pdf.page.margins.top - 17),{width: this.cols[3] - this.cols[2]})
			    	.text("Debe"		, this.cols[3], (this.pdf.page.margins.top - 17),{width: this.cols[4] - this.cols[3],align: "right"})
			    	.text("Haber"		, this.cols[4], (this.pdf.page.margins.top - 17),{width: this.cols[5] - this.cols[4],align: "right"})
			    	.text(" "			, this.cols[5], (this.pdf.page.margins.top - 17),{width: this.pdfWidth + this.pdf.page.margins.left - this.cols[5],align: "right"})
		        	.moveTo(this.pdf.page.margins.left, this.pdf.y + 0.10)
		        	.lineTo( this.pdfWidth + this.pdf.page.margins.left, this.pdf.y + 0.10)
		        	.lineWidth(0.5)
			    	.stroke()
			    
			    	// FOOTER
			    	.fontSize(this.defHeaderFontSize)
			    	.moveTo(this.pdf.page.margins.left, this.pdf.page.height - preMarginBottom)
		        	.lineTo( this.pdfWidth + this.pdf.page.margins.left , this.pdf.page.height - preMarginBottom)
		        	.text( this.pdf.info.CreationDate.toLocaleString(), this.pdf.page.margins.left , this.pdf.page.height - 20,{lineBreak: false})
		        	.text("Pág: " + this.pageNumber, this.pdf.page.margins.left , this.pdf.page.height - 20,{width: this.pdfWidth,align: "right"},{lineBreak: false})
		        	.stroke()
			    	
			    	// RESET TO STARTING POINT
		        	.font(this.defFont)
		        	.fontSize(this.defFontSize)
			    	.text("", this.pdf.page.margins.left, this.pdf.page.margins.top ,{lineBreak: false})
		            ;
				this.pdf.page.margins.bottom = preMarginBottom;
				this.y = this.pdf.y;
			});
			this.pdf.addPage();
		}
		,finalize : function(stream) {
		    this.pdf.pipe(stream);
		    this.pdf.end();
			console.log('END JOURNAL REPORT');
		}
		,ensure : function (text, length) {
			text = text && text != null?text:"";
			if (text.length > length) {
				text = text.substr(0,length) + "...";
			}
			return text;
		}
		,entry: function( entry ) {
			if (this.pdf.y > 770) {
				this.pdf.addPage();
			}
			
			var entryDate = entry.entryDate ? new Date(entry.entryDate).toLocaleDateString() : "";
			this.pdf
				.fontSize(this.defFontSize)
				.moveDown()
				.text("Fecha: " + entryDate + "  Nº. Diario: " + entry.journal + " (" + this.pdf.y + ")", this.pdf.page.margins.left , this.pdf.y,{width: this.pdfWidth, align: "center"})
				.moveTo(this.pdf.page.margins.left, this.pdf.y + 0.10)
				.lineTo( this.pdfWidth + this.pdf.page.margins.left, this.pdf.y + 0.10)
				.lineWidth(0.3)
				.moveTo(this.pdf.page.margins.left, this.pdf.y + 100)
				.stroke()
				.moveDown()
			;
		}
		,detail: function( detail ) {
			this.y = this.pdf.y;
			this.pdf
				.moveTo(this.cols[0], this.y )
        		.fontSize(this.defFontSize)
        		.text(detail.accountCode						, this.cols[0], this.y ,{width: this.cols[1] - this.cols[0]} ,{lineBreak: false})
        		.text(this.ensure(detail.accountDescription,45)	, this.cols[1], this.y ,{width: this.cols[2] - this.cols[1]} ,{lineBreak: false})
        		.text(detail.concept							, this.cols[2], this.y ,{width: this.cols[3] - this.cols[2]} ,{lineBreak: false})
        		.text(this.formatNumber(detail.debit)			, this.cols[3], this.y ,{width: this.cols[4] - this.cols[3],align: "right"} ,{lineBreak: false})
        		.text(this.formatNumber(detail.credit)			, this.cols[4], this.y ,{width: this.cols[5] - this.cols[4],align: "right"}	,{lineBreak: false})
        		.text(" "										, this.cols[5], this.y ,{width: this.pdfWidth + this.pdf.page.margins.left},{lineBreak: false})
        		;
		}
	};
	journalReport.initialize();
	var oldId = -1;
	for (var i = 0; i < flatEntries.length; i++) {
		flatEntry = flatEntries[i];
		if (oldId != flatEntry.entryId) {
			journalReport.entry( flatEntry );
			oldId = flatEntry.entryId;
		}
		journalReport.detail( flatEntry );	
	}
	journalReport.finalize(stream);
}
