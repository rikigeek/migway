package LDM.navigation;

import LDM.ResourceType_E;
import LDM.VsiTime_T;

public class NavAttitudeState_T {
    public double pitch;
    public double roll;
    public double yaw;
    public double pitchRate;
    public double rollRate;
    public double yawRate;
    public int navElementId;
    public ResourceType_E navElementIdType;
    public NavAttitudeParameterName_E[] recentlyChanged = new NavAttitudeParameterName_E[10];
    public VsiTime_T timeOfDataGeneration;

}
