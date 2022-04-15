package com.vinsguru.grpcserver;

import com.vinsguru.asrservice.Audio;
import com.vinsguru.asrservice.Transcript;
import io.grpc.stub.StreamObserver;

import java.time.LocalTime;


public class AudioObserver implements StreamObserver<Audio> {

    private LocalTime startTime = LocalTime.now();
    private final StreamObserver<Transcript> transcriptStreamObserver;

    public AudioObserver(StreamObserver<Transcript> transcriptStreamObserver) {
        this.transcriptStreamObserver = transcriptStreamObserver;
    }

    @Override
    public void onNext(Audio audio) {

        // client has not yet reached destination
        System.out.println("received audio chunk" + audio.getChunkNumber() + "from client");
        Transcript transcript = Transcript.newBuilder()
                .setTranscript("ওকে: " + audio.getChunkNumber() + "; " + audio.getData().size() )
                .build();
        this.transcriptStreamObserver.onNext(transcript);
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {
        this.transcriptStreamObserver.onCompleted();
        System.out.println("Client reached safely");
    }

}
