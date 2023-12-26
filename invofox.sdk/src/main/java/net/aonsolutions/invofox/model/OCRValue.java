package net.aonsolutions.invofox.model;

import java.math.BigDecimal;
import java.util.Optional;

public interface OCRValue<T> {
    public Optional<T> getValue();
    public Optional<BigDecimal> getConfidence();
}
