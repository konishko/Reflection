package translators;

import baseTranslator.BaseTranslator;
import tuple.Tuple;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BooleanTranslator implements BaseTranslator<Boolean> {
    private final byte id = (byte)0;
    private final Set<String> typeNames = new HashSet<>(Arrays.asList("boolean", "java.lang.Boolean"));

    public byte[] toBytes(String fieldName, String type, Boolean value){
        if(typeNames.contains(type)) {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

            byte[] bytedfFieldName = fieldName.getBytes();
            byte[] bytedNameLength = ByteBuffer.allocate(4).putInt(bytedfFieldName.length).array();

            try {
                byteStream.write(id);
                byteStream.write(bytedNameLength);
                byteStream.write(bytedfFieldName);
                if (value) byteStream.write(1);
                else byteStream.write(0);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return byteStream.toByteArray();
        }
        return null;
    }

    public Tuple<Boolean> fromBytes(byte[] bytes){
        if((int)bytes[0] == id){
            int nameLength = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 1, 5)).getInt();
            String name = new String(Arrays.copyOfRange(bytes,5, 5 + nameLength));

            boolean value;
            int intValue = bytes[5 + nameLength];
            value = intValue > 0;

            return new Tuple<>(name, value, 6 + nameLength);
        }
        else return null;
    }
}
