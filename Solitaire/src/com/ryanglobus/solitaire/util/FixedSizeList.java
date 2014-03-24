package com.ryanglobus.solitaire.util;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;

public class FixedSizeList<E> extends AbstractList<E> implements Serializable {
	
	private static final long serialVersionUID = 6443314545409372887L;
	private final Object[] data;
	
	public FixedSizeList() {
		this(0);
	}
	
	public FixedSizeList(Collection<? extends E> collection) {
		this(collection.size());
		int i = 0;
		for (E elem : collection) {
			set(i, elem);
			i++;
		}
	}
	
	public FixedSizeList(int size) {
		if (size < 0)
			throw new IllegalArgumentException("FixedSizeList must have non-negative size.");
		data = new Object[size];
	}

	@SuppressWarnings("unchecked")
	@Override
	public E get(int index) {
		if (index < 0 || index >= size())
			throw new IndexOutOfBoundsException();
		return (E) data[index];
	}

	@Override
	public int size() {
		return data.length;
	}
	
	@Override
	public E set(int index, E elem) {
		if (index < 0 || index >= size())
			throw new IndexOutOfBoundsException();
		E oldValue = get(index);
		data[index] = elem;
		return oldValue;
	}

}
