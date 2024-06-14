package sumcoda.webide.entry.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.okhttp.OkDockerHttpClient;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class CompileService {

    private final DockerClient dockerClient;

    // 도커 실행에 필요한 의존성 주입
    public CompileService() {
        // Docker 클라이언트의 기본 설정
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();

        // OkHttp 클라이언트를 사용하여 Docker 클라이언트를 설정
        OkDockerHttpClient httpClient = new OkDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();

        // Docker 클라이언트를 초기화
        this.dockerClient = DockerClientBuilder.getInstance(config)
                .withDockerHttpClient(httpClient)
                .build();
    }

    /**
     *  주어진 확장자와 언어를 사용하여 컴파일을 수행
     *
     * @param extension 언어를 구분하기위한 확장자
     * @param code 컴파일을 진행할 코드
     **/
    public String compileCode(String extension, String code) throws IOException, InterruptedException {

        // 임시 디렉토리의 경로 생성
        String tempDir = System.getProperty("java.io.tmpdir");

        // 확장자에 따른 파일이름 설정
        String filename;
        if (extension.equals(".java")) {
            filename = "Main";
        } else {
            filename = "main";
        }

        // 주어진 언어에 맞는 파일 확장자를 가져와 임시 파일의 경로 생성
        String codeFilePath = tempDir + "/" + filename + extension;

        // 주어진 언어에 맞는 Docker 이미지를 반환
        String dockerImage = getDockerImage(extension);

        // 컴파일을 위한 임시 파일을 생성하고 주어진 코드를 작성
        File codeFile = new File(codeFilePath);
        try (FileWriter writer = new FileWriter(codeFile)) {
            writer.write(code);
        }

        // Docker 컨테이너를 생성
        // 호스트의 임시 디렉토리를 컨테이너의 /usr/src/myapp 디렉토리와 바인딩
        CreateContainerResponse container = dockerClient.createContainerCmd(dockerImage)
                .withHostConfig(HostConfig.newHostConfig().withBinds(new Bind(tempDir, new Volume("/usr/src/myapp"))))
                .exec();

        // 생성된 컨테이너의 ID를 반환
        String containerId = container.getId();

        // 컨테이너 상태를 확인하여 작업이 완료될 때까지 대기
        dockerClient.startContainerCmd(containerId).exec();


        // 컨테이너가 종료될 때까지 대기
        waitForContainerToFinish(containerId);

        // 컨테이너의 로그를 가져오기 위해 콜백을 설정
        LogContainerResultCallback callback = new LogContainerResultCallback();
        dockerClient.logContainerCmd(containerId)
                .withStdOut(true) // 표준 출력을 포함
                .withStdErr(true) // 표준 오류 출력을 포함
                .exec(callback).awaitCompletion(); // 로그가 완전히 수집될 때까지 대기

        // 컴파일 결과를 문자열로 변환
        String result = callback.toString();

        // 사용이 끝난 Docker 컨테이너를 제거
        dockerClient.removeContainerCmd(containerId).exec();

        // 컴파일을 위해서 임시로 생성된 코드 파일을 삭제
        Files.deleteIfExists(Paths.get(codeFilePath));

        // 컴파일 결과를 반환
        return result;
    }



    /**
     *  언어별 실행할 도커 이미지를 이름을 반환
     *
     * @param extension 언어를 구분하기위한 확장자
     **/
    private String getDockerImage(String extension) {
        switch (extension.toLowerCase()) {
            case ".java":
                return "java-compiler";
            case ".c":
                return "c-compiler";
            case ".cpp":
                return "cpp-compiler";
            case ".py":
                return "python-compiler";
            case ".js":
                return "js-compiler";
            default: throw new IllegalArgumentException("Unsupported extension: " + extension);
        }
    }

    /**
     *  컨테이너 상태를 확인하여 작업이 완료될 때까지 대기
     *
     * @param containerId 언어를 구분하기위한 확장자
     **/
    private void waitForContainerToFinish(String containerId) throws InterruptedException {
        while (true) {
            List<Container> containers = dockerClient.listContainersCmd()
                    .withShowAll(true)
                    .withIdFilter(List.of(containerId))
                    .exec();

            if (containers.isEmpty() || containers.get(0).getStatus().contains("Exited")) {
                break;
            }

            Thread.sleep(500); // 0.5초 간격으로 상태 확인
        }
    }

    /**
     *  Docker 컨테이너의 로그를 수집하는 콜백 클래스
     *
     **/
    private static class LogContainerResultCallback extends com.github.dockerjava.core.command.LogContainerResultCallback {
        // 로그 데이터를 저장할 StringBuilder 오브젝트
        private final StringBuilder log = new StringBuilder();

        // 로그의 각 프레임을 수집
        @Override
        public void onNext(com.github.dockerjava.api.model.Frame item) {
            log.append(new String(item.getPayload()));
            super.onNext(item);
        }

        // 수집된 로그를 문자열로 반환
        @Override
        public String toString() {
            return log.toString();
        }
    }

    //    private String getFileExtension(String language) {
//        switch (language.toLowerCase()) {
//            case "java": return ".java";
//            case "c": return ".c";
//            case "cpp": return ".cpp";
//            case "python": return ".py";
//            case "javascript": return ".js";
//            default: throw new IllegalArgumentException("Unsupported language: " + language);
//        }
//    }
}
