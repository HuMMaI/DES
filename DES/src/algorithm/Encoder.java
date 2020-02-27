package algorithm;

import java.math.BigInteger;

public class Encoder {
    public void startEncoder(String text, String key){
        String permutationBinaryStr = initialPermutation(text);
        System.out.println("Permutation binary: " + permutationBinaryStr + '|' + permutationBinaryStr.length());
        String binaryKey = keysGenerator(key);
        System.out.println("Binary key: " + binaryKey + "|" + binaryKey.length());
    }

    private String initialPermutation(String text){
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
            while(textToBit.length() < 64){
                textToBit.insert(0, '0');
            }
        }

        System.out.println("Binary: " + textToBit + "|" + textToBit.length());

        StringBuilder permutationStr = new StringBuilder();

        for (int i = 0; i < ip.length; i++){
            for (int j = 0; j < ip[i].length; j++){
                permutationStr.append(textToBit.charAt(ip[i][j] - 1));
            }
        }

        return new String(permutationStr);
    }

    private String keysGenerator(String key){
        int kp[][] = {
                {57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18},
                {10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36},
                {63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22},
                {14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4}
        };

        StringBuilder keyToBit = new StringBuilder(new BigInteger(key.getBytes()).toString(2));

        if (keyToBit.length() < 64){
            while(keyToBit.length() < 64){
                keyToBit.insert(0, '0');
            }
        }

        StringBuilder permutationKey = new StringBuilder();

        for (int i = 0; i < kp.length; i++){
            for (int j = 0; j < kp[i].length; j++){
                permutationKey.append(keyToBit.charAt(kp[i][j] - 1));
            }
        }

        return new String(permutationKey);
    }
}
