package com.pranjal.grpc.server.interceptor;

import com.pranjal.grpc.common.AppContext;
import io.grpc.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.error.GrpcExceptionResponseHandler;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.UUID;

import static com.pranjal.grpc.common.GrpcConstants.*;

@Slf4j
@RequiredArgsConstructor
@GrpcGlobalServerInterceptor
public class GrpcServerInterceptor implements ServerInterceptor {

    private final GrpcExceptionResponseHandler exceptionHandler;
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        String grpcMethodName = call.getMethodDescriptor().getFullMethodName();
        Context context = Context.current()
                                    .withValue(REQUEST_CONTEXT, headers.get(REQUEST_ID_HEADER_KEY));
        ServerCall.Listener<ReqT> listener = next.startCall(call, headers);

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(listener) {
            @Override
            public void onHalfClose() {
                Context attachedContext = setUpContext();
                try {
                    super.onHalfClose();
                } catch (Exception e) {
                    log.error(e.toString());
                    exceptionHandler.handleError(call, e);
                } finally {
                    cleanContext(attachedContext);
                }
            }

            @Override
            public void onMessage(ReqT message) {
                Context attachedContext = setUpContext();
                try {
                    log.debug("Called: '" + grpcMethodName);
                    super.onMessage(message);
                } catch (Exception e) {
                    log.error(e.toString());
                    exceptionHandler.handleError(call, e);
                } finally {
                    cleanContext(attachedContext);
                }
            }

            @Override
            public void onCancel() {
                Context attachedContext = setUpContext();
                try {
                    super.onCancel();
                } catch (Exception e) {
                    log.error(e.toString());
                    exceptionHandler.handleError(call, e);
                } finally {
                    cleanContext(attachedContext);
                }
            }

            @Override
            public void onComplete() {
                Context attachedContext = setUpContext();
                try {
                    super.onComplete();
                } catch (Exception e) {
                    log.error(e.toString());
                    exceptionHandler.handleError(call, e);
                } finally {
                    cleanContext(attachedContext);
                }
            }

            @Override
            public void onReady() {
                Context attachedContext = setUpContext();
                try {
                    super.onReady();
                } catch (Exception e) {
                    log.error(e.toString());
                    exceptionHandler.handleError(call, e);
                } finally {
                    cleanContext(attachedContext);
                }
            }

            private void cleanContext(Context previous) {
                context.detach(previous);
                AppContext.clean();
                MDC.clear();
            }

            private Context setUpContext() {
                Context attachedContext = context.attach();
                UUID requestId = UUID.randomUUID();
                if (StringUtils.hasText(REQUEST_CONTEXT.get())) {
                    requestId = UUID.fromString(REQUEST_CONTEXT.get());
                }
                AppContext.setRequestId(requestId);
                MDC.put(REQUEST_ID, String.valueOf(AppContext.getRequestId()));
                return attachedContext;
            }
        };
    }
}
