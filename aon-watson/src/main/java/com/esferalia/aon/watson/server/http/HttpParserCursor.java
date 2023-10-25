package com.esferalia.aon.watson.server.http;

/**
 * This class represents a context of a parsing operation:
 * <ul>
 *  <li>the current position the parsing operation is expected to start at</li>
 *  <li>the bounds limiting the scope of the parsing operation</li>
 * </ul>
 *
 * @since 4.0
 */
class HttpParserCursor {

    private final int lowerBound;
    private final int upperBound;
    private int pos;

    HttpParserCursor(final int lowerBound, final int upperBound) {
        super();
        if (lowerBound < 0) {
            throw new IndexOutOfBoundsException("Lower bound cannot be negative");
        }
        if (lowerBound > upperBound) {
            throw new IndexOutOfBoundsException("Lower bound cannot be greater then upper bound");
        }
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.pos = lowerBound;
    }

    int getLowerBound() {
        return this.lowerBound;
    }

    int getUpperBound() {
        return this.upperBound;
    }

    int getPos() {
        return this.pos;
    }

    void updatePos(final int pos) {
        if (pos < this.lowerBound) {
            throw new IndexOutOfBoundsException("pos: "+pos+" < lowerBound: "+this.lowerBound);
        }
        if (pos > this.upperBound) {
            throw new IndexOutOfBoundsException("pos: "+pos+" > upperBound: "+this.upperBound);
        }
        this.pos = pos;
    }

    boolean atEnd() {
        return this.pos >= this.upperBound;
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append('[');
        buffer.append(Integer.toString(this.lowerBound));
        buffer.append('>');
        buffer.append(Integer.toString(this.pos));
        buffer.append('>');
        buffer.append(Integer.toString(this.upperBound));
        buffer.append(']');
        return buffer.toString();
    }

}
