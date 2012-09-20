/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.persistence;

import java.sql.Blob;
import java.sql.Clob;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import com.blazebit.reflection.ReflectionUtils;
import com.blazebit.text.FormatUtils;

/**
 * 
 * 
 * @author Christian Beikov
 * @since 0.1.2
 */
public class FetchUtils {

	private static final Logger log = Logger.getLogger(FetchUtils.class
			.getName());

	public static String getFetchProfilePlaceholder(Class<?> clazz) {
		return "FETCH_PROFILE_" + clazz.getSimpleName();
	}

	public static String getFullQualifiedFetchProfilePlaceholder(Class<?> clazz) {
		return "FETCH_PROFILE_" + clazz.getName();
	}

	public static String query(String query, String alias, FetchProfile<?> f) {
		// Retrieve the simple fetch profile placeholder and check if it is
		// present in the query
		String fetchProfilePlaceholder = getFetchProfilePlaceholder(f
				.getClazz());
		int placeholderIndex = query.indexOf(fetchProfilePlaceholder);

		// If the place holder is not present retrieve if the full qualified
		// place holder
		if (placeholderIndex < 0) {
			fetchProfilePlaceholder = getFullQualifiedFetchProfilePlaceholder(f
					.getClazz());
			placeholderIndex = query.indexOf(fetchProfilePlaceholder);
		}

		// If no place holder is present fail
		if (placeholderIndex < 0) {
			throw new IllegalArgumentException(
					"The fetch profile placeholder '" + fetchProfilePlaceholder
							+ "' could not be found in the query!");
		}

		StringBuilder querySb = new StringBuilder(query);
		StringBuilder sb = new StringBuilder();
		String[] fields = f.getPropertyPaths();
		Map<String, String> classBasedPropertyAliasMap = new HashMap<String, String>();

		// Iterate through all property paths defined in the fetch profile
		for (int i = 0; i < fields.length; i++) {
			String[] propertyNames = fields[i].split("\\.");
			Class<?> currentClass = f.getClazz();
			Class<?> fieldClass;
			boolean fieldCollectionType;
			boolean fieldMapType;
			String parentElement = alias;
			StringBuilder propertyPath = new StringBuilder();

			// Iterate through all property names
			for (int j = 0; j < propertyNames.length; j++) {
				String propertyName = propertyNames[j];
				fieldClass = ReflectionUtils.getFieldType(currentClass,
						propertyName);
				fieldCollectionType = ReflectionUtils.isSubtype(fieldClass,
						Collection.class);

				// avoid call when not necessary
				fieldMapType = fieldCollectionType ? false : ReflectionUtils
						.isSubtype(fieldClass, Map.class);

				// FIXED: add exceptions when type arguments are not sufficient
				if (fieldCollectionType) {
					Class<?>[] types = ReflectionUtils
							.getResolvedFieldTypeArguments(currentClass,
									propertyName);

					if (types.length != 1) {
						throw new IllegalArgumentException(
								"No type parameter given for collection type in class "
										+ currentClass + " for field "
										+ propertyName);
					}

					fieldClass = types[0];
				} else if (fieldMapType) {
					Class<?>[] types = ReflectionUtils
							.getResolvedFieldTypeArguments(currentClass,
									propertyName);

					if (types.length != 2) {
						throw new IllegalArgumentException(
								"No type parameter given for map type in class "
										+ currentClass + " for field "
										+ propertyName);
					}

					fieldClass = types[1];
				}

				// Fail if the field class can not be retrieved
				if (fieldClass == null) {
					throw new IllegalArgumentException("Field with name "
							+ propertyName + " was not found within class "
							+ currentClass.getName());
				}

				// Parseable types do not need to be fetched, so also sub
				// properties would not have to be fetched
				if (FormatUtils.isParseableType(fieldClass)
						|| Blob.class.equals(fieldClass)
						|| Clob.class.equals(fieldClass)
						|| new byte[0].getClass().equals(fieldClass)) {
					log.info(new StringBuilder("Field with name ")
							.append(propertyName)
							.append(" of class ")
							.append(currentClass.getName())
							.append(" is parseable and therefore it has not to be fetched explicitly.")
							.toString());

					if (i + 1 < fields.length) {
						throw new IllegalArgumentException(
								"Can not fetch anything from a simple property!");
					}

					break;
				} else {
					// Add dots if the current property is not the first
					if (j != 0) {
						propertyPath.append(".");
					}

					propertyPath.append(propertyName);
				}

				currentClass = fieldClass;

				// If an alias for the current property path already exists,
				// do not fetch, because it already has been fetched before
				if (!classBasedPropertyAliasMap.containsKey(propertyPath
						.toString())) {
					String currentAlias = "_" + i + "_" + propertyName
							+ "_element_" + j;
					boolean hasAlias = true;

					sb.append(" LEFT OUTER JOIN FETCH ");

					if (!fieldCollectionType && !fieldMapType) {
						// FIXED: Check for collection type of the fieldClass
						// if it is a collection type, just prepend the alias
						// if not, we have to use the whole property path
						// => Hibernate Bug
						hasAlias = false;
						currentAlias = parentElement + "." + propertyName;
					}

					if (j == 0) {
						// add fetching from parent element
						sb.append(parentElement);
					} else {
						// add fetching from alias of the parent element
						sb.append(classBasedPropertyAliasMap.get(parentElement));
					}

					// set up alias for JPQL query for the fetch
					sb.append(".");
					sb.append(propertyName);

					if (hasAlias) {
						sb.append(" ");
						sb.append(currentAlias);
					}

					// set the alias for the property path
					classBasedPropertyAliasMap.put(propertyPath.toString(),
							currentAlias);
				}

				// current property path gets parent element for the next
				// property
				parentElement = propertyPath.toString();
			}
		}

		// Replace the place holder with the fetching
		return querySb.replace(placeholderIndex,
				placeholderIndex + fetchProfilePlaceholder.length(),
				sb.toString()).toString();
	}
	
	public static <X> CriteriaQuery<X> fetch(Class<X> clazz, CriteriaBuilder cb, String... propertyPaths){
		CriteriaQuery<X> query = cb.createQuery(clazz);
		Root<X> root = query.from(clazz);
		
		for (String propertyPath : propertyPaths) {
            FetchParent<X, X> fetch = root;
            
            for (String pathSegment : propertyPath.split("\\.")) {
                fetch = fetch.fetch(pathSegment, JoinType.LEFT);
            }
        }
 
		
		return query;
	}
}