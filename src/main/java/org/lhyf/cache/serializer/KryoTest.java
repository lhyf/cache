package org.lhyf.cache.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/****
 * @author YF
 * @date 2018-08-07 10:58
 * @desc MsgPackTest
 *
 **/
public class KryoTest {

    public static void main(String[] args) throws FileNotFoundException {

        Kryo kryo = new Kryo();
        kryo.register(SomeClass.class);

        SomeClass object = new SomeClass();
        object.value = "Hello Kryo!";

        Output output = new Output(new FileOutputStream("file.bin"));
        kryo.writeObject(output, object);
        output.close();

        Input input = new Input(new FileInputStream("file.bin"));
        SomeClass object2 = kryo.readObject(input, SomeClass.class);
        System.out.println(object2.value);
        input.close();
    }

    static public class SomeClass {
        String value;
    }
}
