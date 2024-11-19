package com.code.aon.faces.component.richfaces.jsf.ui;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import javax.faces.component.UIComponent;

import org.richfaces.component.html.HtmlMenuItem;

class DelegateList<T> implements List<T> {
	
	private List<T> list;

	public DelegateList(List<T> list) {
		super();
		this.list = list;
	}

	public void forEach(Consumer<? super T> action) {
		list.forEach(action);
	}

	public int size() {
		return list.size();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public boolean contains(Object o) {
		return list.contains(o);
	}

	public Iterator<T> iterator() {
		return list.iterator();
	}

	public Object[] toArray() {
		return list.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	public boolean add(T e) {
		return list.add(e);
	}

	public boolean remove(Object o) {
		return list.remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	public boolean addAll(Collection<? extends T> c) {
		return list.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends T> c) {
		return list.addAll(index, c);
	}

	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	public void replaceAll(UnaryOperator<T> operator) {
		list.replaceAll(operator);
	}

	public <T> T[] toArray(IntFunction<T[]> generator) {
		return list.toArray(generator);
	}

	public void sort(Comparator<? super T> c) {
		list.sort(c);
	}

	public void clear() {
		list.clear();
	}

	public boolean equals(Object o) {
		return list.equals(o);
	}

	public int hashCode() {
		return list.hashCode();
	}

	public T get(int index) {
		return list.get(index);
	}

	public T set(int index, T element) {
		return list.set(index, element);
	}

	public void add(int index, T element) {
		list.add(index, element);
	}

	public boolean removeIf(Predicate<? super T> filter) {
		return list.removeIf(filter);
	}

	public T remove(int index) {
		return list.remove(index);
	}

	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	public ListIterator<T> listIterator() {
		return list.listIterator();
	}

	public ListIterator<T> listIterator(int index) {
		return list.listIterator(index);
	}

	public List<T> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	public Spliterator<T> spliterator() {
		return list.spliterator();
	}

	public void addFirst(T e) {
		list.addFirst(e);
	}

	public void addLast(T e) {
		list.addLast(e);
	}

	public T getFirst() {
		return list.getFirst();
	}

	public Stream<T> stream() {
		return list.stream();
	}

	public T getLast() {
		return list.getLast();
	}

	public Stream<T> parallelStream() {
		return list.parallelStream();
	}

	public T removeFirst() {
		return list.removeFirst();
	}

	public T removeLast() {
		return list.removeLast();
	}

	public List<T> reversed() {
		return list.reversed();
	}
    
    
    
}