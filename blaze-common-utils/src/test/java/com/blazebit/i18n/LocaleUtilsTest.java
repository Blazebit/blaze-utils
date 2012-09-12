package com.blazebit.i18n;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

public class LocaleUtilsTest {
	
	private static Locale locale(String locale){
		return LocaleUtils.getLocale(locale);
	}

	private static List<Locale> locales(String... locales){
		List<Locale> list = new ArrayList<Locale>();
		
		for(String l : locales){
			list.add(LocaleUtils.getLocale(l));
		}
		
		return list;
	}
	
	@Test
	public void testGetLocale() {
		assertEquals(LocaleUtils.getLocale("en"), new Locale("en"));
		assertEquals(LocaleUtils.getLocale("en_US"), new Locale("en", "US"));
		assertEquals(LocaleUtils.getLocale("en_US_NY"), new Locale("en", "US", "NY"));
	}
	
	/******************************
	 * Perfect Matching Tests     *
	 ******************************/
	
	@Test
	public void testPerfectMatchEnsuringPriorityResolving1() {
		List<Locale> supportedLocales = locales("de_AT", "de_DE", "de_CH", "de");
		List<Locale> preferredLocales = locales("de_AT", "de_DE", "en_US");
		Locale defaultLocale = locale("en_US");
		Locale expectedResult = locale("de_AT");
		
		assertEquals(expectedResult, LocaleUtils.resolveLocale(preferredLocales, supportedLocales, defaultLocale));
	}
	
	@Test
	public void testPerfectMatchEnsuringPriorityResolving2() {
		List<Locale> supportedLocales = locales("de_AT", "de_DE", "de_CH", "de");
		List<Locale> preferredLocales = locales("de_DE", "de_AT", "en_US");
		Locale defaultLocale = locale("en_US");
		Locale expectedResult = locale("de_DE");
		
		assertEquals(expectedResult, LocaleUtils.resolveLocale(preferredLocales, supportedLocales, defaultLocale));
	}
	
	@Test
	public void testPerfectMatchEnsuringPriorityResolving3() {
		List<Locale> supportedLocales = locales("de_AT", "de_DE", "de_CH", "de");
		List<Locale> preferredLocales = locales("en_US", "de_DE", "de_AT");
		Locale defaultLocale = locale("en_US");
		Locale expectedResult = locale("en_US");
		
		assertEquals(expectedResult, LocaleUtils.resolveLocale(preferredLocales, supportedLocales, defaultLocale));
	}

	/******************************
	 * Language Fallback Tests    *
	 ******************************/
	
	@Test
	public void testLanguageFallbackEnsuringLanguageMatchResolving1() {
		List<Locale> supportedLocales = locales("de_AT", "de_DE", "de", "en_US");
		List<Locale> preferredLocales = locales("de_CH", "en_US");
		Locale defaultLocale = locale("en_US");
		Locale expectedResult = locale("de");
		
		assertEquals(expectedResult, LocaleUtils.resolveLocale(preferredLocales, supportedLocales, defaultLocale));
	}

	@Test
	public void testLanguageFallbackEnsuringLanguageMatchResolving2() {
		List<Locale> supportedLocales = locales("de_AT", "de_DE", "de", "en_UK", "en");
		List<Locale> preferredLocales = locales("fr_CH", "en_US");
		Locale defaultLocale = locale("en_UK");
		Locale expectedResult = locale("en");
		
		assertEquals(expectedResult, LocaleUtils.resolveLocale(preferredLocales, supportedLocales, defaultLocale));
	}

	@Test
	public void testLanguageFallbackEnsuringLanguageMatchResolving3() {
		List<Locale> supportedLocales = locales("de_AT", "de_DE", "de", "en_UK", "en_US");
		List<Locale> preferredLocales = locales("fr_CH", "en_US");
		Locale defaultLocale = locale("en_US");
		Locale expectedResult = locale("en_US");
		
		assertEquals(expectedResult, LocaleUtils.resolveLocale(preferredLocales, supportedLocales, defaultLocale));
	}

	@Test
	public void testLanguageFallbackEnsuringLanguageMatchResolving4() {
		List<Locale> supportedLocales = locales("de_AT_UA", "de_AT_VB", "de_DE", "de", "en_US");
		List<Locale> preferredLocales = locales("de_AT_VI");
		Locale defaultLocale = locale("en_US");
		Locale expectedResult = locale("de");
		
		assertEquals(expectedResult, LocaleUtils.resolveLocale(preferredLocales, supportedLocales, defaultLocale));
	}

