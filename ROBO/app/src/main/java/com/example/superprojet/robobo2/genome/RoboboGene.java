package com.example.superprojet.robobo2.genome;

import android.util.Log;

import com.example.superprojet.robobo2.RoboboApp;
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
import java.util.Random;

/**
 * Created by Quentin on 2017/04/04.
 */

// Seriously thinking about this class being a movement + a color
// not sure about the result, it might blink a lot and be ugly
// Maybe should generate a single color per individual of the base population,
// and have a small chance of mutating the color of each gene on a normal
// distribution

public class RoboboGene implements Runnable{

    public enum MvmtType{
        FORWARD, FORWARD_LEFT, FORWARD_RIGHT, BACKWARDS, BACKWARDS_LEFT, BACKWARDS_RIGHT, TURN_LEFT, TURN_RIGHT
    }

    IRob rob;
    RoboboManager roboboManager;

    MvmtType mvmtType;

    int leftVelocity = 0;
    int rightVelocity = 0;

    RoboboMvmt mvmt;

    IRobStatusListener listener = new IRobStatusListener() {
        @Override
        public void statusMotorsMT(MotorStatus left, MotorStatus right) {
            RoboboGene.this.leftVelocity = left.getAngularVelocity();
            RoboboGene.this.rightVelocity = right.getAngularVelocity();

        }

        @Override
        public void statusMotorPan(MotorStatus status) {

        }

        @Override
        public void statusMotorTilt(MotorStatus status) {

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
    };

    long duration;

    public RoboboGene(RoboboManager roboboManager, MvmtType d){
        this.roboboManager = roboboManager;
        if(roboboManager == null){
            Log.d("RoboboGene", "le manager passé en argument est null");
        }
        try {
            rob = roboboManager.getModuleInstance(BluetoothRobInterfaceModule.class).getRobInterface();
        } catch (ModuleNotFoundException e) {
            Log.d("RoboboApp", "erreur");
            Log.e("ROBOBO-APP", "Module not found: "+e.getMessage());
        }

        rob.addRobStatusListener(new IRobStatusListener() {
            @Override
            public void statusMotorsMT(MotorStatus left, MotorStatus right) {
                RoboboGene.this.leftVelocity = left.getAngularVelocity();
                RoboboGene.this.rightVelocity = right.getAngularVelocity();

            }

            @Override
            public void statusMotorPan(MotorStatus status) {

            }

            @Override
            public void statusMotorTilt(MotorStatus status) {

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

        try {
            rob.setRobStatusPeriod(10); // les statuts du robot sont vérifiés toutes les 50ms
        } catch (InternalErrorException e) {
            e.printStackTrace();
        }

        mvmtType = d;

        switch (d){
            case BACKWARDS:
                mvmt = new MvmtBwd();
                break;
            case BACKWARDS_LEFT:
                mvmt = new MvmtBwdLeft();
                break;
            case BACKWARDS_RIGHT:
                mvmt = new MvmtBwdRight();
                break;
            case FORWARD:
                mvmt = new MvmtFwd();
                break;
            case FORWARD_LEFT:
                mvmt = new MvmtFwdLeft();
                break;
            case FORWARD_RIGHT:
                mvmt = new MvmtFwdRight();
                break;
            case TURN_LEFT:
                mvmt = new MvmtLeft();
                break;
            case TURN_RIGHT:
                mvmt = new MvmtRight();
                break;
            default: break;
        }

    }

    public RoboboGene(IRob r, MvmtType d){
        rob = r;




        mvmtType = d;

        switch (d){
            case BACKWARDS:
                mvmt = new MvmtBwd();
                break;
            case BACKWARDS_LEFT:
                mvmt = new MvmtBwdLeft();
                break;
            case BACKWARDS_RIGHT:
                mvmt = new MvmtBwdRight();
                break;
            case FORWARD:
                mvmt = new MvmtFwd();
                break;
            case FORWARD_LEFT:
                mvmt = new MvmtFwdLeft();
                break;
            case FORWARD_RIGHT:
                mvmt = new MvmtFwdRight();
                break;
            case TURN_LEFT:
                mvmt = new MvmtLeft();
                break;
            case TURN_RIGHT:
                mvmt = new MvmtRight();
                break;
            default: break;
        }

    }

   public RoboboGene(IRob r, String mvt,int lV, int rV,long dur){
        rob = r;
        if(mvt.equals("BACKWARDS")){
            this.setMvmtType(mvmtType.BACKWARDS);
        }
        else if(mvt.equals("BACKWARDS_LEFT")){
            this.setMvmtType(mvmtType.BACKWARDS_LEFT);
        }
        else if(mvt.equals("BACKWARDS_RIGHT")){
            this.setMvmtType(mvmtType.BACKWARDS_RIGHT);
        }
        else if(mvt.equals("FORWARD")){
            this.setMvmtType(mvmtType.FORWARD);
        }
        else if(mvt.equals("FORWARD_LEFT")){
            this.setMvmtType(mvmtType.FORWARD_LEFT);
        }
        else if(mvt.equals("FORWARD_RIGHT")){
            this.setMvmtType(mvmtType.FORWARD_RIGHT);
        }
        else if(mvt.equals("TURN_LEFT")){
            this.setMvmtType(mvmtType.TURN_LEFT);
        }
        else if(mvt.equals("TURN_RIGHT")){
            this.setMvmtType(mvmtType.TURN_RIGHT);
        }
        this.leftVelocity=lV;
        this.rightVelocity=rV;
        this.duration=dur;
    }
    
    public void setMvmtType(MvmtType mvmtType) {
        this.mvmtType = mvmtType;
        switch (mvmtType){
            case BACKWARDS:
                mvmt = new MvmtBwd();
                break;
            case BACKWARDS_LEFT:
                mvmt = new MvmtBwdLeft();
                break;
            case BACKWARDS_RIGHT:
                mvmt = new MvmtBwdRight();
                break;
            case FORWARD:
                mvmt = new MvmtFwd();
                break;
            case FORWARD_LEFT:
                mvmt = new MvmtFwdLeft();
                break;
            case FORWARD_RIGHT:
                mvmt = new MvmtFwdRight();
                break;
            case TURN_LEFT:
                mvmt = new MvmtLeft();
                break;
            case TURN_RIGHT:
                mvmt = new MvmtRight();
                break;
            default: break;
        }
    }

    private RoboboGene(IRob r){
        rob = r;
    }



    public void mutate(){
        RoboboGene mutatedGene = new RoboboGene(rob);
        Random random = new Random();
        MvmtType newMvmt;
        int r = 0;

        r = random.nextInt(MvmtType.values().length);
        newMvmt = MvmtType.values()[r];
        this.setMvmtType(newMvmt);
;
    }

    public RoboboManager getRoboboManager() {
        return roboboManager;
    }

    public MvmtType getMvmtType() {
        return mvmtType;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof RoboboGene)){
            return false;
        }
        RoboboGene gene = (RoboboGene) obj;
        return this.mvmtType == gene.mvmtType;
    }

    @Override
    public void run() {
//        rob.addRobStatusListener(listener);
//
//        try {
//            rob.setRobStatusPeriod(10); // les statuts du robot sont vérifiés toutes les 50ms
//        } catch (InternalErrorException e) {
//            e.printStackTrace();
//        }
//        try {
//            rob.setOperationMode((byte)1);
//        } catch (InternalErrorException e) {
//            e.printStackTrace();
//        }


        try {
            rob.moveMT(mvmt.getDirection(), mvmt.getLeftVelocity(), mvmt.getRightVelocity(), mvmt.getDuration());
//            while(rightVelocity!=0 || leftVelocity!=0){
//                Thread.sleep(10L);// on attend que le robot ait terminé son action
//            }
//            rob.moveMT(MoveMTMode.FORWARD_FORWARD, 0, 0, 1L);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        rob.removeRobStatusListener(listener);
        Log.d("RoboboGene.run()", "over");

    }
    
    public int getLeftVelocity(){return this.leftVelocity;}
    public int getRightVelocity(){return this.rightVelocity;}
    public long getduration(){return mvmt.getDuration();}
}
