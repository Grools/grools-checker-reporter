package fr.cea.ig.grools.svg;
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

import ch.qos.logback.classic.Logger;
import fr.cea.ig.grools.common.Command;
import fr.cea.ig.grools.common.ResourceExporter;
import fr.cea.ig.grools.common.WrapFile;
import fr.cea.ig.grools.fact.Concept;
import fr.cea.ig.grools.fact.Observation;
import fr.cea.ig.grools.fact.PriorKnowledge;
import fr.cea.ig.grools.fact.Relation;
import fr.cea.ig.grools.logic.TruthValue;
import fr.cea.ig.grools.logic.TruthValuePowerSet;
import lombok.NonNull;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
/*
 * @startuml
 * class GraphWriter{
 *  - outputDir :   String
 *  - htmlFile  :   HtmlFile
 * }
 * @enduml
 */
public final class GraphWriter {
    private static final Logger LOG = (Logger) LoggerFactory.getLogger(GraphWriter.class);
    private final String    outputDir;
    private final WrapFile  htmlIndex;
    protected boolean isClosed;

    private static String colorList( final PriorKnowledge pk){
        return toColorPred( pk.getPrediction() ) + ";0.5:" + toColorExp( pk.getExpectation() );
    }

    private static String toColorPred( final TruthValuePowerSet tvSet ){
        String color;
        switch ( tvSet ){
            case T: color = "Lime"; break;
            case TF:
            case TFB:
            case NTF:
            case NTFB:
            case NF:
            case FB:
            case NFB:
            case F: color = "Coral"; break;
            case NB:
            case NTB:
            case TB:
            case B: color = "Plum"; break;
            case n:
            case NT:
            case N:
            default: color = "white";
        }
        return color;
    }

    private static String toColorExp( final TruthValuePowerSet tvSet ){
        String color;
        switch ( tvSet ){
            case NT:
            case T: color = "Lime"; break;
            case TF:
            case TFB:
            case NTF:
            case NTFB:
            case NF:
            case FB:
            case NFB:
            case F: color = "Coral"; break;
            case NB:
            case NTB:
            case TB:
            case B: color = "Plum"; break;
            case n:
            case N:
            default: color = "white";
        }
        return color;
    }

    private static String priorKnowledgeToHTML( @NonNull final PriorKnowledge pk){
        return String.format( "%s<br>Description: %s<br>is dispensable: %s<br>Prediction: %s<br>Expectation: %s<br>Conclusion: %s", pk.getName(), pk.getDescription().replaceAll("\'","&quote;"), pk.getIsDispensable()? "Yes":"No", pk.getPrediction(), pk.getExpectation(), pk.getConclusion() );
    }
    private static String observationToHTML( @NonNull final Observation observation){
        return String.format( "%s<br>Description: %s<br>type: %s<br>Truth value: %s", observation.getName(), observation.getDescription().replaceAll("\'","&quote;"), observation.getType(), observation.getTruthValue() );
    }

    private static boolean addNode( final Concept concept, final String id, final DotFile dotFile){
        boolean status = false;
        if( concept instanceof PriorKnowledge ) {
            final PriorKnowledge pk = (PriorKnowledge ) concept;
            dotFile.addNode( id, colorList( pk ), "box" );
            status = true;
        }
        else if( concept instanceof Observation ) {
            final Observation o = (Observation ) concept;
            dotFile.addNode( id, o.getName(), (o.getTruthValue() == TruthValue.t)? "Lime":"Coral" , "oval" );
            status = true;
        }
        return status;
    }

    private static String underscoretify(@NonNull final String str){
        return str.replace(" ", "_").replaceAll("[\\s.\\-,.:/!$]", "_");
    }

