/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tubes.kapita;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author Acer
 */
public class TubesKapita {
    private final int jmlHari;
    private final int jmlKuotaShift;
    private final int jmlKromosom;
    private final int popSize;
    private final int maxIterasi;
    private final int jmlIndeksAnggota;
    private final double pc;
    private final double pm;
    
    private double p[][];
    private double pTerbaik[];
    private double cc[][];
    private final double cm[][];
     
    private int dataAnggota[][];
    private String namaAnggota[][]; 

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        // TODO code application logic here
	TubesKapita tk = new TubesKapita();
        tk.openDataset();
        tk.prosesTubesKapita();
    }
    
    public TubesKapita(){

        Scanner sc = new Scanner(System.in);
        System.out.print("Masukkan nilai popsize: ");
        this.popSize = sc.nextInt();
        System.out.print("Masukkan jumlah max iterasi: ");
        this.maxIterasi = sc.nextInt();
        System.out.print("Masukkan jumlah anggota per-shift: ");
        this.jmlKuotaShift = sc.nextInt();
        System.out.print("Masukkan jumlah hari: ");
        this.jmlHari = sc.nextInt();
        
        //this.popSize = 3;
        //this.maxIterasi = 250;
        //this.jmlKuotaShift = 12;
        //this.jmlHari = 5;
        
        this.jmlIndeksAnggota = 111;
        this.jmlKromosom = this.jmlKuotaShift * 2 * this.jmlHari;
        this.p = new double[this.popSize][this.jmlKromosom + 1];
        
        this.pc = 0.5;
        this.pm = 0.2;
        
        int osCrossover = (int) Math.ceil(this.pc*this.popSize);
        int osMutation = (int) Math.ceil(this.pm*this.popSize);
        this.cc = new double[osCrossover][this.jmlKromosom + 1];
        this.cm = new double[osMutation][this.jmlKromosom + 1];
        this.pTerbaik = new double[this.jmlKromosom + 1];
        
        this.dataAnggota = new int[111][10];
        this.namaAnggota = new String[111][4];
    }
    
    public void openDataset() throws FileNotFoundException{  
        String currentDirectory = System.getProperty("user.dir");
        String loc =  currentDirectory+"\\src\\tubes\\kapita\\";
        Scanner jadwal = new Scanner(new File(loc+"KapitaJadwal.txt"));
        Scanner anggota = new Scanner(new File(loc+"DataKapita.txt"));
        

        String temp;
        int counter = 0;
        while(jadwal.hasNext()){
            temp = jadwal.next();
            String []arr = temp.split(",");
            for (int j = 0; j < 10; j++) {
                this.dataAnggota[counter][j] = Integer.parseInt(arr[j]);
            }
            counter++;
        }
        
 
        String temp2;
        int counter2 = 0;
        while(anggota.hasNextLine()){
            temp2 = anggota.nextLine();
            String []arr = temp2.split(",");
            for (int j = 0; j < 4; j++) {
                this.namaAnggota[counter2][j] = arr[j];
            }
            counter2++;
        }
    }
    
    public void inisialisasi(){
        int counter = 0;
        int Min = 0;
        int Max = this.jmlIndeksAnggota - 1;
        Random r = new Random();
        
        for (int i = 0; i < this.popSize; i++) {
            for (int j = 0; j < this.jmlHari; j++) {  
                for (int k = 0; k < 2; k++) {
                    ArrayList<Integer> arr = new ArrayList<>();
                    for (int l = 0; l < this.jmlKuotaShift; l++) {
                        int temp;
                        if(arr.isEmpty()){
                            temp = Min + (int)(Math.random() * ((Max - Min) + 1));
                        }else{
                            temp = getRandomWithExclusion2(r, Min, Max, arr);
                        }
                        arr.add(temp);
                        Collections.sort(arr);
                        this.p[i][counter++] = temp;
                    }
                    arr.clear();
                }
            }

    
            this.p[i][this.p[i].length - 1] = hitungFitness(this.p[i]);
            counter = 0;
        }
    }
    public void crossover(){
        if (this.pc > 0) {
                 
            int p1 = 0 + (int) (Math.random() * (((this.popSize - 1) - 0) + 1));
            int p2 = 0 + (int) (Math.random() * (((this.popSize - 1) - 0) + 1));
            
         
            int cutPoint = 1 + (int) (Math.random() * (((this.jmlKromosom - 2) - 1) + 1));
            
            double tempCC[][] = new double[this.cc.length][this.jmlKromosom + 1];
            System.arraycopy(this.p[p1], 0, tempCC[0], 0, this.jmlKromosom);
            System.arraycopy(this.p[p2], 0, tempCC[1], 0, this.jmlKromosom);
            for (int i = 0; i < this.jmlKromosom; i++) {
                if (i >= cutPoint) {
                    tempCC[0][i] = this.p[p2][i];
                    tempCC[1][i] = this.p[p1][i];
                }
                tempCC[0][tempCC[0].length - 1] = hitungFitness(tempCC[0]);
                tempCC[1][tempCC[1].length - 1] = hitungFitness(tempCC[1]);
            }
            System.arraycopy(tempCC, 0, this.cc, 0, tempCC.length);
        }
    }
    public void mutation(){
        if (this.pm > 0) {
            int p1 = 0 + (int) (Math.random() * (((this.popSize - 1) - 0) + 1));
            Random r = new Random();
            int pos1 = 0 + (int) (Math.random() * (((this.jmlKromosom - 1) - 0) + 1));
            int pos2 = getRandomWithExclusion(r, 0, this.jmlKromosom - 1, pos1);
            
          
            System.arraycopy(this.p[p1], 0, this.cm[0], 0, this.jmlKromosom);
            this.cm[0][pos1] = this.p[p1][pos2];
            this.cm[0][pos2] = this.p[p1][pos1];

            
            this.cm[0][this.cm[0].length - 1] = hitungFitness(this.cm[0]);
        }
    }
    
    public void seleksi(){
        double popSeleksi[][] = new double[this.popSize+this.cc.length+this.cm.length]
                                [this.jmlKromosom + 1];
        
        System.arraycopy(this.p, 0, popSeleksi, 0, this.popSize);
        System.arraycopy(this.cc, 0, popSeleksi, this.popSize, this.cc.length);
        System.arraycopy(this.cm, 0, popSeleksi, this.popSize + this.cc.length, this.cm.length);
        
     
        double totFitness = 0;
        for (double[] popSeleksi1 : popSeleksi) {
            totFitness += popSeleksi1[this.jmlKromosom];
        }
        
        double prob[][] = new double[popSeleksi.length][3];
        double probCum = 0;
        for (int i = 0; i < prob.length; i++) {
            for (int j = 0; j < 3; j++) {
                switch (j) {
                    case 0:
                        prob[i][j] = popSeleksi[i][this.jmlKromosom];
                        break;
                    case 1:
                        prob[i][j] = popSeleksi[i][this.jmlKromosom] / totFitness;
                        break;
                    case 2:                
                        probCum += prob[i][1];
                        prob[i][j] = probCum;
                        break;
                    default:
                        break;
                }
            }
        }
       
        double p_rw[][];
        
        p_rw = elitism(popSeleksi);      
      
        updatePopulasi(p_rw);
    }
    
    public void updatePopulasi(double pSeleksi[][]){
        this.p = pSeleksi;
    }
    
    public double[][] elitism(double sel[][]){
        double temp[][] = sel;
        double hasil[][] = new double[this.popSize][this.p[0].length];
        Arrays.sort(temp, new java.util.Comparator<double[]>() {
            public int compare(double[] a, double[] b) {
                return Double.compare(b[b.length - 1], a[a.length - 1]);
            }
        });
        System.arraycopy(temp, 0, hasil, 0, this.popSize);
        return hasil;
    }
    
    public double[][] rouletteWheel(double sel[][], double prob[][]){
        double hasil[][] = new double[this.p.length][this.p[0].length];
        for (int i = 0; i < hasil.length; i++) {
            double r = Math.random();
            for (int j = sel.length-1; j >= 0; j--) {
                if(r <= prob[j][2]){
                    hasil[i] = sel[j];
                }
            }
        }
        return hasil;
    }
    
    public double hitungFitness(double kromosom[]){
        double fitness;
        int penalti1 = 0, penalti2 = 0, penalti3 = 0, penalti4 = 0, 
            penalti5 = 0, penalti6 = 0, penalti7 = 0, penalti8 = 0, 
            penalti9 = 9, penalti10 = 0, totalPenalti;
            for (int i = 0; i < this.jmlKromosom; i++) {
                if(this.dataAnggota[(int)kromosom[i]][0] == 0 && (i >= 0 && i <= 11)){
                    penalti1++;
                }else if(this.dataAnggota[(int)kromosom[i]][1] == 0 && (i >= 12 && i <= 23)){
                    penalti2++;
                }else if(this.dataAnggota[(int)kromosom[i]][2] == 0 && (i >= 24 && i <= 35)){
                    penalti3++;
                }else if(this.dataAnggota[(int)kromosom[i]][3] == 0 && (i >= 36 && i <= 47)){
                    penalti4++;
                }else if(this.dataAnggota[(int)kromosom[i]][4] == 0 && (i >= 48 && i <= 59)){
                    penalti5++;
                }else if(this.dataAnggota[(int)kromosom[i]][5] == 0 && (i >= 60 && i <= 71)){
                    penalti6++;
                }else if(this.dataAnggota[(int)kromosom[i]][6] == 0 && (i >= 72 && i <= 83)){
                    penalti7++;
                }else if(this.dataAnggota[(int)kromosom[i]][7] == 0 && (i >= 84 && i <= 95)){
                    penalti8++;
                }else if(this.dataAnggota[(int)kromosom[i]][8] == 0 && (i >= 96 && i <= 107)){
                    penalti9++;
                }else if(this.dataAnggota[(int)kromosom[i]][9] == 0 && (i >= 108 && i <= 119)){
                    penalti10++;
                }
            }
        
        totalPenalti = penalti1 + penalti2 + penalti3 + penalti4 + penalti5 + 
                penalti6 + penalti7 + penalti8 + penalti9 + penalti10;
        int tidakKebagianPiket = anggotaTidakKebagianPiket(kromosom);
        fitness = (double) 1 / (1 + totalPenalti + tidakKebagianPiket);
        return fitness;
    }
    public int anggotaTidakKebagianPiket(double kromosom[]){        
        int iAnggotaPiket[] = removeDuplicates(kromosom);
        int iAnggotaTidakPiket[] = new int[this.namaAnggota.length-iAnggotaPiket.length];                
        int counter = 0, counter2 = 0;
        for (int i = 0; i < this.namaAnggota.length; i++) {
            if(i == iAnggotaPiket[counter]){
                counter++;
            }else{
                iAnggotaTidakPiket[counter2++] = i;
            }
            if((counter - 1) == iAnggotaPiket.length - 1){
                counter--;
            }
        }return iAnggotaTidakPiket.length;
    }
    public int[] removeDuplicates(int[] arr){
        boolean set[] = new boolean[1001];
        int totalItems = 0;
        for (int i = 0; i < arr.length; ++i) {
            if(!set[arr[i]]){
                set[arr[i]] = true;
                totalItems++;
            }
        }
        int[] ret = new int[totalItems];
        int c = 0;
        for (int i = 0; i < set.length; i++) {
            if(set[i]){
                ret[c++] = i;
            }
        }return ret;
    }
    
    public void cariIndividuTerbaik(){
        double max = 0.0;
        double tempPTerbaik[] = new double[this.p[0].length];
        // cari individu terbaik dalam populasi
        for (int i = 0; i < this.popSize; i++) {
            if(max < this.p[i][this.p[i].length-1]){
                max = this.p[i][this.p[i].length-1];
                tempPTerbaik = this.p[i];
            }
        }
        // cek jika individu sebelumnya pertama lebih baik
        if (tempPTerbaik[tempPTerbaik.length-1] < this.pTerbaik[this.pTerbaik.length-1]) {
            this.pTerbaik = this.pTerbaik;
        }else{
            this.pTerbaik = tempPTerbaik;
        }
        
    }
    
    public void prosesTubesKapita(){
        inisialisasi();
        cariIndividuTerbaik();
        
        for (int i = 0; i < this.maxIterasi; i++) {
            
            crossover();
               
            mutation();
             
            seleksi();
            cariIndividuTerbaik();
               
                printFitnessIndividuTerbaik();
                
        }
        printIndividuTerbaik();
        decoding();
        printTidakPiket();
    }
    
    public int getRandomWithExclusion(Random rnd, int start, int end, int... exclude) {
        int random = start + rnd.nextInt(end - start + 1 - exclude.length);
        for (int ex : exclude) {
            if (random < ex) {
                break;
            }
            random++;
        }
        return random;
    }
    
    public int getRandomWithExclusion2(Random rnd, int start, int end, ArrayList<Integer> exclude) {        
        int arr[] = new int[exclude.size()];
        for (int i = 0; i < exclude.size(); i++) {
            arr[i] = exclude.get(i);
        }
        int random = start + rnd.nextInt(end - start + 1 - arr.length);
        for (int ex : arr) {
            if (random < ex) {
                break;
            }
            random++;
        }
        return random;
    }
    
    public void printKromosom(){
        System.out.println("Kromosom: ");
        for (int i = 0; i < this.popSize; i++) {
            for (int j = 0; j < this.jmlKromosom + 1; j++) {
                System.out.print(this.p[i][j]+" ");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    public void printCrossover(){
        System.out.println("Crossover: ");
        for (double[] cc1 : this.cc) {
            for (int j = 0; j < this.jmlKromosom + 1; j++) {
                System.out.print(cc1[j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    public void printMutation(){
        System.out.println("Mutation: ");
        for (double[] cm1 : this.cm) {
            for (int j = 0; j < this.jmlKromosom + 1; j++) {
                System.out.print(cm1[j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    public void printIndividuTerbaik(){
        System.out.println("Individu Terbaik: ");
        for (int j = 0; j < this.jmlKromosom+1; j++) {
            System.out.print(this.pTerbaik[j]+" ");
        }
        System.out.println("\n");
    }
    
    public void printFitnessIndividuTerbaik(){
        System.out.println(this.pTerbaik[this.pTerbaik.length-1]);
    }
    
    public void printTidakPiket(){        
        int iAnggotaPiket[] = removeDuplicates(this.pTerbaik);
        int iAnggotaTidakPiket[] = new int[this.namaAnggota.length-iAnggotaPiket.length];        
        
        for (int x = 0; x < iAnggotaPiket.length; x++) {
            System.out.print(iAnggotaPiket[x]+" ");
        }
        System.out.println();
        System.out.println("jumlah anggota yang dapat jadwal piket: "+iAnggotaPiket.length);
        
        int counter = 0, counter2 = 0;
        for (int i = 0; i < this.namaAnggota.length; i++) {
            if(i == iAnggotaPiket[counter]){
                counter++;
            }else{
                iAnggotaTidakPiket[counter2++] = i;
            }
            
            if((counter - 1) == iAnggotaPiket.length - 1){
                counter--;
            }
        }
        
        for (int x = 0; x < iAnggotaTidakPiket.length; x++) {
            System.out.print(iAnggotaTidakPiket[x]+" ");
        }
        System.out.println();
        System.out.println("jumlah anggota yang tidak dapat jadwal piket: "+iAnggotaTidakPiket.length);
    }
    
    public int[] removeDuplicates(double[] arr){
        boolean set[] = new boolean[1001];
        int totalItems = 0;
        
        for (int i = 0; i < arr.length; ++i) {
            if(!set[(int)arr[i]]){
                set[(int)arr[i]] = true;
                totalItems++;
            }
        }
        
        int[] ret = new int[totalItems];
        int c = 0;
        for (int i = 0; i < set.length; i++) {
            if(set[i]){
                ret[c++] = i;
            }
        }
        return ret;
    }
    
    public void decoding(){
        int counter = 0;
        System.out.println("Hasil Penjadwalan Piket: ");
        for (int m = 0; m < this.jmlHari; m++) {
            String hari = "";
            switch (m) {
                case 0:
                    hari = "Senin";
                    break;
                case 1:
                    hari = "Selasa";
                    break;
                case 2:
                    hari = "Rabu";
                    break;
                case 3:
                    hari = "Kamis";
                    break;
                case 4:
                    hari = "Jumat";
                    break;
            }
            System.out.printf("Hari %s\n", hari);
            for (int n = 0; n < 2; n++) {
                String shift = "";
                switch (n) {
                    case 0:
                        shift = "Pagi";
                        break;
                    case 1:
                        shift = "Siang";
                        break;
                }
                System.out.printf("Shift %-8s: ", shift);
                for (int i = 0; i < this.jmlKuotaShift; i++) {
                    if(i == this.jmlKuotaShift-1){
                        System.out.print(this.namaAnggota[(int) this.pTerbaik[counter++]][0]);
                    }else{
                        System.out.print(this.namaAnggota[(int) this.pTerbaik[counter++]][0] + ", ");
                    }
                }
                System.out.println();
            }
        }
        System.out.println();
    }
}
