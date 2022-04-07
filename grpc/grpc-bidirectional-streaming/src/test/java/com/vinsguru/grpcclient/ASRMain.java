package com.vinsguru.grpcclient;

public class ASRMain {

    public static void main(String[] args) {

        BiDirectionalASRTest biDirectionalASRTest= new BiDirectionalASRTest();
        biDirectionalASRTest.setup();
        try {
            biDirectionalASRTest.audioTest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        biDirectionalASRTest.teardown();

    }
}

