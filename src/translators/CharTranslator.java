package translators;

import baseTranslator.BaseTranslator;
import tuple.Tuple;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CharTranslator implements BaseTranslator<Character> {
    private final byte id = (byte)2;
    private final Set<String> typeNames = new HashSet<>(Arrays.asList("char", "java.lang.Character"));


    public byte[] toBytes(String fieldName, String type, Character value){
        if(typeNames.contains(type)) {
            StringTranslator strTran = new StringTranslator();
            byte[] bytes = strTran.toBytes(fieldName, "java.lang.String", value.toString());
            bytes[0] = id;

            return bytes;
        }
        return null;
    }

    public Tuple<Character> fromBytes(byte[] bytes){
        if((int)bytes[0] == id){
            int nameLength = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 1, 5)).getInt();
            String name = new String(Arrays.copyOfRange(bytes,5, 5 + nameLength));

            int valueLength = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 5 + nameLength, 9 + nameLength)).getInt();
            String value = new String(Arrays.copyOfRange(bytes,9 + nameLength, 9 + nameLength + valueLength));

            return new Tuple<>(name, value.charAt(0), 9 + nameLength + valueLength);
        }
        else return null;
    }
}
