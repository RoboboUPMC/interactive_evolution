package com.example.superprojet.robobo2;

import android.util.Log;

import com.example.superprojet.robobo2.genome.RoboboDNA;
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
 * class with the sole purpose of lowering the tilt of the panel so that ROBOBO may be stored in its
 * box
 */

public class RoboboApp implements Runnable {


    private DefaultRobMovementModule bouger;
    private IRobMovementModule move;
    private com.mytechia.robobo.rob.IRobInterfaceModule robModule;
    private IRob rob;
    private DefaultEmotionModule emo;
    private MainActivity ma;

    private Callable<Integer> bhv;


    public RoboboApp(IRob iRob, IRobMovementModule m, Callable<Integer> bhv) {
        this.bhv = bhv;



        rob = iRob;
        move = m;

    }


    @Override
    public void run() {
        try {
            rob.setOperationMode((byte)1);
        } catch (InternalErrorException e) {
            e.printStackTrace();
        }



        try {
            move.moveTilt(6, 26);
        } catch (InternalErrorException e) {
            e.printStackTrace();
        }

    }



}











































