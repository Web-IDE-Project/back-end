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
public class WebIdeApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebIdeApplication.class, args);
    }


    @PostConstruct
    public void init() {
        try {
//            // docker-build.sh 파일에 실행 권한 부여
//            ProcessBuilder chmodProcessBuilder = new ProcessBuilder("chmod", "+x", "./docker-build.sh");
//            Process chmodProcess = chmodProcessBuilder.start();
//            int chmodExitCode = chmodProcess.waitFor();
//            if (chmodExitCode != 0) {
//                log.error("Failed to set executable permission on docker-build.sh with exit code: " + chmodExitCode);
//                throw new InterruptedException("Failed to set executable permission on docker-build.sh.");
//            }

            // docker-build.sh 파일에 실행 권한 부여
            File script = new File("./docker-build.sh");
            if (!script.setExecutable(true)) {
                throw new InterruptedException("Failed to set executable permission on docker-build.sh.");
            }

            // docker-build.sh 스크립트 실행
            ProcessBuilder processBuilder = new ProcessBuilder("./docker-build.sh");
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
                log.error("Docker build failed with exit code: " + exitCode);
                throw new InterruptedException("Docker images build failed.");
            }
        } catch (Exception e) {
            log.error("Exception occurred while building Docker images: ", e);
        }
    }
}
