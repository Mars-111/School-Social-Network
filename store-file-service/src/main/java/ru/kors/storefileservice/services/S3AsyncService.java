package ru.kors.storefileservice.services;

import lombok.RequiredArgsConstructor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.ByteBuffer;

@Service
@RequiredArgsConstructor
public class S3AsyncService {

    @Value("${s3.bucket}")
    private String bucket;

    private final S3AsyncClient s3AsyncClient;


    public Mono<Void> upload(String key, Flux<DataBuffer> dataBufferFlux) {
        var publisher = dataBufferFlux.map(DataBuffer::asByteBuffer);

        AsyncRequestBody requestBody = AsyncRequestBody.fromPublisher(publisher);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        return Mono.fromFuture(() -> s3AsyncClient.putObject(request, requestBody)).then();
    }

    public Flux<DataBuffer> download(String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        return Flux.create(sink ->
                s3AsyncClient.getObject(request, AsyncResponseTransformer.toPublisher())
                        .whenComplete((response, error) -> {
                            if (error != null) {
                                sink.error(error);
                            } else {
                                response.subscribe(new Subscriber<ByteBuffer>() {
                                    @Override public void onSubscribe(Subscription s) { s.request(Long.MAX_VALUE); }
                                    @Override public void onNext(ByteBuffer buffer) {
                                        byte[] bytes = new byte[buffer.remaining()];
                                        buffer.get(bytes);
                                        DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(bytes);
                                        sink.next(dataBuffer);
                                    }
                                    @Override public void onError(Throwable t) { sink.error(t); }
                                    @Override public void onComplete() { sink.complete(); }
                                });
                            }
                        }));
    }
}

