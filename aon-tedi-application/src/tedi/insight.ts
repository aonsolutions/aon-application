import { Insight } from '../tedi-ewok/TediEwok';
import { TediOccam } from '../tedi-occam/TediOccam';

export class TediInsight {
  public static getInsightAsync(document: string): Promise<Insight> {
    return TediOccam.getInsightAsync(document);
  }
}
