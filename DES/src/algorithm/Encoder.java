package algorithm;

import java.math.BigInteger;

public class Encoder {
    public StringBuilder startEncoder(String text, String key){
        StringBuilder permutationBinaryStr = initialPermutation(text);
        System.out.println("Permutation binary: " + permutationBinaryStr + '|' + permutationBinaryStr.length());
        StringBuilder[] binaryKeys = keysGenerator(key);

        StringBuilder encryptionText = encryptionRounds(permutationBinaryStr, binaryKeys);
        System.out.println("EncText: " + encryptionText + " | length: " + encryptionText.length());

        return encryptionText;
//        String s = new BigInteger(key.getBytes()).toString();
//        System.out.println("text: " + s);
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
        StringBuilder nextRightBlock = new StringBuilder(expansionPermutation(rightBlock));
        nextRightBlock = keyXorring(nextRightBlock, binaryKey);
        nextRightBlock = bitExpansion(nextRightBlock, 48);
        nextRightBlock = sBox(nextRightBlock);
        nextRightBlock = pBox(nextRightBlock);
        nextRightBlock = leftBlockXorring(leftBlock, nextRightBlock);
        nextRightBlock = bitExpansion(nextRightBlock, 32);

        return nextRightBlock;
    }

    private StringBuilder leftBlockXorring(StringBuilder leftBlock, StringBuilder nextRightBlock) {
        return new StringBuilder((new BigInteger(nextRightBlock.toString(), 2)
                .xor(new BigInteger(leftBlock.toString(), 2)))
                .toString(2));
    }

    private StringBuilder pBox(StringBuilder nextRightBlock) {
        int pBoxPerm[][] = {
                {16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5, 18, 31, 10},
                {2, 8, 24, 14, 32, 27, 3, 9, 19, 13, 30, 6, 22, 11, 4, 25}
        };

        StringBuilder permutationStr = new StringBuilder();

        for (int i = 0; i < pBoxPerm.length; i++){
            for (int j = 0; j < pBoxPerm[i].length; j++){
                permutationStr.append(nextRightBlock.charAt(pBoxPerm[i][j] - 1));
            }
        }

        return permutationStr;
    }

