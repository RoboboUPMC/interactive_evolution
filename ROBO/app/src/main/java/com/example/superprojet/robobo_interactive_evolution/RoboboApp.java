package com.example.superprojet.robobo_interactive_evolution;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.hri.emotion.DefaultEmotionModule;
import com.mytechia.robobo.rob.IRob;
import com.mytechia.robobo.rob.movement.DefaultRobMovementModule;
import com.mytechia.robobo.rob.movement.IRobMovementModule;


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











































