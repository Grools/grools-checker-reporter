package fr.cea.ig.grools.reporter;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
public class DotNode {
    
    @NonNull
    @Getter
    private final String id;
    
    @Getter
    private final String label;
    
    // yellow, blue …
    @Getter
    private final String color;
    
    // yellow, blue …
    @Getter
    private final String fillcolor;
    
    // rounded, filled, dashed, dotted solid, bold …
    @Getter
    private final String style;
    
    // box
    @Getter
    private final String shape;
    
    @Override
    public String toString(){
        final StringBuilder sb = new StringBuilder( " " + id + " [ id=\"" + id + "\"" );
        if( label != null )
            sb.append( " label=\""+ label +"\"" );
        if( fillcolor != null )
            sb.append( " fillcolor=\""+ fillcolor +"\"" );
        if( shape != null )
            sb.append( " shape=\""+ shape +"\"" );
        if( shape != null )
            sb.append( " style=\""+ style +"\"" );
        sb.append( " ];" );
        return sb.toString();
    }
    
}
