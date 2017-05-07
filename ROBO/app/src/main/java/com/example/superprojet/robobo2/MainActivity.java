package com.example.superprojet.robobo2;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.superprojet.robobo2.genome.RoboboGene;
import com.example.superprojet.robobo2.genome.RoboboDNA;
import com.example.superprojet.robobo2.genome.RoboboPopulation;
import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;

import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mytechia.robobo.framework.exception.ModuleNotFoundException;



import com.mytechia.robobo.framework.hri.speech.production.ISpeechProductionModule;
import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlModule;
import com.mytechia.robobo.framework.remote_control.remotemodule.Status;
import com.mytechia.robobo.framework.service.RoboboServiceHelper;
import com.mytechia.robobo.rob.BluetoothRobInterfaceModule;

import com.mytechia.robobo.rob.IRob;
import com.mytechia.robobo.rob.MoveMTMode;
import com.mytechia.robobo.rob.movement.IRobMovementModule;
import com.mytechia.robobo.rob.util.RoboboDeviceSelectionDialog;


import org.java_websocket.client.WebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;


public class MainActivity extends AppCompatActivity implements ITestListener {

    //ArrayList<String> myList = new ArrayList<>();
    static Boolean atMainview;

    public RoboboManager roboboManager;
    public RoboboApp app;

    public RoboboPopulation NSpop;
    public RoboboPopulation roboPop;
    public Boolean roboPopInit;
    public ArrayList<Integer> parent_list;
    private AlertDialog resetPopup;
    private AlertDialog presavePopup;
    private int basicPopSize = 10;
    
    /****/
    final Context context=this;

    /**************************Connection Bluetooth*************************************/

    private static final String TAG="RemoteControlActivity";
    public IRob rob;
    public IRobMovementModule move;

    public MainActivity() throws URISyntaxException {
        IRemoteControlModule a;

    }
    private RoboboServiceHelper roboboHelper;

    URI uri = new URI("ws://localhost:40404");
    Integer i = 1;
    private ProgressDialog waitDialog;

    private Button startBtn=null;

    private IRemoteControlModule remoteModule;

    private ISpeechProductionModule productionModule;
    private WebSocketClient ws ;

    public boolean onTouchEvent(MotionEvent event){

        Status s = new Status("TapNumber");
        s.putContents("Taps",i.toString());
        i = i+1;
        Log.d(TAG,s.toString());
        remoteModule.postStatus(s);

        return true;

    }//lala

    private void showRoboboDeviceSelectionDialog() {

        RoboboDeviceSelectionDialog dialog = new RoboboDeviceSelectionDialog();
        dialog.setListener(new RoboboDeviceSelectionDialog.Listener() {
            @Override
            public void roboboSelected(String roboboName) {

                final String roboboBluetoothName = roboboName;

                //start the framework in background
                AsyncTask<Void, Void, Void> launchRoboboService =
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                launchAndConnectRoboboService(roboboBluetoothName);
                                return null;
                            }
                        };
                launchRoboboService.execute();

            }

            @Override
            public void selectionCancelled() {

            }

            @Override
            public void bluetoothIsDisabled() {
                finish();
            }

        });
        dialog.show(getFragmentManager(),"BLUETOOTH-DIALOG");

    }

    private void launchAndConnectRoboboService(String roboboBluetoothName) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //wait to dialog shown during the startup of the framework and the bluetooth connection
                waitDialog = ProgressDialog.show(MainActivity.this,
                        "Connect","connect", true);
            }
        });


        //we use the RoboboServiceHelper class to manage the startup and binding
        //of the Robobo Manager service and Robobo modules
        roboboHelper = new RoboboServiceHelper(this, new RoboboServiceHelper.Listener() {
            @Override
            public void onRoboboManagerStarted(RoboboManager robobo) {

                //the robobo service and manager have been started up
                roboboManager = robobo;
                try {
                    rob = roboboManager.getModuleInstance(BluetoothRobInterfaceModule.class).getRobInterface();
                    move = roboboManager.getModuleInstance((IRobMovementModule.class));
                } catch (ModuleNotFoundException e) {
                    Log.d("RoboboApp", "erreur");
                    Log.e("ROBOBO-APP", "Module not found: "+e.getMessage());
                }
                app = new RoboboApp(rob, move, null);


                //dismiss the wait dialog
                waitDialog.dismiss();

                //start the "custom" robobo application
                // startRoboboApplication();

            }

            @Override
            public void onError(final String errorMsg) {

                final String error = errorMsg;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //dismiss the wait dialog
                        waitDialog.dismiss();

                        //show an error dialog


                    }
                });

            }

        });

        //start & bind the Robobo service
        Bundle options = new Bundle();
        options.putString(BluetoothRobInterfaceModule.ROBOBO_BT_NAME_OPTION, roboboBluetoothName);
        roboboHelper.bindRoboboService(options);

    }


    public void onThingsHappen(final String things) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

