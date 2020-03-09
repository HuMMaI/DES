package algorithm;

import java.math.BigInteger;
import java.util.concurrent.CountDownLatch;

public class Encoder {
    public StringBuilder startEncoder(StringBuilder text, String key){
        StringBuilder permutationBinaryStr = new StringBuilder();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Runnable initPerm = () -> {
            permutationBinaryStr.append(permutation(text, Tools.ip, PermutationType.INITIAL_PERMUTATION));
            countDownLatch.countDown();
        };

        Thread initPermThread = new Thread(initPerm);
        initPermThread.start();

        StringBuilder[] binaryKeys = keysGenerator(key);

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        initPermThread.interrupt();

        StringBuilder encryptionText = encryptionRounds(permutationBinaryStr, binaryKeys);
        System.out.println("EncTextBin: " + encryptionText + " | length: " + encryptionText.length());

        encryptionText = parseString(encryptionText);
        System.out.println("EncText: " + encryptionText + " | length: " + encryptionText.length());

        return encryptionText;
    }

    private StringBuilder parseString(StringBuilder encryptionText) {
        StringBuilder str = new StringBuilder();

        for (int i = 0, j = 8; j <= encryptionText.length(); i += 8, j += 8){
            String buff = encryptionText.substring(i, j);
            str.append((char)Integer.parseInt(buff, 2));
        }

        return str;
    }

    private StringBuilder encryptionRounds(StringBuilder permutationBinaryStr, StringBuilder[] binaryKeys) {
        StringBuilder[] leftBlock = new StringBuilder[17];
        StringBuilder[] rightBlock = new StringBuilder[17];

        leftBlock[0] = new StringBuilder(permutationBinaryStr.substring(0, 32));
        rightBlock[0] = new StringBuilder(permutationBinaryStr.substring(32, permutationBinaryStr.length()));

        for (int i = 1; i < leftBlock.length; i++){
            leftBlock[i] = new StringBuilder(rightBlock[i - 1]);
            rightBlock[i] = rightBlockOperations(leftBlock[i - 1], rightBlock[i - 1], binaryKeys[i - 1]);
        }

        return new StringBuilder().append(leftBlock[leftBlock.length - 1]).append(rightBlock[rightBlock.length - 1]);
    }

    private StringBuilder rightBlockOperations(StringBuilder leftBlock, StringBuilder rightBlock, StringBuilder binaryKey) {
        StringBuilder nextRightBlock = permutation(rightBlock, Tools.ep, PermutationType.EXPANSION_PERMUTATION);
        nextRightBlock = keyXorring(nextRightBlock, binaryKey);
        bitExpansion(nextRightBlock, 48);
        nextRightBlock = sBox(nextRightBlock);
        nextRightBlock = permutation(nextRightBlock, Tools.pBoxPerm, PermutationType.P_BOX_PERMUTATION);
        nextRightBlock = leftBlockXorring(leftBlock, nextRightBlock);
        bitExpansion(nextRightBlock, 32);

        return nextRightBlock;
    }

    private StringBuilder leftBlockXorring(StringBuilder leftBlock, StringBuilder nextRightBlock) {
        return new StringBuilder((new BigInteger(nextRightBlock.toString(), 2)
                .xor(new BigInteger(leftBlock.toString(), 2)))
                .toString(2));
    }

