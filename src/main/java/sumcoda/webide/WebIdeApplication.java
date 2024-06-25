package sumcoda.webide;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

@Slf4j
@SpringBootApplication
public class WebIdeApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(WebIdeApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        try {
            // init.sh에 실행 권한 부여
            File initScript = new File("./init.sh");
            if (!initScript.setExecutable(true)) {
                throw new InterruptedException("Failed to set executable permission on init.sh.");
            }

            // init.sh 스크립트 실행
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "./init.sh");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info(line);
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new InterruptedException("init.sh script execution failed.");
            }
        } catch (Exception e) {
            throw new InterruptedException("Failed to execute init.sh script.");
        }
    }
}
