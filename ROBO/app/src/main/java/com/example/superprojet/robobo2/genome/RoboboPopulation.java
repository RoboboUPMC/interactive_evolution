package com.example.superprojet.robobo2.genome;

import com.mytechia.robobo.framework.RoboboManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Quentin on 2017/04/05.
 */

public class RoboboPopulation {

    ArrayList<RoboboDNA> pop = new ArrayList<>();


    public void init(int popSize, RoboboManager roboboManager)
    {
        pop.clear();
        int i;
        for (i = 0; i < popSize; i++)
        {
            pop.add(new RoboboDNA(roboboManager));
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

    public int distLevenshtein(RoboboDNA individu_1) {

        int long1 = individu_1.getGenotype().size();
        int min_val = Integer.MAX_VALUE;
        for (RoboboDNA individu_2 : this.pop)
        {
            int long2 = individu_2.getGenotype().size();

            int[][] d = new int[long1 + 1][long2 + 1];
            int substitutionCost;
            int deletionCost = 1;
            int insertionCost = 1;

            for (int i = 0; i < d.length; i++) {
                d[i][0] = i;
            }

            for (int j = 0; j < d[0].length; j++) {
                d[0][j] = j;
            }

            for (int i = 0; i < d.length; i++) {
                for (int j = 0; j < d[0].length; j++) {
                    if (individu_1.getGenotype().get(i).equals(individu_1.getGenotype().get(j))) {
                        substitutionCost = 0;
                    } else {
                        substitutionCost = 1;
                    }
                    d[i][j] = Math.min(d[i - 1][j] + deletionCost, Math.min(d[i][j - 1] + insertionCost, d[i - 1][j - 1] + substitutionCost));
                }
            }

            if (d[long1][long2] < min_val){min_val = d[long1][long2];}
        }

        return min_val;

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
        ArrayList<Float> proba = new ArrayList<Float>(this.pop.size());
        float sumProba = 0;
        float cumulSum = 0;

        for(int i=0; i<this.pop.size(); i++){
            float currProba = ( 1f / (this.pop.get(i).selectionCounter + 1) * this.pop.size());
            proba.set(i, currProba);
            sumProba += currProba;
        }

        for(int i=0; i<this.pop.size(); i++){
            float normalizedProba =  proba.get(i) * (1f / sumProba);
            cumulSum += normalizedProba;
            proba.set(i, cumulSum);
        }

        float r = random.nextFloat();
        for(int i=0; i<this.pop.size(); i++){
            if(proba.get(i)<r){
                parent= this.pop.get(i);
                break;
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
        ArrayList<Float> mean = new ArrayList<>(8);
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
                    mean.set(a, .5f);
                    break;

                case BACKWARDS:
                    mean.set(b, .5f);
                    break;

                case TURN_LEFT:
                    mean.set(c, .5f);
                    break;

                case TURN_RIGHT:
                    mean.set(d, .5f);
                    break;

                case FORWARD_LEFT:
                    mean.set(e, .5f);
                    break;

                case FORWARD_RIGHT:
                    mean.set(f, .5f);
                    break;

                case BACKWARDS_LEFT:
                    mean.set(g, .5f);
                    break;

                case BACKWARDS_RIGHT:
                    mean.set(h, .5f);
                    break;

            }
        }
        for(RoboboGene mvmt : p2.getGenotype()){
            switch (mvmt.getMvmtType()) {

                case FORWARD:
                    mean.set(a, .5f);
                    break;

                case BACKWARDS:
                    mean.set(b, .5f);
                    break;

                case TURN_LEFT:
                    mean.set(c, .5f);
                    break;

                case TURN_RIGHT:
                    mean.set(d, .5f);
                    break;

                case FORWARD_LEFT:
                    mean.set(e, .5f);
                    break;

                case FORWARD_RIGHT:
                    mean.set(f, .5f);
                    break;

                case BACKWARDS_LEFT:
                    mean.set(g, .5f);
                    break;

                case BACKWARDS_RIGHT:
                    mean.set(h, .5f);
                    break;

            }
        }

        ArrayList<Integer> nbOcc = new ArrayList<>(8);

        Integer[][] adjaTable = new Integer[8][8];
        for(int i = 0 ; i < 8 ; i++){
            for(int j = 0 ; j < 8 ; i++){
                adjaTable[i][j] = 0;
            }
        }
        Float max = Collections.max(mean);

        while(max > 0){
            ArrayList<Integer> indexes = new ArrayList<>();
            int i;
            for(i = 0 ; i < mean.size() ; i++){
                if(mean.get(i) >= max){
                    indexes.add(i);
                    mean.set(i, max-1);
                }
            }
            Random r = new Random();
            i = indexes.get(r.nextInt(indexes.size()));
            nbOcc.set(i, nbOcc.get(i)==null?1:nbOcc.get(i)+1);
            max = Collections.max(mean);
        }


        for(int i = 0 ; i < p1.getGenotype().size() ; i++){
            adjaTable[p1.getGenotype().get(i).getMvmtType().ordinal()][p1.getGenotype().get((i+1) % p1.getGenotype().size()).getMvmtType().ordinal()] +=1;
            adjaTable[p1.getGenotype().get(i).getMvmtType().ordinal()][p1.getGenotype().get((i-1) % p1.getGenotype().size()).getMvmtType().ordinal()] +=1;
        }
        for(int i = 0 ; i < p2.getGenotype().size() ; i++){
            adjaTable[p1.getGenotype().get(i).getMvmtType().ordinal()][p1.getGenotype().get((i+1) % p1.getGenotype().size()).getMvmtType().ordinal()] +=1;
            adjaTable[p1.getGenotype().get(i).getMvmtType().ordinal()][p1.getGenotype().get((i-1) % p1.getGenotype().size()).getMvmtType().ordinal()] +=1;
        }
        for(int i = 0 ; i < nbOcc.size() ; i++){
            if(nbOcc.get(i) == null){
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
        while(!nbOcc.equals(Arrays.asList(new Integer[] { 0, 0, 0, 0, 0, 0, 0, 0 }))){

            // update of the number of occurrences table and adjacency table
            child.add(index, chosenMvmt);
            nbOcc.set(chosenMvmt, nbOcc.get(nbOcc.indexOf(chosenMvmt))-1);

            // We list all the neighbors that have the maximum adjacency with our chosen point
            for(int i = 0 ; i < 8 ; i++){
                adjaTable[i][chosenMvmt] = nbOcc.get(nbOcc.indexOf(chosenMvmt)) == 0 ? 0 : adjaTable[i][chosenMvmt]-1;
            }
            ArrayList<Integer> candidates = new ArrayList<>();
            Integer bestNeigh = 0;
            for(int i = 0 ; i < 8 ; i++){
                if(adjaTable[chosenMvmt][i] > adjaTable[chosenMvmt][bestNeigh]){
                    candidates.clear();
                    candidates.add(i);
                    bestNeigh = adjaTable[chosenMvmt][i];
                }
                else if(adjaTable[chosenMvmt][i] == adjaTable[chosenMvmt][bestNeigh]){
                    bestNeigh = adjaTable[chosenMvmt][i];
                }
            }

            chosenMvmt = candidates.get(r.nextInt(candidates.size()));
            index += r.nextInt(1);

        }

        return new RoboboDNA(p1.getGenotype().get(0).getRoboboManager(), child);

    }


    public RoboboPopulation noveltySearch(RoboboPopulation nspop){
        //this.purge();
        return null;
    }


    public ArrayList<RoboboDNA> getPop() {
        return pop;
    }
}
