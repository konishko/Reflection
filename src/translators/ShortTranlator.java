package translators;

import baseTranslator.BaseTranslator;
import tuple.Tuple;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ShortTranlator implements BaseTranslator<Short> {
    private final byte id = (byte)7;
    private final Set<String> typeNames = new HashSet<>(Arrays.asList("short", "java.lang.Short"));

    public byte[] toBytes(String fieldName, String type, Short value){
        if(typeNames.contains(type)) {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

            byte[] bytedfFieldName = fieldName.getBytes();
            byte[] bytedNameLength = ByteBuffer.allocate(4).putInt(bytedfFieldName.length).array();

            try {
                byteStream.write(id);
                byteStream.write(bytedNameLength);
                byteStream.write(bytedfFieldName);
                byteStream.write(ByteBuffer.allocate(2).putShort(value).array());
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return byteStream.toByteArray();
        }
        return null;
    }

    public Tuple<Short> fromBytes(byte[] bytes){
        if((int)bytes[0] == id){
            int nameLength = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 1, 5)).getInt();
            String name = new String(Arrays.copyOfRange(bytes,5, 5 + nameLength));

            short value = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 5 + nameLength, 7 + nameLength)).getShort();

            return new Tuple<>(name, value, 7 + nameLength);
        }
        else return null;
    }
}
