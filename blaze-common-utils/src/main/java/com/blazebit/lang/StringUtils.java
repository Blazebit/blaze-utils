package com.blazebit.lang;

import java.util.Iterator;

public final class StringUtils {

	private StringUtils() {
	}

	public static boolean isEmpty(CharSequence sequence) {
		if (sequence == null || sequence.length() == 0) {
			return true;
		}

		return false;
	}

	public static boolean isEmpty(CharSequence... sequence) {
		if (sequence == null || sequence.length == 0) {
			return true;
		}

		for (CharSequence s : sequence) {
			if (s == null || s.length() == 0) {
				return true;
			}
		}

		return false;
	}

	public static String join(CharSequence delimiter, CharSequence... parts) {
		return join(
				new StringBuilder((parts.length + delimiter.length()) * 10),
				delimiter, parts).toString();
	}

	public static StringBuilder join(StringBuilder sb, CharSequence delimiter,
			CharSequence... parts) {
		if (parts.length == 0) {
			return sb;
		}

		final int last = parts.length - 1;

		for (int i = 0; i < last; i++) {
			sb.append(parts[i]);
			sb.append(delimiter);
		}

		sb.append(parts[last]);
		return sb;
	}

	public static String join(CharSequence delimiter,
			Iterable<? extends CharSequence> parts) {
		return join(new StringBuilder(), delimiter, parts).toString();
	}

	public static <X, Y extends CharSequence> String join(CharSequence delimiter, Iterable<X> parts,
			ValueRetriever<X, Y> expression) {
		return join(new StringBuilder(), delimiter, parts, expression)
				.toString();
	}

	public static StringBuilder join(StringBuilder sb, CharSequence delimiter,
			Iterable<? extends CharSequence> parts) {
		return join(sb, delimiter, parts.iterator());
	}

	public static <X, Y extends CharSequence> StringBuilder join(StringBuilder sb,
			CharSequence delimiter, Iterable<X> parts,
			ValueRetriever<X, Y> expression) {
		return join(sb, delimiter, parts.iterator(), expression);
	}

	public static String join(CharSequence delimiter,
			Iterator<? extends CharSequence> iter) {
		return join(new StringBuilder(), delimiter, iter).toString();
	}

	public static <X, Y extends CharSequence> String join(CharSequence delimiter, Iterator<X> iter,
			ValueRetriever<X, Y> expression) {
		return join(new StringBuilder(), delimiter, iter, expression)
				.toString();
	}

	public static StringBuilder join(StringBuilder sb, CharSequence delimiter,
			Iterator<? extends CharSequence> iter) {
		if (!iter.hasNext()) {
			return sb;
		}

		sb.append(iter.next());

		while (iter.hasNext()) {
			sb.append(delimiter);
			sb.append(iter.next());
		}

		return sb;
	}

	public static <X, Y extends CharSequence> StringBuilder join(StringBuilder sb,
			CharSequence delimiter, Iterator<X> iter,
			ValueRetriever<X, Y> expression) {
		if (!iter.hasNext()) {
			return sb;
		}

		sb.append(expression.getValue(iter.next()).toString());

		while (iter.hasNext()) {
			sb.append(delimiter);
			sb.append(expression.getValue(iter.next()).toString());
		}

		return sb;
	}

	public static String firstToUpper(CharSequence s) {
		return addFirstToUpper(new StringBuilder(s.length()), s).toString();
	}

	public static StringBuilder addFirstToUpper(StringBuilder sb, CharSequence s) {
		sb.append(Character.toUpperCase(s.charAt(0)));
		sb.append(s, 1, s.length());
		return sb;
	}

	public static String firstToLower(CharSequence s) {
		return addFirstToLower(new StringBuilder(s.length()), s).toString();
	}

	public static StringBuilder addFirstToLower(StringBuilder sb, CharSequence s) {
		sb.append(Character.toLowerCase(s.charAt(0)));
		sb.append(s, 1, s.length());
		return sb;
	}
}