//                tv.setText(things);


            }
        });
    }

    /************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        atMainview = true;

        roboPop = new RoboboPopulation();
        roboPopInit = false;
        parent_list = new ArrayList<Integer>();
        NSpop = new RoboboPopulation();// maybe not needed here

        // generating popups
        // RESET
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.resetPopupContent)
                .setTitle(R.string.resetPopupTitle);
        builder.setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("MainActivity.onCreate", "rob = " + (rob==null?"null":"des trucs"));
                roboPop.init(basicPopSize, rob);
                NSpop.setPop(roboPop.getPop());
                displayBGen(findViewById(R.id.behavior_generator_reset));
                dialog.dismiss();
            }
        });
        resetPopup = builder.create();

        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setMessage(R.string.preSavePopupContent)
                .setTitle(R.string.presavePopupTitle);
        builder2.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        presavePopup = builder2.create();



        /*
        myList.add("tralala");
        myList.add("truc");
        myList.add("machin");
        myList.add("chouette");
        myList.add("chose");
        myList.add("bestiole");
        */
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        roboboHelper.unbindRoboboService();
    }

    @Override
    public void onBackPressed() {
        if(!atMainview) {
            setContentView(R.layout.activity_main);
            atMainview = true;
        } else {
            moveTaskToBack(true);
            //finish();
        }
        Log.d("Info", "Back button pressed");
    }

    public void displayBGen(View view){
        int pos = 0;

        setContentView(R.layout.behavior_generator);
        atMainview = false;

        LinearLayout parent = (LinearLayout)findViewById(R.id.behavior_generator_list);

        // check that parent has no children (remove all children otherwise)
        parent.removeAllViews();

        ViewGroup.LayoutParams params = parent.getLayoutParams();
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        params.height = size.y / 10;

        int counter;
        for (counter = 0; counter < roboPop.getPop().size(); counter++) {
        //for (counter = 0; counter < myList.size(); counter++) {
            View child = getLayoutInflater().inflate(R.layout.behavior_example, null);

            child.setId(pos);
            child.setLayoutParams(params);
            EditText editText = (EditText) child.findViewById(R.id.theTextField);
            editText.setText("Behavior " + (counter+1));
            pos++;

            parent.addView(child);
        }
    }

    public void onClickMain(View button) {

        switch (button.getId()) {
            case R.id.main_activity_goto_bg :
                if (!roboPopInit)
                {
                    //roboPopInit

                    Log.d("MainActivity.onCreate", "rob = " + (rob==null?"null":"des trucs"));
                    try{
                        roboPop.init(basicPopSize, rob);
                    }catch(java.util.ConcurrentModificationException e){
                        Log.d("init : ", "Exception de merde");
                    }
                    try{
                        NSpop.setPop(roboPop.getPop());
                    }catch(java.util.ConcurrentModificationException e){
                        Log.d("setpop : ", "Exception de merde");
                    }
                    roboPopInit = true;
                    Log.d("onClickMain", "initialize RoboPop");
                }
                displayBGen(button);
                break;
            case R.id.main_activity_run_test :
                /*Thread t = new Thread(app);
                t.start();*/
                app.run();
                break;
            case R.id.main_activity_connect_bluetooth :
                showRoboboDeviceSelectionDialog();// initialisation du RoboboApp

                break;
            default:
                Log.d("onClickMain", "reached end of switch");
        }
    }

    public void onClickOptions(View button) {
        int counter;
        LinearLayout parent;
        //ArrayList<String> chosen = new ArrayList<>();
        switch (button.getId()) {
            case R.id.behavior_generator_reset :
                Log.d("onClickOptions", "Reset button");
                resetPopup.show();
                break;
            case R.id.behavior_generator_nsbutton :
                Log.d("onClickOptions", "NS button");
                parent = (LinearLayout)findViewById(R.id.behavior_generator_list);
                parent_list.clear();
                for (counter = 0; counter < parent.getChildCount(); counter++) {
                    CheckBox checkBox = (CheckBox) parent.findViewById(counter).findViewById(R.id.mycheckbox);
                    if (checkBox.isChecked()) {
                        //chosen.add(myList.get(counter));
                        parent_list.add(counter);
                    }
                }
                // insert real purpose here
                roboPop = roboPop.noveltySearch(NSpop, parent_list);
                /*
                for (String s : chosen) {
                    //parent.removeView(parent.findViewById(i));
                    myList.remove(s);
                }
                */
                displayBGen(button);
                Log.d("onClickOptions", parent_list.toString());
                break;
            case R.id.behavior_generator_load :
                Log.d("onClickOptions", "Load button");
                try {
                    onClickcharger(button);
                } catch (Exception e) {
                    Log.e("onClickOptions", "Error loading file.");
                }
                break;
            case R.id.behavior_generator_end :
                Log.d("onClickOptions", "End button");
                parent = (LinearLayout)findViewById(R.id.behavior_generator_list);
                RoboboDNA toSave = null;
                for (counter = 0; counter < parent.getChildCount(); counter++) {
                    CheckBox checkBox = (CheckBox) parent.findViewById(counter).findViewById(R.id.mycheckbox);
                    if (checkBox.isChecked()) {
                        if (toSave == null)
                            toSave = roboPop.getPop().get(counter);
                        else
                        {
                            toSave = null;
                            break;
                        }
                    }
                }
                if (toSave == null)
                {
                    Log.d("onClickOptions", "not single behavior selected");
                    presavePopup.show();
                }
                else
                {
                    Log.d("onClickOptions", "saving...");
                    boiteSauvegarde(toSave);
                }
                // saves the selected behavior to a file
                // displays a popup, clears all behaviors, sets roboPopInit = false
                break;
            default :
                Log.d("onClickOptions", "went to default");
        }
    }

    public void onClickList(View button)  {

        //LinearLayout parent = (LinearLayout)findViewById(R.id.behavior_generator_list);

        View view = (View) button.getParent();
        int id = view.getId();

        try {
            roboPop.getPop().get(id).exec();
        } catch (Exception e) {
            e.printStackTrace();
        }


        /*
        if(id == 1) {
            parent.removeView(view);
            try {
                new RoboboApp(roboboManager, MainActivity.this, new Callable<Integer>() {
                    public Integer call() {
                        int i = 0;
                        try {
                            return carre();
                        } catch (InternalErrorException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return i;
                    }});
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        */

        Log.d("onCLick", "Testing behavior "+id);//myList.get(id)
    }

    public int carre() throws InternalErrorException, InterruptedException {

        IRob rob = roboboManager.getModuleInstance(BluetoothRobInterfaceModule.class).getRobInterface();

        for(int i=0;i<4;i++) {
            rob.moveMT(MoveMTMode.FORWARD_FORWARD, 80, 80, 200L);
            Thread.sleep(200L);
            rob.moveMT(MoveMTMode.FORWARD_REVERSE, 80, 240, 80, 240);
            Thread.sleep(200L);
        }
        return 0;
    }
    
    /*****Sauvegarde/Chargement************************************************************/
     public void boiteSauvegarde(final RoboboDNA rDNA)  {
        final String[] nom = new String[1];
        LayoutInflater factory = LayoutInflater.from(this);
        final View alertDialogView = factory.inflate(R.layout.serialization_name, null);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);

        adb.setView(alertDialogView);
        adb.setTitle("Sauvegarder");


        adb.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                EditText valsaisie = (EditText)alertDialogView.findViewById(R.id.EditText);
                nom[0] =valsaisie.getText().toString();
                try {
                    serialization(nom[0],serialisationDNA(rDNA));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } });



        adb.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            } });
        adb.show();
    }
    public String serialisationDNA(RoboboDNA rDNA) {
        String data="";
        for(int i=0;i<rDNA.getGenotype().size();i++){
            switch (rDNA.getGenotype().get(i).getMvmtType()){
                case BACKWARDS:
                    data += "BACKWARDS";

                    break;
                case BACKWARDS_LEFT:
                    data += "BACKWARDS_LEFT";
                    break;
                case BACKWARDS_RIGHT:
                    data += "BACKWARDS_RIGHT";
                    break;
                case FORWARD:
                    data += "FORWARD";
                    break;
                case FORWARD_LEFT:
                    data += "FORWARD_LEFT";
                    break;
                case FORWARD_RIGHT:
                    data += "FORWARD_RIGHT";
                    break;
                case TURN_LEFT:
                    data += "TURN_LEFT";
                    break;
                case TURN_RIGHT:
                    data += "TURN_RIGHT";
                    break;
                default: break;
            }
            data += " "+rDNA.getGenotype().get(i).getLeftVelocity()
                    +" "+rDNA.getGenotype().get(i).getRightVelocity()
                    +" "+rDNA.getGenotype().get(i).getduration()+" ";
        }
        return data;

    }
    public void  serialization(String nom, String data) throws IOException {
        FileOutputStream outputStream ;

        try {
            outputStream = openFileOutput(nom+".txt", Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            Toast.makeText(context, "Sauvegarder", Toast.LENGTH_SHORT).show();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
       if(!nom.equals("nomFichier"))
        {sauvegarderListFichier(nom);}
    }
    public void onClickcharger(View view) throws IOException {
        LayoutInflater factory = LayoutInflater.from(this);
        final View alertDialogView = factory.inflate(R.layout.load_name, null);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        final String [] nomdesFichiers=genererListeFichier();
        final Spinner spin = (Spinner) alertDialogView.findViewById(R.id.loadspinner);
        adb.setView(alertDialogView);
        adb.setTitle("Charger");
        final String[] resultat = new String[1];

        if(nomdesFichiers.length!=0){
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, nomdesFichiers);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin.setAdapter(dataAdapter);
        }

        adb.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(nomdesFichiers.length!=0) {

                    String nom = (String)spin.getSelectedItem();
                    Toast.makeText(context,nom,Toast.LENGTH_SHORT).show();
                    try {
                        resultat[0] =charger(nom);
                        roboPop.getPop().add(remplirDNA(resultat[0]));
                         setContentView(R.layout.behavior_generator);
                        atMainview = false;

                        LinearLayout parent = (LinearLayout)findViewById(R.id.behavior_generator_list);

                        // check that parent has no children (remove all children otherwise)
                        parent.removeAllViews();

                        ViewGroup.LayoutParams params = parent.getLayoutParams();
                        Point size = new Point();
                        getWindowManager().getDefaultDisplay().getSize(size);
                        params.height = size.y / 10;

                        int counter;
                        for (counter = 0; counter < roboPop.getPop().size()-1; counter++) {
                            //for (counter = 0; counter < myList.size(); counter++) {
                            View child = getLayoutInflater().inflate(R.layout.behavior_example, null);
                            child.setId(pos);
                            child.setLayoutParams(params);
                            EditText editText = (EditText) child.findViewById(R.id.theTextField);
                            editText.setText("Behavior " + (counter+1));
                            pos++;
                            parent.addView(child);
                        }
                        View child = getLayoutInflater().inflate(R.layout.behavior_example, null);
                        child.setId(pos);
                        child.setLayoutParams(params);
                        EditText editText = (EditText) child.findViewById(R.id.theTextField);
                        editText.setText(nom);
                        parent.addView(child);
                        
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } });

        adb.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            } });
        adb.show();
    }
    public String charger(String nom) throws IOException {
        FileInputStream FIS =null;
        String data="";
        try{

            FIS = openFileInput(nom+".txt");
            byte[] buffer =new byte[1];
            StringBuilder content=new StringBuilder();
            while((FIS.read(buffer))!=-1){
                content.append((new String(buffer)));
            }
            data = content.toString();
            Toast.makeText(context,data,Toast.LENGTH_SHORT).show();

        }
        catch (FileNotFoundException e){
           e.printStackTrace();

        }
        catch (IOException e){
            return "";

        }
        finally {
            FIS.close();
        }
        return data;
    }
    public void sauvegarderListFichier(String nvFichier) throws IOException {

        try{
            String data=charger("nomFichier");
            data+=" "+nvFichier;
            serialization("nomFichier",data);}
        catch(Exception e){
            String data=nvFichier;
            serialization("nomFichier",data);
        }


    }
    public String[] genererListeFichier() throws IOException {
        String s;
        s=charger("nomFichier");
        String [] mot= s.split(" ");

        return mot;
    }
  public RoboboDNA remplirDNA  (String resultat){
        RoboboDNA rDNA=new RoboboDNA(rob, resultat);
        return rDNA;
    }
}
