package Serializtor;

import BaseSerializator.BaseSerializator;
import baseTranslator.BaseTranslator;
import translators.ClassInstanceTranslator;
import tuple.Tuple;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class ObjectSerializator<T> implements BaseSerializator{
    private final int id = 127;
    private final byte[] startPacket = "?--".getBytes(StandardCharsets.UTF_8);
    private final byte[] endPacket = "--!".getBytes(StandardCharsets.UTF_8);

    private ArrayList<BaseTranslator> translators = new ArrayList<>();
    private ClassInstanceTranslator cIT = new ClassInstanceTranslator();

    public ObjectSerializator(){
        this.translators.add(cIT);
    }

    public void register(BaseTranslator translator){
        translators.add(0, translator);
        cIT.register(translator);
    }

    public byte[] serialize(Object obj) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        String className = obj.getClass().getName();
        String type = obj.getClass().getTypeName();

        try {
            for (BaseTranslator translator : translators) {
                try {
                    byte[] objectInBytes = translator.toBytes(className, type, obj);
                    if (objectInBytes == null)
                        continue;

                    byteStream.write(id);
                    byteStream.write(startPacket);
                    byteStream.write(objectInBytes);
                    byteStream.write(endPacket);

                    break;
                } catch (ClassCastException ex) {
                    continue;
                }
            }

            return byteStream.toByteArray();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }

        return null;
    }

    public Object deserialize(byte[] bytes){
        if((int)bytes[0] == id && Arrays.equals(Arrays.copyOfRange(bytes, 1, 4) ,startPacket) &&
           Arrays.equals(endPacket, Arrays.copyOfRange(bytes, bytes.length - 3, bytes.length))){
            for (BaseTranslator translator : translators) {
                try {
                    Tuple tuple = translator.fromBytes(Arrays.copyOfRange(bytes, 4, bytes.length - 3));
                    if (tuple == null)
                        continue;

                    return  tuple.getValue();
                } catch (ClassCastException ex) {
                    continue;
                }
            }
        }
        return null;
    }
}
