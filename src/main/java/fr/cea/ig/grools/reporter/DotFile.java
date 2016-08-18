package fr.cea.ig.grools.reporter;
/*
 * Copyright LABGeM 19/02/15
 *
 * author: Jonathan MERCIER
 *
 * This software is a computer program whose purpose is to annotate a complete genome.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */


import fr.cea.ig.grools.common.WrapFile;

import java.io.*;

/**
 *
 */
/*
 * @startuml
 * class DotFile extends WrapFile{
 * -file: File
 * -dos: DataOutputStream
 * + DotFile(final String filepath)
 * + DotFile( final File file)
 * + close() : void
 * + addln( final String str ): void
 * }
 * @enduml
 */
public final class DotFile extends WrapFile {

    private final String graphName;

    private void init( ) throws IOException {
        writeln( "digraph " + graphName + " {" );
        writeln( "  orientation=landscape;" );
        writeln( "  rankdir=LR;" );
        writeln( "  orientation=-90;" );
        writeln( "  edge[ color=grey, penwidth=0.75, fontcolor=darkred, fontsize=10];" );
        writeln( "  rankdir=BT" );
        writeln( "  node[shape=\"box\" style=\"filled, rounded\" fontcolor=blue ]" );
    }

    public DotFile( final String gName, final String filepath ) throws IOException {
        super( new File( filepath ) );
        graphName = gName;
        init( );
    }

    public DotFile( final String gName, final File file ) throws IOException {
        super( file );
        graphName = gName;
        init( );
    }

    public void close( ) throws IOException {
        if( !isClosed( ) )
            writeln( "}" );
        super.close( );
    }

    public void addNode( final String uniqueNodeName, final String color ) {
        addNode( uniqueNodeName, color, "" );
    }

    public void addNode( final String uniqueNodeName, final String color, final String shape ) {
        final String label = uniqueNodeName.replace( '_', ' ' );
        addNode( uniqueNodeName, label, color, shape );
    }

    public void addNode( final String uniqueNodeName, final String label, final String color, final String shape ) {
        try {
            if( shape.isEmpty( ) )
                writeln( String.format( "  %s [ id=\"%s\" label=\"%s\" fillcolor=\"%s\" ];", uniqueNodeName, uniqueNodeName, label, color ) );
            else
                writeln( String.format( "  %s [ id=\"%s\" label=\"%s\" shape=\"%s\" fillcolor=\"%s\" ];", uniqueNodeName, uniqueNodeName, label, shape, color ) );
        }
        catch( IOException e ) {
            e.printStackTrace( );
        }
    }

    public void linkNode( final String parent, final String child, final String label ) {
        try {
            writeln( "  " + parent.replace( ' ', '_' ) + " -> " + child + " [ label=\"" + label + "\"];" );
        }
        catch( IOException e ) {
            e.printStackTrace( );
        }
    }

}
