/*
-------------------------------------------------------------------------
  _______ ______ _____ _____    _____ _______ _____  _____ _   _  _____ 
 |__   __|  ____|  __ \_   _|  / ____|__   __|  __ \|_   _| \ | |/ ____|
    | |  | |__  | |  | || |   | (___    | |  | |__) | | | |  \| | |  __ 
    | |  |  __| | |  | || |    \___ \   | |  |  _  /  | | | . ` | | |_ |
    | |  | |____| |__| || |_   ____) |  | |  | | \ \ _| |_| |\  | |__| |
    |_|  |______|_____/_____| |_____/   |_|  |_|  \_\_____|_| \_|\_____|
-------------------------------------------------------------------------
*/
import v from 'voca';

export class TediString {
  public static defaultEmpty(subject: string | undefined | null): string {
    return subject && subject !== undefined && subject !== null ? subject : '';
  }

  public static defaultBlank(subject: string | undefined | null): string {
    return subject && subject !== undefined && subject !== null ? subject : ' ';
  }

  public static isBlank(subject: string | undefined | null): boolean {
    return !subject || subject === undefined || subject === null || TediString.trim(subject) === '';
  }

  public static pad(subject?: string, length?: number, pad?: string): string {
    return v.pad(subject, length, pad);
  }

  public static trim(subject?: string): string {
    return v.trim(subject);
  }

  public static repeat(subject?: string, times?: number): string {
    return v.repeat(TediString.defaultBlank(subject), times);
  }

  public static polish(subject?: string): string {
    if (!subject) {
      return '';
    }
    subject = subject.replace(/\s+/g, ' ');
    return v.trim(subject);
  }
}
