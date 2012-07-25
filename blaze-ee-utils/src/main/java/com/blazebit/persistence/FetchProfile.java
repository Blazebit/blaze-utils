/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.persistence;

/**
 * FetchProfile holds information about the fetching of property paths. This
 * class is can be used with FetchUtil and simply holds the property paths of
 * relations that should be fetched.
 * 
 * @param <T>
 *            The type of the on which the fetching should be applied
 * @author Christian Beikov
 * @see FetchUtil
 */
public class FetchProfile<T> {

	private Class<T> clazz;
	private String[] propertyPaths;

	/**
	 * Constructs an immutable FetchProfile object for the given class type and
	 * the given property paths.
	 * 
	 * @param clazz
	 *            The class from which to fetch relations
	 * @param propertyPaths
	 *            The property paths that stand for the relations
	 */
	public FetchProfile(Class<T> clazz, String... propertyPaths) {
		this.clazz = clazz;
		this.propertyPaths = propertyPaths;
	}

	/**
	 * Returns the class from which to fetch relations.
	 * 
	 * @return The class from which to fetch relations.
	 */
	public Class<T> getClazz() {
		return clazz;
	}

	/**
	 * Returns the property paths that stand for the relations
	 * 
	 * @return The property paths that stand for the relations
	 */
	public String[] getPropertyPaths() {
		return propertyPaths.clone();
	}

}
