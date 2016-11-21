package migway.demo.dds;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

import migway.demo.dds.model.Platform;

import org.omg.dds.core.InstanceHandle;
import org.omg.dds.core.ServiceEnvironment;
import org.omg.dds.domain.DomainParticipant;
import org.omg.dds.domain.DomainParticipantFactory;
import org.omg.dds.pub.DataWriter;
import org.omg.dds.pub.Publisher;
import org.omg.dds.topic.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dds.gva.LDM.ResourceType_E;
import dds.gva.LDM.tag_CommonCapability_T;
import dds.gva.LDM.tag_ModeCapability_T;
import dds.gva.LDM.tag_SoftwareVersionDescriptor_T;
import dds.gva.LDM.tag_VsiTime_T;
import dds.gva.LDM.navigation.NavAttitudeParameterName_E;
import dds.gva.LDM.navigation.NavAttitudeState_T;
import dds.gva.LDM.navigation.NavErrorType_E;
import dds.gva.LDM.navigation.NavPositionParameterName_E;
import dds.gva.LDM.navigation.NavPositionState_T;
import dds.gva.LDM.navigation.tag_NavErrorDataType_T;
import dds.gva.LDM.platform.PlatformCapability_T;

public class DdsManager {
    private static Logger LOGGER = LoggerFactory.getLogger(DdsManager.class);
    private DomainParticipant dp;
    private DataWriter<PlatformCapability_T> platformWriter;
    private DataWriter<NavPositionState_T> positionWriter;
    private DataWriter<NavAttitudeState_T> attitudeWriter;
    private List<Platform> platforms;
    private PlatformCapability_T platformInstance;
    private NavPositionState_T platformPositionInstance;
    private NavAttitudeState_T platformAttitudeInstance;
    private InstanceHandle platformInstanceHandle;
    private InstanceHandle positionInstanceHandle;
    private InstanceHandle attitudeInstanceHandle;

    /**
     * initialize connection to DDS bus
     * 
     */
    public DdsManager() {
        platforms = new ArrayList<Platform>();

        // Connection to DDS. After that, dp is initialized
        connect();

        // The topic that will be exchanged
        Topic<PlatformCapability_T> platformTopic = dp.createTopic("Platform", PlatformCapability_T.class);
        Topic<NavPositionState_T> positionTopic = dp.createTopic("Position", NavPositionState_T.class);
        Topic<NavAttitudeState_T> attitudeTopic = dp.createTopic("Attitude", NavAttitudeState_T.class);

        // The publisher
        Publisher pub = dp.createPublisher();

        // The writers
        platformWriter = pub.createDataWriter(platformTopic);
        positionWriter = pub.createDataWriter(positionTopic);
        attitudeWriter = pub.createDataWriter(attitudeTopic);
    }

    /**
     * Connect to the DDS Bus. Initialize the dp (domain participant)
     */
    private void connect() {
        System.setProperty(ServiceEnvironment.IMPLEMENTATION_CLASS_NAME_PROPERTY, "org.opensplice.dds.core.OsplServiceEnvironment");
        ServiceEnvironment env = ServiceEnvironment.createInstance(null);
        DomainParticipantFactory factory = DomainParticipantFactory.getInstance(env);

        dp = factory.createParticipant(0);

    }

