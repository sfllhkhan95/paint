package pk.aspirasoft.core.io;


class TResult<Result> {

    private Result result;
    private Exception error;

    Result getResult() {
        return result;
    }

    void setResult(Result result) {
        this.result = result;
    }

    Exception getError() {
        return error;
    }

    void setError(Exception error) {
        this.error = error;
    }
}
