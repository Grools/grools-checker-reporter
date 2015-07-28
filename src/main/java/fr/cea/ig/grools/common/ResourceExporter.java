package fr.cea.ig.grools.common;
/*
 * Copyright LABGeM 24/02/15
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
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

/**
 *
 */
/*
 * @startuml
 * class ResourceExporter{
 *  {static} + export(final String resourceName) : String
 * }
 * @enduml
 */
public class ResourceExporter {
    private static final Logger LOG = (Logger) LoggerFactory.getLogger(ResourceExporter.class);
    /**
     * Export a resource embedded into a Jar file to the local file path.
     *
     * @param resourceName ie.: "/foo.txt"
     * @param outputDir  path where file will be copied
     * @return The path to the exported resource
     * @throws Exception for any kind of exception
     */
    static public String export( final String resourceName, final  String outputDir) throws Exception {
        InputStream     stream      = null;
        OutputStream    resStreamOut= null;
        int readBytes;
        final byte[] buffer         = new byte[4096];
        final File  file            = Paths.get(outputDir, resourceName).toFile();
        final String outputFile     = file.getAbsolutePath();
        final boolean isCreated    = file.getParentFile().mkdirs();
        if( ! isCreated )
            LOG.info("Directory " + file.getParentFile() + "exists already");

        try {
            stream = ResourceExporter.class.getResourceAsStream(resourceName);
            if(stream == null)
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");

            resStreamOut    = new FileOutputStream(outputFile);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            try {
                    if( stream != null)
                        stream.close();
                    if(resStreamOut != null)
                        resStreamOut.close();
            } catch (Exception ex){
                throw ex;
            }
        }

        return outputFile;
    }

    private    ResourceExporter(){};
}
