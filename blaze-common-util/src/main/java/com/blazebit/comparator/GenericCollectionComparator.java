/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.comparator;

import org.apache.commons.collections.CollectionUtils;

/**
 * 
 * @author cchet
 */
public class GenericCollectionComparator<T> extends GenericComparator<T> {

	private int index;

	/**
	 * @param field
	 * @param index
	 */
	public GenericCollectionComparator(String field, int index) {
		super(field);
		this.index = index;
	}

	@Override
	@SuppressWarnings("unchecked")
	public int compare(T object1, T object2) {
		if (object1 == null || object2 == null) {
			return compareNullObjects(object1, object2);
		}

		return super.compare((T) CollectionUtils.get(object1, index),
				(T) CollectionUtils.get(object2, index));
	}
}
