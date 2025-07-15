package force.ssafy.domain.awsS3.controller;

import force.ssafy.domain.awsS3.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/file")
public class AwsS3Controller {

    private final AwsS3Service awsS3Service;

    @PostMapping
    public ResponseEntity<List<String>> uploadFile(@RequestParam("multipartFiles") List<MultipartFile> multipartFiles){
        return ResponseEntity.ok(awsS3Service.uploadFile(multipartFiles));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteFile(@RequestParam String fileName){
        awsS3Service.deleteFile(fileName);
        return ResponseEntity.ok(fileName);
    }
}
