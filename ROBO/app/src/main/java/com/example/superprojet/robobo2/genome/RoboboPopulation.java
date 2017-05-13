package com.example.superprojet.robobo2.genome;

import android.util.Log;

import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.rob.IRob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;

/**
 * Created by Quentin on 2017/04/05.
 */

public class RoboboPopulation {

    ArrayList<RoboboDNA> pop = new ArrayList<>();


    public void init(int popSize, IRob rob)
    {
        pop.clear();
        int i;
        for (i = 0; i < popSize; i++)
        {
            pop.add(new RoboboDNA(rob));
        }
    }

    public void setPop(ArrayList<RoboboDNA> pop) {
        this.pop = new ArrayList<>(pop);
    }

    public void purge(ArrayList<Integer> parent_list)
    {
        int i;
        for (i=this.pop.size()-1; i>=0; i--)
        {
            if(!parent_list.contains(i)) this.pop.remove(i);
        }
    }

    public int levensteinCutoff()
    {
        int cutoffVal = Integer.MAX_VALUE;
        int test_val;
        for (RoboboDNA individu_i : this.pop)
        {
            test_val = this.minLevenstein(individu_i);
            if (test_val < cutoffVal) cutoffVal = test_val;
        }
        return cutoffVal;
    }

    public int minLevenstein(RoboboDNA individu_1)//actually maxLevenstein
    {
        int min_val = 0;
        int val;
        for (RoboboDNA individu_2 : this.pop)
        {
            val = distLevenshtein(individu_1, individu_2);
            if (val > min_val){min_val = val;}
        }
        return Math.max((min_val+1) / 2, 3);
    }

    public int maxLevenstein(RoboboDNA individu_1)
    {
        int min_val = Integer.MAX_VALUE;
        int val;
        for (RoboboDNA individu_2 : this.pop)
        {
            val = distLevenshtein(individu_1, individu_2);
            if (val < min_val){min_val = val;}
        }
        return min_val;
    }

    public int distLevenshtein(RoboboDNA individu_1, RoboboDNA individu_2) {

        int long1 = individu_1.getGenotype().size();
        int long2 = individu_2.getGenotype().size();

        int[][] d = new int[long1][long2];
        int substitutionCost;
        int deletionCost = 1;
        int insertionCost = 1;

        for (int i = 0; i < d.length; i++) {
            d[i][0] = i;
        }

        for (int j = 0; j < d[0].length; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i < d.length; i++) {
            for (int j = 1; j < d[0].length; j++) {
                if (individu_1.getGenotype().get(i).equals(individu_2.getGenotype().get(j))) {
                    substitutionCost = 0;
                } else {
                    substitutionCost = 5;
                }
                d[i][j] = Math.min(d[i - 1][j] + deletionCost, Math.min(d[i][j - 1] + insertionCost, d[i - 1][j - 1] + substitutionCost));
            }
        }

        return d[long1-1][long2-1];

    }

    private class Edge {
        RoboboGene g;

        public Edge(RoboboGene g, boolean b){
            this.g = g;
        }
    }


    public RoboboDNA choisirParent(){
        RoboboDNA parent = null;
        Random random = new Random();
        ArrayList<Float> proba = new ArrayList<>();
        float sumProba = 0;
        float cumulSum = 0;

        for(int i=0; i<this.pop.size(); i++){
            float currProba = ( 1f / (this.pop.get(i).selectionCounter + 1) * this.pop.size());
            proba.add(currProba);
            sumProba += currProba;
        }

        for(int i=0; i<this.pop.size(); i++){
            float normalizedProba =  proba.get(i) * (1f / sumProba);
            cumulSum += normalizedProba;
            proba.set(i, cumulSum);
        }


        Log.d("xOver", proba.toString());
        while(parent == null){
            float r = random.nextFloat();
            for(int i=0; i<this.pop.size(); i++){
                if(proba.get(i)<r){
                    parent= this.pop.get(i);
                    break;
                }
            }
        }
        parent.selectionCounter++;
        return parent;
    }

