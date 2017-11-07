package com;

public class Nil {
    public static void main(String[] args) {
        System.out.println((2 << 15) - 1);
        System.out.println(2 << 32);

        // HHyyssMMmmdd
        // 18088456812 299045
        //  9738438326 299045
        // 15668136717 299045
        String basicModuleName = "120-abc";
        if (basicModuleName.contains("-")) {
            basicModuleName = basicModuleName.substring(basicModuleName.indexOf("-") + 1);
        }
        System.out.println(basicModuleName);
    }
}
