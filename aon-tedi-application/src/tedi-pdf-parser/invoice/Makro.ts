import moment from 'moment';
import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';
import { Invoice, InvoiceCategory, InvoiceStatus, InvoiceType, TaxType, TediString } from '../../tedi-ewok/TediEwok';
import { TediUnknownInvoiceFormatError } from '../TediPdfParser';
import { TediPDFParserLine, TediPDFParserUtils } from './Utils';

//  ___________________________________________________________________________________________________________
// |                                                                           																|
// |    MAKRO AUTOSERVICIO MAYORISTA, S.A.									                                                  |
// |                                                                          																|
// |    Addres sender                                    											                    				  	|
// |    cp sender                                   Fecha de entrega:    11/02/2019 19:57        		          |
// |                                           													                        			        |
// |                                                                                                          |
// |    NIF: xxxxxxxxxxxxx                                                                                    |
// |    _____________________________________________________________________________________________________ |
// |    Name    receiver                                                                       								|
// |    Address receiver                              N.cliente                                           		|
// |    cp city receiver                              N.I.F:                              					    			|
// |    _____________________________________________________________________________________________________ |
// |    N. Artículo   descrip. artículo           Cont    Prec. Ud.   Cont P.   Precio    Cant.   Importe     |
// |    _____________________________________________________________________________________________________ |
// |                                                                          																|
// |                                                                                Importe        331,37			|
// |                                                                                					    						|
// |		                                                            																					|
// |                                                                   Mercancía  % IMP    Total Imp. 				|
// |                                                                                                          |
// |                                                                           																|
// |                                                                   78,14  0= 0,00%       0,00             |
// |                                                                   87,08  1= 3,00%       2,61             |
// |                                                                   166,15  2= 6,50%      10,80            |
// |                                                                  -------------------------------         |
// |                                                                   331,37                13,41            |
// |                                                                   Total a pagar        344,78            |
// |                                                                           																|
// | _________________________________________________________________________________________________________|

