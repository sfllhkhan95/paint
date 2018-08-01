package pk.aspirasoft.core.io;

import org.jetbrains.annotations.Nullable;


public abstract class AsyncTask<T> extends android.os.AsyncTask<String, Void, TResult<T>> {

    private OnCompleteListener<T> onCompleteListener;

    public void setOnCompleteListener(OnCompleteListener<T> onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    protected abstract @Nullable
    T run(String... params) throws Exception;

    @Override
    protected TResult<T> doInBackground(String... params) {
        TResult<T> tResult = new TResult<>();
        try {
            T t = run(params);
            assert t != null;
            tResult.setResult(t);
        } catch (Exception e) {
            tResult.setError(e);
        }
        return tResult;
    }

    @Override
    protected void onPostExecute(TResult<T> tResult) {
        if (onCompleteListener != null) {
            if (tResult.getError() != null) {
                onCompleteListener.onFailure(tResult.getError());
            } else if (tResult.getResult() != null) {
                onCompleteListener.onSuccess(tResult.getResult());
            }
        }
    }
}