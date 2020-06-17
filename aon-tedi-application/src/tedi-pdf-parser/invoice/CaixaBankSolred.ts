import moment from 'moment';
import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';
import { Invoice, InvoiceCategory, InvoiceStatus, InvoiceType, TaxType } from '../../tedi-ewok/TediEwok';
import { TediUnknownInvoiceFormatError } from '../TediPdfParser';
import { TediPDFParserLine, TediPDFParserUtils } from './Utils';

// |____________________________________________________________________________________________
// |                                                                                            |
// |                                                                  4 de junio de 2019        |
// |                                                                                            |
// |                                                                                            |
// |                                                                                            |
// |                       |DUQUE DE WELLINGTON 52                                              |
// |                       01010 VITORIA-GASTEIZARABA                                           |                                                                                            |
// |                                                                                            |
// |                                                                                            |
// |       IDENTIFICADOR CUENTA IBAN                              FECHA VENCIMIENTO             |
// |       xxxxxxxxxxxxxxxxxxxxxxxx                               01.02.2019                    |
// |       ____________________________________________________________________________________ |
// |                                                                                            |
// |       TITULAR                             NIF                                              |
// |       xxxxxxx                             xxxx                                             |
// |                                                                                            |
// |                                                                                            |
// |       ____________________________________________________________________________________ |
// |                                                                                            |
// |       Nº FACTURA                   FECHA FACTURA                                           |
// |       xxxxxxx                       01.02.2019                                             |
// |       ____________________________________________________________________________________ |
// |                                                                                            |
// |                                                                                            |
// |                           Base imponible       CUOTA             IMPORTE                   |
// |                                                                                            |
// |       TOTAL FACTURA        104,33              21,91             126,24                    |
// |                                                                                            |
// |      ______________________________________________________________________________________|
// |
// |
export class CaixaBankSolred {
  public static extract(pdf: PDFExtractResult): Invoice {
    const pages: PDFExtractPage[] = pdf.pages;
    // TODO: sort pages by page num ?
    let line0: TediPDFParserLine | undefined;
    let content0: PDFExtractText[] | undefined;
    let match;
    let i = 0;
    for (i = 0; i < pages.length; i++) {
      const page0: PDFExtractPage = pages[i];
      try {
        // TODO: sort content by x and y coordinates ?
        content0 = page0.content;
        if (content0) {
          TediPDFParserUtils.sort(content0);
          line0 = TediPDFParserUtils.getLine(content0);
          if (line0) {
            do {
              match = line0.str.match(/\s*SOLRED,\s*S.A.\s*A\s*79707345\s*/i);
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
      throw new TediUnknownInvoiceFormatError('NOT CAIXA BANK SOLRED INVOICE');
    }

    const invoice: Invoice = {
      type: InvoiceType.RECIBIDA,
      status: InvoiceStatus.inbox,
      verified: true,
      category: InvoiceCategory.C6280,
      sender: {
        address: {
          address: 'C/ Méndez Álvaro, 44,',
          postal_code: '28045',
          city: 'Madrid',
          province: 'Madrid',
          country: 'ES',
        },
        document_country: 'ES',
        document: 'A 79707345',
        name: 'SOLRED,S.A',
      },
    };

    let line: TediPDFParserLine | undefined = line0;
    const content: PDFExtractText[] = content0;
    let index = 0;

    // Go to Página 1
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        if (line.str.match(/\s*P.gina\s*1/i)) {
          break;
        }
      }
    } while (line);

    // Skip incorrect receiver name
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // Receiver Address
    invoice.receiver = {};
    invoice.receiver.address = {};
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      invoice.receiver.address.address = line.str;
    }

    // Receiver: postal code, city
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/(^\d+\s*\d*)\s*(\w*\s*\w*)/i);

        if (match) {
          invoice.receiver.address.postal_code = match[1].replace(/\s+/gi, '');
          invoice.receiver.address.city = match[2].replace(/\s+/gi, '');
          break;
        }
      }
    } while (line);

    // Skip line IBAN
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // Finances: iban, due date
    invoice.finances = [{}];
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      const a: string[] = line.str.split(' ');
      const issueDateStr = a[a.length - 1].replace(/\./gi, '/');
      invoice.finances[0].iban = a.slice(0, 6).join(' ');
      invoice.finances[0].due_date = moment(issueDateStr, 'DD/MM/YYYY').toDate();
    }

    // Skip datos generales
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/\s*generales\s*/i);
        if (match) {
          break;
        }
      }
    } while (line);

    // Receiver name, document, country
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      const a: string[] = line.str.split(' ');
      invoice.receiver.name = a.slice(0, a.length - 1).join(' ');
      invoice.receiver.document = a[a.length - 1];
      invoice.receiver.document_country = 'ES';
    }

    // Skip line lugar
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // Invoice reference, date
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      const a: string[] = line.str.split(' ');
      invoice.reference = a[0];
      const issueDateStr = a[1].replace(/\./gi, '/');
      invoice.date = moment(issueDateStr, 'DD/MM/YYYY').toDate();
    }

    invoice.taxes = [
      {
        tax: TaxType.IVA,
        base: 0,
        quota: 0,
        percentage: 0,
      },
    ];
    // Invoice tax: percentage
    let perc: number = 0;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/\s*(\d+,\d+)\s*(\d+,\d+)\s*%/i);
        if (match) {
          perc = Number.parseFloat(match[2].replace(/,/g, '.'));
          break;
        }
      }
    } while (line);

    // Invoice tax: percentage base quota. Invoice total. Invoice finances amount
    let mainTotal: number = 0;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/\s*total\s*factura\s*(\d+,\d+)\s*(\d+,\d+)\s*(\d+,\d+)/i);
        if (match) {
          invoice.taxes[0] = {
            tax: TaxType.IVA,
            base: Number.parseFloat(match[1].replace(/,/g, '.')),
            quota: Number.parseFloat(match[2].replace(/,/g, '.')),
            percentage: perc,
          };
          mainTotal = Number.parseFloat(match[3].replace(/,/g, '.'));
          invoice.total = mainTotal;
          invoice.finances[0].amount = mainTotal;
          break;
        }
      }
    } while (line);

    // COMMENT INVOICE DETAILS
    // 23.05.2019 E.S. QUINTANAPALLAII QUINTANAPALLA EFITEC 95 N 37,03 52,18
    // invoice.details = [];
    // let disc;
    // let pric;
    // let bas;
    // let desc;
    // let matchDiscount;
    // do {
    //   line = TediPDFParserUtils.getLine(content, index);
    //   if (line) {
    //     if (line.str.match(/CaixaBank/i)) {
    //       break;
    //     }
    //     index = line.index;
    //     match = line.str.match(/(\d+\.\d+\s*\d+.\d+\s*\d+.\d+)\s*(\D+.\D*.\s*\D+\s+)/i);
    //     if (match) {
    //       const a = line.str.split(' ');
    //       pric = Number.parseFloat(a[a.length - 1].replace(/,/g, '.'));
    //       bas = Number.parseFloat(a[a.length - 1].replace(/,/g, '.'));
    //       desc = match[2].trim();
    //     }
    //     // Get line discount
    //     matchDiscount = line.str.match(/\s*Importe:\s*(-?\d*,\d*)/i) || line.str.match(/(\d+:\d+)/i);
    //     if (matchDiscount) {
    //       disc = Number.parseFloat(matchDiscount[1].replace(/,/g, '.'));
    //     }

    //     // If match discount then push new detail
    //     if (matchDiscount) {
    //       matchDiscount = null;
    //       invoice.details.push({
    //         price: pric,
    //         base: bas,
    //         description: desc,
    //         quantity: 1,
    //         discount: disc ? disc : 0,
    //       });
    //     }
    //   }
    // } while (line);

    // Adjust the total amount with taxes
    TediPDFParserUtils.adjustTaxes(invoice.taxes, mainTotal);

    return invoice;
  }
}
