package solutions.aon.seg.social.exceptions.invalidData;

import solutions.aon.seg.social.objects.Liquidation;

public class LiquidationDoesNotExist extends InvalidDataException {
    public LiquidationDoesNotExist(){}
    public LiquidationDoesNotExist(String msg){super(msg);}
}
