package migway.core.mapper;

import migway.core.mapper.InPojo.InStruct;

public class OutPojo implements Cloneable {
    public OutPojo() {
    }

    public OutStruct outA;
    public int outB;
    public int outC;
    public InStruct outD;
    public InPojo outE;
    public OutStruct[] outArrayA;
    public int[] outArrayB;
    
    public static class OutStruct {
        public int sA;
        public int sB;
        public OutStruct sC;
        public int sD;
        public String[] sArrayA;
        public int[] sArrayB;
    }
    
    public OutPojo clone() throws CloneNotSupportedException {
        return (OutPojo)super.clone();
    }
}
