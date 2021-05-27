import moment from 'moment';
import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';
import { Invoice, InvoiceCategory, InvoiceStatus, InvoiceType, TaxType, TediString } from '../../tedi-ewok/TediEwok';
import { TediUnknownInvoiceFormatError } from '../TediPdfParser';
import { TediPDFParserLine, TediPDFParserUtils } from './Utils';

//  ___________________________________________________________________________________________________________
// |                                                                           																|
// |   IBERDROLA COMERCIALIZACIÓN DE ÚLTIMO RECURSO,S.A.U.                     																|
// |   CIF A-95758389                                                          																|
// | _________________________________________________________________________________________________________|

export class IberdrolaCUR {
  public static extract(pdf: PDFExtractResult): Invoice {
    const pages: PDFExtractPage[] = pdf.pages;
    // TODO: sort pages by page num ?
    let line0: TediPDFParserLine | undefined;
    let content0: PDFExtractText[] | undefined;
    let match0;
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
              // IBERDROLA COMERCIALIZACIÓN DE ÚLTIMO RECURSO
              match0 = line0.str.match(
                /\s*Bizkaia\s*al\s*Tomo\s*5015\s*.\s*Folio\s*19\s*.\s*Hoja\s*BI-53822\s*.\s*inscripci.n\s*1.\s*-\s*CIF\s*A-95554630/i,
              );
              if (match0) {
                break;
              }
              line0 = TediPDFParserUtils.getLine(content0, line0.index);
            } while (line0);
            if (match0) {
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
      throw new TediUnknownInvoiceFormatError('Not Iberdroal CUR  invoice');
    }

    const invoice: Invoice = {
      type: InvoiceType.RECIBIDA,
      status: InvoiceStatus.inbox,
      verified: true,
      category: InvoiceCategory.C6280,
      sender: {
        address: {
          address: 'Plaza Euskadi 5',
          postal_code: '48009',
          city: 'Bilbao',
          province: 'Bizkaia',
          country: 'ES',
        },
        document_country: 'ES',
        document: 'A95554630',
        name: 'IBERDROLA COMERCIALIZACIÓN DE ÚLTIMO RECURSO,S.A.U.',
      },
    };

    let line: TediPDFParserLine | undefined = line0;
    let content: PDFExtractText[] = content0;

    let index = 0;