    private static void writeJSInfo( @NonNull final JsFile jsFile, @NonNull final Concept concept, @NonNull final String graphName) throws IOException {
            String          color;
            if( concept instanceof PriorKnowledge ) {
                final PriorKnowledge priorKnowledge = (PriorKnowledge) concept;
                switch (priorKnowledge.getConclusion()) {
                    case CONFIRMED_ABSENCE:
                    case CONFIRMED_PRESENCE:
                        color = "Chartreuse";
                        break;
                    case UNCONFIRMED_PRESENCE:
                    case UNCONFIRMED_ABSENCE:
                        color = "White";
                        break;
                    default:
                        color = "LightPink";
                        break;
                }
                final String name = underscoretify( concept.getName() );
                jsFile.writeln(String.format("    const svg_%s = svgdoc_%s.getElementById('%s');", name, graphName, name ));
                jsFile.writeln(String.format("    tooltips_event(tooltipsId, svg_%s, createInformativeNode('%s', '%s') );", name, priorKnowledgeToHTML(priorKnowledge), color));
            }
            else if( concept instanceof Observation ){
                final Observation observation = (Observation)concept;
                color = "White";
                final String name = underscoretify( concept.getName() );
                jsFile.writeln(String.format("    const svg_%s = svgdoc_%s.getElementById('%s');", name, graphName, name ));
                jsFile.writeln(String.format("    tooltips_event(tooltipsId, svg_%s, createInformativeNode('%s', '%s') );", name, observationToHTML(observation), color));
            }
    }

    private String writeDotFile(final String graphName, final Set<Relation> relations ) throws Exception {
        final String    dotFilename = Paths.get(outputDir, graphName, graphName + ".dot").toString();
        final DotFile   dotFile     = new DotFile(graphName, dotFilename);

        for( final Relation relation : relations){
            final Concept source    = relation.getSource();
            final Concept target    = relation.getTarget();
            final String  sourceId  = underscoretify( source.getName() );
            final String  targetId  = underscoretify( target.getName() );
            if( !addNode( source, sourceId, dotFile ) ){
                LOG.warn( "Unexpected type: " + source.getClass() );
                continue;
            }
            if( !addNode( target, targetId, dotFile ) ){
                LOG.warn( "Unexpected type: " + source.getClass() );
                continue;
            }
            dotFile.linkNode( sourceId, targetId, relation.getType().toString() );
        }

        dotFile.close();
        dotToSvg(graphName, dotFile);
        return dotFilename;
    }

    private void dotToSvg( final String graphName, final DotFile dotFile ) throws Exception {
        final String outFile = Paths.get(outputDir, graphName, graphName + ".svg").toString();
        Command.run("dot", Arrays.asList("-Tsvg", "-o" + outFile, dotFile.getAbsolutePath()));
    }

    private String writeJsFile( final String graphName, final Set<Relation> relations ) throws IOException {
        final String    jsFilename  = Paths.get(outputDir, graphName, "js", graphName + ".js").toString();
        final JsFile    jsFile      = new JsFile(jsFilename);
        jsFile.writeln(String.format("    const object_svg_%s   = document.getElementById('%s');", graphName, graphName));
        jsFile.writeln(String.format("    const svgdoc_%s       = object_svg_%s.contentDocument;", graphName, graphName));
        jsFile.writeln(              "    const tooltips        = document.createElement('div');");
        jsFile.writeln(              "    tooltips.id           = 'tooltips-content';");
        jsFile.writeln(              "    tooltips.className    = 'grools';");
        jsFile.writeln(              "    document.body.appendChild(tooltips);");
        jsFile.writeln(              "    const tooltipsId     = document.getElementById('tooltips-content');");
        Set<Concept> concepts = new HashSet<>(relations.size()*2);
        for( final Relation relation : relations){
            concepts.add(relation.getSource());
            concepts.add(relation.getTarget());
        }
        for( final Concept concept : concepts)
            writeJSInfo( jsFile, concept, graphName );
        jsFile.close();
        return jsFilename;
    }

