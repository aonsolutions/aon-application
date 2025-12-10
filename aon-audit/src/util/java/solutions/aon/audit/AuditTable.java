package solutions.aon.audit;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.BetweenAndStep;
import org.jooq.Binding;
import org.jooq.Catalog;
import org.jooq.Check;
import org.jooq.Collation;
import org.jooq.Comment;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.ContextConverter;
import org.jooq.Converter;
import org.jooq.DataType;
import org.jooq.DatePart;
import org.jooq.DivideByOnStep;
import org.jooq.Field;
import org.jooq.Fields;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.JoinType;
import org.jooq.LikeEscapeStep;
import org.jooq.Name;
import org.jooq.Package;
import org.jooq.Path;
import org.jooq.QualifiedAsterisk;
import org.jooq.QuantifiedSelect;
import org.jooq.QueryPart;
import org.jooq.QueryPartInternal;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.RecordType;
import org.jooq.Result;
import org.jooq.Row;
import org.jooq.RowId;
import org.jooq.SQL;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.SelectField;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableLike;
import org.jooq.TableOnStep;
import org.jooq.TableOptionalOnStep;
import org.jooq.TableOptions;
import org.jooq.TableOptions.TableType;
import org.jooq.TablePartitionByStep;
import org.jooq.UniqueKey;
import org.jooq.WindowIgnoreNullsStep;
import org.jooq.WindowPartitionByStep;
import org.jooq.impl.DSL;
import org.jooq.impl.QOM.JoinHint;
import org.jooq.impl.SQLDataType;

@SuppressWarnings("serial")
public class AuditTable<R extends Record> implements Table<R> {

	
	public static class AuditField<T> implements Field<T> {
		public Condition notEqual(T arg2) {
			return delegate.notEqual(arg2);
		}

		public Condition notEqualIgnoreCase(String value) {
			return delegate.notEqualIgnoreCase(value);
		}

		public Condition notEqualIgnoreCase(Field<String> value) {
			return delegate.notEqualIgnoreCase(value);
		}

		public Condition notIn(Select<? extends Record1<T>> arg2) {
			return delegate.notIn(arg2);
		}

		public Condition notIn(Collection<?> values) {
			return delegate.notIn(values);
		}

		public Condition notIn(Result<? extends Record1<T>> result) {
			return delegate.notIn(result);
		}

		public Condition notIn(Field<?>... values) {
			return delegate.notIn(values);
		}

		public LikeEscapeStep notLike(String pattern) {
			return delegate.notLike(pattern);
		}

		public LikeEscapeStep notLike(Field<String> pattern) {
			return delegate.notLike(pattern);
		}

		public LikeEscapeStep notLikeIgnoreCase(String pattern) {
			return delegate.notLikeIgnoreCase(pattern);
		}

		public LikeEscapeStep notLikeIgnoreCase(Field<String> pattern) {
			return delegate.notLikeIgnoreCase(pattern);
		}

		public Condition notLike(Field<String> field, char escape) {
			return delegate.notLike(field, escape);
		}

		public Condition notLike(String value, char escape) {
			return delegate.notLike(value, escape);
		}

		public Condition notIn(T... values) {
			return delegate.notIn(values);
		}

		public LikeEscapeStep notLike(QuantifiedSelect<? extends Record1<String>> arg0) {
			return delegate.notLike(arg0);
		}

		public Condition notLikeIgnoreCase(Field<String> field, char escape) {
			return delegate.notLikeIgnoreCase(field, escape);
		}

		public Condition notLikeIgnoreCase(String value, char escape) {
			return delegate.notLikeIgnoreCase(value, escape);
		}

		public Field<BigDecimal> power(Field<? extends Number> exponent) {
			return delegate.power(exponent);
		}

		public Field<BigDecimal> pow(Number exponent) {
			return delegate.pow(exponent);
		}

		public Field<BigDecimal> pow(Field<? extends Number> exponent) {
			return delegate.pow(exponent);
		}

		public Field<T> plus(Number value) {
			return delegate.plus(value);
		}

		public Field<T> plus(Field<?> value) {
			return delegate.plus(value);
		}

		public Condition notEqual(Select<? extends Record1<T>> arg2) {
			return delegate.notEqual(arg2);
		}

		public Condition notEqual(Field<T> arg2) {
			return delegate.notEqual(arg2);
		}

		public Condition notContains(T value) {
			return delegate.notContains(value);
		}

		public Condition notContains(Field<T> value) {
			return delegate.notContains(value);
		}

		public Condition notContainsIgnoreCase(T value) {
			return delegate.notContainsIgnoreCase(value);
		}

		public Condition notContainsIgnoreCase(Field<T> value) {
			return delegate.notContainsIgnoreCase(value);
		}

		public Condition notEqual(QuantifiedSelect<? extends Record1<T>> arg0) {
			return delegate.notEqual(arg0);
		}

		public Field<Integer> position(String search) {
			return delegate.position(search);
		}

		public Field<Integer> position(Field<String> search) {
			return delegate.position(search);
		}

		public Field<Integer> octetLength() {
			return delegate.octetLength();
		}

		public <Z> Field<Z> nvl2(Z valueIfNotNull, Z valueIfNull) {
			return delegate.nvl2(valueIfNotNull, valueIfNull);
		}

		public T original(Record record) {
			return delegate.original(record);
		}

		public LikeEscapeStep similarTo(Field<String> pattern) {
			return delegate.similarTo(pattern);
		}

		public Field<T> rem(Number divisor) {
			return delegate.rem(divisor);
		}

		public Field<T> rem(Field<? extends Number> divisor) {
			return delegate.rem(divisor);
		}

		public Field<BigDecimal> power(Number exponent) {
			return delegate.power(exponent);
		}

		public Field<T> shl(Number count) {
			return delegate.shl(count);
		}

		public Field<T> shl(Field<? extends Number> count) {
			return delegate.shl(count);
		}

		public Field<T> shr(Number count) {
			return delegate.shr(count);
		}

		public Field<T> shr(Field<? extends Number> count) {
			return delegate.shr(count);
		}

		public Condition similarTo(Field<String> value, char escape) {
			return delegate.similarTo(value, escape);
		}

		public Field<Integer> sign() {
			return delegate.sign();
		}

		public Field<T> round() {
			return delegate.round();
		}

		public Field<T> round(int decimals) {
			return delegate.round(decimals);
		}

		public Field<BigDecimal> rad() {
			return delegate.rad();
		}

		public Field<String> rtrim() {
			return delegate.rtrim();
		}

		public Field<String> rpad(Field<? extends Number> length) {
			return delegate.rpad(length);
		}

		public Field<String> rpad(int length) {
			return delegate.rpad(length);
		}

		public Field<String> rpad(Field<? extends Number> length, Field<String> character) {
			return delegate.rpad(length, character);
		}

		public Field<String> rpad(int length, char character) {
			return delegate.rpad(length, character);
		}

		public Field<String> repeat(Number count) {
			return delegate.repeat(count);
		}

		public Field<String> repeat(Field<? extends Number> count) {
			return delegate.repeat(count);
		}

		public Field<String> replace(Field<String> search) {
			return delegate.replace(search);
		}

		public Field<String> replace(String search) {
			return delegate.replace(search);
		}

		public Field<String> replace(Field<String> search, Field<String> replace) {
			return delegate.replace(search, replace);
		}

		public Field<String> replace(String search, String replace) {
			return delegate.replace(search, replace);
		}

		public void reset(Record record) {
			delegate.reset(record);
		}

		public LikeEscapeStep similarTo(QuantifiedSelect<? extends Record1<String>> arg0) {
			return delegate.similarTo(arg0);
		}

		public String toString() {
			return delegate.toString();
		}

		public SortField<T> sortDefault() {
			return delegate.sortDefault();
		}

		public SortField<T> sort(SortOrder order) {
			return delegate.sort(order);
		}

		public SortField<Integer> sortAsc(Collection<T> sortList) {
			return delegate.sortAsc(sortList);
		}

		public SortField<Integer> sortAsc(T... sortList) {
			return delegate.sortAsc(sortList);
		}

		public SortField<Integer> sortDesc(Collection<T> sortList) {
			return delegate.sortDesc(sortList);
		}

		public SortField<Integer> sortDesc(T... sortList) {
			return delegate.sortDesc(sortList);
		}

		public <Z> SortField<Z> sort(Map<T, Z> sortMap) {
			return delegate.sort(sortMap);
		}

		public LikeEscapeStep similarTo(String pattern) {
			return delegate.similarTo(pattern);
		}

		public Condition startsWith(T prefix) {
			return delegate.startsWith(prefix);
		}

