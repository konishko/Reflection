package translators;

import baseTranslator.BaseTranslator;
import tuple.Tuple;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class IntTranslator implements BaseTranslator<Integer> {
    private final byte id = (byte)5;
    private final Set<String> typeNames = new HashSet<>(Arrays.asList("int", "java.lang.Integer"));

    public byte[] toBytes(String fieldName, String type, Integer value){
        if(typeNames.contains(type)) {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

            byte[] bytedfFieldName = fieldName.getBytes();
            byte[] bytedNameLength = ByteBuffer.allocate(4).putInt(bytedfFieldName.length).array();

            try {
                byteStream.write(id);
                byteStream.write(bytedNameLength);
                byteStream.write(bytedfFieldName);
                byteStream.write(ByteBuffer.allocate(4).putInt(value).array());
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return byteStream.toByteArray();
        }return null;
    }

    public Tuple<Integer> fromBytes(byte[] bytes){
        if((int)bytes[0] == id){
            int nameLength = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 1, 5)).getInt();
            String name = new String(Arrays.copyOfRange(bytes,5, 5 + nameLength));

            int value = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 5 + nameLength, 9 + nameLength)).getInt();

            return new Tuple<>(name, value, 9 + nameLength);
        }
        else return null;
    }
}
