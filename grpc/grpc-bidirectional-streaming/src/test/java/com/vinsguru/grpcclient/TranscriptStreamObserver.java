package com.vinsguru.grpcclient;

import com.google.common.util.concurrent.Uninterruptibles;
import com.google.protobuf.ByteString;
import com.vinsguru.asrservice.Audio;
import com.vinsguru.asrservice.Transcript;
import io.grpc.stub.StreamObserver;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;



public class TranscriptStreamObserver implements StreamObserver<Transcript> {

    private StreamObserver<Audio> requestStreamObserver;

    @Override
    public void onNext(Transcript transcript) {
        if(!transcript.getTranscript().equals("")){
            print(transcript);
            System.out.println(Arrays.toString(transcript.getTranscript().getBytes(StandardCharsets.UTF_8)));
        }else{
            this.requestStreamObserver.onCompleted();
        }
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {
        System.out.println("Trip Completed");
    }

    public void startAudio(byte[][]data, int sampleRate, StreamObserver<Audio> requestStreamObserver){
        this.requestStreamObserver = requestStreamObserver;
        int ln = data.length;
        int i = 0 ;
        for (i = 0 ; i < ln; i++)
            this.drive(data[i], i, sampleRate);
    }

    private void drive(byte[] data, int i, int sampleRate){
        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
        Audio audio = Audio.newBuilder().setData(ByteString.copyFrom(data)).setChunkNumber(i).setSampleRate(sampleRate).build();
        requestStreamObserver.onNext(audio);
    }

    private void print(Transcript transcript){
        System.out.println(LocalTime.now() + ": Received transcript : " + transcript.getTranscript());
        System.out.println("------------------------------");
    }

}
