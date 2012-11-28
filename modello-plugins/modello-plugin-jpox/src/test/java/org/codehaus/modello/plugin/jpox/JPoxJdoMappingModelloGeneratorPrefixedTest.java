package org.codehaus.modello.plugin.jpox;

/*
 * Copyright (c) 2005, Codehaus.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.modello.ModelloParameterConstants;
import org.codehaus.modello.core.ModelloCore;
import org.codehaus.modello.model.Model;
import org.codehaus.plexus.util.ReaderFactory;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.Properties;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: JPoxJdoMappingModelloGeneratorPrefixedTest.java 840 2007-07-17 18:50:39Z hboutemy $
 */
public class JPoxJdoMappingModelloGeneratorPrefixedTest extends AbstractJpoxGeneratorTestCase
{
    public JPoxJdoMappingModelloGeneratorPrefixedTest()
    {
        super( "jpox-jdo-mapping" );
    }

    public void testInvocationWithPrefixes() throws Exception
    {
        ModelloCore core = (ModelloCore) lookup( ModelloCore.ROLE );

        Model model = core.loadModel( ReaderFactory.newXmlReader( getTestFile( "src/test/resources/test-with-prefixes.mdo" ) ) );

        // ----------------------------------------------------------------------
        // Generate the code
        // ----------------------------------------------------------------------

        Properties parameters = new Properties();

        parameters.setProperty( ModelloParameterConstants.OUTPUT_DIRECTORY, getGeneratedSources().getAbsolutePath() );

        parameters.setProperty( ModelloParameterConstants.VERSION, "1.0.0" );

        parameters.setProperty( ModelloParameterConstants.PACKAGE_WITH_VERSION, Boolean.FALSE.toString() );

        core.generate( model, "jpox-jdo-mapping", parameters );

        // ----------------------------------------------------------------------
        // Assert
        // ----------------------------------------------------------------------

        assertGeneratedFileExists( "package.jdo" );

        SAXReader reader = new SAXReader();
        reader.setEntityResolver( new JdoEntityResolver() );
        Document jdoDocument = reader.read( new File( "target/" + getName() + "/package.jdo" ) );

        assertNotNull( jdoDocument );

        // Tree should consist of only elements with attributes. NO TEXT.
        assertNoTextNodes( jdoDocument, "//jdo", true );

        assertAttributeEquals( jdoDocument,
                               "//class[@name='RbacJdoModelModelloMetadata']/field[@name='modelVersion']/column",
                               "default-value", "1.0.0" );

        assertAttributeEquals( jdoDocument, "//class[@name='JdoResource']/field[@name='modelEncoding']", "persistence-modifier",
                               "none" );

        // -----------------------------------------------------------------------
        // Association Tests.

        //   mdo/association/jpox.dependent-element == false (only on association with "*" multiplicity (default type)
        assertAttributeEquals( jdoDocument, "//class[@name='JdoRole']/field[@name='permissions']/collection",
                               "dependent-element", "false" );

        //   mdo/association (default type) with "1" multiplicity.
        assertElementNotExists( jdoDocument, "//class[@name='JdoPermission']/field[@name='operation']/collection" );

        // -----------------------------------------------------------------------
        // Fetch Group Tests
        assertAttributeMissing( jdoDocument, "//class[@name='JdoRole']/field[@name='assignable']", "default-fetch-group" );
        assertAttributeEquals( jdoDocument, "//class[@name='JdoRole']/field[@name='childRoleNames']",
                               "default-fetch-group", "true" );

        // -----------------------------------------------------------------------
        // Alternate Table and Column Names Tests.
        assertAttributeEquals( jdoDocument, "//class[@name='JdoPermission']", "table", "SECURITY_PERMISSIONS" );
        assertAttributeEquals( jdoDocument, "//class[@name='JdoOperation']/field[@name='name']/column", "name", "OPERATION_NAME" );
        assertAttributeEquals( jdoDocument, "//class[@name='JdoRole']/field[@name='permissions']", "table", "SECURITY_ROLE_PERMISSION_MAP" );
    }
}