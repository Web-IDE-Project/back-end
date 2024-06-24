package sumcoda.webide;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Slf4j
@SpringBootApplication
public class WebIdeApplication  {

    public static void main(String[] args) {
        SpringApplication.run(WebIdeApplication.class, args);
    }

//    @Override
//    public void run(String... args) throws Exception {
//        try {
//            ProcessBuilder processBuilder = new ProcessBuilder("./docker-build.sh");
//            processBuilder.redirectErrorStream(true);
//            Process process = processBuilder.start();
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    log.info(line);
//                }
//            }
//            int exitCode = process.waitFor();
//            if (exitCode != 0) {
//                throw new InterruptedException("Docker images build failed.");
//            }
//        } catch (Exception e) {
//            throw new InterruptedException("Failed to build Docker images.");
//        }
//    }
}
