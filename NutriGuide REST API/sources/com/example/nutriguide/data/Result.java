package com.example.nutriguide.data;

public class Result<T> {
    private Result() {
    }

    public String toString() {
        if (this instanceof Success) {
            return "Success[data=" + ((Success) this).getData().toString() + "]";
        } else if (!(this instanceof Error)) {
            return "";
        } else {
            return "Error[exception=" + ((Error) this).getError().toString() + "]";
        }
    }

    public static final class Success<T> extends Result {
        private T data;

        public Success(T data2) {
            super();
            this.data = data2;
        }

        public T getData() {
            return this.data;
        }
    }

    public static final class Error extends Result {
        private Exception error;

        public Error(Exception error2) {
            super();
            this.error = error2;
        }

        public Exception getError() {
            return this.error;
        }
    }
}