		public Condition startsWith(Field<T> prefix) {
			return delegate.startsWith(prefix);
		}

		public Condition startsWithIgnoreCase(T prefix) {
			return delegate.startsWithIgnoreCase(prefix);
		}

		public Condition startsWithIgnoreCase(Field<T> prefix) {
			return delegate.startsWithIgnoreCase(prefix);
		}

		public Field<T> unaryMinus() {
			return delegate.unaryMinus();
		}

		public Field<T> unaryPlus() {
			return delegate.unaryPlus();
		}

		public Field<T> sub(Number value) {
			return delegate.sub(value);
		}

		public Field<T> sub(Field<?> value) {
			return delegate.sub(value);
		}

		public Field<T> subtract(Number value) {
			return delegate.subtract(value);
		}

		public Field<T> subtract(Field<?> value) {
			return delegate.subtract(value);
		}

		public Field<T> times(Number value) {
			return delegate.times(value);
		}

		public Field<T> times(Field<? extends Number> value) {
			return delegate.times(value);
		}

		public Condition similarTo(String value, char escape) {
			return delegate.similarTo(value, escape);
		}

		public Field<BigDecimal> sqrt() {
			return delegate.sqrt();
		}

		public Field<BigDecimal> sin() {
			return delegate.sin();
		}

		public Field<BigDecimal> tan() {
			return delegate.tan();
		}

		public Field<BigDecimal> sinh() {
			return delegate.sinh();
		}

		public Field<BigDecimal> tanh() {
			return delegate.tanh();
		}

		public Field<BigDecimal> sum() {
			return delegate.sum();
		}

		public Field<BigDecimal> stddevPop() {
			return delegate.stddevPop();
		}

		public Field<BigDecimal> stddevSamp() {
			return delegate.stddevSamp();
		}

		public Field<BigDecimal> varPop() {
			return delegate.varPop();
		}

		public Field<BigDecimal> varSamp() {
			return delegate.varSamp();
		}

		public WindowPartitionByStep<BigDecimal> sumOver() {
			return delegate.sumOver();
		}

		public WindowPartitionByStep<BigDecimal> stddevPopOver() {
			return delegate.stddevPopOver();
		}

		public WindowPartitionByStep<BigDecimal> stddevSampOver() {
			return delegate.stddevSampOver();
		}

		public WindowPartitionByStep<BigDecimal> varPopOver() {
			return delegate.varPopOver();
		}

		public WindowPartitionByStep<BigDecimal> varSampOver() {
			return delegate.varSampOver();
		}

		public Field<String> upper() {
			return delegate.upper();
		}

		public Field<String> trim() {
			return delegate.trim();
		}

		public Field<String> substring(int startingPosition) {
			return delegate.substring(startingPosition);
		}

		public Field<String> substring(Field<? extends Number> startingPosition) {
			return delegate.substring(startingPosition);
		}

		public Field<String> substring(int startingPosition, int length) {
			return delegate.substring(startingPosition, length);
		}

		public Field<String> substring(Field<? extends Number> startingPosition, Field<? extends Number> length) {
			return delegate.substring(startingPosition, length);
		}

		public boolean touched(Record record) {
			return delegate.touched(record);
		}

		public Condition ne(T arg2) {
			return delegate.ne(arg2);
		}

		public Condition ne(Select<? extends Record1<T>> arg2) {
			return delegate.ne(arg2);
		}

		public Condition ne(Field<T> arg2) {
			return delegate.ne(arg2);
		}

		public Condition notBinaryLike(byte[] pattern) {
			return delegate.notBinaryLike(pattern);
		}

		public Condition notBinaryLike(Field<byte[]> pattern) {
			return delegate.notBinaryLike(pattern);
		}

		public Field<T> modulo(Number divisor) {
			return delegate.modulo(divisor);
		}

		public Field<T> modulo(Field<? extends Number> divisor) {
			return delegate.modulo(divisor);
		}

		public Field<T> neg() {
			return delegate.neg();
		}

		public Field<T> mul(Number value) {
			return delegate.mul(value);
		}

		public Field<T> mul(Field<? extends Number> value) {
			return delegate.mul(value);
		}

		public Field<T> multiply(Number value) {
			return delegate.multiply(value);
		}

		public Field<T> multiply(Field<? extends Number> value) {
			return delegate.multiply(value);
		}

		public Condition ne(QuantifiedSelect<? extends Record1<T>> arg0) {
			return delegate.ne(arg0);
		}

		public Condition notBetween(T minValue, T maxValue) {
			return delegate.notBetween(minValue, maxValue);
		}

		public Condition notBetween(Field<T> minValue, Field<T> maxValue) {
			return delegate.notBetween(minValue, maxValue);
		}

		public Condition notBetweenSymmetric(T minValue, T maxValue) {
			return delegate.notBetweenSymmetric(minValue, maxValue);
		}

		public Condition notBetweenSymmetric(Field<T> minValue, Field<T> maxValue) {
			return delegate.notBetweenSymmetric(minValue, maxValue);
		}

		public BetweenAndStep<T> notBetween(T minValue) {
			return delegate.notBetween(minValue);
		}

		public BetweenAndStep<T> notBetween(Field<T> minValue) {
			return delegate.notBetween(minValue);
		}

		public BetweenAndStep<T> notBetweenSymmetric(T minValue) {
			return delegate.notBetweenSymmetric(minValue);
		}

		public BetweenAndStep<T> notBetweenSymmetric(Field<T> minValue) {
			return delegate.notBetweenSymmetric(minValue);
		}

		public Condition notBinaryLike(QuantifiedSelect<? extends Record1<byte[]>> arg0) {
			return delegate.notBinaryLike(arg0);
		}

		public Condition lt(T arg2) {
			return delegate.lt(arg2);
		}

		public Condition lt(Select<? extends Record1<T>> arg2) {
			return delegate.lt(arg2);
		}

		public Condition lt(Field<T> arg2) {
			return delegate.lt(arg2);
		}

		public Field<T> mod(Number divisor) {
			return delegate.mod(divisor);
		}

		public Field<T> mod(Field<? extends Number> divisor) {
			return delegate.mod(divisor);
		}

		public Field<T> minus(Number value) {
			return delegate.minus(value);
		}

		public Field<T> minus(Field<?> value) {
			return delegate.minus(value);
		}

		public Field<T> max() {
			return delegate.max();
		}

		public Field<T> min() {
			return delegate.min();
		}

		public Field<BigDecimal> median() {
			return delegate.median();
		}

		public WindowPartitionByStep<T> maxOver() {
			return delegate.maxOver();
		}

		public WindowPartitionByStep<T> minOver() {
			return delegate.minOver();
		}

		public Field<String> ltrim() {
			return delegate.ltrim();
		}

		public Field<String> lpad(Field<? extends Number> length) {
			return delegate.lpad(length);
		}

		public Field<String> lpad(int length) {
			return delegate.lpad(length);
		}

		public Field<String> lpad(int length, char character) {
			return delegate.lpad(length, character);
		}

		public Condition lt(QuantifiedSelect<? extends Record1<T>> arg0) {
			return delegate.lt(arg0);
		}

		public Condition isNull() {
			return delegate.isNull();
		}

		public Condition isNotDistinctFrom(T arg2) {
			return delegate.isNotDistinctFrom(arg2);
		}

		public Condition isNotDistinctFrom(Select<? extends Record1<T>> arg2) {
			return delegate.isNotDistinctFrom(arg2);
		}

		public Condition isNotDistinctFrom(Field<T> arg2) {
			return delegate.isNotDistinctFrom(arg2);
		}

		public Condition isNotNull() {
			return delegate.isNotNull();
		}

		public Condition le(T arg2) {
			return delegate.le(arg2);
		}

		public Condition le(Select<? extends Record1<T>> arg2) {
			return delegate.le(arg2);
		}

		public Condition le(Field<T> arg2) {
			return delegate.le(arg2);
		}

		public Condition lessOrEqual(T arg2) {
			return delegate.lessOrEqual(arg2);
		}

		public Condition lessOrEqual(Select<? extends Record1<T>> arg2) {
			return delegate.lessOrEqual(arg2);
		}

		public Condition lessOrEqual(Field<T> arg2) {
			return delegate.lessOrEqual(arg2);
		}

		public Condition lessThan(T arg2) {
			return delegate.lessThan(arg2);
		}

		public Condition lessThan(Select<? extends Record1<T>> arg2) {
			return delegate.lessThan(arg2);
		}

		public LikeEscapeStep like(String pattern) {
			return delegate.like(pattern);
		}

		public LikeEscapeStep like(Field<String> pattern) {
			return delegate.like(pattern);
		}

