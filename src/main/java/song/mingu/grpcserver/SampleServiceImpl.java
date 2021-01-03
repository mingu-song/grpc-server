package song.mingu.grpcserver;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import song.mingu.proto.SampleRequest;
import song.mingu.proto.SampleResponse;
import song.mingu.proto.SampleServiceGrpc;

@Slf4j
public class SampleServiceImpl extends SampleServiceGrpc.SampleServiceImplBase {

    @Override
    public void sampleCall(SampleRequest request, StreamObserver<SampleResponse> responseObserver) {
        log.info("SampleServiceImpl#sampleCall - {}, {}, CurrentThread = {}", request.getUserId(), request.getMessage(), Thread.currentThread().getName());
        SampleResponse sampleResponse = SampleResponse.newBuilder()
                .setMessage("grpc service response")
                .build();
        responseObserver.onNext(sampleResponse);
        responseObserver.onCompleted();
    }
}
