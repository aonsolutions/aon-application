import moment from 'moment';
import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';
import { Invoice, InvoiceCategory, InvoiceStatus, InvoiceType, TaxType } from '../../tedi-ewok/TediEwok';
import { TediUnknownInvoiceFormatError } from '../TediPdfParser';
import { TediPDFParserLine, TediPDFParserUtils } from './Utils';

export class Endesa {
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
              // Endesa.
              match = line0.str.match(/\s*Secci.n\s*8.\s*.\s*Hoja\s*M-205.381\s*.\s*CIF\s*A81948077/i);
              if (match) {
                break;
              }
              // Endesa.
              match = line0.str.match(/\s*Secci.n\s*8.\s*.\s*Hoja\s*272.593\s*.\s*CIF\s*B-82846825/i);
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
      throw new TediUnknownInvoiceFormatError('Not Endesa invoice');
    }

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
              match = line0.str.match(/CIF\s*(A81948077)/i);
              if (match) {
                break;
              }
              match = line0.str.match(/Cif:\s*(B82846825)/i);
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
      }
    }

    if (!line0) {
      throw Error('Error line');
    }

    const invoice: Invoice = {
      type: InvoiceType.RECIBIDA,
      status: InvoiceStatus.inbox,
      verified: true,
      category: InvoiceCategory.C6280,
      sender: {
        address: {
          address: 'C/ Ribera del Loira 60.',
          postal_code: '28042',
          city: 'Madrid',
          province: 'Madrid',
          country: 'ES',
        },
        document_country: 'ES',
        document: match[1],
        name: 'Endesa Energía, S.A.U.',
      },
    };

    if (match) {
      invoice.reference = match[1];
    }

    let line: TediPDFParserLine | undefined = line0;
    const content: PDFExtractText[] = content0;
    let index = 0;

    invoice.finances = [{}];
    do {
      // Datos de la factura
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/Endesa Energ.a/i);
        if (match) {
          invoice.number = match[1];
          break;
        }

        match = line.str.match(/Nº\s*\de\s*factura:\s*(\w*)/i);
        if (match) {
          invoice.number = match[1];
        }

        match = line.str.match(/Fecha\s*de\s*cargo:\s*(\d*)\s*de\s*(\w*)\s*de\s*(\d*)/i);
        if (match) {
          const dateStr = match[1] + '/' + TediPDFParserUtils.month(match[2]) + '/' + match[3];
          invoice.finances[0].due_date = moment(dateStr, 'DD/MM/YYYY').toDate();
        } else {
          match = line.str.match(/Fecha\s*de\s*cargo:\s*(\d*)\W*(\d*)\W*(\d*)/i);
          if (match) {
            const dateStr = match[1] + '/' + match[2] + '/' + match[3];
            invoice.finances[0].due_date = moment(dateStr, 'DD/MM/YYYY').toDate();
          }
        }

        match = line.str.match(/Fecha\s*emisi.n\s*factura:\s(\d*)\W*(\d*)\W*(\d*)/i);
        if (match) {
          const dateStr = match[1] + '/' + match[2] + '/' + match[3];
          invoice.date = moment(dateStr, 'DD/MM/YYYY').toDate();
        }
      }
    } while (line);

    // ===================================                    =================================== //

    invoice.receiver = {};
    invoice.receiver.address = {};
    let iva;
    let percent;
    let mainTotal: number = 0;
    // CIF Endesa B82846825 | A81948077
    if (invoice.reference === 'B82846825') {
      // Cif: B82846825
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
      }

      // Name
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        invoice.receiver.name = line.str;
        invoice.receiver.document_country = 'ES';
      }

      // Address endesa
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
      }

      // Address  Receiver
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        invoice.receiver.address.address = line.str;
      }

      // CP + CITY
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/(\d*){1,5}\s*(\d){0,1}\s*([\w\W]+)/i);
        if (match.length === 4) {
          invoice.receiver.address.postal_code = match[1] + match[2];
        } else {
          invoice.receiver.address.postal_code = match[1];
        }

        invoice.receiver.address.city = match[3];
        line = TediPDFParserUtils.getLine(content, index);
        if (line) {
          index = line.index;
          invoice.receiver.address.province = line.str;
        }
      }

      // ==================== DETALLES ==================== //

      // Factura Resumen
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
      }

      invoice.taxes = [];
      // invoice.details = [];
      do {
        line = TediPDFParserUtils.getLine(content, index);
        if (line) {
          index = line.index;
          match = line.str.match(/IVA\s*normal\s*\W?(\d*)\W?\W?\s*(\d*,\d*)\W?/i);
          if (match) {
            iva = match[2];
            percent = match[1];
            break;
          }

          // match = line.str.match(/(\D+)\s*(\d*,\d*)\s*\W?/i);
          // if (match) {
          //   invoice.details.push({
          //     price: TediPDFParserUtils.string2Number(match[2]),
          //     base: TediPDFParserUtils.string2Number(match[2]),
          //     description: match[1],
          //     quantity: 1,
          //     discount: 0,
          //   });
          // }
        }
      } while (line);

      // Dots
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
      }

      // Total Importe
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/TOTAL\s*IMPORTE\s*FACTURA\s*(\d*,\d*)\W?/i);
        mainTotal = TediPDFParserUtils.string2Number(match[1]);
        invoice.total = mainTotal;
        invoice.finances[0].amount = mainTotal;

        if (match) {
          invoice.taxes.push({
            tax: TaxType.IVA,
            base: TediPDFParserUtils.string2Number(match[1]) - TediPDFParserUtils.string2Number(iva),
            quota: TediPDFParserUtils.string2Number(iva),
            percentage: TediPDFParserUtils.string2Number(percent),
          });
        }
      }

      do {
        line = TediPDFParserUtils.getLine(content, index);
        if (line) {
          index = line.index;
          match = line.str.match(/NIF:\s*(B23741903)/i);
          if (match) {
            invoice.receiver.document = match[1];
            break;
          }
          match = line.str
            .replace(' de ', ' ')
            .replace('de ', ' ')
            .replace('  ', '')
            .match(/Fecha\s*emisi.n\s*factura\s*:\s*(\d+)\s*(\D+)\s*(\d+)/i);
          if (match) {
            const dateStr = match[1] + '/' + TediPDFParserUtils.month(match[2]) + '/' + match[3];
            invoice.date = moment(dateStr, 'DD/MM/YYYY').toDate();
          }
        }
      } while (line);
    } else {
      // Endesa:  CIF A81948077.
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
      }

      // - ARUNDEL S.L
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        invoice.receiver.name = line.str.replace('- ', '');
      }

      // Endesa-Address: C/Ribera del Loira, nº 60 28042 - Madrid.
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
      }

      let address;
      // TRAMONTANA 21-A 3 Addres-part-1
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        address = line.str;
      }

      // 28223 HUMERA-SOMOSAGUAS-PRADO DEL RE Addres-part-2
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/(\d*){1,5}(\D*)/i);
        invoice.receiver.address.address = address + match[2];
        invoice.receiver.address.postal_code = match[1];
      }

      // RESUMEN DE LA FACTURA Y DATOS DE PAGO
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
      }

      invoice.taxes = [];
      // invoice.details = [];
      do {
        line = TediPDFParserUtils.getLine(content, index);
        if (line) {
          index = line.index;
          match = line.str.match(/IVA\s*normal\s*\W?(\d*)\W?\W?\s*(\d*,\d*)\W?/i);
          if (match) {
            iva = match[2];
            percent = match[1];
            break;
          }

          // match = line.str.match(/(\D+)\s*(\d*,\d*)\s*\W?/i);
          // if (match) {
          //   invoice.details.push({
          //     price: TediPDFParserUtils.string2Number(match[2]),
          //     base: TediPDFParserUtils.string2Number(match[2]),
          //     description: match[1].replace(' -', '').replace(' ', ''),
          //     quantity: 1,
          //     discount: 0,
          //   });
          // }
        }
      } while (line);

      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
      }

      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
      }

      // Total Importe

      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/TOTAL\s*IMPORTE\s*FACTURA\s*(\d*,\d*)\W?/i);

        // Total Importe
        mainTotal = TediPDFParserUtils.string2Number(match[1]);
        invoice.total = mainTotal;
        invoice.finances[0].amount = mainTotal;

        if (match) {
          invoice.taxes.push({
            tax: TaxType.IVA,
            base: Number.parseFloat((TediPDFParserUtils.string2Number(match[1]) - TediPDFParserUtils.string2Number(iva)).toPrecision(5)),
            quota: TediPDFParserUtils.string2Number(iva),
            percentage: TediPDFParserUtils.string2Number(percent),
          });
        }
      }

      // ======================  PAGINA 2  ====================== //

      const content1 = pdf.pages[1].content;
      index = 0;

      do {
        line = TediPDFParserUtils.getLine(content1, index);
        if (line) {
          index = line.index;
          match = line.str.match(/NIF:\s*(\w*)/i);
          if (match) {
            invoice.receiver.document = match[1];
            invoice.receiver.document_country = 'ES';
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
