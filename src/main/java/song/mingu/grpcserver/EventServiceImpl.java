package song.mingu.grpcserver;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import song.mingu.proto.EventRequest;
import song.mingu.proto.EventResponse;
import song.mingu.proto.EventServiceGrpc;

import java.util.concurrent.TimeUnit;

@Slf4j
public class EventServiceImpl extends EventServiceGrpc.EventServiceImplBase {

    @Override
    public void unaryEvent(EventRequest request, StreamObserver<EventResponse> responseObserver) {
        log.info("Request = sourceId : {}, eventId : {}", request.getSourceId(), request.getEventId());
        EventResponse eventResponse = EventResponse.newBuilder()
                .setResult(request.getSourceId() + request.getEventId())
                .build();

        // unary onNext 1회만 호출 가능
        responseObserver.onNext(eventResponse);
        log.info("onNext");

        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        responseObserver.onCompleted();
        log.info("onCompleted");
    }

    @Override
    public void serverStreamingEvent(EventRequest request, StreamObserver<EventResponse> responseObserver) {
        log.info("Request = sourceId : {}, eventId : {}", request.getSourceId(), request.getEventId());
        EventResponse eventResponse = EventResponse.newBuilder()
                .setResult(request.getSourceId() + request.getEventId())
                .build();

        // stream return 여러번 호출 가능
        responseObserver.onNext(eventResponse);
        responseObserver.onNext(eventResponse);
        responseObserver.onNext(eventResponse);
        responseObserver.onNext(eventResponse);
        log.info("onNext");

        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        responseObserver.onCompleted();
        log.info("onCompleted");
    }

    @Override
    public StreamObserver<EventRequest> clientStreamingEvent(StreamObserver<EventResponse> responseObserver) {
        return new StreamObserver<EventRequest>() {
            @Override
            public void onNext(EventRequest value) {
                log.info("Request = sourceId : {}, eventId : {}", value.getSourceId(), value.getEventId());
            }

            @Override
            public void onError(Throwable t) {
                log.info("onError");
            }

            @Override
            public void onCompleted() {
                // 클라이언트로 부터 스트림 완료가 되면 1번 호출
                responseObserver.onNext(EventResponse.newBuilder().setResult("response").build());
                responseObserver.onCompleted();
                log.info("onNext + onCompleted");
            }
        };
    }

    @Override
    public StreamObserver<EventRequest> biStreamingEvent(StreamObserver<EventResponse> responseObserver) {
        return new StreamObserver<EventRequest>() {
            @Override
            public void onNext(EventRequest value) {
                log.info("BiStream = sourceId : {}, eventId : {}", value.getSourceId(), value.getEventId());
                // 클라이언트에서 한번 이벤트가 발생할때 마다 3개의 응답을 보낸다
                responseObserver.onNext(EventResponse.newBuilder().setResult("response1").build());
                responseObserver.onNext(EventResponse.newBuilder().setResult("response2").build());
                responseObserver.onNext(EventResponse.newBuilder().setResult("response3").build());
                log.info("onNext");
            }

            @Override
            public void onError(Throwable t) {
                log.info("onError");
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
                log.info("onCompleted");
            }
        };
    }
}
