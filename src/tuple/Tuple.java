package tuple;

public class Tuple<T> {
    private final String fieldName;
    private final T value;
    private final int offset;

    public Tuple(String name, T value){
        this.fieldName = name;
        this.value = value;
        this.offset = 0;
    }

    public Tuple(String name, T value, int offset){
        this.fieldName = name;
        this.value = value;
        this.offset = offset;
    }

    public String getFieldName() {
        return fieldName;
    }

    public T getValue(){
        return value;
    }

    public int getOffset(){
        return offset;
    }
}
