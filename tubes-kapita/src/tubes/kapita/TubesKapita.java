/* Program Algoritma Genetika ini dibuat untuk memenuhi nilai UAS Matakuliah Kapita Selekta 
Informatika - ITERA tahun ajaran 2020

Anggota Kelompok 2 ( Kapita-RA )

Rizki Bhaskara Mulya Efendi - 14117084
Laurensius Joshua Anrico Agustinus - 14117141
LEO VIRANDA MILLENNIUM - 14117167
Muhammad Nur Faqqih - 14117168

*/
package tubes.kapita;

// Libraries yang dibutuhkan untuk menjalan kan program

import java.util.Collections;
import java.util.Random;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.io.File;
import java.util.Scanner;

public class TubesKapita {

    // Deklarasi Variabel
  
    private final double pc;
    private final int jmlHari;
    private final int jmlKromosom;
    private final int jmlKuotaShift;
    private final int popSize;
    private final int maxIterasi;
    private final double pm;
    private final int jmlIndeksAnggota;

    private String namaAnggota[][];  
    private int dataAnggota[][];
    
    private double cc[][];
    private final double cm[][];
    private double p[][];
    private double pTerbaik[];
    
    // Deklarasi Main 

    public static void main(String[] args) throws FileNotFoundException {
    
	TubesKapita tk = new TubesKapita();
        tk.openDataset();
        tk.prosesTubesKapita();
    }
    
    public TubesKapita(){

        Scanner sc = new Scanner(System.in);
        System.out.print("Inputkan nilai popsize: ");
        this.popSize = sc.nextInt();
        System.out.print("Inputkan jumlah maksimum iterasi: ");
        this.maxIterasi = sc.nextInt();
        System.out.print("Inputkan jumlah anggota per-shift: ");
        this.jmlKuotaShift = sc.nextInt();
        System.out.print("Inputkan jumlah harinya: ");
        this.jmlHari = sc.nextInt();
        
        /* Parameter dibawah ini merupakan contoh condition yang harus 
        dipenuhi saat menjaalakan program */
        
        //this.popSize = 3;
        //this.maxIterasi = 250;
        //this.jmlKuotaShift = 12;
        //this.jmlHari = 5;
        
        this.jmlIndeksAnggota = 111;
        this.jmlKromosom = this.jmlKuotaShift * 2 * this.jmlHari;
        this.p = new double[this.popSize][this.jmlKromosom + 1];
        
        this.pc = 0.5;
        this.pm = 0.2;
        
        /* membuat sebuah variabel dengan menggunakan fungs ceil, yang 
            berfungsi menghasilkan sebuat bilangan dari parameter yang berikan
            yang mana bilangannnya berupa sebuah bilangan yang beruapa pembulatan
            dari parameter yang diberikan

            pembulatan berdasarkan ke arah yang besar

            misalkan : -0.65 mejadi 0.0 
                        2,46 menjadi 3.0
        
        */
        
        int osCrossover = (int) Math.ceil(this.pc*this.popSize);
        int osMutation = (int) Math.ceil(this.pm*this.popSize);
        this.cc = new double[osCrossover][this.jmlKromosom + 1];
        this.cm = new double[osMutation][this.jmlKromosom + 1];
        this.pTerbaik = new double[this.jmlKromosom + 1];
        
        this.dataAnggota = new int[111][10];
        this.namaAnggota = new String[111][4];
    }
    
