package com.hackthedrive.siride;

import com.hackthedrive.siride.CommandParser.CommandSet;
import com.loopj.android.http.RequestParams;

import android.util.Log;
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
        CHECK_BATTERY, CHECK_FUEL, CHECK_DOOR, LOCK, CHECK_WINDOW, CHECK_TRUNK, CHECK_ODOMETER
        ,CHECK_LOCATION, CHECK_LAST, SEND_ADDRESS, HEAD_LIGHT, INVALID, HORN;

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

        String[] tmp7 = {"odometer", "odometers", "distance", "total", "traveled", "travelled", "travel"};
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

        String[] tmp9 = {"last", "trip"};
        for(String c: CommandParser.function){
            for(String s: tmp9){
                actionMap.put(c+"-"+s, Action.CHECK_LAST);
            }
        }

        actionMap.put("send-address",Action.SEND_ADDRESS);
        actionMap.put("send-addresses",Action.SEND_ADDRESS);

        actionMap.put("set-like", Action.HEAD_LIGHT);
        actionMap.put("set-likes",Action.HEAD_LIGHT);
        actionMap.put("set-light", Action.HEAD_LIGHT);
        actionMap.put("set-lights",Action.HEAD_LIGHT);
        actionMap.put("set-headlight", Action.HEAD_LIGHT);
        actionMap.put("set-headlights",Action.HEAD_LIGHT);
        actionMap.put("lock-null", Action.LOCK);
        actionMap.put("locks-null", Action.LOCK);
        actionMap.put("lock-door", Action.LOCK);
        actionMap.put("lock-doors", Action.LOCK);
        actionMap.put("lock-vehicle", Action.LOCK);
        actionMap.put("locks-vehicle", Action.LOCK);
        actionMap.put("lock-car", Action.LOCK);
        actionMap.put("honk-null", Action.HORN);
        actionMap.put("honk-car", Action.HORN);
        actionMap.put("honk-vehicle", Action.HORN);
        actionMap.put("horn-null", Action.HORN);
        actionMap.put("horn-car", Action.HORN);
        actionMap.put("horn-vehicle", Action.HORN);
    }

    public Action determineAction(CommandSet c){
        Action act = actionMap.get(c.getFunction()+"-"+c.getParam());
        if(c!=null && act!=null) {
            act.set = c;
            return act;
        }
        else
        {
           return Action.INVALID;
        }
    }

    public void execute(CommandSet c){
        String count;

        Action action = determineAction(c);
        if(c==null || action==Action.INVALID){
            main.display("Siride", "Invalid Command");
            return;
        }
        switch (action){
            case CHECK_BATTERY:
                api.getCall("battery", action);
                break;
            case CHECK_FUEL:
                api.getCall("fuel", action);
                break;
            case CHECK_DOOR:
                api.getCall("door", action);
                break;
            case CHECK_WINDOW:
                api.getCall("window", action);
                break;
            case CHECK_TRUNK:
                api.getCall("trunk", action);
                break;
            case CHECK_ODOMETER:
                api.getCall("odometer", action);
                break;
            case CHECK_LOCATION:
                api.getCall("location", action);
                break;
            case CHECK_LAST:
                api.getCall("lastTrip", action);
                break;
            case SEND_ADDRESS:
                api.postAddress("Navdy HQ", "37.773550", "-122.403309");
                break;
            case HEAD_LIGHT:
                count = "3";
                for(String s:action.set.getQualifier()){
                    if(getCount(s) > 0){
                        count = String.valueOf(getCount(s));
                        break;
                    }
                }
                api.postLight(count);
                break;
            case LOCK:
                api.postLock("aravind15");
                break;
            case HORN:
                count = "1";
                for(String s:action.set.getQualifier()){
                    if(getCount(s) > 0){
                        count = String.valueOf(getCount(s));
                        break;
                    }
                }
                api.postHorn("aravind15", count);
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
                else if(action.hasQual("range")||action.hasQual("ranges")||
                        action.hasQual("distance")||action.hasQual("distances")){
                    if(action.hasQual("kilometer")||action.hasQual("kilometers")) {
                        main.display("Siride", "The car can travel " + valOf(map, "remainingRangeMi") + " more miles on battery.");
                    }
                    else{
                        main.display("Siride", "The car can travel "+valOf(map, "remainingRangeMi")+" more miles on battery.");
                    }

                }
                else if(
                        action.hasQual("percent")||action.hasQual("percentage")||action.hasQual("remain")||action.hasQual("remaining"))
                {
                    main.display("Siride", valOf(map, "remainingPercent")+" battery percentage remaining.");
                }
                else{
                    main.display("Siride", "The battery charging status is "+valOf(map, "chargingStatus")+".\n"+
                            valOf(map, "remainingPercent")+" battery percentage remaining.\n"+
                            "The car can travel "+valOf(map, "remainingRangeMi")+" more miles on battery.");
                }
                break;

            case CHECK_FUEL:
                if(action.hasQual("remaining")||action.hasQual("remain")){
                    if(action.hasQual("liters")){
                        main.display("Siride", "The car has "+valOf(map, "remainingLiters")+" liters of gas remaining.");
                    }
                    else{
                        main.display("Siride", "The car has "+valOf(map, "remainingGallons")+" gallons of gas remaining.");
                    }
                }
                else if(action.hasQual("percent")|| action.hasQual("percentage")){
                    main.display("Siride", valOf(map, "remainingPercent")+" fuel percentage remaining.");
                }
                else if(action.hasQual("range")||action.hasQual("ranges")||action.hasQual("distance")||action.hasQual("distances")){
                    if(action.hasQual("kilometer")||action.hasQual("kilometers")){
                        main.display("Siride", "The car can travel "+valOf(map, "remainingRangeKm")+" more miles on fuel.");
                    }
                    else
                        main.display("Siride", "The car can travel "+valOf(map, "remainingRangeMi")+" more miles on fuel.");
                }
                else{
                    main.display("Siride", "The car has "+valOf(map, "remainingGallons")+" gallons of gas remaining.\n"+
                            valOf(map, "remainingPercent")+" fuel percentage remaining.\n"+
                            "The car can travel "+valOf(map, "remainingRangeMi")+" more miles on fuel.");
                }

            case CHECK_DOOR:
                if(action.hasQual("front")){
                    if(action.hasQual("driver")){
                        main.display("Siride", "The front driver door is "+openClose(Boolean.valueOf(valOf(map, "isDriverFrontOpen")))+".");
                    }
                    else if(action.hasQual("passenger")){
                        main.display("Siride", "The front passenger door is "+openClose(Boolean.valueOf(valOf(map, "isPassengerFrontOpen")))+".");
                    }
                    else{
                        main.display("Siride", "The front driver door is "+openClose(Boolean.valueOf(valOf(map, "isDriverFrontOpen")))+".\n"+
                                    "The front passenger door is "+openClose(Boolean.valueOf(valOf(map, "isPassengerFrontOpen")))+".");
                    }
                }
                else if(action.hasQual("rear")||action.hasQual("back")){
                    if(action.hasQual("driver")){
                        main.display("Siride", "The rear driver door is "+openClose(Boolean.valueOf(valOf(map, "isDriverRearOpen")))+".");
                    }
                    else if(action.hasQual("passenger")){
                        main.display("Siride", "The rear passenger door is "+openClose(Boolean.valueOf(valOf(map, "isPassengerRearOpen")))+".");
                    }
                    else{
                        main.display("Siride", "The rear driver door is "+openClose(Boolean.valueOf(valOf(map, "isDriverRearOpen")))+".\n"+
                                "The rear passenger door is "+openClose(Boolean.valueOf(valOf(map, "isPassengerRearOpen")))+".");

                    }
                }
                else if(action.hasQual("passenger")){
                    main.display("Siride", "The front passenger door is "+openClose(Boolean.valueOf(valOf(map, "isPassengerFrontOpen")))+".\n"+
                            "The rear passenger door is "+openClose(Boolean.valueOf(valOf(map, "isPassengerRearOpen")))+".");
                }
                else if(action.hasQual("driver")){
                    main.display("Siride", "The front driver door is "+openClose(Boolean.valueOf(valOf(map, "isDriverFrontOpen")))+".\n"+
                            "The rear driver door is "+openClose(Boolean.valueOf(valOf(map, "isDriverRearOpen")))+".");
                }
                else{
                    main.display("Siride", "The front driver door is "+openClose(Boolean.valueOf(valOf(map, "isDriverFrontOpen")))+".\n"+
                            "The front passenger door is "+openClose(Boolean.valueOf(valOf(map, "isPassengerFrontOpen")))+".\n"+
                            "The rear driver door is "+openClose(Boolean.valueOf(valOf(map, "isDriverRearOpen")))+".\n"+
                            "The rear passenger door is "+openClose(Boolean.valueOf(valOf(map, "isPassengerRearOpen")))+"."
                    );
                }
                break;

            case CHECK_WINDOW:
                if(action.hasQual("driver")){
                    main.display("Siride", "The driver window is "+openClose(Boolean.valueOf(valOf(map, "isDriverOpen")))+".");
                }
                else if(action.hasQual("passenger")){
                    main.display("Siride", "The passenger window is "+openClose(Boolean.valueOf(valOf(map, "isPassengerOpen")))+".");
                }
                else{
                        main.display("Siride", "The driver window is "+openClose(Boolean.valueOf(valOf(map, "isDriverOpen")))+".\n"+
                                "The passenger window is "+openClose(Boolean.valueOf(valOf(map, "isPassengerOpen")))+".");
                }
                break;

            case CHECK_TRUNK:
                if(action.hasQual("front")){
                    main.display("Siride", "The front trunk is "+openClose(Boolean.valueOf(valOf(map, "isFrontOpen")))+".");
                }
                else if(action.hasQual("back")|| action.hasQual("rear")){
                    main.display("Siride", "The rear trunk is "+openClose(Boolean.valueOf(valOf(map, "isRearOpen")))+".");
                }
                else{
                        main.display("Siride", "The front trunk is "+openClose(Boolean.valueOf(valOf(map, "isFrontOpen")))+".\n"+
                                "The rear trunk is "+openClose(Boolean.valueOf(valOf(map, "isRearOpen")))+".");
                }
                break;

            case CHECK_ODOMETER:
                if(action.hasQual("kilometer") || action.hasQual("kilometers")){
                    main.display("Siride", "You have travelled a total of "+valOf(map, "totalKm")+" kilometers.");
                }
                else{
                    main.display("Siride", "You have travelled a total of "+valOf(map, "totalMi")+" miles.");
                }
            break;

            case CHECK_LOCATION:
                main.display("Siride", "You are heading "+valOf(map, "heading")+" degrees north.\n"+
                             "Your coordinates are "+valOf(map, "lat")+" latitude and "+valOf(map, "lon")+" longitude.");
            break;

            case CHECK_LAST:
                if(action.hasQual("kilometers") || action.hasQual("kilometer")) {
                    main.display("Siride", "Your last trip was on "+valOf(map, "tripDate")+".\n"+
                    "You traveled "+valOf(map, "distanceTotalKm")+" kilometers, taking "+valOf(map, "durationMinutes")+" minutes.\n"+
                    "Your average speed is "+
                            String.valueOf(
                                    Double.valueOf(valOf(map, "distanceTotalKm"))/(Double.valueOf(valOf(map, "durationMinutes"))/60))+" km/hr.");
                }
                else{
                    main.display("Siride", "Your last trip was on "+valOf(map, "tripDate")+".\n"+
                            "You traveled "+valOf(map, "distanceTotalMi")+" miles, taking "+valOf(map, "durationMinutes")+" minutes.\n"+
                            "Your average speed was "+
                            String.valueOf(
                                    Math.ceil(Double.valueOf(valOf(map, "distanceTotalMi")) / (Double.valueOf(valOf(map, "durationMinutes")) / 60))+" miles/hr."));
                }
        }
    }

    private String valOf(HashMap<String, Object> map, String key){
        return String.valueOf(map.get(key));
    }

    private String openClose(boolean t){
        if(t){return "open";}
        else{return "closed";}
    }

    private int getCount(String s){
        if(s.equalsIgnoreCase("1")||s.equalsIgnoreCase("2")||s.equalsIgnoreCase("3")||s.equalsIgnoreCase("4")||s.equalsIgnoreCase("5")){
            return Integer.valueOf(s);
        }
        else if(s.equalsIgnoreCase("one")){
            return 1;
        }
        else if(s.equalsIgnoreCase("two")){
            return 2;
        }
        else if(s.equalsIgnoreCase("three")){
            return 3;
        }
        else if(s.equalsIgnoreCase("four")){
            return 4;
        }
        else if(s.equalsIgnoreCase("five")){
            return 5;
        }
        else{
            return 0;
        }
    }
}