		public LikeEscapeStep likeIgnoreCase(String pattern) {
			return delegate.likeIgnoreCase(pattern);
		}

		public LikeEscapeStep likeIgnoreCase(Field<String> pattern) {
			return delegate.likeIgnoreCase(pattern);
		}

		public Condition isNotDocument() {
			return delegate.isNotDocument();
		}

		public Condition isJson() {
			return delegate.isJson();
		}

		public Condition isNotJson() {
			return delegate.isNotJson();
		}

		public Condition likeRegex(String pattern) {
			return delegate.likeRegex(pattern);
		}

		public Condition likeRegex(Field<String> pattern) {
			return delegate.likeRegex(pattern);
		}

		public Condition like(String value, char escape) {
			return delegate.like(value, escape);
		}

		public Condition isTrue() {
			return delegate.isTrue();
		}

		public WindowIgnoreNullsStep<T> lastValue() {
			return delegate.lastValue();
		}

		public WindowIgnoreNullsStep<T> lead() {
			return delegate.lead();
		}

		public WindowIgnoreNullsStep<T> lead(int offset) {
			return delegate.lead(offset);
		}

		public WindowIgnoreNullsStep<T> lead(int offset, T defaultValue) {
			return delegate.lead(offset, defaultValue);
		}

		public WindowIgnoreNullsStep<T> lead(int offset, Field<T> defaultValue) {
			return delegate.lead(offset, defaultValue);
		}

		public WindowIgnoreNullsStep<T> lag() {
			return delegate.lag();
		}

		public WindowIgnoreNullsStep<T> lag(int offset) {
			return delegate.lag(offset);
		}

		public WindowIgnoreNullsStep<T> lag(int offset, T defaultValue) {
			return delegate.lag(offset, defaultValue);
		}

		public WindowIgnoreNullsStep<T> lag(int offset, Field<T> defaultValue) {
			return delegate.lag(offset, defaultValue);
		}

		public Condition le(QuantifiedSelect<? extends Record1<T>> arg0) {
			return delegate.le(arg0);
		}

		public Field<Integer> length() {
			return delegate.length();
		}

		public Field<T> least(T... others) {
			return delegate.least(others);
		}

		public Condition lessOrEqual(QuantifiedSelect<? extends Record1<T>> arg0) {
			return delegate.lessOrEqual(arg0);
		}

		public Condition lessThan(Field<T> arg2) {
			return delegate.lessThan(arg2);
		}

		public Condition lessThan(QuantifiedSelect<? extends Record1<T>> arg0) {
			return delegate.lessThan(arg0);
		}

		public Condition like(Field<String> value, char escape) {
			return delegate.like(value, escape);
		}

		public LikeEscapeStep like(QuantifiedSelect<? extends Record1<String>> arg0) {
			return delegate.like(arg0);
		}

		public Condition likeIgnoreCase(Field<String> field, char escape) {
			return delegate.likeIgnoreCase(field, escape);
		}

		public Condition likeIgnoreCase(String value, char escape) {
			return delegate.likeIgnoreCase(value, escape);
		}

		public Field<BigDecimal> ln() {
			return delegate.ln();
		}

		public Field<BigDecimal> log(int base) {
			return delegate.log(base);
		}

		public Field<String> lower() {
			return delegate.lower();
		}

		public Field<String> lpad(Field<? extends Number> length, Field<String> character) {
			return delegate.lpad(length, character);
		}

		public Field<T> least(Field<?>... others) {
			return delegate.least(others);
		}

		public boolean equals(Object other) {
			return delegate.equals(other);
		}

		public SortField<T> desc() {
			return delegate.desc();
		}

		public Condition eq(T arg2) {
			return delegate.eq(arg2);
		}

		public Condition eq(Select<? extends Record1<T>> arg2) {
			return delegate.eq(arg2);
		}

		public Condition eq(Field<T> arg2) {
			return delegate.eq(arg2);
		}

		public Condition equal(T arg2) {
			return delegate.equal(arg2);
		}

		public Condition equal(Select<? extends Record1<T>> arg2) {
			return delegate.equal(arg2);
		}

		public Condition equal(Field<T> arg2) {
			return delegate.equal(arg2);
		}

		public Condition endsWith(T suffix) {
			return delegate.endsWith(suffix);
		}

		public Condition endsWith(Field<T> suffix) {
			return delegate.endsWith(suffix);
		}

		public Condition endsWithIgnoreCase(T suffix) {
			return delegate.endsWithIgnoreCase(suffix);
		}

		public Condition endsWithIgnoreCase(Field<T> suffix) {
			return delegate.endsWithIgnoreCase(suffix);
		}

		public Field<T> div(Number value) {
			return delegate.div(value);
		}

		public Field<T> div(Field<? extends Number> value) {
			return delegate.div(value);
		}

		public Field<T> divide(Number value) {
			return delegate.divide(value);
		}

		public Field<T> divide(Field<? extends Number> value) {
			return delegate.divide(value);
		}

		public Condition equalIgnoreCase(String value) {
			return delegate.equalIgnoreCase(value);
		}

		public Condition equalIgnoreCase(Field<String> value) {
			return delegate.equalIgnoreCase(value);
		}

		public Field<BigDecimal> exp() {
			return delegate.exp();
		}

		public Field<BigDecimal> coth() {
			return delegate.coth();
		}

		public Field<BigDecimal> deg() {
			return delegate.deg();
		}

		public Field<Integer> count() {
			return delegate.count();
		}

		public Field<Integer> countDistinct() {
			return delegate.countDistinct();
		}

		public WindowPartitionByStep<Integer> countOver() {
			return delegate.countOver();
		}

		public Field<Integer> extract(DatePart datePart) {
			return delegate.extract(datePart);
		}

		public <Z> Field<Z> decode(T search, Z result) {
			return delegate.decode(search, result);
		}

		public <Z> Field<Z> decode(T search, Z result, Object... more) {
			return delegate.decode(search, result, more);
		}

		public <Z> Field<Z> decode(Field<T> search, Field<Z> result) {
			return delegate.decode(search, result);
		}

		public <Z> Field<Z> decode(Field<T> search, Field<Z> result, Field<?>... more) {
			return delegate.decode(search, result, more);
		}

		public Condition eq(QuantifiedSelect<? extends Record1<T>> arg0) {
			return delegate.eq(arg0);
		}

		public Condition equal(QuantifiedSelect<? extends Record1<T>> arg0) {
			return delegate.equal(arg0);
		}

		public <U> Field<U> convert(Binding<T, U> binding) {
			return delegate.convert(binding);
		}

		public <U> Field<U> convert(Converter<T, U> converter) {
			return delegate.convert(converter);
		}

		public <U> Field<U> convert(Class<U> toType, Function<? super T, ? extends U> from,
				Function<? super U, ? extends T> to) {
			return delegate.convert(toType, from, to);
		}

		public <U> Field<U> convertFrom(Class<U> toType, Function<? super T, ? extends U> from) {
			return delegate.convertFrom(toType, from);
		}

		public <U> Field<U> convertFrom(Function<? super T, ? extends U> from) {
			return delegate.convertFrom(from);
		}

		public <U> Field<U> convertTo(Class<U> toType, Function<? super U, ? extends T> to) {
			return delegate.convertTo(toType, to);
		}

		public <U> Field<U> convertTo(Function<? super U, ? extends T> to) {
			return delegate.convertTo(to);
		}

		public Condition contains(T content) {
			return delegate.contains(content);
		}

		public Condition contains(Field<T> content) {
			return delegate.contains(content);
		}

		public Condition containsIgnoreCase(T content) {
			return delegate.containsIgnoreCase(content);
		}

		public Condition containsIgnoreCase(Field<T> content) {
			return delegate.containsIgnoreCase(content);
		}

		public Condition compare(Comparator comparator, T value) {
			return delegate.compare(comparator, value);
		}

		public Condition compare(Comparator comparator, Select<? extends Record1<T>> query) {
			return delegate.compare(comparator, query);
		}

		public Condition compare(Comparator comparator, QuantifiedSelect<? extends Record1<T>> query) {
			return delegate.compare(comparator, query);
		}

		public Field<BigDecimal> cos() {
			return delegate.cos();
		}

		public Field<BigDecimal> cot() {
			return delegate.cot();
		}

		public Field<BigDecimal> cosh() {
			return delegate.cosh();
		}

		public Field<String> concat(Field<?>... fields) {
			return delegate.concat(fields);
		}

		public Field<String> concat(String... values) {
			return delegate.concat(values);
		}

		public Field<String> concat(char... values) {
			return delegate.concat(values);
		}

