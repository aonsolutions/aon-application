package com.esferalia.aon.occam.impl.jooq.console;

import java.util.Arrays;
import java.util.List;

import org.jooq.Key;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;

abstract class  AbstractKey<R extends Record> implements Key<R> {

    private static final long serialVersionUID = 8176874459141379340L;

    private final String name;
    private final Table<R> table;
    private final TableField<R, ?>[] fields;

    @SafeVarargs
    AbstractKey(Table<R> table, TableField<R, ?>... fields) {
        this(table, null, fields);
    }

    @SafeVarargs
    AbstractKey(Table<R> table, String name, TableField<R, ?>... fields) {
        this.table = table;
        this.name = name;
        this.fields = fields;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final Table<R> getTable() {
        return table;
    }

    @Override
    public final List<TableField<R, ?>> getFields() {
        return Arrays.asList(fields);
    }

    @Override
    public final TableField<R, ?>[] getFieldsArray() {
        return fields;
    }
}