    public void openDataset() throws FileNotFoundException{  

        /* Data yang menjadi acuan dalam program ini diambil dari luar program
            yang mana ada di direktori package dari program ini

            Ada 2 file yang berisi data yang diperlukan dalam menjalankan program ini
            Yaitu : 1. DataKapita.txt 
                        -> berisi data-data nama mahasiswa , yang akan dijadikan data
                        dari nama-nama yang diacak untuk mendapatkan jadwal piket 
                        sesuai dengan ketersediaan jadwalnya menggunakan konsep algoritma
                        genetika

                    2. KapitaJadwal.txt
                        -> berisi jadwal yang tersedia atau tidak , dengan diwakilkan
                        0 dan 1 , yang berarti tersedia atau tidak 

        */

        String currentDirectory = System.getProperty("user.dir");
        String loc =  currentDirectory+"\\src\\tubes\\kapita\\";
        Scanner jadwal = new Scanner(new File(loc+"KapitaJadwal.txt"));
        Scanner anggota = new Scanner(new File(loc+"DataKapita.txt"));

        String temp;
        int counter = 0;

        /* Kode dibawah ini berfungsi untuk mengambil data dari jadwal selama 
           masih ada isi node yang setelah dari gerbong yang terakhir diambil
           ( hasNext ), kemudian dimasukan kedalam variabel dataAnggota 
        */
         
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

        /* Kode dibawah ini berfungsi untuk mengambil data dari anggota selama 
           masih ada isi node yang setelah dari gerbong yang terakhir diambil
           ( hasNext ), kemudian dimasukan kedalam variabel NamaAnggota 
        */

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

        // inisialisasi setelah data dikumpulkan dari input
        
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

    // Method dibawah ini berfungsi untuk proses cross over 

    public void crossover(){
        if (this.pc > 0) {
             
            // Disini kita meelakukan sebuah deklarasi 

            int p1 = 0 + (int) (Math.random() * (((this.popSize - 1) - 0) + 1));
            int p2 = 0 + (int) (Math.random() * (((this.popSize - 1) - 0) + 1));
         
            int cutPoint = 1 + (int) (Math.random() * (((this.jmlKromosom - 2) - 1) + 1));
            
            double tempCC[][] = new double[this.cc.length][this.jmlKromosom + 1];
            System.arraycopy(this.p[p1], 0, tempCC[0], 0, this.jmlKromosom);
            System.arraycopy(this.p[p2], 0, tempCC[1], 0, this.jmlKromosom);

            // Pengesetan nilai 

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

    /* Method dibawah ini berfungsi untuk proses mutasi , setelah didaptakannya
       individu atau gen yaitu pos1 dan pos2 
    */

    public void mutation(){
        if (this.pm > 0) {

            // Deklarasi

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
    
    /* Method dibawah ini berfungsi untuk melakukan seleksi untuk mendapatkan 
       Gen atau invidu terbaik yang dipilih berdasarkan nilai fitness yang
       terbaik
    */

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
    
    //  Mengupdate populasi setelah didapatkannya individu baru 

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
    
    /* Fungsi yang berguna untuk menghitung nilai fitness dari setiap individu
       Nilai fitness , didapatkan dari data jadwal yang tersedia yang ada di file 
       KapitaJadwal.txt , yang kami diterjemahkan menjadi nilai fitness dari 
       sebuah individu
    */

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

    /* Fungsi yang akan menghitung berapa banyak anggota yang tidak kebagian
       piket , jika memang ternyata input yang diberikan tidak dapat memberikan hasil
       yang maksimal agar setiap anggota bisa melakukan piket 
    */ 

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

    // Menghapus array yang duplikat ( jika ada )

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

    // Method yang berfugnsi untuk memilih individu terbaik yang tersedia didalam data
    
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

    // Method utama yang berisi penjalanan dari semua fungsi dan method yang diperlukan
    
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
    
    // Menampilkan kromosom yang telah di dapatkan datanya

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
    
    // menampilkan data crossover yang telah terjadi

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
    
    // menampilkan data mutasi yang telah terjadi

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

    // Menampilkan individu terbaik yang telah di lakukan seleksi
    
    public void printIndividuTerbaik(){
        System.out.println("Individu Terbaik: ");
        for (int j = 0; j < this.jmlKromosom+1; j++) {
            System.out.print(this.pTerbaik[j]+" ");
        }
        System.out.println("\n");
    }

    // Menampilkan nilai fitness dari individu terbaik yang telah di lakukan seleksi
    
    public void printFitnessIndividuTerbaik(){
        System.out.println(this.pTerbaik[this.pTerbaik.length-1]);
    }
    
    // Menampilkan data Anggota yang mendapat piket dan yang tidak 

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

    /* Method yang berfungsi menampilkan hasil penjadwalan berdasarkan hari 
        dan shift pagi atau sore berdasarkan penjadwalan yang sudah dilakukan
    */
    
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
                        shift = "Sore";
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
