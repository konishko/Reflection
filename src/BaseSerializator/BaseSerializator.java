package BaseSerializator;

public interface BaseSerializator<T>{
    public byte[] serialize(Object obj);

    public Object deserialize(byte[] bytes);
}
