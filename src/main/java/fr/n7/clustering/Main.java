package fr.n7.clustering;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import fr.n7.clustering.methods.Method1;
import fr.n7.clustering.methods.Method2;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: Clustering.jar <method-number> <number-of-regions>");
        }

        try (FileReader fileReader = new FileReader("res/generated.csv")) {
            try (CSVReader csvReader = new CSVReaderBuilder(fileReader)
                    .withSkipLines(1)
                    .build()) {

                List<Record> allData = csvReader
                        .readAll()
                        .stream()
                        .map(Record::new)
                        .toList();

                int method = Integer.parseInt(args[0]);
                short regions = Short.parseShort(args[1]);

                System.out.println("Using method " + method + ", with " + regions + " regions");

                switch (method) {
                    case 1 -> new Method1().run_xyz(allData, regions);
                    case 2 -> new Method2().run_xyz(allData, regions);
                    default -> throw new RuntimeException("Unknown method number");
                }
            }

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }
}