package algorithm;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class Encoder {
    public String[] startEncoder(StringBuilder[] strBinMapping, String key, DESMode desMode) {
        StringBuilder text = strBinMapping[0];
        StringBuilder bin = strBinMapping[1];
        int numberOfSymbols = 8;
        int numberOfBitsSplit = 8;

        if (desMode.equals(DESMode.ENCRYPTION_CYRILLIC) || desMode.equals(DESMode.DECRYPTION_CYRILLIC)){
            numberOfBitsSplit = 16;
            numberOfSymbols = 4;
        }

        List<StringBuilder> binaryText = new ArrayList<>();
        if (bin.toString().equals("")){
            List<StringBuilder> split = textSplitter(text, numberOfSymbols);

            for (int i = 0; i < split.size(); i++){
                binaryText.add(parseBit(split.get(i), 64, numberOfBitsSplit));
            }
        } else {
            binaryText = textSplitter(bin, 64);
        }

        List<StringBuilder> permutationBinaryBlocks = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        List<StringBuilder> finalBinaryText = binaryText;
        Runnable initPerm = () -> {
            for (int i = 0; i < finalBinaryText.size(); i++) {
                StringBuilder permutation = permutation(finalBinaryText.get(i), Tools.ip, PermutationType.INITIAL_PERMUTATION);
                permutationBinaryBlocks.add(permutation);
            }

            countDownLatch.countDown();
        };

        Thread initPermThread = new Thread(initPerm);
        initPermThread.start();

        StringBuilder[] binaryKeys = keysGenerator(key, desMode);

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        initPermThread.interrupt();

        List<StringBuilder> encryptionRounds = encryptionRounds(permutationBinaryBlocks, binaryKeys, desMode);

        List<StringBuilder> finalPermText = new ArrayList<>();

        for (int i = 0; i < encryptionRounds.size(); i++) {
            finalPermText.add(permutation(encryptionRounds.get(i), Tools.fp, PermutationType.FINAL_PERMUTATION));
        }

        String finalBinText = finalPermText.stream()
                .map(Object::toString)
                .collect(Collectors.joining());

        StringBuilder finalText = parseString(finalPermText, numberOfBitsSplit);

        String[] finalStrBinMapping = {finalText.toString(), finalBinText};

        return finalStrBinMapping;
    }

    private int count(StringBuilder text) {
        StringBuilder textCopy = new StringBuilder(text);
        int numberOfBits = 8;

        char[] chars = textCopy.toString().toCharArray();
        List<Character> charsList = new ArrayList<>();

        for (char sym: chars) {
            charsList.add(sym);
        }

        Character character = charsList.stream()
                .filter(bin -> (int) bin > 255)
                .findAny()
                .orElse(null);

        if (character != null){
            numberOfBits = 16;
        }

        return numberOfBits;
    }

    private List<StringBuilder> textSplitter(StringBuilder text, int numberOfSymbols) {
        int numberOfSplits = (int) Math.ceil(text.length() / (double)numberOfSymbols);

        List<StringBuilder> textBlocks = new ArrayList<>();

        for (int i = 0, j = 0; i < numberOfSplits; i++, j += numberOfSymbols) {
            int k = j + numberOfSymbols;

            if (i == numberOfSplits - 1) {
                k = text.length();
            }

            StringBuilder split = new StringBuilder(text.substring(j, k));

            textBlocks.add(split);
        }

        return textBlocks;
    }

    private StringBuilder permutation(StringBuilder text, int[][] permutationRule, PermutationType permutationType) {
        StringBuilder binaryStr = new StringBuilder();

        if (permutationType.equals(PermutationType.KEY_PERMUTATION)){
            //TODO key cyrillic
            binaryStr.append(parseBit(text, 64, 8));
        } else {
            binaryStr.append(text);
        }

        StringBuilder permutationStr = new StringBuilder();

        for (int i = 0; i < permutationRule.length; i++) {
            for (int j = 0; j < permutationRule[i].length; j++) {
                permutationStr.append(binaryStr.charAt(permutationRule[i][j] - 1));
            }
        }

        return permutationStr;
    }

    private StringBuilder parseString(List<StringBuilder> encryptionTexts, int split) {
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < encryptionTexts.size(); i++) {
            for (int j = 0, k = split; k <= encryptionTexts.get(i).length(); j += split, k += split) {
                String buff = encryptionTexts.get(i).substring(j, k);

                str.append((char) Integer.parseInt(buff, 2));
            }
        }
        return str;
    }

    private List<StringBuilder> encryptionRounds(List<StringBuilder> permutationBinaryStr, StringBuilder[] binaryKeys, DESMode desMode) {
        List<StringBuilder[]> leftBlocks = new ArrayList<>();
        List<StringBuilder[]> rightBlocks = new ArrayList<>();

        for (int i = 0; i < permutationBinaryStr.size(); i++) {
            leftBlocks.add(new StringBuilder[17]);
            rightBlocks.add(new StringBuilder[17]);
        }

        final int[] index = {0, 0};
        leftBlocks.forEach(s -> {
            s[0] = new StringBuilder(permutationBinaryStr.get(index[0]).substring(0, 32));
            index[0]++;
        });

        rightBlocks.forEach(s -> {
            s[0] = new StringBuilder(permutationBinaryStr.get(index[1]).substring(32, permutationBinaryStr.get(index[1]).length()));
            index[1]++;
        });

        List<StringBuilder> results = new ArrayList<>();


        if (desMode.equals(DESMode.DECRYPTION) || desMode.equals(DESMode.DECRYPTION_CYRILLIC)) {
            binaryKeys = keysReverse(binaryKeys);
        }

        for (int i = 0; i < leftBlocks.size(); i++) {
            for (int j = 1; j < leftBlocks.get(i).length; j++) {
                leftBlocks.get(i)[j] = new StringBuilder(rightBlocks.get(i)[j - 1]);
                rightBlocks.get(i)[j] = rightBlockOperations(leftBlocks.get(i)[j - 1], rightBlocks.get(i)[j - 1], binaryKeys[j - 1]);
            }
            results.add(new StringBuilder()
                    .append(rightBlocks.get(i)[rightBlocks.get(i).length - 1])
                    .append(leftBlocks.get(i)[leftBlocks.get(i).length - 1]));
        }

        return results;
    }

    private StringBuilder[] keysReverse(StringBuilder[] binaryKeys) {
        StringBuilder[] copyBinaryKeys = binaryKeys;

        for (int i = 0; i < copyBinaryKeys.length / 2; i++) {
            StringBuilder temp = copyBinaryKeys[i];
            copyBinaryKeys[i] = copyBinaryKeys[copyBinaryKeys.length - i - 1];
            copyBinaryKeys[copyBinaryKeys.length - i - 1] = temp;
        }

        return copyBinaryKeys;
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
                        {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}
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

        for (int i = 0, j = 0, k = 5; i < fourBitStr.length; i++, j += 6, k += 6) {
            int row = Integer.parseInt(String.valueOf(nextRightBlock.charAt(j)) + nextRightBlock.charAt(k), 2);
            int column = Integer.parseInt(String.valueOf(nextRightBlock.substring(j + 1, k)), 2);

            fourBitStr[i] = new StringBuilder(Integer.toBinaryString(sBoxes[i][row][column]));

            if (fourBitStr[i].length() < 4) {
                fourBitStr[i] = bitExpansion(fourBitStr[i], 4);
            }
        }

        nextRightBlock = new StringBuilder();

        for (int i = 0; i < fourBitStr.length; i++) {
            nextRightBlock.append(fourBitStr[i]);
        }

        return nextRightBlock;
    }

    private StringBuilder keyXorring(StringBuilder nextRightBlock, StringBuilder binaryKey) {
        return new StringBuilder((new BigInteger(nextRightBlock.toString(), 2)
                .xor(new BigInteger(binaryKey.toString(), 2)))
                .toString(2));
    }

    private StringBuilder[] keysGenerator(String key, DESMode desMode) {
        StringBuilder permutationKey = permutation(new StringBuilder(key), Tools.kp, PermutationType.KEY_PERMUTATION);

        StringBuilder c0 = new StringBuilder(permutationKey.substring(0, 28));
        StringBuilder d0 = new StringBuilder(permutationKey.substring(28, permutationKey.length()));

        StringBuilder[] keys = bitShift(c0, d0, desMode);

        StringBuilder[] compressionKeys = new StringBuilder[16];

        for (int i = 0; i < keys.length; i++) {
            compressionKeys[i] = permutation(keys[i], Tools.cp, PermutationType.COMPRESS_PERMUTATION);
        }

        return compressionKeys;
    }

    private StringBuilder[] bitShift(StringBuilder c0, StringBuilder d0, DESMode desMode) {
        StringBuilder[] keys = new StringBuilder[16];

        for (int i = 0; i < keys.length; i++) {
            StringBuilder c1 = new StringBuilder(new BigInteger(c0.toString(), 2)
                    .shiftLeft(Tools.encryptionShiftRule[i]).toString(2));
            StringBuilder d1 = new StringBuilder(new BigInteger(d0.toString(), 2)
                    .shiftLeft(Tools.encryptionShiftRule[i]).toString(2));

            if (c1.length() > 28 || d1.length() > 28) {
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

            if (c1.length() < 28 || d1.length() < 28) {
                c1 = new StringBuilder(bitExpansion(c1, 28));
                d1 = new StringBuilder(bitExpansion(d1, 28));
            }

            c0 = new StringBuilder(c1);
            d0 = new StringBuilder(d1);

            keys[i] = new StringBuilder().append(c1).append(d1);
        }

        return keys;
    }

    private StringBuilder parseBit(StringBuilder text, int numberOfBits, int split) {
        StringBuilder bitStr = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            int charInt = text.charAt(i);
            String bits = Integer.toBinaryString(charInt);

            if (bits.length() < split) {
                bits = bitExpansion(new StringBuilder(bits), split).toString();
            }

            bitStr.append(bits);
        }

        return bitStr.length() < 64 ? bitExpansion(bitStr, numberOfBits) : bitStr;
    }

    private void sizeChecker(StringBuilder str) {
        int delta = str.length() - 28;

        if (delta == 1) {
            str.replace(str.length() - 1, str.length(), String.valueOf(str.charAt(0)));
            str.deleteCharAt(0);
        } else if (delta == 2) {
            str.replace(str.length() - 2, str.length(), str.substring(0, 2));
            str.delete(0, 2);
        }
    }

    //TODO Delete duplications
    private StringBuilder bitExpansion(StringBuilder bin, int numberOfBits) {
        while (bin.length() < numberOfBits) {
            bin.insert(0, '0');
        }

        return bin;
    }
}
