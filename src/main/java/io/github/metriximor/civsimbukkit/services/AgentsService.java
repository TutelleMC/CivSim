package io.github.metriximor.civsimbukkit.services;

import io.github.metriximor.civsimbukkit.AgentsServiceGrpc;
import io.github.metriximor.civsimbukkit.HelloReply;
import io.github.metriximor.civsimbukkit.HelloRequest;
import io.grpc.stub.StreamObserver;

public class AgentsService extends AgentsServiceGrpc.AgentsServiceImplBase {
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
