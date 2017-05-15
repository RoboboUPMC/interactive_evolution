package com.example.superprojet.robobo_interactive_evolution.genome;

import com.mytechia.robobo.rob.MoveMTMode;

/**
 * Created by Quentin on 2017/04/04.
 */

abstract class RoboboMvmt {


    long duration =  1000L;
    int leftVelocity;
    int rightVelocity;

    MoveMTMode direction;


    RoboboMvmt(int left, int right, long duration){
        this.leftVelocity = left;
        this.rightVelocity = right;
        this.duration = duration;
    }

    RoboboMvmt(int left, int right){
        this.leftVelocity = left;
        this.rightVelocity = right;
    }


    public MoveMTMode getDirection() {
        return direction;
    }

    public int getLeftVelocity() {
        return leftVelocity;
    }

    public int getRightVelocity() {
        return rightVelocity;
    }

    public long getDuration() {
        return duration;
    }
}
