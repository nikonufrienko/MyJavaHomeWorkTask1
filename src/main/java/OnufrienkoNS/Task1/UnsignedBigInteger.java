package OnufrienkoNS.Task1;


import java.util.LinkedList;

public class UnsignedBigInteger{
    private final LinkedList<Character> unsignedCells;
    public static final UnsignedBigInteger zero = new UnsignedBigInteger(0);
    public static final UnsignedBigInteger ten = new UnsignedBigInteger(10);

    private byte getBit(long index) {
        char cell = this.readUnsignedCell((int)index / 16);
        if((cell & (1L<<(index%16))) != 0) return 1;
        else return 0;
    }

    private void mul2() {
        char oldBitBuffer = 0;
        char newBitBuffer = 0;
        int currNumberOfCells = unsignedCells.size();
        for (int i = 0; i < currNumberOfCells; i++) {
            char currCell = readUnsignedCell(i);
            newBitBuffer = (char) ((char) (currCell & (1<<15))>>15);
            currCell = (char) ((currCell<<1) | oldBitBuffer);
            oldBitBuffer = newBitBuffer;
            writeUnsignedCell(i, currCell);
        }
        if(oldBitBuffer != 0){ appendUnsignedCell(oldBitBuffer);
        }
    }

    private void div2() {
        char oldBitBuffer = 0;
        char newBitBuffer = 0;
        int currNumberOfCells = unsignedCells.size();
        for (int i = currNumberOfCells-1; i >=0 ; i--) {
            char currCell = readUnsignedCell(i);
            newBitBuffer = (char) ((currCell & 1));
            currCell = (char) ((currCell>>1) | oldBitBuffer<<15);
            oldBitBuffer = newBitBuffer;
            writeUnsignedCell(i, currCell);
        }
        clearEmptyCells();
    }

    public UnsignedBigInteger multiply(UnsignedBigInteger other) {
        UnsignedBigInteger buffer = new UnsignedBigInteger(this.unsignedCells);
        UnsignedBigInteger answer = new UnsignedBigInteger(0);
        for(int i = 0; i < other.unsignedCells.size() * 16; i++) {
            if (other.getBit(i) == 1)
                answer = answer.plus(buffer);
            buffer.mul2();
        }
        return answer;
    }

    public UnsignedBigInteger divide(UnsignedBigInteger other) {
        return new UnsignedBigInteger(this.unsignedCells).divideAndGetRemainder(other);
    }

    public UnsignedBigInteger getRemainder(UnsignedBigInteger other) {
        UnsignedBigInteger answer = new UnsignedBigInteger(this.unsignedCells);
        answer.divideAndGetRemainder(other);
        return answer;
    }

    private void writeUnsignedCell(int index, char value) {
        unsignedCells.set(index, value);
    }

    public UnsignedBigInteger plus(UnsignedBigInteger other) {
        UnsignedBigInteger result = new UnsignedBigInteger(this.unsignedCells);
        while(other.unsignedCells.size() > result.unsignedCells.size()) result.appendUnsignedCell((char) 0);
        int buffer = 0;
        for(int i = 0; i < result.unsignedCells.size(); i++) {
            int sum;
            if(other.unsignedCells.size() > i)
                sum = (int)result.readUnsignedCell(i) + (int)other.readUnsignedCell(i) + buffer;
            else if(buffer != 0)
                sum = (int)result.readUnsignedCell(i) + buffer;
            else break;
            buffer = sum / 0x01_00_00;
            result.writeUnsignedCell(i, (char)(sum % 0x01_00_00));
        }
        if(buffer != 0) result.appendUnsignedCell((char) buffer);
        return result;
    }

    private void clearEmptyCells() {
        for(int i = unsignedCells.size() - 1 ; i > 0; i--) {
            if(readUnsignedCell(i) == (char) 0) unsignedCells.removeLast();
            else break;
        }
    }

    private UnsignedBigInteger eternalMinus(UnsignedBigInteger other) {
        int buffer = 0;
        UnsignedBigInteger result = new UnsignedBigInteger(this.unsignedCells);
        if(other.unsignedCells.size() > result.unsignedCells.size())
            return new UnsignedBigInteger(0);
        for(int i = 0; i < result.unsignedCells.size(); i++) {
            int deduct;
            if(other.unsignedCells.size() > i)
                deduct = (int)result.readUnsignedCell(i) - (int)other.readUnsignedCell(i) - buffer;
            else
                deduct = (int)result.readUnsignedCell(i) - buffer;
            if(deduct < 0) {
                buffer = 1;
                deduct += 0x1_00_00 ;
            } else buffer = 0;
            result.writeUnsignedCell(i, (char)(deduct));
        }
        result.clearEmptyCells();
        if (buffer != 0)
            return new UnsignedBigInteger(0);
        return result;
    }

