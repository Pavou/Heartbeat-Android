package com.avoupavou.heartBeat;

public class DataSeries {

    public static int FS = 30;

    private static float runningMean;
    private static int samples;

    private float[] data;

    private int confidenceWindow;

    private float runningMeanFFT;
    private int samplesFFT;


    private  int size;
    private int pointer;

    private float[] fft_coefs;

    public static void updateFps(float fps){
        runningMean += fps;
        samples++;

        FS = Math.round(runningMean / samples);
    }

    public DataSeries(int size) {
        this.data = new float[size];
        this.size = size;
        this.fft_coefs = new float[size*size*2];
        this.pointer = -1;

        confidenceWindow =0;


        runningMean = 0.0f;
        samples = 0;

        init_fft();
    }

    public void addData(float d){

            d = d < 0.0f ? 0 : d;

            pointer++;
            if (pointer >= size) {
                pointer = 0;
            }

            data[pointer] = d;

        confidenceWindow++;
        confidenceWindow %= this.size;

    }

    public void invalidate(){
        confidenceWindow = 0;
        runningMeanFFT =0.0f;
        samplesFFT =0;
    }



    public float[] getNormData(int n){

        n = n >= size ? size-1 : n;

        float[] out = new float[n];
        n--;
        int lp = pointer;

        while(n >= 0){

            out[n] = data[lp];
            lp--;
            lp = lp < 0 ? size-1 : lp;
            n--;
        }

        return out;
    }

    public float[] getData(){
        return data;
    }

    private void init_fft(){
        for(int i =0;i < size/2+1;i++) {
            for (int j = 0; j < size; j++) {
                this.fft_coefs[(i * size + j) * 2 + 0] = (float) Math.cos(2 * Math.PI * i * j / size);
                this.fft_coefs[(i * size + j) * 2 + 1] = (float) Math.sin(2 * Math.PI * i * j / size);
            }
        }
    }

    public float calc_max_fft(){

        if(confidenceWindow < 50) return  0;

        double max = 0;
        int index = -1;

        int L = Math.max(confidenceWindow,this.size);

        int maxS = (int) Math.floor( 200.0f * this.size / (FS * 60.0f) );
        int minS = (int) Math.floor( 50.0f  * this.size / (FS * 60.0f) );

        int max_sample = L/2 < maxS ?  L/2 : maxS;

        for(int i = minS; i<max_sample; i++){

            double real = 0.0;
            double img  = 0.0;

            for (int j=0; j<L; j++) {
                real += data[j] * Math.cos(2 * Math.PI * i * j / L);
                img  += data[j] * Math.sin(2 * Math.PI * i * j / L);


            }

            double mag = Math.sqrt(real*real + img*img);
            if(max < mag){
                max = mag;
                index = i;
            }
        }

        runningMeanFFT += (float)index * this.FS / L;
        samplesFFT++;

        if(samplesFFT > this.size){
            runningMeanFFT = runningMeanFFT / samplesFFT;
            samplesFFT =1;
        }

        return (float)runningMeanFFT / samplesFFT;

    }


}