    /**
     * Register a new platform on DDS. This Platform object will be converted
     * into 3 DDS Topics instances: handles and Object instances are available
     * as private fields
     * 
     * @param platform
     */
    public void addPlatform(Platform platform) {
        // Do this only if the platform is new
        if (!platforms.contains(platform)) {
            // save the Business object in a list
            platforms.add(platform);

            // Get the id of the object
            int id = platform.hashCode() + (int)(Math.random() *10000);
            ResourceType_E idType = ResourceType_E.RESOURCE_TYPE__NAV_UNIT;

            // Set couple of informations needed for Topic creation
            tag_NavErrorDataType_T noPositionError = new tag_NavErrorDataType_T(0, NavErrorType_E.NAV_ERROR_TYPE__GDOP);
            double utcTimeOfDay = new Date().getTime();
            double distanceTravelled = platform.getDistanceTravelled();
            String date = DateFormat.getInstance().format(new Date());
            // Reduce date string to 20 characters
            date = (date.length() > 20 ? date.substring(0, 20) : date);
            // date = "";
            tag_VsiTime_T timeOfDataGeneration = new tag_VsiTime_T(System.currentTimeMillis() / 1000, 0);
            double pitchRate = 0.1;
            double yawRate = 0.1;
            double rollRate = 0.1;

            // By default, we set all parameter as changed
            final NavAttitudeParameterName_E[] ATTITUDE_CHANGED_EVERYTHING = new NavAttitudeParameterName_E[] {
                    NavAttitudeParameterName_E.NAV_ATTITUDE_PARAMETER_NAME__PITCH,
                    NavAttitudeParameterName_E.NAV_ATTITUDE_PARAMETER_NAME__PITCH_RATE,
                    NavAttitudeParameterName_E.NAV_ATTITUDE_PARAMETER_NAME__ROLL,
                    NavAttitudeParameterName_E.NAV_ATTITUDE_PARAMETER_NAME__ROLL_RATE,
                    NavAttitudeParameterName_E.NAV_ATTITUDE_PARAMETER_NAME__YAW,
                    NavAttitudeParameterName_E.NAV_ATTITUDE_PARAMETER_NAME__YAW_RATE };

            final NavPositionParameterName_E[] POSITION_CHANGED_EVERYTHING = new NavPositionParameterName_E[] {
                    NavPositionParameterName_E.NAV_POSITION_PARAMETER_NAME__DATE,
                    NavPositionParameterName_E.NAV_POSITION_PARAMETER_NAME__DISTANCE_TRAVELLED,
                    NavPositionParameterName_E.NAV_POSITION_PARAMETER_NAME__HEIGHT,
                    NavPositionParameterName_E.NAV_POSITION_PARAMETER_NAME__HEIGHT_ERROR,
                    NavPositionParameterName_E.NAV_POSITION_PARAMETER_NAME__LATITUDE,
                    NavPositionParameterName_E.NAV_POSITION_PARAMETER_NAME__LONGITUDE,
                    NavPositionParameterName_E.NAV_POSITION_PARAMETER_NAME__POSITION_ERROR,
                    NavPositionParameterName_E.NAV_POSITION_PARAMETER_NAME__UTC_TIME_OF_DAY };

            // Creation of the topic instances
            platformInstance = new PlatformCapability_T(id, idType, new tag_CommonCapability_T("Sebi", "DDSSimulated",
                    "A DDS simulated vehicule", "42", "none", "MIGWAY", "",
                    new tag_SoftwareVersionDescriptor_T[] { new tag_SoftwareVersionDescriptor_T("core", "0.1") }, new tag_ModeCapability_T(
                            true, true, true, true, true), timeOfDataGeneration));

            platformPositionInstance = new NavPositionState_T(id, idType, platform.getLongitude(), platform.getLatitude(),
                    platform.getHeight(), noPositionError, noPositionError, utcTimeOfDay, date, distanceTravelled,
                    POSITION_CHANGED_EVERYTHING, timeOfDataGeneration);

            platformAttitudeInstance = new NavAttitudeState_T(platform.getPitch(), platform.getRoll(), platform.getYaw(), pitchRate,
                    rollRate, yawRate, id, idType, ATTITUDE_CHANGED_EVERYTHING, timeOfDataGeneration);

            // Try to register and write those instance to DDS
            try {
                platformInstanceHandle = platformWriter.registerInstance(platformInstance);
                positionInstanceHandle = positionWriter.registerInstance(platformPositionInstance);
                attitudeInstanceHandle = attitudeWriter.registerInstance(platformAttitudeInstance);

                // If register fails, handle is nil
                if (!platformInstanceHandle.isNil()) {
                    platformWriter.write(platformInstance, platformInstanceHandle);
                    LOGGER.error("Write platformInstance " + platformInstanceHandle);
                }

                if (!positionInstanceHandle.isNil()) {
                    positionWriter.write(platformPositionInstance, positionInstanceHandle);
                }
                if (!attitudeInstanceHandle.isNil()) {
                    attitudeWriter.write(platformAttitudeInstance, attitudeInstanceHandle);
                }

            } catch (TimeoutException e) {
                // TODO don't ignore timeout. Should try again
                e.printStackTrace();
            }
        }

    }

