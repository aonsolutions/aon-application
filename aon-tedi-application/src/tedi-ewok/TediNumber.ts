import { TediMath } from './TediMath';

/*
-------------------------------------------------------------------------------------------------------
 _______ ______ _____ _____    _____
 |__   __|  ____|  __ \_   _| |  __ \
    | |  | |__  | |  | || |   | |
    | |  |  __| | |  | || |   | |
    | |  | |____| |__| || |_  | |
    |_|  |______|_____/_____| |_|
-------------------------------------------------------------------------------------------------------
*/
export class TediNumber {
  public static formatNumber(value: number | undefined): string {
    if (!value) {
      value = 0;
    }
    let val = TediMath.round(value)
      .toString()
      .replace('.', ',')
      .replace(/\B(?=(\d{3})+(?!\d))/g, '.');
    if (!val.includes(',')) {
      val = val + ',00';
    } else if (val.substring(val.indexOf(','), val.length).length === 2) {
      val = val + '0';
    }
    return val;
  }
}
