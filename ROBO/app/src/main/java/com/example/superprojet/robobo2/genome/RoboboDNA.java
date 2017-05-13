package com.example.superprojet.robobo2.genome;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
        int idealSize = 20;

        ArrayList<RoboboGene> newGenotype = new ArrayList<>();
        float mutaProba = 1f/currSize;
        float lossProba = Math.min(1f, currSize/(2*idealSize));
        float r = 0;
        Random random = new Random();
        RoboboGene currGene = null;

        for(int i = 0; i<currSize; i++){
            r = random.nextFloat();
            if(r < mutaProba)
            {
                if(r>=lossProba){
                    currGene = this.getGenotype().get(i);
                    currGene.mutate();
                    newGenotype.add(currGene);
                }
            }
            else newGenotype.add(this.getGenotype().get(i));
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


    public Bitmap DNAtoImage2(int ival){
        RoboboDNASim sim = new RoboboDNASim();
        ArrayList<int[]> pixels = new ArrayList<int[]>();
        int maxX = (int) sim.getPosition()[0];
        int minX = (int) sim.getPosition()[0];
        int maxY = (int) sim.getPosition()[1];
        int minY = (int) sim.getPosition()[1];
        int imHeight;
        int imWidth;

        //im.setPixel(((int) sim.getPosition()[0]),( (int) sim.getPosition()[1]), Color.BLUE);
        pixels.add(new int[]{(int) sim.getPosition()[0], (int) sim.getPosition()[1]});
        for(RoboboGene gene: this.getGenotype()){
            RoboboGene.MvmtType mv = gene.mvmtType;
            double[] action = sim.mvmtToAction(mv);
            if(action[0] == 0){
                sim.execAction(action);
                //im.setPixel(java.lang.Math.min(im.getWidth()-1, (int)sim.getPosition()[0]), java.lang.Math.min(im.getHeight()-1, (int)sim.getPosition()[1]), Color.YELLOW);
                pixels.add(new int[]{(int) sim.getPosition()[0], (int) sim.getPosition()[1]});
            }
            else{
                for(int i = 0; i < 25; i++){
                    sim.execAction(action);
                    pixels.add(new int[]{(int) sim.getPosition()[0], (int) sim.getPosition()[1]});
                    //im.setPixel(java.lang.Math.min(im.getWidth()-1, (int)sim.getPosition()[0]), java.lang.Math.min(im.getHeight()-1, (int)sim.getPosition()[1]), Color.YELLOW);
                }
            }
            //System.out.println("Apres l'action, le robobo est en " + sim.position.toString() + "avec une vitesse " + sim.vitesse);
        }

        pixels.add(new int[]{(int) sim.getPosition()[0], (int) sim.getPosition()[1]});
        //im.setPixel(java.lang.Math.min(im.getWidth()-1, (int)sim.getPosition()[0]), java.lang.Math.min(im.getHeight()-1, (int)sim.getPosition()[1]), Color.RED);


        for(int[] p: pixels){
            if(p[0]>maxX){
                maxX = p[0];
            }
            if(p[1]>maxY){
                maxY = p[1];
            }
            if(p[0]<minX){
                minX = p[0];
            }

            if(p[1]<minY){
                minY = p[1];
            }
        }

        imWidth = (int) 1.1*Math.max(200, (maxX-minX)+10);
        imHeight = (int) 1.1*Math.max(200, (maxY-minY)+10);

        Bitmap im = Bitmap.createBitmap(imWidth, imHeight, Bitmap.Config.ARGB_8888);
        im.setHasAlpha(false);

        for(int i = 0; i< pixels.size(); i++){
            int x = pixels.get(i)[0] - minX;
            int y = pixels.get(i)[1] - minY;

            if(i<=10){
                im.setPixel(x,y, Color.RED);
            }
            else if(i >= (pixels.size()-10)){
                im.setPixel(x,y, Color.BLUE);
            }
            else if(ival%2==0){
                im.setPixel(x,y, Color.YELLOW);
            }
            else{
                im.setPixel(x,y, Color.GREEN);
            }
        }

        Bitmap res = Bitmap.createScaledBitmap(im, 50, 50, true);
        int width = im.getWidth();
        int height = im.getHeight();
        float scaleWidth = ((float) 100) / width;
        float scaleHeight = ((float) 150) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                im, 0, 0, width, height, matrix, false);
        im.recycle();
        return resizedBitmap;
        //return res;
        //return im;
    }


    public Bitmap DNAtoImage(int ival){

        RoboboDNASim sim = new RoboboDNASim();
        Bitmap im = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
        im.setHasAlpha(false);

        im.setPixel(((int) sim.getPosition()[0]),( (int) sim.getPosition()[1]), Color.BLUE);
        for(RoboboGene gene: this.getGenotype()){
            RoboboGene.MvmtType mv = gene.mvmtType;
            double[] action = sim.mvmtToAction(mv);
            if(action[0] == 0){
                sim.execAction(action);

                /*
                si la trajectoire sort de l'image, on ne l'ecrit pas
                 */
                if((int)sim.getPosition()[0] < (im.getWidth()) && (int)sim.getPosition()[0] >= 0){
                    if((int)sim.getPosition()[1] < im.getHeight() && (int)sim.getPosition()[1] >= 0){
                        im.setPixel( (int)sim.getPosition()[0], (int)sim.getPosition()[1], Color.YELLOW);
                    }
                }
                //im.setPixel(java.lang.Math.min(im.getWidth()-1, (int)sim.getPosition()[0]), java.lang.Math.min(im.getHeight()-1, (int)sim.getPosition()[1]), Color.YELLOW);
            }
            else{
                for(int i = 0; i < 25; i++){
                    sim.execAction(action);
                    if((int)sim.getPosition()[0] < (im.getWidth()) && (int)sim.getPosition()[0] >= 0){
                        if((int)sim.getPosition()[1] < im.getHeight() && (int)sim.getPosition()[1] >= 0){
                            im.setPixel( (int)sim.getPosition()[0], (int)sim.getPosition()[1], Color.YELLOW);
                        }
                    }
                    //im.setPixel(java.lang.Math.min(im.getWidth()-1, (int)sim.getPosition()[0]), java.lang.Math.min(im.getHeight()-1, (int)sim.getPosition()[1]), Color.YELLOW);
                }
            }
            //System.out.println("Apres l'action, le robobo est en " + sim.position.toString() + "avec une vitesse " + sim.vitesse);
        }

        if((int)sim.getPosition()[0] < (im.getWidth()) && (int)sim.getPosition()[0] >= 0){
            if((int)sim.getPosition()[1] < im.getHeight() && (int)sim.getPosition()[1] >= 0){
                im.setPixel( (int)sim.getPosition()[0], (int)sim.getPosition()[1], Color.RED);
            }
        }
        //im.setPixel(java.lang.Math.min(im.getWidth()-1, (int)sim.getPosition()[0]), java.lang.Math.min(im.getHeight()-1, (int)sim.getPosition()[1]), Color.RED);
        return im;
    }


    public ArrayList<RoboboGene> getGenotype() {
        return genotype;
    }
}
