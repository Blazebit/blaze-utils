/*
 * Copyright 2011 Blazebit
 */
package com.blazebit.text;

import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

/**
 * @author Christian Beikov
 */
public class FormatUtilsTest {

    static Map<Class<?>, Serializable> VALUES;

    static {
        Map<Class<?>, Serializable> values = new HashMap<>();
        values.put(Integer.TYPE, Integer.valueOf(1));
        values.put(Long.TYPE, Long.valueOf(1));
        values.put(Double.TYPE, Double.valueOf(1));
        values.put(Float.TYPE, Float.valueOf(1));
        values.put(Boolean.TYPE, Boolean.TRUE);
        values.put(Character.TYPE, 'A');
        values.put(Byte.TYPE, (byte) 1);
        values.put(Short.TYPE, (short) 1);
        values.put(Integer.class, Integer.valueOf(1));
        values.put(Long.class, Long.valueOf(1));
        values.put(Double.class, Double.valueOf(1));
        values.put(Float.class, Float.valueOf(1));
        values.put(Boolean.class, Boolean.TRUE);
        values.put(Character.class, 'A');
        values.put(Byte.class, (byte) 1);
        values.put(Short.class, (short) 1);
        values.put(String.class, "A");
        values.put(BigInteger.class, BigInteger.valueOf(1));
        values.put(BigDecimal.class, BigDecimal.valueOf(1));
        values.put(Date.class, new Date());
        values.put(java.sql.Date.class, new java.sql.Date(System.currentTimeMillis()));
        values.put(Timestamp.class, new Timestamp(System.currentTimeMillis()));
        values.put(Time.class, new Time(System.currentTimeMillis()));
        values.put(Calendar.class, Calendar.getInstance());
        values.put(GregorianCalendar.class, new GregorianCalendar());
        values.put(Locale.class, Locale.GERMANY);
        values.put(TimeZone.class, TimeZone.getTimeZone("UTC"));
        values.put(Currency.class, Currency.getInstance("EUR"));
        values.put(Class.class, String.class);
        values.put(UUID.class, UUID.randomUUID());

        try {
            values.put(URL.class, new URL("https://blazebit.com"));
            values.put(Class.forName("java.time.Instant"), (Serializable) Class.forName("java.time.Instant").getMethod("now").invoke(null));
            values.put(Class.forName("java.time.LocalDateTime"), (Serializable) Class.forName("java.time.LocalDateTime").getMethod("now").invoke(null));
            values.put(Class.forName("java.time.LocalDate"), (Serializable) Class.forName("java.time.LocalDate").getMethod("now").invoke(null));
            values.put(Class.forName("java.time.LocalTime"), (Serializable) Class.forName("java.time.LocalTime").getMethod("now").invoke(null));
            values.put(Class.forName("java.time.OffsetTime"), (Serializable) Class.forName("java.time.OffsetTime").getMethod("now").invoke(null));
            values.put(Class.forName("java.time.OffsetDateTime"), (Serializable) Class.forName("java.time.OffsetDateTime").getMethod("now").invoke(null));
            values.put(Class.forName("java.time.ZonedDateTime"), (Serializable) Class.forName("java.time.ZonedDateTime").getMethod("now").invoke(null));
            values.put(Class.forName("java.time.Duration"), (Serializable) Class.forName("java.time.Duration").getField("ZERO").get(null));
            values.put(Class.forName("java.time.MonthDay"), (Serializable) Class.forName("java.time.MonthDay").getMethod("now").invoke(null));
            values.put(Class.forName("java.time.Year"), (Serializable) Class.forName("java.time.Year").getMethod("now").invoke(null));
            values.put(Class.forName("java.time.YearMonth"), (Serializable) Class.forName("java.time.YearMonth").getMethod("now").invoke(null));
            values.put(Class.forName("java.time.Period"), (Serializable) Class.forName("java.time.Period").getField("ZERO").get(null));
            values.put(Class.forName("java.time.ZoneId"), (Serializable) Class.forName("java.time.ZoneId").getMethod("of", String.class).invoke(null, "UTC"));
            values.put(Class.forName("java.time.ZoneOffset"), (Serializable) Class.forName("java.time.ZoneOffset").getField("UTC").get(null));
        } catch (Exception ex) {
            // We are on Java 7
        }
        VALUES = values;
    }

    @Test
    public void testFormatAndParse() throws Exception {
        ParserContext context = new ParserContext() {
            @Override
            public Object getAttribute(String name) {
                return null;
            }
        };
        for (Map.Entry<Class<? extends Serializable>, SerializableFormat<? extends Serializable>> entry : FormatUtils.getAvailableFormatters().entrySet()) {
            Serializable originalValue = VALUES.get(entry.getKey());
            String text = ((SerializableFormat<Serializable>) entry.getValue()).format(originalValue, context);
            Serializable value = entry.getValue().parse(text, context);
            Assert.assertEquals(originalValue, value);
        }
    }
}
