package translators;

import baseTranslator.BaseTranslator;
import tuple.Tuple;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class ClassInstanceTranslator implements BaseTranslator<Object> {
    private final byte id = (byte)126;
    private ArrayList<BaseTranslator> translators = new ArrayList<>();

    public void register(BaseTranslator translator){
        translators.add(0, translator);
    }

    public ClassInstanceTranslator(){
        translators.add(this);
    }

    public byte[] toBytes(String fieldName, String type, Object value) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] bytedfFieldName = fieldName.getBytes();
        byte[] bytedNameLength = ByteBuffer.allocate(4).putInt(bytedfFieldName.length).array();
        byte[] bytedType = type.getBytes();
        byte[] bytedTypeLength = ByteBuffer.allocate(4).putInt(bytedType.length).array();

        try {
            byteStream.write(id);
            byteStream.write(bytedNameLength);
            byteStream.write(bytedfFieldName);
            byteStream.write(bytedTypeLength);
            byteStream.write(bytedType);


            if (value != null) {
                Field[] fields = value.getClass().getDeclaredFields();
                ByteArrayOutputStream fieldsByteStream = new ByteArrayOutputStream();

                try {
                    for (Field field : fields) {
                        if (!field.canAccess(value))
                            field.setAccessible(true);

                        for (BaseTranslator translator : translators) {
                            try {
                                byte[] fieldInBytes = translator.toBytes(field.getName(), field.getType().getName(), field.get(value));
                                if (fieldInBytes == null)
                                    continue;

                                fieldsByteStream.write(fieldInBytes);
                                break;
                            } catch (ClassCastException ex) {
                                continue;
                            }
                        }
                    }
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }

                int fieldsLength = fieldsByteStream.size();
                byteStream.write(ByteBuffer.allocate(4).putInt(fieldsLength).array());
                byteStream.write(fieldsByteStream.toByteArray());
            } else
                byteStream.write(ByteBuffer.allocate(4).putInt(-1).array());

            return byteStream.toByteArray();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Tuple<Object> fromBytes(byte[] bytes){
        if((int)bytes[0] == id) {
            byte[] bytedClassNameLength = Arrays.copyOfRange(bytes, 1, 5);
            int classNameLength = ByteBuffer.wrap(bytedClassNameLength).getInt();
            byte[] bytedClassName = Arrays.copyOfRange(bytes, 5, 5 + classNameLength);
            String className = new String(bytedClassName);

            byte[] bytedTypeLength = Arrays.copyOfRange(bytes, 5 + classNameLength, 9 + classNameLength);
            int typeLength = ByteBuffer.wrap(bytedTypeLength).getInt();
            byte[] bytedType = Arrays.copyOfRange(bytes, 9 + classNameLength, 9 + classNameLength + typeLength);
            String type = new String(bytedType);

            byte[] bytedValueLength = Arrays.copyOfRange(bytes, 9 + classNameLength + typeLength, 13 + classNameLength + typeLength);
            int valueLength = ByteBuffer.wrap(bytedValueLength).getInt();

            if(valueLength != -1) {
                ArrayList<Tuple> raw_fields = new ArrayList<>();
                int offset = 13 + classNameLength + typeLength;

                while (offset < 13 + classNameLength + typeLength + valueLength) {
                    for (BaseTranslator translator : translators) {
                        Tuple<?> field = translator.fromBytes(Arrays.copyOfRange(bytes, offset, bytes.length));

                        if (field != null) {
                            offset += field.getOffset();
                            raw_fields.add(field);

                            break;
                        }
                    }
                }

                try {
                    Class newClass = Class.forName(type);
                    Object instanceOfClass = newClass.getDeclaredConstructor().newInstance();

                    for (Tuple field : raw_fields) {
                        try {
                            Field packField = instanceOfClass.getClass().getDeclaredField(field.getFieldName());
                            if(!packField.canAccess(instanceOfClass))
                                packField.setAccessible(true);

                            packField.set(instanceOfClass, field.getValue());

                        } catch (NoSuchFieldException ex) {
                            System.out.println("Problems with packing");
                            ex.printStackTrace();
                            break;
                        } catch (IllegalAccessException ex) {
                            System.out.println("Problems with accessing");
                            ex.printStackTrace();
                            break;
                        }
                    }
                    return new Tuple<>(className, instanceOfClass, offset);
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                        | NoSuchMethodException | InvocationTargetException ex) {
                    ex.printStackTrace();
                }
            }
            else return new Tuple<>(className, null, 13 + classNameLength + typeLength);
        }
        return null;
    }
}