    private StringBuilder sBox(StringBuilder nextRightBlock) {
        int sBoxes[][][] = {
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

//        int sBox2[][] = {
//                {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10},
//                {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5},
//                {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15},
//                {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12 ,0, 5, 14, 9}
//        };

//        int sBox3[][] = {
//                {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8},
//                {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1},
//                {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7},
//                {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}
//        };

//        int sBox4[][] = {
//                {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15},
//                {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9},
//                {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4},
//                {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}
//        };

//        int sBox5[][] = {
//                {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9},
//                {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6},
//                {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14},
//                {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}
//        };

//        int sBox6[][] = {
//                {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11},
//                {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8},
//                {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6},
//                {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}
//        };

//        int sBox7[][] = {
//                {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
//                {13, 0, 11, 7, 4, 9, 1, 10, 14, 4, 5, 12, 2, 15, 8, 6},
//                {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2},
//                {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}
//        };

//        int sBox8[][] = {
//                {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7},
//                {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
//                {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8},
//                {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}
//        };

        StringBuilder fourBitStr[] = new StringBuilder[8];

        System.out.println("length: " + nextRightBlock.length());
        for (int i = 0, j = 0, k = 5; i < fourBitStr.length; i++, j+=6, k+=6){
            int row = Integer.parseInt(String.valueOf(new StringBuilder().append(nextRightBlock.charAt(j)).append(nextRightBlock.charAt(k))), 2);
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
        StringBuilder xorStr =
                new StringBuilder((new BigInteger(nextRightBlock.toString(), 2)
                        .xor(new BigInteger(binaryKey.toString(), 2)))
                        .toString(2));

        return xorStr;
    }

    private StringBuilder expansionPermutation(StringBuilder rightBlock) {
        int ep[][] = {
                {32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9},
                {8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17},
                {16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25},
                {24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1}
        };

        StringBuilder permutationStr = new StringBuilder();

        for (int i = 0; i < ep.length; i++){
            for (int j = 0; j < ep[i].length; j++){
                permutationStr.append(rightBlock.charAt(ep[i][j] - 1));
            }
        }

        return permutationStr;
    }

    private StringBuilder initialPermutation(String text){
        int ip[][] = {
                {58, 50, 42, 34, 26, 18, 10, 2},
                {60, 52, 44, 36, 28, 20, 12, 4},
                {62, 54, 46, 38, 30, 22, 14, 6},
                {64, 56, 48, 40, 32, 24, 16, 8},
                {57, 49, 41, 33, 25, 17, 9, 1},
                {59, 51, 43, 35, 27, 19, 11, 3},
                {61, 53, 45, 37, 29, 21, 13, 5},
                {63, 55, 47, 39, 31, 23, 15, 7}
        };

        StringBuilder textToBit = new StringBuilder(new BigInteger(text.getBytes()).toString(2));

        if (textToBit.length() < 64){
            textToBit = bitExpansion(textToBit, 64);
        }

        System.out.println("Binary: " + textToBit + "|" + textToBit.length());

        StringBuilder permutationStr = new StringBuilder();

        for (int i = 0; i < ip.length; i++){
            for (int j = 0; j < ip[i].length; j++){
                permutationStr.append(textToBit.charAt(ip[i][j] - 1));
            }
        }

        return permutationStr;
    }

    private StringBuilder[] keysGenerator(String key){
        int kp[][] = {
                {57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18},
                {10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36},
                {63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22},
                {14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4}
        };

        StringBuilder keyToBit = new StringBuilder(new BigInteger(key.getBytes()).toString(2));

        if (keyToBit.length() < 64){
            keyToBit = bitExpansion(keyToBit, 64);
        }

        StringBuilder permutationKey = new StringBuilder();

        for (int i = 0; i < kp.length; i++){
            for (int j = 0; j < kp[i].length; j++){
                permutationKey.append(keyToBit.charAt(kp[i][j] - 1));
            }
        }

        StringBuilder c0 = new StringBuilder(permutationKey.substring(0, 28));
        StringBuilder d0 = new StringBuilder(permutationKey.substring(28, permutationKey.length()));

        StringBuilder[] keys = bitShift(c0, d0);

        int cp[][] = {
                {14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10},
                {23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2},
                {41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48},
                {44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32}
        };

        StringBuilder compressionKeys[] = new StringBuilder[16];

        for (int i = 0; i < keys.length; i++){
            compressionKeys[i] = new StringBuilder();
            for (int j = 0; j < cp.length; j++){
                for (int k = 0; k < cp[j].length; k++){
                    compressionKeys[i].append(keys[i].charAt(cp[j][k] - 1));
                }
            }
        }

        //TODO Delete outputting
        System.out.println("Perm keys::");
        for (int i = 0; i < compressionKeys.length; i++){
            System.out.println("Key[" + i + "] : " + compressionKeys[i] + " | length: " + compressionKeys[i].length());
        }

        return compressionKeys;
    }

    private StringBuilder[] bitShift(StringBuilder c0, StringBuilder d0){
        StringBuilder keys[] = new StringBuilder[16];

        int shiftRule[] = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};

        //TODO Add threads

        for (int i = 0; i < keys.length; i++){
            StringBuilder c1 = new StringBuilder(new BigInteger(c0.toString(), 2).shiftLeft(shiftRule[i]).toString(2));
            StringBuilder d1 = new StringBuilder(new BigInteger(d0.toString(), 2).shiftLeft(shiftRule[i]).toString(2));

            if (c1.length() > 28 || d1.length() > 28){
                c1 = sizeChecker(c1);
                d1 = sizeChecker(d1);
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

    private StringBuilder sizeChecker(StringBuilder str) {
        int delta = str.length() - 28;

        if (delta == 1){
            str.replace(str.length() - 1, str.length(), String.valueOf(str.charAt(0)));
            str.deleteCharAt(0);
        } else if (delta == 2){
            str.replace(str.length() - 2, str.length(), str.substring(0, 2));
            str.delete(0, 2);
        }

        return str;
    }

    //TODO Delete duplications
    private StringBuilder bitExpansion(StringBuilder bin, int numberOfBits){
        while (bin.length() < numberOfBits){
            bin.insert(0, '0');
        }

        return bin;
    }
}
