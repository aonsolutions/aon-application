import moment from 'moment';
import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';
import { Invoice, InvoiceCategory, InvoiceStatus, InvoiceType, TaxType } from '../../tedi-ewok/TediEwok';
import { TediUnknownInvoiceFormatError } from '../TediPdfParser';
import { TediPDFParserLine, TediPDFParserUtils } from './Utils';
//

//                                                    ________________________________________________________
//                                                   |   NOMBRE CLIENTE                  	                    |
//                                                   |   DIRECCIÓN                                            |
//                                                   |   CÓDIGO POSTAL                                        |
//                                                   |   CIUDAD                                               |
//                                                   |________________________________________________________|
//  ___________________________________________________________________________________________________________
// |                            |                                       |      																|
// | FACTURA  Nº XXXXXXXX       |    FECHA        XX-XX-XXXX            |  CIF/DNI        XXXXXXXXXXXXX       |
// |____________________________|_______________________________________|_____________________________________|
// |                                CONCEPTO                            ||   HONORARIOS    ||  SUPLIDOS       |
// |                                                                    ||                 ||              		|
// |   XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX                               ||    xxxx,xxx     ||                 |
// |                                                                    ||                 ||                 |
// |                                                                    ||                 ||              		|
// |                                                                    ||                 ||          				|
// |                                                                    ||                 ||              		|
// |                                                                    ||                 ||                 |
// |                                                                    ||                 ||                 |
// |                                                                    ||                 ||              		|
// |                                                                    ||                 ||                 |
// |                                                                    ||                 ||                 |
// |                                                                    ||                 ||                 |
// |                                                                    ||                 ||                 |
// |                                                                    ||     						 ||									|
// |                                                                    ||                 ||                 |
// |                                                                    ||                 ||                 |
// |                                                                    ||                 ||                 |
// |                                                                    ||     						 ||									|
// |                                                                    ||                 ||                 |
// |                                                                    ||     						 ||									|
// |                                                                    ||     						 ||									|
// |                                                                    ||                 ||                 |
// |                                                                    ||     					   ||									|
// |                                                                    ||     				     ||									|
// |                                                                    ||                 ||                 |
// |                                                                    ||                 ||                 |
// |                                                                    ||                 ||                 |
// |                                                                    ||     			       ||									|
// |                                                                    ||     			       ||									|
// |                                                                    ||    			       ||									|
// |                                                                    ||                 ||                 |
// |                                                                    ||     				     ||									|
// |                                                                    ||    				     ||									|
// |                                                                    ||     				     ||									|
// | ___________________________________________________________________||_________________||_________________|
// |   T.HONORARIOS   || T.SUPLIDOS ||  %IVA   || CUOTA IVA ||   %RET   ||  CUOTA RET   ||	TOTAL FACTURA 		|
// |                  ||            ||         ||           ||          ||     				  ||									  |
// |    XX,XX         ||            ||  XX,X%  ||  X,XX     ||          ||    				  ||			XX,XX € 		  |
// |                  ||            ||         ||           ||          ||     				  ||									  |
// |__________________||____________||_________||___________||__________||______________||____________________|

