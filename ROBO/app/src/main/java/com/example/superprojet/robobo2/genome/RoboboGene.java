package com.example.superprojet.robobo2.genome;

import android.util.Log;

import com.mytechia.robobo.rob.IRob;
import com.mytechia.robobo.util.Color;

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

    MvmtType mvmtType;

    int leftVelocity = 0;
    int rightVelocity = 0;

    RoboboMvmt mvmt;



    long duration;


    /**
     * Creates a RoboboGene using an IRob (used to control the RBB)
     * and a MvmtType
     * This is the most widely used constructor of RoboboGene throughout the code
     * @param r
     * @param d
     */
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

    /**
     * Creates a new gene using an IRob (used to control the RBB)
     * and a String (the movement type)
     * Used to load a previously saved behavior
     * @param r
     * @param mvt
     * @param lV
     * @param rV
     * @param dur
     */
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

    /**
     * changes the mvmt variableto one of the type set in parameter
     * used in mutate()
     * @param mvmtType
     */
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


    /**
     * Changes the movement type of a gene to a new random one that may or may not be the same
     */
    public void mutate(){
        Random random = new Random();
        MvmtType newMvmt;
        int r = 0;

        r = random.nextInt(MvmtType.values().length);
        newMvmt = MvmtType.values()[r];
        this.setMvmtType(newMvmt);
        ;
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

    /**
     * executes the movement associated with the current gene
     * also lights a random LED to a random color
     */
    @Override
    public void run() {
        Random random = new Random();

        try {
            rob.moveMT(mvmt.getDirection(), mvmt.getLeftVelocity(), mvmt.getRightVelocity(), mvmt.getDuration());
            int i = random.nextInt(9) + 1;
            int r = random.nextInt(255);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            Color c = new Color(r,g,b);
            rob.setLEDColor(i,c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("RoboboGene.run()", "over");

    }

    public int getLeftVelocity(){return this.leftVelocity;}
    public int getRightVelocity(){return this.rightVelocity;}
    public long getduration(){return mvmt.getDuration();}
}