export class Makro {
  public static extract(pdf: PDFExtractResult): Invoice {
    const pages: PDFExtractPage[] = pdf.pages;

    // TODO: sort pages by page num ?
    let line0: TediPDFParserLine | undefined;
    let content0: PDFExtractText[] | undefined;

    let i = 0;
    let match;
    let matchNIF;
    let strId: string = '';
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
              matchNIF = line0.str.match(/^NIF\s*:\s*A-28\/647451/i);

              if (matchNIF) {
                strId += line0.str;
              }
              match = line0.str.match(/M-61.688/i);
              // Concact NIF with M-61.688
              if (match) {
                strId += ' ' + match[0];
              }
              if (strId.match(/NIF\s*:\s*A-28\/647451\s*M-61.688/i)) {
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
      throw new TediUnknownInvoiceFormatError('Not MAKRO AUTOSERVICIO MAYORISTA, S.A. invoice');
    }

    const invoice: Invoice = {
      type: InvoiceType.RECIBIDA,
      status: InvoiceStatus.pending,
      category: InvoiceCategory.C6280,
      sender: {
        address: {
          address: 'PASEO IMPERIAL 40',
          postal_code: '28005',
          city: 'MADRID',
          province: 'Madrid',
          country: 'ES',
        },
        document_country: 'ES',
        document: 'A28/647451',
        name: 'MAKRO AUTOSERVICIO MAYORISTA, S.A.',
      },
    };

    let line: TediPDFParserLine | undefined = line0;
    const content: PDFExtractText[] = content0;

    let index = 0;
    invoice.finances = [{}];
    // Invoice date: fecha de venta: 08/02/2019
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/Fecha\s*de\s*venta\s*:\s*(\d+).(\d+).(\d+)/i);
        if (match) {
          const issueDateStr = match[1] + '/' + match[2] + '/' + match[3];
          invoice.date = moment(issueDateStr, 'DD/MM/YYYY').toDate();
          invoice.finances[0].due_date = invoice.date;
          break;
        }
      }
    } while (line);

    // Invoice reference: Factura   0/0(058)0051/(2019)085051
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/^Factura\s*([.\S]*)/i);
        if (match) {
          invoice.reference = match[1];
          break;
        }
      }
    } while (line);

    // Receiver name: CZIPTAK ZSOFIA  N.cliente: 58 012840 1
    invoice.receiver = { name: '' };
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/(.*)\s*N.cliente\s*:\s*(.*)/i);
        if (match) {
          invoice.receiver.name = match[1].trim();
          break;
        }
      }
    } while (line);

    // Receiver NIF and address: MEXICO 5 N.I.F.: Y5549126E
    let addr: string = '';
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/(.*)N.I.F.\s*:\s*(.*)/i);
        if (match) {
          invoice.receiver.document = match[2];
          addr += match[1];
          break;
        }
      }
    } while (line);
    invoice.receiver.address = { address: TediString.polish(addr) };

    // Receiver: postal code, city, country
    let arr: string[] = [];
    line = TediPDFParserUtils.getLine(content, index + 1);
    if (line) {
      index = line.index;
      arr = line.str.split(' ');
      invoice.receiver.address.postal_code = arr[1];
      invoice.receiver.address.city = arr[2];
      invoice.receiver.document_country = 'ES';
    }

    // Skip line: MM Num. artículo
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/MM\s*Num.\s*art.culo/i);
        if (match) {
          break;
        }
      }
    } while (line);

    // COMMENT INVOICE DETAILS
    // There are 2 types of pdf, with different details structure
    // const oldIndex = index;
    // let otherDetails: boolean = true;
    // do {
    //   line = TediPDFParserUtils.getLine(content, index);
    //   if (line) {
    //     index = line.index;
    //     if (line.str.match(/Entregado\s*a\s*:\s*/i)) {
    //       otherDetails = false;
    //       break;
    //     }
    //   }
    // } while (line);

    // invoice.details = [];
    // let a: string[];
    // let innerLine;
    // let descriptionItem: string;

    // /* First type of pdf details
    //    145153         Queso vaca-cabra MAKRO CHEF rulo 1kg
    //                            RL   6,740   1   6,74    1    6,74
    // */
    // if (!otherDetails) {
    //   do {
    //     line = TediPDFParserUtils.getLine(content, index);
    //     if (line) {
    //       index = line.index;
    //       if (line.str.match(/Mercanc.a\s*%/i)) {
    //         break;
    //       }

    //       // Transform detail line to array
    //       a = line.str
    //         .trim()
    //         .replace(/\s{3,}/g, '-')
    //         .split('-');
    //       // Long detail-> 171888   xxxxx   5,750   13,660   78,55    1   78,55    0
    //       if (line.str.match(/([0-9]{5,})/i) && a.length > 3) {
    //         if (a.length > 8) {
    //           this.addDetail(
    //             invoice.details,
    //             TediString.trim(a[1]),
    //             TediPDFParserUtils.string2Number(a[6]),
    //             TediPDFParserUtils.string2Number(a[5]),
    //             TediPDFParserUtils.string2Number(a[7]),
    //             0,
    //           );
    //         } else {
    //           a[1] = a[1].slice(0, a[1].length - 2);
    //           this.addDetail(
    //             invoice.details,
    //             TediString.trim(a[1]),
    //             TediPDFParserUtils.string2Number(a[5]),
    //             TediPDFParserUtils.string2Number(a[4]),
    //             TediPDFParserUtils.string2Number(a[6]),
    //             0,
    //           );
    //         }

    //         // Short detail-> 171888   xxxxx
    //       } else if (line.str.match(/([0-9]{5,})/i) && line.str.indexOf('Fin de número de pedido') < 0) {
    //         descriptionItem = TediString.trim(line.str.replace(/\d{5,}/, ''));

    //         do {
    //           innerLine = TediPDFParserUtils.getLine(content, index);
    //           if (innerLine) {
    //             index = innerLine.index;
    //           }
    //         } while (innerLine.str.length < 2);
    //         a = innerLine.str
    //           .trim()
    //           .replace(/\s{3,}/g, '-')
    //           .split('-');
    //         // Add Detail
    //         this.addDetail(
    //           invoice.details,
    //           descriptionItem,
    //           TediPDFParserUtils.string2Number(a[4]),
    //           TediPDFParserUtils.string2Number(a[3]),
    //           TediPDFParserUtils.string2Number(a[5]),
    //           0,
    //         );
    //       }
    //     }
    //   } while (line);
    // } else {
    //   /* Second type of pdf details (get old index)
    //      Descrip. artículo               Cont    Prec. Ud.   Cont P.    Precio   Cant.   Importe
    //      KKKKKKKKKKKKKKKKKKKKKKKKKKK      RT      2,200        3         6,60      1       6,60
    //   */
    //   index = oldIndex;
    //   do {
    //     line = TediPDFParserUtils.getLine(content, index);
    //     if (line) {
    //       index = line.index;
    //       if (line.str.match(/Mercanc.a\s*%/i)) {
    //         break;
    //       }
    //       if (line.str.match(/^(M\s*[0-9]{5,})|^(\s*[0-9]{5,})/i)) {
    //         a = line.str
    //           .trim()
    //           .replace(/\s{3,}/g, '-')
    //           .split('-');
    //         if (line.str.match(/^[a-z]{1,2}/i)) {
    //           a = a.slice(1);
    //         }
    //         // Add detail
    //         this.addDetail(
    //           invoice.details,
    //           TediString.trim(a[0].replace(/\d{5,}/, '')),
    //           TediPDFParserUtils.string2Number(a[5]),
    //           TediPDFParserUtils.string2Number(a[4]),
    //           TediPDFParserUtils.string2Number(a[6]),
    //           0,
    //         );
    //       }
    //     }
    //   } while (line);
    // }

    // Taxes:  109,24  0= 0,00%   0,00
    invoice.taxes = [];
    let total;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        total = line.str.match(/Total\s*a\s*pagar\s*(-?\d+(,\d+)?)/i);
        if (total) {
          break;
        }
        match = line.str.match(/\s*(-?\d+(,\d+)?)\s*\d=\s*(-?\d+\s*(,\s*\d+)?%)\s*(-?\d+\s*(,\s*\d+)?)/i);
        if (match) {
          invoice.taxes.push({
            tax: TaxType.IVA,
            base: TediPDFParserUtils.string2Number(match[1]),
            percentage: TediPDFParserUtils.string2Number(match[3]),
            quota: TediPDFParserUtils.string2Number(match[5]),
          });
        }
      }
    } while (line);

    // Invoice total
    const mainTotal: number = TediPDFParserUtils.string2Number(total[1]);
    invoice.total = mainTotal;
    invoice.finances[0].amount = mainTotal;

    // Adjust the total amount with taxes
    TediPDFParserUtils.adjustTaxes(invoice.taxes, mainTotal);
    return invoice;
  }

  /**
   * Add invoice's detail function
   */
  public static addDetail(a: any[], des: string, qua: number, pri: number, bas: number, dis: number) {
    a.push({
      description: des,
      quantity: qua,
      price: pri,
      base: bas,
      discount: dis,
    });
  }
}
