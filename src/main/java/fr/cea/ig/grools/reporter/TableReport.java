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

package fr.cea.ig.grools.reporter;


import ch.qos.logback.classic.Logger;
import fr.cea.ig.grools.reasoner.Reasoner;
import fr.cea.ig.grools.common.ResourceExporter;
import fr.cea.ig.grools.common.WrapFile;
import fr.cea.ig.grools.fact.PriorKnowledge;
import fr.cea.ig.grools.logic.TruthValueSet;
import lombok.NonNull;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
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
public final class TableReport extends WrapFile {
    private static final Logger LOG = ( Logger ) LoggerFactory.getLogger( TableReport.class );
    
    private void init( @NonNull final String resourcesDir ) throws Exception {
        final String outputDir = file.getParent( );
        writeln( "<!DOCTYPE html>" );
        writeln( "<html>" );
        writeln( "    <head>" );
        writeln( "        <title>Reporting</title>" );
        writeln( "        <meta charset=\"utf-8\">" );
        writeln( "        <meta name=\"author\" content=\"LABGeM - genoscope.cns.fr\">" );
        writeln( "        <meta name=\"description\" content=\"GROOLS: save time, do more, Graphical viewer\">" );
        writeln( "        <link rel=\"stylesheet\" type=\"text/css\" href=\"" + resourcesDir + "/css/table.css\">" );
        writeln( "        <script type=\"text/javascript\" src=\"" + resourcesDir + "js/list.js\"></script>" );
        writeln( "        <script type=\"text/javascript\">" );
        writeln( "          window.onload=function() {" );
        writeln( "            var options = { valueNames: [ 'Prior-Knowledge', 'Description', 'Prediction', 'Expectation', 'Conclusion', 'Leaf Statistics' ] };" );
        writeln( "            var rowList = new List('results', options);" );
        writeln( "          };" );
        writeln( "        </script>" );
        writeln( "    </head>" );
        writeln( "    <body>" );
        writeln( "        <div id=\"results\">" );
        writeln( "            <div class=\"button\">" );
        writeln( "                <input class=\"search\" placeholder=\"Search\" />" );
        writeln( "                <a href=\"./results.csv\">Get results CSV file</a>" );
        writeln( "            </div>" );
        writeln( "            <table id=\"resultstable\">" );
        writeln( "                <colgroup>" );
        writeln( "                    <col width=\"15%\">" );
        writeln( "                    <col width=\"40%\">" );
        writeln( "                    <col width=\"10%\">" );
        writeln( "                    <col width=\"10%\">" );
        writeln( "                    <col width=\"10%\">" );
        writeln( "                    <col width=\"15%\">" );
        writeln( "                </colgroup>" );
        writeln( "                <thead>" );
        writeln( "                <tr>" );
        writeln( "                    <th><span class=\"sort\" data-sort=\"Concept\">Prior-Knowledge</span></th>" );
        writeln( "                    <th><span class=\"sort\" data-sort=\"Description\">Description</span></th>" );
        writeln( "                    <th><span class=\"sort\" data-sort=\"Expectation\">Expectation</span></th>" );
        writeln( "                    <th><span class=\"sort\" data-sort=\"Prediction\">Prediction</span></th>" );
        writeln( "                    <th><span class=\"sort\" data-sort=\"Conclusion\">Conclusion</span></th>" );
        writeln( "                    <th><span class=\"sort\" data-sort=\"Statistics\">Leaf Statistics</span></th>" );
        writeln( "                </tr>" );
        writeln( "                </thead>" );
        writeln( "                <tbody  class=\"list\">" );
        
    }
    
    public void addRow( @NonNull final PriorKnowledge priorKnowledge ) throws IOException {
        addRow( priorKnowledge, null,priorKnowledge.getName( ), priorKnowledge.getDescription( ) );
    }
    
    public void addRow( @NonNull final PriorKnowledge priorKnowledge, final Map<String,Float> stats, @NonNull final String concept, @NonNull final String description ) throws IOException {
        final StringBuilder sb = new StringBuilder( "" );
        final String expectation = TruthValueSet.toLiteral( Reasoner.expectationToTruthValueSet( priorKnowledge.getExpectation( ) ) ) + " - " + priorKnowledge.getExpectation( );
        final String prediction  = TruthValueSet.toLiteral( Reasoner.predictionToTruthValueSet( priorKnowledge.getPrediction( ) ) ) + " - " + priorKnowledge.getPrediction( );
        writeln( "                <tr>" );
        if( stats != null) {
            stats.entrySet( )
                 .stream( )
                 .filter( entry -> !entry.getKey( ).equals( "nb concepts" ) )
                 .forEach( entry -> sb.append( ( entry.getKey( ).equals( "nb leaf concepts" ) ) ? "Total" + ":" + entry.getValue( ).intValue( ) + "<br>"
                                                       : entry.getKey( ).toString( ) + ": " + entry.getValue( ).intValue( ) + "<br>" ) );
            writeln( "                    <td class=\"Prior-Knowledge\">"   + concept + "<br>"+ stats.get( "nb concepts" ).intValue( ) + " prior-knowledges" + "</td>" );
        }
        else
            writeln( "                    <td class=\"Prior-Knowledge\">"   + concept  + "</td>" );
        writeln( "                    <td class=\"Description\">"       + description                       + "</td>" );
        writeln( "                    <td class=\"Expectation\">"       + expectation                       + "</td>" );
        writeln( "                    <td class=\"Prediction\">"        + prediction                        + "</td>" );
        writeln( "                    <td class=\"Conclusion\">"        + priorKnowledge.getConclusion( )   + "</td>" );
        writeln( "                    <td class=\"Leaf Statistics\">"   + sb.toString()                     + "</td>" );
        writeln( "                </tr>" );
    }
    
    public TableReport( @NonNull final String outputDir, @NonNull final String fileName, @NonNull final String jsDir ) throws Exception {
        super( Paths.get( outputDir, fileName ).toFile( ) );
        init( jsDir );
    }
    
    public TableReport( final File file, @NonNull final String resourcesDir ) throws Exception {
        super( file );
        init( resourcesDir );
    }
    
    public void close( ) throws IOException {
        if( !isClosed( ) ) {
            writeln( "                </tbody>" );
            writeln( "            </table>" );
            writeln( "        <div>" );
            writeln( "    </body>" );
            writeln( "</html>" );
            super.close( );
        }
    }
    
    public void finalize( ) throws Throwable {
        close( );
        super.finalize( );
    }
    
}