    // IMPORTE FACTURA:      999,99 €
    invoice.finances = [{}];
    let mainTotal: number = 0;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/IMPORTE\s*FACTURA\s*:\s*(-?\d+(,\d+)?)/i);
        if (match) {
          mainTotal = TediPDFParserUtils.string2Number(match[1]);
          invoice.total = mainTotal;
          invoice.finances[0].amount = mainTotal;

          break;
        }
      }
    } while (line);

    // Número de factura 99999999999999999
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/^N.\s*Factura:\s*(.*)\s*emitida\s*el\s*(\d+)\s*de\s*(\D+)\s*de\s*(\d+)/i);

        if (match) {
          invoice.reference = TediString.trim(match[1]);
          const issueDateStr = match[2] + '/' + TediPDFParserUtils.month(match[3]) + '/' + match[4];
          invoice.date = moment(issueDateStr, 'DD/MM/YYYY').toDate();

          break;
        }
      }
    } while (line);

    // Fecha prevista de cargo dd/MM/yyyy
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/Fecha\s*de\s*cargo:\s*(\d+)\s*de\s*(\D+)\s*de\s*(\d+)/i);
        if (match) {
          const issueDateStr = match[1] + '/' + TediPDFParserUtils.month(match[2]) + '/' + match[3];
          invoice.finances[0].due_date = moment(issueDateStr, 'DD/MM/YYYY').toDate();
          break;
        }
      }
    } while (line);

    /* Comment details
    // Por potencia contratada      999,99 €
    invoice.details = [];
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/Por\s*potencia\s*contratada\s*(-?\d+(,\d+)?)/i);
        if (match) {
          invoice.details.push({
            description: 'Por potencia contratada',
            quantity: 1,
            price: TediPDFParserUtils.string2Number(match[1]),
            discount: 0,
            base: TediPDFParserUtils.string2Number(match[1]),
          });
          break;
        }
      }
    } while (line);

    // Por energía consumida      999,99 €
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/Por\s*energ.a\s*consumida\s*(-?\d+(,\d+)?)/i);
        if (match) {
          invoice.details.push({
            description: 'Por energía consumida',
            quantity: 1,
            price: TediPDFParserUtils.string2Number(match[1]),
            discount: 0,
            base: TediPDFParserUtils.string2Number(match[1]),
          });
          break;
        }
      }
    } while (line);

    // Impuesto electricidad      999,99 €
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/Impuesto\s*electricidad\s*(-?\d+(,\d+)?)/i);
        if (match) {
          invoice.details.push({
            description: 'Por energía consumida',
            quantity: 1,
            price: TediPDFParserUtils.string2Number(match[1]),
            discount: 0,
            base: TediPDFParserUtils.string2Number(match[1]),
          });
          break;
        }
      }
    } while (line);

    // Alquiler equipos medida y control      999,99 €
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/Alquiler\s*equipos\s*medida\s*y\s*control\s*(-?\d+(,\d+)?)/i);
        if (match) {
          invoice.details.push({
            description: 'Alquiler equipos medida y control',
            quantity: 1,
            price: TediPDFParserUtils.string2Number(match[1]),
            discount: 0,
            base: TediPDFParserUtils.string2Number(match[1]),
          });
          break;
        }
      }
    } while (line);
    End comments */

    // IVA 99% s/999,99 €                   999,99 €
    invoice.taxes = [
      {
        tax: TaxType.IVA,
        quota: 0,
        base: 0,
        percentage: 0,
      },
    ];

    // 21% s/10
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/(-?\d+(,\d+)?)\s*%\s*s\/\s*(-?\d+(,\d+)?)\s*/i);
        if (match) {
          const taxPercentage = TediPDFParserUtils.string2Number(match[1]);
          invoice.taxes[0].percentage = taxPercentage;
          invoice.taxes[0].base = TediPDFParserUtils.string2Number(match[3]);

          /* Comment details
          invoice.details.forEach(detail => (detail.vat = taxPercentage));
          */
          break;
        }
      }
    } while (line);
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/IVA\s*(-?\d+(,\d+)?)/i);
        if (match) {
          invoice.taxes[0].quota = TediPDFParserUtils.string2Number(match[1]);
          break;
        }
      }
    } while (line);

    // Titular: XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    invoice.receiver = {};
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/Titular\s*:\s*(.+)/i);
        if (match) {
          invoice.receiver.name = match[1];
          break;
        }
      }
    } while (line);

    // NIF: XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/NIF\s*:\s*(.+)/i);
        if (match) {
          invoice.receiver.document_country = 'ES';
          invoice.receiver.document = match[1];
          break;
        }
      }
    } while (line);

    // Dirección de suministro: XXXXXXXXXXXXXXXXXXXXXXXXXX
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/Direcci.n\s*de\s*suministro\s*:\s*(.*)/i);
        if (match) {
          invoice.receiver.address = { address: TediString.polish(match[1]) };
          break;
        }
      }
    } while (line);

    let financeMatch;
    for (i++; i < pages.length; i++) {
      const page = pages[i];
      try {
        // TODO: sort content by x and y coordinates ?
        content = page.content;
        TediPDFParserUtils.sort(content);
        line = TediPDFParserUtils.getLine(content);
        if (line) {
          do {
            index = line.index;
            financeMatch = line.str.match(/^Entidad\s*:\s*(.*)/i);
            if (financeMatch) {
              break;
            }
            line = TediPDFParserUtils.getLine(content, index);
          } while (line);
        }
        if (financeMatch) {
          break;
        }
      } catch (e) {
        // tslint:disable-next-line: no-console
        console.error(' Catched!!' + e);
      }
    }

    if (financeMatch) {
      do {
        line = TediPDFParserUtils.getLine(content, index);
        if (line) {
          index = line.index;
          const match = line.str.match(/IBAN\s*:\s*(.*)/i);
          if (match) {
            invoice.finances[0].iban = match[1];
            break;
          }
        }
      } while (line);
    }

    // Adjust the total amount with taxes
    TediPDFParserUtils.adjustTaxes(invoice.taxes, mainTotal);

    return invoice;
  }
}
