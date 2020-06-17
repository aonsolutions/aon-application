import moment from 'moment';
import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';
import { Invoice, InvoiceCategory, InvoiceStatus, InvoiceType, TaxType } from '../../tedi-ewok/TediEwok';
import { TediUnknownInvoiceFormatError } from '../TediPdfParser';
import { TediPDFParserLine, TediPDFParserUtils } from './Utils';

//  ___________________________________________________________________________________________________________
// |                                                                           																|
// |   Datos del cliente                                             																          |
// |   Titular                                                          																      |
// |   CIF/NIF                                                                                                |
// |   Dirección                                                             																  |
// |                                                                         																  |
// |   Información factura                                                   																  |
// |   Id factura                         																                                    |
// |   Fecha emisión                         																                                  |
// |   Fecha Vencimiento                                     																                  |
// |   Fecha de emisión de factura dd de MMM de yyyy                           																|
// |  _______________________________________________                                                         |
// |   TOTAL                         999,99 €                                                                 |
// |   IVA 21%                       999,99 €                                                									|
// |   TOTAL FACTURA                 999,99 €                                                                 |
//  __________________________________________________________________________________________________________|
// |                                                                           																|
// |  Detalles    Importe     Descuento                            																            |
// |  TOTAL       28,18         €0,00                                                                         |
// |                                                                           																|
// |                                                                           																|
// | _________________________________________________________________________________________________________|

export class BipDrive {
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
              match = line0.str.match(/Bip&Drive,\s*E.D.E..\s*S.A.\s*\w*\s*Calle\s*Serrano\s*45,\s*2\s*planta,\s*2\s*8001\s*-\s*Madrid/i);
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
      throw new TediUnknownInvoiceFormatError('NOT Bip&Drive INVOICE');
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

    // Receiver name: Titular JOSEP COMAS ARNAU
    invoice.receiver = {};
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/\s*Titular\s*(\D*)/i);
        if (match) {
          invoice.receiver.name = match[1];

          break;
        }
      }
    } while (line);

    // Receiver document: CIF/NIF 39311407Z
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/\s*CIF\/NIF\s*(\w*-?\w*)/i);
        if (match) {
          invoice.receiver.document = match[1];
          invoice.receiver.document_country = 'ES';
          break;
        }
      }
    } while (line);

    // Receiver address : Dirección El Ojáncano, 23,
    invoice.receiver.address = {};
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/\s*Direcci.n\s*(.*)/i);
        if (match) {
          const str = line.str.replace('Dirección', '').trim();
          invoice.receiver.address.address = str;
          break;
        }
      }
    } while (line);

    // Receiver postal code, city
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/^(\d{5,5})\s*-\s*(\w*)/i);
        if (match) {
          invoice.receiver.address.postal_code = match[1];
          invoice.receiver.address.city = match[2];
          break;
        }
      }
    } while (line);

    // Invoice reference: Id de la factura CI0002464605-0819
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/Id\s*de\s*la\s*factura\s*(\w*-?\w*)/i);
        if (match) {
          invoice.reference = match[1];
          break;
        }
      }
    } while (line);

    // Invoice date: Fecha emisión 31/08/2019
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/Fecha\s*emisi.n\s*(\d+\/\d+\/\d+)/i);
        if (match) {
          const issueDateStr = match[1] + '/' + match[2] + '/' + match[3];
          invoice.date = moment(issueDateStr, 'DD/MM/YYYY').toDate();
          break;
        }
      }
    } while (line);

    // Due date: Fecha Vencimiento 06/09/2019
    invoice.finances = [{}];
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/Fecha\s*Vencimiento\s*(\d+\/\d+\/\d+)/i);
        if (match) {
          const issueDateStr = match[1] + '/' + match[2] + '/' + match[3];
          invoice.finances[0].due_date = moment(issueDateStr, 'DD/MM/YYYY').toDate();
          break;
        }
      }
    } while (line);

    // Invoice tax base:  Total ( antes de impuestos ) 43,18 €
    invoice.taxes = [];
    let base = '';
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/Total\s*.\s*antes\s*de\s*impuestos\s*.\s*(\d+,\d+)\s*€/i);
        if (match) {
          base = match[1];
          break;
        }
      }
    } while (line);

    // Invoice tax type: IVA 21% 9,07 €
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/IVA\s*(\d+)\s*%\s*(\d+,\d+)\s*€/i);
        if (match) {
          invoice.taxes.push({
            tax: TaxType.IVA,
            base: TediPDFParserUtils.string2Number(base),
            quota: TediPDFParserUtils.string2Number(match[2]),
            percentage: TediPDFParserUtils.string2Number(match[1]),
          });
          break;
        }
      }
    } while (line);

    // Invoice total: TOTAL FACTURA 52,25 €
    let mainTotal: number = 0;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/TOTAL\s*FACTURA\s*(\d+,\d+)\s*€/i);
        if (match) {
          mainTotal = TediPDFParserUtils.string2Number(match[1]);
          invoice.total = mainTotal;
          invoice.finances[0].amount = mainTotal;
          break;
        }
      }
    } while (line);

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

    // // Detail description: peajes autopistas
    // let descriptlMatch;
    // let desc;
    // do {
    //   line = TediPDFParserUtils.getLine(content1, index);
    //   if (line) {
    //     index = line.index;
    //     descriptlMatch = line.str.match(/Peajes\s*autopistas/i);
    //     if (descriptlMatch) {
    //       desc = line.str;
    //       break;
    //     }
    //   }
    // } while (line);

    // // TOTAL  28,18 €   0,00 €
    // invoice.details = [];
    // do {
    //   line = TediPDFParserUtils.getLine(content1, index);
    //   if (line) {
    //     index = line.index;
    //     match = line.str.match(/\s*TOTAL\s*(\d+,\d+)\s*€\s*(\d+,\d+)\s*€/i);
    //     if (match) {
    //       invoice.details.push({
    //         price: TediPDFParserUtils.string2Number(match[1]),
    //         base: TediPDFParserUtils.string2Number(match[1]),
    //         quantity: 1,
    //         description: desc,
    //         discount: TediPDFParserUtils.string2Number(match[2]),
    //       });
    //       break;
    //     }
    //   }
    // } while (line);

    // Adjust the total amount with taxes
    TediPDFParserUtils.adjustTaxes(invoice.taxes, mainTotal);

    return invoice;
  }
}
