package com.blazebit.i18n;

import java.util.*;

public final class LocaleUtils {

    private LocaleUtils() {
    }

    public static Locale getLocale(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }

        int found = 0, position = 0;
        char[] chars = string.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '_') {
                switch (found) {
                    case 0:
                        string = new String(chars, position, i - position);
                        position = i + 1;
                        break;
                    case 1:
                        if (chars.length > i + 1) {
                            return new Locale(string, new String(chars, position, i
                                    - position), new String(chars, i + 1,
                                    chars.length - i - 1));
                        } else {
                            return new Locale(string, new String(chars, position, i
                                    - position), "");
                        }
                }

                found++;
            }
        }

        switch (found) {
            case 0:
                return new Locale(string);
            case 1:
                return new Locale(string, new String(chars, position, chars.length
                        - position));
        }

        // Should never happen
        return null;
    }

    public static Locale resolveLocale(Iterable<Locale> preferredLocales,
                                       Collection<Locale> supportedLocales, Locale defaultLocale) {
        return resolveLocale(preferredLocales.iterator(), supportedLocales,
                defaultLocale);
    }

    public static Locale resolveLocale(Iterable<Locale> preferredLocales,
                                       Collection<Locale> supportedLocales, Locale defaultLocale,
                                       boolean strictVarianMatching) {
        return resolveLocale(preferredLocales.iterator(), supportedLocales,
                defaultLocale, strictVarianMatching);
    }

    public static Locale resolveLocale(Iterator<Locale> preferredLocales,
                                       Collection<Locale> supportedLocales, Locale defaultLocale) {
        return resolveLocale(preferredLocales, supportedLocales, defaultLocale,
                true);
    }

    public static Locale resolveLocale(Iterator<Locale> preferredLocales,
                                       Collection<Locale> supportedLocales, Locale defaultLocale,
                                       boolean strictVarianMatching) {
        return resolveLocale(preferredLocales, supportedLocales, defaultLocale,
                true, strictVarianMatching);
    }

    /**
     * Resolves the best matching locale from the given set of preferred
     * locales, supported locales and the default locale. Use ordered
     * collections to get deterministic results and prevent unexpected behavior.
     * <p>
     * The algorithm for resolving works as follows:
     * <ol>
     * <li>If no supported or preferred locales are given, return default
     * locale.</li>
     * <li>
     * Iterate over preferred locales
     * <ol>
     * <li>If a perfect match can be found, return it.</li>
     * <li>If a supported locale without a variant that matches the language and
     * country of the preferred locale can be found, return it</li>
     * <li>If strict variants matching is turned off and a supported locale that
     * matches the language and country of the preferred locale can be found,
     * return it</li>
     * <li>If a supported locale without a variant and country that matches the
     * language of the preferred locale can be found, return it</li>
     * </ol>
     * </li>
     * <li>If country matching is turned on, return the first locale of the
     * supported locales that matches the country</li>
     * <li>Return the default locale</li>
     * </ol>
     *
     * @param preferredLocales      The preferred locales to search for. The order of the locales
     *                              returned by the iterator represents the preference.
     * @param supportedLocales      The available locales to search from. The order of the locales
     *                              in the collection represents the preference.
     * @param defaultLocale         The default locale if no match can be found.
     * @param countryMatching       True if country matching should be turned on, otherwise false.
     * @param strictVariantMatching True if a resolved locale may also contain a supported locale
     *                              that has a different variant than a preferred locale,
     *                              otherwise false.
     * @return Returns the best match or the default locale if nothing matches.
     */
    public static Locale resolveLocale(Iterator<Locale> preferredLocales,
                                       Collection<Locale> supportedLocales, Locale defaultLocale,
                                       boolean countryMatching, boolean strictVariantMatching) {
        // No point if there are no options
        if (supportedLocales == null || supportedLocales.isEmpty()) {
            return defaultLocale;
        } else if (preferredLocales == null || !preferredLocales.hasNext()) {
            return defaultLocale;
        }

		/* Use this as last fallback before using default locale */
        final List<Locale> countryMatchingLocales = new ArrayList<Locale>();

        do {
            final Locale preferredLocale = preferredLocales.next();

            if (supportedLocales.contains(preferredLocale)
                    || (defaultLocale != null && defaultLocale
                    .equals(preferredLocale))) {
                /* If we have a perfect match, just return it */
                return preferredLocale;
            }

            final String preferredLocaleLanguage = preferredLocale
                    .getLanguage();
            final String preferredLocaleCountry = preferredLocale.getCountry();

			/* Skip locales that have no language */
            if (empty(preferredLocaleLanguage)) {
                continue;
            }

            Locale lastLanguageMatch = null, lastLanguageAndCountryMatch = null;
            boolean languageMatching = false;

            for (Locale supportedLocale : supportedLocales) {
                if (supportedLocale != null) {
                    if (equal(preferredLocaleLanguage,
                            supportedLocale.getLanguage())) {
                        languageMatching = true;

                        if (equal(supportedLocale.getCountry(),
                                preferredLocaleCountry)) {
							/*
							 * the country of the language matching locale
							 * matches the preferred locale country
							 */

                            if (lastLanguageAndCountryMatch == null
                                    && (!strictVariantMatching || empty(supportedLocale
                                    .getVariant()))) {
								/*
								 * Use the first language and country matching
								 * locale if strict variant matching is turned
								 * off or it has no variant
								 */
                                lastLanguageAndCountryMatch = supportedLocale;
                            } else if (lastLanguageAndCountryMatch != null
                                    && empty(supportedLocale.getVariant())) {
								/*
								 * Only use language and country matching locale
								 * if it has no variant
								 */
                                lastLanguageAndCountryMatch = supportedLocale;
                            }
                        } else if (lastLanguageMatch == null
                                || empty(supportedLocale.getCountry())) {
							/* Use a language only matching locale */
                            lastLanguageMatch = supportedLocale;
                        }
                    } else if (countryMatching
                            && !languageMatching
                            && equal(preferredLocaleCountry,
                            supportedLocale.getCountry())) {
						/*
						 * We only care about country matches if no language
						 * matches can be found, since a language match is much
						 * more worth. This is a optimization concerning memory
						 * consumption. Preserve country match if no perfect
						 * match can be found
						 */
                        countryMatchingLocales.add(supportedLocale);
                    }
                }
            }

			/* Return language match if we have one */
            if (lastLanguageAndCountryMatch != null
                    || lastLanguageMatch != null) {
                return lastLanguageAndCountryMatch != null ? lastLanguageAndCountryMatch
                        : lastLanguageMatch;
            }
        } while (preferredLocales.hasNext());

		/* This is our best bet, default locale may be even worse */
        if (countryMatching && !countryMatchingLocales.isEmpty()) {
            return countryMatchingLocales.get(0);
        }

		/* Could not find any better match, so just use the default */
        return defaultLocale;
    }

    private static boolean empty(String s) {
        return s == null || s.isEmpty();
    }

    private static boolean equal(String s1, String s2) {
        return s1 != null && s1.equalsIgnoreCase(s2);
    }
}
