package fr.cea.ig.grools.reporter;

import lombok.NonNull;

import java.util.EnumMap;
import java.util.Map;


public enum SensitivitySpecificity {

    TRUE_POSITIVE("TRUE POSITIVE"),
    TRUE_NEGATIVE("TRUE NEGATIVE"),
    FALSE_POSITIVE("FALSE POSITIVE"),
    FALSE_NEGATIVE("FALSE NEGATIVE"),
    NOT_AVAILABLE("NOT AVAILABLE");

    private String category;

    SensitivitySpecificity( @NonNull final String category ){
        this.category = category;
    }

    @Override
    public String toString(){
        return category;
    }

    @NonNull
    static public Double getTruePositiveRate( @NonNull final EnumMap<SensitivitySpecificity,Integer> values ){
        final Long dividend = values.getOrDefault(TRUE_POSITIVE,0 ).longValue();
        final Long divider  = ( values.getOrDefault(TRUE_POSITIVE,0 ).longValue() + values.getOrDefault(FALSE_NEGATIVE,0 ).longValue());
        return  division( dividend, divider );
    }
    
    @NonNull
    static public Double getFalsePositiveRate( @NonNull final EnumMap<SensitivitySpecificity,Integer> values ){
        final Long dividend = values.getOrDefault(FALSE_POSITIVE,0 ).longValue();
        final Long divider  = ( values.getOrDefault(FALSE_POSITIVE,0 ).longValue() + values.getOrDefault(TRUE_NEGATIVE,0 ).longValue() );
        return  division( dividend, divider );
    }
    
    @NonNull
    static public Double getTrueNegativeRate( @NonNull final EnumMap<SensitivitySpecificity,Integer> values ){
        final Long dividend = values.getOrDefault(TRUE_NEGATIVE,0 ).longValue();
        final Long divider  = ( values.getOrDefault(TRUE_NEGATIVE,0 ).longValue() + values.getOrDefault(FALSE_POSITIVE,0 ).longValue());
        return  division( dividend, divider );
    }
    
    @NonNull
    static public Double getPositivePredictiveValue( @NonNull final Map<SensitivitySpecificity,Integer> values ){
        final Long dividend = values.getOrDefault(TRUE_POSITIVE,0 ).longValue();
        final Long divider  = ( values.getOrDefault(TRUE_POSITIVE,0 ).longValue() + values.getOrDefault(FALSE_POSITIVE,0 ).longValue());
        return  division( dividend, divider );
    }
    
    @NonNull
    static public Double getNegativePredictiveValue( @NonNull final EnumMap<SensitivitySpecificity,Integer> values ){
        final Long dividend = values.getOrDefault(TRUE_NEGATIVE,0 ).longValue();
        final Long divider  = ( values.getOrDefault(TRUE_NEGATIVE,0 ).longValue() + values.getOrDefault(FALSE_NEGATIVE,0 ).longValue() );
        return  division( dividend, divider );
    }
    
    @NonNull
    static public Double getFalseDiscoveryRateValue( @NonNull final Map<SensitivitySpecificity,Integer> values ){
        final Long dividend = values.getOrDefault(FALSE_POSITIVE,0).longValue();
        final Long divider  = ( values.getOrDefault(FALSE_POSITIVE,0).longValue() + values.getOrDefault(TRUE_POSITIVE,0).longValue());
        return  division( dividend, divider );
    }
    
    @NonNull
    static public Double getAccuracyValue( @NonNull final EnumMap<SensitivitySpecificity,Integer> values ){
        final Long dividend = ( values.getOrDefault(TRUE_POSITIVE,0).longValue() + values.getOrDefault(TRUE_NEGATIVE,0).longValue());
        final Long divider  = ( values.getOrDefault(FALSE_POSITIVE,0).longValue() + values.getOrDefault( FALSE_NEGATIVE,0 ) + values.getOrDefault(TRUE_POSITIVE,0) + values.getOrDefault( TRUE_NEGATIVE, 0 ).longValue());
        return  division( dividend, divider );
    }

    @NonNull
    static public Double getF1score( @NonNull final EnumMap<SensitivitySpecificity,Integer> values ){
        final Long dividend = ( 2 * values.getOrDefault(TRUE_POSITIVE,0 ).longValue() );
        final Long divider  = ( values.getOrDefault(FALSE_POSITIVE,0 ) + values.getOrDefault( FALSE_NEGATIVE,0  ).longValue() + (2 * values.getOrDefault(TRUE_POSITIVE,0 ).longValue()));
        return  division( dividend, divider );
    }

    @NonNull
    static private Double division(@NonNull final Long dividend, @NonNull final Long divider){
        return (divider == 0)? Double.NaN : ((double)dividend/(double)divider);
    }
}
