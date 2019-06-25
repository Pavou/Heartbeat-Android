package com.example.cameraaplication;

import java.util.ArrayList;

public class DataSeries {

    private float[] data;
    private  int size;
    private int pointer;

    private float[] fft_coefs;

    public DataSeries(int size) {
        this.data = new float[size];
        this.size = size;
        this.fft_coefs = new float[size*size*2];
        pointer =-1;
        init_fft();
    }

    public void addData(float d){
        pointer++;
        if (pointer >=size){
            pointer=0;
        }
        data[pointer] = d;
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
        for(int i =0;i < size;i++) {
            for (int j = 0; j < size; j++) {
                this.fft_coefs[(i * size + j) * 2 + 0] = (float) Math.cos(2 * Math.PI * i * j / size);
                this.fft_coefs[(i * size + j) * 2 + 1] = (float) Math.sin(2 * Math.PI * i * j / size);
            }
        }
    }

    public float calc_max_fft(){

        double max = 0;
        int index = -1;
        for(int i =1; i<size/2; i++){

            double a = 0;
            double b = 0;

            for (int j=0; j<size; j++) {
                a += data[j] * this.fft_coefs[(i * size + j) * 2 + 0];
                b += data[j] * this.fft_coefs[(i * size + j) * 2 + 1];


            }

            double mag = Math.sqrt(a*a + b*b);
            if(max < mag){
                max = mag;
                index = i;
            }
        }

        return (float)index * 30.0f / this.size;


    }


}
