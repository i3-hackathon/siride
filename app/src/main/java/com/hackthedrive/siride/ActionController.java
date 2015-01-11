package com.hackthedrive.siride;

import com.hackthedrive.siride.CommandParser.CommandSet;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by Leo on 1/10/15.
 */
public class ActionController {

    public Map<String, Action> actionMap;
    private APIController api;
    MainActivity main;

    public enum Action{
        CHECK_BATTERY, CHECK_FUEL, CHECK_DOOR, CHECK_LOCK, CHECK_WINDOW, CHECK_TRUNK, CHECK_ODOMETER
        ,CHECK_LOCATION, CHECK_LAST;

        CommandSet set;
        public Boolean hasQual(String q){return set.getQualifier().contains(q);}
    }

    public ActionController(APIController api, MainActivity main){
        actionMap = new HashMap<String, Action>();
        this.api = api;
        this.main = main;

        String[] tmp1 = {"battery", "batteries"};
        for(String c: CommandParser.function){
            for(String s: tmp1){
                actionMap.put(c+"-"+s, Action.CHECK_BATTERY);
            }
        }

        String[] tmp2 = {"fuel", "fuels"};
        for(String c: CommandParser.function){
            for(String s: tmp2){
                actionMap.put(c+"-"+s, Action.CHECK_FUEL);
            }
        }

        String[] tmp3 = {"door", "doors"};
        for(String c: CommandParser.function){
            for(String s: tmp3){
                actionMap.put(c+"-"+s, Action.CHECK_DOOR);
            }
        }

        String[] tmp4 = {"lock", "locks"};
        for(String c: CommandParser.function){
            for(String s: tmp4){
                actionMap.put(c+"-"+s, Action.CHECK_LOCK);
            }
        }

        String[] tmp5 = {"window", "windows"};
        for(String c: CommandParser.function){
            for(String s: tmp5){
                actionMap.put(c+"-"+s, Action.CHECK_WINDOW);
            }
        }

        String[] tmp6 = {"trunk", "trunks"};
        for(String c: CommandParser.function){
            for(String s: tmp6){
                actionMap.put(c+"-"+s, Action.CHECK_TRUNK);
            }
        }

        String[] tmp7 = {"odometer", "odometers", "distance"};
        for(String c: CommandParser.function){
            for(String s: tmp7){
                actionMap.put(c+"-"+s, Action.CHECK_ODOMETER);
            }
        }

        String[] tmp8 = {"direction", "directions", "location", "locations"};
        for(String c: CommandParser.function){
            for(String s: tmp8){
                actionMap.put(c+"-"+s, Action.CHECK_LOCATION);
            }
        }

        String[] tmp9 = {"last"};
        for(String c: CommandParser.function){
            for(String s: tmp9){
                actionMap.put(c+"-"+s, Action.CHECK_LAST);
            }
        }
    }

    public Action determineAction(CommandSet c){
        Action act = actionMap.get(c.getFunction()+"-"+c.getParam());
        act.set=c;
        return act;
    }

    public void execute(CommandSet c){
        Action action = determineAction(c);
        switch (action){
            case CHECK_BATTERY:
                api.getCall("battery", action);
                break;
            default:
                main.display("Siride", "Invalid Command");
                break;
        }
    }

    public void process(Action action, MainActivity main, HashMap<String, Object> map){
        switch (action){
            case CHECK_BATTERY:
                if(action.hasQual("status")||action.hasQual("statuses")){
                    main.display("Siride",
                            "The battery charging status is "+valOf(map, "chargingStatus")+".");
                }
                else if((action.hasQual("range")||action.hasQual("ranges")||
                        action.hasQual("distance")||action.hasQual("distances"))&&action.hasQual("miles")){
                    main.display("Siride", "The car can travel "+valOf(map, "remainingRangeMi")+" more miles on battery.");

                }
                else if((action.hasQual("range")||action.hasQual("ranges")||
                        action.hasQual("distance")||action.hasQual("distances"))&&action.hasQual("kilometers")){
                    main.display("Siride", "The car can travel "+valOf(map, "remainingRangeKm")+" more kilometers on battery.");

                }
                else if(action.hasQual("range")||action.hasQual("ranges")||
                        action.hasQual("distance")||action.hasQual("distances")){
                    main.display("Siride", "The car can travel "+valOf(map, "remainingRangeMi")+" more miles on battery.");

                }
                else{
                    main.display("Siride", "The battery charging status is "+valOf(map, "chargingStatus")+".\n"+
                            valOf(map, "remainingPercent")+" battery percent remaining.\n"+
                            "The car can travel "+valOf(map, "remainingRangeMi")+" more miles on battery.");
                }

        }
    }

    private String valOf(HashMap<String, Object> map, String key){
        return String.valueOf(map.get(key));
    }
}
