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
import fr.cea.ig.grools.Reasoner;

import fr.cea.ig.grools.common.Command;
import fr.cea.ig.grools.common.ResourceExporter;
import fr.cea.ig.grools.fact.Concept;
import fr.cea.ig.grools.fact.Observation;
import fr.cea.ig.grools.fact.PriorKnowledge;
import fr.cea.ig.grools.fact.Relation;
import fr.cea.ig.grools.logic.Conclusion;
import fr.cea.ig.grools.logic.TruthValue;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

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
    private final HtmlFile  htmlFile;

    private static String toColor( final Conclusion conclusion){
        String          color;
        switch (conclusion){
            case ABSENT:
            case UNEXPLAINED:
            case UNCONFIRMED_PRESENCE:
            case UNCONFIRMED_ABSENCE:       color = "PaleTurquoise"; break;
            case CONFIRMED_ABSENCE:
            case CONFIRMED_PRESENCE:        color = "PaleGreen"; break;
            case MISSING:
            case UNEXPECTED_ABSENCE:
            case UNEXPECTED_PRESENCE:       color = "MistyRose"; break;
            case AMBIGUOUS:
            case AMBIGUOUS_PRESENCE:
            case AMBIGUOUS_ABSENCE:         color = "Fuchsia"; break;
            case UNCONFIRMED_CONTRADICTORY:
            case AMBIGUOUS_CONTRADICTORY:   color = "BlueViolet"; break;
            case CONTRADICTORY_PRESENCE:
            case CONTRADICTORY_ABSENCE:     color = "LavenderBlush"; break;
            default:                        color = "GhostWhite"; break;
        }
        return color;
    }

    private static boolean addNode( final Concept concept, final String id, final DotFile dotFile){
        boolean status = false;
        if( concept instanceof PriorKnowledge ) {
            final PriorKnowledge pk = (PriorKnowledge ) concept;
            dotFile.addNode( id, toColor( pk.getConclusion() ), "box" );
            status = true;
        }
        else if( concept instanceof Observation ) {
            final Observation o = (Observation ) concept;
            dotFile.addNode( id, (o.getTruthValue() == TruthValue.t)? "PaleTurquoise":"MistyRose" , "oval" );
            status = true;
        }
        return status;
    }

    private String writeDotFile(final String graphName, final Reasoner reasoner ) throws Exception {
        final String    dotFilename = Paths.get(outputDir, graphName + ".dot").toString();
        final DotFile   dotFile     = new DotFile(graphName, dotFilename);

        for( final Relation relation : reasoner.getRelations()){
            final Concept source    = relation.getSource();
            final Concept target    = relation.getTarget();
            final String  sourceId  = source.getName().replaceAll( "[\\s.\\-/]", "_" );
            final String  targetId  = target.getName().replaceAll( "[\\s.\\-/]", "_" );
            if( !addNode( source, sourceId, dotFile ) ){
                System.err.println( "Unexpected type: " + source.getClass() );
                continue;
            }
            if( !addNode( target, targetId, dotFile ) ){
                System.err.println( "Unexpected type: " + source.getClass() );
                continue;
            }
            dotFile.linkNode( sourceId, targetId, relation.getType().toString() );
        }

        dotFile.close();
        dotToSvg(graphName, dotFile);
        return dotFilename;
    }

    private void dotToSvg( final String graphName, final DotFile dotFile ) throws Exception {
        final String outFile = Paths.get(outputDir, graphName + ".svg").toString();
        Command.run("dot", Arrays.asList("-Tsvg", "-o" + outFile, dotFile.getAbsolutePath()));
    }

    private String writeJsFile( final String graphName, final Reasoner reasoner ) throws IOException {
        final String    jsFilename  = Paths.get(outputDir, "js", graphName + ".js").toString();
        final JsFile    jsFile      = new JsFile(jsFilename);
        String          color       = "";
        jsFile.writeln(String.format("    const object_svg_%s   = document.getElementById('%s');", graphName, graphName));
        jsFile.writeln(String.format("    const svgdoc_%s       = object_svg_%s.contentDocument;", graphName, graphName));
        jsFile.writeln(              "    const tooltips        = document.createElement('div');");
        jsFile.writeln(              "    tooltips.id           = 'tooltips-content';");
        jsFile.writeln(              "    tooltips.className    = 'grools';");
        jsFile.writeln(              "    document.body.appendChild(tooltips);");
        jsFile.writeln(              "    const tooltipsId    = document.getElementById('tooltips-content');");
        for( final PriorKnowledge knowledge : reasoner.getPriorKnowledges()){
            final String name = knowledge.getName().replace(" ", "_");
            jsFile.writeln(String.format("    const svg_%s = svgdoc_%s.getElementById('%s');", name, graphName, name ));
            switch (knowledge.getConclusion()){
                case CONFIRMED_ABSENCE:
                case CONFIRMED_PRESENCE:    color = "green"; break;
                case UNCONFIRMED_PRESENCE:
                case UNCONFIRMED_ABSENCE:   color = "Chartreuse"; break;
                default:                    color = "LightPink"; break;
            }
            jsFile.writeln(String.format("    tooltips_event(tooltipsId, svg_%s, createInformativeNode('%s', '%s') );", name, knowledge.getConclusion(), color) );
        }
        jsFile.close();
        return jsFilename;
    }

    public GraphWriter(final String outDir ) throws IOException{
        outputDir       = outDir;
        htmlFile        = new HtmlFile(Paths.get(outputDir,"result.html").toString());
    }

    public void addGraph( final String graphName, final Reasoner reasoner) throws Exception {
        final String    name = graphName.replace( "-", "_" );
        final String    dotFilename = writeDotFile( name, reasoner);
        final String    jsFilename  = writeJsFile( name, reasoner );
        LOG.info("File copied " + jsFilename );
        LOG.info("File copied " + dotFilename );

        htmlFile.addGraph(name, name+".svg");

        String jsPath3 = ResourceExporter.export("/js/svg_common.js", outputDir);
        LOG.info("File copied " + jsPath3 );
    }

    public void close() throws IOException{
        htmlFile.close();
    }

}
