package ru.practicum.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.grpc.stats.controller.UserActionControllerGrpc;
import ru.practicum.ewm.grpc.stats.event.UserActionProto;
import ru.practicum.handler.UserActionHandlerImpl;

@Slf4j
@GrpcService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserActionController extends UserActionControllerGrpc.UserActionControllerImplBase {
    final UserActionHandlerImpl handler;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Начало обработки действия пользователя: {}", request);
            handler.handle(request);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }
}