		public Condition compare(Comparator comparator, Field<T> field) {
			return delegate.compare(comparator, field);
		}

		Field<T> delegate;

		public AuditField(Field<T> delegate) {
			this.delegate = delegate;
		}

		public Name $name() {
			return delegate.$name();
		}

		public DataType<T> $dataType() {
			return delegate.$dataType();
		}

		public Field<T> as(Function<? super Field<T>, ? extends String> aliasFunction) {
			return delegate.as(aliasFunction);
		}

		public Field<T> as(String alias) {
			return delegate.as(alias);
		}

		public Field<T> as(Name alias) {
			return delegate.as(alias);
		}

		public Field<T> as(Field<?> otherField) {
			return delegate.as(otherField);
		}

		public SortField<T> asc() {
			return delegate.asc();
		}

		public Field<T> add(Number value) {
			return delegate.add(value);
		}

		public Field<T> add(Field<?> value) {
			return delegate.add(value);
		}

		public Field<T> abs() {
			return delegate.abs();
		}

		public Field<BigDecimal> acos() {
			return delegate.acos();
		}

		public Field<BigDecimal> asin() {
			return delegate.asin();
		}

		public Field<BigDecimal> atan() {
			return delegate.atan();
		}

		public Field<BigDecimal> atan2(Field<? extends Number> y) {
			return delegate.atan2(y);
		}

		public Field<Integer> ascii() {
			return delegate.ascii();
		}

		public Condition binaryLike(byte[] pattern) {
			return delegate.binaryLike(pattern);
		}

		public Condition binaryLike(Field<byte[]> pattern) {
			return delegate.binaryLike(pattern);
		}

		public Field<T> bitAnd(T arg2) {
			return delegate.bitAnd(arg2);
		}

		public Field<T> bitAnd(Field<T> arg2) {
			return delegate.bitAnd(arg2);
		}

		public Field<T> bitNand(T arg2) {
			return delegate.bitNand(arg2);
		}

		public Field<T> bitNand(Field<T> arg2) {
			return delegate.bitNand(arg2);
		}

		public Field<T> bitNor(T arg2) {
			return delegate.bitNor(arg2);
		}

		public Field<T> bitNor(Field<T> arg2) {
			return delegate.bitNor(arg2);
		}

		public Field<T> bitNot() {
			return delegate.bitNot();
		}

		public Field<T> bitOr(T arg2) {
			return delegate.bitOr(arg2);
		}

		public Field<T> bitOr(Field<T> arg2) {
			return delegate.bitOr(arg2);
		}

		public Field<T> bitXNor(T arg2) {
			return delegate.bitXNor(arg2);
		}

		public Field<T> bitXNor(Field<T> arg2) {
			return delegate.bitXNor(arg2);
		}

		public Field<T> bitXor(T arg2) {
			return delegate.bitXor(arg2);
		}

		public Field<T> bitXor(Field<T> arg2) {
			return delegate.bitXor(arg2);
		}

		public Condition between(T minValue, T maxValue) {
			return delegate.between(minValue, maxValue);
		}

		public Condition between(Field<T> minValue, Field<T> maxValue) {
			return delegate.between(minValue, maxValue);
		}

		public Condition betweenSymmetric(T minValue, T maxValue) {
			return delegate.betweenSymmetric(minValue, maxValue);
		}

		public Condition betweenSymmetric(Field<T> minValue, Field<T> maxValue) {
			return delegate.betweenSymmetric(minValue, maxValue);
		}

		public BetweenAndStep<T> between(T minValue) {
			return delegate.between(minValue);
		}

		public BetweenAndStep<T> between(Field<T> minValue) {
			return delegate.between(minValue);
		}

		public BetweenAndStep<T> betweenSymmetric(T minValue) {
			return delegate.betweenSymmetric(minValue);
		}

		public BetweenAndStep<T> betweenSymmetric(Field<T> minValue) {
			return delegate.betweenSymmetric(minValue);
		}

		public Field<BigDecimal> atan2(Number y) {
			return delegate.atan2(y);
		}

		public Field<BigDecimal> avg() {
			return delegate.avg();
		}

		public WindowPartitionByStep<BigDecimal> avgOver() {
			return delegate.avgOver();
		}

		public Condition binaryLike(QuantifiedSelect<? extends Record1<byte[]>> arg0) {
			return delegate.binaryLike(arg0);
		}

		public Field<Integer> bitLength() {
			return delegate.bitLength();
		}

		public Field<T> comment(String comment) {
			return delegate.comment(comment);
		}

		public Field<T> comment(Comment comment) {
			return delegate.comment(comment);
		}

		public <Z> Field<Z> cast(Field<Z> field) {
			return delegate.cast(field);
		}

		public <Z> Field<Z> cast(DataType<Z> type) {
			return delegate.cast(type);
		}

		public <Z> Field<Z> cast(Class<Z> type) {
			return delegate.cast(type);
		}

		public <Z> Field<Z> coerce(Field<Z> field) {
			return delegate.coerce(field);
		}

		public <Z> Field<Z> coerce(DataType<Z> type) {
			return delegate.coerce(type);
		}

		public <Z> Field<Z> coerce(Class<Z> type) {
			return delegate.coerce(type);
		}

		public Field<T> ceil() {
			return delegate.ceil();
		}

		public Field<String> collate(String collation) {
			return delegate.collate(collation);
		}

		public Field<String> collate(Name collation) {
			return delegate.collate(collation);
		}

		public Field<String> collate(Collation collation) {
			return delegate.collate(collation);
		}

		public Field<Integer> charLength() {
			return delegate.charLength();
		}

		public Field<T> coalesce(T option, T... options) {
			return delegate.coalesce(option, options);
		}

		public Field<T> coalesce(Field<T> option, Field<?>... options) {
			return delegate.coalesce(option, options);
		}

		public boolean changed(Record record) {
			return delegate.changed(record);
		}

		public ContextConverter<?, T> getConverter() {
			return delegate.getConverter();
		}

		public Name getQualifiedName() {
			return delegate.getQualifiedName();
		}

		public Name getUnqualifiedName() {
			return delegate.getUnqualifiedName();
		}

		public Binding<?, T> getBinding() {
			return delegate.getBinding();
		}

		public Comment getCommentPart() {
			return delegate.getCommentPart();
		}

		public Class<T> getType() {
			return delegate.getType();
		}

		public DataType<T> getDataType() {
			return delegate.getDataType().identity(false);
		}

		public DataType<T> getDataType(Configuration configuration) {
			return delegate.getDataType(configuration).identity(false);
		}

		public String getName() {
			return delegate.getName();
		}

		public String getComment() {
			return delegate.getComment();
		}

		public Condition ge(T arg2) {
			return delegate.ge(arg2);
		}

		public Condition ge(Select<? extends Record1<T>> arg2) {
			return delegate.ge(arg2);
		}

		public Condition ge(Field<T> arg2) {
			return delegate.ge(arg2);
		}

		public Condition greaterOrEqual(Select<? extends Record1<T>> arg2) {
			return delegate.greaterOrEqual(arg2);
		}

		public Condition greaterOrEqual(Field<T> arg2) {
			return delegate.greaterOrEqual(arg2);
		}

		public Field<T> floor() {
			return delegate.floor();
		}

		public WindowIgnoreNullsStep<T> firstValue() {
			return delegate.firstValue();
		}

		public Field<T> field(Record record) {
			return delegate.field(record);
		}

		public T get(Record record) {
			return delegate.get(record);
		}

		public T getValue(Record record) {
			return delegate.getValue(record);
		}

		public Record1<T> from(Record record) {
			return delegate.from(record);
		}

		public Condition ge(QuantifiedSelect<? extends Record1<T>> arg0) {
			return delegate.ge(arg0);
		}

		public Condition greaterOrEqual(QuantifiedSelect<? extends Record1<T>> arg0) {
			return delegate.greaterOrEqual(arg0);
		}

		public Condition greaterOrEqual(T arg2) {
			return delegate.greaterOrEqual(arg2);
		}

		public Condition greaterThan(Field<T> arg2) {
			return delegate.greaterThan(arg2);
		}

		public Condition greaterThan(QuantifiedSelect<? extends Record1<T>> arg0) {
			return delegate.greaterThan(arg0);
		}

		public int hashCode() {
			return delegate.hashCode();
		}

		public Condition greaterThan(T arg2) {
			return delegate.greaterThan(arg2);
		}

		public Condition greaterThan(Select<? extends Record1<T>> arg2) {
			return delegate.greaterThan(arg2);
		}

		public Condition gt(T arg2) {
			return delegate.gt(arg2);
		}

