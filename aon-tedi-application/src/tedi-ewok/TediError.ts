export class TediError extends Error {
  public level?: string;
  public type?: string;
  public code?: string;
  public context?: string;
  public statusCode?: number;
  public time?: Date;
  public details?: string[];

  constructor(message: string, statusCode?: number) {
    super(message);
    this.statusCode = statusCode;
  }
}
