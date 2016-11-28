package fr.cea.ig.grools.reporter;

import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class DotAttribute {
    
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
    private final List<String> style;
    
    // box
    @Getter
    private final String shape;

    @java.beans.ConstructorProperties( { "id", "label", "color", "fillcolor", "style", "shape" } )
    DotAttribute( String id, String label, String color, String fillcolor, List< String > style, String shape ) {
        this.id = id;
        this.label = label;
        this.color = color;
        this.fillcolor = fillcolor;
        this.style = style;
        this.shape = shape;
    }

    public static DotAttributeBuilder builder( ) {
        return new DotAttributeBuilder( );
    }

    @Override
    public String toString(){
        final StringBuilder sb = new StringBuilder( " " );
        if( id != null )
            sb.append( " " + id + " [ id=\"" + id + "\"" );
        else
            sb.append( " [ " );
        if( label != null )
            sb.append( " label=\""+ label +"\"" );
        if( fillcolor != null )
            sb.append( " fillcolor=\""+ fillcolor +"\"" );
        if( color != null )
            sb.append( " color=\""+ color +"\"" );
        if( shape != null )
            sb.append( " shape=\""+ shape +"\"" );
        if( style != null )
            sb.append( " style=\""+ String.join( ", ", style ) +"\"" );
        sb.append( " ];" );
        return sb.toString();
    }

    public static class DotAttributeBuilder {
        private String id;
        private String label;
        private String color;
        private String fillcolor;
        private List< String > style;
        private String shape;

        DotAttributeBuilder( ) {
        }

        public DotAttributeBuilder id( final String id ) {
            this.id = id;
            return this;
        }

        public DotAttributeBuilder label( final String label ) {
            this.label = label;
            return this;
        }

        public DotAttributeBuilder color( final String color ) {
            this.color = color;
            return this;
        }

        public DotAttributeBuilder fillcolor( final String fillcolor ) {
            this.fillcolor = fillcolor;
            return this;
        }

        public DotAttributeBuilder style( final List< String > style ) {
            this.style = style;
            return this;
        }

        public DotAttributeBuilder style( final String style ) {
            if( this.style == null )
                this.style = new ArrayList<>(  );
            this.style.add( style );
            return this;
        }

        public DotAttributeBuilder shape( final String shape ) {
            this.shape = shape;
            return this;
        }

        public DotAttribute build( ) {
            return new DotAttribute( id, label, color, fillcolor, style, shape );
        }

        public String toString( ) {
            return "fr.cea.ig.grools.reporter.DotAttribute.DotAttributeBuilder(id=" + this.id + ", label=" + this.label + ", color=" + this.color + ", fillcolor=" + this.fillcolor + ", style=" + this.style + ", shape=" + this.shape + ")";
        }
    }
}
