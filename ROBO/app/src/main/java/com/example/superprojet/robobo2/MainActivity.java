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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.superprojet.robobo2.genome.RoboboDNA;
import com.example.superprojet.robobo2.genome.RoboboPopulation;
import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;

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
import com.mytechia.robobo.rob.util.RoboboDeviceSelectionDialog;


import org.java_websocket.client.WebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;


public class MainActivity extends AppCompatActivity implements ITestListener {

    ArrayList<String> myList = new ArrayList<>();
    static Boolean atMainview;

    public RoboboManager roboboManager;
    public RoboboApp app;

    public RoboboPopulation NSpop;
    public RoboboPopulation roboPop;
    public Boolean roboPopInit;
    public ArrayList<Integer> parent_list;
    private AlertDialog resetPopup;
    private int basicPopSize = 10;


    /**************************Connection Bluetooth*************************************/

    private static final String TAG="RemoteControlActivity";

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
                app = new RoboboApp(roboboManager, MainActivity.this, null);


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
                roboPop.init(basicPopSize, roboboManager);
                NSpop.setPop(roboPop.getPop());
                displayBGen(findViewById(R.id.behavior_generator_reset));
                dialog.dismiss();
            }
        });
        resetPopup = builder.create();



        myList.add("tralala");
        myList.add("truc");
        myList.add("machin");
        myList.add("chouette");
        myList.add("chose");
        myList.add("bestiole");
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
                    roboPop.init(basicPopSize, roboboManager);
                    NSpop.setPop(roboPop.getPop());
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
        //ArrayList<String> chosen = new ArrayList<>();
        switch (button.getId()) {
            case R.id.behavior_generator_reset :
                Log.d("onClickOptions", "Reset button");
                resetPopup.show();
                break;
            case R.id.behavior_generator_nsbutton :
                Log.d("onClickOptions", "NS button");
                LinearLayout parent = (LinearLayout)findViewById(R.id.behavior_generator_list);
                parent_list.clear();
                for (counter = parent.getChildCount()-1; counter >= 0; counter--) {
                    CheckBox checkBox = (CheckBox) parent.findViewById(counter).findViewById(R.id.mycheckbox);
                    if (checkBox.isChecked()) {
                        //chosen.add(myList.get(counter));
                        parent_list.add(counter);
                    }
                }
                // insert real purpose here

                /*
                for (String s : chosen) {
                    //parent.removeView(parent.findViewById(i));
                    myList.remove(s);
                }
                */
                displayBGen(button);
                Log.d("onClickOptions", parent_list.toString());
                break;
            case R.id.behavior_generator_optimiser :
                Log.d("onClickOptions", "Optimize button");
                //does nothing for now
                break;
            case R.id.behavior_generator_end :
                Log.d("onClickOptions", "End button");
                // saves the selected behavior to a file
                // displays a popup, clears all behaviors, sets roboPopInit = false
                break;
            default :
                Log.d("onClickOptions", "went to default");
        }
    }

    public void onClickList(View button)  {

        LinearLayout parent = (LinearLayout)findViewById(R.id.behavior_generator_list);

        View view = (View) button.getParent();
        int id = view.getId();

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

        Log.d("onCLick", myList.get(id));
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
}
