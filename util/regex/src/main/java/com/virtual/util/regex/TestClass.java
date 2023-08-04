package com.virtual.util.regex;

public class TestClass {

    public static void main(String[] args) {
        System.out.println("start main");
        System.out.println(PhoneRegexUtils.checkPhone("13456789432"));
        System.out.println(PhoneRegexUtils.containsPhone("abc13456789432pyt"));
        System.out.println(PhoneRegexUtils.takePhone("abc13456789432pyt"));
    }
}