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

import ch.qos.logback.classic.Logger;
import fr.cea.ig.grools.common.Command;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(GraphWriter.class);
    private final String      outputDir;
    private final TableReport tableReport;
    private final CSVReport   csvReport;


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

    private static boolean addNode( @NonNull final Concept concept, @NonNull final String id, @NonNull final DotFile dotFile){
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
        return str.replaceAll("[\\s.+\\-,.:/!$()\\[\\]]", "_")
                  .replaceAll("(_)(\\1{2,})", "$1");
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
                jsFile.writeln(String.format("    tooltips_event( svg_%s, '%s', '%s' );", name, priorKnowledgeToHTML(priorKnowledge), color));
            }
            else if( concept instanceof Observation ){
                final Observation observation = (Observation)concept;
                color = "White";
                final String name = underscoretify( concept.getName() );
                jsFile.writeln(String.format("    const svg_%s = svgdoc_%s.getElementById('%s');", name, graphName, name ));
                jsFile.writeln(String.format("    tooltips_event( svg_%s, '%s', '%s' );", name, observationToHTML(observation), color));
            }
    }

    private void dotToSvg( @NonNull final String graphName, @NonNull final DotFile dotFile ) throws Exception {
        final String outFile = Paths.get(outputDir, graphName, graphName + ".svg").toString();
        Command.run("dot", Arrays.asList("-Tsvg", "-o" + outFile, dotFile.getAbsolutePath()));
    }

    private String writeDotFile( @NonNull final String graphName, @NonNull final Set<Relation> relations, final Set<Concept> concepts ) throws Exception {
        final String    dotFilename = Paths.get(outputDir, graphName, graphName + ".dot").toString();
        final DotFile   dotFile     = new DotFile(graphName, dotFilename);

        for( final Concept concept: concepts){
            final String  sourceId  = underscoretify( concept.getName() );
            if( !addNode( concept, sourceId, dotFile ) ){
                LOGGER.warn("Unexpected type: " + concept.getClass());
            }
        }

        for( final Relation relation : relations){
            final Concept source    = relation.getSource();
            final Concept target    = relation.getTarget();
            final String  sourceId  = underscoretify( source.getName() );
            final String  targetId  = underscoretify( target.getName() );
            dotFile.linkNode( sourceId, targetId, relation.getType().toString() );
        }

        dotFile.close();
        dotToSvg(graphName, dotFile);
        return dotFilename;
    }

    private String writeJsFile( final String graphName, final Set<Concept> concepts ) throws IOException {
        final String    jsFilename  = Paths.get(outputDir, graphName, "js", graphName + ".js").toString();
        final JsFile    jsFile      = new JsFile(jsFilename);
        jsFile.writeln(String.format("    const object_svg_%s   = document.getElementById('%s');", graphName, graphName));
        jsFile.writeln(String.format("    const svgdoc_%s       = object_svg_%s.contentDocument;", graphName, graphName));
        for( final Concept concept : concepts)
            writeJSInfo( jsFile, concept, graphName );
        jsFile.close();
        return jsFilename;
    }

    private String writeTableReport(@NonNull final Path path, @NonNull final Set<Concept> concepts) throws Exception {
        final TableReport table = new TableReport(path.toFile());
        for( final Concept concept: concepts){
            if( concept instanceof PriorKnowledge)
                table.addRow((PriorKnowledge) concept);
        }
        table.close();
        return table.getFileName();
    }

    private String writeCSVReport(@NonNull final Path path, @NonNull final Set<Concept> concepts) throws Exception {
        CSVReport csv = new CSVReport(path.toFile());
        for( final Concept concept : concepts )
            csv.addRow( concept );
        csv.close();
        return csv.getFileName();
    }

    private String writeJSONFile(@NonNull final Path jsonPathFile, @NonNull final Set<Relation> relations, final Set<Concept> concepts) throws Exception {
        final WrapFile json     = new WrapFile(jsonPathFile.toFile());
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        json.writeln("{");
        json.writeln("  \"edges\": [");
        final String edges = relations.stream()
                                      .map( rel -> String.format("    {\"source\": \"%s\", \"target\":\"%s\" ,\"id\":\"%s_%s\" , \"type\": \"arrow\", \"label\":\"%s\"}", rel.getSource().getName(), rel.getTarget().getName(), rel.getSource().getName(), rel.getTarget().getName(), rel.getType() ))
                                      .collect(Collectors.joining(",\n"));
        json.writeln( edges );
        json.writeln("  ]");
        json.writeln("  \"nodes\": [");
        final String nodes = concepts.stream()
                                      .map( concept -> String.format("     {\"label\":\"%s\",\"id\":\"%s\" ,\"color\":\"rgb(0,255,0)\",\"size\":1, \"x\": %.2f, \"y\": %.2f}", concept.getLabel(), concept.getName(),  Math.cos(Math.PI * 2 * atomicInteger.get() / concepts.size()), Math.sin(Math.PI * 2 * atomicInteger.getAndIncrement() / concepts.size()) ) )
                                      .collect(Collectors.joining(",\n"));
        json.writeln( nodes );
        json.writeln("  ]");
        json.writeln("}");
        json.close();
        return jsonPathFile.toString();
    }

    public GraphWriter( @NonNull final String outDir ) throws Exception{
        outputDir   = outDir;
        tableReport = new TableReport(Paths.get(outputDir, "index.html").toFile() );
        csvReport   = new CSVReport(Paths.get(outputDir, "results.csv").toFile() );
    }

    public void addGraph( @NonNull final PriorKnowledge priorKnowledge, @NonNull Set<Relation> relations) throws Exception {
        final String graphName   = priorKnowledge.getName().replace( "-", "_" );

        final File      outDir      = Paths.get( outputDir, graphName).toFile();
        outDir.mkdirs();
        final GraphicReport graphicReport = new GraphicReport(Paths.get(outputDir, graphName, "result_svg.html").toString());

        // TODO reporting could be faster by writing row by row for csv,js,dot,html in same time
        final Set<Concept> concepts = relations.stream()
                                               .map( rel -> Arrays.asList(rel.getSource(),rel.getTarget()) )
                                               .flatMap(Collection::stream)
                                               .collect(Collectors.toSet());

        final String    dotFilename = writeDotFile( graphName, relations, concepts );
        final String    jsFilename  = writeJsFile( graphName, concepts );
        final String    trFilename  = writeTableReport(Paths.get(outputDir, graphName, "result_table.html"), concepts);
        final String    csvFilename = writeCSVReport(Paths.get(outputDir, graphName, "results.csv"), concepts);
        final String    jsonFilename = writeJSONFile(Paths.get(outputDir, graphName, "results.json"), relations,concepts);

        graphicReport.addGraph(graphName, graphName+".svg");
        graphicReport.close();
        final String    url         = graphName+" <a href=\""+Paths.get(graphName,"result_svg.html").toString()+"\">SVG</a>"+" <a href=\""+Paths.get(graphName,"result_table.html").toString()+"\">Table</a>";
        final int       colonIndex  = priorKnowledge.getDescription().indexOf(':');
        final String    description = (colonIndex >= 0) ? priorKnowledge.getDescription().substring(0, colonIndex):priorKnowledge.getDescription();
        tableReport.addRow( priorKnowledge, url, description);
        csvReport.addRow(priorKnowledge);
        LOGGER.debug("File copied " + jsFilename);
        LOGGER.debug("File copied " + trFilename);
        LOGGER.debug("File copied " + jsonFilename);
        LOGGER.debug("File copied " + csvFilename);
        LOGGER.debug("File copied " + dotFilename);
    }

    public void close() throws IOException {
        tableReport.close();
        csvReport.close();
    }

    public void finalize() throws Throwable {
        if( ! tableReport.isClosed() )
            tableReport.close();
        if( ! csvReport.isClosed() )
            csvReport.close();
    }



}
