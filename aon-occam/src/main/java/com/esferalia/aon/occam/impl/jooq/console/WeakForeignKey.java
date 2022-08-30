package com.esferalia.aon.occam.impl.jooq.console;

import java.util.Collection;
import java.util.List;

import org.jooq.Comment;
import org.jooq.Constraint;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;

final class WeakForeignKey<R extends Record, O extends Record> extends AbstractKey<R> implements ForeignKey<R, O> {

    private static final String NOT_IMPLEMENTED = "Not implemented";

	private static final long serialVersionUID = 3636724364192618701L;

    private final UniqueKey<O> key;

    @SafeVarargs
    WeakForeignKey(UniqueKey<O> key, Table<R> table, String name, TableField<R, ?>... fields) {
        super(table, name, fields);
        this.key = key;
    }

    @Override
    public final UniqueKey<O> getKey() {
        return key;
    }

    @Override
    public final O fetchParent(R rec) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    @SafeVarargs
    public final Result<O> fetchParents(R... records) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public final Result<R> fetchChildren(O rec) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    @SafeVarargs
    public final Result<R> fetchChildren(O... records) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public final Result<O> fetchParents(Collection<? extends R> records) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public final Result<R> fetchChildren(Collection<? extends O> records) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public Constraint constraint() {
        return DSL.constraint(getName()).foreignKey(getFieldsArray()).references(key.getTable(),
                key.getFieldsArray());
    }

	@Override
	public boolean enforced() {
		return false;
	}

	@Override
	public boolean nullable() {
		return false;
	}

	@Override
	public Name getQualifiedName() {
		return null;
	}

	@Override
	public Name getUnqualifiedName() {
		return null;
	}

	@Override
	public String getComment() {
		return null;
	}

	@Override
	public Comment getCommentPart() {
		return null;
	}

	@Override
	public Name $name() {
		return null;
	}

	@Override
	public List<TableField<O, ?>> getKeyFields() {
		return this.key.getFields();
	}

	@Override
	public TableField<O, ?> [] getKeyFieldsArray() {
		return this.key.getFieldsArray();
	}

	@Override
	public Table<O> parent(R record) {
		return null;
	}

	@Override
	public Table<O> parents(@SuppressWarnings("unchecked") R... records) {
		return null;
	}

	@Override
	public Table<O> parents(Collection<? extends R> records) {
		return null;
	}

	@Override
	public Table<R> children(O record) {
		return null;
	}

	@Override
	public Table<R> children(@SuppressWarnings("unchecked") O... records) {
		return null;
	}

	@Override
	public Table<R> children(Collection<? extends O> records) {
		return null;
	}
}
