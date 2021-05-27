import moment from 'moment';
import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';
import { Invoice, InvoiceCategory, InvoiceStatus, InvoiceType, TaxType } from '../../tedi-ewok/TediEwok';
import { TediUnknownInvoiceFormatError } from '../TediPdfParser';
import { TediPDFParserLine, TediPDFParserUtils } from './Utils';

export class Securitas {
  public static extract(pdf: PDFExtractResult): Invoice {
    const pages: PDFExtractPage[] = pdf.pages;

    // TODO: sort pages by page num ?
    let line0: TediPDFParserLine | undefined;
    let content0: PDFExtractText[] | undefined;
    let matchCIF;
    let match;

    let i = 0;
    let preLineStr = '';
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
              matchCIF = line0.str.match(/C.I.F.\s*(A26106013)/i);

              if (matchCIF) {
                preLineStr += ' ' + line0.str;
              }
              match = preLineStr.match(/www.securitasdirect.es\s*C.I.F.\s*A26106013\s*(.*)/i);
              if (match) {
                break;
              }
              preLineStr = line0.str;
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
      throw new TediUnknownInvoiceFormatError('NOT SECURITAS INVOICE');
    }

    const invoice: Invoice = {
      type: InvoiceType.RECIBIDA,
      status: InvoiceStatus.inbox,
      verified: true,
      category: InvoiceCategory.C6280,
      sender: {
        address: {
          address: 'C/ Priégola, 2',
          postal_code: '28224',
          city: 'Madrid',
          province: 'Madrid',
          country: 'ES',
        },
        document_country: 'ES',
        document: 'A26106013',
        name: 'Securitas Direct España, SAU',
      },
    };

    if (matchCIF) {
      invoice.reference = matchCIF[1];
    }

    let line: TediPDFParserLine | undefined = line0;
    const content: PDFExtractText[] = content0;
    let index = 0;
    invoice.receiver = {};

    do {
      // 25376850F CIF / NIF:
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/(\w*)\s*CIF\s*\W?\s*NIF:/i);
        if (match) {
          invoice.receiver.document = match[1];
          invoice.receiver.document_country = 'ES';
          break;
        }
      }
    } while (line);

    // IDEABLE SOLUTIONS S.L.
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      invoice.receiver.name = line.str.replace('  ', ' ');
    }

    // CALLE PONIENTE 8 PUERTA 37 Dirección:
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    invoice.receiver.address = {};
    // CALLE LA VIRTUD 1 LONJA IZQUIERDA
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      invoice.receiver.address.address = line.str;
    }

    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // 48901 BARAKALDO VIZCAYA
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      let arr: string[];
      arr = line.str.split(' ');
      invoice.receiver.address.postal_code = arr[0];
      invoice.receiver.address.city = arr[1];
    }

    // BARAKALDO - VIZCAYA Población - Provincia:
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // 1901C00720261 Nº de factura:
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/(\w*)\s*Nº\s*de\s*factura:/i);
      if (match) {
        invoice.number = match[1];
      }
    }

    // 01/01/2019 Fecha de factura:
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/\s*(\d+)(\D+)(\d+)(\D+)(\d+)\s*Fecha\s*de\s*factura:/i);
      if (match) {
        const dateStr = match[1] + '/' + TediPDFParserUtils.month(match[2]) + '/' + match[3];
        invoice.date = moment(dateStr, 'DD/MM/YYYY').toDate();
      } else {
        line = TediPDFParserUtils.getLine(content, index);
        if (line) {
          index = line.index;
          match = line.str.match(/\s*(\d+)(\D+)(\d+)(\D+)(\d+)\s*Fecha\s*de\s*factura:/i);
          if (match) {
            const dateStr = match[1] + '/' + TediPDFParserUtils.month(match[2]) + '/' + match[3];
            invoice.date = moment(dateStr, 'DD/MM/YYYY').toDate();
          }
        }
      }
    }

    // ======================  PAGINA 2  ====================== //
    const content1 = pdf.pages[1].content;
    index = 3;

    // ================================ DETAILS ================================ //
    // invoice.details = [];
    // let text;
    do {
      line = TediPDFParserUtils.getLine(content1, index);
      if (line) {
        index = line.index;
        match = line.str.match(/\s*Sello/i);
        if (match) {
          break;
        }
        //  else {
        //   match = line.str.match(/^[+-]?[0-9]{1,9}(?:,[0-9]{1,2})?/i);
        //   if (match) {
        //     let arr: string[];
        //     arr = line.str.split(' ');
        //     for (i = 0; i < arr.length; i++) {
        //       if (arr[i] === '€') {
        //         arr.splice(i, 1);
        //       }
        //     }

        //     invoice.details.push({
        //       price: TediPDFParserUtils.string2Number(arr[1]),
        //       quantity: TediPDFParserUtils.string2Number(arr[0]),
        //       base: TediPDFParserUtils.string2Number(arr[1]),
        //       discount: 0,
        //       description: text.replace('  ', ' '),
        //     });
        //   } else {
        //     text = line.str;
        //   }
        // }
      }
    } while (line);

    let base;
    let quota;
    let percent;

    // ================================ TAXES ================================ //
    invoice.taxes = [];
    line = TediPDFParserUtils.getLine(content1, index);
    if (line) {
      index = line.index;
      base = TediPDFParserUtils.string2Number(line.str);
    }

    line = TediPDFParserUtils.getLine(content1, index);
    if (line) {
      index = line.index;
    }

    line = TediPDFParserUtils.getLine(content1, index);
    if (line) {
      index = line.index;
    }

    line = TediPDFParserUtils.getLine(content1, index);
    if (line) {
      index = line.index;
    }

    line = TediPDFParserUtils.getLine(content1, index);
    if (line) {
      index = line.index;
      quota = TediPDFParserUtils.string2Number(line.str);
    }

    line = TediPDFParserUtils.getLine(content1, index);
    if (line) {
      index = line.index;
      match = line.str.match(/IVA\s*(\d*)\W?/i);
      if (match) {
        percent = match[1];
        invoice.taxes.push({
          tax: TaxType.IVA,
          base,
          quota,
          percentage: TediPDFParserUtils.string2Number(percent),
        });
      }
    }

    let mainTotal: number = 0;
    do {
      line = TediPDFParserUtils.getLine(content1, index);
      if (line) {
        index = line.index;
        match = line.str.match(/^(-?\d+(,\d+)?\s*.)$/i);
        if (match) {
          mainTotal = TediPDFParserUtils.string2Number(match[1]);
          invoice.total = mainTotal;
          break;
        }
      }
    } while (line);

    // Adjust the total amount with taxes
    TediPDFParserUtils.adjustTaxes(invoice.taxes, mainTotal);

    return invoice;
  }
}