export class AsesoriaOnline {
  public static extract(pdf: PDFExtractResult): Invoice {
    const pages: PDFExtractPage[] = pdf.pages;
    // TODO: sort pages by page num ?
    let line0: TediPDFParserLine | undefined;
    let content0: PDFExtractText[] | undefined;
    let match;
    let i = 0;
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
              match = line0.str.match(/^C\D?\s*Princesa\s*\d*/i);
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
        // console.error(' Catched!!' + e);
        throw new TediUnknownInvoiceFormatError(e);
      }
    }

    if (i === pages.length || !line0 || !content0) {
      throw new TediUnknownInvoiceFormatError('NOT TAPO INVOICE');
    }

    const invoice: Invoice = {
      type: InvoiceType.RECIBIDA,
      status: InvoiceStatus.inbox,
      verified: true,
      category: InvoiceCategory.C6280,
      sender: {
        address: {
          address: 'C/Princesa 24',
          postal_code: '28008',
          city: 'MADRID',
          province: 'Madrid',
          country: 'ES',
        },
        document_country: 'ES',
        document: 'B87266656',
        name: 'TU ASESORIA PERSONAL ONLINE S.L.',
      },
    };

    if (match) {
      invoice.reference = 'B87266656';
    }

    let line: TediPDFParserLine | undefined = line0;
    const content: PDFExtractText[] = content0;
    let index = 0;

    invoice.receiver = {};

    // MARIA JENNIFER VIQUE QUINDE | NOMBRE
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      invoice.receiver.name = line.str.replace(/\s*$/, '');
    }

    // AV ESPAÑA, 51 2º | ADDRESS
    invoice.receiver.address = {};
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      invoice.receiver.address.address = line.str.replace(/\s*$/, '');
    }

    // 28710 | CP
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      if (match) {
        match = line.str.match(/(\d+)\s*/i);
        invoice.receiver.address.postal_code = match[1];
      }
    }

    // Madrid | CITY
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      invoice.receiver.address.city = line.str;
    }

    // Nº INVOICE + DATE
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/^FACTURA\s*N\W?\s*(\d+)\s*FECHA\s*(\d+)(\D+)(\d+)(\D+)(\d+)/i);
      if (match) {
        invoice.number = match[1];
        const dateStr = match[2] + '/' + TediPDFParserUtils.month(match[3]) + '/' + match[4];
        invoice.date = moment(dateStr, 'DD/MM/YYYY').toDate();
      }
    }

    // CIF/DNI
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      invoice.receiver.document = line.str;
      invoice.receiver.document_country = 'ES';
    }

    //  ============ Details ============ //

    // Skip line
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    invoice.details = [];
    let price;
    let text;
    const exit = 'T.HONORARIOS T.SUPLIDOS %IVA CUOTA IVA %RET CUOTA RET TOTAL FACTURA';

    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        if (line.str === exit) {
          break;
        }
        match = line.str.match(/^[+-]?[0-9]{1,9}(?:,[0-9]{1,2})?/i);
        if (match) {
          price = match[0];
        } else {
          match = line.str.match(/[A-Za-zñÑáéíóúÁÉÍÓÚ\s]+/i);
          if (match) {
            text = match[0];

            invoice.details.push({
              price: TediPDFParserUtils.string2Number(price),
              base: TediPDFParserUtils.string2Number(price),
              description: text,
              quantity: 1,
              discount: 0,
            });
          }
        }
      }
    } while (line);

    // =========== TAXES  =========== //
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/(^[+-]?[0-9]{1,9}(?:,[0-9]{1,2})?\s?)(\W?)/i);

      invoice.taxes = [];
      let quota;
      let base;
      let percen;

      if (match) {
        // Total Invoice
        invoice.total = TediPDFParserUtils.string2Number(match[1]);

        // Base + Quota
        line = TediPDFParserUtils.getLine(content, index);
        if (line) {
          index = line.index;
          let arr: string[];
          arr = line.str.split(' ');
          base = arr[0];
          quota = arr[1];
        }

        // Percen
        line = TediPDFParserUtils.getLine(content, index);
        if (line) {
          index = line.index;
          percen = line.str;
        }

        invoice.taxes.push({
          tax: TaxType.IVA,
          base: TediPDFParserUtils.string2Number(base),
          quota: TediPDFParserUtils.string2Number(quota),
          percentage: TediPDFParserUtils.string2Number(percen),
        });
      } else {
        // When MATCH = €

        // TOTAL FACTURA 99.999 €
        line = TediPDFParserUtils.getLine(content, index);
        if (line) {
          index = line.index;
          invoice.total = TediPDFParserUtils.string2Number(line.str);
        }

        // QUOTA IVA 99.999
        line = TediPDFParserUtils.getLine(content, index);
        if (line) {
          index = line.index;
          quota = line.str;
        }

        // T. HONORARIOS 999.999
        line = TediPDFParserUtils.getLine(content, index);
        if (line) {
          index = line.index;
          base = line.str;
        }

        // %IVA 99%
        line = TediPDFParserUtils.getLine(content, index);
        if (line) {
          index = line.index;
          percen = line.str;
        }

        invoice.taxes.push({
          tax: TaxType.IVA,
          base: TediPDFParserUtils.string2Number(base),
          quota: TediPDFParserUtils.string2Number(quota),
          percentage: TediPDFParserUtils.string2Number(percen),
        });
      }
    }
    return invoice;
  }
}
