package Serializtor;

import BaseSerializator.BaseSerializator;
import baseTranslator.BaseTranslator;
import translators.ClassInstanceTranslator;
import tuple.Tuple;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class PacketSerializator<T> implements BaseSerializator{
    private final int id = 127;
    private ArrayList<BaseTranslator> translators = new ArrayList<>();
    private ClassInstanceTranslator cIT = new ClassInstanceTranslator();

    public PacketSerializator(){
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
//      в джаве есть .isPrimitive()
        try {
            byteStream.write(id);
            for (BaseTranslator translator : translators) {
                try {
                    byte[] fieldInBytes = translator.toBytes(className, type, obj);
                    if (fieldInBytes == null)
                        continue;

                    byteStream.write(fieldInBytes);
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
        if((int)bytes[0] == id) {
            for (BaseTranslator translator : translators) {
                try {
                    Tuple tuple = translator.fromBytes(Arrays.copyOfRange(bytes, 1, bytes.length));
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
