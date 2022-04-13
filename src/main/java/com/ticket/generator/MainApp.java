package com.ticket.generator;

import com.ticket.generator.service.StripGeneratorService;

import java.util.Date;

public class MainApp {

    public static void main(String[] args) {
        System.out.println("Start app");
        StripGeneratorService stripGenerator = new StripGeneratorService();

        long startTime = new Date().getTime();
        for (int i = 1; i <= 10000; i++) {
//            if (i % 100 == 0) {
//                System.out.println(i);
//            }
            stripGenerator.generateStrip();
        }
//        stripGenerator.generateStrip();
        long endTime = new Date().getTime();

        System.out.println("10000 strips generated in " + (endTime - startTime) / 1000 + " seconds");
        stripGenerator.generateStrip().print();
    }
}