		public Condition gt(Select<? extends Record1<T>> arg2) {
			return delegate.gt(arg2);
		}

		public Condition gt(Field<T> arg2) {
			return delegate.gt(arg2);
		}

		public Condition in(Select<? extends Record1<T>> arg2) {
			return delegate.in(arg2);
		}

		public Condition isDistinctFrom(T arg2) {
			return delegate.isDistinctFrom(arg2);
		}

		public Condition isDistinctFrom(Select<? extends Record1<T>> arg2) {
			return delegate.isDistinctFrom(arg2);
		}

		public Condition isDistinctFrom(Field<T> arg2) {
			return delegate.isDistinctFrom(arg2);
		}

		public Condition isDocument() {
			return delegate.isDocument();
		}

		public Condition in(Collection<?> values) {
			return delegate.in(values);
		}

		public Condition in(Result<? extends Record1<T>> result) {
			return delegate.in(result);
		}

		public Condition in(T... values) {
			return delegate.in(values);
		}

		public Condition in(Field<?>... values) {
			return delegate.in(values);
		}

		public Condition isFalse() {
			return delegate.isFalse();
		}

		public Field<T> greatest(T... others) {
			return delegate.greatest(others);
		}

		public Field<T> greatest(Field<?>... others) {
			return delegate.greatest(others);
		}

		public Condition gt(QuantifiedSelect<? extends Record1<T>> arg0) {
			return delegate.gt(arg0);
		}

		public SortField<T> nullsFirst() {
			return delegate.nullsFirst();
		}

		public SortField<T> nullsLast() {
			return delegate.nullsLast();
		}

		public LikeEscapeStep notSimilarTo(String pattern) {
			return delegate.notSimilarTo(pattern);
		}

		public LikeEscapeStep notSimilarTo(Field<String> pattern) {
			return delegate.notSimilarTo(pattern);
		}

		public Condition notLikeRegex(String pattern) {
			return delegate.notLikeRegex(pattern);
		}

		public Condition notLikeRegex(Field<String> pattern) {
			return delegate.notLikeRegex(pattern);
		}

		public Condition notSimilarTo(Field<String> field, char escape) {
			return delegate.notSimilarTo(field, escape);
		}

		public LikeEscapeStep notSimilarTo(QuantifiedSelect<? extends Record1<String>> arg0) {
			return delegate.notSimilarTo(arg0);
		}

		public Condition notSimilarTo(String value, char escape) {
			return delegate.notSimilarTo(value, escape);
		}

		public Field<T> nvl(T defaultValue) {
			return delegate.nvl(defaultValue);
		}

		public Field<T> nvl(Field<T> defaultValue) {
			return delegate.nvl(defaultValue);
		}

		public <Z> Field<Z> nvl2(Field<Z> valueIfNotNull, Field<Z> valueIfNull) {
			return delegate.nvl2(valueIfNotNull, valueIfNull);
		}

		public Field<T> nullif(T other) {
			return delegate.nullif(other);
		}

		public Field<T> nullif(Field<T> other) {
			return delegate.nullif(other);
		}

	}

	public static class AuditFields implements Fields {


		public static final Field<String> AUDIT_DIGEST = DSL.field(DSL.name("audit_digest"),SQLDataType.CHAR(64).nullable(false));
		public static final Field<Byte> AUDIT_EVENT = DSL.field(DSL.name("audit_event"),SQLDataType.TINYINT.nullable(false));
		public static final Field<String> AUDIT_SCHEMA =  DSL.field(DSL.name("audit_schema"),SQLDataType.CHAR(64).nullable(false));  
		public static final Field<Long> AUDIT_TIMESTAMP = DSL.field(DSL.name("audit_timestamp"),SQLDataType.BIGINT.nullable(false));  

		protected static final Field<?> [] AUDIT_FIELDS = { AUDIT_TIMESTAMP, AUDIT_EVENT, AUDIT_SCHEMA, AUDIT_DIGEST  };

		private Fields delegate;

		public AuditFields(Fields delegate) {
			this.delegate = delegate;
		}

		public Fields fieldsIncludingHidden() {
			return delegate.fieldsIncludingHidden();
		}

		public Field<?>[] fields() {
			return Stream.concat(Arrays.stream(delegate.fields()), Arrays.stream(AUDIT_FIELDS))
					.map(f -> new AuditField<>(f)).toArray(Field<?>[]::new);
		}

		public Row fieldsRow() {
			return delegate.fieldsRow();
		}

		public Stream<Field<?>> fieldStream() {
			return delegate.fieldStream();
		}

		public <T> Field<T> field(Field<T> field) {
			return delegate.field(field);
		}

		public Field<?> field(String name) {
			return delegate.field(name);
		}

		public <T> Field<T> field(String name, Class<T> type) {
			return delegate.field(name, type);
		}

		public <T> Field<T> field(String name, DataType<T> dataType) {
			return delegate.field(name, dataType);
		}

		public Field<?> field(Name name) {
			return delegate.field(name);
		}

		public <T> Field<T> field(Name name, Class<T> type) {
			return delegate.field(name, type);
		}

		public <T> Field<T> field(Name name, DataType<T> dataType) {
			return delegate.field(name, dataType);
		}

		public Field<?> field(int index) {
			return delegate.field(index);
		}

		public <T> Field<T> field(int index, Class<T> type) {
			return delegate.field(index, type);
		}

		public <T> Field<T> field(int index, DataType<T> dataType) {
			return delegate.field(index, dataType);
		}

		public Field<?>[] fields(Field<?>... fields) {
			return delegate.fields(fields);
		}

		public Field<?>[] fields(String... names) {
			return delegate.fields(names);
		}

		public Field<?>[] fields(Name... names) {
			return delegate.fields(names);
		}

		public Field<?>[] fields(int... indexes) {
			return delegate.fields(indexes);
		}

		public int indexOf(Field<?> field) {
			return delegate.indexOf(field);
		}

		public int indexOf(String name) {
			return delegate.indexOf(name);
		}

		public int indexOf(Name name) {
			return delegate.indexOf(name);
		}

		public Class<?>[] types() {
			return delegate.types();
		}

		public Class<?> type(int index) {
			return delegate.type(index);
		}

		public Class<?> type(String name) {
			return delegate.type(name);
		}

		public Class<?> type(Name name) {
			return delegate.type(name);
		}

		public DataType<?>[] dataTypes() {
			return delegate.dataTypes();
		}

		public DataType<?> dataType(int index) {
			return delegate.dataType(index);
		}

		public DataType<?> dataType(String name) {
			return delegate.dataType(name);
		}

		public DataType<?> dataType(Name name) {
			return delegate.dataType(name);
		}

	}

	private Table<R> delegate;

	public AuditTable(Table<R> table) {
		this.delegate = table;
	}

	public Table<R> getDelegate() {
		return delegate;
	}

	public Package getPackage() {
		return delegate.getPackage();
	}

	public Catalog getCatalog() {
		return delegate.getCatalog();
	}

	public String getName() {
		return delegate.getName();
	}

	public Schema getSchema() {
		return delegate.getSchema();
	}

	public ContextConverter<?, R> getConverter() {
		return delegate.getConverter();
	}

	public Schema $schema() {
		return delegate.$schema();
	}

	public Name getQualifiedName() {
		return delegate.getQualifiedName();
	}

	public Class<? extends R> getRecordType() {
		return delegate.getRecordType();
	}

	public String toString() {
		return delegate.toString();
	}

	public Name getUnqualifiedName() {
		return delegate.getUnqualifiedName();
	}

	public DataType<R> getDataType() {
		return delegate.getDataType();
	}

	public String getComment() {
		return delegate.getComment();
	}

	public R newRecord() {
		return delegate.newRecord();
	}

	public Binding<?, R> getBinding() {
		return delegate.getBinding();
	}

	public Comment getCommentPart() {
		return delegate.getCommentPart();
	}

	public Field<Result<R>> asMultiset() {
		return delegate.asMultiset();
	}

	public Class<R> getType() {
		return delegate.getType();
	}

	public Name $name() {
		return delegate.$name();
	}

	public Field<Result<R>> asMultiset(String alias) {
		return delegate.asMultiset(alias);
	}

	public DataType<R> getDataType(Configuration configuration) {
		return delegate.getDataType(configuration);
	}

	public DataType<R> $dataType() {
		return delegate.$dataType();
	}

	public Field<Result<R>> asMultiset(Name alias) {
		return delegate.asMultiset(alias);
	}

	public Field<Result<R>> asMultiset(Field<?> alias) {
		return delegate.asMultiset(alias);
	}

	public Table<R> asTable() {
		return delegate.asTable();
	}

