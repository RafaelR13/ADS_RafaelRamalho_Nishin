package com.upf.nishin.service;

import jakarta.enterprise.context.ApplicationScoped;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.net.URI;
import software.amazon.awssdk.services.s3.S3Configuration;

@ApplicationScoped
public class R2Service {

    private final S3Client r2;

    private final String bucket = "nishin";
    private final String accountId = "1f48390929920ed49613ff66baf24bff";
    private final String accessKey = "3d092badb9f367ea19e57602d7cfff29";
    private final String secretKey = "55ac49e0b43970cd38e4e6cc023c1d47b092dab2906f785a51ed835084b24c94";

    public R2Service() {

        AwsBasicCredentials creds = AwsBasicCredentials.create(accessKey, secretKey);

        this.r2 = S3Client.builder()
                .region(Region.of("auto"))
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .endpointOverride(URI.create("https://" + accountId + ".r2.cloudflarestorage.com"))
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build()
                )
                .build();
    }

    /**
     * ============================================================ UPLOAD
     * PRINCIPAL (usado pelo controller)
     * ============================================================
     */
    public String uploadFile(InputStream input, String fileName) throws Exception {
        byte[] bytes = input.readAllBytes();
        return upload(fileName, bytes, "image/*");
    }

    /**
     * ============================================================ MÉTODO DE
     * UPLOAD INTERNO
     * ============================================================
     */
    public String upload(String fileName, byte[] bytes, String contentType) {

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(contentType)
                .build(); // Sem ACL (não é suportado no R2)

        r2.putObject(req, RequestBody.fromBytes(bytes));

        // URL pública
        return "https://" + accountId + ".r2.cloudflarestorage.com/" + bucket + "/" + fileName;
    }
}
