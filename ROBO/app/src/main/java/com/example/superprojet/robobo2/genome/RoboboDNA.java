package com.example.superprojet.robobo2.genome;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.exception.ModuleNotFoundException;
import com.mytechia.robobo.rob.BluetoothRobInterfaceModule;
import com.mytechia.robobo.rob.IRob;
import com.mytechia.robobo.rob.MoveMTMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Quentin on 2017/04/05.
 */
//a
public class RoboboDNA {

    public IRob rob;
    public RoboboManager roboboManager;
    // It's called genotype but actually also reflects the phenotype
    private ArrayList<RoboboGene> genotype = new ArrayList<>();
    public int selectionCounter = 0;


    public RoboboDNA(IRob iRob){
        rob = iRob;
        Integer seqSize = 10;
        Random r = new Random();
        for(int i = 0 ; i < seqSize ; i++){
            genotype.add(new RoboboGene(rob, RoboboGene.MvmtType.values()[r.nextInt(RoboboGene.MvmtType.values().length)]));
        }

    }
    public RoboboDNA(IRob r ,String s){
        rob = r;
        String [] mot  = s.split(" ");

        for(int i=0;i<mot.length;i=i+4){
            this.getGenotype().add(new RoboboGene(rob,mot[i],Integer.parseInt(mot[i+1]),Integer.parseInt(mot[i+2]),Long.parseLong(mot[i+3])));
        }
    }

    public void mutate(){
        int currSize = this.genotype.size();

        //set arbitrarily, may change
        int idealSize = 10;

        ArrayList<RoboboGene> newGenotype = new ArrayList<>();
        float mutaProba = 1f/currSize;
        float lossProba = Math.min(1f, currSize/(2*idealSize));
        float r = 0;
        Random random = new Random();
        RoboboGene currGene = null;

        for(int i = 0; i<currSize; i++){
            r = random.nextFloat();
            if(r>=lossProba){
                currGene = this.getGenotype().get(i);
                currGene.mutate();
                newGenotype.add(currGene);
            }
        }

        r = random.nextFloat();
        if(r >= lossProba){
            RoboboGene newGene = new RoboboGene(this.rob, RoboboGene.MvmtType.FORWARD);
            newGene.mutate();
            newGenotype.add(newGene);
        }

        this.setGenotype(newGenotype);
    }






    /**
     * Creates a RoboboDNA from the list made by crossover
     * @param child
     */
    public RoboboDNA(IRob rm, ArrayList<Integer> child){
        this.rob = rm;
        for(Integer  mvmt : child){
            genotype.add(new RoboboGene(rob, RoboboGene.MvmtType.values()[mvmt]));
        }

    }


    public void setGenotype(ArrayList<RoboboGene> genotype) {
        this.genotype = genotype;
    }

    public void exec(){

        try {
            rob.setOperationMode((byte)1);
          //  rob.moveMT(MoveMTMode.FORWARD_FORWARD, 80, 1000, 80, 1000);
        } catch (InternalErrorException e) {
            e.printStackTrace();
        }
        for(RoboboGene gene: this.getGenotype()){
            gene.run();
            try {
                Thread.sleep(gene.getduration());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap DNAtoImage(){
        RoboboDNASim sim = new RoboboDNASim();
        Bitmap im = Bitmap.createBitmap(200,200, Bitmap.Config.ARGB_4444);

        im.setPixel(((int) sim.getPosition()[0]),( (int) sim.getPosition()[1]), Color.BLUE);
        for(RoboboGene gene: this.getGenotype()){
            RoboboGene.MvmtType mv = gene.mvmtType;
            double[] action = sim.mvmtToAction(mv);
            if(action[0] == 0){
                sim.execAtion(action);
                im.setPixel(((int) sim.getPosition()[0]),( (int)sim.getPosition()[1]), Color.YELLOW);
            }
            else{
                for(int i = 0; i < 25; i++){
                    sim.execAtion(action);
                    im.setPixel(((int) sim.getPosition()[0]),( (int)sim.getPosition()[1]), Color.YELLOW);
                }
            }
            //System.out.println("Apres l'action, le robobo est en " + sim.position.toString() + "avec une vitesse " + sim.vitesse);
        }

        im.setPixel(((int) sim.getPosition()[0]),( (int) sim.getPosition()[1]), Color.RED);
        return im;
    }


    public ArrayList<RoboboGene> getGenotype() {
        return genotype;
    }
}
