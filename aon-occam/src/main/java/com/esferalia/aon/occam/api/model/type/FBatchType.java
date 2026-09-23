package com.esferalia.aon.occam.api.model.type;

public enum FBatchType {
    CHARGE          ((byte)0, new byte[]{ 0, 8, 12, 13 })
  , PAYMENT         ((byte)1, new byte[]{ 0, 9 })
  , PAYROLL_PAYMENT ((byte)1, new byte[]{ 10 })
  ;

  private final byte payment;
  private final byte[] selectableCodes;

  private FBatchType(byte payment, byte[] selectableCodes) {
      this.payment = payment;
      this.selectableCodes = selectableCodes;
  }

  public byte getPayment() { return payment; }

  /** Tipos ofrecidos al crear o editar. NO es el filtro del listado:
   *  existen tipos heredados (1,2,4,5,6,7,11,14) que deben seguir viéndose. */
  public byte[] getSelectableCodes() { return selectableCodes; }

  public boolean isSelectable(Byte code) {
      if (code == null) return false;
      for (byte c : selectableCodes) if (c == code) return true;
      return false;
  }

  public static FBatchType of(Byte payment, Byte code) {
      if (payment == null) return null;
      if (payment == (byte)1)
          return (code != null && code == (byte)10) ? PAYROLL_PAYMENT : PAYMENT;
      return CHARGE;
  }

  public String getFileTypeDescription(Byte code) {
      if (code == null) return "Desconocido";
      switch (code) {
          case 0:  return this == CHARGE ? "Ninguno" : "VISA";
          case 8:  return "SEPA 19-14 CORE (XML)";
          case 9:  return "SEPA 34-14 (XML)";
          case 10: return "SEPA 34-14 N\u00f3mina (XML)";
          case 12: return "SEPA 58 ANTICIPO (XML)";
          case 13: return "SEPA 58 COBRO (XML)";
          // TODO tipos heredados: 1, 2, 4, 5, 6, 7, 11, 14
          default: return "Tipo " + code;
      }
  }

  /** El código 0 ("Ninguno" / "VISA") no produce fichero. */
  public static boolean generatesFile(Byte code) {
      return code != null && code.byteValue() != (byte) 0;
  }

  public static boolean requiresBankAccount(Byte code) { return generatesFile(code); }
  public static boolean isSepaFile(Byte code)          { return generatesFile(code); }

  /** Forma de pago exigida al vencimiento. En cobros siempre domiciliación. */
  public PayMethodType expectedPayMethod(Byte code) {
      if (this == CHARGE) return PayMethodType.NEGOTIABLE_DOCUMENT;
      return generatesFile(code) ? PayMethodType.BANK_TRANSFER : null;
  }

  /** Solo el 19-14 limita los vencimientos a la fecha de la remesa. */
  public static boolean dueDateLimitedByIssueDate(Byte code) {
      return code != null && code.byteValue() == (byte) 8;
  }
}