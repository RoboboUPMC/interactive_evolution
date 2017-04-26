package com.example.superprojet.robobo2;

import android.util.Log;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.exception.ModuleNotFoundException;
import com.mytechia.robobo.framework.hri.emotion.DefaultEmotionModule;
import com.mytechia.robobo.rob.BatteryStatus;
import com.mytechia.robobo.rob.BluetoothRobInterfaceModule;
import com.mytechia.robobo.rob.FallStatus;
import com.mytechia.robobo.rob.GapStatus;
import com.mytechia.robobo.rob.IRSensorStatus;
import com.mytechia.robobo.rob.IRob;
import com.mytechia.robobo.rob.IRobStatusListener;
import com.mytechia.robobo.rob.MotorStatus;
import com.mytechia.robobo.rob.MoveMTMode;
import com.mytechia.robobo.rob.WallConnectionStatus;
import com.mytechia.robobo.rob.movement.DefaultRobMovementModule;
import com.mytechia.robobo.rob.movement.IRobMovementModule;
import com.mytechia.robobo.util.Color;


import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * Created by Quentin on 2017/03/02.
 */

/**
 * Classe séparée pour gérer les actions de robobo.
 * pour l'instant, il faut que RoboboManager (et textview mais c'est un détail) soit public dans MainActivity
 */

public class RoboboApp implements Runnable {

    int leftVelocity = 0;
    int rightVelocity = 0;


    private DefaultRobMovementModule bouger;
    private IRobMovementModule move;
    private com.mytechia.robobo.rob.IRobInterfaceModule robModule;
    private IRob rob;
    private DefaultEmotionModule emo;
    private MainActivity ma;

    private Callable<Integer> bhv;


    public RoboboApp(RoboboManager roboboManager, MainActivity m, Callable<Integer> bhv) {
        this.bhv = bhv;

        ma = m;

        ma.roboboManager = roboboManager;



        try {
            bouger = ma.roboboManager.getModuleInstance(DefaultRobMovementModule.class);

            //emo = ma.roboboManager.getModuleInstance((DefaultEmotionModule.class));
            move = ma.roboboManager.getModuleInstance((IRobMovementModule.class));

            robModule = ma.roboboManager.getModuleInstance(BluetoothRobInterfaceModule.class);
            //get the instance of the ROB interface
            this.rob = robModule.getRobInterface();
        }
        catch(ModuleNotFoundException ex) {
            //ma.tv.setText("erreur");
            Log.d("RoboboApp", "erreur");
            Log.e("ROBOBO-APP", "Module not found: "+ex.getMessage());
        }


    }


    @Override
    public void run() {
        try {
            rob.setOperationMode((byte)1);
        } catch (InternalErrorException e) {
            e.printStackTrace();
        }


        try {
            bhv.call();
            carre();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            move.moveTilt(6, 26);
        } catch (InternalErrorException e) {
            e.printStackTrace();
        }

    }

    public void actionSequence(){

        try{
            //bouger.startup(ma.roboboManager);
            // Le listener qui permet de savoir les statuts de ROBOBO.
            // pour l'instant seuls les moteurs sont écoutés
            rob.addRobStatusListener(new IRobStatusListener() {
                @Override
                public void statusMotorsMT(MotorStatus left, MotorStatus right) {
                    RoboboApp.this.leftVelocity = left.getAngularVelocity();
                    RoboboApp.this.rightVelocity = right.getAngularVelocity();



                }

                @Override
                public void statusMotorPan(MotorStatus status) {

                }

                @Override
                public void statusMotorTilt(MotorStatus status) {
                    if(status.getVariationAngle()!=27){
                        status.setVariationAngle(27);
                        status.setAngularVelocity(6);
                    }



                }

                @Override
                public void statusGaps(Collection<GapStatus> gaps) {

                }

                @Override
                public void statusFalls(Collection<FallStatus> fall) {

                }

                @Override
                public void statusIRSensorStatus(Collection<IRSensorStatus> irSensorStatus) {

                }

                @Override
                public void statusBattery(BatteryStatus battery) {

                }

                @Override
                public void statusWallConnectionStatus(WallConnectionStatus wallConnectionStatus) {

                }

                @Override
                public void robCommunicationError(InternalErrorException ex) {

                }
            });



            rob.setRobStatusPeriod(50); // les statuts du robot sont vérifiés toutes les 50ms
            rob.setOperationMode((byte)1);
            rob.moveMT(MoveMTMode.FORWARD_FORWARD,80,2000,80,2000); // équivaut à un tour d'environ pi/2

            Thread.sleep(200L); // on attend un peu pour que le listener ait mis à jour les statuts
            move.moveTilt(6,27);


            while(rightVelocity!=0 || leftVelocity!=0){
                Thread.sleep(10L);// on attend que le robot ait terminé son action
            }
            //rob.moveMT(MoveMTMode.REVERSE_REVERSE,80,360,80,360);
            Color c = new Color(160,0,0); // couleur à mettre en parametre pour les LEDs
            rob.movePan(1000,800);
            for(int i=1;i<=9;i++){
                // setLEDColor s'occupe d'une LED à la fois
                // pour le numéro des LED voir https://bitbucket.org/mytechia/robobo-framework/wiki/using-rob-interface
                rob.setLEDColor(i, c);
            }

            /*if(!rob.getLastStatusMotors().isEmpty())
            ma.tv.setText(rob.getLastStatusMotors().get(0).getAngularVelocity());*/

        }catch (Exception e){
            //ma.tv.setText(e.getMessage());
            Log.d("actionSequence", e.getMessage());
        }

    }

