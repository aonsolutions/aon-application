import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';
import { Observable, Observer } from 'rxjs';
import { Company, Invoice, InvoiceStatus, InvoiceTax, InvoiceType, Nif, NifType, TaxType, TediImportInvoicesInfo } from '../../tedi-ewok/TediEwok';
// import { TediRegistry } from '../../tedi/registry';

export class AutoML {
  public static predict(info: TediImportInvoicesInfo, result: PDFExtractResult): Observable<Invoice> {
    return Observable.create((observer: Observer<Invoice>) => {
      const invoice: Invoice = {
        type: InvoiceType.RECIBIDA,
        status: InvoiceStatus.photonmilkbath,
      };

      const nifs: Nif[] = AutoML.getNifs(result, invoice);
      // tslint:disable-next-line: no-console
      // console.log(`NIFs: ${JSON.stringify(nifs)} :-( !!!!`);
      invoice.type = nifs.length <= 1 ? InvoiceType.TICKET : invoice.type;

      const docs: string[] = nifs.map((nif: Nif) => nif.str);

      (info.companies || [])
        .filter((company: Company) => docs.includes(company.document))
        .forEach((company: Company) => {
          invoice.receiver = {
            name: company.name,
            address: company.address,
            document: company.document,
            document_country: 'ES',
          };
          invoice.company = company.document;
        });

      invoice.insight = {
        nifs,
        dates: AutoML.getDates(result),
        amounts: AutoML.getAmounts(result),
        taxTypes: AutoML.getTaxTypes(result),
        references: AutoML.getReferences(result), // flat(references),
      };

      // tslint:disable-next-line: no-console
      // console.log(`INVOICE: ${JSON.stringify(invoice)} :-( !!!!`);

      const senders: Nif[] = nifs.filter((nif: Nif) => invoice.company !== nif.str);
      invoice.sender = {
        document_country: 'ES',
        document: senders[0] && senders[0].str,
      };

      // TediRegistry.getRegistries(senders.map(s => s.str))
      //   .then(registries => {
      //     registries.forEach(registry => {
      //       invoice.sender = registry;
      //     });
      //     observer.next(invoice);
      //     observer.complete();
      //   })
      //   .catch(reason => {
      //     observer.next(invoice);
      //     observer.complete();
      //   });

      observer.next(invoice);
      observer.complete();

      // const pages: number = Math.max(result.pages.length, 5);
    });
  }

  public static insight(invoice: Invoice): Invoice {
    AutoML.insightAmounts(invoice);
    AutoML.insightDates(invoice);
    AutoML.insightReferences(invoice);
    invoice.status = InvoiceStatus.inbox;

    return invoice;
  }

  private static getNifs(result: PDFExtractResult, invoice: Invoice): Nif[] {
    const nifs: Nif[] = [];

    result.pages
      .map((page: PDFExtractPage) => page.content)
      .reduce((acc, val) => acc.concat(val), [])
      .map((text: PDFExtractText) => Nif.find(text.str))
      .reduce((acc, val) => acc.concat(val), [])
      .reduce((map: Map<string, Nif>, nif: Nif) => map.set(nif.str, nif), new Map())
      .forEach((nif: Nif, num: string) => nifs.push(nif));
    const orders: Map<NifType, number> = new Map();
    orders.set(NifType.CIF, 1);
    orders.set(NifType.NIF, 2);
    orders.set(NifType.DNI, 3);
    orders.set(NifType.NIE, 4);

    const order = (nif: Nif): number => {
      return orders.get(nif.type) || 5;
    };

    nifs.sort((n1, n2) => order(n1) - order(n2));

    return nifs;
  }

  //   private static insight(invoice: Invoice, threshold: number): Invoice {
  //     let iva: AutoMLInvoiceAmount | undefined;
  //     let base: AutoMLInvoiceAmount | undefined;
  //     let total: AutoMLInvoiceAmount | undefined;

