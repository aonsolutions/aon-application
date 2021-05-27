/*
--------------------------------------------------------------
  _______ ______ _____ _____   __  __       _______ _    _ 
 |__   __|  ____|  __ \_   _| |  \/  |   /\|__   __| |  | |
    | |  | |__  | |  | || |   | \  / |  /  \  | |  | |__| |
    | |  |  __| | |  | || |   | |\/| | / /\ \ | |  |  __  |
    | |  | |____| |__| || |_  | |  | |/ ____ \| |  | |  | |
    |_|  |______|_____/_____| |_|  |_/_/    \_\_|  |_|  |_|
--------------------------------------------------------------
*/
export class TediMath {
  public static defaultZero(value: number | undefined | null): number {
    return TediMath.ensure(value, 0);
  }

  public static ensure(value: number | undefined | null, def?: number): number {
    if (!def) {
      def = 0;
    }
    if (value === undefined || value === null || isNaN(value) || value === Infinity) {
      return def;
    }
    return value;
  }

  public static round(value: number, exp?: number): number {
    if (typeof exp === 'undefined') {
      exp = 2;
    }
    if (typeof exp === 'undefined' || +exp === 0) {
      return Math.round(value);
    }
    value = +value;
    exp = +exp;

    if (isNaN(value) || !(typeof exp === 'number' && exp % 1 === 0)) {
      return NaN;
    }
    // Shift
    let v: string[] = value.toString().split('e');
    value = Math.round(+(v[0] + 'e' + (v[1] ? +v[1] + exp : exp)));

    // Shift back
    v = value.toString().split('e');
    return +(v[0] + 'e' + (v[1] ? +v[1] - exp : -exp));
  }
}
