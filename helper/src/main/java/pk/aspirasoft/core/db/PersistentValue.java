package pk.aspirasoft.core.db;

/**
 * PersistentValue is a generic class which represents a single data value which is stored
 * in {@link PersistentStorage}.
 *
 * @author saifkhichi96
 */
public class PersistentValue<T> {

    private final String TAG;
    private final T defValue;

    private T value;

    public PersistentValue(String tag, Class<T> type) {
        TAG = tag;
        this.defValue = null;
        load(type);
    }

    public PersistentValue(String tag, T defValue) {
        TAG = tag;
        this.defValue = defValue;
        load((Class<T>) defValue.getClass());
    }

    private void load(Class<T> tClass) {
        T savedValue = PersistentStorage.get(TAG, tClass);
        if (savedValue != null) {
            this.value = savedValue;
        } else {
            reset();
        }
    }

    public void save() {
        PersistentStorage.put(TAG, value);
    }

    public void reset() {
        this.value = defValue;
        save();
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
        save();
    }

    @Override
    protected void finalize() throws Throwable {
        save();
        super.finalize();
    }

}