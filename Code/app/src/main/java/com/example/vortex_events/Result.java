package com.example.vortex_events;

// Helper class to represent data
/**
 * A generic wrapper class used to represent the outcome of an asynchronous or uncertain operation.
 * This class distinguishes between successful results and failed results
 *
 * The Result type follows a functional-programming style approach:
 * - {@link Success} represents a successful computation containing data.
 * - {@link Error} represents a failure containing an exception.
 *
 * This class is often used in Firebase callbacks, database operations,
 * or any method where a result may succeed or fail.
 *
 * @param <T> the type of data returned in a successful result
 */
public class Result<T> {

    /**
     * Private constructor prevents direct instantiation.
     * Use the Result.Success or Result.Error subclasses instead.
     */
    private Result() {}

    /**
     * Represents a successful result containing data of type T.
     *
     * @param <T> the type of data returned in the result
     */
    public static final class Success<T> extends Result<T> {
        /** The data associated with the successful operation. */
        public final T data;
        /**
         * Creates a new Success result containing the given data.
         *
         * @param data the successful result data
         */
        public Success(T data) { this.data = data; }
    }
    /**
     * Represents a failed operation containing an exception that describes what went wrong.
     *
     * @param <T> the expected type of the successful result
     */
    public static final class Error<T> extends Result<T> {
        /** The exception associated with the failed operation. */
        public final Exception exception;
        /**
         * Creates a new Error result containing the given exception.
         *
         * @param exception the error that occurred during the operation
         */
        public Error(Exception exception) { this.exception = exception; }
    }
}