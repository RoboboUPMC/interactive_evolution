package com.example.superprojet.robobo2.genome;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.rob.IRob;

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

    /**
     * Creates a new random behavior
     * It takes an IRob (used to control the RBB) as parameter
     * It is used when initializing (or resetting) the population
     * @param iRob
     */
    public RoboboDNA(IRob iRob){
        rob = iRob;
        Integer seqSize = 20;
        Random r = new Random();
        for(int i = 0 ; i < seqSize ; i++){
            genotype.add(new RoboboGene(rob, RoboboGene.MvmtType.values()[r.nextInt(RoboboGene.MvmtType.values().length)]));
        }

    }

    /**
     * Creates a new behavior using a String
     * It is used when loading a previously saved behavior
     * @param r
     * @param s
     */
    public RoboboDNA(IRob r ,String s){
        rob = r;
        String [] mot  = s.split(" ");

        for(int i=0;i<mot.length;i=i+4){
            this.getGenotype().add(new RoboboGene(rob,mot[i],Integer.parseInt(mot[i+1]),Integer.parseInt(mot[i+2]),Long.parseLong(mot[i+3])));
        }
    }

    /**
     * May mutate the genotype
     * Each mutation has a chance of losing or gaining a gene
     */
    public void mutate(){
        int currSize = this.genotype.size();

        //set arbitrarily, may change
        int idealSize = 20;

        ArrayList<RoboboGene> newGenotype = new ArrayList<>();
        float mutaProba = 1f/currSize;
        float lossProba = Math.min(1f, currSize/(2*idealSize));
        float lossOrGainProba = .2f;
        float r = 0;
        Random random = new Random();
        RoboboGene currGene = null;

        for(int i = 0; i<currSize; i++){
            r = random.nextFloat();
            if(r < mutaProba)
            {
                r = random.nextFloat();
                if(r>=lossOrGainProba)
                {
                    currGene = this.getGenotype().get(i);
                    currGene.mutate();
                    newGenotype.add(currGene);
                }
                else
                {
                    r = random.nextFloat();
                    if (r>=lossProba)
                    {
                        newGenotype.add(this.getGenotype().get(i));
                        RoboboGene newGene = new RoboboGene(this.rob, RoboboGene.MvmtType.FORWARD);
                        newGene.mutate();
                        newGenotype.add(newGene);
                    }
                }
            }
            else newGenotype.add(this.getGenotype().get(i));
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


    /**
     * calls every RoboboGene.run() one by one
     * The operation mode is set to unsafe (1) beforehand because the falling detection
     * is flawed and the robot won't move eon certain surfaces
     * All LEDs are turned off before and after the execution
     */
    public void exec(){

        com.mytechia.robobo.util.Color off = new com.mytechia.robobo.util.Color(0,0,0);
        try {
            rob.setOperationMode((byte)1);
            for(int i = 1; i <= 9; i++){

                rob.setLEDColor(i, off);

            }
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
        for(int i = 1; i <= 9; i++){
            try {
                rob.setLEDColor(i, off);
            } catch (InternalErrorException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Generates the bitmap preview of the behavior
     * the first pixels are in red and the last are in red
     * the other pixels are alternatively set to yellow and green
     * depending on the parameter.
     * That way every other behavior has the same color to facilitate readability
     * @param ival
     * @return
     */
    public Bitmap DNAtoImage(int ival){
        RoboboDNASim sim = new RoboboDNASim();
        ArrayList<int[]> pixels = new ArrayList<int[]>();
        int maxX = (int) sim.getPosition()[0];
        int minX = (int) sim.getPosition()[0];
        int maxY = (int) sim.getPosition()[1];
        int minY = (int) sim.getPosition()[1];
        int imHeight;
        int imWidth;

        pixels.add(new int[]{(int) sim.getPosition()[0], (int) sim.getPosition()[1]});
        for(RoboboGene gene: this.getGenotype()){
            RoboboGene.MvmtType mv = gene.mvmtType;
            double[] action = sim.mvmtToAction(mv);
            if(action[0] == 0){
                sim.execAction(action);
                pixels.add(new int[]{(int) sim.getPosition()[0], (int) sim.getPosition()[1]});
            }
            else{
                for(int i = 0; i < 25; i++){
                    sim.execAction(action);
                    pixels.add(new int[]{(int) sim.getPosition()[0], (int) sim.getPosition()[1]});
                }
            }
        }

        pixels.add(new int[]{(int) sim.getPosition()[0], (int) sim.getPosition()[1]});


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

        return im;
    }



    public ArrayList<RoboboGene> getGenotype() {
        return genotype;
    }
}