    public  UnsignedBigInteger minus(UnsignedBigInteger other)  {
        if(other.isBiggerThan(this))
            throw new ArithmeticException("Беззнаковое число не может быть отрицательным.");
        return this.eternalMinus(other);
    }

    private char readUnsignedCell(int index) {
        return unsignedCells.get(index);
    }

    private void appendUnsignedCell(char value) {
        unsignedCells.add(value);
    }

    public UnsignedBigInteger(int value) {
        unsignedCells = new LinkedList<>();
        if(value < 0)
            appendUnsignedCell((char) 0);
        else {
            appendUnsignedCell((char)(value % 0x01_00_00));
            if(value / 0x01_00_00 != 0) appendUnsignedCell((char)(value / 0x01_00_00));
        }
    }

    private UnsignedBigInteger(LinkedList <Character> cells) {
        unsignedCells = new LinkedList<>(cells);
    }

    public UnsignedBigInteger(String value) {
        UnsignedBigInteger buffer = new UnsignedBigInteger(0);
        UnsignedBigInteger rankBuff = new UnsignedBigInteger(1);
        for(int i = value.length() - 1; i >=0 ; i--) {
            int number = (int)(value.charAt(i) - '0');
            buffer = buffer.plus(new UnsignedBigInteger(number).multiply(rankBuff));
            rankBuff = rankBuff.multiply(ten);
        }
        unsignedCells = new LinkedList<>(buffer.unsignedCells);
    }

    /**
     * Выводит текущее значение беззнаковых ячеек.
    * */
    public String getBinaryString() {
        StringBuilder answer = new StringBuilder();
        for(int i = unsignedCells.size() * 16 - 1 ; i >= 0 ;i--) {
            answer.append(this.getBit(i));
        }
        return answer.toString();
    }

    public int toInt(){
        if ((unsignedCells.size() == 2) && (getBit(31) != 1)) {
            return readUnsignedCell(0) + readUnsignedCell(1) * 0x1_00_00;
        }
        else if (unsignedCells.size() == 1)
            return readUnsignedCell(0);
        else return -1;
    }

    public String toString() {
        StringBuilder answer = new StringBuilder();
        UnsignedBigInteger buffer = new UnsignedBigInteger(this.unsignedCells);
        do {
            answer.append(buffer.getRemainder(ten).toInt());
            buffer = buffer.divide(ten);
        } while(!buffer.equals(zero));
        return answer.reverse().toString();
    }

    public boolean equals(UnsignedBigInteger other) {
        int result1 = new UnsignedBigInteger(this.unsignedCells)
                .eternalMinus(new UnsignedBigInteger(other.unsignedCells)).toInt();
        int result2 = new UnsignedBigInteger(other.unsignedCells)
                .eternalMinus(new UnsignedBigInteger(this.unsignedCells)).toInt();
        return result1 == result2 && result1 == 0;
    }

    public boolean isBiggerThan(UnsignedBigInteger other) {
        return this.eternalMinus(other).toInt() != 0;
    }

    public boolean isSmallerThan(UnsignedBigInteger other) {
        return other.eternalMinus(this).toInt() != 0;
    }

    private UnsignedBigInteger divideAndGetRemainder(UnsignedBigInteger divisor)  {
        if(divisor.equals(zero))
            throw new ArithmeticException("Деление на ноль.");
        UnsignedBigInteger divBuff = new UnsignedBigInteger(divisor.unsignedCells);
        long counter = -1;
        while(!this.isSmallerThan(divBuff)) {
            divBuff.mul2();
            counter++;
        }
        divBuff.div2();
        UnsignedBigInteger result = new UnsignedBigInteger(0);
        if(counter == -1) {
            return result;
        }
        for(int rankSteps = 0; rankSteps <= counter; rankSteps++){
            if(!this.isSmallerThan(divBuff)) {
                UnsignedBigInteger bufferOfThis  = this.eternalMinus(divBuff);
                this.unsignedCells.clear();
                this.unsignedCells.addAll(bufferOfThis.unsignedCells);
                result.mul2();
                result = result.plus(new UnsignedBigInteger(1));
            } else result.mul2();
            divBuff.div2();
        }
        this.clearEmptyCells();
        return result;
    }

}
