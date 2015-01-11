package com.hackthedrive.siride;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, TextToSpeech.OnInitListener{

    protected static final int REQUEST_OK = 1;

    // Log tag of the class
    private static final String TAG = "MainActivity Log";
    private TextToSpeech tts;

    // List Adapters
    ArrayAdapter adapter;
    ListView listView;
    ArrayList displayList;

    // Controllers
    CommandParser parser;
    APIController api;
    ActionController control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button1).setOnClickListener(this);
        tts = new TextToSpeech(this, this);
        listView = (ListView)findViewById(R.id.listview);
        displayList = new ArrayList();
        adapter = new ArrayAdapter<String>(this, R.layout.customlist, displayList);
        listView.setAdapter(adapter);

        parser = new CommandParser();
        api = new APIController("WBY1Z4C55EV273078", this);
        control = new ActionController(api, this);
        api.setActionController(control);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.US);

        } else {
            Log.e("TTS", "Initialization failed");
        }

    }

    private void say(String text){
        tts.speak(text, TextToSpeech.QUEUE_ADD, null);
    }


    @Override
    public void onClick(View v) {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
        try {
            startActivityForResult(i, REQUEST_OK);
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_OK  && resultCode==RESULT_OK) {
            ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String command = thingsYouSaid.get(0);

            // Remove prompt
            ((TextView)findViewById(R.id.text1)).setText("");

            // Displays what you said
            display("You", command);

            // Cleaning the command
            String[] clean = command.trim().split(" ");
            for(String s: clean){s.toLowerCase();}

            // Getting the command set from parser
            CommandParser.CommandSet cmd = parser.parseCommand(Arrays.asList(clean));

            // Execute the appropriate command set
            control.execute(cmd);
        }
    }

    public void display(String person, String text){
        String s = person+": "+text;
        displayList.add(0, s);
        adapter.notifyDataSetChanged();
        say(text);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
}