	public int hashCode() {
		return delegate.hashCode();
	}

	public Table<R> asTable(String alias) {
		return delegate.asTable(alias);
	}

	public Table<R> asTable(String alias, String... fieldAliases) {
		return delegate.asTable(alias, fieldAliases);
	}

	public Table<R> asTable(String alias, Collection<? extends String> fieldAliases) {
		return delegate.asTable(alias, fieldAliases);
	}

	public Table<R> asTable(Name alias) {
		return delegate.asTable(alias);
	}

	public Table<R> asTable(Name alias, Name... fieldAliases) {
		return delegate.asTable(alias, fieldAliases);
	}

	public Field<?>[] fields() {
		return delegate.fields();
	}

	public Row fieldsRow() {
		return delegate.fieldsRow();
	}

	public Table<R> asTable(Name alias, Collection<? extends Name> fieldAliases) {
		return delegate.asTable(alias, fieldAliases);
	}

	public Stream<Field<?>> fieldStream() {
		return delegate.fieldStream();
	}

	public TableType getTableType() {
		return delegate.getTableType();
	}

	public TableOptions getOptions() {
		return delegate.getOptions();
	}

	public <T> Field<T> field(Field<T> field) {
		return delegate.field(field);
	}

	public Table<R> asTable(Table<?> alias) {
		return delegate.asTable(alias);
	}

	public RecordType<R> recordType() {
		return delegate.recordType();
	}

	public SelectField<R> as(Field<?> otherField) {
		return delegate.as(otherField);
	}

	public Identity<R, ?> getIdentity() {
		return delegate.getIdentity();
	}

	public Table<R> asTable(Table<?> alias, Field<?>... fieldAliases) {
		return delegate.asTable(alias, fieldAliases);
	}

	public Table<R> asTable(Table<?> alias, Collection<? extends Field<?>> fieldAliases) {
		return delegate.asTable(alias, fieldAliases);
	}

	public Table<R> asTable(String alias, Function<? super Field<?>, ? extends String> aliasFunction) {
		return delegate.asTable(alias, aliasFunction);
	}

	public Field<?> field(String name) {
		return delegate.field(name);
	}

	public <T> Field<T> field(String name, Class<T> type) {
		return delegate.field(name, type);
	}

	public <U> SelectField<U> convert(Binding<R, U> binding) {
		return delegate.convert(binding);
	}

	public Table<R> asTable(String alias,
			BiFunction<? super Field<?>, ? super Integer, ? extends String> aliasFunction) {
		return delegate.asTable(alias, aliasFunction);
	}

	public TableField<R, ?> getRecordVersion() {
		return delegate.getRecordVersion();
	}

	public <T> Field<T> field(String name, DataType<T> dataType) {
		return delegate.field(name, dataType);
	}

	public <U> SelectField<U> convert(Converter<R, U> converter) {
		return delegate.convert(converter);
	}

	public TableField<R, ?> getRecordTimestamp() {
		return delegate.getRecordTimestamp();
	}

	public Field<?> field(Name name) {
		return delegate.field(name);
	}

	public <U> SelectField<U> convert(Class<U> toType, Function<? super R, ? extends U> from,
			Function<? super U, ? extends R> to) {
		return delegate.convert(toType, from, to);
	}

	public <T> Field<T> field(Name name, Class<T> type) {
		return delegate.field(name, type);
	}

	public <U> SelectField<U> convertFrom(Class<U> toType, Function<? super R, ? extends U> from) {
		return delegate.convertFrom(toType, from);
	}

	public <U> SelectField<U> convertFrom(Function<? super R, ? extends U> from) {
		return delegate.convertFrom(from);
	}

	public <T> Field<T> field(Name name, DataType<T> dataType) {
		return delegate.field(name, dataType);
	}

	public List<Check<R>> getChecks() {
		return delegate.getChecks();
	}

	public <U> SelectField<U> convertTo(Class<U> toType, Function<? super U, ? extends R> to) {
		return delegate.convertTo(toType, to);
	}

	public QualifiedAsterisk asterisk() {
		return delegate.asterisk();
	}

	public Field<?> field(int index) {
		return delegate.field(index);
	}

	public <T> Field<T> field(int index, Class<T> type) {
		return delegate.field(index, type);
	}

	public <U> SelectField<U> convertTo(Function<? super U, ? extends R> to) {
		return delegate.convertTo(to);
	}

	public Table<R> as(String alias) {
		return delegate.as(alias);
	}

	public <T> Field<T> field(int index, DataType<T> dataType) {
		return delegate.field(index, dataType);
	}

	public Field<?>[] fields(Field<?>... fields) {
		return delegate.fields(fields);
	}

	public Field<?>[] fields(String... names) {
		return delegate.fields(names);
	}

	public Field<?>[] fields(Name... names) {
		return delegate.fields(names);
	}

	public Field<?>[] fields(int... indexes) {
		return delegate.fields(indexes);
	}

	public Table<R> as(String alias, String... fieldAliases) {
		return delegate.as(alias, fieldAliases);
	}

	public int indexOf(Field<?> field) {
		return delegate.indexOf(field);
	}

	public int indexOf(String name) {
		return delegate.indexOf(name);
	}

	public int indexOf(Name name) {
		return delegate.indexOf(name);
	}

	public Class<?>[] types() {
		return delegate.types();
	}

	public Class<?> type(int index) {
		return delegate.type(index);
	}

	public Class<?> type(String name) {
		return delegate.type(name);
	}

	public Class<?> type(Name name) {
		return delegate.type(name);
	}

	public DataType<?>[] dataTypes() {
		return delegate.dataTypes();
	}

	public DataType<?> dataType(int index) {
		return delegate.dataType(index);
	}

	public DataType<?> dataType(String name) {
		return delegate.dataType(name);
	}

	public Table<R> as(String alias, Collection<? extends String> fieldAliases) {
		return delegate.as(alias, fieldAliases);
	}

	public DataType<?> dataType(Name name) {
		return delegate.dataType(name);
	}

	public Table<R> as(String alias, Function<? super Field<?>, ? extends String> aliasFunction) {
		return delegate.as(alias, aliasFunction);
	}

	public Table<R> as(String alias, BiFunction<? super Field<?>, ? super Integer, ? extends String> aliasFunction) {
		return delegate.as(alias, aliasFunction);
	}

	public Table<R> as(Name alias) {
		return delegate.as(alias);
	}

	public Table<R> as(Name alias, Name... fieldAliases) {
		return delegate.as(alias, fieldAliases);
	}

	public Table<R> as(Name alias, Collection<? extends Name> fieldAliases) {
		return delegate.as(alias, fieldAliases);
	}

	public Table<R> as(Name alias, Function<? super Field<?>, ? extends Name> aliasFunction) {
		return delegate.as(alias, aliasFunction);
	}

	public Table<R> as(Name alias, BiFunction<? super Field<?>, ? super Integer, ? extends Name> aliasFunction) {
		return delegate.as(alias, aliasFunction);
	}

	public Table<R> as(Table<?> otherTable) {
		return delegate.as(otherTable);
	}

	public Table<R> as(Table<?> otherTable, Field<?>... otherFields) {
		return delegate.as(otherTable, otherFields);
	}

	public Table<R> as(Table<?> otherTable, Collection<? extends Field<?>> otherFields) {
		return delegate.as(otherTable, otherFields);
	}

	public Table<R> as(Table<?> otherTable, Function<? super Field<?>, ? extends Field<?>> aliasFunction) {
		return delegate.as(otherTable, aliasFunction);
	}

	public Table<R> as(Table<?> otherTable,
			BiFunction<? super Field<?>, ? super Integer, ? extends Field<?>> aliasFunction) {
		return delegate.as(otherTable, aliasFunction);
	}

	public Table<R> where(Condition condition) {
		return delegate.where(condition);
	}

	public Table<R> where(Condition... conditions) {
		return delegate.where(conditions);
	}

	public Table<R> where(Collection<? extends Condition> conditions) {
		return delegate.where(conditions);
	}

	public Table<R> where(Field<Boolean> field) {
		return delegate.where(field);
	}

	public Table<R> where(SQL sql) {
		return delegate.where(sql);
	}

	public Table<R> where(String sql) {
		return delegate.where(sql);
	}

	public Table<R> where(String sql, Object... bindings) {
		return delegate.where(sql, bindings);
	}

	public Table<R> where(String sql, QueryPart... parts) {
		return delegate.where(sql, parts);
	}

	public Table<R> whereExists(Select<?> select) {
		return delegate.whereExists(select);
	}

	public Table<R> whereNotExists(Select<?> select) {
		return delegate.whereNotExists(select);
	}

