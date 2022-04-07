package com.vinsguru.grpcserver;

import com.vinsguru.gps.NavigationServiceGrpc;
import com.vinsguru.gps.TripRequest;
import com.vinsguru.gps.TripResponse;
import com.vinsguru.asrservice.*;
import io.grpc.stub.StreamObserver;

public class ASRClientService extends ASRServiceGrpc.ASRServiceImplBase{
    @Override
    public StreamObserver<Audio> transcribeStreamTest(StreamObserver<Transcript> transcriptObserver) {
        return new AudioObserver(transcriptObserver);
    }
}
