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

        try {
            byteStream.write(id);
            byteStream.write(cIT.toBytes(className, obj.getClass().getTypeName() , obj));

            return byteStream.toByteArray();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }

        return null;
    }

    public Object deserialize(byte[] bytes){
        if((int)bytes[0] == id) {
            Tuple tuple = cIT.fromBytes(Arrays.copyOfRange(bytes, 1, bytes.length));

            return tuple.getValue();
        }
        return null;
    }
}
