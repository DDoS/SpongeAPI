package org.spongepowered.api.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.common.base.Optional;

public class PrimitiveUtilTest {

    @Test
    public void testAsCharacter() {
        String character = "myChar";
        Optional<Character> optional = PrimitiveUtil.asCharacter(character);
        assertTrue(optional.isPresent());
        assertTrue(optional.get() == 'm');

        char myChar = 'm';
        Optional<Character> optional1 = PrimitiveUtil.asCharacter(myChar);
        assertTrue(optional1.isPresent());
        assertTrue(optional1.get() == 'm');

        int myInt = 1;
        Optional<Character> optional2 = PrimitiveUtil.asCharacter(myInt);
        assertTrue(optional2.isPresent());
        assertTrue(optional2.get() == '1');

        Optional<Character> nullOptional = PrimitiveUtil.asCharacter(null);
        assertTrue(!nullOptional.isPresent());
    }

    @Test
    public void testAsBoolean() throws Exception {
        Object myBool = Boolean.TRUE;
        Optional<Boolean> optional = PrimitiveUtil.asBoolean(myBool);
        assertTrue(optional.isPresent());
        assertTrue(optional.get());

        Optional<Boolean> nullBoolean = PrimitiveUtil.asBoolean(null);
        assertTrue(!nullBoolean.isPresent());

        Optional<Boolean> invalidBoolean = PrimitiveUtil.asBoolean("Mybool");
        assertTrue(!nullBoolean.isPresent());
    }

    @Test
    public void testAsByte() {
        String character = "1";
        Optional<Byte> optional = PrimitiveUtil.asByte(character);
        assertTrue(optional.isPresent());
        assertTrue(optional.get() == 1);

        char myChar = '1';
        Optional<Byte> optional1 = PrimitiveUtil.asByte(myChar);
        assertTrue(optional1.isPresent());
        assertTrue(optional1.get() == 1);

        String invalidString = "mwahahahah";
        Optional<Byte> invalidOptional = PrimitiveUtil.asByte(invalidString);
        assertTrue(!invalidOptional.isPresent());

        int myInt = 1;
        Optional<Byte> optional2 = PrimitiveUtil.asByte(myInt);
        assertTrue(optional2.isPresent());
        assertTrue(optional2.get() == 1);



        Optional<Byte> nullOptional = PrimitiveUtil.asByte(null);
        assertTrue(!nullOptional.isPresent());
    }

    @Test
    public void testAsShort() {
        String character = "1";
        Optional<Short> optional = PrimitiveUtil.asShort(character);
        assertTrue(optional.isPresent());
        assertTrue(optional.get() == 1);

        char myChar = '1';
        Optional<Short> optional1 = PrimitiveUtil.asShort(myChar);
        assertTrue(optional1.isPresent());
        assertTrue(optional1.get() == 1);

        String invalidString = "mwahahahah";
        Optional<Short> invalidOptional = PrimitiveUtil.asShort(invalidString);
        assertTrue(!invalidOptional.isPresent());

        int myInt = 1;
        Optional<Short> optional2 = PrimitiveUtil.asShort(myInt);
        assertTrue(optional2.isPresent());
        assertTrue(optional2.get() == 1);



        Optional<Short> nullOptional = PrimitiveUtil.asShort(null);
        assertTrue(!nullOptional.isPresent());
    }

    @Test
    public void testAsLong() {
        String character = "1";
        Optional<Long> optional = PrimitiveUtil.asLong(character);
        assertTrue(optional.isPresent());
        assertTrue(optional.get() == 1);

        char myChar = '1';
        Optional<Long> optional1 = PrimitiveUtil.asLong(myChar);
        assertTrue(optional1.isPresent());
        assertTrue(optional1.get() == 1);

        String invalidString = "mwahahahah";
        Optional<Long> invalidOptional = PrimitiveUtil.asLong(invalidString);
        assertTrue(!invalidOptional.isPresent());

        int myInt = 1;
        Optional<Long> optional2 = PrimitiveUtil.asLong(myInt);
        assertTrue(optional2.isPresent());
        assertTrue(optional2.get() == 1);



        Optional<Long> nullOptional = PrimitiveUtil.asLong(null);
        assertTrue(!nullOptional.isPresent());
    }