	@Test
	public void testLanguageFallbackEnsuringLanguageMatchResolving5() {
		List<Locale> supportedLocales = locales("de_AT_UA", "de_AT_VB", "de_AT", "de_DE", "de", "en_US");
		List<Locale> preferredLocales = locales("de_AT_VI");
		Locale defaultLocale = locale("en_US");
		Locale expectedResult = locale("de_AT");
		
		assertEquals(expectedResult, LocaleUtils.resolveLocale(preferredLocales, supportedLocales, defaultLocale));
	}

	@Test
	public void testLanguageFallbackEnsuringLanguageMatchResolving6() {
		List<Locale> supportedLocales = locales("de_AT_UA", "de_AT_VB", "de_AT", "de_DE", "de", "en_US");
		List<Locale> preferredLocales = locales("de_AT_VI");
		Locale defaultLocale = locale("en_US");
		Locale expectedResult = locale("de_AT");
		
		assertEquals(expectedResult, LocaleUtils.resolveLocale(preferredLocales, supportedLocales, defaultLocale, false));
	}

	@Test
	public void testLanguageFallbackEnsuringLanguageMatchResolving7() {
		List<Locale> supportedLocales = locales("de_AT_UA", "de_AT_VB", "de_DE", "de", "en_US");
		List<Locale> preferredLocales = locales("de_AT_VI");
		Locale defaultLocale = locale("en_US");
		Locale expectedResult = locale("de_AT_UA");
		
		assertEquals(expectedResult, LocaleUtils.resolveLocale(preferredLocales, supportedLocales, defaultLocale, false));
	}

	@Test
	public void testLanguageFallbackEnsuringLanguageMatchResolving8() {
		List<Locale> supportedLocales = locales("de_AT_UA", "de_AT_VB", "en_US");
		List<Locale> preferredLocales = locales("de_DE_BE");
		Locale defaultLocale = locale("en_US");
		Locale expectedResult = locale("de_AT_UA");
		
		assertEquals(expectedResult, LocaleUtils.resolveLocale(preferredLocales, supportedLocales, defaultLocale));
	}

	@Test
	public void testLanguageFallbackEnsuringLanguageMatchAndPriorityResolving() {
		List<Locale> supportedLocales = locales("de_AT", "de_DE", "en_US");
		List<Locale> preferredLocales = locales("de_CH", "fr_CH");
		Locale defaultLocale = locale("en_US");
		Locale expectedResult = locale("de_AT");
		
		assertEquals(expectedResult, LocaleUtils.resolveLocale(preferredLocales, supportedLocales, defaultLocale));
	}

	@Test
	public void testLanguageFallbackEnsuringLanguageAndCountryMatchResolving() {
		List<Locale> supportedLocales = locales("de_AT", "de_AT_UA", "de_AT_VB", "de_DE", "de", "en_US");
		List<Locale> preferredLocales = locales("de_AT_VI", "de_DE");
		Locale defaultLocale = locale("en_US");
		Locale expectedResult = locale("de_AT");
		
		assertEquals(expectedResult, LocaleUtils.resolveLocale(preferredLocales, supportedLocales, defaultLocale));
	}

	/******************************
	 * Country Fallback Tests     *
	 ******************************/

	@Test
	public void testCountryFallbackEnsuringPriorityResolving1() {
		List<Locale> supportedLocales = locales("de_AT", "de_DE", "de_CH", "en_CH");
		List<Locale> preferredLocales = locales("fr_CH");
		Locale defaultLocale = locale("en_US");
		Locale expectedResult = locale("de_CH");
		
		assertEquals(expectedResult, LocaleUtils.resolveLocale(preferredLocales, supportedLocales, defaultLocale));
	}

	@Test
	public void testCountryFallbackEnsuringPriorityResolving2() {
		List<Locale> supportedLocales = locales("de_AT", "de_DE", "en_CH", "de_CH");
		List<Locale> preferredLocales = locales("fr_CH");
		Locale defaultLocale = locale("en_US");
		Locale expectedResult = locale("en_CH");
		
		assertEquals(expectedResult, LocaleUtils.resolveLocale(preferredLocales, supportedLocales, defaultLocale));
	}

	/******************************
	 * Default Fallback Tests     *
	 ******************************/

	@Test
	public void testDefaultFallback() {
		List<Locale> supportedLocales = locales("de_AT", "de_DE", "de_CH", "en_US");
		List<Locale> preferredLocales = locales("fr_FR");
		Locale defaultLocale = locale("en_US");
		Locale expectedResult = locale("en_US");
		
		assertEquals(expectedResult, LocaleUtils.resolveLocale(preferredLocales, supportedLocales, defaultLocale));
	}

}
