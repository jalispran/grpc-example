package com.pranjal.grpc.common;

import java.util.UUID;

public class AppContext {
    private static ThreadLocal<UUID> requestId = new ThreadLocal<>();
    public static UUID getRequestId() {
        return requestId.get();
    }
    public static void setRequestId(UUID requestId) {
        AppContext.requestId.set(requestId);
    }
    public static void clean() {
        requestId.remove();
    }
}
