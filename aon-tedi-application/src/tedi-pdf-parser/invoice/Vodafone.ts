import moment from 'moment';
import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';
import { Invoice, InvoiceCategory, InvoiceStatus, InvoiceType, TaxType } from '../../tedi-ewok/TediEwok';
import { TediUnknownInvoiceFormatError } from '../TediPdfParser';
import { TediPDFParserLine, TediPDFParserUtils } from './Utils';

export class Vodafone {
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
              // Vodafone.
              match = line0.str.match(
                /\s*Madrid\s*T.\s*34.679,\s*Folio\s*55,\s*Libro\s*0,\s*Secci.n\s*8.,\s*Hoja\s*M-623775,\s*Inscripci.n\s*1.\s*C.I.F\s*B-87539284/i,
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
      throw new TediUnknownInvoiceFormatError('Not VODAFONE invoice');
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
              // match = line0.str.match(/^Nº\s*de\s*factura:\s+([\w-]+)/i); B-87539284
              // match = line0.str.match( /Inscripci.n\s*1ª\s*C.I.F\s*(\w?\W?\d*.)/i );
              // match = line0.str.match(/\s*C.I.F\s*(B-87539284)/i);
              match = line0.str.match(/\S*(B-87539284)/i);
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
      throw new Error('Error line');
    }

    const invoice: Invoice = {
      type: InvoiceType.RECIBIDA,
      status: InvoiceStatus.inbox,
      verified: true,
      category: InvoiceCategory.C6070,
      sender: {
        address: {
          address: 'Avenida de América 115',
          postal_code: '28042',
          city: 'Madrid',
          province: 'Madrid',
          country: 'ES',
        },
        document_country: 'ES',
        document: 'B87539284',
        name: 'Vodafone Servicios, S.L.U.',
      },
    };

    if (match) {
      invoice.reference = match[1].replace('.', '');
    }

    let line: TediPDFParserLine | undefined = line0;
    const content: PDFExtractText[] = content0;
    let index = 0;

    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/Nº\s*de\s*factura:\s*([\w-\d]*)/i);
      if (match) {
        invoice.number = match[1];
      }
    }

    // Nº de cuenta Vodafone: 108571325
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // Ref. domiciliación: 108571325
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // Titular: AON SOLUTIONS SL
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // 85#!$!838#%?¶
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    invoice.receiver = {};
    invoice.receiver.address = {};
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/CIF:\s*(\w?\d*)/i);
      if (match) {
        invoice.receiver.document = match[1];
        invoice.receiver.document_country = 'ES';
      }
    }

    // 01012-05
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // Fecha de emisión: 01/07/2019
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/Fecha\s*de\s*emisi.n\s*:\s*(\d+\/\d+\/\d+)/i);
      if (match) {
        const issueDateStr = match[1] + '/' + match[2] + '/' + match[3];
        invoice.date = moment(issueDateStr, 'DD/MM/YYYY').toDate();
      }
    }

    // AON SOLUTIONS SL
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      invoice.receiver.name = line.str;
    }

    // Lugar de emisión: Madrid
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // Address CL DUQUE DE WELLINGTON 52  BA 1
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      invoice.receiver.address.address = line.str;
    }

    // Empty
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // 01010 VITORIA-GASTEIZ
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      let arr: string[];
      arr = line.str.split(' ');
      invoice.receiver.address.postal_code = arr[0];
      invoice.receiver.address.city = arr[1];
    }

    // Forma de pago: Domiciliación bancaria
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // ALAVA
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // Nº de cuenta: ******9190
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // España
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // Fecha de vencimiento: 12/07/2019
    invoice.finances = [{}];
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/Fecha\s*de\s*vencimiento\s*:\s*(\d+\/\d+\/\d+)/i);
      if (match) {
        const issueDateStr = match[1] + '/' + match[2] + '/' + match[3];
        invoice.finances[0].due_date = moment(issueDateStr, 'DD/MM/YYYY').toDate();
      }
    }

    // Periodo de facturación: 01/06/2019 al 30/06/2019
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    invoice.taxes = [];
    let percent;
    let base;
    let quota;

    // Base imponible (21%) 72.99
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/Base\s*imponible\s*\W?\d+%\W?\s*(\d+,\d+)/i);
      if (match) {
        base = match[1];
      }
    }

    // IVA (21%) 15.33
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/IVA\s*\W?(\d+)%\W?\s*(\d*,\d*)/i);
      percent = match[1];
      quota = match[2];
    }

    invoice.taxes.push({
      tax: TaxType.IVA,
      base: TediPDFParserUtils.string2Number(base),
      quota: TediPDFParserUtils.string2Number(quota),
      percentage: TediPDFParserUtils.string2Number(percent),
    });

    // Total Factura 88,32
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // Total a Pagar 88,32
    line = TediPDFParserUtils.getLine(content, index);
    let mainTotal: number = 0;
    if (line) {
      index = line.index;
      match = line.str.match(/Total\s*a\s*Pagar\s*(\d*,\d*)/i);
      if (match) {
        mainTotal = TediPDFParserUtils.string2Number(match[1]);
        invoice.total = mainTotal;
        invoice.finances[0].amount = mainTotal;
      }
    }

    // invoice.details = [];
    // Tipo de servicio Cuotas Consumos  Total: 18,4400€
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      // match = line.str.match(/Tipo\s*de\s*servicio\s*Cuotas\s*Consumos\s*Total:\s*(\d*,\d*)€/i);
      // if (match) {
      //   invoice.details.push({
      //     price: TediPDFParserUtils.string2Number(match[1]),
      //     base: TediPDFParserUtils.string2Number(match[1]),
      //     description: 'Servicios',
      //     quantity: 1,
      //     discount: 0,
      //   });
      // }
    }

    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // Cuotas a nivel de cuenta Total:  54,5500€
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      // match = line.str.match(/Cuotas\s*a\s*nivel\s*de\s*cuenta\s*Total:\s*(\d*,\d*)€/i);
      // if (match) {
      //   invoice.details.push({
      //     price: TediPDFParserUtils.string2Number(match[1]),
      //     base: TediPDFParserUtils.string2Number(match[1]),
      //     description: 'Cuotas a nivel de cuenta Total',
      //     quantity: 1,
      //     discount: 0,
      //   });
      // }
    }

    // Adjust the total amount with taxes
    TediPDFParserUtils.adjustTaxes(invoice.taxes, mainTotal);

    return invoice;
  }
}