    private StringBuilder sBox(StringBuilder nextRightBlock) {
        int[][][] sBoxes = {
                {
                        {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7},
                        {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8},
                        {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0},
                        {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}
                },

                {
                        {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10},
                        {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5},
                        {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15},
                        {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12 ,0, 5, 14, 9}
                },

                {
                        {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8},
                        {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1},
                        {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7},
                        {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}
                },

                {
                        {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15},
                        {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9},
                        {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4},
                        {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}
                },

                {
                        {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9},
                        {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6},
                        {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14},
                        {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}
                },

                {
                        {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11},
                        {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8},
                        {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6},
                        {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}
                },

                {
                        {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
                        {13, 0, 11, 7, 4, 9, 1, 10, 14, 4, 5, 12, 2, 15, 8, 6},
                        {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2},
                        {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}
                },

                {
                        {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7},
                        {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
                        {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8},
                        {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}
                }
        };

        StringBuilder[] fourBitStr = new StringBuilder[8];

        System.out.println("length: " + nextRightBlock.length());
        for (int i = 0, j = 0, k = 5; i < fourBitStr.length; i++, j+=6, k+=6){
            int row = Integer.parseInt(String.valueOf(nextRightBlock.charAt(j)) + nextRightBlock.charAt(k), 2);
            int column = Integer.parseInt(String.valueOf(nextRightBlock.substring(j + 1, k)), 2);

            fourBitStr[i] = new StringBuilder(Integer.toBinaryString(sBoxes[i][row][column]));

            if (fourBitStr[i].length() < 4){
                fourBitStr[i] = bitExpansion(fourBitStr[i], 4);
            }
        }

        nextRightBlock = new StringBuilder();

        for (int i = 0; i < fourBitStr.length; i++){
            nextRightBlock.append(fourBitStr[i]);
        }

        return nextRightBlock;
    }

    private StringBuilder keyXorring(StringBuilder nextRightBlock, StringBuilder binaryKey) {
        return new StringBuilder((new BigInteger(nextRightBlock.toString(), 2)
                .xor(new BigInteger(binaryKey.toString(), 2)))
                .toString(2));
    }

    private StringBuilder permutation(StringBuilder text, int[][] permutationRule, PermutationType permutationType){
        StringBuilder binaryStr = new StringBuilder();

        switch (permutationType){
            case INITIAL_PERMUTATION:

            case KEY_PERMUTATION:
                binaryStr.append(parseBit(text, 64));
                break;

            case EXPANSION_PERMUTATION:

            case P_BOX_PERMUTATION:

            case COMPRESS_PERMUTATION:
                binaryStr.append(text);
                break;
        }

        System.out.println("Binary: " + binaryStr + "|" + binaryStr.length());

        StringBuilder permutationStr = new StringBuilder();

        for (int i = 0; i < permutationRule.length; i++){
            for (int j = 0; j < permutationRule[i].length; j++){
                permutationStr.append(binaryStr.charAt(permutationRule[i][j] - 1));
            }
        }

        return permutationStr;
    }

    private StringBuilder[] keysGenerator(String key){
        StringBuilder permutationKey = permutation(new StringBuilder(key), Tools.kp, PermutationType.KEY_PERMUTATION);

        StringBuilder c0 = new StringBuilder(permutationKey.substring(0, 28));
        StringBuilder d0 = new StringBuilder(permutationKey.substring(28, permutationKey.length()));

        StringBuilder[] keys = bitShift(c0, d0);

        StringBuilder[] compressionKeys = new StringBuilder[16];

        for (int i = 0; i < keys.length; i++){
            compressionKeys[i] = permutation(keys[i], Tools.cp, PermutationType.COMPRESS_PERMUTATION);
        }

        //TODO Delete outputting
        System.out.println("Perm keys::");
        for (int i = 0; i < compressionKeys.length; i++){
            System.out.println("Key[" + i + "] : " + compressionKeys[i] + " | length: " + compressionKeys[i].length());
        }

        return compressionKeys;
    }

    private StringBuilder parseBit(StringBuilder text, int numberOfBits) {
        StringBuilder bitStr = new StringBuilder();

        for (int i = 0; i < text.length(); i++){
            String bits = new BigInteger(String.valueOf(text.charAt(i)).getBytes()).toString(2);

            if (bits.length() < 8){
                bits = bitExpansion(new StringBuilder(bits), 8).toString();
            }

            bitStr.append(bits);
        }


        return bitStr.length() < 64 ? bitExpansion(bitStr, numberOfBits) : bitStr;
    }

    private StringBuilder[] bitShift(StringBuilder c0, StringBuilder d0){
        StringBuilder[] keys = new StringBuilder[16];

        int[] shiftRule = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};

        for (int i = 0; i < keys.length; i++){
            StringBuilder c1 = new StringBuilder(new BigInteger(c0.toString(), 2).shiftLeft(shiftRule[i]).toString(2));
            StringBuilder d1 = new StringBuilder(new BigInteger(d0.toString(), 2).shiftLeft(shiftRule[i]).toString(2));

            if (c1.length() > 28 || d1.length() > 28){
                StringBuilder finalC = c1;
                CountDownLatch countDownLatch = new CountDownLatch(1);

                Runnable keyShift = () -> {
                    sizeChecker(finalC);
                    countDownLatch.countDown();
                };

                Thread keyShiftThread = new Thread(keyShift);
                keyShiftThread.start();

                sizeChecker(d1);

                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                keyShiftThread.interrupt();

                c1 = finalC;
            }

            if (c1.length() < 28 || d1.length() < 28){
                c1 = new StringBuilder(bitExpansion(c1, 28));
                d1 = new StringBuilder(bitExpansion(d1, 28));
            }

            c0 = new StringBuilder(c1);
            d0 = new StringBuilder(d1);

            keys[i] = new StringBuilder().append(c1).append(d1);
        }

        //TODO Delete outputting
        for (int i = 0; i < keys.length; i++){
            System.out.println("Key[" + i + "] : " + keys[i] + " | length: " + keys[i].length());
        }

        return keys;
    }

    private void sizeChecker(StringBuilder str) {
        int delta = str.length() - 28;

        if (delta == 1){
            str.replace(str.length() - 1, str.length(), String.valueOf(str.charAt(0)));
            str.deleteCharAt(0);
        } else if (delta == 2){
            str.replace(str.length() - 2, str.length(), str.substring(0, 2));
            str.delete(0, 2);
        }
    }

    //TODO Delete duplications
    private StringBuilder bitExpansion(StringBuilder bin, int numberOfBits){
        while (bin.length() < numberOfBits){
            bin.insert(0, '0');
        }

        return bin;
    }
}