    public void carre() throws InternalErrorException, InterruptedException {
        rob.addRobStatusListener(new IRobStatusListener() {
            @Override
            public void statusMotorsMT(MotorStatus left, MotorStatus right) {
                RoboboApp.this.leftVelocity = left.getAngularVelocity();
                RoboboApp.this.rightVelocity = right.getAngularVelocity();



            }

            @Override
            public void statusMotorPan(MotorStatus status) {

            }

            @Override
            public void statusMotorTilt(MotorStatus status) {
                if(status.getVariationAngle()!=27){
                    status.setVariationAngle(27);
                    status.setAngularVelocity(6);
                }



            }

            @Override
            public void statusGaps(Collection<GapStatus> gaps) {

            }

            @Override
            public void statusFalls(Collection<FallStatus> fall) {

            }

            @Override
            public void statusIRSensorStatus(Collection<IRSensorStatus> irSensorStatus) {

            }

            @Override
            public void statusBattery(BatteryStatus battery) {

            }

            @Override
            public void statusWallConnectionStatus(WallConnectionStatus wallConnectionStatus) {

            }

            @Override
            public void robCommunicationError(InternalErrorException ex) {

            }
        });
        rob.setOperationMode((byte)1);

        //for(int i=0;i<4;i++) {
            rob.moveMT(MoveMTMode.FORWARD_FORWARD,80,2000,80,2000);
            Thread.sleep(200L);
            while(rightVelocity!=0 || leftVelocity!=0){
                Thread.sleep(10L);// on attend que le robot ait terminé son action
            }            rob.moveMT(MoveMTMode.FORWARD_REVERSE, 80, 240, 80, 240);
            Thread.sleep(200L);
            while(rightVelocity!=0 || leftVelocity!=0){
                Thread.sleep(10L);// on attend que le robot ait terminé son action
            }
        rob.moveMT(MoveMTMode.FORWARD_FORWARD,80,2000,80,2000);
        Thread.sleep(200L);
        while(rightVelocity!=0 || leftVelocity!=0){
            Thread.sleep(10L);// on attend que le robot ait terminé son action
        }            rob.moveMT(MoveMTMode.FORWARD_REVERSE, 80, 240, 80, 240);
        Thread.sleep(200L);
        while(rightVelocity!=0 || leftVelocity!=0){
            Thread.sleep(10L);// on attend que le robot ait terminé son action
        }
        rob.moveMT(MoveMTMode.FORWARD_FORWARD,80,2000,80,2000);
        Thread.sleep(200L);
        while(rightVelocity!=0 || leftVelocity!=0){
            Thread.sleep(10L);// on attend que le robot ait terminé son action
        }            rob.moveMT(MoveMTMode.FORWARD_REVERSE, 80, 240, 80, 240);
        Thread.sleep(200L);
        while(rightVelocity!=0 || leftVelocity!=0){
            Thread.sleep(10L);// on attend que le robot ait terminé son action
        }
        rob.moveMT(MoveMTMode.FORWARD_FORWARD,80,2000,80,2000);
        Thread.sleep(200L);
        while(rightVelocity!=0 || leftVelocity!=0){
            Thread.sleep(10L);// on attend que le robot ait terminé son action
        }            rob.moveMT(MoveMTMode.FORWARD_REVERSE, 80, 240, 80, 240);
        Thread.sleep(200L);
        while(rightVelocity!=0 || leftVelocity!=0){
            Thread.sleep(10L);// on attend que le robot ait terminé son action
        }
       // }
    }

}











































