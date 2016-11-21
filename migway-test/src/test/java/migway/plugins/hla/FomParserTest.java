package migway.plugins.hla;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAASCIIchar;
import hla.rti1516e.encoding.HLAASCIIstring;
import hla.rti1516e.encoding.HLAboolean;
import hla.rti1516e.encoding.HLAbyte;
import hla.rti1516e.encoding.HLAfixedArray;
import hla.rti1516e.encoding.HLAfixedRecord;
import hla.rti1516e.encoding.HLAfloat32BE;
import hla.rti1516e.encoding.HLAfloat32LE;
import hla.rti1516e.encoding.HLAfloat64BE;
import hla.rti1516e.encoding.HLAfloat64LE;
import hla.rti1516e.encoding.HLAinteger16BE;
import hla.rti1516e.encoding.HLAinteger16LE;
import hla.rti1516e.encoding.HLAinteger32BE;
import hla.rti1516e.encoding.HLAinteger32LE;
import hla.rti1516e.encoding.HLAinteger64BE;
import hla.rti1516e.encoding.HLAinteger64LE;
import hla.rti1516e.encoding.HLAoctet;
import hla.rti1516e.encoding.HLAunicodeChar;
import hla.rti1516e.encoding.HLAunicodeString;
import hla.rti1516e.encoding.HLAvariantRecord;
import hla.rti1516e.exceptions.InconsistentFDD;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Stack;

import migway.core.config.ArrayType;
import migway.core.config.BasicType;
import migway.core.config.ClassStructure;
import migway.core.config.ConfigHelper;
import migway.core.config.Element;
import migway.core.config.Structure;
import migway.core.config.StructureRef;
import migway.core.config.UnionStructure;
import migway.core.helper.PojoLoaderHelper;
import migway.core.samples.HlaSamples;

