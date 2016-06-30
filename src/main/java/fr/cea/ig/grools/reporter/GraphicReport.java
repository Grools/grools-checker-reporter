package fr.cea.ig.grools.reporter;
/*
 * Copyright LABGeM 20/02/15
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
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
/*
 * @startuml
 * class GraphicReport{
 * }
 * @enduml
 */
public final class GraphicReport extends WrapFile {
    private final Map<String, String> svgs= new HashMap<>() ;

    private void init() throws IOException{
        writeln("<!DOCTYPE html>");
        writeln("<html>");
        writeln("    <head>");
        writeln("        <meta charset=\"utf-8\">");
        writeln("        <style type='text/css'>");
        writeln("            .grools{");
        writeln("                position: fixed;");
        writeln("            }");
        writeln("            .grools::before {");
        writeln("                width: 100%;");
        writeln("                height: 100%;");
        writeln("                display: block;");
        writeln("                -webkit-border-radius: 2em;");
        writeln("                -moz-border-radius: 2em;");
        writeln("                border-radius: 2em;");
        writeln("                content: '';");
        writeln("                background-image: linear-gradient(to bottom, #1a82f7 0%, #3a4ed5 20%);");
        writeln("                position:absolute;");
        writeln("                z-index:-1;");
        writeln("           }");
        writeln("            .grools p{");
        writeln("                padding-left:1em;");
        writeln("                padding-bottom:1em;");
        writeln("            }");
        writeln("        </style>");
        writeln("        <script type='text/javascript' src='../js/svg_common.js'></script>");
    }

    public GraphicReport(final String filepath) throws IOException {
        super(new File(filepath));
        init();
    }

    public GraphicReport(final File file) throws IOException {
        super(file);
        init();
    }

    public void addGraph( final String graphId, final String svgFile) throws IOException {
        writeln("        <script type='text/javascript' src='js/" + graphId + ".js'></script>");
        svgs.put(graphId, svgFile);
    }

    public void close() throws IOException {
        writeln( "    </head>" );
        writeln( "    <body>" );
        for(final Map.Entry<String, String> entry: svgs.entrySet() ){
            writeln( "        <object id='" + entry.getKey() + "' data='" + entry.getValue() + "' type='image/svg+xml'>" );
            writeln( "        Your browser doesn't support SVG" );
            writeln( "        </object>" );
        }
        writeln( "    </body>" );
        writeln( "</html>");
        super.close();
    }

    public void finalize() throws Throwable {
        if( ! isClosed )
            close();
        super.finalize();
    }

}
