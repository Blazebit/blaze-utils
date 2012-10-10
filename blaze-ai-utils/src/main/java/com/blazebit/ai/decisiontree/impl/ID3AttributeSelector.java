package com.blazebit.ai.decisiontree.impl;

import com.blazebit.ai.decisiontree.Attribute;
import com.blazebit.ai.decisiontree.AttributeSelector;
import com.blazebit.ai.decisiontree.AttributeValue;
import com.blazebit.ai.decisiontree.DiscreteAttribute;
import com.blazebit.ai.decisiontree.Example;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Christian Beikov
 */
public class ID3AttributeSelector implements AttributeSelector<Boolean> {

    @Override
    public Attribute select(final Set<Example<Boolean>> examples, final Set<Attribute> availableAttributes, final Set<Attribute> usedAttributes) {
        Attribute attribute = null;
        float attributeRem = Float.MAX_VALUE;
        int attributeValueCount = Integer.MAX_VALUE;
        float positives = 0;
        float negatives = 0;

        final Map<Attribute, Map<AttributeValue, Pair>> attributeUsage = new HashMap<Attribute, Map<AttributeValue, Pair>>();
        
        /* Make array for performance */
        final Example<Boolean>[] exampleArray = examples.toArray(new Example[0]);
        final int examplesSize = exampleArray.length;
        
        for (final Attribute attr : availableAttributes) {
            if (usedAttributes.contains(attr)) {
                continue;
            }

            final Map<AttributeValue, Pair> valueUsage = new HashMap<AttributeValue, Pair>();
            attributeUsage.put(attr, valueUsage);

            for (int i = 0; i < examplesSize; i++) {
                final AttributeValue value = exampleArray[i].getValues().get(attr);
                Pair valueUsageExamples = valueUsage.get(value);

                if (valueUsageExamples == null) {
                    valueUsageExamples = new Pair();
                    valueUsage.put(value, valueUsageExamples);
                }

                if (exampleArray[i].getResult()) {
                    ++valueUsageExamples.positive;
                    ++positives;
                } else {
                    ++valueUsageExamples.negative;
                    ++negatives;
                }
            }
        }

        if (positives > 0 && negatives > 0) {
            for (final Map.Entry<Attribute, Map<AttributeValue, Pair>> entry : attributeUsage.entrySet()) {
                final Attribute attr = entry.getKey();
                final float rem = Pair.rem(entry.getValue().values(), positives, negatives);

                if (rem < attributeRem) {
                    attribute = attr;
                    attributeRem = rem;

                    if (attr instanceof DiscreteAttribute) {
                        attributeValueCount = ((DiscreteAttribute) attr).getValues().size();
                    }
                } else if (attr instanceof DiscreteAttribute && (rem == attributeRem) && ((DiscreteAttribute) attr).getValues().size() < attributeValueCount) {
                    attribute = attr;
                    attributeRem = rem;
                    attributeValueCount = ((DiscreteAttribute) attr).getValues().size();
                }
            }
        }

        return attribute;
    }

    private static class Pair {

        static final float log2 = (float) Math.log(2);
        float positive = 0;
        float negative = 0;

        double entropy() {
            final float localPositive = positive;
            final float localNegative = negative;
            final float localLog2 = log2;

            if (localPositive == 0 || localNegative == 0) {
                return 0;
            }

            final float p = localPositive / (localPositive + localNegative);
            return -p * (Math.log(p) / localLog2) - (1 - p) * (Math.log(1 - p) / localLog2);
        }

        static float rem(final Collection<Pair> pairs, final float positives, final float negatives) {
            float rem = 0;

            for (final Pair p : pairs) {
                rem += ((p.positive + p.negative) / (positives + negatives)) * p.entropy();
            }

            return rem;
        }
    }
}
