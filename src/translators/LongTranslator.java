package translators;

import baseTranslator.BaseTranslator;
import tuple.Tuple;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class LongTranslator implements BaseTranslator<Long> {
    private final byte id = (byte)6;
    private final String typeName = "long";

    public byte[] toBytes(String fieldName, String type, Long value){
        if(type.equals(typeName)) {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

            byte[] bytedfFieldName = fieldName.getBytes();
            byte[] bytedNameLength = ByteBuffer.allocate(4).putInt(bytedfFieldName.length).array();

            try {
                byteStream.write(id);
                byteStream.write(bytedNameLength);
                byteStream.write(bytedfFieldName);
                byteStream.write(ByteBuffer.allocate(8).putLong(value).array());
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return byteStream.toByteArray();
        }
        return null;
    }

    public Tuple<Long> fromBytes(byte[] bytes){
        if((int)bytes[0] == id){
            int nameLength = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 1, 5)).getInt();
            String name = new String(Arrays.copyOfRange(bytes,5, 5 + nameLength));

            long value = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 5 + nameLength, 13 + nameLength)).getLong();

            return new Tuple<>(name, value, 13 + nameLength);
        }
        else return null;
    }
}