	public TableOptionalOnStep<Record> join(TableLike<?> table, JoinType type) {
		return delegate.join(table, type);
	}

	public TableOptionalOnStep<Record> join(TableLike<?> table, JoinType type, JoinHint hint) {
		return delegate.join(table, type, hint);
	}

	public TableOnStep<Record> join(TableLike<?> table) {
		return delegate.join(table);
	}

	public TableOptionalOnStep<Record> join(Path<?> path) {
		return delegate.join(path);
	}

	public TableOnStep<Record> join(SQL sql) {
		return delegate.join(sql);
	}

	public TableOnStep<Record> join(String sql) {
		return delegate.join(sql);
	}

	public TableOnStep<Record> join(String sql, Object... bindings) {
		return delegate.join(sql, bindings);
	}

	public TableOnStep<Record> join(String sql, QueryPart... parts) {
		return delegate.join(sql, parts);
	}

	public TableOnStep<Record> join(Name name) {
		return delegate.join(name);
	}

	public TableOnStep<Record> innerJoin(TableLike<?> table) {
		return delegate.innerJoin(table);
	}

	public TableOptionalOnStep<Record> innerJoin(Path<?> path) {
		return delegate.innerJoin(path);
	}

	public TableOnStep<Record> innerJoin(SQL sql) {
		return delegate.innerJoin(sql);
	}

	public TableOnStep<Record> innerJoin(String sql) {
		return delegate.innerJoin(sql);
	}

	public TableOnStep<Record> innerJoin(String sql, Object... bindings) {
		return delegate.innerJoin(sql, bindings);
	}

	public TableOnStep<Record> innerJoin(String sql, QueryPart... parts) {
		return delegate.innerJoin(sql, parts);
	}

	public TableOnStep<Record> innerJoin(Name name) {
		return delegate.innerJoin(name);
	}

	public TablePartitionByStep<Record> leftJoin(TableLike<?> table) {
		return delegate.leftJoin(table);
	}

	public TableOptionalOnStep<Record> leftJoin(Path<?> path) {
		return delegate.leftJoin(path);
	}

	public TablePartitionByStep<Record> leftJoin(SQL sql) {
		return delegate.leftJoin(sql);
	}

	public TablePartitionByStep<Record> leftJoin(String sql) {
		return delegate.leftJoin(sql);
	}

	public TablePartitionByStep<Record> leftJoin(String sql, Object... bindings) {
		return delegate.leftJoin(sql, bindings);
	}

	public TablePartitionByStep<Record> leftJoin(String sql, QueryPart... parts) {
		return delegate.leftJoin(sql, parts);
	}

	public TablePartitionByStep<Record> leftJoin(Name name) {
		return delegate.leftJoin(name);
	}

	public TablePartitionByStep<Record> leftOuterJoin(TableLike<?> table) {
		return delegate.leftOuterJoin(table);
	}

	public TableOptionalOnStep<Record> leftOuterJoin(Path<?> path) {
		return delegate.leftOuterJoin(path);
	}

	public TablePartitionByStep<Record> leftOuterJoin(SQL sql) {
		return delegate.leftOuterJoin(sql);
	}

	public TablePartitionByStep<Record> leftOuterJoin(String sql) {
		return delegate.leftOuterJoin(sql);
	}

	public TablePartitionByStep<Record> leftOuterJoin(String sql, Object... bindings) {
		return delegate.leftOuterJoin(sql, bindings);
	}

	public TablePartitionByStep<Record> leftOuterJoin(String sql, QueryPart... parts) {
		return delegate.leftOuterJoin(sql, parts);
	}

	public TablePartitionByStep<Record> leftOuterJoin(Name name) {
		return delegate.leftOuterJoin(name);
	}

	public TablePartitionByStep<Record> rightJoin(TableLike<?> table) {
		return delegate.rightJoin(table);
	}

	public TableOptionalOnStep<Record> rightJoin(Path<?> path) {
		return delegate.rightJoin(path);
	}

	public TablePartitionByStep<Record> rightJoin(SQL sql) {
		return delegate.rightJoin(sql);
	}

	public TablePartitionByStep<Record> rightJoin(String sql) {
		return delegate.rightJoin(sql);
	}

	public TablePartitionByStep<Record> rightJoin(String sql, Object... bindings) {
		return delegate.rightJoin(sql, bindings);
	}

	public TablePartitionByStep<Record> rightJoin(String sql, QueryPart... parts) {
		return delegate.rightJoin(sql, parts);
	}

	public TablePartitionByStep<Record> rightJoin(Name name) {
		return delegate.rightJoin(name);
	}

	public TablePartitionByStep<Record> rightOuterJoin(TableLike<?> table) {
		return delegate.rightOuterJoin(table);
	}

	public TableOptionalOnStep<Record> rightOuterJoin(Path<?> path) {
		return delegate.rightOuterJoin(path);
	}

	public TablePartitionByStep<Record> rightOuterJoin(SQL sql) {
		return delegate.rightOuterJoin(sql);
	}

	public TablePartitionByStep<Record> rightOuterJoin(String sql) {
		return delegate.rightOuterJoin(sql);
	}

	public TablePartitionByStep<Record> rightOuterJoin(String sql, Object... bindings) {
		return delegate.rightOuterJoin(sql, bindings);
	}

	public TablePartitionByStep<Record> rightOuterJoin(String sql, QueryPart... parts) {
		return delegate.rightOuterJoin(sql, parts);
	}

	public TablePartitionByStep<Record> rightOuterJoin(Name name) {
		return delegate.rightOuterJoin(name);
	}

	public TablePartitionByStep<Record> fullJoin(TableLike<?> table) {
		return delegate.fullJoin(table);
	}

	public TableOptionalOnStep<Record> fullJoin(Path<?> path) {
		return delegate.fullJoin(path);
	}

	public TablePartitionByStep<Record> fullJoin(SQL sql) {
		return delegate.fullJoin(sql);
	}

	public TablePartitionByStep<Record> fullJoin(String sql) {
		return delegate.fullJoin(sql);
	}

	public TablePartitionByStep<Record> fullJoin(String sql, Object... bindings) {
		return delegate.fullJoin(sql, bindings);
	}

	public TablePartitionByStep<Record> fullJoin(String sql, QueryPart... parts) {
		return delegate.fullJoin(sql, parts);
	}

	public TablePartitionByStep<Record> fullJoin(Name name) {
		return delegate.fullJoin(name);
	}

	public TablePartitionByStep<Record> fullOuterJoin(TableLike<?> table) {
		return delegate.fullOuterJoin(table);
	}

	public TableOptionalOnStep<Record> fullOuterJoin(Path<?> path) {
		return delegate.fullOuterJoin(path);
	}

	public TablePartitionByStep<Record> fullOuterJoin(SQL sql) {
		return delegate.fullOuterJoin(sql);
	}

	public TablePartitionByStep<Record> fullOuterJoin(String sql) {
		return delegate.fullOuterJoin(sql);
	}

	public TablePartitionByStep<Record> fullOuterJoin(String sql, Object... bindings) {
		return delegate.fullOuterJoin(sql, bindings);
	}

	public TablePartitionByStep<Record> fullOuterJoin(String sql, QueryPart... parts) {
		return delegate.fullOuterJoin(sql, parts);
	}

	public TablePartitionByStep<Record> fullOuterJoin(Name name) {
		return delegate.fullOuterJoin(name);
	}

	public Table<Record> crossJoin(TableLike<?> table) {
		return delegate.crossJoin(table);
	}

	public Table<Record> crossJoin(SQL sql) {
		return delegate.crossJoin(sql);
	}

	public Table<Record> crossJoin(String sql) {
		return delegate.crossJoin(sql);
	}

	public Table<Record> crossJoin(String sql, Object... bindings) {
		return delegate.crossJoin(sql, bindings);
	}

	public Table<Record> crossJoin(String sql, QueryPart... parts) {
		return delegate.crossJoin(sql, parts);
	}

	public Table<Record> crossJoin(Name name) {
		return delegate.crossJoin(name);
	}

	public Table<Record> naturalJoin(TableLike<?> table) {
		return delegate.naturalJoin(table);
	}

	public Table<Record> naturalJoin(SQL sql) {
		return delegate.naturalJoin(sql);
	}

	public Table<Record> naturalJoin(String sql) {
		return delegate.naturalJoin(sql);
	}

	public Table<Record> naturalJoin(String sql, Object... bindings) {
		return delegate.naturalJoin(sql, bindings);
	}

	public Table<Record> naturalJoin(Name name) {
		return delegate.naturalJoin(name);
	}

