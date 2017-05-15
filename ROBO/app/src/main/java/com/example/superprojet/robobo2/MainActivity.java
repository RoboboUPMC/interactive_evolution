package com.example.superprojet.robobo2;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.superprojet.robobo2.genome.RoboboDNASim;
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
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity implements ITestListener {

    static Boolean atMainview;
    private ProgressDialog pDialog;

    public RoboboManager roboboManager;
    public RoboboApp app;

    public RoboboPopulation NSpop;
    public RoboboPopulation roboPop;
    public Boolean roboPopInit;
    public ArrayList<Integer> parent_list;
    public ArrayList<Bitmap> image_list;
    private AlertDialog resetPopup;
    private AlertDialog presavePopup;
    private AlertDialog oopsPopup;
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
        //remoteModule.postStatus(s);//had to comment this

        return true;

    }

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

                //tv.setText(things);

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
        parent_list = new ArrayList<>();
        image_list = new ArrayList<>();
        NSpop = new RoboboPopulation();

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
                drawImages();
                displayBGen(findViewById(R.id.behavior_generator_reset));
                dialog.dismiss();
            }
        });
        resetPopup = builder.create();

        // SAVING
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

        // OOPS
        AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
        builder3.setMessage(R.string.oops_dialog)
                .setTitle(R.string.oops);
        builder3.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        oopsPopup = builder3.create();

    }

    @Override
    protected void onDestroy(){
        if (rob!=null) roboboHelper.unbindRoboboService();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(!atMainview) {
            setContentView(R.layout.activity_main);
            atMainview = true;
        } else {
            moveTaskToBack(true);
        }
        Log.d("Info", "Back button pressed");
    }

    /**
     * Refreshes the Robobo Generator screen to display the correct (current) information
     * @param view
     */
    public void displayBGen(View view){
        int pos = 0;

        setContentView(R.layout.behavior_generator);
        atMainview = false;

        LinearLayout parent = (LinearLayout)findViewById(R.id.behavior_generator_list);

        // remove all children from the parent
        parent.removeAllViews();

        ViewGroup.LayoutParams params = parent.getLayoutParams();
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        params.height = size.y / 10;

        int counter;
        for (counter = 0; counter < roboPop.getPop().size(); counter++) {
            View child = getLayoutInflater().inflate(R.layout.behavior_example, null);

            child.setId(pos);
            child.setLayoutParams(params);
            ImageView imageView = (ImageView) child.findViewById(R.id.theImage);
            Bitmap image = image_list.get(counter);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageBitmap(image);
            pos++;

            parent.addView(child);
        }
    }

    /**
     * Draws the images resulting from the simulation of the current behaviors
     * and adds them to a list
     */
    public void drawImages()
    {
        int i;
        ArrayList<RoboboDNA> pop = roboPop.getPop();
        image_list.clear();
        for (i=0; i<pop.size(); i++)
        {
            image_list.add(pop.get(i).DNAtoImage(i));
        }
    }

    /**
     * Called when one of the main interface buttons is tapped
     * Take a view as parameter used to check which button was tapped
     * Either displays the Behavior Generator screen, lowers the panel of the connected ROBOBO,
     * connects via bluetooth to a ROBOBO device, or exits the app
     * @param button
     */
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
                        Log.d("setpop : ", "Exception");
                    }
                    roboPopInit = true;
                    Log.d("onClickMain", "initialize RoboPop");
                }
                drawImages();
                displayBGen(button);
                break;

            case R.id.main_activity_run_test :// now lowers the top panel to put away the device
                if (rob!=null) app.run();
                else oopsPopup.show();
                break;

            case R.id.main_activity_connect_bluetooth :
                showRoboboDeviceSelectionDialog();// initialisation du RoboboApp
                break;

            case R.id.main_activity_exit :
                finish();
                break;

            default:
                Log.d("onClickMain", "reached end of switch");
        }
    }

    /**
     * Called when a user taps one of the button at the top of Behavior Generator
     * Takes a view as parameter used to check which button was tapped
     * Either resets the behavior list, executes the NS assisted genetic algorithm,
     * starts a behavior loading dialog, or starts a behavior saving dialog
     * @param button
     */
    public void onClickOptions(View button) {
        int counter;
        LinearLayout parent;
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
                        parent_list.add(counter);
                    }
                }

                try {
                    new asyncNS().execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                drawImages();
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
            case R.id.behavior_generator_end ://now used as a save button
                Log.d("onClickOptions", "Save button");
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
                break;
            default :
                Log.d("onClickOptions", "went to default");
        }
    }

    /**
     * Makes a ROBOBO execute a specific behavior if connected to it
     * Takes a View as parameter (the button tapped which called this function)
     * @param button
     */
    public void onClickList(View button)  {

        View view = (View) button.getParent();
        int id = view.getId();

        if (rob!=null)
        {
            try {
                roboPop.getPop().get(id).exec();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else oopsPopup.show();

        Log.d("onCLick", "Testing behavior "+id);
    }

    /**
     * Displays an image in an AlertDialog with a larger version of the image tapped
     * It takes a view as a parameter (the one which caused the function to be called)
     * It is called when a user taps on an image in the Behavior Generator list
     * @param view
     */
     public void onClickImage(View view){

        LinearLayout list = (LinearLayout) findViewById(R.id.behavior_generator_list);
        LinearLayout lin = (LinearLayout) view.getParent();
        int pos= list.indexOfChild(lin);

        LayoutInflater factory = LayoutInflater.from(this);
        final View alertDialogView = factory.inflate(R.layout.show_image, null);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);

        ImageView imageView2 = (ImageView)  alertDialogView.findViewById(R.id.ImageView);

        imageView2.setImageBitmap(image_list.get(pos));
        imageView2.setScaleType(ImageView.ScaleType.FIT_CENTER);
        adb.setView(alertDialogView);
        adb.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            } });

        adb.show();
    }

    /*****Saving/Loading************************************************************/
    /**
     * Starts a Dialog to help the user save a selected behavior and saves it
     * It takes a RoboboDNA as a parameter (the one to be saved)
     * It is called when a user taps on the SAVE button in Behavior Generator
     * @param rDNA
     */
    public void boiteSauvegarde(final RoboboDNA rDNA)  {
        final String[] nom = new String[1];
        LayoutInflater factory = LayoutInflater.from(this);
        final View alertDialogView = factory.inflate(R.layout.serialization_name, null);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);

        adb.setView(alertDialogView);
        adb.setTitle(R.string.saving_title);


        adb.setPositiveButton(R.string.save_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                EditText valsaisie = (EditText)alertDialogView.findViewById(R.id.EditText);
                nom[0] = valsaisie.getText().toString();
                 try {
                    if(nomDejaUtilise(nom[0])){
                        try {
                            serialization(nom[0], serialisationDNA(rDNA));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        if(nom[0].equals("nomFichier") || nom[0].contains(" ") ){
                            Toast.makeText(context,"name is incorrect", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(context, nom[0] + " is already used", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                     try {
                         if(nom[0].equals("nomFichier") || nom[0].contains(" ") ){
                             Toast.makeText(context,"name is incorrect", Toast.LENGTH_SHORT).show();
                         }
                         else {
                             serialization(nom[0], serialisationDNA(rDNA));
                         }
                     } catch (IOException e1) {
                         e1.printStackTrace();
                     }
                }
            } });



        adb.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            } });
        adb.show();
    }

    /**
     * Checks if a behavior with this name already exists in save files
     * It takes a String as a paramter (used to check against the other behaviors saved)
     * @param nom
     * @return
     * @throws IOException
     */
    boolean nomDejaUtilise(String nom) throws IOException {
        String fichier[] = genererListeFichier();
        for (int i=0;i<fichier.length;i++) {
            if (fichier[i].equals(nom)) {
                return false;
            }
        }
        if (nom.equals("nomFichier")){
            return false;
        }
        if(nom.contains(" ")){
            return false;
        }
        return true;
    }

    /**
     * Converts a RoboboDNA to a serializable data format
     * It takes a RoboboDNA as a parameter (the one to be converted)
     * @param rDNA
     * @return
     */
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

    /**
     * Saves a behavior to file as a String
     * It takes two Strings as parameters : the name of the behavior and the data
     * @param nom
     * @param data
     * @throws IOException
     */
    public void  serialization(String nom, String data) throws IOException {
        FileOutputStream outputStream ;

        try {
            outputStream = openFileOutput(nom+".txt", Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            Toast.makeText(context, R.string.saving_title, Toast.LENGTH_SHORT).show();
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

    /**
     * Starts a Dialog to help the user load a previously saved behavior
     * It takes a View as a parameter (the one which called the function)
     * It is called when a user taps on the LOAD button in Behavior Generator
     * @param view
     * @throws IOException
     */
    public void onClickcharger(View view) throws IOException {
        LayoutInflater factory = LayoutInflater.from(this);
        final View alertDialogView = factory.inflate(R.layout.load_name, null);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        final String [] nomdesFichiers=genererListeFichier();
        final Spinner spin = (Spinner) alertDialogView.findViewById(R.id.loadspinner);
        adb.setView(alertDialogView);
        adb.setTitle(R.string.loading_title);
        final String[] resultat = new String[1];

        if(nomdesFichiers.length!=0){
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, nomdesFichiers);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin.setAdapter(dataAdapter);
        }

        adb.setPositiveButton(R.string.load_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(nomdesFichiers.length!=0) {

                    String nom = (String)spin.getSelectedItem();
                    
                    Toast.makeText(context,"Loading "+nom,Toast.LENGTH_SHORT).show();
                    
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
                        int pos = 0;
                        drawImages();
                        for (counter = 0; counter < roboPop.getPop().size(); counter++) {
                            View child = getLayoutInflater().inflate(R.layout.behavior_example, null);
                            child.setId(pos);
                            child.setLayoutParams(params);
                            //EditText editText = (EditText) child.findViewById(R.id.theTextField);
                            //editText.setText("Behavior " + (counter+1));
                            
                            ImageView imageView = (ImageView) child.findViewById(R.id.theImage);
                            Bitmap image = image_list.get(counter);
                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            imageView.setImageBitmap(image);
                            
                            pos++;
                            parent.addView(child);
                        }
                        
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } });

        adb.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            } });
        adb.show();
    }

    /**
     * Loads a behavior from file
     * It takes a String as a parameter (the name of the behavior to be loaded)
     * It is called when a user taps on the SAVE button in Behavior Generator
     * @param nom
     */
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

    /**
     * Saves the list of saved behaviors
     * It takes a String as a parameter (the list of saved files)
     * @param nvFichier
     * @throws IOException
     */
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

    /**
     * Returns the list of names of the saved behaviors
     * @return
     * @throws IOException
     */
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

    /**
     * This AsyncTask is used to run the entire process of generating a
     * new population asynchronously
     */
    private class asyncNS extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            roboPop = roboPop.noveltySearch(NSpop, parent_list);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            Log.d("onPostExecute", "done");
        }

    }
}