    public RoboboDNA xOver(){

        RoboboDNA p1 = choisirParent();
        RoboboDNA p2 = choisirParent();


        // Class : movement type Integer[] : max number of adjacencies, mean number of occurrences
        HashMap<Class, Float[]> maxOcc = new HashMap<>(8);
        List<Float> mean = Arrays.asList(0.f, 0.f, 0.f, 0.f, 0.f, 0.f, 0.f, 0.f);
        maxOcc.put(MvmtFwd.class, new Float[]{0f, 0f});
        maxOcc.put(MvmtBwd.class, new Float[]{0f, 0f});
        maxOcc.put(MvmtLeft.class, new Float[]{0f, 0f});
        maxOcc.put(MvmtRight.class, new Float[]{0f, 0f});
        maxOcc.put(MvmtFwdLeft.class, new Float[]{0f, 0f});
        maxOcc.put(MvmtFwdRight.class, new Float[]{0f, 0f});
        maxOcc.put(MvmtBwdLeft.class, new Float[]{0f, 0f});
        maxOcc.put(MvmtBwdRight.class, new Float[]{0f, 0f});



        int a = RoboboGene.MvmtType.FORWARD.ordinal();
        int b = RoboboGene.MvmtType.BACKWARDS.ordinal();
        int c = RoboboGene.MvmtType.TURN_LEFT.ordinal();
        int d = RoboboGene.MvmtType.TURN_RIGHT.ordinal();
        int e = RoboboGene.MvmtType.FORWARD_LEFT.ordinal();
        int f = RoboboGene.MvmtType.FORWARD_RIGHT.ordinal();
        int g = RoboboGene.MvmtType.BACKWARDS_LEFT.ordinal();
        int h = RoboboGene.MvmtType.BACKWARDS_RIGHT.ordinal();

        for(RoboboGene mvmt : p1.getGenotype()){
            switch (mvmt.getMvmtType()) {

                case FORWARD:
                    mean.set(a, mean.get(a) + .5f);
                    break;

                case BACKWARDS:
                    mean.set(b, mean.get(b) + .5f);
                    break;

                case TURN_LEFT:
                    mean.set(c, mean.get(c) + .5f);
                    break;

                case TURN_RIGHT:
                    mean.set(d, mean.get(d) + .5f);
                    break;

                case FORWARD_LEFT:
                    mean.set(e, mean.get(e) + .5f);
                    break;

                case FORWARD_RIGHT:
                    mean.set(f, mean.get(f) + .5f);
                    break;

                case BACKWARDS_LEFT:
                    mean.set(g, mean.get(g) + .5f);
                    break;

                case BACKWARDS_RIGHT:
                    mean.set(h, mean.get(h) + .5f);
                    break;

            }
        }
        for(RoboboGene mvmt : p2.getGenotype()){
            switch (mvmt.getMvmtType()) {

                case FORWARD:
                    mean.set(a, mean.get(a) + .5f);
                    break;

                case BACKWARDS:
                    mean.set(b, mean.get(b) + .5f);
                    break;

                case TURN_LEFT:
                    mean.set(c, mean.get(c) + .5f);
                    break;

                case TURN_RIGHT:
                    mean.set(d, mean.get(d) + .5f);
                    break;

                case FORWARD_LEFT:
                    mean.set(e, mean.get(e) + .5f);
                    break;

                case FORWARD_RIGHT:
                    mean.set(f, mean.get(f) + .5f);
                    break;

                case BACKWARDS_LEFT:
                    mean.set(g, mean.get(g) + .5f);
                    break;

                case BACKWARDS_RIGHT:
                    mean.set(h, mean.get(h) + .5f);
                    break;

            }
        }

        List<Integer> nbOcc = Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0);

        Integer[][] adjaTable = new Integer[8][8];
        for(int i = 0 ; i < 8 ; i++){
            for(int j = 0 ; j < 8 ; j++){
                adjaTable[i][j] = 0;
            }
        }
        Float max = Collections.max(mean);

//        while(max > 0){
//            ArrayList<Integer> indexes = new ArrayList<>();
//            int i;
//            for(i = 0 ; i < mean.size() ; i++){
//                if(mean.get(i) >= max){
//                    indexes.add(i);
//                    mean.set(i, max-1);
//                }
//            }
//            Random r = new Random();
//            i = indexes.get(r.nextInt(indexes.size()));
//            nbOcc.set(i, nbOcc.get(i)==null?1:nbOcc.get(i)+1);
//            nbOcc.set(i, max.intValue());
//            max = Collections.max(mean);
//        }
        for(int i = 0 ; i < 8 ; i++){
            nbOcc.set(i, mean.get(i).intValue());
        }


        for(int i = 0 ; i < p1.getGenotype().size() ; i++){
            adjaTable[p1.getGenotype().get(i).getMvmtType().ordinal()][p1.getGenotype().get((i+1) < p1.getGenotype().size()-1 ? i+1 : 0).getMvmtType().ordinal()] +=1;
            adjaTable[p1.getGenotype().get(i).getMvmtType().ordinal()][p1.getGenotype().get((i-1) >= 0 ? i-1 : p1.getGenotype().size()-1).getMvmtType().ordinal()] +=1;
        }
        for(int i = 0 ; i < p2.getGenotype().size()-1 ; i++){
            adjaTable[p2.getGenotype().get(i).getMvmtType().ordinal()][p2.getGenotype().get((i+1) < p1.getGenotype().size()-1 ? i+1 : 0).getMvmtType().ordinal()] +=1;
            adjaTable[p2.getGenotype().get(i).getMvmtType().ordinal()][p2.getGenotype().get((i-1) >= 0 ? i-1 : p1.getGenotype().size()-1).getMvmtType().ordinal()] +=1;
        }
        for(int i = 0 ; i < nbOcc.size() ; i++){
            if(nbOcc.get(i) == 0){
                for(int j = 0 ; j < 8 ; j++){
                    adjaTable[i][j] = 0;
                }
            }
        }

