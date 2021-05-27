package solutions.aon.seg.social.exception.invalid;

import solutions.aon.seg.social.object.Liquidation;

public class LiquidationDoesNotExist extends InvalidDataException {
    public LiquidationDoesNotExist(){}
    public LiquidationDoesNotExist(String msg){super(msg);}
}
