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
import fr.cea.ig.grools.common.BiMap;
import fr.cea.ig.grools.common.BidirectionalMap;
import fr.cea.ig.grools.common.Command;
import fr.cea.ig.grools.common.ResourceExporter;
import fr.cea.ig.grools.model.PriorKnowledge;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    private static String getId( final String prefix, final PriorKnowledge knowledge, final BiMap<String, PriorKnowledge> ids ){
        boolean isRunning   = true;
        final String oriId  = prefix + "_" + knowledge.getName().replace(" ", "_");
        String  newId       = oriId;
        int     copy        = 1;
        while( isRunning ){
            if( ids.containsKey(newId) && ids.get(newId) != knowledge ) {
                newId = oriId + String.valueOf( copy );
                copy++;
            }
            else{
                ids.put(newId, knowledge);
                isRunning = false;
            }
        }
        return  newId;
    }

    private void generateId(final String graphName, final List<PriorKnowledge> knowledgeList, final BiMap<String, PriorKnowledge> ids ){
        for( PriorKnowledge knowledge : knowledgeList)
            getId( graphName , knowledge, ids );
    }

    private String writeDotFile(final String graphName, final BiMap<String, PriorKnowledge> ids ) throws Exception {
        final String    dotFilename = Paths.get(outputDir, graphName + ".dot").toString();
        final DotFile   dotFile     = new DotFile(graphName, dotFilename);
        String          color       = "Black";
        String          id;
        PriorKnowledge knowledge;

        for( final Map.Entry<String,PriorKnowledge> entry : ids.entrySet()){
            knowledge   = entry.getValue();
            id          = entry.getKey();
            switch (knowledge.getConclusion()){
                case UNCONFIRMED_PRESENCE:
                case UNCONFIRMED_ABSENCE:   color = "PaleTurquoise"; break;
                case CONFIRMED_ABSENCE:
                case CONFIRMED_PRESENCE:    color = "PaleGreen "; break;
                case UNEXPECTED:
                case UNEXPECTED_ABSENCE:
                case UNEXPECTED_PRESENCE:   color = "MistyRose "; break;
                case CONTRADICTORY:
                case CONTRADICTORY_PRESENCE:
                case CONTRADICTORY_ABSENCE: color = "LavenderBlush"; break;
                default:                    color = "GhostWhite"; break;
            }
            switch ( knowledge.getNodeType() ){
                case OR: dotFile.addNode(id, color, "octagon"); break;
                case AND:
                default: dotFile.addNode(id, color); break;
            }
            BiMap<PriorKnowledge,String> idsInv = ids.inverse();
            for( final PriorKnowledge parent: knowledge.getPartOf() ) {
                final String parentId = idsInv.get(parent);
                dotFile.linkNode( parentId, id );
            }
        }
        dotFile.close();
        dotToSvg(graphName, dotFile);
        return dotFilename;
    }

    private void dotToSvg( final String graphName, final DotFile dotFile ) throws Exception {
        final String outFile = Paths.get(outputDir, graphName + ".svg").toString();
        Command.run("dot", Arrays.asList("-Tsvg", "-o" + outFile, dotFile.getAbsolutePath()));
    }

    private String writeJsFile( final String graphName, final Map<String, PriorKnowledge> ids ) throws IOException {
        final String    jsFilename  = Paths.get(outputDir, "js", graphName + ".js").toString();
        final JsFile    jsFile      = new JsFile(jsFilename);
        String          color       = "";
        PriorKnowledge knowledge;
        jsFile.writeln(String.format("    const object_svg_%s   = document.getElementById('%s');", graphName, graphName));
        jsFile.writeln(String.format("    const svgdoc_%s       = object_svg_%s.contentDocument;", graphName, graphName));
        jsFile.writeln(              "    const tooltips        = document.createElement('div');");
        jsFile.writeln(              "    tooltips.id           = 'tooltips-content';");
        jsFile.writeln(              "    tooltips.className    = 'grools';");
        jsFile.writeln(              "    document.body.appendChild(tooltips);");
        jsFile.writeln(              "    const tooltipsId    = document.getElementById('tooltips-content');");
        for( final Map.Entry<String,PriorKnowledge> entry : ids.entrySet()){
            knowledge = entry.getValue();
            jsFile.writeln(String.format("    const svg_%s = svgdoc_%s.getElementById('%s');", entry.getKey(), graphName, entry.getKey() ));
            switch (knowledge.getConclusion()){
                case CONFIRMED_ABSENCE:
                case CONFIRMED_PRESENCE:    color = "green"; break;
                case UNCONFIRMED_PRESENCE:
                case UNCONFIRMED_ABSENCE:   color = "Chartreuse"; break;
                default:                    color = "LightPink"; break;
            }
            jsFile.writeln(String.format("    tooltips_event(tooltipsId, svg_%s, createInformativeNode('%s', '%s') );", entry.getKey(), knowledge.getConclusion(), color) );
        }
        jsFile.close();
        return jsFilename;
    }

    public GraphWriter(final String outDir ) throws IOException{
        outputDir       = outDir;
        htmlFile        = new HtmlFile(Paths.get(outputDir,"result.html").toString());
    }

    public void addGraph( final String graphName, final List<PriorKnowledge> knowledgeList) throws Exception {
        final BiMap<String, PriorKnowledge>  ids             = new BidirectionalMap<String, PriorKnowledge>();
        generateId(graphName, knowledgeList, ids);
        final String    dotFilename = writeDotFile(graphName, ids);
        final String    jsFilename  = writeJsFile( graphName, ids );
        LOG.info("File copied " + jsFilename );
        LOG.info("File copied " + dotFilename );

        htmlFile.addGraph(graphName, graphName+".svg");

        String jsPath3 = ResourceExporter.export("/js/svg_common.js", outputDir);
        LOG.info("File copied " + jsPath3 );
    }

    public void close() throws IOException{
        htmlFile.close();
    }

}