    public GraphWriter(final String outDir ) throws Exception{
        isClosed    = false;
        outputDir   = outDir;
        htmlIndex   = new WrapFile( Paths.get( outputDir, "index.html").toFile() );
        htmlIndex.writeln("<!DOCTYPE html>");
        htmlIndex.writeln("<html>");
        htmlIndex.writeln("    <head>");
        htmlIndex.writeln("        <title>Reporting</title>");
        htmlIndex.writeln("        <meta charset=\"utf-8\">");
        htmlIndex.writeln("        <style type='text/css'>");
        htmlIndex.writeln("            table, th, td {");
        htmlIndex.writeln("                border: 1px solid black;");
        htmlIndex.writeln("            }");
        htmlIndex.writeln("            #results                                     { width:100%;}");
        htmlIndex.writeln("            #results td                                  { padding: 5px 5px 5px 5px;}");
        htmlIndex.writeln("            #results tr > td:first-child                 { text-align:left;}");
        htmlIndex.writeln("            #results tr > td:first-child + td            { text-align:justify;}");
        htmlIndex.writeln("            #results tr > td:first-child + td + td       { text-align:center;}");
        htmlIndex.writeln("            #results tr > td:first-child + td + td + td  { text-align:center;}");
        htmlIndex.writeln("        </style>");
        htmlIndex.writeln("    </head>");
        htmlIndex.writeln( "    </head>" );
        htmlIndex.writeln( "    <body>" );
        htmlIndex.writeln( "        <table id=\"results\">" );
        htmlIndex.writeln( "            <col width=\"20%\">" );
        htmlIndex.writeln( "            <col width=\"60%\">" );
        htmlIndex.writeln( "            <col width=\"10%\">" );
        htmlIndex.writeln( "            <col width=\"10%\">" );
        htmlIndex.writeln( "            <tr>" );
        htmlIndex.writeln( "                <th>Concept</th>" );
        htmlIndex.writeln( "                <th>Description</th>" );
        htmlIndex.writeln( "                <th>Prediction</th>" );
        htmlIndex.writeln( "                <th>Expectation</th>" );
        htmlIndex.writeln( "            </tr>" );
        String jsPath3 = ResourceExporter.export("/js/svg_common.js", outputDir);
        LOG.debug("File copied " + jsPath3 );
    }

    public void addGraph( final PriorKnowledge priorKnowledge, Set<Relation> relations) throws Exception {
        final String graphName   = priorKnowledge.getName().replace( "-", "_" );

        final File      outDir      = Paths.get( outputDir, graphName).toFile();
        outDir.mkdirs();
        final HtmlFile  htmlFile    = new HtmlFile(Paths.get(outputDir,graphName,"result.html").toString());
        final String    dotFilename = writeDotFile( graphName, relations );
        final String    jsFilename  = writeJsFile( graphName, relations );

        htmlFile.addGraph(graphName, graphName+".svg");
        htmlFile.close();
        final int colonIndex = priorKnowledge.getDescription().indexOf(':');
        final String description = (colonIndex >= 0) ? priorKnowledge.getDescription().substring(0, colonIndex):"";

        final String url = "<a href=\""+Paths.get(graphName,"result.html").toString()+"\">"+graphName+"</a>";
        htmlIndex.writeln( "            <tr>" );
        htmlIndex.writeln( "                <td>" + url                             + "</td>" );
        htmlIndex.writeln( "                <td>" + description                     + "</td>" );
        htmlIndex.writeln( "                <td>" + priorKnowledge.getPrediction()  + "</td>" );
        htmlIndex.writeln( "                <td>" + priorKnowledge.getExpectation() + "</td>" );
        htmlIndex.writeln( "            </tr>" );

        LOG.debug("File copied " + jsFilename );
        LOG.debug("File copied " + dotFilename );
    }

    public void finalize() throws Throwable {
        if( ! isClosed )
            close();
        super.finalize();
    }

    public void close() throws IOException{
        isClosed = true;
        htmlIndex.writeln( "        </table>" );
        htmlIndex.writeln( "    </body>" );
        htmlIndex.writeln("</html>");
        htmlIndex.close();
    }

}
