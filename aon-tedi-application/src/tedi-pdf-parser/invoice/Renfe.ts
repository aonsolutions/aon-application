import moment = require('moment');
import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';
import { Invoice, InvoiceCategory, InvoiceStatus, InvoiceType, TaxType, TediString } from '../../tedi-ewok/TediEwok';
import { TediUnknownInvoiceFormatError } from '../TediPdfParser';
import { TediPDFParserLine, TediPDFParserUtils } from './Utils';

//  ___________________________________________________________________________________________________________
// |                                                                           																|
// |    RENFE VIAJEROS S.M.E., .... 									                                                        |
// |                                                                          																|
// |    Name receiver                                     											                    					|
// |    cif/dni receiver                                       											                    		  |
// |    Address                                       													                        			|
// |                                                                                                          |
// |    Factura número: xxxxxxxxxxxxx                                                                         |
// |     ____________________________________________________________________________________________					|
// |                                                                           																|
// |        Vencimiento fecha                                                                          				|
// |           08-7-2019                                                            					    						|
// |                                                                                          								|
// |                                                                           																|
// |        Descripción                                                            Importe          					|
// |        xxxxxxx                                                                20.00   				 				  	|
// |		                                                            																					|
// |                                              Total Servicio:             								20,33						|
// |                                              IVA quota           												2,05	          |
// |                                                                          				                    		|
// |                                              Total factura                               999,99	        |
// |                                                                           																|
// |        Descripción                       						Base           Tipo %          Cuota        	      |
// |        xxxxxxxxxx  										 						  20,33          10,00           2,05	                |
// |                                                                           																|
// | _________________________________________________________________________________________________________|

