package fr.cea.ig.grools.reporter;

import lombok.NonNull;

import java.util.EnumMap;
import java.util.Map;


public enum SensitivitySpecifity {
    TRUE_POSITIVE,
    TRUE_NEGATIVE,
    FALSE_POSITIVE,
    FALSE_NEGATIVE;
    
    @NonNull
    static public Long getTruePositiveRate( @NonNull final EnumMap<SensitivitySpecifity,Integer> values ){
        return  values.get(TRUE_POSITIVE).longValue() /
                ( values.get(TRUE_POSITIVE) + values.get(FALSE_NEGATIVE));
    }
    
    @NonNull
    static public Long getFalsePositiveRate( @NonNull final EnumMap<SensitivitySpecifity,Integer> values ){
        return  values.get(FALSE_POSITIVE).longValue() /
                ( values.get(FALSE_POSITIVE) + values.get(TRUE_NEGATIVE));
    }
    
    @NonNull
    static public Long getTrueNegativeRate( @NonNull final EnumMap<SensitivitySpecifity,Integer> values ){
        return  values.get(TRUE_NEGATIVE).longValue() /
                ( values.get(TRUE_NEGATIVE) + values.get(FALSE_POSITIVE));
    }
    
    @NonNull
    static public Long getPositivePredictiveValue( @NonNull final Map<SensitivitySpecifity,Integer> values ){
        return  values.get(TRUE_POSITIVE).longValue() /
                ( values.get(TRUE_POSITIVE) + values.get(FALSE_POSITIVE));
    }
    
    @NonNull
    static public Long getNegativePredictiveValue( @NonNull final EnumMap<SensitivitySpecifity,Integer> values ){
        return  values.get(TRUE_NEGATIVE).longValue() /
                ( values.get(TRUE_NEGATIVE) + values.get(FALSE_NEGATIVE));
    }
    
    @NonNull
    static public Long getFalseDiscoveryRateValue( @NonNull final Map<SensitivitySpecifity,Integer> values ){
        return  values.get(FALSE_POSITIVE).longValue() /
                ( values.get(FALSE_POSITIVE) + values.get(TRUE_POSITIVE));
    }
    
    @NonNull
    static public Long getAccuracyValue( @NonNull final EnumMap<SensitivitySpecifity,Integer> values ){
        return  ( values.get(TRUE_POSITIVE).longValue() + values.get(TRUE_NEGATIVE).longValue()) /
                ( values.get(FALSE_POSITIVE) + values.get( FALSE_NEGATIVE ) + values.get(TRUE_POSITIVE) + values.get( TRUE_NEGATIVE ));
    }
    
    
    @NonNull
    static public Long getF1score( @NonNull final EnumMap<SensitivitySpecifity,Integer> values ){
        return  ( 2 * values.get(TRUE_POSITIVE).longValue() ) /
                ( values.get(FALSE_POSITIVE) + values.get( FALSE_NEGATIVE ) + (2 * values.get(TRUE_POSITIVE)));
    }
}
