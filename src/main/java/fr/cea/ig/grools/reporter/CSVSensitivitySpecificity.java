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
import fr.cea.ig.grools.common.WrapFile;
import fr.cea.ig.grools.fact.PriorKnowledge;
import fr.cea.ig.grools.reasoner.Reasoner;
import lombok.NonNull;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
/*
 * @startuml
 * class CSVReport{
 * }
 * @enduml
 */
public class CSVSensitivitySpecificity extends WrapFile {
    private static final Logger    LOGGER = ( Logger ) LoggerFactory.getLogger( CSVSensitivitySpecificity.class );
    private static final Object[]  header = { "Name",  "Conclusion", "Category" };
    private static final CSVFormat format = CSVFormat.RFC4180
            .withDelimiter( ';' )
            .withRecordSeparator( '\n' )
            .withQuote( '"' )
            .withFirstRecordAsHeader( );
    private final CSVPrinter csvPrinter;

    public CSVSensitivitySpecificity( @NonNull final String filepath, @NonNull final Reasoner reasoner ) throws IOException {
        super( filepath );
        this.csvPrinter = new CSVPrinter( bos, format );
        csvPrinter.printRecord( header );
    }

    public CSVSensitivitySpecificity( @NonNull final File file ) throws IOException {
        super( file );
        this.csvPrinter = new CSVPrinter( bos, format );
        csvPrinter.printRecord( header );
    }

    public void addRow( @NonNull final PriorKnowledge priorKnowledge ) throws IOException {
        final SensitivitySpecificity ss = Reporter.toClassification( priorKnowledge.getConclusion( ) );
        if( ss != null ) { // some conclusions fall into any Sensitivity-Specificity class
            final List< Object > records = new ArrayList<>( );
            records.add( priorKnowledge.getName( ) );
            records.add( priorKnowledge.getConclusion( ) );
            records.add( ss );
            csvPrinter.printRecord( records );
        }
    }

    public void close( ) throws IOException {
        if( !isClosed( ) )
            csvPrinter.close( );
        super.close( );
    }

    public void finalize( ) throws Throwable {
        close( );
        super.finalize( );
    }
}