export class Renfe {
  public static extract(pdf: PDFExtractResult): Invoice {
    const pages: PDFExtractPage[] = pdf.pages;

    let line0: TediPDFParserLine | undefined;
    let content0: PDFExtractText[] | undefined;
    let i = 0;
    let match;

    for (i = 0; i < pages.length; i++) {
      const page: PDFExtractPage = pages[i];
      try {
        // TODO: sort content by x and y coordinates ?
        content0 = page.content;
        if (content0) {
          TediPDFParserUtils.sort(content0);
          line0 = TediPDFParserUtils.getLine(content0);
          if (line0) {
            do {
              // RENFE, S.A.U.
              match = line0.str.match(
                /^RENFE\s*VIAJEROS\s*S.M.E.\s*.\s*S.A.\s*AVENIDA\s*PIO\s*XII\s*110\s*28036\s*MADRID\s*ESPA.A\s*NIVA\s*:\s*ESA86868189\s*/i,
              );
              if (match) {
                break;
              }
              line0 = TediPDFParserUtils.getLine(content0, line0.index);
            } while (line0);
            if (match) {
              break;
            }
          }
        }
      } catch (e) {
        // tslint:disable-next-line: no-console
        console.error(' Catched!!' + e);
        throw new TediUnknownInvoiceFormatError(e);
      }
    }
    if (i === pages.length || !line0 || !content0) {
      throw new TediUnknownInvoiceFormatError('Not RENFE VIAJEROS invoice');
    }

    const invoice: Invoice = {
      type: InvoiceType.RECIBIDA,
      status: InvoiceStatus.inbox,
      verified: true,
      category: InvoiceCategory.C6280,
      sender: {
        address: {
          address: 'AVENIDA PIO XII 110',
          postal_code: '28036',
          city: 'POZUELO DE ALARCÓN',
          province: 'Madrid',
          country: 'ES',
        },
        document_country: 'ES',
        document: 'ESA86868189', // Cif empresa
        name: 'RENFE VIAJEROS S.M.E., S.A.',
      },
    };

    let line: TediPDFParserLine | undefined = line0;
    const content: PDFExtractText[] = content0;

    // Skip line datos postales
    let index = line.index;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        if (line.str.match(/Datos\s*Postales/i)) {
          break;
        }
      }
    } while (line);

    invoice.receiver = {
      name: '',
    };

    // Receiver Name: INNOVACIÓN COLABORATIVA, S.L.
    const indexName = index;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/^([^0-9]*)/i);
        if (match) {
          invoice.receiver.name = match ? match[1] : '';
          break;
        }
      }
    } while (line);

    // Match CIF/DNI receiver
    index = indexName;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/([A-Z]{1}\d{8})/i);
        if (match) {
          invoice.receiver.document = match[1]; // CIF cliente
          invoice.receiver.document_country = 'ES';
          break;
        }
        match = line.str.match(/(\d{8}[a-zA-Z]{1})/i);
        if (match) {
          invoice.receiver.document = match[1]; // DNI cliente
          invoice.receiver.document_country = 'ES';
          break;
        }
      }
    } while (line);

    /*  Get receiver address, cp, province and number invoice
     *  - first, second variables to store previous lines
     *  - Discard repeated lines
     *  - Discard break lines
     */
    let first: string = '';
    let second: string = '';
    let current: string = '';
    let address: string = '';
    let cp: string = '';
    let province: string = '';
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        current = line.str;
        index = line.index;
        match = line.str.match(/FACTURA\s*N.MERO\s*:\s*(.*)/i);
        if (match) {
          invoice.reference = match[1];
          break;
        }
        // Discard repeated lines
        if (second !== first) {
          if (second === current || second.startsWith(current.substring(0, current.length * 0.5))) {
            address += (address.length > 0 ? ', ' : '') + second;
          } else if (current.match(/^[a-zA-ZÀ-ÿ\u00f1\u00d1]{3,},\s?\d{1,2}\s?de\s?[a-zA-ZÀ-ÿ\u00f1\u00d1]{3,}\s?de\s?\d{2,4}/i)) {
            address += ', ' + first;
          }
          // cp
          match = second.match(/^(\d{5})/i);
          if (match) {
            cp = match[1];
          }
          // province
          match = second.match(/^[a-zA-ZÀ-ÿ\u00f1\u00d1]+(\s*[a-zA-ZÀ-ÿ\u00f1\u00d1]*)*[a-zA-ZÀ-ÿ\u00f1\u00d1]+$/i);
          if (match) {
            // If there are 2 differents provinces (error pdf?) select first
            if (second !== current) {
              province = first;
            } else {
              province = second;
            }
          }
        }
        // Store previous lines for use in the next cycle.
        first = second;
        second = current;
      }
    } while (line);
    invoice.receiver.address = { address: TediString.polish(address) };
    invoice.receiver.address.postal_code = cp;
    invoice.receiver.address.province = province;

    // Fecha de factura: dd/MM/yyyy
    invoice.finances = [{}];
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        if (line.str.match(/Descripti.n\s+importe/i)) {
          break;
        }
        match = line.str.match(/(\d+).(\d+).(\d+)/i);
        if (match) {
          const issueDateStr = match[1] + '/' + match[2] + '/' + match[3];
          invoice.date = moment(issueDateStr, 'DD/MM/YYYY').toDate();
          invoice.finances[0].due_date = moment(issueDateStr, 'DD/MM/YYYY').toDate();
          break;
        }
      }
    } while (line);

    // COMMENT INVOICE DETAILS
    // Invoice details: Total Servicio: 115.55
    // invoice.details = [];
    // let desc: string = '';
    // let basePrice: string = '';
    // do {
    //   line = TediPDFParserUtils.getLine(content, index);
    //   if (line) {
    //     index = line.index;
    //     match = line.str.match(/Total\s*Servicio\s*:\s*(-?\d+(.\d+)?)/i);
    //     if (match) {
    //       basePrice = match[1];
    //       break;
    //     }
    //     // Invoice details descripción: FACTURA DEL BILLETE: 7784000954965 - 05070 - MADRID-PUERTA...
    //     if (!line.str.match(/Descripci.n\s+importe/i)) {
    //       desc += desc.length === 0 ? line.str.trim() : ' ' + line.str.trim();
    //     }
    //   }
    // } while (line);

    // invoice.details.push({
    //   price: this.formatToNumber(basePrice),
    //   quantity: 1,
    //   base: this.formatToNumber(basePrice),
    //   discount: 0,
    //   description: desc.replace(/\s*(\d+\.\d+)|(\d+,\d+)\s*/, '').trim(),
    // });

    // Invoice total: Total Factura 76,40
    let mainTotal: number = 0;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/Total\s*Factura\s*(-?\d+(.\d+)?)/i);
        if (match) {
          mainTotal = this.formatToNumber(match[1]);
          invoice.total = mainTotal;
          invoice.finances[0].amount = mainTotal;
          break;
        }
      }
    } while (line);

    invoice.taxes = [
      {
        tax: TaxType.IVA,
        base: 0,
        percentage: 0,
        quota: 0,
      },
    ];

    // Taxes: IVA REPERCUTIDO_10% (DESDE 01.09.2012) 66,55 10,00 6,00
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/(.*)\s+(-?\d+(.\d+)?)\s+(-?\d+(.\d+)?)\s+(-?\d+(.\d+)?)/i);
        if (match) {
          invoice.taxes[0].base = this.formatToNumber(match[2]);
          invoice.taxes[0].percentage = this.formatToNumber(match[4]);
          invoice.taxes[0].quota = this.formatToNumber(match[6]);
          break;
        }
      }
    } while (line);

    // Adjust the total amount with taxes
    TediPDFParserUtils.adjustTaxes(invoice.taxes, mainTotal);

    return invoice;
  }
  // Prepare string to parse in number. Differents pdf number formats (22.00 or 22,00)
  public static formatToNumber(str: string): number {
    str = str.trim();
    str = str.replace(/\s/g, '').replace(/,/g, '.');
    return Number.parseFloat(str);
  }
}
