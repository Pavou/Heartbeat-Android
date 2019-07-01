package com.avoupavou.heartBeat;

import android.util.Log;

public class DataSeries {

    public static int FS = 30;

    private static float fpsRunningMean;
    private static int fpsSamples;

    private float[] data;



    private float runningMeanFFT;
    private int samplesFFT;

    private int confidenceWindow;
    private float pulseMean;

    private float confidence;



    private  int size;
    private int pointer;

    private float[] fft_coefs;

    public static void updateFps(float fps){
        fpsRunningMean += fps;
        fpsSamples++;

        if (fpsSamples > 300){
            fpsRunningMean = fpsRunningMean / fpsSamples;
            fpsSamples = 1;
        }

        FS = Math.round(fpsRunningMean / fpsSamples);
    }

    public DataSeries(int size) {
        this.data = new float[size];
        this.size = size;
        this.fft_coefs = new float[size*size*2];
        this.pointer = -1;

        confidenceWindow =1;
        pulseMean = 60.0f;

        confidence = 0.0f;


        fpsRunningMean = 0.0f;
        fpsSamples = 0;

        invalidate();
        init_fft();
    }

    public void addData(float d){

        d = d < 0.0f ? 0.0f : d;
        d = d > 128 ? 128 : d;

        if (confidenceWindow > 2*FS){
            pointer++;
            pointer %= size;
            data[pointer] = d;
        }

        pulseMean += d;
        confidenceWindow++;
    }

    public void invalidate(){

        for (int i=0;i< size;i++){
            data[i] = pulseMean / confidenceWindow;
        }

        pulseMean = pulseMean / confidenceWindow;
        confidenceWindow = 1;


        runningMeanFFT =0.0f;
        samplesFFT =0;

        confidence =0;
    }



    public float[] getNormData(int n){

        n = n >= size ? size-1 : n;

        float[] out = new float[n];
        n--;
        int lp = Math.max(pointer,0);

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



        double max = 0;
        int index = -1;

        int L = this.size;

        int maxS = (int) Math.floor( 200.0f * L / (FS * 60.0f) );
        int minS = (int) Math.floor( 50.0f  * L / (FS * 60.0f) );


        if(confidenceWindow < maxS) return  0;

        int max_sample = L/2 < maxS ?  L/2 : maxS;

        float sum_all = 0.0f;

        for(int i = minS; i<max_sample; i++){

            double real = 0.0;
            double img  = 0.0;

            for (int j=0; j<L; j++) {
//                real += data[j] * Math.cos(2 * Math.PI * i * j / L);
//                img  += data[j] * Math.sin(2 * Math.PI * i * j / L);
                real += data[j] * this.fft_coefs[(i*size + j)*2 +0];
                img  += data[j] * this.fft_coefs[(i*size + j)*2 +1];


            }

            double mag = Math.sqrt(real*real + img*img);

            sum_all += mag;

            if(max < mag){
                max = mag;
                index = i;
            }
        }


        confidence = (float)max / sum_all;

        if (confidence < 0.08f) return 0;

        runningMeanFFT += (float)index * this.FS / L;
        samplesFFT++;

        if(samplesFFT > this.size){
            runningMeanFFT = runningMeanFFT / samplesFFT;
            samplesFFT =1;
        }

        return (float)runningMeanFFT / samplesFFT;

    }


    public float getConfidence() {
        return confidence;
    }
}