  //     ((invoice.automl_tables && invoice.automl_tables.amounts) || []).forEach((amount: AutoMLInvoiceAmount) => {
  //       iva = ((amount.target_IVA_score || 0.0) >= Math.max(threshold, (iva && iva.target_IVA_score) || 0.0) && amount) || iva;
  //       base = ((amount.target_BASE_score || 0.0) >= Math.max(threshold, (base && base.target_BASE_score) || 0.0) && amount) || base;
  //       total = ((amount.target_TOTAL_score || 0.0) >= Math.max(threshold, (total && total.target_TOTAL_score) || 0.0) && amount) || total;
  //     });
  //     if (total && iva && base) {
  //       // A happy world :-)
  //       invoice.total = total.amount;
  //       invoice.taxes = [
  //         {
  //           tax: TaxType.IVA,
  //           base: base.amount,
  //           quota: iva.amount,
  //           percentage: +((100 * iva.amount) / base.amount).toFixed(2),
  //         },
  //       ];
  //     }
  //     if (total) {
  //       invoice.total = total.amount;
  //       if (base) {
  //         // then iva wasn't found :-(
  //         const quota = total.amount - base.amount;
  //         invoice.taxes = [
  //           {
  //             tax: TaxType.IVA,
  //             base: base.amount,
  //             quota,
  //             percentage: +((100 * quota) / base.amount).toFixed(2),
  //           },
  //         ];
  //       }
  //       if (iva) {
  //         // then base wasn't found :-(
  //         const bas3 = total.amount - iva.amount;
  //         invoice.taxes = [
  //           {
  //             tax: TaxType.IVA,
  //             base: bas3,
  //             quota: iva.amount,
  //             percentage: +((100 * iva.amount) / bas3).toFixed(2),
  //           },
  //         ];
  //       }
  //     }

  //     return invoice;
  //   }

  private static getAmounts(result: PDFExtractResult): number[] {
    const amounts: number[] = [];

    const text: string = AutoML.getFullText(result);
    // tslint:disable-next-line: no-console
    // console.log(text);

    const re: RegExp = /((-\s*)?\d+\s*([\.|,]\s*(\d)+){1,2}-?)/gim;

    let match: RegExpMatchArray | null = re.exec(text);
    while (match !== null) {
      // XXX XX,XX => XXXXX,XX
      let str: string = match[0].replace(/\s/g, '').replace(/(.*)-$/, '-$1'); // postfix

      // XXX.XXX,XX => XXXXXX.XX
      if (str.match(/,/)) {
        str = str.replace(/\./g, '').replace(/,/g, '.');
      }

      match = re.exec(text);

      // clean mismatched
      if (/\d+\.\d+\.\d+/g.test(str)) {
        continue;
      }

      // if (/\d+\.\d{3}/g.test(str)) {
      //   continue;
      // }

      const amount: number = parseFloat(str);
      if (!amount) {
        continue;
      }

      amounts.push(amount);
    }

    return amounts;
  }

  private static getDates(result: PDFExtractResult): Date[] {
    const text: string = AutoML.getFullText(result);
    return AutoML.extractDates(text);
  }

  private static getTaxTypes(result: PDFExtractResult): TaxType[] {
    const taxTypes: TaxType[] = [];
    const text: string = AutoML.getFullText(result);
    if (text.match(/I\s*\.?\s*V\s*\.?\s*A/gim)) {
      taxTypes.push(TaxType.IVA);
    }
    if (text.match(/I\s*\.?\s*R\s*\.?\s*P\s*\.?\s*F/gim)) {
      taxTypes.push(TaxType.IRPF);
    }
    return taxTypes;
  }

  private static getReferences(result: PDFExtractResult, document?: string): string[] {
    const text: string = AutoML.getFullText(result);

    const references: string[] = [];

    // serie / number

    const patterns: string[] = [];
    // if (document) {
    //   const insight: Insight = await TediInsight.getInsightAsync(document);
    //   patterns = insight.references || [];
    // }
    patterns.push('FACTURA\\s*(?:SIMPLIFICADA|N\\S?)?\\s*:?\\s*([a-z0-9_/-]*[0-9][a-z0-9_/-]{4,})');
    // if (patterns.length === 0) {
    //   patterns = ['facturas*N?S?:?s*([a-z0-9/_-]{6,})'];
    // }

    for (const pattern of patterns) {
      const re: RegExp = new RegExp(pattern, 'gim');
      let match: RegExpMatchArray | null = re.exec(text);
      while (match !== null) {
        references.push(match[1] || match[0]);
        match = re.exec(text);
      }
    }
    // tslint:disable-next-line: no-console
    // console.log(`${document}: ${patterns} : ${text}`);

    return references;
  }
  private static getFullText(result: PDFExtractResult): string {
    return result.pages
      .map((page: PDFExtractPage) => page.content)
      .reduce((acc: PDFExtractText[], val: PDFExtractText[]) => acc.concat(val), [])
      .map((text: PDFExtractText) => text.str)
      .join(' ');
  }

