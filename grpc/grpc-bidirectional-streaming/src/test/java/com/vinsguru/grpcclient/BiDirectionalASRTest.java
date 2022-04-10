package com.vinsguru.grpcclient;

import com.vinsguru.gps.NavigationServiceGrpc;
import com.vinsguru.asrservice.*;
import com.vinsguru.gps.TripRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.time.LocalTime;


public class BiDirectionalASRTest {

    private ManagedChannel channel;
    private ASRServiceGrpc.ASRServiceStub clientStub;
    public static String WAV_FILE = "src/main/resources/test_audio/output.wav";
    public static float sampleRate = 0;
    public static float bits_per_sample = 0;
    public static float num_channels = 0;
    public static int chunkSize = 0;


    public static byte[] processAudioOffline ()
    {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream in = null;

        try {
            in = new BufferedInputStream(new FileInputStream(WAV_FILE));

            AudioInputStream inputStream =
                    AudioSystem.getAudioInputStream(in);
            int numBytes = inputStream.available();

            AudioFormat audioFormat = inputStream.getFormat();
            sampleRate = audioFormat.getSampleRate();
            bits_per_sample = audioFormat.getSampleSizeInBits();
            num_channels = audioFormat.getChannels();
            chunkSize = (int) (0.03 * bits_per_sample * num_channels * sampleRate / 8);

            byte[] buffer = new byte[numBytes];
            System.out.println(numBytes);
            inputStream.read(buffer, 0, numBytes);
            byte[] data1 = new byte[buffer.length - 44];
            System.arraycopy(buffer, 44, data1, 0, buffer.length - 44);
            return data1;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
        return null;


    }


    public byte[][] divideArray( byte[] source) {



        int num_chunks = (int) Math.ceil(source.length / (double) chunkSize);

        byte[][] ret = new byte[num_chunks][chunkSize];

        int start = 0;

        for (int i = 0; i < num_chunks; i++) {
//            ret[i] = Arrays.copyOfRange(source, start, start + chunksize);
            if (i==num_chunks - 1)
                System.arraycopy(source, start, ret[i], 0, source.length - start);
            else
                System.arraycopy(source, start, ret[i], 0, ret[i].length);
            start += chunkSize;
            //audioList.add(audio);
        }
        return ret ;

    }


    public void setup(){
        //localhost, 6565
        this.channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();
//        this.channel = ManagedChannelBuilder.forAddress("localhost", 6565)
//                .usePlaintext()
//                .build();
        this.clientStub = ASRServiceGrpc.newStub(channel);
    }

    public void audioTest() throws InterruptedException {
        TranscriptStreamObserver transcriptStreamObserver = new TranscriptStreamObserver();
        StreamObserver<Audio> requestStreamObserver = this.clientStub.transcribeStreamTest(transcriptStreamObserver);
        byte[] audio = processAudioOffline();
        byte[][] ret = divideArray(audio);
        int ln = ret.length;
        int i = 0 ;
        System.out.println("");
        transcriptStreamObserver.startAudio(ret, (int) sampleRate, requestStreamObserver);


        // just for testing
        Thread.sleep(3000);
    }

    public void teardown(){
        this.channel.shutdown();
    }



}
