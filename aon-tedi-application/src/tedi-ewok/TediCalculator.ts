import { TediMath } from './TediMath';
import { Invoice, InvoiceTax, TaxType } from './TediModel';

/* 
-------------------------------------------------------------------------------------------------------
 _______ ______ _____ _____    _____          _      _____ _    _ _            _______ ____  _____  
 |__   __|  ____|  __ \_   _|  / ____|   /\   | |    / ____| |  | | |        /\|__   __/ __ \|  __ \ 
    | |  | |__  | |  | || |   | |       /  \  | |   | |    | |  | | |       /  \  | | | |  | | |__) |
    | |  |  __| | |  | || |   | |      / /\ \ | |   | |    | |  | | |      / /\ \ | | | |  | |  _  / 
    | |  | |____| |__| || |_  | |____ / ____ \| |___| |____| |__| | |____ / ____ \| | | |__| | | \ \ 
    |_|  |______|_____/_____|  \_____/_/    \_\______\_____|\____/|______/_/    \_\_|  \____/|_|  \_\
-------------------------------------------------------------------------------------------------------
*/
export class TediCalculator {
  public static calculate(invoice: Invoice): Invoice {
    invoice.taxes = [];
    if (invoice.details !== undefined) {
      invoice.details.forEach(detail => {
        if (detail.price === undefined || isNaN(detail.price)) {
          detail.price = 0;
        }
        if (detail.quantity === undefined || isNaN(detail.quantity)) {
          detail.quantity = 0;
        }
        let detailBase = detail.price * detail.quantity;
        if (detail.discount !== undefined) {
          detailBase = detailBase - (detailBase * detail.discount) / 100;
        }
        detail.base = TediMath.round(detailBase);
        if (detail.vat !== undefined) {
          const invoiceTax: InvoiceTax = {
            tax: TaxType.IVA,
            base: detail.base,
            percentage: detail.vat,
            quota: 0,
          };
          if (detail.surcharge !== undefined) {
            invoiceTax.surcharge = detail.surcharge;
          }
          TediCalculator.mergeTax(invoice, invoiceTax);
        }
      });
    }
    let total = 0;
    invoice.taxes.forEach(tax => {
      tax.base = TediMath.round(tax.base);
      tax.quota = TediMath.round((tax.base * tax.percentage) / 100);
      let sq = 0;
      if (tax.surcharge) {
        tax.surcharge_quota = TediMath.round((tax.base * tax.surcharge) / 100);
        sq = tax.surcharge_quota;
      }
      total = total + tax.base + tax.quota + sq;
    });
    invoice.total = total;
    return invoice;
  }

  private static mergeTax(invoice: Invoice, tax: InvoiceTax) {
    if (invoice.taxes === undefined) {
      invoice.taxes = [];
    }
    let found = false;
    invoice.taxes.forEach(invoiceTax => {
      if (invoiceTax.tax === tax.tax && invoiceTax.percentage === tax.percentage && invoiceTax.surcharge === tax.surcharge) {
        found = true;
        invoiceTax.base = invoiceTax.base + tax.base;
      }
    });
    if (!found) {
      invoice.taxes.push(tax);
    }
  }
}