	public Table<Record> naturalJoin(String sql, QueryPart... parts) {
		return delegate.naturalJoin(sql, parts);
	}

	public Table<Record> naturalLeftOuterJoin(TableLike<?> table) {
		return delegate.naturalLeftOuterJoin(table);
	}

	public Table<Record> naturalLeftOuterJoin(SQL sql) {
		return delegate.naturalLeftOuterJoin(sql);
	}

	public Table<Record> naturalLeftOuterJoin(String sql) {
		return delegate.naturalLeftOuterJoin(sql);
	}

	public Table<Record> naturalLeftOuterJoin(String sql, Object... bindings) {
		return delegate.naturalLeftOuterJoin(sql, bindings);
	}

	public Table<Record> naturalLeftOuterJoin(String sql, QueryPart... parts) {
		return delegate.naturalLeftOuterJoin(sql, parts);
	}

	public Table<Record> naturalLeftOuterJoin(Name name) {
		return delegate.naturalLeftOuterJoin(name);
	}

	public Table<Record> naturalRightOuterJoin(TableLike<?> table) {
		return delegate.naturalRightOuterJoin(table);
	}

	public Table<Record> naturalRightOuterJoin(SQL sql) {
		return delegate.naturalRightOuterJoin(sql);
	}

	public Table<Record> naturalRightOuterJoin(String sql) {
		return delegate.naturalRightOuterJoin(sql);
	}

	public Table<Record> naturalRightOuterJoin(String sql, Object... bindings) {
		return delegate.naturalRightOuterJoin(sql, bindings);
	}

	public Table<Record> naturalRightOuterJoin(String sql, QueryPart... parts) {
		return delegate.naturalRightOuterJoin(sql, parts);
	}

	public Table<Record> naturalRightOuterJoin(Name name) {
		return delegate.naturalRightOuterJoin(name);
	}

	public Table<Record> naturalFullOuterJoin(TableLike<?> table) {
		return delegate.naturalFullOuterJoin(table);
	}

	public Table<Record> naturalFullOuterJoin(SQL sql) {
		return delegate.naturalFullOuterJoin(sql);
	}

	public Table<Record> naturalFullOuterJoin(String sql) {
		return delegate.naturalFullOuterJoin(sql);
	}

	public Table<Record> naturalFullOuterJoin(String sql, Object... bindings) {
		return delegate.naturalFullOuterJoin(sql, bindings);
	}

	public Table<Record> naturalFullOuterJoin(String sql, QueryPart... parts) {
		return delegate.naturalFullOuterJoin(sql, parts);
	}

	public Table<Record> naturalFullOuterJoin(Name name) {
		return delegate.naturalFullOuterJoin(name);
	}

	public Table<Record> crossApply(TableLike<?> table) {
		return delegate.crossApply(table);
	}

	public Table<Record> crossApply(SQL sql) {
		return delegate.crossApply(sql);
	}

	public Table<Record> crossApply(String sql) {
		return delegate.crossApply(sql);
	}

	public Table<Record> crossApply(String sql, Object... bindings) {
		return delegate.crossApply(sql, bindings);
	}

	public Table<Record> crossApply(String sql, QueryPart... parts) {
		return delegate.crossApply(sql, parts);
	}

	public Table<Record> crossApply(Name name) {
		return delegate.crossApply(name);
	}

	public Table<Record> outerApply(TableLike<?> table) {
		return delegate.outerApply(table);
	}

	public Table<Record> outerApply(SQL sql) {
		return delegate.outerApply(sql);
	}

	public Table<Record> outerApply(String sql) {
		return delegate.outerApply(sql);
	}

	public Table<Record> outerApply(String sql, Object... bindings) {
		return delegate.outerApply(sql, bindings);
	}

	public Table<Record> outerApply(String sql, QueryPart... parts) {
		return delegate.outerApply(sql, parts);
	}

	public Table<Record> outerApply(Name name) {
		return delegate.outerApply(name);
	}

	public TableOnStep<Record> straightJoin(TableLike<?> table) {
		return delegate.straightJoin(table);
	}

	public TableOptionalOnStep<Record> straightJoin(Path<?> path) {
		return delegate.straightJoin(path);
	}

	public TableOnStep<Record> straightJoin(SQL sql) {
		return delegate.straightJoin(sql);
	}

	public TableOnStep<Record> straightJoin(String sql) {
		return delegate.straightJoin(sql);
	}

	public TableOnStep<Record> straightJoin(String sql, Object... bindings) {
		return delegate.straightJoin(sql, bindings);
	}

	public TableOnStep<Record> straightJoin(String sql, QueryPart... parts) {
		return delegate.straightJoin(sql, parts);
	}

	public TableOnStep<Record> straightJoin(Name name) {
		return delegate.straightJoin(name);
	}

	public Condition eq(Table<R> arg2) {
		return delegate.eq(arg2);
	}

	public Condition equal(Table<R> arg2) {
		return delegate.equal(arg2);
	}

	public Condition ne(Table<R> arg2) {
		return delegate.ne(arg2);
	}

	public Condition notEqual(Table<R> arg2) {
		return delegate.notEqual(arg2);
	}

	public Field<RowId> rowid() {
		return delegate.rowid();
	}

	public boolean equals(Object other) {
		return delegate.equals(other);
	}

	public Table<R> useIndex(String... indexes) {
		return delegate.useIndex(indexes);
	}

	public Table<R> useIndexForJoin(String... indexes) {
		return delegate.useIndexForJoin(indexes);
	}

	public Table<R> useIndexForOrderBy(String... indexes) {
		return delegate.useIndexForOrderBy(indexes);
	}

	public Table<R> useIndexForGroupBy(String... indexes) {
		return delegate.useIndexForGroupBy(indexes);
	}

	public Table<R> ignoreIndex(String... indexes) {
		return delegate.ignoreIndex(indexes);
	}

	public Table<R> ignoreIndexForJoin(String... indexes) {
		return delegate.ignoreIndexForJoin(indexes);
	}

	public Table<R> ignoreIndexForOrderBy(String... indexes) {
		return delegate.ignoreIndexForOrderBy(indexes);
	}

	public Table<R> ignoreIndexForGroupBy(String... indexes) {
		return delegate.ignoreIndexForGroupBy(indexes);
	}

	public Table<R> forceIndex(String... indexes) {
		return delegate.forceIndex(indexes);
	}

	public Table<R> forceIndexForJoin(String... indexes) {
		return delegate.forceIndexForJoin(indexes);
	}

	public Table<R> forceIndexForOrderBy(String... indexes) {
		return delegate.forceIndexForOrderBy(indexes);
	}

	public Table<R> forceIndexForGroupBy(String... indexes) {
		return delegate.forceIndexForGroupBy(indexes);
	}

	public Table<Record> withOrdinality() {
		return delegate.withOrdinality();
	}

	public DivideByOnStep divideBy(Table<?> divisor) {
		return delegate.divideBy(divisor);
	}

	public TableOnStep<R> leftSemiJoin(TableLike<?> table) {
		return delegate.leftSemiJoin(table);
	}

	public TableOptionalOnStep<R> leftSemiJoin(Path<?> path) {
		return delegate.leftSemiJoin(path);
	}

	public TableOnStep<R> leftAntiJoin(TableLike<?> table) {
		return delegate.leftAntiJoin(table);
	}

	public TableOptionalOnStep<R> leftAntiJoin(Path<?> path) {
		return delegate.leftAntiJoin(path);
	}

	public R from(Record record) {
		return delegate.from(record);
	}

	public UniqueKey<R> getPrimaryKey() {
		return null; // delegate.getPrimaryKey();
	}

	public List<Index> getIndexes() {
		return Collections.emptyList(); // delegate.getIndexes();
	}

	public List<UniqueKey<R>> getKeys() {
		return Collections.emptyList(); // delegate.getKeys();
	}

	public List<ForeignKey<R, ?>> getReferences() {
		return Collections.emptyList(); // delegate.getReferences();
	}

	public List<UniqueKey<R>> getUniqueKeys() {
		return Collections.emptyList(); // delegate.getUniqueKeys();
	}

	public <O extends Record> List<ForeignKey<O, R>> getReferencesFrom(Table<O> other) {
		return Collections.emptyList(); // delegate.getReferencesFrom(other);
	}

	public <O extends Record> List<ForeignKey<R, O>> getReferencesTo(Table<O> other) {
		return Collections.emptyList(); // delegate.getReferencesTo(other);
	}

	public Fields fieldsIncludingHidden() {
		return new AuditFields(delegate.fieldsIncludingHidden()); // delegate.fieldsIncludingHidden()
	}
	
	
}