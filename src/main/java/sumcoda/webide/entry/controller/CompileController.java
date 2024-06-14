package sumcoda.webide.entry.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sumcoda.webide.entry.dto.request.CompileRequestDTO;
import sumcoda.webide.entry.service.CompileService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CompileController {

    private final CompileService compileService;

    /**
     *  컴파일 요청 캐치
     *
     * @param compileRequestDTO 프론트에서 전달받은 컴파일을 위한 DTO
     **/
    @PostMapping("/api/workspaces/entries/execute")
    public ResponseEntity<?> compileCode(@RequestBody CompileRequestDTO compileRequestDTO) {

        Map<String, Object> responseData = new HashMap<>();

        try {
            String result = compileService.compileCode(compileRequestDTO.getExtension(), compileRequestDTO.getCode());

            responseData.put("message", "컴파일에 성공하였습니다.");

            responseData.put("result", result);
            return ResponseEntity.status(HttpStatus.OK).body(responseData);
        } catch (Exception e) {
            responseData.put("message", "컴파일에 실패하였습니다.");

            responseData.put("result", e.getMessage());
            return ResponseEntity.status(500).body(responseData);
        }
    }

}
