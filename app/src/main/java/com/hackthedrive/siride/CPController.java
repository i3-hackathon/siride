package com.hackthedrive.siride;

/**
 * Created by Eswar on 1/11/2015.
 */

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.*;
import java.util.List;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class CPController
{
    private String xmlBody;
    private String action;
    //private ObjectMapper mapper;
    private MainActivity main;
    private String defAction = "getPublicStations";
    private String defBody =
                    "		<ns2:getPublicStations xmlns:ns2='http://www.example.org/coulombservices/'>\n" +
                    "			<searchQuery>\n" +
                    "				<Proximity>10</Proximity>\n" +
                    "				<proximityUnit>M</proximityUnit>\n" +
                    "				<Geo>\n" +
                    "					<Lat>37.775374</Lat>\n" +
                    "					<Long>-122.400174</Long>\n" +
                    "				</Geo>\n" +
                    "			</searchQuery>\n" +
                    "		</ns2:getPublicStations>\n";


    public CPController(String action, String xmlBody, MainActivity m) {
        //mapper = new ObjectMapper();
        if (action == null)
            action = defAction;
        if (xmlBody == null) {
            xmlBody = defBody;
        }
        main = m;
    }

    public String getCpData() throws MalformedURLException, IOException {

        //Code to make a webservice HTTP request
        String responseString = "";
        String outputString = "";
        String user = "b207195d0b1684270db5aeae7970408c5179ce9f5a4dc1366937247";
        String pass = "167fb3e18980d8622f6a19fbbda3e01d";

        //String wsURL = "http://www.deeptraining.com/webservices/weather.asmx";
        String wsURL = "https://webservices.chargepoint.com/webservices/chargepoint/services/4.1";
        URL url = new URL(wsURL);
        URLConnection connection = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection)connection;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        String xmlInput =
                "  <soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://litwinconsulting.com/webservices/\">\n" +
                        "	<soap:Header>\n" +
                        "		<wsse:Security xmlns:wsse='http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd' soap:mustUnderstand='1'>\n" +
                        "			<wsse:UsernameToken xmlns:wsu='http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd' wsu:Id='UsernameToken-261'>\n" +
                        "				<wsse:Username>" + user + "</wsse:Username>\n" +
                        "				<wsse:Password Type='http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText'>" + pass + "</wsse:Password>\n" +
                        "			</wsse:UsernameToken>\n" +
                        "		</wsse:Security>\n" +
                        "   </soap:Header>\n" +
                        "   <soap:Body>\n" +
                        xmlBody +
                        "   </soap:Body>\n" +
                        "  </soap:Envelope>\n";

        byte[] buffer = new byte[xmlInput.length()];
        buffer = xmlInput.getBytes();
        bout.write(buffer);
        byte[] b = bout.toByteArray();

        //String SOAPAction = "http://litwinconsulting.com/webservices/GetWeather";
        String SOAPAction = "urn:provider/interface/chargepointservices/"+action;

        // Set the appropriate HTTP parameters.
        httpConn.setRequestProperty("Content-Length",
                String.valueOf(b.length));
        httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        httpConn.setRequestProperty("SOAPAction", SOAPAction);
        httpConn.setRequestMethod("POST");
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        OutputStream out = httpConn.getOutputStream();
        //Write the content of the request to the outputstream of the HTTP Connection.
        out.write(b);
        out.close();
        //Ready with sending the request.

        //Read the response.
        InputStreamReader isr =
                new InputStreamReader(httpConn.getInputStream());
        BufferedReader in = new BufferedReader(isr);

        //Write the SOAP message response to a String.
        while ((responseString = in.readLine()) != null) {
            outputString = outputString + responseString;
        }
        return outputString;

    }
}
