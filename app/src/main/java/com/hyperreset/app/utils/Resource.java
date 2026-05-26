package com.hyperreset.app.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A generic class that holds a value with its loading/success/error status.
 *
 * @param <T> The type of the data being wrapped.
 */
public class Resource<T> {

    @NonNull
    public final Status status;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, null);
    }

    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(@NonNull String message, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, message);
    }

    public static <T> Resource<T> error(@NonNull String message) {
        return new Resource<>(Status.ERROR, null, message);
    }

    public enum Status {
        LOADING,
        SUCCESS,
        ERROR
    }

    @NonNull
    @Override
    public String toString() {
        return "Resource{" +
                "status=" + status +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }
}
