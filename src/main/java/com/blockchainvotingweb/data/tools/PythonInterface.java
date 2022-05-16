package com.blockchainvotingweb.data.tools;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class PythonInterface {
    public static String signData(String sender, String receiver, String privateKeyString) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder("python", resolvePythonScriptPath("transaction.py"), sender, receiver, privateKeyString);
        processBuilder.redirectErrorStream(true);

//        System.out.println(data);
        System.out.println(privateKeyString);

        Process process = processBuilder.start();
        List<String> results = readProcessOutput(process.getInputStream());
        for (var info : results) {
            System.out.println(info);
        }
        return results.get(0);
    }

    private static List<String> readProcessOutput(InputStream inputStream) throws IOException {
        try (BufferedReader output = new BufferedReader(new InputStreamReader(inputStream))) {
            return output.lines()
                    .collect(Collectors.toList());
        }
    }

    private static String resolvePythonScriptPath(String filename) {
        File file = new File("src/main/resources/" + filename);
        return file.getAbsolutePath();
    }
}