import org.ieee.standards.ieee1516_2010.ArrayDataType;
import org.ieee.standards.ieee1516_2010.BasicDataType;
import org.ieee.standards.ieee1516_2010.EnumeratedDataType;
import org.ieee.standards.ieee1516_2010.EnumeratedDataType.Enumerator;
import org.ieee.standards.ieee1516_2010.EnumeratedDataTypesType.EnumeratedData;
import org.ieee.standards.ieee1516_2010.FixedRecordDataType;
import org.ieee.standards.ieee1516_2010.FixedRecordDataTypesType.FixedRecordData;
import org.ieee.standards.ieee1516_2010.SimpleDataType;
import org.ieee.standards.ieee1516_2010.SimpleDataTypesType.SimpleData;
import org.ieee.standards.ieee1516_2010.VariantRecordDataType;
import org.ieee.standards.ieee1516_2010.VariantRecordDataType.Alternative;
import org.ieee.standards.ieee1516_2010.VariantRecordDataTypesType.VariantRecordData;
import org.ieee.standards.ieee1516_2010.interfaces.IDataType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FomParserTest {

    private static Logger LOG = LoggerFactory.getLogger(FomParserTest.class);
    private FomParser parser;
    private EncoderFactory encoderFactory;
    private PojoLoaderHelper pojoHelper;
    private ConfigHelper helper;

    @SuppressWarnings("unchecked")
    @Test
    public void testManualBuildSpatialVariantStruct() throws Exception {
        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();
        Stack<IDataType> stackDataType = new Stack<IDataType>();
        Stack<DataElement> stackDataElement = new Stack<DataElement>();

        parser = new FomParser();
        parser.parse(new File("RPR2-D20_2010.xml"));
        LOG.info("FOM parsed");

        IDataType dataType = parser.getDataTypes("SpatialVariantStruct");
        LOG.debug("Structure SpatialVariantStruct = {}", dataType);
        assertTrue(dataType instanceof VariantRecordData);
        // It's a variant.
        // -- discriminant
        assertEquals("DeadReckoningAlgorithm", ((VariantRecordData) dataType).getDiscriminant().getValue());
        // -- discriminant type
        assertEquals("DeadReckoningAlgorithmEnum8", ((VariantRecordData) dataType).getDataType().getValue());
        // -- 9 alternatives
        assertEquals(9, ((VariantRecordData) dataType).getAlternative().size());

        /*
         * Objects:
         * - structure
         * - dataType
         */
        // Get representation of discriminant
        IDataType disc = parser.getDataTypes(((VariantRecordData) dataType).getDataType().getValue());
        assertTrue(disc instanceof EnumeratedData);
        LOG.debug("Before getBasicDataRepresentation");
        disc = parser.getBasicDataRepresentation(disc);
        LOG.debug("getBasicDataRepresentation result = {}", disc);
        assertTrue(disc instanceof EnumeratedData);
        // -- discriminant is a HLAoctet
        assertEquals("HLAoctet", ((EnumeratedData) disc).getRepresentation().getValue());

        // **
        DataElement result = encoderFactory.createHLAvariantRecord(encoderFactory.createHLAoctet()); // SpatialVariantStruct
        // **/
        /**
         * Sauvegarde du Variant courant avant de traiter les alternatives
         */
        stackDataType.push(dataType); // Save SpatialVariantStruct

        // -- alternative #0 // SpatialStaticStruct
        dataType = parser.getDataTypes(((VariantRecordData) dataType).getAlternative().get(0).getDataType().getValue());
        // --- FixedRecord
        assertTrue(dataType instanceof FixedRecordData);
        dataType = parser.getBasicDataRepresentation(dataType); // SpatialStaticStruct
        LOG.debug("getBasicDataRepresentation result = {}", dataType);
        assertTrue(dataType instanceof FixedRecordData);
        assertEquals("HLAfixedRecord", ((FixedRecordData) dataType).getEncoding().getValue());
        assertEquals("SpatialStaticStruct", ((FixedRecordData) dataType).getName().getValue());
        assertEquals(3, ((FixedRecordData) dataType).getField().size());

        stackDataElement.push((DataElement) result); // Save
                                                     // SpatialVariantStruct
        // **
        result = encoderFactory.createHLAfixedRecord(); // SpatialStaticStruct
        // **/

        /**
         * Sauvegarde du FixedRecord courant avant de traiter les champs
         */
        stackDataType.push(dataType); // Save SpatialStaticStruct
        // --- Field #0 // WorldLocationStruct
        dataType = parser.getDataTypes(((FixedRecordData) dataType).getField().get(0).getDataType().getValue());
        dataType = parser.getBasicDataRepresentation(dataType);
        assertTrue(dataType instanceof FixedRecordData);
        assertEquals("HLAfixedRecord", ((FixedRecordData) dataType).getEncoding().getValue());
        assertEquals("WorldLocationStruct", ((FixedRecordData) dataType).getName().getValue());
        assertEquals(3, ((FixedRecordData) dataType).getField().size());

        stackDataElement.push((DataElement) result); // SaveSpatialStaticStruct
        // **
        result = encoderFactory.createHLAfixedRecord(); // WorldLoactionStruct
        // **/
        /**
         * Sauvegarde du FixedRecord courant avant de traiter les champs
         */
        stackDataType.push(dataType); // Save WorldLocationStruct

        // ----Field #0 // Float64BE
        dataType = parser.getDataTypes(((FixedRecordData) dataType).getField().get(0).getDataType().getValue());
        dataType = parser.getBasicDataRepresentation(dataType);
        assertTrue(dataType instanceof SimpleData);
        assertEquals("HLAfloat64BE", ((SimpleData) dataType).getRepresentation().getValue());
        assertEquals("MeterFloat64", ((SimpleData) dataType).getName().getValue());
        // **
        // WorldLocationStruct.add(float64)
        ((HLAfixedRecord) result).add(encoderFactory.createHLAfloat64BE());
        // **/
        dataType = stackDataType.pop(); // Récupère le WorldLocationStruct
        // ----Field #1 // Float64BE
        stackDataType.push(dataType); // Save WorldLocationStruct
        dataType = parser.getDataTypes(((FixedRecordData) dataType).getField().get(0).getDataType().getValue());
        dataType = parser.getBasicDataRepresentation(dataType);
        assertTrue(dataType instanceof SimpleData);
        assertEquals("HLAfloat64BE", ((SimpleData) dataType).getRepresentation().getValue());
        assertEquals("MeterFloat64", ((SimpleData) dataType).getName().getValue());
        // **
        // WorldLocationStruct.add(float64)
        ((HLAfixedRecord) result).add(encoderFactory.createHLAfloat64BE());
        // **/
        dataType = stackDataType.pop(); // Récupère le WorldLocationStruct
        // ----Field #2 // Float64BE
        stackDataType.push(dataType); // Save WorldLocationStruct
        dataType = parser.getDataTypes(((FixedRecordData) dataType).getField().get(0).getDataType().getValue());
        dataType = parser.getBasicDataRepresentation(dataType);
        assertTrue(dataType instanceof SimpleData);
        assertEquals("HLAfloat64BE", ((SimpleData) dataType).getRepresentation().getValue());
        assertEquals("MeterFloat64", ((SimpleData) dataType).getName().getValue());
        // **
        // WorldLocationStruct.add(float64)
        ((HLAfixedRecord) result).add(encoderFactory.createHLAfloat64BE());
        // **/
        dataType = stackDataType.pop(); // Restore WorldLocationStruct
        // --- Fin Field #0 // WorldLocationStruct, add it to
        // SpatialStaticStruct
        dataType = stackDataType.pop(); // Restore SpatialStaticStruct
        // **
        DataElement sup = stackDataElement.pop(); // Restore SpatialStaticStruct
        ((HLAfixedRecord) sup).add((HLAfixedRecord) result); // Add
                                                             // WorldLocationStruct
                                                             // to
                                                             // SpatialStaticStruct
        // **/
        result = sup; // SpatialStaticStruct

        // --- Field #1
        stackDataType.push(dataType); // Save SpatialStaticStruct
        dataType = parser.getDataTypes(((FixedRecordData) dataType).getField().get(1).getDataType().getValue());
        dataType = parser.getBasicDataRepresentation(dataType);
        assertTrue(dataType instanceof EnumeratedData);
        assertEquals("HLAoctet", ((EnumeratedData) dataType).getRepresentation().getValue());
        assertEquals("RPRboolean", ((EnumeratedData) dataType).getName().getValue());
        // **
        ((HLAfixedRecord) result).add(encoderFactory.createHLAoctet());
        // **/
        dataType = stackDataType.pop(); // Restore SpatialStaticStruct
        // --- Field #2
        stackDataType.push(dataType); // Save SpatialStaticStruct
        dataType = parser.getDataTypes(((FixedRecordData) dataType).getField().get(2).getDataType().getValue());
        dataType = parser.getBasicDataRepresentation(dataType); // OrientationStruct
        assertTrue(dataType instanceof FixedRecordData);
        assertEquals("HLAfixedRecord", ((FixedRecordData) dataType).getEncoding().getValue());
        assertEquals("OrientationStruct", ((FixedRecordData) dataType).getName().getValue());
        assertEquals(3, ((FixedRecordData) dataType).getField().size());

        stackDataElement.push(result); // Save SpatialStaticStruct
        // **
        result = encoderFactory.createHLAfixedRecord(); // OrientationStruct
        // **/
        // ---- Field #0
        stackDataType.push(dataType); // Save OrientationStruct
        dataType = parser.getDataTypes(((FixedRecordData) dataType).getField().get(0).getDataType().getValue());
        dataType = parser.getBasicDataRepresentation(dataType);
        assertTrue(dataType instanceof SimpleData);
        assertEquals("HLAfloat32BE", ((SimpleData) dataType).getRepresentation().getValue());
        assertEquals("AngleRadianFloat32", ((SimpleData) dataType).getName().getValue());
        // **
        ((HLAfixedRecord) result).add(encoderFactory.createHLAfloat32BE());
        // **/
        dataType = stackDataType.pop(); // Restore OrientationStruct
        // ---- Field #1
        stackDataType.push(dataType); // Save OrientationStruct
        dataType = parser.getDataTypes(((FixedRecordData) dataType).getField().get(1).getDataType().getValue());
        dataType = parser.getBasicDataRepresentation(dataType);
        assertTrue(dataType instanceof SimpleData);
        assertEquals("HLAfloat32BE", ((SimpleData) dataType).getRepresentation().getValue());
        assertEquals("AngleRadianFloat32", ((SimpleData) dataType).getName().getValue());
        // **
        ((HLAfixedRecord) result).add(encoderFactory.createHLAfloat32BE());
        // **/
        dataType = stackDataType.pop(); // Restore OrientationStruct
        // ---- Field #2
        stackDataType.push(dataType); // Save OrientationStruct
        dataType = parser.getDataTypes(((FixedRecordData) dataType).getField().get(2).getDataType().getValue());
        dataType = parser.getBasicDataRepresentation(dataType);
        assertTrue(dataType instanceof SimpleData);
        assertEquals("HLAfloat32BE", ((SimpleData) dataType).getRepresentation().getValue());
        assertEquals("AngleRadianFloat32", ((SimpleData) dataType).getName().getValue());
        // **
        ((HLAfixedRecord) result).add(encoderFactory.createHLAfloat32BE());
        // **/
        dataType = stackDataType.pop(); // Restore OrientationStruct
        // --- Fin Field #2
        dataType = stackDataType.pop(); // Restore SpatialStaticStruct
        sup = stackDataElement.pop(); // Restore SpatialStaticStruct
        // ** Add OrientationStruct to SpatialStaticStruct
        ((HLAfixedRecord) sup).add((HLAfixedRecord) result);
        // **/
        result = sup;

        dataType = stackDataType.pop(); // Restore SpatialVariantStruct
        LOG.debug("99");
        // -- Fin alternative #0
        sup = stackDataElement.pop(); // Restore SpatialVariantStruct
        // ** Add SpatialStaticStruct to
        ((HLAvariantRecord<HLAoctet>) sup).setVariant(encoderFactory.createHLAoctet((byte) 0), result);
        // **/
        assertTrue(stackDataElement.isEmpty());
        assertTrue(stackDataType.isEmpty());

    }

    @Test
    public void testGetDataType() throws Exception {
        // test du getDataType:
        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        parser = new FomParser();
        parser.parse(new File("GVA_RPR2-D20_2010.xml"));
        LOG.info("FOM GVA-RPR parsed");

        assertNotNull(parser.getDataTypes("HLAoctet"));
        assertNotNull(parser.buildHlaDataElement("HLAoctet"));
        assertNotNull(parser.getDataTypes("SpatialVariantStruct"));

    }

    @Test
    public void testEncodeHlaDataElementVariantVersion() throws Exception {
        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        parser = new FomParser();
        parser.parse(new File("GVA_RPR2-D20_2010.xml"));
        LOG.info("FOM GVA-RPR parsed");

        DataElement variant = parser.buildHlaDataElement("SpatialVariantStruct");
        assertNotNull(variant);
        assertTrue(variant instanceof HLAvariantRecord);

    }

    @Test
    public void testBuildHlaDataElementVariantVersion() throws Exception {
        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        parser = new FomParser();
        parser.parse(new File("GVA_RPR2-D20_2010.xml"));
        LOG.info("FOM GVA-RPR parsed");

        DataElement variant = parser.buildHlaDataElement("SpatialVariantStruct");
        assertNotNull(variant);
        assertTrue(variant instanceof HLAvariantRecord);

        byte[] buffer = HlaSamples.getHlaBufferSpatialVariantStruct();
        @SuppressWarnings("unchecked")
        HLAvariantRecord<DataElement> variantRecord = (HLAvariantRecord<DataElement>) variant;
        variantRecord.decode(buffer);
        assertNotNull(variantRecord.getValue());
    }

    @Test
    public void testConvertHlaToPojo() throws Exception {
        // Initialize parser and helper
        pojoHelper = PojoLoaderHelper.INSTANCE;
        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        parser = new FomParser();
        parser.parse(new File("GVA_RPR2-D20_2010.xml"));
        LOG.info("FOM GVA-RPR parsed");

        helper = ConfigHelper.loadConfig(new File("migway:hla-RPRFOM.edu"));
        assertNotNull(helper);

        // Simulated buffer, as it is received by HlaComponent
        byte[] buffer = HlaSamples.getHlaBufferSpatialVariantStruct();

        String hlaDataTypeName = "SpatialVariantStruct";
        String pojoClassName = "edu.cyc14.essais.pojo.rprfom.SpatialVariantStruct";

        // Decode buffer into Hla object
        DataElement hlaDataElement = parser.buildHlaDataElement(hlaDataTypeName);
        assertNotNull(hlaDataElement);
        assertTrue(hlaDataElement instanceof HLAvariantRecord);
        @SuppressWarnings("unchecked")
        HLAvariantRecord<DataElement> hlaDataTypeObject = (HLAvariantRecord<DataElement>) hlaDataElement;
        hlaDataTypeObject.decode(buffer);
        assertNotNull(hlaDataTypeObject.getValue());

        Structure migStructure = helper.getPojoStructure(pojoClassName);
        assertNotNull(migStructure);

        LOG.debug(migStructure.getName() + " / " + migStructure.getRemoteName("HLA"));
        assertEquals("SpatialVariantStruct", migStructure.getRemoteName("HLA"));
        assertEquals("edu.cyc14.essais.pojo.rprfom.SpatialVariantStruct", migStructure.getName());

        // Build the object
        Object pojo = pojoHelper.loadPojo(pojoClassName);

        // And now, cycle the structure to update Pojo with Hla object values
        if (migStructure instanceof UnionStructure) {
            // Union, must be considered as a variant. Get the discriminant
            UnionStructure migUnionStructure = (UnionStructure) migStructure;
            /** Discriminant */
            assertTrue(migUnionStructure.getDiscrimant().elementType() instanceof StructureRef);
            // Name of the field that contains discriminant
            String pojoDiscriminantFieldName = migUnionStructure.getDiscrimant().name();
            assertEquals("deadReckoningAlgorithm", pojoDiscriminantFieldName);
            //
            int hlaDiscriminantValue = ((HLAoctet) hlaDataTypeObject.getDiscriminant()).getValue();
            assertEquals(2, hlaDiscriminantValue);

            String pojoDiscriminantFieldValue = "";

            // This is how to get, from the value, the name of the enum
            VariantRecordData fomObjectDataType = (VariantRecordData) parser.getDataTypes("SpatialVariantStruct");
            assertNotNull(fomObjectDataType);
            String fomDiscriminantDataTypeName = fomObjectDataType.getDataType().getValue();
            assertEquals("DeadReckoningAlgorithmEnum8", fomDiscriminantDataTypeName);

            EnumeratedData fomDiscriminantDataType = (EnumeratedData) parser.getDataTypes("DeadReckoningAlgorithmEnum8");
            // 1/ Find the index for this value
            int fomDiscriminantValueIndex = 0, tmpIndex = 0;
            for (Enumerator fomDiscriminantEnumerator : fomDiscriminantDataType.getEnumerator()) {
                LOG.trace("testing enumerator#" + tmpIndex + " (" + fomDiscriminantEnumerator.getName().getValue() + ")");
                List<org.ieee.standards.ieee1516_2010.String> fomDiscriminantEnumeratorValueList = fomDiscriminantEnumerator.getValue();
                for (org.ieee.standards.ieee1516_2010.String fomDiscriminantEnumeratorValueString : fomDiscriminantEnumeratorValueList) {
                    LOG.trace("value = " + fomDiscriminantEnumeratorValueString.getValue());
                    if (new Integer(hlaDiscriminantValue).toString().equals(fomDiscriminantEnumeratorValueString.getValue())) {
                        fomDiscriminantValueIndex = tmpIndex;
                        LOG.debug("Found value 2 at enumerator#" + fomDiscriminantValueIndex);
                    }
                }
                tmpIndex++;
            }
            // 2/ Find the name for the index
            String fomDiscriminantValueName = fomDiscriminantDataType.getEnumerator().get(fomDiscriminantValueIndex).getName().getValue();
            assertEquals("DRM_FPW", fomDiscriminantValueName);
            String migSelectedAlternativeName = migUnionStructure.getAlternative(fomDiscriminantValueName);
            assertEquals("spatialFPW", migSelectedAlternativeName);

            @SuppressWarnings("unused")
            Field pojoDiscriminantField = pojoHelper.getField(pojoClassName, pojoDiscriminantFieldName);
            // FIXME : crash, need to be converted to the enum type:
            // fomDiscriminantDataTypeName
            // pojoDiscriminantField.set(pojo, pojoDiscriminantFieldValue);
            LOG.info("Object " + pojoClassName + " affect value " + pojoDiscriminantFieldValue + " to field " + pojoDiscriminantFieldName);

            LOG.info("Object " + pojoClassName + " current alternative is " + migSelectedAlternativeName);

            Element migSelectedAlternativeElement = migUnionStructure.getElement(migSelectedAlternativeName);
            assertTrue(migSelectedAlternativeElement.elementType() instanceof StructureRef);
            String migSelectedAlternativeStructureName = ((StructureRef) migSelectedAlternativeElement.elementType()).getStructureName();
            @SuppressWarnings("unused")
            String pojoSelectedAlternativeClassName = migSelectedAlternativeStructureName;
            String pojoSelectedAlternativeFieldName = migSelectedAlternativeElement.name();
            LOG.info("Create new Object " + migSelectedAlternativeStructureName + " in field " + pojoSelectedAlternativeFieldName);

            Field pojoSelectedAlternativeField = pojoHelper.getField(pojoClassName, pojoSelectedAlternativeFieldName);

            // Get the fom alternative
            List<Alternative> fomAlternativesList = fomObjectDataType.getAlternative();
            Alternative fomSelectedAlternativeElement = null;
            @SuppressWarnings("unused")
            int fomSelectedAlternativeIndex = -1;
            for (int i = 0; i < fomAlternativesList.size(); i++) {
                LOG.debug("FOM Alternative #" + i + " = '" + fomAlternativesList.get(i).getEnumerator().getValue() + "' =? '"
                        + fomDiscriminantValueName + "' / " + fomAlternativesList.get(i).getDataType().getValue() + " / " + fomAlternativesList.get(i).getName().getValue());
                if (fomAlternativesList.get(i).getEnumerator().getValue().equals(fomDiscriminantValueName)) {
                    LOG.debug("-- FOUND");
                    fomSelectedAlternativeIndex = i;
                    fomSelectedAlternativeElement = fomAlternativesList.get(i);
                }
            }
            /** Build field content **/
            // Object pojoAlternativeObject =
            // pojoHelper.loadPojo(pojoSelectedAlternativeClassName);
            Object pojoAlternativeObject = decodeElement(fomSelectedAlternativeElement.getDataType().getValue(),
                    migSelectedAlternativeStructureName, hlaDataTypeObject.getValue());
            pojoSelectedAlternativeField.set(pojo, pojoAlternativeObject);
            assertNotNull(pojoAlternativeObject);
            // String fomObjectDataType =
            // fomSelectedAlternativeElement.getDataType().getValue();
            Structure mgwSpatialFPStruct = helper.getPojoStructure(migSelectedAlternativeStructureName);
            for (int j = 0; j < mgwSpatialFPStruct.getElementsSize(); j++) {
                // 1. Get the value from hla
                // fomObjectDataType.getAlternative().get(index);
                // hlaDataTypeObject

                LOG.info("Create " + mgwSpatialFPStruct.get(j).fullName());
            }
        }
        for (int i = 0; i < migStructure.getElementsSize(); i++) {
            @SuppressWarnings("unused")
            Element mgwElement = migStructure.getElement(i);
            // LOG.debug(mgwElement.getRemoteName() + " -> " + mgwElement.name()
            // + " / ");
        }
    }

    public Object decodeElement(String fomObjectDataTypeName, String migObjectStructureName, DataElement hlaDataElement)
            throws InconsistentFDD, ClassNotFoundException, InstantiationException, IllegalAccessException {
        IDataType fomObjectDataType = parser.getDataTypes(fomObjectDataTypeName);
        if (fomObjectDataType instanceof ArrayDataType)
            return decodeArrayDataType((ArrayDataType) fomObjectDataType, migObjectStructureName, hlaDataElement);
        if (fomObjectDataType instanceof BasicDataType)
            return decodeBasicDataType((BasicDataType) fomObjectDataType, migObjectStructureName, hlaDataElement);
        if (fomObjectDataType instanceof EnumeratedDataType)
            return decodeEnumeratedDataType((EnumeratedDataType) fomObjectDataType, migObjectStructureName, hlaDataElement);
        if (fomObjectDataType instanceof FixedRecordDataType)
            return decodeFixedRecordDataType((FixedRecordDataType) fomObjectDataType, migObjectStructureName, hlaDataElement);
        if (fomObjectDataType instanceof SimpleDataType)
            return decodeSimpleDataType((SimpleDataType) fomObjectDataType, migObjectStructureName, hlaDataElement);
        if (fomObjectDataType instanceof VariantRecordDataType)
            return decodeVariantRecordDataType((VariantRecordDataType) fomObjectDataType, migObjectStructureName, hlaDataElement);

        return null;
    }

    public Object decodeArrayDataType(ArrayDataType fomObjectDataType, String migObjectStructureName, DataElement hlaDataElement) {
        return null;
    }

    public Object decodeBasicDataType(BasicDataType fomObjectDataType, String migObjectStructureName, DataElement hlaDataElement) {
        return null;
    }

    public Object decodeEnumeratedDataType(EnumeratedDataType fomObjectDataType, String migObjectStructureName, DataElement hlaDataElement) {
        return null;
    }

    public Object decodeFixedRecordDataType(FixedRecordDataType fomObjectDataType, String pojoClassName, DataElement hlaDataElement)
            throws InconsistentFDD, ClassNotFoundException, InstantiationException, IllegalAccessException {
        @SuppressWarnings("unused")
        List<FixedRecordDataType.Field> fomObjectFieldList = fomObjectDataType.getField();
        Structure migObjectStructure = helper.getPojoStructure(pojoClassName);
        if (!(migObjectStructure instanceof ClassStructure))
            throw new InconsistentFDD(pojoClassName + " is not a ClassStructure, as described in FOM");
        if (!(hlaDataElement instanceof HLAfixedRecord))
            throw new InconsistentFDD(pojoClassName + " is not a FixedRecord, as described in FOM");

        HLAfixedRecord hlaObject = (HLAfixedRecord) hlaDataElement;
        ClassStructure migClassStructure = (ClassStructure) migObjectStructure;

        // Build the POJO
        Object pojoClassObject = pojoHelper.loadPojo(migObjectStructure.getName());
        // And now, assign values to its fields

        for (int elementId = 0; elementId < migClassStructure.getElementsSize(); elementId++) {
            Element migElement = migClassStructure.getElement(elementId);
            // Field pojoField = pojoHelper.getField(pojoClassName,
            // migElement.name()); // Sera fait dans decodeElement
            // Find Field from FOM
            String fomFieldDataTypeName = null;
            String fomFieldName = null;
            int fomFieldIndex = -1; // will be the position of the field in the
                                    // list
            int tmpCount = 0; // temp id
            for (FixedRecordDataType.Field fomField : fomObjectDataType.getField()) {
                if (fomField.getName().getValue().equals(migElement.getRemoteName("HLA"))) {
                    fomFieldDataTypeName = fomField.getDataType().getValue();
                    fomFieldName = fomField.getName().getValue();
                    fomFieldIndex = tmpCount;
                }
                tmpCount++;
            }
            if (fomFieldIndex >= 0) {
                try {
                    decodeElement(migElement, pojoClassObject, fomFieldDataTypeName, fomFieldName, hlaObject.get(fomFieldIndex));
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {

                }
            } else {
                // Field not found
                // ignore it
            }
        }
        return pojoClassObject;
    }

    public Object decodeElement(Element migElement, Object pojoClassObject, String fomDataTypeName, String fomName,
            DataElement hlaDataObject) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException,
            NoSuchFieldException, SecurityException {
        if (migElement.elementType() instanceof BasicType) {
            // Basic type: simply assign the value
            Field pojoField = pojoHelper.getField(pojoClassObject.getClass().getName(), migElement.name());
            // fomDataTypeName must be the same as migElement.elementType
            switch (((BasicType) migElement.elementType()).getBasicTypeEnum()) {
            case BOOLEAN:
                pojoField.set(pojoClassObject, readBoolean(hlaDataObject));
                break;
            case BYTE:
                pojoField.set(pojoClassObject, readByte(hlaDataObject));
                break;
            case SHORT:
                pojoField.set(pojoClassObject, readShort(hlaDataObject));
                break;
            case INT:
                pojoField.set(pojoClassObject, readInt(hlaDataObject));
                break;
            case LONG:
                pojoField.set(pojoClassObject, readLong(hlaDataObject));
                break;
            case FLOAT:
                pojoField.set(pojoClassObject, readFloat(hlaDataObject));
                break;
            case DOUBLE:
                pojoField.set(pojoClassObject, readDouble(hlaDataObject));
                break;
            case CHAR:
                pojoField.set(pojoClassObject, readChar(hlaDataObject));
                break;
            case STRING:
                pojoField.set(pojoClassObject, readString(hlaDataObject));
                break;
            case UNSIGNED_BYTE:
                pojoField.set(pojoClassObject, readUnsignedByte(hlaDataObject));
                break;
            case UNSIGNED_SHORT:
                pojoField.set(pojoClassObject, readUnsignedShort(hlaDataObject));
                break;
            case UNSIGNED_INT:
                pojoField.set(pojoClassObject, readUnsignedInt(hlaDataObject));
                break;
            case UNSIGNED_FLOAT:
                pojoField.set(pojoClassObject, readUnsignedFloat(hlaDataObject));
                break;
            }
            // get value from field fomName of hlaDataObject
            // assign this value to field migElement.name of pojoClassObject
        }
        if (migElement.elementType() instanceof ArrayType) {
            @SuppressWarnings("unused")
            ArrayType migArray = (ArrayType) migElement.elementType();
            if (hlaDataObject instanceof HLAfixedArray) {
                @SuppressWarnings("unchecked")
                HLAfixedArray<DataElement> hlaArray = (HLAfixedArray<DataElement>) hlaDataObject;
                @SuppressWarnings("unused")
                int size = hlaArray.size();
                // Array.newInstance(migArray.getElementType(), size);
            }
            // Get array size from hla
            // create array in pojo
            // get values from hla
            // assign into pojo

        }
        if (migElement.elementType() instanceof StructureRef) {
            // Complex Structure
            // Object pojoFieldValue = decodeStructure(...)
            // Assign pojoFieldValue to Field with name migElement.name() of
            // pojoClassObject
        }

        return pojoClassObject;
    }


    public boolean readBoolean(DataElement hlaDataObject) {
        if (hlaDataObject instanceof HLAboolean) {
            return ((HLAboolean) hlaDataObject).getValue();
        }
        throw new UnsupportedOperationException("DataElement should be a boolean but is a " + hlaDataObject.getClass().getName());
    }

    private short readUnsignedShort(DataElement hlaDataObject) {
        if (hlaDataObject instanceof HLAinteger16BE) {
            return ((HLAinteger16BE) hlaDataObject).getValue();
        }
        if (hlaDataObject instanceof HLAinteger16LE) {
            return ((HLAinteger16LE) hlaDataObject).getValue();
        }
        throw new UnsupportedOperationException("DataElement should be a unsigned short but is a " + hlaDataObject.getClass().getName());
    }

    private int readUnsignedInt(DataElement hlaDataObject) {
        if (hlaDataObject instanceof HLAinteger32BE) {
            return ((HLAinteger32BE) hlaDataObject).getValue();
        }
        if (hlaDataObject instanceof HLAinteger32LE) {
            return ((HLAinteger32LE) hlaDataObject).getValue();
        }
        throw new UnsupportedOperationException("DataElement should be a unsigned short but is a " + hlaDataObject.getClass().getName());
    }

    private float readUnsignedFloat(DataElement hlaDataObject) {
        if (hlaDataObject instanceof HLAfloat32BE) {
            return ((HLAfloat32BE) hlaDataObject).getValue();
        }
        if (hlaDataObject instanceof HLAfloat32LE) {
            return ((HLAfloat32LE) hlaDataObject).getValue();
        }
        throw new UnsupportedOperationException("DataElement should be a unsigned short but is a " + hlaDataObject.getClass().getName());
    }

    private byte readUnsignedByte(DataElement hlaDataObject) {
        if (hlaDataObject instanceof HLAoctet) {
            return ((HLAoctet) hlaDataObject).getValue();
        }
        if (hlaDataObject instanceof HLAbyte) {
            return ((HLAbyte) hlaDataObject).getValue();
        }
        throw new UnsupportedOperationException("DataElement should be a unsigned short but is a " + hlaDataObject.getClass().getName());
    }

    private String readString(DataElement hlaDataObject) {
        if (hlaDataObject instanceof HLAASCIIstring) {
            return ((HLAASCIIstring) hlaDataObject).getValue();
        }
        if (hlaDataObject instanceof HLAunicodeString) {
            return ((HLAunicodeString) hlaDataObject).getValue();
        }
        throw new UnsupportedOperationException("DataElement should be a unsigned short but is a " + hlaDataObject.getClass().getName());
    }

    private short readShort(DataElement hlaDataObject) {
        if (hlaDataObject instanceof HLAinteger16BE) {
            return ((HLAinteger16BE) hlaDataObject).getValue();
        }
        if (hlaDataObject instanceof HLAinteger16LE) {
            return ((HLAinteger16LE) hlaDataObject).getValue();
        }
        throw new UnsupportedOperationException("DataElement should be a unsigned short but is a " + hlaDataObject.getClass().getName());
    }

    private long readLong(DataElement hlaDataObject) {
        if (hlaDataObject instanceof HLAinteger64BE) {
            return ((HLAinteger64BE) hlaDataObject).getValue();
        }
        if (hlaDataObject instanceof HLAinteger64LE) {
            return ((HLAinteger64LE) hlaDataObject).getValue();
        }
        throw new UnsupportedOperationException("DataElement should be a unsigned short but is a " + hlaDataObject.getClass().getName());
    }

    private int readInt(DataElement hlaDataObject) {
        if (hlaDataObject instanceof HLAinteger32BE) {
            return ((HLAinteger32BE) hlaDataObject).getValue();
        }
        if (hlaDataObject instanceof HLAinteger32LE) {
            return ((HLAinteger32LE) hlaDataObject).getValue();
        }
        throw new UnsupportedOperationException("DataElement should be a unsigned short but is a " + hlaDataObject.getClass().getName());
    }

    private float readFloat(DataElement hlaDataObject) {
        if (hlaDataObject instanceof HLAfloat32LE) {
            return ((HLAfloat32LE) hlaDataObject).getValue();
        }
        if (hlaDataObject instanceof HLAfloat32BE) {
            return ((HLAfloat32BE) hlaDataObject).getValue();
        }
        throw new UnsupportedOperationException("DataElement should be a unsigned short but is a " + hlaDataObject.getClass().getName());
    }

    private double readDouble(DataElement hlaDataObject) {
        if (hlaDataObject instanceof HLAfloat64BE) {
            return ((HLAfloat64BE) hlaDataObject).getValue();
        }
        if (hlaDataObject instanceof HLAfloat64LE) {
            return ((HLAfloat64LE) hlaDataObject).getValue();
        }
        throw new UnsupportedOperationException("DataElement should be a unsigned short but is a " + hlaDataObject.getClass().getName());
    }

    private char readChar(DataElement hlaDataObject) {
        if (hlaDataObject instanceof HLAASCIIchar) {
            return (char) ((HLAASCIIchar) hlaDataObject).getValue();
        }
        if (hlaDataObject instanceof HLAunicodeChar) {
            return (char) ((HLAunicodeChar) hlaDataObject).getValue();
        }
        throw new UnsupportedOperationException("DataElement should be a unsigned short but is a " + hlaDataObject.getClass().getName());
    }

    private byte readByte(DataElement hlaDataObject) {
        if (hlaDataObject instanceof HLAoctet) {
            return ((HLAoctet) hlaDataObject).getValue();

        }
        if (hlaDataObject instanceof HLAbyte) {
            return ((HLAbyte) hlaDataObject).getValue();
        }
        throw new UnsupportedOperationException("DataElement should be a unsigned short but is a " + hlaDataObject.getClass().getName());
    }

    public Object decodeSimpleDataType(SimpleDataType fomObjectDataType, String migObjectStructureName, DataElement hlaDataElement) {
        return null;
    }

    public Object decodeVariantRecordDataType(VariantRecordDataType fomObjectDataType, String migObjectStructureName,
            DataElement hlaDataElement) {
        return null;
    }

    @Test
    public void testBuildHlaDataElement() throws Exception {
        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        parser = new FomParser();
        parser.parse(new File("RPR2-D20_2010.xml"));
        LOG.info("FOM parsed");

        IDataType dataType = parser.getDataTypes("SpatialStaticStruct");
        DataElement hla = parser.buildHlaDataElement(dataType);
        assertNotNull(hla);
        assertTrue(hla instanceof HLAfixedRecord);
        HLAfixedRecord sss = (HLAfixedRecord) hla;
        assertEquals(3, sss.size());
        assertTrue(sss.get(0) instanceof HLAfixedRecord);
        assertTrue(sss.get(1) instanceof HLAoctet);
        assertTrue(sss.get(2) instanceof HLAfixedRecord);
        HLAfixedRecord wls = (HLAfixedRecord) sss.get(0);
        assertEquals(3, wls.size());
        assertTrue(wls.get(0) instanceof HLAfloat64BE);
        assertTrue(wls.get(1) instanceof HLAfloat64BE);
        assertTrue(wls.get(2) instanceof HLAfloat64BE);
        HLAfloat64BE posX = (HLAfloat64BE) wls.get(0);
        assertEquals(0.0, posX.getValue(), 0.0);
        HLAfixedRecord os = (HLAfixedRecord) sss.get(2);
        assertEquals(3, os.size());
        assertTrue(os.get(0) instanceof HLAfloat32BE);
        assertTrue(os.get(1) instanceof HLAfloat32BE);
        assertTrue(os.get(2) instanceof HLAfloat32BE);
    }

}
