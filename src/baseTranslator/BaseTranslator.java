package baseTranslator;

import tuple.Tuple;

public interface BaseTranslator<T> {
    public byte[] toBytes(String fieldName, String type, T value);

    public Tuple<T> fromBytes(byte[] bytes);
}
