import moment from 'moment';
import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';
import { Invoice, InvoiceCategory, InvoiceStatus, InvoiceType, TaxType } from '../../tedi-ewok/TediEwok';
import { TediUnknownInvoiceFormatError } from '../TediPdfParser';
import { TediPDFParserLine, TediPDFParserUtils } from './Utils';

// |____________________________________________________________________________________________
// |                                                                                            |
// |                                                                                            |
// |                       DUQUE DE WELLINGTON 52                                               |
// |                       01010 VITORIA-GASTEIZARABA                                           |
// |                                                                                            |
// |       IDENTIFICADOR CUENTA IBAN                                                            |
// |                                                                                            |
// |       xxxxxxxxxxxxxxxxxxxxxxxx                                                             |
// |       ____________________________________________________________________________________ |
// |                                                                                            |
// |                                                                                            |
// |       TITULAR                                     NIF                                      |
// |       xxxxxxx                                     xxxx                                     |
// |       ____________________________________________________________________________________ |
// |                                                                                            |
// |                                                                                            |
// |                                                                                            |
// |       Nº FACTURA               PERÍODO            FECHAEMISIÓN                             |
// |       xxxxxxx                  xxxx                30.04.2019                              |
// |                                                                                            |
// |                                                                                            |
// |       ____________________________________________________________________________________ |
// |                                                                                            |
// |                                                                                            |
// |       Base imponible 71,08                                                                 |
// |       I.V.A. aplicable(21%)                                       14,92                    |
// |                                                                                            |
// |                                                                                            |
// |                                                                                            |
// |                                                                                            |
// |                                                                                            |
// |                                                                                            |
// |       TOTAL FACTURA                                               86,00                    |
// |                                                                                            |
// |      _____________________________________________________________________________________ |