    @Test
    public void testAsFloat() {
        String character = "1";
        Optional<Float> optional = PrimitiveUtil.asFloat(character);
        assertTrue(optional.isPresent());
        assertTrue(optional.get() == 1);

        char myChar = '1';
        Optional<Float> optional1 = PrimitiveUtil.asFloat(myChar);
        assertTrue(optional1.isPresent());
        assertTrue(optional1.get() == 1);

        String invalidString = "mwahahahah";
        Optional<Float> invalidOptional = PrimitiveUtil.asFloat(invalidString);
        assertTrue(!invalidOptional.isPresent());

        int myInt = 1;
        Optional<Float> optional2 = PrimitiveUtil.asFloat(myInt);
        assertTrue(optional2.isPresent());
        assertTrue(optional2.get() == 1);



        Optional<Float> nullOptional = PrimitiveUtil.asFloat(null);
        assertTrue(!nullOptional.isPresent());
    }

    @Test
    public void testAsDouble() {
        String character = "1";
        Optional<Double> optional = PrimitiveUtil.asDouble(character);
        assertTrue(optional.isPresent());
        assertTrue(optional.get() == 1);

        char myChar = '1';
        Optional<Double> optional1 = PrimitiveUtil.asDouble(myChar);
        assertTrue(optional1.isPresent());
        assertTrue(optional1.get() == 1);

        String invalidString = "mwahahahah";
        Optional<Double> invalidOptional = PrimitiveUtil.asDouble(invalidString);
        assertTrue(!invalidOptional.isPresent());

        int myInt = 1;
        Optional<Double> optional2 = PrimitiveUtil.asDouble(myInt);
        assertTrue(optional2.isPresent());
        assertTrue(optional2.get() == 1);



        Optional<Double> nullOptional = PrimitiveUtil.asDouble(null);
        assertTrue(!nullOptional.isPresent());
    }

    @Test
    public void testAsInteger() {
        String character = "1";
        Optional<Integer> optional = PrimitiveUtil.asInteger(character);
        assertTrue(optional.isPresent());
        assertTrue(optional.get() == 1);

        char myChar = '1';
        Optional<Integer> optional1 = PrimitiveUtil.asInteger(myChar);
        assertTrue(optional1.isPresent());
        assertTrue(optional1.get() == 1);

        String invalidString = "mwahahahah";
        Optional<Integer> invalidOptional = PrimitiveUtil.asInteger(invalidString);
        assertTrue(!invalidOptional.isPresent());

        int myInt = 1;
        Optional<Integer> optional2 = PrimitiveUtil.asInteger(myInt);
        assertTrue(optional2.isPresent());
        assertTrue(optional2.get() == 1);



        Optional<Integer> nullOptional = PrimitiveUtil.asInteger(null);
        assertTrue(!nullOptional.isPresent());
    }

    @Test
    public void testAsString() {
        String character = "1";
        Optional<String> optional = PrimitiveUtil.asString(character);
        assertTrue(optional.isPresent());
        assertTrue(optional.get().equals("1"));

        char myChar = '1';
        Optional<String> optional1 = PrimitiveUtil.asString(myChar);
        assertTrue(optional1.isPresent());
        assertTrue(optional1.get().equals("1"));

        String invalidString = "mwahahahah";
        Optional<String> message = PrimitiveUtil.asString(invalidString);
        assertTrue(message.isPresent());
        assertTrue(message.get().equals("mwahahahah"));

        int myInt = 1;
        Optional<String> optional2 = PrimitiveUtil.asString(myInt);
        assertTrue(optional2.isPresent());
        assertTrue(optional2.get().equals("1"));


        Optional<String> nullOptional = PrimitiveUtil.asString(null);
        assertTrue(!nullOptional.isPresent());
    }
}