    /**
     * A platform has been changed. Reflect this modification to DDS
     * 
     * @param platform
     * @throws IllegalArgumentException
     */
    public void reflectPlatform(Platform platform) throws IllegalArgumentException {
        // Get generation time
        long ms = System.currentTimeMillis();
        tag_VsiTime_T timeOfDataGeneration = new tag_VsiTime_T(ms / 1000, (int)(ms % 1000) * 1000 );

        if (!platforms.contains(platform)) {
            throw new IllegalArgumentException("this platform " + platform.toString() + " is not registered");
        }

        // List of parameter of the topic that has changed
        List<NavAttitudeParameterName_E> attitudeChanged = new ArrayList<NavAttitudeParameterName_E>();
        List<NavPositionParameterName_E> positionChanged = new ArrayList<NavPositionParameterName_E>();
        
        // Find which parameter has changed
        if (platform.getPitch() != platformAttitudeInstance.pitch) {
            platformAttitudeInstance.pitch = platform.getPitch();
            attitudeChanged.add(NavAttitudeParameterName_E.NAV_ATTITUDE_PARAMETER_NAME__PITCH);
//            attitudeChanged.add(NavAttitudeParameterName_E.NAV_ATTITUDE_PARAMETER_NAME__PITCH_RATE);
        }
        if (platform.getRoll() != platformAttitudeInstance.roll) {
            platformAttitudeInstance.roll = platform.getRoll();
            attitudeChanged.add(NavAttitudeParameterName_E.NAV_ATTITUDE_PARAMETER_NAME__ROLL);
//            attitudeChanged.add(NavAttitudeParameterName_E.NAV_ATTITUDE_PARAMETER_NAME__ROLL_RATE);
        }
        if (platform.getYaw() != platformAttitudeInstance.yaw) {
            platformAttitudeInstance.yaw = platform.getYaw();
            attitudeChanged.add(NavAttitudeParameterName_E.NAV_ATTITUDE_PARAMETER_NAME__YAW);
//            attitudeChanged.add(NavAttitudeParameterName_E.NAV_ATTITUDE_PARAMETER_NAME__YAW_RATE);
        }

        if (platform.getLatitude() != platformPositionInstance.latitude) {
            platformPositionInstance.latitude = platform.getLatitude();
            positionChanged.add(NavPositionParameterName_E.NAV_POSITION_PARAMETER_NAME__LATITUDE);
        }
        if (platform.getLongitude() != platformPositionInstance.longitude) {
            platformPositionInstance.longitude = platform.getLongitude();
            positionChanged.add(NavPositionParameterName_E.NAV_POSITION_PARAMETER_NAME__LONGITUDE);
        }
        if (platform.getHeight() != platformPositionInstance.height) {
            platformPositionInstance.height = platform.getHeight();
            positionChanged.add(NavPositionParameterName_E.NAV_POSITION_PARAMETER_NAME__HEIGHT);
        }

        // Try to write the updated topic
        try {
            // We don't update PlatformCapability. Nothing ever change
            // if (!attitudeChanged.isEmpty() || !positionChanged.isEmpty()) {
            // System.err.println("Reflect platform " + platform.toString());
            // if (!platformInstanceHandle.isNil())
            // platformWriter.write(platformInstance, platformInstanceHandle);
            // }

            // update NavPositionState if something changed on it
            if (!positionChanged.isEmpty()) {
                if (!positionInstanceHandle.isNil()) {
                    NavPositionParameterName_E[] attitudeChangedArray = new NavPositionParameterName_E[positionChanged.size()];
                    platformPositionInstance.recentlyChanged = positionChanged.toArray(attitudeChangedArray);
                    platformPositionInstance.timeOfDataGeneration = timeOfDataGeneration;
                    positionWriter.write(platformPositionInstance, positionInstanceHandle);
                }
            }
            // update NavAttitudeState if something changed on it
            if (!attitudeChanged.isEmpty()) {
                if (!attitudeInstanceHandle.isNil())  {
                    NavAttitudeParameterName_E[] attitudeChangedArray = new NavAttitudeParameterName_E[attitudeChanged.size()];
                    platformAttitudeInstance.recentlyChanged = attitudeChanged.toArray(attitudeChangedArray);
                    platformAttitudeInstance.timeOfDataGeneration = timeOfDataGeneration;
                    attitudeWriter.write(platformAttitudeInstance, attitudeInstanceHandle);
                }
            }
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // if a write fail (mainly when a parameter is not conform to the
            // IDL Topic definition)
            e.printStackTrace();
        }
    }

    /**
     * Stop to write to the DDS bus
     */
    public void stop() {
    }

    /**
     * Start to write to the DDS bus
     */
    public void start() {
    }

    /**
     * Close all opened resources on the DDS bus
     */
    @Override
    protected void finalize() throws Throwable {

        LOGGER.debug("DDS Manager Finalization in progress...");
        // Get the publisher, so that we can close it
        Publisher pub = platformWriter.getParent();
        platformWriter.unregisterInstance(platformInstanceHandle);
        positionWriter.unregisterInstance(positionInstanceHandle);
        attitudeWriter.unregisterInstance(attitudeInstanceHandle);
        platformWriter.close();
        positionWriter.close();
        attitudeWriter.close();
        pub.close();
        dp.close();

        // Clear all instances
        platforms.clear();
        LOGGER.debug("DDS Manager finalized");
        super.finalize();
    }

    public void reflectPlatformCapability(Platform platform) {
        LOGGER.debug("Sending platform update, time = {}, {}", platformInstance.coreCapability.timeOfDataGeneration.seconds, platformInstance.coreCapability.timeOfDataGeneration.nanoseconds);
        try {
            platformWriter.write(platformInstance, platformInstanceHandle);
        } catch (TimeoutException e) {
            // TODO timeout = retry
            e.printStackTrace();
        }        
    }

}
