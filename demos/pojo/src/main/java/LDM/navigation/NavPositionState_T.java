package LDM.navigation;

import LDM.ResourceType_E;
import LDM.VsiTime_T;

public class NavPositionState_T {

    public int navElementId;
    public ResourceType_E navElementIdType;
    public double longitude;
    public double latitude;
    public double height;
    public NavErrorDataType_T positionError;
    public NavErrorDataType_T heightError;
    public double utcTimeOfDay;
    public String date;
    public double distanceTravelled;
    public NavPositionParameterName_E recentlyChanged[] = new NavPositionParameterName_E[10];
    public VsiTime_T timeOfDataGeneration;
}
