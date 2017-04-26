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

/**
 * Created by Quentin on 2017/04/04.
 */

// Seriously thinking about this class being a movement + a color
// not sure about the result, it might blink a lot and be ugly
// Maybe should generate a single color per individual of the base population,
// and have a small chance of mutating the color of each gene on a normal
// distribution

public class RoboboGene {

    public enum MvmtType{
        FORWARD, FORWARD_LEFT, FORWARD_RIGHT, BACKWARDS, BACKWARDS_LEFT, BACKWARDS_RIGHT, TURN_LEFT, TURN_RIGHT
    }

    IRob rob;

    MvmtType mvmtType;

    int leftVelocity = 0;
    int rightVelocity = 0;

    RoboboMvmt mvmt;

    long duration;

    public RoboboGene(RoboboManager roboboManager, MvmtType d){
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
            rob.setRobStatusPeriod(50); // les statuts du robot sont vérifiés toutes les 50ms
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

    private RoboboGene(IRob r){
        rob = r;
    }

    public void exec(){
        try {
            rob.setOperationMode((byte) 1);
            rob.moveMT(mvmt.getDirection(), mvmt.getLeftVelocity(), mvmt.getRightVelocity(), mvmt.getDuration());
        } catch (InternalErrorException e) {
            e.printStackTrace();
        }
    }

    public RoboboGene mutate(){
        RoboboGene mutated_gene = new RoboboGene(rob);

        return mutated_gene;
    }


    public MvmtType getMvmtType() {
        return mvmtType;
    }
}
