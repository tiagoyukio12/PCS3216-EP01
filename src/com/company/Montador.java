package com.company;

import java.io.*;
import java.util.HashMap;

class Montador {
    private int ic;
    private int byteCounter;
    private HashMap<String, String> tabSimb;
    private HashMap<String, Integer> tabMne;

    Montador() {
        ic = 0;
        byteCounter = 0;
        tabSimb = new HashMap<>();
        tabMne = new HashMap<>();
    }

    void gerarCodObj(String filename) throws FileNotFoundException {
        File file = new File(filename);
        execPasso1(file);
        execPasso2(file);
    }

    private void execPasso1(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split("\\s+");
                if (line.charAt(0) != ' ') {  // Tem declaracao de simbolo ou label
                    String icString = Integer.toHexString(ic);
                    while (icString.length() < 4) {
                        icString = "0".concat(icString);
                    }
                    tabSimb.put(split[0], icString);
                    if (split.length > 1)
                        if (split[1].equals("K")) {  // simbolo
                            ic += 2;
                        }
                } else {
                    switch (split[1]) {
                        case "@":
                            ic = Integer.parseInt(split[2], 16);
                            byteCounter = ic;
                            break;
                        case "#":
                            // termina passo
                            break;
                        default:   // Uma das 16 instrucoes de 2 bytes
                            ic += 3;
                            // TODO: usar tabMne e checar erros
                            break;
                    }
                }
            }
            byteCounter = ic - byteCounter;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void execPasso2(File file) throws FileNotFoundException {
        File output = new File("objAbs.txt");
        System.out.println(output.getAbsolutePath());
        PrintWriter writer = new PrintWriter(output);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line, icString;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split("\\s+");
                if (line.charAt(0) == ' ') {
                    switch (split[1]) {
                        case "@":
                            ic = Integer.parseInt(split[2], 16);
                            icString = Integer.toHexString(ic);
                            while (icString.length() < 4) {
                                icString = "0".concat(icString);
                            }
                            String byteCounterString = Integer.toHexString(byteCounter);
                            while (byteCounterString.length() < 2) {
                                byteCounterString = "0".concat(byteCounterString);
                            }
                            writer.println(icString + byteCounterString);
                            break;
                        case "#":
                            icString = Integer.toHexString(ic);
                            while (icString.length() < 4) {
                                icString = "0".concat(icString);
                            }
                            String inicExec = tabSimb.get(split[2]);
                            writer.write("\n" + inicExec);
                            break;
                        case "JP":
                            writer.write("00" + tabSimb.get(split[2]));
                            ic += 3;
                            break;
                        case "JZ":
                            writer.write("01" + tabSimb.get(split[2]));
                            ic += 3;
                            break;
                        case "JN":
                            writer.write("02" + tabSimb.get(split[2]));
                            ic += 3;
                            break;
                        case "LV":
                            writer.write("03" + split[2]);
                            ic += 3;
                            break;
                        case "+":
                            writer.write("04" + tabSimb.get(split[2]));
                            ic += 3;
                            break;
                        case "-":
                            writer.write("05" + tabSimb.get(split[2]));
                            ic += 3;
                            break;
                        case "*":
                            writer.write("06" + tabSimb.get(split[2]));
                            ic += 3;
                            break;
                        case "/":
                            writer.write("07" + tabSimb.get(split[2]));
                            ic += 3;
                            break;
                        case "LD":
                            writer.write("08" + tabSimb.get(split[2]));
                            ic += 3;
                            break;
                        case "MM":
                            writer.write("09" + tabSimb.get(split[2]));
                            ic += 3;
                            break;
                        case "SC":
                            writer.write("0a" + tabSimb.get(split[2]));
                            ic += 3;
                            break;
                        case "RS":
                            writer.write("0b" + tabSimb.get(split[2]));
                            ic += 3;
                            break;
                        case "HM":
                            writer.write("0c0000");
                            ic += 3;
                            break;
                        case "GD":
                            writer.write("0d" + split[2]);
                            ic += 3;
                            break;
                        case "PD":
                            writer.write("0e" + split[2]);
                            ic += 3;
                            break;
                        case "SO":
                            writer.write("0f0000");
                            ic += 3;
                            break;
                    }
                } else if (split.length > 1) {
                    if (split[1].equals("K")) {
                        while (split[2].length() < 4)
                            split[2] = "0".concat(split[2]);
                        writer.print(split[2]);
                        ic += 2;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.close();
    }
}
