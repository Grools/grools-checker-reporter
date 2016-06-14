package fr.cea.ig.grools.svg;
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
 * class JsFile{
 * }
 * @enduml
 */
public final class HtmlFile extends WrapFile {
    private final Map<String, String> svgs= new HashMap<>() ;

    private void init() throws IOException{
        writeln("<!DOCTYPE html>");
        writeln("<html>");
        writeln("    <head>");
        writeln("        <meta charset=\"utf-8\">");
        writeln("        <style type='text/css'>");
        writeln("            .grools{");
        writeln("                position: fixed;");
        writeln("                z-index: 10;");
        writeln("            }");
        writeln("            .grools > div:before {");
        writeln("                content: \"\";");
        writeln("                width: 100%;");
        writeln("                height: 100%;");
        writeln("                display: block;");
        writeln("                position: absolute;");
        writeln("                -webkit-border-radius: 8px;");
        writeln("                -moz-border-radius: 8px;");
        writeln("                border-radius: 8px;");
        writeln("                background: -moz-linear-gradient(top,  rgba(255,255,255,1) 0%, rgba(255,255,255,0.5) 8%, rgba(255,255,255,0) 100%);");
        writeln("                background: -webkit-gradient(linear, left top, left bottombottom, color-stop(0%,rgba(255,255,255,1)), color-stop(8%,rgba(255,255,255,0.5)), color-stop(100%,rgba(255,255,255,0)));");
        writeln("                background: -webkit-linear-gradient(top,  rgba(255,255,255,1) 0%,rgba(255,255,255,0.5) 8%,rgba(255,255,255,0) 100%);");
        writeln("                background: -o-linear-gradient(top,  rgba(255,255,255,1) 0%,rgba(255,255,255,0.5) 8%,rgba(255,255,255,0) 100%);");
        writeln("                background: -ms-linear-gradient(top,  rgba(255,255,255,1) 0%,rgba(255,255,255,0.5) 8%,rgba(255,255,255,0) 100%);");
        writeln("                background: linear-gradient(to bottombottom,  rgba(255,255,255,1) 0%,rgba(255,255,255,0.5) 8%,rgba(255,255,255,0) 100%);");
        writeln("                filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#ffffff', endColorstr='#00ffffff',GradientType=0 );");
        writeln("           }");
        writeln("            .grools > div {");
        writeln("                background-color: #002966;");
        writeln("                -webkit-border-radius: 8px;");
        writeln("                -moz-border-radius: 8px;");
        writeln("                border-radius: 8px;");
        writeln("            }");
        writeln("            .grools > div > p{");
        writeln("                z-index: 11;");
        writeln("                padding:1em;");
        writeln("            }");
        writeln("            @keyframes blink {");
        writeln("                50% { border-color: #ff0000; }");
        writeln("            }");
        writeln("            .border-animated-blink{");
        writeln("                animation-name: blink ;");
        writeln("                animation-duration: .5s ;");
        writeln("                animation-timing-function: step-end ;");
        writeln("                animation-iteration-count: infinite ;");
        writeln("                animation-direction: alternate ;");
        writeln("            }");
        writeln("        </style>");
        writeln("        <script type='text/javascript' src='../js/svg_common.js'></script>");
    }

    public HtmlFile(final String filepath) throws IOException {
        super(new File(filepath));
        init();
    }

    public HtmlFile(final File file) throws IOException {
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

}