        ArrayList<Integer> child = new ArrayList<>();
        Integer chosenMvmt;
        Random r = new Random();
        do {
            chosenMvmt = r.nextInt(8);
        }while(nbOcc.get(chosenMvmt) == 0);
        Integer index = 0;
        Integer side = r.nextInt(1);



        while(!nbOcc.equals(Arrays.asList(new Integer[] { 0, 0, 0, 0, 0, 0, 0, 0 }))){
            if(chosenMvmt == -1){
                do {
                    chosenMvmt = r.nextInt(8);
                }while(nbOcc.get(chosenMvmt) == 0);
            }

            Log.d("xOver", "chosenMvmt" +" "+chosenMvmt.toString());
            Log.d("xOver", "nbOcc "+nbOcc.toString());
            for(Integer[] i : adjaTable)
                Log.d("xOver", "adjatable "+Arrays.asList(i).toString());

            // update of the number of occurrences table and adjacency table
            child.add(index, chosenMvmt);

            Log.d("xOver", "child" +" "+child.toString());
            nbOcc.set(chosenMvmt, nbOcc.get(chosenMvmt)-1);

            // We list all the neighbors that have the maximum adjacency with our chosen point
            for(int i = 0 ; i < 8 ; i++){
                adjaTable[i][chosenMvmt] = nbOcc.get(chosenMvmt) == 0 ? 0 : (adjaTable[i][chosenMvmt] == nbOcc.get(chosenMvmt)?adjaTable[i][chosenMvmt]-1:adjaTable[i][chosenMvmt]);
            }
            ArrayList<Integer> candidates = new ArrayList<>();
            Integer bestNeigh = 0;
            for(int i = 0 ; i < 8 ; i++){
                if(adjaTable[chosenMvmt][i] > bestNeigh){
                    candidates.clear();
                    candidates.add(i);
                    bestNeigh = adjaTable[chosenMvmt][i];
                }

                else if(adjaTable[chosenMvmt][i] == bestNeigh){
                    bestNeigh = adjaTable[chosenMvmt][i];
                }
            }


            int counter = 0;
             while(counter < candidates.size()){
                 counter++;

                 chosenMvmt = candidates.get(r.nextInt(candidates.size()));//bug here

                 if(!Arrays.asList(adjaTable[chosenMvmt]).equals(Arrays.asList(new Integer[] { 0, 0, 0, 0, 0, 0, 0, 0 }))){
                     break;
                 }

                 nbOcc.set(chosenMvmt, 0);
                 for(int i = 0 ; i < 8 ; i++){
                     adjaTable[i][chosenMvmt] = 0;
                 }

             }



             if(counter == candidates.size()){
                 chosenMvmt = -1;
             }

            index += side;

        }

        Log.d("xOver fin", nbOcc.toString());

        return new RoboboDNA(p1.rob, child);

    }


    public RoboboPopulation noveltySearch(RoboboPopulation nspop, ArrayList<Integer> parentList){
        this.purge(parentList);
        ArrayList<RoboboDNA> offspring = new ArrayList<>();
        RoboboPopulation newGen = new RoboboPopulation();
        Integer nbTries = 0;
        Long maxTime = 5000L;

        Integer safeDistance = Integer.MAX_VALUE;
        for(RoboboDNA parent : this.pop){
            Integer k = minLevenstein(parent);
            if(k < safeDistance){
                safeDistance = k;
            }
        }
        // safeDistance = 0;

        long start = System.currentTimeMillis();
        Integer k = 0;
        while(offspring.size()<10 && System.currentTimeMillis() - start < maxTime){
            k++;
            Log.d("NS", "essai : "+k.toString()+" temps : "+String.valueOf(System.currentTimeMillis() - start) + "trouvés : "+String.valueOf(offspring.size()));
            RoboboDNA child = xOver();
            Log.d("Avant mutation", String.valueOf(child.getGenotype().size()));
            child.mutate();
            Log.d("Avant mutation", String.valueOf(child.getGenotype().size()));
            Log.d("NS", "distance acceptable : "+safeDistance.toString());
            Boolean newEnough = true;
            for(RoboboDNA other : nspop.getPop()){
                if(distLevenshtein(child, other) < safeDistance){
                    newEnough = false;
                    break;
                }
            }
            if(newEnough){
                Log.d("NS", "distance de l'enfant choisi : " + safeDistance.toString());
                offspring.add(child);
                nspop.getPop().add(child);
            }

        }
        newGen.setPop((!offspring.isEmpty()?offspring:this.pop));
        Log.d("NS", "terminé nbChildren: "+String.valueOf(newGen.getPop().size()));
        return newGen;
    }


    public ArrayList<RoboboDNA> getPop() {
        return pop;
    }
}
