package com.project.shiensys;

import androidx.annotation.NonNull;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final SessionManager session;
    public AuthInterceptor(SessionManager session) { this.session = session; }

    @NonNull @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request r = chain.request();
        String token = session.token();
        if (token == null || token.isEmpty()) return chain.proceed(r);
        return chain.proceed(r.newBuilder().header("Authorization", "Bearer " + token).build());
    }
}