  private static extractDates(text: string): Date[] {
    const dates: Date[] = [];
    // tslint:disable-next-line: no-console
    // console.log(`${text}`);

    let re: RegExp = /(0?[1-9]|[12][0-9]|3[01])\s*[-/]\s*(0?[1-9]|1[012])\s*[-/]\s*(20\d{2}|([12][0-9]))/gim;
    let match: RegExpMatchArray | null = re.exec(text);
    while (match !== null) {
      // tslint:disable-next-line: no-console
      // console.log(`MATCH : ${JSON.stringify(match)}`);
      const day: number = parseInt(match[1], 10);
      const month: number = parseInt(match[2], 10) - 1;
      const year: number = match[4] ? parseInt(`20${match[4]}`, 10) : parseInt(match[3], 10);
      match = re.exec(text);

      const date: Date = new Date(year, month, day);

      if (!date) {
        continue;
      }

      dates.push(date);
    }

    const months = {
      enero: 0,
      en: 0,
      febrero: 1,
      feb: 1,
      marzo: 2,
      mar: 2,
      abril: 3,
      abr: 3,
      mayo: 4,
      may: 4,
      junio: 5,
      jun: 5,
      julio: 6,
      jul: 6,
      agosto: 7,
      ag: 7,
      agto: 7,
      septiembre: 8,
      sep: 8,
      sept: 8,
      octubre: 9,
      oct: 9,
      noviembre: 10,
      nov: 10,
      diciembre: 11,
      dic: 11,
    };

    re = /(0?[1-9]|[12][0-9]|3[01])\s*(?:de|\/)\s*([a-z]+\.?)\s*(?:de|\/)\s*(20\d{2}|([12][0-9]))/gim;
    match = re.exec(text);
    while (match !== null) {
      const day: number = parseInt(match[1], 10);
      const month: number = months[match[2].toLowerCase()];
      const year: number = match[4] ? parseInt(`20${match[4]}`, 10) : parseInt(match[3], 10);

      match = re.exec(text);

      const date: Date = new Date(year, month, day);

      if (!date) {
        continue;
      }

      dates.push(date);
    }

    return dates;
  }

