/*
 *
 * Copyright LABGeM 2015
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
 *
 */

package fr.cea.ig.grools.svg;


import ch.qos.logback.classic.Logger;
import fr.cea.ig.grools.common.ResourceExporter;
import fr.cea.ig.grools.common.WrapFile;
import fr.cea.ig.grools.fact.PriorKnowledge;
import lombok.NonNull;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 *
 */
/*
 * @startuml
 * class GraphicReport{
 * }
 * @enduml
 */
public final class TableReport extends WrapFile {
    private static final Logger LOG = (Logger) LoggerFactory.getLogger(TableReport.class);

    private void init() throws Exception {
        final String outputDir = file.getParent();
        writeln("<!DOCTYPE html>");
        writeln("<html>");
        writeln("    <head>");
        writeln("        <title>Reporting</title>");
        writeln("        <meta charset=\"utf-8\">");
        writeln("        <style type='text/css'>");
        writeln("            td {");
        writeln("                border: 1px solid black;");
        writeln("            }");
        writeln("            #results                                         { width:100%;}");
        writeln("            #results td                                      { padding: 5px 5px 5px 5px;}");
        writeln("            #results tr > td:first-child                     { text-align:left;}");
        writeln("            #results tr > td:first-child + td                { text-align:justify;}");
        writeln("            #results tr > td:first-child + td + td           { text-align:center;}");
        writeln("            #results tr > td:first-child + td + td + td      { text-align:center;}");
        writeln("            #results tr > td:first-child + td + td + td +td  { text-align:center;}");
        writeln("            .list {");
        writeln("              font-family:sans-serif;");
        writeln("            }");
        writeln("            td {");
        writeln("              padding:10px; ");
        writeln("              border:solid 1px #eee;");
        writeln("            }");
        writeln("            input {");
        writeln("              border:solid 1px #ccc;");
        writeln("              border-radius: 5px;");
        writeln("              padding:7px 14px;");
        writeln("              margin-bottom:10px");
        writeln("            }");
        writeln("            input:focus {");
        writeln("              outline:none;");
        writeln("              border-color:#aaa;");
        writeln("            }");
        writeln("            .sort {");
        writeln("              padding:8px 30px;");
        writeln("              border-radius: 6px;");
        writeln("              border:none;");
        writeln("              display:inline-block;");
        writeln("              color:#fff;");
        writeln("              text-decoration: none;");
        writeln("              background-color: #28a8e0;");
        writeln("              height:30px;");
        writeln("            }");
        writeln("            .sort:hover {");
        writeln("              text-decoration: none;");
        writeln("              background-color:#1b8aba;");
        writeln("            }");
        writeln("            .sort:focus {");
        writeln("              outline:none;");
        writeln("            }");
        writeln("            .sort:after {");
        writeln("              display:inline-block;");
        writeln("              width: 0;");
        writeln("              height: 0;");
        writeln("              border-left: 5px solid transparent;");
        writeln("              border-right: 5px solid transparent;");
        writeln("              border-bottom: 5px solid transparent;");
        writeln("              content:\"\";");
        writeln("              position: relative;");
        writeln("              top:-10px;");
        writeln("              right:-5px;");
        writeln("            }");
        writeln("            .sort.asc:after {");
        writeln("              width: 0;");
        writeln("              height: 0;");
        writeln("              border-left: 5px solid transparent;");
        writeln("              border-right: 5px solid transparent;");
        writeln("              border-top: 5px solid #fff;");
        writeln("              content:\"\";");
        writeln("              position: relative;");
        writeln("              top:4px;");
        writeln("              right:-5px;");
        writeln("            }");
        writeln("            .sort.desc:after {");
        writeln("              width: 0;");
        writeln("              height: 0;");
        writeln("              border-left: 5px solid transparent;");
        writeln("              border-right: 5px solid transparent;");
        writeln("              border-bottom: 5px solid #fff;");
        writeln("              content:\"\";");
        writeln("              position: relative;");
        writeln("              top:-4px;");
        writeln("              right:-5px;");
        writeln("            }");
        writeln("        </style>");
        writeln("        <script type=\"text/javascript\" src=\"js/list.js\"></script>");
        writeln("        <script type=\"text/javascript\">");
        writeln("          window.onload=function() {");
        writeln("            var options = { valueNames: [ 'Concept', 'Description', 'Prediction', 'Expectation', 'Conclusion' ] };");
        writeln("            var rowList = new List('results', options);");
        writeln("          };");
        writeln("        </script>");
        writeln("    </head>");
        writeln( "    <body>" );
        writeln( "        <div id=\"results\">" );
        writeln( "            <span><a href=\"./results.csv\">Get results CSV file</a></span>" );
        writeln( "            <table id=\"resultstable\">" );
        writeln( "                <colgroup>" );
        writeln( "                    <col width=\"20%\">" );
        writeln( "                    <col width=\"50%\">" );
        writeln( "                    <col width=\"10%\">" );
        writeln( "                    <col width=\"10%\">" );
        writeln( "                    <col width=\"10%\">" );
        writeln( "                </colgroup>" );
        writeln( "                <thead>" );
        writeln( "                <tr>" );
        writeln( "                    <th><span class=\"sort\" data-sort=\"Concept\">Sort by Concept</span></th>" );
        writeln( "                    <th><span class=\"sort\" data-sort=\"Description\">Sort by Description</span></th>" );
        writeln( "                    <th><span class=\"sort\" data-sort=\"Prediction\">Sort by Prediction</span></th>" );
        writeln( "                    <th><span class=\"sort\" data-sort=\"Expectation\">Expectation</span></th>" );
        writeln( "                    <th><span class=\"sort\" data-sort=\"Conclusion\">Sort by Conclusion</span></th>" );
        writeln( "                </tr>" );
        writeln( "                </thead>" );
        writeln( "                <tbody  class=\"list\">" );
        String jsPath1 = ResourceExporter.export("/js/svg_common.js", outputDir);
        String jsPath2 = ResourceExporter.export("/js/list.js", outputDir);
        LOG.debug("File copied " + jsPath1 );
        LOG.debug("File copied " + jsPath2 );

    }

    public void addRow( @NonNull final PriorKnowledge priorKnowledge ) throws IOException {
        addRow( priorKnowledge, priorKnowledge.getName(), priorKnowledge.getDescription());
    }

    public void addRow( @NonNull final PriorKnowledge priorKnowledge, @NonNull final String concept, @NonNull final String description ) throws IOException {
        writeln("                <tr>" );
        writeln("                    <td class=\"Concept\">"     + concept                         + "</td>");
        writeln("                    <td class=\"Description\">" + description                     + "</td>");
        writeln("                    <td class=\"Prediction\">"  + priorKnowledge.getPrediction()  + "</td>");
        writeln("                    <td class=\"Expectation\">" + priorKnowledge.getExpectation() + "</td>");
        writeln("                    <td class=\"Conclusion\">"  + priorKnowledge.getConclusion()  + "</td>");
        writeln("                </tr>");
    }

    public TableReport(@NonNull final String outputDir, @NonNull final String fileName) throws Exception {
        super( Paths.get(outputDir,fileName).toFile() );
        init();
    }

    public TableReport(final File file) throws Exception {
        super(file);
        init();
    }

    public void close() throws IOException {
        if( ! isClosed() ) {
            isClosed = true;
            writeln("                </tbody>");
            writeln("            </table>");
            writeln("        <div>");
            writeln("    </body>");
            writeln("</html>");
            super.close();
        }
    }

    public void finalize() throws Throwable {
        if( ! isClosed )
            close();
        super.finalize();
    }

}
