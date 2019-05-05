package translators;

import baseTranslator.BaseTranslator;
import tuple.Tuple;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class StringTranslator implements BaseTranslator<String> {
    private final byte id = (byte)8;
    private final String typeName = "java.lang.String";

    public byte[] toBytes(String fieldName, String type, String value){
        if(type.equals(typeName)) {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

            byte[] bytedfFieldName = fieldName.getBytes();
            byte[] bytedNameLength = ByteBuffer.allocate(4).putInt(bytedfFieldName.length).array();
            byte[] bytedValue = new byte[]{};
            byte[] bytedValueLength = ByteBuffer.allocate(4).putInt(-1).array();

            if(value != null) {
                bytedValue = value.getBytes();
                bytedValueLength = ByteBuffer.allocate(4).putInt(bytedValue.length).array();
            }

            try {
                byteStream.write(id);
                byteStream.write(bytedNameLength);
                byteStream.write(bytedfFieldName);
                byteStream.write(bytedValueLength);
                byteStream.write(bytedValue);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return byteStream.toByteArray();
        }
        return null;
    }

    public Tuple<String> fromBytes(byte[] bytes){
        if((int)bytes[0] == id){
            int nameLength = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 1, 5)).getInt();
            String name = new String(Arrays.copyOfRange(bytes,5, 5 + nameLength));
            String value = null;

            int valueLength = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 5 + nameLength, 9 + nameLength)).getInt();

            if(valueLength != -1) {
                value = new String(Arrays.copyOfRange(bytes, 9 + nameLength, 9 + nameLength + valueLength));
                return new Tuple<>(name, value, 9 + nameLength + valueLength);
            }
            return new Tuple<>(name, null, 9 + nameLength);
        }
        else return null;
    }
}