  private static insightAmounts(invoice: Invoice): Invoice {
    const iamounts: number[] = (invoice.insight && invoice.insight.amounts) || [];
    const map: Map<number, number> = iamounts.reduce((m: Map<number, number>, n: number) => m.set(n, (m.get(n) || 0) + 1), new Map());

    const percentages: number[] = [21.0, 10.0, 4.0];

    const amounts: number[] = Array.from(map.keys()).sort((a1, a2) => Math.abs(a2) - Math.abs(a1));
    // .filter((a, i, arr) => i === 0 || arr[i - 1] !== a)

    for (let i: number = 0; i < amounts.length - 2; i++) {
      const total: number = amounts[i];
      for (const percentage of percentages) {
        //
        // total = base + quota
        // total = base + ( base * percentage / 100.00 )
        // total = base ( 1 + ( 1 * percentage / 100.00 ))
        // base = total /  ( 1 + percentage / 100.00 )
        //
        // yes you can replace +(..).toFixed(2) by Math.round(...*100)/100
        //
        const base: number = total / (1 + percentage / 100.0);
        const indexOfBase: number = AutoML.indexOf(amounts, base, i + 1);
        if (indexOfBase >= 0) {
          const quota: number = total - base;
          const indexOfQuota: number = AutoML.indexOf(amounts, quota, i + 1);
          if (indexOfQuota >= 0) {
            invoice.total = total;
            invoice.taxes = [
              {
                tax: TaxType.IVA,
                base: amounts[indexOfBase],
                quota: amounts[indexOfQuota],
                percentage,
              },
            ];
            break;
          } else {
            // tslint:disable-next-line: no-console
            // console.log(`IVA ${quota} not found at ${amounts} :-( !!!!`);
          }
        } else {
          // tslint:disable-next-line: no-console
          // console.log(`BASE ${base} not found at ${amounts} :-( !!!!`);
        }
      }
      if (invoice.total) {
        break;
      }
    }

    // tslint:disable-next-line: no-console
    // console.log(`${JSON.stringify(invoice.taxes)} :-)`);

    if (invoice.sender && invoice.sender.document_country && invoice.sender.document_country !== 'ES' && amounts.length > 0) {
      invoice.total = Array.from(map.entries()).sort((e1: [number, number], e2: [number, number]) => e2[1] - e1[1] || e2[0] - e1[0])[0][0];
    }

    const linesTotals: number[] = [];
    const linesTaxes: InvoiceTax[] = [];
    // 'total = base + iva' not found try 'total - base'
    for (const percentage of percentages) {
      for (let i: number = 0; i < amounts.length - 1; i++) {
        const total: number = amounts[i];
        const base: number = total / (1 + percentage / 100.0);

        const indexOfBase: number = AutoML.indexOf(amounts, base, i + 1);
        if (indexOfBase >= 0) {
          linesTotals.push(total);
          linesTaxes.push({
            tax: TaxType.IVA,
            base: amounts[indexOfBase],
            quota: +(total - amounts[indexOfBase]).toFixed(2),
            percentage,
          });
        } else {
          // tslint:disable-next-line: no-console
          // console.log(`BASE ${base} not found at ${amounts} :-( !!!!`);
        }
      }

      if (linesTotals.length > 0) {
        // if (invoice.total) {
        const total: number = linesTotals.reduce((a: number, t: number) => a + t, 0.0);

        const indexOfTotalLines: number = AutoML.indexOf(amounts, total, 0);
        if (indexOfTotalLines >= 0 && (invoice.total || 0.0) < amounts[indexOfTotalLines]) {
          invoice.total = amounts[indexOfTotalLines];
          const taxesMap: Map<number, InvoiceTax> = linesTaxes.reduce((m: Map<number, InvoiceTax>, line: InvoiceTax) => {
            const tax: InvoiceTax = m.get(line.percentage) || {
              tax: TaxType.IVA,
              base: 0.0,
              quota: 0.0,
              percentage: line.percentage,
            };
            tax.base += line.base;
            tax.quota += line.quota;
            m.set(line.percentage, tax);
            return m;
          }, new Map());
          invoice.taxes = Array.from(taxesMap.values());
        } else if (!invoice.total) {
          invoice.total = linesTotals[0];
          invoice.taxes = [linesTaxes[0]];
        }
        break;
      }
    }
    // tslint:disable-next-line: no-console
    // console.log(`${JSON.stringify(invoice.taxes)} :-)`);
    let taxesTotal: number = 0.0;
    const taxes: InvoiceTax[] = [];
    // 'total = base + iva' not found try 'base + iva'
    for (const percentage of percentages) {
      for (let i: number = 0; i < amounts.length - 1; i++) {
        const base: number = amounts[i];
        const quota: number = (base * percentage) / 100.0;
        const indexOfQuota: number = AutoML.indexOf(amounts, quota, i + 1);
        if (indexOfQuota >= 0) {
          // invoice.total = +(base + quota).toFixed(2);
          taxes.push({
            tax: TaxType.IVA,
            base,
            quota: amounts[indexOfQuota],
            percentage,
          });
          taxesTotal += base + amounts[indexOfQuota];
          // next percentage ?
          break;
        } else {
          // tslint:disable-next-line: no-console
          // console.log(`IVA ${quota} not found at ${amounts} :-( !!!!`);
        }
      }
    }
    // tslint:disable-next-line: no-console
    // console.log(`${JSON.stringify(invoice)} :-)`);
    const indexOfTotal: number = AutoML.indexOf(amounts, taxesTotal, 0);
    if (invoice.total === undefined) {
      invoice.taxes = taxes;
      invoice.total = indexOfTotal >= 0 ? amounts[indexOfTotal] : +taxesTotal.toFixed(2);
    } else if (indexOfTotal >= 0 && amounts[indexOfTotal] > invoice.total) {
      invoice.taxes = taxes;
      invoice.total = amounts[indexOfTotal];
    }

    // tslint:disable-next-line: no-console
    // console.log(`${JSON.stringify(invoiceamountstaxes)} :-(`);
    if (!invoice.total) {
      const allAmounts: number[] = iamounts.sort((a1: number, a2: number) => a2 - a1);
      for (let i = 0; i < 1; i++) {
        if (AutoML.sumOf(allAmounts, i + 1, allAmounts[i]) > 0) {
          invoice.total = allAmounts[i];
          break;
        }
      }
    }

    if (!invoice.total && amounts.length > 0) {
      // tslint:disable-next-line: no-console
      // console.log(`${JSON.stringify(amounts)} :-(`);
      invoice.total = Array.from(map.entries()).sort((e1: [number, number], e2: [number, number]) => e2[1] - e1[1] || e2[0] - e1[0])[0][0];
    }

    if (invoice.type === InvoiceType.TICKET) {
      return invoice;
    }

    const knownAmounts = (invoice.taxes || []).reduce((a: Map<number, number>, tax: InvoiceTax) => {
      a.set(tax.base, 1);
      a.set(tax.quota, 1);
      return a;
    }, new Map());
    knownAmounts.set(invoice.total, 1);
    const unknownAmounts: number[] = amounts.filter((a: number) => !knownAmounts.has(a)).sort((a1: number, a2: number) => a1 - a2);

    // IVA 0%
    for (let i: number = 0; i < unknownAmounts.length; i++) {
      const indexOfZeroTotal = AutoML.indexOf(unknownAmounts, unknownAmounts[i] + invoice.total, i + 1);
      if (indexOfZeroTotal >= 0) {
        invoice.total = unknownAmounts[indexOfZeroTotal];
        invoice.taxes = invoice.taxes || [];
        invoice.taxes.push({
          tax: TaxType.IVA,
          base: unknownAmounts[i],
          quota: 0.0,
          percentage: 0.0,
        });
        break;
      }
    }

    if (!invoice.insight!.taxTypes.includes(TaxType.IRPF)) {
      return invoice;
    }

    // tslint:disable-next-line: no-console
    // console.log(`${JSON.stringify(invoice)} :-(`);

    //
    // total = base + iva -irpf
    //

    for (let i: number = 0; i < unknownAmounts.length; i++) {
      const indexOfIrpfTotal: number = AutoML.indexOf(unknownAmounts, invoice.total - unknownAmounts[i], i + 1);
      if (indexOfIrpfTotal >= 0) {
        invoice.taxes = invoice.taxes || [];
        const quota: number = unknownAmounts[i];
        const base: number = invoice.taxes.reduce((b: number, tax: InvoiceTax) => tax.base, 0.0);
        const percentage: number = +((quota / base) * 100).toFixed(2);
        invoice.taxes.push({
          tax: TaxType.IRPF,
          base,
          quota,
          percentage,
        });
        invoice.total = unknownAmounts[indexOfIrpfTotal];
        break;
      }
    }
    // tslint:disable-next-line: no-console
    // console.log(`${JSON.stringify(invoice)} :-(`);

    return invoice;
  }

