export class TediEnumUtil {
  /**
   * Get all keys from enumeration.
   */
  public static keys(enumType: object): string[] {
    return Object.keys(enumType).filter(x => Number.isNaN(parseInt(x, 10)));
  }

  /**
   * Get values from enumeration.
   */
  public static values(enumType: object): string[] {
    return TediEnumUtil.keys(enumType).map(key => enumType[key]);
  }

  /**
   * Get key-value array from enumeration.
   */
  public static toKeyValueArray(enumType: object): any[] {
    return TediEnumUtil.keys(enumType).map(key => {
      return { key, value: enumType[key] };
    });
  }

  private constructor() {}
}
