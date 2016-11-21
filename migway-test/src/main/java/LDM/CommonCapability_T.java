package LDM;

public class CommonCapability_T {
    public String manufacturer;
    public String productName;
    public String description;
    public String serialNumber;
    public String issue;
    public String modStrike;
    public String natoStockNumber;
    public SoftwareVersionDescriptor_T[]  softwareVersions = new SoftwareVersionDescriptor_T[20];
    public ModeCapability_T supportedModes;
    public VsiTime_T timeOfDataGeneration;
}
