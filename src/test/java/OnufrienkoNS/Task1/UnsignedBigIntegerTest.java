package OnufrienkoNS.Task1;


import java.math.BigInteger;
import java.util.Random;
import static org.junit.Assert.*;
class UnsignedBigIntegerTest {

    private String getRandom() {
        StringBuilder string = new StringBuilder();
        int length = Math.abs(new Random().nextInt()) % 100 + 1;
        for(int i = 0; i < length; i++) {
            string.append(Math.abs(new Random().nextInt())%10);
        }
        return string.toString();
    }

    @org.junit.jupiter.api.Test
    void intConstructorAndToString() {
        for(int i = 0; i < 100; i++)  {
            int value = Math.abs(new Random().nextInt());
            assertEquals(Integer.toString(value), new UnsignedBigInteger(value).toString());
        }
    }

    @org.junit.jupiter.api.Test
    void plus() {
        for(int i = 0; i < 10; i++) {
            String value1 = getRandom();
            String value2 = getRandom();
            BigInteger a = new BigInteger(value1);
            BigInteger b = new BigInteger(value2);
            UnsignedBigInteger uA = new UnsignedBigInteger(value1);
            UnsignedBigInteger uB = new UnsignedBigInteger(value2);
            String p1 = a.add(b).toString();
            String p2 =  uA.plus(uB).toString();
            assertEquals(p1, p2);
        }
    }

    @org.junit.jupiter.api.Test
    void isBiggerAndIsSmallerAndAlsoEquals() {
        for(int i = 0; i < 10; i++) {
            String value1 = getRandom();
            String value2 = getRandom();
            UnsignedBigInteger a = new UnsignedBigInteger(value1);
            UnsignedBigInteger b = new UnsignedBigInteger(value2);
            assertTrue(a.plus(b).isBiggerThan(a) && a.plus(b).isBiggerThan(b));
            assertTrue(a.isSmallerThan(a.plus(b)) && b.isSmallerThan(a.plus(b)));
            assertTrue(a.equals(a) && b.equals(b));
        }
    }

    @org.junit.jupiter.api.Test
    void minus() {
        for(int i = 0; i < 10; i++) {
            String value1 = getRandom();
            String value2 = getRandom();
            BigInteger a = new BigInteger(value1);
            BigInteger b = new BigInteger(value2);
            UnsignedBigInteger uA = new UnsignedBigInteger(value1);
            UnsignedBigInteger uB = new UnsignedBigInteger(value2);
            if(a.max(b).equals(a))// a>b
                assertEquals(a.subtract(b).toString(), uA.minus(uB).toString());
            else try {
                assertEquals("0", uA.minus(uB).toString());
            } catch (ArithmeticException e) {
                assertNotEquals("", e.getMessage());
            }
        }
    }
    @org.junit.jupiter.api.Test

    void divide() {
        for(int i = 0; i < 10; i++) {
            String value1 = getRandom();
            String value2 = getRandom();
            BigInteger a = new BigInteger(value1);
            BigInteger b = new BigInteger(value2);
            UnsignedBigInteger uA = new UnsignedBigInteger(value1);
            UnsignedBigInteger uB = new UnsignedBigInteger(value2);
            if(!uB.equals(UnsignedBigInteger.zero)) {
                assertEquals(a.divide(b).toString(), uA.divide(uB).toString());
                assertEquals(a.remainder(b).toString(), uA.getRemainder(uB).toString());
            } else try {
                assertEquals(a.divide(b).toString(), uA.divide(uB).toString());
                assertEquals(a.remainder(b).toString(), uA.getRemainder(uB).toString());
            } catch (ArithmeticException e) {
                assertNotEquals("", e.getMessage());
            }

        }
    }
}