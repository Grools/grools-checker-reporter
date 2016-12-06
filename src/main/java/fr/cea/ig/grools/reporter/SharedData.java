package fr.cea.ig.grools.reporter;


import fr.cea.ig.grools.reasoner.Reasoner;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class SharedData {
    private static SharedData ourInstance = new SharedData( );

    public static SharedData getInstance( ) {
        return ourInstance;
    }

    @NonNull @Getter @Setter
    private Reasoner reasoner;

    private SharedData( ) {
    }
}
