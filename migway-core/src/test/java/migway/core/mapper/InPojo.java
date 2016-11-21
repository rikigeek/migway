package migway.core.mapper;

import migway.core.mapper.OutPojo.OutStruct;

public class InPojo {
    public InPojo() {
    };

    public InStruct inA;
    public int inB;
    public int inC;
    public OutStruct inD;
    public OutPojo inE;
    public InStruct[] inArrayA;
    public int[] inArrayB;

    public static class InStruct {
        public int sA;
        public int sB;
        public InStruct sC;
        public int sD;
        public String[] sArrayA;
        public int[] sArrayB;
    }

}
