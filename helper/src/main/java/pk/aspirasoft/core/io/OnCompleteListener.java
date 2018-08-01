package pk.aspirasoft.core.io;

import org.jetbrains.annotations.NotNull;

public interface OnCompleteListener<T> {

    void onSuccess(@NotNull T result);

    void onFailure(@NotNull Exception ex);

}
