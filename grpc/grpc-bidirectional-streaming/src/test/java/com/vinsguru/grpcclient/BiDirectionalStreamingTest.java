package com.vinsguru.grpcclient;

import com.vinsguru.gps.NavigationServiceGrpc;
import com.vinsguru.gps.TripRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BiDirectionalStreamingTest {

    private ManagedChannel channel;
    private NavigationServiceGrpc.NavigationServiceStub clientStub;


    public  void setup(){
        this.channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();
        this.clientStub = NavigationServiceGrpc.newStub(channel);
    }

    public void tripTest() throws InterruptedException {
        TripResponseStreamObserver tripResponseStreamObserver = new TripResponseStreamObserver();
        StreamObserver<TripRequest> requestStreamObserver = this.clientStub.navigate(tripResponseStreamObserver);
        tripResponseStreamObserver.startTrip(requestStreamObserver);

        // just for testing
        Thread.sleep(1000);
    }

    public void teardown(){
        this.channel.shutdown();
    }

    public void main(String[] args) {

        this.setup();
        try {
            this.tripTest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.teardown();

    }

}