  private static indexOf(amounts: number[], amount: number, start: number, delta?: number): number {
    let indexOf: number = -1;
    let diffOf: number = 0.5;
    delta = delta || (amount > 0.1 ? 0.019 : 0.0019);

    for (let i: number = start; i < amounts.length; i++) {
      const diff: number = Math.abs(amounts[i] - amount);
      if (diff < delta && diff < diffOf) {
        indexOf = i;
        diffOf = diff;
      }
    }
    return indexOf;
  }

  private static sumOf(arr: number[], i: number, sum: number): number {
    if (Math.abs(sum) <= 0.01) {
      // tslint:disable-next-line: no-console
      // console.log(`${sum} ${arr.slice(i)} :-(`);
      return 1;
    }
    if (i >= arr.length) {
      return sum === 0 ? 1 : 0;
    }
    // tslint:disable-next-line: no-console
    // console.log(`${sum} ${arr.slice(i)} :-(`);

    return AutoML.sumOf(arr, i + 1, sum) || AutoML.sumOf(arr, i + 1, sum - arr[i]);
  }

  private static insightDates(invoice: Invoice): Invoice {
    // const today: Date = new Date();

    const idates: Date[] = (invoice.insight && invoice.insight.dates) || [];

    // tslint:disable-next-line: no-console
    // console.log(`${JSON.stringify(idates)} :-(`);

    const dates: Date[] = idates
      // .filter((d: Date) => (today.getTime() - d.getTime()) / (3600 * 1000 * 24) < 182)
      // .slice(0, 2)
      .sort((d1, d2) => d2.getTime() - d1.getTime())
      .filter((d, i, arr) => i === 0 || arr[i - 1].getTime() !== d.getTime());

    // tslint:disable-next-line: no-console
    // console.log(`${JSON.stringify(dates)} :-(`);

    let date: Date | undefined;
    let dueDate: Date | undefined;

    if (dates.length === 1) {
      date = dates[0];
      dueDate = dates[0];
    } else if (dates.length > 1) {
      date = dates[1];
      dueDate = dates[0];
    }

    invoice.date = date;
    invoice.finances = [
      {
        due_date: dueDate,
        // amount: number,
        // pay_method: PayMethod,
        // iban: string,
        // pending: boolean,
      },
    ];

    return invoice;
  }

  private static insightReferences(invoice: Invoice): Invoice {
    let references: string[] = (invoice.insight && invoice.insight.references) || [];
    references = references.filter(r => AutoML.extractDates(r).length === 0);
    for (const reference of references) {
      const length: number = (invoice.reference && invoice.reference.length) || 0;
      invoice.reference = reference.length > length ? reference : invoice.reference;
    }

    // if (invoice.insight && invoice.insight.references && invoice.insight.references.length > 0) {
    //   invoice.reference = invoice.insight.references[0];
    // }
    return invoice;
  }
}
