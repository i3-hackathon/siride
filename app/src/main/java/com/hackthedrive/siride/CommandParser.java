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
            {"battery", "batteries", "fuel", "fuels", "door", "doors", "window",
                    "windows", "trunk", "trunks", "odometer", "odometers", "distance", "distances",
                    "direction", "directions", "location", "locations", "last", "travelled", "travel",
                    "traveled", "trip", "address", "addresses", "headlight", "headlights", "light",
                    "lights", "like", "lights", "vehicle", "vehicles", "car", "cars"};
    public static final String[] function1 ={"send", "set", "lock", "locks", "honk", "horn"};
    private static final String[] qualifier={"status", "statuses", "range", "ranges", "distance",
                    "distances", "miles", "kilometer", "remaining", "remain", "kilometers",
                    "percent", "percents", "percentage", "front", "back","rear","driver", "passenger",
                    "one", "two", "three", "four", "five"};

    private Set<String> fSet = new HashSet<String>();
    private Set<String> pSet = new HashSet<String>();
    private Set<String> qSet = new HashSet<String>();

    public CommandParser(){
        fSet.addAll(Arrays.asList(function));
        fSet.addAll(Arrays.asList(function1));
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
            String clean = word.toLowerCase();
            if(qSet.contains(clean)){c.qualifier.add(clean); continue;}
            if(pSet.contains(clean)){c.param = clean; continue;}
            if(fSet.contains(clean)){c.function = clean; continue;}

        }
        if (c.function == null){
            c.function="check";//Default function
        }
        Log.v("Command Parsed", "Function: "+c.function+", Param: "+c.param+", Qualifier: "+c.qualifier);
        return c;
    }
}
