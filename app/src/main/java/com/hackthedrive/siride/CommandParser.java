package com.hackthedrive.siride;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

public class CommandParser {
    public static final String[] function ={"check", "checks", "gets", "get", "queries", "query"};
    public static final String[] param=
            {"battery", "batteries", "fuel", "fuels", "door", "doors", "lock", "locks", "window",
                    "windows", "trunk", "trunks", "odometer", "odometers", "distance", "distances",
                    "direction", "directions", "location", "locations", "last"};
    private static final String[] qualifier={"status", "statuses", "range", "ranges", "distance",
                    "distances"};

    private Set<String> fSet = new HashSet<String>();
    private Set<String> pSet = new HashSet<String>();
    private Set<String> qSet = new HashSet<String>();

    public CommandParser(){
        fSet.addAll(Arrays.asList(function));
        pSet.addAll(Arrays.asList(param));
        qSet.addAll(Arrays.asList(qualifier));
    }

    static final class CommandSet{
        private String function;
        private String param;
        private List<String> qualifier;

        public CommandSet(){qualifier=new ArrayList<String>();}

        public String getFunction(){return function;}
        public String getParam(){return param;}
        public List<String> getQualifier(){return qualifier;}
    }


    public CommandSet parseCommand(List<String> sentence){
        CommandSet c = new CommandSet();
        for(String word: sentence){
            if(fSet.contains(word)){c.function = word;}
            else if(pSet.contains(word)){c.param = word;}
            else if(qSet.contains(word)){c.qualifier.add(word);}
        }
        Log.v("Command Parsed", "Function: "+c.function+", Param: "+c.param+", Qualifier: "+c.qualifier);
        return c;
    }
}