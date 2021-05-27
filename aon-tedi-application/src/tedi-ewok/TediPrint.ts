/*
------------------------------------------------------------------
  _______ ______ _____ _____   _____  _____  _____ _   _ _______
 |__   __|  ____|  __ \_   _| |  __ \|  __ \|_   _| \ | |__   __|
    | |  | |__  | |  | || |   | |__) | |__) | | | |  \| |  | |
    | |  |  __| | |  | || |   |  ___/|  _  /  | | | . ` |  | |
    | |  | |____| |__| || |_  | |    | | \ \ _| |_| |\  |  | |
    |_|  |______|_____/_____| |_|    |_|  \_\_____|_| \_|  |_|
------------------------------------------------------------------
*/
import { Company, Invoice, User } from './TediModel';
import { TediString } from './TediString';

export class TediPrint {
  // tslint:disable: no-console
  public static printUser(user: User): void {
    console.log(JSON.stringify(user, null, 1));
  }
  public static printCompany(company: Company): void {
    console.log(JSON.stringify(company, null, 1));
  }
  public static printInvoice(invoice: Invoice): void {
    const length: number = 100;
    console.log('\x1b[32m%s\x1b[0m', TediString.pad('INVOICE: [' + invoice.uuid + ']', length));
    console.log();
    console.log(
      TediString.pad(
        invoice.type +
          ' Ser/Nº: ' +
          invoice.series +
          '/' +
          invoice.number +
          (invoice.reference !== undefined ? ' Ref: ' + invoice.reference : '') +
          (invoice.status !== undefined ? ' Estado: ' + invoice.status : '') +
          (invoice.source !== undefined ? ' Source: ' + invoice.source : ''),
        length,
      ),
    );
    console.log(TediString.pad('Fecha ' + (invoice.date == null ? '' : invoice.date.toLocaleDateString()), length));
    console.log(TediString.pad('[' + invoice.transaction + ']' + (invoice.category !== undefined ? ' Categoría: ' + invoice.category : ''), length));
    if (invoice.sender) {
      console.log(
        'SENDER: ' +
          invoice.sender.document_country +
          '/' +
          invoice.sender.document +
          ' - ' +
          invoice.sender.name +
          (invoice.sender.address !== undefined
            ? invoice.sender.address.address +
              ', ' +
              invoice.sender.address.city +
              ' ' +
              invoice.sender.address.postal_code +
              ' (' +
              invoice.sender.address.province +
              ') ' +
              invoice.sender.address.country
            : ''),
      );
      console.table(invoice.sender.address);
    }
    if (invoice.receiver) {
      console.log(
        'RECEIVER: ' +
          invoice.receiver.document_country +
          '/' +
          invoice.receiver.document +
          ' - ' +
          invoice.receiver.name +
          (invoice.receiver.address !== undefined
            ? invoice.receiver.address.address +
              ', ' +
              invoice.receiver.address.city +
              ' ' +
              invoice.receiver.address.postal_code +
              ' (' +
              invoice.receiver.address.province +
              ') ' +
              invoice.receiver.address.country
            : ''),
      );
    }
    console.table(invoice.details);
    console.table(invoice.taxes);
    console.table({ total: invoice.total });
    console.table(invoice.finances);
    console.log(TediString.repeat('-', length));
  }
}