export class CaixaBankViat {
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
              match = line0.str.match(/Bip&Drive,\s*\D*\s*S.A.\s*A86969607\s*Calle\s*Serrano\s*45,\s*2\s*planta,\s*2\s*8001\s*-\s*Madrid/i);
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
      throw new TediUnknownInvoiceFormatError('NOT CAIXA BANK VIAT INVOICE');
    }

    const invoice: Invoice = {
      type: InvoiceType.RECIBIDA,
      status: InvoiceStatus.inbox,
      verified: true,
      category: InvoiceCategory.C6070,
      sender: {
        address: {
          address: 'Calle Serrano 45, 2 planta, 28001-Madrid',
          postal_code: '28001',
          city: 'Madrid',
          province: 'Madrid',
          country: 'ES',
        },
        document_country: 'ES',
        document: 'A86969607',
        name: 'Bip&Drive,S.A.',
      },
    };

    let line: TediPDFParserLine | undefined = line0;

    const content: PDFExtractText[] = content0;
    let index = 0;

    // Skip página 1 de 2
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/\s*P.gina\s*1\s*/i);
        if (match) {
          break;
        }
      }
    } while (line);

    // Skip incorrect receiver name: AON SOLU TION S SL
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // Receiver address
    invoice.receiver = {};
    invoice.receiver.address = {};
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      invoice.receiver.address.address = line.str;
    }

    // Postal code, city
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/(\d*\s*\d*)\s*(\w*\s*\w*)/i);
      invoice.receiver.address.postal_code = match[1].replace(/\s+/gi, '');
      invoice.receiver.address.city = match[2].replace(/\s+/gi, '');
    }

    // Go to line: Nº CONTRATO
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        if (line.str.match(/\s*Nº\s*CONTRATO/i)) {
          break;
        }
      }
    } while (line);

    // Finances iban
    invoice.finances = [{}];
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      const a = line.str.split(' ');
      invoice.finances[0].iban = a.slice(0, 6).join(' ');
    }

    // Skip line: datos generales
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

    // Line: receiver name, document, country
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      const a: string[] = line.str.split(' ');
      invoice.receiver.name = a.slice(0, a.length - 1).join(' ');
      invoice.receiver.document = a[a.length - 1];
      invoice.receiver.document_country = 'ES';
    }

    // Skip fecha emisión
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/\s*FECHA\s*EMISI.N\s*/i);
        if (match) {
          break;
        }
      }
    } while (line);

    // Invoice number, date
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      const a: string[] = line.str.split(' ');
      invoice.reference = a[0];
      const issueDateStr = a[a.length - 1].replace(/\./gi, '/');
      invoice.date = moment(issueDateStr, 'DD/MM/YYYY').toDate();
      invoice.finances[0].due_date = invoice.date;
    }

    // Base "imponibl e" xx,xx
    let bas: number = 0;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/\s*base\s+imponibl\s*e\s*(\d+,\d+)/i) || line.str.match(/\s*TOTAL\s*.antes\s*de\s*impuestos.\s*(\d+,\d+)/i);
        if (match) {
          bas = Number.parseFloat(match[1].replace(/,/g, '.'));
          break;
        }
      }
    } while (line);

    // I.V. A. aplicable (21%) 10,36
    invoice.taxes = [];
    let perc: number = 0;
    let quote: number = 0;
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/\s*I\s*.\s*V.\s*A\s*.\s*aplicable\s*.(\d+)%.\s*(\d+,\d+)/i);
      if (match) {
        perc = Number.parseFloat(match[1].replace(/,/g, '.'));
        quote = Number.parseFloat(match[2].replace(/,/g, '.'));
        invoice.taxes = [
          {
            tax: TaxType.IVA,
            base: bas,
            quota: quote,
            percentage: perc,
          },
        ];
      }
    }

    // Invoice total: 59,65
    let mainTotal: number = 0;
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/\s*TOTAL\s*FACTURA\s*(-?\d+,\d+)/i);
      mainTotal = Number.parseFloat(match[1].replace(/,/g, '.'));
      invoice.total = mainTotal;
      invoice.finances[0].amount = mainTotal;
    }

    /************************ SEARCH IN SECOND PAGE ********************************/

    // COMMENT INVOICE DETAILS
    // let content1;
    // index = 0;
    // try {
    //   const page = pages[1];
    //   content1 = page.content;
    //   if (content1) {
    //     TediPDFParserUtils.sort(content1);
    //   }
    // } catch (error) {
    //   throw new Error('Error content1 in page 2: ' + error);
    // }

    // // Invoice's details
    // invoice.details = [];
    // let totalPeajes;
    // let totalParkings;
    // let descriptlMatch;
    // let desc: string = '';
    // do {
    //   line = TediPDFParserUtils.getLine(content1, index);
    //   if (line) {
    //     index = line.index;
    //     // Check detail peajes
    //     descriptlMatch = line.str.match(/\s*(\w*)\s*PEAJES\s*AUTOPISTAS$/i);
    //     if (descriptlMatch) {
    //       desc = line.str;
    //     }
    //     totalPeajes = line.str.match(/\s*TOTAL\s*PEAJES\s*(-?\d+,\d+)/i);

    //     // Check detail parkings
    //     descriptlMatch = line.str.match(/\s*(\w*)\s*PARKINGS$/i);
    //     if (descriptlMatch) {
    //       desc = line.str;
    //     }
    //     totalParkings = line.str.match(/\s*TOTAL\s*PARKINGS\s*(-?\d+,\d+)/i);

    //     if (totalPeajes) {
    //       invoice.details.push({
    //         description: desc,
    //         quantity: 1,
    //         price: TediPDFParserUtils.string2Number(totalPeajes[1]),
    //         discount: 0,
    //         base: TediPDFParserUtils.string2Number(totalPeajes[1]),
    //       });
    //     } else if (totalParkings) {
    //       invoice.details.push({
    //         description: desc,
    //         quantity: 1,
    //         price: TediPDFParserUtils.string2Number(totalParkings[1]),
    //         discount: 0,
    //         base: TediPDFParserUtils.string2Number(totalParkings[1]),
    //       });
    //     }
    //   }
    // } while (line);

    // Adjust the total amount with taxes
    TediPDFParserUtils.adjustTaxes(invoice.taxes, mainTotal);

    return invoice;
  }
}
